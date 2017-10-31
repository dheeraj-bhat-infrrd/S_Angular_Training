package com.realtech.socialsurvey.core.dao.impl;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.ReportingSurveyPreInititationDao;
import com.realtech.socialsurvey.core.entities.ReportingSurveyPreInititation;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.exception.DatabaseException;

public class ReportingSurveyPreInititationDaoImpl extends GenericReportingDaoImpl<ReportingSurveyPreInititation, String> implements ReportingSurveyPreInititationDao
{
    private static final Logger LOG = LoggerFactory.getLogger( ReportingSurveyPreInititationDaoImpl.class );


    // Method to get list of incomplete surveys to display in Dash board and profile page.
    @SuppressWarnings ( "unchecked")
    @Override
    public List<ReportingSurveyPreInititation> getIncompleteSurveyForReporting( Timestamp startDate, Timestamp endDate, int startIndex, int batchSize,
        Set<Long> agentIds, boolean isCompanyAdmin, long companyId)
    {
        Criteria criteria = getSession().createCriteria( SurveyPreInitiation.class );
        try {
            if ( startDate != null )
                criteria.add( Restrictions.ge( CommonConstants.CREATED_ON_EST, startDate ) );
            if ( endDate != null )
                criteria.add( Restrictions.le( CommonConstants.CREATED_ON_EST, endDate ) );
            if ( batchSize > 0 )
                criteria.setMaxResults( batchSize );
            if ( startIndex > 0 )
                criteria.setFirstResult( startIndex );

            if ( !isCompanyAdmin && agentIds.isEmpty() )
                criteria.add( Restrictions.in( CommonConstants.AGENT_ID_COLUMN, agentIds ) );
            else {
                criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, companyId ) );
            }
            criteria.add( Restrictions.in( CommonConstants.STATUS_COLUMN, new Integer[] {
                CommonConstants.STATUS_SURVEYPREINITIATION_PROCESSED, CommonConstants.SURVEY_STATUS_INITIATED } ) );
            criteria.addOrder( Order.desc( CommonConstants.MODIFIED_ON_COLUMN ) );
            return criteria.list();
        } catch ( HibernateException e ) {
            LOG.error( "Exception caught in getIncompleteSurveyForReporting() ", e );
            throw new DatabaseException( "Exception caught in getIncompleteSurveyForReporting() ", e );
        }
    }
}
