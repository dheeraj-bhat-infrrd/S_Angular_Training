package com.realtech.socialsurvey.core.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.Utils;
import com.realtech.socialsurvey.core.dao.SurveyPreInitiationDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SocialMediaTokens;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.enums.OrganizationUnit;
import com.realtech.socialsurvey.core.enums.SettingsForApplication;
import com.realtech.socialsurvey.core.exception.FatalException;
import com.realtech.socialsurvey.core.exception.InternalServerException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.ProfileServiceErrorCode;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileNotFoundException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.settingsmanagement.impl.InvalidSettingsStateException;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;


@Component
public class EmailFormatHelper
{
    private static final Logger LOG = LoggerFactory.getLogger( EmailFormatHelper.class );

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private OrganizationManagementService organizationManagementService;

    @Autowired
    private ProfileManagementService profileManagementService;

    @Autowired
    private SurveyHandler surveyHandler;

    @Autowired
    private SurveyPreInitiationDao surveyPreInitiationDao;

    @Autowired
    private Utils utils;

    private static final String PARAM_PATTERN_REGEX = "\\[(.*?)\\]";
    private static final String PARAM_PATTERN = "%s";
    private static final String PARAM_OPEN = "[";
    private static final String PARAM_CLOSE = "]";


    public String buildAgentSignature( String agentName, String agentPhone, String agentTitle, String companyName )
    {
        LOG.info( "Formatting Individual Signature for email" );
        StringBuilder agentDetail = new StringBuilder();
        /*if (agentName != null && !agentName.isEmpty()) {
            agentDetail.append(agentName).append("<br />");
        }*/
        if ( agentPhone != null && !agentPhone.isEmpty() ) {
            agentDetail.append( agentPhone ).append( "<br />" );
        }
        if ( agentTitle != null && !agentTitle.isEmpty() ) {
            agentDetail.append( agentTitle ).append( "<br />" );
        }
        if ( companyName != null && !companyName.isEmpty() ) {
            agentDetail.append( companyName ).append( "<br />" );
        }
        return agentDetail.toString();
    }


    public String replaceEmailBodyWithParams( String mailBody, List<String> paramOrder )
    {
        LOG.info( "Replacing Default String with Email Params" );
        if ( paramOrder != null && !paramOrder.isEmpty() ) {
            for ( String replacementArg : paramOrder ) {
                mailBody = mailBody.replaceFirst( PARAM_PATTERN, PARAM_OPEN + replacementArg + PARAM_CLOSE );
            }
        }
        return mailBody;
    }


    public String replaceEmailBodyParamsWithDefaultValue( String mailBody, List<String> paramOrder )
    {
        LOG.info( "Replacing Email Params with Default String" );
        Pattern pattern = Pattern.compile( PARAM_PATTERN_REGEX );
        Matcher matcher = pattern.matcher( mailBody );
        while ( matcher.find() ) {
            paramOrder.add( matcher.group( 1 ) );
        }
        mailBody = mailBody.replaceAll( PARAM_PATTERN_REGEX, PARAM_PATTERN );
        return mailBody;
    }


    /**
     * Converts email html format to txt format
     * @param htmlFormat
     */
    public String getEmailTextFormat( String htmlFormat )
    {
        LOG.debug( "Converting html to text format" );
        String textFormat = null;
        if ( htmlFormat != null && !htmlFormat.isEmpty() ) {
            Document document = Jsoup.parse( htmlFormat );
            textFormat = document.body().text();
        }
        return textFormat;
    }


    public String getCustomerDisplayNameForEmail( String custFirstName, String custLastName ) throws InvalidInputException
    {
        LOG.debug( "method getCustomerDisplayNameForEmail started for first name : " + custFirstName + " and last name : "
            + custLastName );
        String customerName = custFirstName;
        if ( custFirstName == null || custFirstName.isEmpty() ) {
            throw new InvalidInputException( "Invalid parameter: passed parameter custFirstName is null or empty" );
        }
        if ( custLastName != null && !custLastName.isEmpty() ) {
            customerName += " " + custLastName;
        }

        String[] custNameArray = customerName.split( " " );
        String custDisplayName = custNameArray[0];
        if ( custNameArray.length > 1 ) {
            if ( custNameArray[1] != null && custNameArray[1].length() >= 1 ) {
                custDisplayName += " " + custNameArray[1].substring( 0, 1 ) + ".";
            }
        }
        return WordUtils.capitalize( custDisplayName );
    }


