package com.realtech.socialsurvey.core.dao.impl;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.CompanyTransactionsSourceStatsDao;
import com.realtech.socialsurvey.core.entities.CompanyTransactionsSourceStats;

/**
 * 
 * @author rohit
 *
 */
@Repository
public class CompanyTransactionsSourceStatsDaoImpl  extends GenericReportingDaoImpl<CompanyTransactionsSourceStats, String> implements CompanyTransactionsSourceStatsDao
{
    
    private static final Logger LOG = LoggerFactory.getLogger( CompanyTransactionsSourceStatsDaoImpl.class );

    /**
     * 
     */
    @Override
    @SuppressWarnings ( "unchecked")
    public Map<Long, Long> getTransactionsByCompanyIdAndAfterTransactionDate( Date transactionDate )
    {
        LOG.info( "method getTransactionsForPastNDaysByCompanyId started for transactionDate " +transactionDate );
        Map<Long, Long> companyTransactionCountsMap= new HashMap<Long, Long>();
        
        Criteria criteria = getSession().createCriteria( CompanyTransactionsSourceStats.class );
        criteria.add( Restrictions.ge( CommonConstants.TRANSACTION_MONITOR_DATE_COLUMN, transactionDate ) );
        criteria.setProjection( Projections.projectionList().add(Projections.groupProperty("companyId")).add(  Projections.sum("totalTransactionsCount") ) );
        List<Object[]> companyTransactionCounts = criteria.list();
        for(Object[] companyTransactionsCount : companyTransactionCounts){
            companyTransactionCountsMap.put( (Long) companyTransactionsCount[0] , (Long) companyTransactionsCount[1] );
        }
        
        LOG.info( "method getTransactionsForPastNDaysByCompanyId finished for  transactionDate " + transactionDate );
        return companyTransactionCountsMap;
    }
    
    /**
     * 
     * @param companyId
     * @param startDate
     * @param endDate
     * @return
     */
    @Override
    @SuppressWarnings ( "unchecked")
    public List<CompanyTransactionsSourceStats> getTransactionsCountForCompanyForPastNDays(long companyId , Date startDate , Date endDate)
    {
        LOG.info( "method getTransactionsCountForCompanyForPastNDays started for companyId " + companyId );
       
        Criteria criteria = getSession().createCriteria( CompanyTransactionsSourceStats.class );
        criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, companyId ) );
        
        if(startDate != null){
            criteria.add( Restrictions.ge( CommonConstants.TRANSACTION_MONITOR_DATE_COLUMN, startDate ) );            
        }
        
        if(endDate != null){
            criteria.add( Restrictions.le( CommonConstants.TRANSACTION_MONITOR_DATE_COLUMN, endDate ) );            
        }
        List<CompanyTransactionsSourceStats> companyTransactions = criteria.list();

        LOG.info( "method getTransactionsCountForCompanyForPastNDays finished for companyId " + companyId );
        return companyTransactions;
    }
}
