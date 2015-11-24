package com.realtech.socialsurvey.core.utils.solr;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.dao.SolrImportDao;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
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
	
	@Value("${BATCH_SIZE}")
	private int pageSize;

	@Override
	@Transactional
	public void run() {
		LOG.info("Started run method of BranchesFullImport");
		int pageNo = 1;
		List<Branch> branches = null;

		do {
			LOG.debug("Fetching Branches");
			try {
				branches = solrImportDao.fetchBranchesPage(pageSize * (pageNo - 1), pageSize);
			}
			catch (NoRecordsFetchedException e) {
				LOG.info("NoRecordsFetchedException occurred while fetching branches");
			}

			if (branches == null || branches.isEmpty()) {
				break;
			}

			LOG.debug("Adding Branches to Solr");
			try {
				solrSearchService.addBranchesToSolr(branches);
			}
			catch (SolrException e) {
				LOG.error("SolrException occurred while adding branch to solr" , e);
			} catch ( InvalidInputException e ) {
			    LOG.error("SolrException occurred while adding branch to solr" , e);
            }
			pageNo++;
		}
		while (!branches.isEmpty());
		LOG.info("Finished run method of BranchesFullImport");
	}
}