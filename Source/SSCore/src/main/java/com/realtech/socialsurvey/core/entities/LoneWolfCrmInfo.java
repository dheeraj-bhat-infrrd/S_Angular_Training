package com.realtech.socialsurvey.core.entities;

import java.util.List;

public class LoneWolfCrmInfo extends CRMInfo
{
    private String clientCode;
    private long regionId;
    private long branchId;
    private long agentId;
    private String state;
    private String host;
    private int numberOfDays;
    private String emailAddressForReport;
    private boolean generateReport;
    List<LoneWolfClassificationCode> classificationCodes; 
    
	
    public String getHost() {
		return host;
	}


	public void setHost(String host) {
		this.host = host;
	}


	public int getNumberOfDays() {
		return numberOfDays;
	}


	public void setNumberOfDays(int numberOfDays) {
		this.numberOfDays = numberOfDays;
	}


	public String getEmailAddressForReport() {
		return emailAddressForReport;
	}


	public void setEmailAddressForReport(String emailAddressForReport) {
		this.emailAddressForReport = emailAddressForReport;
	}


	public boolean isGenerateReport() {
		return generateReport;
	}


	public void setGenerateReport(boolean generateReport) {
		this.generateReport = generateReport;
	}



    public String getClientCode()
    {
        return clientCode;
    }


    public void setClientCode( String clientCode )
    {
        this.clientCode = clientCode;
    }


    public long getRegionId()
    {
        return regionId;
    }


    public void setRegionId( long regionId )
    {
        this.regionId = regionId;
    }


    public long getBranchId()
    {
        return branchId;
    }


    public void setBranchId( long branchId )
    {
        this.branchId = branchId;
    }


    public long getAgentId()
    {
        return agentId;
    }


    public void setAgentId( long agentId )
    {
        this.agentId = agentId;
    }

    
    public String getState()
    {
        return state;
    }


    public void setState( String state )
    {
        this.state = state;
    }
    
    public List<LoneWolfClassificationCode> getClassificationCodes()
    {
        return classificationCodes;
    }


    public void setClassificationCodes( List<LoneWolfClassificationCode> classificationCodes )
    {
        this.classificationCodes = classificationCodes;
    }
}
