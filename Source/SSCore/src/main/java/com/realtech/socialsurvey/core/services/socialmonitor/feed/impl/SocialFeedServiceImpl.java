package com.realtech.socialsurvey.core.services.socialmonitor.feed.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.commons.ActionHistoryComparator;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.MacrosComparator;
import com.realtech.socialsurvey.core.dao.BranchDao;
import com.realtech.socialsurvey.core.dao.CompanyDao;
import com.realtech.socialsurvey.core.dao.MongoSocialFeedDao;
import com.realtech.socialsurvey.core.dao.RegionDao;
import com.realtech.socialsurvey.core.dao.UserDao;
import com.realtech.socialsurvey.core.dao.UserProfileDao;
import com.realtech.socialsurvey.core.dao.impl.MongoSocialFeedDaoImpl;
import com.realtech.socialsurvey.core.entities.ActionHistory;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SegmentsEntity;
import com.realtech.socialsurvey.core.entities.SegmentsVO;
import com.realtech.socialsurvey.core.entities.SocialFeedActionResponse;
import com.realtech.socialsurvey.core.entities.SocialFeedsActionUpdate;
import com.realtech.socialsurvey.core.entities.SocialMonitorFeedData;
import com.realtech.socialsurvey.core.entities.SocialMonitorFeedTypeVO;
import com.realtech.socialsurvey.core.entities.SocialMonitorMacro;
import com.realtech.socialsurvey.core.entities.SocialMonitorResponseData;
import com.realtech.socialsurvey.core.entities.SocialMonitorUsersVO;
import com.realtech.socialsurvey.core.entities.SocialResponseObject;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.enums.ActionHistoryType;
import com.realtech.socialsurvey.core.enums.ProfileType;
import com.realtech.socialsurvey.core.enums.SocialFeedStatus;
import com.realtech.socialsurvey.core.enums.TextActionType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.integration.stream.StreamApiConnectException;
import com.realtech.socialsurvey.core.integration.stream.StreamApiException;
import com.realtech.socialsurvey.core.integration.stream.StreamApiIntegrationBuilder;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileNotFoundException;
import com.realtech.socialsurvey.core.services.socialmonitor.feed.SocialFeedService;


/**
 * @author manish
 *
 */
@DependsOn ( "generic")
@Component
public class SocialFeedServiceImpl implements SocialFeedService
{
    private static final Logger LOG = LoggerFactory.getLogger( SocialFeedServiceImpl.class );
    @Autowired
    MongoSocialFeedDao mongoSocialFeedDao;
    
    @Autowired
    OrganizationManagementService organizationManagementService;
    
    @Autowired
    CompanyDao companyDao;
    
    @Autowired
    RegionDao regionDao;
    
    @Resource
    @Qualifier ( "branch")
    BranchDao branchDao;
    
    @Autowired
    UserDao userDao;
    
    @Autowired
    private StreamApiIntegrationBuilder streamApiIntegrationBuilder;

    @Autowired
    UserProfileDao userProfileDao;
    
    private EmailServices emailServices;
    
    @Value("${SOCIAL_FEEDS_ARCHIVE_DAYS_BEFORE}")
    private int archiveSocialFeedBeforeDays;

	@Autowired
	public void setEmailServices(EmailServices emailServices) {
		this.emailServices = emailServices;
	}
	
    private static final int NO_OF_DAYS = 7;
    private static final String FLAGGED = "flagged";
    private static final String UNFLAGGED = "unflagged";

	@Override
    public SocialResponseObject<?> saveFeed( SocialResponseObject<?> socialFeed ) throws InvalidInputException
    {
        LOG.info( "Inside save feed method {}" , socialFeed);
        if(socialFeed == null){
            throw new InvalidInputException( "Feed cannt be null or empy" );
        }
        mongoSocialFeedDao.insertSocialFeed( socialFeed, MongoSocialFeedDaoImpl.SOCIAL_FEED_COLLECTION );
        LOG.info( "End of save feed method" );
        return socialFeed;
    }
    

