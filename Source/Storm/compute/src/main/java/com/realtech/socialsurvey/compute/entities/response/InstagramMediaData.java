package com.realtech.socialsurvey.compute.entities.response;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class InstagramMediaData implements Serializable{

    private static final long serialVersionUID = 1L;

    @SerializedName ( "ig_id")
    private String igId;

    private long timestamp;

    @SerializedName ( "media_url")
    private String mediaUrl;

    @SerializedName ( "media_type")
    private String mediaType;

    private String caption;

    private String id;

    private String username;
    
    @SerializedName ( "permalink")
    private String postLink;

    public String getIgId() { return this.igId; }

    public void setIgId(String igId) { this.igId = igId; }

    public long getTimestamp() { return this.timestamp; }

    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getMediaUrl() { return this.mediaUrl; }

    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }

    public String getMediaType() { return this.mediaType; }

    public void setMediaType(String mediaType) { this.mediaType = mediaType; }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getId() { return this.id; }

    public void setId(String id) { this.id = id; }


    public String getUsername()
    {
        return username;
    }


    public void setUsername( String username )
    {
        this.username = username;
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
        return "InstagramMediaData [igId=" + igId + ", timestamp=" + timestamp + ", mediaUrl=" + mediaUrl + ", mediaType="
            + mediaType + ", caption=" + caption + ", id=" + id + ", username=" + username + ", postLink=" + postLink + "]";
    }
}
