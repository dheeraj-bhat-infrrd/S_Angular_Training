package com.realtech.socialsurvey.core.starter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.realtech.socialsurvey.core.commons.CommonConstants;

public class SolrUserManipulation {
	
	private static final Logger LOG = LoggerFactory.getLogger(SolrUserManipulation.class);
	
	private static final String SOLR_LOCAL_URL = "http://localhost:8983/solr/ss-users/";
	private static final String SOLR_DEMO_URL = "http://localhost:8983/solr/ss-users/";
	private static final String SOLR_PROD_URL = "http://localhost:8983/solr/ss-users/";
	
	/***
	 * method to trim the first and last name of users in solr
	 * @param args
	 */
	public static void main(String[] args) {

		
		LOG.debug("main method started to trim the first and last name of users in solr");
		String solrUserPackageURL = null;
		if(args.length > 0){
			if(args[0].equals("dev")){
				solrUserPackageURL = SOLR_LOCAL_URL;
			}else if(args[0].equals("demo")){
				solrUserPackageURL = SOLR_DEMO_URL;
			}else if(args[0].equals("prod")){
				solrUserPackageURL = SOLR_PROD_URL;
			}
		}else{
			solrUserPackageURL = SOLR_LOCAL_URL;
		}
		int maxRow = 10000;
		String SOLR_EDIT_REPLACE = "set";

		try {
			SolrServer solrServer = new HttpSolrServer(solrUserPackageURL);
			SolrQuery solrQuery = new SolrQuery(CommonConstants.USER_ID_SOLR + ":*");
			solrQuery.setRows(maxRow);
			QueryResponse response = solrServer.query(solrQuery);
			SolrDocumentList result = response.getResults();

			if (result != null) {
				for (SolrDocument document : result) {
					Long userId = (Long) document.getFieldValue(CommonConstants.USER_ID_SOLR);
					String firstName = (String) document.getFieldValue(CommonConstants.USER_FIRST_NAME_SOLR);
					String lastName = (String) document.getFieldValue(CommonConstants.USER_LAST_NAME_SOLR);
					
					boolean updatingRequired = false;

					// create input document
					SolrInputDocument ipDocument = new SolrInputDocument();
					ipDocument.setField(CommonConstants.USER_ID_SOLR, userId);

					// check if first name is not null and and eligible for trim()
					if (firstName != null) {
						if (Character.isWhitespace(firstName.charAt(0)) || Character.isWhitespace(firstName.charAt(firstName.length() - 1))) {
							Map<String, String> firstNameEditKeyValues = new HashMap<String, String>();
							firstNameEditKeyValues.put(SOLR_EDIT_REPLACE, firstName.trim());
							ipDocument.setField(CommonConstants.USER_FIRST_NAME_SOLR, firstNameEditKeyValues);
							updatingRequired = true;
						}
					}

					// check if last name is not null and and eligible for trim()
					if (lastName != null) {
						if (Character.isWhitespace(lastName.charAt(0)) || Character.isWhitespace(lastName.charAt(lastName.length() - 1))) {
							Map<String, String> lastNameEditKeyValues = new HashMap<String, String>();
							lastNameEditKeyValues.put(SOLR_EDIT_REPLACE,lastName.trim());
							ipDocument.setField(CommonConstants.USER_LAST_NAME_SOLR, lastNameEditKeyValues);
							updatingRequired = true;
						}
					}

					if (updatingRequired) {
						LOG.info("updating user record with id : " + userId);
						solrServer.add(ipDocument);
						solrServer.commit();
					}
				}
			}
		} catch (SolrServerException | IOException e) {
			LOG.error("SolrServerException while updating user record " + e);
		}
		LOG.debug("main method ended");
	}
}
