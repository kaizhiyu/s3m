package com.cronutils; 

import com.cronutils.model.Cron; 
 
import com.cronutils.model.definition.CronDefinition; 
import com.cronutils.model.definition.CronDefinitionBuilder; 
import com.cronutils.model.time.ExecutionTime; 
import com.cronutils.parser.CronParser; 
import org.junit.Test; 
import org.threeten.bp.ZonedDateTime; 

import org.junit.Before; 
import static org.junit.Assert.assertEquals; 

public  class  Issue228Test {
	
	<<<<<<< C:\Users\GUILHE~1\AppData\Local\Temp\fstmerge_var1_5318294664336920802.java
private CronDefinition cronDefinition;=======
private CronDefinition cronDefinition = CronDefinitionBuilder.defineCron()
        .withMinutes().and()
        .withHours().and()
        .withDayOfMonth().and()
        .withMonth().and()
        .withDayOfWeek().withValidRange(0, 7).withMondayDoWValue(1).withIntMapping(7, 0).and()
        .enforceStrictRanges()
        .matchDayOfWeekAndDayOfMonth() // the regular UNIX cron definition permits matching either DoW or DoM
        .instance();>>>>>>> C:\Users\GUILHE~1\AppData\Local\Temp\fstmerge_var2_3765875319370545011.java

	
	@Before
	public void setUp(){
		cronDefinition = CronDefinitionBuilder.defineCron()
        	.withMinutes().and()
        	.withHours().and()
        	.withDayOfMonth().supportsL().and()
        	.withMonth().and()
        	.withDayOfWeek().withValidRange(0,7).withMondayDoWValue(1).and()
        	.enforceStrictRanges()
        	.matchDayOfWeekAndDayOfMonth()
        	.instance();
	}
	
    /**
     * Issue #228: dayOfWeek just isn't honored in the cron next execution evaluation and needs to be
     */
    @Test
    public void testFirstMondayOfTheMonthNextExecution() {
        CronParser parser = new CronParser(cronDefinition);

        // This is 9am on a day between the 1st and 7th which is a Monday (in this case it should be Oct 2
        Cron myCron = parser.parse("0 9 1-7 * 1");
        ZonedDateTime time = ZonedDateTime.parse("2017-09-29T14:46:01.166-07:00");
        assertEquals(ZonedDateTime.parse("2017-10-02T09:00-07:00"), ExecutionTime.forCron(myCron).nextExecution(time).get());
    }
	

    @Test
    public void testEveryWeekdayFirstWeekOfMonthNextExecution() {
        CronParser parser = new CronParser(cronDefinition);

        // This is 9am on Mon-Fri day between the 1st and 7th (in this case it should be Oct 2)
        Cron myCron = parser.parse("0 9 1-7 * 1-5");
        ZonedDateTime time = ZonedDateTime.parse("2017-09-29T14:46:01.166-07:00");
        assertEquals(ZonedDateTime.parse("2017-10-02T09:00-07:00"), ExecutionTime.forCron(myCron).nextExecution(time).get());
    }
	

    @Test
    public void testEveryWeekendFirstWeekOfMonthNextExecution() {
        CronParser parser = new CronParser(cronDefinition);

        // This is 9am on Sat and Sun day between the 1st and 7th (in this case it should be Oct 1)
        Cron myCron = parser.parse("0 9 1-7 * 6-7");
        ZonedDateTime time = ZonedDateTime.parse("2017-09-29T14:46:01.166-07:00");
        assertEquals(ZonedDateTime.parse("2017-10-01T09:00-07:00"), ExecutionTime.forCron(myCron).nextExecution(time).get());
    }
	

    @Test
    public void testEveryWeekdaySecondWeekOfMonthNextExecution() {
        CronParser parser = new CronParser(cronDefinition);

        // This is 9am on Mon-Fri day between the 8th and 14th (in this case it should be Oct 9 Mon)
        Cron myCron = parser.parse("0 9 8-14 * 1-5");
        ZonedDateTime time = ZonedDateTime.parse("2017-09-29T14:46:01.166-07:00");
        assertEquals(ZonedDateTime.parse("2017-10-09T09:00-07:00"), ExecutionTime.forCron(myCron).nextExecution(time).get());
    }
	

    @Test
    public void testEveryWeekendForthWeekOfMonthNextExecution() {
        CronParser parser = new CronParser(cronDefinition);

        // This is 9am on Sat and Sun day between the 22nd and 28th (in this case it should be Oct 22)
        Cron myCron = parser.parse("0 9 22-28 * 6-7");
        ZonedDateTime time = ZonedDateTime.parse("2017-09-29T14:46:01.166-07:00");
        assertEquals(ZonedDateTime.parse("2017-10-22T09:00-07:00"), ExecutionTime.forCron(myCron).nextExecution(time).get());
    }

}

