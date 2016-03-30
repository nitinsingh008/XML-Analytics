package com.concept.crew.util;

import java.io.File;
import java.io.FileInputStream;

import org.xml.sax.InputSource;

import com.sun.codemodel.JCodeModel;
import com.sun.tools.xjc.api.S2JJAXBModel;
import com.sun.tools.xjc.api.SchemaCompiler;
import com.sun.tools.xjc.api.XJC;

public class JaxbInfoGenerator 
{

	
	public static void main(String[] args) throws Exception
	{
		final Thread currentThread = Thread.currentThread();
		final ClassLoader contextClassLoader = currentThread.getContextClassLoader();

		File file = new File(contextClassLoader.getResource("CustomersOrders.xsd").getFile()); //resource folder
		
		JaxbInfoGenerator gen = new JaxbInfoGenerator();
		gen.generateInfos(file.getAbsolutePath());
	}
	/*
	 * schemaFilePathName => Absolute Path Name
	 */
	public void generateInfos(String schemaFilePathName) throws Exception
	{
		System.out.println("*** Preparing for generation of Classes ****");
        File outFile = new File(Constants.dirSrcJava);

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
        System.out.println("*** Start generating JAXB Classes ****");
        jCodeModel.build(outFile);
	}

}