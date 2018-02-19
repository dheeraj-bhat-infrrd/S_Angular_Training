package com.realtech.socialsurvey.compute.services.api;

import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.PivotField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;

import com.realtech.socialsurvey.compute.common.RetrofitApiBuilder;
import com.realtech.socialsurvey.compute.entities.ReportRequest;
import com.realtech.socialsurvey.compute.enums.ReportType;
import com.realtech.socialsurvey.compute.utils.ConversionUtils;

import retrofit2.Call;
import retrofit2.Response;

public class TestSolr {

	public static void main(String[] args) throws Exception {
        boolean success = false;

        // get the report request from the tuple
        QueryResponse response = null;
            //convert the startDate and endDate to GMT
            /*String startDateInGmt = getStartDateTimeInGmt( reportRequest.getStartDateExpectedTimeZone() );
            String endDateInGmt = getEndDateTimeInGmt( reportRequest.getEndDateExpectedTimeZone() );*/
            
            String startDateInGmt = "2018-01-01T00:00:00Z";
            String endDateInGmt = "2018-02-28T23:59:59Z";

            SolrServer solrServer = new HttpSolrServer("http://localhost:8983/solr/ss-emails/");
            SolrQuery solrQuery = new SolrQuery().setFacet(true).setRows(0);
            solrQuery.setQuery("*:*");
    		solrQuery.addFilterQuery("agentId : [1 TO *]");
    		solrQuery.addFilterQuery("emailAttemptedDate : ["+startDateInGmt+" TO "+endDateInGmt+"]");
    		solrQuery.addFacetField("agentId");
    		solrQuery.addFacetPivotField("agentId,emailAttemptedDate");
    		
    		try {
				response = solrServer.query( solrQuery );
			} catch (SolrServerException e) {
				e.printStackTrace();
			}
    		//System.out.println(response.getResults());
    		getResult(response.getFacetPivot());
	}

	private static void getResult(NamedList<List<PivotField>> namedList) {
		// TODO Auto-generated method stub
		PivotField pf = namedList.get("agentId,emailAttemptedDate").get(0);
		System.out.println("count : "+pf.getCount()+" field : "+pf.getField()+" value : "+pf.getValue());
		
	}

}
