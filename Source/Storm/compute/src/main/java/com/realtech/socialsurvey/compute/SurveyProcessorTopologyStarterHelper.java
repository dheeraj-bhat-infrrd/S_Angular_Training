package com.realtech.socialsurvey.compute;

import org.apache.storm.Config;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.common.EnvConstants;
import com.realtech.socialsurvey.compute.topology.bolts.surveyprocessor.CalculateSurveyStatsData;
import com.realtech.socialsurvey.compute.topology.bolts.surveyprocessor.FetchPrerequisiteSurveyStatsDataBolt;
import com.realtech.socialsurvey.compute.topology.bolts.surveyprocessor.UpdateSurveyStatsBolt;
import com.realtech.socialsurvey.compute.topology.spouts.KafkaTopicSpoutBuilder;
import com.realtech.socialsurvey.compute.utils.ChararcterUtils;

/**
 * 
 * @author rohitpatidar
 *
 */
public class SurveyProcessorTopologyStarterHelper extends TopologyStarterHelper
{
	
    private static final Logger LOG = LoggerFactory.getLogger( SurveyProcessorTopologyStarterHelper.class );

	
	public static final String SURVEY_PROCESSOR_TOPOLOGY = "SurveyProcessorTopology";
	
	@Override public void displayBanner()
    {
        LOG.info( "███████╗██╗   ██╗██████╗ ██╗   ██╗███████╗██╗   ██╗    ██████╗ ██████╗  ██████╗  ██████╗███████╗███████╗███████╗ ██████╗ ██████╗     ████████╗ ██████╗ ██████╗  ██████╗ ██╗      ██████╗  ██████╗ ██╗   ██╗" );
        LOG.info( "██╔════╝██║   ██║██╔══██╗██║   ██║██╔════╝╚██╗ ██╔╝    ██╔══██╗██╔══██╗██╔═══██╗██╔════╝██╔════╝██╔════╝██╔════╝██╔═══██╗██╔══██╗    ╚══██╔══╝██╔═══██╗██╔══██╗██╔═══██╗██║     ██╔═══██╗██╔════╝ ╚██╗ ██╔╝" );
        LOG.info( "███████╗██║   ██║██████╔╝██║   ██║█████╗   ╚████╔╝     ██████╔╝██████╔╝██║   ██║██║     █████╗  ███████╗███████╗██║   ██║██████╔╝       ██║   ██║   ██║██████╔╝██║   ██║██║     ██║   ██║██║  ███╗ ╚████╔╝ " );
        LOG.info( "╚════██║██║   ██║██╔══██╗╚██╗ ██╔╝██╔══╝    ╚██╔╝      ██╔═══╝ ██╔══██╗██║   ██║██║     ██╔══╝  ╚════██║╚════██║██║   ██║██╔══██╗       ██║   ██║   ██║██╔═══╝ ██║   ██║██║     ██║   ██║██║   ██║  ╚██╔╝  " );
        LOG.info( "███████║╚██████╔╝██║  ██║ ╚████╔╝ ███████╗   ██║       ██║     ██║  ██║╚██████╔╝╚██████╗███████╗███████║███████║╚██████╔╝██║  ██║       ██║   ╚██████╔╝██║     ╚██████╔╝███████╗╚██████╔╝╚██████╔╝   ██║   " );
        LOG.info( "╚══════╝ ╚═════╝ ╚═╝  ╚═╝  ╚═══╝  ╚══════╝   ╚═╝       ╚═╝     ╚═╝  ╚═╝ ╚═════╝  ╚═════╝╚══════╝╚══════╝╚══════╝ ╚═════╝ ╚═╝  ╚═╝       ╚═╝    ╚═════╝ ╚═╝      ╚═════╝ ╚══════╝ ╚═════╝  ╚═════╝    ╚═╝   " );
    }

	@Override
	public Config createConfig(boolean isLocalMode) {
		if ( isLocalMode ) {
            Config config = new Config();
            config.put( Config.TOPOLOGY_DEBUG, true );
            return config;
        } else {
            Config config = new Config();
            config.put( Config.TOPOLOGY_MAX_SPOUT_PENDING, 50 );
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
	protected StormTopology topology() {
		LOG.info( "Creating transaction injestion topology" );
        TopologyBuilder builder = new TopologyBuilder();
        //add the spout
        builder.setSpout("SurveyProcessorSpout", KafkaTopicSpoutBuilder.getInstance().surveyProcessorSpout(), 1);
        //add the bolts
        builder.setBolt("FetchPrerequisiteSurveyStatsDataBolt", new FetchPrerequisiteSurveyStatsDataBolt(), 1)
        .shuffleGrouping("SurveyProcessorSpout");
        builder.setBolt("CalculateSurveyStatsData", new CalculateSurveyStatsData(), 1)
        .shuffleGrouping("FetchPrerequisiteSurveyStatsDataBolt");
        builder.setBolt("UpdateSurveyStatsBolt", new UpdateSurveyStatsBolt(), 1)
        .shuffleGrouping("CalculateSurveyStatsData");
        return builder.createTopology();
	}

	public static void main( String[] args )
    {
        LOG.info( "Starting up survey processor topology..." );
        // Run time params should be the first step
        // DO NOT ADD ANY CODE BEFORE THIS LINE
        EnvConstants.runtimeParams( args );
        new SurveyProcessorTopologyStarterHelper().submitTopology( EnvConstants.getCluster().equals( EnvConstants.LOCAL_TOPOLOGY ),
            ( EnvConstants.getProfile().equals( EnvConstants.PROFILE_PROD ) ) ?
            		SURVEY_PROCESSOR_TOPOLOGY :
                ChararcterUtils.appendWithHypen( SURVEY_PROCESSOR_TOPOLOGY, EnvConstants.getProfile() ) );
    }
}
