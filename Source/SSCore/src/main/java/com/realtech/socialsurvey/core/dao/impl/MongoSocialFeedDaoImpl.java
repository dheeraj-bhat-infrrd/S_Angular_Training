package com.realtech.socialsurvey.core.dao.impl;

import com.mongodb.WriteResult;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.MongoSocialFeedDao;
import com.realtech.socialsurvey.core.entities.*;
import com.realtech.socialsurvey.core.enums.ProfileType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;


@Repository
public class MongoSocialFeedDaoImpl implements MongoSocialFeedDao, InitializingBean
{

    private static final Logger LOG = LoggerFactory.getLogger( MongoSocialFeedDaoImpl.class );

    @Autowired
    private MongoTemplate mongoTemplate;

    public static final String SOCIAL_FEED_COLLECTION = "SOCIAL_FEED_COLLECTION";
    public static final String KEY_IDENTIFIER = "_id";
    private static final String HASH = "hash";
    private static final String COMPANY_ID = "companyId";
    private static final String DUPLICATE_COUNT = "duplicateCount";
    private static final String POST_ID = "postId";
    private static final String FLAGGED = "flagged";
    private static final String FEED_TYPE = "type";
    private static final String ACTION_HISTORY = "actionHistory";
    private static final String IDEN = "iden";
    private static final String STATUS = "status";
    private static final String SOCIALMONITOR_MACROS = "socialMonitorMacros";
    private static final String CONTACT_DETAILS = "contact_details";
    private static final String NAME = "name";
    private static final String PROFILE_IMAGE_URL = "profileImageUrl";
    private static final String TEXT = "text";



