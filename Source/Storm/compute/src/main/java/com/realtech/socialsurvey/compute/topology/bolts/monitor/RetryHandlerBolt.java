package com.realtech.socialsurvey.compute.topology.bolts.monitor;

import com.realtech.socialsurvey.compute.entities.response.SocialResponseObject;
import com.realtech.socialsurvey.compute.services.FailedMessagesService;
import com.realtech.socialsurvey.compute.services.impl.FailedMessagesServiceImpl;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.TupleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RetryHandlerBolt extends BaseComputeBoltWithAck{

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger( RetryHandlerBolt.class );

    @Override
    public void executeTuple(Tuple input) {
        if(!TupleUtils.isTick( input )) {
            boolean success = input.getBooleanByField( "isSuccess" );
            SocialResponseObject<?> post = (SocialResponseObject<?>) input.getValueByField( "post" );

            FailedMessagesService failedMessagesService = new FailedMessagesServiceImpl();

            if ( post != null ) {
                if ( success && post.isRetried() ) {
                    int nRemoved = failedMessagesService.deleteFailedSocialPost( post.getPostId() );
                    if ( nRemoved == 1 )
                        LOG.info( "SocialPost with postId {} has been successfully deleted", post.getPostId() );
                    else
                        LOG.error( "Something went wrong while deleting socialpost having postId {}", post.getPostId() );
                } else if ( !success /*&& post.isRetried()*/ ) {
                    /*int updatedCount = */ failedMessagesService.insertTemporaryFailedSocialPost( post );
                    /*if ( updatedCount == 1 )
                        LOG.info( "SocialPost retrycount with postId {} has been successfully incremented by 1",
                            post.getPostId() );
                    else
                        LOG.error( "Something went wrong while incrementing socialpost retryCount having postId {}",
                            post.getPostId() );*/
                }
            }
        }

    }

    @Override
    public List<Object> prepareTupleForFailure() {
        return new Values(false);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("isSuccess"));
    }
}
