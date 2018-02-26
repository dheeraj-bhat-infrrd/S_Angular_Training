/**
 * 
 */
package com.realtech.socialsurvey.compute.dao;

import org.apache.solr.client.solrj.response.QueryResponse;

/**
 * @author Subhrajit
 *
 */
public interface SolrEmailCountDao {
	
	public QueryResponse getEmailCountForDateRange(String mailType, String startDate, String endDate);

}
