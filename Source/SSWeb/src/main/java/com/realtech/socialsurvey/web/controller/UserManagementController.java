package com.realtech.socialsurvey.web.controller;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.realtech.socialsurvey.core.entities.*;
import com.realtech.socialsurvey.core.enums.*;
import com.realtech.socialsurvey.core.vo.SocialMediaVO;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.SolrDocumentList;
import org.noggit.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.Utils;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.exception.FatalException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.exception.UserAlreadyExistsException;
import com.realtech.socialsurvey.core.services.authentication.AuthenticationService;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileNotFoundException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.services.settingsmanagement.impl.InvalidSettingsStateException;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.web.api.builder.SSApiIntergrationBuilder;
import com.realtech.socialsurvey.web.api.exception.SSAPIException;
import com.realtech.socialsurvey.web.common.ErrorCodes;
import com.realtech.socialsurvey.web.common.ErrorMessages;
import com.realtech.socialsurvey.web.common.ErrorResponse;
import com.realtech.socialsurvey.web.common.JspResolver;

import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


// JIRA SS-37 BY RM02 BOC

/**
 * Controller to manage users
 */
@Controller
public class UserManagementController
{

    private static final Logger LOG = LoggerFactory.getLogger( UserManagementController.class );
    private static final String ROLE_ADMIN = "Admin";
    private static final String ROLE_USER = "User";

    @Autowired
    private MessageUtils messageUtils;

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private OrganizationManagementService organizationManagementService;

    @Autowired
    private OrganizationUnitSettingsDao organizationUnitSettingsDao;

    @Autowired
    private ProfileManagementService profileManagementService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private URLGenerator urlGenerator;

    @Autowired
    private SessionHelper sessionHelper;

    @Autowired
    private SolrSearchService solrSearchService;


    @Autowired
    private Utils utils;


    @Autowired
    private SSApiIntergrationBuilder sSApiIntergrationBuilder;

    private final static int SOLR_BATCH_SIZE = 20;


    // JIRA SS-42 BY RM05 BOC
    /*
     * Method to show the User Management Page to a user on clicking UserManagement link.
     */
    @RequestMapping ( value = "/showusermangementpage", method = RequestMethod.GET)
    public String initUserManagementPage( Model model, HttpServletRequest request )
    {
        LOG.info( "User Management page started" );
        User user = sessionHelper.getCurrentUser();
        HttpSession session = request.getSession( false );

        try {
            if ( user == null ) {
                LOG.warn( "No user found in session" );
                throw new InvalidInputException( "No user found in session", DisplayMessageConstants.NO_USER_IN_SESSION );
            }
            if ( user.getStatus() != CommonConstants.STATUS_ACTIVE ) {
                LOG.error( "Inactive or unauthorized users can not access user management page" );
                model.addAttribute( "message", messageUtils.getDisplayMessage(
                    DisplayMessageConstants.USER_MANAGEMENT_NOT_AUTHORIZED, DisplayMessageType.ERROR_MESSAGE ) );
            }

            long companyId = user.getCompany().getCompanyId();
            try {
                long usersCount = solrSearchService.countUsersByCompany( companyId, 0, SOLR_BATCH_SIZE );
                session.setAttribute( "usersCount", usersCount );
            } catch ( MalformedURLException e ) {
                LOG.warn( "MalformedURLException while fetching users count. ", e );
                throw new NonFatalException( "MalformedURLException while fetching users count", e );
            }
        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException in while inviting new user", nonFatalException );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( nonFatalException.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
        }
        return JspResolver.USER_MANAGEMENT;
    }


