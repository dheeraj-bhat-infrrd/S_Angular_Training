package com.realtech.socialsurvey.compute.topology.bolts.mailevents;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.common.APIOperations;
import com.realtech.socialsurvey.compute.common.EnvConstants;
import com.realtech.socialsurvey.compute.common.LocalPropertyFileHandler;
import com.realtech.socialsurvey.compute.entities.SendgridEvent;
import com.realtech.socialsurvey.compute.entities.SolrEmailMessageWrapper;
import com.realtech.socialsurvey.compute.exception.SolrProcessingException;
import com.realtech.socialsurvey.compute.services.FailedMessagesService;
import com.realtech.socialsurvey.compute.services.impl.FailedMessagesServiceImpl;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck;
import com.realtech.socialsurvey.compute.utils.ConversionUtils;


/**
 * Tracks events from Sendgrid and updates the mails
 * @author nishit
 *
 */
public class UpdateMailEventsBolt extends BaseComputeBoltWithAck
{

    private static final Logger LOG = LoggerFactory.getLogger( UpdateMailEventsBolt.class );

    private static final long serialVersionUID = 1L;

    private static final String SG_EVENT_DEFERRED = "deferred";
    private static final String SG_EVENT_DELIVERED = "delivered";
    private static final String SG_EVENT_OPEN = "open";
    private static final String SG_EVENT_SPAMREPORT = "spamreport";
    private static final String SG_EVENT_UNSUBSCRIBE = "unsubscribe";
    private static final String SG_EVENT_BOUNCE = "bounce";
    private static final String EVENT_CLICK = "click";
    private static final String SG_EVENT_DROPPED = "dropped";

    private static final String SENDGRID_MESSAGE_DELIMITER = ".filter"; // Sendgrid message id are appended with .filter* 
    
    @Override public void executeTuple( Tuple input )
    {
        SendgridEvent event = ConversionUtils.deserialize( input.getString( 0 ), SendgridEvent.class );
        LOG.info( "Processing event {}", event );
        Optional<SolrEmailMessageWrapper> optionalEmailMessageFromSolr = null;
        // check if uuid is present
        // if uuid present, then query SOLR based on randomUUID
        LOG.info( "Event Uuid is " + event.getUuid() );
        if ( event.getUuid() != null && !event.getUuid().isEmpty() ) {
            optionalEmailMessageFromSolr = APIOperations.getInstance().getEmailMessageFromSOLRByRandomUUID( event.getUuid() );
        }
        // get sendgrid event id
        else {
        		String messageId = "";
            if ( event.getSg_message_id() != null && event.getSg_message_id().indexOf( SENDGRID_MESSAGE_DELIMITER ) >= 0 ) {
                messageId = event.getSg_message_id().substring( 0,  event.getSg_message_id().indexOf( SENDGRID_MESSAGE_DELIMITER ) );
            } else if ( event.getSmtpId() != null && event.getSmtpId().indexOf( "@" ) >= 0 ) {
                messageId = event.getSmtpId().substring( 1, event.getSmtpId().indexOf( "@" ) );
            } else if( event.getSmtpId() != null && event.getSmtpId().indexOf( ">" ) >= 0 ){
                messageId = event.getSmtpId().substring( 1, event.getSmtpId().indexOf( ">" ) );
            }
            if( ! StringUtils.isEmpty(messageId)) {
        		optionalEmailMessageFromSolr = APIOperations.getInstance().getEmailMessageFromSOLRBySendgridMsgId( messageId );            	
            }else {
            		LOG.warn("Couldn't extract randomUUID, sgMessageId or SMTP-ID from event");
            }
       }
        
        //update mail event in sor
        if ( optionalEmailMessageFromSolr != null && optionalEmailMessageFromSolr.isPresent() ) {
            SolrEmailMessageWrapper emailMessageFromSolr = optionalEmailMessageFromSolr.get();
            try {
                emailMessageFromSolr = updateEmailMessageWithStatus( emailMessageFromSolr, event );
                APIOperations.getInstance().postEmailToSolr( emailMessageFromSolr );
            } catch ( SolrProcessingException e ) {
                LOG.error( "Error while updating email event.", e );
                LOG.warn( "Message processing will NOT be retried. Message will be logged for inspection." );
                FailedMessagesService failedMessageService = new FailedMessagesServiceImpl();
                failedMessageService.insertPermanentlyFailedEmailMessage( emailMessageFromSolr, new SolrProcessingException(
                    "Updating attempted event time for message event " + event.getEvent() + " with timestamp " + event
                        .getTimestamp() + " failed for message id " + emailMessageFromSolr.getRandomUUID(), e ) );
            }
        } else {
            // Should be fine for profile other than prod as sendgrid account is shared.
            LOG.warn( "Could not find message for this event" );
            if ( LocalPropertyFileHandler.getInstance().getProfile().equals( EnvConstants.PROFILE_PROD ) ) {
                // TODO: Log event in failed message
            }
        }

        _collector.emit( input, Arrays.asList( true ) );
    }


    // modified the solr email message with the event 
    private SolrEmailMessageWrapper updateEmailMessageWithStatus( SolrEmailMessageWrapper solrEmailMessageWrapper,
        SendgridEvent event )
    {
        switch ( event.getEvent() ) {
            case SG_EVENT_DELIVERED:
                solrEmailMessageWrapper
                    .setEmailDeliveredDate( ConversionUtils.convertEpochSecondToSolrTrieFormat( event.getTimestamp() ) );
                break;
            case SG_EVENT_DEFERRED:
                solrEmailMessageWrapper
                    .setEmailDefferedDate( ConversionUtils.convertEpochSecondToSolrTrieFormat( event.getTimestamp() ) );
                break;
            case SG_EVENT_OPEN:
                solrEmailMessageWrapper
                    .setEmailOpenedDate( ConversionUtils.convertEpochSecondToSolrTrieFormat( event.getTimestamp() ) );
                break;
            case SG_EVENT_SPAMREPORT:
                solrEmailMessageWrapper
                    .setEmailMarkedSpamDate( ConversionUtils.convertEpochSecondToSolrTrieFormat( event.getTimestamp() ) );
                break;
            case SG_EVENT_UNSUBSCRIBE:
                solrEmailMessageWrapper
                    .setEmailUnsubscribeDate( ConversionUtils.convertEpochSecondToSolrTrieFormat( event.getTimestamp() ) );
                break;
            case SG_EVENT_BOUNCE:
                solrEmailMessageWrapper
                    .setEmailBounceDate( ConversionUtils.convertEpochSecondToSolrTrieFormat( event.getTimestamp() ) );
                break;
            case EVENT_CLICK:
                solrEmailMessageWrapper
                    .setEmailLinkClickedDate( ConversionUtils.convertEpochSecondToSolrTrieFormat( event.getTimestamp() ) );
                break;
            case SG_EVENT_DROPPED:
                solrEmailMessageWrapper
                    .setEmailDroppedDate( ConversionUtils.convertEpochSecondToSolrTrieFormat( event.getTimestamp() ) );
                break;
            default:
                if ( LOG.isWarnEnabled() ) {
                    LOG.warn( "The event {} is not handled.", event.getEvent() );
                }
        }
        return solrEmailMessageWrapper;
    }


    @Override public void declareOutputFields( OutputFieldsDeclarer declarer )
    {
        declarer.declare( new Fields( "isSuccess" ) );
    }


    @Override public List<Object> prepareTupleForFailure()
    {
        return Arrays.asList( false );
    }


}
