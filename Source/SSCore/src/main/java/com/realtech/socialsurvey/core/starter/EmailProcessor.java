package com.realtech.socialsurvey.core.starter;

import java.util.List;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.Utils;
import com.realtech.socialsurvey.core.dao.EmailDao;
import com.realtech.socialsurvey.core.entities.EmailEntity;
import com.realtech.socialsurvey.core.entities.EmailObject;
import com.realtech.socialsurvey.core.utils.EmailFormatHelper;
import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;
import com.sendgrid.SendGrid.Email;
import com.sendgrid.SendGrid.Response;


@Component
@Scope ( value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EmailProcessor implements Runnable, InitializingBean
{

    public static final Logger LOG = LoggerFactory.getLogger( EmailProcessor.class );

    @Autowired
    EmailDao emailDao;

    @Value ( "${EMAIL_RETRY_COUNT}")
    private String retryCount;

    @Value ( "${SENDGRID_SENDER_USERNAME}")
    private String sendGridUserName;

    @Value ( "${SENDGRID_SENDER_PASSWORD}")
    private String sendGridPassword;

    @Autowired
    private EmailFormatHelper emailFormatHelper;

    @Autowired
    private Utils utils;

    private SendGrid sendGrid;


    @Override
    public void run()
    {
        while ( true ) {
            List<EmailObject> emailObjectList = emailDao.findAllEmails();
            for ( EmailObject emailObject : emailObjectList ) {
                EmailEntity emailEntity = null;
                try {
                    Thread.sleep( 1000 );
                } catch ( InterruptedException ie ) {
                    LOG.error( "Exception Caught " + ie.getMessage() );
                }
                try {
                    emailEntity = (EmailEntity) utils.deserializeObject( emailObject.getEmailBinaryObject() );
                    if ( !sendEmail( emailEntity ) ) {
                        LOG.warn( " Email Sending Failed, Trying again " );
                    } else {
                        LOG.debug( "Email Sent Successfully " );
                        LOG.debug( "Removing The Email From Database" + emailObject.getId() );
                        emailDao.deleteEmail( emailObject );
                    }
                } catch ( Exception e ) {
                    LOG.error( "Exception caught " + e.getMessage() );
                }

            }

        }

    }


    private boolean sendEmail( EmailEntity emailEntity ) throws Exception
    {
        boolean mailSent = true;
        Email email = new Email();
        email.addTo( emailEntity.getRecipients().toArray( new String[emailEntity.getRecipients().size()] ) );
        email.setFrom( emailEntity.getSenderEmailId() );
        email.setFromName( emailEntity.getSenderName() );
        email.setSubject( emailEntity.getSubject() );
        email.setHtml( emailEntity.getBody() );
        email.setText( emailFormatHelper.getEmailTextFormat( emailEntity.getBody() ) );

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
