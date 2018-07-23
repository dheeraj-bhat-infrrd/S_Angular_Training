package com.realtech.socialsurvey.web.admintools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.HierarchySettingsCompare;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.SearchedUser;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.AdminToolsErrorCode;
import com.realtech.socialsurvey.core.exception.BaseRestException;
import com.realtech.socialsurvey.core.exception.InputValidationException;
import com.realtech.socialsurvey.core.exception.InternalServerException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.RestErrorResponse;
import com.realtech.socialsurvey.core.services.admin.AdminService;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.social.SocialManagementService;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.EncryptionHelper;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.core.vo.SubscriptionVO;
import com.realtech.socialsurvey.core.vo.TransactionVO;


@Controller
public class AdminToolsController
{
    private static final Logger LOG = LoggerFactory.getLogger( AdminToolsController.class );

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private OrganizationManagementService organizationManagementService;
    
    @Autowired
    private SocialManagementService socialManagementService;

    @Autowired
    private MessageUtils messageUtils;

    @Autowired
    private EncryptionHelper encryptionHelper;

    @Autowired
    private SurveyHandler surveyHandler;

    @Autowired
    private AdminService adminService;


    /**
     * Controller that returns all the users in a company that match a certain criteria
     * 
     * @param companyId
     *            , HttpServletRequest request
     * @param emailAddress
     * @param firstName
     * @param lastName
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/user/search")
    public Response searchUsersInCompany( @QueryParam ( value = "companyId") long companyId,
        @QueryParam ( value = "emailAddress") String emailAddress, @QueryParam ( value = "firstName") String firstName,
        @QueryParam ( value = "lastName") String lastName, HttpServletRequest request )
    {
        LOG.info( "Method searchUsersInCompany started for companyId : " + companyId + " emailId : " + emailAddress
            + " firstName : " + firstName + " lastName : " + lastName );
        Response response = null;
        try {
            try {
                String authorizationHeader = request.getHeader( "Authorization" );
                validateAuthHeader( authorizationHeader );
                if ( companyId <= 0l ) {
                    throw new InvalidInputException( "Invalid comapnyId : " + companyId );
                }
                Map<String, Object> queries = new HashMap<String, Object>();
                Company company = userManagementService.getCompanyById( companyId );
                queries.put( CommonConstants.COMPANY, company );
                if ( !( emailAddress == null || emailAddress.isEmpty() ) ) {
                    queries.put( CommonConstants.EMAIL_ID, emailAddress );
                }
                if ( !( firstName == null || firstName.isEmpty() ) ) {
                    queries.put( CommonConstants.FIRST_NAME, firstName );
                }
                if ( !( lastName == null || lastName.isEmpty() ) ) {
                    queries.put( CommonConstants.LAST_NAME, lastName );
                }
                if ( queries.size() <= 1 ) {
                    throw new InvalidInputException( "Insufficient search criteria. Please mention at least one." );
                }

                List<User> users = userManagementService.searchUsersInCompanyByMultipleCriteria( queries );
                List<SearchedUser> usersList = new ArrayList<SearchedUser>();
                for ( User user : users ) {
                    SearchedUser searchedUser = new SearchedUser();
                    searchedUser.setUserId( user.getUserId() );
                    searchedUser.setFirstName( user.getFirstName() );
                    searchedUser.setLastName( user.getLastName() );
                    searchedUser.setStatus( user.getStatus() );
                    searchedUser.setEmailAddress( emailAddress );
                    usersList.add( searchedUser );
                }
                String json = new Gson().toJson( usersList );
                response = Response.ok( json ).build();

            } catch ( Exception e ) {
                LOG.error( "Exception occured while searching for users in companyId : " + companyId + ". Reason : "
                    + e.getStackTrace() );
                throw new InternalServerException( new AdminToolsErrorCode( CommonConstants.ERROR_CODE_GENERAL,
                    CommonConstants.SERVICE_CODE_GENERAL, "An exception occured while searching for users in a company" ),
                    e.getMessage(), e );
            }
        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        }
        LOG.info( "Method searchUsersInCompany finished for companyId : " + companyId + " emailId : " + emailAddress
            + " firstName : " + firstName + " lastName : " + lastName );
        return response;
    }


    /**
     * Method to restore a deleted user 1. Check if any user has loginId = user's emailId. 2. Set
     * status = 1, set loginId = emailId 3. Set the status of all user profiles for that user as 1
     * 4. Add user to Solr 6. In mongo, change Status:D to Status:A
     */
    @ResponseBody
    @RequestMapping ( value = "/user/restore/{userId}")
    public Response restoreUser( @PathVariable long userId, @QueryParam ( value = "fullRestore") boolean fullRestore,
        HttpServletRequest request )
    {
        LOG.info( "Method restoreUser started for userId : " + userId );
        Response response = null;
        try {
            try {
                String authorizationHeader = request.getHeader( "Authorization" );
                validateAuthHeader( authorizationHeader );
                userManagementService.restoreDeletedUser( userId, fullRestore );
                response = Response.ok( "UserId " + userId + " was successfully restored." ).build();
            } catch ( Exception e ) {
                LOG.error( "Exception occured while restoring user having userId : " + userId + ". Reason : "
                    + e.getStackTrace() );
                throw new InternalServerException( new AdminToolsErrorCode( CommonConstants.ERROR_CODE_GENERAL,
                    CommonConstants.SERVICE_CODE_GENERAL, "An exception occured while restoring the user" ), e.getMessage(), e );
            }
        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        }

        LOG.info( "Method restoreUser finished for userId : " + userId );
        return response;
    }


