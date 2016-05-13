package com.concept.crew.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventLocator;
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.concept.crew.info.jaxb.Instrument;
import com.concept.crew.info.jaxb.StartingTag;

public class XMLParsingUtil 
{
	
	final   static String 		UTF8 			= "UTF-8";
	private static Logger 		log 			= Logger.getLogger(XMLParsingUtil.class);	
	private static String 		SCHEMA_FILE 	= "SampleBondsXSD.xsd"; // TODO: Will be dynamic from UI 
	private static JAXBContext 	jaxbContext;
	private static Unmarshaller unmarshaller;
	
	static 
	{
		try 
		{
			log.info("************ Start XMLParsingUtil *********************");
			// TODO: Will be dynamic from UI 
			jaxbContext = JAXBContext.newInstance(StartingTag.class, Instrument.class);
			
			final long timeBefore = System.currentTimeMillis();
			log.info("Starting to create unmarshaller");			
			unmarshaller = jaxbContext.createUnmarshaller();
			final SchemaFactory sf = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
			log.info("SchemaFactory created");
			
			final Thread currentThread = Thread.currentThread();
			final ClassLoader contextClassLoader = currentThread.getContextClassLoader();

			final InputStream inputStream = contextClassLoader.getResourceAsStream(SCHEMA_FILE);
			
			final Source src = new StreamSource(inputStream);
			log.info("Source Created");
			
			final Schema schema = sf.newSchema(src);
			log.info("Schema Created");
			
			unmarshaller.setSchema(schema);
			log.info("Schema set on Unmarshaller");
			
			final long timeAfter = System.currentTimeMillis();
			final long timeDiff = timeAfter - timeBefore;
			log.info("Finished creating unmarshaller, which took " + (timeDiff / 1000) + " seconds");
		} 
		catch (Exception e) 
		{
			log.error("FATAL EXCEPTION, cannot start process.", e);
			throw new ExceptionInInitializerError("FATAL EXCEPTION, cannot start process.");
		}
	}

	
	/**
	 * Unmarshal using a specific character encoding e.g. UTF-8 or ISO-8859-2
	 */
	public static StartingTag unmarshall(String filePath, 
									     String encoding)
			throws ParserConfigurationException, SAXException, IOException, JAXBException 
	{
		log.info("****** unmarshall() method ****** Start Unmashalling **************");
		File fXmlFile = new File(filePath);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder dBuilder = dbf.newDocumentBuilder();

		// Register Error Handler anonymously to print details of lines which cause Exceptions
		dBuilder.setErrorHandler(new ErrorHandler() 
		{
			public void warning(SAXParseException exception)
					throws SAXException 
			{
				log.warn(exception, exception);
				log.warn("line=" + exception.getLineNumber());
				log.warn("column=" + exception.getColumnNumber());
			}

			public void fatalError(SAXParseException exception)
					throws SAXException 
			{
				log.fatal(exception, exception);
				log.fatal("line=" + exception.getLineNumber());
				log.fatal("column=" + exception.getColumnNumber());
			}

			public void error(SAXParseException exception) throws SAXException {
				log.error(exception, exception);
				log.error("line=" + exception.getLineNumber());
				log.error("column=" + exception.getColumnNumber());
			}
		});
		
		InputStream inputStream = new FileInputStream(fXmlFile);
		InputSource in = new InputSource(inputStream);
		in.setEncoding(encoding);
		Document document = dBuilder.parse(in);
		document.getDocumentElement().normalize();
		return validateXML(document);
	}
	
