package com.realtech.socialsurvey.core.dao.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.mongodb.WriteResult;
import com.realtech.socialsurvey.core.entities.*;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.NestableRuntimeException;
import org.hibernate.criterion.Restrictions;
import org.hibernate.ejb.criteria.expression.BinaryArithmeticOperation.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObject;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;

import com.realtech.socialsurvey.core.entities.AgentRankingReport;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.FeedIngestionEntity;
import com.realtech.socialsurvey.core.entities.LOSearchEngine;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.ProfileImageUrlData;
import com.realtech.socialsurvey.core.entities.ProfileUrlEntity;
import com.realtech.socialsurvey.core.entities.SavedDigestRecord;
import com.realtech.socialsurvey.core.entities.SocialMediaTokenResponse;
import com.realtech.socialsurvey.core.entities.SocialMediaTokens;
import com.realtech.socialsurvey.core.entities.SurveyStats;
import com.realtech.socialsurvey.core.entities.TransactionSourceFtp;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.vo.AddressGeoLocationVO;
import com.realtech.socialsurvey.core.vo.AdvancedSearchVO;


/**
 * Mongo implementation of settings
 */
@Repository
public class MongoOrganizationUnitSettingDaoImpl implements OrganizationUnitSettingsDao, InitializingBean
{

    public static final String COMPANY_SETTINGS_COLLECTION = "COMPANY_SETTINGS";
    public static final String REGION_SETTINGS_COLLECTION = "REGION_SETTINGS";
    public static final String BRANCH_SETTINGS_COLLECTION = "BRANCH_SETTINGS";
    public static final String AGENT_SETTINGS_COLLECTION = "AGENT_SETTINGS";
    public static final String KEY_CRM_INFO = "crm_info";
    public static final String KEY_CRM_INFO_SOURCE = "crm_info.crm_source";
    public static final String KEY_IDEN = "iden";
    public static final String KEY_COMPANY_ID = "companyId";
    public static final String KEY_CRM_INFO_CLASS = "crm_info._class";
    public static final String KEY_MAIL_CONTENT = "mail_content";
    public static final String KEY_SURVEY_SETTINGS = "survey_settings";
    public static final String KEY_LOCATION_ENABLED = "isLocationEnabled";
    public static final String KEY_ACCOUNT_DISABLED = "isAccountDisabled";
    public static final String KEY_DEFAULT_BY_SYSTEM = "isDefaultBySystem";
    public static final String KEY_SEO_CONTENT_MODIFIED = "isSeoContentModified";
    public static final String KEY_VENDASTA_ACCESS = "vendastaAccessible";
    public static final String KEY_CONTACT_DETAIL_SETTINGS = "contact_details";
    public static final String KEY_LOCK_SETTINGS = "lockSettings";
    public static final String KEY_LINKEDIN_PROFILEDATA = "linkedInProfileData";
    public static final String KEY_PROFILE_NAME = "profileName";
    public static final String KEY_UNIQUE_IDENTIFIER = "uniqueIdentifier";
    public static final String KEY_PROFILE_URL = "profileUrl";
    public static final String KEY_LOGO = "logo";
    public static final String KEY_LOGO_THUMBNAIL = "logoThumbnail";
    public static final String KEY_LOGO_PROCESSED = "isLogoImageProcessed";
    public static final String KEY_PROFILE_IMAGE = "profileImageUrl";
    public static final String KEY_PROFILE_IMAGE_THUMBNAIL = "profileImageUrlThumbnail";
    public static final String KEY_CONTACT_DETAILS = "contact_details";
    public static final String KEY_ASSOCIATION = "associations";
    public static final String KEY_EXPERTISE = "expertise";
    public static final String KEY_HOBBIES = "hobbies";
    public static final String KEY_ACHIEVEMENTS = "achievements";
    public static final String KEY_LICENCES = "licenses";
    public static final String KEY_SOCIAL_MEDIA_TOKENS = "socialMediaTokens";
    public static final String KEY_FACEBOOK_TOKEN_TO_POST = "facebookAccessTokenToPost";
    public static final String KEY_COMPANY_POSITIONS = "positions";
    public static final String KEY_IDENTIFIER = "iden";
    public static final String KEY_HIDDEN_SECTION = "hiddenSection";
    public static final String KEY_VERTICAL = "vertical";
    public static final String KEY_PROFILE_STAGES = "profileStages";
    public static final String KEY_MODIFIED_ON = "modifiedOn";
    public static final String KEY_MODIFIED_BY = "modifiedBy";
    public static final String KEY_CREATED_ON = "createdOn";
    public static final String KEY_CREATED_BY = "createdBy";
    public static final String KEY_DISCLAIMER = "disclaimer";
    public static final String KEY_FACEBOOK_SOCIAL_MEDIA_TOKEN = "socialMediaTokens.facebookToken";
    public static final String KEY_TWITTER_SOCIAL_MEDIA_TOKEN = "socialMediaTokens.twitterToken";
    public static final String KEY_GOOGLE_SOCIAL_MEDIA_TOKEN = "socialMediaTokens.googleToken";
    public static final String KEY_LINKEDIN_SOCIAL_MEDIA_TOKEN = "socialMediaTokens.linkedInToken";
    public static final String KEY_ZILLOW_SOCIAL_MEDIA_TOKEN = "socialMediaTokens.zillowToken";
    public static final String KEY_YELP_SOCIAL_MEDIA_TOKEN = "socialMediaTokens.yelpToken";
    public static final String KEY_LENDINGTREE_SOCIAL_MEDIA_TOKEN = "socialMediaTokens.lendingTreeToken";
    public static final String KEY_REALTOR_SOCIAL_MEDIA_TOKEN = "socialMediaTokens.realtorToken";
    public static final String KEY_GOOGLE_BUSINESS_SOCIAL_MEDIA_TOKEN = "socialMediaTokens.googleBusinessToken";
    public static final String KEY_INSTAGRAM_SOCIAL_MEDIA_TOKEN = "socialMediaTokens.instagramToken";
    public static final String KEY_FACEBOOK_PIXEL_SOCIAL_MEDIA_TOKEN = "socialMediaTokens.facebookPixelToken";
    public static final String KEY_CONTACT_NAME = "contact_details.name";
    public static final String KEY_POSTIONS = "positions";
    public static final String KEY_STATUS = "status";
    public static final String KEY_USER_ENCRYPTED_ID = "userEncryptedId";
    public static final String KEY_ENCRYPTED_ID = "encryptedId";
    public static final String KEY_VENDASTA_RM_SETTINGS = "vendasta_rm_settings";
    public static final String KEY_REVIEW_SORT_CRITERIA = "reviewSortCriteria";
    public static final String KEY_SEND_EMAIL_THROUGH = "sendEmailThrough";
    public static final String KEY_RANKING_REQUIREMENTS = "ranking_requirements";
    public static final String KEY_ALLOW_PARTNER_SURVEY = "allowPartnerSurvey";
    public static final String KEY_SEND_MONTHLY_DIGEST_MAIL = "sendMonthlyDigestMail";
    public static final String KEY_HIDE_PUBLIC_PAGE = "hidePublicPage";
    public static final String KEY_INCLUDE_FOR_TRANSACTION_MONITOR = "includeForTransactionMonitor";
    public static final String KEY_FILTER_KEYWORDS = "filterKeywords";
    public static final String PROFILE_IMAGE_URL = "profileImageUrl";
    public static final String KEY_TRUSTED_SOURCES = "socialMonitorTrustedSources";
    
    public static final String KEY_IS_LOGIN_PREVENTED = "isLoginPrevented";
    public static final String KEY_IS_COPY_TO_CLIPBOARD = "isCopyToClipboard";
    public static final String KEY_SEND_EMAIL_FROM_COMPANY = "sendEmailFromCompany";
    public static final String KEY_HIDE_FROM_BREAD_CRUMB = "hideFromBreadCrumb";
    public static final String KEY_ALLOW_OVERRIDE_FOR_SOCIAL_MEDIA = "allowOverrideForSocialMedia";

    public static final String KEY_DIGEST_RECIPIENTS = "digestRecipients";
    public static final String KEY_ENTITY_ALERT_DETAILS = "entityAlertDetails";
    public static final String KEY_IS_ERROR_ALERT = "isErrorAlert";
    public static final String KEY_IS_WARNING_ALERT_ = "isWarningAlert";

    public static final String KEY_ABUSIVE_EMAIL_SETTING = "survey_settings.abusive_mail_settings";
    public static final String KEY_COMPLAINT_RESOLUTION_SETTING = "survey_settings.complaint_res_settings";

    public static final String KEY_SAVED_DIGEST_RECORD = "savedDigestRecords";
    public static final String KEY_SAVED_DIGEST_RECORD_DATE = "savedDigestRecords.uploadedDate";
    public static final String KEY_SAVED_DIGEST_RECORD_MONTH = "savedDigestRecords.month";
    public static final String KEY_SAVED_DIGEST_RECORD_YEAR = "savedDigestRecords.year";


    public static final String KEY_USER_ADD_DELETE_NOTIFICATION_RECIPIENTS = "userAddDeleteNotificationRecipients";
    
    public static final String KEY_CONTACT_DETAILS_WEB_ADD_WORK = "contact_details.web_addresses.work";
    
    public static final String KEY_AGENT_PROFILE_DISABLED = "isAgentProfileDisabled";
    
    public static final String KEY_SWEAR_WORDS = "swearWords";
    
    public static final String KEY_FACEBOOK_ID = "socialMediaTokens.facebookToken.facebookId";
    public static final String KEY_FACEBOOK_PAGE_LINK = "socialMediaTokens.facebookToken.facebookPageLink";
    public static final String KEY_FACEBOOK_ACCESS_TOKEN = "socialMediaTokens.facebookToken.facebookAccessToken";
    public static final String KEY_FACEBOOK_ACCESS_TOKEN_TO_POST = "socialMediaTokens.facebookToken.facebookAccessTokenToPost";
    
    public static final String KEY_TWITTER_ID = "socialMediaTokens.twitterToken.twitterId";
    public static final String KEY_TWITTER_PAGE_LINK = "socialMediaTokens.twitterToken.twitterPageLink";
    public static final String KEY_TWITTER_ACCESS_TOKEN = "socialMediaTokens.twitterToken.twitterAccessToken";
    public static final String KEY_TWITTER_ACCESS_TOKEN_SECRET = "socialMediaTokens.twitterToken.twitterAccessTokenSecret";
    
    public static final String KEY_LINKEDIN_ID = "socialMediaTokens.linkedInToken.linkedInId";
    public static final String KEY_LINKEDIN_PAGE_LINK = "socialMediaTokens.linkedInToken.linkedInPageLink";
    public static final String KEY_LINKEDIN_ACCESS_TOKEN = "socialMediaTokens.linkedInToken.linkedInAccessToken";
    
    public static final String SOCIAL_MONITOR_ENABLED = "isSocialMonitorEnabled";
    public static final String HAS_REGISTERED_FOR_SUMMIT = "hasRegisteredForSummit";
    public static final String IS_SHOW_SUMMIT_POPUP = "isShowSummitPopup";
    
    public static final String KEY_FTP_INFO = "transactionSourceFtpList";

    //key to get the transactionSourceFtp feilds
    public static final String KEY_TRANSACTION_SOURCE_FTP = "transactionSourceFtpList";
    public static final String KEY_TRANSACTION_SOURCE_FTP_ID = "ftpId";
    public static final String KEY_TRANSACTION_SOURCE_FTP_STATUS = "status";
    public static final String KEY_TRANSACTION_SOURCE_FTP_DOLLAR = "transactionSourceFtpList.$";
    public static final String KEY_TRANSACTION_SOURCE_FTP_EMAIL = "emailId";
    
    //address details in contact detail required for update
    public static final String KEY_CONTACT_DETAILS_ADDRESS = "contact_details.address";
    public static final String KEY_CONTACT_DETAILS_ADDRESS1 = "contact_details.address1";
    public static final String KEY_CONTACT_DETAILS_ADDRESS2 = "contact_details.address2";
    public static final String KEY_CONTACT_DETAILS_COUNTRY = "contact_details.country";
    public static final String KEY_CONTACT_DETAILS_STATE = "contact_details.state";
    public static final String KEY_CONTACT_DETAILS_CITY = "contact_details.city";
    public static final String KEY_CONTACT_DETAILS_COUNTRY_CODE = "contact_details.countryCode";
    public static final String KEY_CONTACT_DETAILS_ZIP_CODE = "contact_details.zipcode";
    public static final String KEY_CONTACT_DETAILS_UPDATED_BY_SYSTEM = "contact_details.updatedBySystem";
    public static final String KEY_CONTACT_DETAILS_NAME = "contact_details.name";
    
    
    public static final String KEY_LOCATION = "geoLocation";
    public static final String KEY_DISTANCE_FIELD = "distanceField";
    public static final String KEY_SURVEY_STATS = "surveyStats";
    
    public static final String APPLICATION_SETTINGS_COLLECTION = "APPLICATION_SETTINGS";
    
    //to search by survey stats
    public static final String KEY_SURVEY_STATS_AVG_SCORE = "surveyStats.avgScore";
    public static final String KEY_SURVEY_STATS_SURVEY_COUNT = "surveyStats.surveyCount";
    public static final String KEY_SURVEY_STATS_SEARCH_RANKING_SCORE = "surveyStats.searchRankingScore";
    
    

    public static final String KEY_SOCIAL_MEDIA_LASTFETCHED = "socialMediaLastFetched";
    public static final String KEY_FBREVIEW_LASTFETCHED = "socialMediaLastFetched.fbReviewLastFetched";
    public static final String KEY_FBREVIEW_LASTFETCHED_CURRENT = "socialMediaLastFetched.fbReviewLastFetched.current";
    public static final String KEY_GOOGLE_REVIEW_LASTFETCHED = "socialMediaLastFetched.googleReviewLastFetched";
    public static final String KEY_GOOGLE_REVIEW_LAST_FETCHED_CURRENT = "socialMediaLastFetched.googleReviewLastFetched.current";

    @Value ( "${CDN_PATH}")
    private String amazonEndPoint;

    private static final Logger LOG = LoggerFactory.getLogger( MongoOrganizationUnitSettingDaoImpl.class );


    @Autowired
    private MongoTemplate mongoTemplate;

    @Value ( "${APPLICATION_BASE_URL}")
    private String applicationBaseUrl;


    @Override
    public void insertOrganizationUnitSettings( OrganizationUnitSettings organizationUnitSettings, String collectionName )
    {
        LOG.debug( "Creating " + collectionName + " document. Organiztion Unit id: " + organizationUnitSettings.getIden() );
        LOG.debug( "Inserting into " + collectionName + ". Object: " + organizationUnitSettings.toString() );
        mongoTemplate.insert( organizationUnitSettings, collectionName );
        LOG.debug( "Inserted into " + collectionName );
    }


