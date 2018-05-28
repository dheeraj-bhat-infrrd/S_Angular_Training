package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;


public class GenericReportingObject implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String keyword;


    public String getKeyword()
    {
        return keyword;
    }


    public void setKeyword( String keyword )
    {
        this.keyword = keyword;
    }


    @Override
    public String toString()
    {
        return "GenericReportingObject [keyword=" + keyword + "]";
    }


}
