package com.realtech.socialsurvey.compute;

import com.realtech.socialsurvey.compute.common.ComputeConstants;
import com.realtech.socialsurvey.compute.common.EnvConstants;
import com.realtech.socialsurvey.compute.topology.bolts.emailreports.*;
import com.realtech.socialsurvey.compute.topology.spouts.KafkaTopicSpoutBuilder;
import com.realtech.socialsurvey.compute.utils.ChararcterUtils;
import org.apache.storm.Config;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class MailReportsTopologyStarterHelper extends TopologyStarterHelper
{

    private static final Logger LOG = LoggerFactory.getLogger( MailReportsTopologyStarterHelper.class );
    private static final String MAIL_REPORTS_TOPOLOGY = "MailReportsTopology";


    @Override protected void displayBanner()
    {
        LOG.info( "                      ███╗   ███╗ █████╗ ██╗██╗                                " );
        LOG.info( "                      ████╗ ████║██╔══██╗██║██║                                " );
        LOG.info( "                      ██╔████╔██║███████║██║██║                                " );
        LOG.info( "                      ██║╚██╔╝██║██╔══██║██║██║                                " );
        LOG.info( "                      ██║ ╚═╝ ██║██║  ██║██║███████╗                           " );
        LOG.info( "                      ╚═╝     ╚═╝╚═╝  ╚═╝╚═╝╚══════╝                           " );
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
            return config;
        }
    }


    @Override protected StormTopology topology()
    {
        LOG.info( "Creating mail reports topology" );
        TopologyBuilder builder = new TopologyBuilder();
        //add the spout
        builder.setSpout("EmailReportGenerationSpout", KafkaTopicSpoutBuilder.emailReportGenerationSpout(), 1);
        //add the bolts
        builder.setBolt("UpdateFileUploadStatusBolt", new UpdateFileUploadStatusBolt(), 1)
                .shuffleGrouping("EmailReportGenerationSpout");
        builder.setBolt("QuerySolrToFetchSurveyRelatedMailsBolt", new QuerySolrToFetchSurveyRelatedMailBolt(), 1)
                .shuffleGrouping("EmailReportGenerationSpout");
        builder.setBolt("WriteEmailReportToExcelBolt", new WriteEmailReportToExcelBolt(), 1)
                .fieldsGrouping("QuerySolrToFetchSurveyRelatedMailsBolt", new Fields("fileUploadId"));
        builder.setBolt("UploadOnAmazonS3Bolt", new UploadOnAmazonS3Bolt(), 1).shuffleGrouping("WriteEmailReportToExcelBolt");
        builder.setBolt("FileUploadStatusAndFileNameUpdationBolt", new UpdateFileUploadStatusAndFileNameBolt(), 1)
                .shuffleGrouping("UploadOnAmazonS3Bolt");

        return builder.createTopology();
    }


    public static void main( String[] args )
    {
        LOG.info( "Starting up mail reports topology..." );
        // Run time params should be the first step
        // DO NOT ADD ANY CODE BEFORE THIS LINE
        EnvConstants.runtimeParams( args );
        new MailReportsTopologyStarterHelper().submitTopology( EnvConstants.getCluster().equals( EnvConstants.LOCAL_TOPOLOGY ),
                ( EnvConstants.getProfile().equals( EnvConstants.PROFILE_PROD ) ) ?
                        MAIL_REPORTS_TOPOLOGY :
                        ChararcterUtils.appendWithHypen( MAIL_REPORTS_TOPOLOGY, EnvConstants.getProfile() ) );
    }
}
