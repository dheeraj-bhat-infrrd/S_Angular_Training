package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.util.Date;


public class SavedDigestRecord implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String absoluteFileName;
    private Date uploadedDate;
    private String month;
    private String year;


    public String getAbsoluteFileName()
    {
        return absoluteFileName;
    }


    public void setAbsoluteFileName( String absoluteFileName )
    {
        this.absoluteFileName = absoluteFileName;
    }


    public Date getUploadedDate()
    {
        return uploadedDate;
    }


    public void setUploadedDate( Date uploadedDate )
    {
        this.uploadedDate = uploadedDate;
    }


    public String getMonth()
    {
        return month;
    }


    public void setMonth( String month )
    {
        this.month = month;
    }


    public String getYear()
    {
        return year;
    }


    public void setYear( String year )
    {
        this.year = year;
    }


    @Override
    public String toString()
    {
        return "SavedDigestRecord [absoluteFileName=" + absoluteFileName + ", uploadedDate=" + uploadedDate + ", month=" + month
            + ", year=" + year + "]";
    }


}
