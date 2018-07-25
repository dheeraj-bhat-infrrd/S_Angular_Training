package com.realtech.socialsurvey.web.controller;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import com.realtech.socialsurvey.core.commons.WidgetTemplateConstants;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.AbusiveMailSettings;
import com.realtech.socialsurvey.core.entities.AccountsMaster;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.ComplaintResolutionSettings;
import com.realtech.socialsurvey.core.entities.DisplayMessage;
import com.realtech.socialsurvey.core.entities.DotLoopCrmInfo;
import com.realtech.socialsurvey.core.entities.EncompassCrmInfo;
import com.realtech.socialsurvey.core.entities.LicenseDetail;
import com.realtech.socialsurvey.core.entities.LockSettings;
import com.realtech.socialsurvey.core.entities.MailContent;
import com.realtech.socialsurvey.core.entities.MailContentSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.ParsedHierarchyUpload;
import com.realtech.socialsurvey.core.entities.StateLookup;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveySettings;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserEmailMapping;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.entities.UserSettings;
import com.realtech.socialsurvey.core.entities.VerticalCrmMapping;
import com.realtech.socialsurvey.core.entities.widget.WidgetConfigurationRequest;
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
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.payment.Payment;
import com.realtech.socialsurvey.core.services.payment.exception.CreditCardException;
import com.realtech.socialsurvey.core.services.payment.exception.PaymentException;
import com.realtech.socialsurvey.core.services.payment.exception.SubscriptionPastDueException;
import com.realtech.socialsurvey.core.services.payment.exception.SubscriptionUnsuccessfulException;
import com.realtech.socialsurvey.core.services.payment.exception.SubscriptionUpgradeUnsuccessfulException;
import com.realtech.socialsurvey.core.services.reportingmanagement.ReportingDashboardManagement;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.services.settingsmanagement.SettingsLocker;
import com.realtech.socialsurvey.core.services.settingsmanagement.SettingsSetter;
import com.realtech.socialsurvey.core.services.settingsmanagement.impl.InvalidSettingsStateException;
import com.realtech.socialsurvey.core.services.social.SocialManagementService;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyBuilder;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;
import com.realtech.socialsurvey.core.services.upload.FileUploadService;
import com.realtech.socialsurvey.core.services.upload.HierarchyUploadService;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.EmailFormatHelper;
import com.realtech.socialsurvey.core.utils.EncryptionHelper;
import com.realtech.socialsurvey.core.utils.FileOperations;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.core.utils.StateLookupExclusionStrategy;
import com.realtech.socialsurvey.core.vo.SurveyPreInitiationList;
import com.realtech.socialsurvey.core.vo.UserList;
import com.realtech.socialsurvey.core.workbook.utils.WorkbookData;
import com.realtech.socialsurvey.web.api.builder.SSApiIntergrationBuilder;
import com.realtech.socialsurvey.web.api.entities.FtpCreateRequest;
import com.realtech.socialsurvey.web.api.exception.SSAPIException;
import com.realtech.socialsurvey.web.common.JspResolver;