    @Transactional
    public String replaceLegends( boolean isSubject, String content, String baseUrl, String logoUrl, String link,
        String custFirstName, String custLastName, String agentName, String agentFirstName, String agentSignature,
        String recipientMailId, String senderEmail, String companyName, String initiatedDate, String currentYear,
        String fullAddress, String links, String agentProfileName, String companyDisclaimer, String agentDisclaimer,
        String agentLicense, String agentTitle, String agentPhoneNumber, String unsubscribeUrl, long userId,
        String branchName, String regionName ) throws InvalidInputException
    {
        LOG.info( "Method to replace legends with values called, replaceLegends() started" );
        if ( StringUtils.isEmpty( content ) ) {
            LOG.error( "Content passed in replaceLegends is null or empty" );
            throw new InvalidInputException( "Content passed in replaceLegends is null or empty" );
        }

        long agentId = 0;
        String customerName = getCustomerDisplayNameForEmail( custFirstName, custLastName );
        
        //take initial of second half  of first name if there is one
        if(custFirstName.indexOf(" ") > 0 && custFirstName.length() >= custFirstName.indexOf(" ") + 2 ) {
        		custFirstName = custFirstName.substring(0, custFirstName.indexOf(" ") + 2 );
		}
        
        content = content.replaceAll( "\\[BaseUrl\\]", "" + baseUrl );
        content = content.replaceAll( "\\[AppBaseUrl\\]", "" + baseUrl );
        content = content.replaceAll( "\\[LogoUrl\\]", "" + logoUrl );
        content = content.replaceAll( "\\[Link\\]", "" + link );
        content = content.replaceAll( "\\[Links\\]", "" + links );
        content = content.replaceAll( "\\[Name\\]", "" + customerName );
        if ( !isSubject ) {
            content = content.replaceFirst( "\\[FirstName\\]", WordUtils.capitalize( custFirstName ) );
        }

        content = content.replaceAll( "\\[FirstName\\]", "" + custFirstName );
        content = content.replaceAll( "\\[AgentName\\]", "" + agentName );
        content = content.replaceAll( "\\[AgentFirstName\\]", "" + agentFirstName );
        content = content.replace( "[AgentSignature]", "" + agentSignature );
        content = content.replaceAll( "\\[RecipientEmail\\]", "" + recipientMailId );
        content = content.replaceAll( "\\[SenderEmail\\]", "" + senderEmail );
        content = content.replaceAll( "\\[CompanyName\\]", "" + companyName );
        content = content.replaceAll( "\\[InitiatedDate\\]", "" + initiatedDate );
        content = content.replaceAll( "\\[CurrentYear\\]", "" + currentYear );
        content = content.replaceAll( "\\[FullAddress\\]", "" + fullAddress );
        content = content.replaceAll( "\\[AgentProfileName\\]", "" + agentProfileName );

        content = content.replaceAll( "\\[AgentTitle\\]", "" + agentTitle );
        content = content.replaceAll( "\\[AgentPhoneNumber\\]", "" + agentPhoneNumber );

        //JIRA SS-473 begin
        content = content.replace( "[CompanyDisclaimer]", companyDisclaimer );
        content = content.replace( "[AgentDisclaimer]", agentDisclaimer );
        content = content.replace( "[AgentLicense]", agentLicense );
        //JIRA SS-473 end
        //JIRA SS-1547
        content = content.replace( "[unsubscribeUrl]", unsubscribeUrl );
        //Add region name and branch name in legend
        content = content.replace( "[BranchName]", branchName );
        content = content.replace( "[RegionName]", regionName );
        String company_facebook_link = null;
        String company_twitter_link = null;
        String company_linkedin_link = null;
        String company_google_plus_link = null;
        String company_google_review_link = null;
        String company_zillow_link = null;
        String company_lending_tree_link = null;
        String company_realtor_com_link = null;
        String company_yelp_link = null;

        String facebook_link = null;
        String twitter_link = null;
        String linkedin_link = null;
        String google_plus_link = null;
        String google_review_link = null;
        String zillow_link = null;
        String lending_tree_link = null;
        String realtor_com_link = null;
        String yelp_link = null;
        //JIRA SS-1504
        String property_address = null;

        //JIRA SS-626 begin
        try {
            User user = userManagementService.getUserByUserId(userId);
            if ( user == null ) {
                throw new NoRecordsFetchedException( "No user found" );
            }
            agentId = user.getUserId();
            AgentSettings agentSettings = userManagementService.getUserSettings( user.getUserId() );
            OrganizationUnitSettings branchSettings = null;
            OrganizationUnitSettings regionSettings = null;
            OrganizationUnitSettings companySettings = null;
            if ( agentSettings == null ) {
                throw new NoRecordsFetchedException( "No agent setting found" );
            }
            OrganizationUnitSettings profileSettings = null;

            Map<String, Long> hierarchyMap = profileManagementService.getPrimaryHierarchyByAgentProfile( agentSettings );
            if ( hierarchyMap != null && !hierarchyMap.isEmpty() ) {
                if ( hierarchyMap.containsKey( CommonConstants.COMPANY_ID_COLUMN ) )
                    companySettings = organizationManagementService
                        .getCompanySettings( hierarchyMap.get( CommonConstants.COMPANY_ID_COLUMN ) );
                if ( hierarchyMap.containsKey( CommonConstants.REGION_ID_COLUMN ) )
                    regionSettings = organizationManagementService
                        .getRegionSettings( hierarchyMap.get( CommonConstants.REGION_ID_COLUMN ) );
                if ( hierarchyMap.containsKey( CommonConstants.BRANCH_ID_COLUMN ) )
                    branchSettings = organizationManagementService
                        .getBranchSettingsDefault( hierarchyMap.get( CommonConstants.BRANCH_ID_COLUMN ) );
            }
            Map<SettingsForApplication, OrganizationUnit> map = null;
            try {
                map = profileManagementService.getPrimaryHierarchyByEntity( CommonConstants.AGENT_ID, agentSettings.getIden() );
                if ( map == null ) {
                    LOG.error( "Unable to fetch primary profile for this user " );
                    throw new FatalException( "Unable to fetch primary profile this user " + agentSettings.getIden() );
                }
            } catch ( InvalidSettingsStateException e ) {
                throw new InternalServerException(
                    new ProfileServiceErrorCode( CommonConstants.ERROR_CODE_REGION_PROFILE_SERVICE_FAILURE,
                        CommonConstants.SERVICE_CODE_REGION_PROFILE, "Error occured while fetching region profile" ),
                    e.getMessage(), e );
            } catch ( ProfileNotFoundException e ) {
                LOG.error( "No profile found for the user ", e );
            }

            profileSettings = profileManagementService.fillUnitSettings( agentSettings,
                MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, companySettings, regionSettings, branchSettings,
                agentSettings, map, true );

            SocialMediaTokens socialMediaTokens = profileSettings.getSocialMediaTokens();
            //Set aggregated values
            if ( socialMediaTokens != null ) {
                if ( socialMediaTokens.getFacebookToken() != null )
                    facebook_link = socialMediaTokens.getFacebookToken().getFacebookPageLink();
                if ( socialMediaTokens.getTwitterToken() != null )
                    twitter_link = socialMediaTokens.getTwitterToken().getTwitterPageLink();
                if ( socialMediaTokens.getLinkedInToken() != null )
                    linkedin_link = socialMediaTokens.getLinkedInToken().getLinkedInPageLink();
                if ( socialMediaTokens.getGoogleToken() != null )
                    google_plus_link = socialMediaTokens.getGoogleToken().getProfileLink();
                if ( socialMediaTokens.getGoogleBusinessToken() != null )
                    google_review_link = socialMediaTokens.getGoogleBusinessToken().getGoogleBusinessLink();
                if ( socialMediaTokens.getZillowToken() != null )
                    zillow_link = socialMediaTokens.getZillowToken().getZillowProfileLink();
                if ( socialMediaTokens.getLendingTreeToken() != null )
                    lending_tree_link = socialMediaTokens.getLendingTreeToken().getLendingTreeProfileLink();
                if ( socialMediaTokens.getRealtorToken() != null )
                    realtor_com_link = socialMediaTokens.getRealtorToken().getRealtorProfileLink();
                if ( socialMediaTokens.getYelpToken() != null )
                    yelp_link = socialMediaTokens.getYelpToken().getYelpPageLink();
            }

            socialMediaTokens = companySettings.getSocialMediaTokens();
            //Set company values
            if ( socialMediaTokens != null ) {
                if ( socialMediaTokens.getFacebookToken() != null )
                    company_facebook_link = socialMediaTokens.getFacebookToken().getFacebookPageLink();
                if ( socialMediaTokens.getTwitterToken() != null )
                    company_twitter_link = socialMediaTokens.getTwitterToken().getTwitterPageLink();
                if ( socialMediaTokens.getLinkedInToken() != null )
                    company_linkedin_link = socialMediaTokens.getLinkedInToken().getLinkedInPageLink();
                if ( socialMediaTokens.getGoogleToken() != null )
                    company_google_plus_link = socialMediaTokens.getGoogleToken().getProfileLink();
                if ( socialMediaTokens.getGoogleBusinessToken() != null )
                    company_google_review_link = socialMediaTokens.getGoogleBusinessToken().getGoogleBusinessLink();
                if ( socialMediaTokens.getZillowToken() != null )
                    company_zillow_link = socialMediaTokens.getZillowToken().getZillowProfileLink();
                if ( socialMediaTokens.getLendingTreeToken() != null )
                    company_lending_tree_link = socialMediaTokens.getLendingTreeToken().getLendingTreeProfileLink();
                if ( socialMediaTokens.getRealtorToken() != null )
                    company_realtor_com_link = socialMediaTokens.getRealtorToken().getRealtorProfileLink();
                if ( socialMediaTokens.getYelpToken() != null )
                    company_yelp_link = socialMediaTokens.getYelpToken().getYelpPageLink();
            }
        } catch ( NoRecordsFetchedException e ) {
            LOG.error( "No user found with email address : " + senderEmail );
        } catch ( ProfileNotFoundException e ) {
            LOG.error( "An error occurred while fetching the profile. Reason : ", e );
        }

        content = content.replace( "[facebook_link]", processUrl( facebook_link ) );
        content = content.replace( "[twitter_link]", processUrl( twitter_link ) );
        content = content.replace( "[linkedin_link]", processUrl( linkedin_link ) );
        content = content.replace( "[google_plus_link]", processUrl( google_plus_link ) );
        content = content.replace( "[google_review_link]", processUrl( google_review_link ) );
        content = content.replace( "[zillow_link]", processUrl( zillow_link ) );
        content = content.replace( "[lending_tree_link]", processUrl( lending_tree_link ) );
        content = content.replace( "[realtor_com_link]", processUrl( realtor_com_link ) );
        content = content.replace( "[yelp_link]", processUrl( yelp_link ) );
        content = content.replace( "[company_facebook_link]", processUrl( company_facebook_link ) );
        content = content.replace( "[company_twitter_link]", processUrl( company_twitter_link ) );
        content = content.replace( "[company_linkedin_link]", processUrl( company_linkedin_link ) );
        content = content.replace( "[company_google_plus_link]", processUrl( company_google_plus_link ) );
        content = content.replace( "[company_google_review_link]", processUrl( company_google_review_link ) );
        content = content.replace( "[company_zillow_link]", processUrl( company_zillow_link ) );
        content = content.replace( "[company_lending_tree_link]", processUrl( company_lending_tree_link ) );
        content = content.replace( "[company_realtor_com_link]", processUrl( company_realtor_com_link ) );
        content = content.replace( "[company_yelp_link]", processUrl( company_yelp_link ) );

        Map<String, String> surveyMap = fetchSurveySourceId( agentId, recipientMailId, initiatedDate );
        content = content.replace( "[survey_source_id]", surveyMap.get( CommonConstants.SURVEY_SOURCE_ID_COLUMN ) );
        content = content.replace( "[survey_source]", surveyMap.get( CommonConstants.SURVEY_SOURCE_COLUMN ) );
        //JIRA SS-1504
        content = content.replace("[property_address]", surveyMap.get( CommonConstants.PROPERTY_ADDRESS ) );
        content = content.replace("[loan_processor_name]", surveyMap.get( CommonConstants.LOAN_PROCESSOR_NAME ) );
        //JIRA SS-626 end
        content = content.replaceAll( "null", "" );
        LOG.info( "Method to replace legends with values called, replaceLegends() ended" );
        return content;
    }


