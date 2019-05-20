package com.realtech.socialsurvey.compute.topology.bolts.contactopt;

import java.util.Arrays;
import java.util.List;

import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.common.SSAPIOperations;
import com.realtech.socialsurvey.compute.entities.SmsInfo;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck;

public class UnsubscribeResubscribeBolt extends BaseComputeBoltWithAck {

	private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger( UnsubscribeResubscribeBolt.class );
    
    @Override public void declareOutputFields( OutputFieldsDeclarer declarer )
    {
        declarer.declare( new Fields( "success", "smsInfo" ) );
    }

    @Override public void executeTuple( Tuple input )
    {
        LOG.info( "Unsubscribe Resubscribe contact number bolt." );
        
        boolean success = (boolean) input.getValueByField( "success" );
        SmsInfo smsInfo = (SmsInfo) input.getValueByField( "smsInfo" );
        
        if( success ) {
        	
        	Boolean changeFlag = null;
        	if( "Unsubscribe".equals( smsInfo.getSmsCategory() ) ) {
        		
        		changeFlag = true;
        	}
        	else if( "Resubscribe".equals( smsInfo.getSmsCategory() ) ) {
        		
        		changeFlag = false;
        	}
        	else if( "InvalidContentsReply".equals( smsInfo.getSmsCategory() ) ) {
        		
        		success = true;
        	}
        	else {
        		
        		success = false;
        	}
        	if( changeFlag != null ) {
        		
        		SSAPIOperations.getInstance().unsubscribeResubscribeContact( smsInfo.getRecipientContactNumber(), changeFlag, smsInfo.getIncomingMessageBody() );
        	}
        	else {
        		LOG.warn( "Sms category is other than Unsubscribe/Resubscribe." );
        	}
        }
        
        LOG.debug( "Emitting tuple with success {}.", success );
        _collector.emit( input, Arrays.asList( success, smsInfo ) );
    }

    @Override public List<Object> prepareTupleForFailure()
    {
        return Arrays.asList( false, null );
    }
}
