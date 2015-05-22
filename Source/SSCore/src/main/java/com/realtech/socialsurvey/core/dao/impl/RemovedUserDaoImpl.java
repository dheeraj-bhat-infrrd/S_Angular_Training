package com.realtech.socialsurvey.core.dao.impl;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.dao.RemovedUserDao;
import com.realtech.socialsurvey.core.entities.RemovedUser;

@Component
public class RemovedUserDaoImpl extends GenericDaoImpl<RemovedUser, Long> implements RemovedUserDao {

	private static final Logger LOG = LoggerFactory.getLogger(RemovedUserDaoImpl.class);
	
	/*
	 * Method to remove all the Removed Users for the company given
	 */
	@Override
	public void deleteRemovedUsersByCompany(long companyId) {
		LOG.info("Method deleteRemovedUsersByCompany() called to delete removed users of company id : " + companyId);
		Query query = getSession().createQuery("delete from RemovedUser where company.companyId=?");
		query.setParameter(0, companyId);
		query.executeUpdate();
		LOG.info("Method deleteRemovedUsersByCompany() finished.");
	}
}
