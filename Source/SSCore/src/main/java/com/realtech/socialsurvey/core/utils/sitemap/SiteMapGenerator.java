package com.realtech.socialsurvey.core.utils.sitemap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;


/**
 * Creates sitemap for Social Survey. 
 * Three sitemaps need to be created.
 * 1. Sitemap with daily frequency. This will contain the urls that need to be indexed immediately
 * 2. Sitemap with weekly frequency. This will contain the urls that is of medium priority
 * 3. Sitemap with monthly frequency. This will contain the urls that is of lowest priority.
 *
 */
public class SiteMapGenerator implements Runnable{
	
	public static final Logger LOG = LoggerFactory.getLogger(SiteMapGenerator.class);

	public static final String DAILY_CONTENT= "daily";
	//public static final String WEEKLY_CONTENT= "weekly";
	//public static final String MONTHLY_CONTENT= "monthly";
	
	public static final String ORG_COMPANY= "company";
	public static final String ORG_REGION = "region";
	public static final String ORG_BRANCH = "branch";
	public static final String ORG_INDIVIDUAL = "individual";
	
	private String interval; // one of the three types possible
	private String organizationUnit;
	
	private String companySiteMapPath;
	private String regionSiteMapPath;
	private String branchSiteMapPath;
	private String individualSiteMapPath;
	
	private ApplicationContext context;
	
	public SiteMapGenerator(String interval, String organizationUnit, ApplicationContext context){
		this.interval = interval;
		this.organizationUnit = organizationUnit;
		this.context = context;
		companySiteMapPath = context.getEnvironment().getProperty("COMPANY_SITEMAP_PATH");
		regionSiteMapPath = context.getEnvironment().getProperty("REGION_SITEMAP_PATH");
		branchSiteMapPath = context.getEnvironment().getProperty("BRANCH_SITEMAP_PATH");
		individualSiteMapPath = context.getEnvironment().getProperty("INDIVIDUAL_SITEMAP_PATH");
	}
	
	
	@Override
	public void run() {
		// Check the organization unit and interval and query accordingly
		switch(organizationUnit){
			case ORG_COMPANY:
				if(interval.equals(DAILY_CONTENT)){
					LOG.debug("Getting sitemap content for company");
					SitemapContentFecher fetcher = new MongoSiteMapContentFetcher(MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, interval, context);
					SiteMapWriter siteMapWriter = new SiteMapWriter(companySiteMapPath, fetcher);
					siteMapWriter.writeSiteMap();
				}
				break;
			case ORG_REGION:
				if(interval.equals(DAILY_CONTENT)){
					LOG.debug("Getting sitemap content for region");
					SitemapContentFecher fetcher = new MongoSiteMapContentFetcher(MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, interval, context);
					SiteMapWriter siteMapWriter = new SiteMapWriter(regionSiteMapPath, fetcher);
					siteMapWriter.writeSiteMap();
				}
				break;
			case ORG_BRANCH:
				if(interval.equals(DAILY_CONTENT)){
					LOG.debug("Getting sitemap content for branches");
					SitemapContentFecher fetcher = new MongoSiteMapContentFetcher(MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, interval, context);
					SiteMapWriter siteMapWriter = new SiteMapWriter(branchSiteMapPath, fetcher);
					siteMapWriter.writeSiteMap();
				}
				break;
			case ORG_INDIVIDUAL:
				if(interval.equals(DAILY_CONTENT)){
					LOG.debug("Getting sitemap content for agents");
					SitemapContentFecher fetcher = new MongoSiteMapContentFetcher(MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, interval, context);
					SiteMapWriter siteMapWriter = new SiteMapWriter(individualSiteMapPath, fetcher);
					siteMapWriter.writeSiteMap();
				}
				break;
			default:
				break;
		}
	}
}