    @Transactional
    public String replaceLegendsWithSettings( boolean isSubject, String content, String baseUrl, String logoUrl, String link,
        String custFirstName, String custLastName, String agentName, String agentFirstName, String agentSignature,
        String recipientMailId, String senderEmail, String companyName, String initiatedDate, String currentYear,
        String fullAddress, String links, String agentProfileName, String companyDisclaimer, String agentDisclaimer,
        String agentLicense, String agentTitle, String agentPhoneNumber, User user, OrganizationUnitSettings agentSettings,
        OrganizationUnitSettings branchSettings, OrganizationUnitSettings regionSettings,
        OrganizationUnitSettings companySettings, Map<SettingsForApplication, OrganizationUnit> map,
        Map<String, String> surveyMap ) throws InvalidInputException
    {
        LOG.info( "Method to replace legends with values called, replaceLegends() started" );
        if ( StringUtils.isEmpty( content ) ) {
            LOG.error( "Content passed in replaceLegends is null or empty" );
            throw new InvalidInputException( "Content passed in replaceLegends is null or empty" );
        }

        String customerName = getCustomerDisplayNameForEmail( custFirstName, custLastName );

        content = StringUtils.replace( content, "[BaseUrl]", "" + baseUrl );
        content = StringUtils.replace( content, "[AppBaseUrl]", "" + baseUrl );
        content = StringUtils.replace( content, "[LogoUrl]", "" + logoUrl );
        content = StringUtils.replace( content, "[Link]", "" + link );
        content = StringUtils.replace( content, "[Links]", "" + links );
        content = StringUtils.replace( content, "[Name]", "" + customerName );
        if ( !isSubject ) {
            content = content.replaceFirst( "\\[FirstName\\]", WordUtils.capitalize( custFirstName ) );
        }

        content = StringUtils.replace( content, "[FirstName]", "" + custFirstName );
        content = StringUtils.replace( content, "[AgentName]", "" + agentName );
        content = StringUtils.replace( content, "[AgentFirstName]", "" + agentFirstName );
        content = StringUtils.replace( content, "[AgentSignature]", "" + agentSignature );
        content = StringUtils.replace( content, "[RecipientEmail]", "" + recipientMailId );
        content = StringUtils.replace( content, "[SenderEmail]", "" + senderEmail );
        content = StringUtils.replace( content, "[CompanyName]", "" + companyName );
        content = StringUtils.replace( content, "[InitiatedDate]", "" + initiatedDate );
        content = StringUtils.replace( content, "[CurrentYear]", "" + currentYear );
        content = StringUtils.replace( content, "[FullAddress]", "" + fullAddress );
        content = StringUtils.replace( content, "[AgentProfileName]", "" + agentProfileName );

        content = StringUtils.replace( content, "[AgentTitle]", agentTitle );
        content = StringUtils.replace( content, "[AgentPhoneNumber]", agentPhoneNumber );
        //JIRA SS-473 begin
        content = StringUtils.replace( content, "[CompanyDisclaimer]", companyDisclaimer );
        content = StringUtils.replace( content, "[AgentDisclaimer]", agentDisclaimer );
        content = StringUtils.replace( content, "[AgentLicense]", agentLicense );
        //JIRA SS-473 end
        String company_facebook_link = null;
        String company_twitter_link = null;
        String company_linkedin_link = null;
        String company_google_plus_link = null;
        String company_google_review_link = null;
        String company_zillow_link = null;
        String company_lending_tree_link = null;
        String company_realtor_com_link = null;
        String company_yelp_link = null;

        String facebook_link = null;
        String twitter_link = null;
        String linkedin_link = null;
        String google_plus_link = null;
        String google_review_link = null;
        String zillow_link = null;
        String lending_tree_link = null;
        String realtor_com_link = null;
        String yelp_link = null;

        //JIRA SS-626 begin
        try {
            if ( user == null ) {
                throw new NoRecordsFetchedException( "No user found" );
            }
            if ( agentSettings == null ) {
                throw new NoRecordsFetchedException( "No agent setting found" );
            }
            OrganizationUnitSettings profileSettings = null;
            profileSettings = profileManagementService.fillUnitSettings( agentSettings,
                MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, companySettings, regionSettings, branchSettings,
                agentSettings, map, true );

            SocialMediaTokens socialMediaTokens = profileSettings.getSocialMediaTokens();
            //Set aggregated values
            if ( socialMediaTokens != null ) {
                if ( socialMediaTokens.getFacebookToken() != null )
                    facebook_link = socialMediaTokens.getFacebookToken().getFacebookPageLink();
                if ( socialMediaTokens.getTwitterToken() != null )
                    twitter_link = socialMediaTokens.getTwitterToken().getTwitterPageLink();
                if ( socialMediaTokens.getLinkedInToken() != null )
                    linkedin_link = socialMediaTokens.getLinkedInToken().getLinkedInPageLink();
                if ( socialMediaTokens.getGoogleToken() != null )
                    google_plus_link = socialMediaTokens.getGoogleToken().getProfileLink();
                if ( socialMediaTokens.getGoogleBusinessToken() != null )
                    google_review_link = socialMediaTokens.getGoogleBusinessToken().getGoogleBusinessLink();
                if ( socialMediaTokens.getZillowToken() != null )
                    zillow_link = socialMediaTokens.getZillowToken().getZillowProfileLink();
                if ( socialMediaTokens.getLendingTreeToken() != null )
                    lending_tree_link = socialMediaTokens.getLendingTreeToken().getLendingTreeProfileLink();
                if ( socialMediaTokens.getRealtorToken() != null )
                    realtor_com_link = socialMediaTokens.getRealtorToken().getRealtorProfileLink();
                if ( socialMediaTokens.getYelpToken() != null )
                    yelp_link = socialMediaTokens.getYelpToken().getYelpPageLink();
            }

            socialMediaTokens = companySettings.getSocialMediaTokens();
            //Set company values
            if ( socialMediaTokens != null ) {
                if ( socialMediaTokens.getFacebookToken() != null )
                    company_facebook_link = socialMediaTokens.getFacebookToken().getFacebookPageLink();
                if ( socialMediaTokens.getTwitterToken() != null )
                    company_twitter_link = socialMediaTokens.getTwitterToken().getTwitterPageLink();
                if ( socialMediaTokens.getLinkedInToken() != null )
                    company_linkedin_link = socialMediaTokens.getLinkedInToken().getLinkedInPageLink();
                if ( socialMediaTokens.getGoogleToken() != null )
                    company_google_plus_link = socialMediaTokens.getGoogleToken().getProfileLink();
                if ( socialMediaTokens.getGoogleBusinessToken() != null )
                    company_google_review_link = socialMediaTokens.getGoogleBusinessToken().getGoogleBusinessLink();
                if ( socialMediaTokens.getZillowToken() != null )
                    company_zillow_link = socialMediaTokens.getZillowToken().getZillowProfileLink();
                if ( socialMediaTokens.getLendingTreeToken() != null )
                    company_lending_tree_link = socialMediaTokens.getLendingTreeToken().getLendingTreeProfileLink();
                if ( socialMediaTokens.getRealtorToken() != null )
                    company_realtor_com_link = socialMediaTokens.getRealtorToken().getRealtorProfileLink();
                if ( socialMediaTokens.getYelpToken() != null )
                    company_yelp_link = socialMediaTokens.getYelpToken().getYelpPageLink();
            }
        } catch ( NoRecordsFetchedException e ) {
            LOG.error( "No user found with email address : " + senderEmail );
        }

        content = StringUtils.replace( content, "[facebook_link]", processUrl( facebook_link ) );
        content = StringUtils.replace( content, "[twitter_link]", processUrl( twitter_link ) );
        content = StringUtils.replace( content, "[linkedin_link]", processUrl( linkedin_link ) );
        content = StringUtils.replace( content, "[google_plus_link]", processUrl( google_plus_link ) );
        content = StringUtils.replace( content, "[google_review_link]", processUrl( google_review_link ) );
        content = StringUtils.replace( content, "[zillow_link]", processUrl( zillow_link ) );
        content = StringUtils.replace( content, "[lending_tree_link]", processUrl( lending_tree_link ) );
        content = StringUtils.replace( content, "[realtor_com_link]", processUrl( realtor_com_link ) );
        content = StringUtils.replace( content, "[yelp_link]", processUrl( yelp_link ) );
        content = StringUtils.replace( content, "[company_facebook_link]", processUrl( company_facebook_link ) );
        content = StringUtils.replace( content, "[company_twitter_link]", processUrl( company_twitter_link ) );
        content = StringUtils.replace( content, "[company_linkedin_link]", processUrl( company_linkedin_link ) );
        content = StringUtils.replace( content, "[company_google_plus_link]", processUrl( company_google_plus_link ) );
        content = StringUtils.replace( content, "[company_google_review_link]", processUrl( company_google_review_link ) );
        content = StringUtils.replace( content, "[company_zillow_link]", processUrl( company_zillow_link ) );
        content = StringUtils.replace( content, "[company_lending_tree_link]", processUrl( company_lending_tree_link ) );
        content = StringUtils.replace( content, "[company_realtor_com_link]", processUrl( company_realtor_com_link ) );
        content = StringUtils.replace( content, "[company_yelp_link]", processUrl( company_yelp_link ) );

        content = StringUtils.replace( content, "[survey_source_id]",
            surveyMap.get( CommonConstants.SURVEY_SOURCE_ID_COLUMN ) );
        content = StringUtils.replace( content, "[survey_source]", surveyMap.get( CommonConstants.SURVEY_SOURCE_COLUMN ) );
        //JIRA SS-1504
        content = StringUtils.replace( content, "[property_address]", surveyMap.get( CommonConstants.PROPERTY_ADDRESS ) );
        
        content = StringUtils.replace( content, "[loan_processor_name]", surveyMap.get( CommonConstants.LOAN_PROCESSOR_NAME ) );

        //JIRA SS-626 end
        content = StringUtils.replace( content, "null", "" );
        LOG.info( "Method to replace legends with values called, replaceLegends() ended" );
        return content;
    }


