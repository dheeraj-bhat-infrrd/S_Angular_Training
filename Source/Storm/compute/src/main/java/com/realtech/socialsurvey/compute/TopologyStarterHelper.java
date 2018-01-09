package com.realtech.socialsurvey.compute;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.generated.StormTopology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.common.ComputeConstants;
import com.realtech.socialsurvey.compute.common.EnvConstants;
import com.realtech.socialsurvey.compute.exception.UnstatisfiedConfigurationException;


/**
 * Starter helper
 * @author nishit
 *
 */
public abstract class TopologyStarterHelper
{
 
    private static final Logger LOG = LoggerFactory.getLogger( TopologyStarterHelper.class );
    
    /**
     * Displays banner
     */
    protected void displayBanner()
    {}


    /**
     * Validates the topology environemnt
     */
    protected boolean validateTopologyEnvironment()
    {
        return true;
    }


    /**
     * Submits the topology
     * @param isLocalMode
     * @param config
     * @param topologyName
     */
    public final void submitTopology( boolean isLocalMode, String topologyName )
    {
        displayBanner();
        if ( validateTopologyEnvironment() ) {
            LOG.info( "Submitting topology {} with mode local mode as {}", topologyName, isLocalMode );
            Config config = createConfig(isLocalMode);
            config = enhanceConfigWithRuntimeParams( config );
            config = enhanceConfigWithCustomVariables( config );
            if ( isLocalMode ) {
                LocalCluster cluster = new LocalCluster();
                cluster.submitTopology( topologyName, config, topology() );
            }else {
                try {
                    StormSubmitter.submitTopology( topologyName, config, topology() );
                } catch ( AlreadyAliveException | InvalidTopologyException | AuthorizationException e ) {
                    LOG.error( "Exception while submitting the topology: {}", topologyName, e );
                }
            }
        } else {
            throw new UnstatisfiedConfigurationException( "Check if env variables are set" );
        }

    }


    private Config enhanceConfigWithRuntimeParams( Config config )
    {
        config.put( ComputeConstants.RUNTIME_PARAMS, EnvConstants.getRuntimeParamsMap() );
        return config;
    }


    protected Config enhanceConfigWithCustomVariables( Config config )
    {
        return config;
    }
    
    public abstract Config createConfig(boolean isLocalMode);


    protected abstract StormTopology topology();

}
