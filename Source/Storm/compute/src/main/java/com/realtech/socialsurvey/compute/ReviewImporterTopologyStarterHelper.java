/**
 * 
 */
package com.realtech.socialsurvey.compute;

import com.realtech.socialsurvey.compute.common.EnvConstants;
import com.realtech.socialsurvey.compute.topology.bolts.reviews.FacebookReviewExtractorBolt;
import com.realtech.socialsurvey.compute.topology.bolts.reviews.ProcessGoogleReviewsBolt;
import com.realtech.socialsurvey.compute.topology.bolts.reviews.SaveOrUpdateReviews;
import com.realtech.socialsurvey.compute.topology.spouts.FacebookTokenExtractorSpout;
import com.realtech.socialsurvey.compute.topology.spouts.GoogleAuthorizationSpout;
import com.realtech.socialsurvey.compute.utils.ChararcterUtils;
import org.apache.storm.Config;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Lavanya
 *
 * Topology for extracting reviews from various social media
 */
public class ReviewImporterTopologyStarterHelper extends TopologyStarterHelper
{
    private static final Logger LOG = LoggerFactory.getLogger( ReviewImporterTopologyStarterHelper.class );

    public static final String TOPOLOGY_NAME = "ReviewImporterTopology";
    public static final String FACEBOOK_TOKEN_EXTRACTOR_SPOUT = "FacebookTokenExtractorSpout";
    public static final String FACEBOOK_REVIEW_EXTRACTOR_BOLT = "FacebookReviewExtractorBolt";
    public static final String SAVE_OR_UPDATE_REVIEWS_BOLT = "SaveOrUpdateReviewsBolt";
    public static final String GOOGLE_AUTHORIZATION_SPOUT = "GoogleAuthorizationSpout";
    public static final String PROCESS_GOOGLE_REVIEW_BOLT = "ProcessGoogleReviewsBolt";

    @Override protected void displayBanner()
    {
        LOG.info( "            	   ██████╗ ███████╗██╗   ██╗██╗███████╗██╗    ██╗               ");
        LOG.info( "                ██╔══██╗██╔════╝██║   ██║██║██╔════╝██║    ██║               ");
        LOG.info( "                ██████╔╝█████╗  ██║   ██║██║█████╗  ██║ █╗ ██║               ");
        LOG.info( "                ██╔══██╗██╔══╝  ╚██╗ ██╔╝██║██╔══╝  ██║███╗██║               ");
        LOG.info( "                ██║  ██║███████╗ ╚████╔╝ ██║███████╗╚███╔███╔╝               ");
        LOG.info( "                ╚═╝  ╚═╝╚══════╝  ╚═══╝  ╚═╝╚══════╝ ╚══╝╚══╝                ");
        LOG.info( "                                                                             ");
        LOG.info( "        ██╗███╗   ███╗██████╗  ██████╗ ██████╗ ████████╗███████╗██████╗      ");
        LOG.info( "        ██║████╗ ████║██╔══██╗██╔═══██╗██╔══██╗╚══██╔══╝██╔════╝██╔══██╗     ");
        LOG.info( "        ██║██╔████╔██║██████╔╝██║   ██║██████╔╝   ██║   █████╗  ██████╔╝     ");
        LOG.info( "        ██║██║╚██╔╝██║██╔═══╝ ██║   ██║██╔══██╗   ██║   ██╔══╝  ██╔══██╗     ");
        LOG.info( "        ██║██║ ╚═╝ ██║██║     ╚██████╔╝██║  ██║   ██║   ███████╗██║  ██║     ");
        LOG.info( "        ╚═╝╚═╝     ╚═╝╚═╝      ╚═════╝ ╚═╝  ╚═╝   ╚═╝   ╚══════╝╚═╝  ╚═╝     ");
        LOG.info( "                                                                             ");
        LOG.info( "    ████████╗ ██████╗ ██████╗  ██████╗ ██╗      ██████╗  ██████╗██╗   ██╗    ");
        LOG.info( "    ╚══██╔══╝██╔═══██╗██╔══██╗██╔═══██╗██║     ██╔═══██╗██╔════╝╚██╗ ██╔╝    ");
        LOG.info( "       ██║   ██║   ██║██████╔╝██║   ██║██║     ██║   ██║██║  ███╗╚████╔╝     ");
        LOG.info( "       ██║   ██║   ██║██╔═══╝ ██║   ██║██║     ██║   ██║██║   ██║ ╚██╔╝      ");
        LOG.info( "       ██║   ╚██████╔╝██║     ╚██████╔╝███████╗╚██████╔╝╚██████╔╝  ██║       ");
        LOG.info( "       ╚═╝    ╚═════╝ ╚═╝      ╚═════╝ ╚══════╝ ╚═════╝  ╚═════╝   ╚═╝       ");
    }


    @Override
    public Config createConfig( boolean isLocalMode )
    {
        if ( isLocalMode ) {
            Config config = new Config();
            config.put( Config.TOPOLOGY_DEBUG, true );
            config.put( Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 60 );
            return config;
        } else {
            Config config = new Config();
            config.put( Config.TOPOLOGY_MAX_SPOUT_PENDING, 50 );
            config.put( Config.STORM_NIMBUS_RETRY_TIMES, 3 );
            config.put( Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 60 );
            return config;
        }
    }

    @Override
    protected StormTopology topology()
    {
        LOG.info( "Creating review importer topology" );
        TopologyBuilder builder = new TopologyBuilder();
        /*builder.setSpout( FACEBOOK_TOKEN_EXTRACTOR_SPOUT, new FacebookTokenExtractorSpout(),1 );
        builder.setBolt( FACEBOOK_REVIEW_EXTRACTOR_BOLT, new FacebookReviewExtractorBolt(), 1 )
            .shuffleGrouping( FACEBOOK_TOKEN_EXTRACTOR_SPOUT );*/

        // Spout for google
        builder.setSpout( GOOGLE_AUTHORIZATION_SPOUT, new GoogleAuthorizationSpout(),1 );
        builder.setBolt( PROCESS_GOOGLE_REVIEW_BOLT, new ProcessGoogleReviewsBolt(),1 )
        .shuffleGrouping( GOOGLE_AUTHORIZATION_SPOUT );
        builder.setBolt( SAVE_OR_UPDATE_REVIEWS_BOLT, new SaveOrUpdateReviews(), 1 )
            .shuffleGrouping( PROCESS_GOOGLE_REVIEW_BOLT );//.shuffleGrouping( FACEBOOK_REVIEW_EXTRACTOR_BOLT );

        return builder.createTopology();
    }


    public static void main( String[] args )
    {
            LOG.info( "Starting up review importer topology..." );
            // Run time params should be the first step
            // DO NOT ADD ANY CODE BEFORE THIS LINE
            EnvConstants.runtimeParams( args );

            // Review Importer topology
            new ReviewImporterTopologyStarterHelper().submitTopology( EnvConstants.getCluster().equals( EnvConstants.LOCAL_TOPOLOGY ),
                ( EnvConstants.getProfile().equals( EnvConstants.PROFILE_PROD ) )
                    ? ReviewImporterTopologyStarterHelper.TOPOLOGY_NAME
                    : ChararcterUtils.appendWithHypen( ReviewImporterTopologyStarterHelper.TOPOLOGY_NAME,
                    EnvConstants.getProfile() ) );
    }

}