    /*
     * Method to send invitation to a new user to join.
     */
    @RequestMapping ( value = "/invitenewuser", method = RequestMethod.POST)
    public String inviteNewUser( Model model, HttpServletRequest request ) throws NumberFormatException, JSONException
    {
        LOG.info( "Method to add a new user by existing admin, inviteNewUser() called." );
        HttpSession session = request.getSession( false );
        User admin = sessionHelper.getCurrentUser();
        Long adminId = (Long) session.getAttribute( CommonConstants.REALTECH_USER_ID );
        try {
            if ( admin == null ) {
                LOG.warn( "No user found in session" );
                throw new InvalidInputException( "No user found in session", DisplayMessageConstants.NO_USER_IN_SESSION );
            }
            String firstName = request.getParameter( CommonConstants.FIRST_NAME );
            String lastName = request.getParameter( CommonConstants.LAST_NAME );
            String emailId = request.getParameter( CommonConstants.EMAIL_ID );

            // form parameter validations for inviting new user
            if ( firstName == null || firstName.isEmpty() || !firstName.matches( CommonConstants.FIRST_NAME_REGEX ) ) {
                LOG.warn( "First name invalid" );
                throw new InvalidInputException( "First name invalid", DisplayMessageConstants.INVALID_FIRSTNAME );
            }
            if ( lastName != null && !lastName.isEmpty() && !lastName.matches( CommonConstants.LAST_NAME_REGEX ) ) {
                LOG.warn( "Last name invalid" );
                throw new InvalidInputException( "Last name invalid", DisplayMessageConstants.INVALID_LASTNAME );
            }
            if ( emailId == null || emailId.isEmpty() || !organizationManagementService.validateEmail( emailId ) ) {
                LOG.warn( "EmailId not valid" );
                throw new InvalidInputException( "EmailId not valid", DisplayMessageConstants.INVALID_EMAILID );
            }
            AccountType accountType = (AccountType) session.getAttribute( CommonConstants.ACCOUNT_TYPE_IN_SESSION );
            User user = null;
            try {
                if ( userManagementService.isUserAdditionAllowed( admin ) ) {
                    try {
                        user = userManagementService.getUserByEmailAddress( emailId );
                        LOG.warn( "User already exists in the company with the email id : " + emailId );
                        model.addAttribute( "existingUserId", user.getUserId() );
                        throw new UserAlreadyExistsException( "User already exists with the email id : " + emailId );
                    } catch ( NoRecordsFetchedException noRecordsFetchedException ) {
                        LOG.error( "No records exist with the email id passed, inviting the new user",
                            noRecordsFetchedException );
                        user = userManagementService.inviteUser( admin, firstName, lastName, emailId,
                            ( adminId != null && adminId > 0 ) ? true : false );
                        String profileName = userManagementService.getUserSettings( user.getUserId() ).getProfileName();
                        userManagementService.sendRegistrationCompletionLink( emailId, firstName, lastName,
                            admin.getCompany().getCompanyId(), profileName, user.getLoginName(), false );

                        // If account type is team assign user to default branch
                        if ( accountType.getValue() == CommonConstants.ACCOUNTS_MASTER_TEAM ) {
                            String branches = solrSearchService.searchBranches( "", admin.getCompany(), null, null, 0, 0 );
                            branches = branches.substring( 1, branches.length() - 1 );
                            JSONObject defaultBranch = new JSONObject( branches );
                            // assign new user to default branch in case of team account type
                            userManagementService.assignUserToBranch( admin, user.getUserId(),
                                Long.parseLong( defaultBranch.get( CommonConstants.BRANCH_ID_SOLR ).toString() ) );
                        }
                    }
                } else {
                    LOG.warn( "Limit for maximum users has already reached." );
                    throw new InvalidInputException( "Limit for maximum users has already reached.",
                        DisplayMessageConstants.MAX_USERS_LIMIT_REACHED );
                }
            } catch ( InvalidInputException e ) {
                LOG.error( "NonFatalException in inviteNewUser() while inviting new user. Reason : ", e );
                model.addAttribute( "message",
                    messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
                return JspResolver.MESSAGE_HEADER;
            } catch ( UndeliveredEmailException e ) {
                LOG.warn( "UndeliveredEmailException in inviteNewUser() while inviting new user. Reason : ", e );
                throw new UndeliveredEmailException( e.getMessage(), DisplayMessageConstants.REGISTRATION_INVITE_GENERAL_ERROR,
                    e );
            } catch ( UserAlreadyExistsException e ) {
                LOG.warn( "UserAlreadyExistsException in inviteNewUser() while inviting new user. Reason : ", e );
                throw new UserAlreadyExistsException( e.getMessage(), DisplayMessageConstants.EMAILID_ALREADY_TAKEN, e );
            }
            model.addAttribute( "userId", user.getUserId() );
            model.addAttribute( "message", messageUtils.getDisplayMessage(
                DisplayMessageConstants.REGISTRATION_INVITE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE ) );
        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException in while inviting new user. Reason : ", nonFatalException );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( nonFatalException.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
        }
        LOG.info( "Method to add a new user by existing admin, inviteNewUser() finished." );
        return JspResolver.USER_ID_ON_INVITE;
    }


    /*
     * Method to fetch list of branches a user is assigned to.
     */
    @RequestMapping ( value = "/finduserandbranchesbyuserid", method = RequestMethod.POST)
    public String findUserAndAssignedBranchesByUserId( Model model, HttpServletRequest request )
    {
        LOG.info( "Method to fetch user by user, findUserByUserId() started." );
        try {
            String userIdStr = request.getParameter( CommonConstants.USER_ID );
            HttpSession session = request.getSession( false );
            User admin = sessionHelper.getCurrentUser();
            if ( admin == null ) {
                LOG.warn( "No user found in session" );
                throw new InvalidInputException( "No user found in session", DisplayMessageConstants.NO_USER_IN_SESSION );
            }
            AccountType accountType = (AccountType) session.getAttribute( CommonConstants.ACCOUNT_TYPE_IN_SESSION );
            int accountTypeVal = accountType.getValue();
            model.addAttribute( "accounttypeval", accountTypeVal );
            if ( userIdStr == null ) {
                LOG.warn( "Invalid user id passed in method findUserByUserId()." );
                throw new InvalidInputException( "Invalid user id passed in method findUserByUserId()." );
            } else if ( userIdStr.isEmpty() ) {
                return JspResolver.USER_DETAILS;
            }
            long userId = 0;
            try {
                userId = Long.parseLong( userIdStr );
            } catch ( NumberFormatException e ) {
                LOG.warn( "Number format exception while parsing user Id", e );
                throw new NonFatalException( "Number format execption while parsing user id",
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }
            User user = userManagementService.getUserByUserId( userId );
            try {
                List<Branch> branches = userManagementService.getBranchesAssignedToUser( user );
                // Adding assigned branches to the model attribute as assignedBranches
                model.addAttribute( "assignedBranches", branches );
            } catch ( NoRecordsFetchedException e ) {
                LOG.error( "No branch attched with the user " + userId );
            }
            model.addAttribute( "searchedUser", user );

        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException while searching for user id. Reason : ", nonFatalException );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( nonFatalException.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
            return JspResolver.MESSAGE_HEADER;
        }
        LOG.info( "Method to fetch user by user id , findUserByUserId() finished." );
        // return user details page on success
        return JspResolver.USER_DETAILS;
    }


    /*
     * Method to fetch list of all the users who belong to the same as that of current user. Current
     * user is company admin who can assign different roles to other users.
     */
    @RequestMapping ( value = "/findusersforcompany", method = RequestMethod.GET)
    public String findUsersForCompany( Model model, HttpServletRequest request )
    {
        LOG.info( "Method to fetch user by company, findUsersForCompany() started." );
        int startIndex = 0;
        int batchSize = 0;

        try {
            String startIndexStr = request.getParameter( "startIndex" );
            String batchSizeStr = request.getParameter( "batchSize" );
            try {
                if ( startIndexStr == null || startIndexStr.isEmpty() ) {
                    LOG.warn( "Invalid value found in startIndex. It cannot be null or empty." );
                    throw new InvalidInputException( "Invalid value found in startIndex. It cannot be null or empty." );
                }
                if ( batchSizeStr == null || batchSizeStr.isEmpty() ) {
                    LOG.warn( "Invalid value found in batchSizeStr. It cannot be null or empty." );
                    batchSize = SOLR_BATCH_SIZE;
                }

                startIndex = Integer.parseInt( startIndexStr );
                batchSize = Integer.parseInt( batchSizeStr );
            } catch ( NumberFormatException e ) {
                LOG.warn( "NumberFormatException while searching for user id. Reason : ", e );
                throw new NonFatalException( "NumberFormatException while searching for user id", e );
            }

            User admin = sessionHelper.getCurrentUser();
            if ( admin == null ) {
                LOG.warn( "No user found in session" );
                throw new InvalidInputException( "No user found in session", DisplayMessageConstants.NO_USER_IN_SESSION );
            }

            // fetching admin details
            UserFromSearch adminUser;
            try {
                String adminUserDoc = JSONUtil.toJSON( solrSearchService.getUserByUniqueId( admin.getUserId() ) );
                Type searchedUser = new TypeToken<UserFromSearch>() {}.getType();
                adminUser = new Gson().fromJson( adminUserDoc.toString(), searchedUser );
            } catch ( SolrException e ) {
                LOG.warn( "SolrException while searching for user id", e );
                throw new NonFatalException( "SolrException while searching for user id.", e );
            }

            List<UserFromSearch> usersList = null;
            if ( admin.isCompanyAdmin() ) {
                usersList = userManagementService.getUsersUnderCompanyAdmin( admin, startIndex,
                    batchSize );
                usersList = userManagementService.checkUserCanEdit( admin, adminUser, usersList );

                model.addAttribute( "numFound", userManagementService.getUsersUnderCompanyAdminCount( admin ) );
            } else if ( admin.isRegionAdmin() ) {
                usersList = userManagementService.getUsersUnderRegionAdmin( admin, startIndex, batchSize );
                usersList = userManagementService.checkUserCanEdit( admin, adminUser, usersList );

                model.addAttribute( "numFound", userManagementService.getUsersUnderRegionAdminCount( admin ) );
            } else if ( admin.isBranchAdmin() ) {
                usersList = userManagementService.getUsersUnderBranchAdmin( admin, startIndex, batchSize );
                usersList = userManagementService.checkUserCanEdit( admin, adminUser, usersList );

                model.addAttribute( "numFound", userManagementService.getUsersUnderBranchAdminCount( admin ) );
            }

            //add socialmedia details of the users to UserFromSearch object
            SocialMediaVO socialMediaVO;
            for(UserFromSearch user: usersList) {
                List<SocialMediaVO> socialMediaVOS = new ArrayList<>(  );
                //get the details of the socialmedia which the user has connected from mongo using user
                SocialMediaTokens socialMediaTokens = organizationUnitSettingsDao.fetchSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, user.getUserId() );

                    //facebook
                    socialMediaVO = new SocialMediaVO( CommonConstants.FACEBOOK_SOCIAL_SITE );
                    if ( socialMediaTokens!= null && socialMediaTokens.getFacebookToken() != null ) {
                        if ( socialMediaTokens.getFacebookToken().isTokenExpiryAlertSent() )
                            socialMediaVO.setStatus( SocialMediaConnectionStatus.EXPIRED );
                        else
                            socialMediaVO.setStatus( SocialMediaConnectionStatus.CONNECTED );
                    }
                    socialMediaVOS.add( socialMediaVO );

                    //instagram
                    socialMediaVO = new SocialMediaVO( CommonConstants.INSTAGRAM_SOCIAL_SITE );
                    if ( socialMediaTokens!= null && socialMediaTokens.getInstagramToken() != null ) {
                        if ( socialMediaTokens.getInstagramToken().isTokenExpiryAlertSent() )
                            socialMediaVO.setStatus( SocialMediaConnectionStatus.EXPIRED );
                        else
                            socialMediaVO.setStatus( SocialMediaConnectionStatus.CONNECTED );
                    }
                    socialMediaVOS.add( socialMediaVO );

                    //facebookpixel
                    socialMediaVO = new SocialMediaVO( CommonConstants.FACEBOOK_PIXEL );
                    if ( socialMediaTokens!= null && socialMediaTokens.getFacebookPixelToken() != null ) {
                        socialMediaVO.setStatus( SocialMediaConnectionStatus.CONNECTED );
                    }
                    socialMediaVOS.add( socialMediaVO );

                    //google business
                    socialMediaVO = new SocialMediaVO( CommonConstants.GOOGLE_BUSINESS_SOCIAL_SITE );
                    if ( socialMediaTokens!= null && socialMediaTokens.getGoogleBusinessToken() != null ) {
                        socialMediaVO.setStatus( SocialMediaConnectionStatus.CONNECTED );
                    }
                    socialMediaVOS.add( socialMediaVO );

                    //google
                    socialMediaVO = new SocialMediaVO( CommonConstants.GOOGLE_SOCIAL_SITE );
                    if ( socialMediaTokens!= null && socialMediaTokens.getGoogleToken() != null ) {
                        socialMediaVO.setStatus( SocialMediaConnectionStatus.CONNECTED );
                    }
                    socialMediaVOS.add( socialMediaVO );

                    //lendingtree
                    socialMediaVO = new SocialMediaVO( CommonConstants.LENDINGTREE_SOCIAL_SITE );
                    if ( socialMediaTokens!= null && socialMediaTokens.getLendingTreeToken() != null ) {
                        socialMediaVO.setStatus( SocialMediaConnectionStatus.CONNECTED );
                    }
                    socialMediaVOS.add( socialMediaVO );

                    //linkedin
                    socialMediaVO = new SocialMediaVO( CommonConstants.LINKEDIN_SOCIAL_SITE );
                    if ( socialMediaTokens!= null && socialMediaTokens.getLinkedInToken() != null ) {
                        if ( socialMediaTokens.getLinkedInToken().isTokenExpiryAlertSent() )
                            socialMediaVO.setStatus( SocialMediaConnectionStatus.EXPIRED );
                        else
                            socialMediaVO.setStatus( SocialMediaConnectionStatus.CONNECTED );
                    }
                    socialMediaVOS.add( socialMediaVO );

                    //realtor
                    socialMediaVO = new SocialMediaVO( CommonConstants.REALTOR_SOCIAL_SITE );
                    if ( socialMediaTokens!= null && socialMediaTokens.getRealtorToken() != null ) {
                        socialMediaVO.setStatus( SocialMediaConnectionStatus.CONNECTED );
                    }
                    socialMediaVOS.add( socialMediaVO );

                    //twitter
                    socialMediaVO = new SocialMediaVO( CommonConstants.TWITTER_SOCIAL_SITE );
                    if ( socialMediaTokens!= null && socialMediaTokens.getTwitterToken() != null ) {
                        socialMediaVO.setStatus( SocialMediaConnectionStatus.CONNECTED );
                    }
                    socialMediaVOS.add( socialMediaVO );

                    //yelp
                    socialMediaVO = new SocialMediaVO( CommonConstants.YELP_SOCIAL_SITE );
                    if ( socialMediaTokens!= null && socialMediaTokens.getYelpToken() != null ) {
                        socialMediaVO.setStatus( SocialMediaConnectionStatus.CONNECTED );
                    }
                    socialMediaVOS.add( socialMediaVO );

                    //zillow
                    socialMediaVO = new SocialMediaVO( CommonConstants.ZILLOW_SOCIAL_SITE );
                    if ( socialMediaTokens!= null && socialMediaTokens.getZillowToken() != null ) {
                        socialMediaVO.setStatus( SocialMediaConnectionStatus.CONNECTED );
                    }
                    socialMediaVOS.add( socialMediaVO );

                user.setSocialMediaVOs( socialMediaVOS );
            }
            model.addAttribute( "userslist", usersList );

        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException while searching for user id. Reason : ", nonFatalException );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( nonFatalException.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
            return JspResolver.MESSAGE_HEADER;
        }

        LOG.info( "Method to fetch users by company , findUsersForCompany() finished." );
        return JspResolver.USER_LIST_FOR_MANAGEMENT;
    }


    /*
     * Method to find a user on the basis of email id provided.
     */
    @ResponseBody
    @RequestMapping ( value = "/finduserbyemail", method = RequestMethod.GET)
    public String findUserByEmail( Model model, HttpServletRequest request )
    {
        LOG.info( "Method to find users by email id, findUserByEmail() called." );
        String users = "";
        int startIndex = 0;
        int batchSize = 0;
        try {
            String searchKey = request.getParameter( "searchKey" );
            if ( searchKey == null ) {
                LOG.warn( "Invalid search key passed in method findUserByEmail()." );
                throw new InvalidInputException( "Invalid searchKey passed in method findUserByEmail()." );
            }

            String startIndexStr = request.getParameter( "startIndex" );
            String batchSizeStr = request.getParameter( "batchSize" );
            try {
                startIndex = Integer.parseInt( startIndexStr );
                batchSize = Integer.parseInt( batchSizeStr );
            } catch ( NumberFormatException e ) {
                LOG.warn( "NumberFormatException while searching for user id. Reason : ", e );
            }

            User user = sessionHelper.getCurrentUser();
            if ( user == null ) {
                LOG.warn( "No user found in current session in findUserByEmail()." );
                throw new InvalidInputException( "No user found in current session in findUserByEmail()." );
            }

            try {
                SolrDocumentList usersResult = solrSearchService.searchUsersByLoginNameOrName( searchKey,
                    user.getCompany().getCompanyId(), startIndex, batchSize );
                users = new Gson().toJson( solrSearchService.getUsersWithMetaDataFromSolrDocuments( usersResult ) );
                LOG.debug( "User search result is : " + usersResult );
                model.addAttribute( "numFound", usersResult.getNumFound() );
            } catch ( InvalidInputException invalidInputException ) {
                LOG.warn( "InvalidInputException while searching for user id. Reason : ", invalidInputException );
                throw new InvalidInputException( invalidInputException.getMessage(), invalidInputException );
            } catch ( MalformedURLException e ) {
                LOG.warn( "Error occured while searching for email id in findUserByEmail(). Reason is ", e );
                throw new NonFatalException( "Error occured while searching for email id in findUserByEmail(). Reason is ", e );
            }
        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException while searching for user by email id id. Reason : ", nonFatalException );
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setErrCode( ErrorCodes.REQUEST_FAILED );
            errorResponse.setErrMessage( ErrorMessages.REQUEST_FAILED );
            return JSONUtil.toJSON( errorResponse );
        }
        LOG.info( "Method to find users by email id, findUserByEmail() finished." );
        return users;
    }


    @RequestMapping ( value = "/findusers", method = RequestMethod.GET)
    public String findUsersByEmailIdAndRedirectToPage( Model model, HttpServletRequest request )
    {
        LOG.info( "Method for Finding users and redirecting to search page, findUsersByEmailIdAndRedirectToPage() started" );

        try {
            String users = findUserByEmail( model, request );

            User admin = sessionHelper.getCurrentUser();
            if ( admin == null ) {
                LOG.warn( "No user found in session" );
                throw new InvalidInputException( "No user found in session", DisplayMessageConstants.NO_USER_IN_SESSION );
            }

            /**
             * fetching admin details
             */
            UserFromSearch adminUser = null;
            try {
                String adminUserDoc = JSONUtil.toJSON( solrSearchService.getUserByUniqueId( admin.getUserId() ) );
                Type searchedUser = new TypeToken<UserFromSearch>() {}.getType();
                adminUser = new Gson().fromJson( adminUserDoc.toString(), searchedUser );
            } catch ( SolrException e ) {
                LOG.warn( "SolrException while searching for user id.Reason:", e );
                throw new NonFatalException( "SolrException while searching for user id.Reason:" + e.getMessage(),
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }
            Type searchedUsersList = new TypeToken<List<UserFromSearch>>() {}.getType();
            List<UserFromSearch> usersList = new Gson().fromJson( users, searchedUsersList );
            LOG.debug( "Users List in findusers:{} ", users );

            /**
             * checking the edit capabilities of user
             */
            usersList = userManagementService.checkUserCanEdit( admin, adminUser, usersList );

            model.addAttribute( "userslist", usersList );
        } catch (

        NonFatalException e ) {
            LOG.error( "NonFatalException in findusers. Reason : ", e );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
            return JspResolver.MESSAGE_HEADER;
        }

        LOG.info( "Method for Finding users and redirecting to search page, findUsersByEmailIdAndRedirectToPage() Finished" );

        return JspResolver.USER_LIST_FOR_MANAGEMENT;
    }


    @RequestMapping ( value = "/findusersunderadmin", method = RequestMethod.GET)
    public String findUsersUnderAdminAndRedirectToPage( Model model, HttpServletRequest request ) throws NonFatalException
    {
        LOG.info(
            "Method for Finding users under admin and redirecting to search page, findUsersUnderAdminAndRedirectToPage() started" );
        int startIndex = 0;
        int batchSize = 0;

        try {
            //            String users = findUserByEmail( model, request );

            String searchKey = request.getParameter( "searchKey" );
            if ( searchKey == null ) {
                LOG.warn( "Invalid search key passed in method findUserByEmail()." );
                throw new InvalidInputException( "Invalid searchKey passed in method findUserByEmail()." );
            }

            String startIndexStr = request.getParameter( "startIndex" );
            String batchSizeStr = request.getParameter( "batchSize" );
            try {
                startIndex = Integer.parseInt( startIndexStr );
                batchSize = Integer.parseInt( batchSizeStr );
            } catch ( NumberFormatException e ) {
                LOG.warn( "NumberFormatException while parsing the start index or batch size. Reason : ", e );
            }

            User admin = sessionHelper.getCurrentUser();
            if ( admin == null ) {
                LOG.warn( "No user found in session" );
                throw new InvalidInputException( "No user found in session", DisplayMessageConstants.NO_USER_IN_SESSION );
            }

            /**
             * fetching admin details
             */
            UserFromSearch adminUser = null;
            try {
                String adminUserDoc = JSONUtil.toJSON( solrSearchService.getUserByUniqueId( admin.getUserId() ) );
                Type searchedUser = new TypeToken<UserFromSearch>() {}.getType();
                adminUser = new Gson().fromJson( adminUserDoc.toString(), searchedUser );
            } catch ( SolrException e ) {
                LOG.warn( "SolrException while searching for user id. Reason:", e );
                throw new NonFatalException( "SolrException while searching for user id.Reason:" + e.getMessage(),
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }
            SolrDocumentList userIdList = solrSearchService.searchUsersByLoginNameOrNameUnderAdmin( searchKey, admin, adminUser,
                startIndex, batchSize );
            if ( userIdList != null && userIdList.size() != 0 ) {
                Set<Long> userIds = solrSearchService.getUserIdsFromSolrDocumentList( userIdList );
                List<UserFromSearch> usersList = userManagementService.getUsersByUserIds( userIds );
                usersList = userManagementService.checkUserCanEdit( admin, adminUser, usersList );
                model.addAttribute( "numFound", userIdList.getNumFound() );
                model.addAttribute( "userslist", usersList );
            } else {
                LOG.warn( "No users found under the admin id : " + admin.getUserId() );
            }
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException in findusers. Reason : ", e );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
            return JspResolver.MESSAGE_HEADER;
        } catch ( MalformedURLException e ) {
            LOG.warn( "Error occured while searching for users in findUsersUnderAdminAndRedirectToPage(). Reason is ", e );
            throw new NonFatalException(
                "Error occured while searching for users in findUsersUnderAdminAndRedirectToPage(). Reason is ", e );
        }

        LOG.info(
            "Method for Finding users under admin and redirecting to search page, findUsersUnderAdminAndRedirectToPage() finished" );
        return JspResolver.USER_LIST_FOR_MANAGEMENT;
    }


    /*
     * Method to remove an existing user. Soft delete is done.
     */
    @ResponseBody
    @RequestMapping ( value = "/removeexistinguser", method = RequestMethod.POST)
    public String removeExistingUser( Model model, HttpServletRequest request )
    {
        LOG.info( "Method to deactivate an existing user, removeExistingUser() called." );
        Map<String, String> statusMap = new HashMap<String, String>();

        String message = "";
        long userIdToRemove = 0;

        try {
            try {
                userIdToRemove = Long.parseLong( request.getParameter( "userIdToRemove" ) );
            } catch ( NumberFormatException e ) {
                LOG.warn( "Number format exception while parsing user Id", e );
                throw new NonFatalException( "Number format execption while parsing user id",
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }

            if ( userIdToRemove < 0 ) {
                LOG.warn( "Invalid user Id found to remove in removeExistingUser()." );
                throw new InvalidInputException( "Invalid user Id found to remove in removeExistingUser().",
                    DisplayMessageConstants.NO_USER_IN_SESSION );
            }

            User loggedInUser = sessionHelper.getCurrentUser();
            HttpSession session = request.getSession( false );
            Long adminId = (Long) session.getAttribute( CommonConstants.REALTECH_USER_ID );

            User userToRemove = findUserById( userIdToRemove );
            if ( loggedInUser == null ) {
                LOG.warn( "No user found in current session in removeExistingUser()." );
                throw new InvalidInputException( "No user found in current session in removeExistingUser()." );
            }

            try {
                if ( checkIfTheUserCanBeDeleted( loggedInUser, userToRemove ) ) {
                    userManagementService.deleteUserDataFromAllSources( loggedInUser, userIdToRemove,
                        CommonConstants.STATUS_INACTIVE, false, ( adminId != null && adminId > 0 ) ? true : false );
                } else {
                    statusMap.put( "status", CommonConstants.ERROR );
                }
            } catch ( InvalidInputException e ) {
                LOG.warn( "InvalidInputException found in removeExistingUser()", e );
                throw new InvalidInputException( e.getMessage(), DisplayMessageConstants.REGISTRATION_INVITE_GENERAL_ERROR, e );
            }

            message = messageUtils
                .getDisplayMessage( DisplayMessageConstants.USER_DELETE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE )
                .getMessage();
            statusMap.put( "status", CommonConstants.SUCCESS_ATTRIBUTE );
        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException while removing user. Reason : ", nonFatalException );
            statusMap.put( "status", CommonConstants.ERROR );
            message = messageUtils.getDisplayMessage( nonFatalException.getErrorCode(), DisplayMessageType.ERROR_MESSAGE )
                .getMessage();
        }

        LOG.info( "Method to remove an existing user, removeExistingUser() finished." );
        statusMap.put( "message", message );

        return new Gson().toJson( statusMap );
    }


    private User findUserById( long userId )
    {
        User user = null;
        try {
            user = userManagementService.getUserByUserId( userId );
        } catch ( InvalidInputException ie ) {
            LOG.warn( "Exception caught ", ie );
        }
        return user;
    }


    private boolean checkIfTheUserCanBeDeleted( User loggedInUser, User userToRemove )
    {
        boolean canBeDeleted = true;
        if ( userToRemove == null ) {
            canBeDeleted = false;
        } else {
            if ( loggedInUser.getUserId() == userToRemove.getUserId() ) {
                canBeDeleted = false;
            } else if ( userToRemove.isCompanyAdmin() ) {
                canBeDeleted = false;
            }
        }
        return canBeDeleted;

    }


    /*
     * Method to assign a user to a branch.
     */
    @RequestMapping ( value = "/assignusertobranch", method = RequestMethod.POST)
    public String assignUserToBranch( Model model, HttpServletRequest request )
    {
        User admin = sessionHelper.getCurrentUser();
        String userIdStr = request.getParameter( CommonConstants.USER_ID );
        String branchIdStr = request.getParameter( "branchId" );
        try {
            if ( admin == null ) {
                LOG.error( "No user found in session" );
                throw new InvalidInputException( "No user found in session", DisplayMessageConstants.NO_USER_IN_SESSION );
            }
            if ( userIdStr == null || userIdStr.isEmpty() ) {
                LOG.error( "Invalid user id passed in method assignUserToBranch()." );
                throw new InvalidInputException( "Invalid user id passed in method assiguserIdnUserToBranch()." );
            }
            if ( branchIdStr == null || branchIdStr.isEmpty() ) {
                LOG.error( "Invalid branch id passed in method assignUserToBranch()." );
                throw new InvalidInputException( "Invalid branch id passed in method assignUserToBranch()." );
            }

            LOG.info( "Method to assign user to branch is called for user " + userIdStr );
            long userId = 0;
            long branchId = 0;
            try {
                userId = Long.parseLong( userIdStr );
                branchId = Long.parseLong( branchIdStr );
            } catch ( NumberFormatException e ) {
                LOG.error( "Number format exception while parsing user Id or branch id", e );
                throw new NonFatalException( "Number format execption while parsing user id or branch id",
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }
            try {
                userManagementService.assignUserToBranch( admin, userId, branchId );
                model.addAttribute( "message", messageUtils.getDisplayMessage( DisplayMessageConstants.BRANCH_ASSIGN_SUCCESSFUL,
                    DisplayMessageType.SUCCESS_MESSAGE ) );
            } catch ( InvalidInputException e ) {
                model.addAttribute( "message", messageUtils.getDisplayMessage(
                    DisplayMessageConstants.BRANCH_ASSIGNING_NOT_AUTHORIZED, DisplayMessageType.ERROR_MESSAGE ) );
                return JspResolver.MESSAGE_HEADER;
            }
        } catch ( NonFatalException e ) {
            LOG.error( "Exception occured while assigning user to a branch. Reason : " + e.getMessage(), e );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
        }

        LOG.info( "Method to assign user to branch is finished for user " + userIdStr );
        return JspResolver.MESSAGE_HEADER;
    }


    @RequestMapping ( value = "/unassignuserfrombranch", method = RequestMethod.POST)
    public String unassignUserFromBranch( Model model, HttpServletRequest request )
    {
        User admin = sessionHelper.getCurrentUser();
        String userIdStr = request.getParameter( CommonConstants.USER_ID );
        String branchIdStr = request.getParameter( "branchId" );
        try {
            if ( admin == null ) {
                LOG.error( "No user found in session" );
                throw new InvalidInputException( "No user found in session", DisplayMessageConstants.NO_USER_IN_SESSION );
            }
            if ( userIdStr == null || userIdStr.isEmpty() ) {
                LOG.error( "Invalid user id passed in method unAssignUserFromBranch()." );
                throw new InvalidInputException( "Invalid user id passed in method unAssignUserFromBranch()." );
            }
            if ( branchIdStr == null || branchIdStr.isEmpty() ) {
                LOG.error( "Invalid branch id passed in method assignUserToBranch()." );
                throw new InvalidInputException( "Invalid branch id passed in method unAssignUserFromBranch()." );
            }

            LOG.info( "Method to unassign user to branch is called for user " + userIdStr );
            long userId = 0;
            long branchId = 0;
            try {
                userId = Long.parseLong( userIdStr );
                branchId = Long.parseLong( branchIdStr );
            } catch ( NumberFormatException e ) {
                LOG.error( "Number format exception while parsing user Id or branch id", e );
                throw new NonFatalException( "Number format execption while parsing user id or branch id",
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }

            userManagementService.unassignUserFromBranch( admin, userId, branchId );
            model.addAttribute( "message", messageUtils.getDisplayMessage( DisplayMessageConstants.BRANCH_UNASSIGN_SUCCESSFUL,
                DisplayMessageType.SUCCESS_MESSAGE ) );
        } catch ( NonFatalException e ) {
            LOG.error( "Exception occured while unassigning user from a branch. Reason : " + e.getMessage(), e );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
        }
        LOG.info( "Method to unassign user from branch is finished for user " + userIdStr );
        return JspResolver.MESSAGE_HEADER;
    }


    /**
     * Method to assign a user as branch admin
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/assignorunassignbranchadmin", method = RequestMethod.POST)
    public String assignOrUnassignBranchAdmin( Model model, HttpServletRequest request )
    {
        LOG.info( "Method to assign or unassign branch admin called" );
        try {
            User admin = sessionHelper.getCurrentUser();
            if ( admin == null ) {
                LOG.error( "No user found in session" );
                throw new InvalidInputException( "No user found in session", DisplayMessageConstants.NO_USER_IN_SESSION );
            }
            String branch = request.getParameter( "branchId" );
            String userToAssign = request.getParameter( "userId" );
            String isAssign = request.getParameter( "isAssign" );
            if ( branch == null || branch.isEmpty() ) {
                LOG.error( "Null or empty value passed for branch in assignOrUnassignBranchAdmin()" );
                throw new InvalidInputException( "Null or empty value passed for branch in assignOrUnassignBranchAdmin()" );
            }
            if ( userToAssign == null || userToAssign.isEmpty() ) {
                LOG.error( "Null or empty value passed for user id in assignOrUnassignBranchAdmin()" );
                throw new InvalidInputException( "Null or empty value passed for user id in assignOrUnassignBranchAdmin()" );
            }
            if ( isAssign == null || isAssign.isEmpty() ) {
                LOG.error( "Null or empty value passed for check field isAssign in assignOrUnassignBranchAdmin()" );
                throw new InvalidInputException( "Null or empty value passed for user id in assignOrUnassignBranchAdmin()" );
            }

            long branchId = 0l;
            long userId = 0l;
            try {
                branchId = Long.parseLong( branch );
                userId = Long.parseLong( userToAssign );
                if ( isAssign.equalsIgnoreCase( CommonConstants.IS_ASSIGN_ADMIN ) )
                    // Assigns the given user as branch admin
                    userManagementService.assignBranchAdmin( admin, branchId, userId );
                else if ( isAssign.equalsIgnoreCase( CommonConstants.IS_UNASSIGN_ADMIN ) )
                    // Unassigns the given user as branch admin
                    userManagementService.unassignBranchAdmin( admin, branchId, userId );
            } catch ( NumberFormatException e ) {
                LOG.error( "Number format exception while parsing branch Id or user id", e );
                throw new NonFatalException( "Number format execption while parsing branch Id or user id",
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }
        }
        // TODO add success message.
        catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException while trying to assign or unassign a user to branch. Reason : "
                + nonFatalException.getMessage(), nonFatalException );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( nonFatalException.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
        }
        LOG.info( "Successfully completed method to assign or unassign branch admin" );
        return JspResolver.MESSAGE_HEADER;
    }


    // JIRA SS-42 BY RM05 EOC
    /**
     * Method to assign a user as region admin.
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/assignregionadmin", method = RequestMethod.POST)
    public String assignRegionAdmin( Model model, HttpServletRequest request )
    {
        LOG.info( "Method to assign region admin called" );

        try {
            User admin = sessionHelper.getCurrentUser();
            if ( admin == null ) {
                LOG.error( "No user found in session" );
                throw new InvalidInputException( "No user found in session", DisplayMessageConstants.NO_USER_IN_SESSION );
            }
            String region = request.getParameter( "regionId" );
            String userToAssign = request.getParameter( "userId" );

            if ( region == null || region.isEmpty() ) {
                LOG.error( "Null or empty value passed for region id in assignRegionAdmin()" );
                throw new InvalidInputException( "Null or empty value passed for region id in assignRegionAdmin()" );
            }
            if ( userToAssign == null || userToAssign.isEmpty() ) {
                LOG.error( "Null or empty value passed for user id in assignRegionAdmin()" );
                throw new InvalidInputException( "Null or empty value passed for user id in assignRegionAdmin()" );
            }

            long regionId = 0l;
            long userId = 0l;

            try {
                regionId = Long.parseLong( region );
                userId = Long.parseLong( userToAssign );
            } catch ( NumberFormatException e ) {
                LOG.error( "Number format exception while parsing region Id or user id", e );
                throw new NonFatalException( "Number format execption while parsing region Id or user id",
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }
            try {
                User assigneeUser = userManagementService.getUserByUserId( userId );
                organizationManagementService.assignRegionToUser( admin, regionId, assigneeUser, true );
            } catch ( InvalidInputException | NoRecordsFetchedException | SolrException e ) {
                LOG.error( "Exception while assigning user as region admin.Reason:" + e.getMessage(), e );
                throw new NonFatalException( "Exception while assigning user as region admin.Reason:" + e.getMessage(),
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }
        }
        // TODO add success message.
        catch ( NonFatalException e ) {
            LOG.error( "Exception occured while assigning region admin. Reason : " + e.getMessage(), e );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
        }
        LOG.info( "Successfully completed method to assign region admin" );
        return JspResolver.MESSAGE_HEADER;
    }


    /**
     * Method to remove a region admin.
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/unassignregionadmin", method = RequestMethod.POST)
    public String unassignRegionAdmin( Model model, HttpServletRequest request )
    {

        LOG.info( "Method to remove region admin called" );

        try {
            User admin = sessionHelper.getCurrentUser();
            if ( admin == null ) {
                LOG.error( "No user found in session" );
                throw new InvalidInputException( "No user found in session", DisplayMessageConstants.NO_USER_IN_SESSION );
            }
            String region = request.getParameter( "regionId" );
            String userIdToRemove = request.getParameter( "userId" );

            if ( region == null || region.isEmpty() ) {
                LOG.error( "Null or empty value passed for region id in removeRegionAdmin()" );
                throw new InvalidInputException( "Null or empty value passed for region id in removeRegionAdmin()" );
            }
            if ( userIdToRemove == null || userIdToRemove.isEmpty() ) {
                LOG.error( "Null or empty value passed for user id in unassignRegionAdmin()" );
                throw new InvalidInputException( "Null or empty value passed for user id in unassignRegionAdmin()" );
            }

            long regionId = 0l;
            long userId = 0l;

            try {
                regionId = Long.parseLong( region );
                userId = Long.parseLong( userIdToRemove );

                // Remove the given user from branch admin.
                userManagementService.unassignRegionAdmin( admin, regionId, userId );
            } catch ( NumberFormatException e ) {
                LOG.error( "Number format exception while parsing region Id or user id", e );
                throw new NonFatalException( "Number format execption while parsing region Id",
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }
        }
        // TODO add success message.
        catch ( NonFatalException e ) {
            LOG.error( "Exception occured while assigning region admin.Reason : " + e.getMessage(), e );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
        }
        LOG.info( "Successfully completed method to remove region admin" );
        return JspResolver.MESSAGE_HEADER;
    }


    /**
     * Method to activate or deactivate a user.
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/updateuser", method = RequestMethod.POST)
    public String updateUser( Model model, HttpServletRequest request )
    {

        LOG.info( "Method to activate or deactivate a user, activateOrDecativateUser() called." );
        try {
            User admin = sessionHelper.getCurrentUser();
            if ( admin == null ) {
                LOG.error( "No user found in session" );
                throw new InvalidInputException( "No user found in session", DisplayMessageConstants.NO_USER_IN_SESSION );
            }
            String isAssign = request.getParameter( "isAssign" );
            if ( isAssign == null || isAssign.isEmpty() ) {
                LOG.error( "Null or empty value passed for check field isAssign in assignOrUnassignBranchAdmin()" );
                throw new InvalidInputException( "Null or empty value passed for user id in assignOrUnassignBranchAdmin()" );
            }
            long userIdToUpdate = 0;
            try {
                userIdToUpdate = Long.parseLong( request.getParameter( "userIdToUpdate" ) );
            } catch ( NumberFormatException e ) {
                LOG.error( "Number format exception while parsing user Id", e );
                throw new NonFatalException( "Number format execption while parsing user id",
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }
            if ( userIdToUpdate < 0 ) {
                LOG.error( "Invalid user Id found to update in updateUser()." );
                throw new InvalidInputException( "Invalid user Id found to update in updateUser()." );
            }

            try {
                if ( isAssign.equalsIgnoreCase( "YES" ) )
                    // Set the given user as active.
                    userManagementService.updateUser( admin, userIdToUpdate, true );
                else if ( isAssign.equalsIgnoreCase( "NO" ) )
                    // Set the given user as inactive.
                    userManagementService.updateUser( admin, userIdToUpdate, false );
            } catch ( InvalidInputException e ) {
                throw new InvalidInputException( e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e );
            }
            model.addAttribute( "message", messageUtils.getDisplayMessage(
                DisplayMessageConstants.USER_STATUS_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE ) );
        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException while removing user. Reason : " + nonFatalException.getMessage(), nonFatalException );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( nonFatalException.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
        }
        LOG.info( "Method to activate or deactivate a user, updateUser() finished." );
        return JspResolver.MESSAGE_HEADER;
    }


    /**
     * Method to show registration completion page to the user. User can update first name, last
     * name here. User must update the password for completion of registration.
     * 
     * @param model
     * @param request
     * @return
     * @throws InvalidInputException
     */
    @RequestMapping ( value = "/showcompleteregistrationpage", method = RequestMethod.GET)
    public String showCompleteRegistrationPage( HttpServletRequest request, @RequestParam ( "q") String encryptedUrlParams,
        Model model, RedirectAttributes redirectAttributes )
    {
        LOG.info( "Method showCompleteRegistrationPage() to complete registration of user started." );

        // Check for existing session
        if ( sessionHelper.isUserActiveSessionExists() ) {
            LOG.info( "Existing Active Session detected" );

            // Invalidate session in browser
            request.getSession( false ).invalidate();
            SecurityContextHolder.clearContext();
        }

        try {
            Map<String, String> urlParams = null;
            try {
                urlParams = urlGenerator.decryptParameters( encryptedUrlParams );
            } catch ( InvalidInputException e ) {
                LOG.error( "Invalid Input exception in decrypting url parameters in showCompleteRegistrationPage(). Reason "
                    + e.getMessage(), e );
                throw new InvalidInputException( e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e );
            }

            // fetching details from urlparams
            long companyId;
            try {
                companyId = Long.parseLong( urlParams.get( CommonConstants.COMPANY ) );
            } catch ( NumberFormatException e ) {
                throw new NonFatalException( e.getMessage(), DisplayMessageConstants.INVALID_REGISTRATION_INVITE, e );
            }

            // checking status of user
            String emailId = urlParams.get( CommonConstants.EMAIL_ID );
            User newUser = null;
            try {
                newUser = userManagementService.getUserByEmailAndCompany( companyId, emailId );
            } catch ( NoRecordsFetchedException e ) {
                throw new NonFatalException( "No users found with the login name : {}",
                    DisplayMessageConstants.USER_LINK_EXPIRED );
            }

            if ( newUser.getStatus() == CommonConstants.STATUS_NOT_VERIFIED ) {
                redirectAttributes.addFlashAttribute( CommonConstants.COMPANY, urlParams.get( CommonConstants.COMPANY ) );
                redirectAttributes.addFlashAttribute( CommonConstants.FIRST_NAME, urlParams.get( CommonConstants.FIRST_NAME ) );
                redirectAttributes.addFlashAttribute( CommonConstants.EMAIL_ID, emailId );
                redirectAttributes.addFlashAttribute( "q", encryptedUrlParams );

                User user = userManagementService.getUserByEmail( emailId );
                AgentSettings agentSettings = userManagementService.getAgentSettingsForUserProfiles( user.getUserId() );
                if ( agentSettings == null ) {
                    throw new InvalidInputException( "Settings not found for the given user." );
                }
                redirectAttributes.addFlashAttribute( "profileUrl", agentSettings.getCompleteProfileUrl() );

                String lastName = urlParams.get( CommonConstants.LAST_NAME );
                if ( lastName != null && !lastName.isEmpty() ) {
                    redirectAttributes.addFlashAttribute( CommonConstants.LAST_NAME, lastName );
                }
                LOG.debug( "Validation of url completed. Service returning params to be prepopulated in registration page" );
            } else {
                LOG.debug( "The registration url had been used earlier" );
                redirectAttributes.addFlashAttribute( "message", "The registration url is no longer valid" );
                redirectAttributes.addFlashAttribute( "status", DisplayMessageType.ERROR_MESSAGE );
                return "redirect:/" + JspResolver.LOGIN + ".do";
            }
            LOG.info( "Method showCompleteRegistrationPage() to complete registration of user finished." );
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException in showCompleteRegistrationPage(). Reason : " + e.getMessage(), e );
            return JspResolver.LINK_EXPIRED;
        }

        return "redirect:/" + JspResolver.COMPLETE_REGISTRATION_PAGE + ".do";
    }


    @RequestMapping ( value = "/completeregistrationpage")
    public String initCompleteRegistrationPage()
    {
        LOG.info( "CompleteRegistration Page started" );
        return JspResolver.COMPLETE_REGISTRATION;
    }


    /**
     * Method to complete registration of the user.
     * 
     * @param model
     * @param request
     * @return
     * @throws InvalidInputException
     */
    @RequestMapping ( value = "/completeregistration", method = RequestMethod.POST)
    public String completeRegistration( HttpServletRequest request, RedirectAttributes redirectAttributes )
    {
        LOG.info( "Method completeRegistration() to complete registration of user started." );
        User user = null;

        try {
            String firstName = request.getParameter( CommonConstants.FIRST_NAME );
            String lastName = request.getParameter( CommonConstants.LAST_NAME );
            String emailId = request.getParameter( CommonConstants.EMAIL_ID );
            String password = request.getParameter( "password" );
            String confirmPassword = request.getParameter( "confirmPassword" );
            String companyIdStr = request.getParameter( "companyId" );

            if ( firstName != null && !firstName.equals( "" ) ) {
                firstName = firstName.trim();
            }

            if ( lastName != null && !lastName.equals( "" ) ) {
                lastName = lastName.trim();
            }

            // form parameters validation
            validateCompleteRegistrationForm( firstName, lastName, emailId, password, companyIdStr, confirmPassword );

            // Decrypting URL parameters
            Map<String, String> urlParams = new HashMap<>();
            try {
                String encryptedUrlParameters = request.getParameter( "q" );
                urlParams = urlGenerator.decryptParameters( encryptedUrlParameters );
            } catch ( InvalidInputException e ) {
                throw new InvalidInputException( e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e );
            }

            // check if email address entered matches with the one in the encrypted url
            if ( !urlParams.get( "emailId" ).equalsIgnoreCase( emailId ) ) {
                LOG.error(
                    "Invalid Input exception. Reason emailId entered does not match with the one to which the mail was sent" );
                throw new InvalidInputException( "Invalid Input exception", DisplayMessageConstants.INVALID_EMAILID );
            }

            long companyId = 0;
            try {
                companyId = Long.parseLong( companyIdStr );
            } catch ( NumberFormatException e ) {
                throw new InvalidInputException( "NumberFormat exception parsing companyId. Reason : " + e.getMessage(),
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }

            try {
                OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( companyId );
                redirectAttributes.addFlashAttribute( "hiddenSection", companySettings.isHiddenSection() );
            } catch ( InvalidInputException e ) {
                throw new InvalidInputException( "Invalid Input exception occured in method getCompanySettings()",
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }

            try {
                // fetch user object with email Id
                user = authenticationService.getUserWithLoginNameAndCompanyId( emailId, companyId );

                // calling service to update user details on registration
                user = userManagementService.updateUserOnCompleteRegistration( user, emailId, companyId, firstName, lastName,
                    password );
            } catch ( InvalidInputException e ) {
                throw new InvalidInputException( e.getMessage(), DisplayMessageConstants.USER_NOT_PRESENT, e );
            }

            //get agent settings
            AgentSettings agentSettings = null;
            try {
                agentSettings = organizationManagementService.getAgentSettings( user.getUserId() );
            } catch ( NoRecordsFetchedException e ) {
                throw new InvalidInputException( "No settings found for user", DisplayMessageConstants.GENERAL_ERROR );
            }

            //check if login is prevented for user
            if ( agentSettings.isLoginPrevented() ) {
                SecurityContextHolder.clearContext();
                return JspResolver.LOGIN_DISABLED_PAGE;
            }

            LOG.debug( "Adding newly registered user to principal session" );
            sessionHelper.loginOnRegistration( emailId, password );
            LOG.debug( "Successfully added registered user to principal session" );

            AccountType accountType = null;
            HttpSession session = request.getSession( true );
            List<LicenseDetail> licenseDetails = user.getCompany().getLicenseDetails();
            if ( licenseDetails != null && !licenseDetails.isEmpty() ) {
                LicenseDetail licenseDetail = licenseDetails.get( 0 );
                accountType = AccountType.getAccountType( licenseDetail.getAccountsMaster().getAccountsMasterId() );
                LOG.debug( "Adding account type in session" );
                session.setAttribute( CommonConstants.ACCOUNT_TYPE_IN_SESSION, accountType );
            } else {
                LOG.debug( "License details not found for the user's company" );
            }

            // updating the flags for user profiles
            if ( user.getIsAtleastOneUserprofileComplete() == CommonConstants.PROCESS_COMPLETE ) {
                // get the user's canonical settings
                LOG.info( "Fetching the user's canonical settings and setting it in session" );
                sessionHelper.getCanonicalSettings( session );
                sessionHelper.setSettingVariablesInSession( session );
                LOG.debug( "Updating user count modification notification" );
                userManagementService.updateUserCountModificationNotification( user.getCompany() );
            } else {
                LOG.info( "No User profile present" );
                return "redirect:/" + JspResolver.NO_ACTIVE_PROFILES + ".do";
            }

            // Setting session variable to show linkedin signup and sendsurvey popups only once
            String popupStatus = (String) session.getAttribute( CommonConstants.POPUP_FLAG_IN_SESSION );
            if ( popupStatus == null ) {
                session.setAttribute( CommonConstants.POPUP_FLAG_IN_SESSION, CommonConstants.YES_STRING );
            } else if ( popupStatus.equals( CommonConstants.YES_STRING ) ) {
                session.setAttribute( CommonConstants.POPUP_FLAG_IN_SESSION, CommonConstants.NO_STRING );
            }

            // updating session with signup path params
            LOG.debug( "Updating session with selected user profile if not set" );
            boolean showLinkedInPopup = false;
            boolean showSendSurveyPopup = false;
            List<UserProfile> profiles = userManagementService.getAllUserProfilesForUser( user );
            for ( UserProfile profile : profiles ) {
                if ( profile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID ) {
                    showLinkedInPopup = true;
                    showSendSurveyPopup = true;
                    break;
                }
            }
            redirectAttributes.addFlashAttribute( "showLinkedInPopup", String.valueOf( showLinkedInPopup ) );
            redirectAttributes.addFlashAttribute( "showSendSurveyPopup", String.valueOf( showSendSurveyPopup ) );

            // updating session with aggregated user profiles, if not set
            sessionHelper.processAssignments( session, user );

            // update the last login time and number of logins
            userManagementService.updateUserLoginTimeAndNum( user );
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while setting new Password. Reason : " + e.getMessage(), e );
            redirectAttributes.addFlashAttribute( "message",
                messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
            return "redirect:/" + JspResolver.COMPLETE_REGISTRATION_PAGE + ".do";
        }

        LOG.info( "Method completeRegistration() to complete registration of user finished." );
        return "redirect:/" + JspResolver.LANDING + ".do";
    }


    @RequestMapping ( value = "/showlinkedindatacompare")
    public String showLinkedInDataCompare( Model model, HttpServletRequest request )
    {
        LOG.info( "Method showLinkedInDataCompare() called" );
        HttpSession session = request.getSession( false );

        User user = sessionHelper.getCurrentUser();
        AccountType accountType = (AccountType) session.getAttribute( CommonConstants.ACCOUNT_TYPE_IN_SESSION );
        UserSettings userSettings = (UserSettings) session.getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );

        long branchId = 0;
        long regionId = 0;
        long companyId = 0;
        long agentId = 0;
        OrganizationUnitSettings profileSettings = null;
        Map<SettingsForApplication, OrganizationUnit> map = null;

        //Get the hierarchy details associated with the current profile
        try {
            Map<String, Long> hierarchyDetails = profileManagementService.getHierarchyDetailsByEntity( entityType, entityId );
            if ( hierarchyDetails == null ) {
                LOG.error( "Unable to fetch primary profile for this user " );
                throw new FatalException(
                    "Unable to fetch primary profile for type : " + entityType + " and ID : " + entityId );
            }
            branchId = hierarchyDetails.get( CommonConstants.BRANCH_ID_COLUMN );
            regionId = hierarchyDetails.get( CommonConstants.REGION_ID_COLUMN );
            companyId = hierarchyDetails.get( CommonConstants.COMPANY_ID_COLUMN );
            agentId = hierarchyDetails.get( CommonConstants.AGENT_ID_COLUMN );
            LOG.debug( "Company ID : " + companyId + " Region ID : " + regionId + " Branch ID : " + branchId + " Agent ID : "
                + agentId );
        } catch ( InvalidInputException | ProfileNotFoundException e ) {
            LOG.error( "InvalidInputException while showing profile page. Reason :" + e.getMessage(), e );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
        }

        int profilesMaster = 0;
        if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
            model.addAttribute( "columnName", entityType );
            profilesMaster = CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID;

            OrganizationUnitSettings companyProfile = null;
            try {
                companyProfile = organizationManagementService.getCompanySettings( companyId );
            } catch ( InvalidInputException e ) {
                LOG.error( "Error occured while fetching company profile", e );
            }
            profileSettings = companyProfile;

        } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
            model.addAttribute( "columnName", entityType );
            profilesMaster = CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID;

            OrganizationUnitSettings regionProfile = null;
            OrganizationUnitSettings companyProfile = null;
            try {
                companyProfile = organizationManagementService.getCompanySettings( companyId );
                regionProfile = organizationManagementService.getRegionSettings( regionId );

                try {
                    map = profileManagementService.getPrimaryHierarchyByEntity( CommonConstants.REGION_ID,
                        regionProfile.getIden() );
                    if ( map == null ) {
                        LOG.error( "Unable to fetch primary profile for this user " );
                        throw new FatalException( "Unable to fetch primary profile this user " + regionProfile.getIden() );
                    }
                } catch ( InvalidSettingsStateException | ProfileNotFoundException e ) {
                    LOG.error( "Error occured while fetching region profile", e );
                }
                regionProfile = profileManagementService.fillUnitSettings( regionProfile,
                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, companyProfile, regionProfile, null, null,
                    map, true );
            } catch ( InvalidInputException e ) {
                LOG.error( "Error occured while fetching region profile", e );
            }
            profileSettings = regionProfile;

        } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
            model.addAttribute( "columnName", entityType );
            profilesMaster = CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID;

            OrganizationUnitSettings companyProfile = null;
            OrganizationUnitSettings branchProfile = null;
            OrganizationUnitSettings regionProfile = null;

            try {
                companyProfile = organizationManagementService.getCompanySettings( companyId );
                regionProfile = organizationManagementService.getRegionSettings( regionId );
                branchProfile = organizationManagementService.getBranchSettingsDefault( branchId );

                try {
                    map = profileManagementService.getPrimaryHierarchyByEntity( CommonConstants.BRANCH_ID_COLUMN,
                        branchProfile.getIden() );
                    if ( map == null ) {
                        LOG.error( "Unable to fetch primary profile for this user " );
                        throw new FatalException( "Unable to fetch primary profile this user " + branchProfile.getIden() );
                    }

                } catch ( InvalidSettingsStateException | ProfileNotFoundException e ) {
                    LOG.error( "Error occured while fetching branch profile", e );
                }
                branchProfile = profileManagementService.fillUnitSettings( branchProfile,
                    MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, companyProfile, regionProfile,
                    branchProfile, null, map, true );
            } catch ( InvalidInputException e ) {
                LOG.error( "Error occured while fetching branch profile", e );
            } catch ( NoRecordsFetchedException e ) {
                LOG.error( "NoRecordsFetchedException: message : " + e.getMessage(), e );
            }
            profileSettings = branchProfile;
        } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
            model.addAttribute( "columnName", CommonConstants.AGENT_ID_COLUMN );
            profilesMaster = CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID;

            OrganizationUnitSettings companyProfile = null;
            OrganizationUnitSettings regionProfile = null;
            OrganizationUnitSettings branchProfile = null;
            AgentSettings individualProfile = null;

            try {
                companyProfile = organizationManagementService.getCompanySettings( companyId );
                regionProfile = organizationManagementService.getRegionSettings( regionId );
                branchProfile = organizationManagementService.getBranchSettingsDefault( branchId );
                individualProfile = userManagementService.getAgentSettingsForUserProfiles( agentId );

                try {
                    map = profileManagementService.getPrimaryHierarchyByEntity( CommonConstants.AGENT_ID_COLUMN,
                        individualProfile.getIden() );
                    if ( map == null ) {
                        LOG.error( "Unable to fetch primary profile for this user " );
                        throw new FatalException( "Unable to fetch primary profile this user " + branchProfile.getIden() );
                    }

                } catch ( InvalidSettingsStateException | ProfileNotFoundException e ) {
                    LOG.error( "Error occured while fetching branch profile" + e.getMessage() );
                }

                if ( map == null ) {
                    LOG.error( "Unable to fetch primary profile for this user " );
                    throw new FatalException( "Unable to fetch primary profile this user " + individualProfile.getIden() );
                }

                individualProfile = (AgentSettings) profileManagementService.fillUnitSettings( individualProfile,
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, companyProfile, regionProfile, branchProfile,
                    individualProfile, map, true );
            } catch ( InvalidInputException e ) {
                LOG.error( "InvalidInputException: message : " + e.getMessage(), e );
            } catch ( NoRecordsFetchedException e ) {
                LOG.error( "NoRecordsFetchedException: message : " + e.getMessage(), e );
            }
            profileSettings = individualProfile;
        }

        model.addAttribute( "profileSettings", profileSettings );
        session.setAttribute( CommonConstants.USER_PROFILE_SETTINGS, profileSettings );

        // Setting parentLock in session
        LockSettings parentLock = null;
        try {
            parentLock = profileManagementService.fetchHierarchyLockSettings( companyId, branchId, regionId, entityType );
        } catch ( NonFatalException e ) {
            LOG.error( "Unable to fetch lock values", e );
        }
        session.setAttribute( CommonConstants.PARENT_LOCK, parentLock );

        LOG.info( "Method showLinkedInDataCompare() finished" );
        return JspResolver.LINKEDIN_COMPARE;
    }


    @ResponseBody
    @RequestMapping ( value = "/fetchuploadedprofileimage", method = RequestMethod.GET)
    public String fetchProfileImage( Model model, HttpServletRequest request )
    {
        LOG.info( "Fetching profile image" );
        OrganizationUnitSettings profileSettings = (OrganizationUnitSettings) request.getSession( false )
            .getAttribute( CommonConstants.USER_PROFILE_SETTINGS );
        return profileSettings.getProfileImageUrlThumbnail();
    }


    /**
     * Method to validate complete registration formF
     * 
     * @param firstName
     * @param lastName
     * @param emailId
     * @param password
     * @param companyIdStr
     * @param confirmPassword
     * @throws InvalidInputException
     */
    private void validateCompleteRegistrationForm( String firstName, String lastName, String emailId, String password,
        String companyIdStr, String confirmPassword ) throws InvalidInputException
    {
        LOG.debug( "Method validateCompleteRegistrationForm called" );
        if ( firstName == null || firstName.isEmpty() || !firstName.trim().matches( CommonConstants.FIRST_NAME_REGEX ) ) {
            LOG.error( "First name invalid" );
            throw new InvalidInputException( "First name invalid", DisplayMessageConstants.INVALID_FIRSTNAME );
        }
        if ( lastName != null && !lastName.isEmpty() && !lastName.matches( CommonConstants.LAST_NAME_REGEX ) ) {
            LOG.error( "Last name invalid" );
            throw new InvalidInputException( "Last name invalid", DisplayMessageConstants.INVALID_LASTNAME );
        }
        if ( emailId == null || emailId.isEmpty() || !organizationManagementService.validateEmail( emailId ) ) {
            LOG.error( "EmailId not valid" );
            throw new InvalidInputException( "EmailId not valid", DisplayMessageConstants.INVALID_EMAILID );
        }
        if ( password == null || password.isEmpty() || password.length() < CommonConstants.PASSWORD_LENGTH ) {
            LOG.error( "Password passed was invalid" );
            throw new InvalidInputException( "Password passed was invalid", DisplayMessageConstants.INVALID_PASSWORD );
        }
        if ( companyIdStr == null || companyIdStr.isEmpty() ) {
            LOG.error( "Company Id passed was null or empty" );
            throw new InvalidInputException( "Company Id passed was null or empty",
                DisplayMessageConstants.INVALID_COMPANY_NAME );
        }
        if ( confirmPassword == null || confirmPassword.isEmpty() ) {
            LOG.error( "Confirm password passed was null or empty" );
            throw new InvalidInputException( "Confirm password passed was null or empty",
                DisplayMessageConstants.INVALID_PASSWORD );
        }
        // check if password and confirm password field match
        if ( !password.equals( confirmPassword ) ) {
            LOG.error( "Password and confirm password fields do not match" );
            throw new InvalidInputException( "Password and confirm password fields do not match",
                DisplayMessageConstants.PASSWORDS_MISMATCH );
        }
        LOG.debug( "Method validateCompleteRegistrationForm executed successfully" );
    }


    @RequestMapping ( value = "/showchangepasswordpage")
    public String showChangePasswordPage()
    {
        return JspResolver.CHANGE_PASSWORD;
    }


    // JIRA SS-77 BY RM07 BOC
    /**
     * Method to change password
     */
    @RequestMapping ( value = "/changepassword", method = RequestMethod.POST)
    public String changePassword( Model model, HttpServletRequest request )
    {
        LOG.info( "change the password" );
        User user = sessionHelper.getCurrentUser();

        try {
            String oldPassword = request.getParameter( "oldpassword" );
            String newPassword = request.getParameter( "newpassword" );
            String confirmNewPassword = request.getParameter( "confirmnewpassword" );
            validateChangePasswordFormParameters( oldPassword, newPassword, confirmNewPassword );

            // check if old password entered matches with the one in the encrypted
            try {
                LOG.debug( "Calling authentication service to validate user while changing password" );
                authenticationService.validateUser( user, oldPassword );
                LOG.debug( "Successfully executed authentication service to validate user" );
            } catch ( InvalidInputException e ) {
                LOG.error( "Invalid Input exception in validating User. Reason " + e.getMessage(), e );
                throw new InvalidInputException( e.getMessage(), DisplayMessageConstants.INVALID_PASSWORD, e );
            }

            // change user's password
            authenticationService.changePassword( user, newPassword );
            LOG.info( "change user password executed successfully" );

            model.addAttribute( "status", DisplayMessageType.SUCCESS_MESSAGE );
            model.addAttribute( "message", messageUtils.getDisplayMessage( DisplayMessageConstants.PASSWORD_CHANGE_SUCCESSFUL,
                DisplayMessageType.SUCCESS_MESSAGE ) );
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while changing password. Reason : " + e.getMessage(), e );
            model.addAttribute( "status", DisplayMessageType.ERROR_MESSAGE );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
        }
        return JspResolver.CHANGE_PASSWORD;
    }


    @RequestMapping ( value = "/finduserassignments", method = RequestMethod.GET)
    public String getUserAssignments( Model model, HttpServletRequest request )
    {
        LOG.info( "Method getUserAssignments() called from UserManagementController" );
        HttpSession session = request.getSession();

        try {
            long userId = 0l;
            try {
                userId = Long.parseLong( request.getParameter( "userId" ) );
            } catch ( NumberFormatException e ) {
                throw new InvalidInputException( "NumberFormatException while parsing userId.Reason: " + e.getMessage(),
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }
            User user = null;
            AgentSettings agentSettings = null;
            try {
                user = userManagementService.getUserByUserId( userId );
                agentSettings = userManagementService.getUserSettings( userId );
            } catch ( InvalidInputException e ) {
                throw new InvalidInputException( "InvalidInputException while getting user.Reason: " + e.getMessage(),
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }

            //partner survey details
            model.addAttribute( "partnerSurveyAllowedForCompany",
                organizationManagementService.isPartnerSurveyAllowedForComapny( user.getCompany().getCompanyId() ) );
            model.addAttribute( "partnerSurveyAllowedForUser", agentSettings.isAllowPartnerSurvey() );


            //user assignments
            UserHierarchyAssignments assignments = (UserHierarchyAssignments) session
                .getAttribute( CommonConstants.USER_ASSIGNMENTS );
            Map<Long, String> regions = assignments.getRegions();
            Map<Long, String> branches = assignments.getBranches();

            String branchName = "";
            String regionName = "";
            List<UserAssignment> userAssignments = new ArrayList<UserAssignment>();
            for ( UserProfile userProfile : user.getUserProfiles() ) {
                // Check if profile is complete
                if ( userProfile.getIsProfileComplete() != CommonConstants.PROCESS_COMPLETE
                    || userProfile.getStatus() != CommonConstants.STATUS_ACTIVE ) {
                    continue;
                }
                UserAssignment assignment = new UserAssignment();
                assignment.setUserId( user.getUserId() );
                assignment.setProfileId( userProfile.getUserProfileId() );
                assignment.setStatus( userProfile.getStatus() );

                long regionId;
                long branchId;
                int profileMaster = userProfile.getProfilesMaster().getProfileId();
                switch ( profileMaster ) {
                    case CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID:
                        regionId = userProfile.getRegionId();
                        if ( regionId == 0l ) {
                            continue;
                        }

                        // if region is not default
                        regionName = regions.get( regionId );
                        if ( regionName != null ) {
                            assignment.setEntityId( regionId );
                            assignment.setEntityName( regionName );
                        }
                        // if region is default
                        else {
                            continue;
                        }
                        assignment.setRole( ROLE_ADMIN );

                        break;

                    case CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID:
                        branchId = userProfile.getBranchId();
                        if ( branchId == 0l ) {
                            continue;
                        }

                        // if branch is not default
                        branchName = branches.get( branchId );
                        if ( branchName != null ) {
                            assignment.setEntityId( branchId );
                            assignment.setEntityName( branchName );
                        }
                        // if branch is default
                        else {
                            continue;
                        }
                        assignment.setRole( ROLE_ADMIN );

                        break;

                    case CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID:
                        branchId = userProfile.getBranchId();
                        if ( branchId == 0l ) {
                            continue;
                        }

                        // if branch is not default
                        branchName = branches.get( branchId );
                        if ( branchName != null ) {
                            assignment.setEntityId( branchId );
                            assignment.setEntityName( branchName );
                        }
                        // if branch is default
                        else {
                            regionId = userProfile.getRegionId();
                            if ( regionId == 0l ) {
                                continue;
                            }

                            // if region is not default
                            regionName = regions.get( regionId );
                            if ( regionName != null ) {
                                assignment.setEntityId( regionId );
                                assignment.setEntityName( regionName );
                            }
                            // if region is default
                            else {
                                assignment.setEntityId( user.getCompany().getCompanyId() );
                                assignment.setEntityName( user.getCompany().getCompany() );
                            }
                        }
                        assignment.setRole( ROLE_USER );
                        break;

                    default:
                }
                userAssignments.add( assignment );
            }

            // set the request parameters in model
            model.addAttribute( "firstName", user.getFirstName() );
            model.addAttribute( "lastName", user.getLastName() );
            model.addAttribute( "emailId", user.getEmailId() );
            model.addAttribute( "userId", user.getUserId() );
            model.addAttribute( "status", user.getStatus() );

            // returning in descending order
            Collections.reverse( userAssignments );
            model.addAttribute( "profiles", userAssignments );
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while finding user assignments Reason : " + e.getMessage(), e );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
            return JspResolver.MESSAGE_HEADER;
        }

        LOG.info( "Method getUserAssignments() finished from UserManagementController" );
        return JspResolver.USER_MANAGEMENT_EDIT_USER_DETAILS;
    }


    @ResponseBody
    @RequestMapping ( value = "/updateuserbyadmin", method = RequestMethod.POST)
    public String updateUserByAdmin( Model model, HttpServletRequest request )
    {
        LOG.info( "Method updateUserByAdmin() called from UserManagementController" );
        String userIdStr = request.getParameter( "userId" );
        String firstName = request.getParameter( "firstName" );
        String lastName = request.getParameter( "lastName" );
        String emailId = request.getParameter( "emailId" );

        if ( firstName != null && firstName != "" ) {
            firstName = replaceQuoteInString( firstName );
        }

        if ( lastName != null && lastName != "" ) {
            lastName = replaceQuoteInString( lastName );
        }


        String fullName = firstName;
        if ( lastName != null && lastName != "" ) {
            fullName += " " + lastName;
        }


        try {
            long userId = 0;
            try {
                userId = Long.parseLong( userIdStr );
            } catch ( NumberFormatException e ) {
                LOG.error( "NumberFormatException while parsing user id. Reason : " + e.getMessage(), e );
                throw e;
            }

            // Check for email in DB for other users
            try {
                User existingUser = userManagementService.getUserByEmailAddress( emailId );
                if ( existingUser != null && existingUser.getUserId() != userId ) {
                    return "Email address is already in use";
                }
            } catch ( InvalidInputException | NoRecordsFetchedException e ) {
                LOG.warn( "No users found with given emailId" );
            }

            // Update AgentSetting in MySQL
            User user = userManagementService.getUserByUserId( userId );
            user.setFirstName( firstName );
            user.setLastName( lastName );
            user.setEmailId( emailId );
            user.setLoginName( emailId );
            user.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );

            // update in solr
            Map<String, Object> userMap = new HashMap<>();
            userMap.put( CommonConstants.USER_DISPLAY_NAME_SOLR, fullName );
            userMap.put( CommonConstants.USER_FIRST_NAME_SOLR, firstName );
            userMap.put( CommonConstants.USER_LAST_NAME_SOLR, lastName );
            userMap.put( CommonConstants.USER_EMAIL_ID_SOLR, emailId );
            userMap.put( CommonConstants.USER_LOGIN_NAME_SOLR, emailId );
            userManagementService.updateUser( user, userMap );

            // Update AgentSetting in MongoDB
            AgentSettings agentSettings = userManagementService.getAgentSettingsForUserProfiles( user.getUserId() );
            ContactDetailsSettings contactDetails = agentSettings.getContact_details();
            contactDetails.setFirstName( firstName );
            contactDetails.setLastName( lastName );
            contactDetails.setName( fullName );
            contactDetails.getMail_ids().setWork( emailId );
            if ( user.getStatus() == CommonConstants.STATUS_ACCOUNT_DISABLED ) {
                String profileName = userManagementService.generateIndividualProfileName( user.getUserId(),
                    contactDetails.getName(), user.getEmailId() );
                agentSettings.setProfileName( profileName );

                String profileUrl = utils.generateAgentProfileUrl( profileName );
                agentSettings.setProfileUrl( profileUrl );
                userManagementService.sendRegistrationCompletionLink( emailId, firstName, lastName,
                    user.getCompany().getCompanyId(), profileName, user.getLoginName(), false );

                // Update the profile pic URL
                organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
                    MongoOrganizationUnitSettingDaoImpl.KEY_PROFILE_URL, agentSettings.getProfileUrl(), agentSettings,
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );

                organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
                    MongoOrganizationUnitSettingDaoImpl.KEY_PROFILE_NAME, agentSettings.getProfileName(), agentSettings,
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );
            }

            profileManagementService.updateContactDetails( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION,
                agentSettings, contactDetails );
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException caught in updateUserByAdmin(). Nested exception is ", e );
            return e.getMessage();
        }

        LOG.info( "Method updateUserByAdmin() finished from UserManagementController" );
        return "User details updated successfully";
    }


    @ResponseBody
    @RequestMapping ( value = "/updateuserprofile", method = RequestMethod.POST)
    public String updateUserProfile( Model model, HttpServletRequest request )
    {
        LOG.info( "Method updateUserProfile() called from UserManagementController" );
        Map<String, String> statusMap = new HashMap<String, String>();
        String message = "";

        try {
            User user = sessionHelper.getCurrentUser();
            long profileId = Long.parseLong( request.getParameter( "profileId" ) );
            int status = Integer.parseInt( request.getParameter( "status" ) );

            userManagementService.updateUserProfile( user, profileId, status );
            userManagementService.updateUserProfilesStatus( user, profileId );

            message = messageUtils
                .getDisplayMessage( DisplayMessageConstants.PROFILE_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE )
                .getMessage();
            statusMap.put( "status", CommonConstants.SUCCESS_ATTRIBUTE );

            // update user profiles in session if current user
            User updatedUser = userManagementService.getUserByProfileId( profileId );
            if ( user.getUserId() == updatedUser.getUserId() ) {
                try {
                    sessionHelper.processAssignments( request.getSession( false ), user );
                } catch ( NonFatalException e ) {
                    LOG.error( "NonFatalException while logging in. Reason : " + e.getMessage(), e );
                }
            }
        } catch ( NumberFormatException e ) {
            LOG.error( "NumberFormatException while parsing profileId. Reason : " + e.getMessage(), e );
            statusMap.put( "status", CommonConstants.ERROR );
            message = messageUtils.getDisplayMessage( e.getMessage(), DisplayMessageType.ERROR_MESSAGE ).getMessage();
        } catch ( InvalidInputException e ) {
            LOG.error( "InvalidInputException while updating profile. Reason : " + e.getMessage(), e );
            statusMap.put( "status", CommonConstants.ERROR );
            message = messageUtils.getDisplayMessage( e.getMessage(), DisplayMessageType.ERROR_MESSAGE ).getMessage();
        }

        statusMap.put( "message", message );
        LOG.info( "Method updateUserProfile() finished from UserManagementController" );
        return new Gson().toJson( statusMap );
    }


    @ResponseBody
    @RequestMapping ( value = "/deleteuserprofile", method = RequestMethod.POST)
    public String deleteUserProfile( Model model, HttpServletRequest request )
    {
        LOG.info( "Method deleteUserProfile() called from UserManagementController" );
        try {
            try {
                User sessionUser = sessionHelper.getCurrentUser();
                long profileId = Long.parseLong( request.getParameter( "profileId" ) );
                int status = CommonConstants.STATUS_INACTIVE;

                // update user profiles in session if current user
                User updatedUser = userManagementService.getUserByProfileId( profileId );

                List<UserProfile> userprofileList = userManagementService.getAllUserProfilesForUser( updatedUser );
                if ( userprofileList.size() == 1 && userprofileList.get( 0 ).getUserProfileId() == profileId ) {
                    return messageUtils.getDisplayMessage( DisplayMessageConstants.NOT_ABLE_TO_DELETE_USER_PRIFILE,
                        DisplayMessageType.ERROR_MESSAGE ).getMessage();
                }

                userManagementService.updateUserProfile( sessionUser, profileId, status );
                userManagementService.updateUserProfilesStatus( sessionUser, profileId );
                userManagementService.removeUserProfile( profileId );

                //userManagementService.removeUserProfile( profileId );

                userManagementService.updatePrimaryProfileOfUser( updatedUser );
                updatedUser = userManagementService.getUserByUserId( updatedUser.getUserId() );
                userManagementService.updateUserInSolr( updatedUser );


                //move surveys if deleted assignment is an agent assignment and user also has another agent assignment
                organizationManagementService.updateSurveyAssignments( updatedUser, userprofileList, profileId );


                userManagementService.updateUserCountModificationNotification( updatedUser.getCompany() );

                if ( sessionUser.getUserId() == updatedUser.getUserId() ) {
                    try {
                        sessionHelper.processAssignments( request.getSession( false ), sessionUser );
                    } catch ( NonFatalException e ) {
                        throw new NonFatalException(
                            "Exception occurred while processing user assignments in. Reason : " + e.getMessage(), e );
                    }
                }
            } catch ( NumberFormatException e ) {
                throw new NonFatalException( "NumberFormatException while parsing profileId. Reason : " + e.getMessage(), e );
            } catch ( InvalidInputException e ) {
                throw new NonFatalException( "InvalidInputException while deleting profile. Reason : " + e.getMessage(), e );
            }
        } catch ( NonFatalException e ) {
            LOG.error(
                "NonFatalException occurred while deleting UserProfile in UserManagementController. Reason : " + e.getMessage(),
                e );
            return CommonConstants.ERROR;
        }
        LOG.info( "Method deleteUserProfile() finished from UserManagementController" );
        return CommonConstants.SUCCESS_ATTRIBUTE;
    }


    @ResponseBody
    @RequestMapping ( value = "/reinviteuser", method = RequestMethod.GET)
    public String sendInvitationForRegistration( Model model, HttpServletRequest request )
    {
        LOG.info( "Sending invitation to user" );
        Map<String, String> statusMap = new HashMap<String, String>();
        String message = "";
        User user = sessionHelper.getCurrentUser();

        try {
            String emailId = request.getParameter( "emailId" );
            String firstName = request.getParameter( "firstName" );
            String lastName = request.getParameter( "lastName" );

            if ( emailId == null || emailId.isEmpty() ) {
                LOG.warn( "Email id is not present to resend invitation" );
                throw new InvalidInputException( "Invalid email id.", DisplayMessageConstants.INVALID_EMAILID );
            }
            if ( firstName == null || firstName.isEmpty() ) {
                LOG.warn( "First Name is not present to resend invitation" );
                throw new InvalidInputException( "Invalid first name.", DisplayMessageConstants.INVALID_FIRSTNAME );
            }
            if ( lastName == null || lastName.isEmpty() ) {
                lastName = " ";
            }

            LOG.debug( "Sending invitation..." );
            User invitedUser = userManagementService.getUserByEmail( emailId );
            String profileName = userManagementService.getUserSettings( invitedUser.getUserId() ).getProfileName();
            userManagementService.sendRegistrationCompletionLink( emailId, firstName, lastName,
                user.getCompany().getCompanyId(), profileName, invitedUser.getLoginName(), false );

            message = messageUtils
                .getDisplayMessage( DisplayMessageConstants.INVITATION_RESEND_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE )
                .getMessage();
            statusMap.put( "status", CommonConstants.SUCCESS_ATTRIBUTE );
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while reinviting user. Reason : " + e.getMessage(), e );
            statusMap.put( "status", CommonConstants.ERROR );
            message = messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ).getMessage();
        }

        LOG.info( "Invitation sent to user" );
        statusMap.put( "message", message );
        return new Gson().toJson( statusMap );
    }


    @ResponseBody
    @RequestMapping ( value = "/sendverificationmail", method = RequestMethod.GET)
    public String sendVerificationMail( Model model, HttpServletRequest request )
    {
        LOG.info( "Method sendVerificationMail() called from UserManagementController" );
        try {
            User user = sessionHelper.getCurrentUser();
            userManagementService.sendVerificationLink( user );
        } catch ( NonFatalException e ) {
            LOG.error( "InvalidInputException while updating profile. Reason : " + e.getMessage(), e );
            return "Sorry, Something went wrong while sending mail.";
        }
        return "Verification Mail has been sent to your email id. Please click on the link provided to continue.";
    }


    @ResponseBody
    @RequestMapping ( value = "/initialofusername", method = RequestMethod.GET)
    public String getInitialOfUserName( Model model, HttpServletRequest request )
    {
        LOG.info( "Method sendVerificationMail() called from UserManagementController" );
        User user;
        String initial = "";
        user = sessionHelper.getCurrentUser();
        if ( user != null ) {
            if ( user.getFirstName() != null ) {
                initial = user.getFirstName().substring( 0, 1 );
            }
        }
        return initial;
    }


    // verify change password parameters
    private void validateChangePasswordFormParameters( String oldPassword, String newPassword, String confirmNewPassword )
        throws InvalidInputException
    {
        LOG.debug( "Validating change password form paramters" );
        if ( oldPassword == null || oldPassword.isEmpty() || oldPassword.length() < CommonConstants.PASSWORD_LENGTH ) {
            LOG.error( "Invalid old password" );
            throw new InvalidInputException( "Invalid old password", DisplayMessageConstants.INVALID_CURRENT_PASSWORD );
        }
        if ( newPassword == null || newPassword.isEmpty() || newPassword.length() < CommonConstants.PASSWORD_LENGTH ) {
            LOG.error( "Invalid new password" );
            throw new InvalidInputException( "Invalid new password", DisplayMessageConstants.INVALID_NEW_PASSWORD );
        }
        if ( confirmNewPassword == null || confirmNewPassword.isEmpty() ) {
            LOG.error( "Confirm Password can not be null or empty" );
            throw new InvalidInputException( "Confirm Password can not be null or empty",
                DisplayMessageConstants.INVALID_CONFIRM_NEW_PASSWORD );
        }

        // check if new password and confirm new password field match
        if ( !newPassword.equals( confirmNewPassword ) ) {
            LOG.error( "Password and confirm password fields do not match" );
            throw new InvalidInputException( "Password and confirm password fields do not match",
                DisplayMessageConstants.PASSWORDS_MISMATCH );
        }
        LOG.debug( "change password form parameters validated successfully" );
    }


    private String replaceQuoteInString( String str )
    {
        if ( str.contains( "\"" ) ) {
            str = str.replace( "\"", "&quot;" );
        }
        return str;
    }


    /**
     * 
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/fetchuserprofileflags", method = RequestMethod.GET)
    public String fetchUserProfileFlags( HttpServletRequest request )
    {
        LOG.info( "Method fetchUserProfileFlags() started." );
        String userIdStr = request.getParameter( "userId" );
        Map<String, String> response = new HashMap<>();

        response.put( "success", "false" );

        if ( StringUtils.isEmpty( userIdStr ) ) {
            response.put( "success", "false" );
            response.put( "reason", "Invalid user ID" );
        } else {
            try {
                long userId = Long.parseLong( userIdStr );
                Response apiResponse = sSApiIntergrationBuilder.getIntegrationApi().getUserProfileFlags( userId );
                
                TypedByteArray body = (TypedByteArray) apiResponse.getBody();
                
                if( body != null ) {
                    String responseString = new String( body.getBytes() );
                    return responseString;
                }
                
            } catch ( NumberFormatException | SSAPIException error ) {
                LOG.warn( "Unable get user profile flags", error );
                response.put( "reason", error.getMessage() );
            }
        }

        String responseString = new Gson().toJson( response );
        LOG.info( "Method fetchUserProfileFlags() finished." );
        return responseString;
    }
}
// JIRA SS-77 BY RM07 EOC
// JIRA SS-37 BY RM02 EOC
