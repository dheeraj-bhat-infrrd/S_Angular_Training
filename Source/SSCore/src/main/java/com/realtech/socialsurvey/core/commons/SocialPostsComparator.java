package com.realtech.socialsurvey.core.commons;

import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.core.entities.SocialPost;

/**
 * Compares AgentRankingReport based on their average score
 */
public class SocialPostsComparator implements Comparator<SocialPost> {

    private static final Logger LOG = LoggerFactory.getLogger(SocialPostsComparator.class);

    @Override
    public int compare(SocialPost post1, SocialPost post2) {
        LOG.debug("Comparing SurveyDetails");

        if (post1.getTimeInMillis() > post2.getTimeInMillis()) {
            return -1;
        }
        else if (post1.getTimeInMillis() < post2.getTimeInMillis()) {
            return 1;
        }
        else {
            return 0;
        }
    }
}