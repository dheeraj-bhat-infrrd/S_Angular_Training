package com.realtech.socialsurvey.compute;

import com.realtech.socialsurvey.compute.common.ComputeConstants;
import com.realtech.socialsurvey.compute.common.EnvConstants;
import com.realtech.socialsurvey.compute.topology.bolts.emailreports.*;
import com.realtech.socialsurvey.compute.topology.spouts.KafkaTopicSpoutBuilder;
import com.realtech.socialsurvey.compute.utils.ChararcterUtils;
import org.apache.storm.Config;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class ReportsTopologyStarterHelper extends TopologyStarterHelper
{

    private static final Logger LOG = LoggerFactory.getLogger( ReportsTopologyStarterHelper.class );
    public static final String REPORTS_TOPOLOGY = "ReportsTopology";


    @Override protected void displayBanner()
    {
        LOG.info( "          ██████╗ ███████╗██████╗  ██████╗ ██████╗ ████████╗███████╗           " );
        LOG.info( "          ██╔══██╗██╔════╝██╔══██╗██╔═══██╗██╔══██╗╚══██╔══╝██╔════╝           " );
        LOG.info( "          ██████╔╝█████╗  ██████╔╝██║   ██║██████╔╝   ██║   ███████╗           " );
        LOG.info( "          ██╔══██╗██╔══╝  ██╔═══╝ ██║   ██║██╔══██╗   ██║   ╚════██║           " );
        LOG.info( "          ██║  ██║███████╗██║     ╚██████╔╝██║  ██║   ██║   ███████║           " );
        LOG.info( "          ╚═╝  ╚═╝╚══════╝╚═╝      ╚═════╝ ╚═╝  ╚═╝   ╚═╝   ╚══════╝           " );
        LOG.info( "      ████████╗ ██████╗ ██████╗  ██████╗ ██╗      ██████╗  ██████╗██╗   ██╗    " );
        LOG.info( "      ╚══██╔══╝██╔═══██╗██╔══██╗██╔═══██╗██║     ██╔═══██╗██╔════╝╚██╗ ██╔╝    " );
        LOG.info( "         ██║   ██║   ██║██████╔╝██║   ██║██║     ██║   ██║██║  ███╗╚████╔╝     " );
        LOG.info( "         ██║   ██║   ██║██╔═══╝ ██║   ██║██║     ██║   ██║██║   ██║ ╚██╔╝      " );
        LOG.info( "         ██║   ╚██████╔╝██║     ╚██████╔╝███████╗╚██████╔╝╚██████╔╝  ██║       " );
        LOG.info( "         ╚═╝    ╚═════╝ ╚═╝      ╚═════╝ ╚══════╝ ╚═════╝  ╚═════╝   ╚═╝       " );
    }


    @Override protected boolean validateTopologyEnvironment()
    {
        if ( !EnvConstants.amazonAccessKey().isPresent() || !EnvConstants.amazonSecretKey().isPresent() ) {
            LOG.warn( "Unsatisfied configuration either Amazon access key/ secret" );
            return false;
        } else {
            return true;
        }
    }


    @Override protected Config enhanceConfigWithCustomVariables( Config config )
    {
        config.put( ComputeConstants.AMAZON_ACCESS_KEY, EnvConstants.amazonAccessKey().orElse( null ) );
        config.put( ComputeConstants.AMAZON_SECRET_KEY, EnvConstants.amazonSecretKey().orElse( null ) );
        return config;
    }

    @Override public Config createConfig( boolean isLocalMode )
    {
        if ( isLocalMode ) {
            Config config = new Config();
            config.put( Config.TOPOLOGY_DEBUG, true );
            return config;
        } else {
            Config config = new Config();
            config.put( Config.TOPOLOGY_MAX_SPOUT_PENDING, 5000 );
            config.put( Config.STORM_NIMBUS_RETRY_TIMES, 3 );
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


    @Override protected StormTopology topology()
    {
        LOG.info( "Creating mail reports topology" );
        TopologyBuilder builder = new TopologyBuilder();
        //add the spout
        builder.setSpout("ReportGenerationSpout", KafkaTopicSpoutBuilder.getInstance().reportGenerationSpout(), 1);
        //add the bolts
        builder.setBolt("AggregateSolrQueryBolt", new AggregateSolrQueryBolt(), 1)
        .shuffleGrouping("ReportGenerationSpout");
        
        return builder.createTopology();
    }


    public static void main( String[] args )
    {
        LOG.info( "Starting up mail reports topology..." );
        // Run time params should be the first step
        // DO NOT ADD ANY CODE BEFORE THIS LINE
        EnvConstants.runtimeParams( args );
        new ReportsTopologyStarterHelper().submitTopology( EnvConstants.getCluster().equals( EnvConstants.LOCAL_TOPOLOGY ),
                ( EnvConstants.getProfile().equals( EnvConstants.PROFILE_PROD ) ) ?
                        REPORTS_TOPOLOGY :
                        ChararcterUtils.appendWithHypen(REPORTS_TOPOLOGY, EnvConstants.getProfile() ) );
    }
}
