package com.realtech.socialsurvey.core.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Utils {

	private final String REGION_PROFILE_URL_PATTERN = "/%s/region/%s";
	private final String BRANCH_PROFILE_URL_PATTERN = "/%s/branch/%s";
	private static final Logger LOG = LoggerFactory.getLogger(Utils.class);

	/**
	 * Method to generate region profile url based on company profile name and region profile name
	 * 
	 * @param companyProfileName
	 * @param regionProfileName
	 * @return
	 */
	public String generateRegionProfileUrl(String companyProfileName, String regionProfileName) {
		LOG.info("Method generateRegionProfileUrl called for companyProfileName:" + companyProfileName + " and regionProfileName:"
				+ regionProfileName);
		String profileUrl = null;
		profileUrl = String.format(REGION_PROFILE_URL_PATTERN, companyProfileName, regionProfileName);

		LOG.info("Method generateRegionProfileUrl excecuted. Returning profile url:" + profileUrl);
		return profileUrl;
	}

	/**
	 * Method to generate branch profile url
	 * 
	 * @param companyProfileName
	 * @param branchProfileName
	 * @return
	 */
	public String generateBranchProfileUrl(String companyProfileName, String branchProfileName) {
		LOG.info("Method generateBranchProfileUrl called for companyProfileName:" + companyProfileName + " and branchProfileName:"
				+ branchProfileName);
		String profileUrl = null;
		profileUrl = String.format(BRANCH_PROFILE_URL_PATTERN, companyProfileName, branchProfileName);
		LOG.info("Method generateBranchProfileUrl excecuted. Returning profile url:" + profileUrl);
		return profileUrl;
	}
}
