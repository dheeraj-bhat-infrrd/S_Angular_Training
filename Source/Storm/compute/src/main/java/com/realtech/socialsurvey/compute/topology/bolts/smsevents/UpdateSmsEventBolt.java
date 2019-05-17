package com.realtech.socialsurvey.compute.topology.bolts.smsevents;

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
import com.realtech.socialsurvey.compute.common.SSAPIOperations;
import com.realtech.socialsurvey.compute.entities.SmsInfo;
import com.realtech.socialsurvey.compute.entities.SolrSmsWrapper;
import com.realtech.socialsurvey.compute.exception.SolrProcessingException;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck;
import com.realtech.socialsurvey.compute.utils.ConversionUtils;

public class UpdateSmsEventBolt extends BaseComputeBoltWithAck {

	private static final Logger LOG = LoggerFactory.getLogger( UpdateSmsEventBolt.class );

    private static final long serialVersionUID = 1L;

    private static final String TWS_EVENT_SENT = "sent";
    private static final String TWS_EVENT_DELIVERED = "delivered";
    
    @Override public void executeTuple( Tuple input )
    {
        SmsInfo smsInfo = ConversionUtils.deserialize( input.getString( 0 ), SmsInfo.class );
        LOG.info( "Processing event {}", smsInfo );
        Optional<SolrSmsWrapper> optionalSmsFromSlor = null;
        // check if twilioSmsId is present
        // if twilioSmsId present, then query SOLR based on twilioSmsId
        LOG.info( "Event twilioSmsId is {}", smsInfo.getTwilioSmsId() );
        if ( !StringUtils.isEmpty( smsInfo.getTwilioSmsId() ) ) {
        	
        	optionalSmsFromSlor = APIOperations.getInstance().getSmsFromSOLRByTwilioSmsId( smsInfo.getTwilioSmsId() );
        }
        else {
        	
        	LOG.warn("Couldn't extract sms from solr based on twilioSmsId, twilioSmsId is null or empty");
        }
        
        //update sms event in solr
        if ( optionalSmsFromSlor != null && optionalSmsFromSlor.isPresent() ) {
        	
        	SolrSmsWrapper solrSmsWrapper = optionalSmsFromSlor.get();
            try {
            	
            	long currentDate = System.currentTimeMillis();
            	
            	if( TWS_EVENT_SENT.equals( smsInfo.getSmsStatus() ) ) {
            		
            		solrSmsWrapper.setSmsSentDate( ConversionUtils.convertEpochSecondToSolrTrieFormat( currentDate ) );
            	}
            	else if( TWS_EVENT_DELIVERED.equals( smsInfo.getSmsStatus() ) ) {
            		
            		solrSmsWrapper.setSmsDeliveredDate(  ConversionUtils.convertEpochSecondToSolrTrieFormat( currentDate ) );
            	}
            	else {
            		
            		solrSmsWrapper.setErrorCode( smsInfo.getErrorCode() );
            		solrSmsWrapper.setErrorMessage( smsInfo.getErrorMessage() );
            	}
            	solrSmsWrapper.setSmsUpdatedDate( ConversionUtils.convertEpochSecondToSolrTrieFormat( currentDate ) );
            	solrSmsWrapper.setSmsStatus( smsInfo.getSmsStatus() );
                APIOperations.getInstance().postSmsToSolr( solrSmsWrapper );
            } catch ( SolrProcessingException e ) {
                LOG.error( "Error while updating sms event.", e );
                LOG.warn( "Message processing will NOT be retried. Message will be logged for inspection." );
                SSAPIOperations.getInstance().addFailedStreamSms( smsInfo );
            }
        } else {
        	
            LOG.warn( "Could not find sms for this event" );
        }

        _collector.emit( input, Arrays.asList( true ) );
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
