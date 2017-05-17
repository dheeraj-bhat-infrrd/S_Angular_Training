package com.realtech.socialsurvey.core.integration.zillow;

import com.realtech.socialsurvey.core.entities.LenderRef;

public class FetchZillowReviewBodyByNMLS {
	
	private String partnerId;
    
    private LenderRef lenderRef;
    
    private String[] fields = {"closeDateSatisfaction","closingCostsSatisfaction","content","dateOfService","details","interestRateSatisfaction","loanPurpose","loanProgram","loanType","rating","serviceProvided","title","zipCode","companyReviewee","created","individualReviewee","response","reviewerName","reviewId","updated"};
    
    private int pageSize = 50;

    public String getPartnerId()
    {
        return partnerId;
    }

    public void setPartnerId( String partnerId )
    {
        this.partnerId = partnerId;
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

	public LenderRef getLenderRef() 
	{
		return lenderRef;
	}

	public void setLenderRef(LenderRef lenderRef) 
	{
		this.lenderRef = lenderRef;
	}   

}
