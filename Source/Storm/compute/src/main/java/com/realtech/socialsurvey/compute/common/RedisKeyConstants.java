package com.realtech.socialsurvey.compute.common;

/**
 * Redis key constants
 * @author manish
 *
 */
public class RedisKeyConstants
{
    private RedisKeyConstants()
    {}

    public static final String WAIT_FOR_NEXT_FETCH = "waitForNextFetch";

    public static final String IS_KAFKA_DOWN = "kafkaDown";

    public static final String MODIFIEDON = "modifiedOn";
    public static final String KEYWORDS = "keywords";
    public static final String TRUSTED_SOURCES = "truestedSources";
    public static final String COMPANYKEYWORDS_KEY_PREFIX = "companykeywords:";
    public static final String COMPANY_TRUSTED_SOURCES_KEY_PREFIX = "companytrustedsources:";

    public static final String PREVIOUS = "previous";
    public static final String CURRENT = "current";
    
    public static final String SOCIAL_MONITOR_COMPANYIDS = "socialMonitorEnabledCompanyIds";
    public static final String COMPANYIDS = "companyIds";

    public static final String WAIT_FOR_NEXT_FETCH_TIME = "waitForNextFetchTime";

}