    /**
     * Method to move surveys from one user id to another user sample url :
     * /users/movesurveys?from_user={fromUserId}&to_user={toUserId}
     */
    @ResponseBody
    @RequestMapping ( value = "/user/movesurveys")
    public Response moveSurveysToAnotherUser( @QueryParam ( value = "from_user") long from_user,
        @QueryParam ( value = "to_user") long to_user, HttpServletRequest request )
    {
        Response response = null;
        long fromUserId = 0;
        long toUserId = 0;
        try {
            try {
                LOG.info( "Method to move surveys from user id : " + from_user + " to user id : " + to_user + " started." );
                LOG.info( "Checking for authorization to perform survey move operation" );
                String authorizationHeader = request.getHeader( "Authorization" );
                validateAuthHeader( authorizationHeader );
                LOG.info( "Authorization confirmed to perform survey move operation" );
                fromUserId = Long.valueOf( from_user ).longValue();
                toUserId = Long.valueOf( to_user ).longValue();
                if ( fromUserId <= 0 )
                    throw new InvalidInputException( "Invalid from user id passed as parameter" );
                if ( toUserId <= 0 )
                    throw new InvalidInputException( "Invalid to user id passed as parameter" );
                LOG.info( "Moving surveys from one user to another operation started" );
                surveyHandler.moveSurveysToAnotherUser( fromUserId, toUserId );
                LOG.info( "Moving surveys from one user to another operation finished" );
                response = Response.ok(
                    "Surveys from user id : " + from_user + " has been successfully moved to user id : " + to_user ).build();
            } catch ( Exception e ) {
                LOG.error( "Error occurred while moving surveys from user id : " + from_user + " to user id : " + to_user
                    + ", Reason : ", e );
                throw new InternalServerException( new AdminToolsErrorCode( CommonConstants.ERROR_CODE_GENERAL,
                    CommonConstants.SERVICE_CODE_GENERAL, "Error occurred while moving surveys from user id : " + from_user
                        + " to user id : " + to_user ), e.getMessage(), e );
            }
        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        }
        LOG.info( "Method to move surveys from user id : " + from_user + " to user id : " + to_user + " ended." );
        return response;
    }


