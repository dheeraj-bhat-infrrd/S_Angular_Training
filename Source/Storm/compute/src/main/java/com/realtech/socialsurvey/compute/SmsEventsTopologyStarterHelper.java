package com.realtech.socialsurvey.compute;

import org.apache.storm.Config;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.common.EnvConstants;
import com.realtech.socialsurvey.compute.topology.bolts.smsevents.UpdateSmsEventBolt;
import com.realtech.socialsurvey.compute.topology.spouts.KafkaTopicSpoutBuilder;
import com.realtech.socialsurvey.compute.utils.ChararcterUtils;

public class SmsEventsTopologyStarterHelper extends TopologyStarterHelper {

	private static final Logger LOG = LoggerFactory.getLogger( SmsEventsTopologyStarterHelper.class );
    public static final String SMS_EVENT_TOPOLOGY = "SmsEventTopology";


    @Override
    protected void displayBanner()
    {
        LOG.info( "███████╗███╗   ███╗███████╗        ███████╗██╗   ██╗███████╗███╗   ██╗████████╗" );
        LOG.info( "██╔════╝████╗ ████║██╔════╝        ██╔════╝██║   ██║██╔════╝████╗  ██║╚══██╔══╝" );
        LOG.info( "███████╗██╔████╔██║███████╗        █████╗  ██║   ██║█████╗  ██╔██╗ ██║   ██║   " );
        LOG.info( "╚════██║██║╚██╔╝██║╚════██║        ██╔══╝  ╚██╗ ██╔╝██╔══╝  ██║╚██╗██║   ██║   " );
        LOG.info( "███████║██║ ╚═╝ ██║███████║        ███████╗ ╚████╔╝ ███████╗██║ ╚████║   ██║   " );
        LOG.info( "╚══════╝╚═╝     ╚═╝╚══════╝        ╚══════╝  ╚═══╝  ╚══════╝╚═╝  ╚═══╝   ╚═╝   " );
        LOG.info( "                                                                               " );
        LOG.info( "    ████████╗ ██████╗ ██████╗  ██████╗ ██╗      ██████╗  ██████╗██╗   ██╗      " );
        LOG.info( "    ╚══██╔══╝██╔═══██╗██╔══██╗██╔═══██╗██║     ██╔═══██╗██╔════╝╚██╗ ██╔╝      " );
        LOG.info( "       ██║   ██║   ██║██████╔╝██║   ██║██║     ██║   ██║██║  ███╗╚████╔╝       " );
        LOG.info( "       ██║   ██║   ██║██╔═══╝ ██║   ██║██║     ██║   ██║██║   ██║ ╚██╔╝        " );
        LOG.info( "       ██║   ╚██████╔╝██║     ╚██████╔╝███████╗╚██████╔╝╚██████╔╝  ██║         " );
        LOG.info( "       ╚═╝    ╚═════╝ ╚═╝      ╚═════╝ ╚══════╝ ╚═════╝  ╚═════╝   ╚═╝         " );
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
            config.put( Config.TOPOLOGY_MAX_SPOUT_PENDING, 100 );
            config.put( Config.STORM_NIMBUS_RETRY_TIMES, 3 );
            return config;
        }
    }


    @Override
    protected StormTopology topology()
    {
        LOG.info( "Creating sms event topology" );
        TopologyBuilder builder = new TopologyBuilder();
        // add twilio sms event kafka spout
        builder.setSpout( "TwilioSmsEventCaptureSpout", KafkaTopicSpoutBuilder.getInstance().twilioSmsEventTopicSpout(), 1 );
        // add bolts
        builder.setBolt( "ProcessSmsEventBolt", new UpdateSmsEventBolt(), 1 ).shuffleGrouping( "TwilioSmsEventCaptureSpout" );
        return builder.createTopology();
    }


    public static void main( String[] args )
    {
        LOG.info( "Starting up sms event topology..." );
        // Run time params should be the first step
        // DO NOT ADD ANY CODE BEFORE THIS LINE
        EnvConstants.runtimeParams( args );
        new SmsEventsTopologyStarterHelper().submitTopology( EnvConstants.getCluster().equals( EnvConstants.LOCAL_TOPOLOGY ),
            ( EnvConstants.getProfile().equals( EnvConstants.PROFILE_PROD ) ) ? SMS_EVENT_TOPOLOGY
                : ChararcterUtils.appendWithHypen( SMS_EVENT_TOPOLOGY, EnvConstants.getProfile() ) );
    }
}
