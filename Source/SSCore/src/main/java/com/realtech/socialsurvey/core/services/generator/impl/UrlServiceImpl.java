package com.realtech.socialsurvey.core.services.generator.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.UrlDetailsDao;
import com.realtech.socialsurvey.core.entities.UrlDetails;
import com.realtech.socialsurvey.core.services.generator.UrlService;
import com.realtech.socialsurvey.core.utils.EncryptionHelper;


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


    @Override
    public String shortenUrl( String url )
    {
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

            LOG.info( "Inserting Url Details into mongo : " + urlDetails );
            urlDetailsId = urlDetailsDao.insertUrlDetails( urlDetails );
            LOG.info( "Newly created Url Details id : " + urlDetailsId );
        } else {
            LOG.info( "Url Details already exist for url : " + url );
            urlDetailsId = existingUrlDetails.get_id();

            Date modifiedDate = new Date();
            List<Date> accessDates = new ArrayList<Date>();
            if ( existingUrlDetails.getAccessDates() != null )
                accessDates = existingUrlDetails.getAccessDates();
            accessDates.add( modifiedDate );
            existingUrlDetails.setAccessDates( accessDates );
            if ( existingUrlDetails.getStatus() == CommonConstants.STATUS_NOTACCESSED ) {
                existingUrlDetails.setStatus( CommonConstants.STATUS_ACCESSED );
                existingUrlDetails.setModifiedOn( modifiedDate );
            }

            LOG.info( "Updating Url Details : " + existingUrlDetails );
            urlDetailsDao.updateUrlDetails( urlDetailsId, existingUrlDetails );
            LOG.info( "Updated Url Details : " + existingUrlDetails );
        }
        LOG.info( "Encrypting the url detail id into Base64." );
        String encryptedIdStr = encryptionHelper.encodeBase64( urlDetailsId );
        LOG.info( "Encrypted the url detail id into Base64. Encrypted value : " + encryptedIdStr );

        return applicationBaseUrl + CommonConstants.SHORTENED_URL_SUFFIX + "?q=" + encryptedIdStr;
    }


    @Override
    public String retrieveCompleteUrlForID( String encryptedIDStr )
    {
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


    private String getUrlType( String url )
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

}
