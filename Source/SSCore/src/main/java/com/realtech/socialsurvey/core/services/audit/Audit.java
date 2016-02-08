package com.realtech.socialsurvey.core.services.audit;

import java.util.Map;

/**
 * Used for auditing
 *
 */
public interface Audit
{

    public static final String LOGIN_ATTEMPTED = "LOGIN_ATTEMPTED";
    public static final String LOGIN_SUCCESSFUL = "LOGIN_ATTEMPTED";
    public static final String AUTO_LOGIN = "AUTO_LOGIN";
    public static final String FACEBOOK_CONNECTION_INITIATED = "FACEBOOK_CONNECTION_INITIATED";
    public static final String TWITTER_CONNECTION_INITIATED = "TWITTER_CONNECTION_INITIATED";
    public static final String LINKEDIN_CONNECTION_INITIATED = "LINKEDIN_CONNECTION_INITIATED";
    public static final String GOOGLEPLUS_CONNECTION_INITIATED = "GOOGLEPLUS_CONNECTION_INITIATED";
    public static final String FACEBOOK_CONNECTION_SUCCESS = "FACEBOOK_CONNECTION_SUCCESS";
    public static final String TWITTER_CONNECTION_SUCCESS = "TWITTER_CONNECTION_SUCCESS";
    public static final String LINKEDIN_CONNECTION_SUCCESS = "LINKEDIN_CONNECTION_SUCCESS";
    public static final String GOOGLEPLUS_CONNECTION_SUCCESS = "GOOGLEPLUS_CONNECTION_SUCCESS";
    
    public static final String SESSIONID_KEY = "SESSIONID_KEY";
    public static final String IP_ADDRESS_KEY = "IP_ADDRESS_KEY";
    public static final String USER_ID_KEY = "USER_ID_KEY";
    
    
    
    /**
     * Method to audit
     * @param auditType
     * @param auditParams
     * @param auditMetaData
     */
    public void auditActions(String auditType, Map<String, String> auditParams, String... auditMetaData);
    
}
