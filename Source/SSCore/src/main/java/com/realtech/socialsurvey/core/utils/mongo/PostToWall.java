package com.realtech.socialsurvey.core.utils.mongo;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;

@Component
public class PostToWall implements Runnable {

	public static final Logger LOG = LoggerFactory.getLogger(PostToWall.class);
	private static final int POST_SIZE = 100;
	private static final String POST_DATA = "Lorem Ipsum is simply dummy text of the printing and typesetting industry"
			+ ". Lorem Ipsum has been the industry's standard dummy text ever since the 1500s"
			+ ", when an unknown printer took a galley of type and scrambled it to make a type specimen book";

	@Autowired
	private UserManagementService userManagementService;

	@Autowired
	private ProfileManagementService profileManagementService;

	@Override
	@Transactional
	public void run() {
		LOG.info("Started run method of PostToWall");
		
		// TODO parse emailIds
		List<String> emailIds = new ArrayList<String>();
		emailIds.add("ss_agent1@mailinator.com");

		User user = null;
		for (String email : emailIds) {
			// Fetching user with emailId
			try {
				user = userManagementService.getUserByEmail(email);
			}
			catch (InvalidInputException | NoRecordsFetchedException e) {
				LOG.error("Exception while fetching user with emailId: " + email);
				e.printStackTrace();

				continue;
			}

			// Posting to wall of user
			UserProfile selectedProfile = user.getUserProfiles().get(CommonConstants.INITIAL_INDEX);
			for (int i = 0; i < POST_SIZE; i++) {
				try {
					profileManagementService.addSocialPosts(selectedProfile, POST_DATA);
				}
				catch (InvalidInputException e) {
					LOG.info("InvalidInputException occurred while fetching regions");
				}
			}
		}
		LOG.info("Finished run method of PostToWall");
	}
}