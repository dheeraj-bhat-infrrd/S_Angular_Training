package com.realtech.socialsurvey.core.dao.impl;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.dao.RegionDao;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component("region")
public class RegionDaoImpl extends GenericDaoImpl<Region, Long> implements RegionDao {

	private static final Logger LOG = LoggerFactory.getLogger(RegionDaoImpl.class);

	/*
	 * Method to delete all the users of a company.
	 */
	@Override
	public void deleteRegionsByCompanyId(long companyId) {
		LOG.info("Method to delete all the regions by company id,deleteRegionsByCompanyId() started.");
		try {
			Query query = getSession().createQuery("delete from Region where company.companyId=?");
			query.setParameter(0, companyId);
			query.executeUpdate();
		}
		catch (HibernateException hibernateException) {
			LOG.error("Exception caught in deleteRegionsByCompanyId() ", hibernateException);
			throw new DatabaseException("Exception caught in deleteRegionsByCompanyId() ", hibernateException);
		}
		LOG.info("Method to delete all the regions by company id, deleteRegionsByCompanyId() finished.");
	}
}
// JIRA SS-42 By RM-05 EOC
