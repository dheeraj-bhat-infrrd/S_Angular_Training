package com.realtech.socialsurvey.web.controller;

import java.util.ArrayList;
// JIRA SS-21 : by RM-06 : BOC
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.LicenseDetail;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.RegistrationStage;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.entities.UserSettings;
import com.realtech.socialsurvey.core.entities.VerticalsMaster;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.authentication.AuthenticationService;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.web.common.JspResolver;


@Controller
public class LoginController
{

    private static final Logger LOG = LoggerFactory.getLogger( LoginController.class );
    private static final String STATUS_PARAM = "s";
    private static final String AUTH_ERROR = "autherror";
    private static final String SESSION_ERROR = "sessionerror";
    private static final String LOGOUT = "logout";

    @Autowired
    private MessageUtils messageUtils;
    @Autowired
    private URLGenerator urlGenerator;
    @Autowired
    private SessionHelper sessionHelper;
    @Autowired
    private UserManagementService userManagementService;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private OrganizationManagementService organizationManagementService;
    
    @Value ( "${ENABLE_CAPTCHA}")
    private String enableCaptcha;


    // Redirects user to Landing Page if session is active
    @ResponseBody
    @RequestMapping ( value = "/redirectifexistsactivesession")
    public String redirectToUserHomeIfSessionExists()
    {
        LOG.info( "Method redirectToUserHomeIfSessionExists() called from LoginController" );
        return String.valueOf( sessionHelper.isUserActiveSessionExists() );
    }


    // Redirects user to Landing Page if session is active, else to index page
    @RequestMapping ( value = "/home")
    public String initHomePage( HttpServletResponse response )
    {
        LOG.info( "Method initHomePage() called from LoginController" );
        sessionHelper.redirectToUserSessionIfExists( response );
        return JspResolver.INDEX;
    }


    @RequestMapping ( value = "/index")
    public String initIndexPage( HttpServletResponse response )
    {
        LOG.info( "Method initIndexPage() called from LoginController" );
        return JspResolver.INDEX;
    }


    @RequestMapping ( value = "/accountsignupredirect")
    public String initNewAccountSignUp( @RequestParam ( value = "PlanId", required = false) String planId,
        @RequestParam ( value = "newUser", required = false) String newUser, Model model, RedirectAttributes attributes )
    {
        LOG.info( "Method initNewAccountSignUp() called from LoginController" );
        attributes.addFlashAttribute( "planId", planId );
        attributes.addFlashAttribute( "newUser", newUser );
        Map<String, Object> map = model.asMap();
        attributes.addFlashAttribute( "isLinkedin", map.get( "isLinkedin" ) );
        attributes.addFlashAttribute( "linkedinResponse", map.get( "linkedinResponse" ) );
        attributes.addFlashAttribute( "userId", map.get( "userId" ) );
        attributes.addFlashAttribute( "companyId", map.get( "companyId" ) );
        attributes.addFlashAttribute( "firstName", map.get( "firstName" ) );
        attributes.addFlashAttribute( "lastName", map.get( "lastName" ) );
        attributes.addFlashAttribute( "setPassword", map.get( "setPassword" ) );
        
        attributes.addFlashAttribute( "enableCaptcha", enableCaptcha );
        return "redirect:/accountsignup.do";
    }


    @RequestMapping ( value = "/accountsignup")
    public String initNewAccountSignUpRedirect( Model model )
    {
        LOG.info( "Method initNewAccountSignUp() called from LoginController" );
        return JspResolver.NEW_ACCOUNT_SIGNUP;
    }


    @RequestMapping ( value = "/noactiveprofiles")
    public String initNoProfilesFoundPage( HttpServletResponse response )
    {
        LOG.info( "Method initIndexPage() called from LoginController" );
        return JspResolver.NO_PROFILES_FOUND;
    }


