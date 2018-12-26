package com.realtech.socialsurvey.compute.entities.response;

import java.io.Serializable;


public class Seller implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String id;

    public String getId() { return this.id; }

    public void setId(String id) { this.id = id; }

    private String title;

    public String getTitle() { return this.title; }

    public void setTitle(String title) { this.title = title; }

    private String type;

    public String getType() { return this.type; }

    public void setType(String type) { this.type = type; }

    private String url;

    public String getUrl() { return this.url; }

    public void setUrl(String url) { this.url = url; }

}
