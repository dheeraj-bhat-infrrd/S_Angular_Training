package com.realtech.socialsurvey.api.controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.realtech.socialsurvey.api.exceptions.SSApiException;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.WidgetTemplateConstants;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.widget.WidgetConfiguration;
import com.realtech.socialsurvey.core.entities.widget.WidgetConfigurationRequest;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileNotFoundException;
import com.realtech.socialsurvey.core.services.widget.WidgetManagementService;
import com.realtech.socialsurvey.core.utils.FileOperations;

import io.swagger.annotations.ApiOperation;
import retrofit.http.Query;


@RestController
@RequestMapping ( "/v1")
public class WidgetApiController
{
    private static final Logger LOG = LoggerFactory.getLogger( WidgetApiController.class );


    @Autowired
    private WidgetManagementService widgetManagementService;


    @Autowired
    private ProfileManagementService profileManagementService;

    @Autowired
    private OrganizationManagementService organizationManagementService;

    @Autowired
    private FileOperations fileOperations;


    @ResponseBody
    @RequestMapping ( value = "/savewidgetconfiguration", method = RequestMethod.POST)
    @ApiOperation ( value = "save widget configuration")
    public String saveWidgetConfiguration( @Query ( "entityId") long entityId, @Query ( "entityType") String entityType,
        @Query ( "userId") long userId, @RequestBody WidgetConfigurationRequest widgetConfigurationRequest )
    {
        LOG.info( "Method saveWidgetConfiguration() started to store widget configuration from ss-api." );
        try {
            widgetManagementService.saveWidgetConfigurationForEntity( entityId, entityType, userId,
                widgetConfigurationRequest );
            return "Widget Configuration Successfully Updated";
        } catch ( InvalidInputException e ) {
            LOG.error( "could not save widget configuration", e );
            return e.getMessage();
        }
    }


    @ResponseBody
    @RequestMapping ( value = "/getdefaultwidgetconfiguration", method = RequestMethod.GET)
    @ApiOperation ( value = "get Default widget configuration")
    public WidgetConfiguration getDefaultWidgetConfiguration( @Query ( "entityId") long entityId,
        @Query ( "entityType") String entityType ) throws SSApiException
    {
        LOG.info( "Method getDefaultWidgetConfiguration() started to get default widget configuration from ss-api." );
        try {
            return widgetManagementService.getDefaultWidgetConfiguration(
                organizationManagementService.getEntitySettings( entityId, entityType ), entityType );
        } catch ( InvalidInputException e ) {
            LOG.error( "could not get default widget configuration", e );
            throw new SSApiException( "could not get default widget configuration", e );
        }
    }