    /**
     * Method to move surveys for a pre-initiation id to another user id 
     * /
     */
    @ResponseBody
    @RequestMapping ( value = "/movesurvey")
    public Response moveASurveyBetweenUsers( @QueryParam ( value = "survey_preinitiation_id") long survey_preinitiation_id,
        @QueryParam ( value = "to_user") long to_user, HttpServletRequest request )
    {
        Response response = null;
        long surveyPreinitiationId = 0;
        long toUserId = 0;
        try {
            try {
                LOG.info( "Method to move a survey with preinitiation id :{}  to user id : {} started.",
                    survey_preinitiation_id, to_user );
                LOG.info( "Checking for authorization to perform survey move operation" );
                String authorizationHeader = request.getHeader( "Authorization" );
                validateAuthHeader( authorizationHeader );
                LOG.info( "Authorization confirmed to perform survey move operation" );
                surveyPreinitiationId = Long.valueOf( survey_preinitiation_id ).longValue();
                toUserId = Long.valueOf( to_user ).longValue();
                if ( surveyPreinitiationId <= 0 )
                    throw new InvalidInputException( "Invalid surveyPreinitiationId passed as parameter" );
                if ( toUserId <= 0 )
                    throw new InvalidInputException( "Invalid to user id passed as parameter" );
                LOG.info( "Moving survey from one user to another operation started" );
                surveyHandler.moveSurveyBetweenUsers( surveyPreinitiationId, toUserId );
                LOG.info( "Moving surveys from one user to another operation finished" );
                response = Response.ok( "Surveys for pre-initiation id : " + surveyPreinitiationId
                    + " has been successfully moved to user id : " + to_user ).build();
            } catch ( Exception e ) {
                LOG.error( "Error occurred while moving surveys for pre-initiation id : " + surveyPreinitiationId
                    + " to user id : " + to_user + ", Reason : ", e );
                throw new InternalServerException( new AdminToolsErrorCode( CommonConstants.ERROR_CODE_GENERAL,
                    CommonConstants.SERVICE_CODE_GENERAL, "Error occurred while moving surveys for pre-initiation id : "
                        + surveyPreinitiationId + " to user id : " + to_user ),
                    e.getMessage(), e );
            }
        } catch ( BaseRestException e ) {
            LOG.error( "Error occurred while moving surveys for pre-initiation id : " + surveyPreinitiationId + " to user id : "
                + to_user + ", Reason : ", e );
            response = getErrorResponse( e );
        }
        LOG.info( "Method to move surveys for pre-initiation id :{} to user id : {} ended.", surveyPreinitiationId, to_user );
        return response;
    }


