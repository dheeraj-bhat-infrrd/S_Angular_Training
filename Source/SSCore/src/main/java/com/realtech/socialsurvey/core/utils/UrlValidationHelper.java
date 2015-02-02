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
		URL u = new URL(url);
		HttpURLConnection huc = (HttpURLConnection) u.openConnection();
		huc.setRequestMethod("GET"); // OR huc.setRequestMethod ("HEAD");
		huc.connect();
		LOG.info("Url-{} validated successfully", url);
	}

}