    @ResponseBody
    @RequestMapping ( value = "/getwidgetreviews", method = RequestMethod.GET)
    public ResponseEntity<?> getWidgetReviews( @Query ( "profileName") String profileName,
        @Query ( "companyProfileName") String companyProfileName, @Query ( "profileLevel") String profileLevel,
        @Query ( "startScore") double startScore, @Query ( "limitScore") double limitScore,
        @Query ( "startIndex") int startIndex, @Query ( "numOfRows") int numOfRows,
        @Query ( "fetchAbusive") boolean fetchAbusive, @Query ( "startDateStr") String startDateStr,
        @Query ( "endDateStr") String endDateStr, @Query ( "sortCriteria") String sortCriteria,
        @Query ( "surveySourcesStr") String surveySourcesStr, @Query ( "order") String order ) throws SSApiException
    {
        LOG.info( "Method getWidgetReviews() started to store widget configuration from ss-api." );
        try {
            OrganizationUnitSettings unitSettings = getEntitySettingsforProfile( profileLevel, profileName,
                companyProfileName );

            Date startDate = null;
            Date endDate = null;

            if ( StringUtils.isNotEmpty( startDateStr ) ) {
                startDate = new SimpleDateFormat( "MM/dd/yyyy" ).parse( startDateStr );
            }

            if ( StringUtils.isNotEmpty( endDateStr ) ) {
                endDate = new SimpleDateFormat( "MM/dd/yyyy" ).parse( endDateStr );
            }

            List<String> surveySourcesList = new ArrayList<>();
            if ( surveySourcesStr != null && !surveySourcesStr.isEmpty() ) {
                String[] sourceList = surveySourcesStr.split( "," );

                for ( int i = 0; i < sourceList.length; i++ ) {
                    if ( StringUtils.equals( sourceList[i], "SocialSurvey" ) ) {
                        surveySourcesList.addAll( CommonConstants.CRM_UNVERIFIED_SOURCES );
                    } else if ( StringUtils.equals( sourceList[i], "SocialSurvey Verified" ) ) {
                        surveySourcesList.addAll( Arrays.asList( CommonConstants.CRM_INFO_SOURCE_ENCOMPASS,
                            CommonConstants.CRM_SOURCE_LONEWOLF, CommonConstants.CRM_SOURCE_DOTLOOP,
                            CommonConstants.CRM_INFO_SOURCE_FTP, CommonConstants.CRM_INFO_SOURCE_API ) );
                    } else if ( StringUtils.equals( sourceList[i], "Zillow" ) ) {
                        surveySourcesList.add( CommonConstants.SURVEY_SOURCE_ZILLOW );
                    } else if ( StringUtils.equals( sourceList[i], "Facebook" ) ) {
                        surveySourcesList.add( "Facebook" );
                    } else if ( StringUtils.equals( sourceList[i], "LinkedIn" ) ) {
                        surveySourcesList.add( "LinkedIn" );
                    } else if ( StringUtils.equals( sourceList[i], "Google" ) ) {
                        surveySourcesList.add( "Google" );
                    }
                }
            }

            List<SurveyDetails> surveyDetails = profileManagementService.getReviews( unitSettings.getIden(), startScore,
                limitScore, startIndex, numOfRows, profileLevel, fetchAbusive, startDate, endDate, sortCriteria,
                surveySourcesList, order, true );
            return new ResponseEntity<>( surveyDetails, HttpStatus.OK );
        } catch ( InvalidInputException | ParseException e ) {
            LOG.error( "Unable to get widget reviews.", e );
            throw new SSApiException( "Unable to get widget reviews.", e );
        }
    }


    @ResponseBody
    @RequestMapping ( value = "/getwidgetdetails", method = RequestMethod.GET)
    public ResponseEntity<?> getWidgetDetails( @Query ( "profileName") String profileName,
        @Query ( "profileLevel") String profileLevel, @Query ( "companyProfileName") String companyProfileName,
        @Query ( "hideHistory") boolean hideHistory ) throws SSApiException
    {
        LOG.info( "Method getWidgetDetails() started to store widget configuration from ss-api." );
        try {
            OrganizationUnitSettings unitSettings = getEntitySettingsforProfile( profileLevel, profileName,
                companyProfileName );
            WidgetConfiguration widgetConfiguration = widgetManagementService
                .getWidgetConfigurationForEntity( unitSettings.getIden(), profileLevel, true );
            long oneStar = profileManagementService.getSimpleReviewsCount( unitSettings.getIden(), 0, 2, profileLevel, false );
            long twoStar = profileManagementService.getSimpleReviewsCount( unitSettings.getIden(), 2, 3, profileLevel, false );
            long threeStar = profileManagementService.getSimpleReviewsCount( unitSettings.getIden(), 3, 4, profileLevel,
                false );
            long fourStar = profileManagementService.getSimpleReviewsCount( unitSettings.getIden(), 4, 5, profileLevel, false );
            long fiveStar = profileManagementService.getSimpleReviewsCount( unitSettings.getIden(), 5, 5.1, profileLevel,
                false );
            double averageRating = profileManagementService.getAverageRatings( unitSettings.getIden(), profileLevel, false );
            List<String> availableSources = widgetManagementService.getListOfAvailableSources( profileLevel,
                unitSettings.getIden() );

            if ( hideHistory ) {
                widgetConfiguration.setHistory( null );
            }

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put( "widgetConfiguration", widgetConfiguration );
            responseMap.put( "oneStar", oneStar );
            responseMap.put( "twoStar", twoStar );
            responseMap.put( "threeStar", threeStar );
            responseMap.put( "fourStar", fourStar );
            responseMap.put( "fiveStar", fiveStar );
            responseMap.put( "averageRating", averageRating );
            responseMap.put( "availableSources", availableSources );


            return new ResponseEntity<>( responseMap, HttpStatus.OK );
        } catch ( InvalidInputException e ) {
            LOG.error( "Unable to get widget details.", e );
            throw new SSApiException( "Unable to get widget details.", e );
        }
    }


