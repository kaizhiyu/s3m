package melnorme.lang.ide.ui;

import java.util.List;
import melnorme.lang.ide.core.LangCore_Actual;
import melnorme.lang.ide.ui.editor.hover.ILangEditorTextHover;
import melnorme.lang.ide.ui.editor.text.LangAutoEditsPreferencesAccess;
import melnorme.lang.ide.ui.views.StructureElementLabelProvider;
import org.eclipse.jface.text.source.ISourceViewer;
import LANG_PROJECT_ID.ide.core.text.LANGUAGE_AutoEditStrategy;
import _org.eclipse.jdt.internal.ui.text.java.hover.AnnotationHover;
import _org.eclipse.jdt.internal.ui.text.java.hover.ProblemHover;
import com.googlecode.goclipse.ui.GoPluginImages;
import com.googlecode.goclipse.ui.GoStructureElementLabelProvider;
import com.googlecode.goclipse.ui.editor.GoDocHover;
import com.googlecode.goclipse.ui.editor.text.GoAutoEditStrategy;

/**
 * Actual/concrete IDE constants and other bindings, for Lang UI code. 
 */
public final class LangUIPlugin_Actual {

    public static final String PLUGIN_ID = "com.googlecode.goclipse.ui";

    public static final String ROOT_PREF_PAGE_ID = PLUGIN_ID + ".PreferencePages.Root";

    public static final String RULER_CONTEXT = "#GoEditorRulerContext";

    public static final String EDITOR_CONTEXT = "#GoEditorContext";

    // ID to start the debug plugin automatically, if present
    protected static final String DEBUG_PLUGIN_ID = "com.googlecode.goclipse.debug";

    protected static final Class<?> PLUGIN_IMAGES_CLASS = GoPluginImages.class;

    protected static void initTextHovers(List<Class<? extends ILangEditorTextHover<?>>> textHoverSpecifications) {
        textHoverSpecifications.add(ProblemHover.class);
        textHoverSpecifications.add(GoDocHover.class);
        textHoverSpecifications.add(AnnotationHover.class);
    }

    public static GoAutoEditStrategy createAutoEditStrategy(ISourceViewer sourceViewer, String contentType) {
        return new GoAutoEditStrategy(contentType, sourceViewer, new LangAutoEditsPreferencesAccess());
    }

    public static StructureElementLabelProvider getStructureElementLabelProvider() {
        return new GoStructureElementLabelProvider();
    }

    /* ----------------- UI messages:  ----------------- */
    public static final String TOOLS_CONSOLE_NAME = LangCore_Actual.LANGUAGE_NAME + " build";

    public static final String DAEMON_TOOL_Name = "gocode";

    public static final String DAEMON_TOOL_ConsoleName = "Oracle/gocode log";
}