    @SuppressWarnings ( "unchecked")
    @Override
    public SocialMonitorResponseData getAllSocialPosts( int startIndex, int limit, String status, boolean flag,
        List<String> feedtype, Long companyId, List<Long> regionIds, List<Long> branchIds, List<Long> agentIds,
        String searchText, boolean isCompanySet ) throws InvalidInputException
    {
        LOG.debug( "Fetching social posts" );

        SocialMonitorResponseData socialMonitorResponseData = new SocialMonitorResponseData();
        List<SocialMonitorFeedData> socialMonitorStreamDataList = new ArrayList<>();
        List<SocialResponseObject> socialResponseObjects;
        OrganizationUnitSettings organizationUnitSettings;

        socialResponseObjects = mongoSocialFeedDao.getAllSocialFeeds( startIndex, limit, flag, status, feedtype, companyId,
            regionIds, branchIds, agentIds, searchText, isCompanySet );
        if ( socialResponseObjects != null && !socialResponseObjects.isEmpty() ) {
            for ( SocialResponseObject socialResponseObject : socialResponseObjects ) {
                SocialMonitorFeedData socialMonitorFeedData = new SocialMonitorFeedData();
                if ( socialResponseObject.getProfileType().equals( ProfileType.COMPANY ) ) {
                    organizationUnitSettings = mongoSocialFeedDao.getProfileImageUrl( socialResponseObject.getCompanyId(),
                        CommonConstants.COMPANY_SETTINGS_COLLECTION );
                } else if ( socialResponseObject.getProfileType().equals( ProfileType.REGION ) ) {
                    organizationUnitSettings = mongoSocialFeedDao.getProfileImageUrl( socialResponseObject.getRegionId(),
                        CommonConstants.REGION_SETTINGS_COLLECTION );
                } else if ( socialResponseObject.getProfileType().equals( ProfileType.BRANCH ) ) {
                    organizationUnitSettings = mongoSocialFeedDao.getProfileImageUrl( socialResponseObject.getBranchId(),
                        CommonConstants.BRANCH_SETTINGS_COLLECTION );
                } else {
                    organizationUnitSettings = mongoSocialFeedDao.getProfileImageUrl( socialResponseObject.getAgentId(),
                        CommonConstants.AGENT_SETTINGS_COLLECTION );
                }
                socialMonitorFeedData.setType( socialResponseObject.getType() );
                socialMonitorFeedData.setStatus( socialResponseObject.getStatus() );
                socialMonitorFeedData.setText( socialResponseObject.getText() );
                socialMonitorFeedData.setPictures( socialResponseObject.getPictures() );
                socialMonitorFeedData.setOwnerName( socialResponseObject.getOwnerName() );
                if ( organizationUnitSettings != null ) {
                    socialMonitorFeedData.setOwnerProfileImage( organizationUnitSettings.getProfileImageUrl() );
                }
                socialMonitorFeedData.setCompanyId( socialResponseObject.getCompanyId() );
                socialMonitorFeedData.setRegionId( socialResponseObject.getRegionId() );
                socialMonitorFeedData.setBranchId( socialResponseObject.getBranchId() );
                socialMonitorFeedData.setAgentId( socialResponseObject.getAgentId() );
                socialMonitorFeedData.setPostId( socialResponseObject.getPostId() );
                socialMonitorFeedData.setFlagged( socialResponseObject.isFlagged() );
                if ( socialResponseObject.getActionHistory() != null ) {
                    Collections.sort( socialResponseObject.getActionHistory(), new ActionHistoryComparator() );
                }
                socialMonitorFeedData.setActionHistory( socialResponseObject.getActionHistory() );
                socialMonitorFeedData.setUpdatedOn( socialResponseObject.getCreatedTime() );
                socialMonitorFeedData.setFoundKeywords( socialResponseObject.getFoundKeywords() );
                socialMonitorFeedData.setDuplicateCount( socialResponseObject.getDuplicateCount() );
                socialMonitorFeedData.setPageLink( socialResponseObject.getPageLink() );
                socialMonitorFeedData.setPostLink( socialResponseObject.getPostLink() );
                socialMonitorFeedData.setFromTrustedSource(socialResponseObject.isFromTrustedSource());
                socialMonitorFeedData.setPostSource(socialResponseObject.getPostSource());
                if(StringUtils.isNotEmpty( socialResponseObject.getTextHighlighted() )){
                    socialMonitorFeedData.setTextHighlighted( socialResponseObject.getTextHighlighted() );
                } else {
                    socialMonitorFeedData.setTextHighlighted( socialResponseObject.getText() );
                }
                
                socialMonitorStreamDataList.add( socialMonitorFeedData );
            }
            socialMonitorResponseData.setCount( mongoSocialFeedDao.getAllSocialFeedsCount( flag, status, feedtype, companyId,
                regionIds, branchIds, agentIds, searchText, isCompanySet ) );
            if ( flag ) {
                socialMonitorResponseData.setStatus( "FLAGGED" );
            } else if ( status != null && !flag ) {
                socialMonitorResponseData.setStatus( status.toUpperCase() );
            } else if ( status == null && !flag ) {
                socialMonitorResponseData.setStatus( "ALL" );
            }
            socialMonitorResponseData.setSocialMonitorFeedData( socialMonitorStreamDataList );
        } else {
            LOG.warn( "List is empty" );
        }
        LOG.debug( "End of getSocialPostsForStream{}" );
        return socialMonitorResponseData;
    }
	

