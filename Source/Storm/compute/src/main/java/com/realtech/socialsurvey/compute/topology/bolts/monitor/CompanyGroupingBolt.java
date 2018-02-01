package com.realtech.socialsurvey.compute.topology.bolts.monitor;

import java.util.Arrays;

import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.reflect.TypeToken;
import com.realtech.socialsurvey.compute.entities.SocialResponseType;
import com.realtech.socialsurvey.compute.entities.response.FacebookFeedData;
import com.realtech.socialsurvey.compute.entities.response.SocialResponseObject;
import com.realtech.socialsurvey.compute.entities.response.TwitterFeedData;
import com.realtech.socialsurvey.compute.entities.response.linkedin.LinkedinFeedData;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBolt;
import com.realtech.socialsurvey.compute.utils.ConversionUtils;


/**
 * @author manish
 *
 */
public class CompanyGroupingBolt extends BaseComputeBolt
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger( CompanyGroupingBolt.class );


    @Override
    public void execute( Tuple input )
    {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "Filter :{}", input.getValue( 0 ) );
        }
        // Extract response type
        SocialResponseType socialResponseType = ConversionUtils.deserialize( input.getString( 0 ), SocialResponseType.class );

        SocialResponseObject<?> post = null;
        // Check and desierialise reponse from social media
        if ( socialResponseType != null && socialResponseType.getType() != null ) {
            if ( socialResponseType.getType().equals( "FACEBOOK" ) ) {
                post = ConversionUtils.deserialize( input.getString( 0 ),
                    new TypeToken<SocialResponseObject<FacebookFeedData>>() {}.getType() );
            } else if ( socialResponseType.getType().equals( "TWITTER" ) ) {
                post = ConversionUtils.deserialize( input.getString( 0 ),
                    new TypeToken<SocialResponseObject<TwitterFeedData>>() {}.getType() );
            } else if ( socialResponseType.getType().equals( "LINKEDIN" ) ) {
                post = ConversionUtils.deserialize( input.getString( 0 ),
                    new TypeToken<SocialResponseObject<LinkedinFeedData>>() {}.getType() );
            }
        }

        long companyId = 0L;
        if ( post != null ) {
            companyId = post.getCompanyId();
        }
        _collector.emit( input, Arrays.asList( companyId, post, socialResponseType ) );
        _collector.ack( input );
    }


    @Override
    public void declareOutputFields( OutputFieldsDeclarer declarer )
    {
        declarer.declare( new Fields( "companyId", "post", "type" ) );
    }
}
