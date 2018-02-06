package com.realtech.socialsurvey.compute.feeds;

import java.util.List;

import com.realtech.socialsurvey.compute.entities.FacebookToken;
import com.realtech.socialsurvey.compute.entities.response.FacebookFeedData;

/**
 * @author manish
 *
 */
public interface FacebookFeedProcessor
{
    /**
     * Fetch feeds from facebook
     * @param companyId
     * @param token
     * @return
     */
    public List<FacebookFeedData> fetchFeeds(long companyId,  FacebookToken token );
}
