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
@Table(name= "company_source_count")
public class CompanyTransactionsSourceStats  implements Serializable 
{
    
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY)
    @Column ( name = "company_source_count_id")
    private String dailyTransactionsSourceStatsId;
    
    @Column( name = "company_id")
    private long companyId;

    @Column( name = "date_val")
    private Date transactionDate;
    
    @Column(name = "API")
    private int apiTransactionsCount;
    
    @Column(name = "LONEWOLF")
    private int lonewolfTransactionsCount;
    
    @Column(name = "FTP")
    private int ftpTransactionsCount;
    
    @Column(name = "DOTLOOP")
    private int dotloopTransactionsCount;
    
    @Column(name = "encompass")
    private int encompassTransactionsCount;
    
    @Column(name = "total")
    private int totalTransactionsCount;
    
    @Column(name = "CSV_UPLOAD")
    private int csvUploadTransactionsCount;

    public String getDailyTransactionsSourceStatsId()
    {
        return dailyTransactionsSourceStatsId;
    }

    public void setDailyTransactionsSourceStatsId( String dailyTransactionsSourceStatsId )
    {
        this.dailyTransactionsSourceStatsId = dailyTransactionsSourceStatsId;
    }

    public long getCompanyId()
    {
        return companyId;
    }

    public void setCompanyId( long companyId )
    {
        this.companyId = companyId;
    }

    
    public int getApiTransactionsCount()
    {
        return apiTransactionsCount;
    }

    public void setApiTransactionsCount( int apiTransactionsCount )
    {
        this.apiTransactionsCount = apiTransactionsCount;
    }

    public int getLonewolfTransactionsCount()
    {
        return lonewolfTransactionsCount;
    }

    public void setLonewolfTransactionsCount( int lonewolfTransactionsCount )
    {
        this.lonewolfTransactionsCount = lonewolfTransactionsCount;
    }

    public int getFtpTransactionsCount()
    {
        return ftpTransactionsCount;
    }

    public void setFtpTransactionsCount( int ftpTransactionsCount )
    {
        this.ftpTransactionsCount = ftpTransactionsCount;
    }

    public int getDotloopTransactionsCount()
    {
        return dotloopTransactionsCount;
    }

    public void setDotloopTransactionsCount( int dotloopTransactionsCount )
    {
        this.dotloopTransactionsCount = dotloopTransactionsCount;
    }

    public int getEncompassTransactionsCount()
    {
        return encompassTransactionsCount;
    }

    public void setEncompassTransactionsCount( int encompassTransactionsCount )
    {
        this.encompassTransactionsCount = encompassTransactionsCount;
    }

    public int getTotalTransactionsCount()
    {
        return totalTransactionsCount;
    }

    public void setTotalTransactionsCount( int totalTransactionsCount )
    {
        this.totalTransactionsCount = totalTransactionsCount;
    }

    public int getCsvUploadTransactionsCount()
    {
        return csvUploadTransactionsCount;
    }

    public void setCsvUploadTransactionsCount( int csvUploadTransactionsCount )
    {
        this.csvUploadTransactionsCount = csvUploadTransactionsCount;
    }

    public Date getTransactionDate()
    {
        return transactionDate;
    }

    public void setTransactionDate( Date transactionDate )
    {
        this.transactionDate = transactionDate;
    }

}