    @Override
    public void insertSocialFeed( SocialResponseObject<?> socialFeed, String collectionName )
    {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "Creating {} document. Social feed id: {}", collectionName, socialFeed.getId() );
            LOG.debug( "Inserting into {}. Object: {}", collectionName, socialFeed.toString() );
        }

        mongoTemplate.insert( socialFeed, collectionName );
        LOG.debug( "Inserted into {}", collectionName );
    }

    @Override
	public long updateDuplicateCount(int hash, long companyId) {
		if(LOG.isDebugEnabled()){
			LOG.debug("Fetching count of duplicate posts with hash = {} and companyId = {}", hash, companyId);
		}
		Query query = new  Query();
		query.addCriteria(Criteria.where(HASH).is(hash).and(COMPANY_ID).is(companyId));
		long duplicates =  mongoTemplate.count(query, SOCIAL_FEED_COLLECTION);

		//if duplicates = 1, then there is only one post with the hash so no need to update the duplicateCount
		if(duplicates > 1) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Updating posts with duplicateCount {} having hash = {} ", duplicates, hash);
			}
			Query updateQuery = new Query().addCriteria(Criteria.where(HASH).is(hash).and(COMPANY_ID).is(companyId));
			Update update = new Update().set(DUPLICATE_COUNT, duplicates);
			WriteResult result = mongoTemplate.updateMulti(updateQuery, update, SOCIAL_FEED_COLLECTION);
			return result.getN();
		}
		else return duplicates;
	}
    
	@Override
	public void updateSocialFeed(SocialFeedsActionUpdate socialFeedsActionUpdate, String postId,
			List<ActionHistory> actionHistories, int updateFlag, String collectionName) {
		LOG.debug("Method updateSocialFeed() started");

		LOG.debug("Updating {} document. Social feed id: {}", collectionName, postId);

		Query query = new Query();

		query.addCriteria(Criteria.where(POST_ID).is(postId));

		Update update = new Update();

		if (updateFlag == 2) {
			update.set(FLAGGED, socialFeedsActionUpdate.isFlagged());
		} else if (updateFlag == 3) {
			update.set(FLAGGED, false);
			update.set(STATUS, socialFeedsActionUpdate.getStatus());
		}
		for (ActionHistory actionHistory : actionHistories) {
			update.push(ACTION_HISTORY, actionHistory);
			mongoTemplate.updateFirst(query, update, collectionName);
		}
		LOG.debug("Updated {}", collectionName);
	}


    @Override
    public void afterPropertiesSet() throws Exception
    {
        LOG.debug( "Checking if collections are created in mongodb" );
        if ( !mongoTemplate.collectionExists( SOCIAL_FEED_COLLECTION ) ) {
            LOG.debug( "Creating {}" , SOCIAL_FEED_COLLECTION );
            mongoTemplate.createCollection( SOCIAL_FEED_COLLECTION );
        }

    }

	@Override
	public OrganizationUnitSettings FetchMacros(Long companyId) {
		LOG.debug("Fetching Macros from COMAPNY_SETTINGS");
		Query query = new Query();

		query.addCriteria(Criteria.where(IDEN).is(companyId));
		query.fields().exclude( KEY_IDENTIFIER ).include( SOCIALMONITOR_MACROS );

		return mongoTemplate.findOne(query, OrganizationUnitSettings.class,
				CommonConstants.COMPANY_SETTINGS_COLLECTION);

	}

	@Override
	public void updateMacros(SocialMonitorMacro socialMonitorMacro, long companyId) {
		LOG.debug("Updating Macros in COMPANY_SETTINGS");
		Query query = new Query();

		query.addCriteria(Criteria.where(IDEN).is(companyId));

		Update update = new Update();

		update.addToSet(SOCIALMONITOR_MACROS, socialMonitorMacro);

		mongoTemplate.updateFirst(query, update, CommonConstants.COMPANY_SETTINGS_COLLECTION);
		LOG.debug("Updated {}", CommonConstants.COMPANY_SETTINGS_COLLECTION);

	}

	@Override
	public void updateMacroList(List<SocialMonitorMacro> socialMonitorMacros, long companyId) {
		LOG.debug("Updating Macro count in COMPANY_SETTINGS");
		
		Query query = new Query();
		
		query.addCriteria(Criteria.where(IDEN).is(companyId));
		
		Update update = new Update();

		update.set(SOCIALMONITOR_MACROS, socialMonitorMacros);
		
		mongoTemplate.updateFirst(query, update, CommonConstants.COMPANY_SETTINGS_COLLECTION);
		LOG.debug("Updated {}", CommonConstants.COMPANY_SETTINGS_COLLECTION);
		
	}

	@Override
	public List<SocialResponseObject> getAllSocialFeeds(int startIndex, int limit, boolean flag, String status,
			List<String> feedtype, Long companyId, List<Long> regionIds, List<Long> branchIds, List<Long> agentIds, String searchText, boolean isCompanySet) {
		LOG.debug("Fetching All Social Feeds");
		Query query = new Query();
		List<Criteria> criterias = new ArrayList<>();
		if (flag) {
			if (companyId != null) {
				criterias.add((Criteria.where(CommonConstants.COMPANY_ID).is(companyId)
						.andOperator((Criteria.where(CommonConstants.PROFILE_TYPE).is(ProfileType.COMPANY)), (Criteria.where(FLAGGED).is(flag)), (Criteria.where(FEED_TYPE).in(feedtype)))));
			}
			if (regionIds != null && !regionIds.isEmpty()) {
			    criterias.add((Criteria.where(CommonConstants.REGION_ID).in(regionIds)
                    .andOperator((Criteria.where(CommonConstants.PROFILE_TYPE).is(ProfileType.REGION)), (Criteria.where(FLAGGED).is(flag)), (Criteria.where(FEED_TYPE).in(feedtype)))));
			}
			if (branchIds != null && !branchIds.isEmpty()) {
			    criterias.add((Criteria.where(CommonConstants.BRANCH_ID).in(branchIds)
                    .andOperator((Criteria.where(CommonConstants.PROFILE_TYPE).is(ProfileType.BRANCH)), (Criteria.where(FLAGGED).is(flag)), (Criteria.where(FEED_TYPE).in(feedtype)))));
			}
			if (agentIds != null && !agentIds.isEmpty()) {
			    criterias.add((Criteria.where(CommonConstants.AGENT_ID).in(agentIds)
                    .andOperator((Criteria.where(CommonConstants.PROFILE_TYPE).is(ProfileType.AGENT)), (Criteria.where(FLAGGED).is(flag)), (Criteria.where(FEED_TYPE).in(feedtype)))));
			}
			
		} else if (status != null && !flag) {
            if (companyId != null) {
                criterias.add((Criteria.where(CommonConstants.COMPANY_ID).is(companyId).andOperator((Criteria.where(CommonConstants.PROFILE_TYPE).is(ProfileType.COMPANY)),
                        (Criteria.where(CommonConstants.STATUS_COLUMN).is(status.toUpperCase())),
                        (Criteria.where(FEED_TYPE).in(feedtype)))));
            }
            if (regionIds != null && !regionIds.isEmpty()) {
                criterias.add((Criteria.where(CommonConstants.REGION_ID).in(regionIds).andOperator((Criteria.where(CommonConstants.PROFILE_TYPE).is(ProfileType.REGION)),
                        (Criteria.where(CommonConstants.STATUS_COLUMN).is(status.toUpperCase())),
                        (Criteria.where(FEED_TYPE).in(feedtype)))));
            }
            if (branchIds != null && !branchIds.isEmpty()) {
                criterias.add((Criteria.where(CommonConstants.BRANCH_ID).in(branchIds).andOperator((Criteria.where(CommonConstants.PROFILE_TYPE).is(ProfileType.BRANCH)),
                        (Criteria.where(CommonConstants.STATUS_COLUMN).is(status.toUpperCase())),
                        (Criteria.where(FEED_TYPE).in(feedtype)))));
            }
            if (agentIds != null && !agentIds.isEmpty()) {
                criterias.add((Criteria.where(CommonConstants.AGENT_ID).in(agentIds).andOperator((Criteria.where(CommonConstants.PROFILE_TYPE).is(ProfileType.AGENT)),
                        (Criteria.where(CommonConstants.STATUS_COLUMN).is(status.toUpperCase())),
                        (Criteria.where(FEED_TYPE).in(feedtype)))));
            }
    
        } else if (status == null && !flag) {
            if (companyId != null) {
                criterias.add((Criteria.where(CommonConstants.COMPANY_ID).is(companyId)
                        .andOperator((Criteria.where(CommonConstants.PROFILE_TYPE).is(ProfileType.COMPANY)),Criteria.where(FEED_TYPE).in(feedtype))));
            }

            if (regionIds != null && !regionIds.isEmpty()) {
                criterias.add((Criteria.where(CommonConstants.REGION_ID).in(regionIds)
                        .andOperator((Criteria.where(CommonConstants.PROFILE_TYPE).is(ProfileType.REGION)), Criteria.where(FEED_TYPE).in(feedtype))));
            }
            if (branchIds != null && !branchIds.isEmpty()) {
                criterias.add((Criteria.where(CommonConstants.BRANCH_ID).in(branchIds)
                        .andOperator((Criteria.where(CommonConstants.PROFILE_TYPE).is(ProfileType.BRANCH)), Criteria.where(FEED_TYPE).in(feedtype))));
            }
            if (agentIds != null && !agentIds.isEmpty()) {
                criterias.add((Criteria.where(CommonConstants.AGENT_ID).in(agentIds)
                        .andOperator((Criteria.where(CommonConstants.PROFILE_TYPE).is(ProfileType.AGENT)), Criteria.where(FEED_TYPE).in(feedtype))));
            }
        }
		Criteria criteria = new Criteria();
		
		if(!criterias.isEmpty() && criterias!=null && isCompanySet) {
		    criteria.orOperator(criterias.toArray(new Criteria[criterias.size()]));
		}else {
		    criteria.orOperator((Criteria.where(CommonConstants.COMPANY_ID).is(companyId)));
		}
		
		if(searchText != null)
        {
		    criteria.andOperator((Criteria.where( TEXT ).regex( Pattern.compile(searchText.trim() , Pattern.CASE_INSENSITIVE) )));
        }
		query.addCriteria(criteria);
		query.with(new Sort(Sort.Direction.DESC, "updatedTime"));
		if (startIndex > -1) {
			query.skip(startIndex);
		}
		if (limit > -1) {
			query.limit(limit);
		}

		return mongoTemplate.find(query, SocialResponseObject.class, SOCIAL_FEED_COLLECTION);
	}

	@Override
	public long getAllSocialFeedsCount(boolean flag, String status, List<String> feedtype, Long companyId,
			List<Long> regionIds, List<Long> branchIds, List<Long> agentIds, String searchText, boolean isCompanySet) {
		LOG.debug("Fetching All Social Feeds count");
		Query query = new Query();
		List<Criteria> criterias = new ArrayList<>();
		if (flag) {
            if (companyId != null) {
                criterias.add((Criteria.where(CommonConstants.COMPANY_ID).is(companyId)
                        .andOperator((Criteria.where(CommonConstants.PROFILE_TYPE).is(ProfileType.COMPANY)), (Criteria.where(FLAGGED).is(flag)), (Criteria.where(FEED_TYPE).in(feedtype)))));
            }
            if (regionIds != null && !regionIds.isEmpty()) {
                criterias.add((Criteria.where(CommonConstants.REGION_ID).in(regionIds)
                    .andOperator((Criteria.where(CommonConstants.PROFILE_TYPE).is(ProfileType.REGION)), (Criteria.where(FLAGGED).is(flag)), (Criteria.where(FEED_TYPE).in(feedtype)))));
            }
            if (branchIds != null && !branchIds.isEmpty()) {
                criterias.add((Criteria.where(CommonConstants.BRANCH_ID).in(branchIds)
                    .andOperator((Criteria.where(CommonConstants.PROFILE_TYPE).is(ProfileType.BRANCH)), (Criteria.where(FLAGGED).is(flag)), (Criteria.where(FEED_TYPE).in(feedtype)))));
            }
            if (agentIds != null && !agentIds.isEmpty()) {
                criterias.add((Criteria.where(CommonConstants.AGENT_ID).in(agentIds)
                    .andOperator((Criteria.where(CommonConstants.PROFILE_TYPE).is(ProfileType.AGENT)), (Criteria.where(FLAGGED).is(flag)), (Criteria.where(FEED_TYPE).in(feedtype)))));
            }
            
        } else if (status != null && !flag) {
			if (companyId != null) {
				criterias.add((Criteria.where(CommonConstants.COMPANY_ID).is(companyId).andOperator((Criteria.where(CommonConstants.PROFILE_TYPE).is(ProfileType.COMPANY)),
						(Criteria.where(CommonConstants.STATUS_COLUMN).is(status.toUpperCase())),
						(Criteria.where(FEED_TYPE).in(feedtype)))));
			}
			if (regionIds != null && !regionIds.isEmpty()) {
				criterias.add((Criteria.where(CommonConstants.REGION_ID).in(regionIds).andOperator((Criteria.where(CommonConstants.PROFILE_TYPE).is(ProfileType.REGION)),
						(Criteria.where(CommonConstants.STATUS_COLUMN).is(status.toUpperCase())),
						(Criteria.where(FEED_TYPE).in(feedtype)))));
			}
			if (branchIds != null && !branchIds.isEmpty()) {
				criterias.add((Criteria.where(CommonConstants.BRANCH_ID).in(branchIds).andOperator((Criteria.where(CommonConstants.PROFILE_TYPE).is(ProfileType.BRANCH)),
						(Criteria.where(CommonConstants.STATUS_COLUMN).is(status.toUpperCase())),
						(Criteria.where(FEED_TYPE).in(feedtype)))));
			}
			if (agentIds != null && !agentIds.isEmpty()) {
				criterias.add((Criteria.where(CommonConstants.AGENT_ID).in(agentIds).andOperator((Criteria.where(CommonConstants.PROFILE_TYPE).is(ProfileType.AGENT)),
						(Criteria.where(CommonConstants.STATUS_COLUMN).is(status.toUpperCase())),
						(Criteria.where(FEED_TYPE).in(feedtype)))));
			}
	
		} else if (status == null && !flag) {
			if (companyId != null) {
				criterias.add((Criteria.where(CommonConstants.COMPANY_ID).is(companyId)
						.andOperator((Criteria.where(CommonConstants.PROFILE_TYPE).is(ProfileType.COMPANY)),Criteria.where(FEED_TYPE).in(feedtype))));
			}

			if (regionIds != null && !regionIds.isEmpty()) {
				criterias.add((Criteria.where(CommonConstants.REGION_ID).in(regionIds)
						.andOperator((Criteria.where(CommonConstants.PROFILE_TYPE).is(ProfileType.REGION)), Criteria.where(FEED_TYPE).in(feedtype))));
			}
			if (branchIds != null && !branchIds.isEmpty()) {
				criterias.add((Criteria.where(CommonConstants.BRANCH_ID).in(branchIds)
						.andOperator((Criteria.where(CommonConstants.PROFILE_TYPE).is(ProfileType.BRANCH)), Criteria.where(FEED_TYPE).in(feedtype))));
			}
			if (agentIds != null && !agentIds.isEmpty()) {
				criterias.add((Criteria.where(CommonConstants.AGENT_ID).in(agentIds)
						.andOperator((Criteria.where(CommonConstants.PROFILE_TYPE).is(ProfileType.AGENT)), Criteria.where(FEED_TYPE).in(feedtype))));
			}
		}
		
		Criteria criteria = new Criteria();
        
        if(!criterias.isEmpty() && criterias!=null && isCompanySet) {
            criteria.orOperator(criterias.toArray(new Criteria[criterias.size()]));
        }else {
            criteria.orOperator((Criteria.where(CommonConstants.COMPANY_ID).is(companyId)));
        }
        
        if(searchText != null)
        {
            criteria.andOperator((Criteria.where( TEXT ).regex( Pattern.compile(searchText.trim() , Pattern.CASE_INSENSITIVE) )));
        }
        
        query.addCriteria(criteria);

		return mongoTemplate.count(query, SOCIAL_FEED_COLLECTION);
	}
	
	@Override
	public OrganizationUnitSettings getCompanyDetails(Long companyId) {
		LOG.debug( "Fetching company details from {}", CommonConstants.COMPANY_SETTINGS_COLLECTION );
		OrganizationUnitSettings organizationUnitSettings = null;
        Query query = new Query();
        query.addCriteria( Criteria.where( "iden" ).is( companyId ) );
        query.fields().exclude( KEY_IDENTIFIER ).include(IDEN).include(CONTACT_DETAILS + "." + NAME).include(PROFILE_IMAGE_URL);
        organizationUnitSettings = mongoTemplate.findOne( query, OrganizationUnitSettings.class, CommonConstants.COMPANY_SETTINGS_COLLECTION );
        LOG.debug( "Fetched company details from {}", CommonConstants.COMPANY_SETTINGS_COLLECTION );
        return organizationUnitSettings;
	}

	@Override
	public List<OrganizationUnitSettings> getAllRegionDetails(List<Long> regionIds) {
		LOG.debug( "Fetching all region details from {}", CommonConstants.REGION_SETTINGS_COLLECTION );
        List<OrganizationUnitSettings> organizationUnitSettings = null;
        Query query = new Query();
        query.addCriteria( Criteria.where( "iden" ).in( regionIds ) );
        query.fields().exclude( KEY_IDENTIFIER ).include(IDEN).include(CONTACT_DETAILS + "." + NAME).include(PROFILE_IMAGE_URL);
        organizationUnitSettings = mongoTemplate.find( query, OrganizationUnitSettings.class, CommonConstants.REGION_SETTINGS_COLLECTION );
        LOG.debug( "Fetched all region details from {}", CommonConstants.REGION_SETTINGS_COLLECTION );
        return organizationUnitSettings;
	}


	@Override
	public List<OrganizationUnitSettings> getAllBranchDetails(List<Long> branchIds) {
		LOG.debug( "Fetching all branch details from {}", CommonConstants.BRANCH_SETTINGS_COLLECTION );
        List<OrganizationUnitSettings> organizationUnitSettings = null;
        Query query = new Query();
        query.addCriteria( Criteria.where( "iden" ).in( branchIds ) );
        query.fields().exclude( KEY_IDENTIFIER ).include(IDEN).include(CONTACT_DETAILS + "." + NAME).include(PROFILE_IMAGE_URL);
        organizationUnitSettings = mongoTemplate.find( query, OrganizationUnitSettings.class, CommonConstants.BRANCH_SETTINGS_COLLECTION );
        LOG.debug( "Fetched all branch details from {}", CommonConstants.BRANCH_SETTINGS_COLLECTION );
        return organizationUnitSettings;
	}

	@Override
	public OrganizationUnitSettings getAllUserDetails(Long userId) {
		LOG.debug( "Fetching all user details from {}", CommonConstants.AGENT_SETTINGS_COLLECTION );
        OrganizationUnitSettings organizationUnitSettings = null;
        Query query = new Query();
        query.addCriteria( Criteria.where( "iden" ).is( userId ) );
        query.fields().exclude( KEY_IDENTIFIER ).include(IDEN).include(CONTACT_DETAILS + "." + NAME).include(PROFILE_IMAGE_URL);
        organizationUnitSettings = mongoTemplate.findOne( query, OrganizationUnitSettings.class, CommonConstants.AGENT_SETTINGS_COLLECTION );
        LOG.debug( "Fetched all user details from {}", CommonConstants.AGENT_SETTINGS_COLLECTION );
        return organizationUnitSettings;
	}

    @Override
    public List<SocialResponseObject> getSocialPostsByIds( Set<String> postIds, String collectionName )
    {
        LOG.debug("Fetching Social Feeds for postIds {}", postIds);
        Query query = new Query();

        query.addCriteria(Criteria.where(POST_ID).in(postIds));

        return mongoTemplate.find(query, SocialResponseObject.class, collectionName);
    }

    @Override
    public List<SocialResponseObject> getDuplicatePostIds( int hash, Long companyId )
    {
        LOG.debug("Fetching duplicate posts with hash = {} and companyId = {}", hash, companyId);
        Query query = new  Query();
        query.addCriteria(Criteria.where(HASH).is(hash).and(COMPANY_ID).is(companyId));
        query.fields().exclude( KEY_IDENTIFIER ).include(POST_ID);
        return mongoTemplate.find( query, SocialResponseObject.class, SOCIAL_FEED_COLLECTION );
    }	
	
	
	
	
	
	
	
	

}
