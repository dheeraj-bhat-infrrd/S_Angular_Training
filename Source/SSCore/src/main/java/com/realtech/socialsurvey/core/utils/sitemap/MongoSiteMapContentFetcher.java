package com.realtech.socialsurvey.core.utils.sitemap;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.ProfileUrlEntity;
import com.realtech.socialsurvey.core.entities.SiteMapEntry;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MongoSiteMapContentFetcher implements SitemapContentFecher {

	private static final Logger LOG = LoggerFactory.getLogger(MongoSiteMapContentFetcher.class);

	private String collectionName;
	private String interval;

	private int limit = 50;
	private long count;
	private int recordsFetched;
	private boolean areMoreRecordsPresent;

	@Autowired
	private OrganizationUnitSettingsDao organizationUnitSettingsDao;

	@Value("${APPLICATION_BASE_URL}")
	private String applicationUrl;

	public void setInterval(String interval) {
		this.interval = interval;
	}
	
	public void setCollectionName(String collectionName){
		this.collectionName = collectionName;
	}

	@Override
	public List<SiteMapEntry> getInitialContent() {
		LOG.info("Getting initial content for collection");
		List<SiteMapEntry> entries = null;
		// check the interval
		if (interval.equals(SiteMapGenerator.DAILY_CONTENT)) {
			count = organizationUnitSettingsDao.fetchSEOOptimizedOrganizationUnitCount(collectionName);
			LOG.debug("Total number of records are " + count + ". Limit is " + limit);
			if (count <= limit) {
				areMoreRecordsPresent = false;
			}
			else {
				areMoreRecordsPresent = true;
			}
			if (count > 0) {
				// get the records
				List<ProfileUrlEntity> profileUrls = organizationUnitSettingsDao.fetchSEOOptimizedOrganizationUnitSettings(collectionName, 0, limit);
				// convert profile urls to Site Map Entry
				entries = prepareSMEObjects(profileUrls);
			}
		}
		recordsFetched = limit;
		return entries;
	}

	@Override
	public boolean hasNext() {
		return areMoreRecordsPresent;
	}

	@Override
	public List<SiteMapEntry> nextBatch() {
		LOG.info("Getting next batch with limit " + limit + " from " + recordsFetched);
		List<SiteMapEntry> entries = null;
		if (areMoreRecordsPresent) {
			if (interval.equals(SiteMapGenerator.DAILY_CONTENT)) {
				List<ProfileUrlEntity> profileUrls = organizationUnitSettingsDao.fetchSEOOptimizedOrganizationUnitSettings(collectionName,
						recordsFetched, limit);
				entries = prepareSMEObjects(profileUrls);
			}
			recordsFetched += limit;
		}
		if (recordsFetched >= count) {
			areMoreRecordsPresent = false;
		}
		return entries;
	}

	private String generateLocation(String profileUrl) {
		// check the collection name and generate location accordingly
		LOG.debug("Generating location url for " + profileUrl + " for collection " + collectionName);
		if (collectionName.equals(MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION)) {
			return applicationUrl + "pages/company" + profileUrl;
		}
		else if (collectionName.equals(MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION)
				|| collectionName.equals(MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION)
				|| collectionName.equals(MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION)) {
			return applicationUrl + "pages" + profileUrl;
		}
		else {
			return null;
		}
	}

	private List<SiteMapEntry> prepareSMEObjects(List<ProfileUrlEntity> profileUrls) {
		LOG.info("Preparing SME objects");
		List<SiteMapEntry> entries = new ArrayList<SiteMapEntry>();
		SiteMapEntry entry = null;
		for (ProfileUrlEntity profileUrl : profileUrls) {
			LOG.info("Converting " + profileUrl + " to SME");
			entry = new SiteMapEntry();
			// generate location
			entry.setLocation(generateLocation(profileUrl.getProfileUrl()));
			entries.add(entry);
		}
		return entries;
	}

}
