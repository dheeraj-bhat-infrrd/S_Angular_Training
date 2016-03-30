package com.realtech.socialsurvey.core.services.reports;

import java.util.List;

import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.CompanyReportsSearch;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;


/**
 * Generates reports from the admin panel
 *
 */
public interface AdminReports {

	/**
	 * Gets company related data
	 * @param search
	 * @return
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 */
	public List<Company> companyCreationReports(CompanyReportsSearch search) throws InvalidInputException, NoRecordsFetchedException;

    /**
     * Method to create an entry in the file upload table for billing report
     */
    public void createEntryInFileUploadForBillingReport( String mailId );

    /**
     * 
     * @param mailId
     * @param companyId
     * @throws InvalidInputException 
     */
    public void createEntryInFileUploadForCompanyUserReport( String mailId, long companyId ) throws InvalidInputException;


    /**
     * Method to create an entry in the file upload table for company hierarchy report
     * @param mailId
     * @param companyId
     * @throws InvalidInputException
     */
    public void createEntryInFileUploadForCompanyHierarchyReport( String mailId, long companyId ) throws InvalidInputException;
}
