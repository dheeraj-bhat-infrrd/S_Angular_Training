package com.realtech.socialsurvey.compute;

import org.apache.storm.Config;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.common.EnvConstants;
import com.realtech.socialsurvey.compute.topology.bolts.KafkaProducerBolt;
import com.realtech.socialsurvey.compute.topology.spouts.FacebookFeedExtractorSpout;
import com.realtech.socialsurvey.compute.topology.spouts.LinkedinFeedExtractorSpout;
import com.realtech.socialsurvey.compute.topology.spouts.TwitterFeedExtractorSpout;
import com.realtech.socialsurvey.compute.utils.ChararcterUtils;


public class SocialPostExtractorTopologyStarterHelper extends TopologyStarterHelper
{

    private static final Logger LOG = LoggerFactory.getLogger( SocialPostExtractorTopologyStarterHelper.class );

    public static final String TOPOLOGY_NAME = "SocialPostExtractorTopology";


    @Override
    public void displayBanner()
    {
        LOG.info( "███████╗ ██████╗  ██████╗██╗ █████╗ ██╗         ██████╗  ██████╗ ███████╗████████╗    " );
        LOG.info( "██╔════╝██╔═══██╗██╔════╝██║██╔══██╗██║         ██╔══██╗██╔═══██╗██╔════╝╚══██╔══╝    " );
        LOG.info( "███████╗██║   ██║██║     ██║███████║██║         ██████╔╝██║   ██║███████╗   ██║       " );
        LOG.info( "╚════██║██║   ██║██║     ██║██╔══██║██║         ██╔═══╝ ██║   ██║╚════██║   ██║       " );
        LOG.info( "███████║╚██████╔╝╚██████╗██║██║  ██║███████╗    ██║     ╚██████╔╝███████║   ██║       " );
        LOG.info( "╚══════╝ ╚═════╝  ╚═════╝╚═╝╚═╝  ╚═╝╚══════╝    ╚═╝      ╚═════╝ ╚══════╝   ╚═╝       " );
        LOG.info( "                                                                                      " );
        LOG.info( "  ███████╗██╗  ██╗████████╗██████╗  █████╗  ██████╗████████╗ ██████╗ ██████╗          " );
        LOG.info( "  ██╔════╝╚██╗██╔╝╚══██╔══╝██╔══██╗██╔══██╗██╔════╝╚══██╔══╝██╔═══██╗██╔══██╗         " );
        LOG.info( "  █████╗   ╚███╔╝    ██║   ██████╔╝███████║██║        ██║   ██║   ██║██████╔╝         " );
        LOG.info( "  ██╔══╝   ██╔██╗    ██║   ██╔══██╗██╔══██║██║        ██║   ██║   ██║██╔══██╗         " );
        LOG.info( "  ███████╗██╔╝ ██╗   ██║   ██║  ██║██║  ██║╚██████╗   ██║   ╚██████╔╝██║  ██║         " );
        LOG.info( "  ╚══════╝╚═╝  ╚═╝   ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝   ╚═╝    ╚═════╝ ╚═╝  ╚═╝         " );
        LOG.info( "                                                                                      " );
        LOG.info( "    ████████╗ ██████╗ ██████╗  ██████╗ ██╗      ██████╗  ██████╗██╗   ██╗             " );
        LOG.info( "    ╚══██╔══╝██╔═══██╗██╔══██╗██╔═══██╗██║     ██╔═══██╗██╔════╝╚██╗ ██╔╝             " );
        LOG.info( "       ██║   ██║   ██║██████╔╝██║   ██║██║     ██║   ██║██║  ███╗╚████╔╝              " );
        LOG.info( "       ██║   ██║   ██║██╔═══╝ ██║   ██║██║     ██║   ██║██║   ██║ ╚██╔╝               " );
        LOG.info( "       ██║   ╚██████╔╝██║     ╚██████╔╝███████╗╚██████╔╝╚██████╔╝  ██║                " );
        LOG.info( "       ╚═╝    ╚═════╝ ╚═╝      ╚═════╝ ╚══════╝ ╚═════╝  ╚═════╝   ╚═╝                " );
    }


    @Override
    public Config createConfig( boolean isLocalMode )
    {
        if ( isLocalMode ) {
            Config config = new Config();
            config.put( Config.TOPOLOGY_DEBUG, true );
            return config;
        } else {
            Config config = new Config();
            config.put( Config.TOPOLOGY_MAX_SPOUT_PENDING, 5000 );
            config.put( Config.STORM_NIMBUS_RETRY_TIMES, 3 );
            return config;
        }
    }


    @Override
    protected StormTopology topology()
    {
        LOG.info( "Creating Social Post Extractor topology" );
        TopologyBuilder builder = new TopologyBuilder();
        // add feed extractor spout
        builder.setSpout( "FacebookFeedExtractorSpout", new FacebookFeedExtractorSpout(), 1 );

        builder.setSpout( "TwitterFeedExtractorSpout", new TwitterFeedExtractorSpout(), 1 );

        builder.setSpout( "LinkedinFeedExtractorSpout", new LinkedinFeedExtractorSpout(), 1 );
        // add bolts
        builder.setBolt("KafkaProducerBolt", new KafkaProducerBolt(), 3).shuffleGrouping("LinkedinFeedExtractorSpout")
                .shuffleGrouping("FacebookFeedExtractorSpout").shuffleGrouping("TwitterFeedExtractorSpout");

        return builder.createTopology();
    }


    public static void main( String[] args )
    {
        LOG.info( "Starting up Social Post Extractor topology..." );
        // Run time params should be the first step
        // DO NOT ADD ANY CODE BEFORE THIS LINE
        EnvConstants.runtimeParams( args );

        // Social post topology
        new SocialPostExtractorTopologyStarterHelper().submitTopology(
            EnvConstants.getCluster().equals( EnvConstants.LOCAL_TOPOLOGY ),
            ( EnvConstants.getProfile().equals( EnvConstants.PROFILE_PROD ) )
                ? SocialPostExtractorTopologyStarterHelper.TOPOLOGY_NAME
                : ChararcterUtils.appendWithHypen( SocialPostExtractorTopologyStarterHelper.TOPOLOGY_NAME,
                    EnvConstants.getProfile() ) );
    }
}
