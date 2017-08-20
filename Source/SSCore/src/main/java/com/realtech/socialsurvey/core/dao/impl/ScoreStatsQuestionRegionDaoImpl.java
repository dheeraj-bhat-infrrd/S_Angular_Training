package com.realtech.socialsurvey.core.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.ScoreStatsQuestionRegionDao;
import com.realtech.socialsurvey.core.entities.ScoreStatsQuestionCompany;
import com.realtech.socialsurvey.core.entities.ScoreStatsQuestionRegion;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class ScoreStatsQuestionRegionDaoImpl extends GenericReportingDaoImpl<ScoreStatsQuestionRegion, String> implements ScoreStatsQuestionRegionDao{

	private static final Logger LOG = LoggerFactory.getLogger( ScoreStatsQuestionRegionDaoImpl.class );

	@Override
	public List<ScoreStatsQuestionRegion> fetchScoreStatsQuestionForRegion(Long regionId, Long questionId, int startMonth, int endMonth, int year) {
		LOG.info( "Method to fetch all the score stats question for region,fetchScoreStatsQuestionForRegion() started." );
        Criteria criteria = getSession().createCriteria( ScoreStatsQuestionRegion.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.REGION_ID_COLUMN, regionId ) );
            criteria.add( Restrictions.eq( CommonConstants.QUESTION_ID, questionId ) );
            criteria.add( Restrictions.ge( CommonConstants.MONTH_VAL, startMonth ) );
            criteria.add( Restrictions.le( CommonConstants.MONTH_VAL, endMonth ) );
            criteria.add( Restrictions.eq( CommonConstants.YEAR_VAL, year ) );
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchScoreStatsQuestionForRegion() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchScoreStatsQuestionForRegion() ", hibernateException );
        }

        LOG.info( "Method to fetch all the score stats question for region,fetchScoreStatsQuestionForRegion() finished." );

        return (List<ScoreStatsQuestionRegion>) criteria.list();
	}

	@Override
	public List<Long> fetchActiveQuestionsForRegion(Long regionId, int startMonth, int endMonth, int year) {

		LOG.info( "Method to fetch active questions for Region,fetchActiveQuestionForRegion() started." );

		Criteria criteria = getSession().createCriteria(ScoreStatsQuestionRegion.class);
		try {
            criteria.add( Restrictions.eq( CommonConstants.REGION_ID_COLUMN, regionId ) );
            criteria.add( Restrictions.ge( CommonConstants.MONTH_VAL, startMonth ) );
            criteria.add( Restrictions.le( CommonConstants.MONTH_VAL, endMonth ) );
            criteria.add( Restrictions.eq( CommonConstants.YEAR_VAL, year ) );
            criteria.setProjection(Projections.distinct(Projections.property(CommonConstants.QUESTION_ID)));
            criteria.addOrder( Order.asc( CommonConstants.QUESTION_ID ) );
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchActiveQuestionForRegion() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchActiveQuestionForRegion() ", hibernateException );
        }
		
		LOG.info( "Method to fetch active questions for Region,fetchActiveQuestionForRegion() finished." );
		
		List<Long> activeQuestionsList = new ArrayList<>();
		
		for( ScoreStatsQuestionRegion scoreStatsQuestionRegion : (List<ScoreStatsQuestionRegion>) criteria.list()){
			activeQuestionsList.add(scoreStatsQuestionRegion.getQuestionId());
		}
		return activeQuestionsList;
	}

}
