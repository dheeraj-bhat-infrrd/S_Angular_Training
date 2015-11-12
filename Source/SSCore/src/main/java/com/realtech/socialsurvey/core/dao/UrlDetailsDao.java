package com.realtech.socialsurvey.core.dao;

import com.realtech.socialsurvey.core.entities.UrlDetails;


public interface UrlDetailsDao
{

    public String insertUrlDetails( UrlDetails urlDetails );


    public UrlDetails findUrlDetailsById( String idStr );


    public UrlDetails findUrlDetailsByUrl( String url );


    public void updateUrlDetails( String idStr, UrlDetails urlDetails );

}
