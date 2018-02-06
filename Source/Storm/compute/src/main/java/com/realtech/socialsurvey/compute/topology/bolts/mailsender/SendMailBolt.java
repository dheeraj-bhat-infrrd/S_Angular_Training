package com.realtech.socialsurvey.compute.topology.bolts.mailsender;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.common.APIOperations;
import com.realtech.socialsurvey.compute.common.ComputeConstants;
import com.realtech.socialsurvey.compute.common.EmailConstants;
import com.realtech.socialsurvey.compute.common.LocalPropertyFileHandler;
import com.realtech.socialsurvey.compute.entities.EmailAttachment;
import com.realtech.socialsurvey.compute.entities.EmailMessage;
import com.realtech.socialsurvey.compute.entities.SolrEmailMessageWrapper;
import com.realtech.socialsurvey.compute.exception.QueueingMessageProcessingException;
import com.realtech.socialsurvey.compute.exception.SolrProcessingException;
import com.realtech.socialsurvey.compute.services.FailedMessagesService;
import com.realtech.socialsurvey.compute.services.impl.FailedMessagesServiceImpl;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck;
import com.realtech.socialsurvey.compute.topology.bolts.mailsender.exception.MailProcessingException;
import com.realtech.socialsurvey.compute.topology.bolts.mailsender.exception.TemporaryMailProcessingException;
import com.realtech.socialsurvey.compute.utils.ConversionUtils;
import com.sendgrid.Attachments;
import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Personalization;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;


/**
 * Sends mail to the recipients in the email entity from the tuple
 * @author nishit
 *
 */
public class SendMailBolt extends BaseComputeBoltWithAck
{

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger( SendMailBolt.class );

    private static final List<String> IGNORE_TYPES = new ArrayList<>(
            Arrays.asList( EmailConstants.EMAIL_TYPE_FATAL_EXCEPTION_EMAIL, EmailConstants.EMAIL_TYPE_WEB_EXCEPTION_EMAIL, EmailConstants.EMAIL_TYPE_REPORT_BUG_MAIL_TO_ADMIN ) );

    private transient SendGrid sendgridMe;
    private transient SendGrid sendgridUs;

    private String sendgridMeApiKey;
    private String sendgridUsApiKey;


    private synchronized SendGrid getSendGridME()
    {
        LOG.info( "Creating sendgrid ME instance" );
        if ( sendgridMe == null ) {
            if ( sendgridMeApiKey != null ) {
                LOG.debug( "Sendgrid ME api key found." );
                sendgridMe = new SendGrid( sendgridMeApiKey );
            } else {
                LOG.error( "Send grid ME api key is null. Should not have reached this place." );
            }
        }
        return sendgridMe;
    }


    private synchronized SendGrid getSendGridUS()
    {
        LOG.info( "Creating sendgrid US instance" );
        if ( sendgridUs == null ) {
            LOG.debug( "Sendgrid US api key found." );
            if ( sendgridUsApiKey != null ) {
                sendgridUs = new SendGrid( sendgridUsApiKey );
            } else {
                LOG.error( "Send grid US api key is null. Should not have reached this place." );
            }

        }
        return sendgridUs;
    }


    @Override
    public void declareOutputFields( OutputFieldsDeclarer declarer )
    {
        declarer.declare( new Fields( "success", "mailType", "emailMessage", "isTemporaryException" ) );

    }


    @Override
    public void prepare( @SuppressWarnings ( "rawtypes") Map stormConf, TopologyContext context, OutputCollector collector )
    {
        super.prepare( stormConf, context, collector );
        sendgridMeApiKey = (String) stormConf.get( ComputeConstants.SENDGRID_ME_API_KEY );
        sendgridUsApiKey = (String) stormConf.get( ComputeConstants.SENDGRID_US_API_KEY );
    }


