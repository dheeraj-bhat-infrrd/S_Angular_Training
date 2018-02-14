package com.realtech.socialsurvey.compute.feeds;

import java.io.Serializable;
import java.util.List;

import com.realtech.socialsurvey.compute.entities.TwitterToken;
import com.realtech.socialsurvey.compute.entities.response.TwitterFeedData;

/**
 * @author manish
 *
 */
public interface TwitterFeedProcessor extends Serializable
{

    /**
     * Fetch twitter tweets
     * @param companyId
     * @param token
     * @return
     */
    List<TwitterFeedData> fetchFeed( long companyId,  TwitterToken token );
}