    /**
     * Return the incorrect branches
     * 
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/hierarchy/settings/incorrectoffices")
    public Response getIncorrectHierarchySettingsForBranches( HttpServletRequest request )
    {
        Response response = null;
        LOG.info( "Getting list of branches with incorrect settings" );
        try {
            try {
                String authorizationHeader = request.getHeader( "Authorization" );
                validateAuthHeader( authorizationHeader );
                List<Branch> branches = organizationManagementService.getAllNonDefaultBranches();
                // pass this list to get the list of incorrect branch id
                List<HierarchySettingsCompare> compareObjects = organizationManagementService
                    .mismatchBranchHierarchySettings( branches );
                String json = new Gson().toJson( compareObjects );
                response = Response.ok( json ).build();
            } catch ( Exception e ) {
                LOG.error( "Error occurred while fetching branches with incorrect hierarchy settings", e );
                throw new InternalServerException( new AdminToolsErrorCode( CommonConstants.ERROR_CODE_GENERAL,
                    CommonConstants.SERVICE_CODE_GENERAL, e.getMessage() ), e.getMessage(), e );
            }
        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        }
        return response;
    }


    /**
     * Return the incorrect branches
     * 
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/hierarchy/settings/incorrectregions")
    public Response getIncorrectHierarchySettingsForRegions( HttpServletRequest request )
    {
        Response response = null;
        LOG.info( "Getting list of regions with incorrect settings" );
        try {
            try {
                String authorizationHeader = request.getHeader( "Authorization" );
                validateAuthHeader( authorizationHeader );
                List<Region> regions = organizationManagementService.getAllNonDefaultRegions();
                // pass this list to get the list of incorrect region id
                List<HierarchySettingsCompare> compareObjects = organizationManagementService
                    .mismatchRegionHierarchySettings( regions );
                String json = new Gson().toJson( compareObjects );
                response = Response.ok( json ).build();
            } catch ( Exception e ) {
                LOG.error( "Error occurred while fetching regions with incorrect hierarchy settings", e );
                throw new InternalServerException( new AdminToolsErrorCode( CommonConstants.ERROR_CODE_GENERAL,
                    CommonConstants.SERVICE_CODE_GENERAL, e.getMessage() ), e.getMessage(), e );
            }
        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        }
        return response;
    }


    /**
     * Method to get the error response object from base rest exception
     * 
     * @param ex
     * @return
     */
    private Response getErrorResponse( BaseRestException ex )
    {
        LOG.debug( "Resolve Error Response" );
        Status httpStatus = resolveHttpStatus( ex );
        RestErrorResponse errorResponse = ex.transformException( httpStatus.getStatusCode() );
        Response response = Response.status( httpStatus ).entity( new Gson().toJson( errorResponse ) ).build();
        return response;
    }


    /**
     * Method to get the http status based on the exception type
     * 
     * @param ex
     * @return
     */
    private Status resolveHttpStatus( BaseRestException ex )
    {
        LOG.debug( "Resolving http status" );
        Status httpStatus = Status.INTERNAL_SERVER_ERROR;
        if ( ex instanceof InputValidationException ) {
            httpStatus = Status.UNAUTHORIZED;
        } else if ( ex instanceof InternalServerException ) {
            httpStatus = Status.INTERNAL_SERVER_ERROR;
        }
        LOG.debug( "Resolved http status to " + httpStatus.getStatusCode() );
        return httpStatus;
    }


    private long validateAuthHeader( String authorizationHeader ) throws InvalidInputException
    {
        LOG.debug( " method validateAuthHeader started" );

        if (StringUtils.isBlank( authorizationHeader )) {
            throw new InvalidInputException( "Authorization failure. Header is null" );
        }

        String encodedUserPassword = authorizationHeader.replaceFirst("Basic" + " ", "");
        
        String plainText = null;
        try {
             plainText = encryptionHelper.decryptAES( encodedUserPassword, "" );
        } catch ( Exception e ) {
            throw new InvalidInputException( "Authorization failure. Not a valid token" );
        }
        
        long comapnyId;
       
        boolean isValid;
        try {
            String[] keyValuePair = plainText.split( ":" );
            String apiKey = keyValuePair[0];
            String apiSecret = keyValuePair[1];
            String comapnyIdStr = apiSecret.split( "_" )[0];
            comapnyId = Long.valueOf( comapnyIdStr ) ;
            isValid = userManagementService.validateUserApiKey( apiKey, apiSecret, comapnyId );
        } catch ( NumberFormatException | InvalidInputException e ) {
            throw new InvalidInputException( "Authorization failure" );
        }
        
        if(!isValid){
            throw new InvalidInputException( "Authorization failure. Not a valid token" );
        }
       
        LOG.debug( " method validateAuthHeader ended" );
        return comapnyId;
    }


