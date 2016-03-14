package com.realtech.socialsurvey.core.starter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.Utils;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.AccountsMaster;
import com.realtech.socialsurvey.core.entities.AgentMediaPostResponseDetails;
import com.realtech.socialsurvey.core.entities.BranchMediaPostDetails;
import com.realtech.socialsurvey.core.entities.BranchMediaPostResponseDetails;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.CompanyMediaPostResponseDetails;
import com.realtech.socialsurvey.core.entities.ComplaintResolutionSettings;
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.ExternalSurveyTracker;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.RegionMediaPostDetails;
import com.realtech.socialsurvey.core.entities.RegionMediaPostResponseDetails;
import com.realtech.socialsurvey.core.entities.SocialMediaPostDetails;
import com.realtech.socialsurvey.core.entities.SocialMediaPostResponseDetails;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.ZillowTempPost;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.batchtracker.BatchTrackerService;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.social.SocialManagementService;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;
import com.realtech.socialsurvey.core.utils.EmailFormatHelper;


public class ZillowReviewProcessorAndAutoPostStarter extends QuartzJobBean
{
    public static final Logger LOG = LoggerFactory.getLogger( ZillowReviewProcessorAndAutoPostStarter.class );
    private UserManagementService userManagementService;
    private SurveyHandler surveyHandler;
    private OrganizationManagementService organizationManagementService;
    private ProfileManagementService profileManagementService;
    private SocialManagementService socialManagementService;
    private BatchTrackerService batchTrackerService;
    private Utils utils;
    private final int batchSize = 50;
    private int zillowAutoPostThreshold;
    private EmailFormatHelper emailFormatHelper;
    private EmailServices emailServices;


    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext )
    {
        try {
            LOG.info( "Executing ZillowReviewFetchAndAutoPoster" );
            initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
            //update last run start time
            batchTrackerService.getLastRunEndTimeAndUpdateLastStartTimeByBatchType(
                CommonConstants.BATCH_TYPE_ZILLOW_REVIEW_PROCESSOR_AND_AUTO_POSTER,
                CommonConstants.BATCH_NAME_ZILLOW_REVIEW_PROCESSOR_AND_AUTO_POSTER );

            // Fetch All companies
            for ( Company company : organizationManagementService.getAllCompanies() ) {
                try {
                    long companyId = company.getCompanyId();
                    List<OrganizationUnitSettings> regionSettings = new ArrayList<OrganizationUnitSettings>();
                    List<OrganizationUnitSettings> branchSettings = new ArrayList<OrganizationUnitSettings>();
                    List<OrganizationUnitSettings> agentSettings = new ArrayList<OrganizationUnitSettings>();
                    Map<Long, OrganizationUnitSettings> agentIdSettingsMap = new HashMap<Long, OrganizationUnitSettings>();

                    // find all users connected to zillow
                    LOG.debug( "Fetching settings of agents connected to zillow under company id : " + companyId );
                    int start = 0;
                    List<Long> batchUserIdList = new ArrayList<Long>();
                    do {
                        batchUserIdList = organizationManagementService.getAgentIdsUnderCompany( companyId, start, batchSize );
                        if ( batchUserIdList != null && batchUserIdList.size() > 0 ) {
                            // fetch zillow settings for these ids and add to list
                            List<OrganizationUnitSettings> currBatchUserSettings = organizationManagementService
                                .fetchUnitSettingsConnectedToZillow(
                                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, batchUserIdList );
                            if ( currBatchUserSettings != null && !currBatchUserSettings.isEmpty() ) {
                                agentSettings.addAll( currBatchUserSettings );
                            }
                        }
                        start += batchSize;
                    } while ( batchUserIdList != null && batchUserIdList.size() == batchSize );

                    // find all branches connected to zillow
                    LOG.debug( "Fetching settings of branches connected to zillow under company id : " + companyId );
                    start = 0;
                    List<Long> batchBranchIdList = new ArrayList<Long>();
                    do {
                        batchBranchIdList = organizationManagementService
                            .getBranchIdsUnderCompany( companyId, start, batchSize );
                        if ( batchBranchIdList != null && batchBranchIdList.size() > 0 ) {
                            // fetch zillow settings for these ids and add to list
                            List<OrganizationUnitSettings> currBatchBatchSettings = organizationManagementService
                                .fetchUnitSettingsConnectedToZillow(
                                    MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, batchBranchIdList );
                            if ( currBatchBatchSettings != null && !currBatchBatchSettings.isEmpty() ) {
                                branchSettings.addAll( currBatchBatchSettings );
                            }
                        }
                        start += batchSize;
                    } while ( batchBranchIdList != null && batchBranchIdList.size() == batchSize );

                    // find all regions connected to zillow.
                    LOG.debug( "Fetching settings of regions connected to zillow under company id : " + companyId );
                    start = 0;
                    List<Long> batchRegionIdList = new ArrayList<Long>();
                    do {
                        batchRegionIdList = organizationManagementService
                            .getRegionIdsUnderCompany( companyId, start, batchSize );
                        if ( batchRegionIdList != null && batchRegionIdList.size() > 0 ) {
                            // fetch zillow settings for these ids and add to list
                            List<OrganizationUnitSettings> currBatchRegionSettings = organizationManagementService
                                .fetchUnitSettingsConnectedToZillow(
                                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, batchRegionIdList );
                            if ( currBatchRegionSettings != null && !currBatchRegionSettings.isEmpty() ) {
                                regionSettings.addAll( currBatchRegionSettings );
                            }
                        }
                        start += batchSize;
                    } while ( batchRegionIdList != null && batchRegionIdList.size() == batchSize );


                    // Fetch Company Settings
                    OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( companyId );

                    // Fetch & save zillow reviews for agents
                    if ( agentSettings != null && !agentSettings.isEmpty() ) {
                        for ( OrganizationUnitSettings agentSetting : agentSettings ) {
                            agentIdSettingsMap.put( agentSetting.getIden(), agentSetting );
                            LOG.debug( "Fetching and saving zillow reviews for agent id : " + agentSetting.getIden() );
                            profileManagementService.fetchAndSaveZillowData( agentSetting,
                                MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, companyId, true, false );
                            LOG.debug( "Fetched and saved zillow reviews for agent id : " + agentSetting.getIden() );
                        }
                    }

                    // Fetch & save zillow reviews for branches
                    if ( branchSettings != null && !branchSettings.isEmpty() ) {
                        for ( OrganizationUnitSettings branchSetting : branchSettings ) {
                            LOG.debug( "Fetching and saving zillow reviews for branch id : " + branchSetting.getIden() );
                            profileManagementService.fetchAndSaveZillowData( branchSetting,
                                MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, companyId, true, false );
                            LOG.debug( "Fetched and saved zillow reviews for branch id : " + branchSetting.getIden() );
                        }
                    }

                    // Fetch & save zillow reviews for regions
                    if ( regionSettings != null && !regionSettings.isEmpty() ) {
                        for ( OrganizationUnitSettings regionSetting : regionSettings ) {
                            LOG.debug( "Fetching and saving zillow reviews for region id : " + regionSetting.getIden() );
                            profileManagementService.fetchAndSaveZillowData( regionSetting,
                                MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, companyId, true, false );
                            LOG.debug( "Fetched and saved zillow reviews for region id : " + regionSetting.getIden() );
                        }
                    }

                    // Fetch & save zillow reviews for company
                    if ( companySettings != null && companySettings.getSocialMediaTokens() != null
                        && companySettings.getSocialMediaTokens().getZillowToken() != null ) {
                        LOG.debug( "Fetching and saving zillow reviews for company id : " + companyId );
                        profileManagementService.fetchAndSaveZillowData( companySettings,
                            MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companyId, true, false );
                        LOG.debug( "Fetched and saved zillow reviews for company id : " + companyId );
                    }

                    // Fetch all zillow data from temp table and trigger auto post
                    List<ZillowTempPost> zillowTempPostList = socialManagementService.getAllZillowTempPosts();
                    List<Long> processedZillowTempPostIds = new ArrayList<Long>();
                    if ( zillowTempPostList != null && !zillowTempPostList.isEmpty() ) {
                        for ( ZillowTempPost zillowTempPost : zillowTempPostList ) {
                            try {
                                if ( zillowTempPost != null ) {
                                    // change this to support another units in hierarchy
                                    OrganizationUnitSettings agentSetting = agentIdSettingsMap.get( zillowTempPost
                                        .getEntityId() );
                                    SurveyDetails surveyDetails = surveyHandler.getSurveyDetails( zillowTempPost
                                        .getZillowSurveyId() );
                                    if ( checkReviewCanBePostedToSocialMedia( zillowTempPost, agentSetting, companySettings,
                                        surveyDetails ) ) {
                                        // post the zillow review to social media
                                        boolean autoPostSuccess = false;
                                        try {
                                            autoPostSuccess = postToSocialMedia( zillowTempPost, agentSetting, surveyDetails );
                                        } catch ( Exception e ) {
                                            LOG.error( "Error occurred while posting to social media. Reason", e );
                                        }
                                        int postToSocialMedia = 0;
                                        if ( autoPostSuccess ) {
                                            postToSocialMedia = CommonConstants.YES;
                                        }
                                        
                                        // check review for complaint resolution
                                        boolean complaintResStatus = triggerComplaintResolutionWorkflowForZillowReview(
                                            companySettings, zillowTempPost, surveyDetails, agentSetting, postToSocialMedia );

                                        if ( !complaintResStatus ) {// add to external survey tracker
                                            socialManagementService.saveExternalSurveyTracker(
                                                zillowTempPost.getEntityColumnName(), zillowTempPost.getEntityId(),
                                                CommonConstants.SURVEY_SOURCE_ZILLOW, agentSetting.getSocialMediaTokens()
                                                    .getZillowToken().getZillowProfileLink(),
                                                zillowTempPost.getZillowReviewUrl(), zillowTempPost.getZillowReviewRating(),
                                                postToSocialMedia, CommonConstants.NO, zillowTempPost.getZillowReviewDate() );
                                        }
                                    }
                                    // add to zillow temp post id to processed list
                                    processedZillowTempPostIds.add( zillowTempPost.getId() );
                                }
                            } catch ( Exception e ) {
                                LOG.error( "Error occurred while auto posting zillow review to social media", e );
                            }
                        }
                        // remove processed zillow temp posts
                        if ( !processedZillowTempPostIds.isEmpty() ) {
                            socialManagementService.removeProcessedZillowTempPosts( processedZillowTempPostIds );
                        }
                    }

                } catch ( Exception e ) {
                    LOG.error( "Exception occurred while processing zillow for company id : " + company.getCompanyId() );
                  //update batch tracker with error message
                    batchTrackerService.updateErrorForBatchTrackerByBatchType(
                        CommonConstants.BATCH_TYPE_ZILLOW_REVIEW_PROCESSOR_AND_AUTO_POSTER, e.getMessage() );
                    //send report bug mail to admin
                    batchTrackerService.sendMailToAdminRegardingBatchError(
                        CommonConstants.BATCH_NAME_ZILLOW_REVIEW_PROCESSOR_AND_AUTO_POSTER, System.currentTimeMillis(), e );
                }
            }

            //Update last build time in batch tracker table
            batchTrackerService
                .updateLastRunEndTimeByBatchType( CommonConstants.BATCH_TYPE_ZILLOW_REVIEW_PROCESSOR_AND_AUTO_POSTER );
        } catch ( Exception e ) {
            LOG.error( "Error in ZillowReviewFetchAndAutoPoster", e );
            try {
                //update batch tracker with error message
                batchTrackerService.updateErrorForBatchTrackerByBatchType(
                    CommonConstants.BATCH_TYPE_ZILLOW_REVIEW_PROCESSOR_AND_AUTO_POSTER, e.getMessage() );
                //send report bug mail to admin
                batchTrackerService.sendMailToAdminRegardingBatchError(
                    CommonConstants.BATCH_NAME_ZILLOW_REVIEW_PROCESSOR_AND_AUTO_POSTER, System.currentTimeMillis(), e );
            } catch ( NoRecordsFetchedException | InvalidInputException e1 ) {
                LOG.error( "Error while updating error message in ZillowReviewFetchAndAutoPoster " );
            } catch ( UndeliveredEmailException e1 ) {
                LOG.error( "Error while sending report exception mail to admin " );
            }
        }
    }


    /**
     * Method to check whether Zillow Review can be posted to social Media
     * */
    boolean checkReviewCanBePostedToSocialMedia( ZillowTempPost zillowTempPost, OrganizationUnitSettings unitSettings,
        OrganizationUnitSettings companySettings, SurveyDetails survey )
    {
        if ( zillowTempPost == null ) {
            LOG.error( "zillowTempPost passed cannot be null" );
            return false;
        }
        if ( unitSettings == null ) {
            LOG.error( "unitSettings passed cannot be null" );
            return false;
        }
        if ( survey == null ) {
            LOG.error( "survey passed cannot be null" );
            return false;
        }

        Calendar cal = Calendar.getInstance();
        cal.add( Calendar.DATE, -zillowAutoPostThreshold );
        cal.set( Calendar.HOUR, 0 );
        cal.set( Calendar.MINUTE, 0 );
        cal.set( Calendar.SECOND, 0 );
        cal.set( Calendar.MILLISECOND, 0 );

        if ( socialManagementService.checkExternalSurveyTrackerExist( zillowTempPost.getEntityColumnName(),
            zillowTempPost.getEntityId(), CommonConstants.SURVEY_SOURCE_ZILLOW, zillowTempPost.getZillowReviewUrl(),
            zillowTempPost.getZillowReviewDate() ) == null
            && unitSettings.getSurvey_settings() != null
            && !utils.checkReviewForSwearWords( zillowTempPost.getZillowReviewDescription(), surveyHandler.getSwearList() )
            && zillowTempPost.getZillowReviewDate().after( cal.getTime() ) ) {
            return true;
        }

        return false;
    }


    /* String getCollectionNameBasedOnColumnName(String columnName){
         if(columnName == null || columnName.isEmpty() ) {
             LOG.error( "columnName passed as argument cannot be null or empty" );
             return null;
         }
         switch(columnName){
             case CommonConstants.COMPANY_ID_COLUMN :
                 return MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION;
             case CommonConstants.REGION_ID_COLUMN :
                 return MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION;
             case CommonConstants.BRANCH_ID_COLUMN :
                 return MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION;
             case CommonConstants.AGENT_ID_COLUMN :
                 return MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION;
         }
         return null;
     }*/

    public boolean postToSocialMedia( ZillowTempPost zillowTempPost, OrganizationUnitSettings organizationUnitSettings,
        SurveyDetails surveyDetails ) throws NonFatalException
    {

        LOG.info( "Method to post feedback of customer to various pages of social networking sites started." );
        boolean successfullyPosted = true;

        try {

            DecimalFormat ratingFormat = CommonConstants.SOCIAL_RANKING_FORMAT;
            ratingFormat.setMinimumFractionDigits( 1 );
            ratingFormat.setMaximumFractionDigits( 1 );

            // add else to support other units in hierarchy
            if ( zillowTempPost.getEntityColumnName().equalsIgnoreCase( CommonConstants.AGENT_ID_COLUMN ) ) {
                long agentId = organizationUnitSettings.getIden();
                User agent = userManagementService.getUserByUserId( agentId );
                ContactDetailsSettings contactDetailSettings = organizationUnitSettings.getContact_details();
                String agentName = emailFormatHelper.getCustomerDisplayNameForEmail( contactDetailSettings.getFirstName(),
                    contactDetailSettings.getLastName() );
                int accountMasterId = 0;
                try {
                    AccountsMaster masterAccount = agent.getCompany().getLicenseDetails().get( CommonConstants.INITIAL_INDEX )
                        .getAccountsMaster();
                    accountMasterId = masterAccount.getAccountsMasterId();
                } catch ( NullPointerException e ) {
                    LOG.error( "NullPointerException caught in postToSocialMedia() while fetching account master id for agent "
                        + agent.getFirstName() );
                    successfullyPosted = false;
                }

                Map<String, List<OrganizationUnitSettings>> settingsMap = socialManagementService
                    .getSettingsForBranchesAndRegionsInHierarchy( agentId );
                List<OrganizationUnitSettings> companySettings = settingsMap
                    .get( MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );
                List<OrganizationUnitSettings> regionSettings = settingsMap
                    .get( MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION );
                List<OrganizationUnitSettings> branchSettings = settingsMap
                    .get( MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION );

                SocialMediaPostDetails socialMediaPostDetails = surveyHandler.getSocialMediaPostDetailsBySurvey( surveyDetails,
                    companySettings.get( 0 ), regionSettings, branchSettings );

                //create socialMediaPostResponseDetails object
                SocialMediaPostResponseDetails socialMediaPostResponseDetails = surveyDetails
                    .getSocialMediaPostResponseDetails();
                if ( socialMediaPostResponseDetails == null ) {
                    socialMediaPostResponseDetails = new SocialMediaPostResponseDetails();
                }
                AgentMediaPostResponseDetails agentMediaPostResponseDetails = socialMediaPostResponseDetails
                    .getAgentMediaPostResponseDetails();
                if ( agentMediaPostResponseDetails == null ) {
                    agentMediaPostResponseDetails = new AgentMediaPostResponseDetails();
                    agentMediaPostResponseDetails.setAgentId( socialMediaPostDetails.getAgentMediaPostDetails().getAgentId() );
                }
                CompanyMediaPostResponseDetails companyMediaPostResponseDetails = socialMediaPostResponseDetails
                    .getCompanyMediaPostResponseDetails();
                if ( companyMediaPostResponseDetails == null ) {
                    companyMediaPostResponseDetails = new CompanyMediaPostResponseDetails();
                    companyMediaPostResponseDetails.setCompanyId( socialMediaPostDetails.getCompanyMediaPostDetails()
                        .getCompanyId() );
                }
                List<RegionMediaPostResponseDetails> regionMediaPostResponseDetailsList = socialMediaPostResponseDetails
                    .getRegionMediaPostResponseDetailsList();
                if ( regionMediaPostResponseDetailsList == null ) {
                    regionMediaPostResponseDetailsList = new ArrayList<RegionMediaPostResponseDetails>();
                }
                List<BranchMediaPostResponseDetails> branchMediaPostResponseDetailsList = socialMediaPostResponseDetails
                    .getBranchMediaPostResponseDetailsList();
                if ( branchMediaPostResponseDetailsList == null ) {
                    branchMediaPostResponseDetailsList = new ArrayList<BranchMediaPostResponseDetails>();
                }


                if ( socialMediaPostDetails.getAgentMediaPostDetails().getSharedOn() == null ) {
                    socialMediaPostDetails.getAgentMediaPostDetails().setSharedOn( new ArrayList<String>() );
                }
                if ( socialMediaPostDetails.getCompanyMediaPostDetails().getSharedOn() == null ) {
                    socialMediaPostDetails.getCompanyMediaPostDetails().setSharedOn( new ArrayList<String>() );
                }

                List<String> agentSocialList = socialMediaPostDetails.getAgentMediaPostDetails().getSharedOn();
                List<String> companySocialList = socialMediaPostDetails.getCompanyMediaPostDetails().getSharedOn();


                for ( BranchMediaPostDetails branchMediaPostDetails : socialMediaPostDetails.getBranchMediaPostDetailsList() ) {
                    if ( branchMediaPostDetails.getSharedOn() == null ) {
                        branchMediaPostDetails.setSharedOn( new ArrayList<String>() );
                    }
                    //create BranchMediaPostResponseDetails
                    BranchMediaPostResponseDetails branchMediaPostResponseDetails = new BranchMediaPostResponseDetails();
                    branchMediaPostResponseDetails.setBranchId( branchMediaPostDetails.getBranchId() );
                    branchMediaPostResponseDetails.setRegionId( branchMediaPostDetails.getRegionId() );
                    if ( socialManagementService.getBMPRDFromBMPRDList( branchMediaPostResponseDetailsList,
                        branchMediaPostDetails.getBranchId() ) == null ) {
                        branchMediaPostResponseDetailsList.add( branchMediaPostResponseDetails );
                    }
                }
                for ( RegionMediaPostDetails regionMediaPostDetails : socialMediaPostDetails.getRegionMediaPostDetailsList() ) {
                    if ( regionMediaPostDetails.getSharedOn() == null ) {
                        regionMediaPostDetails.setSharedOn( new ArrayList<String>() );
                    }
                    //create RegionMediaPostResponseDetails
                    RegionMediaPostResponseDetails regionMediaPostResponseDetails = new RegionMediaPostResponseDetails();
                    regionMediaPostResponseDetails.setRegionId( regionMediaPostDetails.getRegionId() );
                    if ( socialManagementService.getRMPRDFromRMPRDList( regionMediaPostResponseDetailsList,
                        regionMediaPostDetails.getRegionId() ) == null ) {
                        regionMediaPostResponseDetailsList.add( regionMediaPostResponseDetails );
                    }
                }

                socialMediaPostResponseDetails.setAgentMediaPostResponseDetails( agentMediaPostResponseDetails );
                socialMediaPostResponseDetails.setCompanyMediaPostResponseDetails( companyMediaPostResponseDetails );
                socialMediaPostResponseDetails.setBranchMediaPostResponseDetailsList( branchMediaPostResponseDetailsList );
                socialMediaPostResponseDetails.setRegionMediaPostResponseDetailsList( regionMediaPostResponseDetailsList );

                if ( !agentSocialList.contains( CommonConstants.SOCIAL_SURVEY_SOCIAL_SITE ) )
                    agentSocialList.add( CommonConstants.SOCIAL_SURVEY_SOCIAL_SITE );

                if ( !companySocialList.contains( CommonConstants.SOCIAL_SURVEY_SOCIAL_SITE ) )
                    companySocialList.add( CommonConstants.SOCIAL_SURVEY_SOCIAL_SITE );

                for ( RegionMediaPostDetails regionMediaPostDetails : socialMediaPostDetails.getRegionMediaPostDetailsList() ) {
                    List<String> regionSocialList = regionMediaPostDetails.getSharedOn();
                    if ( !regionSocialList.contains( CommonConstants.SOCIAL_SURVEY_SOCIAL_SITE ) )
                        regionSocialList.add( CommonConstants.SOCIAL_SURVEY_SOCIAL_SITE );
                    regionMediaPostDetails.setSharedOn( regionSocialList );


                }
                for ( BranchMediaPostDetails branchMediaPostDetails : socialMediaPostDetails.getBranchMediaPostDetailsList() ) {
                    List<String> branchSocialList = branchMediaPostDetails.getSharedOn();
                    if ( !branchSocialList.contains( CommonConstants.SOCIAL_SURVEY_SOCIAL_SITE ) )
                        branchSocialList.add( CommonConstants.SOCIAL_SURVEY_SOCIAL_SITE );
                    branchMediaPostDetails.setSharedOn( branchSocialList );
                }

                String feedback = Jsoup.parse( zillowTempPost.getZillowReviewDescription() ).text();
                String linkedInfeedback = StringEscapeUtils.escapeXml( feedback );


                // Facebook
                String facebookMessage = ratingFormat.format( zillowTempPost.getZillowReviewRating() ) + "-Star response from "
                    + surveyDetails.getCustomerFirstName() + " for " + agentName + " on Zillow - view at "
                    + zillowTempPost.getZillowReviewUrl();
                facebookMessage += "\n Feedback : " + feedback;

                socialManagementService.postToFacebookForHierarchy( facebookMessage, zillowTempPost.getZillowReviewRating(),
                    zillowTempPost.getZillowReviewUrl(), accountMasterId, socialMediaPostDetails,
                    socialMediaPostResponseDetails );

                // LinkedIn
                String linkedinMessage = ratingFormat.format( zillowTempPost.getZillowReviewRating() ) + "-Star response from "
                    + surveyDetails.getCustomerFirstName() + " for " + agentName + " on Zillow ";
                String linkedinProfileUrl = zillowTempPost.getZillowReviewUrl();
                String linkedinMessageFeedback = "From : " + surveyDetails.getCustomerFirstName() + " - " + linkedInfeedback;

                socialManagementService.postToLinkedInForHierarchy( linkedinMessage, zillowTempPost.getZillowReviewRating(),
                    linkedinProfileUrl, linkedinMessageFeedback, accountMasterId, socialMediaPostDetails,
                    socialMediaPostResponseDetails );

                // Twitter
                String twitterMessage = String.format( CommonConstants.ZILLOW_TWITTER_MESSAGE,
                    ratingFormat.format( zillowTempPost.getZillowReviewRating() ), surveyDetails.getCustomerFirstName(),
                    agentName, "@SocialSurveyMe" ) + zillowTempPost.getZillowReviewUrl();

                socialManagementService.postToTwitterForHierarchy( twitterMessage, zillowTempPost.getZillowReviewRating(),
                    zillowTempPost.getZillowReviewUrl(), accountMasterId, socialMediaPostDetails,
                    socialMediaPostResponseDetails );


                surveyDetails.setSocialMediaPostResponseDetails( socialMediaPostResponseDetails );

                socialMediaPostDetails.getAgentMediaPostDetails().setSharedOn( agentSocialList );
                socialMediaPostDetails.getCompanyMediaPostDetails().setSharedOn( companySocialList );
                surveyDetails.setSocialMediaPostDetails( socialMediaPostDetails );
                surveyHandler.updateSurveyDetails( surveyDetails );

                // check if auto post triggered anywhere in hierarchy
                if ( agentSocialList != null && agentSocialList.size() > 0 ) {
                    return true;
                } else if ( companySocialList != null && companySocialList.size() > 0 ) {
                    return true;
                } else if ( socialMediaPostDetails != null && socialMediaPostDetails.getRegionMediaPostDetailsList() != null ) {
                    for ( RegionMediaPostDetails regionMediaPostDetailsList : socialMediaPostDetails
                        .getRegionMediaPostDetailsList() ) {
                        if ( regionMediaPostDetailsList != null && regionMediaPostDetailsList.getSharedOn() != null
                            && regionMediaPostDetailsList.getSharedOn().size() > 0 )
                            return true;
                    }
                } else if ( socialMediaPostDetails != null && socialMediaPostDetails.getBranchMediaPostDetailsList() != null ) {
                    for ( BranchMediaPostDetails branchMediaPostDetails : socialMediaPostDetails
                        .getBranchMediaPostDetailsList() ) {
                        if ( branchMediaPostDetails != null && branchMediaPostDetails.getSharedOn() != null
                            && branchMediaPostDetails.getSharedOn().size() > 0 )
                            return true;
                    }
                } else {
                    return false;
                }
            }
        } catch ( NonFatalException e ) {
            LOG.error(
                "Non fatal Exception caught in postToSocialMedia() while trying to post to social networking sites. Nested excption is ",
                e );
            successfullyPosted = false;
            throw new NonFatalException( e.getMessage() );
        }
        LOG.info( "Method to post feedback of customer to various pages of social networking sites finished." );
        return successfullyPosted;

    }


    private boolean triggerComplaintResolutionWorkflowForZillowReview( OrganizationUnitSettings companySettings,
        ZillowTempPost zillowTempPost, SurveyDetails survey, OrganizationUnitSettings unitSettings, int autoPostSuccess )
    {
        LOG.info( "Method to trigger complaint resolution workflow for a review, triggerComplaintResolutionWorkflowForZillowReview started." );
        // trigger complaint resolution workflow if configured
        if ( companySettings.getSurvey_settings() != null
            && companySettings.getSurvey_settings().getComplaint_res_settings() != null ) {
            ComplaintResolutionSettings complaintRegistrationSettings = companySettings.getSurvey_settings()
                .getComplaint_res_settings();
            ExternalSurveyTracker externalSurveyTracker = socialManagementService.checkExternalSurveyTrackerExist(
                zillowTempPost.getEntityColumnName(), zillowTempPost.getEntityId(), CommonConstants.SURVEY_SOURCE_ZILLOW,
                zillowTempPost.getZillowReviewUrl(), zillowTempPost.getZillowReviewDate() );
            if ( complaintRegistrationSettings.isEnabled()
                && ( ( zillowTempPost.getZillowReviewRating() > 0d && complaintRegistrationSettings.getRating() > 0d && zillowTempPost
                    .getZillowReviewRating() <= complaintRegistrationSettings.getRating() ) )
                && ( externalSurveyTracker == null || externalSurveyTracker.getComplaintResolutionStatus() == CommonConstants.NO ) ) {
                try {
                    survey.setUnderResolution( true );
                    surveyHandler.updateSurveyAsUnderResolution( survey.get_id() );
                    emailServices.sendZillowReviewComplaintHandleMail( complaintRegistrationSettings.getMailId(),
                        zillowTempPost.getZillowReviewerName(), String.valueOf( zillowTempPost.getZillowReviewRating() ),
                        zillowTempPost.getZillowReviewUrl() );

                    // add complaint resolution status in External Survey Tracker
                    socialManagementService.saveExternalSurveyTracker( zillowTempPost.getEntityColumnName(),
                        zillowTempPost.getEntityId(), CommonConstants.SURVEY_SOURCE_ZILLOW, unitSettings.getSocialMediaTokens()
                            .getZillowToken().getZillowProfileLink(), zillowTempPost.getZillowReviewUrl(),
                        zillowTempPost.getZillowReviewRating(), autoPostSuccess, CommonConstants.YES,
                        zillowTempPost.getZillowReviewDate() );
                    return true;
                } catch ( InvalidInputException | UndeliveredEmailException e ) {
                    LOG.error( "Error while sending complaint resolution mail to admins. Reason :", e );
                    return true;
                }
            }
        }
        LOG.info( "Method to trigger complaint resolution workflow for a review, triggerComplaintResolutionWorkflowForZillowReview finished." );
        return false;
    }


    private void initializeDependencies( JobDataMap jobMap )
    {
        surveyHandler = (SurveyHandler) jobMap.get( "surveyHandler" );
        userManagementService = (UserManagementService) jobMap.get( "userManagementService" );
        socialManagementService = (SocialManagementService) jobMap.get( "socialManagementService" );
        organizationManagementService = (OrganizationManagementService) jobMap.get( "organizationManagementService" );
        profileManagementService = (ProfileManagementService) jobMap.get( "profileManagementService" );
        batchTrackerService = (BatchTrackerService) jobMap.get( "batchTrackerService" );
        zillowAutoPostThreshold = Integer.parseInt( (String) jobMap.get( "zillowAutoPostThreshold" ) );
        utils = (Utils) jobMap.get( "utils" );
        emailFormatHelper = (EmailFormatHelper) jobMap.get( "emailFormatHelper" );
        emailServices = (EmailServices) jobMap.get( "emailServices" );
    }
}
