package com.realtech.socialsurvey.compute;

import com.realtech.socialsurvey.compute.common.ComputeConstants;
import com.realtech.socialsurvey.compute.common.EnvConstants;
import com.realtech.socialsurvey.compute.topology.bolts.KafkaProducerBolt;
import com.realtech.socialsurvey.compute.topology.bolts.monitor.FacebookFeedExtractorBolt;
import com.realtech.socialsurvey.compute.topology.bolts.monitor.InstagramFeedExactorBolt;
import com.realtech.socialsurvey.compute.topology.bolts.monitor.TwitterFeedExtractorBolt;
import com.realtech.socialsurvey.compute.topology.spouts.SocialMediaTokenExtractorSpout;
import com.realtech.socialsurvey.compute.utils.ChararcterUtils;
import org.apache.storm.Config;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Social post extractor topology starter
 * @author manish
 *
 */
public class SocialPostExtractorTopologyStarterHelper extends TopologyStarterHelper
{
    private static final Logger LOG = LoggerFactory.getLogger( SocialPostExtractorTopologyStarterHelper.class );
    public static final String TOPOLOGY_NAME = "SocialPostExtractorTopology";
    private static final String SOCIAL_MEDIA_TOKEN_EXTRACTOR_SPOUT = "SocialMediaTokenExtractorSpout";
    private static final String KAFKA_PRODUCER_BOLT = "KafkaProducerBolt";
    private static final String FACEBOOK_FEED_EXTRACTOR_BOLT = "FacebookFeedExtractorBolt";
    private static final String INSTAGRAM_FEED_EXTRACTOR_BOLT = "InstagramFeedExtractorBolt";
    private static final String LINKEDIN_FEED_EXTRACTOR_BOLT = "LinkedinFeedExtractorBolt";
    private static final String TWITTER_FEED_EXTRACTOR_BOLT = "TwitterFeedExtractorBolt";


    @Override
    public void displayBanner()
    {
        LOG.info( "███████╗ ██████╗  ██████╗██╗ █████╗ ██╗         ██████╗  ██████╗ ███████╗████████╗    " );
        LOG.info( "██╔════╝██╔═══██╗██╔════╝██║██╔══██╗██║         ██╔══██╗██╔═══██╗██╔════╝╚══██╔══╝    " );
        LOG.info( "███████╗██║   ██║██║     ██║███████║██║         ██████╔╝██║   ██║███████╗   ██║       " );
        LOG.info( "╚════██║██║   ██║██║     ██║██╔══██║██║         ██╔═══╝ ██║   ██║╚════██║   ██║       " );
        LOG.info( "███████║╚██████╔╝╚██████╗██║██║  ██║███████╗    ██║     ╚██████╔╝███████║   ██║       " );
        LOG.info( "╚══════╝ ╚═════╝  ╚═════╝╚═╝╚═╝  ╚═╝╚══════╝    ╚═╝      ╚═════╝ ╚══════╝   ╚═╝       " );
        LOG.info( "                                                                                      " );
        LOG.info( "  ███████╗██╗  ██╗████████╗██████╗  █████╗  ██████╗████████╗ ██████╗ ██████╗          " );
        LOG.info( "  ██╔════╝╚██╗██╔╝╚══██╔══╝██╔══██╗██╔══██╗██╔════╝╚══██╔══╝██╔═══██╗██╔══██╗         " );
        LOG.info( "  █████╗   ╚███╔╝    ██║   ██████╔╝███████║██║        ██║   ██║   ██║██████╔╝         " );
        LOG.info( "  ██╔══╝   ██╔██╗    ██║   ██╔══██╗██╔══██║██║        ██║   ██║   ██║██╔══██╗         " );
        LOG.info( "  ███████╗██╔╝ ██╗   ██║   ██║  ██║██║  ██║╚██████╗   ██║   ╚██████╔╝██║  ██║         " );
        LOG.info( "  ╚══════╝╚═╝  ╚═╝   ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝   ╚═╝    ╚═════╝ ╚═╝  ╚═╝         " );
        LOG.info( "                                                                                      " );
        LOG.info( "    ████████╗ ██████╗ ██████╗  ██████╗ ██╗      ██████╗  ██████╗██╗   ██╗             " );
        LOG.info( "    ╚══██╔══╝██╔═══██╗██╔══██╗██╔═══██╗██║     ██╔═══██╗██╔════╝╚██╗ ██╔╝             " );
        LOG.info( "       ██║   ██║   ██║██████╔╝██║   ██║██║     ██║   ██║██║  ███╗╚████╔╝              " );
        LOG.info( "       ██║   ██║   ██║██╔═══╝ ██║   ██║██║     ██║   ██║██║   ██║ ╚██╔╝               " );
        LOG.info( "       ██║   ╚██████╔╝██║     ╚██████╔╝███████╗╚██████╔╝╚██████╔╝  ██║                " );
        LOG.info( "       ╚═╝    ╚═════╝ ╚═╝      ╚═════╝ ╚══════╝ ╚═════╝  ╚═════╝   ╚═╝                " );
    }