    @ResponseBody
    @RequestMapping ( value = "/getsubscription", method = RequestMethod.GET)
    public Response getSubscriptionDetailBySubscriptionId( @RequestParam String subscriptionId, HttpServletRequest request )
    {
        LOG.info( "Method to getSubscriptionDetailBySubscriptionId started." );
        SubscriptionVO subscription = null;
        Response response = null;
        try {
            try {
                String authorizationHeader = request.getHeader( "Authorization" );
                validateAuthHeader( authorizationHeader );
                subscription = adminService.getSubscriptionVOBySubscriptionId( subscriptionId );
                response = Response.ok( subscription ).build();
            } catch ( Exception e ) {
                LOG.error( "Exception occured while getting Subscription for subscriptionId : " + subscriptionId
                    + ". Reason : " + e.getStackTrace() );
                throw new InternalServerException( new AdminToolsErrorCode( CommonConstants.ERROR_CODE_GENERAL,
                    CommonConstants.SERVICE_CODE_GENERAL, "An exception occured while getting Subscription" ), e.getMessage(), e );
            }
        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        }
        return response;


    }


    @ResponseBody
    @RequestMapping ( value = "/gettransactions", method = RequestMethod.GET)
    public Response getTransactionBySubscriptionId( @RequestParam String subscriptionId, HttpServletRequest request )
    {
        LOG.info( "Method to getSubscriptionDetailBySubscriptionId started." );
        List<TransactionVO> transactions = null;
        String recipientMailId = request.getParameter( "recipientMailId" );
        Response response = null;
        List<String> emailIdList = new ArrayList<String>();
        try {
            try {
                String authorizationHeader = request.getHeader( "Authorization" );
                validateAuthHeader( authorizationHeader );

                if ( recipientMailId != null && !recipientMailId.isEmpty() ) {

                    if ( !recipientMailId.contains( "," ) ) {
                        if ( !organizationManagementService.validateEmail( recipientMailId.trim() ) )
                            throw new InvalidInputException( "Mail id - " + recipientMailId
                                + " entered as send alert to input is invalid", DisplayMessageConstants.GENERAL_ERROR );
                        else
                            emailIdList.add( recipientMailId.trim() );
                    } else {
                        String mailIds[] = recipientMailId.split( "," );

                        for ( String mailID : mailIds ) {
                            if ( !organizationManagementService.validateEmail( mailID.trim() ) )
                                throw new InvalidInputException( "Mail id - " + mailID
                                    + " entered amongst the mail ids as send alert to input is invalid",
                                    DisplayMessageConstants.GENERAL_ERROR );
                            else
                                emailIdList.add( mailID.trim() );
                        }
                    }

                }
                transactions = adminService.getTransactionListBySubscriptionIs( subscriptionId );

                if ( emailIdList != null && !emailIdList.isEmpty() ) {
                    LOG.debug( "Generating excel and sending mail to user" );
                    adminService.generateTransactionListExcelAndMail( transactions, emailIdList, subscriptionId );
                }

                response = Response.ok( transactions ).build();
            } catch ( Exception e ) {
                LOG.error( "Exception occured while getting transactions for subscriptionId : " + subscriptionId
                    + ". Reason : " + e.getStackTrace() );
                throw new InternalServerException( new AdminToolsErrorCode( CommonConstants.ERROR_CODE_GENERAL,
                    CommonConstants.SERVICE_CODE_GENERAL, "An exception occured while getting transactions" ), e.getMessage(), e );
            }
        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        }

        return response;

    }


