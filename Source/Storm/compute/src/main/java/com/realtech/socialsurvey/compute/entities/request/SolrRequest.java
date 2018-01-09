package com.realtech.socialsurvey.compute.entities.request;

public class SolrRequest<T>
{
    private SolrAdd<T> add;


    public SolrAdd<T> getAdd()
    {
        return add;
    }


    public void setAdd( SolrAdd<T> add )
    {
        this.add = add;
    }


}
