package com.realtech.socialsurvey.core.dao.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.ZillowTempPostDao;
import com.realtech.socialsurvey.core.entities.ZillowTempPost;


@Component ( "zillowTempPost")
public class ZillowTempPostDaoImpl extends GenericDaoImpl<ZillowTempPost, Integer> implements ZillowTempPostDao
{

    private static final Logger LOG = LoggerFactory.getLogger( ZillowTempPostDaoImpl.class );


    @Override
    @Transactional
    public ZillowTempPost saveOrUpdateZillowTempPost( ZillowTempPost zillowTempPost )
    {
        String entityName = zillowTempPost.getEntityColumnName();
        long entityId = zillowTempPost.getEntityId();
        // check whether zillow Temp Post already exist
        Map<String, Object> queries = new HashMap<String, Object>();
        queries.put( CommonConstants.ZILLOW_REVIEW_URL_COLUMN, zillowTempPost.getZillowReviewUrl() );
        queries.put( CommonConstants.ENTITY_COLUMN_NAME_COLUMN, entityName );
        queries.put( CommonConstants.ZILLOW_ENTITY_ID_COLUMN, entityId );

        List<ZillowTempPost> zillowTempPostList = findByKeyValue( ZillowTempPost.class, queries );
        String surveyId = zillowTempPost.getZillowSurveyId();
        if ( zillowTempPost != null && zillowTempPostList.size() > 0 ) {
            zillowTempPost = zillowTempPostList.get( 0 );
            zillowTempPost.setZillowSurveyId( surveyId );
            zillowTempPost.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
            zillowTempPost.setModifiedBy( String.valueOf( entityId ) );

            update( zillowTempPost );
        } else {
            zillowTempPost.setCreatedOn( new Timestamp( System.currentTimeMillis() ) );
            zillowTempPost.setCreatedBy( String.valueOf( entityId ) );
            zillowTempPost.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
            zillowTempPost.setModifiedBy( String.valueOf( entityId ) );

            save( zillowTempPost );
        }

        return zillowTempPost;
    }


    @Override
    @Transactional
    public void removeProcessedZillowTempPosts( List<Long> processedZillowTempPostIds )
    {
        LOG.info( "Method to remove processed zillow temp posts, removeProcessedZillowTempPosts called" );
        Query query = getSession().createQuery( "DELETE FROM ZillowTempPost WHERE ID IN :ids" );
        query.setParameterList( "ids", processedZillowTempPostIds );

        int count = query.executeUpdate();

        if ( count == processedZillowTempPostIds.size() ) {
            LOG.info( "Deleted all the temp zillow posts successfully" );
        }
        LOG.info( "Method to remove processed zillow temp posts, removeProcessedZillowTempPosts ended" );
    }
}
