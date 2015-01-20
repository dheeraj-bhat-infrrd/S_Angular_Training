package com.realtech.socialsurvey.core.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * JIRA:SS-62 BY RM 02 Holds utility methods for performing solr search operations
 */
@Component
public class SolrSearchUtils {

	private static final Logger LOG = LoggerFactory.getLogger(MessageUtils.class);

	/**
	 * Method to get string from input stream
	 * @param is
	 * @return
	 */
	public String getStringFromInputStream(InputStream is) {
		LOG.debug("Method getStringFromInputStream called");
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		}
		catch (IOException e) {
			LOG.error("IOException while getting string from input stream", e);
		}
		finally {
			if (br != null) {
				try {
					br.close();
				}
				catch (IOException e) {
					LOG.error("IOException while getting string from input stream", e);
				}
			}
		}
		LOG.debug("Method getStringFromInputStream finished");
		return sb.toString();
	}
}
