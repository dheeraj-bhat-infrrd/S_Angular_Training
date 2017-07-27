package com.realtech.socialsurvey.core.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.SurveyStatsReportRegionDao;
import com.realtech.socialsurvey.core.entities.SurveyStatsReportCompany;
import com.realtech.socialsurvey.core.entities.SurveyStatsReportRegion;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class SurveyStatsReportRegionDaoImpl extends GenericReportingDaoImpl<SurveyStatsReportRegion, String>implements SurveyStatsReportRegionDao
{
    private static final Logger LOG = LoggerFactory.getLogger( SurveyStatsReportRegionDaoImpl.class );


    @Override
    public List<SurveyStatsReportRegion> fetchRegionSurveyStatsById( Long regionId , String startTrxMonth , String endTrxMonth )
    {
        LOG.info( "Method to fetch all the survey stats,fetchRegionSurveyStatsById() started." );
        Criteria criteria = getSession().createCriteria( SurveyStatsReportRegion.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.REGION_ID_COLUMN, regionId ) );
            criteria.add( Restrictions.gt( CommonConstants.TRX_MONTH, startTrxMonth ) );
            criteria.add( Restrictions.le( CommonConstants.TRX_MONTH, endTrxMonth ) );
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchRegionSurveyStatsById() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchRegionSurveyStatsById() ", hibernateException );
        }

        LOG.info( "Method to fetch all the users by email id, fetchRegionSurveyStatsById() finished." );

        return (List<SurveyStatsReportRegion>) criteria.list();
    }

}