    @Override
    public SocialFeedActionResponse updateActionForFeeds( SocialFeedsActionUpdate socialFeedsActionUpdate, Long companyId,
        boolean duplicateFlag ) throws InvalidInputException
    {
        LOG.debug( "Updating social Feeds for social monitor" );
        if ( socialFeedsActionUpdate == null ) {
            LOG.error( "No action passed" );
            throw new InvalidInputException( "No action passed" );
        }
        int updateFlag = 0;
        SocialFeedActionResponse socialFeedActionResponse = new SocialFeedActionResponse();
        List<String> successPostIds = new ArrayList<>();
        SocialMonitorMacro socialMonitorMacro = null;
        boolean macroFlag = false;
        int macroActionFlag = 0;
        //add duplicate post ids to the Set of existing postIds in socialFeedsActionUpdate
        if ( duplicateFlag ) {
            socialFeedsActionUpdate = getPostIdsWithDuplicates( socialFeedsActionUpdate, companyId );
        }
        // Check if a macro is applied
        OrganizationUnitSettings organizationUnitSettings = new OrganizationUnitSettings();
        if ( !socialFeedsActionUpdate.getMacroId().isEmpty() ) {
            organizationUnitSettings = mongoSocialFeedDao.FetchMacros( companyId );
            if(organizationUnitSettings.getSocialMonitorMacros() != null)
            {
                for ( SocialMonitorMacro macro : organizationUnitSettings.getSocialMonitorMacros() ) {
                    if ( macro.getMacroId().equalsIgnoreCase( socialFeedsActionUpdate.getMacroId() ) ) {
                        socialMonitorMacro = macro;
                        macroFlag = true;
                        break;
                    }
                }
            }
        }
        List<SocialResponseObject> socialResponseObjectsToAdd = mongoSocialFeedDao
            .getSocialPostsByIds( socialFeedsActionUpdate.getPostIds(), companyId, MongoSocialFeedDaoImpl.SOCIAL_FEED_COLLECTION );
        for ( SocialResponseObject socialResponseObject : socialResponseObjectsToAdd ) {
            List<ActionHistory> actionHistories = new ArrayList<>();
            updateFlag = 0;
            String previousStatus = null;
            String currentStatus = null;
            if ( socialFeedsActionUpdate.getStatus() != null ) {
                updateFlag = 1;
                if ( macroFlag ) {
                    macroActionFlag = 1;
                }
                if ( socialResponseObject.isFlagged() != socialFeedsActionUpdate.isFlagged()
                    && !( socialFeedsActionUpdate.getStatus().equals( SocialFeedStatus.RESOLVED ) )
                    && !( socialFeedsActionUpdate.getStatus().equals( SocialFeedStatus.ESCALATED ) ) ) {
                    ActionHistory actionHistory = new ActionHistory();
                    if ( socialFeedsActionUpdate.isFlagged()
                        && ( socialResponseObject.getStatus().equals( SocialFeedStatus.NEW ) ) ) {
                        updateFlag = 2;
                        if ( macroFlag ) {
                            macroActionFlag = 2;
                        }
                        actionHistory.setActionType( ActionHistoryType.FLAGGED );
                        actionHistory.setText( "Post was <b class='soc-mon-bold-text'>Flagged</b> manually by " + "<b class='soc-mon-bold-text'>" + socialFeedsActionUpdate.getUserName() + "</b>" );
                        actionHistory.setOwnerName( socialFeedsActionUpdate.getUserName() );
                        actionHistory.setCreatedDate( new Date().getTime() );
                        actionHistories.add( actionHistory );
                        socialResponseObject.setUpdatedTime( new Date().getTime() );
                        previousStatus = UNFLAGGED;
                        currentStatus = FLAGGED;
                        
                    } else if ( !socialFeedsActionUpdate.isFlagged()
                        && ( socialResponseObject.getStatus().equals( SocialFeedStatus.NEW ) ) ) {
                        updateFlag = 2;
                        if ( macroFlag ) {
                            macroActionFlag = 2;
                        }
                        actionHistory.setActionType( ActionHistoryType.UNFLAGGED );
                        actionHistory.setText( "Post was <b class='soc-mon-bold-text'>Unflagged</b> by " + "<b class='soc-mon-bold-text'>" + socialFeedsActionUpdate.getUserName() + "</b>" );
                        actionHistory.setOwnerName( socialFeedsActionUpdate.getUserName() );
                        actionHistory.setCreatedDate( new Date().getTime() );
                        actionHistories.add( actionHistory );
                        socialResponseObject.setUpdatedTime( new Date().getTime() );
                        previousStatus = FLAGGED;
                        currentStatus = UNFLAGGED;
                    }
                }
                if ( !socialFeedsActionUpdate.getStatus().toString()
                    .equalsIgnoreCase( socialResponseObject.getStatus().toString() )
                    && socialFeedsActionUpdate.getStatus() != null
                    && !socialFeedsActionUpdate.getStatus().toString().equalsIgnoreCase( SocialFeedStatus.NEW.toString() ) ) {
                    ActionHistory actionHistory = new ActionHistory();
                    if ( socialFeedsActionUpdate.getStatus().toString()
                        .equalsIgnoreCase( SocialFeedStatus.ESCALATED.toString() ) ) {
                        updateFlag = 3;
                        if ( macroFlag ) {
                            macroActionFlag = 3;
                        }
                        actionHistory.setActionType( ActionHistoryType.ESCALATE );
                        actionHistory.setText( "Post was <b class='soc-mon-bold-text'>Escalated</b> by " + "<b class='soc-mon-bold-text'>" + socialFeedsActionUpdate.getUserName() + "</b>");
                        actionHistory.setOwnerName( socialFeedsActionUpdate.getUserName() );
                        actionHistory.setCreatedDate( new Date().getTime() );
                        actionHistories.add( actionHistory );
                        socialResponseObject.setUpdatedTime( new Date().getTime() );
                        if(socialResponseObject.isFlagged()) {
                            previousStatus = FLAGGED;
                        } else if(!socialResponseObject.isFlagged() && !socialResponseObject.getStatus().toString().equalsIgnoreCase( SocialFeedStatus.RESOLVED.toString() )) {
                            previousStatus = UNFLAGGED;
                        } else if(socialResponseObject.getStatus().toString().equalsIgnoreCase( SocialFeedStatus.RESOLVED.toString() )) {
                            previousStatus = SocialFeedStatus.RESOLVED.toString().toLowerCase();
                        }
                        currentStatus = SocialFeedStatus.ESCALATED.toString().toLowerCase();         
                    } else if ( socialFeedsActionUpdate.getStatus().toString()
                        .equalsIgnoreCase( SocialFeedStatus.RESOLVED.toString() )
                        && socialResponseObject.getStatus().toString().equalsIgnoreCase( SocialFeedStatus.ESCALATED.toString() )
                        && !socialResponseObject.isFlagged() ) {
                        updateFlag = 3;
                        if ( macroFlag ) {
                            macroActionFlag = 3;
                        }
                        actionHistory.setActionType( ActionHistoryType.RESOLVED );
                        actionHistory.setText( "Post was <b class='soc-mon-bold-text'>Resolved</b> by " + "<b class='soc-mon-bold-text'>" + socialFeedsActionUpdate.getUserName() + "</b>" );
                        actionHistory.setOwnerName( socialFeedsActionUpdate.getUserName() );
                        actionHistory.setCreatedDate( new Date().getTime() );
                        actionHistories.add( actionHistory );
                        socialResponseObject.setUpdatedTime( new Date().getTime() );
                        previousStatus = SocialFeedStatus.ESCALATED.toString().toLowerCase();
                        currentStatus = SocialFeedStatus.RESOLVED.toString().toLowerCase();
                    }
                }
            }
            if ( ( macroActionFlag != 1 && updateFlag != 1 )
                || ( socialFeedsActionUpdate.getStatus().toString().equalsIgnoreCase( SocialFeedStatus.SUBMIT.toString() ) ) ) {
                if ( ( socialFeedsActionUpdate.getTextActionType().toString()
                    .equalsIgnoreCase( TextActionType.PRIVATE_NOTE.toString() ) )
                    && ( socialFeedsActionUpdate.getText() != null ) && !( socialFeedsActionUpdate.getText().isEmpty() ) ) {
                    ActionHistory actionHistory = new ActionHistory();
                    actionHistory.setActionType( ActionHistoryType.PRIVATE_MESSAGE );
                    actionHistory.setText( socialFeedsActionUpdate.getText() );
                    actionHistory.setOwnerName( socialFeedsActionUpdate.getUserName() );
                    actionHistory.setCreatedDate( new Date().getTime() );
                    socialResponseObject.setUpdatedTime( new Date().getTime() );
                    actionHistories.add( actionHistory );
                }
                if ( ( socialFeedsActionUpdate.getTextActionType().toString()
                    .equalsIgnoreCase( TextActionType.SEND_EMAIL.toString() ) ) && ( socialFeedsActionUpdate.getText() != null )
                    && !( socialFeedsActionUpdate.getText().isEmpty() ) ) {
                    ActionHistory actionHistory = new ActionHistory();
                    actionHistory.setActionType( ActionHistoryType.EMAIL );
                    actionHistory.setText( socialFeedsActionUpdate.getText() );
                    actionHistory.setOwnerName( socialFeedsActionUpdate.getUserName() );
                    actionHistory.setCreatedDate( new Date().getTime() );
                    socialResponseObject.setUpdatedTime( new Date().getTime() );
                    actionHistories.add( actionHistory );
                    // send mail to the user
                    try {
                        emailServices.sendSocialMonitorActionMail( socialResponseObject.getOwnerEmail(),
                            socialResponseObject.getOwnerName(), socialFeedsActionUpdate.getText(),
                            socialFeedsActionUpdate.getUserName(), socialFeedsActionUpdate.getUserEmailId(), previousStatus,
                            currentStatus, socialResponseObject.getType().toString().toLowerCase() );
                    } catch ( UndeliveredEmailException e ) {
                        LOG.error( "Email could not be delivered", e );
                    }
                }
                mongoSocialFeedDao.updateSocialFeed( socialFeedsActionUpdate, socialResponseObject, companyId, actionHistories,
                    updateFlag, MongoSocialFeedDaoImpl.SOCIAL_FEED_COLLECTION );
            }
            //add successful postIds
            if ( ( updateFlag != 1 && macroActionFlag != 1 )
                || ( socialFeedsActionUpdate.getStatus().toString().equalsIgnoreCase( SocialFeedStatus.SUBMIT.toString() ) ) ) {
                successPostIds.add( socialResponseObject.getPostId() );
                socialFeedActionResponse.setSuccessPostIds( successPostIds );
            }
            //update macro count only if the action is applied on the current postId
            if ( macroFlag && macroActionFlag != 1 ) {
                socialMonitorMacro.setLastUsedTime( new Date().getTime() );
                ( socialMonitorMacro.getMacroUsageTime() ).add( new Date().getTime() );
            }
        }
        if(macroFlag) {
            mongoSocialFeedDao.updateMacroList( organizationUnitSettings.getSocialMonitorMacros(), companyId );
        }
            
        LOG.debug( "End of saveSocialPostsForStream{}" );
        return socialFeedActionResponse;

    }