    String processUrl( String url )
    {
        if ( url == null )
            return "";
        return url;
    }


    @Transactional
    public Map<String, String> fetchSurveySourceId( long agentId, String customerEmailAddress, String dateStr )
        throws InvalidInputException
    {
        LOG.info( "Method fetchSurveySourceId started for agentId : " + agentId + " and customer : " + customerEmailAddress );
        if ( agentId <= 0 ) {
            throw new InvalidInputException( "Invalid Agent ID : " + agentId );
        }
        if ( customerEmailAddress == null || customerEmailAddress.isEmpty() )
            throw new InvalidInputException( "Customer Email Address cannot be empty" );
        if ( dateStr == null || dateStr.isEmpty() )
            throw new InvalidInputException( "initiated dateStr cannot be empty" );
        Map<String, String> surveyMap = new HashMap<>();
        DateFormat dateFormat = new SimpleDateFormat( "yyyy/MM/dd" );
        Date date = null;
        try {
            date = dateFormat.parse( dateStr );
        } catch ( ParseException e ) {
            throw new InvalidInputException( "A Date parse exception occurred. Reason : ", e );
        }
        String surveySourceId = "";
        String surveySource = "";
        String propertyAddress = "";
        String loanProcessorName = "";
        List<SurveyPreInitiation> surveyList = surveyPreInitiationDao.getValidSurveyByAgentIdAndCustomeEmail( agentId,
            customerEmailAddress );
        if ( surveyList.isEmpty() ) {
            throw new InvalidInputException( "No survey found!" );
        }
        long oneDay = 1000 * 60 * 60 * 24;
        if ( surveyList.size() > 1 ) {
            for ( SurveyPreInitiation survey : surveyList ) {
                if ( Math.abs( survey.getCreatedOn().getTime() - date.getTime() ) <= oneDay ) {
                    surveySourceId = fetchSurveySourceId( survey );
                    if ( survey.getSurveySource() != null )
                        surveySource = survey.getSurveySource();
                    if(survey.getPropertyAddress() != null)
                        propertyAddress = survey.getPropertyAddress();
                    if(survey.getLoanProcessorName() != null)
                        loanProcessorName = survey.getLoanProcessorName();
                    break;
                }
            }
        } else {
            SurveyPreInitiation survey = surveyList.get( 0 );
            surveySourceId = fetchSurveySourceId( survey );
            if ( survey.getSurveySource() != null )
                surveySource = survey.getSurveySource();
            if(survey.getPropertyAddress() != null)
                propertyAddress = survey.getPropertyAddress();
            if(survey.getLoanProcessorName() != null)
                loanProcessorName = survey.getLoanProcessorName();
        }
        LOG.info( "Method fetchSurveySourceId finished for agentId : " + agentId + " and customer : " + customerEmailAddress );
        surveyMap.put( CommonConstants.SURVEY_SOURCE_ID_COLUMN,
            !StringUtils.isEmpty( surveySourceId ) ? surveySourceId : "--" );
        surveyMap.put( CommonConstants.SURVEY_SOURCE_COLUMN, surveySource );
        surveyMap.put( CommonConstants.PROPERTY_ADDRESS, propertyAddress );
        surveyMap.put( CommonConstants.LOAN_PROCESSOR_NAME, loanProcessorName );
        return surveyMap;
    }


