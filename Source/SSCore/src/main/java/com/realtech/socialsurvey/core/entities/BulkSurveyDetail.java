package com.realtech.socialsurvey.core.entities;


public class BulkSurveyDetail
{

    private String customerFirstName;

    private String customerLastName;

    private String customerEmailId;

    private String agentFirstName;

    private String agentLastName;

    private String agentEmailId;

    private String loanClosedDate;

    private String status;

    private String reason;


    public String getCustomerFirstName()
    {
        return customerFirstName;
    }


    public void setCustomerFirstName( String customerFirstName )
    {
        this.customerFirstName = customerFirstName;
    }


    public String getCustomerLastName()
    {
        return customerLastName;
    }


    public void setCustomerLastName( String customerLastName )
    {
        this.customerLastName = customerLastName;
    }


    public String getCustomerEmailId()
    {
        return customerEmailId;
    }


    public void setCustomerEmailId( String customerEmailId )
    {
        this.customerEmailId = customerEmailId;
    }


    public String getAgentFirstName()
    {
        return agentFirstName;
    }


    public void setAgentFirstName( String agentFirstName )
    {
        this.agentFirstName = agentFirstName;
    }


    public String getAgentLastName()
    {
        return agentLastName;
    }


    public void setAgentLastName( String agentLastName )
    {
        this.agentLastName = agentLastName;
    }


    public String getAgentEmailId()
    {
        return agentEmailId;
    }


    public void setAgentEmailId( String agentEmailId )
    {
        this.agentEmailId = agentEmailId;
    }


    public String getLoanClosedDate()
    {
        return loanClosedDate;
    }


    public void setLoanClosedDate( String loanClosedDate )
    {
        this.loanClosedDate = loanClosedDate;
    }


    public String getStatus()
    {
        return status;
    }


    public void setStatus( String status )
    {
        this.status = status;
    }


    public String getReason()
    {
        return reason;
    }


    public void setReason( String reason )
    {
        this.reason = reason;
    }

}