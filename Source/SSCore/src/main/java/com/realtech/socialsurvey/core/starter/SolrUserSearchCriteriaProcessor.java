package com.realtech.socialsurvey.core.starter;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.CompanyHiddenNotification;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.batchtracker.BatchTrackerService;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;


@Component
public class SolrUserSearchCriteriaProcessor
{
    public static final Logger LOG = LoggerFactory.getLogger( SolrUserSearchCriteriaProcessor.class );


    @Autowired
    private OrganizationManagementService organizationManagementService;

    @Autowired
    private BatchTrackerService batchTrackerService;

    @Autowired
    private SolrSearchService solrSearchService;


    
    public void solrAgentSearchCriteriaUpdator()
    {
        try {
            //update last run start time
            batchTrackerService.getLastRunEndTimeAndUpdateLastStartTimeByBatchType(
                CommonConstants.BATCH_TYPE_HIDE_USERS_OF_HIDDEN_COMPANIES_IN_SOLR,
                CommonConstants.BATCH_NAME_HIDE_USERS_OF_HIDDEN_COMPANIES_IN_SOLR );

            List<CompanyHiddenNotification> hiddenCompanyRecords = organizationManagementService
                .getCompaniesWithHiddenSectionEnabled();
            if ( hiddenCompanyRecords != null && !hiddenCompanyRecords.isEmpty() ) {
                LOG.debug( "Found " + hiddenCompanyRecords.size() + " to process" );
                for ( CompanyHiddenNotification record : hiddenCompanyRecords ) {
                    LOG.debug( "Processing for Company for the record: " + record.getCompanyHiddenNotificationId() );
                    if ( record.getCompany() != null ) {
                        solrSearchService.showOrHideUsersOfCompanyInSolr( record.getCompany().getCompanyId(),
                            record.isHidden() );
                    }
                    organizationManagementService.deleteCompanyHiddenNotificationRecord( record );
                }
            } else {
                LOG.info( "There are no Companies to mofified" );
            }

            //Update last build time in batch tracker table
            batchTrackerService
                .updateLastRunEndTimeByBatchType( CommonConstants.BATCH_TYPE_HIDE_USERS_OF_HIDDEN_COMPANIES_IN_SOLR );
        } catch ( Exception error ) {
            LOG.error( "Error in SolrUserSearchCriteriaProcessor", error );
            try {
                //update batch tracker with error message
                batchTrackerService.updateErrorForBatchTrackerByBatchType(
                    CommonConstants.BATCH_TYPE_HIDE_USERS_OF_HIDDEN_COMPANIES_IN_SOLR, error.getMessage() );
                //send report bug mail to admin
                batchTrackerService.sendMailToAdminRegardingBatchError(
                    CommonConstants.BATCH_NAME_HIDE_USERS_OF_HIDDEN_COMPANIES_IN_SOLR, System.currentTimeMillis(), error );
            } catch ( NoRecordsFetchedException | InvalidInputException nestedError ) {
                LOG.error( "Error while updating error message in SolrUserSearchCriteriaProcessor " );
            } catch ( UndeliveredEmailException nestedError ) {
                LOG.error( "Error while sending report excption mail to admin " );
            }
        }
    }

}
