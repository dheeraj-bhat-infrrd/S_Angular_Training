package com.realtech.socialsurvey.core.commons;

/**
 * Holds application level constants
 */
public interface CommonConstants {

	// default company id for application. if any entity is linked to this id, then its an orphan
	// entity
	public static final int DEFAULT_COMPANY_ID = 1;
	// no profile constant. this is linked to a user profile in case no profile has been attached to
	// it
	public static final int PROFILES_MASTER_NO_PROFILE_ID = 1;
	public static final int PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID = 2;
	public static final int PROFILES_MASTER_REGION_ADMIN_PROFILE_ID = 3;
	public static final int PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID = 4;
	public static final int PROFILES_MASTER_AGENT_PROFILE_ID = 5;
	public static final int DEFAULT_REGION_ID = 1;
	public static final int DEFAULT_BRANCH_ID = 1;
	public static final int DEFAULT_AGENT_ID = 1;
	public static final String DEFAULT_SOURCE_APPLICATION = "AP";
	public static final String GUEST_USER_NAME = "GUEST";
	public static final String ADMIN_USER_NAME = "ADMIN";
	public static final Integer STATUS_ACTIVE = 1;
	public static final Integer STATUS_INACTIVE = 0;
	public static final String USER_INVITE_INVITATION_PARAMETERS_COLUMN = "invitationParameters";
	public static final String USER_INVITE_INVITATION_VALID_UNTIL = "invitationValidUntil";
	
	public static final String REQUEST_MAPPING_SHOW_REGISTRATION = "showregistrationpage.do";
}
