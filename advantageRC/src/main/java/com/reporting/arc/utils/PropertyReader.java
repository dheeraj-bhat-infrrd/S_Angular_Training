/**
 * 
 */
package com.reporting.arc.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author E7440
 *
 */
public class PropertyReader {
	
    public static String getValueForKey(String key) {
		String value = "";
		Properties properties = new Properties();
		InputStream input = null;
		try {
		    //ClassLoader classLoader = PropertyReader.class.getClassLoader();
			input = PropertyReader.class.getClassLoader().getResourceAsStream("application.properties");
			properties.load(input);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		value = properties.getProperty(key);
		return value;
	}

}
