package com.realtech.socialsurvey.core.entities;

public class EncompassCrmInfo extends CRMInfo
{

    private String crm_username;
    private String crm_password;
    private String url;
    private String crm_fieldId;
    private String state;
    private int numberOfDays;
    private String emailAddressForReport;
    private boolean generateReport;
    private String version;
    
    private String buyerAgentEmail;
    private String buyerAgentName;
    private String sellerAgentEmail;
    private String sellerAgentName;
    
    private String propertyAddress;
    private String loanProcessorName;
    private String loanProcessorEmail;


    public String getVersion()
    {
        return version;
    }


    public void setVersion( String version )
    {
        this.version = version;
    }

    
    public String getCrm_username()
    {
        return crm_username;
    }


    public void setCrm_username( String crm_username )
    {
        this.crm_username = crm_username;
    }


    public String getCrm_password()
    {
        return crm_password;
    }


    public void setCrm_password( String crm_password )
    {
        this.crm_password = crm_password;
    }


    public String getUrl()
    {
        return url;
    }


    public void setUrl( String url )
    {
        this.url = url;
    }


    public String getCrm_fieldId()
    {
        return crm_fieldId;
    }


    public void setCrm_fieldId( String crm_fieldId )
    {
        this.crm_fieldId = crm_fieldId;
    }


    public String getState()
    {
        return state;
    }


    public void setState( String state )
    {
        this.state = state;
    }


    public int getNumberOfDays()
    {
        return numberOfDays;
    }


    public void setNumberOfDays( int numberOfDays )
    {
        this.numberOfDays = numberOfDays;
    }


    public String getEmailAddressForReport()
    {
        return emailAddressForReport;
    }


    public void setEmailAddressForReport( String emailAddressForReport )
    {
        this.emailAddressForReport = emailAddressForReport;
    }


    public boolean isGenerateReport()
    {
        return generateReport;
    }


    public void setGenerateReport( boolean generateReport )
    {
        this.generateReport = generateReport;
    }

    public String getBuyerAgentEmail()
    {
        return buyerAgentEmail;
    }


    public void setBuyerAgentEmail( String buyerAgentEmail )
    {
        this.buyerAgentEmail = buyerAgentEmail;
    }


    public String getBuyerAgentName()
    {
        return buyerAgentName;
    }


    public void setBuyerAgentName( String buyerAgentName )
    {
        this.buyerAgentName = buyerAgentName;
    }


    public String getSellerAgentEmail()
    {
        return sellerAgentEmail;
    }


    public void setSellerAgentEmail( String sellerAgentEmail )
    {
        this.sellerAgentEmail = sellerAgentEmail;
    }


    public String getSellerAgentName()
    {
        return sellerAgentName;
    }


    public void setSellerAgentName( String sellerAgentName )
    {
        this.sellerAgentName = sellerAgentName;
    }

    

    public String getPropertyAddress() {
		return propertyAddress;
	}


	public void setPropertyAddress(String propertyAddress) {
		this.propertyAddress = propertyAddress;
	}


	public String getLoanProcessorName() {
		return loanProcessorName;
	}


	public void setLoanProcessorName(String loanProcessorName) {
		this.loanProcessorName = loanProcessorName;
	}


	public String getLoanProcessorEmail() {
		return loanProcessorEmail;
	}


	public void setLoanProcessorEmail(String loanProcessorEmail) {
		this.loanProcessorEmail = loanProcessorEmail;
	}


	@Override
    public String toString()
    {
        return "crm_username: " + crm_username;
    }
}
