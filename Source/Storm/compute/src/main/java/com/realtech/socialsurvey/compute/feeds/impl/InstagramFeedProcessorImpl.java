package com.realtech.socialsurvey.compute.feeds.impl;

import com.realtech.socialsurvey.compute.common.ComputeConstants;
import com.realtech.socialsurvey.compute.common.FacebookAPIOperations;
import com.realtech.socialsurvey.compute.common.SSAPIOperations;
import com.realtech.socialsurvey.compute.dao.RedisSocialMediaStateDao;
import com.realtech.socialsurvey.compute.dao.impl.RedisSocialMediaStateDaoImpl;
import com.realtech.socialsurvey.compute.entities.FacebookXUsageHeader;
import com.realtech.socialsurvey.compute.entities.InstagramTokenForSM;
import com.realtech.socialsurvey.compute.entities.SocialMediaTokenResponse;
import com.realtech.socialsurvey.compute.entities.response.ConnectedInstagramAccount;
import com.realtech.socialsurvey.compute.entities.response.InstagramMedia;
import com.realtech.socialsurvey.compute.entities.response.InstagramMediaData;
import com.realtech.socialsurvey.compute.entities.response.InstagramResponse;
import com.realtech.socialsurvey.compute.enums.ProfileType;
import com.realtech.socialsurvey.compute.exception.FacebookFeedException;
import com.realtech.socialsurvey.compute.feeds.InstagramFeedProcessor;
import com.realtech.socialsurvey.compute.utils.ConversionUtils;
import com.realtech.socialsurvey.compute.utils.UrlHelper;
import okhttp3.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class InstagramFeedProcessorImpl implements InstagramFeedProcessor {
    /**
     * @author Lavanya
     */
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger( InstagramFeedProcessorImpl.class );

    private RedisSocialMediaStateDao redisSocialMediaStateDao;

    private static final int USAGE_PERCENT = 80;

    private static final int TOKEN_BLOCK_TIME = 3600;

    private static final int PAGE_BLOCK_TIME = 86400;

    private static final String X_PAGE_USAGE = "X-Page-Usage";

    private static final String X_APP_USAGE = "X-App-Usage";

    private static final int LIMIT = 50;

    public InstagramFeedProcessorImpl() {
        this.redisSocialMediaStateDao = new RedisSocialMediaStateDaoImpl();
    }

    @Override
    public List<InstagramMediaData> fetchFeeds(long companyId, SocialMediaTokenResponse mediaToken) {

        LOG.info("Getting instagram feed with companyId {}", companyId);

        List<InstagramMediaData> instagramMediaData = new ArrayList<>();
        InstagramMedia instagramMedia ;
        ConnectedInstagramAccount instagramAccount;
        InstagramTokenForSM igToken = mediaToken.getSocialMediaTokens().getInstagramToken();

        String pageId = UrlHelper.getInstagramPageIdFromURL( igToken.getPageLink() );
        String lastFetchedKey = "IG_" + mediaToken.getProfileType().toString() + "_" + mediaToken.getIden() + "_" + pageId;

        String lastFetchedIgId = redisSocialMediaStateDao.getLastFetched( lastFetchedKey );

        if(lastFetchedIgId == null || lastFetchedIgId.isEmpty() ){
            //run the extractor for the first time so get latest 50 records
            instagramAccount = fetchFeeds( igToken.getId(), igToken.getAccessTokenToPost(), mediaToken.getIden(), mediaToken.getProfileType() );
            if ( instagramAccount != null && instagramAccount.getMedia() != null){
                instagramMediaData.addAll(instagramAccount.getMedia().getData());
                //save the first record in the redis
                redisSocialMediaStateDao.saveLastFetched(lastFetchedKey, instagramMediaData.get(0).getIgId(), "" );
            }
        } else{
            //Get all the feeds until we encounter the lastFetchedIgId
            instagramAccount = fetchFeeds( igToken.getId(), igToken.getAccessTokenToPost(), mediaToken.getIden(), mediaToken.getProfileType() );
            if( instagramAccount != null){
                instagramMedia = instagramAccount.getMedia();
                instagramMediaData.addAll(addInstagramMedia(instagramMedia, lastFetchedIgId));
               /* Here we are checking for size because if lastFetchedIg is encountered
                then we'll fetch only till that record ignoring the rest.Hence the size < 50*/
                while( instagramMediaData.size() == LIMIT && instagramMedia.getPaging() != null && instagramMedia.getPaging().getNext() != null ){
                    instagramMedia = fetchFeeds(pageId, instagramAccount.getId(), igToken.getAccessTokenToPost(),
                            instagramMedia.getPaging().getCursors().getAfter(), mediaToken.getIden(), mediaToken.getProfileType());
                    instagramMediaData.addAll(addInstagramMedia(instagramMedia, lastFetchedIgId));
                }
                //save the lastestIgId for consecutive fetches
                if (!instagramMediaData.isEmpty())
                    redisSocialMediaStateDao.saveLastFetched(lastFetchedKey, instagramMediaData.get(0).getIgId(), lastFetchedIgId);
            }
            else
                LOG.warn("Facebook account with ID {} is currently not linked with instagram" , igToken.getId());
        }
        return instagramMediaData;
    }

    private List<InstagramMediaData> addInstagramMedia(InstagramMedia instagramMedia, String lastFetchedIgId) {
        if ( instagramMedia != null && !instagramMedia.getData().isEmpty() ) {
            List<String> igIds = instagramMedia.getData().stream().map( InstagramMediaData::getIgId ).collect(Collectors.toList());
            if ( igIds.contains(lastFetchedIgId) ) {
                return instagramMedia.getData().subList(0, igIds.indexOf(lastFetchedIgId));
            } else {
                return instagramMedia.getData();
            }
        } else
            return Collections.emptyList();
    }

    /**
     * Fetches the first batch of instagram media.
     * @param pageId
     * @param accessToken
     * @param iden
     * @param profileType
     * @return
     */
    private ConnectedInstagramAccount fetchFeeds( String pageId, String accessToken, long iden, ProfileType profileType ) {
        try{
            Response<InstagramResponse> response = FacebookAPIOperations.getInstance().fetchMedia( pageId, accessToken );

            if ( response != null ) {
                 checkRateLimiting( response.headers(), pageId, accessToken );
                return response.body().getConnectedInstagramAccount();
            }
        } catch (FacebookFeedException e) {
            handleError(pageId, e, iden, profileType);
        }
        return null;
    }

    /**
     * Fetches the instagram media using cursor
     * @param igAccountId
     * @param accessToken
     * @param after
     * @param iden
     * @param profileType
     * @return
     */
    private InstagramMedia fetchFeeds( String pageId, String igAccountId, String accessToken, String after, long iden,
        ProfileType profileType ) {
        try {
            Response<InstagramMedia> response = FacebookAPIOperations.getInstance().fetchMedia( igAccountId, accessToken, after );

            if ( response != null ) {
                checkRateLimiting( response.headers(), pageId, accessToken );
                return response.body();
            }
        } catch (FacebookFeedException e) {
            handleError(igAccountId, e, iden, profileType );
        }

        return null;
    }

    private void handleError( String pageId, FacebookFeedException e, long iden, ProfileType profileType ) {
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
        } else if(e.getFacebookErrorCode() == 190) {
            SSAPIOperations.getInstance().updateTokenExpiryAlert(iden, ComputeConstants.INSTAGRAM_TOKEN_EXPIRY_FIELD,
                true, profileType.getValue());
        }

    }

    private void checkRateLimiting(Headers headers, String pageId, String accessToken )
    {
        String xAppUsageHeaderStr = headers.get( X_APP_USAGE );
        String xPageUsageHeaderStr = headers.get( X_PAGE_USAGE );

        if ( xPageUsageHeaderStr != null ) {
            FacebookXUsageHeader xPageUsageHeader = ConversionUtils.deserialize( xPageUsageHeaderStr,
                    FacebookXUsageHeader.class );
            LOG.debug( "Response contains X-Page-Usage header, {}", xPageUsageHeader );
            if ( xPageUsageHeader.getCallCount() >= USAGE_PERCENT || xPageUsageHeader.getTotalCputime() >= USAGE_PERCENT
                    || xPageUsageHeader.getTotalTime() >= USAGE_PERCENT) {
                redisSocialMediaStateDao.setFacebookLockForPage( pageId, PAGE_BLOCK_TIME );
            }
        }

        if ( xAppUsageHeaderStr != null ) {
            FacebookXUsageHeader xAppUsageHeader = ConversionUtils.deserialize( xAppUsageHeaderStr,
                    FacebookXUsageHeader.class );
            LOG.debug( "Response contains X-App-Usage header, {}", xAppUsageHeader );
            if ( xAppUsageHeader.getCallCount() >= USAGE_PERCENT || xAppUsageHeader.getTotalCputime() >= USAGE_PERCENT
                    || xAppUsageHeader.getTotalTime() >= USAGE_PERCENT) {
                redisSocialMediaStateDao.setFacebookLockForToken( accessToken, TOKEN_BLOCK_TIME );
            }
        }
    }

}
