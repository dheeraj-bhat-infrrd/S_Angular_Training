package com.realtech.socialsurvey.core.dao;

import java.util.List;
import com.realtech.socialsurvey.core.entities.SocialPost;

/*
 * This interface contains methods to interact 
 */
public interface SocialPostDao {

	// Method to add a post to user's profile.
	public void addPostToUserProfile(SocialPost socialPost);

	// Method to fetch social posts for a particular user.
	public List<SocialPost> getPostsByUserId(long userId, int skip, int limit);

	public long getPostsCountByUserId(long userId);
	
}
