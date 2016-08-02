package com.realtech.socialsurvey.core.services.social.impl;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.social.SocialMediaExceptionHandler;

import facebook4j.FacebookException;


@Component
public class SocialMediaExceptionHandlerImpl implements SocialMediaExceptionHandler
{

    Logger LOG = LoggerFactory.getLogger( SocialMediaExceptionHandlerImpl.class );

    @Autowired
    private EmailServices emailServices;

    private final int fbTokenExpireErrorCode = 190;

    private final int[] fbTokenExpireErrorSubCode = { 458, 459, 460, 463, 464, 467 };

    @Override
    public void handleFacebookException( FacebookException e, OrganizationUnitSettings settings )
    {
       

        LOG.debug( "Inside Method handleFacebookException" );
        if ( e.getErrorCode() == fbTokenExpireErrorCode ) {
            if ( ArrayUtils.contains( fbTokenExpireErrorSubCode, e.getErrorSubcode() ) ) {
                String emailId = null;
                String name = "User";

                String errorMsg = "";
                errorMsg += "<br>" + "Your Facebook has expired." + "<br><br>";

                if ( settings != null && settings.getContact_details() != null ) {
                    if ( settings.getContact_details().getMail_ids() != null
                        && settings.getContact_details().getMail_ids().getWork() != null ) {
                        emailId = settings.getContact_details().getMail_ids().getWork();
                        LOG.info( "Sending bug mail to admin" );
                        //TODO update email template and than send mail with new parameter
                        //emailServices.sendSocialMediaTokenExpiryEmail( name, errorMsg, emailId );
                        
                    }
                }


            }
        }

    }


    @Override
    public void handleLinkedinException( OrganizationUnitSettings settings )
    {
        LOG.debug( "Inside Method handleLinkedinException" );
        String emailId = null;
        String name = "User";
        String errorMsg = "";
        errorMsg += "<br>" + "Your Linkedin token has expired." + "<br><br>";
        LOG.info( "Sending bug mail to admin" );
        
        if ( settings != null && settings.getContact_details() != null ) {
            if ( settings.getContact_details().getMail_ids() != null
                && settings.getContact_details().getMail_ids().getWork() != null ) {
                emailId = settings.getContact_details().getMail_ids().getWork();
                LOG.info( "Sending bug mail to admin" );
                //TODO update email template and than send mail with new parameter
                 //emailServices.sendSocialMediaTokenExpiryEmail( name, errorMsg, emailId );
               
            }
        }
    }


}
