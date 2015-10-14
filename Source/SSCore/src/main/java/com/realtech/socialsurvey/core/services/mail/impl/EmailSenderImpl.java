package com.realtech.socialsurvey.core.services.mail.impl;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.entities.EmailEntity;
import com.realtech.socialsurvey.core.entities.EmailObject;
import com.realtech.socialsurvey.core.entities.FileContentReplacements;
import com.realtech.socialsurvey.core.entities.SmtpSettings;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.mail.EmailSender;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.utils.FileOperations;


// JIRA: SS-7: By RM02: BOC
/**
 * Class with utility methods to send mails
 */
public final class EmailSenderImpl implements EmailSender
{

    private static final Logger LOG = LoggerFactory.getLogger( EmailSenderImpl.class );

    @Autowired
    private SmtpSettings smtpSettings;

    @Autowired
    private FileOperations fileOperations;

    @Autowired
    private GenericDao<EmailObject, Long> emailDao;


    @Override
    public boolean sendEmailByEmailEntity( EmailEntity emailEntity ) throws InvalidInputException
    {
        return false;
    }


    /**
     * Method to send mail with provided email entity and smtp settings
     * 
     * @param emailEntity
     * @throws InvalidInputException
     * @throws UndeliveredEmailException
     */
    private void sendMail( EmailEntity emailEntity ) throws InvalidInputException, UndeliveredEmailException
    {
        if ( emailEntity == null ) {
            LOG.error( "Email entity is null for sending mail" );
            throw new InvalidInputException( "Email entity is null for sending mail" );
        }
        LOG.info( "Method sendMail called with smtpSettings : " + smtpSettings + " and emailEntity : " + emailEntity );

        int port = smtpSettings.getMailPort();

        if ( emailEntity.getSenderEmailId() == null || emailEntity.getSenderEmailId().isEmpty() ) {
            LOG.error( "Sender email id is not valid for sending mail" );
            throw new InvalidInputException( "Sender email id is not valid for sending mail" );
        }
        if ( emailEntity.getSenderName() == null || emailEntity.getSenderName().isEmpty() ) {
            LOG.error( "Sender name is not valid for sending mail" );
            throw new InvalidInputException( "Sender name is not valid for sending mail" );
        }
        if ( emailEntity.getSenderPassword() == null || emailEntity.getSenderPassword().isEmpty() ) {
            LOG.error( "Sender password is not valid for sending mail" );
            throw new InvalidInputException( "Sender password is not valid for sending mail" );
        }
        List<String> recipients = emailEntity.getRecipients();
        if ( recipients == null || recipients.isEmpty() ) {
            LOG.error( "Recipient list is empty for sending mail" );
            throw new InvalidInputException( "Recipient list is empty for sending mail" );
        }

        // Create the mail session object
        Session session = createSession();

        try {
            LOG.debug( "Preparing transport object for sending mail" );
            Transport transport = session.getTransport( SmtpSettings.MAIL_TRANSPORT );
            transport.connect( smtpSettings.getMailHost(), port, emailEntity.getSenderEmailId(),
                emailEntity.getSenderPassword() );
            LOG.trace( "Connection successful" );

            // Adding the recipients to address list
            Address[] addresses = createRecipientAddresses( recipients );

            // Setting up new MimeMessage
            Message message = createMessage( emailEntity, session, addresses );

            // Send the mail
            LOG.debug( "Mail to be sent : " + emailEntity.getBody() );
            transport.sendMessage( message, addresses );
            transport.close();

            LOG.info( "Mail sent successfully. Returning from method sendMail" );
        } catch ( MessagingException e ) {
            LOG.error( "Messaging exception while sending mail", e );
            throw new UndeliveredEmailException( "Error while sending email", e );
        } catch ( UnsupportedEncodingException e ) {
            LOG.error( "Unsupported Encoding Exception while sending mail", e );
            throw new UndeliveredEmailException( "Error while sending email", e );
        } catch ( IllegalStateException e ) {
            LOG.error( "Illegal State Exception while sending mail", e );
            throw new UndeliveredEmailException( "Illegal State Exception while sending mail", e );
        }
    }


    /**
     * Method to mail with subject and body provided from templates and mail body replacements
     * required
     * 
     * @param emailEntity
     * @param subjectFileName
     * @param messageBodyReplacements
     * @throws InvalidInputException
     * @throws UndeliveredEmailException
     */
    public void sendEmailWithBodyReplacements( EmailEntity emailEntity, String subjectFileName,
        FileContentReplacements messageBodyReplacements, boolean isImmediate ) throws InvalidInputException,
        UndeliveredEmailException
    {
        LOG.info( "Method sendEmailWithBodyReplacements called for emailEntity : " + emailEntity + " subjectFileName : "
            + subjectFileName + " and messageBodyReplacements : " + messageBodyReplacements );

        if ( subjectFileName == null || subjectFileName.isEmpty() ) {
            throw new InvalidInputException( "Subject file name is null for sending mail" );
        }
        if ( messageBodyReplacements == null ) {
            throw new InvalidInputException( "Email body file name and replacements are null for sending mail" );
        }

        /**
         * Read the subject template to get the subject and set in emailEntity
         */
        LOG.debug( "Reading template to set the mail subject" );
        emailEntity.setSubject( fileOperations.getContentFromFile( subjectFileName ) );

        /**
         * Read the mail body template, replace the required contents with arguments provided and
         * set in emailEntity
         */
        LOG.debug( "Reading template to set the mail body" );
        emailEntity.setBody( fileOperations.replaceFileContents( messageBodyReplacements ) );

        // Send the mail
        sendMail( emailEntity );
        LOG.info( "Method sendEmailWithBodyReplacements completed successfully" );
    }


