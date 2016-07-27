package com.realtech.socialsurvey.core.services.lonewolf;

import java.util.List;

import com.realtech.socialsurvey.core.entities.LoneWolfMember;
import com.realtech.socialsurvey.core.entities.LoneWolfTransaction;

public interface LoneWolfIntegrationService
{
    
    List<LoneWolfTransaction> fetchLoneWolfTransactionsData( String secretKey, String apiToken, String clientCode );
    
    List<LoneWolfMember> fetchLoneWolfMembersData( String secretKey, String apiToken, String clientCode );

}
