package com.realtech.socialsurvey.core.dao.impl;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.dao.BranchDao;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component("branch")
public class BranchDaoImpl extends GenericDaoImpl<Branch, Long> implements BranchDao {

	private static final Logger LOG = LoggerFactory.getLogger(BranchDaoImpl.class);

	/*
	 * Method to delete all the users of a company.
	 */
	@Override
	public void deleteBranchesByCompanyId(long companyId) {
		LOG.info("Method to delete all the branches by company id,deleteBranchesByCompanyId() started.");
		try {
			Query query = getSession().createQuery("delete from Branch where company.companyId=?");
			query.setParameter(0, companyId);
			query.executeUpdate();
		}
		catch (HibernateException hibernateException) {
			LOG.error("Exception caught in deleteBranchesByCompanyId() ", hibernateException);
			throw new DatabaseException("Exception caught in deleteBranchesByCompanyId() ", hibernateException);
		}
		LOG.info("Method to delete all the branches by company id, deleteBranchesByCompanyId() finished.");
	}
}
// JIRA SS-42 By RM-05 EOC
