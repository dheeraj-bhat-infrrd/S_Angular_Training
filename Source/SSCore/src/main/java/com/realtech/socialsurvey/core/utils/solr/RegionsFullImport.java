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
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;

@Component
public class RegionsFullImport implements Runnable {

	public static final Logger LOG = LoggerFactory.getLogger(RegionsFullImport.class);

	@Resource
	@Qualifier("solrimport")
	private SolrImportDao solrImportDao;

	@Autowired
	private SolrSearchService solrSearchService;

	@Override
	@Transactional
	public void run() {
		int pageNo = 1;
		int pageSize = 10;
		List<Region> regions = null;

		do {
			// fetch regions
			try {
				regions = solrImportDao.fetchRegionsPage(pageSize * (pageNo - 1), pageSize);
			}
			catch (NoRecordsFetchedException e) {
				LOG.error("NoRecordsFetchedException occurred while fetching regions");
			}

			// write to solr
			for (Region region : regions) {
				try {
					solrSearchService.addOrUpdateRegionToSolr(region);
				}
				catch (SolrException e) {
					LOG.error("SolrException occurred while adding region to solr");
				}
			}
			pageNo++;
		}
		while (!regions.isEmpty());
	}
}
