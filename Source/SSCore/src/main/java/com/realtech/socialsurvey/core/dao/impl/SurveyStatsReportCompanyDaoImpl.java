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
    public List<SurveyStatsReportCompany> fetchCompanySurveyStatsById( Long companyId )
    {
        LOG.info( "Method to fetch all the survey stats,fetchCompanySurveyStatsById() started." );
        Criteria criteria = getSession().createCriteria( SurveyStatsReportCompany.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, companyId ) );
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchCompanySurveyStatsById() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchCompanySurveyStatsById() ", hibernateException );
        }

        LOG.info( "Method to fetch all the users by email id, fetchCompanySurveyStatsById() finished." );

        return (List<SurveyStatsReportCompany>) criteria.list();
    }
   
}
