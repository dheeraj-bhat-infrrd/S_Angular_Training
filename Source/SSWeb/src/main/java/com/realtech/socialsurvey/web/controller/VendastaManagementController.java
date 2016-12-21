package com.realtech.socialsurvey.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.VendastaProductSettings;
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.VendastaManagementService;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.web.common.JspResolver;


/**
 *manages all the HTTP requests for vendasta related tasks
 */
@Controller
public class VendastaManagementController
{
    private static final Logger LOG = LoggerFactory.getLogger( VendastaManagementController.class );

    @Value ( "${VENDASTA_REPUTATION_MANAGEMENT_URL}")
    private String productUrl;

    @Autowired
    OrganizationManagementService organizationManagementService;

    @Autowired
    VendastaManagementService vendastaManagementService;

    @Autowired
    MessageUtils messageUtils;


    // updates the boolean value vendastaAccessible in mongo for every hierarchy

    @RequestMapping ( value = "/updatevendastaaccesssetting", method = RequestMethod.POST)
    @ResponseBody
    public String updateVendastaAccessSettings( HttpServletRequest request )
    {
        LOG.info( "Method to update Vendasta Access Settings started" );
        HttpSession session = request.getSession();
        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        try {
            String hasVendastaAcess = request.getParameter( "hasVendastaAcess" );
            boolean isVendastaAcessible = false;
            OrganizationUnitSettings unitSettings = null;
            String collectionName = "";

            if ( hasVendastaAcess != null && !hasVendastaAcess.isEmpty() ) {
                isVendastaAcessible = Boolean.parseBoolean( hasVendastaAcess );
                Map<String, Object> hierarchyDetails = vendastaManagementService.getUnitSettingsForAHierarchy( entityType,
                    entityId );
                unitSettings = (OrganizationUnitSettings) hierarchyDetails.get( "unitSettings" );
                collectionName = (String) hierarchyDetails.get( "collectionName" );
                if ( unitSettings == null )
                    throw new Exception();
                else {
                    unitSettings.setVendastaAccess( isVendastaAcessible );
                    if ( vendastaManagementService.updateVendastaAccess( collectionName, unitSettings ) ) {
                        LOG.info( "Updated Vendasta Access Settings" );
                    }
                }
            }
        } catch ( Exception error ) {
            LOG.error(
                "Exception occured in updateVendastaAccessSettings() while updating Vendasta Access Settings. Nested exception is ",
                error );
            return error.getMessage();
        }

        LOG.info( "Method to update Vendasta Access Settings finished" );
        return "Successfully updated Vendasta Access Settings";
    }


