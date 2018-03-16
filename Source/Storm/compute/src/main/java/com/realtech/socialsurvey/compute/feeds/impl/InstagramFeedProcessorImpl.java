package com.realtech.socialsurvey.compute.feeds.impl;

import com.realtech.socialsurvey.compute.common.FacebookAPIOperations;
import com.realtech.socialsurvey.compute.dao.RedisSocialMediaStateDao;
import com.realtech.socialsurvey.compute.dao.impl.RedisSocialMediaStateDaoImpl;
import com.realtech.socialsurvey.compute.entities.FacebookTokenForSM;
import com.realtech.socialsurvey.compute.entities.SocialMediaTokenResponse;
import com.realtech.socialsurvey.compute.entities.response.InstagramMedia;
import com.realtech.socialsurvey.compute.entities.response.InstagramMediaData;
import com.realtech.socialsurvey.compute.exception.FacebookFeedException;
import com.realtech.socialsurvey.compute.feeds.InstagramFeedProcessor;
import com.realtech.socialsurvey.compute.utils.UrlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Response;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InstagramFeedProcessorImpl implements InstagramFeedProcessor {
    /**
     * @author Lavanya
     */

    private static final Logger LOG = LoggerFactory.getLogger( InstagramFeedProcessorImpl.class );

    private RedisSocialMediaStateDao redisSocialMediaStateDao;
    private static final String limit = "50";
    public InstagramFeedProcessorImpl() {
        this.redisSocialMediaStateDao = new RedisSocialMediaStateDaoImpl();
    }

    @Override
    public List<InstagramMediaData> fetchFeeds(long companyId, SocialMediaTokenResponse mediaToken) {

        LOG.info("Getting instagram feed with companyId {}", companyId);

        List<InstagramMediaData> instagramMediaData = null;
        InstagramMedia instagramMedia;
        FacebookTokenForSM fbToken = mediaToken.getSocialMediaTokens().getFacebookToken();

        String pageId = UrlHelper.getFacebookPageIdFromURL( fbToken.getFacebookPageLink() );
        String lastFetchedKey = mediaToken.getProfileType().toString() + "_" + mediaToken.getIden() + "_" + pageId;

        String lastFetchedIgId = redisSocialMediaStateDao.getLastFetched( lastFetchedKey );

        if(lastFetchedIgId == null || lastFetchedIgId.isEmpty() ){
            //run the extractor for the first time so get latest 50 records
            instagramMedia = fetchFeeds(pageId, fbToken.getFacebookAccessToken(), "");
            if ( instagramMedia != null){
                instagramMediaData = instagramMedia.getData();
                //save the first record in the redis
                redisSocialMediaStateDao.saveLastFetched(lastFetchedKey, instagramMediaData.get(0).getIgId(), "" );
            }
        } else{
            //Get all the feeds untill we encounter the lastFetchedIgId
                instagramMedia = fetchFeeds(pageId, fbToken.getFacebookAccessToken(), "");
                do{
                    if(instagramMedia != null){
                        Map<String, String> queryMap = UrlHelper.getQueryParamsFromUrl( instagramMedia.getPaging().getNext() );

                        List<String> igIds =  instagramMedia.getData().stream().map(x -> x.getIgId()).collect(Collectors.toList());
                        if(igIds.contains(lastFetchedIgId)){
                            instagramMediaData.addAll(instagramMedia.getData().subList(0, igIds.indexOf(lastFetchedIgId)));
                            break;
                        } else{
                            instagramMediaData.addAll(instagramMedia.getData());
                        }
                    }
                } while (instagramMedia != null && instagramMedia.getPaging().getNext() != null);
            //save the lastestigId for
            if (instagramMediaData != null)
                redisSocialMediaStateDao.saveLastFetched(lastFetchedKey, instagramMediaData.get(0).getIgId(), lastFetchedIgId);
        }

        return instagramMediaData;
    }

    private InstagramMedia fetchFeeds(String pageId, String accessToken, String after) {
        try {
            Response<InstagramMedia> response = FacebookAPIOperations.getInstance().fetchMedia( pageId, accessToken, after );

            if ( response != null ) {
               // checkRateLimiting( response.headers(), pageId, accessToken );
                return response.body();
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
