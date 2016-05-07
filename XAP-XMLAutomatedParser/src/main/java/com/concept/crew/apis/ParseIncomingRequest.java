package com.concept.crew.apis;

import java.io.File;
import java.io.IOException;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import com.concept.crew.parser.XapParsingUtil;
import com.concept.crew.util.AutomationHelper;

public class ParseIncomingRequest {

	public static <T> void main(String[] args) throws ClassNotFoundException, ParserConfigurationException, SAXException, IOException, JAXBException {
		File xsd = new File(args[0]);
		Class<T> rootClass = AutomationHelper.getRootClass(xsd);
		XapParsingUtil util = XapParsingUtil.getInstance(rootClass, xsd.getName());
		T jaxbObject =  (T) util.parseXml(args[1], "ISO-8859-2", rootClass);
	}
}