    @Override
    public long updateDuplicateCount(int hash, long companyId) throws InvalidInputException {
        LOG.info("Executing updateDuplicateCount method with hash = {}, companyId = {}", hash, companyId);
        if( hash  == 0 || companyId <= 0){
            throw new InvalidInputException( "companyId cannot be <= 0 or hash cannot be 0" );
        }
        return mongoSocialFeedDao.updateDuplicateCount(hash, companyId);
    }


    @Override
    public List<SocialMonitorMacro> getMacros( long companyId, String searchMacros ) throws InvalidInputException
    {
        LOG.debug( "Fetching all Macros for company with Id {} ", companyId );
        if ( companyId <= 0 ) {
            LOG.error( "Invalid companyId" );
            throw new InvalidInputException( "Invalid companyId" );
        }
        List<SocialMonitorMacro> macros = new ArrayList<>();
        List<SocialMonitorMacro> macrosToAdd = new ArrayList<>();
        OrganizationUnitSettings organizationUnitSettings = mongoSocialFeedDao.FetchMacros( companyId );
        if ( organizationUnitSettings != null && organizationUnitSettings.getSocialMonitorMacros() != null
            && !organizationUnitSettings.getSocialMonitorMacros().isEmpty() ) {
            macros = organizationUnitSettings.getSocialMonitorMacros();
            for ( SocialMonitorMacro macro : macros ) {
                if ( searchMacros != null && !searchMacros.isEmpty() ) {
                    if ( macro.getMacroName().toLowerCase().contains( searchMacros.trim().toLowerCase() ) ) {
                        macrosToAdd.add( macro );
                    }
                } else {
                    macrosToAdd.add( macro );
                }
                macro.setLast7DaysMacroCount( last7DaysCountForMacro( macro.getMacroUsageTime() ) );
            }
        } else {
            LOG.warn( "The List is empty" );
        }
        Collections.sort( macrosToAdd, new MacrosComparator() );
        return macrosToAdd;

    }


