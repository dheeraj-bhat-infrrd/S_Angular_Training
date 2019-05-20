package com.realtech.socialsurvey.compute;

import org.apache.storm.Config;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.common.EnvConstants;
import com.realtech.socialsurvey.compute.topology.bolts.contactopt.SaveContactToSolrBolt;
import com.realtech.socialsurvey.compute.topology.bolts.contactopt.SendAcknowledgementBolt;
import com.realtech.socialsurvey.compute.topology.bolts.contactopt.UnsubscribeResubscribeBolt;
import com.realtech.socialsurvey.compute.topology.spouts.KafkaTopicSpoutBuilder;
import com.realtech.socialsurvey.compute.utils.ChararcterUtils;

public class ContactOptTopologyStarterHelper extends TopologyStarterHelper {

	private static final Logger LOG = LoggerFactory.getLogger( ContactOptTopologyStarterHelper.class );
	
	public static final String CONTACT_OPT_TOPOLOGY = "ContactOptTopology";
			
    @Override public void displayBanner()
    {
      LOG.info( "██╗   ██╗███╗   ██╗███████╗██╗   ██╗██████╗ ███████╗ ██████╗██████╗ ██╗██████╗ ███████╗    ██████╗ ███████╗███████╗██╗   ██╗██████╗ ███████╗ ██████╗██████╗ ██╗██████╗ ███████╗" );
      LOG.info( "██║   ██║████╗  ██║██╔════╝██║   ██║██╔══██╗██╔════╝██╔════╝██╔══██╗██║██╔══██╗██╔════╝    ██╔══██╗██╔════╝██╔════╝██║   ██║██╔══██╗██╔════╝██╔════╝██╔══██╗██║██╔══██╗██╔════╝" );
      LOG.info( "██║   ██║██╔██╗ ██║███████╗██║   ██║██████╔╝███████╗██║     ██████╔╝██║██████╔╝█████╗      ██████╔╝█████╗  ███████╗██║   ██║██████╔╝███████╗██║     ██████╔╝██║██████╔╝█████╗  " );
      LOG.info( "██║   ██║██║╚██╗██║╚════██║██║   ██║██╔══██╗╚════██║██║     ██╔══██╗██║██╔══██╗██╔══╝      ██╔══██╗██╔══╝  ╚════██║██║   ██║██╔══██╗╚════██║██║     ██╔══██╗██║██╔══██╗██╔══╝  " );
      LOG.info( "╚██████╔╝██║ ╚████║███████║╚██████╔╝██████╔╝███████║╚██████╗██║  ██║██║██████╔╝███████╗    ██║  ██║███████╗███████║╚██████╔╝██████╔╝███████║╚██████╗██║  ██║██║██████╔╝███████╗" );
      LOG.info( " ╚═════╝ ╚═╝  ╚═══╝╚══════╝ ╚═════╝ ╚═════╝ ╚══════╝ ╚═════╝╚═╝  ╚═╝╚═╝╚═════╝ ╚══════╝    ╚═╝  ╚═╝╚══════╝╚══════╝ ╚═════╝ ╚═════╝ ╚══════╝ ╚═════╝╚═╝  ╚═╝╚═╝╚═════╝ ╚══════╝" );
      LOG.info( "                                                                                                                                                                               " );
      LOG.info( "                                                    ████████╗ ██████╗ ██████╗  ██████╗ ██╗      ██████╗  ██████╗██╗   ██╗                                                      " );
      LOG.info( "                                                    ╚══██╔══╝██╔═══██╗██╔══██╗██╔═══██╗██║     ██╔═══██╗██╔════╝╚██╗ ██╔╝                                                      " );
      LOG.info( "                                                       ██║   ██║   ██║██████╔╝██║   ██║██║     ██║   ██║██║  ███╗╚████╔╝                                                       " );
      LOG.info( "                                                       ██║   ██║   ██║██╔═══╝ ██║   ██║██║     ██║   ██║██║   ██║ ╚██╔╝                                                        " );
      LOG.info( "                                                       ██║   ╚██████╔╝██║     ╚██████╔╝███████╗╚██████╔╝╚██████╔╝  ██║                                                         " );
      LOG.info( "                                                       ╚═╝    ╚═════╝ ╚═╝      ╚═════╝ ╚══════╝ ╚═════╝  ╚═════╝   ╚═╝														    " ); 
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
        LOG.info( "Creating contact unsubscribe/resubscribe topology" );
        TopologyBuilder builder = new TopologyBuilder();
        // add contact opt kafka spout
        builder.setSpout( "ContactOptSpout", KafkaTopicSpoutBuilder.getInstance().contactOptTopicKafkaSpout(), 1 );
        // add bolts
        builder.setBolt( "SaveContactToSolrBolt", new SaveContactToSolrBolt(), 1 ).shuffleGrouping( "ContactOptSpout" );
        builder.setBolt( "UnsubscribeResubscribeBolt", new UnsubscribeResubscribeBolt(), 1 ).shuffleGrouping( "SaveContactToSolrBolt" );
        builder.setBolt( "SendAcknowledgementBolt", new SendAcknowledgementBolt(), 1 ).shuffleGrouping( "UnsubscribeResubscribeBolt" );
        return builder.createTopology();
    }
    
    public static void main( String[] args )
    {
        LOG.info( "Starting up unsubscribe/resubscribe contact topology..." );
        // Run time params should be the first step
        // DO NOT ADD ANY CODE BEFORE THIS LINE
        EnvConstants.runtimeParams( args );
        new ContactOptTopologyStarterHelper().submitTopology( EnvConstants.getCluster().equals( EnvConstants.LOCAL_TOPOLOGY ),
            ( EnvConstants.getProfile().equals( EnvConstants.PROFILE_PROD ) ) ?
            	CONTACT_OPT_TOPOLOGY :
                ChararcterUtils.appendWithHypen( CONTACT_OPT_TOPOLOGY, EnvConstants.getProfile() ) );
    }
}