    //@Transactional
    String fetchSurveySourceId( SurveyPreInitiation survey ) throws InvalidInputException
    {
        LOG.info( "Method fetchSurveySourceId started." );
        if ( survey == null ) {
            throw new InvalidInputException( "Survey cannot be null" );
        }
        String surveySourceId = "";
        if ( survey.getSurveySourceId() != null )
            surveySourceId = survey.getSurveySourceId();
        LOG.info( "Method fetchSurveySourceId finished." );
        return surveySourceId;
    }


    /**
     * 
     * @param adminName
     * @param adminEmailId
     * @param user
     * @param agentSettings
     * @return
     * @throws InvalidInputException 
     */
    public String buildAgentAdditionOrDeletionMessage( String adminName, String adminEmailId, User user,
        OrganizationUnitSettings agentSettings, boolean isUserAdded ) throws InvalidInputException
    {
        LOG.debug( "method buildAgentAdditionOrDeletionMessage() started" );
        StringBuilder message = new StringBuilder();

        if ( user == null || agentSettings == null ) {
            LOG.warn( "No user data specified" );
            throw new InvalidInputException( "No user data specified" );
        }

        message.append( "<p>" );
        message.append( isUserAdded ? "A new user has been added " : "A user has been deleted " );
        if ( StringUtils.isNotEmpty( adminName ) || StringUtils.isNotEmpty( adminEmailId ) ) {
            message.append( "by " );
            if ( StringUtils.isNotEmpty( adminName ) ) {
                message.append( adminName ).append( ", " );
            }
            if ( StringUtils.isNotEmpty( adminEmailId ) ) {
                message.append( "[" ).append( adminEmailId ).append( "] " );
            }
        }
        message.append( "at " );
        message.append( utils.convertDateToTimeZone( new Date().getTime(), CommonConstants.TIMEZONE_EST ) ).append( ". " );
        message.append( "Please find the details below." );
        message.append( "</p>" );

        message.append( buildAgentDetailBulletList(
            user.getFirstName() + ( StringUtils.isEmpty( user.getLastName() ) ? "" : " " + user.getLastName() ),
            user.getEmailId(), agentSettings.getCompleteProfileUrl() ) );

        LOG.debug( "method buildAgentAdditionOrDeletionMessage() finished" );
        return message.toString();
    }


    private String buildAgentDetailBulletList( String agentName, String agentEmail, String publicProfileUrl )
    {
        return wrapUnorderedListTag(
            wrapListElementTag( "Name for User : " + agentName ) + wrapListElementTag( "Email : " + agentEmail )
                + ( "Public Profile Link : " + ( StringUtils.isEmpty( publicProfileUrl ) ? ""
                    : wrapListElementTag( wrapHrefForLink( publicProfileUrl ) ) ) ) );
    }


    private String wrapUnorderedListTag( String data )
    {
        return "<ul>" + data + "</ul>";
    }


    private String wrapListElementTag( String data )
    {
        return "<li>" + data + "</li>";
    }


    private String wrapHrefForLink( String link )
    {
        return "<a href=" + link + ">" + link + "</a>";
    }
}