    @Override
    public void updateMacrosForFeeds( SocialMonitorMacro socialMonitorMacro, long companyId ) throws InvalidInputException
    {
        LOG.debug( "Updating macros for social monitor for company with id {}", companyId );
        SocialMonitorMacro macro;
        if ( socialMonitorMacro == null || companyId <= 0 ) {
            LOG.error( "Invalid parameters passed" );
            throw new InvalidInputException( "Invalid parameters passed" );
        }
        if ( socialMonitorMacro.getMacroId() == null || socialMonitorMacro.getMacroId().isEmpty() ) {
            socialMonitorMacro.setLast7DaysMacroCount( 0 );
            socialMonitorMacro.setMacroUsageTime( new ArrayList<Long>() );
            socialMonitorMacro.setMacroId( UUID.randomUUID().toString() );
            socialMonitorMacro.setCreatedOn( new Date().getTime() );
            socialMonitorMacro.setModifiedOn( new Date().getTime() );
            mongoSocialFeedDao.updateMacros( socialMonitorMacro, companyId );

        } else {
            macro = getMacroById( socialMonitorMacro.getMacroId(), companyId );
            OrganizationUnitSettings organizationUnitSettings = mongoSocialFeedDao.FetchMacros( companyId );
            if ( organizationUnitSettings != null ) {
                organizationUnitSettings.getSocialMonitorMacros().remove( macro );
                socialMonitorMacro.setModifiedOn( new Date().getTime() );
                organizationUnitSettings.getSocialMonitorMacros().add( socialMonitorMacro );
                mongoSocialFeedDao.updateMacroList( organizationUnitSettings.getSocialMonitorMacros(), companyId );
            }


        }

    }

	@Override
	public SocialMonitorMacro getMacroById(String macroId, Long companyId) throws InvalidInputException {
		LOG.debug("Fetching Macro with Id {} and companyId {}", macroId, companyId);
		if (macroId == null || macroId.isEmpty() || companyId <= 0 || companyId == null) {
			LOG.error("Invalid input parameters");
			throw new InvalidInputException("Invalid input parameters");
		}
		SocialMonitorMacro socialMonitorMacro = null;
		OrganizationUnitSettings organizationUnitSettings = mongoSocialFeedDao.FetchMacros(companyId);
		if (organizationUnitSettings != null && organizationUnitSettings.getSocialMonitorMacros() != null
				&& !organizationUnitSettings.getSocialMonitorMacros().isEmpty()) {
			for (SocialMonitorMacro macro : organizationUnitSettings.getSocialMonitorMacros()) {
				if (macro.getMacroId().equalsIgnoreCase(macroId)) {
					socialMonitorMacro = macro;
				}
			}
		} else {
			LOG.warn("The List is empty");
		}
		return socialMonitorMacro;

	}

