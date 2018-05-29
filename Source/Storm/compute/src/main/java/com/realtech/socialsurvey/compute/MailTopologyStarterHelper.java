package com.realtech.socialsurvey.compute;

import org.apache.storm.Config;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.common.ComputeConstants;
import com.realtech.socialsurvey.compute.common.EnvConstants;
import com.realtech.socialsurvey.compute.topology.bolts.mailsender.RetryHandlerBolt;
import com.realtech.socialsurvey.compute.topology.bolts.mailsender.SaveMailToSolrBolt;
import com.realtech.socialsurvey.compute.topology.bolts.mailsender.SendMailBolt;
import com.realtech.socialsurvey.compute.topology.spouts.KafkaTopicSpoutBuilder;
import com.realtech.socialsurvey.compute.utils.ChararcterUtils;


public class MailTopologyStarterHelper extends TopologyStarterHelper
{

    private static final Logger LOG = LoggerFactory.getLogger( MailTopologyStarterHelper.class );

    public static final String MAIL_TOPOLOGY = "MailTopology";


    @Override public void displayBanner()
    {
        LOG.info( "███╗   ███╗ █████╗ ██╗██╗         ████████╗ ██████╗ ██████╗  ██████╗ ██╗      ██████╗  ██████╗██╗   ██╗" );
        LOG.info( "████╗ ████║██╔══██╗██║██║         ╚══██╔══╝██╔═══██╗██╔══██╗██╔═══██╗██║     ██╔═══██╗██╔════╝╚██╗ ██╔╝" );
        LOG.info( "██╔████╔██║███████║██║██║            ██║   ██║   ██║██████╔╝██║   ██║██║     ██║   ██║██║  ███╗╚████╔╝ " );
        LOG.info( "██║╚██╔╝██║██╔══██║██║██║            ██║   ██║   ██║██╔═══╝ ██║   ██║██║     ██║   ██║██║   ██║ ╚██╔╝  " );
        LOG.info( "██║ ╚═╝ ██║██║  ██║██║███████╗       ██║   ╚██████╔╝██║     ╚██████╔╝███████╗╚██████╔╝╚██████╔╝  ██║   " );
        LOG.info( "╚═╝     ╚═╝╚═╝  ╚═╝╚═╝╚══════╝       ╚═╝    ╚═════╝ ╚═╝      ╚═════╝ ╚══════╝ ╚═════╝  ╚═════╝   ╚═╝   " );
    }


    @Override public boolean validateTopologyEnvironment()
    {
        if ( !EnvConstants.sendGridMeApiKeys().isPresent() || !EnvConstants.sendGridUsApiKeys().isPresent() ) {
            LOG.warn( "Unsatisfied configuration either SENDGRID_API_KEY/ SENDGRID_US_API_KEY" );
            return false;
        } else {
            return true;
        }
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


    @Override public Config enhanceConfigWithCustomVariables( Config config )
    {
        config.put( ComputeConstants.SENDGRID_ME_API_KEY, EnvConstants.sendGridMeApiKeys().orElse( null ) );
        config.put( ComputeConstants.SENDGRID_US_API_KEY, EnvConstants.sendGridUsApiKeys().orElse( null ) );
        return config;
    }


    @Override protected StormTopology topology()
    {
        LOG.info( "Creating mail topology" );
        TopologyBuilder builder = new TopologyBuilder();
        // add mail kafka spout
        builder.setSpout( "MailSenderSpout", KafkaTopicSpoutBuilder.getInstance().emailTopicKafkaSpout(), 1 );
        // add bolts
        builder.setBolt( "SaveMailToSolrBolt", new SaveMailToSolrBolt(), 1 ).shuffleGrouping( "MailSenderSpout" );
        builder.setBolt( "SendMailBolt", new SendMailBolt(), 1 ).shuffleGrouping( "SaveMailToSolrBolt" );
        builder.setBolt("RetryHandlerBolt" , new RetryHandlerBolt(), 1).shuffleGrouping("SendMailBolt");

        return builder.createTopology();
    }


    public static void main( String[] args )
    {
        LOG.info( "Starting up mail topology..." );
        // Run time params should be the first step
        // DO NOT ADD ANY CODE BEFORE THIS LINE
        EnvConstants.runtimeParams( args );
        new MailTopologyStarterHelper().submitTopology( EnvConstants.getCluster().equals( EnvConstants.LOCAL_TOPOLOGY ),
            ( EnvConstants.getProfile().equals( EnvConstants.PROFILE_PROD ) ) ?
                MAIL_TOPOLOGY :
                ChararcterUtils.appendWithHypen( MAIL_TOPOLOGY, EnvConstants.getProfile() ) );
    }
}