    @ResponseBody
    @RequestMapping ( value = "/getactivesubscriptions", method = RequestMethod.GET)
    public Response getAllActiveSubscriptions( HttpServletRequest request )
    {
        LOG.info( "Method to getAllActiveSubscriptions started." );
        List<SubscriptionVO> subscriptions = null;
        Response response = null;
        String recipientMailId = request.getParameter( "recipientMailId" );
        List<String> emailIdList = new ArrayList<String>();
        try {
            try {
                String authorizationHeader = request.getHeader( "Authorization" );
                validateAuthHeader( authorizationHeader );
                if ( recipientMailId != null && !recipientMailId.isEmpty() ) {

                    if ( !recipientMailId.contains( "," ) ) {
                        if ( !organizationManagementService.validateEmail( recipientMailId.trim() ) )
                            throw new InvalidInputException( "Mail id - " + recipientMailId
                                + " entered as send alert to input is invalid", DisplayMessageConstants.GENERAL_ERROR );
                        else
                            emailIdList.add( recipientMailId.trim() );
                    } else {
                        String mailIds[] = recipientMailId.split( "," );

                        for ( String mailID : mailIds ) {
                            if ( !organizationManagementService.validateEmail( mailID.trim() ) )
                                throw new InvalidInputException( "Mail id - " + mailID
                                    + " entered amongst the mail ids as send alert to input is invalid",
                                    DisplayMessageConstants.GENERAL_ERROR );
                            else
                                emailIdList.add( mailID.trim() );
                        }
                    }

                }
                subscriptions = adminService.getActiveSubscriptionsList();
                if ( emailIdList != null && !emailIdList.isEmpty() ) {
                    LOG.debug( "Generating excel and sending mail to user" );
                    adminService.generateSubscriptionListExcelAndMail( subscriptions, emailIdList );
                }

                response = Response.ok( subscriptions ).build();
            } catch ( Exception e ) {
                LOG.error( "Exception occured while getting active subscriptions. Reason : " + e.getMessage());
                throw new InternalServerException( new AdminToolsErrorCode( CommonConstants.ERROR_CODE_GENERAL,
                    CommonConstants.SERVICE_CODE_GENERAL, "An exception occured while getting all active subscriptions" ),
                    e.getMessage(), e );
            }
        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        }

        return response;
    }


    @ResponseBody
    @RequestMapping ( value = "/getbillingmodeautocompanies", method = RequestMethod.GET)
    public Response getBillingModeAutoCompanies( HttpServletRequest request )
    {
        LOG.info( "Method to getBillingModeAutoCompanies started." );
        List<Company> companies = null;
        Response response = null;
        String recipientMailId = request.getParameter( "recipientMailId" );
        List<String> emailIdList = new ArrayList<String>();
        try {
            try {
                String authorizationHeader = request.getHeader( "Authorization" );
                validateAuthHeader( authorizationHeader );
                //parse email  ids
                if ( recipientMailId != null && !recipientMailId.isEmpty() ) {

                    if ( !recipientMailId.contains( "," ) ) {
                        if ( !organizationManagementService.validateEmail( recipientMailId.trim() ) )
                            throw new InvalidInputException( "Mail id - " + recipientMailId
                                + " entered as send alert to input is invalid", DisplayMessageConstants.GENERAL_ERROR );
                        else
                            emailIdList.add( recipientMailId.trim() );
                    } else {
                        String mailIds[] = recipientMailId.split( "," );

                        for ( String mailID : mailIds ) {
                            if ( !organizationManagementService.validateEmail( mailID.trim() ) )
                                throw new InvalidInputException( "Mail id - " + mailID
                                    + " entered amongst the mail ids as send alert to input is invalid",
                                    DisplayMessageConstants.GENERAL_ERROR );
                            else
                                emailIdList.add( mailID.trim() );
                        }
                    }

                }
                companies = adminService.getAllAutoBillingModeCompanies();
                if ( emailIdList != null && !emailIdList.isEmpty() ) {
                    LOG.debug( "Generating excel and sending mail to user" );
                    adminService.generateAutoBillingCompanyListExcelAndMail( companies, emailIdList );
                }
                response = Response.ok( companies ).build();
            } catch ( Exception e ) {
                LOG.error( "Exception occured while getting auto billing mode companies. Reason : " + e.getStackTrace() );
                throw new InternalServerException( new AdminToolsErrorCode( CommonConstants.ERROR_CODE_GENERAL,
                    CommonConstants.SERVICE_CODE_GENERAL, "An exception occured while getting auto billing mode companies" ),
                    e.getMessage(), e );
            }
        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        }

        return response;
    }
    
