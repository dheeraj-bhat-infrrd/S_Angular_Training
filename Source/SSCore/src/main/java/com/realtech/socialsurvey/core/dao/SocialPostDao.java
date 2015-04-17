package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.SocialPost;

/*
 * This interface contains methods to interact 
 */
public interface SocialPostDao {

	// Method to add a post to user's profile.
	public void addPostToUserProfile(SocialPost socialPost);

	/**
	 * Gets the social posts
	 * @param iden
	 * @param key
	 * @param skip
	 * @param limit
	 * @return
	 */
	public List<SocialPost> getSocialPosts(long iden, String key, int skip, int limit);

	public long getPostsCountByUserId(long userId);
	
}