import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


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
    private ProfileManagementService profileManagementService;

    @Autowired
    private Payment gateway;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private HierarchyUploadService hierarchyUploadService;

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
    private ReportingDashboardManagement  reportingDashboardManagement;

    @Autowired
    private EncryptionHelper encryptionHelper;
    
    @Autowired
    private SSApiIntergrationBuilder ssApiIntergrationBuilder;
    
    @Autowired
    private FileOperations fileOperations;

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

    @Value ( "${SALES_LEAD_EMAIL_ADDRESS}")
    private String salesLeadEmail;

    @Autowired
    private WorkbookData workbookData;


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
            LOG.error( "NonFatalException while uploading Logo.", e );
            message = e.getMessage();
        }
        return message;
    }


    /**
     * Method to call service for adding company information for a user
     * 
     * @param redirectAttributes
     * @param request
     * @return
     */
    @RequestMapping ( value = "/addcompanyinformation", method = RequestMethod.POST)
    public String addCompanyInformation( HttpServletRequest request, RedirectAttributes redirectAttributes )
    {
        LOG.info( "Method addCompanyInformation of OrganizationManagementController called" );
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
            LOG.error( "NonFatalException while adding company information. Reason :", e );
            redirectAttributes.addFlashAttribute( "status", DisplayMessageType.ERROR_MESSAGE );
            redirectAttributes.addFlashAttribute( "message",
                messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
            return "redirect:/" + JspResolver.COMPANY_INFORMATION_PAGE + ".do";
        }

        LOG.info( "Method addCompanyInformation of OrganizationManagementController completed successfully" );
        return "redirect:/" + JspResolver.ACCOUNT_TYPE_SELECTION_PAGE + ".do";
    }


    @RequestMapping ( value = "/selectaccounttype")
    public String initSelectAccountTypePage()
    {
        LOG.info( "SelectAccountType Page initSelectAccountTypePage() function started" );
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
        LOG.debug( "Method validateCompanyInfoParams called  for companyName : {}, address : {}, zipCode : {}, companyContactNo : {} " ,companyName,address,zipCode,companyContactNo );

        if ( companyName == null || companyName.isEmpty() || companyName.contains( "\"" ) ) {
            LOG.warn( "Method validateCompanyInfoParams is throwing an InvalidInputException for invalid company name" );
            throw new InvalidInputException( "Company name is null or empty while adding company information",
                DisplayMessageConstants.INVALID_COMPANY_NAME );
        }
        if ( address == null || address.isEmpty() ) {
            LOG.warn( "Method validateCompanyInfoParams is throwing an InvalidInputException for invalid address" );
            throw new InvalidInputException( "Address is null or empty while adding company information",
                DisplayMessageConstants.INVALID_ADDRESS );
        }

        if ( country == null || country.isEmpty() ) {
            LOG.warn( "Method validateCompanyInfoParams is throwing an InvalidInputException for invalid country" );
            throw new InvalidInputException( "Country is null or empty while adding company information",
                DisplayMessageConstants.INVALID_COUNTRY );
        }

        if ( countryCode == null || countryCode.isEmpty() ) {
            LOG.warn( "Method validateCompanyInfoParams is throwing an InvalidInputException for invalid country code" );
            throw new InvalidInputException( "Country code is null or empty while adding company information",
                DisplayMessageConstants.INVALID_COUNTRY );
        }

        if ( zipCode == null || zipCode.isEmpty() ) {
            LOG.warn( "Method validateCompanyInfoParams is throwing an InvalidInputException for invalid zipCode" );
            throw new InvalidInputException( "Zipcode is not valid while adding company information",
                DisplayMessageConstants.INVALID_ZIPCODE );
        }

        if ( companyContactNo == null || companyContactNo.isEmpty() ) {
            LOG.warn( "Method validateCompanyInfoParams is throwing an InvalidInputException for invalid companyContactNo" );
            throw new InvalidInputException( "Company contact number is not valid while adding company information",
                DisplayMessageConstants.INVALID_COMPANY_PHONEN0 );
        }

        if ( vertical == null || vertical.isEmpty() ) {
            LOG.warn( "Method validateCompanyInfoParams is throwing an InvalidInputException for invalid vertical" );
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
        LOG.debug( "Getting complete address for address1 : {} and address2 : {}",address1,address2 );
        String address = address1;
        /**
         * if address line 2 is present, append it to address1 else the complete address is address1
         */
        if ( address1 != null && !address1.isEmpty() && address2 != null && !address2.isEmpty() ) {
            address = address1 + " " + address2;
        }
        LOG.debug( "Returning complete address : ",address );
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
        LOG.info( "Method addAccountType of OrganizationManagementController called" );
        String strAccountType = request.getParameter( "accounttype" );
        String returnPage = null;
        try {
            if ( strAccountType == null || strAccountType.isEmpty() ) {
                LOG.warn( "Method addAccountType is throwing an InvalidInputException for invalid strAccountType" );
                throw new InvalidInputException( "Accounttype is null for adding account type",
                    DisplayMessageConstants.INVALID_ADDRESS );
            }
            LOG.debug( "AccountType obtained : " + strAccountType );

            User user = sessionHelper.getCurrentUser();

            // JIRA - SS-536

            // We check if there is mapped survey for the company and add a default survey if
            // not.
            if ( surveyBuilder.checkForExistingSurvey( user ) == null ) {
                surveyBuilder.createNewSurveyForCompany( user );
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

                LOG.info( "Method addAccountType of OrganizationManagementController completed successfully" );
                returnPage = JspResolver.PAYMENT;
            }
            unlockIndividualAccountLogoSettings( user, Integer.parseInt( strAccountType ) );
        } catch ( NonFatalException e ) {
            LOG.error( "NonfatalException while adding account type. Reason: ", e );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
            return JspResolver.MESSAGE_HEADER;
        }
        LOG.info( "Method addAccountType of OrganizationManagementController completed successfully" );
        return returnPage;
    }


    // Check if the account type is individual. If so, check if the logo is set. If so, unlock it
    private void unlockIndividualAccountLogoSettings( User user, int accountType ) throws NonFatalException
    {
        LOG.debug( "Unlocking the logo for individual account and Checking if the account type is individual" );
        if ( accountType == CommonConstants.ACCOUNTS_MASTER_INDIVIDUAL ) {
            LOG.debug( "Check if logo is set" );
            if ( settingsSetter.isSettingsValueSet( OrganizationUnit.COMPANY,
                new BigInteger( user.getCompany().getSettingsSetStatus() ), SettingsForApplication.LOGO ) ) {
                LOG.debug( "Unlocking the logo" );
                try {
                    if ( settingsLocker.isSettingsValueLocked( OrganizationUnit.COMPANY,
                    		new BigInteger( user.getCompany().getSettingsLockStatus() ), SettingsForApplication.LOGO ) ) {
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
        LOG.info( "Method showWidget of OrganizationManagementController completed successfully" );
        return JspResolver.SHOW_WIDGET;
    }
    
    @RequestMapping ( value = "/shownewwidget", method = RequestMethod.GET)
    public String showNewWidget( Model model, HttpServletRequest request )
    {
        LOG.info( "Method showNewWidget of OrganizationManagementController called" );
        
        try {  
            User user = sessionHelper.getCurrentUser();
            HttpSession session = request.getSession( false );
            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            
            OrganizationUnitSettings unitSettings = organizationManagementService.getEntitySettings( entityId, entityType );
            OrganizationUnitSettings companySettings;
            
            companySettings = organizationManagementService.getCompanySettings( user.getCompany().getCompanyId() );
            
            
            model.addAttribute( "applicationBaseUrl", applicationBaseUrl );
            model.addAttribute( "profileName", unitSettings.getProfileName() );
            model.addAttribute( "companyProfileName", companySettings.getProfileName() );
            model.addAttribute( "resourcesUrl", endpoint );
            model.addAttribute( "widgetPlaceAndForget",
                URLEncoder.encode(
                    fileOperations.getContentFromFile(
                        WidgetTemplateConstants.WIDGET_TEMPLATES_FOLDER + WidgetTemplateConstants.WIDGET_PLACE_AND_FORGET ),
                    CommonConstants.UTF_8_ENCODING ) );
            model.addAttribute( "widgetCustomContainer",
                URLEncoder.encode(
                    fileOperations.getContentFromFile(
                        WidgetTemplateConstants.WIDGET_TEMPLATES_FOLDER + WidgetTemplateConstants.WIDGET_CUSTOM_CONTAINER ),
                    CommonConstants.UTF_8_ENCODING ) );
            model.addAttribute( "widgetJavascriptIframe",
                URLEncoder.encode(
                    fileOperations.getContentFromFile(
                        WidgetTemplateConstants.WIDGET_TEMPLATES_FOLDER + WidgetTemplateConstants.WIDGET_JAVASCRIPT_IFRAME ),
                    CommonConstants.UTF_8_ENCODING ) );

            
        } catch ( NonFatalException e ) {
            LOG.error( "NonfatalException while showing javascript widget settings. Reason: ", e );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
            return JspResolver.MESSAGE_HEADER;
        } catch( UnsupportedEncodingException e ) {
            LOG.error( "NonfatalException while showing javascript widget settings. Reason: ", e );
            model.addAttribute( "message", "UTF-8 not supported" );
            return JspResolver.MESSAGE_HEADER;            
        }

        
        LOG.info( "Method showNewWidget of OrganizationManagementController completed successfully" );
        return JspResolver.SHOW_NEW_WIDGET;
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
                LOG.warn( "Method showAppSettings is throwing an InvalidInputException cause exception occured while fetching vertical crm mappings",e );
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
                    LOG.error( "Number format exception occurred while parsing the entity id. Reason : ", e );
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
            
            // try and decipher encompass password 
            organizationManagementService.decryptEncompassPasswordIfPossible( unitSettings );
            
            model.addAttribute( CommonConstants.USER_APP_SETTINGS, unitSettings );


            model.addAttribute( CommonConstants.ENCOMPASS_VERSION_LIST,
                organizationManagementService.getActiveEncompassSdkVersions() );

            //REALTECH_USER_ID is set only for real tech and SS admin
            boolean isRealTechOrSSAdmin = false;
            Long adminUserid = (Long) session.getAttribute( CommonConstants.REALTECH_USER_ID );
            if ( adminUserid != null ) {
                isRealTechOrSSAdmin = true;
            }
            model.addAttribute( "isRealTechOrSSAdmin", isRealTechOrSSAdmin );


        } catch ( NonFatalException e ) {
            LOG.error( "NonfatalException while showing app settings. Reason: ", e );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
            return JspResolver.MESSAGE_HEADER;
        }
        model.addAttribute( "crmMappings", mappings );
        LOG.info( "Method showAppSettings of OrganizationManagementController completed successfully" );

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
        LOG.info( "Method showCompanySettings of OrganizationManagementController called" );
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
                LOG.error( "Number format exception occurred while parsing the entity id. Reason : ", e );
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
            model.addAttribute( "autoPostLinkToUserSite", false );
            model.addAttribute( "vendastaAccess", unitSettings.isVendastaAccessible() );
            
            // prepare digest recipients String
            String digestRecipients = "";
            if( unitSettings.getDigestRecipients() != null ){
                digestRecipients = StringUtils.join( unitSettings.getDigestRecipients(), ",\n" );
            }
            model.addAttribute( "digestRecipients", digestRecipients );

            // prepare user notify recipients String
            String userNotifyRecipients = "";
            if( unitSettings.getUserAddDeleteNotificationRecipients() != null ){
                userNotifyRecipients = StringUtils.join( unitSettings.getUserAddDeleteNotificationRecipients(), ",\n" );
            }
            model.addAttribute( "userNotifyRecipients", userNotifyRecipients );

            //set allow parter survey
            boolean allowPartnerSurvey = false;
            if ( unitSettings.getCrm_info() != null )
                allowPartnerSurvey = unitSettings.getCrm_info().isAllowPartnerSurvey();

            model.addAttribute( "allowPartnerSurvey", allowPartnerSurvey );
            model.addAttribute( "includeForTransactionMonitor", unitSettings.getIncludeForTransactionMonitor() );


            OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user );
            model.addAttribute( "reviewSortCriteria", profileManagementService.processSortCriteria( companySettings.getIden(),
                companySettings.getReviewSortCriteria() ) );
            
            model.addAttribute( "hidePublicPage", unitSettings.isHidePublicPage() );
            model.addAttribute( "hiddenSection", unitSettings.isHiddenSection() );
            model.addAttribute( "hideFromBreadCrumb", unitSettings.getHideFromBreadCrumb() );
            model.addAttribute( "allowOverrideForSocialMedia", unitSettings.isAllowOverrideForSocialMedia() );
            model.addAttribute( "sendEmailFromCompany", unitSettings.isSendEmailFromCompany() );
           
            //REALTECH_USER_ID is set only for real tech and SS admin
            boolean isRealTechOrSSAdmin = false;
            Long adminUserid = (Long) session.getAttribute( CommonConstants.REALTECH_USER_ID );
            if ( adminUserid != null ) {
                isRealTechOrSSAdmin = true;
            }
            model.addAttribute( "isRealTechOrSSAdmin", isRealTechOrSSAdmin );

            if ( surveySettings != null ) {
                model.addAttribute( "autoPostEnabled", surveySettings.isAutoPostEnabled() );
                model.addAttribute( "minpostscore", surveySettings.getShow_survey_above_score() );
                model.addAttribute( "autoPostLinkToUserSite", surveySettings.isAutoPostLinkToUserSiteEnabled() );
            }

            //enocode before sending to UI
            encodeSurveySettings( unitSettings.getSurvey_settings() );
            session.setAttribute( CommonConstants.USER_ACCOUNT_SETTINGS, unitSettings );

            //get default setting and store in model
            SurveySettings defaultSurveySettings = organizationManagementService.retrieveDefaultSurveyProperties();
            //enocode before sending to UI
            encodeSurveySettings( defaultSurveySettings );
            model.addAttribute( "defaultSurveyProperties", defaultSurveySettings );

            if ( companySettings.getSendEmailThrough() == null ) {
                model.addAttribute( "sendEmailThrough", CommonConstants.SEND_EMAIL_THROUGH_SOCIALSURVEY_ME );
            } else {
                model.addAttribute( "sendEmailThrough", companySettings.getSendEmailThrough() );

            }

            
         // adding the survey completion mail threshold
            if ( unitSettings.getSurvey_settings() != null ) {
                model.addAttribute( CommonConstants.SURVEY_MAIL_THRESHOLD,
                    unitSettings.getSurvey_settings().getSurveyCompletedMailThreshold() );
            }
            
            // add send monthly digest email flag
            model.addAttribute( "sendMonthlyDigestMail", unitSettings.isSendMonthlyDigestMail() );
            model.addAttribute( "copyToClipBoard", unitSettings.getIsCopyToClipboard() );
            
            // add isSocialMonitorEnabled flag
            model.addAttribute( "isSocialMonitorEnabled", unitSettings.isSocialMonitorEnabled() );
            
            
            // add isSocialMonitorEnabled flag
            model.addAttribute( "isSocialMonitorEnabled", unitSettings.isSocialMonitorEnabled() );
            
            
        } catch ( InvalidInputException | NoRecordsFetchedException e ) {
            LOG.error( "NonFatalException while fetching profile details. Reason : ", e );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( DisplayMessageConstants.GENERAL_ERROR, DisplayMessageType.ERROR_MESSAGE ) );
            return JspResolver.MESSAGE_HEADER;
        }
        LOG.info( "Method showCompanySettings of OrganizationManagementController completed successfully" );

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

        String buyerAgentEmail = request.getParameter( "buyer-agent-email" );
        String buyerAgentName = request.getParameter( "buyer-agent-name" );
        String sellerAgentEmail = request.getParameter( "seller-agnt-email" );
        String sellerAgentName = request.getParameter( "seller-agnt-name" );
        String propertyAddress = request.getParameter( "property-address" );
        String loanProcessorEmail = request.getParameter( "loan-processor-email" );
        String loanProcessorName = request.getParameter( "loan-processor-name" );
        
        String loanOfficerEmail = request.getParameter( "loan-officer-email" );
        String loanOfficerName = request.getParameter( "loan-officer-name" );

        String version = request.getParameter( "sdk-version-selection-list" );

        Map<String, Object> responseMap = new HashMap<String, Object>();
        String message;
        boolean status = true;

        try {

            if ( encompassUsername == null || encompassUsername.isEmpty() ) {
                LOG.warn( "Method saveEncompassDetails is throwing an InvalidInputException since user name can not be empty" );
                throw new InvalidInputException( "User name can not be empty" );
            }
            if ( encompassPassword == null || encompassPassword.isEmpty() ) {
                LOG.warn( "Method saveEncompassDetails is throwing an InvalidInputException since password can not be empty" );
                throw new InvalidInputException( "Password can not be empty" );
            }
            if ( encompassUrl == null || encompassUrl.isEmpty() ) {
                LOG.warn( "Method saveEncompassDetails is throwing an InvalidInputException since url can not be empty" );
                throw new InvalidInputException( "Url can not be empty" );
            }
            if ( encompassFieldId == null || encompassFieldId.isEmpty() ) {
                LOG.debug( "Field Id is empty" );
                encompassFieldId = CommonConstants.ENCOMPASS_DEFAULT_FEILD_ID;
            }

            if ( version == null || version.isEmpty() ) {
                LOG.warn( "Method saveEncompassDetails is throwing an InvalidInputException since version can not be empty" );
                throw new InvalidInputException( "version can not be empty" );
            }

            if ( state == null || state.isEmpty() || state.equals( CommonConstants.CRM_INFO_DRY_RUN_STATE ) ) {
                state = CommonConstants.CRM_INFO_DRY_RUN_STATE;
            } else {
                state = CommonConstants.CRM_INFO_PRODUCTION_STATE;
            }

            // encrypting the password
            String cipherPassword = encryptionHelper.encryptAES( encompassPassword, "" );

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

            encompassCrmInfo.setVersion( version );
            
            //save loan officer name and email fields details if given
            if( ! StringUtils.isEmpty(loanOfficerEmail)) {
            	encompassCrmInfo.setLoanOfficerEmail(loanOfficerEmail);
            }
            if( ! StringUtils.isEmpty(loanOfficerName)) {
            	encompassCrmInfo.setLoanOfficerName(loanOfficerName);
            }

            //check if it's need to update real state agent detail
            if ( !StringUtils.isEmpty( buyerAgentEmail ) || !StringUtils.isEmpty( buyerAgentName )
                || !StringUtils.isEmpty( sellerAgentEmail ) || !StringUtils.isEmpty( sellerAgentName ) ) {

                if ( StringUtils.isEmpty( buyerAgentEmail ) ) {
                    throw new InvalidInputException( "Buyer agent email can not be empty" );
                }
                if ( StringUtils.isEmpty( buyerAgentName ) ) {
                    throw new InvalidInputException( "Buyer agent name can not be empty" );
                }
                if ( StringUtils.isEmpty( sellerAgentEmail ) ) {
                    throw new InvalidInputException( "Seller agent email can not be empty" );
                }
                if ( StringUtils.isEmpty( sellerAgentName ) ) {
                    throw new InvalidInputException( "Seller agent name can not be empty" );
                }

                encompassCrmInfo.setBuyerAgentEmail( buyerAgentEmail );
                encompassCrmInfo.setBuyerAgentName( buyerAgentName );
                encompassCrmInfo.setSellerAgentEmail( sellerAgentEmail );
                encompassCrmInfo.setSellerAgentName( sellerAgentName );
            }
            
            // save property address
            if( StringUtils.isNotEmpty(propertyAddress) ) {
            	encompassCrmInfo.setPropertyAddress(propertyAddress);
            } else {
            	encompassCrmInfo.setPropertyAddress("");            	
            }
            
            
            // save loan processor name and email
            if( StringUtils.isNotEmpty(loanProcessorName) ) {
            	encompassCrmInfo.setLoanProcessorName(loanProcessorName);
            } else {
            	encompassCrmInfo.setLoanProcessorName("");            	
            }
            
            if( StringUtils.isNotEmpty( loanProcessorEmail ) ) {
            	encompassCrmInfo.setLoanProcessorEmail(loanProcessorEmail);
            } else {
            	encompassCrmInfo.setLoanProcessorEmail("");            	            	
            }

            organizationManagementService.updateCRMDetails( companySettings, encompassCrmInfo,
                "com.realtech.socialsurvey.core.entities.EncompassCrmInfo" );

            // set the updated settings value in session with plain password
            encompassCrmInfo.setCrm_password( encompassPassword );
            companySettings.setCrm_info( encompassCrmInfo );
            message = messageUtils.getDisplayMessage( DisplayMessageConstants.ENCOMPASS_DATA_UPDATE_SUCCESSFUL,
                DisplayMessageType.SUCCESS_MESSAGE ).getMessage();
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while testing encompass detials. Reason : ", e );
            status = false;
            message = messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ).getMessage();
        }
        responseMap.put( "status", status );
        responseMap.put( "message", message );
        String response = new Gson().toJson( responseMap );
        LOG.info( "Saving encompass details completed successfully" );
        return response;
    }

    @RequestMapping ( value = "/saveftpdetails", method = RequestMethod.POST)
    @ResponseBody
    public String saveFtpDetails( Model model, HttpServletRequest request )
    {
        LOG.info( "Saving ftp details" );
        User user = sessionHelper.getCurrentUser();
        request.setAttribute( "saveftpdetails", "true" );

        String ftpUsername = request.getParameter( "ftp-username" );
        String ftpPassword = request.getParameter( "ftp-password" );
        String ftpUrl = request.getParameter( "ftp-url" );
        String ftpDir = request.getParameter( "ftp-dir" );
        
        Map<String, Object> responseMap = new HashMap<String, Object>();
        String message;
        boolean status = true;

        try {

            if ( ftpUsername == null || ftpUsername.isEmpty() ) {
                LOG.warn( "Method saveFtpDetails is throwing an InvalidInputException since user name can not be empty" );
                throw new InvalidInputException( "User name can not be empty" );
            }
            if ( ftpPassword == null || ftpPassword.isEmpty() ) {
                LOG.warn( "Method saveFtpDetails is throwing an InvalidInputException since password can not be empty" );
                throw new InvalidInputException( "Password can not be empty" );
            }
            if ( ftpUrl == null || ftpUrl.isEmpty() ) {
                LOG.warn( "Method saveFtpDetails is throwing an InvalidInputException since url can not be empty" );
                throw new InvalidInputException( "Url can not be empty" );
            }
            if ( ftpDir == null || ftpDir.isEmpty() ) {
                LOG.warn( "Method saveFtpDetails is throwing an InvalidInputException since directory can not be empty" );
                throw new InvalidInputException( "directory can not be empty" );
            }

            // encrypting the password
            String cipherPassword = encryptionHelper.encryptAES( ftpPassword, "" );
            
            //create ftpCreateRequest object
            FtpCreateRequest ftpCreateRequest = new FtpCreateRequest();
            ftpCreateRequest.setUsername( ftpUsername );
            ftpCreateRequest.setPassword( cipherPassword );
            ftpCreateRequest.setFtpServerUrl( ftpUrl );
            ftpCreateRequest.setFtpDirectoryPath( ftpDir );
            
            
            //call api to save ftp data
            ssApiIntergrationBuilder.getIntegrationApi().setFtpCrm(user.getCompany().getCompanyId() , ftpCreateRequest);

            message = messageUtils.getDisplayMessage( DisplayMessageConstants.FTP_DATA_UPDATE_SUCCESSFUL,
                DisplayMessageType.SUCCESS_MESSAGE ).getMessage();
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while testing encompass detials. Reason : ", e );
            status = false;
            message = messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ).getMessage();
        }
        responseMap.put( "status", status );
        responseMap.put( "message", message );
        String response = new Gson().toJson( responseMap );
        LOG.info( "Saving ftp details completed successfully" );
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
        HttpSession session = request.getSession( false );
        Long adminUserid = (Long) session.getAttribute( CommonConstants.REALTECH_USER_ID );
        String eventFiredBy = adminUserid != null ? CommonConstants.ADMIN_USER_NAME : String.valueOf( user.getUserId() );
        String message;

        try {
            OrganizationUnitSettings companySettings = organizationManagementService
                .getCompanySettings( user.getCompany().getCompanyId() );
            EncompassCrmInfo encompassCrmInfo = (EncompassCrmInfo) companySettings.getCrm_info();
            encompassCrmInfo.setState( CommonConstants.CRM_INFO_PRODUCTION_STATE );
            encompassCrmInfo.setNumberOfDays( 0 );
            encompassCrmInfo.setEmailAddressForReport( null );
            encompassCrmInfo.setGenerateReport( false );
            organizationManagementService.updateCRMDetails( companySettings, encompassCrmInfo,
                "com.realtech.socialsurvey.core.entities.EncompassCrmInfo" );
            organizationManagementService.logEvent( CommonConstants.ENCOMPASS_CONNECTION, CommonConstants.ACTION_ENABLED,
                eventFiredBy, user.getCompany().getCompanyId(), 0, 0, 0 );
            message = messageUtils
                .getDisplayMessage( DisplayMessageConstants.ENCOMPASS_ENABLE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE )
                .getMessage();
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while saving encompass detials. Reason : ", e );
            message = messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ).getMessage();
        }
        LOG.info( "Updating encompass details to 'Enabled' completed successfully" );
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
        HttpSession session = request.getSession( false );
        Long adminUserid = (Long) session.getAttribute( CommonConstants.REALTECH_USER_ID );
        String eventFiredBy = adminUserid != null ? CommonConstants.ADMIN_USER_NAME : String.valueOf( user.getUserId() );
        String message;

        try {
            OrganizationUnitSettings companySettings = organizationManagementService
                .getCompanySettings( user.getCompany().getCompanyId() );
            EncompassCrmInfo encompassCrmInfo = (EncompassCrmInfo) companySettings.getCrm_info();
            encompassCrmInfo.setState( CommonConstants.CRM_INFO_DRY_RUN_STATE );
            organizationManagementService.updateCRMDetails( companySettings, encompassCrmInfo,
                "com.realtech.socialsurvey.core.entities.EncompassCrmInfo" );
            organizationManagementService.logEvent( CommonConstants.ENCOMPASS_CONNECTION, CommonConstants.ACTION_DISABLED,
                eventFiredBy, user.getCompany().getCompanyId(), 0, 0, 0 );
            message = messageUtils
                .getDisplayMessage( DisplayMessageConstants.ENCOMPASS_DISABLE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE )
                .getMessage();
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while disabling encompass. Reason : ", e );
            message = messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ).getMessage();
        }
        LOG.info( "Updating encompass details to 'Disabled' completed successfully" );
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
            String numOfDaysStr = request.getParameter( "noOfdays" );

            if ( numOfDaysStr == null || numOfDaysStr.isEmpty() ) {
                throw new InvalidInputException( "Number of days cannot be empty" );
            }

            int numOfDays = Integer.parseInt( numOfDaysStr );
            String emailIdForReport = request.getParameter( "reportEmail" );
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
            LOG.error( "NonFatalException while enabling report generation for encompass. Reason : ", e );
            message = e.getMessage();
        }
        LOG.info( "Enabling report generation for encompass details completed successfully" );
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
                LOG.warn( "Method testEncompassConnection is thorwing a NonFatalException : ",e );
                throw e;
            } else {
                LOG.error( "NonFatalException while testing encompass detials. Reason : ", e );
            }
            message = messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ).getMessage();
        }
        LOG.info( "Testing connections completed successfully" );
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


    @RequestMapping ( value = "/updateautopostlinktousersiteforsurvey", method = RequestMethod.POST)
    @ResponseBody
    public String updateAutoPostLinkToUserSiteForSurvey( HttpServletRequest request )
    {
        LOG.info( "Method to update autopost link to user website for a survey started" );
        HttpSession session = request.getSession();
        long companyId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );

        try {
            String autopostLinkToUserSite = request.getParameter( "autopostlinktousersite" );

            boolean isAutoPostLinkToUserSiteEnabled = false;
            if ( autopostLinkToUserSite != null && !autopostLinkToUserSite.isEmpty() ) {
                isAutoPostLinkToUserSiteEnabled = Boolean.parseBoolean( autopostLinkToUserSite );

                OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( companyId );

                if ( companySettings == null )
                    throw new Exception();
                else {
                    SurveySettings surveySettings = companySettings.getSurvey_settings();
                    surveySettings.setAutoPostLinkToUserSiteEnabled( isAutoPostLinkToUserSiteEnabled );
                    if ( organizationManagementService.updateScoreForSurvey(
                        MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySettings, surveySettings ) ) {
                        companySettings.setSurvey_settings( surveySettings );
                        LOG.info( "Updated Survey Settings" );
                    }
                }
            }
        } catch ( Exception error ) {
            LOG.error(
                "Exception occured in updateAutoPostLinkToUserSiteForSurvey() while updating whether to enable autopost link to company site or not. Nested exception is ",
                error );
            return error.getMessage();
        }

        LOG.info( "Method to update autopost link to user site for a survey finished" );
        return "Successfully updated autopost link to user site setting";
    }
    
    /**
     * method to update send digest mail toggle
     * @param request
     * @return
     */
    @RequestMapping ( value = "/updatesenddigestmailtoggle", method = RequestMethod.POST)
    @ResponseBody
    public String updateSendMonthlyDigestMailToggle( HttpServletRequest request )
    {
        LOG.info( "Method updateSendMonthlyDigestMailToggle started" );
        HttpSession session = request.getSession();
        
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        long companyId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );

        try {
            return String.valueOf( reportingDashboardManagement.updateSendDigestMailToggle( entityType,
                companyId, Boolean.parseBoolean( request.getParameter( "sendMonthlyDigestMail" ) ) ) );
        } catch ( Exception error ) {
            LOG.error(
                "Exception occured in updateSendMonthlyDigestMailToggle() while updating send montlhy digest mail flag. Nested exception is ",
                error );
            return "false";
        }
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
            
            else if ( mailCategory != null && mailCategory.equals( "max-reminder-count" ) ) {
                int maxReminderCount = Integer.parseInt( request.getParameter( "max-reminder-count" ) );
                if ( maxReminderCount == 0 ) {
                    LOG.warn( "Reminder Count is 0." );
                    throw new InvalidInputException( "Reminder Count is 0.", DisplayMessageConstants.GENERAL_ERROR );
                }

                originalSurveySettings = companySettings.getSurvey_settings();
                if ( originalSurveySettings != null ) {
                    originalSurveySettings.setMax_number_of_survey_reminders( maxReminderCount );
                }
                LOG.info( "Updating Survey Settings Reminder Count" );
                message = messageUtils.getDisplayMessage( DisplayMessageConstants.SURVEY_REMINDER_COUNT_UPDATE_SUCCESSFUL,
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
            // Updating settings in session
            HttpSession session = request.getSession();
            UserSettings userSettings = (UserSettings) session
                .getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
            if ( userSettings != null )
                userSettings.setCompanySettings( companySettings );

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
                organizationManagementService.processCancelSubscriptionRequest( companySettings, isAccountDisabled,
                    user.getUserId() );
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
            
            // default digest flag is the new account type is enterprise
            if ( newAccountsMasterId == CommonConstants.ACCOUNTS_MASTER_ENTERPRISE) {
                reportingDashboardManagement.updateSendDigestMailToggle( CommonConstants.COMPANY_ID_COLUMN, user.getCompany().getCompanyId(), true );
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
            
            OrganizationUnitSettings companySettings = null;
            
            try {
                companySettings = organizationManagementService.getCompanySettings( user.getCompany().getCompanyId() );
                redirectAttributes.addFlashAttribute( "hiddenSection", companySettings.isHiddenSection() );
            } catch ( InvalidInputException e ) {
                throw new InvalidInputException( "Invalid Input exception occured in method getCompanySettings()",
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }

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

            
            // default digest flag
            if ( currentAccountsMaster.getAccountsMasterId() == CommonConstants.ACCOUNTS_MASTER_ENTERPRISE) {
                reportingDashboardManagement.updateSendDigestMailToggle( CommonConstants.COMPANY_ID_COLUMN, companySettings.getIden(), true );
            }
            
            
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

            //decode text
            text = new String( DatatypeConverter.parseBase64Binary( text ) );

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


    // Method to deactivate company.
    @RequestMapping ( value = "/deactivatecompany", method = RequestMethod.GET)
    public String deactivateCompany( HttpServletRequest request, Model model, RedirectAttributes redirectAttributes )
        throws NonFatalException
    {
        User user = sessionHelper.getCurrentUser();
        String message = "";

        try {
            if ( user != null && user.isCompanyAdmin() ) {
                // Add an entry into Disabled_Accounts table with disable_date as current date
                // and status as inactive.

                organizationManagementService.processDeactivateCompany( user.getCompany(), user.getUserId() );

                LOG.info( "Company deactivated successfully, logging out now." );
                request.getSession( false ).invalidate();
                SecurityContextHolder.clearContext();

                message = messageUtils.getDisplayMessage( DisplayMessageConstants.ACCOUNT_DELETION_SUCCESSFUL,
                    DisplayMessageType.SUCCESS_MESSAGE ).toString();
            }
        } catch ( NonFatalException e ) {
            LOG.error( "InvalidInputException caught in deactivateCompany(). Nested exception is ", e );
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
            
            String activeSession = (String) session.getAttribute("activeSession");
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
            newSession.setAttribute("activeSession",activeSession);
            
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
        String activeSession = (String) session.getAttribute("activeSession");
        
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
            session.setAttribute("activeSession",activeSession);
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
            
            AbusiveMailSettings abusiveMailSettings = new AbusiveMailSettings();
            if( unitSettings.getSurvey_settings() != null
            		&& unitSettings.getSurvey_settings().getAbusive_mail_settings() != null) {
            	abusiveMailSettings = unitSettings.getSurvey_settings().getAbusive_mail_settings();
            }
            session.setAttribute(CommonConstants.ABUSIVE_MAIL_SETTINGS, abusiveMailSettings);
            
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
                if ( !organizationManagementService.validateEmail( mailId.trim() ) )
                    throw new InvalidInputException( "Mail id - " + mailId + " entered as send alert to input is invalid",
                        DisplayMessageConstants.GENERAL_ERROR );
                else
                	mailId = mailId.trim();
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

    @RequestMapping ( value = "/updateabusivesurveysettings", method = RequestMethod.POST)
    @ResponseBody
    public String updateAbusiveSurveyettings( Model model, HttpServletRequest request )
    {
        LOG.info( "Updating Abusive Survey Settings" );
        String mailId = request.getParameter( "mailId" );

        String message = "";
        User user = sessionHelper.getCurrentUser();
       

        try {

            if ( !user.isCompanyAdmin() )
                throw new AuthorizationException( "User is not authorized to access this page" );

            if ( mailId == null || mailId.isEmpty() ) {
                throw new InvalidInputException( "Mail Id(s) of Complaint Handler(s) is null",
                    DisplayMessageConstants.GENERAL_ERROR );
            }


            long entityId = user.getCompany().getCompanyId();
            
            //add service function
            ssApiIntergrationBuilder.getIntegrationApi().updateAbusiveMail( entityId,mailId);


            LOG.info( "Updated Abusive Email Settings" );
            message = messageUtils.getDisplayMessage( DisplayMessageConstants.ABUSIVE_EMAIL_SUCCESSFUL,
                DisplayMessageType.SUCCESS_MESSAGE ).getMessage();
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while updating abusive registration settings. Reason : " + e.getMessage(), e );
            message = messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ).getMessage();
        }	

        return message;
    }
    
    @RequestMapping( value = "/unsetabusivesurveysettings" , method = RequestMethod.POST)
    @ResponseBody
    public String unsetAbusiveSurveySettings( Model model, HttpServletRequest request )
    {
        LOG.info( "Unset Abusive Survey Settings" );
        String message = "";
        User user = sessionHelper.getCurrentUser();

        if ( !user.isCompanyAdmin() )
		    throw new AuthorizationException( "User is not authorized to access this page" );

		long entityId = user.getCompany().getCompanyId();
		
		//add service function
		ssApiIntergrationBuilder.getIntegrationApi().unsetAbusiveMail(entityId);


		LOG.info( "Unset Abusive Email Settings" );
		message = messageUtils.getDisplayMessage( DisplayMessageConstants.ABUSIVE_EMAIL_SUCCESSFUL,
		    DisplayMessageType.SUCCESS_MESSAGE ).getMessage();	

        return message;
    
    }
    
    @RequestMapping( value = "/unsetcomplaintresolution" , method = RequestMethod.POST)
    @ResponseBody
    public String unsetComplaintResSettings( Model model, HttpServletRequest request )
    {
        LOG.info( "Unset Complaint Resolution Settings" );
        String message = "";
        User user = sessionHelper.getCurrentUser();

        if ( !user.isCompanyAdmin() )
		    throw new AuthorizationException( "User is not authorized to access this page" );

		long entityId = user.getCompany().getCompanyId();
		
		//add service function
		ssApiIntergrationBuilder.getIntegrationApi().unsetCompRes(entityId);


		LOG.info( "Unset Complaint Resolution Settings" );
		message = messageUtils.getDisplayMessage( DisplayMessageConstants.COMPLAINT_REGISTRATION_SUCCESSFUL,
		    DisplayMessageType.SUCCESS_MESSAGE ).getMessage();	

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

        //call the streamApi recieveSendGridEvents for click tracking
        String uuid = request.getParameter("u");
        if( uuid != null && !uuid.isEmpty() ) {
            LOG.info( "Found UUID : " + uuid );
            urlService.sendClickEvent(uuid);
        }


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
    @RequestMapping ( value = "/uploadXlsx", method = RequestMethod.POST)
    public String saveHierarchyFile( Model model, @RequestParam ( "file") MultipartFile fileLocal, HttpServletRequest request )
    {
        boolean status = true;
        Object response = null;
        try {
            if ( fileLocal == null ) {
                throw new InvalidInputException( "file is empty" );
            }
            LOG.info( "Saving the hierarchy file" );
            String fileLocalName = request.getParameter( "filename" );
            String uploadType = request.getParameter( "uploadType" );


            if ( StringUtils.isEmpty( uploadType ) ) {
                throw new InvalidInputException( "filename is empty" );
            } else if ( StringUtils.isEmpty( uploadType ) || !Arrays.asList( "append", "replace" ).contains( uploadType ) ) {
                throw new InvalidInputException( "valid upload type is required" );
            }

            File convFile = new File( fileLocalName );

            fileLocal.transferTo( convFile );

            // Set the new filename
            User user = sessionHelper.getCurrentUser();
            String uploadedFileName = "COMPANY_HIERARCHY_UPLOAD_" + user.getCompany().getCompany() + "_"
                + new DateTime( System.currentTimeMillis() ).toString() + ".xlsx";
            fileUploadService.uploadFileAtDefautBucket( convFile, uploadedFileName );
            uploadedFileName = endpoint + CommonConstants.FILE_SEPARATOR + URLEncoder.encode( uploadedFileName, "UTF-8" );


            // save all the above details in mongoDB
            ParsedHierarchyUpload savedUploadDetails = hierarchyUploadService.insertUploadHierarchyXlsxDetails( user,
                fileLocalName, uploadedFileName, new Date(), uploadType.equals( "append" ) ? true : false );
            response = savedUploadDetails;

        } catch ( Exception error ) {
            status = false;
            response = error.getMessage();
        }
        Map<String, Object> responseMap = new HashMap<String, Object>();
        responseMap.put( "status", status );
        responseMap.put( "response", response );
        return new Gson().toJson( responseMap );
    }

    @ResponseBody
    @RequestMapping ( value = "/savesurveycsvfile", method = RequestMethod.POST)
    public String saveSurveyCsvFile( Model model, @RequestParam ( "file") MultipartFile file,
        @RequestParam ( "filename") String fileName, @RequestParam ( "uploaderEmail") String uploaderEmail,
        HttpServletRequest request )
    {
        LOG.debug( "Saving the csv survey info file" );

        boolean status = false;
        Object message = null;
        try {
            String hierarchyType = request.getParameter( "hierarchyType" );
            long hierarchyId = 0l;

            if ( file == null || file.isEmpty() )
                throw new InvalidInputException( "Please provide a valid CSV file." );

            if ( StringUtils.isEmpty( fileName ) )
                throw new InvalidInputException( "Please provide a valid CSV file name." );

            if ( StringUtils.isEmpty( uploaderEmail ) && organizationManagementService.validateEmail( uploaderEmail.trim() ) )
                throw new InvalidInputException( "Please provide a valid uploader email for csv upload." );

            if ( StringUtils.isEmpty( hierarchyType ) )
                throw new InvalidInputException( "Please provide a valid hiearchyType." );

            if ( !StringUtils.isEmpty( request.getParameter( "hierarchyValue" ) ) ) {
                try {
                    hierarchyId = Long.parseLong( request.getParameter( "hierarchyValue" ) );
                } catch ( NumberFormatException unableToFormatId ) {
                    throw new InvalidInputException( "please provide a valid hierarchy Identifier." );
                }
            } else {
                throw new InvalidInputException( "Please provide a hiearchy Identifier." );
            }

            if ( !surveyHandler.isFileAlreadyUploaded( fileName, uploaderEmail ) ) {
                status = surveyHandler.createEntryForSurveyUploadWithCsv( hierarchyType, file, fileName, hierarchyId,
                    sessionHelper.getCurrentUser(), uploaderEmail );
                message = "CSV file uploaded successfully.";
            } else {
                message = "CSV file: " + fileName + " is already uploaded using the provided email.";
            }


        } catch ( InvalidInputException expectedError ) {
            status = false;
            message = expectedError.getMessage();
        } catch ( Exception unhandledError ) {
            status = false;
            message = "Sorry, Unable to upload csv, Please try again.";
        }

        Map<String, Object> responseMap = new HashMap<String, Object>();
        responseMap.put( "status", status );
        responseMap.put( "message", message );
        return new Gson().toJson( responseMap );
    }



    @ResponseBody
    @RequestMapping ( value = "/starthierarchyxlsxfileupload", method = RequestMethod.POST)
    public String startHierarchyUpload( Model model, HttpServletRequest request ) throws InvalidInputException
    {
        boolean status = true;
        Object response = null;

        LOG.info( "method validateHierarchyFile started" );
        try {

            User user = sessionHelper.getCurrentUser();
            String uploadType = request.getParameter( "uploadType" );
            String ignoreWarningStr = request.getParameter( "isWarningToBeIgnored" );
            String verifyOnly = request.getParameter( "verifyOnly" );

            if( user.getIsOwner() != CommonConstants.STATUS_ACTIVE ){
                throw new InvalidInputException( "Sorry, only a company admin or social survey admin can initiate hierarchy upload." );
            } else if ( StringUtils.isEmpty( uploadType ) || !Arrays.asList( "append", "replace" ).contains( uploadType ) ) {
                throw new InvalidInputException( "Please provide a valid upload mode for processing." );
            } else if ( StringUtils.isEmpty( ignoreWarningStr )
                || !Arrays.asList( "true", "false" ).contains( ignoreWarningStr.trim() ) ) {
                throw new InvalidInputException( "Please specify if warnings are to be ignored." );
            } else if( StringUtils.isEmpty( verifyOnly )
                || !Arrays.asList( "true", "false" ).contains( verifyOnly.trim() ) ){
                throw new InvalidInputException( "Please specify whether to abort after verification." );
            }

            ParsedHierarchyUpload uploadStatus = hierarchyUploadService
                .getParsedHierarchyUpload( user.getCompany().getCompanyId() );

            if ( uploadStatus == null || uploadStatus.getStatus() != CommonConstants.HIERARCHY_UPLOAD_STATUS_NEW_ENTRY ) {
                throw new NoRecordsFetchedException( "No file Found for hierarchy upload." );
            } else {
                uploadStatus.setStatus( CommonConstants.HIERARCHY_UPLOAD_STATUS_INITIATED );
                uploadStatus.setInAppendMode( "append".equals( uploadType ) ? true : false );
                uploadStatus.setWarningToBeIgnored( Boolean.parseBoolean( ignoreWarningStr.trim() ) );
                uploadStatus.setVerifyOnly( Boolean.parseBoolean( verifyOnly.trim() ) );
                hierarchyUploadService.reinsertParsedHierarchyUpload( uploadStatus );
                response = uploadStatus;
            }

        } catch ( Exception error ) {
            status = false;
            response = error.getMessage();
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
        LOG.info( "Fetching the latest batch processing details" );
        User user = sessionHelper.getCurrentUser();
        boolean status = true;
        Object response = null;

        try {
            ParsedHierarchyUpload uploadStatus = hierarchyUploadService
                .getParsedHierarchyUpload( user.getCompany().getCompanyId() );

            if ( uploadStatus.getStatus() == CommonConstants.HIERARCHY_UPLOAD_STATUS_IMPORTED
                || uploadStatus.getStatus() == CommonConstants.HIERARCHY_UPLOAD_STATUS_IMPORTED_WITH_ERRORS ) {
                // Refresh session
                sessionHelper.refreshSession( request.getSession( false ), user );
            }

            response = uploadStatus;

        } catch ( NoRecordsFetchedException noUploadFound ) {
            status = false;
            response = "Either the batch is busy or there are no uploads in the Queue. Please reload page to confirm.";
        } catch ( InvalidInputException invalidInput ) {
            status = false;
            response = "Invalid data provided to find upload process details.";
        } catch ( Exception error ) {
            status = false;
            response = error.getMessage();
        }

        Map<String, Object> responseMap = new HashMap<String, Object>();
        responseMap.put( "status", status );
        responseMap.put( "response", response );
        return new Gson().toJson( responseMap );
    }


    @ResponseBody
    @RequestMapping ( value = "/getunmatchedpreinitiatedsurveys", method = RequestMethod.GET)
    public String getUnmatchedPreinitiatedSurveys( HttpServletRequest request, Model model )
    {
        LOG.info( "Method to get getUnmatchedPreinitiatedSurveys started" );
        String startIndexStr = request.getParameter( "startIndex" );
        String batchSizeStr = request.getParameter( "batchSize" );
        String countStr = request.getParameter( "count" );

        if ( startIndexStr == null || batchSizeStr == null || countStr == null ) {
            LOG.error( "Null value found for startIndex , batch size or countStr." );
            return "Null value found for startIndex or batch size or countStr.";
        }

        SurveyPreInitiationList surveyPreInitiationList = new SurveyPreInitiationList();
        int startIndex;
        int batchSize;
        long count;
        try {

            User user = sessionHelper.getCurrentUser();
            if ( user == null || user.getCompany() == null ) {
                throw new NonFatalException( "Insufficient permission for this process" );
            }

            try {
                startIndex = Integer.parseInt( startIndexStr );
                batchSize = Integer.parseInt( batchSizeStr );
                count = Long.parseLong( countStr );
            } catch ( NumberFormatException e ) {
                LOG.error(
                    "NumberFormatException caught while trying to convert startIndex or batchSize or companyId  Nested exception is ",
                    e );
                throw e;
            }


            surveyPreInitiationList = socialManagementService.getUnmatchedPreInitiatedSurveys( user.getCompany().getCompanyId(),
                startIndex, batchSize, count );
        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException while fetching posts. Reason :" + nonFatalException.getMessage(), nonFatalException );
            return messageUtils.getDisplayMessage( DisplayMessageConstants.FETCH_UNMATCHED_PREINITIATED_SURVEYS_UNSUCCESSFUL,
                DisplayMessageType.ERROR_MESSAGE ).getMessage();
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
        String countStr = request.getParameter( "count" );
        SurveyPreInitiationList surveyPreInitiationList = new SurveyPreInitiationList();
        int startIndex;
        int batchSize;
        long count;

        if ( startIndexStr == null || batchSizeStr == null || countStr == null ) {
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
                count = Long.parseLong( countStr );
            } catch ( NumberFormatException e ) {
                LOG.error(
                    "NumberFormatException caught while trying to convert startIndex or batchSize or companyId  Nested exception is ",
                    e );
                throw e;
            }

            surveyPreInitiationList = socialManagementService.getProcessedPreInitiatedSurveys( user.getCompany().getCompanyId(),
                startIndex, batchSize, count );
        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException while fetching posts. Reason :" + nonFatalException.getMessage(), nonFatalException );
            return messageUtils.getDisplayMessage( DisplayMessageConstants.FETCH_PROCESSED_PREINITIATED_SURVEYS_UNSUCCESSFUL,
                DisplayMessageType.ERROR_MESSAGE ).getMessage();
        }
        LOG.info( "Method to get posts for the user, getProcessedPreInitiatedSurveys() finished" );
        return new Gson().toJson( surveyPreInitiationList );
    }


    @ResponseBody
    @RequestMapping ( value = "/getcorruptpreinitiatedsurveys", method = RequestMethod.GET)
    public String getCorruptPreInitiatedSurveys( HttpServletRequest request, Model model )
    {
        LOG.info( "Method to getCorruptPreInitiatedSurveys started" );
        String startIndexStr = request.getParameter( "startIndex" );
        String batchSizeStr = request.getParameter( "batchSize" );
        String countStr = request.getParameter( "count" );
        if ( startIndexStr == null || batchSizeStr == null || countStr == null ) {
            LOG.error( "Null value found for startIndex or batch size." );
            return "Null value found for startIndex or batch size.";
        }

        SurveyPreInitiationList surveyPreInitiationList = new SurveyPreInitiationList();
        int startIndex;
        int batchSize;
        long count;
        try {
            User user = sessionHelper.getCurrentUser();
            if ( user == null || user.getCompany() == null ) {
                throw new NonFatalException( "Insufficient permission for this process" );
            }
            try {
                startIndex = Integer.parseInt( startIndexStr );
                batchSize = Integer.parseInt( batchSizeStr );
                count = Long.parseLong( countStr );
            } catch ( NumberFormatException e ) {
                LOG.error(
                    "NumberFormatException caught while trying to convert startIndex or batchSize or companyId  Nested exception is ",
                    e );
                throw e;
            }
            surveyPreInitiationList = socialManagementService.getCorruptPreInitiatedSurveys( user.getCompany().getCompanyId(),
                startIndex, batchSize, count );
        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException while fetching posts. Reason :" + nonFatalException.getMessage(), nonFatalException );
            return messageUtils.getDisplayMessage( DisplayMessageConstants.FETCH_CORRUPT_PREINITIATED_SURVEYS_UNSUCCESSFUL,
                DisplayMessageType.ERROR_MESSAGE ).getMessage();
        }
        LOG.info( "Method to getCorruptPreInitiatedSurveys() finished" );
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
        LOG.info( "Method to saveUserEmailMapping for transientEmail: {} , agentId: {} , ignoreEmailFlag: {}",emailAddress,agentIdStr,ignoredEmailStr );
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

    			//check if there is an agent already in system( User with agent profile) 
            try {
                User existingUser = userManagementService.getActiveAgentByEmailAndCompany( sessionHelper.getCurrentUser().getCompany().getCompanyId() , emailAddress );
                if ( existingUser != null ) 
                    throw new UserAlreadyExistsException("The email addresss " + emailAddress + " is already present in our database." );            
            } catch ( NoRecordsFetchedException e ) {
                if ( ignoredEmail ) {
                    userManagementService.saveIgnoredEmailCompanyMappingAndUpdateSurveyPreinitiation( emailAddress,
                        loggedInUser.getCompany().getCompanyId() );
                } else {
                    userManagementService.saveEmailUserMappingAndUpdateAgentIdInSurveyPreinitiation( emailAddress, agentId );
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


    @ResponseBody
    @RequestMapping ( value = "/getuserwithaliasedemails", method = RequestMethod.GET)
    public String getUserWithAliasedEmails( HttpServletRequest request, Model model )
    {
        LOG.info( "Method to get getUserWithAliasedEmails started" );
        String startIndexStr = request.getParameter( "startIndex" );
        String batchSizeStr = request.getParameter( "batchSize" );
        String countStr = request.getParameter( "count" );

        if ( startIndexStr == null || batchSizeStr == null || countStr == null ) {
            LOG.error( "Null value found for startIndex or batch size." );
            return "Null value found for startIndex or batch size.";
        }

        UserList userList = new UserList();
        int startIndex;
        int batchSize;
        long count;
        try {

            User user = sessionHelper.getCurrentUser();
            if ( user == null || user.getCompany() == null ) {
                throw new NonFatalException( "Insufficient permission for this process" );
            }

            try {
                startIndex = Integer.parseInt( startIndexStr );
                batchSize = Integer.parseInt( batchSizeStr );
                count = Long.parseLong( countStr );
            } catch ( NumberFormatException e ) {
                LOG.error( "NumberFormatException caught while trying to convert startIndex or batchSize  Nested exception is ",
                    e );
                throw e;
            }


            userList = userManagementService.getUsersAndEmailMappingForCompany( user.getCompany().getCompanyId(), startIndex,
                batchSize, count );
        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException while fetching UserWithAliasedEmails. Reason :" + nonFatalException.getMessage(),
                nonFatalException );
            return messageUtils.getDisplayMessage( DisplayMessageConstants.FETCH_USER_FOR_EMAIL_MAPPING_UNSUCCESSFUL,
                DisplayMessageType.ERROR_MESSAGE ).getMessage();
        }
        LOG.info( "Method to get posts for the user, getUserWithAliasedEmails() finished" );
        return new Gson().toJson( userList );
    }


    @ResponseBody
    @RequestMapping ( value = "/getemailmappingsforuser", method = RequestMethod.GET)
    public String getEmailMappingsForUser( HttpServletRequest request, Model model )
    {
        LOG.info( "Method to get getEmailMappingsForUser started" );

        String agentIdStr = request.getParameter( "agentId" );
        long agentId;
        List<UserEmailMapping> userEmailMappings = new ArrayList<UserEmailMapping>();

        try {

            try {
                agentId = Integer.parseInt( agentIdStr );
            } catch ( NumberFormatException e ) {
                LOG.error( "NumberFormatException caught while trying to convert agentId Nested exception is ", e );
                throw e;
            }

            userEmailMappings = userManagementService.getUserEmailMappingsForUser( agentId );


        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException while getting EmailMappingsForUser. Reason :" + nonFatalException.getMessage(),
                nonFatalException );
            return messageUtils.getDisplayMessage( DisplayMessageConstants.FETCH_EMAIL_MAPPINGS_FOR_USER_UNSUCCESSFUL,
                DisplayMessageType.ERROR_MESSAGE ).getMessage();
        }
        LOG.info( "Method getEmailMappingsForUser() finished" );
        return new Gson().toJson( userEmailMappings );
    }


    @ResponseBody
    @RequestMapping ( value = "/updateuseremailmapping", method = RequestMethod.GET)
    public String updateUserEmailMapping( HttpServletRequest request, Model model )
    {
        LOG.info( "Method to update user email mapping started" );
        Map<String, Object> responseMap = new HashMap<String, Object>();
        boolean succeed;
        String message;
        String emailMappingIdStr = request.getParameter( "emailMappingId" );
        String statusStr = request.getParameter( "status" );
        long emailMappingId;
        int status;

        try {

            try {
                emailMappingId = Integer.parseInt( emailMappingIdStr );
                status = Integer.parseInt( statusStr );
            } catch ( NumberFormatException e ) {
                LOG.error( "NumberFormatException caught while trying to convert emailMappingId Nested exception is ", e );
                throw e;
            }


            User user = sessionHelper.getCurrentUser();
            if ( user == null || user.getCompany() == null ) {
                throw new NonFatalException( "Insufficient permission for this process" );
            }

            userManagementService.updateUserEmailMapping( user, emailMappingId, status );

            message = messageUtils.getDisplayMessage( DisplayMessageConstants.UPDATE_EMAIL_MAPPING_FOR_USER__SUCCESSFUL,
                DisplayMessageType.SUCCESS_MESSAGE ).getMessage();
            succeed = true;

        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException while fetching posts. Reason :" + nonFatalException.getMessage(), nonFatalException );
            succeed = false;
            message = messageUtils.getDisplayMessage( DisplayMessageConstants.FETCH_UNMATCHED_PREINITIATED_SURVEYS_UNSUCCESSFUL,
                DisplayMessageType.ERROR_MESSAGE ).getMessage();
        }
        LOG.info( "Method to get posts for the user, getUnmatchedPreinitiatedSurveys() finished" );

        responseMap.put( "succeed", succeed );
        responseMap.put( "message", message );
        return new Gson().toJson( responseMap );
    }


    @ResponseBody
    @RequestMapping ( value = "/saveemailmappingsforuser", method = RequestMethod.POST)
    public String saveUserEmailMappingsForUser( HttpServletRequest request, Model model )
    {
        LOG.info( "Method to get saveUserEmailMappingsForUser started" );
        String emailIds = request.getParameter( "emailIds" );
        String agentIdStr = request.getParameter( "agentId" );

        try {
            long agentId;

            try {
                agentId = Integer.parseInt( agentIdStr );
            } catch ( NumberFormatException e ) {
                LOG.error( "NumberFormatException caught while trying to convert agentId Nested exception is ", e );
                throw e;
            }
            if ( emailIds == null || emailIds.isEmpty() ) {
                throw new InvalidInputException( "Email Id can't be null or empty" );
            }
            //parse email ids
            List<String> emailIdList = new ArrayList<String>();
            if ( emailIds != null && !emailIds.isEmpty() ) {

                if ( !emailIds.contains( "," ) ) {
                    if ( !organizationManagementService.validateEmail( emailIds.trim() ) )
                        throw new InvalidInputException( "Mail id - " + emailIds + " entered as send alert to input is invalid",
                            DisplayMessageConstants.GENERAL_ERROR );
                    else
                        emailIdList.add( emailIds.trim() );
                } else {
                    String mailIds[] = emailIds.split( "," );

                    for ( String mailID : mailIds ) {
                        if ( !organizationManagementService.validateEmail( mailID.trim() ) )
                            throw new InvalidInputException(
                                "Mail id - " + mailID + " entered amongst the mail ids as send alert to input is invalid",
                                DisplayMessageConstants.GENERAL_ERROR );
                        else
                            emailIdList.add( mailID.trim() );
                    }
                }

            }


            for ( String emailId : emailIdList ) {
                try {
            			//check if there is an agent already in system( User with agent profile)
                    User existingUser = userManagementService.getActiveAgentByEmailAndCompany(sessionHelper.getCurrentUser().getCompany().getCompanyId() , emailId );
                    if ( existingUser != null ) {
                        		throw new UserAlreadyExistsException("The email addresss " + emailId + " is already present in our database." );
                    }
                } catch ( NoRecordsFetchedException e ) {
                    userManagementService.saveEmailUserMappingAndUpdateAgentIdInSurveyPreinitiation( emailId, agentId );

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


    @SuppressWarnings ( "static-access")
    @RequestMapping ( value = "/downloadusersurveyreport")
    public void getUserSurveyReport( Model model, HttpServletRequest request, HttpServletResponse response )
    {
        LOG.info( "Method getUserSurveyReport() started." );
        try {
            String companyIdStr = request.getParameter( "companyId" );
            String tabIdStr = request.getParameter( "tabId" );
            long companyId;
            int tabId;
            if ( companyIdStr == null || companyIdStr.isEmpty() ) {
                throw new InvalidInputException( "Passed parameter companyId is invalid" );
            }
            if ( tabIdStr == null || tabIdStr.isEmpty() ) {
                throw new InvalidInputException( "Passed parameter tabIdStr is invalid" );
            }

            try {
                companyId = Long.parseLong( companyIdStr );
                tabId = Integer.parseInt( tabIdStr );
            } catch ( NumberFormatException e ) {
                LOG.error(
                    "NumberFormatException caught while parsing companyId/tabId in getUserSurveyReport(). Nested exception is ",
                    e );
                throw e;
            }

            try {
                User user = sessionHelper.getCurrentUser();
                String fileName = getSurveyReportFileNameByTabId().get( tabId ) + "_" + user.getCompany().getCompany() + "_"
                    + new DateTime( System.currentTimeMillis() ).toString() + workbookData.EXCEL_FILE_EXTENSION;
                XSSFWorkbook workbook = socialManagementService.getUserSurveyReportByTabId( tabId, companyId );
                response.setContentType( workbookData.EXCEL_FORMAT );
                String headerKey = workbookData.CONTENT_DISPOSITION_HEADER;
                String headerValue = String.format( "attachment; filename=\"%s\"", new File( fileName ).getName() );
                response.setHeader( headerKey, headerValue );
                OutputStream responseStream = null;

                try {
                    responseStream = response.getOutputStream();
                    workbook.write( responseStream );
                } catch ( IOException e ) {
                    LOG.error( "IOException caught in getUserSurveyReport(). Nested exception is ", e );
                } finally {
                    try {
                        responseStream.close();
                    } catch ( IOException e ) {
                        LOG.error( "IOException caught in getUserSurveyReport(). Nested exception is ", e );
                    }
                }

                response.flushBuffer();
            } catch ( InvalidInputException e ) {
                LOG.error( "InvalidInputException caught in getUserSurveyReport(). Nested exception is ", e );
                throw e;
            } catch ( IOException e ) {
                LOG.error( "IOException caught in getUserSurveyReport(). Nested exception is ", e );
            }
        } catch ( NonFatalException e ) {
            LOG.error( "Error while getting survey Report", e );
        }
        LOG.info( "Method getUserSurveyReport() ended." );
    }


    private Map<Integer, String> getSurveyReportFileNameByTabId()
    {
        Map<Integer, String> fineNameMap = new HashMap<Integer, String>();
        fineNameMap.put( CommonConstants.UNMATCHED_USER_TABID, "Unmatched-Survey-Report" );
        fineNameMap.put( CommonConstants.PROCESSED_USER_TABID, "Processed-Survey-Report" );
        fineNameMap.put( CommonConstants.MAPPED_USER_TABID, "Mapped-Survey-Report" );
        fineNameMap.put( CommonConstants.CORRUPT_USER_TABID, "Corrupt-Survey-Report" );
        return fineNameMap;
    }


    @RequestMapping ( value = "/updatesortcriteria", method = RequestMethod.POST)
    @ResponseBody
    public String updateSortCriteria( HttpServletRequest request )
    {
        LOG.info( "Method UpdateSortCriteria of OrganizationManagementController called" );
        DisplayMessage message = null;
        try {
            HttpSession session = request.getSession( false );
            OrganizationUnitSettings companySettings = null;
            String sortCriteria = (String) request.getParameter( "sortCriteria" );
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            if ( entityType.equals( "companyId" ) ) {
                try {
                    companySettings = organizationManagementService.getCompanySettings( entityId );
                    User user = sessionHelper.getCurrentUser();
                    if ( companySettings == null ) {
                        message = messageUtils.getDisplayMessage( DisplayMessageConstants.INVALID_COMPANY_ID,
                            DisplayMessageType.ERROR_MESSAGE );
                    } else if ( user != null && ( user.isCompanyAdmin() || user.isSuperAdmin() ) ) {
                        organizationManagementService.updateSortCriteriaForCompany( companySettings, sortCriteria );
                        message = messageUtils.getDisplayMessage( DisplayMessageConstants.SORT_CRITERIA_SUCCESSFULLY_UPDATED,
                            DisplayMessageType.SUCCESS_MESSAGE );
                    } else {
                        message = messageUtils.getDisplayMessage( DisplayMessageConstants.INSUFFICIENT_USER_PERMISSION,
                            DisplayMessageType.ERROR_MESSAGE );
                    }
                } catch ( InvalidInputException error ) {
                    LOG.error( "unable to update sort criteria, company doesnt exist" );
                    message = messageUtils.getDisplayMessage( error.getErrorCode(), DisplayMessageType.ERROR_MESSAGE );
                }

            }
        } catch ( Exception globalError ) {
            LOG.error( "unable to update sort criteria" );
            message = messageUtils.getDisplayMessage( ( (NonFatalException) globalError ).getErrorCode(),
                DisplayMessageType.ERROR_MESSAGE );
        }
        LOG.info( "Method UpdateSortCriteria of OrganizationManagementController finished" );
        return new Gson().toJson( message );
    }


    //JIRA SS-975
    @RequestMapping ( value = "/updatesendemailthrough", method = RequestMethod.POST)
    @ResponseBody
    public String updateSendEmailThrough( HttpServletRequest request )
    {
        LOG.info( "Method UpdateSendEmailThrough of OrganizationManagementController called" );
        DisplayMessage message = null;
        try {
            HttpSession session = request.getSession( false );
            OrganizationUnitSettings companySettings = null;
            String sendEmailThrough = (String) request.getParameter( "sendEmailThrough" );
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            Long adminUserid = (Long) session.getAttribute( CommonConstants.REALTECH_USER_ID );
            if ( entityType.equals( "companyId" ) ) {
                try {
                    companySettings = organizationManagementService.getCompanySettings( entityId );
                    User user = sessionHelper.getCurrentUser();
                    if ( companySettings == null ) {
                        message = messageUtils.getDisplayMessage( DisplayMessageConstants.INVALID_COMPANY_ID,
                            DisplayMessageType.ERROR_MESSAGE );
                    } else if ( user != null && adminUserid != null ) {
                        if ( sendEmailThrough.equals( CommonConstants.SEND_EMAIL_THROUGH_SOCIALSURVEY_ME )
                            || sendEmailThrough.equals( CommonConstants.SEND_EMAIL_THROUGH_SOCIALSURVEY_US ) ) {
                            organizationManagementService.updateSendEmailThroughForCompany( companySettings, sendEmailThrough );
                            message = messageUtils.getDisplayMessage(
                                DisplayMessageConstants.SEND_EMAIL_THROUGH_SUCCESSFULLY_UPDATED,
                                DisplayMessageType.SUCCESS_MESSAGE );
                        } else {
                            message = messageUtils.getDisplayMessage(
                                DisplayMessageConstants.SEND_EMAIL_THROUGH_UNSUCCESSFULLY_UPDATED,
                                DisplayMessageType.ERROR_MESSAGE );
                        }

                    } else {
                        message = messageUtils.getDisplayMessage( DisplayMessageConstants.INSUFFICIENT_SENDGRID_USER_PERMISSION,
                            DisplayMessageType.ERROR_MESSAGE );
                    }
                } catch ( InvalidInputException error ) {
                    LOG.error( "unable to update email criteria, company doesnt exist" );
                    message = messageUtils.getDisplayMessage( error.getErrorCode(), DisplayMessageType.ERROR_MESSAGE );
                }

            }
        } catch ( Exception globalError ) {
            LOG.error( "unable to update email criteria" );
            message = messageUtils.getDisplayMessage( ( (NonFatalException) globalError ).getErrorCode(),
                DisplayMessageType.ERROR_MESSAGE );
        }
        LOG.info( "Method updateSendEmailThrough of OrganizationManagementController finished" );
        return new Gson().toJson( message );
    }


    @RequestMapping ( value = "/updateallowpartnersurveyforcompany", method = RequestMethod.POST)
    @ResponseBody
    public String updateAllowPartnerSurveyForCompany( HttpServletRequest request )
    {
        LOG.info( "Method to update AllowPartnerSurvey started" );

        try {

            HttpSession session = request.getSession();
            long companyId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );

            String allowPartnerSurveyString = request.getParameter( "allowPartnerSurvey" );

            boolean allowPartnerSurvey = Boolean.parseBoolean( allowPartnerSurveyString );

            OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( companyId );
            User companyAdmin = userManagementService.getCompanyAdmin( companyId );
            Set<Long> userIds = userManagementService.getUserIdsUnderAdmin( companyAdmin );
            organizationManagementService.updateAllowPartnerSurveyForAllUsers( userIds, allowPartnerSurvey );

            if ( companySettings == null )
                throw new Exception();

            if ( companySettings.getCrm_info() == null ) {
                return "No crm connected to company";
            }

            try {
                EncompassCrmInfo encompassCrmInfo = (EncompassCrmInfo) companySettings.getCrm_info();
                encompassCrmInfo.setAllowPartnerSurvey( allowPartnerSurvey );

                organizationManagementService.updateCRMDetails( companySettings, encompassCrmInfo,
                    "com.realtech.socialsurvey.core.entities.EncompassCrmInfo" );
            } catch ( ClassCastException e ) {
                return "Encompass is not connected for company";
            }


        } catch ( Exception error ) {
            LOG.error( "Exception occured in updateallowpartnersurvey(). Nested exception is ", error );
            return error.getMessage();
        }

        LOG.info( "Method to update allow partner survey finished" );
        return "success";
    }


    @RequestMapping ( value = "/updateallowpartnersurveyforuser", method = RequestMethod.POST)
    @ResponseBody
    public String updateAllowPartnerSurveyForUser( HttpServletRequest request )
    {
        LOG.info( "Method to update updateAllowPartnerSurveyForUser started" );

        try {
            String allowPartnerSurveyString = request.getParameter( "allowPartnerSurvey" );
            String userIdString = request.getParameter( "userId" );

            boolean allowPartnerSurvey = Boolean.parseBoolean( allowPartnerSurveyString );
            long userId = Long.parseLong( userIdString );

            AgentSettings agentSettings = userManagementService.getUserSettings( userId );

            if ( agentSettings == null )
                throw new InvalidInputException( "No user found with user id : " + userId );

            agentSettings.setAllowPartnerSurvey( true );
            organizationManagementService.updatellowPartnerSurveyForUser( agentSettings, allowPartnerSurvey );


        } catch ( Exception error ) {
            LOG.error( "Exception occured in updateallowpartnersurvey(). Nested exception is ", error );
            return error.getMessage();
        }

        LOG.info( "Method to update allow partner survey finished" );
        return "success";
    }


    /**
     * 
     * @param surveySettings
     */
    private void encodeSurveySettings( SurveySettings surveySettings )
    {
        if ( surveySettings.getHappyText() != null )
            surveySettings.setHappyText( DatatypeConverter.printBase64Binary( surveySettings.getHappyText().getBytes() ) );
        if ( surveySettings.getSadText() != null )
            surveySettings.setSadText( DatatypeConverter.printBase64Binary( surveySettings.getSadText().getBytes() ) );
        if ( surveySettings.getNeutralText() != null )
            surveySettings.setNeutralText( DatatypeConverter.printBase64Binary( surveySettings.getNeutralText().getBytes() ) );

        if ( surveySettings.getHappyTextComplete() != null )
            surveySettings.setHappyTextComplete(
                DatatypeConverter.printBase64Binary( surveySettings.getHappyTextComplete().getBytes() ) );
        if ( surveySettings.getSadTextComplete() != null )
            surveySettings
                .setSadTextComplete( DatatypeConverter.printBase64Binary( surveySettings.getSadTextComplete().getBytes() ) );
        if ( surveySettings.getNeutralTextComplete() != null )
            surveySettings.setNeutralTextComplete(
                DatatypeConverter.printBase64Binary( surveySettings.getNeutralTextComplete().getBytes() ) );

    }
    
    
    @RequestMapping ( value = "/updatemanualsurveyremindercount", method = RequestMethod.POST)
    @ResponseBody
    public String updateSurveyReminderCount( HttpServletRequest request )
    {
        LOG.info( "Method to updateSurveyReminderCount started" );
        HttpSession session = request.getSession();
        long companyId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        int maxSurveyReminderCount;
        try {
            String maxSurveyReminderCountStr = request.getParameter( "surveyReminderCount" );
            maxSurveyReminderCount = Integer.parseInt( maxSurveyReminderCountStr );
            OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( companyId );
            if(companySettings == null)
                throw new InvalidInputException("No settings fould for company with id " + companyId);

            SurveySettings surveySettings = companySettings.getSurvey_settings();
            surveySettings.setMax_number_of_survey_reminders( maxSurveyReminderCount );
            if ( organizationManagementService.updateScoreForSurvey(
                MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySettings, surveySettings ) ) {
                companySettings.setSurvey_settings( surveySettings );
                LOG.info( "Updated Survey Settings" );
            }

        } catch ( Exception error ) {
            LOG.error(
                "Exception occured in updateAutoPostLinkToUserSiteForSurvey() while updating whether to enable autopost link to company site or not. Nested exception is ",
                error );
            return error.getMessage();
        }

        LOG.info( "Method to updateSurveyReminderCount finished" );
        return "Successfully updated the max survey reminder count";
    }
    
    @RequestMapping ( value = "/updatesurveymailthreshold", method = RequestMethod.POST)
    @ResponseBody
    public String updateSurveyMailThreshold( HttpServletRequest request )
    {
        LOG.info( "Method to updateSurveyMailThreshold started" );
        HttpSession session = request.getSession();
        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        double surveyCompletedMailThreshold;
        OrganizationUnitSettings unitSettings = null;
        String collectionName = null;

        try {

            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                collectionName = MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION;
                unitSettings = organizationManagementService.getCompanySettings( entityId );
            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                collectionName = MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION;
                unitSettings = organizationManagementService.getRegionSettings( entityId );
            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                collectionName = MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION;
                unitSettings = organizationManagementService.getBranchSettingsDefault( entityId );
            } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
                collectionName = MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION;
                unitSettings = userManagementService.getUserSettings( entityId );
            }

            if ( unitSettings != null ) {

                String surveyCompletedMailThresholdStr = request.getParameter( "surveyCompletedMailThreshold" );
                surveyCompletedMailThreshold = Double.parseDouble( surveyCompletedMailThresholdStr );
                SurveySettings surveySettings = unitSettings.getSurvey_settings();
                
                if ( surveySettings == null ) {
                    surveySettings = new SurveySettings();
                }
                
                surveySettings.setSurveyCompletedMailThreshold( surveyCompletedMailThreshold );
                organizationManagementService.updateScoreForSurvey( collectionName, unitSettings, surveySettings );

            } else {
                LOG.error( "settings not found for the current session." );
                throw new InvalidInputException( "settings not found for the current session." );
            }
            
            LOG.info( "Method to updateSurveyMailThreshold finished" );
            return "Successfully updated the Survey Mail Threshold.";

        } catch ( Exception error ) {
            LOG.error(
                "Exception occured in updateSurveyMailThreshold() while updating survey completion threshold. Nested exception is ",
                error );
            return "Unable To Update Survey Mail Threshold.";
        }
    }
    
    @ResponseBody
    @RequestMapping ( value = "/getcompaniesfortransactionmonitor", method = RequestMethod.GET)
    public String getCompaniesForTransactionMonitor()
    {
        LOG.info( "Method  getCompaniesForTransactionMonitor started" );   
        List<OrganizationUnitSettings> companyList =  organizationManagementService.getCompaniesForTransactionMonitor();
        LOG.info( "Method getCompaniesForTransactionMonitor() finished" );
        return new Gson().toJson( companyList );
    }

    
    @RequestMapping ( value = "/updatetransactionmonitorsettingforcompany", method = RequestMethod.GET)
    @ResponseBody
    public String updateTransactionMonitorSettingForCompany( HttpServletRequest request )
    {
        LOG.info( "Method to update updateTransactionMonitorSettingForCompany started" );

        try {

            HttpSession session = request.getSession();
            long companyId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );

            String updateTransactionMonitorSettingStr = request.getParameter( "updateTransactionMonitorSetting" );

            boolean updateTransactionMonitorSetting = Boolean.parseBoolean( updateTransactionMonitorSettingStr );

            OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( companyId );
            if(companySettings == null){
                throw new InvalidInputException("Wrong input passed. No company found for given id");
            }
           
            organizationManagementService.updateTransactionMonitorSettingForCompany( companyId, updateTransactionMonitorSetting );


        } catch ( Exception error ) {
            LOG.error( "Exception occured in updateTransactionMonitorSettingForCompany(). Nested exception is ", error );
            return error.getMessage();
        }

        LOG.info( "Method to update allow partner survey finished" );
        return "success";
    }
    
    
    /**
     * This controller is called to store e-mails set for digest
     * 
     * @param request
     * @param model
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/updatedigestrecipients", method = RequestMethod.POST)
    public String updateDigestRecipients( HttpServletRequest request, Model model )
    {
        LOG.info( "Method updateDigestRecipients() started." );
        HttpSession session = request.getSession();
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String status = "";

        try {

            String emailsStr = request.getParameter( "emails" );
            if ( emailsStr == null ) {
                LOG.warn( "Null or empty value found in updateDigestRecipients() for emails." );
                status = "Unable to find email data";
            } else {

                Set<String> emailList = organizationManagementService.parseEmailsList( emailsStr );
                organizationManagementService.updateDigestRecipients( entityType, entityId, emailList );

                if ( emailList == null || emailList.isEmpty() ) {
                    status = "Additional digest recipients removed!";
                } else {
                    status = "Digest recipient List updated successfully!";
                }

            }

        } catch ( NonFatalException error ) {
            LOG.warn( "Non fatal exception caught in updateDigestRecipients(). Nested exception is ", error );
            status = "Unable to update digest recipients";
        }

        LOG.info( "Method updateDigestRecipients() finished." );
        return status;
    }


    @ResponseBody
    @RequestMapping ( value = "/updateadddeletenotifyrecipients", method = RequestMethod.POST)
    public String updateAddDeleteNotifyRecipients( HttpServletRequest request, Model model )
    {
        LOG.info( "Method updateAddDeleteNotifyRecipients() started." );
        HttpSession session = request.getSession();
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String status = "";
        
        if( StringUtils.isEmpty( entityType ) || !CommonConstants.COMPANY_ID.equals( entityType ) ) {
            LOG.warn( "Unable to update add/delete notification recipients" );
            return "Unable to update add/delete notification recipients, insufficient permissions";
        }

        try {

            String emailsStr = request.getParameter( "emails" );
            if ( emailsStr == null ) {
                LOG.warn( "Null or empty value found in updateAddDeleteNotifyRecipients() for emails." );
                status = "Unable to find email data";
            } else {

                Set<String> emailList = organizationManagementService.parseEmailsList( emailsStr );
                organizationManagementService.updateUserAdditionDeletionRecipients( entityType, entityId, emailList );

                if ( emailList == null || emailList.isEmpty() ) {
                    status = "User add/delete notification recipients removed!";
                } else {
                    status = "User add/delete notification recipients updated successfully!";
                }

            }

        } catch ( NonFatalException error ) {
            LOG.warn( "Non fatal exception caught in updateAddDeleteNotifyRecipients(). Nested exception is ", error );
            status = "Unable to update add/delete notification recipients";
        }

        LOG.info( "Method updateAddDeleteNotifyRecipients() finished." );
        return status;
    }
    

    @RequestMapping ( value = "/updatecopytoclipboardsettings", method = RequestMethod.GET)
    @ResponseBody
    public String updateCopyToClipBoardSettings( HttpServletRequest request )
    {
        LOG.info( "Method to update updateTransactionMonitorSettingForCompany started" );

        try {

            HttpSession session = request.getSession();
            long companyId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );

            String updateCopyToClipBoardSettingStr = request.getParameter( "updateCopyToClipBoardSetting" );

            boolean updateCopyToClipBoardSetting = Boolean.parseBoolean( updateCopyToClipBoardSettingStr );

            OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( companyId );
            if(companySettings == null){
                throw new InvalidInputException("Wrong input passed. No company found for given id");
            }
           
            organizationManagementService.updateCopyToClipBoardSettings( companyId, updateCopyToClipBoardSetting );


        } catch ( Exception error ) {
            LOG.error( "Exception occured in updateCopyToClipBoardSettings(). Nested exception is ", error );
            return error.getMessage();
        }

        LOG.info( "Method to update copy to clipboard finished" );
        return "success";
    }
    
    @RequestMapping ( value = "/enablesocialmonitortoggle", method = RequestMethod.POST)
    @ResponseBody
    public String enableSocialMonitorToggle( HttpServletRequest request )
    {
        LOG.info( "Method enableSocialMonitorToggle started" );
        HttpSession session = request.getSession();

        long companyId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );

        try {
            return String.valueOf( organizationManagementService.enableSocialMonitorToggle( companyId,
                Boolean.parseBoolean( request.getParameter( "isSocialMonitorEnabled" ) ) ) );
        } catch ( Exception error ) {
            LOG.error(
                "Exception occured in enableSocialMonitorToggle() while updating social monitor flag. Nested exception is ",
                error );
            return "false";
        }
    }

    @ResponseBody
    @RequestMapping ( value = "/updateentitysettings", method = RequestMethod.POST)
    public String updateEntitySettings( HttpServletRequest request, Model model )
    {
        LOG.info( "Method updateEntitySettings() started." );
        HttpSession session = request.getSession();
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String settingName = (String) request.getParameter( "settingName" );
        String settingStatus = (String) request.getParameter( "settingStatus" );
        String message = null;
        if ( organizationManagementService.updateEntitySettings( entityType, entityId, settingName, settingStatus ) ) {
            message = "Successfully updated settings";
        } else {
            message = "Some problem occurred while updating settings. Please try again later";
        }
        return message;
    }    

    @ResponseBody
    @RequestMapping ( value = "/savewidgetconfiguration", method=RequestMethod.POST)
    public String saveWidgetConfiguration( HttpServletRequest request )
    {
        LOG.info( "Method saveWidgetConfiguration() started to store widget configuration." );

        try {
            WidgetConfigurationRequest widgetConfigurationRequest = new WidgetConfigurationRequest();
            widgetConfigurationRequest.setFont( request.getParameter( CommonConstants.WIDGET_FONT ) );
            widgetConfigurationRequest.setBackgroundColor( request.getParameter( CommonConstants.WIDGET_BACKGROUND_COLOR ) );
            widgetConfigurationRequest
                .setRatingAndStarColor( request.getParameter( CommonConstants.WIDGET_RATING_AND_STAR_COLOR ) );
            widgetConfigurationRequest.setBarGraphColor( request.getParameter( CommonConstants.WIDGET_BAR_GRAPH_COLOR ) );
            widgetConfigurationRequest.setForegroundColor( request.getParameter( CommonConstants.WIDGET_FOREGROUND_COLOR ) );
            widgetConfigurationRequest.setFontTheme( request.getParameter( CommonConstants.WIDGET_FONT_THEME ) );
            widgetConfigurationRequest
                .setEmbeddedFontTheme( request.getParameter( CommonConstants.WIDGET_EMBEDDED_FONT_THEME ) );
            widgetConfigurationRequest.setButtonOneName( request.getParameter( CommonConstants.WIDGET_BUTTON1_TEXT ) );
            widgetConfigurationRequest.setButtonOneLink( request.getParameter( CommonConstants.WIDGET_BUTTON1_LINK ) );
            widgetConfigurationRequest.setButtonOneOpacity( request.getParameter( CommonConstants.WIDGET_BUTTON1_OPACITY ) );
            widgetConfigurationRequest.setButtonTwoName( request.getParameter( CommonConstants.WIDGET_BUTTON2_TEXT ) );
            widgetConfigurationRequest.setButtonTwoLink( request.getParameter( CommonConstants.WIDGET_BUTTON2_LINK ) );
            widgetConfigurationRequest.setButtonTwoOpacity( request.getParameter( CommonConstants.WIDGET_BUTTON2_OPACITY ) );
            widgetConfigurationRequest
                .setReviewLoaderName( request.getParameter( CommonConstants.WIDGET_LOAD_MORE_BUTTON_TEXT ) );
            widgetConfigurationRequest
                .setReviewLoaderOpacity( request.getParameter( CommonConstants.WIDGET_LOAD_MORE_BUTTON_OPACITY ) );
            widgetConfigurationRequest.setReviewSortOrder( request.getParameter( CommonConstants.WIDGET_SORT_ORDER ) );
            widgetConfigurationRequest.setMaxReviewsOnLoadMore( request.getParameter( CommonConstants.WIDGET_MAX_REVIEWS_ON_LOAD_MORE ) );
            widgetConfigurationRequest.setInitialNumberOfReviews( request.getParameter( CommonConstants.WIDGET_INITIAL_NUMBER_OF_REVIEWS ) );
            widgetConfigurationRequest.setHideBarGraph( request.getParameter( CommonConstants.WIDGET_HIDE_BAR_GRAPH ) );
            widgetConfigurationRequest.setHideOptions( request.getParameter( CommonConstants.WIDGET_HIDE_OPTIONS ) );
            widgetConfigurationRequest.setAllowModestBranding(request.getParameter( CommonConstants.WIDGET_ALLOW_MODEST_BRANDING ) );
            widgetConfigurationRequest.setReviewSources( request.getParameter( CommonConstants.WIDGET_REVIEW_SOURCES ) );
            widgetConfigurationRequest.setRequestMessage( request.getParameter( CommonConstants.REQUEST_MESSAGE ) );
            widgetConfigurationRequest.setSeoTitle( request.getParameter( CommonConstants.WIDGET_SEO_TITLE ) );
            widgetConfigurationRequest.setSeoKeywords( request.getParameter( CommonConstants.WIDGET_SEO_KEYWORDS ) );
            widgetConfigurationRequest.setSeoDescription( request.getParameter( CommonConstants.WIDGET_SEO_DESCRIPTION ) );

            
            HttpSession session = request.getSession();
            User user = sessionHelper.getCurrentUser();
            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            Response response = null;

            response = ssApiIntergrationBuilder.getIntegrationApi().saveWidgetConfiguration( entityId, entityType,
                user.getUserId(), widgetConfigurationRequest );

            LOG.info( "Method to saveWidgetConfiguration() finished." );
            String responseString = null;
            responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
            return responseString;
        } catch ( SSAPIException e ) {
            LOG.error( "Unable to save widget configuration.", e );
            return e.getMessage();
        } catch ( Exception e ) {
            LOG.error( "Unable to save widget configuration.", e );
            return "could not save widget configuration";
        }
    }
    
    @ResponseBody
    @RequestMapping ( value = "/getdefaultwidgetconfiguration", method=RequestMethod.GET)
    public String getDefaultWidgetConfiguration( HttpServletRequest request )
    {
        LOG.info( "Method getDefaultWidgetConfiguration() started to store widget configuration." );

        try {
            
            HttpSession session = request.getSession();
            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            Response response = null;

            response = ssApiIntergrationBuilder.getIntegrationApi().getDefaultWidgetConfiguration( entityId, entityType );

            LOG.info( "Method to getDefaultWidgetConfiguration() finished." );
            String responseString = null;
            responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
            return responseString;
        } catch ( SSAPIException e ) {
            LOG.error( "Unable to get default widget configuration.", e );
            return e.getMessage();
        } catch ( Exception e ) {
            LOG.error( "Unable to get default widget configuration.", e );
            return "could not get default widget configuration";
        }
    }
        
}
// JIRA: SS-24 BY RM02 EOC