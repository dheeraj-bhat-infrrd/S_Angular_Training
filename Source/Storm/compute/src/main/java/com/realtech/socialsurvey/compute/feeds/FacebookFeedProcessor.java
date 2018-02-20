package com.realtech.socialsurvey.compute.feeds;

import java.io.Serializable;
import java.util.List;

import com.realtech.socialsurvey.compute.entities.SocialMediaTokenResponse;
import com.realtech.socialsurvey.compute.entities.response.FacebookFeedData;

/**
 * @author manish
 *
 */
public interface FacebookFeedProcessor extends Serializable
{
    /**
     * Fetch feeds from facebook
     * @param companyId
     * @param token
     * @return
     */
    public List<FacebookFeedData> fetchFeeds(long companyId,  SocialMediaTokenResponse mediaToken );
}
