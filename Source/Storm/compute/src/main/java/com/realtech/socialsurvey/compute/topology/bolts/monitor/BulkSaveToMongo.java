package com.realtech.socialsurvey.compute.topology.bolts.monitor;

import com.realtech.socialsurvey.compute.common.SSAPIOperations;
import com.realtech.socialsurvey.compute.entities.response.BulkWriteErrorVO;
import com.realtech.socialsurvey.compute.entities.response.SocialResponseObject;
import com.realtech.socialsurvey.compute.exception.APIIntegrationException;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * @author Lavanya
 */

public class BulkSaveToMongo extends BaseComputeBoltWithAck
{
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger( BulkSaveToMongo.class );

    /** The queue holding tuples in a batch. */

    protected LinkedBlockingQueue<Tuple> queue = new LinkedBlockingQueue<Tuple>();

    /** The threshold after which the batch should be flushed out. */


    int batchSize = 100;

    /**
     *
     * The batch interval in sec. Minimum time between flushes if the batch
     * sizes
     *
     * are not met. This should typically be equal to
     *
     * topology.tick.tuple.freq.secs and half of topology.message.timeout.secs
     *
     */
    int batchIntervalInSec = 45;
    /** The last batch process time seconds. Used for tracking purpose */
    long lastBatchProcessTimeSeconds = 0;

    @Override public void executeTuple( Tuple tuple )
    {
        LOG.info("Executing bulk save to mongo bolt ... ");
        //boolean success = tuple.getBooleanByField("isSuccess");

        if ( TupleUtils.isTick(tuple)) {
            // If so, it is indication for batch flush. But don't flush if
            // previous flush was done very recently (either due to batch size
            // threshold
            // was crossed or because of another tick tuple
            if (System.currentTimeMillis() / 1000 - lastBatchProcessTimeSeconds >= batchIntervalInSec
                && queue.size() > 0) {
                LOG.info("Current queue size is " + queue.size() + ". But received tick tuple so executing the batch");
                finishBatch();

            } else
                LOG.info(
                    "Current queue size is {}. Received tick tuple but last batch was executed  {}  seconds back that is less than {}  so ignoring the tick tuple",
                    queue.size(), System.currentTimeMillis() / 1000 - lastBatchProcessTimeSeconds,
                    batchIntervalInSec);

            // acking tick tuple

        } else {
            // Add the tuple to queue.
            queue.add(tuple);
            final int queueSize = queue.size();
            LOG.info("current queue size is " + queueSize);

            if (queueSize >= batchSize) {
                LOG.debug("Current queue size is >= {} executing the batch", batchSize);
                finishBatch();
            }

        }
    }


    private void finishBatch()
    {
        FailedMessagesService failedMessagesService = new FailedMessagesServiceImpl();
        LOG.info("Finishing batch of size " + queue.size());
        lastBatchProcessTimeSeconds = System.currentTimeMillis() / 1000;
        final List<SocialResponseObject> socialPosts = new ArrayList<>(  );
        Optional<List<BulkWriteErrorVO>> bulkWriteErrors = Optional.empty();
        final List<Tuple> tuples = new ArrayList<>();
        queue.drainTo(tuples);
        for(Tuple tuple : tuples){
            long companyId = tuple.getLongByField("companyId");
            SocialResponseObject<?> socialPost = (SocialResponseObject<?>) tuple.getValueByField( "post" );
            if(socialPost != null){
                socialPosts.add( socialPost );
            }
        }
        try {
            bulkWriteErrors =  bulkInsertToMongo(socialPosts);

        } catch ( IOException | APIIntegrationException e ) {
            LOG.error("Exception occurred", e);
            for(int i=0; i< tuples.size(); i++) {
                SocialResponseObject socialPost = socialPosts.get( i );
                failedMessagesService.insertTemporaryFailedSocialPost( socialPost );
            }
        }
        //if there are any bulk write errors then save them to streamdb failed msgs for retrying
        if( bulkWriteErrors.isPresent() && !bulkWriteErrors.get().isEmpty() ) {
            for(BulkWriteErrorVO error : bulkWriteErrors.get()){
                //BulkWriteErrors give you exactly index of which document in the list failed , so get that
                // and implement error handling logic
                if(error.getCode() == 11000 && error.getMessage().contains( "duplicate key" )) {
                    SocialResponseObject socialPost = socialPosts.get( error.getIndex() );
                    //if the post is retried then post is already saved so try updating duplicatecount
                    if(socialPost.isRetried() && socialPost.getHash() != 0){
                        try {
                            updateSocialPostDuplicateCount( socialPost.getHash(), socialPost.getCompanyId(), socialPost.getId() );
                        } catch ( IOException | APIIntegrationException e ) {
                            LOG.error( "Sothing went wrong while trying to update duplicate count of post = {}", socialPost.getId() );
                            failedMessagesService.insertTemporaryFailedSocialPost( socialPost );
                        }
                    } else{
                        LOG.info( "Duplicate post !!! Ignoring" );
                    }
                }
                else {
                    LOG.error( "Unhandled Exception while performing bulk inserts to mongo", error.getMessage() );
                    LOG.warn( " Exception needs to be handled immediately " );
                }
            }
        }


    }


    private Optional<List<BulkWriteErrorVO>> bulkInsertToMongo( List<SocialResponseObject> socialPosts ) throws IOException
    {
        return SSAPIOperations.getInstance().bulkInsertToMongo(socialPosts);
    }

    private Optional<Long> updateSocialPostDuplicateCount( int hash, long companyId, String id ) throws IOException
    {
        return SSAPIOperations.getInstance().updateSocialPostDuplicateCount(hash, companyId, id);
    }

    @Override public List<Object> prepareTupleForFailure()
    {
        return new Values(false, 0L, null);
    }


    @Override public void declareOutputFields( OutputFieldsDeclarer declarer )
    {
        declarer.declareStream("SUCCESS_STREAM", new Fields("isSuccess", "companyId", "post" ) );
    }
}
