package com.realtech.socialsurvey.compute.dao;

import java.util.List;

import com.realtech.socialsurvey.compute.entities.Keyword;


public interface CompanyKeywordsDao
{
    /**
     * DOA method to return list of keywords for company id.
     * @param companyIden
     * @return
     */
    public List<Keyword> getCompanyKeywordsForCompanyId( long companyIden );
}
