package com.realtech.socialsurvey.compute.entities.response;

import java.util.List;


public class SOLRResponse<T>
{

    private int numFound;
    private int start;
    private List<T> docs;


    public int getNumFound()
    {
        return numFound;
    }


    public void setNumFound( int numFound )
    {
        this.numFound = numFound;
    }


    public int getStart()
    {
        return start;
    }


    public void setStart( int start )
    {
        this.start = start;
    }


    public List<T> getDocs()
    {
        return docs;
    }


    public void setDocs( List<T> docs )
    {
        this.docs = docs;
    }


}
