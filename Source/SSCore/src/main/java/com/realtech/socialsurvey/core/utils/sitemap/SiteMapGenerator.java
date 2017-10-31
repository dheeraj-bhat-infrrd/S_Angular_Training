package com.realtech.socialsurvey.core.utils.sitemap;

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
 * Creates sitemap for Social Survey. Three sitemaps need to be created. 1. Sitemap with daily
 * frequency. This will contain the urls that need to be indexed immediately 2. Sitemap with weekly
 * frequency. This will contain the urls that is of medium priority 3. Sitemap with monthly
 * frequency. This will contain the urls that is of lowest priority.
 */
@Component
@Scope ( value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SiteMapGenerator implements Runnable
{

    public static final Logger LOG = LoggerFactory.getLogger( SiteMapGenerator.class );

    public static final String DAILY_CONTENT = "daily";

    public static final String ORG_COMPANY = "company";
    public static final String ORG_REGION = "region";
    public static final String ORG_BRANCH = "branch";
    public static final String ORG_INDIVIDUAL = "individual";

    private static final String HOURLY_FREQUENCY = "hourly";

    private static final float COMPANY_PRIORITY = 0.2f;
    private static final float REGION_PRIORITY = 0.4f;
    private static final float BRANCH_PRIORITY = 0.6f;
    private static final float USERS_PRIORITY = 0.8f;

    private String interval; // one of the three types possible
    private String organizationUnit;

    @Autowired
    private MongoSiteMapContentFetcher companyFetcher;

    @Autowired
    private MongoSiteMapContentFetcher regionFetcher;

    @Autowired
    private MongoSiteMapContentFetcher branchFetcher;

    @Autowired
    private MongoSiteMapContentFetcher agentFetcher;

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private OrganizationManagementService organizationManagementService;
    
    @Value ( "${COMPANY_SITEMAP_PATH}")
    private String companySiteMapPath;

    @Value ( "${REGION_SITEMAP_PATH}")
    private String regionSiteMapPath;

    @Value ( "${BRANCH_SITEMAP_PATH}")
    private String branchSiteMapPath;

    @Value ( "${INDIVIDUAL_SITEMAP_PATH}")
    private String individualSiteMapPath;


    public void setInterval( String interval )
    {
        this.interval = interval;
    }


    public void setOrganizationUnit( String organizationUnit )
    {
        this.organizationUnit = organizationUnit;
    }


    @Override
    public void run()
    {
        // Check the organization unit and interval and query accordingly
        switch ( organizationUnit ) {
            case ORG_COMPANY:
                if ( interval.equals( DAILY_CONTENT ) ) {
                    LOG.debug( "Getting sitemap content for company" );
                    companyFetcher.setInterval( DAILY_CONTENT );
                    companyFetcher.setCollectionName( MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );
                    companyFetcher.setChangeFrequency( HOURLY_FREQUENCY );
                    companyFetcher.setPriority( COMPANY_PRIORITY );
                    List<Long> excludedCompanyIds = organizationManagementService.getHiddenPublicPageCompanyIds();
                    companyFetcher.setExcludedEntityIds( excludedCompanyIds );
                    
                    SiteMapWriter siteMapWriter = new SiteMapWriter( companySiteMapPath, companyFetcher );
                    siteMapWriter.writeSiteMap();
                }
                break;
            case ORG_REGION:
                if ( interval.equals( DAILY_CONTENT ) ) {
                    LOG.debug( "Getting sitemap content for region" );
                    regionFetcher.setInterval( DAILY_CONTENT );
                    regionFetcher.setCollectionName( MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION );
                    regionFetcher.setChangeFrequency( HOURLY_FREQUENCY );
                    regionFetcher.setPriority( REGION_PRIORITY );
                    List<Long> excludedRegionIds = organizationManagementService.getHiddenPublicPageRegionIds();
                    regionFetcher.setExcludedEntityIds( excludedRegionIds );
                    
                    SiteMapWriter siteMapWriter = new SiteMapWriter( regionSiteMapPath, regionFetcher );
                    siteMapWriter.writeSiteMap();
                }
                break;
            case ORG_BRANCH:
                if ( interval.equals( DAILY_CONTENT ) ) {
                    LOG.debug( "Getting sitemap content for branches" );
                    branchFetcher.setInterval( DAILY_CONTENT );
                    branchFetcher.setCollectionName( MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION );
                    branchFetcher.setChangeFrequency( HOURLY_FREQUENCY );
                    branchFetcher.setPriority( BRANCH_PRIORITY );
                    List<Long> excludedBranchIds = organizationManagementService.getHiddenPublicPageBranchIds();
                    branchFetcher.setExcludedEntityIds( excludedBranchIds );
                    
                    SiteMapWriter siteMapWriter = new SiteMapWriter( branchSiteMapPath, branchFetcher );
                    siteMapWriter.writeSiteMap();
                }
                break;
            case ORG_INDIVIDUAL:
                if ( interval.equals( DAILY_CONTENT ) ) {
                    LOG.debug( "Getting sitemap content for agents" );
                    agentFetcher.setInterval( DAILY_CONTENT );
                    agentFetcher.setCollectionName( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );
                    agentFetcher.setChangeFrequency( HOURLY_FREQUENCY );
                    agentFetcher.setPriority( USERS_PRIORITY );
                    
                    List<Long> excludedUserIds = organizationManagementService.getHiddenPublicPageUserIds();
                    excludedUserIds.addAll( userManagementService.getExcludedUserIds() );
                    agentFetcher.setExcludedEntityIds( excludedUserIds );
                    
                    SiteMapWriter siteMapWriter = new SiteMapWriter( individualSiteMapPath, agentFetcher );
                    siteMapWriter.writeSiteMap();
                }
                break;
            default:
                break;
        }
    }
}
