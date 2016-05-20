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
	
	public FrameworkSettings(String projectName) {
		super();
		this.projectName = projectName;
		this.dirSrcJava 		= mavenProjectPath + "/"+ projectName + "/src/main/java";
		this.pojoPackagePath = dirSrcJava + File.separator 
				+ "com" + File.separator + "concept" + File.separator + "crew" + File.separator + "info" + File.separator
				+ "pojo";
		this.resourcePath 	= mavenProjectPath + "/" +projectName +"/" + dirSrcResource;
		this.pomPath 			= mavenProjectPath + "/" +projectName + "/pom.xml";
		this.targetPath 		= mavenProjectPath + "/" + projectName + "/target";
		this.jarFileName 		= projectName + "-1.0.jar";
		this.compilationPath  = this.dirSrcJava + "/com/concept/crew/info/jaxb";
		this.pathToRootClass 	= this.targetPath + "/" + "classes";
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
	
	
	
	
}