    @RequestMapping ( value = "/login")
    public String initLoginPage( HttpServletRequest request, Model model, @RequestParam ( value = STATUS_PARAM, required = false) String status,
        RedirectAttributes redirectAttributes )
    {
        LOG.info( "Inside initLoginPage() of LoginController" );

        // Check for existing session
        if ( sessionHelper.isUserActiveSessionExists() ) {
            LOG.info( "Existing Active Session detected" );

            redirectAttributes.addFlashAttribute( CommonConstants.ACTIVE_SESSIONS_FOUND, "true" );
            redirectAttributes.addFlashAttribute( "isDirectRegistration", request.getParameter( "isDirectRegistration" ) );
            return "redirect:/" + JspResolver.USER_LOGIN + ".do?bm=I";
        }

        if ( status != null ) {
            switch ( status ) {
                case AUTH_ERROR:
                    model.addAttribute( "status", DisplayMessageType.ERROR_MESSAGE );
                    model.addAttribute( "message", messageUtils.getDisplayMessage(
                        DisplayMessageConstants.INVALID_USER_CREDENTIALS, DisplayMessageType.ERROR_MESSAGE ) );
                    break;
                case SESSION_ERROR:
                    model.addAttribute( "status", DisplayMessageType.ERROR_MESSAGE );
                    model.addAttribute( "message", messageUtils.getDisplayMessage( DisplayMessageConstants.SESSION_EXPIRED,
                        DisplayMessageType.ERROR_MESSAGE ) );
                    break;
                case LOGOUT:
                    model.addAttribute( "status", DisplayMessageType.SUCCESS_MESSAGE );
                    model.addAttribute( "message", messageUtils.getDisplayMessage(
                        DisplayMessageConstants.USER_LOGOUT_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE ) );
                    break;
            }
        }
        return JspResolver.LOGIN;
    }


    @RequestMapping ( value = "/newlogin")
    public String newLogin()
    {
        return JspResolver.LOGIN;
    }


