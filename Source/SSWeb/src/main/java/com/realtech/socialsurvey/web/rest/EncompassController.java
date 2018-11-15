package com.realtech.socialsurvey.web.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.realtech.socialsurvey.core.entities.EncompassCrmInfoVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.CompanyEncompassInfo;
import com.realtech.socialsurvey.core.entities.EncompassCrmInfo;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.exception.BaseRestException;
import com.realtech.socialsurvey.core.exception.EncompassErrorCode;
import com.realtech.socialsurvey.core.exception.InternalServerException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;


@Controller
@RequestMapping ( value = "/encompass")
public class EncompassController extends AbstractController
{
    private static final Logger LOG = LoggerFactory.getLogger( EncompassController.class );

    @Autowired
    private OrganizationManagementService organizationManagementService;

    @Value ( "${ENCOMPASS_APP_URL}")
    private String encompassTestUrl;


    /**
     * Controller to get a list of companies connected to encompass and their credentials 
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/getcompanycredentials")
    public List<CompanyEncompassInfo> getCompanyCredentials( @QueryParam ( value = "state") String state,
        @QueryParam ( value = "version") String version, @QueryParam ( value = "status") String status )
    {
        List<CompanyEncompassInfo> crmInfoList = new ArrayList<CompanyEncompassInfo>();
        LOG.info( "Method to get the encompass credentials for all companies started." );
        try {
            if ( state == null || state.isEmpty() ) {
                throw new InvalidInputException( "The state parameter is empty" );
            }

            if ( version == null || version.isEmpty() ) {
                throw new InvalidInputException( "The version parameter is empty" );
            }

            List<OrganizationUnitSettings> companyList = organizationManagementService.getCompanyListForEncompass( state, version );

            
            List<Long> companies = new ArrayList<>();
            
            // build company Identifier list
            for ( OrganizationUnitSettings company : companyList ) {
            	companies.add( company.getIden() );
            }
            
            // get the list of active companies
            companies = organizationManagementService.filterCompanyIdsByStatus( companies, status );

            for ( OrganizationUnitSettings company : companyList ) {
            	if( companies.contains(company.getIden() ) ) {
	                CompanyEncompassInfo companyEncompassInfo = new CompanyEncompassInfo();
	                companyEncompassInfo.setCompanyName( company.getProfileName() );
	                EncompassCrmInfoVO encompassCrmInfoVO = EncompassCrmInfoVO.encompassCrmInfoVOMapper(
                        (EncompassCrmInfo) company.getCrm_info() , company.isAllowPartnerSurvey());
	                companyEncompassInfo.setEncompassCrmInfo( encompassCrmInfoVO );
	                crmInfoList.add( companyEncompassInfo );
            	}
            }

        } catch ( NoRecordsFetchedException e ) {
            LOG.info( "No company connected to encompass found!" );
        } catch ( InvalidInputException e ) {
            LOG.error( "An exception occured while fetching the list of companies connected to encompass. Reason : " + e );
            throw new InternalServerException( new EncompassErrorCode(
                CommonConstants.ERROR_CODE_ENCOMPASS_COMPANY_FETCH_FAILURE, CommonConstants.SERVICE_CODE_COMPANY_CRM_INFO,
                "Error occured while fetching companies connected to encompass" ), e.getMessage(), e );
        }
        LOG.info( "Method to get the encompass credentials for all companies finished." );
        return crmInfoList;
    }


    /**
     * Controller to make a call to the encompass application and test the credentials
     * @param username
     * @param password
     * @param url
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/testcredentials")
    public String testCompanyCredentials( @QueryParam ( value = "username") String username,
        @QueryParam ( value = "password") String password, @QueryParam ( value = "url") String url, @QueryParam ( value = "version") String version )
    {
        LOG.info( "Method to test encompass credentials started for username : " + username + " password : " + password
            + " url : " + url + " started." );
        Response response = null;
        boolean status = false;

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
                
                if ( version == null || version.isEmpty() ) {
                    throw new InvalidInputException( "URL cannot be empty" );
                }

                Map<String, String> jsonMap = new HashMap<String, String>();
                jsonMap.put( CommonConstants.ENCOMPASS_CLIENT_URL_COLUMN, url );
                jsonMap.put( CommonConstants.ENCOMPASS_USERNAME_COLUMN, username );
                jsonMap.put( CommonConstants.ENCOMPASS_PASSWORD_COLUMN, password );
                String jsonString = new Gson().toJson( jsonMap );
                LOG.info( "JSON Request object : " + jsonString );

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType( MediaType.APPLICATION_JSON );
                headers.setAccept( Arrays.asList( MediaType.APPLICATION_JSON ) );
                
                //get host name
                String hostName = organizationManagementService.getEncompassHostByVersion( version );

                HttpEntity<String> requestEntity = new HttpEntity<String>( jsonString, headers );
                HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
                requestFactory.setReadTimeout( 30 * 1000 );
                requestFactory.setConnectTimeout( 30 * 1000 );
                RestTemplate restTemplate = new RestTemplate( requestFactory );
                restTemplate.getMessageConverters().add( new FormHttpMessageConverter() );
                restTemplate.getMessageConverters().add( new MappingJackson2HttpMessageConverter() );
                //Make request to the encompass application and get the response
                String responseBody = restTemplate.postForObject( CommonConstants.HTTP_REQUEST_URL_INITIAL + hostName + encompassTestUrl, requestEntity, String.class );
                Map<String, String> responseMap = new Gson().fromJson( responseBody,
                    new TypeToken<Map<String, String>>() {}.getType() );
                if ( Boolean.parseBoolean( responseMap.get( CommonConstants.STATUS_COLUMN ) ) ) {
                    status = true;
                }
                Map<String, Object> resultMap = new HashMap<String, Object>();
                resultMap.put( CommonConstants.STATUS_COLUMN, status );
                resultMap.put( CommonConstants.MESSAGE, responseMap.get( CommonConstants.MESSAGE ) );
                response = Response.ok( new Gson().toJson( resultMap ) ).build();
            } catch ( ResourceAccessException e ) {
                LOG.error( "An error occured while testing encompass credentials. Reason : " + e.getMessage() );
                throw new InternalServerException( new EncompassErrorCode( CommonConstants.ERROR_CODE_GENERAL,
                    CommonConstants.SERVICE_CODE_GENERAL, "Exception occured while testing connection" ),
                    "Unable to connect to encompass server at the moment" , e );
            } catch ( HttpServerErrorException e ) {
                LOG.error( "An error occured while testing encompass credentials. Reason : " + e.getMessage() );
                throw new InternalServerException( new EncompassErrorCode( CommonConstants.ERROR_CODE_GENERAL,
                    CommonConstants.SERVICE_CODE_GENERAL, "Exception occured while testing connection" ),
                    "Unable to connect to encompass server at the moment", e );
            } catch ( Exception e ) {
                throw new InternalServerException( new EncompassErrorCode( CommonConstants.ERROR_CODE_GENERAL,
                    CommonConstants.SERVICE_CODE_GENERAL, "Exception occured while testing connection" ), e.getMessage(), e );
            }
        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        }

        LOG.info( "Method to test encompass credentials started for username : " + username + " password : " + password
            + " url : " + url + " finished." );
        return response.getEntity().toString();
    }


    /**
     * Method to set the generateReport field to false in EncompassCrmInfo
     * @param companyId
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/disablereportgeneration/{companyId}")
    public boolean disableEncompassReportGeneration( @PathVariable long companyId )
    {
        LOG.info( "Method to disable report generation for companyId : " + companyId + " started." );
        boolean success = true;
        try {
            if ( companyId <= 0l ) {
                throw new InvalidInputException( "Invalid companyId : " + companyId );
            }
            OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( companyId );
            if ( companySettings == null ) {
                throw new InvalidInputException( "Company Settings was not found for companyId : " + companyId );
            }

            if ( companySettings.getCrm_info() == null || companySettings.getCrm_info().getCrm_source() == null
                || companySettings.getCrm_info().getCrm_source().isEmpty()
                || !( companySettings.getCrm_info().getCrm_source().equals( CommonConstants.CRM_INFO_SOURCE_ENCOMPASS ) ) ) {
                throw new InvalidInputException( "No Encompass CRM Info was found for the company : " + companyId );
            }

            EncompassCrmInfo encompassCrmInfo = (EncompassCrmInfo) companySettings.getCrm_info();
            encompassCrmInfo.setGenerateReport( false );
            organizationManagementService.updateCRMDetails( companySettings, encompassCrmInfo,
                "com.realtech.socialsurvey.core.entities.EncompassCrmInfo" );

        } catch ( Exception e ) {
            LOG.error( "An exception occured while disabling report generation for companyId : " + companyId, e );
            success = false;
        }
        LOG.info( "Method to disable report generation for companyId : " + companyId + " finished." );
        return success;
    }

}
