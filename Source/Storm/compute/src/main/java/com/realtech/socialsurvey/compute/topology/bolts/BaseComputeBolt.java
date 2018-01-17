package com.realtech.socialsurvey.compute.topology.bolts;

import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.base.BaseRichBolt;

import com.realtech.socialsurvey.compute.common.ComputeConstants;
import com.realtech.socialsurvey.compute.common.LocalPropertyFileHandler;


public abstract class BaseComputeBolt extends BaseRichBolt
{

    private static final long serialVersionUID = -4456350173228747613L;

    protected OutputCollector _collector;


    @SuppressWarnings ( "unchecked")
    @Override
    public void prepare( @SuppressWarnings ( "rawtypes") Map stormConf, TopologyContext context, OutputCollector collector )
    {
        this._collector = collector;
        Map<String, String> runtimeParams = (Map<String, String>) stormConf.get( ComputeConstants.RUNTIME_PARAMS );
        LocalPropertyFileHandler.getInstance().prepare( runtimeParams.get( ComputeConstants.PROFILE ) );
    }
}
