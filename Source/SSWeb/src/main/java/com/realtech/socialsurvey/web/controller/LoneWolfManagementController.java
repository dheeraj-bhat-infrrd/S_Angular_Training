package com.realtech.socialsurvey.web.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.LoneWolfClassificationCode;
import com.realtech.socialsurvey.core.entities.LoneWolfCrmInfo;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.exception.BaseRestException;
import com.realtech.socialsurvey.core.exception.InternalServerException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.LoneWolfErrorCode;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.lonewolf.LoneWolfIntegrationService;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.web.common.JspResolver;
import com.realtech.socialsurvey.web.rest.AbstractController;

import retrofit.mime.TypedByteArray;


@Controller
public class LoneWolfManagementController extends AbstractController
{
    private static final Logger LOG = LoggerFactory.getLogger( LoneWolfManagementController.class );

    private SessionHelper sessionHelper;
    private OrganizationManagementService organizationManagementService;
    private MessageUtils messageUtils;
    private LoneWolfIntegrationService loneWolfIntegrationService;

    @Value ( "${LONEWOLF_API_TOKEN}")
    private String apiToken;

    @Value ( "${LONEWOLF_SECRET_KEY}")
    private String secretKey;


    @Autowired
    public LoneWolfManagementController( SessionHelper sessionHelper,
        OrganizationManagementService organizationManagementService, MessageUtils messageUtils,
        LoneWolfIntegrationService loneWolfIntegrationService )
    {
        this.sessionHelper = sessionHelper;
        this.organizationManagementService = organizationManagementService;
        this.messageUtils = messageUtils;
        this.loneWolfIntegrationService = loneWolfIntegrationService;
    }


