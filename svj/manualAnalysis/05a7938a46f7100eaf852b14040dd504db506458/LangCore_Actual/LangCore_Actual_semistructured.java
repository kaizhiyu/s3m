package melnorme.lang.ide.core; 

import com.googlecode.goclipse.core.engine.GoBundleModelManager; 
import com.googlecode.goclipse.core.engine.GoBundleModelManager.GoBundleModel; 
import com.googlecode.goclipse.core.engine.GoSourceModelManager; 
import com.googlecode.goclipse.core.operations.GoBuildManager; 
import com.googlecode.goclipse.core.operations.GoToolManager; 

 
 
 
 

import melnorme.lang.ide.core.engine.SourceModelManager; 
import melnorme.lang.ide.core.operations.ToolManager; 
import melnorme.lang.ide.core.operations.build.BuildManager; 
 
import melnorme.lang.ide.core.project_model.LangBundleModel; 

public  class  LangCore_Actual {
	
	
	public static final String PLUGIN_ID = "com.googlecode.goclipse.core";
	
	public static final String NATURE_ID = PLUGIN_ID + ".goNature";
	
	
//	public static final String BUILDER_ID = PLUGIN_ID + ".Builder";
	public static final String BUILDER_ID = "com.googlecode.goclipse.goBuilder";
	
	public static final String BUILD_PROBLEM_ID = PLUGIN_ID + ".goProblem";
	
	public static final String SOURCE_PROBLEM_ID = PLUGIN_ID + ".source_problem";
	
	
	// Note: the variable should not be named with a prefix of LANGUAGE, 
	// or it will interfere with MelnormeEclipse templating
	public static final String NAME_OF_LANGUAGE = "Go";
	
	
	public static final String VAR_NAME_SdkToolPath = "SDK_TOOL_PATH";
	
	public static final String VAR_NAME_SdkToolPath_DESCRIPTION = "The path of the SDK tool";
	
	
	
	public static LangCore instance;
	
	
	/* ----------------- Owned singletons: ----------------- */
	
	protected final ToolManager toolManager;
	
	protected final GoBundleModelManager bundleManager;
	
	protected final BuildManager buildManager;
	
	protected final GoSourceModelManager sourceModelManager;
	
	
	public LangCore_Actual() {
		instance = (LangCore) this;
		
		toolManager = createToolManagerSingleton();
		bundleManager = createBundleModelManager();
		buildManager = createBuildManager(bundleManager.getModel());
		sourceModelManager = createSourceModelManager();
	}
	
	
	public static GoToolManager createToolManagerSingleton() {
		return new GoToolManager();
	}
	
	
	public static GoBundleModelManager createBundleModelManager() {
		return new GoBundleModelManager();
	}
	
	public static BuildManager createBuildManager(LangBundleModel bundleModel) {
<<<<<<< C:\Users\GUILHE~1\AppData\Local\Temp\fstmerge_var1_5137049313137288051.java
		return new GoBuildManager(bundleModel);
=======
		return new LANGUAGE_BuildManager(bundleModel, LangCore.getToolManager());
>>>>>>> C:\Users\GUILHE~1\AppData\Local\Temp\fstmerge_var2_3159676208078249953.java
	}
	
	
	public static GoSourceModelManager createSourceModelManager() {
		return new GoSourceModelManager();
	}
	
	
		
	/* -----------------  ----------------- */
	
	
	public static ToolManager getToolManager() {
		return instance.toolManager;
	}
	
	public static GoBundleModel getBundleModel() {
		return instance.bundleManager.getModel();
	}
	
	public static BuildManager getBuildManager() {
		return instance.buildManager;
	}
	
	public static GoBundleModelManager getBundleModelManager() {
		return instance.bundleManager;
	}
	
	public static SourceModelManager getSourceModelManager() {
		return instance.sourceModelManager;
	}
	
	
	    LANGUAGE_BundleModel   {

	}

}

