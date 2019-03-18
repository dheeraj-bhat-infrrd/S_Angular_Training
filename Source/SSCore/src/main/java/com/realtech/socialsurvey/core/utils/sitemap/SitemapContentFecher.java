package com.realtech.socialsurvey.core.utils.sitemap;

import java.util.List;
import com.realtech.socialsurvey.core.entities.SiteMapEntry;

/**
 * Queries the data store to fetch the content for sitemap
 *
 */
public interface SitemapContentFecher {
	
	/**
	 * Gets the content for sitemap
	 * @return
	 */
	public List<SiteMapEntry> getInitialContent();
	
	/**
	 * Set the flag to check if there is more data
	 * @return
	 */
	public boolean hasNext();
	
	/**
	 * Gets the next set of records
	 * @return
	 */
	public List<SiteMapEntry> nextBatch();
	
	/**
	 * Gets the content for seo sitemap
	 * @param locationType 
	 * @return
	 */
	public List<SiteMapEntry> getInitialSEOContent(String locationType);
	
	/**
	 * @param locatioType
	 * @return
	 */
	public List<SiteMapEntry> nextSEOBatch(String locatioType);
}
