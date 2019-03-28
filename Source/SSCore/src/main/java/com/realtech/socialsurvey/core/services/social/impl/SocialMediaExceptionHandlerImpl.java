package com.realtech.socialsurvey.core.services.social.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.FacebookToken;
import com.realtech.socialsurvey.core.entities.LinkedInToken;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.social.SocialManagementService;
import com.realtech.socialsurvey.core.services.social.SocialMediaExceptionHandler;

import facebook4j.FacebookException;


@Component
public class SocialMediaExceptionHandlerImpl implements SocialMediaExceptionHandler
{

    Logger LOG = LoggerFactory.getLogger( SocialMediaExceptionHandlerImpl.class );

    @Value ( "${APPLICATION_BASE_URL}")
    private String appBaseUrl;

    @Autowired
    private EmailServices emailServices;

    @Autowired
    private SocialManagementService socialManagementService;
    
    @Autowired
    private URLGenerator urlGenerator;

    private final int fbTokenInsufficientPermissionErrorCode = 200;
    
    private final int fbTokenExpireErrorCode = 190;

    private final int[] fbTokenExpireErrorSubCode = { 458, 459, 460, 463, 464, 467 };


    @Override
    public void handleFacebookException( FacebookException e, OrganizationUnitSettings settings, String collectionName )
    {
        LOG.debug( " Method handleFacebookException started" );
        if ( (e.getErrorCode() == fbTokenExpireErrorCode &&  ArrayUtils.contains( fbTokenExpireErrorSubCode, e.getErrorSubcode() )) || e.getErrorCode() == fbTokenInsufficientPermissionErrorCode) {
          if ( settings.getSocialMediaTokens() != null && settings.getSocialMediaTokens().getFacebookToken() != null ) {
                    FacebookToken facebookToken = settings.getSocialMediaTokens().getFacebookToken();
                    //check if already a mail has been sent
                    if ( !facebookToken.isTokenExpiryAlertSent() ) {
                        LOG.debug( "Alert Mail hasn't send to sending alert mail for entity" );
                        //send mail to email id
                        String emailId = generateAndSendSocialMedialTokenExpiryMail( settings , collectionName , CommonConstants.FACEBOOK_SOCIAL_SITE );
                        //update alert detail in token
                        if ( emailId != null ) {
                            facebookToken.setTokenExpiryAlertEmail( emailId );
                            facebookToken.setTokenExpiryAlertSent( true );
                            facebookToken.setTokenExpiryAlertTime( new Date() );
                            socialManagementService.updateFacebookToken( collectionName, settings.getIden(), facebookToken );

                        }
                    }
                }
            
        }

        LOG.debug( " Method handleFacebookException ended" );
    }


    /**
     * 
     */
    @Override
    public void handleLinkedinException( OrganizationUnitSettings settings, String collectionName )
    {
        LOG.debug( "Method handleLinkedinException started" );
        if ( settings.getSocialMediaTokens() != null && settings.getSocialMediaTokens().getLinkedInToken() != null ) {
            LinkedInToken linkedInToken = settings.getSocialMediaTokens().getLinkedInToken();
            if ( !linkedInToken.isTokenExpiryAlertSent() ) {
                String emailId = generateAndSendSocialMedialTokenExpiryMail( settings , collectionName , CommonConstants.LINKEDIN_SOCIAL_SITE);
                //update alert detail in token
                if ( emailId != null ) {
                    linkedInToken.setTokenExpiryAlertEmail( emailId );
                    linkedInToken.setTokenExpiryAlertSent( true );
                    linkedInToken.setTokenExpiryAlertTime( new Date() );
                    socialManagementService.updateLinkedinToken( collectionName, settings.getIden(), linkedInToken );
                }
            }
        }
        LOG.debug( "Method handleLinkedinException ended" );
    }
    
