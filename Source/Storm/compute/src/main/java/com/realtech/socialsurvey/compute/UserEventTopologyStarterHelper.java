package com.realtech.socialsurvey.compute;

import org.apache.storm.Config;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.common.EnvConstants;
import com.realtech.socialsurvey.compute.topology.bolts.userevent.SaveUserEventToSolrBolt;
import com.realtech.socialsurvey.compute.topology.spouts.KafkaTopicSpoutBuilder;
import com.realtech.socialsurvey.compute.utils.ChararcterUtils;


public class UserEventTopologyStarterHelper extends TopologyStarterHelper
{

    private static final Logger LOG = LoggerFactory.getLogger( UserEventTopologyStarterHelper.class );
    public static final String USER_EVENT_TOPOLOGY = "UserEventTopology";


    @Override
    protected void displayBanner()
    {
        LOG.info( "                                                                                           " );
        LOG.info( "                                                                                           " );
        LOG.info( "     ██╗   ██╗███████╗███████╗██████╗     ███████╗██╗   ██╗███████╗███╗   ██╗████████╗     " );
        LOG.info( "     ██║   ██║██╔════╝██╔════╝██╔══██╗    ██╔════╝██║   ██║██╔════╝████╗  ██║╚══██╔══╝     " );
        LOG.info( "     ██║   ██║███████╗█████╗  ██████╔╝    █████╗  ██║   ██║█████╗  ██╔██╗ ██║   ██║        " );
        LOG.info( "     ██║   ██║╚════██║██╔══╝  ██╔══██╗    ██╔══╝  ╚██╗ ██╔╝██╔══╝  ██║╚██╗██║   ██║        " );
        LOG.info( "     ╚██████╔╝███████║███████╗██║  ██║    ███████╗ ╚████╔╝ ███████╗██║ ╚████║   ██║        " );
        LOG.info( "      ╚═════╝ ╚══════╝╚══════╝╚═╝  ╚═╝    ╚══════╝  ╚═══╝  ╚══════╝╚═╝  ╚═══╝   ╚═╝        " );
        LOG.info( "                                                                                           " );
        LOG.info( "         ████████╗ ██████╗ ██████╗  ██████╗ ██╗      ██████╗  ██████╗██╗   ██╗             " );
        LOG.info( "         ╚══██╔══╝██╔═══██╗██╔══██╗██╔═══██╗██║     ██╔═══██╗██╔════╝╚██╗ ██╔╝             " );
        LOG.info( "            ██║   ██║   ██║██████╔╝██║   ██║██║     ██║   ██║██║  ███╗╚████╔╝              " );
        LOG.info( "            ██║   ██║   ██║██╔═══╝ ██║   ██║██║     ██║   ██║██║   ██║ ╚██╔╝               " );
        LOG.info( "            ██║   ╚██████╔╝██║     ╚██████╔╝███████╗╚██████╔╝╚██████╔╝  ██║                " );
        LOG.info( "            ╚═╝    ╚═════╝ ╚═╝      ╚═════╝ ╚══════╝ ╚═════╝  ╚═════╝   ╚═╝                " );
        LOG.info( "                                                                                           " );
        LOG.info( "                                                                                           " );
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
            config.put( Config.TOPOLOGY_MAX_SPOUT_PENDING, 10 );
            config.put( Config.STORM_NIMBUS_RETRY_TIMES, 3 );
            return config;
        }
    }


    @Override
    protected StormTopology topology()
    {
        LOG.info( "Creating user event topology" );
        TopologyBuilder builder = new TopologyBuilder();
        // add user event kafka spout
        builder.setSpout( "UserEventCaptureSpout", KafkaTopicSpoutBuilder.getInstance().userEventTopicSpout(), 1 );
        // add bolt
        builder.setBolt( "SaveUserEventToSolrBolt", new SaveUserEventToSolrBolt(), 1 )
            .shuffleGrouping( "UserEventCaptureSpout" );
        return builder.createTopology();
    }


    public static void main( String[] args )
    {
        LOG.info( "Starting up user event topology..." );
        // Run time params should be the first step
        // DO NOT ADD ANY CODE BEFORE THIS LINE
        EnvConstants.runtimeParams( args );
        new UserEventTopologyStarterHelper().submitTopology( EnvConstants.getCluster().equals( EnvConstants.LOCAL_TOPOLOGY ),
            ( EnvConstants.getProfile().equals( EnvConstants.PROFILE_PROD ) ) ? USER_EVENT_TOPOLOGY
                : ChararcterUtils.appendWithHypen( USER_EVENT_TOPOLOGY, EnvConstants.getProfile() ) );
    }

}
