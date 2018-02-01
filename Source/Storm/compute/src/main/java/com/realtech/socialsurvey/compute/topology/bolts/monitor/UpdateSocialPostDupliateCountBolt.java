package com.realtech.socialsurvey.compute.topology.bolts.monitor;

import com.realtech.socialsurvey.compute.common.SSAPIOperations;
import com.realtech.socialsurvey.compute.entities.response.SocialResponseObject;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class UpdateSocialPostDupliateCountBolt extends BaseComputeBolt {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger( SaveFeedsToMongoBolt.class );

    @Override
    public void execute(Tuple tuple) {
        LOG.info("Executing update duplicate feedCount bolt ... ");
        long companyId = tuple.getLongByField("companyId");
        SocialResponseObject<?> socialPost = (SocialResponseObject<?>) tuple.getValueByField( "post" );

        int hash = socialPost.getHash();
        LOG.debug("HashCode of the social text = {}", hash);

        if(hash != 0) {
            //check if posts with same hash are already present
            Optional<Long> duplicateCount = getSocialPostDuplicateCount(hash, companyId);
            if (duplicateCount.isPresent() && duplicateCount.get() > 1) {
                //update all the duplicateCount field of the social post
                Optional<Long> updatedPosts = updateSocialPostDuplicateCount(hash, companyId, duplicateCount.get());
                if (updatedPosts.isPresent() && updatedPosts.get() > 1) {
                    LOG.info(" Total {} docs were successfully updated having hash {} ", updatedPosts.get(), hash);
                } else
                    LOG.warn("Something went wrong while updating duplicateCount !!! ");
            } else {
                LOG.info("No duplicates found for the post with hash = {} ", hash);
            }
        }
        _collector.emit( tuple, new Values(companyId, socialPost));
        _collector.ack( tuple );
        LOG.info( "Successfully emitted message." );
    }

    private Optional<Long> updateSocialPostDuplicateCount(int hash, long companyId, long duplicateCount) {
        return SSAPIOperations.getInstance().updateSocialPostDuplicateCount(hash, companyId, duplicateCount);
    }

    private Optional<Long> getSocialPostDuplicateCount(int hash, long comapanyId) {
        return SSAPIOperations.getInstance().getSocialPostDuplicateCount( hash, comapanyId);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare( new Fields( "companyId", "post" ) );
    }
}