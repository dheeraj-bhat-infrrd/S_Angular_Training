package com.realtech.socialsurvey.core.dao.impl;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.SocialPostDao;
import com.realtech.socialsurvey.core.entities.SocialPost;
import com.realtech.socialsurvey.core.entities.SocialPostCompanyIdMapping;
import com.realtech.socialsurvey.core.entities.SocialUpdateAction;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;

@Repository
public class MongoSocialPostDaoImpl implements SocialPostDao {

	private static final Logger LOG = LoggerFactory.getLogger(MongoSocialPostDaoImpl.class);

	public static final String KEY_POST_ID = "postId";
	public static final String KEY_POST_TEXT = "postText";
	public static final String KEY_POSTED_BY = "postedBy";
	public static final String KEY_SOURCE = "source";
	public static final String KEY_COMPANY_ID = "companyId";
	public static final String KEY_REGION_ID = "regionId";
	public static final String KEY_BRANCH_ID = "branchId";
	public static final String KEY_AGENT_ID = "agentId";
	public static final String KEY_TIME_IN_MILLIS = "timeInMillis";
	public static final String KEY_POST_URL = "postUrl";
	public static final String KEY_SOURCE_SS = "SocialSurvey";
	public static final String KEY_MONGO_ID = "_id";

    //Regex to exclude posts made by the social survey application
    public static final String NON_SS_POSTS_REGEX = "^((?!Star Survey Response).)*$";
	@Autowired
	private MongoTemplate mongoTemplate;

	// Method to add a post to user's profile.
	@Override
	public void addPostToUserProfile(SocialPost socialPost) {
		LOG.info("Inserting into " + CommonConstants.SOCIAL_POST_COLLECTION + ". Object: " + socialPost.toString());
		mongoTemplate.insert(socialPost, CommonConstants.SOCIAL_POST_COLLECTION);
		LOG.info("Inserted into " + CommonConstants.SOCIAL_POST_COLLECTION);
	}
	
	// Method to get a post by mongo object id
	@Override
	public SocialPost getPostByMongoObjectId(String mongoObjectId){
		LOG.info("Fetching Social Post with with mongo id : " + mongoObjectId);
		SocialPost socialPost = mongoTemplate.findById(mongoObjectId, SocialPost.class , CommonConstants.SOCIAL_POST_COLLECTION);
		return socialPost;
	}
	
	@Override
	public void removePostFromUsersProfile(SocialPost socialPost) {
		LOG.info("Deleting from " + CommonConstants.SOCIAL_POST_COLLECTION + ". Object: " + socialPost.toString());
		mongoTemplate.remove(socialPost, CommonConstants.SOCIAL_POST_COLLECTION);
		LOG.info("Deleting from " + CommonConstants.SOCIAL_POST_COLLECTION);
	}
	
	// Method to fetch social posts for a particular user.
    @Override
    public List<SocialPost> getSocialPosts( long iden, String key, int skip, int limit )
    {
        return getSocialPosts( iden, key, skip, limit, null, null );
    }


    // Method to fetch social posts for a particular user given the start date and end date
    @Override
    public List<SocialPost> getSocialPosts( long iden, String key, int skip, int limit, Date startDate, Date endDate )
    {
        LOG.info( "Fetching Social Posts" );
        Query query = new Query();
        query.fields().include( KEY_POST_ID );
        query.fields().include( KEY_POST_TEXT );
        query.fields().include( KEY_POSTED_BY );
        query.fields().include( KEY_SOURCE );
        query.fields().include( KEY_TIME_IN_MILLIS );
        query.fields().include( KEY_POST_URL );
        query.fields().include( KEY_MONGO_ID );
        query.fields().include( KEY_AGENT_ID );
        query.fields().include( KEY_BRANCH_ID );
        query.fields().include( KEY_REGION_ID );
        query.fields().include( KEY_COMPANY_ID );


        if ( startDate != null && endDate == null ) {
            query.addCriteria( Criteria.where( key ).is( iden ) );
            query.addCriteria( Criteria.where( KEY_TIME_IN_MILLIS ).gte( startDate.getTime() ) );
        }
        if ( endDate != null && startDate == null ) {
            query.addCriteria( Criteria.where( key ).is( iden ) );
            query.addCriteria( Criteria.where( KEY_TIME_IN_MILLIS ).lte( endDate.getTime() ) );
        } else if ( startDate != null && endDate != null ) {
            query.addCriteria( Criteria
                .where( key )
                .is( iden )
                .andOperator( Criteria.where( KEY_TIME_IN_MILLIS ).gte( startDate.getTime() ),
                    Criteria.where( KEY_TIME_IN_MILLIS ).lte( endDate.getTime() ) ) );
        } else {
            query.addCriteria( Criteria.where( key ).is( iden ) );
        }
        if ( skip != -1 )
            query.skip( skip );
        if ( limit != -1 )
            query.limit( limit );

        query.with( new Sort( Sort.Direction.DESC, "timeInMillis" ) );
        List<SocialPost> posts = mongoTemplate.find( query, SocialPost.class, CommonConstants.SOCIAL_POST_COLLECTION );

        return posts;
    }

	// Method to fetch count of social posts for a particular user.
	@Override
	public long getPostsCountByUserId(String columnName, long columnValue) {
		Query query = new Query();
		if ( columnName != null ) {
            query.addCriteria( Criteria.where( columnName ).is( columnValue ) );
        }
		return mongoTemplate.count(query, CommonConstants.SOCIAL_POST_COLLECTION);
	}

