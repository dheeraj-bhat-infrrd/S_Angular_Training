package com.realtech.socialsurvey.stream.entities;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Dummy feed data
 * @author nishit
 *
 */
@Document ( collection = "dummy_feeds")
public class DummyFeed implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    @Id
    private ObjectId id;
    private String feed;


    public ObjectId getId()
    {
        return id;
    }


    public void setId( ObjectId id )
    {
        this.id = id;
    }


    public String getFeed()
    {
        return feed;
    }


    public void setFeed( String feed )
    {
        this.feed = feed;
    }


    @Override
    public String toString()
    {
        return "Feed [id=" + id + ", feed=" + feed + "]";
    }


}
