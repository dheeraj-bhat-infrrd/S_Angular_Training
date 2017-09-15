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
import com.realtech.socialsurvey.core.dao.SurveyTransactionReportBranchDao;
import com.realtech.socialsurvey.core.entities.SurveyTransactionReportBranch;
import com.realtech.socialsurvey.core.entities.SurveyTransactionReportRegion;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class SurveyTransactionReportBranchDaoImpl extends GenericReportingDaoImpl<SurveyTransactionReportBranchDao , String> implements SurveyTransactionReportBranchDao
{
    private static final Logger LOG = LoggerFactory.getLogger( SurveyTransactionReportBranchDaoImpl.class );

    @Override
    public List<SurveyTransactionReportBranch> fetchSurveyTransactionByBranchId(Long branchId , int startYear , int startMonth , int endYear , int endMonth)
    {
        LOG.info( "method to fetch survey transaction report based on branchId,fetchSurveyTransactionByBranchId() started" );
        Criteria criteria = getSession().createCriteria( SurveyTransactionReportBranch.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.BRANCH_ID_COLUMN, branchId ) );
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
            LOG.error( "Exception caught in fetchSurveyTransactionByBranchId() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchSurveyTransactionByBranchId() ", hibernateException );
        }

        LOG.info( "method to fetch survey transaction report based on branchId, fetchSurveyTransactionByBranchId() finished." );
        return (List<SurveyTransactionReportBranch>) criteria.list();
        
    }

}
