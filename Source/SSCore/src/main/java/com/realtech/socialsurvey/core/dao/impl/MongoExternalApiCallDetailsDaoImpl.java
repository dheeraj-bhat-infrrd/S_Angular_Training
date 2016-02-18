package com.realtech.socialsurvey.core.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.ExternalApiCallDetailsDao;
import com.realtech.socialsurvey.core.entities.ExternalAPICallDetails;
import com.realtech.socialsurvey.core.exception.InvalidInputException;


@Repository
public class MongoExternalApiCallDetailsDaoImpl implements ExternalApiCallDetailsDao
{
    private static final Logger LOG = LoggerFactory.getLogger( MongoExternalApiCallDetailsDaoImpl.class );

    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public void insertApiCallDetails( ExternalAPICallDetails callDetails ) throws InvalidInputException
    {
        LOG.info( "Method insertApiCallDetails started" );
        if ( callDetails == null ) {
            LOG.error( "The call details cannot be null" );
            throw new InvalidInputException( "The call details cannot be null" );
        }

        mongoTemplate.save( callDetails, CommonConstants.EXTERNAL_API_CALL_DETAILS_COLLECTION );
        LOG.info( "Method insertApiCallDetails finished" );
    }
}