    @Override
    public boolean validateTopologyEnvironment()
    {
        if ( !EnvConstants.twitterConsumerKey().isPresent() || !EnvConstants.twitterConsumerSecret().isPresent() ) {
            LOG.warn( "Unsatisfied configuration either TWITTER_CONSUMER_KEY/SECRET is not present" );
            return false;
        } else {
            return true;
        }
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
            config.put( Config.TOPOLOGY_MAX_SPOUT_PENDING, 50 );
            config.put( Config.STORM_NIMBUS_RETRY_TIMES, 3 );
            return config;
        }
    }

    @Override
    public Config enhanceConfigWithCustomVariables( Config config ) {
        config.put( ComputeConstants.TWITTER_CONSUMER_KEY, EnvConstants.twitterConsumerKey().orElse( null ) );
        config.put( ComputeConstants.TWITTER_CONSUMER_SECRET, EnvConstants.twitterConsumerSecret().orElse( null ) );
        return config;
    }


    @Override
    protected StormTopology topology()
    {
        LOG.info( "Creating Social Post Extractor topology" );
        TopologyBuilder builder = new TopologyBuilder();
        // add feed extractor spout
        builder.setSpout( SOCIAL_MEDIA_TOKEN_EXTRACTOR_SPOUT, new SocialMediaTokenExtractorSpout(), 1 );

        // add bolts
        builder.setBolt( TWITTER_FEED_EXTRACTOR_BOLT, new TwitterFeedExtractorBolt(), 1 )
            .shuffleGrouping( SOCIAL_MEDIA_TOKEN_EXTRACTOR_SPOUT, "TwitterStream" );
        /*builder.setBolt( LINKEDIN_FEED_EXTRACTOR_BOLT, new LinkedinFeedExtractorBolt(), 1 )
            .shuffleGrouping( SOCIAL_MEDIA_TOKEN_EXTRACTOR_SPOUT, "LinkedinStream" );*/
        builder.setBolt( FACEBOOK_FEED_EXTRACTOR_BOLT, new FacebookFeedExtractorBolt(), 1 )
            .shuffleGrouping( SOCIAL_MEDIA_TOKEN_EXTRACTOR_SPOUT, "FacebookStream" );
        builder.setBolt( INSTAGRAM_FEED_EXTRACTOR_BOLT, new InstagramFeedExactorBolt(), 1 )
                .shuffleGrouping( SOCIAL_MEDIA_TOKEN_EXTRACTOR_SPOUT, "InstagramStream" );
        builder.setBolt( KAFKA_PRODUCER_BOLT, new KafkaProducerBolt(), 3 ).shuffleGrouping( TWITTER_FEED_EXTRACTOR_BOLT )
            .shuffleGrouping( FACEBOOK_FEED_EXTRACTOR_BOLT ).shuffleGrouping(INSTAGRAM_FEED_EXTRACTOR_BOLT);

        return builder.createTopology();
    }

    public static void main( String[] args )
    {
        LOG.info( "Starting up Social Post Extractor topology..." );
        // Run time params should be the first step
        // DO NOT ADD ANY CODE BEFORE THIS LINE
        EnvConstants.runtimeParams( args );

        // Social post topology
        new SocialPostExtractorTopologyStarterHelper().submitTopology(
            EnvConstants.getCluster().equals( EnvConstants.LOCAL_TOPOLOGY ),
            ( EnvConstants.getProfile().equals( EnvConstants.PROFILE_PROD ) )
                ? SocialPostExtractorTopologyStarterHelper.TOPOLOGY_NAME
                : ChararcterUtils.appendWithHypen( SocialPostExtractorTopologyStarterHelper.TOPOLOGY_NAME,
                    EnvConstants.getProfile() ) );
    }
}
