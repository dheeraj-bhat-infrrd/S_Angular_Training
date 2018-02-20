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
		List<SurveyInvitationEmailCountMonth> mailCount = new ArrayList<SurveyInvitationEmailCountMonth>();
		SurveyInvitationEmailCountMonth month = new SurveyInvitationEmailCountMonth();
		month.setAgentId(1);
		month.setAttempted(10);
		mailCount.add(month);
		
		for(SurveyInvitationEmailCountMonth months : mailCount) {
			if(months.getAgentId()==1) {
				months.setBlocked(20);
			}
		}
		System.out.println(mailCount.get(0).getBlocked());
		System.out.println(mailCount.get(0).getAttempted());
		
	}
}
