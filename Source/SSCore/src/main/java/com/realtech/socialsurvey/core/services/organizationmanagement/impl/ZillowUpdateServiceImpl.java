package com.realtech.socialsurvey.core.services.organizationmanagement.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.organizationmanagement.ZillowUpdateService;


@Component
public class ZillowUpdateServiceImpl implements ZillowUpdateService
{
    private static final Logger LOG = LoggerFactory.getLogger( ZillowUpdateServiceImpl.class );

    @Autowired
    private OrganizationUnitSettingsDao organizationUnitSettingsDao;


    @Async
    @Override
    //    @Transactional
    public void updateZillowReviewCountAndAverage( String collectionName, long iden, double zillowReviewCount,
        double zillowAverage )
    {
        if ( collectionName == null || collectionName.isEmpty() ) {
            LOG.error( "Collection name passed cannot be null or empty" );
        }
        if ( iden <= 0l ) {
            LOG.error( "Invalid iden passed as argument" );
        }
        LOG.info( "Updating the zillow review count and average in collection : " + collectionName );
        try {
            organizationUnitSettingsDao.updateZillowReviewScoreAndAverage( collectionName, iden, zillowReviewCount,
                zillowAverage );
        } catch ( InvalidInputException e ) {
            LOG.error( "Exception occurred while updating zillow review count and average. Reason : " + e );
        }
        LOG.info( "Updated the zillow review count and average in collection : " + collectionName );
    }
}
