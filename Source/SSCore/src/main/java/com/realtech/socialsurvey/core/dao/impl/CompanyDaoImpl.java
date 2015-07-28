package com.realtech.socialsurvey.core.dao.impl;

import java.sql.Timestamp;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.commons.CommonConstants;
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
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Company> searchCompaniesByNameAndKeyValue(String namePattern, int accountType, int status) {
		Criteria criteria = getSession().createCriteria(Company.class);
		criteria.add(Restrictions.ilike("company", namePattern, MatchMode.START));
		if (status > -1) {
			criteria.add(Restrictions.eq(CommonConstants.STATUS_COLUMN, status));
		}
		if (accountType > -1) {
			criteria.add(Restrictions.sqlRestriction("COMPANY_ID in (select ld.COMPANY_ID from LICENSE_DETAILS ld where ACCOUNTS_MASTER_ID=" + accountType + ")"));
		}
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Company> getCompaniesByDateRange(Timestamp startTime, Timestamp endTime) {
		Criteria criteria = getSession().createCriteria(Company.class);
		criteria.add(Restrictions.ne(CommonConstants.CREATED_BY, "ADMIN"));
		criteria.addOrder(Order.desc(CommonConstants.CREATED_ON));
		if (startTime != null)
			criteria.add(Restrictions.ge(CommonConstants.CREATED_ON, startTime));
		if (endTime != null)
			criteria.add(Restrictions.le(CommonConstants.CREATED_ON, endTime));
		return criteria.list();
	}
	
}
// JIRA SS-42 By RM-05 EOC