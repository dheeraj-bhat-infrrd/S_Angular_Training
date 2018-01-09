package com.realtech.socialsurvey.compute;

import org.apache.storm.Config;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.common.EnvConstants;
import com.realtech.socialsurvey.compute.topology.bolts.mailevents.UpdateMailEventsBolt;
import com.realtech.socialsurvey.compute.topology.spouts.KafkaTopicSpoutBuilder;
import com.realtech.socialsurvey.compute.utils.ChararcterUtils;


public class MailEventsTopologyStarterHelper extends TopologyStarterHelper
{

    private static final Logger LOG = LoggerFactory.getLogger( MailEventsTopologyStarterHelper.class );
    public static final String MAIL_EVENT_TOPOLOGY = "MailEventTopology";


    @Override
    protected void displayBanner()
    {
        LOG.info( "███╗   ███╗ █████╗ ██╗██╗         ███████╗██╗   ██╗███████╗███╗   ██╗████████╗" );
        LOG.info( "████╗ ████║██╔══██╗██║██║         ██╔════╝██║   ██║██╔════╝████╗  ██║╚══██╔══╝" );
        LOG.info( "██╔████╔██║███████║██║██║         █████╗  ██║   ██║█████╗  ██╔██╗ ██║   ██║   " );
        LOG.info( "██║╚██╔╝██║██╔══██║██║██║         ██╔══╝  ╚██╗ ██╔╝██╔══╝  ██║╚██╗██║   ██║   " );
        LOG.info( "██║ ╚═╝ ██║██║  ██║██║███████╗    ███████╗ ╚████╔╝ ███████╗██║ ╚████║   ██║   " );
        LOG.info( "╚═╝     ╚═╝╚═╝  ╚═╝╚═╝╚══════╝    ╚══════╝  ╚═══╝  ╚══════╝╚═╝  ╚═══╝   ╚═╝   " );
        LOG.info( "                                                                              " );
        LOG.info( "    ████████╗ ██████╗ ██████╗  ██████╗ ██╗      ██████╗  ██████╗██╗   ██╗     " );
        LOG.info( "    ╚══██╔══╝██╔═══██╗██╔══██╗██╔═══██╗██║     ██╔═══██╗██╔════╝╚██╗ ██╔╝     " );
        LOG.info( "       ██║   ██║   ██║██████╔╝██║   ██║██║     ██║   ██║██║  ███╗╚████╔╝      " );
        LOG.info( "       ██║   ██║   ██║██╔═══╝ ██║   ██║██║     ██║   ██║██║   ██║ ╚██╔╝       " );
        LOG.info( "       ██║   ╚██████╔╝██║     ╚██████╔╝███████╗╚██████╔╝╚██████╔╝  ██║        " );
        LOG.info( "       ╚═╝    ╚═════╝ ╚═╝      ╚═════╝ ╚══════╝ ╚═════╝  ╚═════╝   ╚═╝        " );
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
        LOG.info( "Creating mail event topology" );
        TopologyBuilder builder = new TopologyBuilder();
        // add mail kafka spout
        builder.setSpout( "SendgridEventCaptureSpout", KafkaTopicSpoutBuilder.sendGridEventTopicSpout(), 1 );
        // add bolts
        builder.setBolt( "ProceessEventBolt", new UpdateMailEventsBolt(), 1 ).shuffleGrouping( "SendgridEventCaptureSpout" );
        return builder.createTopology();
    }


    public static void main( String[] args )
    {
        LOG.info( "Starting up mail event topology..." );
        // Run time params should be the first step
        // DO NOT ADD ANY CODE BEFORE THIS LINE
        EnvConstants.runtimeParams( args );
        new MailEventsTopologyStarterHelper().submitTopology( EnvConstants.getCluster().equals( EnvConstants.LOCAL_TOPOLOGY ),
            ( EnvConstants.getProfile().equals( EnvConstants.PROFILE_PROD ) ) ? MAIL_EVENT_TOPOLOGY
                : ChararcterUtils.appendWithHypen( MAIL_EVENT_TOPOLOGY, EnvConstants.getProfile() ) );
    }

}
