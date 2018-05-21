package com.realtech.socialsurvey.stream.entities;

import java.io.Serializable;

public class ReportRequest implements Serializable
{
    private static final long serialVersionUID = 1L;

    private long fileUploadId;
    private String reportType;
    private String startDateExpectedTimeZone;
    private String endDateExpectedTimeZone;
    private String profileLevel;
    private long profileValue;
    private long companyId;
    private String actualTimeZone;
    private String expectedTimeZone;
    private long startTime;
	private long endTime;
    private String keyword;


    public long getFileUploadId()
    {
        return fileUploadId;
    }


    public void setFileUploadId( long fileUploadId )
    {
        this.fileUploadId = fileUploadId;
    }


    public String getReportType()
    {
        return reportType;
    }


    public void setReportType( String reportType )
    {
        this.reportType = reportType;
    }


    public String getStartDateExpectedTimeZone()
    {
        return startDateExpectedTimeZone;
    }


    public void setStartDateExpectedTimeZone( String startDateExpectedTimeZone )
    {
        this.startDateExpectedTimeZone = startDateExpectedTimeZone;
    }


    public String getEndDateExpectedTimeZone()
    {
        return endDateExpectedTimeZone;
    }


    public void setEndDateExpectedTimeZone( String endDateExpectedTimeZone )
    {
        this.endDateExpectedTimeZone = endDateExpectedTimeZone;
    }


    public String getProfileLevel()
    {
        return profileLevel;
    }


    public void setProfileLevel( String profileLevel )
    {
        this.profileLevel = profileLevel;
    }


    public long getProfileValue()
    {
        return profileValue;
    }


    public void setProfileValue( long profileValue )
    {
        this.profileValue = profileValue;
    }


    public long getCompanyId()
    {
        return companyId;
    }


    public void setCompanyId( long companyId )
    {
        this.companyId = companyId;
    }


    public String getActualTimeZone()
    {
        return actualTimeZone;
    }


    public void setActualTimeZone( String actualTimeZone )
    {
        this.actualTimeZone = actualTimeZone;
    }


    public String getExpectedTimeZone()
    {
        return expectedTimeZone;
    }


    public void setExpectedTimeZone( String expectedTimeZone )
    {
        this.expectedTimeZone = expectedTimeZone;
    }

    public long getStartTime() {
		return startTime;
	}


	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}


	public long getEndTime() {
		return endTime;
	}


	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public String getKeyword()
    {
        return keyword;
    }


    public void setKeyword( String keyword )
    {
        this.keyword = keyword;
    }


    @Override
    public String toString()
    {
        return "ReportRequest [fileUploadId=" + fileUploadId + ", reportType=" + reportType + ", startDateExpectedTimeZone="
            + startDateExpectedTimeZone + ", endDateExpectedTimeZone=" + endDateExpectedTimeZone + ", profileLevel="
            + profileLevel + ", profileValue=" + profileValue + ", companyId=" + companyId + ", actualTimeZone="
            + actualTimeZone + ", expectedTimeZone=" + expectedTimeZone + ", startTime=" + startTime + ", endTime=" + endTime
            + ", keyword=" + keyword + "]";
    }



}
