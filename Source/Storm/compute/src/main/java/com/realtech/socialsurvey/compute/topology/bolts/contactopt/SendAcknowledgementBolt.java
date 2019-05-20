package com.realtech.socialsurvey.compute.topology.bolts.contactopt;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.common.APIOperations;
import com.realtech.socialsurvey.compute.entities.SmsInfo;
import com.realtech.socialsurvey.compute.entities.SolrSmsWrapper;
import com.realtech.socialsurvey.compute.exception.SolrProcessingException;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck;
import com.realtech.socialsurvey.compute.topology.bolts.smssender.exception.SmsProcessingException;
import com.realtech.socialsurvey.compute.topology.bolts.smssender.exception.TemporarySmsProcessingException;
import com.realtech.socialsurvey.compute.utils.SmsUtils;

public class SendAcknowledgementBolt extends BaseComputeBoltWithAck {

	private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger( SendAcknowledgementBolt.class );
    
    private transient SmsUtils smsUtils;
    
    @Override public void declareOutputFields( OutputFieldsDeclarer declarer )
    {
        declarer.declare( new Fields( "success" ) );
    }
    
    private synchronized SmsUtils getSmsUtils() {

		LOG.debug( "Creating SmsSender instance" );
		if ( smsUtils == null ) {
			smsUtils = new SmsUtils();
		}
		return smsUtils;
	}

    @Override public void executeTuple( Tuple input )
    {
        LOG.info( "Executing save contact to solr bolt." );
        boolean success = (boolean) input.getValueByField( "success" );
        SmsInfo smsInfo = (SmsInfo) input.getValueByField( "smsInfo" );
        
        LOG.info("Starting bolt to send acknowledgement to recipient {}", smsInfo.getRecipientContactNumber() );

        // check if the record already saved and sms delivery was attempted
        LOG.info("Getting entity from solr by UUID {}", smsInfo.getRandomUUID());
        
        Optional<SolrSmsWrapper> optionalSolrsms = APIOperations.getInstance().getSmsFromSOLR( smsInfo );
        if ( optionalSolrsms.isPresent() ) {
            
            // if response not sent, then save and emit the tuple
            try {
            	
            	SolrSmsWrapper solrSmsWrapper = optionalSolrsms.get();
            	smsUtils = getSmsUtils();
            	smsUtils.sendSms( solrSmsWrapper );
            	APIOperations.getInstance().postSmsToSolr( solrSmsWrapper );
            	success = true;
                LOG.debug( "Record updtaed in solr" );                
            }
            catch ( SmsProcessingException | SolrProcessingException e ) {
                LOG.error( "Error while queueing/ processing sms. {}", e );
            }
            catch (TemporarySmsProcessingException e) {
				LOG.error( "Temporary error while queueing/processing sms.", e );
			}
        } else {
        	
        	LOG.warn( "Record is not present in solr with UUID {}", smsInfo.getRandomUUID() );
        }
        LOG.debug( "Emitting tuple with success {}.", success );
        _collector.emit( input, Arrays.asList( success ) );
    }

    @Override public List<Object> prepareTupleForFailure()
    {
        return Arrays.asList( false );
    }
}
