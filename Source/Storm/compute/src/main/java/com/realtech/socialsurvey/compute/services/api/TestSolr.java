package com.realtech.socialsurvey.compute.services.api;

import java.util.ArrayList;
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
import com.realtech.socialsurvey.compute.entity.SurveyInvitationEmailCountMonth;
import com.realtech.socialsurvey.compute.enums.ReportType;
import com.realtech.socialsurvey.compute.utils.ConversionUtils;

import retrofit2.Call;
import retrofit2.Response;

public class TestSolr {

	public static void main(String[] args) throws Exception {
		Call<String> request = RetrofitApiBuilder.apiBuilderInstance().getSolrAPIIntergrationService().getEmailCounts("*:*", 0, "agentId : [1 TO *]",
				"json", "true", "agentId", "agentId,emailAttemptedDate");
		Response<String> response = request.execute();
		System.out.println(response.body());
	}
}
