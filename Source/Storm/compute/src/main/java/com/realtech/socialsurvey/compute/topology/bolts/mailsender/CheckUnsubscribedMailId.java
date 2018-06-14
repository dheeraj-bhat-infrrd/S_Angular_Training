/**
 * 
 */
package com.realtech.socialsurvey.compute.topology.bolts.mailsender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.common.SSAPIOperations;
import com.realtech.socialsurvey.compute.entities.EmailMessage;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck;
import com.realtech.socialsurvey.compute.utils.ConversionUtils;

/**
 * @author Subhrajit
 *
 */
public class CheckUnsubscribedMailId extends BaseComputeBoltWithAck
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private static final Logger LOG = LoggerFactory.getLogger( CheckUnsubscribedMailId.class );


    /* (non-Javadoc)
     * @see org.apache.storm.topology.IComponent#declareOutputFields(org.apache.storm.topology.OutputFieldsDeclarer)
     */
    @Override
    public void declareOutputFields( OutputFieldsDeclarer declarer )
    {
        declarer.declare( new Fields( "emailMessage" ) );

    }


    /* (non-Javadoc)
     * @see com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck#executeTuple(org.apache.storm.tuple.Tuple)
     */
    @Override
    public void executeTuple( Tuple input )
    {
        LOG.info( "Check email address for unsubscribed emails bolt." );
        EmailMessage emailMessage = ConversionUtils.deserialize( input.getString( 0 ), EmailMessage.class );
        List<String> recipients = emailMessage.getRecipients();
        List<String> unsubscribedEmails = new ArrayList<String>();
        
        for(String recipient : recipients) {
            if(SSAPIOperations.getInstance().isEmailUnsubscribed(recipient,emailMessage.getCompanyId())) {
                unsubscribedEmails.add( recipient );
            }
        }
        LOG.debug( "Unsubscribed emails list size : {}",unsubscribedEmails.size() );
        
        if(unsubscribedEmails.size() > 0) {
            emailMessage.setEmailUnsubscribed( true );
            emailMessage.setUnsubscribedEmails( unsubscribedEmails );
        }
        _collector.emit( input, Arrays.asList( emailMessage ) );
    }


    /* (non-Javadoc)
     * @see com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck#prepareTupleForFailure()
     */
    @Override
    public List<Object> prepareTupleForFailure()
    {
        return null;
    }

}