    @ResponseBody
    @RequestMapping ( value = "/updateuserloginprevention", method = RequestMethod.POST)
    public Response updateUserLoginPrevention( HttpServletRequest request )
    {
    		LOG.info("Method updateUserLoginPrevention started");
    		
		try {
			Long userId = 0l;
			Boolean isLoginPrevented = false;
			String authorizationHeader = request.getHeader("Authorization");
			String userIdStr = request.getParameter("userId");
			String isLoginPreventedStr = request.getParameter("isLoginPrevented");
			
			// authorize request
			try {
				validateAuthHeader(authorizationHeader);
			} catch (InvalidInputException e) {
				return Response.status(Response.Status.UNAUTHORIZED).tag(e.getMessage()).build();
			}

			// process request
			if(StringUtils.isEmpty(isLoginPreventedStr) || (! isLoginPreventedStr.equals("true") && ! isLoginPreventedStr.equals("false")) ) {
				throw new InvalidInputException("Wrong value passed for parameter isLoginPrevented");
			}
			isLoginPrevented = Boolean.parseBoolean(isLoginPreventedStr);
			
			try {
				userId = Long.parseLong(userIdStr);
			} catch (NumberFormatException e) {
				throw new InvalidInputException("Wrong value passed for parameter userId ");
			}

			organizationManagementService.updateIsLoginPreventedForUser(userId, isLoginPrevented);
			// update hidePublicPage as well
			organizationManagementService.updateHidePublicPageForUser(userId, isLoginPrevented);
			//update social media tokens
			organizationManagementService.updateSocialMediaForUser( userId, isLoginPrevented );
			
			LOG.info("Method updateUserLoginPrevention finished");
			return Response.status(Response.Status.CREATED).tag("Field isLoginPrevented updated for user id " + userId  + " to : " +  isLoginPrevented ).build();
		} catch (Exception e) {
			LOG.error("Error while updating user log in prevention field " , e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR ).tag(e.getMessage()).build();
		}
    }
    
