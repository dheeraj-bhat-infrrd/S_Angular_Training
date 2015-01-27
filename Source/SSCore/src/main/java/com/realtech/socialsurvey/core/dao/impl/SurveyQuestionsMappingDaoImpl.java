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
import com.realtech.socialsurvey.core.dao.SurveyQuestionsMappingDao;
import com.realtech.socialsurvey.core.entities.Survey;
import com.realtech.socialsurvey.core.entities.SurveyQuestionsMapping;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component("surveyQuestionsMapping")
public class SurveyQuestionsMappingDaoImpl extends GenericDaoImpl<SurveyQuestionsMapping, Long> implements SurveyQuestionsMappingDao {

	private static final Logger LOG = LoggerFactory.getLogger(SurveyQuestionsMappingDaoImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public List<SurveyQuestionsMapping> fetchActiveSurveyQuestions(User user, Survey survey) {
		LOG.info("Method to fetch all surveyQuestions, fetchActiveSurveyQuestions() started.");
		
		Criteria criteria = getSession().createCriteria(SurveyQuestionsMapping.class);
		try {
			criteria.add(Restrictions.eq(CommonConstants.SURVEY_COLUMN, survey));
			criteria.addOrder(Order.asc("questionOrder"));

			Criterion criterion = Restrictions.or(Restrictions.eq(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE));
			criteria.add(criterion);
			LOG.info("Method to fetch all surveyQuestions, fetchActiveSurveyQuestions() finished.");
		}
		catch (HibernateException hibernateException) {
			LOG.error("Exception caught in fetchUsersBySimilarEmailId() ", hibernateException);
			throw new DatabaseException("Exception caught in fetchUsersBySimilarEmailId() ", hibernateException);
		}
		return (List<SurveyQuestionsMapping>) criteria.list();
	}
}