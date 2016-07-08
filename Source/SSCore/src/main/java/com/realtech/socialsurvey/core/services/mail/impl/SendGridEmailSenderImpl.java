package com.realtech.socialsurvey.core.services.mail.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.Utils;
import com.realtech.socialsurvey.core.dao.EmailDao;
import com.realtech.socialsurvey.core.entities.EmailEntity;
import com.realtech.socialsurvey.core.entities.EmailObject;
import com.realtech.socialsurvey.core.entities.FileContentReplacements;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.mail.EmailSender;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.utils.EmailFormatHelper;
import com.realtech.socialsurvey.core.utils.FileOperations;
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


    @Value ( "${SENDGRID_SENDER_NAME}")
    private String defaultSendName;

    @Value ( "${DEFAULT_EMAIL_FROM_ADDRESS}")
    private String defaultFromAddress;

    @Value ( "${SEND_MAIL}")
    private String sendMail;

    @Value ( "${SENDGRID_SENDER_USERNAME}")
    private String sendGridUserName;

    @Value ( "${SENDGRID_SENDER_PASSWORD}")
    private String sendGridPassword;
    
    @Value ( "${SALES_LEAD_EMAIL_ADDRESS}")
    private String salesLeadEmail;

    @Autowired
    private FileOperations fileOperations;

    @Autowired
    private Utils utils;

    @Autowired
    private EmailFormatHelper emailFormatHelper;

    private SendGrid sendGrid;

    @Autowired
    private EmailDao emailDao;


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
            throw new InvalidInputException( "No recipients to send mail" );
        }
        if ( emailEntity.getBody() == null || emailEntity.getBody().isEmpty() ) {
            throw new InvalidInputException( "Email body is blank." );
        }
        if ( emailEntity.getSubject() == null || emailEntity.getSubject().isEmpty() ) {
            throw new InvalidInputException( "Email subject is blank." );
        }
        List<String> recipients = emailEntity.getRecipients();
        if ( recipients == null || recipients.isEmpty() ) {
            LOG.error( "Recipient list is empty for sending mail" );
            throw new InvalidInputException( "Recipient list is empty for sending mail" );
        }

        boolean mailSent = true;
        Email email = new Email();
        email.addTo( emailEntity.getRecipients().toArray( new String[emailEntity.getRecipients().size()] ) );
        //No need to encrypt user id , JIRA SS-60
       // String encryptedFromId = utils.encryptUserEmailId( emailEntity.getSenderEmailId() );
      //  email.setFrom( encryptedFromId );
        email.setFrom( emailEntity.getSenderEmailId() );
        email.setFromName( emailEntity.getSenderName() );
        email.setSubject( emailEntity.getSubject() );
        email.setHtml( emailEntity.getBody() );
        email.setText( emailFormatHelper.getEmailTextFormat( emailEntity.getBody() ) );
        if ( sendMailToSalesLead ) {
            email.addBcc( salesLeadEmail );
        }

        if ( emailEntity.getAttachmentDetail() != null ) {
            Iterator<Map.Entry<String, String>> entries = emailEntity.getAttachmentDetail().entrySet().iterator();
            while ( entries.hasNext() ) {
                Entry<String, String> entry = entries.next();
                if ( entry.getKey() != null && entry.getValue() != null ) {
                    File file = null;
                    FileInputStream fileInputStream = null;
                    try {
                        file = new File( entry.getValue() );
                        fileInputStream = new FileInputStream( file );

                        InputStream inputStream = null;
                        inputStream = new ByteArrayInputStream( FileUtils.readFileToByteArray( file ) );
                        email.addAttachment( entry.getKey(), inputStream );

                    } catch ( IOException e ) {
                        LOG.error( "Exception caught " + e.getMessage() );
                    } finally {
                        if ( file != null ) {
                            if ( file.exists() ) {
                                file.delete();
                            }
                        }
                        if ( fileInputStream != null ) {
                            try {
                                fileInputStream.close();
                            } catch ( IOException e ) {
                                LOG.error( "Exception caught " + e.getMessage() );
                            }
                        }
                    }
                }
            }
        }

        Response response = null;
        try {
            LOG.debug( "About to send mail. " + emailEntity.toString() );
            response = sendGrid.send( email );
            LOG.debug( "Sent the mail. " + emailEntity.toString() );
        } catch ( SendGridException e ) {
            LOG.error( "Exception while sending the mail. " + emailEntity.toString(), e );
            mailSent = false;
        }

        if ( response.getStatus() ) {
            LOG.debug( "Mail sent successfully to " + emailEntity.toString() );
        } else {
            LOG.error( "Could not send mail to " + emailEntity.toString() + ". Reason: " + response.getMessage() );
            mailSent = false;
        }
        return mailSent;
    }


    void saveEmail( EmailEntity emailEntity, boolean holdSendingMail ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "Sending mail: " + emailEntity.toString() );
        if ( emailEntity.getRecipients() == null || emailEntity.getRecipients().isEmpty() ) {
            throw new InvalidInputException( "No recipients to send mail" );
        }
        if ( emailEntity.getSenderEmailId() == null || emailEntity.getSenderEmailId().isEmpty() ) {
            LOG.debug( "Setting default from email id" );
            emailEntity.setSenderEmailId( defaultFromAddress );
        }
        if ( emailEntity.getSenderName() == null || emailEntity.getSenderName().isEmpty() ) {
            LOG.debug( "Setting default sender name" );
            emailEntity.setSenderName( defaultSendName );
        }
        if ( emailEntity.getBody() == null || emailEntity.getBody().isEmpty() ) {
            throw new InvalidInputException( "Email body is blank." );
        }
        if ( emailEntity.getSubject() == null || emailEntity.getSubject().isEmpty() ) {
            throw new InvalidInputException( "Email subject is blank." );
        }

        EmailObject emailObject = new EmailObject();
        byte[] emailBinaryObject = null;
        try {
            emailBinaryObject = utils.serializeObject( emailEntity );
        } catch ( IOException ie ) {
            LOG.error( "Exception caught " + ie.getMessage() );
        }
        if(holdSendingMail){
        	emailObject.setHoldSendingMail(CommonConstants.YES);
        }else{
        	emailObject.setHoldSendingMail(CommonConstants.NO);
        }
        emailObject.setCreatedOn( new Timestamp( System.currentTimeMillis() ) );
        emailObject.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        emailObject.setCreatedBy( CommonConstants.ADMIN_USER_NAME );
        emailObject.setModifiedBy( CommonConstants.ADMIN_USER_NAME );
        emailObject.setEmailBinaryObject( emailBinaryObject );

        saveEmailInDb( emailObject );
    }


    @Override
    public void sendEmailWithBodyReplacements( EmailEntity emailEntity, String subjectFileName,
        FileContentReplacements messageBodyReplacements, boolean isImmediate, boolean holdSendingMail )
        throws InvalidInputException, UndeliveredEmailException
    {
        sendEmailWithBodyReplacements( emailEntity, subjectFileName, messageBodyReplacements, isImmediate, holdSendingMail,
            false );
    }
    
    
    @Override
    public void sendEmailWithBodyReplacements( EmailEntity emailEntity, String subjectFileName,
        FileContentReplacements messageBodyReplacements, boolean isImmediate, boolean holdSendingMail, boolean sendMailToSalesLead ) throws InvalidInputException,
        UndeliveredEmailException
    {
        LOG.info( "Method sendEmailWithBodyReplacements called for emailEntity : " + emailEntity + " subjectFileName : "
            + subjectFileName + " and messageBodyReplacements : " + messageBodyReplacements );

        // check if mail needs to be sent
        if ( sendMail.equals( CommonConstants.YES_STRING ) ) {
            if ( subjectFileName == null || subjectFileName.isEmpty() ) {
                throw new InvalidInputException( "Subject file name is null for sending mail" );
            }
            if ( messageBodyReplacements == null ) {
                throw new InvalidInputException( "Email body file name  and replacements are null for sending mail" );
            }

            /**
             * Read the subject template to get the subject and set in emailEntity
             */
            LOG.debug( "Reading template to set the mail subject" );
            emailEntity.setSubject( fileOperations.getContentFromFile( subjectFileName ) );

            /**
             * Read the mail body template, replace the required contents with arguments provided
             * and set in emailEntity
             */
            LOG.debug( "Reading template to set the mail body" );
            emailEntity.setBody( fileOperations.replaceFileContents( messageBodyReplacements ) );

            // Send the mail
            if ( isImmediate ) {
                sendEmailByEmailEntity( emailEntity, sendMailToSalesLead );
            } else {
                saveEmail( emailEntity, holdSendingMail );
            }

        }

        LOG.info( "Method sendEmailWithBodyReplacements completed successfully" );
    }


    @Override
    public void sendEmailWithSubjectAndBodyReplacements( EmailEntity emailEntity, FileContentReplacements subjectReplacements,
        FileContentReplacements messageBodyReplacements, boolean isImmediate, boolean holdSendingMail ) throws InvalidInputException,
        UndeliveredEmailException
    {
        LOG.info( "Method sendEmailWithSubjectAndBodyReplacements called for emailEntity : " + emailEntity
            + " subjectReplacements : " + subjectReplacements + " and messageBodyReplacements : " + messageBodyReplacements );

        if ( sendMail.equals( CommonConstants.YES_STRING ) ) {
            if ( subjectReplacements == null ) {
                throw new InvalidInputException( "Email subject file name and replacements are null for sending mail" );
            }
            if ( messageBodyReplacements == null ) {
                throw new InvalidInputException( "Email body file name and replacements are null for sending mail" );
            }

            /**
             * Read the subject template to get the subject and set in emailEntity
             */
            LOG.debug( "Reading template to set the mail subject" );
            emailEntity.setSubject( fileOperations.replaceFileContents( subjectReplacements ) );

            /**
             * Read the mail body template, replace the required contents with arguments provided
             * and set in emailEntity
             */
            LOG.debug( "Reading template to set the mail body" );
            emailEntity.setBody( fileOperations.replaceFileContents( messageBodyReplacements ) );

            // Send the mail
            if ( isImmediate ) {
                sendEmailByEmailEntity( emailEntity, false );
            } else {
                saveEmail( emailEntity, holdSendingMail );
            }
            LOG.info( "Method sendEmailWithSubjectAndBodyReplacements completed successfully" );
        }
    }


    @Override
    public void sendEmail( EmailEntity emailEntity, String subject, String mailBody, boolean isImmediate, boolean holdSendingMail )
        throws InvalidInputException, UndeliveredEmailException
    {
        LOG.info( "Method sendEmail called for subject : " + subject );
        if ( sendMail.equals( CommonConstants.YES_STRING ) ) {
            if ( subject == null || subject.isEmpty() ) {
                throw new InvalidInputException( "Subject is null for sending mail" );
            }
            if ( mailBody == null ) {
                throw new InvalidInputException( "Email body is null for sending mail" );
            }

            LOG.debug( "Setting the mail subject and body" );
            emailEntity.setSubject( subject );
            emailEntity.setBody( mailBody );

            // Send the mail
            if ( isImmediate ) {
                sendEmailByEmailEntity( emailEntity, false );
            } else {
                saveEmail( emailEntity, holdSendingMail );
            }
        }
        LOG.info( "Method sendEmail completed successfully" );
    }


    @Override
    public void saveEmailInDb( EmailObject emailObject )
    {
        LOG.info( "Saving Email Object " );
        emailDao.saveEmailObjectInDB( emailObject );

    }


    @Override
    public void afterPropertiesSet() throws Exception
    {
        LOG.info( "Settings Up sendGrid gateway" );

        if ( sendGrid == null ) {
            LOG.info( "Initialising Sendgrid gateway with " + sendGridUserName + " and " + sendGridPassword );
            sendGrid = new SendGrid( sendGridUserName, sendGridPassword );
            LOG.info( "Sendgrid gateway initialised!" );
        }

    }

}