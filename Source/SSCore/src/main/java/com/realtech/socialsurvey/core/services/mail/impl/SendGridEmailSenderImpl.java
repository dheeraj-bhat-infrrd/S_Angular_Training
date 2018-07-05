package com.realtech.socialsurvey.core.services.mail.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.EmailAttachment;
import com.realtech.socialsurvey.core.entities.EmailEntity;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.mail.EmailSender;
import com.realtech.socialsurvey.core.services.mail.EmailUnsubscribeService;
import com.realtech.socialsurvey.core.utils.EmailFormatHelper;
import com.sendgrid.SendGrid;
import com.sendgrid.SendGrid.Email;
import com.sendgrid.SendGrid.Response;
import com.sendgrid.SendGridException;


/**
 * Uses sendgrid api to send mails
 * 
 * @author nishit
 */
@Component
public class SendGridEmailSenderImpl implements EmailSender, InitializingBean
{

    private static final Logger LOG = LoggerFactory.getLogger( SendGridEmailSenderImpl.class );

    //FOR DEFAULT SENDGRID ACCOUNT 
    @Value ( "${SENDGRID_SENDER_SOCIALSURVEYME_NAME}")
    private String defaultSendName;

    @Value ( "${SENDGRID_SENDER_SOCIALSURVEYME_USERNAME}")
    private String sendGridUserName;

    @Value ( "${SENDGRID_SENDER_SOCIALSURVEYME_PASSWORD}")
    private String sendGridPassword;

    //FOR SOCIALSURVEY.US SENDGRID ACCOUNT
    @Value ( "${SENDGRID_SENDER_SOCIALSURVEYUS_NAME}")
    private String socialSurveyUsSendName;

    @Value ( "${SENDGRID_SENDER_SOCIALSURVEYUS_USERNAME}")
    private String sendGridSocialSurveyUsUserName;

    @Value ( "${SENDGRID_SENDER_SOCIALSURVEYUS_PASSWORD}")
    private String sendGridSocialSurveyUsPassword;


    @Value ( "${DEFAULT_EMAIL_FROM_ADDRESS}")
    private String defaultFromAddress;

    @Value ( "${SEND_MAIL}")
    private String sendMail;

    @Value ( "${SALES_LEAD_EMAIL_ADDRESS}")
    private String salesLeadEmail;

    private String sendEmailThrough;

    @Autowired
    private EmailFormatHelper emailFormatHelper;

    //since we have two diff sendGrid accounts
    private SendGrid sendGrid1;

    private SendGrid sendGrid2;
    
    @Autowired
    private EmailUnsubscribeService unsubscribeService;


