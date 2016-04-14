package com.realtech.socialsurvey.core.dao;

import java.sql.Timestamp;
import java.util.List;

import com.realtech.socialsurvey.core.entities.BillingReportData;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

/*
 * This interface contains methods which are required for queries and criteria on Branch table.
 */
public interface CompanyDao extends GenericDao<Company, Long> {

	public List<Company> searchBetweenTimeIntervals(Timestamp lowerTime, Timestamp higherTime);
	
	public List<Company> searchCompaniesByName(String namePattern);

	public List<Company> searchCompaniesByNameAndKeyValue(String namePattern, int accountType, int status , boolean inCompleteCompany , Timestamp startDate);

	public List<Company> getCompaniesByDateRange(Timestamp startTime, Timestamp endTime);

    public List<Object[]> getUserAdoptionData( long companyId ) throws InvalidInputException;

    List<BillingReportData> getAllUsersInCompanysForBillingReport( int startIndex, int batchSize );

    List<BillingReportData> getAllUsersInGivenCompaniesForBillingReport( int startIndex, int batchSize, Long companyId );

    List<Company> getCompaniesWithExpiredInvoice();

    Company getCompanyByBraintreeSubscriptionId( String subscriptionId );

    List<Company> getAllInvoicedActiveCompanies();

    List<Company> getCompaniesByBillingModeAuto();

}
