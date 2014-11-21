package com.realtech.socialsurvey.core.commons;

/**
 * Holds application level constants
 *
 */
public interface CommonConstants {

	// default company id for application. if any entity is linked to this id, then its an orphan entity
	public static final long DEFAULT_COMPANY_ID = 1;
	
	// no profile constant. this is linked to a user profile in case no profile has been attached to it
	public static final int NO_PROFILE_ID = 1;
}
