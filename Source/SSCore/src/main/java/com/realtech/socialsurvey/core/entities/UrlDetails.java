package com.realtech.socialsurvey.core.entities;

import java.util.Date;
import java.util.List;


public class UrlDetails
{
    private String _id;
    private String urlType;
    private String url;
    private Date createdOn;
    private String createdBy;
    private Date modifiedOn;
    private String modifiedBy;
    private List<Date> accessDates;
    private int status;


    public String get_id()
    {
        return _id;
    }


    public void set_id( String _id )
    {
        this._id = _id;
    }


    public String getUrlType()
    {
        return urlType;
    }


    public void setUrlType( String urlType )
    {
        this.urlType = urlType;
    }


    public String getUrl()
    {
        return url;
    }


    public void setUrl( String url )
    {
        this.url = url;
    }


    public Date getCreatedOn()
    {
        return createdOn;
    }


    public void setCreatedOn( Date createdOn )
    {
        this.createdOn = createdOn;
    }


    public String getCreatedBy()
    {
        return createdBy;
    }


    public void setCreatedBy( String createdBy )
    {
        this.createdBy = createdBy;
    }


    public Date getModifiedOn()
    {
        return modifiedOn;
    }


    public void setModifiedOn( Date modifiedOn )
    {
        this.modifiedOn = modifiedOn;
    }


    public String getModifiedBy()
    {
        return modifiedBy;
    }


    public void setModifiedBy( String modifiedBy )
    {
        this.modifiedBy = modifiedBy;
    }


    public List<Date> getAccessDates()
    {
        return accessDates;
    }


    public void setAccessDates( List<Date> accessDate )
    {
        this.accessDates = accessDate;
    }


    public int getStatus()
    {
        return status;
    }


    public void setStatus( int status )
    {
        this.status = status;
    }


    @Override
    public String toString()
    {
        return "Url Details : [ URL Type : " + urlType + ", URL : " + url + ", " + ", Status : " + status + "]";

    }
}
