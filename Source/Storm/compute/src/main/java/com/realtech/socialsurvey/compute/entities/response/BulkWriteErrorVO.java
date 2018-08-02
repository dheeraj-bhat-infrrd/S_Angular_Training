package com.realtech.socialsurvey.compute.entities.response;

import java.io.Serializable;
import java.util.Objects;


public class BulkWriteErrorVO implements Serializable
{
    private static final long serialVersionUID = 1L;

    private  int index;
    private  int code;
    private  String message;


    public BulkWriteErrorVO()
    {
    }


    public BulkWriteErrorVO( int index, int code, String message )
    {
        this.index = index;
        this.code = code;
        this.message = message;
    }


    public int getIndex()
    {
        return index;
    }


    public void setIndex( int index )
    {
        this.index = index;
    }


    public int getCode()
    {
        return code;
    }


    public void setCode( int code )
    {
        this.code = code;
    }


    public String getMessage()
    {
        return message;
    }


    public void setMessage( String message )
    {
        this.message = message;
    }


    @Override public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;
        BulkWriteErrorVO that = (BulkWriteErrorVO) o;
        return index == that.index && code == that.code && Objects.equals( message, that.message );
    }


    @Override public int hashCode()
    {
        return Objects.hash( index, code, message );
    }


    @Override public String toString()
    {
        return "BulkWriteErrorVO{" + "index=" + index + ", code=" + code + ", message='" + message + '\'' + '}';
    }
}
