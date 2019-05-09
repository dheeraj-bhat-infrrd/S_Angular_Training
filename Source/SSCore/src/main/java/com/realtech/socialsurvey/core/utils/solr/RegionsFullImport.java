package com.realtech.socialsurvey.core.utils.solr;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.dao.SolrImportDao;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;

@Component
@Resource(name = "regionsFullImport")
@EnableTransactionManagement(proxyTargetClass = true)
public class RegionsFullImport implements Runnable {

	public static final Logger LOG = LoggerFactory.getLogger(RegionsFullImport.class);

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
		LOG.info("Started run method of RegionsFullImport");
		int pageNo = 1;
		List<Region> regions = null;

		do {
			LOG.debug("Fetching Regions");
			try {
				regions = solrImportDao.fetchRegionsPage(pageSize * (pageNo - 1), pageSize);
			}
			catch (NoRecordsFetchedException e) {
				LOG.info("NoRecordsFetchedException occurred while fetching regions");
			}

			if (regions == null || regions.isEmpty()) {
				break;
			}
			
			LOG.debug("Adding Regions to Solr");
			try {
				solrSearchService.addRegionsToSolr(regions);
			}
			catch (SolrException e) {
				LOG.error("SolrException occurred while adding region to solr" , e);
			} catch ( InvalidInputException e ) {
			    LOG.error("SolrException occurred while adding region to solr" , e);
            }
			pageNo++;
		}
		while (!regions.isEmpty());
		LOG.info("Finished run method of RegionsFullImport");
	}
}