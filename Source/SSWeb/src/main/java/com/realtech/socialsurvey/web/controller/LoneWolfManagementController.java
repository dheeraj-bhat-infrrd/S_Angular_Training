package com.realtech.socialsurvey.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.LoneWolfCrmInfo;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.web.common.JspResolver;


@Controller
public class LoneWolfManagementController
{
    private static final Logger LOG = LoggerFactory.getLogger( LoneWolfManagementController.class );

    private SessionHelper sessionHelper;
    private OrganizationManagementService organizationManagementService;
    private MessageUtils messageUtils;


    @Autowired
    public LoneWolfManagementController( SessionHelper sessionHelper,
        OrganizationManagementService organizationManagementService, MessageUtils messageUtils )
    {
        this.sessionHelper = sessionHelper;
        this.organizationManagementService = organizationManagementService;
        this.messageUtils = messageUtils;
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
    public String saveLoneWolfDetails( Model model, HttpServletRequest request )
    {
        LOG.info( "Inside method saveLoneWolfDetails " );
        HttpSession session = request.getSession( false );
        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        boolean status = false;
        String message = null;
        try {
            String clientCode = request.getParameter( "lone-client" );
            String state = request.getParameter( "lone-state" );
            if ( StringUtils.isEmpty( clientCode ) ) {
                throw new InvalidInputException( "Client code cannot be empty" );
            }
            if ( StringUtils.isEmpty( state ) || state.equals( CommonConstants.LONEWOLF_DRY_RUN_STATE ) ) {
                state = CommonConstants.LONEWOLF_DRY_RUN_STATE;
            } else {
                state = CommonConstants.LONEWOLF_PRODUCTION_STATE;
            }

            LoneWolfCrmInfo loneWolfCrmInfo = new LoneWolfCrmInfo();
            loneWolfCrmInfo.setCrm_source( CommonConstants.CRM_SOURCE_LONEWOLF );
            loneWolfCrmInfo.setClientCode( clientCode );
            loneWolfCrmInfo.setState( state );
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
            message = messageUtils
                .getDisplayMessage( DisplayMessageConstants.LONEWOLF_CONNECTION_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE )
                .getMessage();
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
}
