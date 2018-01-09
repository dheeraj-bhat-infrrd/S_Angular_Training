package com.realtech.socialsurvey.compute.dao.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.reflect.TypeToken;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.realtech.socialsurvey.compute.common.MongoDB;
import com.realtech.socialsurvey.compute.dao.CompanyKeywordsDao;
import com.realtech.socialsurvey.compute.entities.Keyword;
import com.realtech.socialsurvey.compute.utils.ConversionUtils;


public class CompanyKeywordsDaoImpl implements CompanyKeywordsDao
{

    private static final Logger LOG = LoggerFactory.getLogger( CompanyKeywordsDaoImpl.class );

    private MongoDB mongoDB;


    public CompanyKeywordsDaoImpl()
    {
        this.mongoDB = new MongoDB();
    }


    @Override
    public List<Keyword> getCompanyKeywordsForCompanyId( long companyIden )
    {
        LOG.info( "Inside getCompanyKeywordsForCompanyId method" );
        BasicDBObject query = new BasicDBObject( "iden", companyIden );
        DBObject object = mongoDB.datastoreForSSDb().getDB().getCollectionFromString( "COMPANY_SETTINGS" ).findOne( query );
        return ConversionUtils.deserialize( object.get( "filterKeywords" ).toString(),
            new TypeToken<List<Keyword>>() {}.getType() );
    }
}