    private void validateEmailMessage( EmailMessage emailMessage )
    {
        LOG.debug( "Validating email message" );

        if ( emailMessage.getRecipients() == null || emailMessage.getRecipients().isEmpty() ) {
            LOG.warn( "Recipient list is empty for sending mail." );
            throw new QueueingMessageProcessingException( "No recipients to send mail" );
        }
        if ( emailMessage.getBody() == null || emailMessage.getBody().isEmpty() ) {
            LOG.warn( "Email message body is blank." );
            throw new QueueingMessageProcessingException( "Email body is blank." );
        }
        if ( emailMessage.getSubject() == null || emailMessage.getSubject().isEmpty() ) {
            LOG.warn( "Email subject is blank" );
            throw new QueueingMessageProcessingException( "Email subject is blank." );
        }

    }


    private String sendMail( EmailMessage emailMessage )
    {
        String responseId = null;
        if ( !emailMessage.isHoldSendingMail() && !IGNORE_TYPES.contains( emailMessage.getMailType() ) ) {
            Mail mail = prepareMail( emailMessage );
            SendGrid sendGrid;
            if ( emailMessage.getSendEmailThrough().equals( ComputeConstants.SEND_EMAIL_THROUGH_SOCIALSURVEY_US ) ) {
                sendGrid = getSendGridUS();
            } else {
                sendGrid = getSendGridME();
            }
            Request request = new Request();
            request.setMethod( Method.POST );
            request.setEndpoint( "mail/send" );
            try {
                request.setBody( mail.build() );
                Response response = sendGrid.api( request );
                LOG.debug( "Response status code {},\nresponse body: {},\nheader: {}", response.getStatusCode(),
                        response.getBody(), response.getHeaders() );
                if ( response.getStatusCode() != HttpStatus.SC_ACCEPTED ) {
                    LOG.warn( "Sending mail failed. Got status code {}", response.getStatusCode() );
                    throw new TemporaryMailProcessingException(
                            "Sending mail failed. Got status code " + response.getStatusCode() );
                } else {
                    // X-Message-Id is the unique send grid id for the email message
                    responseId = response.getHeaders().get( "X-Message-Id" );
                }
            } catch ( IOException e ) {
                LOG.error( "IOException while preparing mail.", e );
                throw new MailProcessingException( "Could not process mail.", e );
            }
        }
        return responseId;

    }


