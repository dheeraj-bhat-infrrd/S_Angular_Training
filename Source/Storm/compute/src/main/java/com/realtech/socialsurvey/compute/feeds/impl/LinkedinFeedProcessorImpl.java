package com.realtech.socialsurvey.compute.feeds.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.common.LinkedinAPIOperations;
import com.realtech.socialsurvey.compute.entities.LinkedInToken;
import com.realtech.socialsurvey.compute.entities.LinkedInTokenForSM;
import com.realtech.socialsurvey.compute.entities.SocialMediaTokenResponse;
import com.realtech.socialsurvey.compute.entities.TwitterToken;
import com.realtech.socialsurvey.compute.entities.response.linkedin.LinkedinFeedData;
import com.realtech.socialsurvey.compute.entities.response.linkedin.LinkedinFeedResponse;
import com.realtech.socialsurvey.compute.feeds.LinkedinFeedProcessor;
import com.realtech.socialsurvey.compute.utils.UrlHelper;


/**
 * @author manish
 *
 */
public class LinkedinFeedProcessorImpl implements LinkedinFeedProcessor
{
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger( LinkedinFeedProcessorImpl.class );

    private static final int RETRIES_INITIAL = 0;
    private static final int PAGE_SIZE = 200;

    long lastFetchedPostId = 0L;


    /* (non-Javadoc)
     * @see com.realtech.socialsurvey.compute.feeds.LinkedinFeedProcessor#fetchFeeds(com.realtech.socialsurvey.compute.entities.LinkedInToken)
     */
    public List<LinkedinFeedData> fetchFeeds( long companyId, SocialMediaTokenResponse mediaToken )
    {
        LOG.info( "Getting feeds with id: {}", companyId );
        List<LinkedinFeedData> feeds = new ArrayList<>();
        
        LinkedInTokenForSM token = null;
        if ( mediaToken != null ) {
            token = mediaToken.getSocialMediaTokens().getLinkedInToken();
        }

        if ( token != null ) {
            String pageId = UrlHelper.getLinkedinPageIdFromURL( token.getLinkedInPageLink() );
            LinkedinFeedResponse response = LinkedinAPIOperations.getInstance().fetchFeeds( pageId,
                token.getLinkedInAccessToken(), 0, null );
            if ( response != null ) {
                feeds.addAll( response.getValues() );
                while ( response.getTotal() > ( response.getStart() + 1 ) * response.getCount() ) {
                    response = LinkedinAPIOperations.getInstance().fetchFeeds( pageId, token.getLinkedInAccessToken(),
                        response.getStart() + 1, null );
                    if ( response != null ) {
                        feeds.addAll( response.getValues() );
                    } else {
                        break;
                    }
                }
            }
        }

        return feeds;
    }
}
