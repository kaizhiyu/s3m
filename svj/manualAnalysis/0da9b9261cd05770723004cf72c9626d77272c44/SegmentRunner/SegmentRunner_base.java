/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.spotify.reaper.service;

import com.google.common.base.Optional;

import com.spotify.reaper.ReaperException;
import com.spotify.reaper.cassandra.JmxProxy;
import com.spotify.reaper.cassandra.RepairStatusHandler;
import com.spotify.reaper.core.ColumnFamily;
import com.spotify.reaper.core.RepairSegment;
import com.spotify.reaper.storage.IStorage;

import org.apache.cassandra.service.ActiveRepairService;
import org.apache.cassandra.utils.SimpleCondition;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

public final class SegmentRunner implements RepairStatusHandler {

  private static final Logger LOG = LoggerFactory.getLogger(SegmentRunner.class);
  private static final int MAX_PENDING_COMPACTIONS = 20;

  private final IStorage storage;
  private final long segmentId;
  private int commandId;

  private final Condition condition = new SimpleCondition();


  public static void triggerRepair(IStorage storage, long segmentId,
      Collection<String> potentialCoordinators, long timeoutMillis,
      JmxConnectionFactory jmxConnectionFactory) {
    new SegmentRunner(storage, segmentId)
        .runRepair(potentialCoordinators, jmxConnectionFactory, timeoutMillis);
  }

  public static void postpone(IStorage storage, RepairSegment segment) {
    LOG.warn("Postponing segment {}", segment.getId());
    storage.updateRepairSegment(segment.with()
        .state(RepairSegment.State.NOT_STARTED)
        .coordinatorHost(null)
        .repairCommandId(null)
        .startTime(null)
        .failCount(segment.getFailCount() + 1)
        .build(segment.getId()));
  }

  public static void abort(IStorage storage, RepairSegment segment, JmxProxy jmxConnection) {
    postpone(storage, segment);
    LOG.warn("Aborting command {} on segment {}", segment.getRepairCommandId(), segment.getId());
    jmxConnection.cancelAllRepairs();
  }

  private SegmentRunner(IStorage storage, long segmentId) {
    this.storage = storage;
    this.segmentId = segmentId;
  }

  private void runRepair(Collection<String> potentialCoordinators,
      JmxConnectionFactory jmxConnectionFactory, long timeoutMillis) {
    final RepairSegment segment = storage.getRepairSegment(segmentId);
    try (JmxProxy jmxConnection = jmxConnectionFactory
        .connectAny(Optional.<RepairStatusHandler>of(this), potentialCoordinators)) {
      ColumnFamily columnFamily =
          storage.getColumnFamily(segment.getColumnFamilyId());
      String keyspace = columnFamily.getKeyspaceName();

      if (!canRepair(jmxConnection, segment)) {
        postpone(segment);
        return;
      }

      synchronized (condition) {
        commandId = jmxConnection
            .triggerRepair(segment.getStartToken(), segment.getEndToken(), keyspace,
                columnFamily.getName());
        LOG.debug("Triggered repair with command id {}", commandId);
        storage.updateRepairSegment(segment.with()
            .state(RepairSegment.State.RUNNING)
            .coordinatorHost(jmxConnection.getHost())
            .repairCommandId(commandId)
            .build(segmentId));
        LOG.info("Repair for segment {} started", segmentId);

        try {
          condition.await(timeoutMillis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
          LOG.warn("Repair command {} on segment {} interrupted", commandId, segmentId);
        } finally {
          RepairSegment resultingSegment = storage.getRepairSegment(segmentId);
          LOG.info("Repair command {} on segment {} exited with state {}", commandId, segmentId,
              resultingSegment.getState());
          if (resultingSegment.getState().equals(RepairSegment.State.RUNNING)) {
            LOG.info("Repair command {} on segment {} has been cancelled while running", commandId,
                segmentId);
            abort(resultingSegment, jmxConnection);
          }
        }
      }
    } catch (ReaperException e) {
      LOG.warn("Failed to connect to a coordinator node for segment {}", segmentId);
      postpone(segment);
    }
  }

  boolean canRepair(JmxProxy jmx, RepairSegment segment) {
    if (segment.getState().equals(RepairSegment.State.RUNNING)) {
      LOG.error("Repair segment {} was already marked as started when SegmentRunner was "
          + "asked to trigger repair", segmentId);
      return false;
    }
    if (jmx.getPendingCompactions() > MAX_PENDING_COMPACTIONS) {
      LOG.warn("SegmentRunner declined to repair segment {} because of too many pending "
          + "compactions (> {})", segmentId, MAX_PENDING_COMPACTIONS);
      return false;
    }
    return true;
  }

  private void postpone(RepairSegment segment) {
    postpone(storage, segment);
  }

  private void abort(RepairSegment segment, JmxProxy jmxConnection) {
    abort(storage, segment, jmxConnection);
  }


  /**
   * Called when there is an event coming either from JMX or this runner regarding on-going
   * repairs.
   *
   * @param repairNumber repair sequence number, obtained when triggering a repair
   * @param status       new status of the repair
   * @param message      additional information about the repair
   */
  @Override
  public void handle(int repairNumber, ActiveRepairService.Status status, String message) {
    synchronized (condition) {
      LOG.debug(
          "handle called for repairCommandId {}, outcome {} and message: {}",
          repairNumber, status, message);
      if (repairNumber != commandId) {
        LOG.debug("Handler for command id {} not handling message with number {}",
            commandId, repairNumber);
        return;
      }

      RepairSegment currentSegment = storage.getRepairSegment(segmentId);
      // See status explanations from: https://wiki.apache.org/cassandra/RepairAsyncAPI
      switch (status) {
        case STARTED:
          DateTime now = DateTime.now();
          storage.updateRepairSegment(currentSegment.with()
              .startTime(now)
              .build(segmentId));
          // We already set the state of the segment to RUNNING.
          break;
        case SESSION_FAILED:
          postpone(currentSegment);
          condition.signalAll();
          break;
        case SESSION_SUCCESS:
          // Do nothing, wait for FINISHED.
          break;
        case FINISHED:
          storage.updateRepairSegment(currentSegment.with()
              .state(RepairSegment.State.DONE)
              .endTime(DateTime.now())
              .build(segmentId));
          condition.signalAll();
          break;
      }
    }
  }
}
