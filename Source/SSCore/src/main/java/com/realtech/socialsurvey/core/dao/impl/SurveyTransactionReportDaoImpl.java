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
import com.realtech.socialsurvey.core.dao.SurveyTransactionReportDao;
import com.realtech.socialsurvey.core.entities.SurveyTransactionReport;
import com.realtech.socialsurvey.core.entities.UserAdoptionReport;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class SurveyTransactionReportDaoImpl extends GenericReportingDaoImpl<SurveyTransactionReportDao, String> implements SurveyTransactionReportDao
{

    private static final Logger LOG = LoggerFactory.getLogger( SurveyTransactionReportDaoImpl.class );
    
    @Override
    public List<SurveyTransactionReport> fetchSurveyTransactionById(Long entityId , String entityType , int startYear , int startMonth , int endYear , int endMonth)
    {
        LOG.info( "method to fetch survey transaction report based on companyId or agentId,fetchSurveyTransactionById() started" );
        Criteria criteria = getSession().createCriteria( SurveyTransactionReport.class );
        try {
            if(entityType.equals( CommonConstants.COMPANY_ID_COLUMN )){
                criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, entityId ) );
            }else if(entityType.equals( CommonConstants.AGENT_ID_COLUMN )){
                criteria.add( Restrictions.eq( CommonConstants.USER_ID, entityId ) );
            }
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
            LOG.error( "Exception caught in fetchSurveyTransactionById() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchSurveyTransactionById() ", hibernateException );
        }

        LOG.info( "method to fetch survey transaction report based on companyId or agentId, fetchSurveyTransactionById() finished." );
        return (List<SurveyTransactionReport>) criteria.list();
        
    }

}
