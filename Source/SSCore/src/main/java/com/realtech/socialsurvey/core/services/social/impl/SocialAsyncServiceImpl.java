package com.realtech.socialsurvey.core.services.social.impl;

import java.util.Map;
import java.util.concurrent.Future;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.realtech.socialsurvey.core.entities.LinkedInToken;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.social.SocialAsyncService;

@Component
public class SocialAsyncServiceImpl implements SocialAsyncService {

	private static final Logger LOG = LoggerFactory.getLogger(SocialAsyncServiceImpl.class);

	@Autowired
	private ProfileManagementService profileManagementService;

	@Value("${LINKED_IN_REST_API_URI}")
	private String linkedInRestApiUri;

	@Async
	@Override
	public Future<OrganizationUnitSettings> linkedInDataUpdate(String collection, OrganizationUnitSettings unitSettings, LinkedInToken linkedInToken) {
		LOG.info("Method linkedInDataUpdate() called from SocialAsyncServiceImpl");

		StringBuilder linkedInFetch = new StringBuilder(linkedInRestApiUri).append("(id,summary,picture-url)");
		linkedInFetch.append("?oauth2_access_token=").append(linkedInToken.getLinkedInAccessToken());
		linkedInFetch.append("&format=json");

		Map<String, Object> mapProfile = null;
		try {
			HttpClient httpclient = HttpClientBuilder.create().build();
			HttpGet httpget = new HttpGet(linkedInFetch.toString());
			String responseBody = httpclient.execute(httpget, new BasicResponseHandler());
			mapProfile = new Gson().fromJson(responseBody, new TypeToken<Map<String, String>>() {}.getType());
		}
		catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

		if (mapProfile.containsKey("summary")) {
			String summary = (String) mapProfile.get("summary");
			if (summary != null && !summary.equals("")) {
				try {
					unitSettings.getContact_details().setAbout_me(summary);
					profileManagementService.updateContactDetails(collection, unitSettings, unitSettings.getContact_details());
				}
				catch (InvalidInputException e) {
					LOG.error(e.getMessage(), e);
				}
			}
		}

		if (mapProfile.containsKey("pictureUrl")) {
			String pictureUrl = (String) mapProfile.get("pictureUrl");
			if (pictureUrl != null && !pictureUrl.equals("")) {
				try {
					unitSettings.setProfileImageUrl(pictureUrl);
					profileManagementService.updateProfileImage(collection, unitSettings, pictureUrl);
				}
				catch (InvalidInputException e) {
					LOG.error(e.getMessage(), e);
				}
			}
		}

		LOG.info("Method linkedInDataUpdate() finished from SocialAsyncServiceImpl");
		return new AsyncResult<OrganizationUnitSettings>(unitSettings);
	}
}