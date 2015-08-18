package com.realtech.socialsurvey.core.services.mail.impl;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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


/**
 * Uses sendgrid api to send mails
 * 
 * @author nishit
 */
@Component
public class SendGridEmailSenderImpl implements EmailSender
{

    private static final Logger LOG = LoggerFactory.getLogger( SendGridEmailSenderImpl.class );


    @Value ( "${SENDGRID_SENDER_NAME}")
    private String defaultSendName;

    @Value ( "${DEFAULT_EMAIL_FROM_ADDRESS}")
    private String defaultFromAddress;

    @Value ( "${SEND_MAIL}")
    private String sendMail;

    @Autowired
    private FileOperations fileOperations;

    @Autowired
    private Utils utils;

    @Autowired
    private EmailFormatHelper emailFormatHelper;


    @Autowired
    private EmailDao emailDao;


    private void saveEmail( EmailEntity emailEntity ) throws InvalidInputException, UndeliveredEmailException
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
        emailObject.setEmailBinaryObject( emailBinaryObject );

        saveEmailInDb( emailObject );
    }


    @Override
    public void sendEmailWithBodyReplacements( EmailEntity emailEntity, String subjectFileName,
        FileContentReplacements messageBodyReplacements ) throws InvalidInputException, UndeliveredEmailException
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
            saveEmail( emailEntity );
        }

        LOG.info( "Method sendEmailWithBodyReplacements completed successfully" );
    }


    @Override
    public void sendEmailWithSubjectAndBodyReplacements( EmailEntity emailEntity, FileContentReplacements subjectReplacements,
        FileContentReplacements messageBodyReplacements ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.info( "Method sendEmailWithSubjectAndBodyReplacements called for emailEntity : " + emailEntity + " subjectReplacements : "
            + subjectReplacements + " and messageBodyReplacements : " + messageBodyReplacements );

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
            saveEmail( emailEntity );
            LOG.info( "Method sendEmailWithSubjectAndBodyReplacements completed successfully" );
        }
    }


    @Override
    public void sendEmail( EmailEntity emailEntity, String subject, String mailBody ) throws InvalidInputException,
        UndeliveredEmailException
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
            saveEmail( emailEntity );
        }
        LOG.info( "Method sendEmail completed successfully" );
    }


    @Override
    public void saveEmailInDb( EmailObject emailObject )
    {
        LOG.info( "Saving Email Object " );
        emailDao.saveEmailObjectInDB( emailObject );

    }

}