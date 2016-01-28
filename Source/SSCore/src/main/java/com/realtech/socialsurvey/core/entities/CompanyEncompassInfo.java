package com.realtech.socialsurvey.core.entities;

public class CompanyEncompassInfo
{
    String companyName;
    EncompassCrmInfo encompassCrmInfo;
    /**
     * @return the companyName
     */
    public String getCompanyName()
    {
        return companyName;
    }
    /**
     * @param companyName the companyName to set
     */
    public void setCompanyName( String companyName )
    {
        this.companyName = companyName;
    }
    /**
     * @return the encompassCrmInfo
     */
    public EncompassCrmInfo getEncompassCrmInfo()
    {
        return encompassCrmInfo;
    }
    /**
     * @param encompassCrmInfo the encompassCrmInfo to set
     */
    public void setEncompassCrmInfo( EncompassCrmInfo encompassCrmInfo )
    {
        this.encompassCrmInfo = encompassCrmInfo;
    }
}
