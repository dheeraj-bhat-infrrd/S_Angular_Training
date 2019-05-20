package com.realtech.socialsurvey.compute.topology.bolts.smssender;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.common.APIOperations;
import com.realtech.socialsurvey.compute.common.SSAPIOperations;
import com.realtech.socialsurvey.compute.entities.SmsInfo;
import com.realtech.socialsurvey.compute.entities.SolrSmsWrapper;
import com.realtech.socialsurvey.compute.exception.QueueingMessageProcessingException;
import com.realtech.socialsurvey.compute.exception.SolrProcessingException;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck;
import com.realtech.socialsurvey.compute.topology.bolts.smssender.exception.SmsProcessingException;
import com.realtech.socialsurvey.compute.topology.bolts.smssender.exception.TemporarySmsProcessingException;
import com.realtech.socialsurvey.compute.utils.SmsUtils;

public class SendSmsBolt extends BaseComputeBoltWithAck {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger( SendSmsBolt.class );

	private transient SmsUtils smsUtils;

	private synchronized SmsUtils getSmsUtils() {

		LOG.debug( "Creating SmsSender instance" );
		if ( smsUtils == null ) {
			smsUtils = new SmsUtils();
		}
		return smsUtils;
	}

	@Override
	public void declareOutputFields( OutputFieldsDeclarer declarer )
	{
		declarer.declare( new Fields( "success" ) );

	}

	@Override
	public void prepare( @SuppressWarnings ( "rawtypes") Map stormConf, TopologyContext context, OutputCollector collector )
	{
		super.prepare( stormConf, context, collector );
	}

	private void validateSmsInfo( SmsInfo smsInfo )
	{
		LOG.debug( "Validating SMS Info" );

		if ( smsInfo.getRecipientContactNumber() == null || smsInfo.getRecipientContactNumber().isEmpty() ) {
			LOG.warn( "Recipient contact number is empty for sending sms." );
			throw new QueueingMessageProcessingException( "No contact number to send sms" );
		}
		if ( smsInfo.getSmsText() == null || smsInfo.getSmsText().isEmpty() ) {
			LOG.warn( "SMS body is blank." );
			throw new QueueingMessageProcessingException( "SMS body is blank." );
		}
	}

	@Override
	public void executeTuple( Tuple input )
	{
		LOG.info( "Executing send mail bolt." );
		boolean isSuccess = false;
		// get the email message
		boolean urlGenerated = input.getBooleanByField( "urlGenerated" );
		SmsInfo smsInfo = (SmsInfo) input.getValueByField( "smsInfo" );
		boolean deliveryAttempted = input.getBooleanByField( "deliveryAttempted" );
		boolean successfull = input.getBooleanByField( "success" );
		LOG.debug( "deliveryAttempted {}", deliveryAttempted );

		if( smsInfo.isContactNumberUnsubscribed() ) {

			LOG.warn( "Recipient contact number has been unsubscribed {}", smsInfo );
		}
		else {

			if( successfull && urlGenerated ) {

				if ( !deliveryAttempted ) {

					LOG.info( "New sms should send and update SOLR" );

					Optional<SolrSmsWrapper> optionalSolrsms = APIOperations.getInstance().getSmsFromSOLR( smsInfo );
					if ( optionalSolrsms.isPresent() ) {

						SolrSmsWrapper solrSmsWrapper = optionalSolrsms.get();
						if ( solrSmsWrapper.getTwilioSmsId() == null || solrSmsWrapper.getTwilioSmsId().isEmpty() ) {

							try {

								validateSmsInfo( smsInfo );
								solrSmsWrapper.setSmsText( smsInfo.getSmsText() );
								solrSmsWrapper.setShortenedUrl( smsInfo.getShortenedUrl() );
								smsUtils = getSmsUtils();
								smsUtils.sendSms( solrSmsWrapper );
								APIOperations.getInstance().postSmsToSolr( solrSmsWrapper );
								isSuccess = true;
							}
							catch ( QueueingMessageProcessingException | SmsProcessingException | SolrProcessingException e ) {

								LOG.error( "Error while queueing/ processing sms.", e );
								if( !StringUtils.isEmpty( solrSmsWrapper.getErrorCode() ) ) {
									
									smsInfo.setErrorCode( solrSmsWrapper.getErrorCode() );
								}
								
								if( !StringUtils.isEmpty( solrSmsWrapper.getErrorMessage() ) ) {
									
									smsInfo.setErrorMessage( solrSmsWrapper.getErrorMessage() );
								}
								else {
								
									smsInfo.setErrorMessage( e.getMessage() );
								}
								SSAPIOperations.getInstance().addFailedStreamSms( smsInfo );
							}
							catch (TemporarySmsProcessingException e) {

								LOG.error( "Temporary error while queueing/ processing sms.", e );
								SSAPIOperations.getInstance().addFailedStreamSms( smsInfo );
							}
						}
						else {

							LOG.warn( "IT LOOKS LIKE SMS HAVE BEEN SENT." );
						}
					}
					else {

						// TODO: Handle when sms entity is not present in SOLR
						LOG.warn( "SMS SHOULD HAVE BEEN PRESENT. THIS MESSAGE SHOULD BE HANDLED IMMEDIATELY." );
					}
				}
				else {

					LOG.warn( "SMS {} was already sent.", smsInfo );
				}
			}
			else {
				
				LOG.warn( "It looks like rebrandly call or Solr Processing failed. IsSuccess: {} && urlGenerated: {}",successfull, urlGenerated );
			}
		}
		_collector.emit( input, Arrays.asList( isSuccess ) );
	}


	@Override
	public List<Object> prepareTupleForFailure()
	{
		return Arrays.asList( false );
	}
}
