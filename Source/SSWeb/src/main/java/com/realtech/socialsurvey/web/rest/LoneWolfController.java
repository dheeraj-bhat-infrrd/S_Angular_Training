package com.realtech.socialsurvey.web.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import com.realtech.socialsurvey.core.entities.LoneWolfClassificationCode;
import com.realtech.socialsurvey.core.exception.BaseRestException;
import com.realtech.socialsurvey.core.exception.InternalServerException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.LoneWolfErrorCode;
import com.realtech.socialsurvey.core.services.lonewolf.LoneWolfIntegrationService;


@Controller
@RequestMapping ( value = "/lonewolf")
public class LoneWolfController extends AbstractController
{
    private static final Logger LOG = LoggerFactory.getLogger( LoneWolfController.class );

    @Autowired
    private LoneWolfIntegrationService loneWolfIntegrationService;

    @Value ( "${LONEWOLF_API_TOKEN}")
    private String apiToken;

    @Value ( "${LONEWOLF_SECRET_KEY}")
    private String secretKey;


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
    public String testCompanyCredentials( @QueryParam ( value = "clientCode") String clientCode )
    {
        LOG.info( "Method to test lonewolf credentials started for clientCode : " + clientCode + " started." );
        Response response = null;
        boolean status = false;
        String message = null;
        List<LoneWolfClassificationCode> classificationCodes = new ArrayList<LoneWolfClassificationCode>();
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

                retrofit.client.Response res = loneWolfIntegrationService.testLoneWolfCompanyCredentials( secretKey, apiToken,
                    clientCode );

                //processing retrofit response and building rest response
                if ( res != null ) {
                    if ( res.getStatus() == HttpStatus.SC_OK ) {
                        status = true;
                        message = "Lone Wolf Test Connection Successful.";
                    } else {
                        String responseString = new String( ( (TypedByteArray) res.getBody() ).getBytes() );
                        Map<String, String> responseMap = new Gson().fromJson( responseString,
                            new TypeToken<Map<String, String>>() {}.getType() );
                        message = responseMap.get( "Message" );
                    }
                }
                
                classificationCodes = loneWolfIntegrationService.fetchLoneWolfClassificationCodes(secretKey, apiToken, clientCode);

                
                resultMap.put( CommonConstants.STATUS_COLUMN, status );
                resultMap.put( CommonConstants.MESSAGE, message );
                resultMap.put( "classifications", classificationCodes );
                response = Response.ok( new Gson().toJson( resultMap ) ).build();
            } catch ( Exception e ) {
                throw new InternalServerException( new LoneWolfErrorCode( CommonConstants.ERROR_CODE_GENERAL,
                    CommonConstants.SERVICE_CODE_GENERAL, "Exception occured while testing connection" ), e.getMessage() );
            }
        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        }

        LOG.debug( "returning response: " + response );
        LOG.info( "Method to test lonewolf credentials finished." );
        return response.getEntity().toString();
    }

}
