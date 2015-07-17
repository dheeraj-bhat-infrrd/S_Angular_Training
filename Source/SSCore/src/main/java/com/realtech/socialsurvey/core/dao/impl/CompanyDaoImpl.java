package com.realtech.socialsurvey.core.dao.impl;

import java.sql.Timestamp;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.dao.CompanyDao;
import com.realtech.socialsurvey.core.entities.Company;

@Component("company")
public class CompanyDaoImpl extends GenericDaoImpl<Company, Long> implements CompanyDao {
	private static final Logger LOG = LoggerFactory.getLogger(CompanyDaoImpl.class);

	@Autowired
	SessionFactory sessionFactory;

	@Override
	@SuppressWarnings("unchecked")
	public List<Company> searchBetweenTimeIntervals(Timestamp lowerTime, Timestamp higherTime) {
		LOG.debug("Inside method searchBetweenTimeIntervals");
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(Company.class);
		criteria.add(Restrictions.ge("createdOn", lowerTime));
		if (higherTime != null) {
			criteria.add(Restrictions.le("createdOn", higherTime));
		}
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Company> searchCompaniesByName(String namePattern) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(Company.class);
		criteria.add(Restrictions.like("company", namePattern, MatchMode.START));
		return criteria.list();
	}
}
// JIRA SS-42 By RM-05 EOC