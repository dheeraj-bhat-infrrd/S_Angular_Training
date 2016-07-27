package com.realtech.socialsurvey.core.services.lonewolf.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import retrofit.mime.TypedByteArray;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.realtech.socialsurvey.core.entities.LoneWolfMember;
import com.realtech.socialsurvey.core.entities.LoneWolfTransaction;
import com.realtech.socialsurvey.core.integration.lonewolf.LoneWolfIntegrationApi;
import com.realtech.socialsurvey.core.integration.lonewolf.LoneWolfIntergrationApiBuilder;
import com.realtech.socialsurvey.core.services.lonewolf.LoneWolfIntegrationService;
import com.realtech.socialsurvey.core.utils.LoneWolfRestUtils;


@Component
public class LoneWolfIntegrationServiceImpl implements LoneWolfIntegrationService
{

    Logger LOG = LoggerFactory.getLogger( LoneWolfIntegrationServiceImpl.class );

    @Autowired
    private LoneWolfRestUtils loneWolfRestUtils;
    @Autowired
    private LoneWolfIntergrationApiBuilder loneWolfIntegrationApiBuilder;

    private LoneWolfIntegrationApi loneWolfIntegrationApi = null;


    @SuppressWarnings ( "unchecked")
    @Override
    public List<LoneWolfTransaction> fetchLoneWolfTransactionsData( String secretKey, String apiToken, String clientCode )
    {
        loneWolfIntegrationApi = loneWolfIntegrationApiBuilder.getLoneWolfIntegrationApi();
        //generating authorization header
        String authHeader = loneWolfRestUtils.generateAuthorizationHeaderFor( LoneWolfIntegrationApi.loneWolfTransactionUrl,
            secretKey, apiToken, clientCode );

        //fetching lone wolf transaction data
        retrofit.client.Response transactionResponse = loneWolfIntegrationApi.fetchClosedTransactions( authHeader,
            loneWolfRestUtils.MD5_EMPTY );

        String responseString = transactionResponse != null ? new String(
            ( (TypedByteArray) transactionResponse.getBody() ).getBytes() ) : null;

        List<LoneWolfTransaction> loneWolfTransactions = responseString != null ? (List<LoneWolfTransaction>) new Gson()
            .fromJson( responseString, new TypeToken<List<LoneWolfTransaction>>() {}.getType() ) : null;

        return loneWolfTransactions;
    }


    @SuppressWarnings ( "unchecked")
    @Override
    public List<LoneWolfMember> fetchLoneWolfMembersData( String secretKey, String apiToken, String clientCode )
    {
        loneWolfIntegrationApi = loneWolfIntegrationApiBuilder.getLoneWolfIntegrationApi();
        //generating authorization header
        String authHeader = loneWolfRestUtils.generateAuthorizationHeaderFor( LoneWolfIntegrationApi.loneWolfMemberUrl,
            secretKey, apiToken, clientCode );

        //fetching lone wolf members data
        retrofit.client.Response response = loneWolfIntegrationApi.fetchMemberDetails( authHeader, loneWolfRestUtils.MD5_EMPTY );

        String responseString = response != null ? new String( ( (TypedByteArray) response.getBody() ).getBytes() ) : null;
        List<LoneWolfMember> members = responseString != null ? (List<LoneWolfMember>) new Gson().fromJson( responseString,
            new TypeToken<List<LoneWolfMember>>() {}.getType() ) : null;

        return members;
    }


    public LoneWolfRestUtils getLoneWolfRestUtils()
    {
        return loneWolfRestUtils;
    }


    public void setLoneWolfRestUtils( LoneWolfRestUtils loneWolfRestUtils )
    {
        this.loneWolfRestUtils = loneWolfRestUtils;
    }


    public LoneWolfIntergrationApiBuilder getLoneWolfIntegrationApiBuilder()
    {
        return loneWolfIntegrationApiBuilder;
    }


    public void setLoneWolfIntegrationApiBuilder( LoneWolfIntergrationApiBuilder loneWolfIntegrationApiBuilder )
    {
        this.loneWolfIntegrationApiBuilder = loneWolfIntegrationApiBuilder;
    }


    public LoneWolfIntegrationApi getLoneWolfIntegrationApi()
    {
        return loneWolfIntegrationApi;
    }


    public void setLoneWolfIntegrationApi( LoneWolfIntegrationApi loneWolfIntegrationApi )
    {
        this.loneWolfIntegrationApi = loneWolfIntegrationApi;
    }

}
