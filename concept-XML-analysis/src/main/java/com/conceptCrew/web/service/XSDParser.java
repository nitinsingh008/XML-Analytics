package com.conceptCrew.web.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.springframework.stereotype.Component;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.conceptCrew.web.dto.Constants;


@Component("XSDParser")
public class XSDParser {

    public static final String PATH = Constants.xsdLocalPath;
    private SchemaSaxHandler customeHandler = new SchemaSaxHandler();
    
    public void parseXSD(String xsdName) throws FileNotFoundException, IOException, SAXException, ParserConfigurationException{
    	 SAXParserFactory spf = SAXParserFactory.newInstance();
         SAXParser sp = spf.newSAXParser();
         XMLReader reader = sp.getXMLReader();
        
         reader.setContentHandler(customeHandler);
         reader.parse(new InputSource(new FileInputStream(new File(PATH, xsdName))));
         
         
    }
    
    public String getXSDInTree(){
    	return customeHandler.getXsdInTree().toString();
    }
}

class SchemaElement {
    
    private String name;
    private String type;
    private List<SchemaElement> children;
    private Map<String, String> attributes;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<SchemaElement> getChildren() {
		return children;
	}
	public void setChildren(List<SchemaElement> children) {
		this.children = children;
	}
	public Map<String, String> getAttributes() {
		return attributes;
	}
	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}
    
    // getters and setters
    
    
}

class SchemaComplexType {
    
    private String name;
    private List<SchemaElement> children;
    private Map<String, String> attributes = new HashMap<>();
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<SchemaElement> getChildren() {
		return children;
	}
	public void setChildren(List<SchemaElement> children) {
		this.children = children;
	}
	public Map<String, String> getAttributes() {
		return attributes;
	}
	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}
   public void addAttribute(String name,String attribute){
	   if(attributes == null){
		   attributes = new HashMap<>();
	   }
	   attributes.put(name, attribute);
   }
    
}

class SchemaSaxHandler extends DefaultHandler {

    // temporary - always null when tag closes
    private String currentSimpleTypeName;
    private String currentSimpleTypeBaseType;
    private SchemaElement currentElement;
    private SchemaComplexType currentComplexType;
    private List<SchemaElement> currentSequence;

    // cumulative - will use the data when XML finishes
    private Map<String, String> simpleTypes = new HashMap<>();
    private Map<String, SchemaComplexType> complexTypes = new HashMap<>();
    private SchemaElement rootElement;
    
    private StringBuilder xsdInTree = new StringBuilder("<ul>");

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {

        if (qName.equals("xs:simpleType")) {
            currentSimpleTypeName = atts.getValue("name");
        }
        if (qName.equals("xs:restriction")) {
            currentSimpleTypeBaseType = atts.getValue("base");
        }

        if (qName.equals("xs:complexType")) {
            currentComplexType = new SchemaComplexType();
            currentComplexType.setName(atts.getValue("name"));
        }

        if (qName.equals("xs:sequence")) {
            currentSequence = new ArrayList<>();
        }

        if (qName.equals("xs:element")) {
            currentElement = new SchemaElement();
            currentElement.setName(atts.getValue("name"));
            currentElement.setType(atts.getValue("type"));
            if (currentSequence != null) {
                currentSequence.add(currentElement);
            } else {
                rootElement = currentElement;
            }
        }

        if (qName.equals("xs:attribute")) {
            currentComplexType.addAttribute(atts.getValue("name"), atts.getValue("type"));
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("xs:simpleType")) {
            simpleTypes.put(currentSimpleTypeName, currentSimpleTypeBaseType);
            currentSimpleTypeName = null;
            currentSimpleTypeBaseType = null;
        }
        if (qName.equals("xs:complexType")) {
        	complexTypes.put(currentComplexType.getName(), currentComplexType);
            currentComplexType = null;
        }
        if (qName.equals("xs:sequence")) {
            if (currentComplexType != null) {
                currentComplexType.setChildren(currentSequence);
            }
            currentSequence = null;
        }
    }

    @Override
    public void endDocument() throws SAXException {
        makeTree(rootElement);
        printTree(rootElement, "");
    }

    public void startDocument() throws SAXException {
        System.out.println();
    }

    public void makeTree(SchemaElement element) {
        SchemaComplexType type = complexTypes.get(element.getType());
        if (type != null) {
        	xsdInTree.append("<li>"+type.getName());
            List<SchemaElement> children = type.getChildren();
            element.setChildren(children);
            for (SchemaElement child : children) {
                makeTree(child);
            }
            element.setAttributes(type.getAttributes());
            xsdInTree.append("</li>");
        } else {
        	xsdInTree.append("<ul>"+element.getName()+"</ul>");
            element.setType(simpleTypes.get(element.getType()));
        }
    }

    private void printTree(SchemaElement element, String indent) {
        System.out.println(indent + element.getName() + " : " + element.getType());
        Map<String, String> attributes = element.getAttributes();
        if (attributes != null) {
            for (Map.Entry<String, String> entry : attributes.entrySet()) {
                System.out.println("    @" + entry.getKey() + " : " + simpleTypes.get(entry.getValue()));
            }
        }
        List<SchemaElement> children = element.getChildren();
        if (children != null) {
            for (SchemaElement child : children) {
                printTree(child, indent + "    ");
            }
        }
    }

	public StringBuilder getXsdInTree() {
		
		return xsdInTree.append("</ul>");
	}

}
