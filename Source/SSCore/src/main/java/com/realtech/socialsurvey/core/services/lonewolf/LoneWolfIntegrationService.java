package com.realtech.socialsurvey.core.services.lonewolf;

import java.util.List;
import java.util.Map;

import retrofit.client.Response;

import com.realtech.socialsurvey.core.entities.LoneWolfMember;
import com.realtech.socialsurvey.core.entities.LoneWolfTransaction;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

public interface LoneWolfIntegrationService
{
    
    List<LoneWolfTransaction> fetchLoneWolfTransactionsData( String secretKey, String apiToken, String clientCode, Map<String, String> queryParam  ) throws InvalidInputException;
    
    List<LoneWolfMember> fetchLoneWolfMembersData( String secretKey, String apiToken, String clientCode );
    
    Response testLoneWolfCompanyCredentials( String secretKey, String apiToken, String clientCode );

}
