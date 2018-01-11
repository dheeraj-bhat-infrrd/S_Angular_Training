package com.realtech.socialsurvey.core.services.generator.impl;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.realtech.socialsurvey.core.entities.SendGridEventEntity;
import com.realtech.socialsurvey.core.integration.stream.StreamApiConnectException;
import com.realtech.socialsurvey.core.integration.stream.StreamApiException;
import com.realtech.socialsurvey.core.integration.stream.StreamApiIntegrationBuilder;
import com.realtech.socialsurvey.core.services.stream.StreamMessagesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.UrlDetailsDao;
import com.realtech.socialsurvey.core.entities.UrlDetails;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;
import com.realtech.socialsurvey.core.services.generator.UrlService;
import com.realtech.socialsurvey.core.utils.EncryptionHelper;

import static java.util.Arrays.*;


@Component
public class UrlServiceImpl implements UrlService
{
    private static final Logger LOG = LoggerFactory.getLogger( UrlServiceImpl.class );

    @Autowired
    private UrlDetailsDao urlDetailsDao;

    @Autowired
    private EncryptionHelper encryptionHelper;

    @Value ( "${APPLICATION_BASE_URL}")
    private String applicationBaseUrl;

    @Autowired
    private URLGenerator urlGenerator;

    private StreamApiIntegrationBuilder streamApiIntegrationBuilder;
    private StreamMessagesService streamMessagesService;

    @Autowired
    public void setStreamApiIntegrationBuilder( StreamApiIntegrationBuilder streamApiIntegrationBuilder )
    {
        this.streamApiIntegrationBuilder = streamApiIntegrationBuilder;
    }

    @Autowired
    public void setStreamMessagesService( StreamMessagesService streamMessagesService )
    {
        this.streamMessagesService = streamMessagesService;
    }

    @Override
    public String shortenUrl( String url, String uuid ) throws InvalidInputException
    {
        if ( url == null || url.isEmpty() ) {
            LOG.error( "URL passed in argument of shortenUrl() is null or empty" );
            throw new InvalidInputException( "URL passed in argument of shortenUrl() is null or empty" );
        }

        Date createdDate = new Date();
        String urlDetailsId = null;
        UrlDetails urlDetails = new UrlDetails();
        UrlDetails existingUrlDetails = urlDetailsDao.findUrlDetailsByUrl( url );
        if ( existingUrlDetails == null ) {
            urlDetails.setUrl( url );
            urlDetails.setUrlType( getUrlType( url ) );
            urlDetails.setCreatedOn( createdDate );
            urlDetails.setCreatedBy( CommonConstants.ADMIN_USER_NAME );
            urlDetails.setModifiedOn( createdDate );
            urlDetails.setModifiedBy( CommonConstants.ADMIN_USER_NAME );
            urlDetails.setCreatedOn( new Date() );
            urlDetails.setStatus( CommonConstants.STATUS_NOTACCESSED );
            if ( url.contains( "q=" ) )
                urlDetails.setQueryParams( getQueryParamsFromUrl( url ) );

            LOG.info( "Inserting Url Details into mongo : " + urlDetails );
            urlDetailsId = urlDetailsDao.insertUrlDetails( urlDetails );
            LOG.info( "Newly created Url Details id : " + urlDetailsId );
        } else {
            LOG.info( "Url Details already exist for url : " + url );
            urlDetailsId = existingUrlDetails.get_id();

            Date modifiedDate = new Date();
            if ( existingUrlDetails.getStatus() == CommonConstants.STATUS_ACCESSED ) {
                List<Date> accessDates = new ArrayList<Date>();
                if ( existingUrlDetails.getAccessDates() != null )
                    accessDates = existingUrlDetails.getAccessDates();
                accessDates.add( modifiedDate );
                existingUrlDetails.setAccessDates( accessDates );
                existingUrlDetails.setModifiedOn( modifiedDate );
            }

            if ( url.contains( "q=" ) && existingUrlDetails.getQueryParams() == null ) {
                existingUrlDetails.setQueryParams( getQueryParamsFromUrl( url ) );
                existingUrlDetails.setModifiedOn( modifiedDate );
            }
            LOG.info( "Updating Url Details : " + existingUrlDetails );
            urlDetailsDao.updateUrlDetails( urlDetailsId, existingUrlDetails );
            LOG.info( "Updated Url Details : " + existingUrlDetails );
        }
        LOG.info( "Encrypting the url detail id into Base64." );
        String encryptedIdStr = encryptionHelper.encodeBase64( urlDetailsId );
        LOG.info( "Encrypted the url detail id into Base64. Encrypted value : " + encryptedIdStr );

        return applicationBaseUrl + CommonConstants.SHORTENED_URL_SUFFIX + "?q=" + encryptedIdStr + "&u=" + uuid;
    }


