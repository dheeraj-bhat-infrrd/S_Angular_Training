package com.realtech.socialsurvey.compute.entities.response;

public class InstagramFeedData {

    private static final long serialVersionUID = 1L;

    private InstagramMedia media;

    private String id;

    public InstagramMedia getMedia() { return this.media; }

    public void setMedia(InstagramMedia media) { this.media = media; }

    public String getId() { return this.id; }

    public void setId(String id) { this.id = id; }
}
