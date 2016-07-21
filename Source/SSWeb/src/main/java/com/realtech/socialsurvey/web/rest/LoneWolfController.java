package com.realtech.socialsurvey.web.rest;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import retrofit.mime.TypedByteArray;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.exception.BaseRestException;
import com.realtech.socialsurvey.core.exception.InternalServerException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.LoneWolfErrorCode;
import com.realtech.socialsurvey.core.integration.lonewolf.LoneWolfIntegrationApi;
import com.realtech.socialsurvey.core.integration.lonewolf.LoneWolfIntergrationApiBuilder;
import com.realtech.socialsurvey.web.util.LoneWolfRestUtils;


@Controller
@RequestMapping ( value = "/lonewolf")
public class LoneWolfController extends AbstractController
{

    private static final Logger LOG = LoggerFactory.getLogger( LoneWolfController.class );

    @Value ( "${LONEWOLF_APP_CONNECTION_URL}")
    private String loneWolfAppConnectionURL;

    @Autowired
    private LoneWolfRestUtils loneWolfRestUtils;

    @Autowired
    private LoneWolfIntergrationApiBuilder loneWolfIntegrationApiBuilder;


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
        @QueryParam ( value = "secretKey") String secretKey, @QueryParam ( value = "clientCode") String clientCode )
    {
        LOG.info( "Method to test lonewolf credentials started for apiToken : " + apiToken + " secretKey : " + secretKey
            + " clientCode : " + clientCode + " started." );

        Response response = null;
        boolean status = false;
        String message = null;
        Map<String, Object> resultMap = new HashMap<String, Object>();

        try {
            try {
                if ( StringUtils.isEmpty( apiToken ) ) {
                    throw new InvalidInputException( "API Token cannot be empty" );
                }
                if ( StringUtils.isEmpty( secretKey ) ) {
                    throw new InvalidInputException( "Secret Key cannot be empty" );
                }
                if ( StringUtils.isEmpty( clientCode ) ) {
                    throw new InvalidInputException( "Client Code cannot be empty" );
                }

                //generating authorization header
                String authHeader = loneWolfRestUtils.generateAuthorizationHeaderFor( loneWolfAppConnectionURL, secretKey,
                    apiToken, clientCode );
                LOG.debug( "Test connection authHeader: " + authHeader );
                LoneWolfIntegrationApi loneWolfIntegrationApi = loneWolfIntegrationApiBuilder.getLoneWolfIntegrationApi();
                //calling get test transaction for id = test
                retrofit.client.Response transactionResponse = loneWolfIntegrationApi.testConnection( authHeader,
                    loneWolfRestUtils.MD5_EMPTY );
                LOG.debug( "Test connection response: " + transactionResponse );

                //processing retrofit response and building rest response
                if ( transactionResponse != null ) {
                    if ( transactionResponse.getStatus() == HttpStatus.SC_NOT_FOUND ) {
                        status = true;
                    } else {
                        String responseString = new String( ( (TypedByteArray) transactionResponse.getBody() ).getBytes() );
                        Map<String, String> responseMap = new Gson().fromJson( responseString,
                            new TypeToken<Map<String, String>>() {}.getType() );
                        message = responseMap.get( "Message" );
                    }
                }
            } catch ( Exception e ) {
                throw new InternalServerException( new LoneWolfErrorCode( CommonConstants.ERROR_CODE_GENERAL,
                    CommonConstants.SERVICE_CODE_GENERAL, "Exception occured while testing connection" ), e.getMessage() );
            }
        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        }
        resultMap.put( CommonConstants.STATUS_COLUMN, status );
        resultMap.put( CommonConstants.MESSAGE, message );
        response = Response.ok( new Gson().toJson( resultMap ) ).build();

        LOG.debug( "returning response: " + response);
        LOG.info( "Method to test lonewolf credentials finished." );
        return response.getEntity().toString();
    }

}
