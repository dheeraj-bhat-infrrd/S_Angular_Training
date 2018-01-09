package com.realtech.socialsurvey.compute.entities.request;

public class SolrAdd<T>
{
    private T doc;


    public T getDoc()
    {
        return doc;
    }


    public void setDoc( T doc )
    {
        this.doc = doc;
    }

}
