package com.realtech.socialsurvey.compute.topology.bolts.monitor;

import com.realtech.socialsurvey.compute.entities.response.SocialResponseObject;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RetryHandlerBolt extends BaseComputeBoltWithAck{

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger( RetryHandlerBolt.class );

    @Override
    public void executeTuple(Tuple input) {
        boolean success = input.getBooleanByField("isSuccess");
        SocialResponseObject<?> post = (SocialResponseObject<?>) input.getValueByField("post");

        if(input.getSourceStreamId().equals("RETRY_STREAM")){
            if(success && post.isRetried()){
                //TODO delete from mongo
            }
            else if(!success && post.isRetried()){
                //TODO update the retryCount
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