    private OrganizationUnitSettings getEntitySettingsforProfile( String profileLevel, String profileName,
        String companyProfileName ) throws InvalidInputException
    {
        OrganizationUnitSettings unitSettings = new OrganizationUnitSettings();
        try {
            if ( profileLevel.equals( CommonConstants.PROFILE_LEVEL_COMPANY ) ) {
                unitSettings = profileManagementService.getCompanyProfileByProfileName( profileName );
            } else if ( profileLevel.equals( CommonConstants.PROFILE_LEVEL_INDIVIDUAL ) ) {
                unitSettings = profileManagementService.getIndividualByProfileName( profileName );
            } else if ( profileLevel.equalsIgnoreCase( CommonConstants.PROFILE_LEVEL_BRANCH ) ) {
                unitSettings = profileManagementService.getBranchSettingsByProfileName( companyProfileName, profileName );
            } else if ( profileLevel.equalsIgnoreCase( CommonConstants.PROFILE_LEVEL_REGION ) ) {
                unitSettings = profileManagementService.getRegionSettingsByProfileName( companyProfileName, profileName );
            } else {
                throw new InvalidInputException( "Invalid entity type passed." );
            }
        } catch ( InvalidInputException | ProfileNotFoundException | NoRecordsFetchedException e ) {
            LOG.error( "Either an invalid profile type was passed or no records were found.", e );
            throw new InvalidInputException( "Either an invalid profile type was passed or no records were found." );
        }
        return unitSettings;
    }


    @RequestMapping ( value = "/widget/scripts", method = RequestMethod.GET)
    @ApiOperation ( value = "get widget script template")
    public List<String> getWidgetScript( @Query ( "scriptType") String scriptType ) throws SSApiException
    {
        LOG.info( "Method getWidgetScript() started to store widget configuration from ss-api." );
        try {

            List<String> widgetScriptList = new ArrayList<>();

            if ( CommonConstants.WIDGET_SCRIPT_TYPE_PAF.equals( scriptType ) ) {
                widgetScriptList.add( fileOperations.getContentFromFile(
                    WidgetTemplateConstants.WIDGET_TEMPLATES_FOLDER + WidgetTemplateConstants.WIDGET_PLACE_AND_FORGET ) );
            } else if ( CommonConstants.WIDGET_SCRIPT_TYPE_CC.equals( scriptType ) ) {
                widgetScriptList.add( fileOperations.getContentFromFile(
                    WidgetTemplateConstants.WIDGET_TEMPLATES_FOLDER + WidgetTemplateConstants.WIDGET_CUSTOM_CONTAINER ) );
            } else if ( CommonConstants.WIDGET_SCRIPT_TYPE_JI.equals( scriptType ) ) {
                widgetScriptList.add( fileOperations.getContentFromFile(
                    WidgetTemplateConstants.WIDGET_TEMPLATES_FOLDER + WidgetTemplateConstants.WIDGET_JAVASCRIPT_IFRAME ) );
            } else {
                widgetScriptList.add( fileOperations.getContentFromFile(
                    WidgetTemplateConstants.WIDGET_TEMPLATES_FOLDER + WidgetTemplateConstants.WIDGET_PLACE_AND_FORGET ) );
                widgetScriptList.add( fileOperations.getContentFromFile(
                    WidgetTemplateConstants.WIDGET_TEMPLATES_FOLDER + WidgetTemplateConstants.WIDGET_CUSTOM_CONTAINER ) );
                widgetScriptList.add( fileOperations.getContentFromFile(
                    WidgetTemplateConstants.WIDGET_TEMPLATES_FOLDER + WidgetTemplateConstants.WIDGET_JAVASCRIPT_IFRAME ) );
            }

            return widgetScriptList;
        } catch ( Exception e ) {
            LOG.error( "could not get widget script", e );
            throw new SSApiException( "could not get widget script", e );
        }
    }
}
