package com.realtech.socialsurvey.core.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;

// SS-14 by RM02 BOC

/**
 * Class with methods to read the values from property file
 */
@Component
public final class PropertyFileReader {

	private static final Logger LOG = LoggerFactory.getLogger(PropertyFileReader.class);

	/**
	 * Map containing property file names and the corresponding properties in them
	 */
	private static Map<String, Properties> propertyMap = new HashMap<String, Properties>();

	private PropertyFileReader() {}

	/**
	 * Method to get the property value for the key from filename provided
	 * 
	 * @param propertyFileName
	 * @param key
	 * @return
	 */
	public String getProperty(String propertyFileName, String key) {
		try {
			if (propertyFileName == null || propertyFileName.isEmpty()) {
				throw new InvalidInputException("File name is null or empty while fetching properties");
			}
			if (key == null || key.isEmpty()) {
				throw new InvalidInputException("Key is null or empty while fetching property value");
			}
			LOG.debug("Getting property for  key : {} from property file {}",key, propertyFileName);

			if (propertyMap.get(propertyFileName) == null) {
				loadPropertyFile(propertyFileName);
			}
			Properties properties = propertyMap.get(propertyFileName);
			if (properties.get(key) == null) {
				throw new InvalidInputException("No value found in property file " + propertyFileName + " for key : " + key);
			}

			LOG.debug("Successfully fetched property value for key : {} from property file {}",key, propertyFileName);
			return properties.getProperty(key);
		}
		catch (NonFatalException e) {
			LOG.error("Exception occured while reading properties.", e);
			return null;
		}
	}

	/**
	 * Method to load property file onto the static hashMap of filename and properties
	 * 
	 * @param propertyFileName
	 * @throws NonFatalException
	 */
	private void loadPropertyFile(String propertyFileName) throws NonFatalException {
		LOG.debug("Loading property file : " + propertyFileName);
		try {
			Properties properties = new Properties();
			properties.load(PropertyFileReader.class.getClassLoader().getResourceAsStream(propertyFileName));
			propertyMap.put(propertyFileName, properties);

			LOG.debug("Successfully loaded property file : " + propertyFileName);
		}
		catch (IOException e) {
			LOG.error("IOException while loading property file " + propertyFileName);
			throw new NonFatalException("IOException while loading property file " + propertyFileName);
		}
	}

}
// SS-14 by RM02 EOC
