package com.realtech.socialsurvey.core.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.SurveyTransactionReportRegionDao;
import com.realtech.socialsurvey.core.entities.SurveyTransactionReport;
import com.realtech.socialsurvey.core.entities.SurveyTransactionReportRegion;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class SurveyTransactionReportRegionDaoImpl extends GenericReportingDaoImpl<SurveyTransactionReportRegionDao, String> implements SurveyTransactionReportRegionDao
{
    private static final Logger LOG = LoggerFactory.getLogger( SurveyTransactionReportRegionDaoImpl.class );


    @Override
    public List<SurveyTransactionReportRegion> fetchSurveyTransactionByRegionId(Long regionId , int startYear , int startMonth , int endYear , int endMonth)
    {
        LOG.info( "method to fetch survey transaction report based on regionId,fetchSurveyTransactionByRegionId() started" );
        Criteria criteria = getSession().createCriteria( SurveyTransactionReportRegion.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.REGION_ID_COLUMN, regionId ) );
            if( startYear != 0 && startMonth != 0 && endYear == 0 && endMonth == 0 ){
                Criterion criterion = Restrictions.and(
                    Restrictions.ge( CommonConstants.AGGREGATE_BY_YEAR, startYear),
                    Restrictions.ge( CommonConstants.AGGREGATE_BY_MONTH, startMonth) );
                criteria.add( criterion );
            }else if( startYear != 0 && startMonth != 0 && endYear != 0 && endMonth != 0 ){
                Criterion criterion = Restrictions.and(
                    Restrictions.ge( CommonConstants.AGGREGATE_BY_YEAR, startYear),
                    Restrictions.ge( CommonConstants.AGGREGATE_BY_MONTH, startMonth),
                    Restrictions.le( CommonConstants.AGGREGATE_BY_YEAR, endYear),
                    Restrictions.le( CommonConstants.AGGREGATE_BY_MONTH, endMonth));
                criteria.add( criterion );
            }
            
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchSurveyTransactionByRegionId() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchSurveyTransactionByRegionId() ", hibernateException );
        }

        LOG.info( "method to fetch survey transaction report based on regionId, fetchSurveyTransactionByRegionId() finished." );
        return (List<SurveyTransactionReportRegion>) criteria.list();
        
    }
}
