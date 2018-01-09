package com.realtech.socialsurvey.compute.topology.bolts.monitor;

import java.util.Arrays;

import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.entities.SocialPost;
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
            LOG.debug( "Filter :{}", input.getString( 0 ) );
        }
        SocialPost post = ConversionUtils.deserialize( input.getString( 0 ), SocialPost.class );
        long companyId = 0L;
        if ( post != null ) {
            companyId = post.getCompanyId();
        }
        _collector.emit( input, Arrays.asList( companyId, post ) );
        _collector.ack( input );
    }


    @Override
    public void declareOutputFields( OutputFieldsDeclarer declarer )
    {
        declarer.declare( new Fields( "companyId", "post" ) );
    }
}
