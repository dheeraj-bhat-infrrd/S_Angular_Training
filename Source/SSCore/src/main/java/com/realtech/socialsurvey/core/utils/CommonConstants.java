package com.realtech.socialsurvey.core.utils;

import org.springframework.stereotype.Component;

// SS-14 By RM02 BOC

/**
 * Class containing constants to be used in the application, also contains variables holding the
 * values from property files
 */
@Component
public final class CommonConstants {

	private CommonConstants() {}

	public static final String CONFIG_PROPERTIES_FILE = "config.properties";
	public static final String SENDGRID_SENDER_USERNAME = "SENDGRID_SENDER_USERNAME";
	public static final String SENDGRID_SENDER_NAME = "SENDGRID_SENDER_NAME";
	public static final String SENDGRID_SENDER_PASSWORD = "SENDGRID_SENDER_PASSWORD";

}
// SS-14 By RM02 EOC
