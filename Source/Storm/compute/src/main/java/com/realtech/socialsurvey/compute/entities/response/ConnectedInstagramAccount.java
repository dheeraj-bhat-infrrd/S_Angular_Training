package com.realtech.socialsurvey.compute.entities.response;

public class ConnectedInstagramAccount {

    private static final long serialVersionUID = 1L;

    private InstagramMedia media;

    public InstagramMedia getMedia() { return this.media; }

    public void setMedia(InstagramMedia media) { this.media = media; }

    private String id;

    public String getId() { return this.id; }

    public void setId(String id) { this.id = id; }
}
