package com.realtech.socialsurvey.web.controller;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.braintreegateway.exceptions.AuthorizationException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.AccountsMaster;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.ComplaintResolutionSettings;
import com.realtech.socialsurvey.core.entities.DotLoopCrmInfo;
import com.realtech.socialsurvey.core.entities.EncompassCrmInfo;
import com.realtech.socialsurvey.core.entities.LicenseDetail;
import com.realtech.socialsurvey.core.entities.LockSettings;
import com.realtech.socialsurvey.core.entities.MailContent;
import com.realtech.socialsurvey.core.entities.MailContentSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.StateLookup;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveySettings;
import com.realtech.socialsurvey.core.entities.UploadValidation;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.entities.UserSettings;
import com.realtech.socialsurvey.core.entities.VerticalCrmMapping;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.enums.OrganizationUnit;
import com.realtech.socialsurvey.core.enums.SettingsForApplication;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.exception.UserAlreadyExistsException;
import com.realtech.socialsurvey.core.services.generator.UrlService;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.payment.Payment;
import com.realtech.socialsurvey.core.services.payment.exception.CreditCardException;
import com.realtech.socialsurvey.core.services.payment.exception.PaymentException;
import com.realtech.socialsurvey.core.services.payment.exception.SubscriptionPastDueException;
import com.realtech.socialsurvey.core.services.payment.exception.SubscriptionUnsuccessfulException;
import com.realtech.socialsurvey.core.services.payment.exception.SubscriptionUpgradeUnsuccessfulException;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.services.settingsmanagement.SettingsLocker;
import com.realtech.socialsurvey.core.services.settingsmanagement.SettingsSetter;
import com.realtech.socialsurvey.core.services.settingsmanagement.impl.InvalidSettingsStateException;
import com.realtech.socialsurvey.core.services.social.SocialManagementService;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyBuilder;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;
import com.realtech.socialsurvey.core.services.upload.FileUploadService;
import com.realtech.socialsurvey.core.services.upload.HierarchyStructureUploadService;
import com.realtech.socialsurvey.core.services.upload.HierarchyUploadService;
import com.realtech.socialsurvey.core.services.upload.UploadValidationService;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.EmailFormatHelper;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.core.utils.StateLookupExclusionStrategy;
import com.realtech.socialsurvey.core.vo.SurveyPreInitiationList;
import com.realtech.socialsurvey.web.common.JspResolver;


// JIRA: SS-24 BY RM02 BOC
/**
 * Controller to manage the organizational settings and information provided by the user.
 */
@Controller
public class OrganizationManagementController
{
    private static final Logger LOG = LoggerFactory.getLogger( OrganizationManagementController.class );

    @Autowired
    private MessageUtils messageUtils;

    @Autowired
    private OrganizationManagementService organizationManagementService;

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private Payment gateway;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private SessionHelper sessionHelper;

    @Autowired
    private EmailFormatHelper emailFormatHelper;

    @Autowired
    private SurveyBuilder surveyBuilder;

    @Autowired
    private SettingsSetter settingsSetter;

    @Autowired
    private SettingsLocker settingsLocker;

    @Autowired
    private Payment payment;

    @Autowired
    private SurveyHandler surveyHandler;

    @Autowired
    private UrlService urlService;

    @Autowired
    private SocialManagementService socialManagementService;

    @Autowired
    private HierarchyUploadService hierarchyUploadService;

    @Autowired
    private HierarchyStructureUploadService hierarchyStructureUploadService;

    @Autowired
    private UploadValidationService uploadValidationService;

    @Value ( "${CDN_PATH}")
    private String endpoint;

    @Value ( "${APPLICATION_LOGO_URL}")
    private String applicationLogoUrl;

    @Value ( "${AMAZON_LOGO_BUCKET}")
    private String logoBucket;

    @Value ( "${HAPPY_TEXT}")
    private String happyText;

    @Value ( "${NEUTRAL_TEXT}")
    private String neutralText;

    @Value ( "${SAD_TEXT}")
    private String sadText;

    @Value ( "${HAPPY_TEXT_COMPLETE}")
    private String happyTextComplete;

    @Value ( "${NEUTRAL_TEXT_COMPLETE}")
    private String neutralTextComplete;

    @Value ( "${SAD_TEXT_COMPLETE}")
    private String sadTextComplete;
    @Value ( "${APPLICATION_BASE_URL}")
    private String applicationBaseUrl;


