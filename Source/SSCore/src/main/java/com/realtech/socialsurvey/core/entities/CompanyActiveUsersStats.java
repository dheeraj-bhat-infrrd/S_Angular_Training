package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name= "company_avtive_users")
public class CompanyActiveUsersStats  implements Serializable 
{

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY)
    @Column ( name = "company_avtive_users_id")
    private String companyActiveUserId;
    
    @Column( name = "company_id")
    private long companyId;

    @Column( name = "date_val")
    private Date statsDate;
    
    @Column(name = "active_users")
    private int noOfActiveUsers;

    
    public String getCompanyActiveUserId()
    {
        return companyActiveUserId;
    }

    public void setCompanyActiveUserId( String companyActiveUserId )
    {
        this.companyActiveUserId = companyActiveUserId;
    }

    public long getCompanyId()
    {
        return companyId;
    }

    public void setCompanyId( long companyId )
    {
        this.companyId = companyId;
    }

    public Date getStatsDate()
    {
        return statsDate;
    }

    public void setStatsDate( Date statsDate )
    {
        this.statsDate = statsDate;
    }

    public int getNoOfActiveUsers()
    {
        return noOfActiveUsers;
    }

    public void setNoOfActiveUsers( int noOfActiveUsers )
    {
        this.noOfActiveUsers = noOfActiveUsers;
    }

}
