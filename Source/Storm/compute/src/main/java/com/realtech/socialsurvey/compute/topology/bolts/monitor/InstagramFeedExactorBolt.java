package com.realtech.socialsurvey.compute.topology.bolts.monitor;

import com.realtech.socialsurvey.compute.entities.SocialMediaTokenResponse;
import com.realtech.socialsurvey.compute.feeds.InstagramFeedProcessor;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBolt;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class InstagramFeedExactorBolt extends BaseComputeBolt {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger( InstagramFeedExactorBolt.class );

    private InstagramFeedProcessor instagramFeedProcessor;

    @Override
    public void execute(Tuple input) {
        try {
            SocialMediaTokenResponse mediaToken = (SocialMediaTokenResponse) input.getValueByField("mediaToken");
            Long companyId = mediaToken.getCompanyId();
            if (isRateLimitExceeded( /*pass the media token*/)) {
                LOG.warn("Rate limit exceeded");
            } else {

            }
        } catch (Exception e) {
            LOG.error(" Error while fetching posts from instragram {} ", e );
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }

    private boolean isRateLimitExceeded(){
        return false;
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        super.prepare(stormConf, context, collector);

    }
}
