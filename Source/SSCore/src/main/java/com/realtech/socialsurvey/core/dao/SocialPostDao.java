package com.realtech.socialsurvey.core.dao;

import java.util.Date;
import java.util.List;

import com.realtech.socialsurvey.core.entities.SocialPost;
import com.realtech.socialsurvey.core.entities.SocialUpdateAction;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;

/**
 * This interface contains methods to interact
 */
public interface SocialPostDao {

	// Method to add a post to user's profile.
	public void addPostToUserProfile(SocialPost socialPost);

	/**
	 * Gets the social posts
	 * 
	 * @param iden
	 * @param key
	 * @param skip
	 * @param limit
	 * @return
	 */
	public List<SocialPost> getSocialPosts(long iden, String key, int skip, int limit);

	// Method to get posts count for user
	public long getPostsCountByUserId(String columnName, long columnValue);

	// Method to purge older social posts
	public void purgeOlderSocialPosts(long timeSpanInMilliSecs);

	// Method to get a social post by mongoid
	public SocialPost getPostByMongoObjectId(String mongoObjectId);

	//method to delete a post
	public void removePostFromUsersProfile(SocialPost socialPost);

    void addActionToSocialConnectionHistory( SocialUpdateAction action );

    List<SocialUpdateAction> getSocialConnectionHistoryByEntity( String entityType, long entityId );

    List<SocialPost> fetchSocialPostsPage( int offset, int pageSize ) throws NoRecordsFetchedException;

    List<SocialPost> getSocialPosts( long iden, String key, int skip, int limit, Date startDate, Date endDate );

    List<SocialPost> fetchSocialPostsPageforSolrIndexing( int offset, int pageSize, Date lastBuildTime )
        throws NoRecordsFetchedException;
}