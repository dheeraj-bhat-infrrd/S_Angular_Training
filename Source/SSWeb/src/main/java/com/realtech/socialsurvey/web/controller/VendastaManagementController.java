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
import com.realtech.socialsurvey.core.entities.DisplayMessage;
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
import com.realtech.socialsurvey.core.utils.UrlValidationHelper;
import com.realtech.socialsurvey.web.api.builder.SSApiIntergrationBuilder;
import com.realtech.socialsurvey.web.api.entities.VendastaRmCreateRequest;
import com.realtech.socialsurvey.web.common.JspResolver;

import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


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

    @Autowired
    UrlValidationHelper urlValidationHelper;

    @Autowired
    SSApiIntergrationBuilder ssApiIntergrationBuilder;


    // updates the boolean value vendastaAccessible in mongo for every hierarchy

    @RequestMapping ( value = "/updatevendastaaccesssetting", method = RequestMethod.POST)
    @ResponseBody
    public String updateVendastaAccessSettings( HttpServletRequest request )
    {
        LOG.info( "Method to update Vendasta Access Settings started" );
        HttpSession session = request.getSession( false );
        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        try {
            String hasVendastaAcess = request.getParameter( "hasVendastaAcess" );
            boolean isVendastaAcessible = false;
            OrganizationUnitSettings unitSettings = null;
            String collectionName = "";

            if ( hasVendastaAcess != null && !hasVendastaAcess.isEmpty() ) {
                isVendastaAcessible = Boolean.parseBoolean( hasVendastaAcess );
                session.setAttribute( CommonConstants.VENDASTA_ACCESS, String.valueOf( isVendastaAcessible ) );
                session.setAttribute( "vendastaAccess", String.valueOf( isVendastaAcessible ) );
                Map<String, Object> hierarchyDetails = vendastaManagementService.getUnitSettingsForAHierarchy( entityType,
                    entityId );
                unitSettings = (OrganizationUnitSettings) hierarchyDetails.get( "unitSettings" );
                collectionName = (String) hierarchyDetails.get( "collectionName" );
                if ( unitSettings == null )
                    throw new Exception( "unitSettings can't be null" );
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
    @RequestMapping ( value = "/showlistingsmanagersettings")
    public String showVendastaSettings( Model model, HttpServletRequest request )
    {
        LOG.debug( "Method showVendastaSettings of OrganizationManagementController called" );
        try {
            HttpSession session = request.getSession( false );
            
            if ( session == null || session.getAttribute( CommonConstants.VENDASTA_ACCESS ) == null
                || !CommonConstants.AGREE_SHARE_COLUMN_TRUE
                    .equals( String.valueOf( session.getAttribute( CommonConstants.VENDASTA_ACCESS ) ) ) ) {
                throw new NonFatalException( "Listings manager settings is not accessible for the current session." );
            }

            String columnName = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            Long columnValue = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            Map<String, Object> hierarchyDetails = vendastaManagementService.getUnitSettingsForAHierarchy( columnName,
                columnValue );
            OrganizationUnitSettings unitSettings = (OrganizationUnitSettings) hierarchyDetails.get( "unitSettings" );
            
            model.addAttribute( "settings", unitSettings );
            model.addAttribute( "columnName", columnName );
            model.addAttribute( "columnValue", columnValue );

            LOG.debug( "Method showVendastaSettings of OrganizationManagementController finished" );
            return JspResolver.VENDASTA_SETTINGS;

        } catch ( Exception error ) {
            LOG.error( "Exception while showing listings manager settings. Reason: " + error.getMessage(), error );
            return JspResolver.LISTINGS_MANAGER_SETTINGS_ERROR;
        }
    }


    // method to add or update vendasta accountId from the jsp entered in the browser    
    @RequestMapping ( value = "/updatevendastasettings", method = RequestMethod.POST)
    @ResponseBody
    public String updateVendastaSettings( HttpServletRequest request )
    {

        LOG.info( "Updating Vendasta Product settings" );
        DisplayMessage message = null;
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
                VendastaProductSettings settings = new VendastaProductSettings();
                if ( !StringUtils.isEmpty( accountId ) ) {
                    if ( vendastaManagementService.isAccountExistInVendasta( accountId ) ) {
                        settings.setAccountId( accountId );
                        if ( vendastaManagementService.updateVendastaRMSettings( collectionName, unitSettings, settings ) ) {
                            LOG.info( "Updated Vendasta Product settings" );
                            message = messageUtils.getDisplayMessage(
                                DisplayMessageConstants.UPDATING_VENDASTA_SETTINGS_SUCCESSFUL,
                                DisplayMessageType.SUCCESS_MESSAGE );
                        }
                    } else {
                        message = messageUtils.getDisplayMessage( DisplayMessageConstants.ACCOUNT_DOESNT_EXIST,
                            DisplayMessageType.ERROR_MESSAGE );
                    }
                } else {
                    message = messageUtils.getDisplayMessage( DisplayMessageConstants.INVALID_VALUES,
                        DisplayMessageType.ERROR_MESSAGE );
                }
            }
        } catch ( NonFatalException error ) {
            LOG.error( "NonFatalException while updating Vendasta Product settings. Reason : " + error.getMessage(), error );
            message = messageUtils.getDisplayMessage( error.getErrorCode(), DisplayMessageType.ERROR_MESSAGE );
        }
        String messageJson = new Gson().toJson( message );
        return messageJson;
    }


    //method to check if a perticular hierarchy entity has  vendasta access for the session
    @RequestMapping ( value = "/isvendastaaccessibleforthesession")
    @ResponseBody
    public String isVendastaAccessible( Model model, HttpServletRequest request )
    {
        LOG.debug( "Checking if vendesta is accessible for this session" );
        HttpSession session = request.getSession( false );

        String vendastaAccess = null;
        if ( session != null && session.getAttribute( CommonConstants.VENDASTA_ACCESS ) != null ) {
            vendastaAccess = String.valueOf( session.getAttribute( CommonConstants.VENDASTA_ACCESS ) );
        }
        LOG.debug( "vendastaAccess {}", vendastaAccess );
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
                        && !StringUtils.isEmpty( unitSettings.getVendasta_rm_settings().getAccountId() ) ) {
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


    @RequestMapping ( value = "/testvendastaurl")
    @ResponseBody
    public String testVendastaUrl( HttpServletRequest request )
    {
        LOG.info( "VendastaManagementController.testVendastaUrl started" );
        String url = request.getParameter( "url" );
        if ( url == null ) {
            return "failed";
        }
        try {
            urlValidationHelper.validateUrl( url );
            return "success";
        } catch ( Exception error ) {
            return "failed";
        }
    }


    @RequestMapping ( value = "/vendastaError")
    public String vendastaErrorPage( HttpServletRequest request )
    {
        return JspResolver.VENDASTA_SSO_ERROR;
    }


    @RequestMapping ( value = "/setuplistingsmanager", method = RequestMethod.POST)
    @ResponseBody
    public String createVendastaRmAccount( HttpServletRequest request )
    {
        DisplayMessage message = null;
        try {
            HttpSession currentSession = request.getSession( false );
            String entityType = (String) currentSession.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            long entityId = (long) currentSession.getAttribute( CommonConstants.ENTITY_ID_COLUMN );

            if ( CommonConstants.AGENT_ID.equals( entityType ) ) {
                message = messageUtils.getDisplayMessage( DisplayMessageConstants.VENDASTA_NOT_FOR_AGENT,
                    DisplayMessageType.ERROR_MESSAGE );
            } else {

                VendastaRmCreateRequest createRequest = new VendastaRmCreateRequest();
                createRequest.setEntityId( entityId );
                createRequest.setEntityType( entityType );
                createRequest.setCompanyName( (String) request.getParameter( "companyName" ) );
                createRequest.setCountry( (String) request.getParameter( "country" ) );
                createRequest.setState( (String) request.getParameter( "state" ) );
                createRequest.setCity( (String) request.getParameter( "city" ) );
                createRequest.setAddress( (String) request.getParameter( "address" ) );
                createRequest.setZip( (String) request.getParameter( "zip" ) );

                Response apiResponse = ssApiIntergrationBuilder.getIntegrationApi().createVendastaRmAccount( createRequest,
                    true );
                message = new DisplayMessage( new String( ( (TypedByteArray) apiResponse.getBody() ).getBytes() ),
                    DisplayMessageType.SUCCESS_MESSAGE );
            }

        } catch ( Exception unhandledException ) {
            LOG.error( "unable to create account in vendasta, Reason: " + unhandledException.getMessage(), unhandledException );
            message = new DisplayMessage( unhandledException.getMessage(), DisplayMessageType.ERROR_MESSAGE );
        }

        return new Gson().toJson( message );
    }
}