	/**
	 * This method validates xml against schema. To validate xml, it basically
	 * try to unmarshal xml in java objects. Umarshalling can be done only if
	 * xml is well defined for schema
	 */
	private static StartingTag validateXML(Document document) 
			throws JAXBException, SAXException, IOException 
	{
		log.info("****** validateXML() method ****** Starting validation **************");
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
		
		log.info("Starting unmarshalling");
		logMemory(log);
		
		final long timeBeforeUnmarshalling = System.currentTimeMillis();
		final JAXBElement<StartingTag> bondFeed = unmarshaller.unmarshal(document,
				StartingTag.class);
		
		final long timeAfterUnmarshalling = System.currentTimeMillis();
		logMemory(log);
		final long timeTookToUnmarshal = timeAfterUnmarshalling
				- timeBeforeUnmarshalling;
		log.info("Finished unmarshalling, which took " + (timeTookToUnmarshal / 1000) + " seconds");
		final ValidationEvent[] validationEvents = validationEventCollector
				.getEvents();
		final int numValidationEvents = validationEvents.length;
		final StartingTag srefRootElement = bondFeed.getValue();
		final List<Instrument> bonds = srefRootElement.getInstrument(); // TODO Dyanmic
		final int origNumBonds = bonds.size();
		log.info("There are " + numValidationEvents
				+ " validation events within " + origNumBonds + " bonds");
		final Set<String> removedXMLTagIds = new HashSet<String>();
		final Node rootNode = document.getDocumentElement();
		final Node rootChild = rootNode.getFirstChild();
		if (numValidationEvents > 0) 
		{
			for (int i = 0; i < validationEvents.length; ++i) 
			{
				final ValidationEvent 	validationEvent 		= validationEvents[i];				
				final String 			validationEventMessage 	= validationEvent.getMessage();
				final ValidationEventLocator locator 			= validationEvent.getLocator();
				final Node 				errorNode 			= locator.getNode();
				final String 			errorNodeName 		= errorNode.getNodeName();
				final Node 				markitBondNode 		= getMarkitBondNode(rootChild,errorNode);				
				final String 			pkey 				= getPkey(markitBondNode);
				
				log.info("Problem Node: " + errorNodeName + " Msg: "+ validationEventMessage);
				if (pkey == null) 
				{
					log.warn("Could not find pkey for basic Node ["+ markitBondNode + "]");
				}
				if (!removedXMLTagIds.contains(pkey)) 
				{
					log.info("Will remove bond with pkey ["
							+ pkey + "].  Erroneous element ["
							+ errorNodeName + "].  Reason: ["
							+ validationEventMessage + "]");
					removedXMLTagIds.add(pkey);
				}
			}
			for (final Iterator<Instrument> i = bonds.iterator(); i.hasNext();) 
			{
				final Instrument instrument = i.next();
/*				final String pkey = instrument.getPkey();
				if (removedInstrumentIds.contains(pkey)) 
				{
					i.remove();
				}*/
			}
		}
		
		final List<Instrument> latestInstrumentList = srefRootElement.getInstrument(); // TODO Dyamic
		final int latestNumBonds = latestInstrumentList.size();
		log.info("Finished validation.  Number of bonds removed: "
				+ removedXMLTagIds.size()
				+ ".  Num of bonds remaining after validation: "
				+ latestNumBonds + " (out of original " + origNumBonds
				+ ") which is a difference of "
				+ (origNumBonds - latestNumBonds));
		logMemory(log);
		return srefRootElement;
	}	
	
	public static String getPkey(final Node markitBondNode) 
	{
		final NodeList markitBondNodeChildrenList = markitBondNode.getChildNodes();
		final int length = markitBondNodeChildrenList.getLength();
		for (int j = 0; j < length; ++j) 
		{
			final Node   childNode = markitBondNodeChildrenList.item(j);
			final String childName = childNode.getNodeName();
			if ("pkey".equals(childName)) 
			{
				final String issuerIdString = childNode.getTextContent();
				return issuerIdString;
			}
		}
		return null;
	}	

	public static Node getMarkitBondNode(final Node rootNode, final Node childNode) 
	{
		final String rootName = rootNode.getNodeName();
		if (rootName == null) 
		{
			log.warn("This Root node's name is null: [" + rootNode + "]");
			return null;
		}
		final String childNodeName = childNode.getNodeName();
		if (childNodeName == null) 
		{
			log.warn("This Child node's name is null: [" + childNode + "]");
			return null;
		}
		final Node parentNode = childNode.getParentNode();
		final String parentName = parentNode.getNodeName();
		if ("MarkitBond".equals(parentName)) 
		{
			return parentNode;
		}
		return getMarkitBondNode(rootNode, parentNode);
	}	
	
	public static void logMemory(final Logger log)
	{
		log.info("Current Total Memory [" + Runtime.getRuntime().totalMemory() 
				+ "], Max Memory [" + Runtime.getRuntime().maxMemory() 
				+ "], Free Memory [" + Runtime.getRuntime().freeMemory() 
				+ "]");
	}	

}
