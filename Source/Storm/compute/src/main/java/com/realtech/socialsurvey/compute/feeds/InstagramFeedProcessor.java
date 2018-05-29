package com.realtech.socialsurvey.compute.feeds;

import com.realtech.socialsurvey.compute.entities.SocialMediaTokenResponse;
import com.realtech.socialsurvey.compute.entities.response.InstagramMediaData;

import java.io.Serializable;
import java.util.List;

public interface InstagramFeedProcessor extends Serializable {
    /**
     * @author Lavanya
     */

    /**
     * Fetch feeds from Instagram
     * @param companyId
     * @param mediaToken
     * @return
     */
    public List<InstagramMediaData> fetchFeeds(long companyId, SocialMediaTokenResponse mediaToken );

}