    /**
     * Method to enable an Lone Wolf connection
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/enablelonewolfdetails", method = RequestMethod.POST)
    @ResponseBody
    public String enableLoneWolfConnection( Model model, HttpServletRequest request )
    {
        LOG.info( "Updating lonewolf details to 'Enabled'" );
        HttpSession session = request.getSession( false );
        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        User user = sessionHelper.getCurrentUser();
        Long adminUserid = (Long) session.getAttribute( CommonConstants.REALTECH_USER_ID );
        String eventFiredBy = adminUserid != null ? CommonConstants.ADMIN_USER_NAME : String.valueOf( user.getUserId() );
        String message = null;
        OrganizationUnitSettings unitSettings = null;
        String collectionName = "";
        int regionId = 0;
        int branchId = 0;
        int agentId = 0;
        try {
            switch ( entityType ) {
                case CommonConstants.COMPANY_ID:
                    collectionName = MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION;
                    unitSettings = organizationManagementService.getCompanySettings( entityId );
                    break;
                case CommonConstants.REGION_ID:
                    collectionName = MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION;
                    unitSettings = organizationManagementService.getRegionSettings( entityId );
                    regionId = (int) entityId;
                    break;
                case CommonConstants.BRANCH_ID:
                    collectionName = MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION;
                    unitSettings = organizationManagementService.getBranchSettingsDefault( entityId );
                    branchId = (int) entityId;
                    break;
                case CommonConstants.AGENT_ID:
                    collectionName = MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION;
                    unitSettings = organizationManagementService.getAgentSettings( entityId );
                    agentId = (int) entityId;
                    break;
            }
            LoneWolfCrmInfo lonewolfCrmInfo = (LoneWolfCrmInfo) unitSettings.getCrm_info();
            lonewolfCrmInfo.setState( CommonConstants.LONEWOLF_PRODUCTION_STATE );
            organizationManagementService.updateCRMDetailsForAnyUnitSettings( unitSettings, collectionName, lonewolfCrmInfo,
                "com.realtech.socialsurvey.core.entities.LoneWolfCrmInfo" );
            organizationManagementService.logEvent( CommonConstants.LONEWOLF_CONNECTION, CommonConstants.ACTION_ENABLED,
                eventFiredBy, user.getCompany().getCompanyId(), agentId, regionId, branchId );
            message = messageUtils
                .getDisplayMessage( DisplayMessageConstants.LONEWOLF_ENABLE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE )
                .getMessage();
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while saving lonewolf detials. Reason : " + e.getMessage(), e );
            message = messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ).getMessage();
        }
        return message;
    }


    /**
     * Method to disable Lone Wolf connection
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/disablelonewolfdetails", method = RequestMethod.POST)
    @ResponseBody
    public String disableLoneWolfConnection( Model model, HttpServletRequest request )
    {
        LOG.info( "Updating lonewolf details to 'Disabled'" );
        HttpSession session = request.getSession( false );
        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        User user = sessionHelper.getCurrentUser();
        Long adminUserid = (Long) session.getAttribute( CommonConstants.REALTECH_USER_ID );
        String eventFiredBy = adminUserid != null ? CommonConstants.ADMIN_USER_NAME : String.valueOf( user.getUserId() );
        String message = null;
        OrganizationUnitSettings unitSettings = null;
        String collectionName = "";
        int regionId = 0;
        int branchId = 0;
        int agentId = 0;
        try {
            switch ( entityType ) {
                case CommonConstants.COMPANY_ID:
                    collectionName = MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION;
                    unitSettings = organizationManagementService.getCompanySettings( entityId );
                    break;
                case CommonConstants.REGION_ID:
                    collectionName = MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION;
                    unitSettings = organizationManagementService.getRegionSettings( entityId );
                    regionId = (int) entityId;
                    break;
                case CommonConstants.BRANCH_ID:
                    collectionName = MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION;
                    unitSettings = organizationManagementService.getBranchSettingsDefault( entityId );
                    branchId = (int) entityId;
                    break;
                case CommonConstants.AGENT_ID:
                    collectionName = MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION;
                    unitSettings = organizationManagementService.getAgentSettings( entityId );
                    agentId = (int) entityId;
                    break;
            }
            LoneWolfCrmInfo lonewolfCrmInfo = (LoneWolfCrmInfo) unitSettings.getCrm_info();
            lonewolfCrmInfo.setState( CommonConstants.LONEWOLF_DRY_RUN_STATE );
            organizationManagementService.updateCRMDetailsForAnyUnitSettings( unitSettings, collectionName, lonewolfCrmInfo,
                "com.realtech.socialsurvey.core.entities.LoneWolfCrmInfo" );
            organizationManagementService.logEvent( CommonConstants.LONEWOLF_CONNECTION, CommonConstants.ACTION_DISABLED,
                eventFiredBy, user.getCompany().getCompanyId(), agentId, regionId, branchId );
            message = messageUtils
                .getDisplayMessage( DisplayMessageConstants.LONEWOLF_DISABLE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE )
                .getMessage();
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while disabling lonewolf. Reason : " + e.getMessage(), e );
            message = messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ).getMessage();
        }
        return message;
    }


    /**
     * Method to save Lone Wolf connection details
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/savelonewolfdetails", method = RequestMethod.POST)
    @ResponseBody
    public String saveLoneWolfDetails( HttpServletRequest request )
    {
        LOG.info( "Inside method saveLoneWolfDetails " );
        HttpSession session = request.getSession( false );
        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        boolean status = false;
        String message = null;
        try {
            String clientCode = request.getParameter( "lonewolfClient" );
            String state = request.getParameter( "lonewolfState" );
            String transactionStartDateStr = request.getParameter( "transactionStartDate" );

            Date transactionStartDate = null;
            if ( transactionStartDateStr != null && !transactionStartDateStr.isEmpty() ) {
                try {
                    transactionStartDate = new SimpleDateFormat( CommonConstants.DATE_FORMAT ).parse( transactionStartDateStr );
                } catch ( ParseException e ) {
                    throw new InvalidInputException(
                        "ParseException caught in getCompleteSurveyFile() while parsing startDate. Nested exception is ", e );
                }
            }
            if ( StringUtils.isEmpty( clientCode ) ) {
                throw new InvalidInputException( "Client code cannot be empty" );
            }
            if ( StringUtils.isEmpty( state ) || state.equals( CommonConstants.LONEWOLF_DRY_RUN_STATE ) ) {
                state = CommonConstants.LONEWOLF_DRY_RUN_STATE;
            } else {
                state = CommonConstants.LONEWOLF_PRODUCTION_STATE;
            }


            String classificationsJson = request.getParameter( "classifications" );

            TypeToken<List<LoneWolfClassificationCode>> token = new TypeToken<List<LoneWolfClassificationCode>>() {};
            List<LoneWolfClassificationCode> classifications = new Gson().fromJson( classificationsJson, token.getType() );


            LoneWolfCrmInfo loneWolfCrmInfo = new LoneWolfCrmInfo();
            loneWolfCrmInfo.setCrm_source( CommonConstants.CRM_SOURCE_LONEWOLF );
            loneWolfCrmInfo.setClientCode( clientCode );
            loneWolfCrmInfo.setState( state );
            loneWolfCrmInfo.setClassificationCodes( classifications );
            loneWolfCrmInfo.setTransactionStartDate( transactionStartDate );
            OrganizationUnitSettings unitSettings = null;
            String collectionName = "";
            if ( entityType.equalsIgnoreCase( CommonConstants.COMPANY_ID ) ) {
                collectionName = MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION;
                unitSettings = organizationManagementService.getCompanySettings( entityId );
                if ( unitSettings != null ) {
                    loneWolfCrmInfo.setCompanyId( unitSettings.getIden() );
                }
            } else if ( entityType.equalsIgnoreCase( CommonConstants.REGION_ID ) ) {
                collectionName = MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION;
                unitSettings = organizationManagementService.getRegionSettings( entityId );
                if ( unitSettings != null ) {
                    loneWolfCrmInfo.setRegionId( unitSettings.getIden() );
                }
            } else if ( entityType.equalsIgnoreCase( CommonConstants.BRANCH_ID ) ) {
                collectionName = MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION;
                unitSettings = organizationManagementService.getBranchSettingsDefault( entityId );
                if ( unitSettings != null ) {
                    loneWolfCrmInfo.setBranchId( unitSettings.getIden() );
                }
            } else if ( entityType.equalsIgnoreCase( CommonConstants.AGENT_ID ) ) {
                collectionName = MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION;
                unitSettings = organizationManagementService.getAgentSettings( entityId );
                loneWolfCrmInfo.setAgentId( unitSettings.getIden() );
            } else {
                throw new InvalidInputException( "Invalid entity type" );
            }
            organizationManagementService.updateCRMDetailsForAnyUnitSettings( unitSettings, collectionName, loneWolfCrmInfo,
                "com.realtech.socialsurvey.core.entities.LoneWolfCrmInfo" );

            unitSettings.setCrm_info( loneWolfCrmInfo );
            status = true;
            message = messageUtils.getDisplayMessage( DisplayMessageConstants.LONEWOLF_DETAIL_SAVED_SUCCESSFUL,
                DisplayMessageType.SUCCESS_MESSAGE ).getMessage();
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while testing lonewolf detials. Reason : " + e.getMessage(), e );
            message = messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ).getMessage();
        }
        LOG.info( "Inside method saveLoneWolfDetails finished." );

        Map<String, Object> responseMap = new HashMap<String, Object>();
        responseMap.put( "status", status );
        responseMap.put( "message", message );
        String response = new Gson().toJson( responseMap );
        return response;
    }


    /**
     * Method to get the generate report pop up for lone wolf dry run
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/lonedryrun")
    public String dryRun( Model model, HttpServletRequest request )
    {
        LOG.info( "Method to display the generate report popup for lone wolf dry run started" );
        HttpSession session = request.getSession( false );
        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        String emailId = "";
        String noOfDays = "";
        try {
            OrganizationUnitSettings unitSettings = null;
            if ( entityType.equalsIgnoreCase( CommonConstants.COMPANY_ID ) ) {
                unitSettings = organizationManagementService.getCompanySettings( entityId );
            } else if ( entityType.equalsIgnoreCase( CommonConstants.REGION_ID ) ) {
                unitSettings = organizationManagementService.getRegionSettings( entityId );
            } else if ( entityType.equalsIgnoreCase( CommonConstants.BRANCH_ID ) ) {
                unitSettings = organizationManagementService.getBranchSettingsDefault( entityId );
            } else if ( entityType.equalsIgnoreCase( CommonConstants.AGENT_ID ) ) {
                unitSettings = organizationManagementService.getAgentSettings( entityId );
            } else {
                throw new InvalidInputException( "Invalid entity type" );
            }
            if ( unitSettings.getCrm_info() != null
                && unitSettings.getCrm_info().getCrm_source().equals( CommonConstants.CRM_SOURCE_LONEWOLF ) ) {
                LoneWolfCrmInfo loneWolfCrmInfo = (LoneWolfCrmInfo) unitSettings.getCrm_info();
                if ( loneWolfCrmInfo.getEmailAddressForReport() != null
                    && !( loneWolfCrmInfo.getEmailAddressForReport().isEmpty() ) ) {
                    emailId = loneWolfCrmInfo.getEmailAddressForReport();
                }
                if ( loneWolfCrmInfo.getNumberOfDays() > 0 ) {
                    noOfDays = String.valueOf( loneWolfCrmInfo.getNumberOfDays() );
                }
            }
            model.addAttribute( "emailId", emailId );
            model.addAttribute( "NumberOfDays", noOfDays );
        } catch ( Exception e ) {
            LOG.error( "An exception occured while fetching the generate report pop up. Reason :", e );
            return CommonConstants.ERROR;
        }
        LOG.info( "Method to display the generate report popup for dry run finished" );
        return JspResolver.DRY_RUN;
    }


    /**
     * Method to enable the generate report for lone wolf 
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/enablelonewolfreportgeneration", method = RequestMethod.POST)
    @ResponseBody
    public String enableLoneWolfReportGeneration( Model model, HttpServletRequest request )
    {
        LOG.info( "Enabling report generation for lone wolf details" );
        HttpSession session = request.getSession( false );
        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        String message;
        try {
            String numOfDaysStr = request.getParameter( "noOfdays" );
            if ( StringUtils.isEmpty( numOfDaysStr ) ) {
                throw new InvalidInputException( "Number of days cannot be empty" );
            }
            int numOfDays = Integer.parseInt( numOfDaysStr );

            String emailIdForReport = request.getParameter( "reportEmail" );
            if ( StringUtils.isEmpty( emailIdForReport ) ) {
                throw new InvalidInputException( "emailId cannot be empty" );
            }

            OrganizationUnitSettings unitSettings = null;
            String collectionName = "";
            if ( entityType.equalsIgnoreCase( CommonConstants.COMPANY_ID ) ) {
                collectionName = MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION;
                unitSettings = organizationManagementService.getCompanySettings( entityId );
            } else if ( entityType.equalsIgnoreCase( CommonConstants.REGION_ID ) ) {
                collectionName = MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION;
                unitSettings = organizationManagementService.getRegionSettings( entityId );
            } else if ( entityType.equalsIgnoreCase( CommonConstants.BRANCH_ID ) ) {
                collectionName = MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION;
                unitSettings = organizationManagementService.getBranchSettingsDefault( entityId );
            } else if ( entityType.equalsIgnoreCase( CommonConstants.AGENT_ID ) ) {
                collectionName = MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION;
                unitSettings = organizationManagementService.getAgentSettings( entityId );
            } else {
                throw new InvalidInputException( "Invalid entity type" );
            }

            LoneWolfCrmInfo loneWolfCrmInfo = (LoneWolfCrmInfo) unitSettings.getCrm_info();
            loneWolfCrmInfo.setNumberOfDays( numOfDays );
            loneWolfCrmInfo.setEmailAddressForReport( emailIdForReport );
            loneWolfCrmInfo.setGenerateReport( true );
            organizationManagementService.updateCRMDetailsForAnyUnitSettings( unitSettings, collectionName, loneWolfCrmInfo,
                "com.realtech.socialsurvey.core.entities.LoneWolfCrmInfo" );
            message = messageUtils.getDisplayMessage( DisplayMessageConstants.LONEWOLF_GENERATE_REPORT_SUCCESSFUL,
                DisplayMessageType.SUCCESS_MESSAGE ).getMessage();
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while enabling report generation for lone wolf. Reason : " + e.getMessage(), e );
            message = e.getMessage();
        }
        return message;
    }


    @RequestMapping ( value = "/getlonewolfclassifications", method = RequestMethod.GET)
    @ResponseBody
    public String getLonewolfClassifications( HttpServletRequest request )
    {
        HttpSession session = request.getSession( false );
        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        String clientCode = request.getParameter( "clientCode" );

        LOG.info( "Method getLonewolfClassifications started for clientCode : " + clientCode + " started." );
        Response response = null;
        boolean status = false;
        String message = null;
        List<LoneWolfClassificationCode> classificationCodes = new ArrayList<LoneWolfClassificationCode>();
        Map<String, String> savedClassificationsByCode = null;
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            try {
                if ( StringUtils.isEmpty( clientCode ) ) {
                    throw new InvalidInputException( "Client Code cannot be empty" );
                }

                retrofit.client.Response res = loneWolfIntegrationService.testLoneWolfCompanyCredentials( secretKey, apiToken,
                    clientCode );

                //processing retrofit response and building rest response
                if ( res != null ) {
                    if ( res.getStatus() == HttpStatus.SC_OK ) {
                        status = true;
                        message = "Successfully Connected to Lone Wolf. Please select classifications";
                        OrganizationUnitSettings unitSettings = null;
                        if ( entityType.equalsIgnoreCase( CommonConstants.COMPANY_ID ) ) {
                            unitSettings = organizationManagementService.getCompanySettings( entityId );
                        } else if ( entityType.equalsIgnoreCase( CommonConstants.REGION_ID ) ) {
                            unitSettings = organizationManagementService.getRegionSettings( entityId );
                        } else if ( entityType.equalsIgnoreCase( CommonConstants.BRANCH_ID ) ) {
                            unitSettings = organizationManagementService.getBranchSettingsDefault( entityId );
                        } else if ( entityType.equalsIgnoreCase( CommonConstants.AGENT_ID ) ) {
                            unitSettings = organizationManagementService.getAgentSettings( entityId );
                        } else {
                            throw new InvalidInputException( "Invalid entity type" );
                        }

                        if ( unitSettings != null && unitSettings.getCrm_info() != null
                            && !StringUtils.isEmpty( unitSettings.getCrm_info().getCrm_source() )
                            && unitSettings.getCrm_info().getCrm_source()
                                .equalsIgnoreCase( CommonConstants.CRM_SOURCE_LONEWOLF )
                            && unitSettings.getCrm_info() instanceof LoneWolfCrmInfo ) {
                            LoneWolfCrmInfo crmInfo = (LoneWolfCrmInfo) unitSettings.getCrm_info();
                            if ( crmInfo.getClientCode().equalsIgnoreCase( clientCode ) ) {
                                if ( crmInfo.getClassificationCodes() != null && !crmInfo.getClassificationCodes().isEmpty() ) {
                                    savedClassificationsByCode = new HashMap<String, String>();
                                    for ( LoneWolfClassificationCode c : crmInfo.getClassificationCodes() ) {
                                        savedClassificationsByCode.put( c.getCode(),
                                            c.getLoneWolfTransactionParticipantsType() );
                                    }
                                }
                            }
                        }

                        classificationCodes = loneWolfIntegrationService.fetchLoneWolfClassificationCodes( secretKey, apiToken,
                            clientCode );
                    } else {
                        String responseString = new String( ( (TypedByteArray) res.getBody() ).getBytes() );
                        Map<String, String> responseMap = new Gson().fromJson( responseString,
                            new TypeToken<Map<String, String>>() {}.getType() );
                        message = responseMap.get( "Message" );
                    }
                }

                resultMap.put( CommonConstants.STATUS_COLUMN, status );
                resultMap.put( CommonConstants.MESSAGE, message );
                resultMap.put( "classifications", classificationCodes );
                resultMap.put( "savedClassificationsByCode", savedClassificationsByCode );

                response = Response.ok( new Gson().toJson( resultMap ) ).build();
            } catch ( Exception e ) {
                throw new InternalServerException( new LoneWolfErrorCode( CommonConstants.ERROR_CODE_GENERAL,
                    CommonConstants.SERVICE_CODE_GENERAL, "Exception occured while getting classifications from lone wolf" ),
                    e.getMessage() );
            }
        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        }

        LOG.debug( "returning response: " + response );
        LOG.info( "Method getLonewolfClassifications finished." );
        return response.getEntity().toString();
    }
}
