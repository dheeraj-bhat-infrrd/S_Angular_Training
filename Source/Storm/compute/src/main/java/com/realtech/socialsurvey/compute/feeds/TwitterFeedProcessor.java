package com.realtech.socialsurvey.compute.feeds;

import java.io.Serializable;
import java.util.List;

import com.realtech.socialsurvey.compute.entities.SocialMediaTokenResponse;
import com.realtech.socialsurvey.compute.entities.response.TwitterFeedData;

/**
 * @author manish
 *
 */
public interface TwitterFeedProcessor extends Serializable
{

    /**
     * Fetch twitter tweets
     * @param mediaToken
     * @param companyId
     * @param twitterConsumerKey
     * @param twitterConsumerSecret
     * @return
     */
    List<TwitterFeedData> fetchFeed(long companyId, SocialMediaTokenResponse mediaToken,
                                    String twitterConsumerKey, String twitterConsumerSecret);
}
