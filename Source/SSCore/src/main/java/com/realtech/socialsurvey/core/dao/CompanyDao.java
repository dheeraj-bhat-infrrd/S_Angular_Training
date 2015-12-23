package com.realtech.socialsurvey.core.dao;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.realtech.socialsurvey.core.entities.BillingReportData;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

/*
 * This interface contains methods which are required for queries and criteria on Branch table.
 */
public interface CompanyDao extends GenericDao<Company, Long> {

	public List<Company> searchBetweenTimeIntervals(Timestamp lowerTime, Timestamp higherTime);
	
	public List<Company> searchCompaniesByName(String namePattern);

	public List<Company> searchCompaniesByNameAndKeyValue(String namePattern, int accountType, int status);

	public List<Company> getCompaniesByDateRange(Timestamp startTime, Timestamp endTime);

    public List<Object[]> getAllUsersAndAdminsUnderACompanyGroupedByBranches( long companyId ) throws InvalidInputException;

    public Map<Long, Integer> getAllActiveUsersAndAdminsUnderACompanyGroupedByBranches( long companyId ) throws InvalidInputException;

    List<BillingReportData> getAllUsersInCompanyForBillingReport( long companyId );

}