    @Override
    public boolean sendEmailByEmailEntity( EmailEntity emailEntity, boolean sendMailToSalesLead ) throws InvalidInputException
    {
        LOG.debug( "metod sendEmail started" );

        if ( emailEntity.getSenderEmailId() == null || emailEntity.getSenderEmailId().isEmpty() ) {
            LOG.debug( "Setting default from email id" );
            emailEntity.setSenderEmailId( defaultFromAddress );
        }
        if ( emailEntity.getSenderName() == null || emailEntity.getSenderName().isEmpty() ) {
            LOG.debug( "Setting default sender name" );
            emailEntity.setSenderName( defaultSendName );
        }
        if ( emailEntity.getRecipients() == null || emailEntity.getRecipients().isEmpty() ) {
        	LOG.warn("No recipients to send mail.");
            throw new InvalidInputException( "No recipients to send mail" );
        }
        if ( emailEntity.getBody() == null || emailEntity.getBody().isEmpty() ) {
        	LOG.warn("Email body is blank.");
            throw new InvalidInputException( "Email body is blank." );
        }
        if ( emailEntity.getSubject() == null || emailEntity.getSubject().isEmpty() ) {
        	LOG.warn("Email subject is blank.");
            throw new InvalidInputException( "Email subject is blank." );
        }
        //setting default if null and actual value if it exists in mongo
        if ( emailEntity.getSendEmailThrough() == null || emailEntity.getSendEmailThrough().isEmpty() ) {
            sendEmailThrough = CommonConstants.SEND_EMAIL_THROUGH_SOCIALSURVEY_ME;

        } else {
            sendEmailThrough = emailEntity.getSendEmailThrough();
        }
        
        // Remove the unsubscribed recipient id.
        List<String> recipients = emailEntity.getRecipients();
        List<String> newRecipients = emailEntity.getRecipients();
        long companyId = emailEntity.getCompanyId();
        for(String recipient : recipients) {
            if(unsubscribeService.isUnsubscribed( recipient, companyId )) {
                newRecipients.remove( recipient );
            }
        }
        recipients = newRecipients;
        
        if ( recipients == null || recipients.isEmpty() ) {
            LOG.warn( "Recipient list is empty for sending mail" );
            throw new InvalidInputException( "Recipient list is empty for sending mail" );
        }

        boolean mailSent = true;
        Email email = new Email();
        email.addTo( recipients.toArray( new String[recipients.size()] ) );
        //No need to encrypt user id , JIRA SS-60
        email.setFrom( emailEntity.getSenderEmailId() );
        email.setFromName( emailEntity.getSenderName() );
        email.setSubject( emailEntity.getSubject() );
        email.setHtml( emailEntity.getBody() );
        email.setText( emailFormatHelper.getEmailTextFormat( emailEntity.getBody() ) );
        if ( sendMailToSalesLead ) {
            email.addBcc( salesLeadEmail );
        }

        if ( emailEntity.getAttachments() != null ) {
            for ( EmailAttachment emailAttachment : emailEntity.getAttachments()) {
                InputStream input = null;
                try {
                		LOG.info("Downloading file "  + emailAttachment.getFilePath());
                    input = new URL(emailAttachment.getFilePath()).openConnection().getInputStream();
                    email.addAttachment( emailAttachment.getFileName(), input );
                    LOG.info("Email atachment has been added. file "  + emailAttachment.getFilePath());
                } catch ( IOException e ) {
                    LOG.error( "Exception occurred when downloading attachment file", e );
                } finally {
                    if(input != null){
                        try {
                            input.close();
                        } catch ( IOException e ) {
                            LOG.error( "Exception occurred when closing connection to attachment file", e );
                        }
                    }
                }
            }
        }

        Response response = null;
        try {
            LOG.warn( "About to send mail. {}", emailEntity.toString() );
            //sets credentials according to account selected
            if ( sendEmailThrough.equals( CommonConstants.SEND_EMAIL_THROUGH_SOCIALSURVEY_US ) ) {
                response = sendGrid2.send( email );
            } else {
                response = sendGrid1.send( email );
            }
           
            LOG.info( "Sent the mail. {}",emailEntity.toString() );
        } catch ( SendGridException e ) {
            LOG.error( "Exception while sending the mail.", e );
            mailSent = false;
        }

        if ( response.getStatus() ) {
            LOG.info( "Mail sent successfully to {}", emailEntity.toString() );
        } else {
            LOG.error( "Could not send mail to {}. Reason: {}",emailEntity.toString(),response.getMessage() );
            mailSent = false;
        }
        return mailSent;
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        LOG.info( "Settings Up sendGrid gateway" );

        //assign both credentials
        if ( sendGrid1 == null || sendGrid2 == null ) {
            LOG.info( "Initialising Sendgrid gateway with " + sendGridUserName + " and " + sendGridPassword + "or"
                + sendGridSocialSurveyUsUserName + " and " + sendGridSocialSurveyUsPassword );
            sendGrid1 = new SendGrid( sendGridUserName, sendGridPassword );
            sendGrid2 = new SendGrid( sendGridSocialSurveyUsUserName, sendGridSocialSurveyUsPassword );
            LOG.info( "Sendgrid gateway initialised!" );
        }

    }

}