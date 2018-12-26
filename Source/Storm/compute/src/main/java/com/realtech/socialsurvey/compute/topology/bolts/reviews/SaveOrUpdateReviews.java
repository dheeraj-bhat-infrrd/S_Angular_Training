package com.realtech.socialsurvey.compute.topology.bolts.reviews;

import com.realtech.socialsurvey.compute.common.SSAPIOperations;
import com.realtech.socialsurvey.compute.entities.SurveyDetailsVO;
import com.realtech.socialsurvey.compute.entities.response.BulkWriteErrorVO;
import com.realtech.socialsurvey.compute.exception.APIIntegrationException;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.TupleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * @author Lavanya
 *
 * Saves or updates reviews in bulk into mongo
 *
 */

public class SaveOrUpdateReviews extends BaseComputeBoltWithAck
{
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger( SaveOrUpdateReviews.class );


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
    int batchIntervalInSec = 30;
    /** The last batch process time seconds. Used for tracking purpose */
    long lastBatchProcessTimeSeconds = 0;

    @Override public void executeTuple( Tuple tuple )
    {
        LOG.info("Executing SaveOrUpdateReviews bolt ... ");

        if( TupleUtils.isTick( tuple )){
            {
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

            }
        } else {
            // Add the tuple to queue by checking if any the reviews belong to more than one hierarchy

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
        LOG.info("Finishing batch of size " + queue.size());

        lastBatchProcessTimeSeconds = System.currentTimeMillis() / 1000;

        final List<Tuple> tuples = new ArrayList<>();
        queue.drainTo(tuples);

        List<SurveyDetailsVO> surveyDetails = new ArrayList<>();
        List<Long> idens = new ArrayList<>();

        for(Tuple tuple: tuples){
            surveyDetails .add( (SurveyDetailsVO) tuple.getValueByField( "surveyDetails" ));
            idens.add( tuple.getLongByField( "iden" ) );
        }

        try {
            final Optional<List<BulkWriteErrorVO>> bulkWriteErrorVOS = saveOrUpdateReviews( surveyDetails );

            //if any mongo errors reset the lastFetchedKey of the mediaToken
            if( bulkWriteErrorVOS.isPresent() && !bulkWriteErrorVOS.get().isEmpty() ){
                for( BulkWriteErrorVO bulkWriteError : bulkWriteErrorVOS.get() ){
                    //reset the lastfetched keys for the failed surveys
                    SurveyDetailsVO surveyDetail= surveyDetails.get( bulkWriteError.getIndex() );
                    SSAPIOperations.getInstance().resetSocialMediaLastFetched( surveyDetail.getProfileType(),
                        idens.get( bulkWriteError.getIndex() ), surveyDetail.getSource() );
                    /*socialMediaStateDao.resetLastFetched( lastFetchedKeys.get( bulkWriteError.getIndex() ) );*/
                }
            }

        } catch ( JedisConnectionException jce ) {
            LOG.error( "Redis might be down !!! Error message is {}", jce.getMessage() );
        } catch ( IOException | APIIntegrationException e ) {
            LOG.error( "Exception occured while saving or updating reviews to mongo", e );
            for( int i=0 ; i< tuples.size(); i++ ){
                SurveyDetailsVO surveyDetail= surveyDetails.get( i );
                SSAPIOperations.getInstance().resetSocialMediaLastFetched( surveyDetail.getProfileType(),
                    idens.get( i ), surveyDetail.getSource() );
                //socialMediaStateDao.resetLastFetched( lastFetchedKey );
            }
        }

    }


    private Optional<List<BulkWriteErrorVO>> saveOrUpdateReviews( List<SurveyDetailsVO> surveyDetails ) throws IOException
    {
        return SSAPIOperations.getInstance().saveOrUpdateReviews( surveyDetails );
    }


    @Override public List<Object> prepareTupleForFailure()
    {
        return new Values( 0l, null );
    }


    @Override public void declareOutputFields( OutputFieldsDeclarer declarer )
    {
        declarer.declare( new Fields( "comapanyId", "surveyDetails" ) );
    }


    @Override public void prepare( @SuppressWarnings ( "rawtypes") Map stormConf, TopologyContext context, OutputCollector collector )
    {
        super.prepare( stormConf, context, collector );

    }
}
