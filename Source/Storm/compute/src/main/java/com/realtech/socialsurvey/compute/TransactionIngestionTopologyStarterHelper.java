package com.realtech.socialsurvey.compute;

import org.apache.storm.Config;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.common.EnvConstants;
import com.realtech.socialsurvey.compute.topology.bolts.transactioningestion.BatchSurveyAndSendToApi;
import com.realtech.socialsurvey.compute.topology.bolts.transactioningestion.ConvertToSurveyObject;
import com.realtech.socialsurvey.compute.topology.bolts.transactioningestion.DownloadFromAmazonS3Bolt;
import com.realtech.socialsurvey.compute.topology.spouts.KafkaTopicSpoutBuilder;
import com.realtech.socialsurvey.compute.utils.ChararcterUtils;

public class TransactionIngestionTopologyStarterHelper extends TopologyStarterHelper
{
    private static final Logger LOG = LoggerFactory.getLogger( TransactionIngestionTopologyStarterHelper.class );
    public static final String TRANSACTION_INGESTION_TOPOLOGY = "TransactionIngestionTopology";
    
    @Override protected void displayBanner()
    {
        LOG.info( " ████████╗██████╗  █████╗ ███╗   ██╗███████╗ █████╗  ██████╗████████╗██╗ ██████╗ ███╗   ██╗ " );   
        LOG.info( " ╚══██╔══╝██╔══██╗██╔══██╗████╗  ██║██╔════╝██╔══██╗██╔════╝╚══██╔══╝██║██╔═══██╗████╗  ██║ " );  
        LOG.info( "    ██║   ██████╔╝███████║██╔██╗ ██║███████╗███████║██║        ██║   ██║██║   ██║██╔██╗ ██║ " );   
        LOG.info( "    ██║   ██╔══██╗██╔══██║██║╚██╗██║╚════██║██╔══██║██║        ██║   ██║██║   ██║██║╚██╗██║ " );   
        LOG.info( "    ██║   ██║  ██║██║  ██║██║ ╚████║███████║██║  ██║╚██████╗   ██║   ██║╚██████╔╝██║ ╚████║ " );   
        LOG.info( "    ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═╝╚═╝  ╚═══╝╚══════╝╚═╝  ╚═╝ ╚═════╝   ╚═╝   ╚═╝ ╚═════╝ ╚═╝  ╚═══╝ " );   
        LOG.info( "                                                                                            " );   
        LOG.info( "         ██╗███╗   ██╗ ██████╗ ███████╗███████╗████████╗██╗ ██████╗ ███╗   ██╗              " );   
        LOG.info( "         ██║████╗  ██║██╔════╝ ██╔════╝██╔════╝╚══██╔══╝██║██╔═══██╗████╗  ██║              " );   
        LOG.info( "         ██║██╔██╗ ██║██║  ███╗█████╗  ███████╗   ██║   ██║██║   ██║██╔██╗ ██║              " );   
        LOG.info( "         ██║██║╚██╗██║██║   ██║██╔══╝  ╚════██║   ██║   ██║██║   ██║██║╚██╗██║              " );   
        LOG.info( "         ██║██║ ╚████║╚██████╔╝███████╗███████║   ██║   ██║╚██████╔╝██║ ╚████║              " );   
        LOG.info( "         ╚═╝╚═╝  ╚═══╝ ╚═════╝ ╚══════╝╚══════╝   ╚═╝   ╚═╝ ╚═════╝ ╚═╝  ╚═══╝              " );   
        LOG.info( "                                                                                            " );   
        LOG.info( "         ████████╗ ██████╗ ██████╗  ██████╗ ██╗      ██████╗  ██████╗██╗   ██╗              " );   
        LOG.info( "         ╚══██╔══╝██╔═══██╗██╔══██╗██╔═══██╗██║     ██╔═══██╗██╔════╝╚██╗ ██╔╝              " );   
        LOG.info( "            ██║   ██║   ██║██████╔╝██║   ██║██║     ██║   ██║██║  ███╗╚████╔╝               " );   
        LOG.info( "            ██║   ██║   ██║██╔═══╝ ██║   ██║██║     ██║   ██║██║   ██║ ╚██╔╝                " );  
        LOG.info( "            ██║   ╚██████╔╝██║     ╚██████╔╝███████╗╚██████╔╝╚██████╔╝  ██║                 " );   
        LOG.info( "            ╚═╝    ╚═════╝ ╚═╝      ╚═════╝ ╚══════╝ ╚═════╝  ╚═════╝   ╚═╝                 " );

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
        LOG.info( "Creating transaction injestion topology" );
        TopologyBuilder builder = new TopologyBuilder();
        //add the spout
        builder.setSpout("TransactionIngestionSpout", KafkaTopicSpoutBuilder.getInstance().transactionIngestionSpout(), 1);
        //add the bolts
        builder.setBolt("DownloadFromAmazonS3Bolt", new DownloadFromAmazonS3Bolt(), 1)
        .shuffleGrouping("TransactionIngestionSpout");
        builder.setBolt("ConvertToSurveyObject", new ConvertToSurveyObject(), 1)
        .shuffleGrouping("DownloadFromAmazonS3Bolt");
        builder.setBolt("BatchSurveyAndSendToApi", new BatchSurveyAndSendToApi(), 1)
        .shuffleGrouping("ConvertToSurveyObject");
        return builder.createTopology();
    }
    

    public static void main( String[] args )
    {
        LOG.info( "Starting up transaction injestion topology..." );
        // Run time params should be the first step
        // DO NOT ADD ANY CODE BEFORE THIS LINE
        EnvConstants.runtimeParams( args );
        new TransactionIngestionTopologyStarterHelper().submitTopology( EnvConstants.getCluster().equals( EnvConstants.LOCAL_TOPOLOGY ),
                ( EnvConstants.getProfile().equals( EnvConstants.PROFILE_PROD ) ) ?
                    TRANSACTION_INGESTION_TOPOLOGY :
                        ChararcterUtils.appendWithHypen(TRANSACTION_INGESTION_TOPOLOGY, EnvConstants.getProfile() ) );
    }
    
}
