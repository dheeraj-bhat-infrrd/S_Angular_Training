package com.realtech.socialsurvey.compute.feeds;

import com.realtech.socialsurvey.compute.entities.SocialMediaTokenResponse;
import com.realtech.socialsurvey.compute.entities.response.InstagramMediaData;

import java.util.List;

public interface InstagramFeedProcessor {
    /**
     * @author Lavanya
     */

    /**
     * Fetch feeds from facebook
     * @param companyId
     * @param mediaToken
     * @return
     */
    public List<InstagramMediaData> fetchFeeds(long companyId, SocialMediaTokenResponse mediaToken );

}
