package com.concept.crew.util;

import java.io.File;
import static com.concept.crew.util.Constants.*;

public class FrameworkSettings {

	private String projectName;
	private String pojoPackagePath;
	private String dirSrcJava;
	private String resourcePath;
	private String  pomPath;
	private String  targetPath;
	private String  jarFileName;
	private String compilationPath;
	private String pathToRootClass;
	private String pathToGenerateSchedules;
	private String pathToLoadSaveProcessor;
	private String pathToParentInfoWrapper;
	private String pathToLoaderType;
	private String defaultConceptCrewPath;
	private String pathToUtilityJar;
	
	public FrameworkSettings(String projectName) {
		super();
		this.projectName 				= projectName;
		this.dirSrcJava 				= mavenProjectPath + "/"+ projectName + "/src/main/java";
		this.defaultConceptCrewPath 	= this.dirSrcJava + File.separator + "com" + File.separator + "concept" + File.separator + "crew" ;
		this.pojoPackagePath 			= this.defaultConceptCrewPath + File.separator + "info" + File.separator  + "pojo";
		this.resourcePath 				= mavenProjectPath + "/" +projectName +"/" + dirSrcResource;
		this.pomPath 					= mavenProjectPath + "/" +projectName + "/pom.xml";
		this.targetPath 				= mavenProjectPath + "/" + projectName + "/target";
		this.jarFileName 				= projectName + "-1.0.jar";
		this.compilationPath  			= this.dirSrcJava + "/com/concept/crew/info/jaxb";
		this.pathToRootClass 			= this.targetPath + "/" + "classes";
		this.pathToGenerateSchedules 	= this.defaultConceptCrewPath + File.separator + "dao" + File.separator + "loader";
		this.pathToLoadSaveProcessor	= this.defaultConceptCrewPath + File.separator + "processor";
		this.pathToParentInfoWrapper	= this.defaultConceptCrewPath + File.separator + "info" + File.separator + "raw" ; 
		this.pathToLoaderType			= this.defaultConceptCrewPath + File.separator + "dao" + File.separator + "loaderUtil" ;
		this.pathToUtilityJar			= mavenProjectPath + "/"+ projectName + "/src/lib";
	}

	public String getProjectName() {
		return projectName;
	}

	public String getPojoPackagePath() {
		return pojoPackagePath;
	}

	public String getDirSrcJava() {
		return dirSrcJava;
	}

	public String getResourcePath() {
		return resourcePath;
	}

	public String getPomPath() {
		return pomPath;
	}

	public String getTargetPath() {
		return targetPath;
	}

	public String getJarFileName() {
		return jarFileName;
	}

	public String getCompilationPath() {
		return compilationPath;
	}

	public String getPathToRootClass() {
		return pathToRootClass;
	}

	public String getPathToGenerateSchedules() {
		return pathToGenerateSchedules;
	}

	public String getPathToLoadSaveProcessor() {
		return pathToLoadSaveProcessor;
	}

	public String getPathToParentInfoWrapper() {
		return pathToParentInfoWrapper;
	}

	public String getPathToLoaderType() {
		return pathToLoaderType;
	}

	public String getDefaultConceptCrewPath() {
		return defaultConceptCrewPath;
	}

	public String getPathToUtilityJar() {
		return pathToUtilityJar;
	}
	
}
