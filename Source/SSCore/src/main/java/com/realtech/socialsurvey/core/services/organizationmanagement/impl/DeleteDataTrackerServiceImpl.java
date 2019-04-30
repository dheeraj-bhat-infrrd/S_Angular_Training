package com.realtech.socialsurvey.core.services.organizationmanagement.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.entities.DeleteDataTracker;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.services.organizationmanagement.DeleteDataTrackerService;


/**
 * @author manish
 *
 */
@Component
public class DeleteDataTrackerServiceImpl implements DeleteDataTrackerService, InitializingBean
{
    private static final Logger LOG = LoggerFactory.getLogger( DeleteDataTrackerServiceImpl.class );

    @Autowired
    private GenericDao<DeleteDataTracker, Long> deleteDataTrackerDao;


    @Override
    public void afterPropertiesSet() throws Exception
    {
        LOG.info( "afterPropertiesSet called for profile management service" );
    }


    @Override
    public void writeToDeleteTrackerForSurveyDetails( List<SurveyDetails> documentsToBeDeleted )
    {
        LOG.info( "Method writeToDeleteTrackerForSurveyDetails start" );
        List<DeleteDataTracker> trackerList = new ArrayList<>();
        for ( SurveyDetails surveyDetails : documentsToBeDeleted ) {
            DeleteDataTracker tracker = new DeleteDataTracker();
            tracker.setEntityId( surveyDetails.get_id() );
            tracker.setEntityType( CommonConstants.SURVEY_DETAILS_TABLE );
            tracker.setIsDeleted( 0 );
            tracker.setCreatedOn( new Timestamp( System.currentTimeMillis() ) );
            tracker.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
            trackerList.add( tracker );
        }
        deleteDataTrackerDao.saveAll( trackerList );
        LOG.info( "Method writeToDeleteTrackerForSurveyDetails finished" );
    }


    @Override
    public void writeToDeleteTrackerForUserProfile( long userProfileId )
    {
        LOG.info( "Method writeToDeleteTrackerForUserProfile start" );
        DeleteDataTracker tracker = new DeleteDataTracker();
        tracker.setEntityId( Long.toString( userProfileId ) );
        tracker.setEntityType( CommonConstants.USER_PROFILE_TABLE );
        tracker.setIsDeleted( 0 );
        tracker.setCreatedOn( new Timestamp( System.currentTimeMillis() ) );
        tracker.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        deleteDataTrackerDao.save( tracker );
        LOG.info( "Method writeToDeleteTrackerForUserProfile finished" );
    }


}
