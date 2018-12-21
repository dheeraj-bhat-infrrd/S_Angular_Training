package com.realtech.socialsurvey.core.starter;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.searchengine.SearchEngineManagementServices;


//One time job
public class UpdateLocation extends QuartzJobBean
{
	public static final Logger LOG = LoggerFactory.getLogger(UpdateLocation.class);
	
	private SearchEngineManagementServices searchEngineManagementServices;
	private OrganizationManagementService organizationManagementService;
	
	@Override
    protected void executeInternal( JobExecutionContext jobExecutionContext )
    {
        LOG.info( "Executing UpdateLocation batch" );
        // initialize the dependencies
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        for ( Company company : organizationManagementService.getAllCompanies() ) 
        {
        	LOG.debug( "Updating location for company with id {}", company.getCompanyId());
        	searchEngineManagementServices.updateHierarchyAddressForCompany(company.getCompanyId());
        	LOG.debug( "Updating location finished for company with id {}",company.getCompanyId());
        }
        searchEngineManagementServices.updatelocForUsersWithLatLngNotUpdated();
    }


    private void initializeDependencies( JobDataMap jobMap )
    {
    	organizationManagementService = (OrganizationManagementService) jobMap.get("organizationManagementService");
    	searchEngineManagementServices = (SearchEngineManagementServices) jobMap.get( "searchEngineManagementServices" );
    }
}
