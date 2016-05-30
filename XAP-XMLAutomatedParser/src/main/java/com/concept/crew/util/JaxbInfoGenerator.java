package com.concept.crew.util;

import java.io.File;
import java.io.FileInputStream;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;

import com.concept.crew.apis.StartAutomation;
import com.sun.codemodel.JCodeModel;
import com.sun.tools.xjc.api.S2JJAXBModel;
import com.sun.tools.xjc.api.SchemaCompiler;
import com.sun.tools.xjc.api.XJC;

public class JaxbInfoGenerator 
{
	private static Logger 		logger 			= Logger.getLogger(JaxbInfoGenerator.class);

	private FrameworkSettings projectSetting;
	
	public JaxbInfoGenerator(FrameworkSettings projectSetting) {
		super();
		this.projectSetting = projectSetting;
	}
	public static void main(String[] args) throws Exception
	{
		/*final Thread currentThread = Thread.currentThread();
		final ClassLoader contextClassLoader = currentThread.getContextClassLoader();

		File file = new File(contextClassLoader.getResource("CustomersOrders.xsd").getFile()); //resource folder
		
		JaxbInfoGenerator gen = new JaxbInfoGenerator(new FrameworkSettings("test"));
		gen.generateInfos(file.getAbsolutePath());*/
	}
	/*
	 * schemaFilePathName => Absolute Path Name
	 */
	public void generateInfos(String schemaFilePathName) throws Exception
	{
		logger.warn("Preparing for generation of Classes ****");
        File outFile = new File(projectSetting.getDirSrcJava());

        // Setup schema compiler
        SchemaCompiler schemaCompiler = XJC.createSchemaCompiler();
        schemaCompiler.forcePackageName(Constants.packageName);

        // Setup SAX InputSource
        File schemaFile = new File(schemaFilePathName);
        InputSource is = new InputSource(new FileInputStream(schemaFile));
        is.setSystemId(schemaFile.toURI().toString());

        // Parse & build
        schemaCompiler.parseSchema(is);
        S2JJAXBModel model = schemaCompiler.bind();
        JCodeModel jCodeModel = model.generateCode(null, null);
        
        jCodeModel.build(outFile); 
        logger.warn("JAXB generated successfully");
	}

}