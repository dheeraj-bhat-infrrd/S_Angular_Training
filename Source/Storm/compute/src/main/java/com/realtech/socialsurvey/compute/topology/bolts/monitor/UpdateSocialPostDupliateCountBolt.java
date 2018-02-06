package com.realtech.socialsurvey.compute.topology.bolts.monitor;

import java.util.Arrays;
import java.util.Optional;

import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.realtech.socialsurvey.compute.common.SSAPIOperations;
import com.realtech.socialsurvey.compute.entities.response.SocialResponseObject;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBolt;

public class UpdateSocialPostDupliateCountBolt extends BaseComputeBolt {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger( SaveFeedsToMongoBolt.class );

    @Override
    public void execute(Tuple tuple) {
        LOG.info("Executing update duplicate feedCount bolt ... ");
        long companyId = tuple.getLongByField("companyId");
        SocialResponseObject<?> socialPost = (SocialResponseObject<?>) tuple.getValueByField( "post" );
        boolean success = tuple.getBooleanByField("isSuccess");

        int hash = socialPost.getHash();
        boolean isSuccess = true;
        LOG.debug("HashCode of the social text = {}", hash);

        if(success && hash != 0) {
            //check if posts with same hash are already present
            Optional<Long> duplicateCount = getSocialPostDuplicateCount(hash, companyId);
            if (duplicateCount.isPresent() && duplicateCount.get() > 1) {
                //update all the duplicateCount field of the social post
                Optional<Long> updatedPosts = updateSocialPostDuplicateCount(hash, companyId, duplicateCount.get());
                if (updatedPosts.isPresent() && updatedPosts.get() > 1) {
                    LOG.info(" Total {} docs were successfully updated having hash {} ", updatedPosts.get(), hash);
                } else {
                    isSuccess = false;
                    LOG.warn("Something went wrong while updating duplicateCount !!! ");
                    repostMessageToKafka(tuple, companyId, socialPost);
                }
            }
            else if(!duplicateCount.isPresent()){
                isSuccess = false;
                LOG.warn("Something went wrong while fetching duplicateCount !!! ");
                repostMessageToKafka(tuple, companyId, socialPost);
            }
        }
        _collector.emit( "SUCCESS_STREAM",tuple, new Values(isSuccess, companyId, socialPost));
        _collector.ack( tuple );
        LOG.info( "Successfully emitted message." );
    }

    private void repostMessageToKafka(Tuple input, long companyId, SocialResponseObject<?> post) {
        _collector.emit("ERROR_STREAM",input, Arrays.asList(Long.toString(companyId), new Gson().toJson(post)));
    }

    private Optional<Long> updateSocialPostDuplicateCount(int hash, long companyId, long duplicateCount) {
        return SSAPIOperations.getInstance().updateSocialPostDuplicateCount(hash, companyId, duplicateCount);
    }

    private Optional<Long> getSocialPostDuplicateCount(int hash, long comapanyId) {
        return SSAPIOperations.getInstance().getSocialPostDuplicateCount( hash, comapanyId);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declareStream("SUCCESS_STREAM", new Fields("isSuccess", "companyId", "post" ) );
        declarer.declareStream("ERROR_STREAM", new Fields( "companyId", "post" ));
    }
}