    public void sendEmailWithSubjectAndBodyReplacements( EmailEntity emailEntity, FileContentReplacements subjectReplacements,
        FileContentReplacements messageBodyReplacements, boolean isImmediate ) throws InvalidInputException,
        UndeliveredEmailException
    {
        LOG.info( "Method sendEmailWithBodyReplacements called for emailEntity : " + emailEntity + " subjectReplacements : "
            + subjectReplacements + " and messageBodyReplacements : " + messageBodyReplacements );

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
         * Read the mail body template, replace the required contents with arguments provided and
         * set in emailEntity
         */
        LOG.debug( "Reading template to set the mail body" );
        emailEntity.setBody( fileOperations.replaceFileContents( messageBodyReplacements ) );

        // Send the mail
        sendMail( emailEntity );
        LOG.info( "Method sendEmailWithBodyReplacements completed successfully" );
    }


    /**
     * Method to mail with subject and body provided as parameters.
     * 
     * @param emailEntity
     * @param subject
     * @param mailBody
     * @throws InvalidInputException
     * @throws UndeliveredEmailException
     */
    @Override
    public void sendEmail( EmailEntity emailEntity, String subject, String mailBody, boolean isImmediate )
        throws InvalidInputException, UndeliveredEmailException
    {
        LOG.info( "Method sendEmail called for subject : " + subject );

        if ( subject == null || subject.isEmpty() ) {
            throw new InvalidInputException( "Subject is null for sending mail" );
        }
        if ( mailBody == null ) {
            throw new InvalidInputException( "Email body is null for sending mail" );
        }

        LOG.debug( "Setting the mail subject" );
        emailEntity.setSubject( subject );

        LOG.debug( "Setting the mail body" );
        emailEntity.setBody( mailBody );

        // Send the mail
        sendMail( emailEntity );

        LOG.info( "Method sendEmail completed successfully" );

    }


    /**
     * Method to create mail session
     * 
     * @return
     */
    private Session createSession()
    {
        LOG.debug( "Preparing session object for sending mail" );
        Properties properties = new Properties();
        properties.put( "mail.smtp.auth", SmtpSettings.MAIL_SMTP_AUTH );
        properties.put( "mail.smtp.starttls.enable", SmtpSettings.MAIL_SMTP_STARTTLS_ENABLE );
        Session mailSession = Session.getInstance( properties );
        LOG.debug( "Returning the session object" );
        return mailSession;
    }


    /**
     * Method creates addresses from the recipient list
     * 
     * @param recipients
     * @return
     * @throws AddressException
     */
    private Address[] createRecipientAddresses( List<String> recipients ) throws AddressException
    {
        LOG.debug( "Creating recipient addresses" );
        StringBuilder recipientsSb = new StringBuilder();
        int count = 0;
        for ( String recipientEmailId : recipients ) {
            if ( count != 0 ) {
                recipientsSb.append( "," );
            }
            LOG.debug( "Adding recipient : " + recipientEmailId );
            recipientsSb.append( recipientEmailId );
            count++;
        }
        LOG.debug( "Recipients are : " + recipientsSb );

        // Adding the recipients to address list
        Address[] addresses = InternetAddress.parse( recipientsSb.toString() );
        return addresses;
    }


    /**
     * Method creates a Mime Message
     * 
     * @param emailEntity
     * @param session
     * @param addresses
     * @return
     * @throws UnsupportedEncodingException
     * @throws MessagingException
     * @throws InvalidInputException
     */
    private Message createMessage( EmailEntity emailEntity, Session session, Address[] addresses )
        throws UnsupportedEncodingException, MessagingException, InvalidInputException
    {
        LOG.debug( "Creating message" );
        Message message = new MimeMessage( session );
        message.setFrom( new InternetAddress( emailEntity.getSenderEmailId(), emailEntity.getSenderName() ) );

        /**
         * Adding the recipients addresses for sending mail as per the recipient type
         */
        if ( emailEntity.getRecipientType() == EmailEntity.RECIPIENT_TYPE_TO ) {
            message.setRecipients( Message.RecipientType.TO, addresses );
        } else if ( emailEntity.getRecipientType() == EmailEntity.RECIPIENT_TYPE_CC ) {
            message.setRecipients( Message.RecipientType.CC, addresses );
        } else if ( emailEntity.getRecipientType() == EmailEntity.RECIPIENT_TYPE_BCC ) {
            message.setRecipients( Message.RecipientType.BCC, addresses );
        } else {
            LOG.error( "Recipients type is not specified for sending mail" );
            throw new InvalidInputException( "Invalid recipient type found for sending mail" );
        }

        // Set the subject of mail
        message.setSubject( emailEntity.getSubject() );

        // Set the mail body
        message.setContent( emailEntity.getBody(), "text/html" );
        return message;
    }


    @Override
    @Transactional
    public void saveEmailInDb( EmailObject emailObject )
    {
        LOG.info( "Saving Email Object " );
        emailDao.save( emailObject );

    }

}