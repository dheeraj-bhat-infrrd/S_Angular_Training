package com.realtech.socialsurvey.core.dao.impl;

import java.sql.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.CompanyActiveUsersStatsDao;
import com.realtech.socialsurvey.core.entities.CompanyActiveUsersStats;

/**
 * 
 * @author rohit
 *
 */
@Component
public class CompanyActiveUsersStatsDaoImpl extends GenericReportingDaoImpl<CompanyActiveUsersStats, String> implements CompanyActiveUsersStatsDao

{

    private static final Logger LOG = LoggerFactory.getLogger( CompanyActiveUsersStatsDaoImpl.class );

    
    @SuppressWarnings ( "unchecked")
    @Override
    public List<CompanyActiveUsersStats> getActiveUsersCountStatsForCompanyForPastNDays( long companyId, Date startDate,
        Date endDate )
    {
        LOG.info( "method getActiveUsersCountStatsForCompanyForPastNDays started for companyId " + companyId );
       
        Criteria criteria = getSession().createCriteria( CompanyActiveUsersStats.class );
        criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, companyId ) );
        
        if(startDate != null){
            criteria.add( Restrictions.ge( CommonConstants.SURVEY_STATS_MONITOR_DATE_COLUMN, startDate ) );            
        }
        
        if(endDate != null){
            criteria.add( Restrictions.le( CommonConstants.SURVEY_STATS_MONITOR_DATE_COLUMN, endDate ) );            
        }
        List<CompanyActiveUsersStats> companyActiveUserCountStats = criteria.list();

        LOG.info( "method getActiveUsersCountStatsForCompanyForPastNDays finished for companyId " + companyId );
        return companyActiveUserCountStats;
    }
    

}