    /**
     * 
     */
    @Override
    public void handleLinkedinV2Exception( OrganizationUnitSettings settings, String collectionName )
    {
        LOG.info( "Method handleLinkedinException started" );

        if ( settings.getSocialMediaTokens() != null && settings.getSocialMediaTokens().getLinkedInV2Token() != null ) {
            LinkedInToken linkedInToken = settings.getSocialMediaTokens().getLinkedInV2Token();
            if ( !linkedInToken.isTokenExpiryAlertSent() ) {
                String emailId = generateAndSendSocialMedialTokenExpiryMail( settings , collectionName , CommonConstants.LINKEDIN_SOCIAL_SITE);
                //update alert detail in token
                if ( emailId != null ) {
                    linkedInToken.setTokenExpiryAlertEmail( emailId );
                    linkedInToken.setTokenExpiryAlertSent( true );
                    linkedInToken.setTokenExpiryAlertTime( new Date() );
                    socialManagementService.updateLinkedinV2Token( collectionName, settings.getIden(), linkedInToken );
                }
            }
        }
        LOG.info( "Method handleLinkedinException ended" );
    }


    /**
     * 
     */
    @Override
    public String generateAndSendSocialMedialTokenExpiryMail( OrganizationUnitSettings settings  , String collectionName , String socialMediaType)
    {
        LOG.debug( "Method generateAndSendSocialMedialTokenExpiryMail started()" );
        String emailId = null;
        String name = "User";
        if ( settings != null && settings.getContact_details() != null ) {
            if ( settings.getContact_details().getMail_ids() != null
                && settings.getContact_details().getMail_ids().getWork() != null ) {
                emailId = settings.getContact_details().getMail_ids().getWork();
                name = settings.getContact_details().getName();
                LOG.info( "Sending SocialMedialTokenExpiryMail to {}", emailId );
                try {
                    //TODO update the update connection url
                    String loginUrl = getApplicationLoginUrl();
                    String columnName = getCoulmnNameForCollection( collectionName );
                    String connectionUpdateUrl = generateUpdateTokenUrl( socialMediaType, columnName, settings.getIden() );
                    emailServices.sendSocialMediaTokenExpiryEmail( name, emailId, connectionUpdateUrl, loginUrl , socialMediaType );
                    LOG.debug( "Method generateAndSendSocialMedialTokenExpiryMail ended()" );
                    return emailId;
                } catch ( InvalidInputException | UndeliveredEmailException e ) {
                    LOG.warn( "Error while sending social media token expiry mail ", e );
                    return null;
                }
            }
        }
        LOG.debug( "Method generateAndSendSocialMedialTokenExpiryMail ended()" );
        return null;
    }

    
    private String generateUpdateTokenUrl(String socialMedia , String columnName , long columnValue ) throws InvalidInputException{
        LOG.info( "Method generateUpdateTokenUrl started" );
        
        Map<String ,String> params = new HashMap<String ,String>();
        params.put( CommonConstants.URL_PARAM_SOCIAL_MEDIA_TYPE, socialMedia );
        params.put( CommonConstants.URL_PARAM_COLUMN_NAME, columnName );
        params.put( CommonConstants.URL_PARAM_COLUMN_VALUE, String.valueOf(columnValue) );
        
        String url = urlGenerator.generateUrl( params, appBaseUrl + CommonConstants.SOCIAL_AUTH_FROM_EMAIL_URL );
        
        LOG.info( "Method generateUpdateTokenUrl ended" );
        return url;
    }

    /**
     * 
     * @return
     */
    private String getApplicationLoginUrl()
    {
        LOG.info( "inside method getApplicationLoginUrl" );
        String loginUrl = appBaseUrl + CommonConstants.LOGIN_URL;
        LOG.debug( "application login url is " + loginUrl );
        return loginUrl;
    }
    
    
    private String getCoulmnNameForCollection(String collectionName)
    {
        LOG.info( "Inside method getCoulmnNameForCollection" );
        if(collectionName.equals( MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION )){
            return CommonConstants.COMPANY_ID_COLUMN;
        }else if(collectionName.equals( MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION )){
            return CommonConstants.REGION_ID_COLUMN;
        }else if(collectionName.equals( MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION )){
            return CommonConstants.BRANCH_ID_COLUMN;
        }else if(collectionName.equals( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION )){
            return CommonConstants.AGENT_ID_COLUMN;
        }
        return null;
    }
}
