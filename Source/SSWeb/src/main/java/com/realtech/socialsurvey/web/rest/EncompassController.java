package com.realtech.socialsurvey.web.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.EncompassCrmInfo;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.exception.BaseRestException;
import com.realtech.socialsurvey.core.exception.EncompassErrorCode;
import com.realtech.socialsurvey.core.exception.InputValidationException;
import com.realtech.socialsurvey.core.exception.InternalServerException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.RestErrorResponse;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;


@Controller
@RequestMapping ( value = "/encompass")
public class EncompassController
{
    private static final Logger LOG = LoggerFactory.getLogger( EncompassController.class );

    @Autowired
    private OrganizationManagementService organizationManagementService;

    @Value ( "${ENCOMPASS_APP_URL}")
    private String encompassTestUrl;


    /**
     * Controller to get a list of companies conencted to encompass and their credentials 
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/getcompanycredentials")
    public Response getCompanyCredentials()
    {
        Response response = null;
        LOG.info( "Method to get the encompass credentials for all companies started." );
        try {
            try {
                List<OrganizationUnitSettings> companyList = organizationManagementService
                    .getOrganizationUnitSettingsForCRMSource( CommonConstants.CRM_INFO_SOURCE_ENCOMPASS,
                        CommonConstants.COMPANY_SETTINGS_COLLECTION );

                List<EncompassCrmInfo> crmInfoList = new ArrayList<EncompassCrmInfo>();
                for ( OrganizationUnitSettings company : companyList ) {
                    crmInfoList.add( (EncompassCrmInfo) company.getCrm_info() );
                }
                String json = new Gson().toJson( crmInfoList );
                LOG.debug( "crmInfoList json : " + json );
                response = Response.ok( json ).build();

            } catch ( InvalidInputException e ) {
                LOG.error( "An exception occured while fetching the list of companies connected to encompass. Reason : " + e );
                throw new InternalServerException( new EncompassErrorCode(
                    CommonConstants.ERROR_CODE_ENCOMPASS_COMPANY_FETCH_FAILURE, CommonConstants.SERVICE_CODE_COMPANY_CRM_INFO,
                    "Error occured while fetching companies connected to encompass" ), e.getMessage() );
            } catch ( NoRecordsFetchedException e ) {
                LOG.info( "No company connected to encompass found!" );
            }
        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        }
        LOG.info( "Method to get the encompass credentials for all companies finished." );
        return response;
    }


    @ResponseBody
    @RequestMapping ( value = "/testcredentials")
    public Response testCompanyCredentials( @QueryParam ( value = "username") String username,
        @QueryParam ( value = "password") String password, @QueryParam ( value = "url") String url )
    {
        LOG.info( "Method to test encompass credentials started for username : " + username + " password : " + password
            + " url : " + url + " started." );
        Response response = null;
        try {
            try {
                if ( username == null || username.isEmpty() ) {
                    throw new InvalidInputException( "Username cannot be empty" );
                }

                if ( password == null || password.isEmpty() ) {
                    throw new InvalidInputException( "Password cannot be empty" );
                }

                if ( url == null || url.isEmpty() ) {
                    throw new InvalidInputException( "URL cannot be empty" );
                }

                String jsonString = "{ \"ClientUrl\" : \"" + url + "\", \"UserName\" : \"" + username + "\", \"Password\" : \""
                    + password + "\" }";
                LOG.info( "JSON Request object : " + jsonString );
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType( MediaType.APPLICATION_JSON );
                headers.setAccept( Arrays.asList( MediaType.APPLICATION_JSON ) );

                RestTemplate restTemplate = new RestTemplate();

                //Make request to the encompass application and get the response
                ResponseEntity<String> restResponse = restTemplate.postForEntity( encompassTestUrl, jsonString, String.class );
                String responseBody = restResponse.getBody();
                response = Response.ok( responseBody ).build();

            } catch ( ResourceAccessException e ) {
                LOG.error( "An error occured while testing encompass credentials. Reason : " + e.getMessage() );
                throw new InternalServerException( new EncompassErrorCode( CommonConstants.ERROR_CODE_GENERAL,
                    CommonConstants.SERVICE_CODE_GENERAL, "Exception occured while testing connection" ),
                    "Unable to connect to encompass server at the moment" );
            } catch ( Exception e ) {
                throw new InternalServerException( new EncompassErrorCode( CommonConstants.ERROR_CODE_GENERAL,
                    CommonConstants.SERVICE_CODE_GENERAL, "Exception occured while testing connection" ), e.getMessage() );
            }
        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        }

        LOG.info( "Method to test encompass credentials started for username : " + username + " password : " + password
            + " url : " + url + " finished." );
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
