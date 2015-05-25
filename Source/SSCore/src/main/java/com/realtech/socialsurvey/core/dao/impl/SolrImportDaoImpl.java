package com.realtech.socialsurvey.core.dao.impl;

import java.util.List;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.dao.SolrImportDao;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;

@Component("solrimport")
public class SolrImportDaoImpl extends BranchDaoImpl implements SolrImportDao {

	private static final Logger LOG = LoggerFactory.getLogger(SolrImportDaoImpl.class);

	@Override
	public List<Region> fetchRegionsPage(int offset, int pageSize) throws NoRecordsFetchedException {
		LOG.info("Fetching paginated region results");

		Query query = createHibernateQuery(offset, pageSize, "FROM Region R ORDER BY R.regionId ASC");
		@SuppressWarnings("unchecked")
		List<Region> regions = query.list();

		LOG.info("Fetched paginated Region results (" + regions.size() + ") from offset: " + offset);
		return regions;
	}

	@Override
	public List<Branch> fetchBranchesPage(int offset, int pageSize) throws NoRecordsFetchedException {
		LOG.info("Fetching paginated branch results");

		Query query = createHibernateQuery(offset, pageSize, "FROM Branch B ORDER BY B.branchId ASC");
		@SuppressWarnings("unchecked")
		List<Branch> branches = query.list();

		LOG.info("Fetched paginated Branch results (" + branches.size() + ") from offset: " + offset);
		return branches;
	}

	@Override
	public List<User> fetchUsersPage(int offset, int pageSize) throws NoRecordsFetchedException {
		LOG.info("Fetching paginated user results");

		Query query = createHibernateQuery(offset, pageSize, "FROM User U ORDER BY U.userId ASC");
		@SuppressWarnings("unchecked")
		List<User> users = query.list();

		LOG.info("Fetched paginated User results (" + users.size() + ") from offset: " + offset);
		return users;
	}

	// creates query
	private Query createHibernateQuery(int offset, int pageSize, String selectQuery) {
		Query query = getSession().createQuery(selectQuery);
		query.setFirstResult(offset);
		query.setMaxResults(pageSize);
		return query;
	}
}