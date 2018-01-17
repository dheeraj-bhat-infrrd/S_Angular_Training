package com.realtech.socialsurvey.compute.topology.bolts.monitor;


import java.util.Arrays;

import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.common.SSAPIOperations;
import com.realtech.socialsurvey.compute.entities.SocialPost;
import com.realtech.socialsurvey.compute.services.FailedMessagesService;
import com.realtech.socialsurvey.compute.services.impl.FailedMessagesServiceImpl;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBolt;
import com.realtech.socialsurvey.compute.topology.bolts.monitor.exception.FeedsProcessingException;


/**
 * Class to save the post/feed to mongo
 * @author manish
 * 
 *
 */
public class SaveFeedsToMongoBolt extends BaseComputeBolt
{

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger( SaveFeedsToMongoBolt.class );


    @Override
    public void execute( Tuple input )
    {
        LOG.info( "Executing save post to mongo bolt." );
        long companyId = input.getLongByField( "companyId" );
        SocialPost post = (SocialPost) input.getValueByField( "post" );

        try {
            LOG.info( "executeTuple: Save feeds to mongo" );
            if ( post != null ) {
                addSocilaFeedToMongo( post );
            } else {
                LOG.warn( "Social post is null" );
            }
        } catch ( FeedsProcessingException e ) {
            LOG.warn( "Mongo feed not found for given mongo id", e );
            FailedMessagesService failedMessageService = new FailedMessagesServiceImpl();
            failedMessageService.insertPermanentlyFailedSocialPost( post, e );
        }
        _collector.emit( input, Arrays.asList( companyId, post ) );
        _collector.ack( input );
    }


    private boolean addSocilaFeedToMongo( SocialPost socialPost )
    {
        return SSAPIOperations.getInstance().saveFeedToMongo( socialPost );
    }


    @Override
    public void declareOutputFields( OutputFieldsDeclarer declarer )
    {
        declarer.declare( new Fields( "companyId", "post" ) );
    }
}
