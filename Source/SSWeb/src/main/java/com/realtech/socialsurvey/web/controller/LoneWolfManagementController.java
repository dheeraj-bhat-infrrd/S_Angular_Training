package com.realtech.socialsurvey.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.LoneWolfCrmInfo;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.MessageUtils;

@Controller
public class LoneWolfManagementController
{
    
    private static final Logger LOG = LoggerFactory.getLogger( LoneWolfManagementController.class );
    
    @Autowired
    private SessionHelper sessionHelper;
    
    @Autowired
    private OrganizationManagementService organizationManagementService;
    
    @Autowired
    private MessageUtils messageUtils;
    
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

        try {
            switch ( entityType ) {
                case CommonConstants.COMPANY_ID:
                    collectionName = MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION;
                    unitSettings = organizationManagementService.getCompanySettings( entityId );
                    break;
                case CommonConstants.REGION_ID:
                    collectionName = MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION;
                    unitSettings = organizationManagementService.getRegionSettings( entityId );
                    break;
                case CommonConstants.BRANCH_ID:
                    collectionName = MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION;
                    unitSettings = organizationManagementService.getBranchSettingsDefault( entityId );
                    break;
                case CommonConstants.AGENT_ID:
                    collectionName = MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION;
                    unitSettings = organizationManagementService.getAgentSettings( entityId );
                    break;
            }
            
            LoneWolfCrmInfo lonewolfCrmInfo = (LoneWolfCrmInfo) unitSettings.getCrm_info();
            lonewolfCrmInfo.setState( CommonConstants.LONEWOLF_PRODUCTION_STATE );
            organizationManagementService.updateCRMDetailsForAnyUnitSettings( unitSettings, collectionName, lonewolfCrmInfo,
                "com.realtech.socialsurvey.core.entities.LoneWolfCrmInfo" );
            organizationManagementService.logEvent( CommonConstants.LONEWOLF_CONNECTION, CommonConstants.ACTION_ENABLED,
                eventFiredBy, user.getCompany().getCompanyId(), 0, 0, 0 );
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

        try {
            switch ( entityType ) {
                case CommonConstants.COMPANY_ID:
                    collectionName = MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION;
                    unitSettings = organizationManagementService.getCompanySettings( entityId );
                    break;
                case CommonConstants.REGION_ID:
                    collectionName = MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION;
                    unitSettings = organizationManagementService.getRegionSettings( entityId );
                    break;
                case CommonConstants.BRANCH_ID:
                    collectionName = MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION;
                    unitSettings = organizationManagementService.getBranchSettingsDefault( entityId );
                    break;
                case CommonConstants.AGENT_ID:
                    collectionName = MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION;
                    unitSettings = organizationManagementService.getAgentSettings( entityId );
                    break;
            }
            
            LoneWolfCrmInfo lonewolfCrmInfo = (LoneWolfCrmInfo) unitSettings.getCrm_info();
            lonewolfCrmInfo.setState( CommonConstants.LONEWOLF_DRY_RUN_STATE );
            organizationManagementService.updateCRMDetailsForAnyUnitSettings( unitSettings, collectionName, lonewolfCrmInfo,
                "com.realtech.socialsurvey.core.entities.LoneWolfCrmInfo" );
            organizationManagementService.logEvent( CommonConstants.LONEWOLF_CONNECTION, CommonConstants.ACTION_DISABLED,
                eventFiredBy, user.getCompany().getCompanyId(), 0, 0, 0 );
            message = messageUtils
                .getDisplayMessage( DisplayMessageConstants.LONEWOLF_DISABLE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE )
                .getMessage();
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while disabling lonewolf. Reason : " + e.getMessage(), e );
            message = messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ).getMessage();
        }
        return message;
    }

}
