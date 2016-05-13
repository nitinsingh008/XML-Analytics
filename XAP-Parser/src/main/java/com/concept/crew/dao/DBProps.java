package com.concept.crew.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Properties;
import org.apache.log4j.Logger;

public class DBProps 
{
	private static Properties properties;
	private static Logger log = Logger.getLogger(DBProps.class.getName());
	private static boolean inited = false;

	static
	{
		initialize();
	}
	
	private static void initialize()
	{
		try
		{
			properties = new Properties();
			InputStream loadfrom = null;
			// first check for the system property
			String userfile = System.getProperty("universe.properties");
			if(userfile != null)
			{
				loadfrom = new FileInputStream(userfile);
			}
			else
			{
				URL url = getResource("universe.properties");
				if(url!=null){
					loadfrom = new FileInputStream(new File(url.getFile()));
					
				} else  {
					throw new IOException("Properties file could not be read");
				}
			}
			properties.load(loadfrom);
			inited = true;
			log.info(properties);
		}
		catch(IOException e)
		{
			log.error(e.getMessage(),e);
		}
	}

	public static String getProperty(String name)
	{
		if(!inited) initialize();
		return properties.getProperty(name);
	}
	public static Properties getProperties()
	{
		if(!inited) initialize();
		return properties;
	}

	public static URL getResource(String resource)
    {
        ClassLoader classLoader = null;
        URL url = null;
        try
        {
                classLoader = getTCL();
                if(classLoader != null)
                {
                    url = classLoader.getResource(resource);
                    if(url != null)
                        return url;
                }
            classLoader = (DBProps.class).getClassLoader();
            if(classLoader != null)
            {
                url = classLoader.getResource(resource);
                if(url != null)
                    return url;
            }
        }
        catch(Throwable t)
        {
        }
        return ClassLoader.getSystemResource(resource);
    }

   @SuppressWarnings("all")
   private static ClassLoader getTCL() throws IllegalAccessException, InvocationTargetException {
		Method method = null;
		try {
			method = (java.lang.Thread.class).getMethod("getContextClassLoader", null);
		} catch (NoSuchMethodException e) {
			return null;
		}
		return (ClassLoader) method.invoke(Thread.currentThread(), null);
	}
}