    @Override
    public void insertAgentSettings( AgentSettings agentSettings )
    {
        LOG.debug( "Inserting agent settings: " + agentSettings.toString() );
        mongoTemplate.insert( agentSettings, AGENT_SETTINGS_COLLECTION );
        LOG.debug( "Inserted into agent settings" );
    }


    @Override
    public OrganizationUnitSettings fetchOrganizationUnitSettingsById( long identifier, String collectionName )
    {
        LOG.debug( "Fetch organization unit settings from {} for id: {}", collectionName, identifier );
        Query query = new Query();
        query.addCriteria( Criteria.where( KEY_IDENTIFIER ).is( identifier ) );
        query.fields().exclude( KEY_LINKEDIN_PROFILEDATA );	
        OrganizationUnitSettings settings = mongoTemplate.findOne( query, OrganizationUnitSettings.class, collectionName );
        setCompleteUrlForSettings( settings, collectionName );
        return settings;
    }


    @Override
    public List<OrganizationUnitSettings> fetchOrganizationUnitSettingsForMultipleIds( Set<Long> identifiers,
        String collectionName )
    {
        LOG.debug( "Fetch organization unit settings from " + collectionName + " for multiple ids." );
        Query query = new Query();
        query.addCriteria( Criteria.where( KEY_IDENTIFIER ).in( identifiers ) );
        query.fields().exclude( KEY_LINKEDIN_PROFILEDATA );
        List<OrganizationUnitSettings> settingsList = mongoTemplate.find( query, OrganizationUnitSettings.class,
            collectionName );
        for ( OrganizationUnitSettings settings : settingsList ) {
            setCompleteUrlForSettings( settings, collectionName );
        }
        return settingsList;
    }


    @Override
    public AgentSettings fetchAgentSettingsById( long identifier )
    {
        LOG.debug( "Fetch agent settings from Mongo for id: {}", identifier );
        AgentSettings settings = mongoTemplate.findOne( new BasicQuery( new BasicDBObject( KEY_IDENTIFIER, identifier ) ),
            AgentSettings.class, AGENT_SETTINGS_COLLECTION );
        setCompleteUrlForSettings( settings, CommonConstants.AGENT_SETTINGS_COLLECTION );
        return settings;
    }


    @Override
    public ContactDetailsSettings fetchAgentContactDetailByEncryptedId( String userEncryptedId )
    {
        LOG.debug( "Fetch agent settings from for userEncruptedId: " + userEncryptedId );
        Query query = new Query();
        query.addCriteria( Criteria.where( KEY_USER_ENCRYPTED_ID ).is( userEncryptedId ) );
        query.fields().include( KEY_CONTACT_DETAILS );
        AgentSettings settings = mongoTemplate.findOne( query, AgentSettings.class, AGENT_SETTINGS_COLLECTION );
        ContactDetailsSettings contactDetails = null;
        if ( settings != null )
            contactDetails = settings.getContact_details();
        return contactDetails;
    }


    @Override
    public List<AgentSettings> fetchMultipleAgentSettingsById( List<Long> identifiers )
    {
        LOG.debug( "Fetch multiple agent settings from list of Ids: " + identifiers );
        Query query = new Query();
        query.addCriteria( Criteria.where( KEY_IDENTIFIER ).in( identifiers ) );
        query.fields().exclude( KEY_LINKEDIN_PROFILEDATA );
        List<AgentSettings> settingsList = mongoTemplate.find( query, AgentSettings.class, AGENT_SETTINGS_COLLECTION );
        for ( AgentSettings settings : settingsList ) {
            setCompleteUrlForSettings( settings, CommonConstants.AGENT_SETTINGS_COLLECTION );
        }
        return settingsList;
    }


    @Override
    public List<AgentSettings> getAllAgentSettings()
    {
        LOG.debug( "Fetch multiple getAllAgentSettings: " );
        Query query = new Query();
        query.fields().exclude( KEY_LINKEDIN_PROFILEDATA );
        List<AgentSettings> settingsList = mongoTemplate.find( query, AgentSettings.class, AGENT_SETTINGS_COLLECTION );
        for ( AgentSettings settings : settingsList ) {
            setCompleteUrlForSettings( settings, CommonConstants.AGENT_SETTINGS_COLLECTION );
        }
        return settingsList;
    }


    @Override
    public void updateParticularKeyOrganizationUnitSettings( String keyToUpdate, Object updatedRecord,
        OrganizationUnitSettings unitSettings, String collectionName )
    {
        LOG.debug( "Updating unit setting in " + collectionName + " with " + unitSettings + " for key: " + keyToUpdate
            + " wtih value: " + updatedRecord );
        Query query = new Query();
        query.addCriteria( Criteria.where( "_id" ).is( unitSettings.getId() ) );
        Update update = new Update();
        update.set( keyToUpdate, updatedRecord );
        update.set( CommonConstants.MODIFIED_ON_COLUMN, System.currentTimeMillis() );
        LOG.debug( "Updating the unit settings" );
        mongoTemplate.updateFirst( query, update, OrganizationUnitSettings.class, collectionName );
        LOG.debug( "Updated the unit setting" );
    }


    // THIS METHOD TO BE USED WHERE COLLECTION HAS iden FIELD
    @Override
    public void updateParticularKeyOrganizationUnitSettingsByIden( String keyToUpdate, Object updatedRecord, long iden,
        String collectionName )
    {
        LOG.debug( "Updating unit setting in " + collectionName + " with identifier " + iden + " for key: " + keyToUpdate
            + " wtih value: " + updatedRecord );
        Query query = new Query();
        query.addCriteria( Criteria.where( KEY_IDEN ).is( iden ) );
        Update update = new Update().set( keyToUpdate, updatedRecord );
        LOG.debug( "Updating the unit settings" );
        mongoTemplate.updateFirst( query, update, OrganizationUnitSettings.class, collectionName );
        LOG.debug( "Updated the unit setting" );
    }


    @Override
    public void updateParticularKeyAgentSettings( String keyToUpdate, Object updatedRecord, AgentSettings agentSettings )
    {
        LOG.debug( "Updating unit setting in AGENT_SETTINGS with " + agentSettings + " for key: " + keyToUpdate
            + " wtih value: " + updatedRecord );
        Query query = new Query();
        query.addCriteria( Criteria.where( "_id" ).is( agentSettings.getId() ) );
        Update update = new Update().set( keyToUpdate, updatedRecord );
        LOG.debug( "Updating the unit settings" );
        mongoTemplate.updateFirst( query, update, OrganizationUnitSettings.class, AGENT_SETTINGS_COLLECTION );
        LOG.debug( "Updated the unit setting" );
    }


