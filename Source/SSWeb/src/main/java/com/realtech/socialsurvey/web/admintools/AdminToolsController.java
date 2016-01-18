package com.realtech.socialsurvey.web.admintools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserFromSearch;
import com.realtech.socialsurvey.core.exception.AdminToolsErrorCode;
import com.realtech.socialsurvey.core.exception.BaseRestException;
import com.realtech.socialsurvey.core.exception.InputValidationException;
import com.realtech.socialsurvey.core.exception.InternalServerException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.RestErrorResponse;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;


@Controller
public class AdminToolsController
{
    private static final Logger LOG = LoggerFactory.getLogger( AdminToolsController.class );

    @Autowired
    private UserManagementService userManagementService;


    /**
     * Controller that returns all the users in a company that match a certain criteria
     * @param companyId
     * @param emailId
     * @param firstName
     * @param lastName
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/{companyId}")
    public Response searchUsersInCompany( @PathVariable long companyId, @QueryParam ( value = "emailId") String emailId,
        @QueryParam ( value = "firstName") String firstName, @QueryParam ( value = "lastName") String lastName )
    {
        LOG.info( "Method searchUsersInCompany started for companyId : " + companyId + " emailId : " + emailId
            + " firstName : " + firstName + " lastName : " + lastName );
        Response response = null;
        try {
            try {
                if ( companyId <= 0l ) {
                    throw new InvalidInputException( "Invalid comapnyId : " + companyId );
                }
                Map<String, Object> queries = new HashMap<String, Object>();
                Company company = userManagementService.getCompanyById( companyId );
                queries.put( CommonConstants.COMPANY, company );
                if ( !( emailId == null || emailId.isEmpty() ) ) {
                    queries.put( CommonConstants.EMAIL_ID, emailId );
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
                List<UserFromSearch> usersList = new ArrayList<UserFromSearch>();
                for ( User user : users ) {
                    UserFromSearch searchedUser = new UserFromSearch();
                    searchedUser.setUserId( user.getUserId() );
                    searchedUser.setCompanyId( companyId );
                    searchedUser.setFirstName( user.getFirstName() );
                    searchedUser.setLastName( user.getLastName() );
                    searchedUser.setLoginName( user.getLoginName() );
                    searchedUser.setEmailId( user.getEmailId() );
                    searchedUser.setStatus( user.getStatus() );
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
        LOG.info( "Method searchUsersInCompany finished for companyId : " + companyId + " emailId : " + emailId
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
    @RequestMapping ( value = "/restoreUser/{userId}")
    public Response restoreUser( @PathVariable long userId )
    {
        LOG.info( "Method restoreUser started for userId : " + userId );
        Response response = null;
        try {
            try {
                userManagementService.restoreDeletedUser( userId );
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
}
