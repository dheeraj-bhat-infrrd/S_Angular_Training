package com.realtech.socialsurvey.stream.entities;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.io.Serializable;


/**
 * Created by nishit on 04/01/18.
 */
public class BaseMongoEntity implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id private ObjectId id;
    private long version;
    private String className;


    public ObjectId getId()
    {
        return id;
    }


    public void setId( ObjectId id )
    {
        this.id = id;
    }


    public long getVersion()
    {
        return version;
    }


    public void setVersion( long version )
    {
        this.version = version;
    }


    public String getClassName()
    {
        return className;
    }


    public void setClassName( String className )
    {
        this.className = className;
    }


    @Override public String toString()
    {
        return "BaseMongoEntity{" +
            "id=" + id +
            ", version=" + version +
            ", className='" + className + '\'' +
            '}';
    }
}
