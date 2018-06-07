package com.realtech.socialsurvey.compute.topology.bolts.userevent;

import java.util.Arrays;
import java.util.List;

import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.common.APIOperations;
import com.realtech.socialsurvey.compute.entities.UserEvent;
import com.realtech.socialsurvey.compute.exception.SolrProcessingException;
import com.realtech.socialsurvey.compute.services.impl.FailedMessagesServiceImpl;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck;
import com.realtech.socialsurvey.compute.topology.bolts.mailevents.UpdateMailEventsBolt;
import com.realtech.socialsurvey.compute.utils.ConversionUtils;


public class SaveUserEventToSolrBolt extends BaseComputeBoltWithAck
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger( SaveUserEventToSolrBolt.class );


    @Override
    public void declareOutputFields( OutputFieldsDeclarer declarer )
    {
        declarer.declare( new Fields( "isSuccess" ) );
    }


    @Override
    public List<Object> prepareTupleForFailure()
    {
        return Arrays.asList( false );
    }


    @Override
    public void executeTuple( Tuple input )
    {
        LOG.info( "Executing save user event to solr bolt." );
        boolean success = false;

        // get the user event message
        UserEvent userEvent = ConversionUtils.deserialize( input.getString( 0 ), UserEvent.class );

        try {

            // make an API call to SOLR to save the user event
            success = APIOperations.getInstance().saveUserEventToSolr( userEvent );
            LOG.debug( "user event saved to solr" );

        } catch ( SolrProcessingException error ) {
            LOG.warn( "Could not save the user event {}", userEvent );
            new FailedMessagesServiceImpl().insertUnsavedUserEvent( userEvent, true, 0, false, false, error );
            success = false;
        }


        LOG.debug( "Emitting user event tuple with success {}, user event {}.", success, userEvent );
        _collector.emit( input, Arrays.asList( success ) );
    }

}
