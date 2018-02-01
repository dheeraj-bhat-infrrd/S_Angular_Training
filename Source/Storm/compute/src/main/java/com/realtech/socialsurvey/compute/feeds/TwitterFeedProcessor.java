package com.realtech.socialsurvey.compute.feeds;

import java.util.List;

import com.realtech.socialsurvey.compute.entities.TwitterToken;
import com.realtech.socialsurvey.compute.entities.response.TwitterFeedData;

import twitter4j.Status;

public interface TwitterFeedProcessor
{

    List<TwitterFeedData> fetchFeed( long iden, String collection, TwitterToken token );

}
