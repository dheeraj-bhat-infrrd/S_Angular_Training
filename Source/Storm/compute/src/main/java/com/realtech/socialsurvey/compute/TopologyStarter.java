package com.realtech.socialsurvey.compute;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.common.EnvConstants;
import com.realtech.socialsurvey.compute.utils.ChararcterUtils;


/**
 * Starter class for storm
 * @author nishit
 *
 */
public class TopologyStarter
{
    private static final Logger LOG = LoggerFactory.getLogger( TopologyStarter.class );


    // private constructor to avoid instantiation
    private TopologyStarter()
    {}

    public static void main( String[] args )
    {
        LOG.info( "Starting up topologies..." );
        // Run time params should be the first step
        // DO NOT ADD ANY CODE BEFORE THIS LINE
        EnvConstants.runtimeParams( args );

        // Mail topology
        new MailTopologyStarterHelper().submitTopology( EnvConstants.getCluster().equals( EnvConstants.LOCAL_TOPOLOGY ),
            ( EnvConstants.getProfile().equals( EnvConstants.PROFILE_PROD ) ) ? MailTopologyStarterHelper.MAIL_TOPOLOGY
                : ChararcterUtils.appendWithHypen( MailTopologyStarterHelper.MAIL_TOPOLOGY, EnvConstants.getProfile() ) );
        // Mail event topology
        new MailEventsTopologyStarterHelper().submitTopology( EnvConstants.getCluster().equals( EnvConstants.LOCAL_TOPOLOGY ),
            ( EnvConstants.getProfile().equals( EnvConstants.PROFILE_PROD ) ) ? MailEventsTopologyStarterHelper.MAIL_EVENT_TOPOLOGY
                : ChararcterUtils.appendWithHypen( MailEventsTopologyStarterHelper.MAIL_EVENT_TOPOLOGY,
                    EnvConstants.getProfile() ) );
        // Social post extractor topology
        new SocialPostExtractorTopologyStarterHelper().submitTopology( EnvConstants.getCluster().equals( EnvConstants.LOCAL_TOPOLOGY ),
            ( EnvConstants.getProfile().equals( EnvConstants.PROFILE_PROD ) ) ? SocialPostExtractorTopologyStarterHelper.TOPOLOGY_NAME
                : ChararcterUtils.appendWithHypen( SocialPostExtractorTopologyStarterHelper.TOPOLOGY_NAME,
                    EnvConstants.getProfile() ) );
        // Social post filter topology
        new SocialPostFilterTopologyStarterHelper().submitTopology( EnvConstants.getCluster().equals( EnvConstants.LOCAL_TOPOLOGY ),
            ( EnvConstants.getProfile().equals( EnvConstants.PROFILE_PROD ) ) ? SocialPostFilterTopologyStarterHelper.TOPOLOGY_NAME
                : ChararcterUtils.appendWithHypen( SocialPostFilterTopologyStarterHelper.TOPOLOGY_NAME,
                    EnvConstants.getProfile() ) );
        // Reports topology
        new ReportsTopologyStarterHelper().submitTopology( EnvConstants.getCluster().equals( EnvConstants.LOCAL_TOPOLOGY ),
                ( EnvConstants.getProfile().equals( EnvConstants.PROFILE_PROD ) ) ? ReportsTopologyStarterHelper.REPORTS_TOPOLOGY
                        : ChararcterUtils.appendWithHypen( ReportsTopologyStarterHelper.REPORTS_TOPOLOGY,
                        EnvConstants.getProfile() ) );
        // user event topology
        new UserEventTopologyStarterHelper().submitTopology( EnvConstants.getCluster().equals( EnvConstants.LOCAL_TOPOLOGY ),
                ( EnvConstants.getProfile().equals( EnvConstants.PROFILE_PROD ) ) ? UserEventTopologyStarterHelper.USER_EVENT_TOPOLOGY
                        : ChararcterUtils.appendWithHypen( UserEventTopologyStarterHelper.USER_EVENT_TOPOLOGY,
                        EnvConstants.getProfile() ) );
        //Transaction ingestion topology
        new TransactionIngestionTopologyStarterHelper().submitTopology( EnvConstants.getCluster().equals( EnvConstants.LOCAL_TOPOLOGY ),
            ( EnvConstants.getProfile().equals( EnvConstants.PROFILE_PROD ) ) ? TransactionIngestionTopologyStarterHelper.TRANSACTION_INGESTION_TOPOLOGY
                    : ChararcterUtils.appendWithHypen( TransactionIngestionTopologyStarterHelper.TRANSACTION_INGESTION_TOPOLOGY,
                    EnvConstants.getProfile() ) );
    }
}