package com.realtech.socialsurvey.compute.entities.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


/**
 * @author Lavanya
 */

public class OpenGraphStory implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String id;

    public String getId() { return this.id; }

    public void setId(String id) { this.id = id; }

    //todo : delete comments once developed
    /*private String message;

    public String getMessage() { return this.message; }

    public void setMessage(String message) { this.message = message; }

    @SerializedName( "start_time" )
    private long startTime;

    public long getStartTime() { return this.startTime; }

    public void setStartTime(long start_time) { this.startTime = start_time; }

    private String type;

    public String getType() { return this.type; }

    public void setType(String type) { this.type = type; }

    private OpenGraphData data;


    public OpenGraphData getData()
    {
        return data;
    }


    public void setData( OpenGraphData data )
    {
        this.data = data;
    }*/
}