    @ResponseBody
    @RequestMapping ( value = "/manualposttolinkedin", method = RequestMethod.POST)
    public Response manualPostToLinkedin( HttpServletRequest request )
	{
		LOG.info("Method manualPostToLinkedin started");
		String authorizationHeader = request.getHeader("Authorization");
		String surveyMongoId = request.getParameter("surveyMongoId");
		String entityType = request.getParameter("entityType");
		String entityIdStr = request.getParameter("entityId");
		Long entityId = 0l;

		try {

			// authorize request
			try {
				validateAuthHeader(authorizationHeader);
			} catch (InvalidInputException e) {
				return Response.status(Response.Status.UNAUTHORIZED).tag(e.getMessage()).build();
			}
			
			//validate request
			try {
				entityId = Long.parseLong(entityIdStr);
			} catch (NumberFormatException e) {
				throw new InvalidInputException("Wrong value passed for parameter entityId ");
			}

			if (StringUtils.isEmpty(surveyMongoId))
				throw new InvalidInputException("Parameter surveyMongoId is missing");

			if (StringUtils.isEmpty(entityType))
				throw new InvalidInputException("Parameter entityType is missing");
			
			socialManagementService.manualPostToLinkedInForEntity(entityType, entityId, surveyMongoId);
			//return response entity
			return Response.status(Response.Status.CREATED)
					.tag("Successfully posted to Linkedin for " + entityType + " with id  " + entityId).build();
		}catch(InvalidInputException e) {
			LOG.error("Error in manualPostToLinkedin " , e);
			return Response.status(Response.Status.BAD_REQUEST ).tag(e.getMessage()).build();
		}catch(Exception e) {
			LOG.error("Error in manualPostToLinkedin " , e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR ).tag(e.getMessage()).build();
		}
	}
    
    
    @ResponseBody
    @RequestMapping ( value = "/updatelistofuserloginpreventions", method = RequestMethod.POST)
    public Response updateListOfUserLoginPreventions( HttpServletRequest request )
    {
        LOG.info( "Method updateListOfUserLoginPreventions started" );

        try {
            List<Long> userIdList = new ArrayList<>();
            String userIdListStr[] = null;
            Boolean isLoginPrevented = false;
            String authorizationHeader = request.getHeader( "Authorization" );
            String userIdsStr = request.getParameter( "userIds" );
            String isLoginPreventedStr = request.getParameter( "isLoginPrevented" );

            // authorize request
            try {
                validateAuthHeader( authorizationHeader );
            } catch ( InvalidInputException e ) {
                return Response.status( Response.Status.UNAUTHORIZED ).tag( e.getMessage() ).build();
            }

            // process request
            if ( StringUtils.isEmpty( isLoginPreventedStr )
                || ( !isLoginPreventedStr.equals( "true" ) && !isLoginPreventedStr.equals( "false" ) ) ) {
                throw new InvalidInputException( "Wrong value passed for parameter isLoginPrevented" );
            }
            isLoginPrevented = Boolean.parseBoolean( isLoginPreventedStr );

            if ( userIdsStr.contains( "," ) ) {
                try {
                    userIdListStr = userIdsStr.split( "," );
                    for ( String userIdStr : userIdListStr ) {
                        userIdList.add( Long.parseLong( userIdStr ) );
                    }
                } catch ( NumberFormatException e ) {
                    throw new InvalidInputException( "Wrong value passed for parameter userId " );
                }

                //update isLoginPrevented for the users
                organizationManagementService.updateIsLoginPreventedForUsers( userIdList, isLoginPrevented );
                // update hidePublicPage as well
                organizationManagementService.updateHidePublicPageForUsers( userIdList, isLoginPrevented );
                //update social media tokens
                organizationManagementService.updateSocialMediaForUsers( userIdList, isLoginPrevented );

                return Response.status( Response.Status.CREATED )
                    .tag( "Field isLoginPrevented updated for user ids: " + userIdList + " to : " + isLoginPrevented ).build();
            }
            LOG.info( "Method updateUserLoginPrevention finished" );
            return Response.status( Response.Status.CREATED )
                .tag( "Field isLoginPrevented updated for user ids: " + userIdList + " to : " + isLoginPrevented ).build();
        } catch ( Exception e ) {
            LOG.error( "Error while updating user log in prevention field ", e );
            return Response.status( Response.Status.INTERNAL_SERVER_ERROR ).tag( e.getMessage() ).build();
        }
    }


    @ResponseBody
    @RequestMapping ( value = "/decodeencompasspassword", method = RequestMethod.POST)
    public Response decodeEncompassPassword( HttpServletRequest request )
	{
		LOG.info("Method manualPostToLinkedin started");
		String authorizationHeader = request.getHeader("Authorization");
		String encryptedPassword = request.getParameter("encryptedPassword");

		try {

			// authorize request
			try {
				long comapnyId = validateAuthHeader(authorizationHeader);
				if(comapnyId != CommonConstants.REALTECH_ADMIN_ID) {
					throw new InvalidInputException("Invalid auth header.");
				}
			} catch (InvalidInputException e) {
				return Response.status(Response.Status.UNAUTHORIZED).tag(e.getMessage()).build();
			}
			
			//validate request
			if (StringUtils.isEmpty(encryptedPassword))
				throw new InvalidInputException("Parameter encryptedPassword is missing");
			
			 String decryptedPassword = encryptionHelper.decryptAES( encryptedPassword, "" );
			//return response entity
			return Response.status(Response.Status.OK)
					.tag("Decrypted password is  " + decryptedPassword).build();
		}catch(InvalidInputException e) {
			LOG.error("Error in decodeEncompassPassword " , e);
			return Response.status(Response.Status.BAD_REQUEST ).tag(e.getMessage()).build();
		}catch(Exception e) {
			LOG.error("Error in decodeEncompassPassword " , e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR ).tag(e.getMessage()).build();
		}
	}

}
