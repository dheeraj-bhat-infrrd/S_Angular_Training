package com.realtech.socialsurvey.compute.reviews;

import com.realtech.socialsurvey.compute.entities.SocialMediaTokenResponse;
import com.realtech.socialsurvey.compute.entities.response.FacebookReviewData;

import java.io.Serializable;
import java.util.List;


/**
 * @author Lavanya
 */

public interface FacebookReviewProcessor extends Serializable
{
    /**
     * Fetches reviews from facebook
     * @param mediaToken
     */
    List<FacebookReviewData> fetchReviews(SocialMediaTokenResponse mediaToken);
}
