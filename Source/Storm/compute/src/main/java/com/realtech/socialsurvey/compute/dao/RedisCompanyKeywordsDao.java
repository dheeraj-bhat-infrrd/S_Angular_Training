package com.realtech.socialsurvey.compute.dao;

import com.realtech.socialsurvey.compute.entities.Keyword;

import java.util.List;


public interface RedisCompanyKeywordsDao
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

    /**
     * Returns the ssapiretrycount value
     * @return
     */
    public int getSSApiRetryCount();

    /**
     * Sets/Updates ssapiretrycount, ssapibreakerstatus and ssapibreakerontime
     */
    public void setSSApiBreakerStateKeys();

    /**
     *Resets/Updates ssapiretrycount, ssapibreakerstatus and ssapibreakerontime
     */
    public void unsetSSApiBreakerStateKeys();

    /**
     * Gets ssapibreakerstatus
     */
    public String getSSApiBreakerStatus();
}
