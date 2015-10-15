package com.realtech.socialsurvey.core.starter;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.commons.Utils;
import com.realtech.socialsurvey.core.dao.EmailDao;
import com.realtech.socialsurvey.core.entities.EmailEntity;
import com.realtech.socialsurvey.core.entities.EmailObject;
import com.realtech.socialsurvey.core.services.mail.EmailSender;


@Component
@Scope ( value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EmailProcessor implements Runnable
{

    public static final Logger LOG = LoggerFactory.getLogger( EmailProcessor.class );

    @Autowired
    EmailDao emailDao;

    @Value ( "${EMAIL_RETRY_COUNT}")
    private String retryCount;

    @Autowired
    private Utils utils;

    @Autowired
    private EmailSender emailSender;


    @Override
    public void run()
    {
        while ( true ) {
            List<EmailObject> emailObjectList = emailDao.findAllEmails();
            if ( emailObjectList.isEmpty() ) {
                try {
                    Thread.sleep( 60000 );
                } catch ( InterruptedException ie ) {
                    LOG.error( "Exception Caught " + ie.getMessage() );
                }

            }
            for ( EmailObject emailObject : emailObjectList ) {
                EmailEntity emailEntity = null;
                try {
                    emailEntity = (EmailEntity) utils.deserializeObject( emailObject.getEmailBinaryObject() );
                    if ( !emailSender.sendEmailByEmailEntity( emailEntity ) ) {
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

}