    @Override
    public void updateIsLoginPreventedForUsersInMongo( List<Long> userIdList, boolean isLoginPrevented )
    {
        LOG.debug( "updating IsLoginPreventedForUsers in Mongo" );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.IDEN ).in( userIdList ) );
        Update update = new Update().set( MongoOrganizationUnitSettingDaoImpl.KEY_IS_LOGIN_PREVENTED, isLoginPrevented );
        mongoTemplate.updateMulti( query, update, OrganizationUnitSettings.class, AGENT_SETTINGS_COLLECTION );
    }


    @Override
    public void updateHidePublicPageForUsers( List<Long> userIdList, boolean hidePublicPage )
    {
        LOG.debug( "updating IsLoginPreventedForUsers in Mongo" );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.IDEN ).in( userIdList ) );
        Update update = new Update().set( MongoOrganizationUnitSettingDaoImpl.KEY_HIDE_PUBLIC_PAGE, hidePublicPage );
        mongoTemplate.updateMulti( query, update, OrganizationUnitSettings.class, AGENT_SETTINGS_COLLECTION );
    }
    
    /**
     * Enable/Disable the social media tokens for the given list of userIds
     */
    @Override
    public void updateSocialMediaForUsers ( List<Long> userIdList, boolean disableSocialMediaTokens )
    {
        LOG.debug( "updating enableSocialMediaForUsers in Mongo" );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.IDEN ).in( userIdList ) );
        Update update = new Update();
        
        if(disableSocialMediaTokens){
            update.rename( KEY_SOCIAL_MEDIA_TOKENS, CommonConstants.DELETED_SOCIAL_MEDIA_TOKENS_COLUMN );
        }
        else {
            update.rename( CommonConstants.DELETED_SOCIAL_MEDIA_TOKENS_COLUMN, KEY_SOCIAL_MEDIA_TOKENS );
        }
        
        mongoTemplate.updateMulti( query, update, OrganizationUnitSettings.class, AGENT_SETTINGS_COLLECTION );
    }


    /**
     * Fetchs the list of names of logos being used.
     * 
     * @return
     */
    @Override
    public List<String> fetchLogoList()
    {
        LOG.debug( "Fetching the list of logos being used" );
        List<OrganizationUnitSettings> settingsList = mongoTemplate.findAll( OrganizationUnitSettings.class,
            COMPANY_SETTINGS_COLLECTION );
        List<String> logoList = new ArrayList<>();

        LOG.debug( "Preparing the list of logo names" );
        for ( OrganizationUnitSettings settings : settingsList ) {
            String logoName = settings.getLogoThumbnail();
            if ( logoName != null && !logoName.isEmpty() ) {
                logoList.add( logoName );
            }
        }
        LOG.debug( "Returning the list prepared!" );
        return logoList;
    }


    /**
     * Updates a particular key of organization unit settings based on criteria specified
     */
    @Override
    public void updateKeyOrganizationUnitSettingsByCriteria( String keyToUpdate, Object updatedRecord, String criteriaKey,
        Object criteriaValue, String collectionName )
    {
        LOG.debug( "Method updateKeyOrganizationUnitSettingsByCriteria called in collection name :" + collectionName
            + " for keyToUpdate :" + keyToUpdate + " criteria key :" + criteriaKey );
        Query query = new Query();
        query.addCriteria( Criteria.where( criteriaKey ).is( criteriaValue ) );
        Update update = new Update().set( keyToUpdate, updatedRecord );
        LOG.debug( "Updating unit settings based on criteria" );
        mongoTemplate.updateMulti( query, update, OrganizationUnitSettings.class, collectionName );
        LOG.debug( "Successfully completed updation of unit settings" );
    }


    /**
     * Updates a particular key of organization unit settings based on criteria specified
     */
    @Override
    public void updateKeyOrganizationUnitSettingsByInCriteria( String keyToUpdate, Object updatedRecord, String criteriaKey,
        List<Object> criteriaValue, String collectionName )
    {
        LOG.debug( "Method updateKeyOrganizationUnitSettingsByInCriteria called in collection name :" + collectionName
            + " for keyToUpdate :" + keyToUpdate + " criteria key :" + criteriaKey );
        Query query = new Query();
        query.addCriteria( Criteria.where( criteriaKey ).in( criteriaValue ) );
        Update update = new Update().set( keyToUpdate, updatedRecord );
        LOG.debug( "Updating unit settings based on in criteria" );
        mongoTemplate.updateMulti( query, update, OrganizationUnitSettings.class, collectionName );
        LOG.debug( "Successfully completed updation of unit settings" );
    }


    /**
     * Method to fetch organization settings based on profile name
     */
    @Override
    public OrganizationUnitSettings fetchOrganizationUnitSettingsByProfileName( String profileName, String collectionName )
    {
        LOG.debug( "Method fetchOrganizationUnitSettingsByProfileName called for profileName:" + profileName
            + " and collectionName:" + collectionName );

        Query query = new Query();
        query.addCriteria( Criteria.where( KEY_PROFILE_NAME ).is( profileName ) );
        query.fields().exclude( KEY_LINKEDIN_PROFILEDATA );
        OrganizationUnitSettings organizationUnitSettings = mongoTemplate.findOne( query, OrganizationUnitSettings.class,
            collectionName );
        setCompleteUrlForSettings( organizationUnitSettings, collectionName );
        LOG.debug( "Successfully executed method fetchOrganizationUnitSettingsByProfileName" );
        return organizationUnitSettings;
    }


    /**
     * Method to fetch organization settings based on profile url
     */
    @Override
    public OrganizationUnitSettings fetchOrganizationUnitSettingsByProfileUrl( String profileUrl, String collectionName )
    {
        LOG.debug( "Method fetchOrganizationUnitSettingsByProfileUrl called for profileUrl:" + profileUrl
            + " and collectionName:" + collectionName );

        OrganizationUnitSettings organizationUnitSettings = mongoTemplate.findOne(
            new BasicQuery( new BasicDBObject( KEY_PROFILE_URL, profileUrl ) ), OrganizationUnitSettings.class,
            collectionName );
        setCompleteUrlForSettings( organizationUnitSettings, collectionName );
        LOG.debug( "Successfully executed method fetchOrganizationUnitSettingsByProfileUrl" );
        return organizationUnitSettings;
    }


    // creates index on field 'iden'
    private void createIndexOnIden( String collectionName )
    {
        LOG.debug( "Creating unique index on 'iden' for " + collectionName );
        mongoTemplate.indexOps( collectionName ).ensureIndex( new Index().on( KEY_IDENTIFIER, Sort.Direction.ASC ).unique() );
        LOG.debug( "Index created" );
    }


    @Override
    public void afterPropertiesSet() throws Exception
    {
        LOG.debug( "Checking if collections are created in mongodb" );
        if ( !mongoTemplate.collectionExists( COMPANY_SETTINGS_COLLECTION ) ) {
            LOG.debug( "Creating " + COMPANY_SETTINGS_COLLECTION );
            mongoTemplate.createCollection( COMPANY_SETTINGS_COLLECTION );
            createIndexOnIden( COMPANY_SETTINGS_COLLECTION );
        }
        if ( !mongoTemplate.collectionExists( REGION_SETTINGS_COLLECTION ) ) {
            LOG.debug( "Creating " + REGION_SETTINGS_COLLECTION );
            mongoTemplate.createCollection( REGION_SETTINGS_COLLECTION );
            createIndexOnIden( REGION_SETTINGS_COLLECTION );
        }
        if ( !mongoTemplate.collectionExists( BRANCH_SETTINGS_COLLECTION ) ) {
            LOG.debug( "Creating " + BRANCH_SETTINGS_COLLECTION );
            mongoTemplate.createCollection( BRANCH_SETTINGS_COLLECTION );
            createIndexOnIden( BRANCH_SETTINGS_COLLECTION );
        }
        if ( !mongoTemplate.collectionExists( AGENT_SETTINGS_COLLECTION ) ) {
            LOG.debug( "Creating " + AGENT_SETTINGS_COLLECTION );
            mongoTemplate.createCollection( AGENT_SETTINGS_COLLECTION );
            createIndexOnIden( AGENT_SETTINGS_COLLECTION );
        }
        if ( !mongoTemplate.collectionExists( APPLICATION_SETTINGS_COLLECTION ) ) {
            LOG.debug( "Creating " + APPLICATION_SETTINGS_COLLECTION );
            mongoTemplate.createCollection( APPLICATION_SETTINGS_COLLECTION );
            createIndexOnIden( APPLICATION_SETTINGS_COLLECTION );
        }
    }


    @Override
    public List<ProfileUrlEntity> fetchSEOOptimizedOrganizationUnitSettings( String collectionName, int skipCount,
        int numOfRecords, List<Long> excludedEntityIds )
    {
        LOG.debug( "Getting SEO related data for " + collectionName );
        List<ProfileUrlEntity> profileUrls = null;
        // only get profile name
        Query query = new Query();
        query.addCriteria( Criteria.where( KEY_DEFAULT_BY_SYSTEM ).is( false ) );
        // query records which are not deleted or incomplete
        query.addCriteria( Criteria.where( KEY_STATUS )
            .nin( Arrays.asList( CommonConstants.STATUS_DELETED_MONGO, CommonConstants.STATUS_INCOMPLETE_MONGO ) ) );
        query.addCriteria( Criteria.where( KEY_IDENTIFIER ).nin( excludedEntityIds ) );
        query.fields().include( KEY_PROFILE_URL ).include( KEY_MODIFIED_ON ).exclude( "_id" );
        query.with( new Sort( Sort.Direction.DESC, KEY_MODIFIED_ON ) );
        if ( skipCount > 0 ) {
            query.skip( skipCount );
        }
        if ( numOfRecords > 0 ) {
            query.limit( numOfRecords );
        }
        try {
            profileUrls = mongoTemplate.find( query, ProfileUrlEntity.class, collectionName );
        } catch(Exception e) {
            LOG.error( "Failed to fetch records from mongo.",e );
        }
        
        return profileUrls;
    }


    @Override
    public long fetchSEOOptimizedOrganizationUnitCount( String collectionName, List<Long> excludedEntityIds )
    {
        LOG.debug( "Getting SEO Optimized count for collection " + collectionName );
        Query query = new Query();
        query.addCriteria( Criteria.where( KEY_DEFAULT_BY_SYSTEM ).is( false ) );
        query.addCriteria( Criteria.where( KEY_STATUS )
            .nin( Arrays.asList( CommonConstants.STATUS_DELETED_MONGO, CommonConstants.STATUS_INCOMPLETE_MONGO ) ) );
        query.addCriteria( Criteria.where( KEY_IDENTIFIER ).nin( excludedEntityIds ) );
        long count = mongoTemplate.count( query, collectionName );
        LOG.debug( "Returning count " + count );
        return count;
    }


    @Override
    public void updateCompletedSurveyCountForAgent( long agentId, int incrementCount )
    {
        LOG.debug( "Method to update completed survey count for agent started." );
        Query query = new Query( Criteria.where( "iden" ).is( agentId ) );
        Update update = new Update();
        update.inc( CommonConstants.REVIEW_COUNT_MONGO, incrementCount );
        mongoTemplate.updateFirst( query, update, AgentSettings.class, CommonConstants.AGENT_SETTINGS_COLLECTION );
        LOG.debug( "Method to update completed survey count for agent finished." );
    }


    @Override
    public List<FeedIngestionEntity> fetchSocialMediaTokens( String collectionName, int skipCount, int numOfRecords )
    {
        LOG.debug( "Fetching social media tokens from " + collectionName );
        List<FeedIngestionEntity> tokens = null;
        Query query = new Query();
        query.addCriteria( Criteria.where( KEY_SOCIAL_MEDIA_TOKENS ).exists( true ) );
        query.fields().include( KEY_SOCIAL_MEDIA_TOKENS ).include( KEY_IDENTIFIER ).exclude( "_id" );
        if ( skipCount > 0 ) {
            query.skip( skipCount );
        }
        if ( numOfRecords > 0 ) {
            query.limit( numOfRecords );
        }
        tokens = mongoTemplate.find( query, FeedIngestionEntity.class, collectionName );
        LOG.debug( "Fetched " + ( tokens != null ? tokens.size() : "none" ) + " items with social media tokens from "
            + collectionName );
        return tokens;
    }
    
    
    @Override
    public List<SocialMediaTokenResponse> fetchSocialMediaTokensForIds( List<Long> ids, String collectionName )
    {
        LOG.debug( "Fetching social media tokens from {}", collectionName );
        List<SocialMediaTokenResponse> tokens = null;
        Query query = new Query();
        query.addCriteria( Criteria.where( KEY_SOCIAL_MEDIA_TOKENS ).exists( true ) );
        query.addCriteria( Criteria.where( "iden" ).in( ids ) );
        query.fields().include( KEY_FACEBOOK_ID ).include( KEY_FACEBOOK_PAGE_LINK ).include( KEY_FACEBOOK_ACCESS_TOKEN )
            .include( KEY_FACEBOOK_ACCESS_TOKEN_TO_POST )

            .include( KEY_TWITTER_PAGE_LINK ).include( KEY_TWITTER_ACCESS_TOKEN ).include( KEY_TWITTER_ID )
            .include( KEY_TWITTER_ACCESS_TOKEN_SECRET )

            .include( KEY_LINKEDIN_ID ).include( KEY_LINKEDIN_PAGE_LINK ).include( KEY_LINKEDIN_ACCESS_TOKEN )
            .include( KEY_IDENTIFIER ).exclude( "_id" )
            .include( PROFILE_IMAGE_URL );
        tokens = mongoTemplate.find( query, SocialMediaTokenResponse.class, collectionName );
        LOG.debug( "Fetched {} items with social media tokens from {}", ( tokens != null ? tokens.size() : "none" ),
            collectionName );
        return tokens;
    }


    /*
     * Method to delete Organization unit settings for list of idens.
     */
    public void removeOganizationUnitSettings( List<Long> agentIds, String collectionName )
    {
        LOG.debug( "Method removeOganizationUnitSettings() started." );
        Query query = new Query();
        query.addCriteria( Criteria.where( "iden" ).in( agentIds ) );
        mongoTemplate.remove( query, collectionName );
        LOG.debug( "Method removeOganizationUnitSettings() finished." );
    }


    @Override
    public Map<Long, OrganizationUnitSettings> getSettingsMapWithLinkedinImageUrl( String collectionName, String matchUrl )
    {
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.PROFILE_IMAGE_URL_SOLR ).regex( matchUrl ) );
        List<OrganizationUnitSettings> settings = mongoTemplate.find( query, OrganizationUnitSettings.class, collectionName );
        Map<Long, OrganizationUnitSettings> settingsMap = new HashMap<>();
        for ( OrganizationUnitSettings setting : settings ) {
            settingsMap.put( setting.getIden(), setting );
        }
        return settingsMap;
    }


    @Override
    public void setAgentDetails( Map<Long, AgentRankingReport> agentsReport )
    {
        LOG.debug( "Method setAgentNames() started." );
        Set<Long> agentIds = agentsReport.keySet();
        List<AgentSettings> agentSettings = fetchMultipleAgentSettingsById( new ArrayList<Long>( agentIds ) );
        for ( AgentSettings setting : agentSettings ) {
            if ( agentsReport.get( setting.getIden() ) != null ) {
                try {
                    agentsReport.get( setting.getIden() ).setAgentFirstName( setting.getContact_details().getFirstName() );
                    agentsReport.get( setting.getIden() ).setAgentLastName( setting.getContact_details().getLastName() );
                    agentsReport.get( setting.getIden() ).setRegistrationDate( setting.getCreatedOn() );
                } catch ( NullPointerException e ) {
                    LOG.error( "Null Pointer exception caught in setAgentNames(). Nested exception is ", e );
                    LOG.debug( "Continuing..." );
                    continue;
                }
            }
        }
        LOG.debug( "Method setAgentNames() finished." );
    }


    @Override
    public OrganizationUnitSettings removeKeyInOrganizationSettings( OrganizationUnitSettings unitSettings, String keyToUpdate,
        String collectionName )
    {
        LOG.debug( "Method removeKeyInOrganizationSettings() started." );
        Query query = new Query();
        query.addCriteria( Criteria.where( "_id" ).is( unitSettings.getId() ) );
        Update update = new Update().unset( keyToUpdate );
        LOG.debug( "Updating the unit settings" );
        mongoTemplate.updateFirst( query, update, OrganizationUnitSettings.class, collectionName );
        unitSettings = mongoTemplate.findOne( query, OrganizationUnitSettings.class, collectionName );
        setCompleteUrlForSettings( unitSettings, collectionName );
        LOG.debug( "Method removeKeyInOrganizationSettings() finished." );
        return unitSettings;
    }


    @Override
    public List<OrganizationUnitSettings> getCompanyListByVerticalName( String verticalName )
    {
        LOG.debug( "Method getCompanyListByVerticalName() called for vertical name : " + verticalName );

        List<OrganizationUnitSettings> unitSettings = null;
        Query query = new Query();
        query.addCriteria( Criteria.where( KEY_VERTICAL ).is( verticalName ) );
        query.fields().include( KEY_LOGO ).include( KEY_CONTACT_DETAILS ).include( KEY_PROFILE_NAME ).include( KEY_VERTICAL )
            .exclude( "_id" );

        unitSettings = mongoTemplate.find( query, OrganizationUnitSettings.class, COMPANY_SETTINGS_COLLECTION );

        return unitSettings;
    }


    @Override
    public List<OrganizationUnitSettings> getCompanyList()
    {
        LOG.debug( "Method getCompanyList() started." );

        List<OrganizationUnitSettings> unitSettings = null;
        Query query = new Query();
        query.fields().include( KEY_CONTACT_DETAILS ).include( KEY_PROFILE_NAME ).include( KEY_VERTICAL ).include( KEY_IDEN )
            .exclude( "_id" );
        query.with( new Sort( Sort.Direction.DESC, KEY_MODIFIED_ON ) );

        LOG.debug( "query: " + query.toString() );

        unitSettings = mongoTemplate.find( query, OrganizationUnitSettings.class, COMPANY_SETTINGS_COLLECTION );
        LOG.debug( "Method getCompanyList() finished." );
        return unitSettings;
    }


    /**
     * Method to fetch the company setting list by set of company ids passed
     */
    @Override
    public List<OrganizationUnitSettings> getCompanyListByIds( Set<Long> companyIds )
    {
        LOG.debug( "Method getCompanyList() started." );

        List<OrganizationUnitSettings> unitSettings = null;
        Query query = new Query();
        query.addCriteria( Criteria.where( KEY_IDENTIFIER ).in( companyIds ) );
        query.fields().include( KEY_CONTACT_DETAILS ).include( KEY_PROFILE_NAME ).include( KEY_VERTICAL ).include( KEY_IDEN )
            .include( KEY_PROFILE_IMAGE ).exclude( "_id" );

        query.with( new Sort( Direction.ASC, KEY_CONTACT_NAME ) );
        LOG.debug( "Query: " + query.toString() );
        unitSettings = mongoTemplate.find( query, OrganizationUnitSettings.class, COMPANY_SETTINGS_COLLECTION );
        LOG.debug( "Method getCompanyList() finished." );
        return unitSettings;
    }


    @Override
    public List<OrganizationUnitSettings> getCompanyListByKey( String searchKey )
    {
        LOG.debug( "Method getCompanyListByKey() called for key : " + searchKey );

        List<OrganizationUnitSettings> unitSettings = null;
        Query query = new Query();
        Pattern pattern = Pattern.compile( searchKey + ".*" );
        query.addCriteria( Criteria.where( KEY_CONTACT_NAME ).regex( pattern ) );
        query.fields().include( KEY_CONTACT_DETAILS ).include( KEY_VERTICAL ).include( KEY_IDEN ).exclude( "_id" );
        query.with( new Sort( Sort.Direction.DESC, KEY_MODIFIED_ON ) );

        unitSettings = mongoTemplate.find( query, OrganizationUnitSettings.class, COMPANY_SETTINGS_COLLECTION );

        return unitSettings;
    }


    /*
     * Method to set complete profile URL for each of the setting being fetched.
     */
    private void setCompleteUrlForSettings( OrganizationUnitSettings settings, String collectionName )
    {
        if ( settings != null && collectionName != null && !collectionName.isEmpty() ) {
            switch ( collectionName ) {
                case CommonConstants.BRANCH_SETTINGS_COLLECTION:
                    settings.setCompleteProfileUrl(
                        applicationBaseUrl + CommonConstants.BRANCH_PROFILE_FIXED_URL + settings.getProfileUrl() );
                    break;
                case CommonConstants.REGION_SETTINGS_COLLECTION:
                    settings.setCompleteProfileUrl(
                        applicationBaseUrl + CommonConstants.REGION_PROFILE_FIXED_URL + settings.getProfileUrl() );
                    break;
                case CommonConstants.COMPANY_SETTINGS_COLLECTION:
                    settings.setCompleteProfileUrl(
                        applicationBaseUrl + CommonConstants.COMPANY_PROFILE_FIXED_URL + settings.getProfileUrl() );
                    break;
                case CommonConstants.AGENT_SETTINGS_COLLECTION:
                    settings.setCompleteProfileUrl(
                        applicationBaseUrl + CommonConstants.AGENT_PROFILE_FIXED_URL + settings.getProfileUrl() );
                    break;
            }
        }
    }


    @Override
    public OrganizationUnitSettings fetchOrganizationUnitSettingsByUniqueIdentifier( String uniqueIdentifier,
        String collectionName )
    {
        Query query = new Query();
        query.addCriteria( Criteria.where( KEY_UNIQUE_IDENTIFIER ).is( uniqueIdentifier ) );
        query.fields().exclude( KEY_LINKEDIN_PROFILEDATA );
        OrganizationUnitSettings organizationUnitSettings = mongoTemplate.findOne( query, OrganizationUnitSettings.class,
            collectionName );
        setCompleteUrlForSettings( organizationUnitSettings, collectionName );
        LOG.debug( "Successfully executed method fetchOrganizationUnitSettingsByProfileName" );
        return organizationUnitSettings;
    }


    @Override
    public List<OrganizationUnitSettings> getOrganizationUnitListWithCRMSource( String source, String collectionName )
        throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.debug( "Getting Organization Unit Settings List from " + collectionName + " for crm source " + source );
        if ( collectionName == null || collectionName.isEmpty() ) {
            LOG.debug( "Collection name is not present to fetch crm info list." );
            throw new InvalidInputException( "Collection name is not present to fetch crm info list." );
        }
        List<OrganizationUnitSettings> organizationUnitsSettingsList = null;
        Query query = new Query();
        if ( source != null && !source.isEmpty() ) {
            query.addCriteria( Criteria.where( KEY_CRM_INFO ).exists( true )
                .andOperator( Criteria.where( KEY_CRM_INFO_SOURCE ).is( source ) ) );
        }
        organizationUnitsSettingsList = mongoTemplate.find( query, OrganizationUnitSettings.class, collectionName );
        if ( organizationUnitsSettingsList == null || organizationUnitsSettingsList.isEmpty() ) {
            LOG.debug( "No records found for crm source: " + source );
            throw new NoRecordsFetchedException( "No records found for crm source: " + source );
        }
        LOG.debug( "Successfully found unit settings for source " + source );
        return organizationUnitsSettingsList;
    }


    /**
     * Method to get a list of companies connected to encompass for a specific encompass info state
     * @param state
     * @return
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     */
    @Override
    public List<OrganizationUnitSettings> getCompanyListForEncompass( String state, String encompassVersion )
        throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.debug( "Getting Company list for encompass where state : " + state );
        if ( state == null || state.isEmpty() ) {
            LOG.debug( "state is not present to fetch encompass info list." );
            throw new InvalidInputException( "state is not present to fetch encompass info list." );
        }


        if ( encompassVersion == null || encompassVersion.isEmpty() ) {
            LOG.debug( " encompass version is not present to fetch encompass info list." );
            throw new InvalidInputException( "encompass version is not present to fetch encompass info list." );
        }

        List<OrganizationUnitSettings> organizationUnitsSettingsList = null;
        Query query = new Query();
        query.addCriteria( Criteria.where( KEY_CRM_INFO ).exists( true ).and( KEY_CRM_INFO_SOURCE )
            .is( CommonConstants.CRM_INFO_SOURCE_ENCOMPASS ) );
        if ( state.equals( CommonConstants.CRM_INFO_DRY_RUN_STATE ) ) {
            query.addCriteria( Criteria.where( KEY_CRM_INFO + "." + CommonConstants.STATE ).is( state )
                .and( KEY_CRM_INFO + "." + CommonConstants.ENCOMPASS_GENERATE_REPORT_COLUMN ).is( true ) );
        } else if ( state.equals( CommonConstants.CRM_INFO_PRODUCTION_STATE ) ) {
            query.addCriteria( Criteria.where( KEY_CRM_INFO + "." + CommonConstants.STATE ).is( state ) );
        } else {
            throw new InvalidInputException( "Invalid encompass crm info state : " + state );
        }

        //filter out other versions of encompass info needed
        query.addCriteria(
            Criteria.where( KEY_CRM_INFO + "." + CommonConstants.ENCOMPASS_VERSION_COULMN ).is( encompassVersion ) );

        //Add criteria to make sure that it doesn't pick up companies that are deleted
        query.addCriteria( Criteria.where( KEY_STATUS ).ne( CommonConstants.STATUS_DELETED_MONGO ) );
        organizationUnitsSettingsList = mongoTemplate.find( query, OrganizationUnitSettings.class,
            CommonConstants.COMPANY_SETTINGS_COLLECTION );
        if ( organizationUnitsSettingsList == null || organizationUnitsSettingsList.isEmpty() ) {
            LOG.debug( "No records found for state : " + state );
            throw new NoRecordsFetchedException( "No records found for state : " + state );
        }
        LOG.debug( "Successfully found company settings for encompass where state : " + state );
        return organizationUnitsSettingsList;
    }


    /**
     * Method to fetch profile image urls for an entity list
     * 
     * @param entityType
     * @param entityId
     * @return
     * @throws InvalidInputException 
     */
    @SuppressWarnings ( "unchecked")
    @Override
    public List<ProfileImageUrlData> fetchProfileImageUrlsForEntityList( String entityType, HashSet<Long> entityList )
        throws InvalidInputException
    {
        LOG.debug( "Fetching profile image urls for entity type : " + entityType );
        String collectionName = null;
        if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
            collectionName = CommonConstants.COMPANY_SETTINGS_COLLECTION;
        } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
            collectionName = CommonConstants.REGION_SETTINGS_COLLECTION;
        } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
            collectionName = CommonConstants.BRANCH_SETTINGS_COLLECTION;
        } else if ( entityType.equals( CommonConstants.USER_ID ) ) {
            collectionName = CommonConstants.AGENT_SETTINGS_COLLECTION;
        }
        if ( entityType == null || entityType.isEmpty() ) {
            throw new InvalidInputException( "Invalid entity sent" );
        }
        List<ProfileImageUrlData> profileImageUrlList = new ArrayList<ProfileImageUrlData>();
        for ( Long id : entityList ) {
            if ( id <= 0 ) {
                throw new InvalidInputException( "Invalid entityId" );
            }
            Query query = new Query();
            query.addCriteria( Criteria.where( CommonConstants.IDEN ).is( id ) );
            query.fields().include( CommonConstants.PROFILE_IMAGE_THUMBNAIL_COLUMN )
                .exclude( CommonConstants.DEFAULT_MONGO_ID_COLUMN );
            String queryStr = query.toString();
            LOG.debug( "Query : " + queryStr );
            HashMap<String, String> imageUrlMap = mongoTemplate.findOne( query, HashMap.class, collectionName );
            String profileImageUrl = null;
            if ( imageUrlMap != null && !( imageUrlMap.isEmpty() ) ) {
                profileImageUrl = imageUrlMap.get( "profileImageUrlThumbnail" );
            }
            if ( profileImageUrl == null || profileImageUrl.isEmpty() ) {
                profileImageUrl = "";
            }
            ProfileImageUrlData profileImageUrlData = new ProfileImageUrlData();
            profileImageUrlData.setEntityId( id );
            profileImageUrlData.setEntityType( entityType );
            profileImageUrlData.setProfileImageUrl( profileImageUrl );
            profileImageUrlList.add( profileImageUrlData );
        }
        LOG.debug( "Method fetchProfileImageUrlsForEntityList() finished" );
        return profileImageUrlList;
    }


    @Override
    public Map<Long, String> getCollectionListOfUnprocessedImages( String collectionName, String imageType )
        throws InvalidInputException
    {
        LOG.debug( "Getting unprocessed " + imageType + " from collection name: " + collectionName );
        Map<Long, String> images = null;
        if ( collectionName == null || collectionName.isEmpty() || imageType == null || imageType.isEmpty() ) {
            LOG.error( "Invalid input getCollectionListOfUnprocessedImages" );
            throw new InvalidInputException( "Invalid input getCollectionListOfUnprocessedImages" );
        }
        Query query = new Query();
        if ( imageType.equals( CommonConstants.IMAGE_TYPE_PROFILE ) ) {
            query
                /*.addCriteria( Criteria.where( CommonConstants.PROFILE_IMAGE_URL_SOLR )
                    .regex( StringEscapeUtils.escapeJava( amazonEndPoint ) + ".*" ) )*/
                .addCriteria( new Criteria().andOperator( Criteria.where( CommonConstants.PROFILE_IMAGE_URL_SOLR ).ne( null ),
                    ( Criteria.where( CommonConstants.PROFILE_IMAGE_URL_SOLR ).ne( "" ) ) ) )
                .addCriteria( Criteria.where( CommonConstants.IS_PROFILE_IMAGE_PROCESSED_COLUMN ).is( false ) );
        } else if ( imageType.equals( CommonConstants.IMAGE_TYPE_LOGO ) ) {
            query
                .addCriteria( Criteria.where( CommonConstants.LOGO_COLUMN )
                    .regex( StringEscapeUtils.escapeJava( amazonEndPoint ) + ".*" ) )
                .addCriteria( Criteria.where( CommonConstants.IS_LOGO_IMAGE_PROCESSED_COLUMN ).is( false ) );
        } else {
            throw new InvalidInputException( "Invalid image type" );
        }
        query.fields().include( CommonConstants.PROFILE_IMAGE_URL_SOLR ).include( CommonConstants.IDEN )
            .include( CommonConstants.LOGO_COLUMN ).exclude( CommonConstants.DEFAULT_MONGO_ID_COLUMN );
        LOG.debug( "Query: " + query.toString() );
        List<OrganizationUnitSettings> unitSettings = mongoTemplate.find( query, OrganizationUnitSettings.class,
            collectionName );
        if ( unitSettings != null && unitSettings.size() > 0 ) {
            LOG.debug( "Found " + unitSettings.size() + " records." );
            images = new HashMap<>();
            for ( OrganizationUnitSettings unitSetting : unitSettings ) {
                if ( imageType.equals( CommonConstants.IMAGE_TYPE_PROFILE ) ) {
                    images.put( unitSetting.getIden(), unitSetting.getProfileImageUrl() );
                } else {
                    images.put( unitSetting.getIden(), unitSetting.getLogo() );
                }
            }
        }
        return images;
    }


    @Override
    public void updateImageForOrganizationUnitSetting( long iden, String imgFileName, String imgThumbnailFileName,
        String rectangularThumbnailFileName, String collectionName, String imageType, boolean flagValue, boolean isThumbnail )
        throws InvalidInputException
    {
        LOG.debug( "Updating thumbnail image details for collection : " + collectionName + " ID: " + iden + " imageType : "
            + imageType + " with filename : " + imgFileName );
        if ( iden <= 0l || collectionName == null || collectionName.isEmpty() || imageType == null || imageType.isEmpty() ) {
            throw new InvalidInputException( "Invalid input provided to the method updateImage" );
        }
        Query query = new Query();
        Update update = new Update();
        query.addCriteria( Criteria.where( CommonConstants.IDEN ).is( iden ) );
        //determine the key and flag to update
        //If the image you're updating isn't a thumbnail, set the same value for the image column and it's thumbnail column
        if ( imageType == CommonConstants.IMAGE_TYPE_PROFILE ) {
            if ( isThumbnail ) {
                update.set( CommonConstants.PROFILE_IMAGE_THUMBNAIL_COLUMN, imgThumbnailFileName );
                update.set( CommonConstants.PROFILE_IMAGE_RECTANGULAR_THUMBNAIL_COLUMN, rectangularThumbnailFileName );
            } else {
                update.set( CommonConstants.PROFILE_IMAGE_URL_SOLR, imgFileName );
                update.set( CommonConstants.PROFILE_IMAGE_THUMBNAIL_COLUMN, imgFileName );
                update.set( CommonConstants.PROFILE_IMAGE_RECTANGULAR_THUMBNAIL_COLUMN, imgFileName );
            }
            update.set( CommonConstants.IS_PROFILE_IMAGE_PROCESSED_COLUMN, flagValue );
        } else if ( imageType == CommonConstants.IMAGE_TYPE_LOGO ) {
            if ( isThumbnail ) {
                update.set( CommonConstants.LOGO_THUMBNAIL_COLUMN, imgThumbnailFileName );
            } else {
                update.set( CommonConstants.LOGO_COLUMN, imgFileName );
                update.set( CommonConstants.LOGO_THUMBNAIL_COLUMN, imgFileName );
            }
            update.set( CommonConstants.IS_LOGO_IMAGE_PROCESSED_COLUMN, flagValue );
        } else {
            throw new InvalidInputException( "Invalid image type" );
        }
        if ( !( isThumbnail ) ) {

        }

        update.set( CommonConstants.MODIFIED_ON_COLUMN, System.currentTimeMillis() );

        mongoTemplate.updateMulti( query, update, OrganizationUnitSettings.class, collectionName );
        LOG.debug( "Updated thumbnail image details" );
    }


    @Override
    public void removeImageForOrganizationUnitSetting( long iden, String collectionName, boolean isThumbnail, String imageType )
        throws InvalidInputException
    {
        LOG.debug( "removing thumbnail image or both profile and thumbnail images for collection: " + collectionName
            + " with id: " + iden );

        if ( iden <= 0l || collectionName == null || collectionName.isEmpty() ) {
            throw new InvalidInputException( "Invalid input provided to the method updateImage" );
        }

        Query query = new Query();
        Update update = new Update();
        query.addCriteria( Criteria.where( CommonConstants.IDEN ).is( iden ) );

        //determine the key and flag to update
        //If the image you're removing isn't a thumb-nail, set the same value for the image column and it's thumb-nail column

        if ( imageType == CommonConstants.IMAGE_TYPE_PROFILE ) {
            if ( isThumbnail ) {
                update.unset( CommonConstants.PROFILE_IMAGE_THUMBNAIL_COLUMN );
                update.unset( CommonConstants.PROFILE_IMAGE_RECTANGULAR_THUMBNAIL_COLUMN );

            } else {
                update.unset( CommonConstants.PROFILE_IMAGE_URL_SOLR );
                update.unset( CommonConstants.PROFILE_IMAGE_THUMBNAIL_COLUMN );
                update.unset( CommonConstants.PROFILE_IMAGE_RECTANGULAR_THUMBNAIL_COLUMN );
            }
            update.set( CommonConstants.IS_PROFILE_IMAGE_PROCESSED_COLUMN, false );
        } else if ( imageType == CommonConstants.IMAGE_TYPE_LOGO ) {
            if ( isThumbnail ) {
                update.unset( CommonConstants.LOGO_THUMBNAIL_COLUMN );
            } else {
                update.unset( CommonConstants.LOGO_COLUMN );
                update.unset( CommonConstants.LOGO_THUMBNAIL_COLUMN );
            }
            update.set( CommonConstants.IS_LOGO_IMAGE_PROCESSED_COLUMN, false );
        } else {
            throw new InvalidInputException( "Invalid image type" );
        }
        if ( !( isThumbnail ) ) {

        }

        update.set( CommonConstants.MODIFIED_ON_COLUMN, System.currentTimeMillis() );

        mongoTemplate.updateMulti( query, update, OrganizationUnitSettings.class, collectionName );
        LOG.debug( "removed image details" );
    }


    /**
     * Method to set the status as active, reset the profileName, and update the modifiedOn for agent.
     * @param newProfileName
     * @param agentSettings
     * @throws InvalidInputException
     */
    @Override
    public void updateAgentSettingsForUserRestoration( String newProfileName, AgentSettings agentSettings,
        boolean restoreSocial, boolean isVerified ) throws InvalidInputException
    {
        if ( agentSettings == null ) {
            throw new InvalidInputException( "AgentSettings cannot be null" );
        }
        LOG.debug( "Method updateAgentSettingsForUserRestoration started for agentId : " + agentSettings.getIden() );

        //Set status to active in mongo
        Query query = new Query();
        Update update = new Update();
        query.addCriteria( Criteria.where( CommonConstants.IDEN ).is( agentSettings.getIden() ) );
        if ( isVerified ) {
            update.set( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE_MONGO );
        } else {
            update.set( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_INCOMPLETE_MONGO );
        }

        //If newProfileName is present, then update profileName and profileUrl in mongo
        if ( newProfileName != null && !( newProfileName.isEmpty() ) ) {
            update.set( CommonConstants.PROFILE_NAME_COLUMN, newProfileName );
            update.set( CommonConstants.PROFILE_URL_SOLR, "/" + newProfileName );
        }

        //Restore social media tokens if requested
        if ( restoreSocial ) {
            if ( agentSettings.getDeletedSocialTokens() != null ) {
                SocialMediaTokens mediaTokens = agentSettings.getDeletedSocialTokens();
                update.set( KEY_SOCIAL_MEDIA_TOKENS, mediaTokens );
                update.unset( CommonConstants.DELETED_SOCIAL_MEDIA_TOKENS_COLUMN );
            }
        }
        //Update the modifiedOn column in mongo
        update.set( CommonConstants.MODIFIED_ON_COLUMN, System.currentTimeMillis() );

        mongoTemplate.updateFirst( query, update, AGENT_SETTINGS_COLLECTION );
    }


    @Override
    public List<OrganizationUnitSettings> fetchUnitSettingsConnectedToZillow( String collectionName, List<Long> identifiers )
    {
        LOG.debug( "Fetching social media tokens from " + collectionName );
        List<OrganizationUnitSettings> settings = null;
        Query query = new Query();
        query.addCriteria( Criteria.where( KEY_ZILLOW_SOCIAL_MEDIA_TOKEN ).exists( true ) );
        query.addCriteria( Criteria.where( KEY_IDENTIFIER ).in( identifiers ) );
        query.fields().include( KEY_SOCIAL_MEDIA_TOKENS ).include( KEY_IDENTIFIER ).include( KEY_SURVEY_SETTINGS )
            .include( KEY_CONTACT_DETAILS ).include( KEY_PROFILE_URL ).exclude( "_id" );
        settings = mongoTemplate.find( query, OrganizationUnitSettings.class, collectionName );
        LOG.debug( "Fetched " + ( settings != null ? settings.size() : "none" ) + " items with social media tokens from "
            + collectionName );
        return settings;
    }


    @Override
    public SocialMediaTokens fetchSocialMediaTokens( String collectionName, long iden )
    {
        LOG.debug( "Getting social media tokens for id: " + iden + " for collection " + collectionName );
        Query query = new Query();
        query.addCriteria( Criteria.where( KEY_IDEN ).is( iden ) );
        query.fields().include( KEY_SOCIAL_MEDIA_TOKENS ).exclude( "_id" );
        FeedIngestionEntity feedIngestionEntity = mongoTemplate.findOne( query, FeedIngestionEntity.class, collectionName );
        
        SocialMediaTokens socialMediaTokens = null;
        if(feedIngestionEntity != null) {
        	socialMediaTokens = feedIngestionEntity.getSocialMediaTokens();
        }
        
        return socialMediaTokens;
    }


    @Override
    public List<OrganizationUnitSettings> fetchUnitSettingsForSocialMediaTokens( String collectionName )
    {
        LOG.debug( "Fetching unit settings for social media token expiry from " + collectionName );
        List<OrganizationUnitSettings> settings = null;

        List<Criteria> cList = new ArrayList<>();

        cList.add( Criteria.where( KEY_FACEBOOK_SOCIAL_MEDIA_TOKEN ).exists( true ) );
        //cList.add( Criteria.where( KEY_TWITTER_SOCIAL_MEDIA_TOKEN ).exists( true ) );
        cList.add( Criteria.where( KEY_LINKEDIN_SOCIAL_MEDIA_TOKEN ).exists( true ) );
        //cList.add( Criteria.where( KEY_GOOGLE_SOCIAL_MEDIA_TOKEN ).exists( true ) );

        Criteria criteria = new Criteria().orOperator( cList.toArray( new Criteria[cList.size()] ) );

        Query query = new Query();
        query.addCriteria( criteria );

        query.fields().include( KEY_SOCIAL_MEDIA_TOKENS ).include( KEY_IDENTIFIER ).include( KEY_CONTACT_DETAILS )
            .include( KEY_CONTACT_DETAILS ).exclude( "_id" );

        settings = mongoTemplate.find( query, OrganizationUnitSettings.class, collectionName );
        LOG.debug( "Fetched " + ( settings != null ? settings.size() : "none" )
            + " unit settings with social media tokens from " + collectionName );

        return settings;
    }


    @Override
    public List<Long> fetchCompanyIdsWithHiddenSection()
    {
        List<Long> entityIds = new ArrayList<Long>();
        Query query = new Query();
        query.addCriteria( Criteria.where( KEY_DEFAULT_BY_SYSTEM ).is( false ) );
        query.addCriteria( Criteria.where( KEY_STATUS )
            .nin( Arrays.asList( CommonConstants.STATUS_DELETED_MONGO, CommonConstants.STATUS_INCOMPLETE_MONGO ) ) );
        query.addCriteria( Criteria.where( KEY_HIDDEN_SECTION ).is( true ) );
        List<OrganizationUnitSettings> settings = mongoTemplate.find( query, OrganizationUnitSettings.class,
            COMPANY_SETTINGS_COLLECTION );
        if ( settings != null && !settings.isEmpty() ) {
            for ( OrganizationUnitSettings setting : settings ) {
                entityIds.add( setting.getIden() );
            }
        }
        return entityIds;
    }
    
    
    @Override
    public List<SocialMediaTokenResponse> getSocialMediaTokensByCollection( String collectionName, int skipCount, int batchSize )
    {
        LOG.debug( "Fetching social media tokens from {}", collectionName );
        Query query = new Query();
        query.addCriteria( Criteria.where( KEY_SOCIAL_MEDIA_TOKENS ).exists( true )  );
        query.addCriteria( Criteria.where( KEY_STATUS )
            .nin( Arrays.asList( CommonConstants.STATUS_DELETED_MONGO, CommonConstants.STATUS_INCOMPLETE_MONGO ) ) );
        query.fields().include( KEY_SOCIAL_MEDIA_TOKENS ).include( KEY_IDENTIFIER )
            .include( KEY_CONTACT_DETAILS ).exclude( "_id" );

        if ( skipCount > 0 ) {
            query.skip( skipCount );
        }
        if ( batchSize > 0 ) {
            query.limit( batchSize );
        }
        return mongoTemplate.find( query, SocialMediaTokenResponse.class, collectionName );
    }
    

    @Override
    public long getSocialMediaTokensCount( String collectionName )
    {
        LOG.debug( "Fetching social media tokens record count from {}", collectionName );
        Query query = new Query();
        query.addCriteria( Criteria.where( KEY_SOCIAL_MEDIA_TOKENS ).exists( true ) );
        query.addCriteria( Criteria.where( KEY_STATUS )
            .nin( Arrays.asList( CommonConstants.STATUS_DELETED_MONGO, CommonConstants.STATUS_INCOMPLETE_MONGO ) ) );
        query.fields().include( KEY_SOCIAL_MEDIA_TOKENS ).include( KEY_IDENTIFIER ).exclude( "_id" );
        return mongoTemplate.count( query, collectionName );
    }
    
    
    /**
     * Method to fetch the company ID list who have opted for monthly digest mail
     */
    @Override
    public List<OrganizationUnitSettings> getMonthlyDigestEnabledEntities( String collectionType, int startIndex,
        int batchSize )
    {
        LOG.debug( "Method getCompaniesOptedForSendingMonthlyDigest() started." );

        List<OrganizationUnitSettings> unitSettings = null;
        Query query = new Query();
        query.addCriteria( Criteria.where( KEY_STATUS )
            .nin( Arrays.asList( CommonConstants.STATUS_DELETED_MONGO, CommonConstants.STATUS_INCOMPLETE_MONGO ) ) );
        query.addCriteria( new Criteria().orOperator( Criteria.where( KEY_SEND_MONTHLY_DIGEST_MAIL ).is( true ),
            Criteria.where( KEY_DIGEST_RECIPIENTS ).exists( true )
                .andOperator( Criteria.where( KEY_DIGEST_RECIPIENTS ).ne( Collections.emptySet() ) ) ) );

        if ( startIndex > -1 ) {
            query.skip( startIndex );
        }
        if ( batchSize > -1 ) {
            query.limit( batchSize );
        }

        query.with( new Sort( Sort.Direction.ASC, KEY_IDEN ) );

        LOG.debug( "Query: " + query.toString() );
        unitSettings = mongoTemplate.find( query, OrganizationUnitSettings.class, collectionType );
        LOG.debug( "Method getCompaniesOptedForSendingMonthlyDigest() finished." );
        return unitSettings;
    }


    @Override
    public List<Long> getHiddenPublicPagesEntityIds( String collection )
    {
        LOG.debug( "method getHiddenPublicPagesEntityIds started for collection {} ", collection );

        List<Long> entityIds = new ArrayList<Long>();
        Query query = new Query();
        query.addCriteria( Criteria.where( KEY_DEFAULT_BY_SYSTEM ).is( false ) );
        query.addCriteria( Criteria.where( KEY_STATUS )
            .nin( Arrays.asList( CommonConstants.STATUS_DELETED_MONGO, CommonConstants.STATUS_INCOMPLETE_MONGO ) ) );
        query.addCriteria( Criteria.where( KEY_HIDE_PUBLIC_PAGE ).is( true ) );

        query.fields().include( KEY_IDEN );

        List<OrganizationUnitSettings> settings = mongoTemplate.find( query, OrganizationUnitSettings.class, collection );
        if ( settings != null && !settings.isEmpty() ) {
            for ( OrganizationUnitSettings setting : settings ) {
                entityIds.add( setting.getIden() );
            }
        }

        LOG.debug( "method getHiddenPublicPagesEntityIds finished for collection {} ", collection );
        return entityIds;


    }


    @Override
    public List<OrganizationUnitSettings> getCompaniesForTransactionMonitor( List<Long> companyIds )
    {
        LOG.debug( "method getCompaniesForTransactionMonitor started " );

        Query query = new Query();

        query.addCriteria( Criteria.where( KEY_IDEN ).in( companyIds ) );

        //include companies those have enabled monitoring
        query.addCriteria( Criteria.where( KEY_INCLUDE_FOR_TRANSACTION_MONITOR ).is( true ) );

        query.fields().include( KEY_IDEN ).include( KEY_CONTACT_DETAILS );
        query.addCriteria( Criteria.where( KEY_STATUS )
            .nin( Arrays.asList( CommonConstants.STATUS_DELETED_MONGO, CommonConstants.STATUS_INCOMPLETE_MONGO ) ) );

        List<OrganizationUnitSettings> settings = mongoTemplate.find( query, OrganizationUnitSettings.class,
            COMPANY_SETTINGS_COLLECTION );

        LOG.debug( "method getCompaniesForTransactionMonitor finished " );
        return settings;


    }


    @Override
    public List<OrganizationUnitSettings> fetchCompaniesByAlertType( String alertType, List<Long> companyIds )
    {
        LOG.info( "method fetchCompaniesByAlertType started for alertType {} and companyIds {}", alertType, companyIds );
        Query query = new Query();
        query.addCriteria( Criteria.where( KEY_IDEN ).in( companyIds ) );

        //include companies those have enabled monitoring
        query.addCriteria( Criteria.where( KEY_INCLUDE_FOR_TRANSACTION_MONITOR ).is( true ) );

        query.addCriteria( Criteria.where( KEY_STATUS )
            .nin( Arrays.asList( CommonConstants.STATUS_DELETED_MONGO, CommonConstants.STATUS_INCOMPLETE_MONGO ) ) );

        if ( alertType.equalsIgnoreCase( CommonConstants.ALERT_TYPE_ERROR ) ) {
            query.addCriteria( Criteria.where( KEY_ENTITY_ALERT_DETAILS + "." + KEY_IS_ERROR_ALERT ).is( true ) );

        } else if ( alertType.equalsIgnoreCase( CommonConstants.ALERT_TYPE_WARNING ) ) {
            query.addCriteria( Criteria.where( KEY_ENTITY_ALERT_DETAILS + "." + KEY_IS_WARNING_ALERT_ ).is( true ) );
        } else if ( alertType.equalsIgnoreCase( CommonConstants.ALERT_TYPE_NORMAL ) ) {
            query.addCriteria( Criteria.where( KEY_ENTITY_ALERT_DETAILS + "." + KEY_IS_ERROR_ALERT ).is( false ) );
            query.addCriteria( Criteria.where( KEY_ENTITY_ALERT_DETAILS + "." + KEY_IS_WARNING_ALERT_ ).is( false ) );
        }


        List<OrganizationUnitSettings> settingsList = mongoTemplate.find( query, OrganizationUnitSettings.class,
            COMPANY_SETTINGS_COLLECTION );
        LOG.info( "method fetchCompaniesByAlertType finished for alertType {} and companyIds {}", alertType, companyIds );

        return settingsList;
    }


    @Override
    public void saveDigestRecord( String profileLevel, long entityId, SavedDigestRecord digestRecord )
        throws InvalidInputException
    {
        LOG.debug( "Method saveDigestRecord() to update digest record list started." );

        String collectionName = null;

        if ( CommonConstants.PROFILE_LEVEL_COMPANY.equals( profileLevel ) ) {
            collectionName = CommonConstants.COMPANY_SETTINGS_COLLECTION;
        } else if ( CommonConstants.PROFILE_LEVEL_REGION.equals( profileLevel ) ) {
            collectionName = CommonConstants.REGION_SETTINGS_COLLECTION;
        } else if ( CommonConstants.PROFILE_LEVEL_BRANCH.equals( profileLevel ) ) {
            collectionName = CommonConstants.BRANCH_SETTINGS_COLLECTION;
        } else {
            LOG.warn( "Invalid profile type" );
            throw new InvalidInputException( "Invalid profile type" );
        }

        Query query = new Query();
        query.addCriteria( Criteria.where( KEY_IDEN ).is( entityId ) );
        Update update = new Update();
        update.set( CommonConstants.MODIFIED_ON_COLUMN, System.currentTimeMillis() );
        update.push( KEY_SAVED_DIGEST_RECORD, digestRecord );
        mongoTemplate.updateMulti( query, update, collectionName );
        LOG.debug( "Method saveDigestRecord() to update digest record list started." );
    }


    @Override
    public OrganizationUnitSettings fetchSavedDigestRecords( String entityType, long entityId ) throws InvalidInputException
    {
        LOG.debug( "Method saveDigestRecord() to fetch digest record list running." );

        String collectionName = null;

        if ( CommonConstants.COMPANY_ID.equals( entityType ) ) {
            collectionName = CommonConstants.COMPANY_SETTINGS_COLLECTION;
        } else if ( CommonConstants.REGION_ID.equals( entityType ) ) {
            collectionName = CommonConstants.REGION_SETTINGS_COLLECTION;
        } else if ( CommonConstants.BRANCH_ID.equals( entityType ) ) {
            collectionName = CommonConstants.BRANCH_SETTINGS_COLLECTION;
        } else {
            LOG.warn( "Invalid profile type" );
            throw new InvalidInputException( "Invalid profile type" );
        }

        Query query = new Query();
        query.addCriteria( Criteria.where( KEY_IDEN ).is( entityId ) );

        query.fields().include( KEY_SAVED_DIGEST_RECORD ).exclude( CommonConstants.DEFAULT_MONGO_ID_COLUMN );
        return mongoTemplate.findOne( query, OrganizationUnitSettings.class, collectionName );
    }
    
    @Override
    public ContactDetailsSettings fetchContactDetailByEncryptedId( String encryptedId, String collection )
    {
        LOG.debug( "Fetch unit settings from for encryptedId: " + encryptedId );
        Query query = new Query();
        query.addCriteria( Criteria.where( KEY_ENCRYPTED_ID ).is( encryptedId ) );
        query.fields().include( KEY_CONTACT_DETAILS );
        OrganizationUnitSettings settings = mongoTemplate.findOne( query, OrganizationUnitSettings.class, collection );
        ContactDetailsSettings contactDetails = null;
        if ( settings != null )
            contactDetails = settings.getContact_details();
        return contactDetails;
    }
    
    @Override
    public OrganizationUnitSettings fetchSavedSwearWords( String entityType, long entityId ) throws InvalidInputException
    {
        LOG.debug( "Method fetchSavedSwearWords() to fetch swear words list running." );

        String collectionName = null;

        if ( CommonConstants.COMPANY_ID.equals( entityType ) ) {
            collectionName = CommonConstants.COMPANY_SETTINGS_COLLECTION;
        }
        Query query = new Query();
        query.addCriteria( Criteria.where( KEY_IDEN ).is( entityId ) );
        
        query.fields().include( KEY_SWEAR_WORDS ).exclude( CommonConstants.DEFAULT_MONGO_ID_COLUMN );
        return mongoTemplate.findOne( query, OrganizationUnitSettings.class, collectionName );
    }
    
    @Override
    public void updateSwearWords( String entityType, long entityId, String[] swearWords ) throws InvalidInputException
    {
        LOG.debug( "Method saveSwearWords() to update swear words list started." );
        String collectionName = null;

        
        if ( CommonConstants.COMPANY_ID.equals( entityType )  ) {
            collectionName = CommonConstants.COMPANY_SETTINGS_COLLECTION;
        } 
        
        Query query = new Query();
        query.addCriteria( Criteria.where( KEY_IDEN ).is( entityId ) );
        Update update = new Update();
        update.set( CommonConstants.MODIFIED_ON_COLUMN, System.currentTimeMillis() );
        update.set( KEY_SWEAR_WORDS, swearWords );
        mongoTemplate.updateFirst( query, update, collectionName );
        LOG.debug( "Method saveDigestRecord() to update digest record list started." );
    }
    
    @Override
    public OrganizationUnitSettings hasRegisteredForSummit( long companyId ) throws InvalidInputException
    {
        LOG.debug( "Method hasRegisteredForSummit() running." );

        Query query = new Query();
        query.addCriteria( Criteria.where( KEY_IDEN ).is( companyId ) );
        
        query.fields().include( HAS_REGISTERED_FOR_SUMMIT ).exclude( CommonConstants.DEFAULT_MONGO_ID_COLUMN );
        return mongoTemplate.findOne( query, OrganizationUnitSettings.class, CommonConstants.COMPANY_SETTINGS_COLLECTION );
    }

    @Override
    public void updateHasRegisteredForSummit( long companyId, boolean hasRegisteredForSummit ) throws InvalidInputException
    {
        LOG.debug( "Method updateHasRegisteredForSummit() started." );
        
        Query query = new Query();
        query.addCriteria( Criteria.where( KEY_IDEN ).is( companyId ) );
        Update update = new Update();
        
        update.set( HAS_REGISTERED_FOR_SUMMIT, hasRegisteredForSummit );
        
        mongoTemplate.updateFirst( query, update, CommonConstants.COMPANY_SETTINGS_COLLECTION );
        LOG.debug( "Method updateHasRegisteredForSummit() finished." );
    }
    
    @Override
    public OrganizationUnitSettings isShowSummitPopup( long entityId, String entityType ) throws InvalidInputException
    {
        LOG.debug( "Method getShowSummitPopupFlag() to fetch show summit popup flag running." );

        Query query = new Query();
        query.addCriteria( Criteria.where( KEY_IDEN ).is( entityId ) );
        
        String collection = CommonConstants.COMPANY_SETTINGS_COLLECTION;
        
        if(entityType.equalsIgnoreCase( CommonConstants.COMPANY_ID )) {
            collection = CommonConstants.COMPANY_SETTINGS_COLLECTION;
        }else if(entityType.equalsIgnoreCase( CommonConstants.BRANCH_ID )) {
            collection = CommonConstants.BRANCH_SETTINGS_COLLECTION;
        }else if(entityType.equalsIgnoreCase( CommonConstants.REGION_ID )) {
            collection = CommonConstants.REGION_SETTINGS_COLLECTION;
        }else if(entityType.equalsIgnoreCase( CommonConstants.AGENT_ID )) {
            collection = CommonConstants.AGENT_SETTINGS_COLLECTION;
        }  
        query.fields().include( IS_SHOW_SUMMIT_POPUP ).exclude( CommonConstants.DEFAULT_MONGO_ID_COLUMN );
        return mongoTemplate.findOne( query, OrganizationUnitSettings.class, collection);
    }

    @Override
    public void updateShowSummitPopup( long entityId, String entityType, boolean isShowSummitPopup ) throws InvalidInputException
    {
        LOG.debug( "Method updateShowSummitPopupFlag() to update showSummitPopup started." );
        
        Query query = new Query();
        query.addCriteria( Criteria.where( KEY_IDEN ).is( entityId ) );
        Update update = new Update();
        
        String collection = CommonConstants.COMPANY_SETTINGS_COLLECTION;
        
        if(entityType.equalsIgnoreCase( CommonConstants.COMPANY_ID )) {
            collection = CommonConstants.COMPANY_SETTINGS_COLLECTION;
        }else if(entityType.equalsIgnoreCase( CommonConstants.BRANCH_ID )) {
            collection = CommonConstants.BRANCH_SETTINGS_COLLECTION;
        }else if(entityType.equalsIgnoreCase( CommonConstants.REGION_ID )) {
            collection = CommonConstants.REGION_SETTINGS_COLLECTION;
        }else if(entityType.equalsIgnoreCase( CommonConstants.AGENT_ID )) {
            collection = CommonConstants.AGENT_SETTINGS_COLLECTION;
        }  
        update.set( IS_SHOW_SUMMIT_POPUP, isShowSummitPopup );
        
        mongoTemplate.updateFirst( query, update, collection );
        LOG.debug( "Method updateHasRegisteredForSummit() finished." );
    }


    /**
     * Method to get a list of active FTP connections information
     * @param state
     * @return
     */
    @Override
    public List<TransactionSourceFtp> getFtpConnectionsForCompany( String status, int startIndex, int batchSize )
    {
        LOG.debug( "Getting ftp connectinos for company " );

        List<OrganizationUnitSettings> companiesWithFtpConnections = null;
        Query query = new Query();

        query.addCriteria( Criteria.where( KEY_FTP_INFO ).exists( true ) );

        if ( startIndex > -1 ) {
            query.skip( startIndex );
        }
        if ( batchSize > -1 ) {
            query.limit( batchSize );
        }

        query.with( new Sort( Sort.Direction.ASC, KEY_IDEN ) );

        LOG.debug( "Query: {}", query );
        companiesWithFtpConnections = mongoTemplate.find( query, OrganizationUnitSettings.class,
            CommonConstants.COMPANY_SETTINGS_COLLECTION );

        if ( companiesWithFtpConnections != null && !companiesWithFtpConnections.isEmpty() ) {
            List<TransactionSourceFtp> ftpInfoList = new ArrayList<>();
            for ( OrganizationUnitSettings company : companiesWithFtpConnections ) {
                if ( company.getTransactionSourceFtpList() != null && !company.getTransactionSourceFtpList().isEmpty() ) {
                    for ( TransactionSourceFtp ftp : company.getTransactionSourceFtpList() ) {
                        if ( StringUtils.equals( status, ftp.getStatus() ) ) {
                            ftpInfoList.add( ftp );  
                        }
                    }
                }
            }
            return ftpInfoList;
        } else {
            return Collections.emptyList();
        }
    }
    
    @Override
    public TransactionSourceFtp fetchFileHeaderMapper(long companyId , long ftpId ) {
        LOG.debug( "The method fetchFileHeaderMapper() to fetch file header started for companyId: {} , ftpId: {}",companyId,ftpId );
        TransactionSourceFtp transactionSourceFtp = null;
        Query query = new Query();
        query.addCriteria( Criteria.where( KEY_IDEN ).is( companyId ) );
        query.addCriteria( Criteria.where( KEY_TRANSACTION_SOURCE_FTP ).elemMatch( Criteria.where( KEY_TRANSACTION_SOURCE_FTP_ID ).is( ftpId ).and(  KEY_TRANSACTION_SOURCE_FTP_STATUS ).is( 'A' ) ) );
        query.fields().include( KEY_TRANSACTION_SOURCE_FTP_DOLLAR).exclude( CommonConstants.DEFAULT_MONGO_ID_COLUMN );
        OrganizationUnitSettings organizationUnitSettings = mongoTemplate.findOne( query, OrganizationUnitSettings.class, COMPANY_SETTINGS_COLLECTION );
        if(organizationUnitSettings != null && organizationUnitSettings.getTransactionSourceFtpList() != null && organizationUnitSettings.getTransactionSourceFtpList().size() ==1) {
            
            transactionSourceFtp =  organizationUnitSettings.getTransactionSourceFtpList().get( 0 );
        }
        LOG.info( "query : {} \n transactionSourceFtp : {}",query,organizationUnitSettings );
        return transactionSourceFtp;
        
    }
    
    @Override
    public List<TransactionSourceFtp> fetchTransactionFtpListActive(long companyId ) {
        LOG.debug( "The method fetchTransactionFtpListActive() to fetch active ftp list started for companyId: {} ",companyId );
        List<TransactionSourceFtp> transactionSourceFtpList = null;
        Query query = new Query();
        query.addCriteria( Criteria.where( KEY_IDEN ).is( companyId ) );
        query.addCriteria( Criteria.where(  KEY_TRANSACTION_SOURCE_FTP+"."+KEY_TRANSACTION_SOURCE_FTP_STATUS ).is( 'A' )  );
        OrganizationUnitSettings organizationUnitSettings = mongoTemplate.findOne( query, OrganizationUnitSettings.class, COMPANY_SETTINGS_COLLECTION );
        if(organizationUnitSettings != null && organizationUnitSettings.getTransactionSourceFtpList() != null) {
            
            transactionSourceFtpList =  organizationUnitSettings.getTransactionSourceFtpList();
        }
        LOG.info( "query : {} \n transactionSourceFtpList : {}",query,transactionSourceFtpList );
        return transactionSourceFtpList;
        
    }
    
    
    @Override
    public void updateFtpTransaction(long companyId , List<TransactionSourceFtp> transactionSourceFtp) {
        LOG.debug( "The method fetchFileHeaderMapper() to fetch file header started for companyId: {} ",companyId);
        Query query = new Query();
        query.addCriteria( Criteria.where( KEY_IDEN ).is( companyId ) );
        Update update = new Update();
        update.set( KEY_TRANSACTION_SOURCE_FTP , transactionSourceFtp);

        mongoTemplate.updateFirst( query, update, COMPANY_SETTINGS_COLLECTION );
        LOG.info( "query : {} ",query );
        
    }

    @Override
    public List<SocialMediaTokenResponse> getFbTokensByCollection( String collectionName, int skipCount, int batchSize )
    {
        LOG.debug( "Fetching social media tokens from {}", collectionName );
        Query query = new Query();
        query.addCriteria( Criteria.where( KEY_FACEBOOK_SOCIAL_MEDIA_TOKEN ).exists( true ) );
        query.addCriteria( Criteria.where( KEY_STATUS )
            .nin( Arrays.asList( CommonConstants.STATUS_DELETED_MONGO, CommonConstants.STATUS_INCOMPLETE_MONGO ) ) );
        query.fields().include( KEY_FACEBOOK_SOCIAL_MEDIA_TOKEN ).include( KEY_IDENTIFIER ).include( KEY_CONTACT_DETAILS )
            .include( KEY_FBREVIEW_LASTFETCHED ).exclude( "_id" );

        if ( skipCount > 0 ) {
            query.skip( skipCount );
        }
        if ( batchSize > 0 ) {
            query.limit( batchSize );
        }
        return mongoTemplate.find( query, SocialMediaTokenResponse.class, collectionName );
    }

    @Override
    public List<OrganizationUnitSettings> getOrganizationSettingsByKey( String key, Object value, String collectionName )
    {
        Query query = new Query();
        query.addCriteria( Criteria.where( key ).is( value ) );
        List<OrganizationUnitSettings> settings = mongoTemplate.find( query, OrganizationUnitSettings.class, collectionName );
        return settings;
    }


    @Override public long getFacebookTokensCount( String collectionName )
    {
        LOG.debug( "Fetching facebook tokens record count from {}", collectionName );
        Query query = new Query();
        query.addCriteria( Criteria.where( KEY_FACEBOOK_SOCIAL_MEDIA_TOKEN ).exists( true ) );
        query.addCriteria( Criteria.where( KEY_STATUS )
            .nin( Arrays.asList( CommonConstants.STATUS_DELETED_MONGO, CommonConstants.STATUS_INCOMPLETE_MONGO ) ) );
        query.fields().include( KEY_FACEBOOK_SOCIAL_MEDIA_TOKEN ).include( KEY_IDENTIFIER ).exclude( "_id" );
        return mongoTemplate.count( query, collectionName );
    }


    @Override public OrganizationUnitSettings fetchSocialMediaLastFetched( long iden, String collection )
    {
        LOG.debug( "Fetching socialMediaLastFetched from {} with id {}", collection, iden );
        Query query = new Query();
        query.addCriteria( Criteria.where( KEY_IDEN  ).is( iden ));
        query.addCriteria( Criteria.where( KEY_SOCIAL_MEDIA_LASTFETCHED ).exists( true )  );
        query.fields().include( KEY_SOCIAL_MEDIA_LASTFETCHED ).exclude( "_id" );
        return mongoTemplate.findOne( query, OrganizationUnitSettings.class,collection );

    }

    @Override
    public boolean removeKeyInOrganizationSettings( long iden, String keyToUpdate, String collectionName )
    {
        LOG.debug( "Method removeKeyInOrganizationSettings() started." );
        Query query = new Query();
        query.addCriteria( Criteria.where( KEY_IDEN ).is( iden ) );
        Update update = new Update().unset( keyToUpdate );
        LOG.debug( "Updating the unit settings" );
        WriteResult updateResult = mongoTemplate.updateFirst( query, update, OrganizationUnitSettings.class, collectionName );
        return updateResult.isUpdateOfExisting();
    }

    
    @Override
    public AddressGeoLocationVO fetchAddressForId(long entityId, String entityType, String collectionName) {
        LOG.debug( "The method fetchAddressForId() to fetch address for entityType:{}, entityId:{} ", entityType, entityId );
        ContactDetailsSettings contactDetails = new ContactDetailsSettings();
        GeoJsonPoint location = null;
        Query query = new Query();
        query.addCriteria( Criteria.where( KEY_IDEN ).is( entityId ) );
        query.addCriteria( Criteria.where(  KEY_CONTACT_DETAILS_ADDRESS ).exists( true ) );
        query.addCriteria( Criteria.where( KEY_DEFAULT_BY_SYSTEM ).is( false ) );
        query.addCriteria( Criteria.where( KEY_ACCOUNT_DISABLED ).is( false ) );
        query.fields().include( KEY_CONTACT_DETAILS).exclude( CommonConstants.DEFAULT_MONGO_ID_COLUMN );
        OrganizationUnitSettings organizationUnitSettings = mongoTemplate.findOne( query, OrganizationUnitSettings.class, collectionName );
        if(organizationUnitSettings != null && organizationUnitSettings.getContact_details() != null) {
            contactDetails =  organizationUnitSettings.getContact_details();
            if(organizationUnitSettings.getGeoLocation() != null) {
            	location = organizationUnitSettings.getGeoLocation();
            }
        }
        return createAddressGeoLocationVo(contactDetails,location);
        
    }
    
    @Override
    public void updateAddressForLowerHierarchy(String collectionName, AddressGeoLocationVO addGeoVO, List<Long> listOfId) {
        LOG.debug( "The method to update collection:{} for listOfId:{} with the address ",collectionName,listOfId );
        Query query = new Query();
        query.addCriteria( Criteria.where( KEY_IDEN ).in( listOfId ) );
        query.addCriteria( Criteria.where( KEY_DEFAULT_BY_SYSTEM ).is( false ) );
        query.addCriteria( Criteria.where( KEY_ACCOUNT_DISABLED ).is( false ) );
        query.addCriteria( 
        		//query check if agent has his own address by seeing the flag or if contact details don't exist
        		new Criteria().orOperator(Criteria.where(  KEY_CONTACT_DETAILS_ADDRESS ).exists( false ),
        				Criteria.where( KEY_CONTACT_DETAILS_UPDATED_BY_SYSTEM ).is( true ),
        				Criteria.where( KEY_CONTACT_DETAILS_UPDATED_BY_SYSTEM ).exists( false )) );
        
      
        mongoTemplate.updateMulti( query, createContactDetailsUpdate(addGeoVO), OrganizationUnitSettings.class, collectionName );
        
    }
    
    @Override
    public void updateAgentAddress(String collectionName, AddressGeoLocationVO addGeoVO, long userId) {
        LOG.debug( "The method to update collection:{} for userId:{} with the address ",collectionName,userId );
        Query query = new Query();
        query.addCriteria( Criteria.where( KEY_IDEN ).is( userId ) );
        query.addCriteria( Criteria.where( KEY_DEFAULT_BY_SYSTEM ).is( false ) );
        query.addCriteria( Criteria.where( KEY_ACCOUNT_DISABLED ).is( false ) );
        query.addCriteria( 
        		//query check if agent has his own address by seeing the flag or if contact details don't exist
        		new Criteria().orOperator(Criteria.where(  KEY_CONTACT_DETAILS_ADDRESS ).exists( false ),
        				Criteria.where( KEY_CONTACT_DETAILS_UPDATED_BY_SYSTEM ).is( true ),
        				Criteria.where( KEY_CONTACT_DETAILS_UPDATED_BY_SYSTEM ).exists( false ))
        		 );
        
        
        mongoTemplate.updateFirst( query, createContactDetailsUpdate(addGeoVO), OrganizationUnitSettings.class, collectionName );
        LOG.info("update done");
        
    }
    
    public Update createContactDetailsUpdate(AddressGeoLocationVO addGeoVO) {
    	Update update = new Update();
        update.set( KEY_CONTACT_DETAILS_ADDRESS, addGeoVO.getAddress() );
        update.set( KEY_CONTACT_DETAILS_ADDRESS1, addGeoVO.getAddress1());
        update.set( KEY_CONTACT_DETAILS_ADDRESS2, addGeoVO.getAddress2());
        update.set( KEY_CONTACT_DETAILS_COUNTRY, addGeoVO.getCountry());
        update.set( KEY_CONTACT_DETAILS_STATE, addGeoVO.getState());
        update.set( KEY_CONTACT_DETAILS_CITY, addGeoVO.getCity());
        update.set( KEY_CONTACT_DETAILS_COUNTRY_CODE, addGeoVO.getCountryCode());
        update.set( KEY_CONTACT_DETAILS_ZIP_CODE, addGeoVO.getZipcode());
        update.set( KEY_CONTACT_DETAILS_UPDATED_BY_SYSTEM, true);
        //update modified on so etl can pick it up
        update.set( CommonConstants.MODIFIED_ON_COLUMN, System.currentTimeMillis() );
        
        //update location
        update.set( KEY_LOCATION , createGeoJsonPoint(addGeoVO.getLatitude(),addGeoVO.getLongitude()));
        return update;
    }
    
    @Override
    public GeoJsonPoint createGeoJsonPoint(double lat,double lng) {
    	//https://drissamri.be/blog/2015/08/18/build-a-location-api-with-spring-data-mongodb-and-geojson/
        return new GeoJsonPoint(
                Double.valueOf(lng),
                Double.valueOf(lat));
        
    }
    
    @Override
    public void updateLocation(double lat,double lng,long entityId,String collectionName) {
    	 LOG.debug( "The method to update collection:{} for userId:{} with the address ",collectionName,entityId );
         Query query = new Query();
         query.addCriteria( Criteria.where( KEY_IDEN ).is( entityId ) );
         Update update = new Update();
         //update location
         if(lat == 0 && lng == 0) {
        	 update.unset( KEY_LOCATION);
         }else {
        	 update.set( KEY_LOCATION ,  createGeoJsonPoint(lat, lng));
         }
         mongoTemplate.updateFirst( query, update, OrganizationUnitSettings.class, collectionName );
    }
    
    @Override
    public AddressGeoLocationVO createAddressGeoLocationVo(ContactDetailsSettings contactDetails, GeoJsonPoint location) {
    	AddressGeoLocationVO addressVo = new AddressGeoLocationVO();
    	addressVo.setAddress(contactDetails.getAddress());
    	addressVo.setAddress1(contactDetails.getAddress1());
    	addressVo.setAddress2(contactDetails.getAddress2());
    	addressVo.setCity(contactDetails.getCity());
    	addressVo.setCountry(contactDetails.getCountry());
    	addressVo.setCountryCode(contactDetails.getCountryCode());
    	addressVo.setState(contactDetails.getState());
    	addressVo.setZipcode(contactDetails.getZipcode());
    	addressVo.setUpdatedBySystem(true);
    	
    	if(location != null) {
    		//https://community.esri.com/thread/191440-pulling-lat-long-data-from-geopoint
    		addressVo.setLatitude(location.getY());
    		addressVo.setLongitude(location.getX());
    	}
    	
    	return addressVo;
    }
    
    //for one time work
    @Override
    public List<OrganizationUnitSettings> fetchUsersWithOwnAddress(String collectionName)
    {
        Query query = new Query();
        if(collectionName.equals(AGENT_SETTINGS_COLLECTION)) {
        	query.addCriteria( Criteria.where( KEY_STATUS )
                    .nin( Arrays.asList( CommonConstants.STATUS_DELETED_MONGO, CommonConstants.STATUS_INCOMPLETE_MONGO ) ) );
        }
        
        //check if contact details has address feild 
        query.addCriteria(Criteria.where(  KEY_CONTACT_DETAILS_ADDRESS ).exists( true ));
        
        //check if flag is false or empty
        query.addCriteria(new Criteria().orOperator(Criteria.where(KEY_CONTACT_DETAILS_UPDATED_BY_SYSTEM).is(false), Criteria.where(KEY_CONTACT_DETAILS_UPDATED_BY_SYSTEM).exists(false)));
        
        //dont xcheck if latLng doesn't exist
       // query.addCriteria(Criteria.where(KEY_LOCATION).exists(false));
        
        //include only necessary feilds
        query.fields().include( KEY_IDEN).include( KEY_CONTACT_DETAILS_ADDRESS).include(KEY_CONTACT_DETAILS_ADDRESS1).include(KEY_CONTACT_DETAILS_ADDRESS2).include(KEY_CONTACT_DETAILS_CITY)
        .include(KEY_CONTACT_DETAILS_COUNTRY).include(KEY_CONTACT_DETAILS_COUNTRY_CODE).include(KEY_CONTACT_DETAILS_STATE).include(KEY_CONTACT_DETAILS_ZIP_CODE).exclude( CommonConstants.DEFAULT_MONGO_ID_COLUMN );
        
        return mongoTemplate.find( query, OrganizationUnitSettings.class,
        		collectionName );
    }
    
    /* db.getCollection('AGENT_SETTINGS').aggregate([{$geoNear: {near: { type: "Point", coordinates: [ 77.6142767 , 13.0039488 ]},
      	distanceField: "dist.calculated",maxDistance: 1600,spherical: true}}])*/
    //query for the below function
    @Override
    public List<OrganizationUnitSettings> nearestToLoc(double longitude,double latitude,double maxDistanceInMeters, String collectionName) {
         LOG.debug("the method nearestToLoc has started");
         Point point = new Point(longitude,latitude);
         
         NearQuery nearQuery = NearQuery.near(point).maxDistance(maxDistanceInMeters, Metrics.MILES).spherical(true);
         TypedAggregation<OrganizationUnitSettings> aggregation = null;
         aggregation = new TypedAggregation<>( OrganizationUnitSettings.class,
                 Aggregation.geoNear(nearQuery, KEY_DISTANCE_FIELD),
                 Aggregation.sort(Sort.Direction.ASC, KEY_DISTANCE_FIELD)
                 );
         AggregationResults<OrganizationUnitSettings> result = mongoTemplate.aggregate( aggregation, collectionName,
        		 OrganizationUnitSettings.class );
         return result.getMappedResults();
    }
    
    /**
     * 
     * @param iden
     * @param collectionName
     * @return
     */
    @Override
    public SurveyStats getSurveyStats(long iden, String collectionName) 
    {
    		LOG.debug("Method  getSurveyStats started for collection {} and iden {}", collectionName, iden);
    		SurveyStats surveyStats;
    		
    		//create criteria query and define field to be fetched
    	 	Query query = new Query( Criteria.where( CommonConstants.IDEN ).is( iden ) );
    	 	query.fields().include( KEY_SURVEY_STATS );
    	 	
    	 	//fetch collection from db
         OrganizationUnitSettings organizationUnitSettings = mongoTemplate.findOne( query, OrganizationUnitSettings.class, collectionName );
         
         //check for empty object
         if(organizationUnitSettings == null)
        	 	surveyStats = new SurveyStats();
         else
        	 	surveyStats = organizationUnitSettings.getSurveyStats();
         
         //return survey stats object
 		LOG.debug("Method  getSurveyStats finished for collection {} and iden {}", collectionName, iden);
 		return surveyStats;

    }
    
    @Override
    public void updateSurveyStats(long iden, String collectionName, SurveyStats surveyStats) 
    {
    		LOG.debug("Method  updateSurveyStats started for collection {} and iden {}", collectionName, iden);
    		
    		//create criteria query and define field to be fetched
    	 	Query query = new Query( Criteria.where( CommonConstants.IDEN ).is( iden ) );
    	 	
    	 	//update collection in db
         Update update = new Update();
         update.set( KEY_SURVEY_STATS , surveyStats);
         mongoTemplate.updateFirst( query, update, collectionName );
         
 		LOG.debug("Method  updateSurveyStats finished for collection {} and iden {}", collectionName, iden);
    }

	@Override
	public List<OrganizationUnitSettings> getSearchResultsForCriteria(AdvancedSearchVO advancedSearchVO, String collectionName,
			LOSearchEngine loSearchEngine, long companyIdFilter, String pattern) {
		LOG.debug("the method to search by given criteria is as follows");

		// adding aggregationOperation for sorting criteria
		TypedAggregation<OrganizationUnitSettings> aggregation = null;
		List<AggregationOperation> operations = new ArrayList<>();
		//check if it's textSearch
		boolean isTextSearch = false;
		// check if it's boolean flag
		boolean isLocationSearch = false;
		Query query = new Query();
		
		// check if it's normal search
		// if normal get default values for reviews and sort criteria
		//to find pattern irrespective of case sensitivity
		String regexOption = "i";
		boolean checkLoc = false;
		if(advancedSearchVO.getNearLocation() != null) {
			checkLoc = true;
		}
		if(advancedSearchVO.getFindBasedOn() != null && !advancedSearchVO.getFindBasedOn().isEmpty()) {
			String regexForName = pattern + advancedSearchVO.getFindBasedOn();
			if(checkLoc)
				query.addCriteria(Criteria.where(KEY_CONTACT_DETAILS_NAME).regex(regexForName,regexOption ));
			else
				operations.add(Aggregation.match(Criteria.where(KEY_CONTACT_DETAILS_NAME).regex(regexForName,regexOption )));
			
			isTextSearch = true;
		}
		
		//exclude default entities
		if(checkLoc)
			query.addCriteria(Criteria.where( KEY_DEFAULT_BY_SYSTEM ).is( false ));
		else
		 operations.add(Aggregation.match(Criteria.where( KEY_DEFAULT_BY_SYSTEM ).is( false )));
		
		//exclude incomplete or deleted
		if(checkLoc)
		  query.addCriteria(Criteria.where( KEY_STATUS )
		         .nin( Arrays.asList( CommonConstants.STATUS_DELETED_MONGO, CommonConstants.STATUS_INCOMPLETE_MONGO ) ));
		else
			operations.add(Aggregation.match(Criteria.where( KEY_STATUS )
			         .nin( Arrays.asList( CommonConstants.STATUS_DELETED_MONGO, CommonConstants.STATUS_INCOMPLETE_MONGO ) )));
		
		// add operation based on rating
		if (advancedSearchVO.getRatingCriteria() != 0) {
			if(checkLoc)
				query.addCriteria(Criteria.where(KEY_SURVEY_STATS_AVG_SCORE).gte(advancedSearchVO.getRatingCriteria()));
			else
				operations.add(Aggregation
					.match(Criteria.where(KEY_SURVEY_STATS_AVG_SCORE).gte(advancedSearchVO.getRatingCriteria())));
		}
		
			
		// add operation based on review
		if (advancedSearchVO.getReviewCountCriteria() != 0) {
			if(checkLoc)
				query.addCriteria(Criteria.where(KEY_SURVEY_STATS_SURVEY_COUNT).gte(advancedSearchVO.getReviewCountCriteria()));
			else
				operations.add(Aggregation.match(
						Criteria.where(KEY_SURVEY_STATS_SURVEY_COUNT).gte(advancedSearchVO.getReviewCountCriteria())));
		} else {
			if(checkLoc)
				query.addCriteria(Criteria.where(KEY_SURVEY_STATS_SURVEY_COUNT).gte(1));
			else
				operations.add(Aggregation.match(Criteria.where(KEY_SURVEY_STATS_SURVEY_COUNT).gte(1)));

		}
		// add operation if category given
		if (advancedSearchVO.getCategoryFilterList() != null && !advancedSearchVO.getCategoryFilterList().isEmpty())
			if(checkLoc)
				query.addCriteria(Criteria.where(KEY_VERTICAL).in(advancedSearchVO.getCategoryFilterList()));
			else
			operations
					.add(Aggregation.match(Criteria.where(KEY_VERTICAL).in(advancedSearchVO.getCategoryFilterList())));
			
		
		// add company id filter if need to search with in company
		if (companyIdFilter > 0l)
			if (checkLoc)
				query.addCriteria(Criteria.where(KEY_COMPANY_ID).is(companyIdFilter));
			else
				operations.add(Aggregation.match(Criteria.where(KEY_COMPANY_ID).is(companyIdFilter)));
		
		// add criteria to ignore hidden section and hide page by default
		if (checkLoc) {
			if(collectionName.equals(AGENT_SETTINGS_COLLECTION)) {
				query.addCriteria(new Criteria().orOperator(Criteria.where(KEY_HIDDEN_SECTION).exists(false),
						Criteria.where(KEY_HIDDEN_SECTION).is(false).orOperator(
								Criteria.where(KEY_HIDE_PUBLIC_PAGE).exists(false),
								Criteria.where(KEY_HIDE_PUBLIC_PAGE).is(false))));
			}else {
				query.addCriteria(new Criteria().orOperator(
								Criteria.where(KEY_HIDE_PUBLIC_PAGE).exists(false),
								Criteria.where(KEY_HIDE_PUBLIC_PAGE).is(false)));
			}
		} else {
			if(collectionName.equals(AGENT_SETTINGS_COLLECTION)) {
				operations.add(Aggregation.match(new Criteria().orOperator(Criteria.where(KEY_HIDDEN_SECTION).exists(false),
						Criteria.where(KEY_HIDDEN_SECTION).is(false))));
				operations
						.add(Aggregation.match(new Criteria().orOperator(Criteria.where(KEY_HIDE_PUBLIC_PAGE).exists(false),
								Criteria.where(KEY_HIDE_PUBLIC_PAGE).is(false))));
			}else {
				operations
				.add(Aggregation.match(new Criteria().orOperator(Criteria.where(KEY_HIDE_PUBLIC_PAGE).exists(false),
						Criteria.where(KEY_HIDE_PUBLIC_PAGE).is(false))));
			}
			
		}
					

		// add near query only if lat and lng is given
		if (checkLoc) {
			// get based on distance
			// give lat and lng
			Point point = new Point(advancedSearchVO.getNearLocation().lng, advancedSearchVO.getNearLocation().lat);
			NearQuery nearQuery = NearQuery.near(point)
					.maxDistance(advancedSearchVO.getDistanceCriteria(), Metrics.MILES).spherical(true).num(2000000000);
			nearQuery.query(query);
			operations.add(Aggregation.geoNear(nearQuery, KEY_DISTANCE_FIELD));
			isLocationSearch = true;
		}

		// sort Criteria where default is best match
		operations.add(getSortByAggOperation(advancedSearchVO.getSortBy(), isLocationSearch, isTextSearch));
		// add skip which is start index
		if (advancedSearchVO.getStartIndex() != 0)
			operations.add(Aggregation.skip((int) advancedSearchVO.getStartIndex()));
		// add limit
		if (advancedSearchVO.getBatchSize() != 0) {
			operations.add(Aggregation.limit(advancedSearchVO.getBatchSize()));
		}
		
		aggregation = new TypedAggregation<>(OrganizationUnitSettings.class, operations);
		AggregationResults<OrganizationUnitSettings> result = mongoTemplate.aggregate(aggregation, collectionName,
				OrganizationUnitSettings.class);
		List<OrganizationUnitSettings> unitSettings = result.getMappedResults();
		for (OrganizationUnitSettings organizationUnitSetting : unitSettings) {
			setCompleteUrlForSettings(organizationUnitSetting, collectionName);
		}
		return unitSettings;

	}
    
    @Override
    public AggregationOperation getSortByAggOperation(String sortBy,Boolean isLocationSearch, Boolean isTextSearch) {
    	switch(sortBy) {
    	case CommonConstants.SEARCH_ENGINE_SORT_BY_RATING:
    		return Aggregation.sort(Sort.Direction.DESC,KEY_SURVEY_STATS_AVG_SCORE).and(Sort.Direction.DESC,KEY_SURVEY_STATS_SURVEY_COUNT);
    	case CommonConstants.SEARCH_ENGINE_SORT_BY_REVIEWS:
    		return Aggregation.sort(Sort.Direction.DESC,KEY_SURVEY_STATS_SURVEY_COUNT);
    	case CommonConstants.SEARCH_ENGINE_SORT_BY_DISTANCE:
    		if(isLocationSearch)
    			return Aggregation.sort(Sort.Direction.ASC,KEY_DISTANCE_FIELD);
    		//return default
    		return Aggregation.sort(Sort.Direction.DESC,KEY_SURVEY_STATS_SEARCH_RANKING_SCORE).and(Sort.Direction.DESC,KEY_SURVEY_STATS_SURVEY_COUNT);
    	case CommonConstants.SEARCH_ENGINE_SORT_BY_BEST_MATCH:
    		if(isTextSearch)
    			return Aggregation.sort(Sort.Direction.ASC,KEY_CONTACT_DETAILS_NAME);
//    		if(isLocationSearch)
//    			return Aggregation.sort(Sort.Direction.ASC,KEY_DISTANCE_FIELD);
    		return Aggregation.sort(Sort.Direction.DESC,KEY_SURVEY_STATS_SEARCH_RANKING_SCORE).and(Sort.Direction.DESC,KEY_SURVEY_STATS_SURVEY_COUNT);
		default :
			return Aggregation.sort(Sort.Direction.DESC,KEY_SURVEY_STATS_SEARCH_RANKING_SCORE).and(Sort.Direction.DESC,KEY_SURVEY_STATS_SURVEY_COUNT);
    	}
    }
    
    @Override
	public long getSearchResultsForCriteriaCount(AdvancedSearchVO advancedSearchVO,
			String collectionName, LOSearchEngine loSearchEngine, long companyIdFilter, String pattern) {
		LOG.debug("the method to get count for search by given criteria is as follows");

		// adding aggregationOperation for sorting criteria
		TypedAggregation<OrganizationUnitSettings> aggregation = null;
		List<AggregationOperation> operations = new ArrayList<>();
		//check if it's textSearch
		boolean isTextSearch = false;
		// check if it's boolean flag
		boolean isLocationSearch = false;
		Query query = new Query();
		
		// check if it's normal search
		// if normal get default values for reviews and sort criteria
		//to find pattern irrespective of case sensitivity
		String regexOption = "i";
		boolean checkLoc = false;
		if(advancedSearchVO.getNearLocation() != null) {
			checkLoc = true;
		}
		if(advancedSearchVO.getFindBasedOn() != null && !advancedSearchVO.getFindBasedOn().isEmpty()) {
			String regexForName = pattern + advancedSearchVO.getFindBasedOn();
			if(checkLoc)
				query.addCriteria(Criteria.where(KEY_CONTACT_DETAILS_NAME).regex(regexForName,regexOption ));
			else
				operations.add(Aggregation.match(Criteria.where(KEY_CONTACT_DETAILS_NAME).regex(regexForName,regexOption )));
			
			isTextSearch = true;
		}
		
		//exclude default entities
		if(checkLoc)
			query.addCriteria(Criteria.where( KEY_DEFAULT_BY_SYSTEM ).is( false ));
		else
		 operations.add(Aggregation.match(Criteria.where( KEY_DEFAULT_BY_SYSTEM ).is( false )));
		
		//exclude incomplete or deleted
		if(checkLoc)
		  query.addCriteria(Criteria.where( KEY_STATUS )
		         .nin( Arrays.asList( CommonConstants.STATUS_DELETED_MONGO, CommonConstants.STATUS_INCOMPLETE_MONGO ) ));
		else
			operations.add(Aggregation.match(Criteria.where( KEY_STATUS )
			         .nin( Arrays.asList( CommonConstants.STATUS_DELETED_MONGO, CommonConstants.STATUS_INCOMPLETE_MONGO ) )));
		
		// add operation based on rating
		if (advancedSearchVO.getRatingCriteria() != 0) {
			if(checkLoc)
				query.addCriteria(Criteria.where(KEY_SURVEY_STATS_AVG_SCORE).gte(advancedSearchVO.getRatingCriteria()));
			else
				operations.add(Aggregation
					.match(Criteria.where(KEY_SURVEY_STATS_AVG_SCORE).gte(advancedSearchVO.getRatingCriteria())));
		}
		
			
		// add operation based on review
		if (advancedSearchVO.getReviewCountCriteria() != 0) {
			if(checkLoc)
				query.addCriteria(Criteria.where(KEY_SURVEY_STATS_SURVEY_COUNT).gte(advancedSearchVO.getReviewCountCriteria()));
			else
				operations.add(Aggregation.match(
						Criteria.where(KEY_SURVEY_STATS_SURVEY_COUNT).gte(advancedSearchVO.getReviewCountCriteria())));
		} else {
			if(checkLoc)
				query.addCriteria(Criteria.where(KEY_SURVEY_STATS_SURVEY_COUNT).gte(1));
			else
				operations.add(Aggregation.match(Criteria.where(KEY_SURVEY_STATS_SURVEY_COUNT).gte(1)));

		}
		// add operation if category given
		if (advancedSearchVO.getCategoryFilterList() != null && !advancedSearchVO.getCategoryFilterList().isEmpty())
			if(checkLoc)
				query.addCriteria(Criteria.where(KEY_VERTICAL).in(advancedSearchVO.getCategoryFilterList()));
			else
			operations
					.add(Aggregation.match(Criteria.where(KEY_VERTICAL).in(advancedSearchVO.getCategoryFilterList())));
			
		
		// add company id filter if need to search with in company
		if (companyIdFilter > 0l)
			if (checkLoc)
				query.addCriteria(Criteria.where(KEY_COMPANY_ID).is(companyIdFilter));
			else
				operations.add(Aggregation.match(Criteria.where(KEY_COMPANY_ID).is(companyIdFilter)));
		
		// add criteria to ignore hidden section and hide page by default
		if (checkLoc) {
			if(collectionName.equals(AGENT_SETTINGS_COLLECTION)) {
				query.addCriteria(new Criteria().orOperator(Criteria.where(KEY_HIDDEN_SECTION).exists(false),
						Criteria.where(KEY_HIDDEN_SECTION).is(false).orOperator(
								Criteria.where(KEY_HIDE_PUBLIC_PAGE).exists(false),
								Criteria.where(KEY_HIDE_PUBLIC_PAGE).is(false))));
			}else {
				query.addCriteria(new Criteria().orOperator(
								Criteria.where(KEY_HIDE_PUBLIC_PAGE).exists(false),
								Criteria.where(KEY_HIDE_PUBLIC_PAGE).is(false)));
			}
		} else {
			if(collectionName.equals(AGENT_SETTINGS_COLLECTION)) {
				operations.add(Aggregation.match(new Criteria().orOperator(Criteria.where(KEY_HIDDEN_SECTION).exists(false),
						Criteria.where(KEY_HIDDEN_SECTION).is(false))));
				operations
						.add(Aggregation.match(new Criteria().orOperator(Criteria.where(KEY_HIDE_PUBLIC_PAGE).exists(false),
								Criteria.where(KEY_HIDE_PUBLIC_PAGE).is(false))));
			}else {
				operations
				.add(Aggregation.match(new Criteria().orOperator(Criteria.where(KEY_HIDE_PUBLIC_PAGE).exists(false),
						Criteria.where(KEY_HIDE_PUBLIC_PAGE).is(false))));
			}
			
		}

		// add near query only if lat and lng is given
		if (checkLoc) {
			// get based on distance
			// give lat and lng
			Point point = new Point(advancedSearchVO.getNearLocation().lng, advancedSearchVO.getNearLocation().lat);
			NearQuery nearQuery = NearQuery.near(point)
					.maxDistance(advancedSearchVO.getDistanceCriteria(), Metrics.MILES).spherical(true).num(2000000000);
			nearQuery.query(query);
			operations.add(Aggregation.geoNear(nearQuery, KEY_DISTANCE_FIELD));
			isLocationSearch = true;
		}

		// sort Criteria where default is best match
		operations.add(getSortByAggOperation(advancedSearchVO.getSortBy(), isLocationSearch, isTextSearch));
		operations.add(Aggregation.group().count().as("count"));

		aggregation = new TypedAggregation<>(OrganizationUnitSettings.class, operations);
		AggregationResults<OrganizationUnitSettings> result = mongoTemplate.aggregate(aggregation, collectionName,
				OrganizationUnitSettings.class);
		@SuppressWarnings ( "unchecked") List<BasicDBObject> shares = (List<BasicDBObject>) result.getRawResults()
	            .get( "result" );
	        long count = 0;
	        if ( shares != null && shares.size() != 0 ) {
	        	count = (int) shares.get( CommonConstants.INITIAL_INDEX ).get( "count" );
	        }
		return count;

	}
    
    @Override
    public List<Long> fetchCompaniesWithHiddenSection()
    {
        LOG.debug( "Fetch companies with hidden section ");
        Query query = new Query();

        query.addCriteria( Criteria.where( KEY_ACCOUNT_DISABLED ).is( false ) );

        query.addCriteria( Criteria.where( KEY_STATUS )
            .nin( Arrays.asList( CommonConstants.STATUS_DELETED_MONGO, CommonConstants.STATUS_INCOMPLETE_MONGO ) ) );
        
        query.addCriteria(Criteria.where(KEY_DEFAULT_BY_SYSTEM).is(false));
        
        query.addCriteria(Criteria.where(KEY_HIDDEN_SECTION).is(true));
        
        query.fields().include(KEY_IDEN);

        List<OrganizationUnitSettings> settingsList = mongoTemplate.find( query, OrganizationUnitSettings.class,
            COMPANY_SETTINGS_COLLECTION );
        List<Long> companyIdList = new ArrayList<>();
        for(OrganizationUnitSettings setting : settingsList) {
        	companyIdList.add(setting.getIden());
        }
        return companyIdList;
    }
    
    @Override
    public List<Long> fetchActiveUserForCompany(long companyId)
    {
        LOG.debug( "Fetch active user's for companyId : {}",companyId);
        Query query = new Query();

        query.addCriteria( Criteria.where( KEY_ACCOUNT_DISABLED ).is( false ) );

        query.addCriteria( Criteria.where( KEY_STATUS )
            .nin( Arrays.asList( CommonConstants.STATUS_DELETED_MONGO, CommonConstants.STATUS_INCOMPLETE_MONGO ) ) );
        
        query.addCriteria(Criteria.where(KEY_DEFAULT_BY_SYSTEM).is(false));
        
        query.addCriteria(Criteria.where(KEY_COMPANY_ID).is(companyId));
        
        query.fields().include(KEY_IDEN);
                

        List<OrganizationUnitSettings> settingsList = mongoTemplate.find( query, OrganizationUnitSettings.class,
            AGENT_SETTINGS_COLLECTION );
        List<Long> userIdList = new ArrayList<>();
        for(OrganizationUnitSettings setting : settingsList) {
        	userIdList.add(setting.getIden());
        }
        return userIdList;
    }

    
}