package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


@Entity
@Table ( name = "nps_report_week")
@NamedQuery ( name = "NpsReportWeek.findAll", query = "SELECT s FROM NpsReportWeek s")
public class NpsReportWeek implements Serializable
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY)
    @Column ( name = "nps_report_week_id")
    private String npsReportWeekId;

    @Column ( name = "week")
    private int week;

    @Column ( name = "year")
    private int year;

    @Column ( name = "company_id")
    private long companyId;

    @Column ( name = "company_name")
    private String companyName;

    @Column ( name = "region_id")
    private long regionId;

    @Column ( name = "region_name")
    private String regionName;

    @Column ( name = "branch_id")
    private long branchId;

    @Column ( name = "branch_name")
    private String branchName;

    @Column ( name = "nps")
    private Double nps;

    @Column ( name = "previous_week_nps")
    private Double previousWeekNps;

    @Column ( name = "nps_delta")
    private Double npsDelta;

    @Column ( name = "responders")
    private int responders;

    @Column ( name = "response_percent")
    private double responsePercent;

    @Column ( name = "avg_nps_rating")
    private double avgNpsRating;

    @Column ( name = "promotors_percent")
    private double promotorsPercent;

    @Column ( name = "detractors_percent")
    private double detractorsPercent;


    public String getNpsReportWeekId()
    {
        return npsReportWeekId;
    }


    public void setNpsReportWeekId( String npsReportWeekId )
    {
        this.npsReportWeekId = npsReportWeekId;
    }


    public int getWeek()
    {
        return week;
    }


    public void setWeek( int week )
    {
        this.week = week;
    }


    public int getYear()
    {
        return year;
    }


    public void setYear( int year )
    {
        this.year = year;
    }


    public long getCompanyId()
    {
        return companyId;
    }


    public void setCompanyId( long companyId )
    {
        this.companyId = companyId;
    }


    public String getCompanyName()
    {
        return companyName;
    }


    public void setCompanyName( String companyName )
    {
        this.companyName = companyName;
    }


    public long getRegionId()
    {
        return regionId;
    }


    public void setRegionId( long regionId )
    {
        this.regionId = regionId;
    }


    public String getRegionName()
    {
        return regionName;
    }


    public void setRegionName( String regionName )
    {
        this.regionName = regionName;
    }


    public long getBranchId()
    {
        return branchId;
    }


    public void setBranchId( long branchId )
    {
        this.branchId = branchId;
    }


    public String getBranchName()
    {
        return branchName;
    }


    public void setBranchName( String branchName )
    {
        this.branchName = branchName;
    }


    public Double getNps()
    {
        return nps;
    }


    public void setNps( Double nps )
    {
        this.nps = nps;
    }


    public Double getPreviousWeekNps()
    {
        return previousWeekNps;
    }


    public void setPreviousWeekNps( Double previousWeekNps )
    {
        this.previousWeekNps = previousWeekNps;
    }


    public Double getNpsDelta()
    {
        return npsDelta;
    }


    public void setNpsDelta( Double npsDelta )
    {
        this.npsDelta = npsDelta;
    }


    public int getResponders()
    {
        return responders;
    }


    public void setResponders( int responders )
    {
        this.responders = responders;
    }


    public double getResponsePercent()
    {
        return responsePercent;
    }


    public void setResponsePercent( double responsePercent )
    {
        this.responsePercent = responsePercent;
    }


    public double getAvgNpsRating()
    {
        return avgNpsRating;
    }


    public void setAvgNpsRating( double avgNpsRating )
    {
        this.avgNpsRating = avgNpsRating;
    }


    public double getPromotorsPercent()
    {
        return promotorsPercent;
    }


    public void setPromotorsPercent( double promotorsPercent )
    {
        this.promotorsPercent = promotorsPercent;
    }


    public double getDetractorsPercent()
    {
        return detractorsPercent;
    }


    public void setDetractorsPercent( double detractorsPercent )
    {
        this.detractorsPercent = detractorsPercent;
    }


    @Override
    public String toString()
    {
        return "NpsReportWeek [npsReportWeekId=" + npsReportWeekId + ", week=" + week + ", year=" + year + ", companyId="
            + companyId + ", companyName=" + companyName + ", regionId=" + regionId + ", regionName=" + regionName
            + ", branchId=" + branchId + ", branchName=" + branchName + ", nps=" + nps + ", previousWeekNps=" + previousWeekNps
            + ", npsDelta=" + npsDelta + ", responders=" + responders + ", responsePercent=" + responsePercent
            + ", avgNpsRating=" + avgNpsRating + ", promotorsPercent=" + promotorsPercent + ", detractorsPercent="
            + detractorsPercent + "]";
    }


}
