package com.realtech.socialsurvey.core.starter;

import java.util.List;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.batchtracker.BatchTrackerService;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;


public class SolrUserSchemaManipulation extends QuartzJobBean
{

    private static final Logger LOG = LoggerFactory.getLogger( SolrUserSchemaManipulation.class );
    private SolrSearchService solrSearchService;
    private OrganizationManagementService organizationManagementService;
    private BatchTrackerService batchTrackerService;


    private void initializeDependencies( JobDataMap jobMap )
    {
        solrSearchService = (SolrSearchService) jobMap.get( "solrSearchService" );
        organizationManagementService = (OrganizationManagementService) jobMap.get( "organizationManagementService" );
        batchTrackerService = (BatchTrackerService) jobMap.get( "batchTrackerService" );
    }


    @Override
    protected void executeInternal( JobExecutionContext context ) throws JobExecutionException
    {
        LOG.info( "Adding Hidden boolean for users in solr" );
        int startIndex = 0;
        int batchSize = 500;
        SolrDocumentList solrDocumentList;
        initializeDependencies( context.getMergedJobDataMap() );

        //update last run start time
        batchTrackerService.getLastRunEndTimeAndUpdateLastStartTimeByBatchType(
            CommonConstants.BATCH_TYPE_SOLR_SCHEMA_MANIPULATION, CommonConstants.BATCH_NAME_SOLR_SCHEMA_MANIPULATION );

        //getting the list of company Ids with Hidden Section as True
        List<Long> hiddenCompanyList = organizationManagementService
            .fetchEntityIdsWithHiddenAttribute( MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );
        if ( hiddenCompanyList != null ) {
            //setting boolean in solr for each user
            try {
                do {
                    solrDocumentList = solrSearchService.getAllUsers( startIndex, batchSize );
                    if ( solrDocumentList != null ) {
                        for ( SolrDocument document : solrDocumentList ) {
                            Long userId = (Long) document.getFieldValue( CommonConstants.USER_ID_SOLR );
                            Long CompanyId = (Long) document.getFieldValue( CommonConstants.COMPANY_ID_SOLR );
                            // Adding fields to be updated                            
                            if ( hiddenCompanyList.contains( CompanyId ) ) {
                                solrSearchService.editUserInSolr( userId, CommonConstants.USER_IS_HIDDEN_FROM_SEARCH_SOLR,
                                    "true" );
                            } else {
                                solrSearchService.editUserInSolr( userId, CommonConstants.USER_IS_HIDDEN_FROM_SEARCH_SOLR,
                                    "false" );
                            }
                        }
                    }
                    startIndex += batchSize;
                } while ( solrDocumentList != null && solrDocumentList.size() == batchSize );
                batchTrackerService.getLastRunEndTimeByBatchType( CommonConstants.BATCH_TYPE_SOLR_SCHEMA_MANIPULATION );
            } catch ( SolrException | NoRecordsFetchedException | InvalidInputException exception ) {
                try {
                    //update batch tracker with error message
                    batchTrackerService.updateErrorForBatchTrackerByBatchType(
                        CommonConstants.BATCH_TYPE_SOLR_SCHEMA_MANIPULATION, exception.getMessage() );
                    //send report bug mail to admin
                    batchTrackerService.sendMailToAdminRegardingBatchError( CommonConstants.BATCH_NAME_SOLR_SCHEMA_MANIPULATION,
                        System.currentTimeMillis(), exception );
                } catch ( NoRecordsFetchedException | InvalidInputException | UndeliveredEmailException except ) {
                    LOG.debug( "Error while executing solr schema changes" + except.getMessage() );
                }
            }
        }
        LOG.info( "Added Hidden boolean for users in solr" );
    }
}
