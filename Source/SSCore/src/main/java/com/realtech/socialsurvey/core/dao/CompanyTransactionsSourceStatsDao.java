package com.realtech.socialsurvey.core.dao;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.realtech.socialsurvey.core.entities.CompanyTransactionsSourceStats;

/**
 * 
 * @author rohit
 *
 */
public interface CompanyTransactionsSourceStatsDao extends GenericReportingDao<CompanyTransactionsSourceStats, String>
{
    /**
     * 
     * @param companyId
     * @param transactionDate
     * @return
     */
    public  Map<Long, Long> getTransactionsByCompanyIdAndAfterTransactionDate( Date transactionDate );

    public List<CompanyTransactionsSourceStats> getTransactionsCountForCompanyForPastNDays( long companyId, Date startDate,
        Date endDate );

}
