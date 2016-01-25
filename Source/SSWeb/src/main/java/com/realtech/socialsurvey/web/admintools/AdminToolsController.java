package com.realtech.socialsurvey.web.admintools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.SearchedUser;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.AdminToolsErrorCode;
import com.realtech.socialsurvey.core.exception.BaseRestException;
import com.realtech.socialsurvey.core.exception.InputValidationException;
import com.realtech.socialsurvey.core.exception.InternalServerException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.RestErrorResponse;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.EncryptionHelper;
import com.realtech.socialsurvey.core.utils.MessageUtils;


@Controller
public class AdminToolsController
{
    private static final Logger LOG = LoggerFactory.getLogger( AdminToolsController.class );

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private MessageUtils messageUtils;

    @Autowired
    private EncryptionHelper encryptionHelper;

    @Autowired
    private SurveyHandler surveyHandler;


    /**
     * Controller that returns all the users in a company that match a certain criteria
     * @param companyId, HttpServletRequest request
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
                    e.getMessage() );
            }
        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        }
        LOG.info( "Method searchUsersInCompany finished for companyId : " + companyId + " emailId : " + emailAddress
            + " firstName : " + firstName + " lastName : " + lastName );
        return response;
    }


    /**
     * Method to restore a deleted user
     * 1. Check if any user has loginId = user's emailId.
     * 2. Set status = 1, set loginId = emailId
     * 3. Set the status of all user profiles for that user as 1
     * 4. Add user to Solr
     * 6. In mongo, change Status:D to Status:A
     */
    @ResponseBody
    @RequestMapping ( value = "/user/restore/{userId}")
    public Response restoreUser( @PathVariable long userId, @QueryParam ( value = "fullRestore") boolean fullRestore, HttpServletRequest request )
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
                    CommonConstants.SERVICE_CODE_GENERAL, "An exception occured while restoring the user" ), e.getMessage() );
            }
        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        }

        LOG.info( "Method restoreUser finished for userId : " + userId );
        return response;
    }


    /**
     * Method to move surveys from one user id to another user
     * sample url : /users/movesurveys?from_user={fromUserId}&to_user={toUserId}
     * */
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
                    "Surveys from user id : " + from_user + " has been successfully moved to user id : " + to_user )
                    .build();
            } catch ( Exception e ) {
                LOG.error( "Error occurred while moving surveys from user id : " + from_user + " to user id : " + to_user
                    + ", Reason : ", e );
                throw new InternalServerException( new AdminToolsErrorCode( CommonConstants.ERROR_CODE_GENERAL,
                    CommonConstants.SERVICE_CODE_GENERAL, "Error occurred while moving surveys from user id : " + from_user
                        + " to user id : " + to_user ), e.getMessage() );
            }
        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        }
        LOG.info( "Method to move surveys from user id : " + from_user + " to user id : " + to_user + " ended." );
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


    private void validateAuthHeader( String authorizationHeader ) throws InvalidInputException
    {
        Map<String, String> params = new HashMap<String, String>();
        if ( authorizationHeader == null || authorizationHeader.isEmpty() ) {
            throw new InvalidInputException( "Authorization failure" );
        }

        LOG.debug( "Validating authroization header " );
        try {
            String plainText = encryptionHelper.decryptAES( authorizationHeader, "" );
            String keyValuePairs[] = plainText.split( "&" );

            for ( int counter = 0; counter < keyValuePairs.length; counter += 1 ) {
                String[] keyValuePair = keyValuePairs[counter].split( "=" );
                params.put( keyValuePair[0], keyValuePair[1] );
            }
        } catch ( InvalidInputException e ) {
            throw new InvalidInputException( "Authorization failure" );
        }

        if ( !surveyHandler.validateDecryptedApiParams( params ) ) {
            throw new InvalidInputException( "Authorization failure" );
        }
    }
}
