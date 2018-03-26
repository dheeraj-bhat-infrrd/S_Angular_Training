package com.realtech.socialsurvey.compute.entities.response;

import java.io.Serializable;

public class InstagramMediaData implements Serializable{

    private static final long serialVersionUID = 1L;

    private String ig_id;

    private long timestamp;

    private String media_url;

    private String media_type;

    private String caption;

    private String id;

    public String getIgId() { return this.ig_id; }

    public void setIgId(String ig_id) { this.ig_id = ig_id; }

    public long getTimestamp() { return this.timestamp; }

    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getMediaUrl() { return this.media_url; }

    public void setMediaUrl(String media_url) { this.media_url = media_url; }

    public String getMediaType() { return this.media_type; }

    public void setMediaType(String media_type) { this.media_type = media_type; }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getId() { return this.id; }

    public void setId(String id) { this.id = id; }

    @Override
    public String toString() {
        return "InstagramMediaData{" +
                "ig_id='" + ig_id + '\'' +
                ", timestamp=" + timestamp +
                ", media_url='" + media_url + '\'' +
                ", media_type='" + media_type + '\'' +
                ", caption='" + caption + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
