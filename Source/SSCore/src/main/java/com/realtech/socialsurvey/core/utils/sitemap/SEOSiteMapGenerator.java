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

import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;

/**
 * Creates sitemap for Social Survey. Three sitemaps need to be created. 1.
 * Sitemap with daily frequency. This will contain the urls that need to be
 * indexed immediately 2. Sitemap with weekly frequency. This will contain the
 * urls that is of medium priority 3. Sitemap with monthly frequency. This will
 * contain the urls that is of lowest priority.
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SEOSiteMapGenerator implements Runnable {

	public static final Logger LOG = LoggerFactory.getLogger(SEOSiteMapGenerator.class);

	public static final String DAILY_FREQUENCY = "daily";

	public static final String ORG_COMPANY = "company";
	public static final String ORG_BRANCH = "branch";
	public static final String ORG_INDIVIDUAL = "individual";

    private static final float COMPANY_PRIORITY = 0.2f;
    private static final float BRANCH_PRIORITY = 0.6f;
    private static final float USERS_PRIORITY = 0.8f;
    
	private String organizationUnit;

	@Autowired
	private MongoSiteMapContentFetcher companyFetcher;

	@Autowired
	private MongoSiteMapContentFetcher branchFetcher;

	@Autowired
	private MongoSiteMapContentFetcher agentFetcher;
	
    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private OrganizationManagementService organizationManagementService;

	@Value("${COMPANY_SEO_SITEMAP_PATH}")
	private String companySiteMapPath;

	@Value("${BRANCH_SEO_SITEMAP_PATH}")
	private String branchSiteMapPath;

	@Value("${INDIVIDUAL_SEO_SITEMAP_PATH}")
	private String individualSiteMapPath;

	public void setOrganizationUnit(String organizationUnit) {
		this.organizationUnit = organizationUnit;
	}

	@Override
	public void run() {
		
		try {
			// Check the organization unit and interval and query accordingly
			switch (organizationUnit) {
				case ORG_COMPANY:
					LOG.debug( "Getting sitemap content for company" );
		            companyFetcher.setCollectionName( MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );
		            companyFetcher.setChangeFrequency( DAILY_FREQUENCY );
		            companyFetcher.setPriority( COMPANY_PRIORITY );
		            List<Long> excludedCompanyIds = organizationManagementService.getHiddenPublicPageCompanyIds();
		            companyFetcher.setExcludedEntityIds( excludedCompanyIds );
		            
					SEOSiteMapWriter compSiteMapWriter = new SEOSiteMapWriter(companySiteMapPath, companyFetcher);
					compSiteMapWriter.writeSiteMap();
					break;
				case ORG_BRANCH:
					LOG.debug("Getting sitemap content for branches");
		            branchFetcher.setCollectionName( MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION );
		            branchFetcher.setChangeFrequency( DAILY_FREQUENCY );
		            branchFetcher.setPriority( BRANCH_PRIORITY );
		            List<Long> excludedBranchIds = organizationManagementService.getHiddenPublicPageBranchIds();
		            branchFetcher.setExcludedEntityIds( excludedBranchIds );
		            
					SEOSiteMapWriter branchSiteMapWriter = new SEOSiteMapWriter(branchSiteMapPath, branchFetcher);
					branchSiteMapWriter.writeSiteMap();
					break;
				case ORG_INDIVIDUAL:
					LOG.debug("Getting sitemap content for agents");
		            agentFetcher.setCollectionName( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );
		            agentFetcher.setChangeFrequency( DAILY_FREQUENCY );
		            agentFetcher.setPriority( USERS_PRIORITY );
		            
		            List<Long> excludedUserIds = new ArrayList<Long>();
		            excludedUserIds.addAll(organizationManagementService.getHiddenPublicPageUserIds());
		            excludedUserIds.addAll( userManagementService.getExcludedUserIds() );
		            agentFetcher.setExcludedEntityIds( excludedUserIds );
		            
					SEOSiteMapWriter agentSiteMapWriter = new SEOSiteMapWriter(individualSiteMapPath, agentFetcher);
					agentSiteMapWriter.writeSiteMap();
					break;
				default:
					break;
		}
		} catch(Exception e) {
			LOG.error("Error in SEOSiteMapGenerator " + e.getMessage());
		}
	}
}
