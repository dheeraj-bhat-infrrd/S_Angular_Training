package com.realtech.socialsurvey.compute.entities.response;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;


public class FacebookFeedData implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String id;
    private String message;
    private String story;
    private FacebookFeedApplication application;
    
    @SerializedName ( "created_time")
    private long createdTime;

    @SerializedName ( "updated_time")
    private long updatedTime;

    @SerializedName ( "full_picture")
    private String fullPicture;

    @SerializedName ( "permalink_url")
    private String postLink;

    private String picture;

    private FacebookFeedFrom from;


    public String getFullPicture()
    {
        return fullPicture;
    }


    public String getPicture()
    {
        return picture;
    }


    public FacebookFeedFrom getFrom()
    {
        return from;
    }


    public void setFullPicture( String fullPicture )
    {
        this.fullPicture = fullPicture;
    }


    public void setPicture( String picture )
    {
        this.picture = picture;
    }


    public void setFrom( FacebookFeedFrom from )
    {
        this.from = from;
    }


    private String link;


    public String getLink()
    {
        return link;
    }


    public void setLink( String link )
    {
        this.link = link;
    }


    public String getId()
    {
        return id;
    }


    public String getMessage()
    {
        return message;
    }


    public void setId( String id )
    {
        this.id = id;
    }


    public void setMessage( String message )
    {
        this.message = message;
    }


    public String getStory()
    {
        return story;
    }


    public void setStory( String story )
    {
        this.story = story;
    }


    public long getCreatedTime()
    {
        return createdTime;
    }


    public void setCreatedTime( long createdTime )
    {
        this.createdTime = createdTime;
    }


    public long getUpdatedTime()
    {
        return updatedTime;
    }


    public void setUpdatedTime( long updatedTime )
    {
        this.updatedTime = updatedTime;
    }

	public FacebookFeedApplication getApplication() {
		return application;
	}


	public void setApplication(FacebookFeedApplication application) {
		this.application = application;
	}

	public String getPostLink()
    {
        return postLink;
    }


    public void setPostLink( String postLink )
    {
        this.postLink = postLink;
    }

    @Override
    public String toString()
    {
        return "FacebookFeedData [id=" + id + ", message=" + message + ", story=" + story + ", createdTime=" + createdTime
            + ", updatedTime=" + updatedTime + ", fullPicture=" + fullPicture + ", postLink=" + postLink + ", picture="
            + picture + ", from=" + from + ", link=" + link + "]";
    }

}
