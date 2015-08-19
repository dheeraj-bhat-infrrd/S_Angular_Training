package com.realtech.socialsurvey.core.starter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SocialMediaTokens;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.entities.ZillowToken;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.integration.zillow.ZillowIntegrationApi;
import com.realtech.socialsurvey.core.integration.zillow.ZillowIntergrationApiBuilder;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;


/**
 * Ingester for social feed
 */
@Component ( "zillowreviewprocessor")
public class ZillowReviewProcessor extends QuartzJobBean
{

    private static final Logger LOG = LoggerFactory.getLogger( ZillowReviewProcessor.class );

    private OrganizationManagementService organizationManagementService;

    private ZillowIntergrationApiBuilder zillowIntegrationApiBuilder;

    private SurveyHandler surveyHandler;

    private UserManagementService userManagementService;

    private String zwsId;


    @SuppressWarnings ( "unchecked")
    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext )
    {
        LOG.info( "Executing zillow review processor" );
        String responseString = null;
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        ZillowIntegrationApi zillowIntegrationApi = zillowIntegrationApiBuilder.getZellowIntegrationApi();
        List<User> users = userManagementService.getAllActiveUsers();
        for ( User user : users ) {
            OrganizationUnitSettings unitSettings = null;
            try {
                unitSettings = organizationManagementService.getCompanySettings( user );
            } catch ( InvalidInputException e1 ) {
                LOG.error( "Exception caught " + e1.getMessage() );
            }
            if ( unitSettings != null ) {
                SocialMediaTokens socialMediaTokens = unitSettings.getSocialMediaTokens();
                if ( socialMediaTokens != null ) {
                    ZillowToken zillowToken = socialMediaTokens.getZillowToken();
                    if ( zillowToken != null ) {
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
                                                        .getSurveyDetailsBySourceId( sourceId );
                                                    if ( surveyDetails == null ) {
                                                        surveyDetails = new SurveyDetails();
                                                        surveyDetails.setAgentId( user.getUserId() );
                                                        surveyDetails.setAgentName( user.getFirstName() + " "
                                                            + user.getLastName() );
                                                        surveyDetails.setCompleteProfileUrl( (String) review
                                                            .get( "reviewerLink" ) );
                                                        surveyDetails.setCustomerFirstName( (String) review.get( "reviewer" ) );
                                                        surveyDetails.setReview( (String) review.get( "reviewSummary" ) );
                                                        surveyDetails.setEditable( false );
                                                        surveyDetails
                                                            .setScore( Double.valueOf( (String) review.get( "rating" ) ) );
                                                        surveyDetails.setSource( CommonConstants.SURVEY_SOURCE_ZILLOW );
                                                        surveyDetails.setSourceId( sourceId );
                                                        surveyDetails.setModifiedOn( new Date( System.currentTimeMillis() ) );
                                                        surveyDetails.setCreatedOn( new Date( System.currentTimeMillis() ) );
                                                        for ( UserProfile userProfile : user.getUserProfiles() ) {
                                                            if ( userProfile.getAgentId() == user.getUserId() ) {
                                                                surveyDetails.setBranchId( userProfile.getBranchId() );
                                                                surveyDetails.setRegionId( userProfile.getRegionId() );
                                                            }
                                                        }
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

            }
        }
    }


    private Map<String, Object> convertJsonStringToMap( String jsonString ) throws JsonParseException, JsonMappingException,
        IOException
    {
        Map<String, Object> map = new ObjectMapper().readValue( jsonString, new TypeReference<HashMap<String, Object>>() {} );
        return map;
    }


    private void initializeDependencies( JobDataMap jobMap )
    {
        organizationManagementService = (OrganizationManagementService) jobMap.get( "organizationManagementService" );
        userManagementService = (UserManagementService) jobMap.get( "userManagementService" );
        zillowIntegrationApiBuilder = (ZillowIntergrationApiBuilder) jobMap.get( "zillowIntegrationApiBuilder" );
        surveyHandler = (SurveyHandler) jobMap.get( "surveyHandler" );
        zwsId = (String) jobMap.get( "zws-id" );
    }
}