	@Override
	public SegmentsVO getSegmentsByCompanyId(Long companyId)
			throws InvalidInputException {
		LOG.debug("Fetching regions and branches for companyId {}", companyId);
		if (companyId <= 0) {
			LOG.error("Invalid companyId");
			throw new InvalidInputException("Invalid companyId");
		}
		SegmentsVO segmentsVO = new SegmentsVO();
		SegmentsEntity companyData = new SegmentsEntity();
		List<SegmentsEntity> regionList = new ArrayList<>();
		List<SegmentsEntity> branchList = new ArrayList<>();

		List<Long> regionIds = regionDao.getRegionIdsOfCompany( companyId );
		List<Long> branchIds = branchDao.getBranchIdsOfCompany( companyId );

		OrganizationUnitSettings companyDetails = mongoSocialFeedDao.getCompanyDetails(companyId);
		if (companyDetails != null) {
			companyData.setIden(companyDetails.getIden());
			companyData.setName(companyDetails.getContact_details().getName());
			companyData.setProfileImageUrl(companyDetails.getProfileImageUrl());
			companyData.setRegionId( 0 );
			segmentsVO.setSegmentsEntity(companyData);
		}

		List<OrganizationUnitSettings> regionDetails = mongoSocialFeedDao.getAllRegionDetails(regionIds);
		if (!regionDetails.isEmpty() || regionDetails != null) {
			for (OrganizationUnitSettings organizationUnitSettings : regionDetails) {
				SegmentsEntity segmentsEntity = new SegmentsEntity();
				segmentsEntity.setIden(organizationUnitSettings.getIden());
				segmentsEntity.setName(organizationUnitSettings.getContact_details().getName());
				segmentsEntity.setProfileImageUrl(organizationUnitSettings.getProfileImageUrl());
				segmentsEntity.setRegionId( 0 );
				regionList.add(segmentsEntity);
			}
			segmentsVO.setRegionDetails(regionList);
		}

		List<OrganizationUnitSettings> branchdetails = mongoSocialFeedDao.getAllBranchDetails(branchIds);
		if (!branchdetails.isEmpty() || branchdetails != null) {
			for (OrganizationUnitSettings organizationUnitSettings : branchdetails) {
				SegmentsEntity segmentsEntity = new SegmentsEntity();
				segmentsEntity.setIden(organizationUnitSettings.getIden());
				segmentsEntity.setName(organizationUnitSettings.getContact_details().getName());
				segmentsEntity.setProfileImageUrl(organizationUnitSettings.getProfileImageUrl());
				segmentsEntity.setRegionId( branchDao.getRegionIdByBranchId( organizationUnitSettings.getIden() ) );
				branchList.add(segmentsEntity);
			}
			segmentsVO.setBranchDetails(branchList);
		}

		return segmentsVO;
	}

	@Override
	public List<SocialMonitorUsersVO> getUsersByCompanyId(Long companyId)
			throws InvalidInputException, ProfileNotFoundException {
		LOG.debug("Fetching users for companyId {}", companyId);
		if (companyId <= 0) {
			LOG.error("Invalid companyId");
			throw new InvalidInputException("Invalid companyId");
		}
		List<SocialMonitorUsersVO> usersList = new ArrayList<>();
		List<UserProfile> userProfiles = userProfileDao.getUserProfiles(companyId);
		if (!userProfiles.isEmpty() || userProfiles != null) {
			for (UserProfile userProfile : userProfiles) {
				if (userProfile.getAgentId() != 0) {
					SocialMonitorUsersVO socialMonitorUsersVO = new SocialMonitorUsersVO();
					socialMonitorUsersVO.setRegionId(userProfile.getRegionId());
					socialMonitorUsersVO.setBranchId(userProfile.getBranchId());
					socialMonitorUsersVO.setUserId(userProfile.getAgentId());

					socialMonitorUsersVO.setName(mongoSocialFeedDao.getAllUserDetails(userProfile.getAgentId())
							.getContact_details().getName());
					socialMonitorUsersVO.setProfileImageUrl(
							mongoSocialFeedDao.getAllUserDetails(userProfile.getAgentId()).getProfileImageUrl());
					usersList.add(socialMonitorUsersVO);
				}
			}
		}
		return usersList;
	}	

    @Override
    public void retryFailedSocialFeeds()
    {
        LOG.info("Starting retryFailedSocialFeeds method.");
        try {
            streamApiIntegrationBuilder.getStreamApi().queueFailedSocialFeeds();
        } catch ( StreamApiException | StreamApiConnectException e ) {
            LOG.error( "Could not stream failed social feeds", e );
        }
    }
    
    public int last7DaysCountForMacro(List<Long> MacroAdditionTime  ) {
    	Date today = new Date();
    	int count = 0;
    	Date daysAgo = new DateTime(today).minusDays(NO_OF_DAYS).toDate();
    	long daysAgoTimestamp = daysAgo.getTime();
    	for(Long time : MacroAdditionTime) {
    		if(time.compareTo(daysAgoTimestamp) == 0 || time.compareTo(daysAgoTimestamp) > 0) {
    			count++;
    		}
    	}
		return count;
    }
    
