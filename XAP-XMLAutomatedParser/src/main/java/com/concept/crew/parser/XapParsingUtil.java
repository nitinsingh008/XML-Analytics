package com.concept.crew.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.util.ValidationEventCollector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XapParsingUtil {
	
	private static Logger 		logger 			= Logger.getLogger(XapParsingUtil.class);	
	private static JAXBContext 	jaxbContext;
	private static Unmarshaller unmarshaller;
	private static XapParsingUtil instance  = new XapParsingUtil();
	private XapParsingUtil(){
		
	}
	
	@SuppressWarnings("rawtypes")
	public static XapParsingUtil getInstance(Class root , String schemaName) 
	{
		try 
		{
			jaxbContext = JAXBContext.newInstance(root);
			
			unmarshaller = jaxbContext.createUnmarshaller();
			final SchemaFactory sf = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
			
			final Thread currentThread = Thread.currentThread();
			final ClassLoader contextClassLoader = currentThread.getContextClassLoader();

			final InputStream inputStream = contextClassLoader.getResourceAsStream(schemaName);
			
			final Source src = new StreamSource(inputStream);
			
			final Schema schema = sf.newSchema(src);
			
			unmarshaller.setSchema(schema);
			
			return instance;
		} 
		catch (Exception e) 
		{
			logger.error("FATAL EXCEPTION, cannot start process.", e);
			throw new ExceptionInInitializerError("FATAL EXCEPTION, cannot start process.");
		}
	}
	
	public Object parseXml(String filePath, String encoding, Class root) throws ParserConfigurationException, SAXException, IOException, JAXBException{
		File fXmlFile = new File(filePath);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder dBuilder = dbf.newDocumentBuilder();
		InputStream inputStream = new FileInputStream(fXmlFile);
		InputSource in = new InputSource(inputStream);
		in.setEncoding(encoding);
		Document document = dBuilder.parse(in);
		document.getDocumentElement().normalize();
		
		final ValidationEventCollector validationEventCollector = new ValidationEventCollector() 
		{
			@Override
			public boolean handleEvent(ValidationEvent ve) 
			{
				super.handleEvent(ve);
				return true;
			}
		};
		
		unmarshaller.setEventHandler(validationEventCollector);
		
		final JAXBElement<?>  unmarshalledObject = unmarshaller.unmarshal(document,root);
		return unmarshalledObject.getValue();
	}

}
