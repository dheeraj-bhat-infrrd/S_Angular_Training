package com.realtech.socialsurvey.compute.topology.bolts.smssender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.common.SSAPIOperations;
import com.realtech.socialsurvey.compute.entities.SmsInfo;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck;
import com.realtech.socialsurvey.compute.utils.ConversionUtils;

public class CheckUnsubscribedContactNumber extends BaseComputeBoltWithAck {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger( CheckUnsubscribedContactNumber.class );


	/* (non-Javadoc)
	 * @see org.apache.storm.topology.IComponent#declareOutputFields(org.apache.storm.topology.OutputFieldsDeclarer)
	 */
	@Override
	public void declareOutputFields( OutputFieldsDeclarer declarer )
	{
		declarer.declare( new Fields( "smsInfo" ) );

	}


	/* (non-Javadoc)
	 * @see com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck#executeTuple(org.apache.storm.tuple.Tuple)
	 */
	@Override
	public void executeTuple( Tuple input )
	{
		LOG.info( "Check for unsubscribed contactnumbers bolt." );
		SmsInfo smsInfo = ConversionUtils.deserialize( input.getString( 0 ), SmsInfo.class );
		String contactNumber = smsInfo.getRecipientContactNumber();
		boolean isUnsubscriberd = false;

		if( SSAPIOperations.getInstance().isContactNumberUnsubscribed( contactNumber, smsInfo.getCompanyId() ) ) {

			isUnsubscriberd = true;
			LOG.info( "Contact number is unsubscribed" );
		}

		if( isUnsubscriberd ) {
			smsInfo.setContactNumberUnsubscribed( isUnsubscriberd );
		}
		_collector.emit( input, Arrays.asList( smsInfo ) );
	}


	/* (non-Javadoc)
	 * @see com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck#prepareTupleForFailure()
	 */
	@Override
	public List<Object> prepareTupleForFailure()
	{
		return Collections.emptyList();
	}

}
