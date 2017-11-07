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
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.CompanySurveyStatusStatsDao;
import com.realtech.socialsurvey.core.entities.CompanySurveyStatusStats;

/**
 * 
 * @author rohit
 *
 */
@Component
public class CompanySurveyStatusStatsDaoImpl extends GenericReportingDaoImpl<CompanySurveyStatusStats, String> implements CompanySurveyStatusStatsDao
{
    
    private static final Logger LOG = LoggerFactory.getLogger( CompanySurveyStatusStatsDaoImpl.class );

    
    @Override
    @SuppressWarnings ( "unchecked")
    public Map<Long, Long> getSentSurveyCountForCompaniesAfterSentDate( Date surveySentDate )
    {
        LOG.info( "method getSentSurveyCountForCompaniesAfterSentDate started for surveySentDate " + surveySentDate );
        Map<Long, Long> companySentSurveyCountsMap= new HashMap<Long, Long>();
        
        Criteria criteria = getSession().createCriteria( CompanySurveyStatusStats.class );
        criteria.add( Restrictions.ge( CommonConstants.SURVEY_STATS_MONITOR_DATE_COLUMN, surveySentDate ) );
        criteria.setProjection( Projections.projectionList().add(Projections.groupProperty("companyId")).add(  Projections.sum("surveyInvitationSentCount") ) );
        List<Object[]> companySentSurveyCountsList = criteria.list();
        for(Object[] companySentSurveyCount : companySentSurveyCountsList){
            companySentSurveyCountsMap.put( (Long) companySentSurveyCount[0] , (Long) companySentSurveyCount[1] );
        }
        
        LOG.info( "method getSentSurveyCountForCompaniesAfterSentDate finished for  surveySentDate " + surveySentDate );
        return companySentSurveyCountsMap;

    }


    @SuppressWarnings ( "unchecked")
    @Override
    public List<CompanySurveyStatusStats> getSurveyStatusCountForCompanyForPastNDays( long companyId, Date startDate,
        Date endDate )
    {
        LOG.info( "method getSurveyStatusCountForCompanyForPastNDays started for companyId " + companyId );
       
        Criteria criteria = getSession().createCriteria( CompanySurveyStatusStats.class );
        criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, companyId ) );
        
        if(startDate != null){
            criteria.add( Restrictions.ge( CommonConstants.TRANSACTION_MONITOR_DATE_COLUMN, startDate ) );            
        }
        
        if(endDate != null){
            criteria.add( Restrictions.le( CommonConstants.TRANSACTION_MONITOR_DATE_COLUMN, endDate ) );            
        }
        List<CompanySurveyStatusStats> companySurveyCountsList = criteria.list();

        LOG.info( "method getSurveyStatusCountForCompanyForPastNDays finished for companyId " + companyId );
        return companySurveyCountsList;
    }

}
