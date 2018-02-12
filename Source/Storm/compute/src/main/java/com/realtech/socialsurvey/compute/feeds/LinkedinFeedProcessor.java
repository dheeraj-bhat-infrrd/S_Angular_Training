package com.realtech.socialsurvey.compute.feeds;

import java.util.List;

import com.realtech.socialsurvey.compute.entities.LinkedInToken;
import com.realtech.socialsurvey.compute.entities.response.linkedin.LinkedinFeedData;

/**
 * @author manish
 *
 */
public interface LinkedinFeedProcessor
{
    /**
     * Method to fetch record for Linkedin
     * @param companyId
     * @param token
     * @return
     */
    public List<LinkedinFeedData> fetchFeeds(long companyId,  LinkedInToken token );
}
