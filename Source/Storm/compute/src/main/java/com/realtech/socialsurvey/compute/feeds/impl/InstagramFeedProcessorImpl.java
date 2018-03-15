package com.realtech.socialsurvey.compute.feeds.impl;

import com.realtech.socialsurvey.compute.common.FacebookAPIOperations;
import com.realtech.socialsurvey.compute.dao.RedisSocialMediaStateDao;
import com.realtech.socialsurvey.compute.dao.impl.RedisSocialMediaStateDaoImpl;
import com.realtech.socialsurvey.compute.entities.FacebookToken;
import com.realtech.socialsurvey.compute.entities.FacebookTokenForSM;
import com.realtech.socialsurvey.compute.entities.SocialMediaTokenResponse;
import com.realtech.socialsurvey.compute.entities.response.FacebookResponse;
import com.realtech.socialsurvey.compute.entities.response.InstagramFeedData;
import com.realtech.socialsurvey.compute.entities.response.InstagramMedia;
import com.realtech.socialsurvey.compute.entities.response.InstagramMediaData;
import com.realtech.socialsurvey.compute.exception.FacebookFeedException;
import com.realtech.socialsurvey.compute.feeds.InstagramFeedProcessor;
import com.realtech.socialsurvey.compute.utils.UrlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InstagramFeedProcessorImpl implements InstagramFeedProcessor {
    /**
     * @author Lavanya
     */

    private static final Logger LOG = LoggerFactory.getLogger( InstagramFeedProcessorImpl.class );

    private RedisSocialMediaStateDao redisSocialMediaStateDao;

    public InstagramFeedProcessorImpl() {
        this.redisSocialMediaStateDao = new RedisSocialMediaStateDaoImpl();
    }

    @Override
    public List<InstagramMediaData> fetchFeeds(long companyId, SocialMediaTokenResponse mediaToken) {

        LOG.info("Getting instagram feed with companyId {}", companyId);

        List<InstagramMediaData> instagramMediaData = null;

        FacebookTokenForSM fbToken = mediaToken.getSocialMediaTokens().getFacebookToken();

        String pageId = UrlHelper.getFacebookPageIdFromURL( fbToken.getFacebookPageLink() );
        String lastFetchedKey = mediaToken.getProfileType().toString() + "_" + mediaToken.getIden() + "_" + pageId;

        String lastFetchedTimeStamp = redisSocialMediaStateDao.getLastFetched( lastFetchedKey );

        if(lastFetchedTimeStamp == null){
            //todo Running the extractor for the first time so get latest 500 records
           InstagramMedia instagramMedia =  fetchFeeds(pageId, fbToken.getFacebookAccessToken());
           if ( instagramMedia != null){
               instagramMediaData = instagramMedia.getData();
               //todo save the lastFetchedTimeStamp in redis
           }

        } else{
            //Get all the feeds untill we encounter the lastFetchedMedia
        }

        return instagramMediaData;
    }

    private InstagramMedia fetchFeeds(String pageId, String accessToken) {
        try {
            Response<InstagramFeedData> response = FacebookAPIOperations.getInstance().fetchFirstMedia( pageId, accessToken );

            if ( response != null ) {
               // checkRateLimiting( response.headers(), pageId, accessToken );
                return response.body().getMedia();
            }
        } catch (FacebookFeedException e) {
           /* if(e.getFacebookErrorCode() == 4){
                // Application level
                redisSocialMediaStateDao.setFacebookLockForApplication(TOKEN_BLOCK_TIME );
            } else if(e.getFacebookErrorCode() == 17){
                // Account level
                redisSocialMediaStateDao.setFacebookLockForPage( pageId, PAGE_BLOCK_TIME );
            } else if(e.getFacebookErrorCode() == 32){
                // Page level rate limit
                redisSocialMediaStateDao.setFacebookLockForPage( pageId, PAGE_BLOCK_TIME );
            }*/
        }

        return null;
    }
}
