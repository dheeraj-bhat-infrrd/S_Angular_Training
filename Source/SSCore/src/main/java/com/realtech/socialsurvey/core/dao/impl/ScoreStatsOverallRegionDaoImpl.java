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
import com.realtech.socialsurvey.core.dao.ScoreStatsOverallRegionDao;
import com.realtech.socialsurvey.core.entities.ScoreStatsOverallRegion;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class ScoreStatsOverallRegionDaoImpl extends GenericReportingDaoImpl<ScoreStatsOverallRegion, String> implements ScoreStatsOverallRegionDao{

	private static final Logger LOG = LoggerFactory.getLogger( ScoreStatsOverallRegionDaoImpl.class );

	@SuppressWarnings("unchecked")
	@Override
	public List<ScoreStatsOverallRegion> fetchScoreStatsOverallForRegion(Long regionId, int startMonth, int startYear , int endMonth , int endYear) {
		LOG.debug( "Method to fetch all the score stats overall for region,fetchScoreStatsOverallForRegion() started." );
        Criteria criteria = getSession().createCriteria( ScoreStatsOverallRegion.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.REGION_ID_COLUMN, regionId) );
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
            LOG.debug( "Method to fetch all the score stats overall for region,fetchScoreStatsOverallForRegion() finished." );

            return (List<ScoreStatsOverallRegion>) criteria.list();
        } catch ( HibernateException hibernateException ) {
            LOG.warn( "Exception caught in fetchScoreStatsOverallForRegion() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchScoreStatsOverallForRegion() ", hibernateException );
        }

     
	}

}
