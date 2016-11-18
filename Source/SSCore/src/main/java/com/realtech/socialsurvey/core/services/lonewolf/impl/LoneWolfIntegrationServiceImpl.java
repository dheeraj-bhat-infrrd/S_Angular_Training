package com.realtech.socialsurvey.core.services.lonewolf.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.LoneWolfClassificationCode;
import com.realtech.socialsurvey.core.entities.LoneWolfMember;
import com.realtech.socialsurvey.core.entities.LoneWolfTransaction;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
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
    public List<LoneWolfTransaction> fetchLoneWolfTransactionsData( String secretKey, String apiToken, String clientCode ,Map<String, String> queryParam  ) throws InvalidInputException
    {
        
        LOG.debug( "Method fetchLoneWolfTransactionsData started " );
        
        loneWolfIntegrationApi = loneWolfIntegrationApiBuilder.getLoneWolfIntegrationApi();
        //generating authorization header
        String completeURI = loneWolfRestUtils.addRequestParamInResourceURI( LoneWolfIntegrationApi.loneWolfTransactionUrl,
            queryParam );
        LOG.info( "URI generated for header is : " + completeURI );
        String authHeader = loneWolfRestUtils.generateAuthorizationHeaderFor( completeURI, secretKey, apiToken, clientCode );

        //fetching lone wolf transaction data
        retrofit.client.Response transactionResponse = loneWolfIntegrationApi.fetchClosedTransactions( authHeader,
            loneWolfRestUtils.MD5_EMPTY , queryParam.get( CommonConstants.LONEWOLF_QUERY_PARAM_$TOP ) , queryParam.get( CommonConstants.LONEWOLF_QUERY_PARAM_$FILTER ),  queryParam.get( CommonConstants.LONEWOLF_QUERY_PARAM_$ORDERBY ) ,  queryParam.get( CommonConstants.LONEWOLF_QUERY_PARAM_$SKIP ) );

        String responseString = transactionResponse != null ? new String(
            ( (TypedByteArray) transactionResponse.getBody() ).getBytes() ) : null;
            

        List<LoneWolfTransaction> loneWolfTransactions = responseString != null ? (List<LoneWolfTransaction>) new Gson()
            .fromJson( responseString, new TypeToken<List<LoneWolfTransaction>>() {}.getType() ) : null;

            LOG.debug( "Method fetchLoneWolfTransactionsData ended " );
        return loneWolfTransactions;
    }


    @SuppressWarnings ( "unchecked")
    @Override
    public List<LoneWolfMember> fetchLoneWolfMembersData( String secretKey, String apiToken, String clientCode ) throws InvalidInputException
    {
        
        List<LoneWolfMember> members = new ArrayList<LoneWolfMember>();
        List<LoneWolfMember> membersBatch = null;
        int skip = 0;
        do {
        LOG.info( "Method fetchLoneWolfMembersData started " );
        loneWolfIntegrationApi = loneWolfIntegrationApiBuilder.getLoneWolfIntegrationApi();
        
        Map<String, String> queryParam = new LinkedHashMap<String, String>();
        queryParam.put( CommonConstants.LONEWOLF_QUERY_PARAM_$TOP, String.valueOf(CommonConstants.LONEWOLF_TRANSACTION_API_BATCH_SIZE) );
        queryParam.put( CommonConstants.LONEWOLF_QUERY_PARAM_$ORDERBY, "FirstName+desc" );
        queryParam.put( CommonConstants.LONEWOLF_QUERY_PARAM_$SKIP, String.valueOf(skip) );

        String completeURI = loneWolfRestUtils.addRequestParamInResourceURI( LoneWolfIntegrationApi.loneWolfMemberUrl,
            queryParam );
        //generating authorization header
        String authHeader = loneWolfRestUtils.generateAuthorizationHeaderFor( completeURI,secretKey, apiToken, clientCode );

        //fetching lone wolf members data
        retrofit.client.Response response = loneWolfIntegrationApi.fetchMemberDetails( authHeader, loneWolfRestUtils.MD5_EMPTY ,   String.valueOf(CommonConstants.LONEWOLF_TRANSACTION_API_BATCH_SIZE) , "FirstName+desc" , String.valueOf(skip)  );

        String responseString = response != null ? new String( ( (TypedByteArray) response.getBody() ).getBytes() ) : null;
        membersBatch = responseString != null ? (List<LoneWolfMember>) new Gson().fromJson( responseString,
            new TypeToken<List<LoneWolfMember>>() {}.getType() ) : null;

            members.addAll( membersBatch );
            skip += CommonConstants.LONEWOLF_TRANSACTION_API_BATCH_SIZE;
        } while ( membersBatch != null
            && membersBatch.size() == CommonConstants.LONEWOLF_TRANSACTION_API_BATCH_SIZE );
            LOG.info( "Method fetchLoneWolfMembersData ended " );
        
            return members;
    }
    
    
    @SuppressWarnings ( "unchecked")
    @Override
    public List<LoneWolfClassificationCode> fetchLoneWolfClassificationCodes( String secretKey, String apiToken, String clientCode ) throws InvalidInputException
    {
        LOG.info( "Method fetchLoneWolfMembersData started " );
        loneWolfIntegrationApi = loneWolfIntegrationApiBuilder.getLoneWolfIntegrationApi();
        //generating authorization header
        String authHeader = loneWolfRestUtils.generateAuthorizationHeaderFor(
            LoneWolfIntegrationApi.loneWolfClassificationCodesUrl, secretKey, apiToken, clientCode );

        //fetching lone wolf members data
        retrofit.client.Response response = loneWolfIntegrationApi.fetchClassificationCodes( authHeader,
            loneWolfRestUtils.MD5_EMPTY );

        String responseString = response != null ? new String( ( (TypedByteArray) response.getBody() ).getBytes() ) : null;


        List<LoneWolfClassificationCode> classificationCodes = null;
        classificationCodes = responseString != null ? (List<LoneWolfClassificationCode>) new Gson().fromJson( responseString,
            new TypeToken<List<LoneWolfClassificationCode>>() {}.getType() ) : null;

        //select classification by LWclientcode and InactiveDate
        Iterator<LoneWolfClassificationCode> iterator = classificationCodes.iterator();
        while ( iterator.hasNext() ) {
            LoneWolfClassificationCode classificationCode = iterator.next();
            if ( ! classificationCode.getLWCompanyCode().equals( clientCode ) || classificationCode.getInactiveDate() != null )
                iterator.remove();
        }
        LOG.info( "Method fetchLoneWolfMembersData ended " );

        return classificationCodes;
    }


    @Override
    public Response testLoneWolfCompanyCredentials( String secretKey, String apiToken, String clientCode )
    {
        loneWolfIntegrationApi = loneWolfIntegrationApiBuilder.getLoneWolfIntegrationApi();
        //generating authorization header
        String authHeader = loneWolfRestUtils.generateAuthorizationHeaderFor( LoneWolfIntegrationApi.loneWolfTestConnectionUrl,
            secretKey, apiToken, clientCode );
        //calling get test transaction for id = test
        Response transactionResponse = loneWolfIntegrationApi.testConnection( authHeader,
            loneWolfRestUtils.MD5_EMPTY );
        LOG.debug( "Test connection response: " + transactionResponse );
        
        return transactionResponse;
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
