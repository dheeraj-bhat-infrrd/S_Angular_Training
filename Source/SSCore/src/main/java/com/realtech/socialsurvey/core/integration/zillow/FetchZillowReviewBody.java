package com.realtech.socialsurvey.core.integration.zillow;

public class FetchZillowReviewBody
{
    private String partnerId;
    
    private String lenderId;
    
    private String[] fields = {"closeDateSatisfaction","closingCostsSatisfaction","content","dateOfService","details","interestRateSatisfaction","loanPurpose","loanProgram","loanType","rating","serviceProvided","title","zipCode","companyReviewee","created","individualReviewee","response","reviewerName","reviewId","updated"};
    
    private int pageSize = 10;

    public String getPartnerId()
    {
        return partnerId;
    }

    public void setPartnerId( String partnerId )
    {
        this.partnerId = partnerId;
    }

    public String getLenderId()
    {
        return lenderId;
    }

    public void setLenderId( String lenderId )
    {
        this.lenderId = lenderId;
    }

    public String[] getFields()
    {
        return fields;
    }

    public void setFields( String[] fields )
    {
        this.fields = fields;
    }

    public int getPageSize()
    {
        return pageSize;
    }

    public void setPageSize( int pageSize )
    {
        this.pageSize = pageSize;
    }
    

}
