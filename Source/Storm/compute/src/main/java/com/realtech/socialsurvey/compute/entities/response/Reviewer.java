package com.realtech.socialsurvey.compute.entities.response;

import java.io.Serializable;


/**
 * @author Lavanya
 */

public class Reviewer implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String name;

    public String getName() { return this.name; }

    public void setName(String name) { this.name = name; }

    private String id;

    public String getId() { return this.id; }

    public void setId(String id) { this.id = id; }
}
