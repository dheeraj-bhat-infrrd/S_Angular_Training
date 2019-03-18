package com.realtech.socialsurvey.core.utils.sitemap;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.ProfileUrlEntity;
import com.realtech.socialsurvey.core.entities.SEOUrlEntity;
import com.realtech.socialsurvey.core.entities.SiteMapEntry;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;


@Component
@Scope ( value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MongoSiteMapContentFetcher implements SitemapContentFecher, InitializingBean
{

    private static final Logger LOG = LoggerFactory.getLogger( MongoSiteMapContentFetcher.class );

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSXXX" );

    // needs to be removed
    private static final String HOURLY_FREQUENCY = "hourly";
    private static final String DAILY_FREQUENCY = "daily";

    private static final float COMPANY_PRIORITY = 0.2f;
    private static final float REGION_PRIORITY = 0.4f;
    private static final float BRANCH_PRIORITY = 0.6f;
    private static final float USERS_PRIORITY = 0.8f;

    private String collectionName;
    private String interval;
    private float priority;
    private String changeFrequency;
    private List<Long> excludedEntityIds = new ArrayList<Long>();

    private int limit = 1000;
    private long count;
    private int recordsFetched;
    private boolean areMoreRecordsPresent;

    @Autowired
    private OrganizationUnitSettingsDao organizationUnitSettingsDao;
    
    @Autowired
    private OrganizationManagementService organizationManagementService;
    
    @Value ( "${APPLICATION_BASE_URL}")
    private String applicationUrl;

    @Value ( "${SITEMAP_LAST_MODIFIED_TIME_INTERVAL}")
    private String lastModifiedTimeInterval;


    public List<Long> getExcludedEntityIds()
    {
        return excludedEntityIds;
    }


    public void setExcludedEntityIds( List<Long> excludedEntityIds )
    {
        this.excludedEntityIds = excludedEntityIds;
    }


    public void setInterval( String interval )
    {
        this.interval = interval;
    }


    public void setCollectionName( String collectionName )
    {
        this.collectionName = collectionName;
    }


    public void setPriority( float priority )
    {
        this.priority = priority;
    }


    public void setChangeFrequency( String changeFrequency )
    {
        this.changeFrequency = changeFrequency;
    }


    @Override
    public List<SiteMapEntry> getInitialContent()
    {
        LOG.info( "Getting initial content for collection" );
        List<SiteMapEntry> entries = null;
        // check the interval
        if ( interval.equals( SiteMapGenerator.DAILY_CONTENT ) ) {
            count = organizationUnitSettingsDao.fetchSEOOptimizedOrganizationUnitCount( collectionName,
                this.getExcludedEntityIds() );
            LOG.debug( "Total number of records are " + count + ". Limit is " + limit );
            if ( count <= limit ) {
                areMoreRecordsPresent = false;
            } else {
                areMoreRecordsPresent = true;
            }
            if ( count > 0 ) {
                // get the records
                List<ProfileUrlEntity> profileUrls = organizationUnitSettingsDao
                    .fetchSEOOptimizedOrganizationUnitSettings( collectionName, 0, limit, this.getExcludedEntityIds() );
                // convert profile urls to Site Map Entry
                entries = prepareSMEObjects( profileUrls );
            }
        }
        recordsFetched = limit;
        return entries;
    }


    @Override
    public boolean hasNext()
    {
        return areMoreRecordsPresent;
    }


    @Override
    public List<SiteMapEntry> nextBatch()
    {
        LOG.info( "Getting next batch with limit " + limit + " from " + recordsFetched );
        List<SiteMapEntry> entries = null;
        if ( areMoreRecordsPresent ) {
            if ( interval.equals( SiteMapGenerator.DAILY_CONTENT ) ) {
                List<ProfileUrlEntity> profileUrls = organizationUnitSettingsDao.fetchSEOOptimizedOrganizationUnitSettings(
                    collectionName, recordsFetched, limit, this.getExcludedEntityIds() );
                entries = prepareSMEObjects( profileUrls );
            }
            recordsFetched += limit;
        }
        if ( recordsFetched >= count ) {
            areMoreRecordsPresent = false;
        }
        return entries;
    }


    String generateLocation( String profileUrl )
    {
        // check the collection name and generate location accordingly
        LOG.trace( "Generating location url for " + profileUrl + " for collection " + collectionName );
        if ( collectionName.equals( MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION ) ) {
            return applicationUrl + "pages/company" + profileUrl;
        } else if ( collectionName.equals( MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION )
            || collectionName.equals( MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION )
            || collectionName.equals( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION ) ) {
            return applicationUrl + "pages" + profileUrl;
        } else {
            return null;
        }
    }


    List<SiteMapEntry> prepareSMEObjects( List<ProfileUrlEntity> profileUrls )
    {
        LOG.info( "Preparing SME objects" );
        List<SiteMapEntry> entries = new ArrayList<SiteMapEntry>();
        SiteMapEntry entry = null;
        for ( ProfileUrlEntity profileUrl : profileUrls ) {
            LOG.trace( "Converting " + profileUrl + " to SME" );
            entry = new SiteMapEntry();
            //set priority
            // entry.setPriority( priority );
            //set frequency
            // entry.setChangeFrequency( changeFrequency );
            entry.setChangeFrequency( HOURLY_FREQUENCY );
            // generate location
            if ( collectionName.equals( MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION ) ) {
                entry.setPriority( COMPANY_PRIORITY );
            } else if ( collectionName.equals( MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION ) ) {
                entry.setPriority( REGION_PRIORITY );
            } else if ( collectionName.equals( MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION ) ) {
                entry.setPriority( BRANCH_PRIORITY );
            } else if ( collectionName.equals( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION ) ) {
                entry.setPriority( USERS_PRIORITY );
            }
            entry.setLocation( generateLocation( profileUrl.getProfileUrl() ) );
            Timestamp modifiedOnTimestamp = new Timestamp( profileUrl.getModifiedOn() );
            // change the modified time if the modified on is older than the configured value
            entry.setLastModifiedDate( DATE_FORMAT.format( modifiedOnTimestamp ) );
            entries.add( entry );
        }
        return entries;
    }


    @Override
    public void afterPropertiesSet() throws Exception
    {
        // Setting the UTC time zone
        DATE_FORMAT.setTimeZone( TimeZone.getDefault() );
    }


	@Override
	public List<SiteMapEntry> getInitialSEOContent(String locationType) {
        LOG.info( "Getting initial SEO related content for collection" );
        List<SiteMapEntry> entries = null;
        
        count = organizationUnitSettingsDao.fetchSEOUrlCount(collectionName, locationType, excludedEntityIds);
        LOG.info("SEO URL conut - " + count);
        if ( count <= limit ) {
            areMoreRecordsPresent = false;
        } else {
            areMoreRecordsPresent = true;
        }
        if(count > 0) {
	   		List<SEOUrlEntity> seoUrls = organizationUnitSettingsDao.fetchSEOUrlEntty(
	   				collectionName, 0, limit, locationType, this.getExcludedEntityIds() );
	   		if(seoUrls != null && !seoUrls.isEmpty()) {
	   			entries = prepareSMEObjectsForSEO(seoUrls, locationType);
	    	}
	   		recordsFetched = limit;
        }
        
        return entries;
    }


	@Override
	public List<SiteMapEntry> nextSEOBatch(String locationType) {
        LOG.info( "Getting next batch with limit " + limit + " from " + recordsFetched );
        List<SiteMapEntry> entries = null;
        if ( areMoreRecordsPresent ) {
               List<SEOUrlEntity> seoUrls = organizationUnitSettingsDao.fetchSEOUrlEntty(
                    collectionName, recordsFetched, limit, locationType, this.getExcludedEntityIds() );
               entries = prepareSMEObjectsForSEO(seoUrls, locationType);
            recordsFetched += limit;
        }
        if ( recordsFetched >= count ) {
            areMoreRecordsPresent = false;
        }
        return entries;
    }


	private List<SiteMapEntry> prepareSMEObjectsForSEO(List<SEOUrlEntity> seoUrls, String locationType) {
		LOG.info("prepareSMEObjectsForSEO started for collection " + collectionName);
		
		List<SiteMapEntry> siteMapUrlList = new ArrayList<SiteMapEntry>();
		if(seoUrls != null) {
			LOG.info("soURLs " + seoUrls.size());
			Map<String,String> stateCodeNameMap = organizationManagementService.getStateCodeNameMap();
			for(SEOUrlEntity seoEntity : seoUrls) {
				LOG.debug("Result from DB with "+ locationType + " = " + seoEntity.getLocation() + " for vertical = " + seoEntity.getVertical() + " for entity = " + collectionName);
				try {
					//check zipcode-vertical is added to sitemap, if not add it.
					if(seoEntity.getLocation() != null && !seoEntity.getLocation().isEmpty() && locationType.equalsIgnoreCase("zipcode")) {
						SiteMapEntry entry = new SiteMapEntry();
						entry.setChangeFrequency(DAILY_FREQUENCY);
						entry.setLastModifiedDate(new Date().toString());
						if ( collectionName.equals( MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION ) ) {
			                entry.setPriority( COMPANY_PRIORITY );
			                entry.setLocation(applicationUrl + "top/" + seoEntity.getVertical().replaceAll(" ", "-").replaceAll("&", "&amp;") +  "/Companies/" + seoEntity.getLocation());
			            } else if ( collectionName.equals( MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION ) ) {
			                entry.setPriority( BRANCH_PRIORITY );
			                entry.setLocation(applicationUrl + "top/" + seoEntity.getVertical().replaceAll(" ", "-").replaceAll("&", "&amp;") + "/Loan-Offices/" + seoEntity.getLocation());
			            } else if ( collectionName.equals( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION ) ) {
			                entry.setPriority( USERS_PRIORITY );
			                entry.setLocation(applicationUrl + "top/" + seoEntity.getVertical().replaceAll(" ", "-").replaceAll("&", "&amp;") + "/Professionals/" + seoEntity.getLocation());
			            }
						siteMapUrlList.add(entry);
					} else if(seoEntity.getLocation() != null && !seoEntity.getLocation().isEmpty() && locationType.equalsIgnoreCase("city") && seoEntity.getState() != null && stateCodeNameMap.get(seoEntity.getState()) != null) {
						SiteMapEntry entry = new SiteMapEntry();
						entry.setChangeFrequency(DAILY_FREQUENCY);
						entry.setLastModifiedDate(new Date().toString());
						if ( collectionName.equals( MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION ) ) {
			                entry.setPriority( COMPANY_PRIORITY );
			                entry.setLocation(applicationUrl + "top/" + seoEntity.getVertical().replaceAll(" ", "-").replaceAll("&", "&amp;") + "/Companies/" + seoEntity.getLocation().replaceAll(" ", "-") + "_" + seoEntity.getState());
			            } else if ( collectionName.equals( MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION ) ) {
			                entry.setPriority( BRANCH_PRIORITY );
			                entry.setLocation(applicationUrl + "top/" + seoEntity.getVertical().replaceAll(" ", "-").replaceAll("&", "&amp;") + "/Loan-Offices/" + seoEntity.getLocation().replaceAll(" ", "-") + "_" + seoEntity.getState());
			            } else if ( collectionName.equals( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION ) ) {
			                entry.setPriority( USERS_PRIORITY );
			                entry.setLocation(applicationUrl + "top/" + seoEntity.getVertical().replaceAll(" ", "-").replaceAll("&", "&amp;") + "/Professionals/" + seoEntity.getLocation().replaceAll(" ", "-") + "_" + seoEntity.getState());
			            }
						siteMapUrlList.add(entry);
					}else if(seoEntity.getLocation() != null && !seoEntity.getLocation().isEmpty() && locationType.equalsIgnoreCase("state") && stateCodeNameMap.get(seoEntity.getLocation()) != null) {
						SiteMapEntry entry = new SiteMapEntry();
						entry.setChangeFrequency(DAILY_FREQUENCY);
						entry.setLastModifiedDate(new Date().toString());
						if ( collectionName.equals( MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION ) ) {
			                entry.setPriority( COMPANY_PRIORITY );
			                entry.setLocation(applicationUrl + "top/" + seoEntity.getVertical().replaceAll(" ", "-").replaceAll("&", "&amp;") + "/Companies/" + stateCodeNameMap.get(seoEntity.getLocation()).replaceAll(" ", "-"));
			            } else if ( collectionName.equals( MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION ) ) {
			                entry.setPriority( BRANCH_PRIORITY );
			                entry.setLocation(applicationUrl + "top/" + seoEntity.getVertical().replaceAll(" ", "-").replaceAll("&", "&amp;") + "/Loan-Offices/" + stateCodeNameMap.get(seoEntity.getLocation()).replaceAll(" ", "-"));
			            } else if ( collectionName.equals( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION ) ) {
			                entry.setPriority( USERS_PRIORITY );
			                entry.setLocation(applicationUrl + "top/" + seoEntity.getVertical().replaceAll(" ", "-").replaceAll("&", "&amp;") + "/Professionals/" + stateCodeNameMap.get(seoEntity.getLocation()).replaceAll(" ", "-"));
			            }
						siteMapUrlList.add(entry);
					}
				} catch(Exception e) {
					LOG.error("error while iterating siteMapEntry " , e);
				}
			}
		}
		return siteMapUrlList;
	}
}