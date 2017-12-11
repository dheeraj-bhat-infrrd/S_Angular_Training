package com.realtech.socialsurvey.core.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.ScoreStatsOverallCompanyDao;
import com.realtech.socialsurvey.core.entities.ScoreStatsOverallCompany;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class ScoreStatsOverallCompanyDaoImpl extends GenericReportingDaoImpl<ScoreStatsOverallCompany, String> implements ScoreStatsOverallCompanyDao{

	private static final Logger LOG = LoggerFactory.getLogger( ScoreStatsOverallCompanyDaoImpl.class );
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ScoreStatsOverallCompany> fetchScoreStatsOverallForCompany(Long companyId, int startMonth, int startYear , int endMonth , int endYear) {
		LOG.debug( "Method to fetch all the score stats overall for company,fetchScoreStatsOverallForCompany() started." );
        Criteria criteria = getSession().createCriteria( ScoreStatsOverallCompany.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, companyId ) );
            Criterion rest1= Restrictions.and(Restrictions.eq( CommonConstants.YEAR_VAL, endYear ), 
                Restrictions.le( CommonConstants.MONTH_VAL, endMonth ));
            if( startMonth != 1){
                Criterion rest2= Restrictions.and(Restrictions.eq( CommonConstants.YEAR_VAL, startYear ), 
                    Restrictions.ge( CommonConstants.MONTH_VAL, startMonth ));
                criteria.add(Restrictions.or(rest1, rest2));
            }else if( startMonth == 1){
                criteria.add(rest1);

            }            
            criteria.addOrder( Order.asc( CommonConstants.YEAR_VAL ) );
            criteria.addOrder( Order.asc( CommonConstants.MONTH_VAL ) );
            LOG.debug( "Method to fetch all the score stats overall for company,fetchScoreStatsOverallForCompany() finished." );

            return (List<ScoreStatsOverallCompany>) criteria.list();

        } catch ( HibernateException hibernateException ) {
            LOG.warn( "Exception caught in fetchScoreStatsOverallForCompany() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchScoreStatsOverallForCompany() ", hibernateException );
        }

      
	}

}
