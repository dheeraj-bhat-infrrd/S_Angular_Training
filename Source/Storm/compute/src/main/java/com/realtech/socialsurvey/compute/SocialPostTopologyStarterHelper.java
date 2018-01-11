package com.realtech.socialsurvey.compute;

import org.apache.storm.Config;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.common.EnvConstants;
import com.realtech.socialsurvey.compute.topology.bolts.monitor.CompanyGroupingBolt;
import com.realtech.socialsurvey.compute.topology.bolts.monitor.FilterSocialPostBolt;
import com.realtech.socialsurvey.compute.topology.bolts.monitor.SaveFeedsToMongoBolt;
import com.realtech.socialsurvey.compute.topology.spouts.KafkaTopicSpoutBuilder;
import com.realtech.socialsurvey.compute.utils.ChararcterUtils;


public class SocialPostTopologyStarterHelper extends TopologyStarterHelper
{

    private static final Logger LOG = LoggerFactory.getLogger( SocialPostTopologyStarterHelper.class );

    public static final String SOCIAL_POST_TOPOLOGY = "SocialPostTopology";


    @Override
    public void displayBanner()
    {
        LOG.info( "███████╗ ██████╗  ██████╗██╗ █████╗ ██╗         ██████╗  ██████╗ ███████╗████████╗" );
        LOG.info( "██╔════╝██╔═══██╗██╔════╝██║██╔══██╗██║         ██╔══██╗██╔═══██╗██╔════╝╚══██╔══╝" );
        LOG.info( "███████╗██║   ██║██║     ██║███████║██║         ██████╔╝██║   ██║███████╗   ██║   " );
        LOG.info( "╚════██║██║   ██║██║     ██║██╔══██║██║         ██╔═══╝ ██║   ██║╚════██║   ██║   " );
        LOG.info( "███████║╚██████╔╝╚██████╗██║██║  ██║███████╗    ██║     ╚██████╔╝███████║   ██║   " );
        LOG.info( "╚══════╝ ╚═════╝  ╚═════╝╚═╝╚═╝  ╚═╝╚══════╝    ╚═╝      ╚═════╝ ╚══════╝   ╚═╝   " );
        LOG.info( "                                                                                  " );
        LOG.info( "                ███████╗██╗██╗  ████████╗███████╗██████╗                          " );
        LOG.info( "                ██╔════╝██║██║  ╚══██╔══╝██╔════╝██╔══██╗                         " );
        LOG.info( "                █████╗  ██║██║     ██║   █████╗  ██████╔╝                         " );
        LOG.info( "                ██╔══╝  ██║██║     ██║   ██╔══╝  ██╔══██╗                         " );
        LOG.info( "                ██║     ██║███████╗██║   ███████╗██║  ██║                         " );
        LOG.info( "                ╚═╝     ╚═╝╚══════╝╚═╝   ╚══════╝╚═╝  ╚═╝                         " );
        LOG.info( "                                                                                  " );
        LOG.info( "    ████████╗ ██████╗ ██████╗  ██████╗ ██╗      ██████╗  ██████╗██╗   ██╗         " );
        LOG.info( "    ╚══██╔══╝██╔═══██╗██╔══██╗██╔═══██╗██║     ██╔═══██╗██╔════╝╚██╗ ██╔╝         " );
        LOG.info( "       ██║   ██║   ██║██████╔╝██║   ██║██║     ██║   ██║██║  ███╗╚████╔╝          " );
        LOG.info( "       ██║   ██║   ██║██╔═══╝ ██║   ██║██║     ██║   ██║██║   ██║ ╚██╔╝           " );
        LOG.info( "       ██║   ╚██████╔╝██║     ╚██████╔╝███████╗╚██████╔╝╚██████╔╝  ██║            " );
        LOG.info( "       ╚═╝    ╚═════╝ ╚═╝      ╚═════╝ ╚══════╝ ╚═════╝  ╚═════╝   ╚═╝            " );
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
        LOG.info( "Creating social post topology" );
        TopologyBuilder builder = new TopologyBuilder();
        // add mail kafka spout
        builder.setSpout( "SocialPostSpout", KafkaTopicSpoutBuilder.socialPostTopicKafkaSpout(), 1 );
        // add bolts
        builder.setBolt( "CompanyGroupingBolt", new CompanyGroupingBolt(), 1 ).shuffleGrouping( "SocialPostSpout" );
        builder.setBolt( "FilterSocialPostBolt", new FilterSocialPostBolt(), 1 ).fieldsGrouping( "CompanyGroupingBolt",
            new Fields( "companyId" ) );
        builder.setBolt( "SaveFeedsToMongoBolt", new SaveFeedsToMongoBolt(), 1 ).shuffleGrouping( "FilterSocialPostBolt" );
        return builder.createTopology();
    }


    public static void main( String[] args )
    {
        LOG.info( "Starting up Social Post topology..." );
        // Run time params should be the first step
        // DO NOT ADD ANY CODE BEFORE THIS LINE
        EnvConstants.runtimeParams( args );

        // Social post topology
        new SocialPostTopologyStarterHelper().submitTopology( EnvConstants.getCluster().equals( EnvConstants.LOCAL_TOPOLOGY ),
            ( EnvConstants.getProfile().equals( EnvConstants.PROFILE_PROD ) )
                ? SocialPostTopologyStarterHelper.SOCIAL_POST_TOPOLOGY
                : ChararcterUtils.appendWithHypen( SocialPostTopologyStarterHelper.SOCIAL_POST_TOPOLOGY,
                    EnvConstants.getProfile() ) );
    }

}