    public SocialFeedsActionUpdate getPostIdsWithDuplicates(SocialFeedsActionUpdate socialFeedsActionUpdate, Long companyId)
    {
        List<SocialResponseObject> socialResponseObjects = mongoSocialFeedDao
            .getSocialPostsByIds( socialFeedsActionUpdate.getPostIds(), companyId, MongoSocialFeedDaoImpl.SOCIAL_FEED_COLLECTION );
        for ( SocialResponseObject socialResponseObject : socialResponseObjects ) {
            List<SocialResponseObject> duplicateSocialResponseObjects = mongoSocialFeedDao
                .getDuplicatePostIds( socialResponseObject.getHash(), companyId );
            for ( SocialResponseObject responseObject : duplicateSocialResponseObjects ) {
                socialFeedsActionUpdate.getPostIds().add( responseObject.getPostId() );
            }
        }
        return socialFeedsActionUpdate;
    }


    @Override
    public SocialMonitorFeedTypeVO getFeedTypesByCompanyId( Long companyId ) throws InvalidInputException
    {
        LOG.debug( "Fetching feedtypes for companyId {}", companyId );
        if ( companyId <= 0 ) {
            LOG.error( "Invalid companyId" );
            throw new InvalidInputException( "Invalid companyId" );
        }
        SocialMonitorFeedTypeVO socialMonitorFeedTypeVO = new SocialMonitorFeedTypeVO();
        List<Long> companyIds = new ArrayList<>();
        List<Long> regionIds = regionDao.getRegionIdsOfCompany( companyId );
        List<Long> branchIds = branchDao.getBranchIdsOfCompany( companyId );
        Set<Long> agentIds = userDao.getActiveUserIdsForCompany( companyDao.findById( Company.class, companyId ) );
        List<Long> agentIdsList = new ArrayList<>();
        agentIdsList.addAll( agentIds );
        companyIds.add( companyId );

        boolean facebookFlag = isFbTokenPresent( companyIds, regionIds, branchIds, agentIdsList );
        boolean twitterFlag = isTwitterTokenPresent( companyIds, regionIds, branchIds, agentIdsList );
        boolean linkedinFlag = isLinkedinTokenPresent( companyIds, regionIds, branchIds, agentIdsList );
        boolean instagramFlag = isInstagramTokenPresent( companyIds, regionIds, branchIds, agentIdsList );

        socialMonitorFeedTypeVO.setFacebook( facebookFlag );
        socialMonitorFeedTypeVO.setInstagram( instagramFlag );
        socialMonitorFeedTypeVO.setTwitter( twitterFlag );
        socialMonitorFeedTypeVO.setLinkedin( linkedinFlag );
        return socialMonitorFeedTypeVO;
    }


    public boolean isFbTokenPresent( List<Long> companyIds, List<Long> regionIds, List<Long> branchIds,
        List<Long> agentIdsList )
    {
        boolean facebookFlag = false;
        long facebookCount = 0;

        facebookCount = mongoSocialFeedDao.fetchFacebookTokenCount( agentIdsList, CommonConstants.AGENT_SETTINGS_COLLECTION );
        if ( facebookCount > 0 ) {
            facebookFlag = true;
        }
        if ( !facebookFlag ) {
            facebookCount = mongoSocialFeedDao.fetchFacebookTokenCount( branchIds, CommonConstants.BRANCH_SETTINGS_COLLECTION );
            if ( facebookCount > 0 ) {
                facebookFlag = true;
            }
        }
        if ( !facebookFlag ) {
            facebookCount = mongoSocialFeedDao.fetchFacebookTokenCount( regionIds, CommonConstants.REGION_SETTINGS_COLLECTION );
            if ( facebookCount > 0 ) {
                facebookFlag = true;
            }
        }
        if ( !facebookFlag ) {
            facebookCount = mongoSocialFeedDao.fetchFacebookTokenCount( companyIds,
                CommonConstants.COMPANY_SETTINGS_COLLECTION );
            if ( facebookCount > 0 ) {
                facebookFlag = true;
            }
        }

        return facebookFlag;
    }


    public boolean isTwitterTokenPresent( List<Long> companyIds, List<Long> regionIds, List<Long> branchIds,
        List<Long> agentIdsList )
    {
        boolean twitterFlag = false;
        long twitterCount = 0;

        twitterCount = mongoSocialFeedDao.fetchTwitterTokenCount( agentIdsList, CommonConstants.AGENT_SETTINGS_COLLECTION );
        if ( twitterCount > 0 ) {
            twitterFlag = true;
        }
        if ( !twitterFlag ) {
            twitterCount = mongoSocialFeedDao.fetchTwitterTokenCount( branchIds, CommonConstants.BRANCH_SETTINGS_COLLECTION );
            if ( twitterCount > 0 ) {
                twitterFlag = true;
            }
        }
        if ( !twitterFlag ) {
            twitterCount = mongoSocialFeedDao.fetchTwitterTokenCount( regionIds, CommonConstants.REGION_SETTINGS_COLLECTION );
            if ( twitterCount > 0 ) {
                twitterFlag = true;
            }
        }
        if ( !twitterFlag ) {
            twitterCount = mongoSocialFeedDao.fetchTwitterTokenCount( companyIds, CommonConstants.COMPANY_SETTINGS_COLLECTION );
            if ( twitterCount > 0 ) {
                twitterFlag = true;
            }
        }

        return twitterFlag;
    }


