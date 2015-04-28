package com.realtech.socialsurvey.core.commons;

import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import facebook4j.Post;

/**
 * Compares social posts based on their created times. Recent post will be placed last.
 */
public class FacebookPostCreatedTimeComparator implements Comparator<Post> {

	private static final Logger LOG = LoggerFactory.getLogger(FacebookPostCreatedTimeComparator.class);

	@Override
	public int compare(Post post1, Post post2) {
		
		if (post1.getCreatedTime().compareTo(post2.getCreatedTime()) > 0) {
			return 1;
		}
		else if (post1.getCreatedTime().compareTo(post2.getCreatedTime()) < 0) {
			return -1;
		}
		else {
			return 0;
		}
	}

}
