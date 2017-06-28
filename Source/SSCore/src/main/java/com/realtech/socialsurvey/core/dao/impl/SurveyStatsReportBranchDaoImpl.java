package com.realtech.socialsurvey.core.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.SurveyStatsReportBranchDao;
import com.realtech.socialsurvey.core.entities.SurveyStatsReportBranch;
import com.realtech.socialsurvey.core.entities.SurveyStatsReportCompany;
import com.realtech.socialsurvey.core.entities.SurveyStatsReportRegion;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class SurveyStatsReportBranchDaoImpl extends GenericReportingDaoImpl<SurveyStatsReportBranch, String>implements SurveyStatsReportBranchDao
{

    private static final Logger LOG = LoggerFactory.getLogger( SurveyStatsReportBranchDaoImpl.class );
   

    @Override
    public List<SurveyStatsReportBranch> fetchBranchSurveyStatsById( Long branchId )
    {
        LOG.info( "Method to fetch all the survey stats,fetchBranchSurveyStatsById() started." );
        Criteria criteria = getSession().createCriteria( SurveyStatsReportBranch.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.BRANCH_ID_COLUMN, branchId ) );
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchBranchSurveyStatsById() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchBranchSurveyStatsById() ", hibernateException );
        }

        LOG.info( "Method to fetch all the users by email id, fetchBranchSurveyStatsById() finished." );

        return (List<SurveyStatsReportBranch>) criteria.list();
    }
}
