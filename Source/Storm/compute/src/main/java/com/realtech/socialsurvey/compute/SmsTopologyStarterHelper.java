package com.realtech.socialsurvey.compute;

import org.apache.storm.Config;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.common.EnvConstants;
import com.realtech.socialsurvey.compute.topology.bolts.smssender.CheckUnsubscribedContactNumber;
import com.realtech.socialsurvey.compute.topology.bolts.smssender.SaveSmsToSolrBolt;
import com.realtech.socialsurvey.compute.topology.bolts.smssender.SendSmsBolt;
import com.realtech.socialsurvey.compute.topology.spouts.KafkaTopicSpoutBuilder;
import com.realtech.socialsurvey.compute.utils.ChararcterUtils;

public class SmsTopologyStarterHelper extends TopologyStarterHelper {
	
	private static final Logger LOG = LoggerFactory.getLogger( SmsTopologyStarterHelper.class );

    public static final String SMS_TOPOLOGY = "SmsTopology";
    
    @Override public void displayBanner()
    {
    	
        LOG.info( "███████╗███╗   ███╗███████╗    ████████╗ ██████╗ ██████╗  ██████╗ ██╗      ██████╗  ██████╗██╗   ██╗" );
        LOG.info( "██╔════╝████╗ ████║██╔════╝    ╚══██╔══╝██╔═══██╗██╔══██╗██╔═══██╗██║     ██╔═══██╗██╔════╝╚██╗ ██╔╝" );
        LOG.info( "███████╗██╔████╔██║███████╗       ██║   ██║   ██║██████╔╝██║   ██║██║     ██║   ██║██║  ███╗╚████╔╝ " );
        LOG.info( "╚════██║██║╚██╔╝██║╚════██║       ██║   ██║   ██║██╔═══╝ ██║   ██║██║     ██║   ██║██║   ██║ ╚██╔╝  " );
        LOG.info( "███████║██║ ╚═╝ ██║███████║       ██║   ╚██████╔╝██║     ╚██████╔╝███████╗╚██████╔╝╚██████╔╝  ██║   " );
        LOG.info( "╚══════╝╚═╝     ╚═╝╚══════╝       ╚═╝    ╚═════╝ ╚═╝      ╚═════╝ ╚══════╝ ╚═════╝  ╚═════╝   ╚═╝   " );
    }

    
    
    @Override public Config createConfig( boolean isLocalMode )
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

    @Override protected StormTopology topology()
    {
        LOG.info( "Creating sms topology" );
        TopologyBuilder builder = new TopologyBuilder();
        // add sms kafka spout
        builder.setSpout( "SmsSenderSpout", KafkaTopicSpoutBuilder.getInstance().smsTopicKafkaSpout(), 1 );
        // add bolts
        builder.setBolt( "CheckUnsubscribedContactBolt", new CheckUnsubscribedContactNumber(), 1 ).shuffleGrouping( "SmsSenderSpout" );
        builder.setBolt( "SaveSmsToSolrBolt", new SaveSmsToSolrBolt(), 1 ).shuffleGrouping( "CheckUnsubscribedContactBolt" );
        builder.setBolt( "SendSmsBolt", new SendSmsBolt(), 1 ).shuffleGrouping( "SaveSmsToSolrBolt" );
        return builder.createTopology();
    }

    public static void main( String[] args )
    {
        LOG.info( "Starting up sms topology..." );
        // Run time params should be the first step
        // DO NOT ADD ANY CODE BEFORE THIS LINE
        EnvConstants.runtimeParams( args );
        new SmsTopologyStarterHelper().submitTopology( EnvConstants.getCluster().equals( EnvConstants.LOCAL_TOPOLOGY ),
            ( EnvConstants.getProfile().equals( EnvConstants.PROFILE_PROD ) ) ?
            	SMS_TOPOLOGY :
                ChararcterUtils.appendWithHypen( SMS_TOPOLOGY, EnvConstants.getProfile() ) );
    }
}