	@Override
	public void purgeOlderSocialPosts(long timeSpanInMilliSecs) {
		long currentTimeInMilliSecs = new Date().getTime();
		long priorTimeInMilliSecs = currentTimeInMilliSecs - timeSpanInMilliSecs;

		Query query = new Query(Criteria.where(KEY_SOURCE).ne(KEY_SOURCE_SS).and(KEY_TIME_IN_MILLIS).lte(priorTimeInMilliSecs));
		mongoTemplate.remove(query, SocialPost.class, CommonConstants.SOCIAL_POST_COLLECTION);
	}
	
	@Override
	public void addActionToSocialConnectionHistory(SocialUpdateAction action){
	    Date date = new Date();
	    action.setUpdateTime( date );
	    LOG.info("Inserting into " + CommonConstants.SOCIAL_HISTORY_COLLECTION + ". Object: " + action.toString());
        mongoTemplate.insert(action, CommonConstants.SOCIAL_HISTORY_COLLECTION);
        LOG.info("Inserted into " + CommonConstants.SOCIAL_HISTORY_COLLECTION);
	}
	
	@Override
	public List<SocialUpdateAction> getSocialConnectionHistoryByEntity(String entityType, long entityId){
	    Query query = new Query();
	    if ( entityType != null ) {
            query.addCriteria( Criteria.where( entityType ).is( entityId ) );
        }
	    List<SocialUpdateAction> actions = mongoTemplate.find( query, SocialUpdateAction.class, CommonConstants.SOCIAL_HISTORY_COLLECTION );
	    return actions;
	}
	
	
	@Override
    public List<SocialUpdateAction> getSocialConnectionHistoryByEntityIds(String entityType, List<Long> entityIds){
        Query query = new Query();
        if ( entityType != null ) {
            query.addCriteria( Criteria.where( entityType ).in( entityIds ) );
        }
        List<SocialUpdateAction> actions = mongoTemplate.find( query, SocialUpdateAction.class, CommonConstants.SOCIAL_HISTORY_COLLECTION );
        return actions;
    }

    /**
     * Method to fetch social posts from mongodb
     */
    @Override
    public List<SocialPost> fetchSocialPostsPage( int offset, int pageSize ) throws NoRecordsFetchedException
    {
        LOG.info( "Fetching paginated social post results" );

        Query query = new Query();
        //query.addCriteria( Criteria.where( CommonConstants.POST_TEXT_SOLR ).regex( NON_SS_POSTS_REGEX ) );
        query.limit( pageSize );
        query.skip( offset );
        query.fields().exclude( "post" );
        return mongoTemplate.find( query, SocialPost.class, CommonConstants.SOCIAL_POST_COLLECTION );
    }
    
    /**
     * Method to fetch social posts from mongodb for solr indexing
     * 
     * @param offset
     * @param pageSize
     * @param lastBuildTime 
     * @return
     * @throws NoRecordsFetchedException
     */
    @Override
    public List<SocialPost> fetchSocialPostsPageforSolrIndexing( int offset, int pageSize, Date lastBuildTime  ) throws NoRecordsFetchedException
    {
        LOG.info( "Fetching paginated social post results for indexing in solr" );

        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.POST_TEXT_SOLR ).regex( NON_SS_POSTS_REGEX ).and( KEY_MONGO_ID ).gt( new ObjectId( lastBuildTime ) ) );
        query.limit( pageSize );
        query.skip( offset );
        query.fields().exclude( "post" );
        return mongoTemplate.find( query, SocialPost.class, CommonConstants.SOCIAL_POST_COLLECTION );
    }
    
    /**
     * Method to set companyIds for socialPosts 
     * 
     * @param socialPostCompanyIdMappings
     * @throws InvalidInputException 
     */
    @Override
    public void updateCompanyIdForSocialPosts( List<SocialPostCompanyIdMapping> socialPostCompanyIdMappings )
        throws InvalidInputException
    {
        LOG.info( "Method updateCompanyIdForSocialPost() started" );

        for ( SocialPostCompanyIdMapping socialPostCompanyIdMapping : socialPostCompanyIdMappings ) {
            if ( socialPostCompanyIdMapping.getEntityType() == null
                || socialPostCompanyIdMapping.getEntityType().isEmpty()
                || !( socialPostCompanyIdMapping.getEntityType() == CommonConstants.REGION_ID_COLUMN
                    || socialPostCompanyIdMapping.getEntityType() == CommonConstants.BRANCH_ID_COLUMN || socialPostCompanyIdMapping
                    .getEntityType() == CommonConstants.AGENT_ID_COLUMN ) ) {
                throw new InvalidInputException( "Invalid Entity Type" );
            }
            LOG.debug( "Updating social posts where entity type : " + socialPostCompanyIdMapping.getEntityType()
                + " entity ID : " + socialPostCompanyIdMapping.getEntityId() + " with companyId : "
                + socialPostCompanyIdMapping.getCompanyId() );
            Query query = new Query();
            query.addCriteria( Criteria.where( KEY_COMPANY_ID ).is( -1 ).and( socialPostCompanyIdMapping.getEntityType() ).is(
                socialPostCompanyIdMapping.getEntityId() ) );

            Update update = new Update();
            update.set( KEY_COMPANY_ID, socialPostCompanyIdMapping.getCompanyId() );
            mongoTemplate.updateMulti( query, update, CommonConstants.SOCIAL_POST_COLLECTION );

        }
        LOG.info( "Method updateCompanyIdForSocialPost() finished" );
    }
}
