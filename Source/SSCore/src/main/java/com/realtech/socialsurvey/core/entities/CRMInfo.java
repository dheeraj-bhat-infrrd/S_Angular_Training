package com.realtech.socialsurvey.core.entities;

/**
 * Holds the CRM information
 */
public abstract class CRMInfo {

	private String crm_source;
	private boolean connection_successful;
    private long companyId;
   
    public String getCrm_source() {
		return crm_source;
	}

	public void setCrm_source(String crm_source) {
		this.crm_source = crm_source;
	}

	public boolean isConnection_successful() {
		return connection_successful;
	}

	public void setConnection_successful(boolean connection_successful) {
		this.connection_successful = connection_successful;
	}

	public long getCompanyId()
    {
        return companyId;
    }

    public void setCompanyId( long companyId )
    {
        this.companyId = companyId;
    }
    
    @Override
	public String toString() {
		return "crm_source: " + crm_source + "\t connection_successful: " + connection_successful;
	}

}
