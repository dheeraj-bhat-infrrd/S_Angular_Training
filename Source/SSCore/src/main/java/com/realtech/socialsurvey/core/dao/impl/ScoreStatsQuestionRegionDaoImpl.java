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
import com.realtech.socialsurvey.core.dao.ScoreStatsQuestionRegionDao;
import com.realtech.socialsurvey.core.entities.ScoreStatsQuestionRegion;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class ScoreStatsQuestionRegionDaoImpl extends GenericReportingDaoImpl<ScoreStatsQuestionRegion, String> implements ScoreStatsQuestionRegionDao{

	private static final Logger LOG = LoggerFactory.getLogger( ScoreStatsQuestionRegionDaoImpl.class );

	@SuppressWarnings("unchecked")
	@Override
    public List<ScoreStatsQuestionRegion> fetchScoreStatsQuestionForRegion( Long regionId, int startMonth, int startYear,
        int endMonth, int endYear, List<Long> questionIds )
    {
		LOG.debug( "Method to fetch all the score stats question for region,fetchScoreStatsQuestionForRegion() started." );
        Criteria criteria = getSession().createCriteria( ScoreStatsQuestionRegion.class );
        try {
          //the end month query will startmonth-1 and end year will be startyear+1
            criteria.add( Restrictions.eq( CommonConstants.REGION_ID_COLUMN, regionId ) );
            criteria.add(Restrictions.in(CommonConstants.QUESTION_ID, questionIds));
            Criterion rest1= Restrictions.and(Restrictions.eq( CommonConstants.YEAR_VAL, endYear ), 
                Restrictions.le( CommonConstants.MONTH_VAL, endMonth ));
            if( startMonth != 1){
                Criterion rest2= Restrictions.and(Restrictions.eq( CommonConstants.YEAR_VAL, startYear ), 
                    Restrictions.ge( CommonConstants.MONTH_VAL, startMonth ));
                criteria.add(Restrictions.or(rest1, rest2));
            }else if( startMonth == 1){
                criteria.add(rest1);

            }
            
            criteria.addOrder( Order.asc( CommonConstants.QUESTION_ID ) );
            criteria.addOrder( Order.asc( CommonConstants.YEAR_VAL ) );
            criteria.addOrder( Order.asc( CommonConstants.MONTH_VAL ) );

            LOG.debug( "Method to fetch all the score stats question for region,fetchScoreStatsQuestionForRegion() finished." );

            return (List<ScoreStatsQuestionRegion>) criteria.list();
        } catch ( HibernateException hibernateException ) {
            LOG.warn( "Exception caught in fetchScoreStatsQuestionForRegion() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchScoreStatsQuestionForRegion() ", hibernateException );
        }

      
	}


}
