
package com.realtech.socialsurvey.compute.entities.response.linkedin;

import java.io.Serializable;

/**
 * @author manish
 *
 */
public class LinkedinFeedShare implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String comment;
    private LinkedinFeedContent content;
    private String id;
    private LinkedinFeedSource source;
    private long timestamp;


    public String getComment()
    {
        return comment;
    }


    public void setComment( String comment )
    {
        this.comment = comment;
    }


    public LinkedinFeedContent getContent()
    {
        return content;
    }


    public void setContent( LinkedinFeedContent content )
    {
        this.content = content;
    }


    public String getId()
    {
        return id;
    }


    public void setId( String id )
    {
        this.id = id;
    }


    public LinkedinFeedSource getSource()
    {
        return source;
    }


    public void setSource( LinkedinFeedSource source )
    {
        this.source = source;
    }
    
    


    public long getTimestamp()
    {
        return timestamp;
    }


    public void setTimestamp( long timestamp )
    {
        this.timestamp = timestamp;
    }


    @Override
    public String toString()
    {
        return "LinkedinFeedShare [comment=" + comment + ", content=" + content + ", id=" + id + ", source=" + source
            + ", timestamp=" + timestamp + "]";
    }

}
