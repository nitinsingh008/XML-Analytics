package com.concept.crew.processor;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

public class FrameworkGenerator {

	public static void main(String[] args) throws Exception
	{
		VelocityEngine ve = new VelocityEngine();
        ve.init();

        // Data
        ArrayList list = new ArrayList();
        
        Map map = new HashMap();

        map.put("name", "Cow");
        map.put("price", "$100.00");
        list.add( map );
 
        map = new HashMap();
        map.put("name", "Eagle");
        map.put("price", "$59.99");
        list.add( map );

        map = new HashMap();
        map.put("ClassName", "InstrumentLoader");
        list.add( map );



        VelocityContext context = new VelocityContext();
        //context.put("petList", list);
        String infoName = "Instrument";
        context.put("Info", infoName);
        context.put("ClassName", infoName + "Loader");


        Template template = ve.getTemplate("./src/main/resources/scheduleLoaders.java.vtl" );

        StringWriter writer = new StringWriter();
        //BufferedWriter writer = new BufferedWriter(new FileWriter("./src/main/resources/JavaClasses/InstrumentLoader.java"));
        
        template.merge( context, writer );
/*        writer.flush();
        writer.close();*/

        System.out.println( writer.toString() );        
	}

}
