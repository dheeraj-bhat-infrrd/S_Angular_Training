package com.realtech.socialsurvey.core.starter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.batchtracker.BatchTrackerService;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileNotFoundException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.social.SocialManagementService;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;
import com.realtech.socialsurvey.core.utils.EmailFormatHelper;


public class IncompleteSocialPostReminderSender extends QuartzJobBean
{

    private UserManagementService userManagementService;

    private SurveyHandler surveyHandler;

    private OrganizationManagementService organizationManagementService;

    private SocialManagementService socialManagementService;

    private BatchTrackerService batchTrackerService;

    public static final Logger LOG = LoggerFactory.getLogger( IncompleteSocialPostReminderSender.class );

    private static final String STYLE_ATTR = "align=\"center\"style=\"display:block; width: 150px; height: 40px; line-height: 40px; margin: 10px auto 10px auto;text-decoration:none;background: #009FE3; border-bottom: 2px solid #077faf; color: #fff; text-align: center; border-radius: 3px; font-size: 15px;border: 0;\"";

    private String fbAppId;


    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext )
    {
        try {
            LOG.info( "Executing IncompleteSocialPostReminderSender" );
            initializeDependencies( jobExecutionContext.getMergedJobDataMap() );

            //update last run start time
            batchTrackerService.getLastRunEndTimeAndUpdateLastStartTimeByBatchType(
                CommonConstants.BATCH_TYPE_INCOMPLETE_SOCIAL_POST_REMINDER_SENDER,
                CommonConstants.BATCH_NAME_INCOMPLETE_SOCIAL_POST_REMINDER_SENDER );

            StringBuilder links = new StringBuilder();
            AgentSettings agentSettings = null;
            User user = null;
            for ( Company company : organizationManagementService.getAllCompanies() ) {
                List<SurveyDetails> incompleteSocialPostCustomers = surveyHandler.getIncompleteSocialPostSurveys( company
                    .getCompanyId() );
                for ( SurveyDetails survey : incompleteSocialPostCustomers ) {
                    // To fetch settings of agents/closest in the hierarchy
                    Map<String, String> socialSitesWithSettings = new HashMap<>();
                    try {
                        socialSitesWithSettings = getSocialSitesWithSettingsConfigured( survey );
                    } catch ( InvalidInputException e ) {
                        LOG.error( "InvalidInputException caught in executeInternal() for SocialpostReminderMail" );
                        continue;
                    }

                    links = new StringBuilder();
                    try {
                        agentSettings = userManagementService.getUserSettings( survey.getAgentId() );
                        user = userManagementService.getUserByUserId( survey.getAgentId() );
                    } catch ( InvalidInputException e ) {
                        LOG.error( "InvalidInputException occured while fetch agent settings/ user details for user id "
                            + survey.getAgentId() + ". Nested exception is ", e );
                        continue;
                    }
                    if ( socialSitesWithSettings.get( CommonConstants.REALTOR_LABEL ) != null ) {
                        links.append( "<a " + STYLE_ATTR + " href="
                            + socialSitesWithSettings.get( CommonConstants.REALTOR_LABEL ) + ">"
                            + CommonConstants.REALTOR_LABEL + "</a>" );
                    }
                    if ( socialSitesWithSettings.get( CommonConstants.LENDING_TREE_LABEL ) != null ) {
                        links.append( "<a " + STYLE_ATTR + " href="
                            + socialSitesWithSettings.get( CommonConstants.LENDING_TREE_LABEL ) + ">"
                            + CommonConstants.LENDING_TREE_LABEL + "</a>" );
                    }
                    if ( socialSitesWithSettings.get( CommonConstants.ZILLOW_LABEL ) != null ) {
                        links.append( "<a " + STYLE_ATTR + " href="
                            + socialSitesWithSettings.get( CommonConstants.ZILLOW_LABEL ) + ">" + CommonConstants.ZILLOW_LABEL
                            + "</a>" );
                    }
                    if ( socialSitesWithSettings.get( CommonConstants.YELP_LABEL ) != null ) {
                        links.append( "<a " + STYLE_ATTR + " href=" + socialSitesWithSettings.get( CommonConstants.YELP_LABEL )
                            + ">" + CommonConstants.YELP_LABEL + "</a>" );
                    }
                    if ( socialSitesWithSettings.get( CommonConstants.GOOGLE_PLUS_LABEL ) != null ) {
                        links.append( "<a " + STYLE_ATTR + " href="
                            + socialSitesWithSettings.get( CommonConstants.GOOGLE_PLUS_LABEL ) + ">"
                            + CommonConstants.GOOGLE_PLUS_LABEL + "</a>" );
                    }
                    if ( socialSitesWithSettings.get( CommonConstants.LINKEDIN_LABEL ) != null ) {
                        links.append( "<a " + STYLE_ATTR + " href="
                            + socialSitesWithSettings.get( CommonConstants.LINKEDIN_LABEL ) + ">"
                            + CommonConstants.LINKEDIN_LABEL + "</a>" );
                    }
                    if ( socialSitesWithSettings.get( CommonConstants.TWITTER_LABEL ) != null ) {
                        links.append( "<a " + STYLE_ATTR + " href="
                            + socialSitesWithSettings.get( CommonConstants.TWITTER_LABEL ) + ">"
                            + CommonConstants.TWITTER_LABEL + "</a>" );
                    }
                    if ( socialSitesWithSettings.get( CommonConstants.FACEBOOK_LABEL ) != null ) {
                        links.append( "<a " + STYLE_ATTR + " href="
                            + socialSitesWithSettings.get( CommonConstants.FACEBOOK_LABEL ) + ">"
                            + CommonConstants.FACEBOOK_LABEL + "</a>" );
                    }

                    // Send email to complete social post for survey to each customer.
                    if ( !links.toString().isEmpty() ) {
                        try {
                            String title = "";
                            String phoneNo = "";
                            String companyName = "";
                            if ( agentSettings != null && agentSettings.getContact_details() != null ) {
                                if ( agentSettings.getContact_details().getTitle() != null ) {
                                    title = agentSettings.getContact_details().getTitle();
                                }
                                if ( agentSettings.getContact_details().getContact_numbers() != null
                                    && agentSettings.getContact_details().getContact_numbers().getWork() != null ) {
                                    phoneNo = agentSettings.getContact_details().getContact_numbers().getWork();
                                }
                                if ( company.getCompany() != null ) {
                                    companyName = company.getCompany();
                                }
                            }

                            String logoUrl = null;
                            try {
                                logoUrl = userManagementService.fetchAppropriateLogoUrlFromHierarchyForUser( survey
                                    .getAgentId() );
                            } catch ( NoRecordsFetchedException e ) {
                                LOG.error( "Error while fatching logo for user with id : " + survey.getAgentId(), e );
                            } catch ( ProfileNotFoundException e ) {
                                LOG.error( "Error while fatching logo for user with id : " + survey.getAgentId(), e );
                            }

                            surveyHandler.sendSocialPostReminderMail( survey.getCustomerEmail(), survey.getCustomerFirstName(),
                                survey.getCustomerLastName(), user, links.toString() );
                            surveyHandler.updateReminderCountForSocialPosts( survey.getAgentId(), survey.getCustomerEmail() );
                        } catch ( InvalidInputException | UndeliveredEmailException | ProfileNotFoundException e ) {
                            LOG.error(
                                "Exception caught in IncompleteSurveyReminderSender.main while trying to send reminder mail to "
                                    + survey.getCustomerFirstName() + " for completion of survey. Nested exception is ", e );
                            continue;
                        }
                    }
                }
            }

            //Update last build time in batch tracker table
            batchTrackerService
                .updateLastRunEndTimeByBatchType( CommonConstants.BATCH_TYPE_INCOMPLETE_SOCIAL_POST_REMINDER_SENDER );

        } catch ( Exception e ) {
            LOG.error( "Error in IncompleteSocialPostReminderSender", e );
            try {
                //update batch tracker with error message
                batchTrackerService.updateErrorForBatchTrackerByBatchType(
                    CommonConstants.BATCH_TYPE_INCOMPLETE_SOCIAL_POST_REMINDER_SENDER, e.getMessage() );
                //send report bug mail to admin
                batchTrackerService.sendMailToAdminRegardingBatchError(
                    CommonConstants.BATCH_NAME_INCOMPLETE_SOCIAL_POST_REMINDER_SENDER, System.currentTimeMillis(), e );
            } catch ( NoRecordsFetchedException | InvalidInputException e1 ) {
                LOG.error( "Error while updating error message in IncompleteSocialPostReminderSender " );
            } catch ( UndeliveredEmailException e1 ) {
                LOG.error( "Error while sending report excption mail to admin " );
            }
        }
    }


    private void initializeDependencies( JobDataMap jobMap )
    {
        surveyHandler = (SurveyHandler) jobMap.get( "surveyHandler" );
        userManagementService = (UserManagementService) jobMap.get( "userManagementService" );
        socialManagementService = (SocialManagementService) jobMap.get( "socialManagementService" );
        organizationManagementService = (OrganizationManagementService) jobMap.get( "organizationManagementService" );
        batchTrackerService = (BatchTrackerService) jobMap.get( "batchTrackerService" );
        fbAppId = (String) jobMap.get( "fbAppId" );

    }


    private Map<String, String> getSocialSitesWithSettingsConfigured( SurveyDetails survey ) throws InvalidInputException
    {
        LOG.debug( "Method to get settings of agent and admins in the hierarchy getSocialSitesWithSettingsConfigured() started." );
        long agentId = survey.getAgentId();
        OrganizationUnitSettings agentSettings = userManagementService.getUserSettings( agentId );
        Map<String, List<OrganizationUnitSettings>> settingsMap = socialManagementService
            .getSettingsForBranchesRegionsAndCompanyInAgentsHierarchy( agentId );
        List<OrganizationUnitSettings> companySettings = settingsMap
            .get( MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );
        List<OrganizationUnitSettings> regionSettings = settingsMap
            .get( MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION );
        List<OrganizationUnitSettings> branchSettings = settingsMap
            .get( MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION );
        Map<String, String> socialSiteUrlMap = new HashMap<String, String>();

        // Enabling Zillow, Realtor, Lending tree and Yelp from agent settings
        if ( agentSettings != null ) {
            if ( agentSettings.getSocialMediaTokens() != null ) {
                if ( agentSettings.getSocialMediaTokens().getRealtorToken() != null ) {
                    socialSiteUrlMap.put( CommonConstants.REALTOR_LABEL,
                        generateSocialSiteUrl( survey, CommonConstants.REALTOR_LABEL, agentSettings ) );
                }
                if ( agentSettings.getSocialMediaTokens().getLendingTreeToken() != null ) {
                    socialSiteUrlMap.put( CommonConstants.LENDING_TREE_LABEL,
                        generateSocialSiteUrl( survey, CommonConstants.LENDING_TREE_LABEL, agentSettings ) );
                }
                if ( agentSettings.getSocialMediaTokens().getZillowToken() != null ) {
                    socialSiteUrlMap.put( CommonConstants.ZILLOW_LABEL,
                        generateSocialSiteUrl( survey, CommonConstants.ZILLOW_LABEL, agentSettings ) );
                }
                if ( agentSettings.getSocialMediaTokens().getYelpToken() != null ) {
                    socialSiteUrlMap.put( CommonConstants.YELP_LABEL,
                        generateSocialSiteUrl( survey, CommonConstants.YELP_LABEL, agentSettings ) );
                }
            }
        }

        // Enabling Zillow, Realtor, Lending tree and Yelp if anyone closest in hierarchy to agent has
        // configured in settings.
        for ( OrganizationUnitSettings setting : branchSettings ) {
            if ( setting.getSocialMediaTokens() != null ) {
                if ( setting.getSocialMediaTokens().getRealtorToken() != null
                    && socialSiteUrlMap.get( CommonConstants.REALTOR_LABEL ) == null ) {
                    socialSiteUrlMap.put( CommonConstants.REALTOR_LABEL,
                        generateSocialSiteUrl( survey, CommonConstants.REALTOR_LABEL, setting ) );
                }
                if ( setting.getSocialMediaTokens().getLendingTreeToken() != null
                    && socialSiteUrlMap.get( CommonConstants.LENDING_TREE_LABEL ) == null ) {
                    socialSiteUrlMap.put( CommonConstants.LENDING_TREE_LABEL,
                        generateSocialSiteUrl( survey, CommonConstants.LENDING_TREE_LABEL, setting ) );
                }
                if ( setting.getSocialMediaTokens().getZillowToken() != null
                    && socialSiteUrlMap.get( CommonConstants.ZILLOW_LABEL ) == null ) {
                    socialSiteUrlMap.put( CommonConstants.ZILLOW_LABEL,
                        generateSocialSiteUrl( survey, CommonConstants.ZILLOW_LABEL, setting ) );
                }
                if ( setting.getSocialMediaTokens().getYelpToken() != null
                    && socialSiteUrlMap.get( CommonConstants.YELP_LABEL ) == null ) {
                    socialSiteUrlMap.put( CommonConstants.YELP_LABEL,
                        generateSocialSiteUrl( survey, CommonConstants.YELP_LABEL, setting ) );
                }
            }
        }
        for ( OrganizationUnitSettings setting : regionSettings ) {
            if ( setting.getSocialMediaTokens() != null ) {
                if ( setting.getSocialMediaTokens().getRealtorToken() != null
                    && socialSiteUrlMap.get( CommonConstants.REALTOR_LABEL ) == null ) {
                    socialSiteUrlMap.put( CommonConstants.REALTOR_LABEL,
                        generateSocialSiteUrl( survey, CommonConstants.REALTOR_LABEL, setting ) );
                }
                if ( setting.getSocialMediaTokens().getLendingTreeToken() != null
                    && socialSiteUrlMap.get( CommonConstants.LENDING_TREE_LABEL ) == null ) {
                    socialSiteUrlMap.put( CommonConstants.LENDING_TREE_LABEL,
                        generateSocialSiteUrl( survey, CommonConstants.LENDING_TREE_LABEL, setting ) );
                }
                if ( setting.getSocialMediaTokens().getZillowToken() != null
                    && socialSiteUrlMap.get( CommonConstants.ZILLOW_LABEL ) == null ) {
                    socialSiteUrlMap.put( CommonConstants.ZILLOW_LABEL,
                        generateSocialSiteUrl( survey, CommonConstants.ZILLOW_LABEL, setting ) );
                }
                if ( setting.getSocialMediaTokens().getYelpToken() != null
                    && socialSiteUrlMap.get( CommonConstants.YELP_LABEL ) == null ) {
                    socialSiteUrlMap.put( CommonConstants.YELP_LABEL,
                        generateSocialSiteUrl( survey, CommonConstants.YELP_LABEL, setting ) );
                }
            }
        }
        for ( OrganizationUnitSettings setting : companySettings ) {
            if ( setting.getSocialMediaTokens() != null ) {
                if ( setting.getSocialMediaTokens().getRealtorToken() != null
                    && socialSiteUrlMap.get( CommonConstants.REALTOR_LABEL ) == null ) {
                    socialSiteUrlMap.put( CommonConstants.REALTOR_LABEL,
                        generateSocialSiteUrl( survey, CommonConstants.REALTOR_LABEL, setting ) );
                }
                if ( setting.getSocialMediaTokens().getLendingTreeToken() != null
                    && socialSiteUrlMap.get( CommonConstants.LENDING_TREE_LABEL ) == null ) {
                    socialSiteUrlMap.put( CommonConstants.LENDING_TREE_LABEL,
                        generateSocialSiteUrl( survey, CommonConstants.LENDING_TREE_LABEL, setting ) );
                }
                if ( setting.getSocialMediaTokens().getZillowToken() != null
                    && socialSiteUrlMap.get( CommonConstants.ZILLOW_LABEL ) == null ) {
                    socialSiteUrlMap.put( CommonConstants.ZILLOW_LABEL,
                        generateSocialSiteUrl( survey, CommonConstants.ZILLOW_LABEL, setting ) );
                }
                if ( setting.getSocialMediaTokens().getYelpToken() != null
                    && socialSiteUrlMap.get( CommonConstants.YELP_LABEL ) == null ) {
                    socialSiteUrlMap.put( CommonConstants.YELP_LABEL,
                        generateSocialSiteUrl( survey, CommonConstants.YELP_LABEL, setting ) );
                }
            }
        }

        // build social site url's like Google Plus, LinkedIn, Twitter and Facebook
        socialSiteUrlMap.put( CommonConstants.GOOGLE_PLUS_LABEL,
            generateSocialSiteUrl( survey, CommonConstants.GOOGLE_PLUS_LABEL, agentSettings ) );
        socialSiteUrlMap.put( CommonConstants.LINKEDIN_LABEL,
            generateSocialSiteUrl( survey, CommonConstants.LINKEDIN_LABEL, agentSettings ) );
        socialSiteUrlMap.put( CommonConstants.TWITTER_LABEL,
            generateSocialSiteUrl( survey, CommonConstants.TWITTER_LABEL, agentSettings ) );
        socialSiteUrlMap.put( CommonConstants.FACEBOOK_LABEL,
            generateSocialSiteUrl( survey, CommonConstants.FACEBOOK_LABEL, agentSettings ) );
        LOG.debug( "Method getSocialSitesWithSettingsConfigured() finished" );
        return socialSiteUrlMap;
    }


    private String generateSocialSiteUrl( SurveyDetails survey, String socialSite,
        OrganizationUnitSettings organizationUnitSettings ) throws InvalidInputException
    {
        LOG.debug( "Method to generate URL for social sites, generateSocialSiteUrl() started." );
        double fmt_Rating = surveyHandler.getFormattedSurveyScore( survey.getScore() );
        String url = "";
        String customerDisplayName = new EmailFormatHelper().getCustomerDisplayNameForEmail( survey.getCustomerFirstName(),
            survey.getCustomerLastName() );
        switch ( socialSite ) {
            case CommonConstants.REALTOR_LABEL:
                url = organizationUnitSettings.getSocialMediaTokens().getRealtorToken().getRealtorProfileLink()
                    + "#reviews-section";
                break;
            case CommonConstants.LENDING_TREE_LABEL:
                url = organizationUnitSettings.getSocialMediaTokens().getLendingTreeToken().getLendingTreeProfileLink();
                break;
            case CommonConstants.ZILLOW_LABEL:
                url = organizationUnitSettings.getSocialMediaTokens().getZillowToken().getZillowProfileLink();
                break;
            case CommonConstants.YELP_LABEL:
                url = organizationUnitSettings.getSocialMediaTokens().getYelpToken().getYelpPageLink();
                break;
            case CommonConstants.GOOGLE_PLUS_LABEL:
                url = "https://plus.google.com/share?url=" + organizationUnitSettings.getCompleteProfileUrl();
                break;
            case CommonConstants.LINKEDIN_LABEL:
                url += "https://www.linkedin.com/shareArticle?mini=true&url="
                    + organizationUnitSettings.getCompleteProfileUrl() + "&title=&summary=" + fmt_Rating
                    + "-star response from " + customerDisplayName + " for " + survey.getAgentName() + " at SocialSurvey - "
                    + survey.getReview() + "&source=";
                break;
            case CommonConstants.TWITTER_LABEL:
                url += "https://twitter.com/intent/tweet?text=" + fmt_Rating + "-star response from " + survey.getAgentName()
                    + " for " + survey.getAgentName() + " at SocialSurvey - " + survey.getReview() + ".&url="
                    + organizationUnitSettings.getCompleteProfileUrl();
                break;
            case CommonConstants.FACEBOOK_LABEL:
                url += "https://www.facebook.com/dialog/feed?app_id=" + fbAppId + "&link="
                    + organizationUnitSettings.getCompleteProfileUrl() + "&description=" + fmt_Rating + "-star response from "
                    + customerDisplayName + " for " + survey.getAgentName() + " at SocialSurvey - " + survey.getReview()
                    + ".&redirect_uri=https://www.facebook.com";
                break;
        }

        LOG.debug( "Method to generate URL for social sites, generateSocialSiteUrl() ended." );
        LOG.debug( "Encoding of URL started." );
        url = url.replaceAll( " ", "%20" );
        LOG.debug( "Encoding of URL ended. Encoded URL : " + url );
        LOG.debug( "Method to generate URL for social sites, generateSocialSiteUrl() ended." );
        return url;
    }
}
