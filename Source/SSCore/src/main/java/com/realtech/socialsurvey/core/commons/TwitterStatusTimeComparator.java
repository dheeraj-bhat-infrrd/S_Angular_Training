package com.realtech.socialsurvey.core.commons;

import java.util.Comparator;
import twitter4j.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Compares social posts based on their created times. Recent post will be placed last.
 */
public class TwitterStatusTimeComparator implements Comparator<Status> {

	private static final Logger LOG = LoggerFactory.getLogger(TwitterStatusTimeComparator.class);

	@Override
	public int compare(Status status1, Status status2) {
		
		if (status1.getCreatedAt().compareTo(status2.getCreatedAt()) > 0) {
			return 1;
		}
		else if (status1.getCreatedAt().compareTo(status2.getCreatedAt()) < 0) {
			return -1;
		}
		else {
			return 0;
		}
	}

}
