package com.realtech.socialsurvey.core.starter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.FeedIngestionEntity;
import com.realtech.socialsurvey.core.entities.SocialMediaTokens;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.ZillowToken;
import com.realtech.socialsurvey.core.integration.zillow.ZillowIntegrationApi;
import com.realtech.socialsurvey.core.integration.zillow.ZillowIntergrationApiBuilder;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;


/**
 * Ingester for social feed
 */
@Component ( "zillowreviewprocessor")
public class ZillowReviewProcessor extends QuartzJobBean
{

    private static final Logger LOG = LoggerFactory.getLogger( ZillowReviewProcessor.class );


    private ZillowIntergrationApiBuilder zillowIntegrationApiBuilder;

    private SurveyHandler surveyHandler;

    private OrganizationManagementService organizationManagementService;

    private String zwsId;

    private int BATCH_SIZE = 50;


    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext )
    {
        LOG.info( "Executing zillow review processor" );
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        startZillowFeedIngestion( MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );
        startZillowFeedIngestion( MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION );
        startZillowFeedIngestion( MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION );
        startZillowFeedIngestion( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );
    }


    public void startZillowFeedIngestion( String collectionName )
    {
        LOG.info( "Kick starting company feed ingestion" );
        List<FeedIngestionEntity> tokens = null;
        int currentBatch = 0;

        LOG.debug( "Getting a list of entities with access tokens" );
        do {
            tokens = organizationManagementService.fetchSocialMediaTokens( collectionName, currentBatch, BATCH_SIZE );
            if ( tokens == null || tokens.size() == 0 ) {
                LOG.debug( "No more tokens for " + collectionName );
                break;
            } else {
                LOG.debug( "Found " + ( currentBatch + tokens.size() ) + " tokens for " + collectionName + " till now." );
                // get individual entity
                for ( FeedIngestionEntity ingestionEntity : tokens ) {
                    try {
                        fetchFeedFromZillow( ingestionEntity, collectionName );
                    } catch ( Exception e ) {
                        LOG.warn( "Exception for " + collectionName + " and " + ingestionEntity.getIden() );
                    }
                }

                if ( tokens.size() < BATCH_SIZE ) {
                    LOG.debug( "No more tokens left for " + collectionName + ". Breaking from loop." );
                    break;
                }
                LOG.debug( "Fetching more tokens from " + collectionName );
                currentBatch += BATCH_SIZE;
            }
        } while ( true );
    }


    private Map<String, Object> convertJsonStringToMap( String jsonString ) throws JsonParseException, JsonMappingException,
        IOException
    {
        Map<String, Object> map = new ObjectMapper().readValue( jsonString, new TypeReference<HashMap<String, Object>>() {} );
        return map;
    }


    @SuppressWarnings ( "unchecked")
    public void fetchFeedFromZillow( FeedIngestionEntity ingestionEntity, String collectionName )
    {
        LOG.debug( "Fetching social feed for " + collectionName + " with iden: " + ingestionEntity.getIden() );

        if ( ingestionEntity != null && ingestionEntity.getSocialMediaTokens() != null ) {
            LOG.debug( "Starting to fetch the feed." );

            SocialMediaTokens token = ingestionEntity.getSocialMediaTokens();
            if ( token != null ) {
                if ( token.getZillowToken() != null ) {
                    ZillowIntegrationApi zillowIntegrationApi = zillowIntegrationApiBuilder.getZellowIntegrationApi();
                    String responseString = null;
                    ZillowToken zillowToken = token.getZillowToken();
                    String zillowScreenName = zillowToken.getZillowScreenName();
                    Response response = zillowIntegrationApi.fetchZillowReviewsByScreennameWithMaxCount( zwsId,
                        zillowScreenName );
                    if ( response != null ) {
                        responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
                    }
                    if ( responseString != null ) {
                        Map<String, Object> map = null;
                        try {
                            map = convertJsonStringToMap( responseString );
                        } catch ( JsonParseException e ) {
                            LOG.error( "Exception caught " + e.getMessage() );
                        } catch ( JsonMappingException e ) {
                            LOG.error( "Exception caught " + e.getMessage() );
                        } catch ( IOException e ) {
                            LOG.error( "Exception caught " + e.getMessage() );
                        }

                        if ( map != null ) {
                            Map<String, Object> responseMap = new HashMap<String, Object>();
                            Map<String, Object> resultMap = new HashMap<String, Object>();
                            Map<String, Object> proReviews = new HashMap<String, Object>();
                            List<HashMap<String, Object>> reviews = new ArrayList<HashMap<String, Object>>();
                            responseMap = (HashMap<String, Object>) map.get( "response" );
                            if ( responseMap != null ) {
                                resultMap = (HashMap<String, Object>) responseMap.get( "results" );
                                if ( resultMap != null ) {
                                    proReviews = (HashMap<String, Object>) resultMap.get( "proReviews" );
                                    if ( proReviews != null ) {
                                        reviews = (List<HashMap<String, Object>>) proReviews.get( "review" );
                                        if ( reviews != null ) {
                                            for ( HashMap<String, Object> review : reviews ) {
                                                String sourceId = (String) review.get( "reviewURL" );
                                                SurveyDetails surveyDetails = surveyHandler
                                                    .getSurveyDetailsBySourceIdAndMongoCollection( sourceId,
                                                        ingestionEntity.getIden(), collectionName );
                                                if ( surveyDetails == null ) {
                                                    surveyDetails = new SurveyDetails();
                                                    if ( collectionName
                                                        .equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION ) ) {
                                                        surveyDetails.setCompanyId( ingestionEntity.getIden() );
                                                    } else if ( collectionName
                                                        .equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION ) ) {
                                                        surveyDetails.setRegionId( ingestionEntity.getIden() );
                                                    } else if ( collectionName
                                                        .equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION ) ) {
                                                        surveyDetails.setBranchId( ingestionEntity.getIden() );
                                                    } else if ( collectionName
                                                        .equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION ) ) {
                                                        surveyDetails.setAgentId( ingestionEntity.getIden() );
                                                    }
                                                    String createdDate = (String) review.get( "reviewDate" );
                                                    surveyDetails.setCompleteProfileUrl( (String) review.get( "reviewerLink" ) );
                                                    surveyDetails.setCustomerFirstName( (String) review.get( "reviewer" ) );
                                                    surveyDetails.setReview( (String) review.get( "description" ) );
                                                    surveyDetails.setEditable( false );
                                                    surveyDetails.setStage( CommonConstants.SURVEY_STAGE_COMPLETE );
                                                    surveyDetails.setScore( Double.valueOf( (String) review.get( "rating" ) ) );
                                                    surveyDetails.setSource( CommonConstants.SURVEY_SOURCE_ZILLOW );
                                                    surveyDetails.setSourceId( sourceId );
                                                    surveyDetails.setModifiedOn( convertStringToDate( createdDate ) );
                                                    surveyDetails.setCreatedOn( convertStringToDate( createdDate ) );
                                                    surveyDetails.setAgreedToShare( "true" );
                                                    surveyDetails.setAbusive( false );
                                                    surveyHandler.insertSurveyDetails( surveyDetails );
                                                }
                                            }
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
            }
        } else {
            LOG.error( "No social media token present for " + collectionName + " with iden: " + ingestionEntity.getIden() );
        }
    }


    private Date convertStringToDate( String dateString )
    {

        DateFormat format = new SimpleDateFormat( "MM/dd/yyyy", Locale.ENGLISH );
        Date date;
        try {
            date = format.parse( dateString );
        } catch ( ParseException e ) {
            return null;
        }
        return date;
    }


    private void initializeDependencies( JobDataMap jobMap )
    {
        zillowIntegrationApiBuilder = (ZillowIntergrationApiBuilder) jobMap.get( "zillowIntegrationApiBuilder" );
        surveyHandler = (SurveyHandler) jobMap.get( "surveyHandler" );
        organizationManagementService = (OrganizationManagementService) jobMap.get( "organizationManagementService" );
        zwsId = (String) jobMap.get( "zws-id" );
    }
}