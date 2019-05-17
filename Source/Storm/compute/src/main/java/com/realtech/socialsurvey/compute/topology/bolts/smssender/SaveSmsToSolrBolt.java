package com.realtech.socialsurvey.compute.topology.bolts.smssender;

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
import com.realtech.socialsurvey.compute.entities.response.RebrandlyVO;
import com.realtech.socialsurvey.compute.exception.SolrProcessingException;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck;

public class SaveSmsToSolrBolt extends BaseComputeBoltWithAck
{
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger( SaveSmsToSolrBolt.class );

    @Override public void declareOutputFields( OutputFieldsDeclarer declarer )
    {
        declarer.declare( new Fields( "urlGenerated", "success", "isNew", "deliveryAttempted", "smsInfo" ) );
    }

    private boolean addSmsToSolr( SmsInfo smsInfo )
    {
        SolrSmsWrapper solrSmsWrapper = new SolrSmsWrapper( smsInfo );
        return APIOperations.getInstance().postSmsToSolr( solrSmsWrapper );
    }

    @Override public void executeTuple( Tuple input )
    {
        LOG.info( "Executing save sms to solr bolt." );
        boolean success = false;
        boolean isNew = false;
        boolean deliveryAttempted = false;
        boolean urlGenerated = false;
        // get the sms
        SmsInfo smsInfo = (SmsInfo) input.getValueByField( "smsInfo" );
        
        LOG.info("Starting bolt to save sms for recipient {}", smsInfo.getRecipientContactNumber() );

        // check if the sms is already saved and sms delivery was attempted
        LOG.info("Getting sms from solr by UUID {}", smsInfo.getRandomUUID());
        
        Optional<SolrSmsWrapper> optionalSolrsms = APIOperations.getInstance().getSmsFromSOLR( smsInfo );
        if ( optionalSolrsms.isPresent() ) {
            // if sms present in system, but delivery was not attempted, then don't save and emit with status not attempted.
        	SolrSmsWrapper solrSmsWrapper = optionalSolrsms.get();
            if ( solrSmsWrapper.getTwilioSmsId() == null || solrSmsWrapper.getTwilioSmsId().isEmpty() ) {
                LOG.info( "Message was saved but not sent." );
                success = true;
                deliveryAttempted = false;
                isNew = false;
                if( StringUtils.isEmpty( solrSmsWrapper.getShortenedUrl() ) ) {
                	
                	RebrandlyVO rebrandlyVO = SSAPIOperations.getInstance().getShortenedSurveyUrl( smsInfo.getSurveyUrl() );
                	if( rebrandlyVO != null ) {
                		
                		smsInfo.setShortenedUrl( rebrandlyVO.getShortUrl() );
                		smsInfo.setRebrandlyErrorCode( rebrandlyVO.getRebrandlyErrorCode() );
                		smsInfo.setRebrandlyErrorMessage( rebrandlyVO.getRebrandlyErrorMessage() );
                	}
                	
                	if( !StringUtils.isEmpty( smsInfo.getShortenedUrl() ) ) {
                		
                		smsInfo.setSmsText( smsInfo.getSmsText().replace("[SurveyUrl]", smsInfo.getShortenedUrl() ) );
                		urlGenerated = true;
                	}
                }
            } else {
                // if sms was attempted, then don't save and emit the bolt with status attempted
                LOG.info( "Message was saved and sent." );
                success = true;
                deliveryAttempted = true;
                isNew = false;
            }
            if( !StringUtils.isEmpty( smsInfo.getShortenedUrl() ) ) {
            	
            	urlGenerated = true;
            }
        } else {
            // if sms not sent, then save and emit the tuple
            try {
            	
            	RebrandlyVO rebrandlyVO = SSAPIOperations.getInstance().getShortenedSurveyUrl( smsInfo.getSurveyUrl() );
            	if( rebrandlyVO != null ) {
            		
            		smsInfo.setShortenedUrl( rebrandlyVO.getShortUrl() );
            		smsInfo.setRebrandlyErrorCode( rebrandlyVO.getRebrandlyErrorCode() );
            		smsInfo.setRebrandlyErrorMessage( rebrandlyVO.getRebrandlyErrorMessage() );
            	}
            	
            	if( !StringUtils.isEmpty( smsInfo.getShortenedUrl() ) ) {
            		
            		smsInfo.setSmsText( smsInfo.getSmsText().replace("[SurveyUrl]", smsInfo.getShortenedUrl() ) );
            	}
            	
            	addSmsToSolr( smsInfo );
                LOG.debug( "Sms saved to solr" );
                
                if( StringUtils.isEmpty( smsInfo.getShortenedUrl() ) ) {
                	
                	success = false;
	                deliveryAttempted = false;
	                isNew = true;
	                SSAPIOperations.getInstance().addFailedStreamSms( smsInfo );
                }
                else {
                	
	                success = true;
	                deliveryAttempted = false;
	                isNew = true;
	                urlGenerated = true;
                }
            } catch ( SolrProcessingException e ) {
                LOG.error( "Could not save the sms {}", smsInfo );
                SSAPIOperations.getInstance().addFailedStreamSms( smsInfo );
                success = false;
                deliveryAttempted = false;
                isNew = true;
            }
        }
        LOG.debug( "Emitting tuple with urlGenerated {} success {}, isNew {}, deliveryAttempted {}.", urlGenerated, success, isNew, deliveryAttempted );
        _collector.emit( input, Arrays.asList( urlGenerated, success, isNew, deliveryAttempted, smsInfo ) );
    }


    @Override public List<Object> prepareTupleForFailure()
    {
        return Arrays.asList( false, false, false, false, null );
    }
}