    @Override
    public String retrieveCompleteUrlForID( String encryptedIDStr ) throws InvalidInputException
    {
        if ( encryptedIDStr == null || encryptedIDStr.isEmpty() ) {
            LOG.error( "Encrypted ID passed in argument of retrieveCompleteUrlForID() is null or empty" );
            throw new InvalidInputException( "Encrypted ID passed in argument of retrieveCompleteUrlForID() is null or empty" );
        }

        LOG.info( "Decoding the encrypted ID" );
        String idStr = encryptionHelper.decodeBase64( encryptedIDStr );
        LOG.info( "Decoded encrypted ID to ID : " + idStr );

        LOG.info( "Retrieving Url Details for ID : " + idStr );
        UrlDetails urlDetails = urlDetailsDao.findUrlDetailsById( idStr );
        LOG.info( "Retrieved Url Details : " + urlDetails );

        List<Date> accessDates = new ArrayList<Date>();
        if ( urlDetails.getAccessDates() != null )
            accessDates = urlDetails.getAccessDates();
        Date modifiedDate = new Date();
        accessDates.add( modifiedDate );
        urlDetails.setAccessDates( accessDates );

        if ( urlDetails.getStatus() == CommonConstants.STATUS_NOTACCESSED ) {
            urlDetails.setStatus( CommonConstants.STATUS_ACCESSED );
            urlDetails.setModifiedOn( modifiedDate );
        }

        LOG.info( "Updating Url Details : " + urlDetails );
        urlDetailsDao.updateUrlDetails( idStr, urlDetails );
        LOG.info( "Updated Url Details : " + urlDetails );

        return urlDetails.getUrl();
    }

    /**
     * This method calls the Stream api recieveSendGridEvents for click tracking
     * @param uuid
     * @return
     */
    @Async
    @Override
    public void sendClickEvent(String uuid) {
        SendGridEventEntity sendGridEventEntity = new SendGridEventEntity();
        sendGridEventEntity.setUuid(uuid);
        sendGridEventEntity.setEvent(CommonConstants.EVENT_CLICK);
        //gets the current time in secs
        sendGridEventEntity.setTimestamp(System.currentTimeMillis()/1000);
        try {
            streamApiIntegrationBuilder.getStreamApi().streamClickEvent(asList(sendGridEventEntity));
        } catch ( StreamApiException | StreamApiConnectException e ) {
            LOG.error( "Could not send click event", e );
            LOG.info( "Saving message into local db" );
            saveMessageToStreamLater( sendGridEventEntity );
        }
    }

    private boolean saveMessageToStreamLater( SendGridEventEntity sendGridEventEntity ) {
        return streamMessagesService.saveFailedStreamClickEvent( sendGridEventEntity );
    }


    String getUrlType( String url )
    {
        if ( url.contains( CommonConstants.MANUAL_REGISTRATION ) )
            return CommonConstants.MANUAL_REGISTRATION_URL_TYPE;
        else if ( url.contains( CommonConstants.SHOW_SURVEY_PAGE_FOR_URL ) )
            return CommonConstants.SHOW_SURVEY_PAGE_FOR_URL_URL_TYPE;
        else if ( url.contains( CommonConstants.REQUEST_MAPPING_EMAIL_EDIT_VERIFICATION ) )
            return CommonConstants.EMAIL_VERIFICATION_URL_TYPE;
        else if ( url.contains( CommonConstants.SHOW_SURVEY_PAGE ) )
            return CommonConstants.SHOW_SURVEY_PAGE_URL_TYPE;
        else if ( url.contains( CommonConstants.REQUEST_MAPPING_SHOW_REGISTRATION ) )
            return CommonConstants.SHOW_EMAIL_REGISTRATION_PAGE_URL_TYPE;
        else if ( url.contains( CommonConstants.SHOW_COMPLETE_REGISTRATION_PAGE ) )
            return CommonConstants.SHOW_COMPLETE_REGISTRATION_PAGE_URL_TYPE;
        else if ( url.contains( CommonConstants.RESET_PASSWORD ) )
            return CommonConstants.RESET_PASSWORD_URL_TYPE;
        else if ( url.contains( CommonConstants.REQUEST_MAPPING_MAIL_VERIFICATION ) )
            return CommonConstants.VERIFICATION_URL_TYPE;
        else {
            LOG.info( "Found an unrecognized url : " + url );
            return CommonConstants.UNKNOWN_URL_TYPE;
        }
    }


    Map<String, String> getQueryParamsFromUrl( String url )
    {
        String urlParts[] = url.split( "\\?q=" );
        Map<String, String> queryParams = new HashMap<String, String>();
        try {
            if ( urlParts.length > 1 ) {
                String queryParamParts[] = urlParts[1].split( "&q=" );
                for ( int i = 0; i < queryParamParts.length; i++ ) {
                    queryParams.putAll( urlGenerator.decryptParameters( URLDecoder.decode(queryParamParts[i], "UTF-8") ) );
                }
            }
            return queryParams;
        } catch ( Exception e ) {
            LOG.error( "Unable to decrypt params for the url : " + url + ", Reason : ", e );
            return null;
        }
    }
}