    @RequestMapping ( value = "/landing")
    public String initLandingPage( Model model, HttpServletRequest request )
    {
        LOG.info( "Login Page started" );

        HttpSession session = request.getSession( false );
        User user = sessionHelper.getCurrentUser();
        
        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        String isAutoLogin = (String) session.getAttribute( CommonConstants.IS_AUTO_LOGIN );

        boolean hiddenSection = false;
        try {
            OrganizationUnitSettings settings = organizationManagementService
                .getCompanySettings( user.getCompany().getCompanyId() );
            if ( settings != null ) {
                hiddenSection = settings.isHiddenSection();
                model.addAttribute( "hiddenSection", hiddenSection );
            }
            
            //REALTECH_USER_ID is set only for real tech and SS admin
            boolean isRealTechOrSSAdmin = false;
            Long adminUserid = (Long) session.getAttribute( CommonConstants.REALTECH_USER_ID );
            if ( adminUserid != null ) {
                isRealTechOrSSAdmin = true;
            }
            model.addAttribute( "isRealTechOrSSAdmin", isRealTechOrSSAdmin );
            
            //get detail of expire social media
        } catch ( InvalidInputException e ) {
            LOG.error( "fetching hiddensction varibale value failed." + e );
        }
        if ( user.isSuperAdmin() ) {
            return JspResolver.ADMIN_LANDING;
        } else {
            boolean enableTokenRefresh = true;
            boolean isTokenRefreshRequired = false;
            boolean isSocialMediaExpired = false;
            List<String> socialMediaListToRefresh = new ArrayList<>();
            try {
                Long adminUserid = (Long) session.getAttribute( CommonConstants.REALTECH_USER_ID );
                if ( adminUserid != null ) {
                    enableTokenRefresh = false;
                } else if ( StringUtils.isNotEmpty( isAutoLogin )  ) {
                    if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                        enableTokenRefresh = organizationManagementService.getCompanySettings( user )
                            .isAllowOverrideForSocialMedia();
                    } else {
                        OrganizationUnitSettings companySettings = organizationManagementService
                            .getCompanySettings( user.getCompany().getCompanyId() );
                        enableTokenRefresh = companySettings.isAllowOverrideForSocialMedia();
                    }
                }
                model.addAttribute( "enableTokenRefresh", enableTokenRefresh );
                if ( enableTokenRefresh ) {
                    //get detail of expire social media           
                    if ( entityType == CommonConstants.AGENT_ID_COLUMN || entityType == CommonConstants.COMPANY_ID_COLUMN ) {
                        socialMediaListToRefresh = organizationManagementService.validateSocailMedia( entityType, entityId );
                        if ( !socialMediaListToRefresh.isEmpty() ) {
                            isTokenRefreshRequired = true;
                        }
                        if ( !organizationManagementService.getExpiredSocailMedia( entityType, entityId ).isEmpty() )
                            isSocialMediaExpired = true;
                    }
                }
            } catch ( InvalidInputException e ) {
                LOG.error( "fetching hiddensction varibale value failed.", e );
            } catch ( NoRecordsFetchedException e ) {
                LOG.error( "No records found while checking social media expiry.", e );
            }
            model.addAttribute( "isSocialMediaExpired", isSocialMediaExpired );
            model.addAttribute( "isTokenRefreshRequired", isTokenRefreshRequired );
            model.addAttribute( "expiredSocialMediaList", new Gson().toJson( socialMediaListToRefresh ) );
        }
        return JspResolver.LANDING;
    }


    @RequestMapping ( value = "/forgotpassword")
    public String initForgotPassword()
    {
        LOG.info( "Forgot Password Page started" );
        return JspResolver.FORGOT_PASSWORD;
    }


    private void setSession( HttpSession session ) throws InvalidInputException, NoRecordsFetchedException
    {
        // get the user's canonical settings
        LOG.info( "Fetching the user's canonical settings and setting it in session" );
        sessionHelper.getCanonicalSettings( session );
        // Set the session variables
        sessionHelper.setSettingVariablesInSession( session );
    }


    /**
     * Method for logging in user
     * 
     * @param model
     * @param request
     * @param response
     * @return
     */
    @RequestMapping ( value = "/userlogin", method = RequestMethod.GET)
    public String login( Model model, HttpServletRequest request, HttpServletResponse response,
        RedirectAttributes redirectAttributes )
    {
        LOG.info( "Login controller called for user login" );
        User user = null;
        AccountType accountType = null;
        String redirectTo = null;
        String isDirectRegistration = null;

        try {

            // Setting the direct registration flag
            isDirectRegistration = request.getParameter( "isDirectRegistration" );
            // handle direct registration, if the user has incomplete
            // registration for manual invite. in that case bm will be set as I
            if ( request.getParameter( "bm" ) != null && request.getParameter( "bm" ).equals( "I" ) ) {
                isDirectRegistration = "false";
            }
            HttpSession session = request.getSession( true );
            user = sessionHelper.getCurrentUser();

            if ( user.getIsForcePassword() == 1
                && !user.getCompany().getRegistrationStage().equalsIgnoreCase( RegistrationStage.COMPLETE.getCode() ) ) {
                return "redirect:/registeraccount/newloginas.do?userId=" + user.getUserId();
            }

            
            try {
                OrganizationUnitSettings companySettings = organizationManagementService
                    .getCompanySettings( user.getCompany().getCompanyId() );
                if ( companySettings != null )
                    redirectAttributes.addFlashAttribute( "hiddenSection", companySettings.isHiddenSection() );
            } catch ( InvalidInputException e ) {
                throw new InvalidInputException( "Invalid Input exception occured in method getCompanySettings()",
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }

            // code to hide the overlay during registration
            if ( isDirectRegistration != null ) {
                if ( isDirectRegistration.equals( "false" ) ) {
                    model.addAttribute( "skippayment", "true" );
                    session.setAttribute( "skippayment", "true" );
                } else if ( isDirectRegistration.equals( "true" ) ) {
                    model.addAttribute( "skippayment", "false" );
                    session.setAttribute( "skippayment", "false" );
                }
            } else {
                model.addAttribute( "skippayment", "false" );
                session.setAttribute( "skippayment", "false" );
            }


            // Check if super admin is logged in
            if ( user.isSuperAdmin() ) {
                session.setAttribute( "isSuperAdmin", true );
                return JspResolver.ADMIN_LANDING;
            } else {
                session.setAttribute( "isSuperAdmin", false );
            }


            if ( userManagementService.isUserSocialSurveyAdmin( user.getUserId() ) ) {
                // social survey admin
                model.addAttribute( "isSuperAdmin", false );
                return JspResolver.ADMIN_LANDING;
            }

            try {
                long realtechUserId = (long) session.getAttribute( CommonConstants.REALTECH_USER_ID );

                if ( realtechUserId > -1 ) {
                    session.setAttribute( CommonConstants.POPUP_FLAG_IN_SESSION, CommonConstants.NO_STRING );
                }
            } catch ( NullPointerException e ) {
                LOG.error( "Realtech User id not present in session, direct user login" );
            }


            userManagementService.setProfilesOfUser( user );
            List<LicenseDetail> licenseDetails = user.getCompany().getLicenseDetails();
            if ( licenseDetails != null && !licenseDetails.isEmpty() ) {
                LicenseDetail licenseDetail = licenseDetails.get( 0 );
                accountType = AccountType.getAccountType( licenseDetail.getAccountsMaster().getAccountsMasterId() );
                session.setAttribute( CommonConstants.ACCOUNT_TYPE_IN_SESSION, accountType );

                LOG.debug( "Checking if the account is disabled because of payment failure" );
                if ( user.getCompany().getStatus() == CommonConstants.STATUS_COMPANY_DISABLED ) {
                    LOG.debug( "Payment has failed. Returning account disabled page" );
                    session.invalidate();
                    SecurityContextHolder.clearContext();
                    model.addAttribute( CommonConstants.DISABLED_ACCOUNT_FLAG, CommonConstants.YES );
                    return JspResolver.ACCOUNT_DISABLED_PAGE;
                }
            } else {
                LOG.debug( "License details not found for the user's company" );
            }

            //get agent settings
            AgentSettings agentSettings = null;
            try {
                agentSettings = organizationManagementService.getAgentSettings(user.getUserId());            	
            }catch(NoRecordsFetchedException e) {
            		throw new InvalidInputException( "No settings found for user", DisplayMessageConstants.GENERAL_ERROR );
            }
            
            //check if login is prevented for user
            if(agentSettings.isLoginPrevented() &&  ! StringUtils.equals(((String) session.getAttribute( CommonConstants.IS_AUTO_LOGIN)), "true")  ) {
            		session.invalidate();
                SecurityContextHolder.clearContext();
                model.addAttribute( CommonConstants.DISABLED_ACCOUNT_FLAG, CommonConstants.YES );
                return JspResolver.LOGIN_DISABLED_PAGE;
            }
            
            /**
             * Check if if the company inserted is default company or registration is not complete ,
             * if company registration not done redirect to company registration page
             */
            LOG.debug( "Checking if company profile registration complete" );
            if ( user.getCompany().getCompanyId() == CommonConstants.DEFAULT_COMPANY_ID
                || user.getCompany().getIsRegistrationComplete() != CommonConstants.PROCESS_COMPLETE ) {

                LOG.debug( "Company profile not complete, redirecting to company information page" );
                redirectTo = JspResolver.COMPANY_INFORMATION;

                List<VerticalsMaster> verticalsMasters = null;
                try {
                    verticalsMasters = organizationManagementService.getAllVerticalsMaster();
                } catch ( InvalidInputException e ) {
                    throw new InvalidInputException( "Invalid Input exception occured in method getAllVerticalsMaster()",
                        DisplayMessageConstants.GENERAL_ERROR, e );
                }

                redirectAttributes.addFlashAttribute( "verticals", verticalsMasters );
                redirectAttributes.addFlashAttribute( "isDirectRegistration", isDirectRegistration );
                return "redirect:/" + JspResolver.COMPANY_INFORMATION_PAGE + ".do";
            } else {
                LOG.debug( "Company profile complete, check any of the user profiles is entered" );
                if ( user.getIsAtleastOneUserprofileComplete() == CommonConstants.PROCESS_COMPLETE ) {
                    /**
                     * Compute all conditions for user and if user is CA then check for profile
                     * completion stage.
                     */
                    if ( user.isCompanyAdmin() ) {
                        UserProfile adminProfile = null;
                        for ( UserProfile userProfile : user.getUserProfiles() ) {
                            if ( ( userProfile.getCompany().getCompanyId() == user.getCompany().getCompanyId() )
                                && ( userProfile.getProfilesMaster()
                                    .getProfileId() == CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID ) )
                                adminProfile = userProfile;
                        }
                        redirectTo = getRedirectionFromProfileCompletionStage( adminProfile, redirectAttributes );
                    } else {
                        redirectTo = JspResolver.LANDING;
                    }

                    if ( redirectTo.equals( JspResolver.LANDING ) ) {
                        setSession( session );

                        if ( sessionHelper.getCurrentUser() != null && sessionHelper.getCurrentUser().getCompany() != null ) {
                            String billingMode = sessionHelper.getCurrentUser().getCompany().getBillingMode();
                            if ( billingMode.equals( CommonConstants.BILLING_MODE_AUTO ) ) {
                                session.setAttribute( CommonConstants.BILLING_MODE_ATTRIBUTE_IN_SESSION,
                                    CommonConstants.BILLING_MODE_AUTO );
                            } else {
                                session.setAttribute( CommonConstants.BILLING_MODE_ATTRIBUTE_IN_SESSION,
                                    CommonConstants.BILLING_MODE_INVOICE );
                            }
                        } else {
                            session.setAttribute( CommonConstants.BILLING_MODE_ATTRIBUTE_IN_SESSION,
                                CommonConstants.BILLING_MODE_AUTO );
                        }

                        // Setting session variable to show linkedin signup and sendsurvey popups
                        // only once

                        String popupStatus = (String) session.getAttribute( CommonConstants.POPUP_FLAG_IN_SESSION );
                        if ( popupStatus == null ) {
                            session.setAttribute( CommonConstants.POPUP_FLAG_IN_SESSION, CommonConstants.YES_STRING );
                        } else if ( popupStatus.equals( CommonConstants.YES_STRING ) ) {
                            session.setAttribute( CommonConstants.POPUP_FLAG_IN_SESSION, CommonConstants.NO_STRING );
                        }

                        // setting linkedin popup attribute
                        boolean showLinkedInPopup = false;
                        boolean showSendSurveyPopup = false;

                        for ( UserProfile profile : user.getUserProfiles() ) {
                            if ( profile.getProfilesMaster()
                                .getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID ) {
                                showLinkedInPopup = true;
                                if ( user.getIsForcePassword() == 1 && user.getLoginPassword() != null ) {
                                    showSendSurveyPopup = true;
                                    break;
                                }
                            }
                        }
                        if ( user.getNumOfLogins() != 0 ) {
                            showLinkedInPopup = false;
                        }
                        redirectAttributes.addFlashAttribute( "showLinkedInPopup", String.valueOf( showLinkedInPopup ) );
                        redirectAttributes.addFlashAttribute( "showSendSurveyPopup", String.valueOf( showSendSurveyPopup ) );

                        // updating session with selected user profile if not set
                        sessionHelper.processAssignments( session, user );

                        // update the last login time and number of logins
                        userManagementService.updateUserLoginTimeAndNum( user );
                    }
                } else {
                    LOG.info( "No User profile present" );
                    return "redirect:/" + JspResolver.NO_ACTIVE_PROFILES + ".do";
                }
            }

            LOG.info( "User login successful" );
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while logging in. Reason : " + e.getMessage(), e );
            redirectAttributes.addFlashAttribute( "message",
                messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
            redirectAttributes.addFlashAttribute( "isDirectRegistration", isDirectRegistration );
            return "redirect:/" + JspResolver.LOGIN + ".do";
        }

        // set the direct registration value, in case if its a manual
        // registration
        LOG.debug( "Settings isDirectRegistration to " + request.getParameter( "isDirectRegistration" ) );
        redirectAttributes.addFlashAttribute( "isDirectRegistration", isDirectRegistration );
        return "redirect:/" + redirectTo + ".do";
    }


    /**
     * Start the companyinformation page
     * 
     * @return
     */
    @RequestMapping ( value = "/addcompanyinformation")
    public String initCompanyInformationPage()
    {
        return JspResolver.COMPANY_INFORMATION;
    }


    /**
     * Start the add account type page
     */
    @RequestMapping ( value = "/addaccounttype")
    public String initAddAccountTypePage()
    {
        LOG.info( "Add account type page started" );
        return JspResolver.ACCOUNT_TYPE_SELECTION;
    }


    /**
     * Controller method to send reset password link to the user email ID
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/sendresetpasswordlink", method = RequestMethod.POST)
    public String sendResetPasswordLink( Model model, HttpServletRequest request )
    {
        LOG.info( "Send password reset link to User" );

        User user = null;
        try {
            String emailId = request.getParameter( "emailId" );
            if ( emailId == null || emailId.isEmpty() || !organizationManagementService.validateEmail( emailId ) ) {
                LOG.error( "Invalid email id passed" );
                throw new InvalidInputException( "Invalid email id passed", DisplayMessageConstants.INVALID_EMAILID );
            }

            try {
                // verify if the user exists with the registered emailId
                user = authenticationService.verifyRegisteredUser( emailId );
            } catch ( InvalidInputException e ) {
                LOG.error( "Invalid Input exception in verifying registered user. Reason " + e.getMessage(), e );
                throw new InvalidInputException( e.getMessage(), DisplayMessageConstants.USER_NOT_PRESENT, e );
            }

            if ( user.getStatus() != CommonConstants.STATUS_ACTIVE ) {
                try {
                    // if user is not active send verification link
                    LOG.info( "User found for " + emailId + " but not verified" );
                    LOG.info( "Resending verification mail to mail id : " + emailId );
                    userManagementService.inviteCorporateToRegister( user.getFirstName(), user.getLastName(), emailId, false,
                        null );
                    LOG.info( "Sent successfully verification mail to mail id : " + emailId );
                    model.addAttribute( "message", messageUtils.getDisplayMessage(
                        DisplayMessageConstants.USER_PRESENT_NOT_REGISTERED, DisplayMessageType.ERROR_MESSAGE ) );
                } catch ( InvalidInputException e ) {
                    LOG.error( "Invalid Input exception while re-sending verification mail to user. Reason " + e.getMessage(),
                        e );
                    throw new InvalidInputException( e.getMessage(), DisplayMessageConstants.REGISTRATION_INVITE_GENERAL_ERROR,
                        e );
                } catch ( UndeliveredEmailException e ) {
                    LOG.error(
                        "Undelivered Email exception while re-sending verification mail to user. Reason " + e.getMessage(), e );
                    throw new UndeliveredEmailException( e.getMessage(),
                        DisplayMessageConstants.REGISTRATION_INVITE_GENERAL_ERROR, e );
                }
            } else {
                // Send reset password link
                try {
                    authenticationService.sendResetPasswordLink( user.getEmailId(),
                        user.getFirstName() + " " + user.getLastName(), user.getCompany().getCompanyId(), user.getLoginName() );
                } catch ( InvalidInputException e ) {
                    LOG.error( "Invalid Input exception in sending reset password link. Reason " + e.getMessage(), e );
                    throw new InvalidInputException( e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e );
                }

                model.addAttribute( "status", DisplayMessageType.SUCCESS_MESSAGE );
                model.addAttribute( "message", messageUtils.getDisplayMessage(
                    DisplayMessageConstants.PASSWORD_RESET_LINK_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE ) );
            }
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while sending the reset password link. Reason : " + e.getStackTrace(), e );
            model.addAttribute( "status", DisplayMessageType.ERROR_MESSAGE );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
        }

        return JspResolver.FORGOT_PASSWORD;
    }


    // RM-06 : BOC
    /**
     * Controller method to display the reset password page
     */
    @RequestMapping ( value = "/resetpassword")
    public String showResetPasswordPage( @RequestParam ( "q") String encryptedUrlParams, Model model )
    {
        LOG.info( "Forgot Password Page started with encrypter url : " + encryptedUrlParams );
        try {
            try {
                Map<String, String> urlParams = urlGenerator.decryptParameters( encryptedUrlParams );
                model.addAttribute( CommonConstants.EMAIL_ID, urlParams.get( CommonConstants.EMAIL_ID ) );
                model.addAttribute( CommonConstants.URL_PARAM_RESET_PASSWORD,
                    urlParams.get( CommonConstants.URL_PARAM_RESET_PASSWORD ) );
            } catch ( InvalidInputException e ) {
                LOG.error( "Invalid Input exception in decrypting url parameters. Reason " + e.getMessage(), e );
                throw new InvalidInputException( e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e );
            }
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while setting new Password. Reason : " + e.getMessage(), e );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
            return JspResolver.MESSAGE_HEADER;
        }
        return JspResolver.RESET_PASSWORD;
    }


    // RM-06 : EOC

    /**
     * Controller method to set a new password from the reset password link
     */
    @RequestMapping ( value = "/setnewpassword", method = RequestMethod.POST)
    public String resetPassword( Model model, HttpServletRequest request )
    {
        LOG.info( "Reset the user password" );
        Map<String, String> urlParams = null;
        String emailId = "";
        User user = null;

        try {
            emailId = request.getParameter( "emailId" );
            String password = request.getParameter( "password" );
            String confirmPassword = request.getParameter( "confirmPassword" );

            // Checking if any of the form parameters are null or empty
            validateResetPasswordFormParameters( emailId, password, confirmPassword );

            // Decrypt Url parameters
            String encryptedUrlParameters = request.getParameter( "q" );
            try {
                urlParams = urlGenerator.decryptParameters( encryptedUrlParameters );
            } catch ( InvalidInputException e ) {
                LOG.error( "Invalid Input exception in decrypting Url. Reason " + e.getMessage(), e );
                throw new InvalidInputException( e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e );
            }

            // check if email ID entered matches with the one in the encrypted
            // url
            if ( !urlParams.get( "emailId" ).equals( emailId ) ) {
                LOG.error(
                    "Invalid Input exception. Reason emailId entered does not match with the one to which the mail was sent" );
                throw new InvalidInputException( "Invalid Input exception", DisplayMessageConstants.INVALID_EMAILID );
            }

            long companyId = 0;
            try {
                companyId = Long.parseLong( urlParams.get( CommonConstants.COMPANY ) );
            } catch ( NumberFormatException | NullPointerException e ) {
                LOG.error( "Invalid company id found in URL parameters. Reason " + e.getMessage(), e );
                throw new InvalidInputException( e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e );
            }

            // fetch user object with email Id
            try {
                user = authenticationService.getUserWithLoginNameAndCompanyId( emailId, companyId );
            } catch ( InvalidInputException e ) {
                LOG.error( "Invalid Input exception in fetching user object. Reason " + e.getMessage(), e );
                throw new InvalidInputException( e.getMessage(), DisplayMessageConstants.USER_NOT_PRESENT, e );
            }

            if ( user.getStatus() == CommonConstants.STATUS_NOT_VERIFIED
                || user.getStatus() == CommonConstants.STATUS_INACTIVE ) {
                LOG.error( "Account with EmailId entered is either inactive or not verified" );
                throw new InvalidInputException( "Your Account is either inactive or not verified",
                    DisplayMessageConstants.INVALID_ACCOUNT );
            }

            // change user's password
            try {
                authenticationService.changePassword( user, password );
            } catch ( InvalidInputException e ) {
                LOG.error( "Invalid Input exception in changing the user's password. Reason " + e.getMessage(), e );
                throw new InvalidInputException( e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e );
            }

            LOG.info( "Reset user password executed successfully" );
            model.addAttribute( "status", DisplayMessageType.SUCCESS_MESSAGE );
            model.addAttribute( "message", messageUtils.getDisplayMessage( DisplayMessageConstants.PASSWORD_CHANGE_SUCCESSFUL,
                DisplayMessageType.SUCCESS_MESSAGE ) );
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while setting new Password. Reason : " + e.getMessage(), e );
            model.addAttribute( "emailId", emailId );
            model.addAttribute( "status", DisplayMessageType.ERROR_MESSAGE );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
            return JspResolver.RESET_PASSWORD;
        }

        return JspResolver.LOGIN;
    }


    /**
     * method for logging out
     * 
     * @param
     * @param request
     * @param response
     * @return
     */
    @RequestMapping ( value = "/logout")
    public String initLogoutPage( Model model, HttpServletRequest request, HttpServletResponse response )
    {
        LOG.info( "logging out" );
        request.getSession( false ).invalidate();
        model.addAttribute( "message", messageUtils.getDisplayMessage( DisplayMessageConstants.USER_LOGOUT_SUCCESSFUL,
            DisplayMessageType.SUCCESS_MESSAGE ) );
        return JspResolver.LOGIN;
    }


    /**
     * Method to get location of display picture
     * 
     * @param
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/getdisplaypiclocation")
    public String getDisplayPictureLocation( Model model, HttpServletRequest request, HttpServletResponse response )
    {
        LOG.info( "fetching display picture" );
        User user = sessionHelper.getCurrentUser();
        HttpSession session = request.getSession( false );
        String imageUrl = "";
        try {
            UserSettings userSettings = (UserSettings) session
                .getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
            if ( userSettings == null ) {
                throw new InvalidInputException( "No user settings found in session" );
            }

            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            if ( entityId == 0 || entityType == null ) {
                throw new InvalidInputException( "No user settings found in session" );
            }

            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                //imageUrl = organizationManagementService.getCompanySettings(user).getProfileImageUrl();
                imageUrl = organizationManagementService.getCompanySettings( user ).getProfileImageUrlThumbnail();
            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                //imageUrl = organizationManagementService.getRegionSettings(entityId).getProfileImageUrl();
                imageUrl = organizationManagementService.getRegionSettings( entityId ).getProfileImageUrlThumbnail();
            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                //imageUrl = organizationManagementService.getBranchSettingsDefault(entityId).getProfileImageUrl();
                imageUrl = organizationManagementService.getBranchSettingsDefault( entityId ).getProfileImageUrlThumbnail();
            } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
                //imageUrl = userManagementService.getUserSettings(entityId).getProfileImageUrl();
                imageUrl = userManagementService.getUserSettings( entityId ).getProfileImageUrlThumbnail();
            }
        } catch ( NonFatalException e ) {
            LOG.error( "Non fatal Exception occurred in getDisplayPictureLocation(). Nested exception is ", e );
            return e.getMessage();
        }
        return new Gson().toJson( imageUrl );
    }


    /**
     * validate reset form parameters
     * 
     * @param emailId
     * @param password
     * @param confirmPassword
     * @throws InvalidInputException
     */
    private void validateResetPasswordFormParameters( String emailId, String password, String confirmPassword )
        throws InvalidInputException
    {
        LOG.debug( "Validating reset password form paramters" );
        if ( emailId == null || emailId.isEmpty() || !organizationManagementService.validateEmail( emailId ) ) {
            LOG.error( "Invalid email id passed" );
            throw new InvalidInputException( "Invalid email id passed", DisplayMessageConstants.INVALID_EMAILID );
        }
        if ( password == null || password.isEmpty() || password.length() < CommonConstants.PASSWORD_LENGTH ) {
            LOG.error( "Invalid password" );
            throw new InvalidInputException( "Invalid password", DisplayMessageConstants.INVALID_PASSWORD );
        }
        if ( confirmPassword == null || confirmPassword.isEmpty() ) {
            LOG.error( "Confirm Password can not be null or empty" );
            throw new InvalidInputException( "Confirm Password can not be null or empty",
                DisplayMessageConstants.INVALID_PASSWORD );
        }

        // check if password and confirm password field match
        if ( !password.equals( confirmPassword ) ) {
            LOG.error( "Password and confirm password fields do not match" );
            throw new InvalidInputException( "Password and confirm password fields do not match",
                DisplayMessageConstants.PASSWORDS_MISMATCH );
        }
        LOG.debug( "Reset password form parameters validated successfully" );
    }


    /**
     * Method to get the redirect page from profile completion stage
     * 
     * @param profileCompletionStage
     * @return
     * @throws InvalidInputException
     */
    private String getRedirectionFromProfileCompletionStage( UserProfile adminProfile, RedirectAttributes redirectAttributes )
        throws InvalidInputException
    {
        String profileCompletionStage = adminProfile.getProfileCompletionStage();
        LOG.debug(
            "Method getRedirectionFromProfileCompletionStage called for profileCompletionStage: " + profileCompletionStage );

        String redirectTo = null;
        switch ( profileCompletionStage ) {
            case CommonConstants.ADD_COMPANY_STAGE:
                redirectTo = JspResolver.COMPANY_INFORMATION_PAGE;

                break;
            case CommonConstants.ADD_ACCOUNT_TYPE_STAGE:
                String invoiceType = adminProfile.getCompany().getBillingMode();
                if ( invoiceType.equalsIgnoreCase( CommonConstants.BILLING_MODE_INVOICE ) ) {
                    redirectAttributes.addFlashAttribute( "skippayment", "true" );
                } else if ( invoiceType.equalsIgnoreCase( CommonConstants.BILLING_MODE_AUTO ) ) {
                    redirectAttributes.addFlashAttribute( "skippayment", "false" );
                }
                redirectTo = JspResolver.ACCOUNT_TYPE_SELECTION_PAGE;

                break;
            case CommonConstants.PRE_PROCESSING_BEFORE_LOGIN_STAGE:
                redirectTo = CommonConstants.PRE_PROCESSING_BEFORE_LOGIN_STAGE;
                break;
            case CommonConstants.DASHBOARD_STAGE:
                redirectTo = JspResolver.LANDING;
                break;
            default:
                throw new InvalidInputException( "Profile completion stage is invalid", DisplayMessageConstants.GENERAL_ERROR );
        }

        LOG.debug( "Method getRedirectionFromProfileCompletionStage finished. Returning : " + redirectTo );
        return redirectTo;
    }
}
// JIRA SS-21 : by RM-06 : EOC