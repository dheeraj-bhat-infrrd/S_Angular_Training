package com.realtech.socialsurvey.compute.topology.spouts;

import java.util.Map;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;

public class FacebookPostIngestSpout extends BaseComputeSpout
{

    private static final long serialVersionUID = 1L;

    private SpoutOutputCollector _collector;

    @Override
    public void open( @SuppressWarnings ( "rawtypes") Map conf, TopologyContext context, SpoutOutputCollector collector )
    {
        super.open( conf, context, collector );
        this._collector = collector;

    }


    @Override
    public void nextTuple()
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void declareOutputFields( OutputFieldsDeclarer declarer )
    {
        // TODO Auto-generated method stub

    }

}
