/**
 * 
 */
package com.realtech.socialsurvey.compute.entities.response;

import java.io.Serializable;


/**
 * @author manish
 *
 */
public class FacebookObjectFrom implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;


    public String getId()
    {
        return id;
    }


    public String getName()
    {
        return name;
    }


    public void setId( String id )
    {
        this.id = id;
    }


    public void setName( String name )
    {
        this.name = name;
    }


    @Override
    public String toString()
    {
        return "FacebookObjectFrom [id=" + id + ", name=" + name + "]";
    }


}
