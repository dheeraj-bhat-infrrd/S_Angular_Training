package com.realtech.socialsurvey.compute.feeds;

import java.io.Serializable;
import java.util.List;

import com.realtech.socialsurvey.compute.entities.LinkedInToken;
import com.realtech.socialsurvey.compute.entities.SocialMediaTokenResponse;
import com.realtech.socialsurvey.compute.entities.response.linkedin.LinkedinFeedData;

/**
 * @author manish
 *
 */
public interface LinkedinFeedProcessor extends Serializable
{
    /**
     * Method to fetch record for Linkedin
     * @param companyId
     * @param token
     * @return
     */
    public List<LinkedinFeedData> fetchFeeds(long companyId, SocialMediaTokenResponse mediaToken);
}
