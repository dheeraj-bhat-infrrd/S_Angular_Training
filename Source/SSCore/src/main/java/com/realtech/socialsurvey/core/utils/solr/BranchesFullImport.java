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
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;

@Component
public class BranchesFullImport implements Runnable {

	public static final Logger LOG = LoggerFactory.getLogger(BranchesFullImport.class);

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
		List<Branch> branches = null;

		do {
			// fetch branches
			try {
				branches = solrImportDao.fetchBranchesPage(pageSize * (pageNo - 1), pageSize);
			}
			catch (NoRecordsFetchedException e) {
				LOG.error("NoRecordsFetchedException occurred while fetching branches");
			}

			// write to solr
			for (Branch branch : branches) {
				try {
					solrSearchService.addOrUpdateBranchToSolr(branch);
				}
				catch (SolrException e) {
					LOG.error("SolrException occurred while adding branch to solr");
				}
			}
			pageNo++;
		}
		while (!branches.isEmpty());
	}
}
