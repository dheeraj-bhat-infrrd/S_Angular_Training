package com.realtech.socialsurvey.core.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UrlValidationHelper {

	private static final Logger LOG = LoggerFactory.getLogger(UrlValidationHelper.class);

	public void validateUrl(String url) throws IOException {
		LOG.info("Checking if url exists : {}", url);
		
		// checking for https protocol
		String tempUrl = new String(url);
		if (!tempUrl.startsWith("https://")) {
			tempUrl = "https://" + tempUrl;
	    }
		
		URL connectUrl = null;
		HttpURLConnection huc;
		try {
			connectUrl = new URL(tempUrl);
			huc = (HttpURLConnection) connectUrl.openConnection();
			huc.setRequestMethod("GET"); // OR huc.setRequestMethod ("HEAD");
			huc.connect();
		}
		catch (IOException e) {
			LOG.info("Url-{} does not support https", url);
			
			// checking for http protocol
			tempUrl = new String(url);
			if (!tempUrl.startsWith("http://")) {
				tempUrl = "http://" + tempUrl;
		    }

			connectUrl = new URL(tempUrl);
			huc = (HttpURLConnection) connectUrl.openConnection();
			huc.setRequestMethod("GET"); // OR huc.setRequestMethod ("HEAD");
			huc.connect();
		}
		LOG.info("Url-{} validated successfully", url);
	}
}