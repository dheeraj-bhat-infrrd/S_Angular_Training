package com.realtech.socialsurvey.core.services.reports;

import java.util.List;
import java.util.Map;

import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.LicenseDetail;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;

public interface BillingReportsService
{

    Map<String, List<Object>> generateBillingReportDataForCompanies();

    void generateBillingReportAndMail( Map<String, List<Object>> data, String recipientMailId , String recipientName ) throws InvalidInputException, UndeliveredEmailException;

    Map<String, List<Object>> generateBillingReportDataForACompany( long companyId );

    List<Company> getCompaniesWithExpiredInvoice();

    void updateNextInvoiceBillingDateInLicenceDetail( LicenseDetail licenseDetail );

    /**
     * Method to generate billing report for companies(Invoice)
     */
    public void companiesBillingReportGenerator();

}
