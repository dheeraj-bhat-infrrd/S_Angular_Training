package com.realtech.socialsurvey.compute.topology.spouts;

import java.util.Map;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.base.BaseRichSpout;

import com.realtech.socialsurvey.compute.common.ComputeConstants;
import com.realtech.socialsurvey.compute.common.LocalPropertyFileHandler;

public abstract class BaseComputeSpout extends BaseRichSpout
{

    private static final long serialVersionUID = 1L;
    private Map<String, String> runtimeParams;


    @SuppressWarnings ( "unchecked")
    @Override
    public void open( @SuppressWarnings ( "rawtypes") Map conf, TopologyContext context, SpoutOutputCollector collector )
    {
        runtimeParams = (Map<String, String>)conf.get( ComputeConstants.RUNTIME_PARAMS );
        LocalPropertyFileHandler.getInstance().prepare( runtimeParams.get( ComputeConstants.PROFILE ) );

    }


}
