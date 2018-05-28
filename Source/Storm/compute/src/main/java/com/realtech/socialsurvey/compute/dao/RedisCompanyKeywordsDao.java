package com.realtech.socialsurvey.compute.dao;

import java.io.Serializable;
import java.util.List;

import com.realtech.socialsurvey.compute.entities.Keyword;


public interface RedisCompanyKeywordsDao extends Serializable
{
    /**
     * DOA method to return list of keywords for company id.
     * @param companyIden
     * @return
     */
    public List<Keyword> getCompanyKeywordsForCompanyId( long companyIden );

    /**
     * Method to get modified on for keywords
     * @param companyIden
     * @return
     */
    public long getKeywordModifiedOn( long companyIden );

}
