package com.realtech.socialsurvey.compute.feeds.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.common.FacebookAPIOperations;
import com.realtech.socialsurvey.compute.entities.FacebookToken;
import com.realtech.socialsurvey.compute.entities.response.FacebookFeedData;
import com.realtech.socialsurvey.compute.entities.response.FacebookResponse;
import com.realtech.socialsurvey.compute.feeds.FacebookFeedProcessor;
import com.realtech.socialsurvey.compute.utils.UrlHelper;


public class FacebookFeedProcessorImpl implements FacebookFeedProcessor, Serializable
{
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger( FacebookFeedProcessorImpl.class );

    private static final int RETRIES_INITIAL = 0;
    private static final int PAGE_SIZE = 200;

    long lastFetchedPostId = 0L;


    /* (non-Javadoc)
     * @see com.realtech.socialsurvey.compute.feeds.FacebookFeedProcessor#fetchFeeds(com.realtech.socialsurvey.compute.entities.FacebookToken)
     */
    public List<FacebookFeedData> fetchFeeds( long companyId, FacebookToken token )
    {
        LOG.info( "Getting feeds with id: {}", companyId );

        List<FacebookFeedData> feeds = new ArrayList<>();

        if ( token != null ) {
            String since = null;
            String until = null;
            if ( since == null || since.isEmpty() ) {
                Calendar cal = Calendar.getInstance();
                until = String.valueOf( cal.getTimeInMillis() / 1000 );
                cal.add( Calendar.DAY_OF_YEAR, -10 );
                since = String.valueOf( cal.getTimeInMillis() / 1000 );
            }

            String pageId = UrlHelper.getFacebookPageIdFromURL( token.getFacebookPageLink() );
            FacebookResponse result = FacebookAPIOperations.getInstance().fetchFeeds( pageId, token.getFacebookAccessToken(),
                since, until, "" );

            if ( result != null ) {
                feeds.addAll( result.getData() );
                while ( result != null && result.getPaging() != null && result.getPaging().getNext() != null ) {
                    Map<String, String> queryMap = UrlHelper.getQueryParamsFromUrl( result.getPaging().getNext() );

                    result = FacebookAPIOperations.getInstance().fetchFeeds( pageId, token.getFacebookAccessToken(),
                        queryMap.get( "since" ), queryMap.get( "until" ), queryMap.get( "__paging_token" ) );

                    if ( result != null ) {
                        feeds.addAll( result.getData() );
                    }
                }
            }
        }

        return feeds;
    }
}
