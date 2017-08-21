package com.realtech.socialsurvey.core.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.ScoreStatsQuestionCompanyDao;
import com.realtech.socialsurvey.core.entities.ScoreStatsOverallCompany;
import com.realtech.socialsurvey.core.entities.ScoreStatsQuestionCompany;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class ScoreStatsQuestionCompanyDaoImpl extends GenericReportingDaoImpl<ScoreStatsQuestionCompany, String> implements ScoreStatsQuestionCompanyDao{

	private static final Logger LOG = LoggerFactory.getLogger( ScoreStatsQuestionCompanyDaoImpl.class );

	@Override
	public List<ScoreStatsQuestionCompany> fetchScoreStatsQuestionForCompany(Long companyId, Long questionId, int startMonth, int endMonth, int year) {
		LOG.info( "Method to fetch all the score stats question for company,fetchScoreStatsQuestionForCompany() started." );
        Criteria criteria = getSession().createCriteria( ScoreStatsQuestionCompany.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, companyId ) );
            criteria.add( Restrictions.eq( CommonConstants.QUESTION_ID, questionId ) );
            criteria.add( Restrictions.ge( CommonConstants.MONTH_VAL, startMonth ) );
            criteria.add( Restrictions.le( CommonConstants.MONTH_VAL, endMonth ) );
            criteria.add( Restrictions.eq( CommonConstants.YEAR_VAL, year ) );
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchScoreStatsQuestionForCompany() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchScoreStatsQuestionForCompany() ", hibernateException );
        }

        LOG.info( "Method to fetch all the score stats question for company,fetchScoreStatsQuestionForCompany() finished." );

        return (List<ScoreStatsQuestionCompany>) criteria.list();
	}

	@Override
	public List<Long> fetchActiveQuestionsForCompany(Long companyId, int startMonth, int endMonth, int year) {
		
		LOG.info( "Method to fetch active questions for company,fetchActiveQuestionForCompany() started." );

		Criteria criteria = getSession().createCriteria(ScoreStatsQuestionCompany.class);
		try {
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, companyId ) );
            criteria.add( Restrictions.ge( CommonConstants.MONTH_VAL, startMonth ) );
            criteria.add( Restrictions.le( CommonConstants.MONTH_VAL, endMonth ) );
            criteria.add( Restrictions.eq( CommonConstants.YEAR_VAL, year ) );
            criteria.setProjection(Projections.distinct(Projections.property(CommonConstants.QUESTION_ID)));
            criteria.addOrder( Order.asc( CommonConstants.QUESTION_ID ) );
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchActiveQuestionForCompany() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchActiveQuestionForCompany() ", hibernateException );
        }
		
		LOG.info( "Method to fetch active questions for company,fetchActiveQuestionForCompany() finished." );
		
		List<Long> activeQuestionsList = new ArrayList<>();
		
		for( ScoreStatsQuestionCompany scoreStatsQuestionCompany : (List<ScoreStatsQuestionCompany>) criteria.list()){
			activeQuestionsList.add(scoreStatsQuestionCompany.getQuestionId());
		}
		return activeQuestionsList;
	}

}