    public boolean isLinkedinTokenPresent( List<Long> companyIds, List<Long> regionIds, List<Long> branchIds,
        List<Long> agentIdsList )
    {
        boolean linkedinFlag = false;
        long linkedinCount = 0;

        linkedinCount = mongoSocialFeedDao.fetchLinkedinTokenCount( agentIdsList, CommonConstants.AGENT_SETTINGS_COLLECTION );
        if ( linkedinCount > 0 ) {
            linkedinFlag = true;
        }
        if ( !linkedinFlag ) {
            linkedinCount = mongoSocialFeedDao.fetchLinkedinTokenCount( branchIds, CommonConstants.BRANCH_SETTINGS_COLLECTION );
            if ( linkedinCount > 0 ) {
                linkedinFlag = true;
            }
        }
        if ( !linkedinFlag ) {
            linkedinCount = mongoSocialFeedDao.fetchLinkedinTokenCount( regionIds, CommonConstants.REGION_SETTINGS_COLLECTION );
            if ( linkedinCount > 0 ) {
                linkedinFlag = true;
            }
        }
        if ( !linkedinFlag ) {
            linkedinCount = mongoSocialFeedDao.fetchLinkedinTokenCount( companyIds,
                CommonConstants.COMPANY_SETTINGS_COLLECTION );
            if ( linkedinCount > 0 ) {
                linkedinFlag = true;
            }
        }

        return linkedinFlag;
    }


    public boolean isInstagramTokenPresent( List<Long> companyIds, List<Long> regionIds, List<Long> branchIds,
        List<Long> agentIdsList )
    {
        boolean instagramFlag = false;
        long instagramCount = 0;

        instagramCount = mongoSocialFeedDao.fetchInstagramTokenCount( agentIdsList, CommonConstants.AGENT_SETTINGS_COLLECTION );
        if ( instagramCount > 0 ) {
            instagramFlag = true;
        }
        if ( !instagramFlag ) {
            instagramCount = mongoSocialFeedDao.fetchInstagramTokenCount( branchIds,
                CommonConstants.BRANCH_SETTINGS_COLLECTION );
            if ( instagramCount > 0 ) {
                instagramFlag = true;
            }
        }
        if ( !instagramFlag ) {
            instagramCount = mongoSocialFeedDao.fetchInstagramTokenCount( regionIds,
                CommonConstants.REGION_SETTINGS_COLLECTION );
            if ( instagramCount > 0 ) {
                instagramFlag = true;
            }
        }
        if ( !instagramFlag ) {
            instagramCount = mongoSocialFeedDao.fetchInstagramTokenCount( companyIds,
                CommonConstants.COMPANY_SETTINGS_COLLECTION );
            if ( instagramCount > 0 ) {
                instagramFlag = true;
            }
        }

        return instagramFlag;
    }


    /**
     * method to fetch social feed having a given keyword
     * @param keyword
     * @param companyId
     * @param startTime
     * @param endTime
     * @param pageSize
     * @param skips
     */
    @Override public List<SocialResponseObject> getSocialFeed( String keyword, long companyId, long startTime, long endTime, int pageSize, int skips )
        throws InvalidInputException
    {
        if( StringUtils.isEmpty( keyword ) || companyId <= 0 || startTime <=0 || endTime <= 0 ){
            throw new InvalidInputException( "specified input is invalid" );
        }
        return mongoSocialFeedDao.getSocialFeed(keyword, companyId, startTime, endTime, pageSize, skips);

    }


    @Override public List<SocialResponseObject> getSocialFeed( long companyId, long startTime, long endTime, int pageSize,
        int skips ) throws InvalidInputException
    {
        if( companyId <= 0 || startTime <=0 || endTime <= 0 ){
            throw new InvalidInputException( "specified input is invalid" );
        }
        return mongoSocialFeedDao.getSocialFeed( companyId, startTime, endTime, pageSize, skips);
    }

    @Override
    public boolean moveDocumentToArchiveCollection()
    {
        return mongoSocialFeedDao.moveDocumentToArchiveCollection(archiveSocialFeedBeforeDays);
    }
    
    private ActionHistory getTrustedSourceActionHistory( String source )
    {
        ActionHistory actionHistory = new ActionHistory();
        actionHistory.setCreatedDate( new Date().getTime() );
        actionHistory.setActionType( ActionHistoryType.RESOLVED );
        actionHistory.setText( "The post was <b class='soc-mon-bold-text'>Resolved</b> for having source<b class='soc-mon-bold-text'>" + source + "</b>");
        return actionHistory;
    }
    
    @Override
    public void updateTrustedSourceForFormerLists(long companyId, String trustedSource) throws InvalidInputException {
        if(LOG.isDebugEnabled()){
            LOG.debug( "Updating the new and escalated status to resolved for trusted source : {} for companyId : {} ",trustedSource,companyId ); 
        }
        //get the getTrustedSourceActionHistory use update.push
        ActionHistory actionHistory = getTrustedSourceActionHistory( trustedSource );
        mongoSocialFeedDao.updateForTrustedSource( companyId, trustedSource, actionHistory );
        
    }
} 

