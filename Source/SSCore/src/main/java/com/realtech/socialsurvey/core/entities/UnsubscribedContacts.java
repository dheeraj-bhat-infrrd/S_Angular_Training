/**
 * 
 */
package com.realtech.socialsurvey.core.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * @author user345
 *
 */
public class UnsubscribedContacts
{
    private String id;
    private long companyId;
    private long agentId;
    private String contactNumber;
    private long createdOn;
    private long modifiedOn;
    private int status;
    private int level;
    private int modifiedBy;
    private String incomingMessageBody;
    private List<OptedContactHistory> optedContactHistory;

    public String getId()
    {
        return id;
    }


    public void setId( String id )
    {
        this.id = id;
    }


    public long getCompanyId()
    {
        return companyId;
    }


    public void setCompanyId( long companyId )
    {
        this.companyId = companyId;
    }


    public long getAgentId()
    {
        return agentId;
    }


    public void setAgentId( long agentId )
    {
        this.agentId = agentId;
    }


    public String getcontactNumber()
    {
        return contactNumber;
    }


    public void setContactNumber( String contactNumber )
    {
        this.contactNumber = contactNumber;
    }


    public long getCreatedOn()
    {
        return createdOn;
    }


    public void setCreatedOn( long createdOn )
    {
        this.createdOn = createdOn;
    }


    public long getModifiedOn()
    {
        return modifiedOn;
    }


    public void setModifiedOn( long modifiedOn )
    {
        this.modifiedOn = modifiedOn;
    }


    public int getStatus()
    {
        return status;
    }


    public void setStatus( int status )
    {
        this.status = status;
    }


    public int getLevel()
    {
        return level;
    }


    public void setLevel( int level )
    {
        this.level = level;
    }


	public int getModifiedBy() {
		return modifiedBy;
	}


	public void setModifiedBy(int modifiedBy) {
		this.modifiedBy = modifiedBy;
	}


	public String getIncomingMessageBody() {
		return incomingMessageBody;
	}


	public void setIncomingMessageBody(String incomingMessageBody) {
		this.incomingMessageBody = incomingMessageBody;
	}


	public List<OptedContactHistory> getOptedContactHistory() {
		
		if( optedContactHistory == null ) {
			optedContactHistory = new ArrayList<OptedContactHistory>();
		}
		return optedContactHistory;
	}


	public void setOptedContactHistory(List<OptedContactHistory> optedContactHistory) {
		this.optedContactHistory = optedContactHistory;
	}
}
