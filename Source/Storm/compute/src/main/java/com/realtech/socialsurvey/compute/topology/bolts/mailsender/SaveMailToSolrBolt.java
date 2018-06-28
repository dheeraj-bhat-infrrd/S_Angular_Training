package com.realtech.socialsurvey.compute.topology.bolts.mailsender;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.common.APIOperations;
import com.realtech.socialsurvey.compute.entities.EmailMessage;
import com.realtech.socialsurvey.compute.entities.SolrEmailMessageWrapper;
import com.realtech.socialsurvey.compute.exception.SolrProcessingException;
import com.realtech.socialsurvey.compute.services.FailedMessagesService;
import com.realtech.socialsurvey.compute.services.impl.FailedMessagesServiceImpl;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck;
import com.realtech.socialsurvey.compute.utils.ConversionUtils;


/**
 * Class to save the email to solr
 *
 *
 */
public class SaveMailToSolrBolt extends BaseComputeBoltWithAck
{

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger( SaveMailToSolrBolt.class );


    @Override public void declareOutputFields( OutputFieldsDeclarer declarer )
    {
        declarer.declare( new Fields( "success", "isNew", "deliveryAttempted", "emailMessage" ) );
    }


    private boolean addEmailMessageToSOLR( EmailMessage emailMessage )
    {
        SolrEmailMessageWrapper emailMessageWrapper = new SolrEmailMessageWrapper( emailMessage );
        emailMessageWrapper.setAttachments( null );
        return APIOperations.getInstance().postEmailToSolr( emailMessageWrapper );
    }


    @Override public void executeTuple( Tuple input )
    {
        LOG.info( "Executing save mail to solr bolt." );
        boolean success = false;
        boolean isNew = false;
        boolean deliveryAttempted = false;
        // get the email message
        //EmailMessage emailMessage = ConversionUtils.deserialize( input.getString( 0 ), EmailMessage.class );
        EmailMessage emailMessage = (EmailMessage) input.getValueByField( "emailMessage" );
        LOG.info("Starting bolt to save mail for recipient " + emailMessage.getRecipients().get(0));

        // check if the mail is already saved and email delivery was attempted
        LOG.info("Getting mail from solr by UUID " + emailMessage.getRandomUUID());
        Optional<SolrEmailMessageWrapper> optionalSolrEmailMessage = APIOperations.getInstance()
            .getEmailMessageFromSOLR( emailMessage );
        if ( optionalSolrEmailMessage.isPresent() ) {
            // if mail present in system, but delivery was not attempted, then don't save and emit with status not attempted.
            SolrEmailMessageWrapper solrEmailMessageWrapper = optionalSolrEmailMessage.get();
            if ( solrEmailMessageWrapper.getEmailAttemptedDate() == null ) {
                LOG.info( "Message was saved but not sent." );
                success = true;
                deliveryAttempted = false;
                isNew = false;
            } else {
                // if mail was attempted, then don't save and emit the bolt with status attempted
                LOG.info( "Message was saved and sent." );
                success = true;
                deliveryAttempted = true;
                isNew = false;
            }
        } else {
            // if mail not sent, then save and emit the tuple
            try {
                addEmailMessageToSOLR( emailMessage );
                LOG.info( "Mail saved to solr" );
                success = true;
                deliveryAttempted = false;
                isNew = true;
            } catch ( SolrProcessingException e ) {
                LOG.error( "Could not save the email message {}", emailMessage );
                FailedMessagesService failedMessageService = new FailedMessagesServiceImpl();
                failedMessageService.insertPermanentlyFailedEmailMessage( emailMessage, e );
                success = false;
                deliveryAttempted = false;
                isNew = true;
            }
        }
        LOG.debug( "Emitting tuple with success {}, isNew {}, deliveryAttempted {}.", success, isNew, deliveryAttempted );
        _collector.emit( input, Arrays.asList( success, isNew, deliveryAttempted, emailMessage ) );
    }


    @Override public List<Object> prepareTupleForFailure()
    {
        return Arrays.asList( false, false, false, null );
    }
}
