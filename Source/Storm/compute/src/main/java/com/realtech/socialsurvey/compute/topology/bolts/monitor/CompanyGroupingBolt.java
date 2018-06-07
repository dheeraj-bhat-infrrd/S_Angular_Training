package com.realtech.socialsurvey.compute.topology.bolts.monitor;

import com.google.gson.reflect.TypeToken;
import com.realtech.socialsurvey.compute.entities.SocialResponseType;
import com.realtech.socialsurvey.compute.entities.response.FacebookFeedData;
import com.realtech.socialsurvey.compute.entities.response.InstagramMediaData;
import com.realtech.socialsurvey.compute.entities.response.SocialResponseObject;
import com.realtech.socialsurvey.compute.entities.response.TwitterFeedData;
import com.realtech.socialsurvey.compute.entities.response.linkedin.LinkedinFeedData;
import com.realtech.socialsurvey.compute.enums.SocialFeedType;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck;
import com.realtech.socialsurvey.compute.utils.ConversionUtils;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;


/**
 * @author manish
 *
 */
public class CompanyGroupingBolt extends BaseComputeBoltWithAck
{

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger( CompanyGroupingBolt.class );


    @Override
    public void executeTuple( Tuple input )
    {
        String postString = input.getString( 0 );
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "Filter :{}", postString );
        }
        // Extract response type
        SocialResponseType socialResponseType = ConversionUtils.deserialize( postString, SocialResponseType.class );
        
        SocialResponseObject<?> post = null;
        String postId = null;
        long companyId = 0L;
        
        // Check and deserialize response from social media
        if ( socialResponseType != null && socialResponseType.getType() != null ) {
            if ( socialResponseType.getType().equals( SocialFeedType.FACEBOOK ) ) {
                post = ConversionUtils.deserialize( postString,
                        new TypeToken<SocialResponseObject<FacebookFeedData>>() {}.getType() );
            } else if ( socialResponseType.getType().equals( SocialFeedType.TWITTER ) ) {
                post = ConversionUtils.deserialize( postString,
                        new TypeToken<SocialResponseObject<TwitterFeedData>>() {}.getType() );
            } else if ( socialResponseType.getType().equals( SocialFeedType.LINKEDIN ) ) {
                post = ConversionUtils.deserialize( postString,
                        new TypeToken<SocialResponseObject<LinkedinFeedData>>() {}.getType() );
            } else if( socialResponseType.getType().equals(SocialFeedType.INSTAGRAM) ) {
                post = ConversionUtils.deserialize( postString,
                        new TypeToken<SocialResponseObject<InstagramMediaData>>() {}.getType());
            }
            
            if ( post != null ) {
                companyId = post.getCompanyId();
                postId = post.getPostId();
            }
        }
        
        LOG.info("Emitting tuple with post having postId = {}",  postId);
        _collector.emit( input, Arrays.asList( companyId, post ) );
    }

    @Override
    public List<Object> prepareTupleForFailure() {
        return new Values(0L, null);
    }


    @Override
    public void declareOutputFields( OutputFieldsDeclarer declarer )
    {
        declarer.declare( new Fields( "companyId", "post" ) );
    }
}
