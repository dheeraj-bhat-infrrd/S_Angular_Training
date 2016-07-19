package com.realtech.socialsurvey.web.rest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.exception.BaseRestException;
import com.realtech.socialsurvey.core.exception.LoneWolfErrorCode;
import com.realtech.socialsurvey.core.exception.InternalServerException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

@Controller
@RequestMapping ( value = "/lonewolf")
public class LoneWolfController extends AbstractController
{
    
    private static final Logger LOG = LoggerFactory.getLogger( LoneWolfController.class );
    
    @Value ( "${LONEWOLF_APP_URL}")
    private String lonewolfTestUrl;
    
    /**
     * Controller to make a call to the lonewolf application and test the credentials
     * @param apiToken
     * @param secretKey
     * @param host
     * @param clientCode
     * @param consumerKey
     * @return connection status
     */
    @ResponseBody
    @RequestMapping ( value = "/testcredentials")
    public String testCompanyCredentials( @QueryParam ( value = "apiToken") String apiToken,
        @QueryParam ( value = "secretKey") String secretKey, @QueryParam ( value = "host") String host,
        @QueryParam ( value = "clientCode") String clientCode, @QueryParam ( value = "consumerKey") String consumerKey)
    {
        LOG.info( "Method to test lonewolf credentials started for apiToken : " + apiToken + " secretKey : " + secretKey
            + " host : " + host + " clientCode : " + clientCode + " consumerKey : " + consumerKey + " started." );
        
        Response response = null;
        boolean status = false;

        try {
            try {
                if ( StringUtils.isEmpty( apiToken ) ) {
                    throw new InvalidInputException( "API Token cannot be empty" );
                }
                if ( StringUtils.isEmpty( secretKey ) ) {
                    throw new InvalidInputException( "Secret Key cannot be empty" );
                }
                if ( StringUtils.isEmpty( host ) ) {
                    throw new InvalidInputException( "Host cannot be empty" );
                }
                if ( StringUtils.isEmpty( clientCode ) ) {
                    throw new InvalidInputException( "Client Code cannot be empty" );
                }
                if ( StringUtils.isEmpty( consumerKey ) ) {
                    throw new InvalidInputException( "Consumer Key cannot be empty" );
                }
                
                Map<String, String> jsonMap = new HashMap<String, String>();
                
                //TODO prepare lonewolf request to test the connection
                
                /*jsonMap.put( CommonConstants.ENCOMPASS_CLIENT_URL_COLUMN, url );
                jsonMap.put( CommonConstants.ENCOMPASS_USERNAME_COLUMN, username );
                jsonMap.put( CommonConstants.ENCOMPASS_PASSWORD_COLUMN, password );*/
                String jsonString = new Gson().toJson( jsonMap );
                LOG.info( "JSON Request object : " + jsonString );

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType( MediaType.APPLICATION_JSON );
                headers.setAccept( Arrays.asList( MediaType.APPLICATION_JSON ) );

                HttpEntity<String> requestEntity = new HttpEntity<String>( jsonString, headers );
                HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
                requestFactory.setReadTimeout( 30 * 1000 );
                requestFactory.setConnectTimeout( 30 * 1000 );
                RestTemplate restTemplate = new RestTemplate( requestFactory );
                restTemplate.getMessageConverters().add( new FormHttpMessageConverter() );
                restTemplate.getMessageConverters().add( new MappingJackson2HttpMessageConverter() );
                //Make request to the lonewolf application and get the response
                String responseBody = restTemplate.postForObject( lonewolfTestUrl, requestEntity, String.class );
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
                LOG.error( "An error occured while testing lonewolf credentials. Reason : " + e.getMessage() );
                throw new InternalServerException( new LoneWolfErrorCode( CommonConstants.ERROR_CODE_GENERAL,
                    CommonConstants.SERVICE_CODE_GENERAL, "Exception occured while testing connection" ),
                    "Unable to connect to lonewolf server at the moment" );
            } catch ( HttpServerErrorException e ) {
                LOG.error( "An error occured while testing lonewolf credentials. Reason : " + e.getMessage() );
                throw new InternalServerException( new LoneWolfErrorCode( CommonConstants.ERROR_CODE_GENERAL,
                    CommonConstants.SERVICE_CODE_GENERAL, "Exception occured while testing connection" ),
                    "Unable to connect to lonewolf server at the moment" );
            } catch ( Exception e ) {
                throw new InternalServerException( new LoneWolfErrorCode( CommonConstants.ERROR_CODE_GENERAL,
                    CommonConstants.SERVICE_CODE_GENERAL, "Exception occured while testing connection" ), e.getMessage() );
            }
        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        }
        
        LOG.info( "Method to test lonewolf credentials started for apiToken : " + apiToken + " secretKey : " + secretKey
            + " host : " + host + " clientCode : " + clientCode + " consumerKey : " + consumerKey + " finished." );
        return response.getEntity().toString();
    }

}
