package com.realtech.socialsurvey.core.utils.solr;

import java.util.List;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.realtech.socialsurvey.core.dao.SolrImportDao;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;

@Component
public class UsersFullImport implements Runnable {

	public static final Logger LOG = LoggerFactory.getLogger(UsersFullImport.class);

	@Resource
	@Qualifier("solrimport")
	private SolrImportDao solrImportDao;

	@Autowired
	private SolrSearchService solrSearchService;

	@Override
	@Transactional
	public void run() {
		int pageSize = 2;
		int pageNo = 1;
		List<User> users = null;

		do {
			// fetch users
			try {
				users = solrImportDao.fetchUsersPage(pageSize * (pageNo - 1), pageSize);
			}
			catch (NoRecordsFetchedException e) {
				LOG.error("NoRecordsFetchedException occurred while fetching users");
			}

			// write to solr
			for (User user : users) {
				try {
					solrSearchService.addUserToSolr(user);
				}
				catch (SolrException e) {
					LOG.error("SolrException occurred while adding user to solr");
				}
			}
			pageNo++;
		}
		while (!users.isEmpty());
	}
}
