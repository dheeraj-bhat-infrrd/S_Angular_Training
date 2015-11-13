package com.realtech.socialsurvey.core.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.UrlDetailsDao;
import com.realtech.socialsurvey.core.entities.UrlDetails;


@Repository
public class MongoUrlDetailsDaoImpl implements UrlDetailsDao
{
    private static final Logger LOG = LoggerFactory.getLogger( MongoUrlDetailsDaoImpl.class );
    public static final String URL_DETAILS_COLLECTION = "URL_DETAILS";

    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public String insertUrlDetails( UrlDetails urlDetails )
    {
        LOG.info( "Method insertUrlDetails() to insert url details started." );
        mongoTemplate.insert( urlDetails, URL_DETAILS_COLLECTION );
        LOG.info( "Method insertUrlDetails() to insert url details finished." );
        return urlDetails.get_id();
    }


    @Override
    public UrlDetails findUrlDetailsById( String idStr )
    {
        LOG.info( "Started Method findUrlDetails() to find url details for id : " + idStr );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).is( idStr ) );
        UrlDetails urlDetails = mongoTemplate.findOne( query, UrlDetails.class, URL_DETAILS_COLLECTION );
        LOG.info( "Method findUrlDetails() to find url details finished." );
        return urlDetails;
    }


    @Override
    public UrlDetails findUrlDetailsByUrl( String url )
    {
        LOG.info( "Started Method findUrlDetails() to find url details for url : " + url );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.URL_COLUMN ).is( url ) );
        UrlDetails urlDetails = mongoTemplate.findOne( query, UrlDetails.class, URL_DETAILS_COLLECTION );
        LOG.info( "Method findUrlDetails() to find url details finished." );
        return urlDetails;
    }


    @Override
    public void updateUrlDetails( String idStr, UrlDetails urlDetails )
    {
        LOG.info( "Method updateUrlDetails() to update url details started." );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).is( idStr ) );

        Update update = new Update();
        update.set( CommonConstants.URL_DETAILS_STATUS_COLUMN, urlDetails.getStatus() );
        update.set( CommonConstants.URL_DETAILS_MODIFIED_ON_COLUMN, urlDetails.getModifiedOn() );
        update.set( CommonConstants.URL_DETAILS_ACCESS_DATES_COLUMN, urlDetails.getAccessDates() );

        mongoTemplate.updateFirst( query, update, UrlDetails.class, URL_DETAILS_COLLECTION );
        LOG.info( "Method updateUrlDetails() to update url details finished." );
    }


}
