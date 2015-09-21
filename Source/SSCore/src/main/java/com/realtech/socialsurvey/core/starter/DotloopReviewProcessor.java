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
import com.realtech.socialsurvey.core.entities.CRMInfo;
import com.realtech.socialsurvey.core.entities.DotLoopCrmInfo;
import com.realtech.socialsurvey.core.entities.FeedIngestionEntity;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SocialMediaTokens;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.ZillowToken;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.integration.dotloop.DotloopIntegrationApi;
import com.realtech.socialsurvey.core.integration.dotloop.DotloopIntergrationApiBuilder;
import com.realtech.socialsurvey.core.integration.zillow.ZillowIntegrationApi;
import com.realtech.socialsurvey.core.integration.zillow.ZillowIntergrationApiBuilder;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;


/**
 * Ingester for social feed
 */
@Component ( "dotloopreviewprocessor")
public class DotloopReviewProcessor extends QuartzJobBean
{

    private static final Logger LOG = LoggerFactory.getLogger( ZillowReviewProcessor.class );


    private DotloopIntergrationApiBuilder dotloopintegrationApiBuilder;

    private UserManagementService userManagementService;

    private SurveyHandler surveyHandler;

    private OrganizationManagementService organizationManagementService;


    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext )
    {
        LOG.info( "Executing dotloop review processor" );
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        List<User> userList = userManagementService.getAllActiveUsers();
        for ( User user : userList ) {
            LOG.debug( "Fetching company settings for user " + user.getEmailId() );
            OrganizationUnitSettings organizationUnitSettings = null;
            try {
                organizationUnitSettings = organizationManagementService.getCompanySettings( user );
            } catch ( InvalidInputException e ) {

            }

            if ( organizationUnitSettings != null ) {
                CRMInfo crmInfo = organizationUnitSettings.getCrm_info();
                if ( crmInfo != null ) {
                    if ( crmInfo.getCrm_source().equalsIgnoreCase( CommonConstants.CRM_SOURCE_DOTLOOP ) ) {
                        DotLoopCrmInfo dotLoop = (DotLoopCrmInfo) crmInfo;
                        String apiKey = dotLoop.getApi();
                        if ( apiKey != null && !apiKey.isEmpty() ) {
                            LOG.debug( "API key is " + apiKey );
                            fetchReviewfromDotloop( apiKey, organizationUnitSettings );
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


    @SuppressWarnings ( "unchecked")
    public void fetchReviewfromDotloop( String apiKey, OrganizationUnitSettings unitSettings )
    {
        DotloopIntegrationApi dotloopIntegrationApi = dotloopintegrationApiBuilder.getDotloopIntegrationApi();
        if ( dotloopIntegrationApi != null ) {
            String authorizationHeader = CommonConstants.AUTHORIZATION_HEADER + apiKey;
            Response dotloopResponse = dotloopIntegrationApi.fetchdotloopProfiles( apiKey );
            String responseString = null;
            if ( dotloopResponse != null ) {
                responseString = new String( ( (TypedByteArray) dotloopResponse.getBody() ).getBytes() );
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
            }
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
        dotloopintegrationApiBuilder = (DotloopIntergrationApiBuilder) jobMap.get( "dotloopintegrationApiBuilder" );
        surveyHandler = (SurveyHandler) jobMap.get( "surveyHandler" );
        organizationManagementService = (OrganizationManagementService) jobMap.get( "organizationManagementService" );
        userManagementService = (UserManagementService) jobMap.get( "userManagementService" );

    }
}