/**
 * 
 */
package com.realtech.socialsurvey.compute.dao.impl;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.common.ComputeConstants;
import com.realtech.socialsurvey.compute.dao.SolrEmailCountDao;

/**
 * @author Subhrajit
 *
 */
public class SolrEmailCountDaoImpl implements SolrEmailCountDao {

	private static final Logger LOG = LoggerFactory.getLogger(SolrEmailCountDaoImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.realtech.socialsurvey.compute.dao.SolrEmailCountDao#
	 * getEmailCountForDateRange(java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public QueryResponse getEmailCountForDateRange(String mailType, String startDate, String endDate) {

		SolrServer solrServer = new HttpSolrServer("http://localhost:8983/solr/ss-emails/");
		SolrQuery solrQuery = new SolrQuery().setFacet(true).setRows(0);
		solrQuery.setQuery("*:*");
		solrQuery.addFilterQuery("agentId : [1 TO *]");
		solrQuery.addFilterQuery("emailAttemptedDate : [" + startDate + " TO " + endDate + "]");
		solrQuery.addFacetField("agentId");
		solrQuery.addFacetPivotField(ComputeConstants.SOLR_PIVOT_AGENT_EMAIL_ATTEMPT)
				 .addFacetPivotField(ComputeConstants.SOLR_PIVOT_AGENT_DELIVERED)
				 .addFacetPivotField(ComputeConstants.SOLR_PIVOT_AGENT_DIFFERED)
				 .addFacetPivotField(ComputeConstants.SOLR_PIVOT_AGENT_BLOCKED)
				 .addFacetPivotField(ComputeConstants.SOLR_PIVOT_AGENT_OPENED)
				 .addFacetPivotField(ComputeConstants.SOLR_PIVOT_AGENT_SPAMED)
				 .addFacetPivotField(ComputeConstants.SOLR_PIVOT_AGENT_UNSUBSCRIBED)
				 .addFacetPivotField(ComputeConstants.SOLR_PIVOT_AGENT_BOUNCED)
				 .addFacetPivotField(ComputeConstants.SOLR_PIVOT_AGENT_LINK_CLICKED);
		QueryResponse response = null;
		try {
			response = solrServer.query(solrQuery);
		} catch (SolrServerException sse) {
			LOG.error("Query to solr failed", sse);
		}
		return response;
	}

}
