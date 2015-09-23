package com.realtech.socialsurvey.core.starter;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import com.realtech.socialsurvey.core.entities.CRMInfo;
import com.realtech.socialsurvey.core.entities.CompanyDotloopProfileMapping;
import com.realtech.socialsurvey.core.entities.DotLoopCrmInfo;
import com.realtech.socialsurvey.core.entities.LoopProfileMapping;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.integration.dotloop.DotloopIntegrationApi;
import com.realtech.socialsurvey.core.integration.dotloop.DotloopIntergrationApiBuilder;
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


    private DotloopIntergrationApiBuilder dotloopIntegrationApiBuilder;

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


    private List<Map<String, String>> convertJsonStringToList( String jsonString ) throws JsonParseException,
        JsonMappingException, IOException
    {
        List<Map<String, String>> listofMap = new ObjectMapper().readValue( jsonString,
            new TypeReference<List<Map<String, String>>>() {} );
        return listofMap;
    }


    private List<Object> convertJsonStringToListObject( String jsonString ) throws JsonParseException, JsonMappingException,
        IOException
    {
        List<Object> listofMap = new ObjectMapper().readValue( jsonString, new TypeReference<List<Object>>() {} );
        return listofMap;
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

        DotloopIntegrationApi dotloopIntegrationApi = dotloopIntegrationApiBuilder.getDotloopIntegrationApi();
        if ( dotloopIntegrationApi != null ) {
            String authorizationHeader = CommonConstants.AUTHORIZATION_HEADER + apiKey;
            Response dotloopResponse = dotloopIntegrationApi.fetchdotloopProfiles( authorizationHeader );
            String responseString = null;
            if ( dotloopResponse != null ) {
                responseString = new String( ( (TypedByteArray) dotloopResponse.getBody() ).getBytes() );
            }
            if ( responseString != null ) {
                List<Map<String, String>> profileList = null;
                try {
                    profileList = convertJsonStringToList( responseString );
                } catch ( JsonParseException e ) {
                    LOG.error( "Exception caught " + e.getMessage() );
                } catch ( JsonMappingException e ) {
                    LOG.error( "Exception caught " + e.getMessage() );
                } catch ( IOException e ) {
                    LOG.error( "Exception caught " + e.getMessage() );
                }

                if ( profileList != null ) {
                    for ( Map<String, String> profile : profileList ) {

                        LOG.debug( "looping profile with statusId as 4 " );
                        String profileId = profile.get( CommonConstants.DOTLOOP_PROFILE_ID );
                        boolean active = Boolean.valueOf( profile.get( CommonConstants.DOTLOOP_PROFILE_ACTIVE ) );
                        String profileEmailAddress = profile.get( CommonConstants.DOTLOOP_PROFILE_EMAIL_ADDRESS );
                        String profileName = profile.get( CommonConstants.DOTLOOP_PROFILE_NAME );

                        CompanyDotloopProfileMapping companyDotloopProfileMapping = organizationManagementService
                            .getCompanyDotloopMappingByCompanyIdAndProfileId( unitSettings.getIden(), profileId );

                        if ( companyDotloopProfileMapping == null ) {
                            companyDotloopProfileMapping = new CompanyDotloopProfileMapping();
                            companyDotloopProfileMapping.setActive( active );
                            companyDotloopProfileMapping.setProfileEmailAddress( profileEmailAddress );
                            companyDotloopProfileMapping.setProfileName( profileName );
                            companyDotloopProfileMapping.setProfileId( profileId );
                            companyDotloopProfileMapping.setCompanyId( unitSettings.getIden() );
                            companyDotloopProfileMapping = organizationManagementService
                                .saveCompanyDotLoopProfileMapping( companyDotloopProfileMapping );


                        }
                        companyDotloopProfileMapping = organizationManagementService
                            .getCompanyDotloopMappingByProfileId( profileId );

                        if ( companyDotloopProfileMapping.isActive() ) {

                            LOG.debug( "The profile is active  " + profileId );
                            Response loopResponse = null;
                            int batchNumber = 1;
                            String loopResponseString = null;
                            List<LoopProfileMapping> dotloopProfileMappingList = new ArrayList<LoopProfileMapping>();
                            do {
                                try {
                                    loopResponse = dotloopIntegrationApi.fetchClosedProfiles( authorizationHeader, profileId,
                                        batchNumber );
                                } catch ( Exception e ) {
                                    LOG.error( "exception caught while fetching this profile, hence marking it as inactive ", e );
                                    loopResponse = null;
                                }
                                if ( loopResponse != null ) {
                                    loopResponseString = new String( ( (TypedByteArray) loopResponse.getBody() ).getBytes() );
                                }

                                if ( loopResponseString != null ) {

                                    List<Object> loopMapList = null;
                                    try {
                                        loopMapList = convertJsonStringToListObject( loopResponseString );
                                    } catch ( JsonParseException e ) {
                                        LOG.error( "Exception caught " + e.getMessage() );
                                    } catch ( JsonMappingException e ) {
                                        LOG.error( "Exception caught " + e.getMessage() );
                                    } catch ( IOException e ) {
                                        LOG.error( "Exception caught " + e.getMessage() );
                                    }

                                    if ( loopMapList != null ) {
                                        for ( Object loopDetails : loopMapList ) {
                                            loopDetails = (Map<Object, LinkedHashMap<String, String>>) loopDetails;

                                            String loopId = String.valueOf( ( (LinkedHashMap<String, Object>) loopDetails )
                                                .get( CommonConstants.DOTLOOP_PROFILE_LOOP_ID ) );
                                            String loopviewId = String.valueOf( ( (LinkedHashMap<String, Object>) loopDetails )
                                                .get( CommonConstants.DOTLOOP_PROFILE_LOOP_VIEW_ID ) );
                                            LoopProfileMapping loopProfileMapping = new LoopProfileMapping();
                                            loopProfileMapping.setProfileId( profileId );
                                            loopProfileMapping.setProfileLoopId( loopId );
                                            loopProfileMapping.setProfileViewId( loopviewId );
                                            loopProfileMapping.setLoopClosedTime( convertEpochDateToTimestamp() );
                                            dotloopProfileMappingList.add( loopProfileMapping );

                                        }
                                    } else {
                                        LOG.debug( "Marking profile as inactive" );
                                        companyDotloopProfileMapping.setActive( false );
                                        organizationManagementService
                                            .updateCompanyDotLoopProfileMapping( companyDotloopProfileMapping );
                                        break;
                                    }

                                    if ( loopResponseString.equalsIgnoreCase( "[]" ) ) {
                                        break;
                                    }
                                    batchNumber++;
                                } else {
                                    LOG.debug( "Marking profile as inactive" );
                                    companyDotloopProfileMapping.setActive( false );
                                    organizationManagementService
                                        .updateCompanyDotLoopProfileMapping( companyDotloopProfileMapping );
                                    break;

                                }
                            } while ( true );

                            List<LoopProfileMapping> loopProfileMappingList = organizationManagementService
                                .getLoopsByProfile( profileId );
                            if ( loopProfileMappingList == null || loopProfileMappingList.isEmpty() ) {
                                LOG.info( "Fetching records for the first time " );
                                for ( LoopProfileMapping loopDetails : dotloopProfileMappingList ) {
                                    organizationManagementService.saveLoopsForProfile( loopDetails );
                                }
                            } else {
                                for ( LoopProfileMapping loopDetails : dotloopProfileMappingList ) {
                                    boolean profileFound = false;
                                    for ( LoopProfileMapping loopProfileMapping : loopProfileMappingList ) {
                                        if ( loopDetails.getProfileLoopId().equalsIgnoreCase(
                                            loopProfileMapping.getProfileLoopId() ) ) {
                                            profileFound = true;
                                            break;
                                        }
                                    }
                                    if ( !profileFound ) {
                                        loopDetails.setLoopClosedTime( new Timestamp( System.currentTimeMillis() ) );
                                        organizationManagementService.saveLoopsForProfile( loopDetails );
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
    }


    private Timestamp convertEpochDateToTimestamp()
    {
        String string = "January 2, 1970";
        DateFormat format = new SimpleDateFormat( "MMMM d, yyyy", Locale.ENGLISH );
        Date date = null;
        ;
        try {
            date = format.parse( string );
        } catch ( ParseException e ) {
            LOG.error( "Exception caught ", e );
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime( date );
        cal.set( Calendar.MILLISECOND, 0 );

        return ( new java.sql.Timestamp( cal.getTimeInMillis() ) );
    }


    private void initializeDependencies( JobDataMap jobMap )
    {
        dotloopIntegrationApiBuilder = (DotloopIntergrationApiBuilder) jobMap.get( "dotloopIntegrationApiBuilder" );
        surveyHandler = (SurveyHandler) jobMap.get( "surveyHandler" );
        organizationManagementService = (OrganizationManagementService) jobMap.get( "organizationManagementService" );
        userManagementService = (UserManagementService) jobMap.get( "userManagementService" );

    }
}