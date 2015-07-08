package com.realtech.socialsurvey.core.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UrlValidationHelper {

	private static final Logger LOG = LoggerFactory.getLogger(UrlValidationHelper.class);

	public void validateUrl(String url) throws IOException {
		validateInputUrl(url);
	}

	public String buildValidUrl(String url) throws IOException {
		return validateInputUrl(url);
	}

	private String validateInputUrl(String url) throws MalformedURLException, IOException, ProtocolException {
		LOG.info("Checking if url exists : {}", url);

		// checking for https protocol
		String tempUrl = new String(url);
		if (!tempUrl.startsWith("https://")) {
			tempUrl = "https://" + tempUrl;
		}

		URL connectUrl = null;
		HttpURLConnection httpURLConnection;
		try {
			connectUrl = new URL(tempUrl);
			httpURLConnection = (HttpURLConnection) connectUrl.openConnection();
			httpURLConnection.setRequestMethod("GET"); // OR huc.setRequestMethod ("HEAD");
			httpURLConnection.connect();
		}
		catch (IOException e) {
			LOG.info("Url-{} does not support https", url);

			// checking for http protocol
			tempUrl = new String(url);
			if (!tempUrl.startsWith("http://")) {
				tempUrl = "http://" + tempUrl;
			}

			connectUrl = new URL(tempUrl);
			httpURLConnection = (HttpURLConnection) connectUrl.openConnection();
			httpURLConnection.setRequestMethod("GET"); // OR huc.setRequestMethod ("HEAD");
			httpURLConnection.connect();
		}

		LOG.info("Url-{} validated successfully", tempUrl);
		return tempUrl;
	}
}