    //method to prepare jsp to add or update vendasta account Id
    @RequestMapping ( value = "/showvendastasettings")
    public String showVendastaSettings( Model model, HttpServletRequest request )
    {

        LOG.info( "Method showVendastaSettings of OrganizationManagementController called" );
        HttpSession session = request.getSession( false );
        String vendastaAccess = null;

        if ( session != null && session.getAttribute( CommonConstants.VENDASTA_ACCESS ) != null ) {
            vendastaAccess = String.valueOf( session.getAttribute( CommonConstants.VENDASTA_ACCESS ) );
        }

        if ( vendastaAccess != null && vendastaAccess == "true" ) {
            String columnName = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            Long columnValue = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );

            try {
                OrganizationUnitSettings unitSettings = null;
                if ( columnName != null && columnValue != null ) {
                    Map<String, Object> hierarchyDetails = vendastaManagementService.getUnitSettingsForAHierarchy( columnName,
                        columnValue );
                    unitSettings = (OrganizationUnitSettings) hierarchyDetails.get( "unitSettings" );
                    if ( unitSettings.getVendasta_rm_settings() != null
                        && unitSettings.getVendasta_rm_settings().getAccountId() != null ) {
                        model.addAttribute( "accountId", unitSettings.getVendasta_rm_settings().getAccountId() );
                        model.addAttribute( "apiUser", unitSettings.getVendasta_rm_settings().getApiUser() );
                        model.addAttribute( "apiKey", unitSettings.getVendasta_rm_settings().getApiKey() );
                    }
                }
            } catch ( NonFatalException error ) {
                LOG.error( "NonfatalException while showing vendasta settings. Reason: " + error.getMessage(), error );
                model.addAttribute( "message",
                    messageUtils.getDisplayMessage( error.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
                return JspResolver.MESSAGE_HEADER;
            }
        } else {

        }

        LOG.info( "Method showVendastaSettings of OrganizationManagementController finished" );
        return JspResolver.VENDASTA_SETTINGS;
    }


    // method to add or update vendasta accountId from the jsp entered in the browser    
    @RequestMapping ( value = "/updatevendastasettings", method = RequestMethod.POST)
    @ResponseBody
    public String updateVendastaSettings( HttpServletRequest request )
    {

        LOG.info( "Updating Vendasta Product settings" );
        String message = "";
        String collectionName = "";
        try {
            HttpSession session = request.getSession( false );
            String columnName = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            Long columnValue = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            OrganizationUnitSettings unitSettings = null;
            if ( columnName != null && columnValue != null ) {
                Map<String, Object> hierarchyDetails = vendastaManagementService.getUnitSettingsForAHierarchy( columnName,
                    columnValue );
                unitSettings = (OrganizationUnitSettings) hierarchyDetails.get( "unitSettings" );
                collectionName = (String) hierarchyDetails.get( "collectionName" );
                String accountId = request.getParameter( "accountId" );
                String apiUser = request.getParameter( "apiUser" );
                String apiKey = request.getParameter( "apiKey" );
                VendastaProductSettings settings = new VendastaProductSettings();
                if ( !StringUtils.isEmpty( accountId ) && !StringUtils.isEmpty( apiUser ) && !StringUtils.isEmpty( apiKey ) ) {
                    if ( vendastaManagementService.isAccountExistInVendasta( accountId, apiUser, apiKey ) ) {
                        settings.setAccountId( accountId );
                        settings.setApiUser( apiUser );
                        settings.setApiKey( apiKey );
                        if ( vendastaManagementService.updateVendastaRMSettings( collectionName, unitSettings, settings ) ) {
                            LOG.info( "Updated Vendasta Product settings" );
                            message = messageUtils
                                .getDisplayMessage( DisplayMessageConstants.UPDATING_VENDASTA_SETTINGS_SUCCESSFUL,
                                    DisplayMessageType.SUCCESS_MESSAGE )
                                .getMessage();
                        }
                    } else {
                        message = accountId + " account does not exist in vendasta for apiUser - " + apiUser
                            + ". Please provide valid vendasta account details.";
                    }
                } else {
                    message = messageUtils
                        .getDisplayMessage( DisplayMessageConstants.INVALID_ACCOUNT_ID, DisplayMessageType.ERROR_MESSAGE )
                        .getMessage();
                }
            }
        } catch ( NonFatalException error ) {
            LOG.error( "NonFatalException while updating Vendasta Product settings. Reason : " + error.getMessage(), error );
            message = messageUtils.getDisplayMessage( error.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ).getMessage();
        }

        return message;
    }


    //method to check if a perticular hierarchy entity has  vendasta access for the session
    @RequestMapping ( value = "/isvendastaaccessibleforthesession")
    @ResponseBody
    public String isVendastaAccessible( Model model, HttpServletRequest request )
    {
        HttpSession session = request.getSession( false );

        String vendastaAccess = null;
        if ( session != null && session.getAttribute( CommonConstants.VENDASTA_ACCESS ) != null ) {
            vendastaAccess = String.valueOf( session.getAttribute( CommonConstants.VENDASTA_ACCESS ) );
        }

        if ( vendastaAccess != null ) {
            return vendastaAccess;
        } else {
            return "false";
        }

    }


    // method to fetch vendasata product url from mongo for a perticular hierarchy entity
    @RequestMapping ( value = "/fetchvendastaurl")
    @ResponseBody
    public String fetchVendastaUrl( Model model, HttpServletRequest request )
    {
        LOG.info( "Method fetchVendastaUrl() started." );
        HttpSession session = request.getSession( false );
        String vendastaAccess = null;

        if ( session != null && session.getAttribute( CommonConstants.VENDASTA_ACCESS ) != null ) {
            vendastaAccess = String.valueOf( session.getAttribute( CommonConstants.VENDASTA_ACCESS ) );
        }
        Map<String, Object> responseMap = new HashMap<String, Object>();

        if ( vendastaAccess != null && vendastaAccess == "true" ) {
            String columnName = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            Long columnValue = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );

            try {
                OrganizationUnitSettings unitSettings = null;
                if ( columnName != null && columnValue != null ) {
                    Map<String, Object> hierarchyDetails = vendastaManagementService.getUnitSettingsForAHierarchy( columnName,
                        columnValue );
                    unitSettings = (OrganizationUnitSettings) hierarchyDetails.get( "unitSettings" );
                    responseMap.put( "url", productUrl );
                    if ( unitSettings.getVendasta_rm_settings() != null
                        && unitSettings.getVendasta_rm_settings().getAccountId() != null ) {
                        responseMap.put( "ssoToken", unitSettings.getVendasta_rm_settings().getAccountId() );
                        responseMap.put( "status", "success" );
                    } else {
                        responseMap.put( "status", "failed" );
                    }
                }
            } catch ( InvalidInputException | NoRecordsFetchedException error ) {
                responseMap.put( "status", "failed" );
                LOG.error( "No such entity with Id: " + columnValue );
            } catch ( Exception error ) {
                responseMap.put( "status", "failed" );
            }
        } else {
            responseMap.put( "status", "failed" );
        }
        LOG.info( "Method fetchVendastaUrl() finished." );
        return new Gson().toJson( responseMap );
    }

}
