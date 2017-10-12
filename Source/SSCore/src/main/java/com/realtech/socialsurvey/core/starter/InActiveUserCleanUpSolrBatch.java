package com.realtech.socialsurvey.core.starter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;

public class InActiveUserCleanUpSolrBatch  extends QuartzJobBean
{

    
    private UserManagementService userManagementService; 
    private EmailServices emailServices;

    
    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext ) throws JobExecutionException
    {

        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        
        //final String SS_USERS = "http://localhost:8983/solr/ss-users/";
        final String SS_USERS = "http://172.30.0.139:8983/solr/ss-users/";
        
        SolrServer solrServer = null;
        
        String solrUserURL = SS_USERS;
        Set<Long> userIds = new HashSet<Long>();
        
        
        solrServer = new HttpSolrServer( solrUserURL );
        for(long userId : userIds){
            try {
                solrServer.deleteById( String.valueOf( userId ) );
                solrServer.commit();
            } catch ( SolrServerException | IOException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
       
        
        try {
            emailServices.sendReportBugMailToAdmin( "Admin", "batch to delete inactive users from solr Successfully finished" , "patidar@infrrd.ai" );
        } catch ( InvalidInputException | UndeliveredEmailException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

       
    }

    
    
    private void initializeDependencies( JobDataMap jobMap )
    {
        userManagementService = (UserManagementService) jobMap.get( "userManagementService" );
        emailServices = (EmailServices) jobMap.get( "emailServices" ); 
    }
}