    private Mail prepareMail( EmailMessage emailMessage )
    {
        Mail mail = new Mail();

        String senderEmailId = null;
        String senderName = null;
        if(emailMessage.getSenderEmailId() == null || emailMessage.getSenderEmailId().isEmpty()){

            //If there's no sender email address specified (as in the case of certain emailed reports), use the site admin's email address
            senderEmailId = LocalPropertyFileHandler.getInstance()

                .getProperty( ComputeConstants.APPLICATION_PROPERTY_FILE, ComputeConstants.ADMIN_EMAIL_ADDRESS )
                .orElse( null );
            senderName = LocalPropertyFileHandler.getInstance()
                .getProperty( ComputeConstants.APPLICATION_PROPERTY_FILE, ComputeConstants.ADMIN_EMAIL_ADDRESS_NAME )
                .orElse( null );
        }
        else{
            senderEmailId = emailMessage.getSenderEmailId();
            senderName = emailMessage.getSenderName();
        }
        
        Email from = new Email( senderEmailId, senderName );
        mail.setFrom( from );
        Personalization personalization = new Personalization();
        for ( String reciepient : emailMessage.getRecipients() ) {
            personalization.addTo( new Email( reciepient ) );
        }
        if ( emailMessage.isSendMailToSalesLead() ) {
            String salesLeadEmailAddress = LocalPropertyFileHandler.getInstance()
                    .getProperty( ComputeConstants.APPLICATION_PROPERTY_FILE, ComputeConstants.SALES_LEAD_EMAIL_ADDRESS )
                    .orElse( null );
            if ( !StringUtils.isBlank( salesLeadEmailAddress ) ) {
                personalization.addBcc( new Email( salesLeadEmailAddress ) );
            }
        }
        mail.addPersonalization( personalization );
        mail.setSubject( emailMessage.getSubject() );
        Content content = new Content( "text/html", emailMessage.getBody() );
        mail.addContent( content );
        if ( emailMessage.getAttachments() != null ) {
            for ( EmailAttachment emailAttachment : emailMessage.getAttachments()) {
                InputStream input = null;
                try {
                    URL url = new URL(emailAttachment.getFilePath());
                    URI uri = new URI( url.getProtocol(), url.getHost(), url.getPath(), null , null );
                    input = uri.toURL().openConnection().getInputStream();
                    Attachments attachments = new Attachments.Builder( emailAttachment.getFileName(), input ).build();
                    mail.addAttachments( attachments );

                } catch ( IOException | URISyntaxException e ) {
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
        return mail;
    }


    @Override
    public void executeTuple( Tuple input )
    {
        LOG.debug( "Executing send mail bolt." );
        boolean isSuccess = false;
        boolean isTemporaryException = false;
        // get the email message
        EmailMessage emailMessage = (EmailMessage) input.getValueByField( "emailMessage" );
        boolean deliveryAttempted = input.getBooleanByField( "deliveryAttempted" );
        LOG.debug( "deliveryAttempted {}", deliveryAttempted );
        if ( !deliveryAttempted ) {
            LOG.debug( "New mail. Should send and update SOLR" );
            // the email from SOLR
            Optional<SolrEmailMessageWrapper> optionalSolrEmailMessage = APIOperations.getInstance()
                    .getEmailMessageFromSOLR( emailMessage );
            if ( optionalSolrEmailMessage.isPresent() ) {
                SolrEmailMessageWrapper solrEmailMessage = optionalSolrEmailMessage.get();
                // check if if the message was already sent
                if ( solrEmailMessage.getEmailAttemptedDate() == null ) {
                    // message is not sent
                    try {
                        LOG.trace( "Validating email" );
                        validateEmailMessage( emailMessage );
                        if ( emailMessage.getSendEmailThrough() == null || emailMessage.getSendEmailThrough().isEmpty() ) {
                            LOG.debug( "Mail to be sent through social survey me account." );
                            emailMessage.setSendEmailThrough( ComputeConstants.SEND_EMAIL_THROUGH_SOCIALSURVEY_ME );
                        }
                        String responseId = sendMail( emailMessage );
                        LOG.debug( "Mail sent successfully. Now updating the mail attempted time" );
                        // update solr with current attempted date and sendgrid id
                        solrEmailMessage.setSendgridMessageId( responseId );
                        solrEmailMessage.setEmailAttemptedDate( ConversionUtils.convertCurrentEpochMillisToSolrTrieFormat() );
                        APIOperations.getInstance().postEmailToSolr( solrEmailMessage );
                        isSuccess = true;
                    } catch ( QueueingMessageProcessingException | MailProcessingException | SolrProcessingException e ) {
                        LOG.error( "Error while queueing/ processing email message.", e );
                        LOG.warn( "Message processing will NOT be retried. Message will be logged for inspection." );
                        FailedMessagesService failedMessageService = new FailedMessagesServiceImpl();
                        failedMessageService.insertPermanentlyFailedEmailMessage( emailMessage, e );
                    } catch ( TemporaryMailProcessingException e ) {
                        //insert into mongo only if it failed for the first time
                        if(!emailMessage.isRetried()) {
                            FailedMessagesService failedMessageService = new FailedMessagesServiceImpl();
                            failedMessageService.insertTemporaryFailedEmailMessage( emailMessage );
                        }
                        //else increase the retryCount by 1
                        else isTemporaryException = true;
                    }
                }
            } else {
                // TODO: Handle when email message is not present in SOLR
                LOG.warn( "MAIL SHOULD HAVE BEEN PRESENT. THIS MESSAGE SHOULD BE HANDLED IMMEDIATELY." );
            }
        } else {
            LOG.warn( "Email {} was already sent.", emailMessage );
        }
        _collector.emit( input, Arrays.asList( isSuccess, emailMessage.getMailType(), emailMessage, isTemporaryException ) );
    }


    @Override
    public List<Object> prepareTupleForFailure()
    {
        return Arrays.asList( false, null, null, false );
    }
}
