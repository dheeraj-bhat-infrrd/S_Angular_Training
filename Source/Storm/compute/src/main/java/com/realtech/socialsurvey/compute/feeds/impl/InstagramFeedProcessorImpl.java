package com.realtech.socialsurvey.compute.feeds.impl;

import com.realtech.socialsurvey.compute.common.FacebookAPIOperations;
import com.realtech.socialsurvey.compute.dao.RedisSocialMediaStateDao;
import com.realtech.socialsurvey.compute.dao.impl.RedisSocialMediaStateDaoImpl;
import com.realtech.socialsurvey.compute.entities.FacebookTokenForSM;
import com.realtech.socialsurvey.compute.entities.SocialMediaTokenResponse;
import com.realtech.socialsurvey.compute.entities.response.ConnectedInstagramAccount;
import com.realtech.socialsurvey.compute.entities.response.InstagramMedia;
import com.realtech.socialsurvey.compute.entities.response.InstagramMediaData;
import com.realtech.socialsurvey.compute.exception.FacebookFeedException;
import com.realtech.socialsurvey.compute.feeds.InstagramFeedProcessor;
import com.realtech.socialsurvey.compute.utils.UrlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InstagramFeedProcessorImpl implements InstagramFeedProcessor {
    /**
     * @author Lavanya
     */
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger( InstagramFeedProcessorImpl.class );

    private RedisSocialMediaStateDao redisSocialMediaStateDao;

    private static final int TOKEN_BLOCK_TIME = 3600;

    private static final int PAGE_BLOCK_TIME = 86400;

    private static final String X_PAGE_USAGE = "X-Page-Usage";

    private static final String X_APP_USAGE = "X-App-Usage";

    public InstagramFeedProcessorImpl() {
        this.redisSocialMediaStateDao = new RedisSocialMediaStateDaoImpl();
    }

    @Override
    public List<InstagramMediaData> fetchFeeds(long companyId, SocialMediaTokenResponse mediaToken) {

        LOG.info("Getting instagram feed with companyId {}", companyId);

        List<InstagramMediaData> instagramMediaData = new ArrayList<>();
        InstagramMedia instagramMedia = null;
        ConnectedInstagramAccount instagramAccount;
        FacebookTokenForSM fbToken = mediaToken.getSocialMediaTokens().getFacebookToken();

        String pageId = UrlHelper.getFacebookPageIdFromURL( fbToken.getFacebookPageLink() );
        String lastFetchedKey = mediaToken.getProfileType().toString() + "_" + mediaToken.getIden() + "_" + pageId;

        String lastFetchedIgId = redisSocialMediaStateDao.getLastFetched( lastFetchedKey );

        if(lastFetchedIgId == null || lastFetchedIgId.isEmpty() ){
            //run the extractor for the first time so get latest 50 records
            instagramAccount = fetchFeeds( pageId, fbToken.getFacebookAccessToken() );
            if ( instagramAccount != null){
                instagramMediaData = instagramAccount.getMedia().getData();
                //save the first record in the redis
                redisSocialMediaStateDao.saveLastFetched(lastFetchedKey, instagramMediaData.get(0).getIgId(), "" );
            }
        } else{
            //Get all the feeds until we encounter the lastFetchedIgId
            instagramAccount = fetchFeeds( pageId, fbToken.getFacebookAccessToken() );
            if( instagramAccount != null ){
                instagramMedia = instagramAccount.getMedia();
                do {
                    if (instagramMedia != null && !instagramMedia.getData().isEmpty()) {
                        List<String> igIds = instagramMedia.getData().stream().map( InstagramMediaData::getIgId ).collect(Collectors.toList());
                        if ( igIds.contains(lastFetchedIgId) ) {
                            instagramMediaData.addAll(instagramMedia.getData().subList(0, igIds.indexOf(lastFetchedIgId)));
                            break;
                        } else {
                            instagramMediaData.addAll(instagramMedia.getData());
                        }

                        if( instagramMedia.getPaging().getNext() != null )
                            instagramMedia = fetchFeeds(instagramAccount.getId(), fbToken.getFacebookAccessToken(),
                                    instagramMedia.getPaging().getCursors().getAfter());
                    }
                }while( instagramMedia.getPaging().getNext() != null );
                //save the lastestIgId for consecutive fetches
                if (instagramMediaData.isEmpty())
                    redisSocialMediaStateDao.saveLastFetched(lastFetchedKey, instagramMediaData.get(0).getIgId(), lastFetchedIgId);
            }
            else
                LOG.warn("Facebook account with ID {} is currently not linked with instagram" , fbToken.getFacebookId());
        }

        return instagramMediaData;
    }

    /**
     * Fetches the first batch of instagram media.
     * @param pageId
     * @param accessToken
     * @return
     */
    private ConnectedInstagramAccount fetchFeeds(String pageId, String accessToken) {
        try{
            Response<ConnectedInstagramAccount> response = FacebookAPIOperations.getInstance().fetchMedia( pageId, accessToken );

            if ( response != null ) {
                // checkRateLimiting( response.headers(), pageId, accessToken );
                return response.body();
            }
        } catch (FacebookFeedException e) {
            handleError(pageId, e);
        }
        return null;
    }

    /**
     * Fetches the instagram media using cursor
     * @param pageId
     * @param accessToken
     * @param after
     * @return
     */
    private InstagramMedia fetchFeeds(String pageId, String accessToken, String after) {
        try {
            Response<InstagramMedia> response = FacebookAPIOperations.getInstance().fetchMedia( pageId, accessToken, after );

            if ( response != null ) {
                // checkRateLimiting( response.headers(), pageId, accessToken );
                return response.body();
            }
        } catch (FacebookFeedException e) {
            handleError(pageId, e);
        }

        return null;
    }

    private void handleError(String pageId, FacebookFeedException e) {
        if(e.getFacebookErrorCode() == 4){
            // Application level
            redisSocialMediaStateDao.setFacebookLockForApplication(TOKEN_BLOCK_TIME );
        } else if(e.getFacebookErrorCode() == 17){
            // Account level
            redisSocialMediaStateDao.setFacebookLockForPage( pageId, PAGE_BLOCK_TIME );
        } else if(e.getFacebookErrorCode() == 32){
            // Page level rate limit
            redisSocialMediaStateDao.setFacebookLockForPage( pageId, PAGE_BLOCK_TIME );
        } else if(e.getFacebookErrorCode() == 100){
            //trying to fetch instagram media from user
            LOG.error(e.getMessage());
        }

    }

}
