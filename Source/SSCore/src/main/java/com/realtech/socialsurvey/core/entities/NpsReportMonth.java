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
@Table ( name = "nps_report_month")
@NamedQuery ( name = "NpsReportMonth.findAll", query = "SELECT s FROM NpsReportMonth s")
public class NpsReportMonth implements Serializable
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY)
    @Column ( name = "nps_report_month_id")
    private String npsReportMonthId;

    @Column ( name = "month")
    private int month;

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
    private double nps;

    @Column ( name = "previous_month_nps")
    private Double previousMonthNps;

    @Column ( name = "nps_delta")
    private double npsDelta;

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


    public String getNpsReportMonthId()
    {
        return npsReportMonthId;
    }


    public void setNpsReportMonthId( String npsReportMonthId )
    {
        this.npsReportMonthId = npsReportMonthId;
    }


    public int getMonth()
    {
        return month;
    }


    public void setMonth( int month )
    {
        this.month = month;
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


    public double getNps()
    {
        return nps;
    }


    public void setNps( double nps )
    {
        this.nps = nps;
    }


    public Double getPreviousMonthNps()
    {
        return previousMonthNps;
    }


    public void setPreviousMonthNps( Double previousMonthNps )
    {
        this.previousMonthNps = previousMonthNps;
    }


    public double getNpsDelta()
    {
        return npsDelta;
    }


    public void setNpsDelta( double npsDelta )
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
        return "NpsReportMonth [npsReportMonthId=" + npsReportMonthId + ", month=" + month + ", year=" + year + ", companyId="
            + companyId + ", companyName=" + companyName + ", regionId=" + regionId + ", regionName=" + regionName
            + ", branchId=" + branchId + ", branchName=" + branchName + ", nps=" + nps + ", previousMonthNps="
            + previousMonthNps + ", npsDelta=" + npsDelta + ", responders=" + responders + ", responsePercent="
            + responsePercent + ", avgNpsRating=" + avgNpsRating + ", promotorsPercent=" + promotorsPercent
            + ", detractorsPercent=" + detractorsPercent + "]";
    }


}