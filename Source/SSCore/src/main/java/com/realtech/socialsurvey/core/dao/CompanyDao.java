package com.realtech.socialsurvey.core.dao;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.realtech.socialsurvey.core.entities.BillingReportData;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

/*
 * This interface contains methods which are required for queries and criteria on Branch table.
 */
public interface CompanyDao extends GenericDao<Company, Long> {

	public List<Company> searchBetweenTimeIntervals(Timestamp lowerTime, Timestamp higherTime);
	
	public List<Object[]> searchCompaniesByName(String namePattern);

	public List<Long> searchCompaniesByNameAndKeyValue(String namePattern, int accountType, int status , boolean inCompleteCompany , Timestamp startDate);

	public List<Company> getCompaniesByDateRange(Timestamp startTime, Timestamp endTime);

    public List<Object[]> getUserAdoptionData( long companyId ) throws InvalidInputException;

    List<BillingReportData> getAllUsersInCompanysForBillingReport( int startIndex, int batchSize );

    List<BillingReportData> getAllUsersInGivenCompaniesForBillingReport( int startIndex, int batchSize, Long companyId );

    List<Company> getCompaniesWithExpiredInvoice();

    Company getCompanyByBraintreeSubscriptionId( String subscriptionId );

    List<Company> getAllInvoicedActiveCompanies();

    List<Company> getCompaniesByBillingModeAuto();

    Map<Long , Company> getCompaniesByIds( Set<Long> ids );

    List<Company> getCompaniesByStatusAndAccountMasterId( int status, int accountMasterId );

    List<Company> getCompanyListByIds( Set<Long> companyIds );

	public List<Long> filterIdsByStatus(List<Long> companies, List<Integer> asList);

}
