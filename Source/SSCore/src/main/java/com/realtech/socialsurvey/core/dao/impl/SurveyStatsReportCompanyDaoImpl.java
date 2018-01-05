package com.realtech.socialsurvey.core.dao.impl;

import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.SurveyStatsReportCompanyDao;
import com.realtech.socialsurvey.core.entities.OverviewUser;
import com.realtech.socialsurvey.core.entities.SurveyStatsReportCompany;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.DatabaseException;
import com.realtech.socialsurvey.core.services.reportingmanagement.impl.OverviewManagementImpl;

@Component 
public class SurveyStatsReportCompanyDaoImpl extends GenericReportingDaoImpl<SurveyStatsReportCompany, String>implements SurveyStatsReportCompanyDao
{
    private static final Logger LOG = LoggerFactory.getLogger( SurveyStatsReportCompanyDaoImpl.class );


    @SuppressWarnings ( "unchecked")
    @Override
    public List<SurveyStatsReportCompany> fetchCompanySurveyStatsById( Long companyId , String startTrxMonth , String endTrxMonth )
    {
        LOG.debug( "Method to fetch all the survey stats,fetchCompanySurveyStatsById() started." );
        Criteria criteria = getSession().createCriteria( SurveyStatsReportCompany.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, companyId ) );
            criteria.add( Restrictions.gt( CommonConstants.TRX_MONTH, startTrxMonth ) );
            criteria.add( Restrictions.le( CommonConstants.TRX_MONTH, endTrxMonth ) );
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchCompanySurveyStatsById() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchCompanySurveyStatsById() ", hibernateException );
        }

        LOG.debug( "Method to fetch all the users by email id, fetchCompanySurveyStatsById() finished." );

        return (List<SurveyStatsReportCompany>) criteria.list();
    }
    
    
    @SuppressWarnings ( "unchecked")
    @Override
    public SurveyStatsReportCompany fetchCompanySurveyStats( long companyId , int month , int year )
    {
        LOG.debug( "Method to fetch all the survey stats,fetchCompanySurveyStats() started." );
        Criteria criteria = getSession().createCriteria( SurveyStatsReportCompany.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, companyId ) );
            criteria.add( Restrictions.eq( CommonConstants.MONTH, (long)month ) );
            criteria.add( Restrictions.eq( CommonConstants.YEAR, (long)year ) );
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchCompanySurveyStats() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchCompanySurveyStats() ", hibernateException );
        }

        LOG.debug( "Method fetchCompanySurveyStats() finished." );

        return (SurveyStatsReportCompany) criteria.uniqueResult();
    }   
}
