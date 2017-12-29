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
    public List<SurveyStatsReportBranch> fetchBranchSurveyStatsById( Long branchId , String startTrxMonth , String endTrxMonth  )
    {
        LOG.debug( "Method to fetch all the survey stats,fetchBranchSurveyStatsById() started." );
        Criteria criteria = getSession().createCriteria( SurveyStatsReportBranch.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.BRANCH_ID_COLUMN, branchId ) );
            criteria.add( Restrictions.gt( CommonConstants.TRX_MONTH, startTrxMonth ) );
            criteria.add( Restrictions.le( CommonConstants.TRX_MONTH, endTrxMonth ) );
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchBranchSurveyStatsById() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchBranchSurveyStatsById() ", hibernateException );
        }

        LOG.debug( "Method to fetch all the users by branch id, fetchBranchSurveyStatsById() finished." );

        return (List<SurveyStatsReportBranch>) criteria.list();
    }
    
    @Override
    public SurveyStatsReportBranch fetchBranchSurveyStats( long branchId, int month , int year  )
    {
        LOG.debug( "Method to fetch all the survey stats,fetchBranchSurveyStats() started." );
        Criteria criteria = getSession().createCriteria( SurveyStatsReportBranch.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.BRANCH_ID_COLUMN, branchId ) );
            criteria.add( Restrictions.eq( CommonConstants.MONTH, (long)month ) );
            criteria.add( Restrictions.eq( CommonConstants.YEAR, (long)year ) );
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchBranchSurveyStats() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchBranchSurveyStats() ", hibernateException );
        }

        LOG.debug( "Method to fetch all the users by branch id, fetchBranchSurveyStats() finished." );
        return (SurveyStatsReportBranch) criteria.uniqueResult();
    }
    
    @Override
    public List<SurveyStatsReportBranch> fetchSurveyStatsById(Long entityId , String entityType)
    {
        LOG.info( "method to fetch branch based on Id and type,fetchSurveyStatsById() started" );
        Criteria criteria = getSession().createCriteria( SurveyStatsReportBranch.class );
        try {
            if(entityType.equals( CommonConstants.COMPANY_ID_COLUMN )){
                criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, entityId ) );
            }else if(entityType.equals( CommonConstants.REGION_ID_COLUMN )){
                criteria.add( Restrictions.eq( CommonConstants.REGION_ID_COLUMN, entityId ) );
            }else if(entityType.equals( CommonConstants.BRANCH_ID_COLUMN )){
                criteria.add( Restrictions.eq( CommonConstants.BRANCH_ID_COLUMN, entityId ) );
            }
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchSurveyStatsById() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchSurveyStatsById() ", hibernateException );
        }

        LOG.info( "method to fetch branch based on Id and type, fetchBranchSurveyStatsById() finished." );
        return (List<SurveyStatsReportBranch>) criteria.list();
        
    }
}
