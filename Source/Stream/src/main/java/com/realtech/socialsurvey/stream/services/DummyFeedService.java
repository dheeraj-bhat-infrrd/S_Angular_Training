package com.realtech.socialsurvey.stream.services;

import com.realtech.socialsurvey.stream.entities.DummyFeed;

/**
 * Insert feed
 * @author nishit
 *
 */
public interface DummyFeedService
{

    /**
     * Inserts a feed
     * @param feed
     * @return
     */
    public DummyFeed insertDummyFeed(DummyFeed feed);
}