    /**
     * Method to upload logo image for a company
     * 
     * @param fileLocal
     * @return
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping ( value = "/uploadcompanylogo", method = RequestMethod.POST)
    public String imageUpload( Model model, @RequestParam ( "logo") MultipartFile fileLocal, HttpServletRequest request )
    {
        LOG.info( "Method imageUpload of OrganizationManagementController called" );
        String message = "";
        String logoName = "";

        LOG.debug( "Overriding Logo image name in Session" );
        if ( request.getSession( false ).getAttribute( CommonConstants.LOGO_NAME ) != null ) {
            request.getSession( false ).removeAttribute( CommonConstants.LOGO_NAME );
        }

        try {
            logoName = fileUploadService.uploadLogo( fileLocal, request.getParameter( "logo_name" ) );
            // Setting the complete logo url in session
            logoName = endpoint + CommonConstants.FILE_SEPARATOR + logoBucket + CommonConstants.FILE_SEPARATOR + logoName;

            LOG.debug( "Setting Logo image name to Session" );
            request.getSession( false ).setAttribute( CommonConstants.LOGO_NAME, logoName );

            LOG.info( "Method imageUpload of OrganizationManagementController completed successfully" );
            message = messageUtils.getDisplayMessage( "LOGO_UPLOAD_SUCCESSFUL", DisplayMessageType.SUCCESS_MESSAGE )
                .getMessage();
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while uploading Logo. Reason :" + e.getMessage(), e );
            message = e.getMessage();
        }
        return message;
    }


    /**
     * Method to call service for adding company information for a user
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/addcompanyinformation", method = RequestMethod.POST)
    public String addCompanyInformation( HttpServletRequest request, RedirectAttributes redirectAttributes )
    {
        LOG.info( "Method addCompanyInformation of UserManagementController called" );
        String companyName = request.getParameter( "company" );
        String address1 = request.getParameter( "address1" );
        String address2 = request.getParameter( "address2" );
        String country = request.getParameter( "country" );
        String countryCode = request.getParameter( "countrycode" );
        String zipCode = request.getParameter( "zipcode" );
        String state = request.getParameter( "state" );
        String city = request.getParameter( "city" );
        String companyContactNo = request.getParameter( "contactno" );
        String vertical = request.getParameter( "vertical" );
        String phoneFormat = request.getParameter( "phoneFormat" );
        String logoDecoyName = request.getParameter( "logoDecoyName" );
        String uniqueIdentifier = request.getParameter( "uniqueIdentifier" );
        // JIRA SS-536: Added for manual registration via invitation
        String strIsDirectRegistration = request.getParameter( "isDirectRegistration" );

        try {
            try {
                validateCompanyInfoParams( companyName, address1, country, countryCode, zipCode, companyContactNo, vertical );
            } catch ( InvalidInputException e ) {
                try {

                    if ( companyName != null && !companyName.isEmpty() ) {
                        companyName = companyName.trim();
                    }
                    if ( address1 != null && !address1.isEmpty() ) {
                        address1 = address1.trim();
                    }
                    if ( address2 != null && !address2.isEmpty() ) {
                        address2 = address2.trim();
                    }
                    if ( state != null && !state.isEmpty() ) {
                        state = state.trim();
                    }
                    if ( country != null && !country.isEmpty() ) {
                        country = country.trim();
                    }

                    redirectAttributes.addFlashAttribute( "verticals", organizationManagementService.getAllVerticalsMaster() );
                    redirectAttributes.addFlashAttribute( "companyName", companyName );
                    redirectAttributes.addFlashAttribute( "address1", address1 );
                    redirectAttributes.addFlashAttribute( "address2", address2 );
                    redirectAttributes.addFlashAttribute( "country", country );
                    redirectAttributes.addFlashAttribute( "countryCode", countryCode );
                    redirectAttributes.addFlashAttribute( "zipCode", zipCode );
                    redirectAttributes.addFlashAttribute( "state", state );
                    redirectAttributes.addFlashAttribute( "city", city );
                    redirectAttributes.addFlashAttribute( "vertical", vertical );
                    redirectAttributes.addFlashAttribute( "companyContactNo", companyContactNo );
                    redirectAttributes.addFlashAttribute( "phoneFormat", phoneFormat );
                    redirectAttributes.addFlashAttribute( "isDirectRegistration", strIsDirectRegistration );
                    redirectAttributes.addFlashAttribute( "uniqueIdentifier", uniqueIdentifier );
                    redirectAttributes.addFlashAttribute( "logoDecoyName", logoDecoyName );
                } catch ( InvalidInputException e1 ) {
                    throw new InvalidInputException( "Invalid Input exception occured in method getAllVerticalsMaster()",
                        DisplayMessageConstants.GENERAL_ERROR, e1 );
                }

                throw new InvalidInputException( "Invalid input exception occured while validating form parameters",
                    e.getErrorCode(), e );
            }

            User user = sessionHelper.getCurrentUser();
            HttpSession session = request.getSession( true );
            String logoName = null;
            if ( session.getAttribute( CommonConstants.LOGO_NAME ) != null ) {
                logoName = session.getAttribute( CommonConstants.LOGO_NAME ).toString();
            }
            session.removeAttribute( CommonConstants.LOGO_NAME );

            Map<String, String> companyDetails = new HashMap<String, String>();
            companyDetails.put( CommonConstants.UNIQUE_IDENTIFIER, uniqueIdentifier );
            companyDetails.put( CommonConstants.COMPANY_NAME, companyName );
            companyDetails.put( CommonConstants.ADDRESS, getCompleteAddress( address1, address2 ) );
            companyDetails.put( CommonConstants.ADDRESS1, address1 );
            if ( address2 != null ) {
                companyDetails.put( CommonConstants.ADDRESS2, address2 );
            }
            companyDetails.put( CommonConstants.COUNTRY, country );
            companyDetails.put( CommonConstants.STATE, state );
            companyDetails.put( CommonConstants.CITY, city );
            companyDetails.put( CommonConstants.COUNTRY_CODE, countryCode );
            companyDetails.put( CommonConstants.ZIPCODE, zipCode );
            companyDetails.put( CommonConstants.COMPANY_CONTACT_NUMBER, companyContactNo );
            if ( logoName != null ) {
                companyDetails.put( CommonConstants.LOGO_NAME, logoName );
            }
            companyDetails.put( CommonConstants.VERTICAL, vertical );

            // JIRA SS-536: Added for manual registration via invitation
            if ( strIsDirectRegistration.equalsIgnoreCase( "false" ) ) {
                companyDetails.put( CommonConstants.BILLING_MODE_COLUMN, CommonConstants.BILLING_MODE_INVOICE );
                /*redirectAttributes.addFlashAttribute( "skippayment", "true" );*/
                session.setAttribute( "skippayment", "true" );
            } else {
                companyDetails.put( CommonConstants.BILLING_MODE_COLUMN, CommonConstants.BILLING_MODE_AUTO );
                /*redirectAttributes.addFlashAttribute( "skippayment", "false" );*/
                session.setAttribute( "skippayment", "false" );
            }

            LOG.debug( "Calling services to add company details" );
            user = organizationManagementService.addCompanyInformation( user, companyDetails );

            LOG.debug( "Updating profile completion stage" );
            userManagementService.updateProfileCompletionStage( user, CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID,
                CommonConstants.ADD_ACCOUNT_TYPE_STAGE );
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while adding company information. Reason :" + e.getMessage(), e );
            redirectAttributes.addFlashAttribute( "status", DisplayMessageType.ERROR_MESSAGE );
            redirectAttributes.addFlashAttribute( "message",
                messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
            return "redirect:/" + JspResolver.COMPANY_INFORMATION_PAGE + ".do";
        }

        LOG.info( "Method addCompanyInformation of UserManagementController completed successfully" );
        return "redirect:/" + JspResolver.ACCOUNT_TYPE_SELECTION_PAGE + ".do";
    }


    @RequestMapping ( value = "/selectaccounttype")
    public String initSelectAccountTypePage()
    {
        LOG.info( "SelectAccountType Page started" );
        return JspResolver.ACCOUNT_TYPE_SELECTION;
    }


    /**
     * Method to validate form parameters of company information provided by the user
     * 
     * @param companyName
     * @param address
     * @param zipCode
     * @param companyContactNo
     * @throws InvalidInputException
     */
    private void validateCompanyInfoParams( String companyName, String address, String country, String countryCode,
        String zipCode, String companyContactNo, String vertical ) throws InvalidInputException
    {
        LOG.debug( "Method validateCompanyInfoParams called  for companyName : " + companyName + " address : " + address
            + " zipCode : " + zipCode + " companyContactNo : " + companyContactNo );

        if ( companyName == null || companyName.isEmpty() || companyName.contains( "\"" ) ) {
            throw new InvalidInputException( "Company name is null or empty while adding company information",
                DisplayMessageConstants.INVALID_COMPANY_NAME );
        }
        if ( address == null || address.isEmpty() ) {
            throw new InvalidInputException( "Address is null or empty while adding company information",
                DisplayMessageConstants.INVALID_ADDRESS );
        }

        if ( country == null || country.isEmpty() ) {
            throw new InvalidInputException( "Country is null or empty while adding company information",
                DisplayMessageConstants.INVALID_COUNTRY );
        }

        if ( countryCode == null || countryCode.isEmpty() ) {
            throw new InvalidInputException( "Country code is null or empty while adding company information",
                DisplayMessageConstants.INVALID_COUNTRY );
        }

        if ( zipCode == null || zipCode.isEmpty() ) {
            throw new InvalidInputException( "Zipcode is not valid while adding company information",
                DisplayMessageConstants.INVALID_ZIPCODE );
        }

        if ( companyContactNo == null || companyContactNo.isEmpty() ) {
            throw new InvalidInputException( "Company contact number is not valid while adding company information",
                DisplayMessageConstants.INVALID_COMPANY_PHONEN0 );
        }

        if ( vertical == null || vertical.isEmpty() ) {
            throw new InvalidInputException( "Vertical selected is not valid", DisplayMessageConstants.INVALID_VERTICAL );
        }
        LOG.debug( "Returning from validateCompanyInfoParams after validating parameters" );
    }


    /**
     * Method to get complete address from multiple address lines
     * 
     * @param address1
     * @param address2
     * @return
     */
    private String getCompleteAddress( String address1, String address2 )
    {
        LOG.debug( "Getting complete address for address1 : " + address1 + " and address2 : " + address2 );
        String address = address1;
        /**
         * if address line 2 is present, append it to address1 else the complete address is address1
         */
        if ( address1 != null && !address1.isEmpty() && address2 != null && !address2.isEmpty() ) {
            address = address1 + " " + address2;
        }
        LOG.debug( "Returning complete address" + address );
        return address;
    }


    /**
     * Method to call services for saving the selected account type(plan)
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/addaccounttype", method = RequestMethod.POST)
    public String addAccountType( Model model, HttpServletRequest request, HttpServletResponse response )
    {
        LOG.info( "Method addAccountType of UserManagementController called" );
        String strAccountType = request.getParameter( "accounttype" );
        String returnPage = null;
        try {
            if ( strAccountType == null || strAccountType.isEmpty() ) {
                throw new InvalidInputException( "Accounttype is null for adding account type",
                    DisplayMessageConstants.INVALID_ADDRESS );
            }
            LOG.debug( "AccountType obtained : " + strAccountType );

            User user = sessionHelper.getCurrentUser();

            // JIRA - SS-536

            // We check if there is mapped survey for the company and add a default survey if
            // not.
            if ( surveyBuilder.checkForExistingSurvey( user ) == null ) {
                surveyBuilder.addDefaultSurveyToCompany( user );
            }

            // check the company and see if manual registaration. Skip payment in that case
            if ( user.getCompany().getBillingMode().equals( CommonConstants.BILLING_MODE_INVOICE ) ) {
                // do what is done after payment
                // insert into license table
                // the account type is the accounts master id
                payment.insertIntoLicenseTable( Integer.parseInt( strAccountType ), user,
                    CommonConstants.INVOICE_BILLED_DEFULAT_SUBSCRIPTION_ID );
                // set profile completion flag for the company admin
                LOG.debug( "Calling sevices for updating profile completion stage" );
                userManagementService.updateProfileCompletionStage( user,
                    CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID,
                    CommonConstants.PRE_PROCESSING_BEFORE_LOGIN_STAGE );
                LOG.debug( "Successfully executed sevices for updating profile completion stage" );
                returnPage = "redirect:./" + CommonConstants.PRE_PROCESSING_BEFORE_LOGIN_STAGE;
            } else {
                LOG.debug( "Checking if payment has already been made." );
                if ( gateway.checkIfPaymentMade( user.getCompany() )
                    && user.getCompany().getLicenseDetails().get( CommonConstants.INITIAL_INDEX ).getAccountsMaster()
                        .getAccountsMasterId() != CommonConstants.ACCOUNTS_MASTER_FREE ) {
                    LOG.debug( "Payment for this company has already been made. Redirecting to dashboard." );
                    return JspResolver.PAYMENT_ALREADY_MADE;
                }

                if ( Integer.parseInt( strAccountType ) == CommonConstants.ACCOUNTS_MASTER_FREE ) {
                    LOG.debug( "Since its a free account type returning no popup jsp" );
                    return null;
                }

                model.addAttribute( "accounttype", strAccountType );
                model.addAttribute( "clienttoken", gateway.getClientToken() );
                model.addAttribute( "message", messageUtils.getDisplayMessage(
                    DisplayMessageConstants.ACCOUNT_TYPE_SELECTION_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE ) );

                LOG.info( "Method addAccountType of UserManagementController completed successfully" );
                returnPage = JspResolver.PAYMENT;
            }
            unlockIndividualAccountLogoSettings( user, Integer.parseInt( strAccountType ) );
        } catch ( NonFatalException e ) {
            LOG.error( "NonfatalException while adding account type. Reason: " + e.getMessage(), e );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
            return JspResolver.MESSAGE_HEADER;
        }

        return returnPage;
    }


    // Check if the account type is individual. If so, check if the logo is set. If so, unlock it
    private void unlockIndividualAccountLogoSettings( User user, int accountType ) throws NonFatalException
    {
        LOG.debug( "Unlocking the logo for individual account" );
        LOG.debug( "Checking if the account type is individual" );
        if ( accountType == CommonConstants.ACCOUNTS_MASTER_INDIVIDUAL ) {
            LOG.debug( "Check if logo is set" );
            if ( settingsSetter.isSettingsValueSet( OrganizationUnit.COMPANY,
                Long.parseLong( user.getCompany().getSettingsSetStatus() ), SettingsForApplication.LOGO ) ) {
                LOG.debug( "Unlocking the logo" );
                try {
                    if ( settingsLocker.isSettingsValueLocked( OrganizationUnit.COMPANY,
                        Long.parseLong( user.getCompany().getSettingsLockStatus() ), SettingsForApplication.LOGO ) ) {
                        settingsLocker.lockSettingsValueForCompany( user.getCompany(), SettingsForApplication.LOGO, false );
                    }
                } catch ( InvalidSettingsStateException e ) {
                    LOG.error( "InvalidSettingsStateException occured. Reason :", e );
                }
                // update company
                userManagementService.updateCompany( user.getCompany() );
            }
        } else {
            LOG.debug( "Not an individual account" );
            return;
        }
    }


    @RequestMapping ( value = "/showemailsettings", method = RequestMethod.GET)
    public String showEmailSettings( Model model, HttpServletRequest request )
    {
        LOG.info( "Method showEmailSettings of OrganizationManagementController called" );
        return JspResolver.EMAIL_SETTINGS;
    }


    @RequestMapping ( value = "/showwidget", method = RequestMethod.GET)
    public String showWidget( Model model, HttpServletRequest request )
    {
        LOG.info( "Method showWidget of OrganizationManagementController called" );
        model.addAttribute( "applicationBaseUrl", applicationBaseUrl );
        return JspResolver.SHOW_WIDGET;
    }


    /**
     * Method to load the app settings page
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/showapps", method = RequestMethod.GET)
    public String showAppSettings( Model model, HttpServletRequest request )
    {
        LOG.info( "Method showAppSettings of OrganizationManagementController called" );

        User user = sessionHelper.getCurrentUser();
        HttpSession session = request.getSession( false );
        List<VerticalCrmMapping> mappings;
        try {
            try {
                mappings = organizationManagementService.getCrmMapping( user );
            } catch ( InvalidInputException e ) {
                throw new InvalidInputException( "Exception occured while fetching vertical crm mappings", e.getMessage(), e );
            }
            long entityId = 0;
            String entityIdStr = request.getParameter( "entityId" );
            if ( entityIdStr == null || entityIdStr.isEmpty() ) {
                entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            } else {
                try {
                    if ( entityIdStr != null && !entityIdStr.equals( "" ) ) {
                        entityId = Long.parseLong( entityIdStr );
                    } else {
                        throw new NumberFormatException();
                    }
                } catch ( NumberFormatException e ) {
                    LOG.error( "Number format exception occurred while parsing the entity id. Reason :" + e.getMessage(), e );
                }
            }

            // Set the app settings in model
            AccountType accountType = (AccountType) session.getAttribute( CommonConstants.ACCOUNT_TYPE_IN_SESSION );
            OrganizationUnitSettings unitSettings = null;
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            int accountMasterId = accountType.getValue();
            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN )
                || accountMasterId == CommonConstants.ACCOUNTS_MASTER_INDIVIDUAL ) {
                unitSettings = organizationManagementService.getCompanySettings( user.getCompany().getCompanyId() );
            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                unitSettings = organizationManagementService.getRegionSettings( entityId );
            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                unitSettings = organizationManagementService.getBranchSettingsDefault( entityId );
            } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
                unitSettings = userManagementService.getUserSettings( user.getUserId() );
            }
            model.addAttribute( CommonConstants.USER_APP_SETTINGS, unitSettings );

        } catch ( NonFatalException e ) {
            LOG.error( "NonfatalException while showing app settings. Reason: " + e.getMessage(), e );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
            return JspResolver.MESSAGE_HEADER;
        }
        model.addAttribute( "crmMappings", mappings );

        return JspResolver.APP_SETTINGS;
    }


    /**
     * Method to show Company settings on edit company
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/showcompanysettings", method = RequestMethod.GET)
    public String showCompanySettings( Model model, HttpServletRequest request )
    {
        LOG.info( "Method showCompanySettings of UserManagementController called" );
        HttpSession session = request.getSession( false );
        User user = sessionHelper.getCurrentUser();

        AccountType accountType = (AccountType) session.getAttribute( CommonConstants.ACCOUNT_TYPE_IN_SESSION );

        long entityId = 0;
        String entityIdStr = request.getParameter( "entityId" );
        if ( entityIdStr == null || entityIdStr.isEmpty() ) {
            entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        } else {
            try {
                if ( entityIdStr != null && !entityIdStr.equals( "" ) ) {
                    entityId = Long.parseLong( entityIdStr );
                } else {
                    throw new NumberFormatException();
                }
            } catch ( NumberFormatException e ) {
                LOG.error( "Number format exception occurred while parsing the entity id. Reason :" + e.getMessage(), e );
            }
        }
        String collectionName = "";
        String entityType = request.getParameter( "entityType" );
        if ( entityType == null || entityType.isEmpty() ) {
            entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        }

        sessionHelper.updateSelectedProfile( session, entityId, entityType );

        OrganizationUnitSettings unitSettings = null;
        int accountMasterId = accountType.getValue();
        try {

            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN )
                || accountMasterId == CommonConstants.ACCOUNTS_MASTER_INDIVIDUAL ) {
                collectionName = MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION;
                unitSettings = organizationManagementService.getCompanySettings( user.getCompany().getCompanyId() );
            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                collectionName = MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION;
                unitSettings = organizationManagementService.getRegionSettings( entityId );
            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                collectionName = MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION;
                unitSettings = organizationManagementService.getBranchSettingsDefault( entityId );
            } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
                collectionName = MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION;
                unitSettings = userManagementService.getUserSettings( user.getUserId() );
            }

            SurveySettings surveySettings = null;
            AgentSettings agentSettings = null;
            // In case of individual account, the survey settings should be taken from agent collection
            if ( accountMasterId == CommonConstants.ACCOUNTS_MASTER_INDIVIDUAL ) {
                agentSettings = userManagementService.getUserSettings( user.getUserId() );
                surveySettings = agentSettings.getSurvey_settings();
            } else {
                surveySettings = unitSettings.getSurvey_settings();
            }
            if ( surveySettings == null ) {
                surveySettings = new SurveySettings();

            }
            if ( surveySettings.getShow_survey_above_score() == 0f ) {
                surveySettings.setShow_survey_above_score( CommonConstants.DEFAULT_AUTOPOST_SCORE );
                surveySettings.setAuto_post_score( CommonConstants.DEFAULT_AUTOPOST_SCORE );
                surveySettings.setAutoPostEnabled( true );
                if ( accountMasterId == CommonConstants.ACCOUNTS_MASTER_INDIVIDUAL ) {
                    agentSettings.setSurvey_settings( surveySettings );
                    organizationManagementService.updateScoreForSurvey(
                        MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, agentSettings, surveySettings );
                } else {
                    unitSettings.setSurvey_settings( surveySettings );
                    organizationManagementService.updateScoreForSurvey( collectionName, unitSettings, surveySettings );
                }
            }

            model.addAttribute( "columnName", entityType );
            model.addAttribute( "columnValue", entityId );

            model.addAttribute( "autoPostEnabled", false );

            if ( surveySettings != null ) {
                model.addAttribute( "autoPostEnabled", surveySettings.isAutoPostEnabled() );
                model.addAttribute( "minpostscore", surveySettings.getShow_survey_above_score() );
            }
            surveySettings = organizationManagementService.retrieveDefaultSurveyProperties();
            model.addAttribute( "defaultSurveyProperties", surveySettings );
            session.setAttribute( CommonConstants.USER_ACCOUNT_SETTINGS, unitSettings );

        } catch ( InvalidInputException | NoRecordsFetchedException e ) {
            LOG.error( "NonFatalException while fetching profile details. Reason :" + e.getMessage(), e );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( DisplayMessageConstants.GENERAL_ERROR, DisplayMessageType.ERROR_MESSAGE ) );
            return JspResolver.MESSAGE_HEADER;
        }

        return JspResolver.EDIT_SETTINGS;
    }


    /**
     * Method to save encompass details / CRM info
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/saveencompassdetails", method = RequestMethod.POST)
    @ResponseBody
    public String saveEncompassDetails( Model model, HttpServletRequest request )
    {
        LOG.info( "Saving encompass details" );
        User user = sessionHelper.getCurrentUser();
        request.setAttribute( "saveencompassdetails", "true" );

        String encompassUsername = request.getParameter( "encompass-username" );
        String encompassPassword = request.getParameter( "encompass-password" );
        String encompassUrl = request.getParameter( "encompass-url" );
        String encompassFieldId = request.getParameter( "encompass-fieldId" );
        String state = request.getParameter( "encompass-state" );
        Map<String, Object> responseMap = new HashMap<String, Object>();
        String message;
        boolean status = true;

        try {

            if ( encompassUsername == null || encompassUsername.isEmpty() ) {
                throw new InvalidInputException( "User name can not be empty" );
            }
            if ( encompassPassword == null || encompassPassword.isEmpty() ) {
                throw new InvalidInputException( "Password can not be empty" );
            }
            if ( encompassUrl == null || encompassUrl.isEmpty() ) {
                throw new InvalidInputException( "Url can not be empty" );
            }
            if ( encompassFieldId == null || encompassFieldId.isEmpty() ) {
                LOG.info( "Field Id is empty" );
                encompassFieldId = CommonConstants.ENCOMPASS_DEFAULT_FEILD_ID;
            }
            if ( state == null || state.isEmpty() || state.equals( CommonConstants.ENCOMPASS_DRY_RUN_STATE ) ) {
                state = CommonConstants.ENCOMPASS_DRY_RUN_STATE;
            } else {
                state = CommonConstants.ENCOMPASS_PRODUCTION_STATE;
            }

            // TODO : Encrypting the password
            String cipherPassword = encompassPassword;

            OrganizationUnitSettings companySettings = organizationManagementService
                .getCompanySettings( user.getCompany().getCompanyId() );
            EncompassCrmInfo encompassCrmInfo;
            if ( companySettings.getCrm_info() != null
                && companySettings.getCrm_info().getCrm_source().equals( CommonConstants.CRM_INFO_SOURCE_ENCOMPASS ) ) {
                encompassCrmInfo = (EncompassCrmInfo) companySettings.getCrm_info();
            } else {
                encompassCrmInfo = new EncompassCrmInfo();
                encompassCrmInfo.setCrm_source( CommonConstants.CRM_INFO_SOURCE_ENCOMPASS );
                encompassCrmInfo.setState( state );
                encompassCrmInfo.setConnection_successful( true );
                encompassCrmInfo.setCompanyId( companySettings.getIden() );
            }
            encompassCrmInfo.setCrm_username( encompassUsername );
            encompassCrmInfo.setCrm_fieldId( encompassFieldId );
            encompassCrmInfo.setCrm_password( cipherPassword );
            encompassCrmInfo.setUrl( encompassUrl );

            organizationManagementService.updateCRMDetails( companySettings, encompassCrmInfo,
                "com.realtech.socialsurvey.core.entities.EncompassCrmInfo" );

            // set the updated settings value in session with plain password
            encompassCrmInfo.setCrm_password( cipherPassword );
            companySettings.setCrm_info( encompassCrmInfo );
            message = messageUtils.getDisplayMessage( DisplayMessageConstants.ENCOMPASS_DATA_UPDATE_SUCCESSFUL,
                DisplayMessageType.SUCCESS_MESSAGE ).getMessage();
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while testing encompass detials. Reason : " + e.getMessage(), e );
            status = false;
            message = messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ).getMessage();
        }
        responseMap.put( "status", status );
        responseMap.put( "message", message );
        String response = new Gson().toJson( responseMap );
        return response;
    }


    /**
     * Method to enable an encompass connection
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/enableencompassdetails", method = RequestMethod.POST)
    @ResponseBody
    public String enableEncompassConnection( Model model, HttpServletRequest request )
    {
        LOG.info( "Updating encompass details to 'Enabled'" );
        User user = sessionHelper.getCurrentUser();
        String message;

        try {
            OrganizationUnitSettings companySettings = organizationManagementService
                .getCompanySettings( user.getCompany().getCompanyId() );
            EncompassCrmInfo encompassCrmInfo = (EncompassCrmInfo) companySettings.getCrm_info();
            encompassCrmInfo.setState( CommonConstants.ENCOMPASS_PRODUCTION_STATE );
            encompassCrmInfo.setNumberOfDays( 0 );
            encompassCrmInfo.setEmailAddressForReport( null );
            encompassCrmInfo.setGenerateReport( false );
            organizationManagementService.updateCRMDetails( companySettings, encompassCrmInfo,
                "com.realtech.socialsurvey.core.entities.EncompassCrmInfo" );
            message = messageUtils
                .getDisplayMessage( DisplayMessageConstants.ENCOMPASS_ENABLE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE )
                .getMessage();
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while saving encompass detials. Reason : " + e.getMessage(), e );
            message = messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ).getMessage();
        }
        return message;
    }


    /**
     * Method to disable an encompass connection
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/disableencompassdetails", method = RequestMethod.POST)
    @ResponseBody
    public String disableEncompassConnection( Model model, HttpServletRequest request )
    {
        LOG.info( "Updating encompass details to 'Disabled'" );
        User user = sessionHelper.getCurrentUser();
        String message;

        try {
            OrganizationUnitSettings companySettings = organizationManagementService
                .getCompanySettings( user.getCompany().getCompanyId() );
            EncompassCrmInfo encompassCrmInfo = (EncompassCrmInfo) companySettings.getCrm_info();
            encompassCrmInfo.setState( CommonConstants.ENCOMPASS_DRY_RUN_STATE );
            organizationManagementService.updateCRMDetails( companySettings, encompassCrmInfo,
                "com.realtech.socialsurvey.core.entities.EncompassCrmInfo" );
            message = messageUtils
                .getDisplayMessage( DisplayMessageConstants.ENCOMPASS_DISABLE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE )
                .getMessage();
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while disabling encompass. Reason : " + e.getMessage(), e );
            message = messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ).getMessage();
        }
        return message;
    }


    /**
     * Method to enable report generation for encompass
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/enableencompassreportgeneration", method = RequestMethod.POST)
    @ResponseBody
    public String enableEncompassReportGeneration( Model model, HttpServletRequest request )
    {
        LOG.info( "Enabling report generation for encompass details" );
        User user = sessionHelper.getCurrentUser();
        String message;
        try {
            String numOfDaysStr = request.getParameter( "encompassNoOfdays" );

            if ( numOfDaysStr == null || numOfDaysStr.isEmpty() ) {
                throw new InvalidInputException( "Number of days cannot be empty" );
            }

            int numOfDays = Integer.parseInt( numOfDaysStr );
            String emailIdForReport = request.getParameter( "encompassReportEmail" );
            if ( emailIdForReport == null || emailIdForReport.isEmpty() ) {
                throw new InvalidInputException( "emailId cannot be empty" );
            }
            OrganizationUnitSettings companySettings = organizationManagementService
                .getCompanySettings( user.getCompany().getCompanyId() );
            EncompassCrmInfo encompassCrmInfo = (EncompassCrmInfo) companySettings.getCrm_info();
            encompassCrmInfo.setNumberOfDays( numOfDays );
            encompassCrmInfo.setEmailAddressForReport( emailIdForReport );
            encompassCrmInfo.setGenerateReport( true );
            organizationManagementService.updateCRMDetails( companySettings, encompassCrmInfo,
                "com.realtech.socialsurvey.core.entities.EncompassCrmInfo" );
            message = messageUtils.getDisplayMessage( DisplayMessageConstants.ENCOMPASS_GENERATE_REPORT_SUCCESSFUL,
                DisplayMessageType.SUCCESS_MESSAGE ).getMessage();
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while enabling report generation for encompass. Reason : " + e.getMessage(), e );
            message = e.getMessage();
        }
        return message;
    }


    /**
     * Method to test encompass details / CRM info NO LONGER USED
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/testencompassconnection", method = RequestMethod.POST)
    @ResponseBody
    public String testEncompassConnection( Model model, HttpServletRequest request ) throws NonFatalException
    {
        LOG.info( "Testing connections" );
        String message;
        try {
            // validate the parameters
            if ( !validateEncompassParameters( request ) ) {
                // TODO: code to test connection
            }
            message = messageUtils.getDisplayMessage( DisplayMessageConstants.ENCOMPASS_CONNECTION_SUCCESSFUL,
                DisplayMessageType.SUCCESS_MESSAGE ).getMessage();
        } catch ( NonFatalException e ) {
            if ( request.getAttribute( "saveencompassdetails" ) != null ) {
                throw e;
            } else {
                LOG.error( "NonFatalException while testing encompass detials. Reason : " + e.getMessage(), e );
            }
            message = messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ).getMessage();
        }
        return message;
    }


    /**
     * Method to validate encompass details / CRM info NO LONGER USED
     * 
     * @param request
     * @return
     */
    private boolean validateEncompassParameters( HttpServletRequest request ) throws InvalidInputException
    {
        LOG.debug( "Validating encompass parameters" );
        String userName = request.getParameter( "encompass-username" );
        String password = request.getParameter( "encompass-password" );
        String url = request.getParameter( "encompass-url" );
        if ( userName == null || userName.isEmpty() || password == null || password.isEmpty() || url == null
            || url.isEmpty() ) {
            LOG.warn( "Encompass validation failed" );
            throw new InvalidInputException( "All fields not set for encompass", DisplayMessageConstants.GENERAL_ERROR );
        }
        LOG.debug( "Encompass validation passed." );
        return true;
    }


    /**
     * Method to save survey Mailbody content
     * 
     * @param model
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/savesurveyparticipationmail", method = RequestMethod.POST)
    public String updateSurveyParticipationMailBody( Model model, HttpServletRequest request )
    {
        LOG.info( "Saving survey participation mail body" );
        User user = sessionHelper.getCurrentUser();
        HttpSession session = request.getSession( false );
        String mailCategory = request.getParameter( "mailcategory" );
        String mailSubject = null;
        String mailBody = null;
        String message = "";

        try {
            OrganizationUnitSettings companySettings = organizationManagementService
                .getCompanySettings( user.getCompany().getCompanyId() );
            MailContentSettings updatedMailContentSettings = null;
            if ( mailCategory != null && mailCategory.equals( "participationmail" ) ) {

                mailSubject = request.getParameter( "survey-mailcontent-subject" );
                if ( mailSubject == null || mailSubject.isEmpty() ) {
                    LOG.warn( "Survey participation mail subject is blank." );
                    throw new InvalidInputException( "Survey participation mail subject is blank.",
                        DisplayMessageConstants.GENERAL_ERROR );
                }

                mailBody = request.getParameter( "survey-participation-mailcontent" );
                if ( mailBody == null || mailBody.isEmpty() ) {
                    LOG.warn( "Survey participation mail body is blank." );
                    throw new InvalidInputException( "Survey participation mail body is blank.",
                        DisplayMessageConstants.GENERAL_ERROR );
                }

                updatedMailContentSettings = organizationManagementService.updateSurveyParticipationMailBody( companySettings,
                    mailSubject, mailBody, CommonConstants.SURVEY_MAIL_BODY_CATEGORY );

                // set the value back in session
                session.setAttribute( CommonConstants.SURVEY_PARTICIPATION_MAIL_SUBJECT_IN_SESSION, mailSubject );
                session.setAttribute( CommonConstants.SURVEY_PARTICIPATION_MAIL_BODY_IN_SESSION, mailBody );

                message = messageUtils
                    .getDisplayMessage( DisplayMessageConstants.SURVEY_PARTICIPATION_MAILBODY_UPDATE_SUCCESSFUL,
                        DisplayMessageType.SUCCESS_MESSAGE )
                    .getMessage();
            }

            else if ( mailCategory != null && mailCategory.equals( "participationremindermail" ) ) {

                mailSubject = request.getParameter( "survey-mailreminder-subject" );
                if ( mailSubject == null || mailSubject.isEmpty() ) {
                    LOG.warn( "Survey participation reminder mail subject is blank." );
                    throw new InvalidInputException( "Survey participation reminder mail subject is blank.",
                        DisplayMessageConstants.GENERAL_ERROR );
                }

                mailBody = request.getParameter( "survey-participation-reminder-mailcontent" );
                if ( mailBody == null || mailBody.isEmpty() ) {
                    LOG.warn( "Survey participation reminder mail body is blank." );
                    throw new InvalidInputException( "Survey participation reminder mail body is blank.",
                        DisplayMessageConstants.GENERAL_ERROR );
                }

                updatedMailContentSettings = organizationManagementService.updateSurveyParticipationMailBody( companySettings,
                    mailSubject, mailBody, CommonConstants.SURVEY_REMINDER_MAIL_BODY_CATEGORY );

                // set the value back in session
                session.setAttribute( CommonConstants.SURVEY_PARTICIPATION_REMINDER_MAIL_SUBJECT_IN_SESSION, mailSubject );
                session.setAttribute( CommonConstants.SURVEY_PARTICIPATION_REMINDER_MAIL_BODY_IN_SESSION, mailBody );

                message = messageUtils
                    .getDisplayMessage( DisplayMessageConstants.SURVEY_PARTICIPATION_REMINDERMAILBODY_UPDATE_SUCCESSFUL,
                        DisplayMessageType.SUCCESS_MESSAGE )
                    .getMessage();
            }

            else if ( mailCategory != null && mailCategory.equals( "surveycompletionmail" ) ) {

                mailSubject = request.getParameter( "survey-completion-subject" );
                if ( mailSubject == null || mailSubject.isEmpty() ) {
                    LOG.warn( "Survey Completion  mail subject is blank." );
                    throw new InvalidInputException( "Survey completion mail subject is blank.",
                        DisplayMessageConstants.GENERAL_ERROR );
                }

                mailBody = request.getParameter( "survey-completion-mailcontent" );
                if ( mailBody == null || mailBody.isEmpty() ) {
                    LOG.warn( "Survey Completion mail body is blank." );
                    throw new InvalidInputException( "Survey completion mail body is blank.",
                        DisplayMessageConstants.GENERAL_ERROR );
                }

                updatedMailContentSettings = organizationManagementService.updateSurveyParticipationMailBody( companySettings,
                    mailSubject, mailBody, CommonConstants.SURVEY_COMPLETION_MAIL_BODY_CATEGORY );

                // set the value back in session
                session.setAttribute( CommonConstants.SURVEY_COMPLETION_MAIL_SUBJECT_IN_SESSION, mailSubject );
                session.setAttribute( CommonConstants.SURVEY_COMPLETION_MAIL_BODY_IN_SESSION, mailBody );

                message = messageUtils.getDisplayMessage( DisplayMessageConstants.SURVEY_COMPLETION_MAILBODY_UPDATE_SUCCESSFUL,
                    DisplayMessageType.SUCCESS_MESSAGE ).getMessage();
            }

            else if ( mailCategory != null && mailCategory.equals( "surveycompletionunpleasantmail" ) ) {

                mailSubject = request.getParameter( "survey-completion-unpleasant-subject" );
                if ( mailSubject == null || mailSubject.isEmpty() ) {
                    LOG.warn( "Survey Completion Unpleasant mail subject is blank." );
                    throw new InvalidInputException( "Survey completion unpleasant mail subject is blank.",
                        DisplayMessageConstants.GENERAL_ERROR );
                }

                mailBody = request.getParameter( "survey-completion-unpleasant-mailcontent" );
                //                if ( mailBody == null || mailBody.isEmpty() ) {
                //                    LOG.warn( "Survey Completion Unpleasant mail body is blank." );
                //                    throw new InvalidInputException( "Survey completion Unpleasant mail body is blank.",
                //                        DisplayMessageConstants.GENERAL_ERROR );
                //                }

                updatedMailContentSettings = organizationManagementService.updateSurveyParticipationMailBody( companySettings,
                    mailSubject, mailBody, CommonConstants.SURVEY_COMPLETION_UNPLEASANT_MAIL_BODY_CATEGORY );

                // set the value back in session
                session.setAttribute( CommonConstants.SURVEY_COMPLETION_UNPLEASANT_MAIL_SUBJECT_IN_SESSION, mailSubject );
                session.setAttribute( CommonConstants.SURVEY_COMPLETION_UNPLEASANT_MAIL_BODY_IN_SESSION, mailBody );

                message = messageUtils
                    .getDisplayMessage( DisplayMessageConstants.SURVEY_COMPLETION_UNPLEASANT_MAILBODY_UPDATE_SUCCESSFUL,
                        DisplayMessageType.SUCCESS_MESSAGE )
                    .getMessage();
            }

            else if ( mailCategory != null && mailCategory.equals( "socialpostremindermail" ) ) {

                mailSubject = request.getParameter( "social-post-reminder-subject" );
                if ( mailSubject == null || mailSubject.isEmpty() ) {
                    LOG.warn( "Social post reminder mail subject is blank." );
                    throw new InvalidInputException( "Social post reminder mail subject is blank.",
                        DisplayMessageConstants.GENERAL_ERROR );
                }

                mailBody = request.getParameter( "social-post-reminder-mailcontent" );
                if ( mailBody == null || mailBody.isEmpty() ) {
                    LOG.warn( "Social post reminder mail body is blank." );
                    throw new InvalidInputException( "Social post reminder mail body is blank.",
                        DisplayMessageConstants.GENERAL_ERROR );
                }

                updatedMailContentSettings = organizationManagementService.updateSurveyParticipationMailBody( companySettings,
                    mailSubject, mailBody, CommonConstants.SOCIAL_POST_REMINDER_MAIL_BODY_CATEGORY );

                // set the value back in session
                session.setAttribute( CommonConstants.SOCIAL_POST_REMINDER_MAIL_SUBJECT_IN_SESSION, mailSubject );
                session.setAttribute( CommonConstants.SOCIAL_POST_REMINDER_MAIL_BODY_IN_SESSION, mailBody );

                message = messageUtils
                    .getDisplayMessage( DisplayMessageConstants.SOCIAL_POST_REMINDER_MAILBODY_UPDATE_SUCCESSFUL,
                        DisplayMessageType.SUCCESS_MESSAGE )
                    .getMessage();
            }

            else if ( mailCategory != null && mailCategory.equals( "restartsurveymail" ) ) {

                mailSubject = request.getParameter( "incomplete-survey-mailreminder-subject" );
                if ( mailSubject == null || mailSubject.isEmpty() ) {
                    LOG.warn( "Incomplete survey reminder  mail subject is blank." );
                    throw new InvalidInputException( "Incomplete survey reminder mail subject is blank.",
                        DisplayMessageConstants.GENERAL_ERROR );
                }

                mailBody = request.getParameter( "incomplete-survey-reminder-mailcontent" );
                if ( mailBody == null || mailBody.isEmpty() ) {
                    LOG.warn( "Incomplete survey reminder mail body is blank." );
                    throw new InvalidInputException( "Incomplete survey reminder mail body is blank.",
                        DisplayMessageConstants.GENERAL_ERROR );
                }

                updatedMailContentSettings = organizationManagementService.updateSurveyParticipationMailBody( companySettings,
                    mailSubject, mailBody, CommonConstants.RESTART_SURVEY_MAIL_BODY_CATEGORY );

                // set the value back in session
                session.setAttribute( CommonConstants.RESTART_SURVEY_MAIL_SUBJECT_IN_SESSION, mailSubject );
                session.setAttribute( CommonConstants.RESTART_SURVEY_MAIL_BODY_IN_SESSION, mailBody );

                message = messageUtils.getDisplayMessage( DisplayMessageConstants.RESTART_SURVEY_MAILBODY_UPDATE_SUCCESSFUL,
                    DisplayMessageType.SUCCESS_MESSAGE ).getMessage();
            }
            // update the mail content settings in session
            companySettings.setMail_content( updatedMailContentSettings );
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while saving survey participation mail body. Reason : " + e.getMessage(), e );
            message = messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ).getMessage();
        }

        return message;
    }


    /**
     * Method to save survey Mailbody content
     * 
     * @param model
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/revertsurveyparticipationmail", method = RequestMethod.POST)
    public String revertSurveyParticipationMailBody( Model model, HttpServletRequest request )
    {
        LOG.info( "Reverting survey participation mail body" );
        User user = sessionHelper.getCurrentUser();
        HttpSession session = request.getSession( false );
        String mailCategory = request.getParameter( "mailcategory" );
        String mailSubject = null;
        String mailBody = null;
        String message = "";

        try {
            OrganizationUnitSettings companySettings = organizationManagementService
                .getCompanySettings( user.getCompany().getCompanyId() );
            MailContent defaultMailContent = null;
            if ( mailCategory != null && mailCategory.equals( "participationmail" ) ) {
                defaultMailContent = organizationManagementService.deleteMailBodyFromSetting( companySettings,
                    CommonConstants.SURVEY_MAIL_BODY_CATEGORY );

                mailBody = defaultMailContent.getMail_body();
                mailBody = emailFormatHelper.replaceEmailBodyWithParams( mailBody,
                    organizationManagementService.getSurveyParamOrder( CommonConstants.SURVEY_MAIL_BODY_CATEGORY ) );
                // mailBody = mailBody.replaceAll("\\[LogoUrl\\]", applicationLogoUrl);

                mailSubject = defaultMailContent.getMail_subject();
                message = messageUtils
                    .getDisplayMessage( DisplayMessageConstants.SURVEY_PARTICIPATION_MAILBODY_UPDATE_SUCCESSFUL,
                        DisplayMessageType.SUCCESS_MESSAGE )
                    .getMessage();

                session.setAttribute( CommonConstants.SURVEY_PARTICIPATION_MAIL_BODY_IN_SESSION, mailBody );
                session.setAttribute( CommonConstants.SURVEY_PARTICIPATION_MAIL_SUBJECT_IN_SESSION, mailSubject );
            }

            else if ( mailCategory != null && mailCategory.equals( "participationremindermail" ) ) {
                defaultMailContent = organizationManagementService.deleteMailBodyFromSetting( companySettings,
                    CommonConstants.SURVEY_REMINDER_MAIL_BODY_CATEGORY );

                mailBody = defaultMailContent.getMail_body();
                mailBody = emailFormatHelper.replaceEmailBodyWithParams( mailBody,
                    organizationManagementService.getSurveyParamOrder( CommonConstants.SURVEY_REMINDER_MAIL_BODY_CATEGORY ) );
                // mailBody = mailBody.replaceAll("\\[LogoUrl\\]", applicationLogoUrl);

                mailSubject = defaultMailContent.getMail_subject();
                message = messageUtils
                    .getDisplayMessage( DisplayMessageConstants.SURVEY_PARTICIPATION_REMINDERMAILBODY_UPDATE_SUCCESSFUL,
                        DisplayMessageType.SUCCESS_MESSAGE )
                    .getMessage();

                session.setAttribute( CommonConstants.SURVEY_PARTICIPATION_REMINDER_MAIL_BODY_IN_SESSION, mailBody );
                session.setAttribute( CommonConstants.SURVEY_PARTICIPATION_REMINDER_MAIL_SUBJECT_IN_SESSION, mailSubject );
            }

            else if ( mailCategory != null && mailCategory.equals( "surveycompletionmail" ) ) {
                defaultMailContent = organizationManagementService.deleteMailBodyFromSetting( companySettings,
                    CommonConstants.SURVEY_COMPLETION_MAIL_BODY_CATEGORY );

                mailBody = defaultMailContent.getMail_body();
                mailBody = emailFormatHelper.replaceEmailBodyWithParams( mailBody,
                    organizationManagementService.getSurveyParamOrder( CommonConstants.SURVEY_COMPLETION_MAIL_BODY_CATEGORY ) );
                // mailBody = mailBody.replaceAll("\\[LogoUrl\\]", applicationLogoUrl);

                mailSubject = defaultMailContent.getMail_subject();
                message = messageUtils.getDisplayMessage( DisplayMessageConstants.SURVEY_COMPLETION_MAILBODY_UPDATE_SUCCESSFUL,
                    DisplayMessageType.SUCCESS_MESSAGE ).getMessage();

                session.setAttribute( CommonConstants.SURVEY_COMPLETION_MAIL_BODY_IN_SESSION, mailBody );
                session.setAttribute( CommonConstants.SURVEY_COMPLETION_MAIL_SUBJECT_IN_SESSION, mailSubject );
            }

            else if ( mailCategory != null && mailCategory.equals( "surveycompletionunpleasantmail" ) ) {
                defaultMailContent = organizationManagementService.deleteMailBodyFromSetting( companySettings,
                    CommonConstants.SURVEY_COMPLETION_UNPLEASANT_MAIL_BODY_CATEGORY );

                mailBody = defaultMailContent.getMail_body();
                mailBody = emailFormatHelper.replaceEmailBodyWithParams( mailBody, organizationManagementService
                    .getSurveyParamOrder( CommonConstants.SURVEY_COMPLETION_UNPLEASANT_MAIL_BODY_CATEGORY ) );
                // mailBody = mailBody.replaceAll("\\[LogoUrl\\]", applicationLogoUrl);

                mailSubject = defaultMailContent.getMail_subject();
                message = messageUtils
                    .getDisplayMessage( DisplayMessageConstants.SURVEY_COMPLETION_UNPLEASANT_MAILBODY_UPDATE_SUCCESSFUL,
                        DisplayMessageType.SUCCESS_MESSAGE )
                    .getMessage();

                session.setAttribute( CommonConstants.SURVEY_COMPLETION_UNPLEASANT_MAIL_BODY_IN_SESSION, mailBody );
                session.setAttribute( CommonConstants.SURVEY_COMPLETION_UNPLEASANT_MAIL_SUBJECT_IN_SESSION, mailSubject );
            }

            else if ( mailCategory != null && mailCategory.equals( "socialpostremindermail" ) ) {
                defaultMailContent = organizationManagementService.deleteMailBodyFromSetting( companySettings,
                    CommonConstants.SOCIAL_POST_REMINDER_MAIL_BODY_CATEGORY );

                mailBody = defaultMailContent.getMail_body();
                mailBody = emailFormatHelper.replaceEmailBodyWithParams( mailBody, organizationManagementService
                    .getSurveyParamOrder( CommonConstants.SOCIAL_POST_REMINDER_MAIL_BODY_CATEGORY ) );
                //mailBody = mailBody.replaceAll("\\[LogoUrl\\]", applicationLogoUrl);

                mailSubject = defaultMailContent.getMail_subject();
                message = messageUtils
                    .getDisplayMessage( DisplayMessageConstants.SOCIAL_POST_REMINDER_MAILBODY_UPDATE_SUCCESSFUL,
                        DisplayMessageType.SUCCESS_MESSAGE )
                    .getMessage();

                session.setAttribute( CommonConstants.SOCIAL_POST_REMINDER_MAIL_BODY_IN_SESSION, mailBody );
                session.setAttribute( CommonConstants.SOCIAL_POST_REMINDER_MAIL_SUBJECT_IN_SESSION, mailSubject );
            }

            else if ( mailCategory != null && mailCategory.equals( "restartsurveymail" ) ) {
                defaultMailContent = organizationManagementService.deleteMailBodyFromSetting( companySettings,
                    CommonConstants.RESTART_SURVEY_MAIL_BODY_CATEGORY );

                mailBody = defaultMailContent.getMail_body();
                mailBody = emailFormatHelper.replaceEmailBodyWithParams( mailBody,
                    organizationManagementService.getSurveyParamOrder( CommonConstants.RESTART_SURVEY_MAIL_BODY_CATEGORY ) );
                // mailBody = mailBody.replaceAll("\\[LogoUrl\\]", applicationLogoUrl);

                mailSubject = defaultMailContent.getMail_subject();
                message = messageUtils.getDisplayMessage( DisplayMessageConstants.RESTART_SURVEY_MAILBODY_UPDATE_SUCCESSFUL,
                    DisplayMessageType.SUCCESS_MESSAGE ).getMessage();

                session.setAttribute( CommonConstants.RESTART_SURVEY_MAIL_BODY_IN_SESSION, mailBody );
                session.setAttribute( CommonConstants.RESTART_SURVEY_MAIL_SUBJECT_IN_SESSION, mailSubject );
            }

        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while reverting survey participation mail body. Reason : " + e.getMessage(), e );
            message = messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ).getMessage();
        }

        return message;
    }


    /**
     * Method to update Survey Settings
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/updatesurveysettings", method = RequestMethod.POST)
    @ResponseBody
    public String updateSurveySettings( Model model, HttpServletRequest request )
    {
        LOG.info( "Updating Survey Settings" );
        String ratingCategory = request.getParameter( "ratingcategory" );
        String autopost = request.getParameter( "autopost" );
        SurveySettings originalSurveySettings = null;
        String message = "";
        HttpSession session = request.getSession();
        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        try {
            boolean isAutopostEnabled = Boolean.parseBoolean( autopost );
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
                throw new InvalidInputException( "Invalid Collection Type" );
            }

            if ( ratingCategory != null && ratingCategory.equals( "rating-auto-post" ) ) {
                double autopostRating = Double.parseDouble( request.getParameter( "rating-auto-post" ) );
                if ( autopostRating == 0 ) {
                    LOG.warn( "Auto Post rating score is 0." );
                    throw new InvalidInputException( "Auto Post rating score is 0.", DisplayMessageConstants.GENERAL_ERROR );
                }

                originalSurveySettings = unitSettings.getSurvey_settings();
                if ( originalSurveySettings == null ) {
                    originalSurveySettings = new SurveySettings();
                }
                originalSurveySettings.setAutoPostEnabled( isAutopostEnabled );
                originalSurveySettings.setAuto_post_score( (float) autopostRating );

                LOG.info( "Updating Survey Settings Post score" );
                message = messageUtils.getDisplayMessage( DisplayMessageConstants.SURVEY_AUTO_POST_SCORE_UPDATE_SUCCESSFUL,
                    DisplayMessageType.SUCCESS_MESSAGE ).getMessage();
            }

            else if ( ratingCategory != null && ratingCategory.equals( "rating-min-post" ) ) {
                double minPostRating = Double.parseDouble( request.getParameter( "rating-min-post" ) );
                if ( minPostRating == 0 ) {
                    LOG.warn( "Minimum Post rating score is 0." );
                    throw new InvalidInputException( "Mimimum Post rating score is 0.", DisplayMessageConstants.GENERAL_ERROR );
                }

                originalSurveySettings = unitSettings.getSurvey_settings();
                if ( originalSurveySettings == null ) {
                    originalSurveySettings = new SurveySettings();
                }
                originalSurveySettings.setAutoPostEnabled( isAutopostEnabled );
                originalSurveySettings.setShow_survey_above_score( (float) minPostRating );
                originalSurveySettings.setAuto_post_score( (float) minPostRating );

                LOG.info( "Updating Survey Settings Min score" );
                message = messageUtils.getDisplayMessage( DisplayMessageConstants.SURVEY_MIN_POST_SCORE_UPDATE_SUCCESSFUL,
                    DisplayMessageType.SUCCESS_MESSAGE ).getMessage();
            }

            if ( organizationManagementService.updateScoreForSurvey( collectionName, unitSettings, originalSurveySettings ) ) {
                unitSettings.setSurvey_settings( originalSurveySettings );
                LOG.info( "Updated Survey Settings" );
            }
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while updating survey settings. Reason : " + e.getMessage(), e );
            message = messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ).getMessage();
        }

        return message;
    }


    @SuppressWarnings ( "unused")
    private LockSettings updateLockSettings( LockSettings parentLock, LockSettings lockSettings, boolean status )
    {
        if ( !parentLock.getIsLogoLocked() ) {
            lockSettings.setLogoLocked( status );
        }
        return lockSettings;
    }


    @RequestMapping ( value = "/updateautopostforsurvey", method = RequestMethod.POST)
    @ResponseBody
    public String updateAutoPostForSurvey( HttpServletRequest request )
    {
        LOG.info( "Method to update autopost for a survey started" );
        HttpSession session = request.getSession();
        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );

        try {
            String autopost = request.getParameter( "autopost" );
            String collectionName = "";
            boolean isAutoPostEnabled = false;
            if ( autopost != null && !autopost.isEmpty() ) {
                isAutoPostEnabled = Boolean.parseBoolean( autopost );

                OrganizationUnitSettings unitSettings = null;
                /*
                 * OrganizationUnitSettings companySettings =
                 * organizationManagementService.getCompanySettings( user.getCompany()
                 * .getCompanyId() );
                 */
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
                    throw new InvalidInputException( "Invalid Collection Type" );
                }

                SurveySettings surveySettings = unitSettings.getSurvey_settings();
                surveySettings.setAutoPostEnabled( isAutoPostEnabled );
                if ( organizationManagementService.updateScoreForSurvey( collectionName, unitSettings, surveySettings ) ) {
                    unitSettings.setSurvey_settings( surveySettings );
                    LOG.info( "Updated Survey Settings" );
                }
            }
        } catch ( Exception e ) {
            LOG.error(
                "Exception occured in updateAutoPostForSurvey() while updating whether to enable autopost or not. Nested exception is ",
                e );
            return e.getMessage();
        }

        LOG.info( "Method to update autopost for a survey finished" );
        return "Successfully updated autopost setting";
    }


    /**
     * Method to update Survey Reminder Settings
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/updatesurveyremindersettings", method = RequestMethod.POST)
    @ResponseBody
    public String updateSurveyReminderSettings( Model model, HttpServletRequest request )
    {
        LOG.info( "Updating Survey Reminder Settings" );
        String mailCategory = request.getParameter( "mailcategory" );
        SurveySettings originalSurveySettings = null;
        String message = "";

        try {
            User user = sessionHelper.getCurrentUser();
            OrganizationUnitSettings companySettings = organizationManagementService
                .getCompanySettings( user.getCompany().getCompanyId() );

            if ( mailCategory != null && mailCategory.equals( "reminder-interval" ) ) {
                int reminderInterval = Integer.parseInt( request.getParameter( "reminder-interval" ) );
                if ( reminderInterval == 0 ) {
                    LOG.warn( "Reminder Interval is 0." );
                    throw new InvalidInputException( "Reminder Interval is 0.", DisplayMessageConstants.GENERAL_ERROR );
                }

                originalSurveySettings = companySettings.getSurvey_settings();
                if ( originalSurveySettings != null ) {
                    originalSurveySettings.setSurvey_reminder_interval_in_days( reminderInterval );
                }
                LOG.info( "Updating Survey Settings Reminder Interval" );
                message = messageUtils.getDisplayMessage( DisplayMessageConstants.SURVEY_REMINDER_INTERVAL_UPDATE_SUCCESSFUL,
                    DisplayMessageType.SUCCESS_MESSAGE ).getMessage();
            }

            else if ( mailCategory != null && mailCategory.equals( "reminder-needed" ) ) {
                boolean isReminderDisabled = Boolean.parseBoolean( request.getParameter( "reminder-needed-hidden" ) );

                originalSurveySettings = companySettings.getSurvey_settings();
                if ( originalSurveySettings != null ) {
                    originalSurveySettings.setReminderDisabled( isReminderDisabled );
                }
                LOG.info( "Updating Survey Settings Reminder Needed" );
                message = messageUtils.getDisplayMessage( DisplayMessageConstants.SURVEY_REMINDER_ENABLED_UPDATE_SUCCESSFUL,
                    DisplayMessageType.SUCCESS_MESSAGE ).getMessage();
            }

            else if ( mailCategory != null && mailCategory.equals( "post-reminder-interval" ) ) {
                int reminderInterval = Integer.parseInt( request.getParameter( "post-reminder-interval" ) );
                if ( reminderInterval == 0 ) {
                    LOG.warn( "Reminder Interval is 0." );
                    throw new InvalidInputException( "Reminder Interval is 0.",
                        DisplayMessageConstants.INVALID_SOCIAL_POST_REMINDER_ERROR );
                }

                if ( reminderInterval > 4 ) {
                    LOG.warn( "Reminder Interval is greater than 4." );
                    throw new InvalidInputException( "Reminder Interval is greater than 4.",
                        DisplayMessageConstants.INVALID_SOCIAL_POST_REMINDER_ERROR );
                }

                originalSurveySettings = companySettings.getSurvey_settings();
                if ( originalSurveySettings != null ) {
                    originalSurveySettings.setSocial_post_reminder_interval_in_days( reminderInterval );
                }
                LOG.info( "Updating Social Post Reminder Interval" );
                message = messageUtils
                    .getDisplayMessage( DisplayMessageConstants.SOCIAL_POST_REMINDER_INTERVAL_UPDATE_SUCCESSFUL,
                        DisplayMessageType.SUCCESS_MESSAGE )
                    .getMessage();
            }

            else if ( mailCategory != null && mailCategory.equals( "post-reminder-needed" ) ) {
                boolean isReminderDisabled = Boolean.parseBoolean( request.getParameter( "post-reminder-needed-hidden" ) );

                originalSurveySettings = companySettings.getSurvey_settings();
                if ( originalSurveySettings != null ) {
                    originalSurveySettings.setSocialPostReminderDisabled( isReminderDisabled );
                }
                LOG.info( "Updating Social Post Reminder Needed" );
                message = messageUtils
                    .getDisplayMessage( DisplayMessageConstants.SOCIAL_POST_REMINDER_ENABLED_UPDATE_SUCCESSFUL,
                        DisplayMessageType.SUCCESS_MESSAGE )
                    .getMessage();
            }

            if ( organizationManagementService.updateSurveySettings( companySettings, originalSurveySettings ) ) {
                companySettings.setSurvey_settings( originalSurveySettings );
                LOG.info( "Updated Survey Settings" );
            }
        } catch ( NumberFormatException e ) {
            LOG.error( "NumberFormatException while updating Reminder Interval. Reason : " + e.getMessage(), e );
            message = messageUtils
                .getDisplayMessage( DisplayMessageConstants.INVALID_SURVEY_REMINDER_INTERVAL, DisplayMessageType.ERROR_MESSAGE )
                .getMessage();
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while updating survey settings. Reason : " + e.getMessage(), e );
            message = messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ).getMessage();
        }
        return message;
    }


    /**
     * Method to update Social Post Reminder Settings
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/updatesocialpostremindersettings", method = RequestMethod.POST)
    @ResponseBody
    public String updateSocialPostReminderSettings( Model model, HttpServletRequest request )
    {
        LOG.info( "Updating Social Post Reminder Settings" );
        String mailCategory = request.getParameter( "mailcategory" );
        SurveySettings originalSurveySettings = null;
        String message = "";

        try {
            User user = sessionHelper.getCurrentUser();
            OrganizationUnitSettings companySettings = organizationManagementService
                .getCompanySettings( user.getCompany().getCompanyId() );

            if ( mailCategory != null && mailCategory.equals( "post-reminder-interval" ) ) {
                int reminderInterval = Integer.parseInt( request.getParameter( "reminder-interval" ) );
                if ( reminderInterval == 0 ) {
                    LOG.warn( "Reminder Interval is 0." );
                    throw new InvalidInputException( "Reminder Interval is 0.", DisplayMessageConstants.GENERAL_ERROR );
                }

                originalSurveySettings = companySettings.getSurvey_settings();
                if ( originalSurveySettings != null ) {
                    originalSurveySettings.setSocial_post_reminder_interval_in_days( reminderInterval );
                }
                LOG.info( "Updating Social Post Reminder Interval" );
                message = messageUtils
                    .getDisplayMessage( DisplayMessageConstants.SOCIAL_POST_REMINDER_INTERVAL_UPDATE_SUCCESSFUL,
                        DisplayMessageType.SUCCESS_MESSAGE )
                    .getMessage();
            }

            else if ( mailCategory != null && mailCategory.equals( "post-reminder-needed" ) ) {
                boolean isReminderDisabled = Boolean.parseBoolean( request.getParameter( "reminder-needed-hidden" ) );

                originalSurveySettings = companySettings.getSurvey_settings();
                if ( originalSurveySettings != null ) {
                    originalSurveySettings.setSocialPostReminderDisabled( isReminderDisabled );
                }
                LOG.info( "Updating Social Post Reminder Needed" );
                message = messageUtils
                    .getDisplayMessage( DisplayMessageConstants.SOCIAL_POST_REMINDER_ENABLED_UPDATE_SUCCESSFUL,
                        DisplayMessageType.SUCCESS_MESSAGE )
                    .getMessage();
            }

            if ( organizationManagementService.updateSurveySettings( companySettings, originalSurveySettings ) ) {
                companySettings.setSurvey_settings( originalSurveySettings );
                LOG.info( "Updated Survey Settings" );
            }
        } catch ( NumberFormatException e ) {
            LOG.error( "NumberFormatException while updating Reminder Interval. Reason : " + e.getMessage(), e );
            message = messageUtils
                .getDisplayMessage( DisplayMessageConstants.INVALID_SURVEY_REMINDER_INTERVAL, DisplayMessageType.ERROR_MESSAGE )
                .getMessage();
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while updating survey settings. Reason : " + e.getMessage(), e );
            message = messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ).getMessage();
        }
        return message;
    }


    /**
     * Method to update Other Company Settings
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/updateothersettings", method = RequestMethod.POST)
    @ResponseBody
    public String updateOtherSettings( Model model, HttpServletRequest request )
    {
        LOG.info( "Updating Location Settings" );
        String otherCategory = request.getParameter( "othercategory" );
        String message = "";

        try {
            User user = sessionHelper.getCurrentUser();
            OrganizationUnitSettings companySettings = organizationManagementService
                .getCompanySettings( user.getCompany().getCompanyId() );

            if ( otherCategory != null && otherCategory.equals( "other-location" ) ) {
                boolean isLocationEnabled = Boolean.parseBoolean( request.getParameter( "other-location" ) );
                organizationManagementService.updateLocationEnabled( companySettings, isLocationEnabled );

                // set the updated settings value in session
                companySettings.setLocationEnabled( isLocationEnabled );
                message = messageUtils.getDisplayMessage( DisplayMessageConstants.LOCATION_SETTINGS_UPDATE_SUCCESSFUL,
                    DisplayMessageType.SUCCESS_MESSAGE ).getMessage();
                LOG.info( "Updated Location Settings" );
            }

            else if ( otherCategory != null && otherCategory.equals( "other-account" ) ) {
                boolean isAccountDisabled = Boolean.parseBoolean( request.getParameter( "other-account" ) );

                // Calling services to update DB
                organizationManagementService.updateAccountDisabled( companySettings, isAccountDisabled );
                if ( isAccountDisabled ) {
                    organizationManagementService.addDisabledAccount( companySettings.getIden(), false );
                } else {
                    organizationManagementService.deleteDisabledAccount( companySettings.getIden() );
                }

                // set the updated settings value in session
                companySettings.setAccountDisabled( isAccountDisabled );
                message = messageUtils.getDisplayMessage( DisplayMessageConstants.ACCOUNT_SETTINGS_UPDATE_SUCCESSFUL,
                    DisplayMessageType.SUCCESS_MESSAGE ).getMessage();
                LOG.info( "Updated Location Settings" );
            }
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while updating other settings. Reason : " + e.getMessage(), e );
            message = messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ).getMessage();
        }
        return message;
    }


    private String makeJsonMessage( int status, String message )
    {

        JSONObject jsonMessage = new JSONObject();
        LOG.debug( "Building json response" );
        try {
            jsonMessage.put( "success", status );
            jsonMessage.put( "message", message );
        } catch ( JSONException e ) {
            LOG.error( "Exception occured while building json response : " + e.getMessage(), e );
        }

        LOG.info( "Returning json response : " + jsonMessage.toString() );
        return jsonMessage.toString();
    }


    /**
     * Method to upgrade a plan
     * 
     * @param request
     * @param model
     * @return
     */
    @RequestMapping ( value = "/upgradeplan", method = RequestMethod.POST)
    @ResponseBody
    public Object upgradePlanForUserInSession( HttpServletRequest request, Model model )
    {
        LOG.info( "Upgrading the user's subscription" );
        String accountType = request.getParameter( CommonConstants.ACCOUNT_TYPE_IN_SESSION );
        String nonce = request.getParameter( CommonConstants.PAYMENT_NONCE );
        String message = null;

        LOG.info( "Fetching the user in session" );
        User user = sessionHelper.getCurrentUser();

        try {
            LOG.info( "Making the braintree API call to upgrade and updating the database!" );

            if ( accountType == null || accountType.isEmpty() ) {
                LOG.error( "Account type parameter passed is null or empty" );
                throw new InvalidInputException( "Account type parameter passed is null or empty",
                    DisplayMessageConstants.GENERAL_ERROR );
            }

            int newAccountsMasterId = 0;
            try {
                newAccountsMasterId = Integer.parseInt( accountType );
            } catch ( NumberFormatException e ) {
                LOG.error( "Error while parsing account type " );
                throw new InvalidInputException( "Error while parsing account type ", DisplayMessageConstants.GENERAL_ERROR,
                    e );
            }
            LOG.info( "Making the API call to upgrade" );
            gateway.upgradePlanForSubscription( user, newAccountsMasterId, nonce );
            LOG.info( "Upgrade successful" );

            switch ( newAccountsMasterId ) {
                case CommonConstants.ACCOUNTS_MASTER_INDIVIDUAL:
                    message = messageUtils
                        .getDisplayMessage( DisplayMessageConstants.TO_INDIVIDUAL_SUBSCRIPTION_UPGRADE_SUCCESSFUL,
                            DisplayMessageType.SUCCESS_MESSAGE )
                        .getMessage();
                    break;
                case CommonConstants.ACCOUNTS_MASTER_TEAM:
                    message = messageUtils.getDisplayMessage( DisplayMessageConstants.TO_TEAM_SUBSCRIPTION_UPGRADE_SUCCESSFUL,
                        DisplayMessageType.SUCCESS_MESSAGE ).getMessage();
                    break;
                case CommonConstants.ACCOUNTS_MASTER_COMPANY:
                    message = messageUtils
                        .getDisplayMessage( DisplayMessageConstants.TO_COMPANY_SUBSCRIPTION_UPGRADE_SUCCESSFUL,
                            DisplayMessageType.SUCCESS_MESSAGE )
                        .getMessage();
                    break;
                case CommonConstants.ACCOUNTS_MASTER_ENTERPRISE:
                    message = messageUtils
                        .getDisplayMessage( DisplayMessageConstants.TO_ENTERPRISE_SUBSCRIPTION_UPGRADE_SUCCESSFUL,
                            DisplayMessageType.SUCCESS_MESSAGE )
                        .getMessage();
                    break;
            }
            LOG.info( "message returned : " + message );
        } catch ( InvalidInputException | NoRecordsFetchedException | SolrException | UndeliveredEmailException e ) {
            LOG.error( "NonFatalException while upgrading subscription. Message : " + e.getMessage(), e );
            message = messageUtils.getDisplayMessage( null, DisplayMessageType.ERROR_MESSAGE ).getMessage();

            return makeJsonMessage( CommonConstants.STATUS_INACTIVE, message );

        } catch ( PaymentException e ) {
            LOG.error( "NonFatalException while upgrading subscription. Message : " + e.getMessage(), e );
            message = messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ).getMessage();

            return makeJsonMessage( CommonConstants.STATUS_INACTIVE, message );
        } catch ( SubscriptionPastDueException e ) {
            LOG.error( "SubscriptionPastDueException while upgrading subscription. Message : " + e.getMessage(), e );
            message = messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ).getMessage();

            return makeJsonMessage( CommonConstants.STATUS_INACTIVE, message );
        } catch ( SubscriptionUpgradeUnsuccessfulException e ) {
            LOG.error( "SubscriptionUpgradeUnsuccessfulException while upgrading subscription. Message : " + e.getMessage(),
                e );
            message = messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ).getMessage();

            return makeJsonMessage( CommonConstants.STATUS_INACTIVE, message );
        } catch ( CreditCardException e ) {
            LOG.error( "Exception has occured : " + e.getMessage(), e );
            message = messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ).getMessage();
            return makeJsonMessage( CommonConstants.STATUS_INACTIVE, message );
        } catch ( SubscriptionUnsuccessfulException e ) {
            LOG.error( "Exception has occured : " + e.getMessage(), e );
            message = messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ).getMessage();
            return makeJsonMessage( CommonConstants.STATUS_INACTIVE, message );
        }

        // After all the updates are done we set the account type in the session to reflect changes
        HttpSession session = request.getSession();
        session.setAttribute( CommonConstants.ACCOUNT_TYPE_IN_SESSION,
            AccountType.getAccountType( Integer.parseInt( accountType ) ) );

        LOG.info( "returning message : " + message );
        return makeJsonMessage( CommonConstants.STATUS_ACTIVE, message );
    }


    /**
     * Method for displaying the upgrade page
     * 
     * @param request
     * @param model
     * @return
     */
    @RequestMapping ( value = "/upgradepage", method = RequestMethod.GET)
    public String upgradePage( HttpServletRequest request, Model model )
    {

        LOG.info( "Upgrade page requested." );

        LOG.debug( "Retrieveing the user from session to get his current plan details" );
        User user = sessionHelper.getCurrentUser();
        LicenseDetail currentLicenseDetail = user.getCompany().getLicenseDetails().get( CommonConstants.INITIAL_INDEX );

        LOG.debug( "Adding the current plan in the model and the upgrade flag" );
        model.addAttribute( CommonConstants.CURRENT_LICENSE_ID,
            currentLicenseDetail.getAccountsMaster().getAccountsMasterId() );
        model.addAttribute( CommonConstants.UPGRADE_FLAG, CommonConstants.YES );

        LOG.info( "Returning the upgrade account selection page" );
        return JspResolver.ACCOUNT_TYPE_SELECTION;

    }


    /**
     * Method for displaying the upgrade page to upgrade from free account
     * 
     * @param request
     * @param model
     * @return
     */
    @RequestMapping ( value = "/upgradetopaidplanpage", method = RequestMethod.GET)
    public String upgradeToPaidPlanPage( HttpServletRequest request, Model model )
    {

        LOG.info( "Upgrade page requested." );

        LOG.debug( "Retrieveing the user from session to get his current plan details" );

        LOG.debug( "Adding the current plan in the model and the upgrade flag" );
        model.addAttribute( CommonConstants.PAID_PLAN_UPGRADE_FLAG, CommonConstants.YES );

        LOG.info( "Returning the upgrade account selection page" );
        return JspResolver.ACCOUNT_TYPE_SELECTION;

    }


    /**
     * Returns the upgrade confirmation page
     * 
     * @param request
     * @param model
     * @return
     */
    @RequestMapping ( value = "/upgradeconfirmation", method = RequestMethod.POST)
    public String getUpgradeConfirmationPage( HttpServletRequest request, Model model )
    {

        LOG.info( "Upgrade confirmation page requested" );
        String accountType = request.getParameter( CommonConstants.ACCOUNT_TYPE_IN_SESSION );
        User user = sessionHelper.getCurrentUser();

        try {
            // We need to calculate the balance amount and put it into the model
            if ( accountType == null || accountType.isEmpty() ) {
                LOG.error( "Account type parameter passed is null or empty" );
                throw new InvalidInputException( "Account type parameter passed is null or empty",
                    DisplayMessageConstants.GENERAL_ERROR );
            }

            int toAccountsMasterId = 0;
            try {
                toAccountsMasterId = Integer.parseInt( accountType );
            } catch ( NumberFormatException e ) {
                LOG.error( "Error while parsing account type " );
                throw new InvalidInputException( "Error while parsing account type ", DisplayMessageConstants.GENERAL_ERROR,
                    e );
            }
            int fromAccountsMasterId = user.getCompany().getLicenseDetails().get( CommonConstants.INITIAL_INDEX )
                .getAccountsMaster().getAccountsMasterId();

            model.addAttribute( "balanceAmount", String.format( "%.02f",
                gateway.getBalacnceAmountForPlanUpgrade( user.getCompany(), fromAccountsMasterId, toAccountsMasterId ) ) );
            model.addAttribute( CommonConstants.ACCOUNT_TYPE_IN_SESSION, toAccountsMasterId );
        } catch ( InvalidInputException e ) {
            LOG.error( "Exception has occured : " + e.getMessage(), e );
        }

        LOG.info( "Returning the confirmation page" );
        return JspResolver.UPGRADE_CONFIRMATION;
    }


    /**
     * This controller is called to initialize the default branches and regions in case they arent
     * done after payment.
     * 
     * @param request
     * @param model
     * @return
     */
    @RequestMapping ( value = "/defaultbrandandregioncreation", method = RequestMethod.GET)
    public String createDefaultBranchesAndRegions( HttpServletRequest request, RedirectAttributes redirectAttributes,
        Model model )
    {
        LOG.info( "createDefaultBranchesAndRegions called to do pre processing before log in" );
        User user = sessionHelper.getCurrentUser();

        try {
            LicenseDetail currentLicenseDetail = user.getCompany().getLicenseDetails().get( CommonConstants.INITIAL_INDEX );
            HttpSession session = request.getSession( false );
            AccountType accountType = null;

            if ( currentLicenseDetail == null ) {
                LOG.error(
                    "createDefaultBranchesAndRegions : License details not found for user with id : " + user.getUserId() );
                throw new InvalidInputException(
                    "createDefaultBranchesAndRegions : License details not found for user with id : " + user.getUserId() );
            }

            AccountsMaster currentAccountsMaster = currentLicenseDetail.getAccountsMaster();
            if ( currentAccountsMaster == null ) {
                LOG.error( "createDefaultBranchesAndRegions : Accounts Master not found for license details with id: "
                    + currentLicenseDetail.getLicenseId() );
                throw new InvalidInputException(
                    "createDefaultBranchesAndRegions : Accounts Master not found for license details with id: "
                        + currentLicenseDetail.getLicenseId() );
            }

            try {
                LOG.debug( "Calling sevices for adding account type of company" );
                accountType = organizationManagementService.addAccountTypeForCompany( user,
                    String.valueOf( currentAccountsMaster.getAccountsMasterId() ) );
                LOG.debug( "Successfully executed sevices for adding account type of company.Returning account type : "
                    + accountType );

                LOG.debug( "Adding account type in session" );
                session.setAttribute( CommonConstants.ACCOUNT_TYPE_IN_SESSION, accountType );
                sessionHelper.getCanonicalSettings( session );
                sessionHelper.setSettingVariablesInSession( session );
                sessionHelper.processAssignments( session, user );
            } catch ( InvalidInputException e ) {
                throw new InvalidInputException( "InvalidInputException in addAccountType. Reason :" + e.getMessage(),
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }
            try {
                /**
                 * For each account type, only the company admin's profile completion stage is
                 * updated, all the other profiles created by default need no action so their
                 * profile completion stage is marked completed at the time of insert
                 */
                LOG.debug( "Calling sevices for updating profile completion stage" );
                userManagementService.updateProfileCompletionStage( user,
                    CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID, CommonConstants.DASHBOARD_STAGE );
                LOG.debug( "Successfully executed sevices for updating profile completion stage" );
            } catch ( InvalidInputException e ) {
                LOG.error( "InvalidInputException while updating profile completion stage. Reason : " + e.getMessage(), e );
                throw new InvalidInputException( e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e );
            }

            // Setting session variable to show linkedin signup and sendsurvey popups only once
            String popupStatus = (String) session.getAttribute( CommonConstants.POPUP_FLAG_IN_SESSION );
            if ( popupStatus == null ) {
                session.setAttribute( CommonConstants.POPUP_FLAG_IN_SESSION, CommonConstants.YES_STRING );
            } else if ( popupStatus.equals( CommonConstants.YES_STRING ) ) {
                session.setAttribute( CommonConstants.POPUP_FLAG_IN_SESSION, CommonConstants.NO_STRING );
            }

            // setting popup attributes
            boolean showLinkedInPopup = false;
            boolean showSendSurveyPopup = false;
            user = userManagementService.getUserByUserId( user.getUserId() );
            for ( UserProfile profile : user.getUserProfiles() ) {
                if ( profile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID ) {
                    showLinkedInPopup = true;
                    showSendSurveyPopup = true;
                    break;
                }
            }
            redirectAttributes.addFlashAttribute( "showLinkedInPopup", String.valueOf( showLinkedInPopup ) );
            redirectAttributes.addFlashAttribute( "showSendSurveyPopup", String.valueOf( showSendSurveyPopup ) );

            // update the last login time and number of logins
            // userManagementService.updateUserLoginTimeAndNum(user);
        } catch ( NonFatalException e ) {
            LOG.error( "NonfatalException while adding account type. Reason: " + e.getMessage(), e );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
            return JspResolver.ERROR_PAGE;
        }

        LOG.info( "createDefaultBranchesAndRegions : Default branches and regions created. Returing the landing page!" );
        return "redirect:/" + JspResolver.LANDING + ".do";
    }


    /**
     * This controller is called to store text to be displayed to a customer after choosing the
     * flow(happy/neutral/sad).
     * 
     * @param request
     * @param model
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/storetextforflow", method = RequestMethod.GET)
    public String storeTextForFlow( HttpServletRequest request, Model model )
    {
        LOG.info( "Method to store text to be displayed to a customer after choosing the flow, storeTextForFlow() started." );
        User user = sessionHelper.getCurrentUser();
        String status = "";

        try {
            String text = request.getParameter( "text" );
            String mood = request.getParameter( "mood" );
            if ( text == null ) {
                LOG.error( "Null or empty value found in storeTextForFlow() for text." );
                text = "";
            }
            text = text.trim();
            if ( mood == null || mood.isEmpty() ) {
                LOG.error( "Null or empty value found in storeTextForFlow() for mood." );
                throw new InvalidInputException( "Null or empty value found in storeTextForFlow() for mood." );
            }

            OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user );

            SurveySettings surveySettings = companySettings.getSurvey_settings();
            if ( mood.equalsIgnoreCase( "happy" ) )
                surveySettings.setHappyText( text );
            else if ( mood.equalsIgnoreCase( "neutral" ) )
                surveySettings.setNeutralText( text );
            else if ( mood.equalsIgnoreCase( "sad" ) )
                surveySettings.setSadText( text );
            else if ( mood.equalsIgnoreCase( "happyComplete" ) )
                surveySettings.setHappyTextComplete( text );
            else if ( mood.equalsIgnoreCase( "neutralComplete" ) )
                surveySettings.setNeutralTextComplete( text );
            else if ( mood.equalsIgnoreCase( "sadComplete" ) )
                surveySettings.setSadTextComplete( text );

            organizationManagementService.updateSurveySettings( companySettings, surveySettings );
            status = CommonConstants.SUCCESS_ATTRIBUTE;

            // Updating settings in session
            HttpSession session = request.getSession();
            UserSettings userSettings = (UserSettings) session
                .getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
            if ( userSettings != null )
                userSettings.setCompanySettings( companySettings );
        } catch ( NonFatalException e ) {
            LOG.error( "Non fatal exception caught in storeTextForFlow(). Nested exception is ", e );
        }

        LOG.info( "Method to store text to be displayed to a customer after choosing the flow, storeTextForFlow() finished." );
        return status;
    }


    /**
     * This controller is called to revert text to be displayed to a customer after choosing the
     * flow(happy/neutral/sad).
     * 
     * @param request
     * @param model
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/resettextforflow", method = RequestMethod.GET)
    public String resetTextForFlow( HttpServletRequest request, Model model )
    {
        LOG.info( "Method to reset text to be displayed to a customer after choosing the flow, resetTextForFlow() started." );
        User user = sessionHelper.getCurrentUser();
        String message = "";

        try {
            String mood = request.getParameter( "mood" );
            if ( mood == null || mood.isEmpty() ) {
                LOG.error( "Null or empty value found in resetTextForFlow() for mood." );
                throw new InvalidInputException( "Null or empty value found in resetTextForFlow() for mood." );
            }

            OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user );

            SurveySettings surveySettings = companySettings.getSurvey_settings();
            message = organizationManagementService.resetDefaultSurveyText( companySettings.getSurvey_settings(), mood );
            organizationManagementService.updateSurveySettings( companySettings, surveySettings );

            message = makeJsonMessage( CommonConstants.STATUS_ACTIVE, message );

            // Updating settings in session
            HttpSession session = request.getSession();
            UserSettings userSettings = (UserSettings) session
                .getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
            if ( userSettings != null )
                userSettings.setCompanySettings( companySettings );
        } catch ( NonFatalException e ) {
            LOG.error( "Non fatal exception caught in resetTextForFlow(). Nested exception is ", e );
        }

        LOG.info( "Method to reset text to be displayed to a customer after choosing the flow, resetTextForFlow() finished." );
        return message;
    }


    @ResponseBody
    @RequestMapping ( value = "/getusstatelist", method = RequestMethod.GET)
    public String getUsStateList( HttpServletRequest request )
    {
        List<StateLookup> lookups = organizationManagementService.getUsStateList();
        Gson gson = new GsonBuilder().setExclusionStrategies( new StateLookupExclusionStrategy() ).create();
        for ( StateLookup s : lookups ) {
            gson.toJson( s );
        }
        String usStateList = gson.toJson( lookups );
        return usStateList;
    }


    @ResponseBody
    @RequestMapping ( value = "/getzipcodesbystateid", method = RequestMethod.GET)
    public String getZipCodesByStateId( HttpServletRequest request )
    {

        String stateIdStr = request.getParameter( "stateId" );
        int stateId = 0;
        try {
            stateId = Integer.parseInt( stateIdStr );
        } catch ( NumberFormatException e ) {
            LOG.error( "Error occurred while parsing state Id" );
        }
        String usStateZipcodeList = organizationManagementService.getZipCodesByStateId( stateId );
        return usStateZipcodeList;
    }


    // Method to delete all the records of a company.
    @RequestMapping ( value = "/deletecompany", method = RequestMethod.GET)
    public String deleteCompany( HttpServletRequest request, Model model, RedirectAttributes redirectAttributes )
        throws NonFatalException
    {
        User user = sessionHelper.getCurrentUser();
        String message = "";

        try {
            if ( user != null && user.isCompanyAdmin() ) {
                // Add an entry into Disabled_Accounts table with disable_date as current date
                // and status as inactive.
                try {
                    organizationManagementService.addDisabledAccount( user.getCompany().getCompanyId(), true );
                } catch ( NoRecordsFetchedException | PaymentException e ) {
                    LOG.error( "Exception caught in deleteCompany() of OrganizationManagementController. Nested exception is ",
                        e );
                    throw e;
                }

                // Modify the company status to inactive.
                user.getCompany().setStatus( CommonConstants.STATUS_INACTIVE );
                organizationManagementService.updateCompany( user.getCompany() );

                LOG.info( "Company deactivated successfully, logging out now." );
                request.getSession( false ).invalidate();
                SecurityContextHolder.clearContext();

                message = messageUtils.getDisplayMessage( DisplayMessageConstants.ACCOUNT_DELETION_SUCCESSFUL,
                    DisplayMessageType.SUCCESS_MESSAGE ).toString();
            }
        } catch ( InvalidInputException e ) {
            LOG.error( "InvalidInputException caught in purgeCompany(). Nested exception is ", e );
            message = messageUtils
                .getDisplayMessage( DisplayMessageConstants.ACCOUNT_DELETION_UNSUCCESSFUL, DisplayMessageType.ERROR_MESSAGE )
                .toString();
        }

        redirectAttributes.addFlashAttribute( CommonConstants.MESSAGE, message );
        return "redirect:/" + JspResolver.LOGIN + ".do";
    }


    /**
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/savedotloopdetails", method = RequestMethod.POST)
    @ResponseBody
    public String saveDotloopDetails( Model model, HttpServletRequest request )
    {
        LOG.info( "Inside method saveDotLoopDetails " );
        HttpSession session = request.getSession( false );
        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        request.setAttribute( "saveencompassdetails", "true" );
        String message = null;

        try {
            // Encrypting the password
            String apiKey = request.getParameter( "dotloop-api" );
            if ( apiKey != null && !apiKey.isEmpty() ) {
                DotLoopCrmInfo dotLoopCrmInfo = new DotLoopCrmInfo();
                dotLoopCrmInfo.setCrm_source( CommonConstants.CRM_SOURCE_DOTLOOP );
                dotLoopCrmInfo.setApi( apiKey );
                dotLoopCrmInfo.setRecordsBeenFetched( false );
                OrganizationUnitSettings unitSettings = null;
                String collectionName = "";
                if ( entityType.equalsIgnoreCase( CommonConstants.COMPANY_ID ) ) {
                    collectionName = MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION;
                    unitSettings = organizationManagementService.getCompanySettings( entityId );
                    if ( unitSettings != null ) {
                        dotLoopCrmInfo.setCompanyId( unitSettings.getIden() );
                    }

                } else if ( entityType.equalsIgnoreCase( CommonConstants.REGION_ID ) ) {
                    collectionName = MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION;
                    unitSettings = organizationManagementService.getRegionSettings( entityId );
                    if ( unitSettings != null ) {
                        dotLoopCrmInfo.setRegionId( unitSettings.getIden() );
                    }
                } else if ( entityType.equalsIgnoreCase( CommonConstants.BRANCH_ID ) ) {
                    collectionName = MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION;
                    unitSettings = organizationManagementService.getBranchSettingsDefault( entityId );
                    if ( unitSettings != null ) {
                        dotLoopCrmInfo.setBranchId( unitSettings.getIden() );
                    }
                } else if ( entityType.equalsIgnoreCase( CommonConstants.AGENT_ID ) ) {
                    collectionName = MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION;
                    unitSettings = organizationManagementService.getAgentSettings( entityId );
                    dotLoopCrmInfo.setAgentId( unitSettings.getIden() );
                } else {
                    throw new InvalidInputException( "Invalid entity type" );
                }

                organizationManagementService.updateCRMDetailsForAnyUnitSettings( unitSettings, collectionName, dotLoopCrmInfo,
                    "com.realtech.socialsurvey.core.entities.DotLoopCrmInfo" );

                unitSettings.setCrm_info( dotLoopCrmInfo );
                message = messageUtils.getDisplayMessage( DisplayMessageConstants.DOTLOOP_CONNECTION_SUCCESSFUL,
                    DisplayMessageType.SUCCESS_MESSAGE ).getMessage();
            }
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while testing encompass detials. Reason : " + e.getMessage(), e );
            message = messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ).getMessage();
        }
        return message;

    }


    @RequestMapping ( value = "/testdotloopconnection", method = RequestMethod.POST)
    @ResponseBody
    public String testDotloopConnection( Model model, HttpServletRequest request ) throws NonFatalException
    {
        LOG.info( "Testing connections" );
        String message;
        try {
            // validate the parameters
            if ( !validateDotloopParameters( request ) ) {
                // TODO: code to test connection
            }
            message = messageUtils
                .getDisplayMessage( DisplayMessageConstants.DOTLOOP_DATA_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE )
                .getMessage();
        } catch ( NonFatalException e ) {
            if ( request.getAttribute( "savedotloopdetails" ) != null ) {
                throw e;
            } else {
                LOG.error( "NonFatalException while testing encompass detials. Reason : " + e.getMessage(), e );
            }
            message = messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ).getMessage();
        }
        return message;
    }


    @ResponseBody
    @RequestMapping ( value = "/logincompanyadminas", method = RequestMethod.GET)
    public String loginCompanyAdminAsUser( Model model, HttpServletRequest request )
    {

        LOG.info( "Inside loginCompanyAdminAsUser() method in organization management controller" );

        String columnName = request.getParameter( "colName" );
        String columnValue = request.getParameter( "colValue" );

        try {

            if ( columnName == null || columnName.isEmpty() ) {
                throw new InvalidInputException( "Column name passed null/empty" );
            }

            if ( columnValue == null || columnValue.isEmpty() ) {
                throw new InvalidInputException( "Column value passed null/empty" );
            }

            long id = 01;

            try {
                id = Long.parseLong( columnValue );
            } catch ( NumberFormatException e ) {
                throw new InvalidInputException( "Invalid id was passed", e );
            }

            HttpSession session = request.getSession();
            Long superAdminUserId = (Long) session.getAttribute( CommonConstants.REALTECH_USER_ID );
            User adminUser = sessionHelper.getCurrentUser();
            User newUser = userManagementService.getUserByUserId( id );

            HttpSession newSession = request.getSession( true );
            if ( adminUser.isCompanyAdmin() ) {
                newSession.setAttribute( CommonConstants.COMPANY_ADMIN_SWITCH_USER_ID, adminUser.getUserId() );
            } else if ( adminUser.isRegionAdmin() ) {
                newSession.setAttribute( CommonConstants.REGION_ADMIN_SWITCH_USER_ID, adminUser.getUserId() );
            } else if ( adminUser.isBranchAdmin() ) {
                newSession.setAttribute( CommonConstants.BRANCH_ADMIN_SWITCH_USER_ID, adminUser.getUserId() );
            }

            if ( superAdminUserId != null )
                newSession.setAttribute( CommonConstants.REALTECH_USER_ID, superAdminUserId );

            //Set the autologin attribute as true
            newSession.setAttribute( CommonConstants.IS_AUTO_LOGIN, "true" );
            sessionHelper.loginAdminAs( newUser.getLoginName(), CommonConstants.BYPASS_PWD );

        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException occurred in loginCompanyAdminAsUser(), reason : " + e.getMessage() );
        }
        return "success";
    }


    @ResponseBody
    @RequestMapping ( value = "/switchtocompanyadmin", method = RequestMethod.GET)
    public String switchToCompanyAdminUser( Model model, HttpServletRequest request )
    {

        HttpSession session = request.getSession();
        Long adminUserid = null;
        if ( session.getAttribute( CommonConstants.COMPANY_ADMIN_SWITCH_USER_ID ) != null ) {
            adminUserid = (Long) session.getAttribute( CommonConstants.COMPANY_ADMIN_SWITCH_USER_ID );
        } else if ( session.getAttribute( CommonConstants.REGION_ADMIN_SWITCH_USER_ID ) != null ) {
            adminUserid = (Long) session.getAttribute( CommonConstants.REGION_ADMIN_SWITCH_USER_ID );
        } else if ( session.getAttribute( CommonConstants.BRANCH_ADMIN_SWITCH_USER_ID ) != null ) {
            adminUserid = (Long) session.getAttribute( CommonConstants.BRANCH_ADMIN_SWITCH_USER_ID );
        }

        try {
            User adminUser = userManagementService.getUserByUserId( adminUserid );

            // This method did not set the profile levels as required
            // userManagementService.setProfilesOfUser(adminUser);

            // Added this code to find the highest profile level for the user
            int profileMasterId = CommonConstants.PROFILES_MASTER_NO_PROFILE_ID;
            for ( UserProfile userProfile : adminUser.getUserProfiles() ) {
                switch ( userProfile.getProfilesMaster().getProfileId() ) {
                    case CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID:
                        profileMasterId = CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID;
                        break;
                    case CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID:
                        profileMasterId = CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID;
                        continue;
                    case CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID:
                        if ( profileMasterId > CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID ) {
                            profileMasterId = CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID;
                        }
                }
            }

            if ( profileMasterId == CommonConstants.PROFILES_MASTER_NO_PROFILE_ID ) {
                throw new InvalidInputException( "User in session is not an admin" );
            }

            // Logout current user
            session.invalidate();
            SecurityContextHolder.clearContext();

            session = request.getSession( true );
            sessionHelper.loginAdminAs( adminUser.getLoginName(), CommonConstants.BYPASS_PWD );
        } catch ( NonFatalException e ) {
            LOG.error( "Exception occurred in switchToAdminUser() method , reason : " + e.getMessage() );
            return "failure";
        }
        return "success";
    }


    private boolean validateDotloopParameters( HttpServletRequest request ) throws InvalidInputException
    {
        LOG.debug( "Validating encompass parameters" );
        String apiKey = request.getParameter( "dotloop-apikey" );
        if ( apiKey == null || apiKey.isEmpty() ) {
            LOG.warn( "Encompass validation failed" );
            throw new InvalidInputException( "All fields not set for dotloop", DisplayMessageConstants.GENERAL_ERROR );
        }
        LOG.debug( "Encompass validation passed." );
        return true;
    }


    @RequestMapping ( value = "/showcomplaintressettings", method = RequestMethod.GET)
    public String showComplaintRegistrationSettings( Model model, HttpServletRequest request )
    {
        LOG.info( "Method showComplaintRegistrationSettings of UserManagementController called" );
        HttpSession session = request.getSession( false );
        User user = sessionHelper.getCurrentUser();

        if ( !user.isCompanyAdmin() )
            throw new AuthorizationException( "User is not authorized to access this page" );

        OrganizationUnitSettings unitSettings = null;
        long entityId = user.getCompany().getCompanyId();
        try {
            unitSettings = organizationManagementService.getCompanySettings( entityId );

            if ( unitSettings == null )
                throw new NonFatalException( "Company settings cannot be found for id : " + entityId );

            ComplaintResolutionSettings complaintRegistrationSettings = new ComplaintResolutionSettings();
            if ( unitSettings.getSurvey_settings() != null
                && unitSettings.getSurvey_settings().getComplaint_res_settings() != null ) {
                complaintRegistrationSettings = unitSettings.getSurvey_settings().getComplaint_res_settings();
            }

            model.addAttribute( "columnName", CommonConstants.COMPANY_ID_COLUMN );
            model.addAttribute( "columnValue", entityId );
            session.setAttribute( CommonConstants.COMPLAIN_REG_SETTINGS, complaintRegistrationSettings );
        } catch ( InvalidInputException e ) {
            LOG.error( "InvalidInputException while fetching complaint resolution details. Reason :" + e.getMessage(), e );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( DisplayMessageConstants.GENERAL_ERROR, DisplayMessageType.ERROR_MESSAGE ) );
            return JspResolver.MESSAGE_HEADER;
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while fetching complaint resolution details. Reason :" + e.getMessage(), e );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( DisplayMessageConstants.GENERAL_ERROR, DisplayMessageType.ERROR_MESSAGE ) );
            return JspResolver.MESSAGE_HEADER;
        }
        return JspResolver.COMPLAINT_REGISTRATION_SETTINGS;
    }


    @RequestMapping ( value = "/updatecomplaintressettings", method = RequestMethod.POST)
    @ResponseBody
    public String updateComplaintResolutionsettings( Model model, HttpServletRequest request )
    {
        LOG.info( "Updating Complaint Resolution Settings" );
        String ratingText = request.getParameter( "rating" );
        String moodText = request.getParameter( "mood" );
        String mailId = request.getParameter( "mailId" );
        String enabled = request.getParameter( "enabled" );
        boolean isComplaintHandlingEnabled = false;

        String message = "";
        String mailIDStr = new String();
        User user = sessionHelper.getCurrentUser();
        OrganizationUnitSettings unitSettings = null;
        ComplaintResolutionSettings originalComplaintRegSettings = new ComplaintResolutionSettings();

        try {

            if ( !user.isCompanyAdmin() )
                throw new AuthorizationException( "User is not authorized to access this page" );

            if ( enabled == null || enabled.isEmpty() ) {
                isComplaintHandlingEnabled = false;
            } else if ( enabled.equalsIgnoreCase( "enable" ) )
                isComplaintHandlingEnabled = true;

            if ( mailId == null || mailId.isEmpty() ) {
                throw new InvalidInputException( "Mail Id(s) of Complaint Handler(s) is null",
                    DisplayMessageConstants.GENERAL_ERROR );
            }

            if ( !mailId.contains( "," ) ) {
                if ( !organizationManagementService.validateEmail( mailId ) )
                    throw new InvalidInputException( "Mail id - " + mailId + " entered as send alert to input is invalid",
                        DisplayMessageConstants.GENERAL_ERROR );
                else
                    mailIDStr = mailId;
            } else {
                String mailIds[] = mailId.split( "," );

                if ( mailIds.length == 0 )
                    throw new InvalidInputException( "Mail id - " + mailId + " entered as send alert to input is empty",
                        DisplayMessageConstants.GENERAL_ERROR );

                for ( String mailID : mailIds ) {
                    if ( !organizationManagementService.validateEmail( mailID.trim() ) )
                        throw new InvalidInputException(
                            "Mail id - " + mailID + " entered amongst the mail ids as send alert to input is invalid",
                            DisplayMessageConstants.GENERAL_ERROR );
                    else
                        mailIDStr += mailID.trim() + " , ";
                }
                mailId = mailIDStr.substring( 0, mailIDStr.length() - 2 );
            }

            long entityId = user.getCompany().getCompanyId();

            unitSettings = organizationManagementService.getCompanySettings( entityId );

            if ( unitSettings == null )
                throw new NonFatalException( "Company settings cannot be found for id : " + entityId );

            if ( unitSettings.getSurvey_settings() == null ) {
                // Adding default text for various flows of survey.
                SurveySettings surveySettings = new SurveySettings();
                surveySettings.setHappyText( happyText );
                surveySettings.setNeutralText( neutralText );
                surveySettings.setSadText( sadText );
                surveySettings.setHappyTextComplete( happyTextComplete );
                surveySettings.setNeutralTextComplete( neutralTextComplete );
                surveySettings.setSadTextComplete( sadTextComplete );
                surveySettings.setAutoPostEnabled( true );
                surveySettings.setShow_survey_above_score( CommonConstants.DEFAULT_AUTOPOST_SCORE );
                surveySettings.setAuto_post_score( CommonConstants.DEFAULT_AUTOPOST_SCORE );

                surveySettings.setSurvey_reminder_interval_in_days( CommonConstants.DEFAULT_REMINDERMAIL_INTERVAL );
                unitSettings.setSurvey_settings( surveySettings );
            }

            if ( unitSettings.getSurvey_settings().getComplaint_res_settings() != null )
                originalComplaintRegSettings = unitSettings.getSurvey_settings().getComplaint_res_settings();

            if ( isComplaintHandlingEnabled ) {
                if ( ( ratingText == null || ratingText.isEmpty() ) && ( moodText == null || moodText.isEmpty() ) ) {
                    throw new InvalidInputException( "Please select a Rating value and Review Mood selected.",
                        DisplayMessageConstants.GENERAL_ERROR );
                }

                if ( ratingText == null || ratingText.isEmpty() ) {
                    ratingText = "0";
                }

                if ( moodText == null || moodText.isEmpty() ) {
                    moodText = "";
                }

                double rating = Double.parseDouble( ratingText );

                originalComplaintRegSettings.setRating( (float) rating );
                originalComplaintRegSettings.setMood( moodText );

            }

            originalComplaintRegSettings.setMailId( mailId );
            originalComplaintRegSettings.setEnabled( isComplaintHandlingEnabled );
            unitSettings.getSurvey_settings().setComplaint_res_settings( originalComplaintRegSettings );

            if ( !isComplaintHandlingEnabled && originalComplaintRegSettings.getMailId().trim().isEmpty() )
                return "";

            LOG.info( "Updating Complaint Resolution Settings" );

            if ( organizationManagementService.updateSurveySettings( unitSettings, unitSettings.getSurvey_settings() ) ) {
                LOG.info( "Updated Complaint Resolution Settings" );
                message = messageUtils.getDisplayMessage( DisplayMessageConstants.COMPLAINT_REGISTRATION_SUCCESSFUL,
                    DisplayMessageType.SUCCESS_MESSAGE ).getMessage();
            }
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while updating complaint registration settings. Reason : " + e.getMessage(), e );
            message = messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ).getMessage();
        }

        return message;
    }


    @RequestMapping ( value = "/fetchsurveysunderresolution", method = RequestMethod.GET)
    public String fetchSurveysUnderResolution( Model model, HttpServletRequest request )
    {
        LOG.info( "Method to get surveys under resolution for a company fetchSurveysUnderResolution() started." );
        try {
            User user = sessionHelper.getCurrentUser();
            String startIndexStr = request.getParameter( "startIndex" );
            String batchSizeStr = request.getParameter( "batchSize" );
            int startIndex = Integer.parseInt( startIndexStr );
            int batchSize = Integer.parseInt( batchSizeStr );

            List<SurveyDetails> surveyDetails = surveyHandler.getSurveysUnderResolution( user.getCompany().getCompanyId(),
                startIndex, batchSize );
            model.addAttribute( "reviews", surveyDetails );
        } catch ( NumberFormatException e ) {
            LOG.error(
                "NumberFormat exception caught in fetchSurveysUnderResolution() while fetching surveys under resolution for a company. Nested exception is ",
                e );
            model.addAttribute( "message", e.getMessage() );
        }
        LOG.info( "Method to get surveys under resolution for a company fetchSurveysUnderResolution() finished." );
        return JspResolver.REVIEWS_UNDER_RESOLUTION_REPORTS;
    }


    @RequestMapping ( value = "/mail", method = RequestMethod.GET)
    public String mailUrlResolution( Model model, HttpServletRequest request ) throws InvalidInputException
    {
        LOG.info( "Method to resolve shortened url sent in mail,mailUrlResolution() started." );
        LOG.info( "Parsing query string for ID" );
        String encryptedIDStr = request.getParameter( "q" );
        if ( encryptedIDStr == null || encryptedIDStr.isEmpty() ) {
            LOG.error( "ID value is missing in the query string." );
            throw new InvalidInputException( "ID value is missing in the query string." );
        }
        LOG.info( "Found encrypted ID : " + encryptedIDStr );

        // Retrieve complete url based on the ID
        LOG.info( "Retrieving complete url for the ID found." );
        String completeUrl = urlService.retrieveCompleteUrlForID( encryptedIDStr );
        if ( completeUrl == null || completeUrl.isEmpty() ) {
            LOG.error( "No complete url found for " + encryptedIDStr + " ID." );
            throw new InvalidInputException( "No complete url found for " + encryptedIDStr + " ID." );
        }
        LOG.info( "Retrieved complete url for the ID : " + completeUrl );
        LOG.info( "Method to resolve shortened url sent in mail,mailUrlResolution() ended." );

        // Redirect to complete url found based on the ID.
        return "redirect:" + completeUrl;
    }


    @RequestMapping ( value = "/hierarchyupload", method = RequestMethod.GET)
    public String showUploadHierarchy( Model model, HttpServletRequest request )
    {
        LOG.info( "Showing the hierarchy page" );
        return JspResolver.HIERARCHY_UPLOAD;
    }


    @RequestMapping ( value = "/fetchEditRegionPopupDetails", method = RequestMethod.GET)
    public String fetchEditRegionPopupDetails( Model model, HttpServletRequest request )
    {
        LOG.info( "Showing Edit Region Popup" );
        return JspResolver.EDIT_REGION_POPUP;
    }


    @ResponseBody
    @RequestMapping ( value = "/savexlsxfile", method = RequestMethod.POST)
    public String saveHierarchyFile( Model model, @RequestParam ( "file") MultipartFile fileLocal, HttpServletRequest request )
    {
        boolean status = true;
        String response = null;
        try {
            if ( fileLocal == null ) {
                throw new InvalidInputException( "file is empty" );
            }
            LOG.info( "Saving the hierarchy file" );
            String fileLocalName = request.getParameter( "filename" );

            if ( fileLocalName == null || fileLocalName.isEmpty() ) {
                throw new InvalidInputException( "filename is empty" );
            }

            File convFile = new File( fileLocalName );
            try {
                fileLocal.transferTo( convFile );

                // Set the new filename
                User user = sessionHelper.getCurrentUser();
                fileLocalName = "COMPANY_HIERARCHY_UPLOAD_" + user.getCompany().getCompany() + "_"
                    + new DateTime( System.currentTimeMillis() ).toString() + ".xlsx";
                fileUploadService.uploadFileAtDefautBucket( convFile, fileLocalName );
                String fileName = endpoint + CommonConstants.FILE_SEPARATOR + URLEncoder.encode( fileLocalName, "UTF-8" );
                response = fileName;
            } catch ( Exception e ) {
                LOG.error( "An exception occured during the file upload. Reason : ", e );
                throw new InvalidInputException( "An error occured during the file upload. Reason : ", e );
            }
        } catch ( InvalidInputException ex ) {
            status = false;
            response = ex.getMessage();
        }
        Map<String, Object> responseMap = new HashMap<String, Object>();
        responseMap.put( "status", status );
        responseMap.put( "response", response );
        return new Gson().toJson( responseMap );
    }


    @ResponseBody
    @RequestMapping ( value = "/verifyxlsxfile", method = RequestMethod.POST)
    public String validateHierarchyFile( Model model, HttpServletRequest request ) throws InvalidInputException
    {
        boolean status = true;
        Object response = null;
        UploadValidation uploadValidation = null;
        LOG.info( "Validating the hierarchy file" );
        String fileUrl = request.getParameter( "fileUrl" );
        try {
            if ( fileUrl == null || fileUrl.isEmpty() ) {
                throw new InvalidInputException( "File URL cannot be empty" );
            }
            User user = sessionHelper.getCurrentUser();
            uploadValidation = hierarchyUploadService.validateUserUploadFile( user.getCompany(), fileUrl );
            response = uploadValidation;
        } catch ( InvalidInputException ex ) {
            status = false;
            response = ex.getMessage();
        }

        Map<String, Object> responseMap = new HashMap<String, Object>();
        responseMap.put( "status", status );
        responseMap.put( "response", response );
        return new Gson().toJson( responseMap );
    }


    @ResponseBody
    @RequestMapping ( value = "/verifyxHierarchyUpload", method = RequestMethod.POST)
    public String validateHierarchyUpload( Model model, HttpServletRequest request )
    {
        LOG.info( "Validating the hierarchy upload data" );
        boolean status = true;
        Object response = null;
        String hierarchyJson = request.getParameter( "hierarchyJson" );
        LOG.info( hierarchyJson );
        UploadValidation uploadValidation = new Gson().fromJson( hierarchyJson, UploadValidation.class );
        try {
            User user = sessionHelper.getCurrentUser();
            uploadValidation = hierarchyUploadService.validateHierarchyUploadJson( user.getCompany(), uploadValidation );
            response = uploadValidation;
        } catch ( Exception ex ) {
            status = false;
            response = ex.getMessage();
        }

        Map<String, Object> responseMap = new HashMap<String, Object>();
        responseMap.put( "status", status );
        responseMap.put( "response", response );
        return new Gson().toJson( responseMap );
    }


    @ResponseBody
    @RequestMapping ( value = "/uploadxlsxfile", method = RequestMethod.POST)
    public String saveHierarchyFileData( Model model, HttpServletRequest request ) throws InvalidInputException
    {
        LOG.info( "Saving the hierarchy file data" );
        boolean status = true;
        String response = null;
        String hierarchyJson = request.getParameter( "hierarchyJson" );
        UploadValidation uploadValidation = new Gson().fromJson( hierarchyJson, UploadValidation.class );
        User user = sessionHelper.getCurrentUser();
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        List<String> value = new ArrayList<String>();
        try {
            map = hierarchyStructureUploadService.uploadHierarchy( uploadValidation.getUpload(), user.getCompany(), user );
            if ( map == null || map.isEmpty() ) {
                value.add( "Data uploaded successfully." );
                map.put( "UPLOAD_SUCCESS", value );
            } else {
                status = false;
            }
        } catch ( Exception ex ) {
            status = false;
            value.add( ex.getMessage() );
            map.put( "UPLOAD_FAILED", value );
        }
        response = new Gson().toJson( map );
        Map<String, Object> responseMap = new HashMap<String, Object>();
        responseMap.put( "status", status );
        responseMap.put( "response", response );
        return new Gson().toJson( responseMap );
    }


    @ResponseBody
    @RequestMapping ( value = "/putxlsxfileinbatch", method = RequestMethod.POST)
    public String putHierarchyUploadInBatch( Model model, HttpServletRequest request ) throws InvalidInputException
    {
        LOG.info( "Putting the hierarchy upload in batch" );
        boolean status = true;
        String response = null;
        String hierarchyJson = request.getParameter( "hierarchyJson" );
        UploadValidation uploadValidation = new Gson().fromJson( hierarchyJson, UploadValidation.class );
        try {
            // Insert upload validation object in mongo
            hierarchyStructureUploadService.saveHierarchyUploadInMongo( uploadValidation.getUpload() );

            // Insert a row in UPLOAD_STATUS with status = 0

            response = "Hierarchy upload batch is initialized successfully";
        } catch ( Exception ex ) {
            status = false;
            response = ex.getMessage();
        }
        Map<String, Object> responseMap = new HashMap<String, Object>();
        responseMap.put( "status", status );
        responseMap.put( "response", response );
        return new Gson().toJson( responseMap );
    }


    @ResponseBody
    @RequestMapping ( value = "/fetchUploadBatchStatus", method = RequestMethod.POST)
    public String fetchUploadBatchStatus( Model model, HttpServletRequest request ) throws InvalidInputException
    {
        LOG.info( "Fetching the latest batch processing message" );
        boolean status = true;
        String response = null;
        try {
            // Fetch the latest message from UPLOAD_STATUS

            response = "Upload Batch started..";

        } catch ( Exception ex ) {
            status = false;
            response = ex.getMessage();
        }
        Map<String, Object> responseMap = new HashMap<String, Object>();
        responseMap.put( "status", status );
        responseMap.put( "response", response );
        return new Gson().toJson( responseMap );
    }


    @SuppressWarnings ( "unused")
    private UploadValidation prepareDummyValidation()
    {
        UploadValidation validation = new UploadValidation();
        validation.setNumberOfRegionsAdded( 3 );
        validation.setNumberOfRegionsModified( 5 );
        validation.setNumberOfBranchesAdded( 10 );
        validation.setNumberOfBranchesModified( 25 );
        validation.setNumberOfUsersModified( 10 );

        validation.setRegionValidationErrors(
            Arrays.asList( new String[] { "Region ABC does not look good.", "What is wrong with the region below abc?" } ) );
        validation.setBranchValidationErrors( Arrays.asList( new String[] { "Branch B does not have a region." } ) );
        validation.setUserValidationErrors(
            Arrays.asList( new String[] { "User A has no assignments.", "Are you kidding me with that?" } ) );

        validation.setRegionValidationWarnings( Arrays.asList( new String[] { "Region names are funny." } ) );
        validation.setBranchValidationWarnings( Arrays
            .asList( new String[] { "Branches all around, but no tree to support them.", "That was a very bad joke." } ) );

        return validation;
    }


    @ResponseBody
    @RequestMapping ( value = "/getunmatchedpreinitiatedsurveys", method = RequestMethod.GET)
    public String getUnmatchedPreinitiatedSurveys( HttpServletRequest request, Model model )
    {
        LOG.info( "Method to get getUnmatchedPreinitiatedSurveys started" );
        String startIndexStr = request.getParameter( "startIndex" );
        String batchSizeStr = request.getParameter( "batchSize" );

        if ( startIndexStr == null || batchSizeStr == null ) {
            LOG.error( "Null value found for startIndex or batch size." );
            return "Null value found for startIndex or batch size.";
        }

        SurveyPreInitiationList surveyPreInitiationList = new SurveyPreInitiationList();
        int startIndex;
        int batchSize;
        try {

            User user = sessionHelper.getCurrentUser();
            if ( user == null || user.getCompany() == null ) {
                throw new NonFatalException( "Insufficient permission for this process" );
            }

            try {
                startIndex = Integer.parseInt( startIndexStr );
                batchSize = Integer.parseInt( batchSizeStr );
            } catch ( NumberFormatException e ) {
                LOG.error(
                    "NumberFormatException caught while trying to convert startIndex or batchSize or companyId  Nested exception is ",
                    e );
                throw e;
            }


            surveyPreInitiationList = socialManagementService.getUnmatchedPreInitiatedSurveys( user.getCompany().getCompanyId(),
                startIndex, batchSize );
        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException while fetching posts. Reason :" + nonFatalException.getMessage(), nonFatalException );
            model.addAttribute( "message", messageUtils.getDisplayMessage(
                DisplayMessageConstants.FETCH_UNMATCHED_PREINITIATED_SURVEYS_UNSUCCESSFUL, DisplayMessageType.ERROR_MESSAGE ) );
        }
        LOG.info( "Method to get posts for the user, getUnmatchedPreinitiatedSurveys() finished" );
        return new Gson().toJson( surveyPreInitiationList );
    }


    @ResponseBody
    @RequestMapping ( value = "/getprocessedpreinitiatedsurveys", method = RequestMethod.GET)
    public String getProcessedPreInitiatedSurveys( HttpServletRequest request, Model model )
    {
        LOG.info( "Method to get getProcessedPreInitiatedSurveys started" );
        String startIndexStr = request.getParameter( "startIndex" );
        String batchSizeStr = request.getParameter( "batchSize" );
        SurveyPreInitiationList surveyPreInitiationList = new SurveyPreInitiationList();
        int startIndex;
        int batchSize;

        if ( startIndexStr == null || batchSizeStr == null ) {
            LOG.error( "Null value found for startIndex or batch size." );
            return "Null value found for startIndex or batch size.";
        }
        try {

            User user = sessionHelper.getCurrentUser();
            if ( user == null || user.getCompany() == null ) {
                throw new NonFatalException( "Insufficient permission for this process" );
            }
            try {
                startIndex = Integer.parseInt( startIndexStr );
                batchSize = Integer.parseInt( batchSizeStr );
            } catch ( NumberFormatException e ) {
                LOG.error(
                    "NumberFormatException caught while trying to convert startIndex or batchSize or companyId  Nested exception is ",
                    e );
                throw e;
            }

            surveyPreInitiationList = socialManagementService.getProcessedPreInitiatedSurveys( user.getCompany().getCompanyId(),
                startIndex, batchSize );
        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException while fetching posts. Reason :" + nonFatalException.getMessage(), nonFatalException );
            model.addAttribute( "message", messageUtils.getDisplayMessage(
                DisplayMessageConstants.FETCH_PROCESSED_PREINITIATED_SURVEYS_UNSUCCESSFUL, DisplayMessageType.ERROR_MESSAGE ) );
        }
        LOG.info( "Method to get posts for the user, getProcessedPreInitiatedSurveys() finished" );
        return new Gson().toJson( surveyPreInitiationList );
    }


    @ResponseBody
    @RequestMapping ( value = "/saveemailmapping", method = RequestMethod.GET)
    public String saveUserEmailMapping( HttpServletRequest request, Model model )
    {
        LOG.info( "Method to get saveUserEmailMapping started" );
        String emailAddress = request.getParameter( "emailAddress" );
        String agentIdStr = request.getParameter( "agentId" );
        String ignoredEmailStr = request.getParameter( "ignoredEmail" );

        try {
            boolean ignoredEmail;
            long agentId;

            try {
                agentId = Integer.parseInt( agentIdStr );
                ignoredEmail = Boolean.parseBoolean( ignoredEmailStr );
            } catch ( NumberFormatException e ) {
                LOG.error( "NumberFormatException caught while trying to convert agentId Nested exception is ", e );
                throw e;
            }
            if ( emailAddress == null || emailAddress.isEmpty() ) {
                throw new InvalidInputException( "Email Id can't be null or empty" );
            }

            User loggedInUser = sessionHelper.getCurrentUser();
            if ( loggedInUser == null || loggedInUser.getCompany() == null ) {
                throw new NonFatalException( "Insufficient permission for this process" );
            }


            try {
                User existingUser = userManagementService.getUserByEmailAddress( emailAddress );
                if ( existingUser != null )
                    throw new UserAlreadyExistsException(
                        "The email addresss " + emailAddress + " is already present in our database." );
            } catch ( NoRecordsFetchedException e ) {
                if ( ignoredEmail ) {
                    userManagementService.saveIgnoredEmailCompanyMapping( emailAddress,
                        loggedInUser.getCompany().getCompanyId() );
                    socialManagementService.updateSurveyPreinitiationRecordsAsIgnored( emailAddress );
                } else {
                    User user = userManagementService.saveEmailUserMapping( emailAddress, agentId );
                    socialManagementService.updateAgentIdOfSurveyPreinitiationRecordsForEmail( user, emailAddress );
                }
            }

        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException while fetching posts. Reason :" + nonFatalException.getMessage(), nonFatalException );
            if ( nonFatalException.getMessage() != null && !nonFatalException.getMessage().isEmpty() ) {
                return nonFatalException.getMessage();
            }
            return messageUtils.getDisplayMessage( DisplayMessageConstants.ADD_EMAIL_ID_FOR_USER__UNSUCCESSFUL,
                DisplayMessageType.ERROR_MESSAGE ).getMessage();
        }
        LOG.info( "Method to get posts for the user, saveUserEmailMapping() finished" );
        return messageUtils
            .getDisplayMessage( DisplayMessageConstants.ADD_EMAIL_ID_FOR_USER__SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE )
            .getMessage();
    }

}
// JIRA: SS-24 BY RM02 EOC