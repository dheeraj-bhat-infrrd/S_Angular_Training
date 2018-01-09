package com.realtech.socialsurvey.compute.topology.bolts;

import java.util.List;

import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class BaseComputeBoltWithAck extends BaseComputeBolt
{

    private static final Logger LOG = LoggerFactory.getLogger( BaseComputeBoltWithAck.class );
    private static final long serialVersionUID = 1L;


    @Override
    public final void execute( Tuple input )
    {
        try {
            executeTuple( input );
        } catch ( Exception ex ) {
            LOG.error( "RISKY ERROR: HANDLE IT!!!!", ex );
            _collector.emit( input, prepareTupleForFailure() );
        } finally {
            _collector.ack( input );
        }
    }


    /**
     * Code for executing the bolt
     * @param input
     */
    public abstract void executeTuple( Tuple input );


    /**
     * Tuple that would be emitted in case of failure
     * @return
     */
    public abstract List<Object> prepareTupleForFailure();

}
