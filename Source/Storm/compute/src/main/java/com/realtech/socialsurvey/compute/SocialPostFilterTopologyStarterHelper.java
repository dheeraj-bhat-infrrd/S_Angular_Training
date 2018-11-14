package com.realtech.socialsurvey.compute;

import com.realtech.socialsurvey.compute.common.EnvConstants;
import com.realtech.socialsurvey.compute.topology.bolts.monitor.*;
import com.realtech.socialsurvey.compute.topology.bolts.monitor.BulkSaveToMongo;
import com.realtech.socialsurvey.compute.topology.spouts.KafkaTopicSpoutBuilder;
import com.realtech.socialsurvey.compute.utils.ChararcterUtils;
import org.apache.storm.Config;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Social post topology starter
 * @author manish
 *
 */
public class SocialPostFilterTopologyStarterHelper extends TopologyStarterHelper
{

    private static final Logger LOG = LoggerFactory.getLogger( SocialPostFilterTopologyStarterHelper.class );

    public static final String TOPOLOGY_NAME = "SocialPostFilterTopology";


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
            config.put( Config.TOPOLOGY_MAX_SPOUT_PENDING, 50 );
            config.put( Config.STORM_NIMBUS_RETRY_TIMES, 3 );
            config.put( Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 60 );
            /* The maximum amount of time given to the topology to
               fully process a message emitted by a spout. If the
               message is not acked within this time frame,
               Storm will fail the message on the spout.
               Some spouts implementations will then replay the
               message at a later time. */
            config.put(Config.TOPOLOGY_MESSAGE_TIMEOUT_SECS, 300);
            return config;
        }
    }

    @Override
    protected StormTopology topology()
    {
        LOG.info( "Creating social post topology" );
        TopologyBuilder builder = new TopologyBuilder();
        // add mail kafka spout
        builder.setSpout( "SocialPostSpout", KafkaTopicSpoutBuilder.getInstance().socialPostTopicKafkaSpout(), 1 );
        // add bolts
        builder.setBolt( "CompanyGroupingBolt", new CompanyGroupingBolt(), 1 ).shuffleGrouping( "SocialPostSpout" );
        builder.setBolt( "FilterSocialPostBolt", new FilterSocialPostBolt(), 1 ).fieldsGrouping( "CompanyGroupingBolt",
            new Fields( "companyId" ) );
        builder.setBolt("BulkSaveToMongo", new BulkSaveToMongo(), 1)
            .shuffleGrouping("FilterSocialPostBolt");

        return builder.createTopology();
    }


    public static void main( String[] args )
    {
        LOG.info( "Starting up Social Post topology..." );
        // Run time params should be the first step
        // DO NOT ADD ANY CODE BEFORE THIS LINE
        EnvConstants.runtimeParams( args );

        // Social post topology
        new SocialPostFilterTopologyStarterHelper().submitTopology( EnvConstants.getCluster().equals( EnvConstants.LOCAL_TOPOLOGY ),
            ( EnvConstants.getProfile().equals( EnvConstants.PROFILE_PROD ) )
                ? SocialPostFilterTopologyStarterHelper.TOPOLOGY_NAME
                : ChararcterUtils.appendWithHypen( SocialPostFilterTopologyStarterHelper.TOPOLOGY_NAME,
                EnvConstants.getProfile() ) );
    }

}