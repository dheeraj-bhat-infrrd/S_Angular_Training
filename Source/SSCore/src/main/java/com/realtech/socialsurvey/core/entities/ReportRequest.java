package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.enums.ReportType;

public class ReportRequest implements Serializable{

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

	public long getFileUploadId() {
		return fileUploadId;
	}

	public void setFileUploadId(long fileUploadId) {
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

	public void setStartDateExpectedTimeZone(String startDateExpectedTimeZone) {
		this.startDateExpectedTimeZone = startDateExpectedTimeZone;
	}

	public String getEndDateExpectedTimeZone() {
		return endDateExpectedTimeZone;
	}

	public void setEndDateExpectedTimeZone(String endDateExpectedTimeZone) {
		this.endDateExpectedTimeZone = endDateExpectedTimeZone;
	}

	public String getProfileLevel() {
		return profileLevel;
	}

	public void setProfileLevel(String profileLevel) {
		this.profileLevel = profileLevel;
	}

	public long getProfileValue() {
		return profileValue;
	}

	public void setProfileValue(long profileValue) {
		this.profileValue = profileValue;
	}

	public long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(long companyId) {
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

    public void transform( FileUpload fileUpload, int actualTimeZoneOffset )
    {
        this.fileUploadId = fileUpload.getFileUploadId();
        this.reportType = ReportType.SURVEY_INVITATION_EMAIL_REPORT.getName();

        //check if offset is zero
        if ( actualTimeZoneOffset == 0 ) {
            this.actualTimeZone = "";
        } else {
            int hours = actualTimeZoneOffset / 60;
            // setting minutes to positive mod of 60
            int minutes = actualTimeZoneOffset % 60;
            if ( minutes < 0 )
                minutes *= -1;
            String minuteString = ( minutes < 10 ? "0" : "" ) + minutes;
            // Reversing hours to add the timezone offset difference
            String sign = ( hours < 0 ? "+" : "-" );
            if ( hours < 0 )
                hours *= -1;
            String hourString = ( hours < 10 ? "0" : "" ) + hours;
            // setting timezone offset in hours
            this.actualTimeZone = sign + hourString + ":" + minuteString;
        }
        this.expectedTimeZone = "-05:00";
        this.startDateExpectedTimeZone = timezoneDate( fileUpload.getStartDate(), "GMT" + this.expectedTimeZone );
        this.endDateExpectedTimeZone = timezoneDate( fileUpload.getEndDate(), "GMT" + this.expectedTimeZone );
        this.profileLevel = fileUpload.getProfileLevel();
        this.profileValue = fileUpload.getProfileValue();
        this.companyId = fileUpload.getCompany().getCompanyId();

    }
    
    public void transform(String timeFrame) {
    	this.reportType = ReportType.SURVEY_INVITATION_EMAIL_REPORT.getName();
    	switch(timeFrame) {
    	case CommonConstants.TIME_FRAME_PAST_MONTH :
    		this.startTime = getFirstDayOfPastMonth();
    		this.endTime = getLastDayOfPastMonth();
    		break;
    	case CommonConstants.TIME_FRAME_THIS_MONTH :
    		this.startTime = getFirstDayOfThisMonth();
    		this.endTime = getYesterday();
    		break;
    	case CommonConstants.TIME_FRAME_ALL_TIME :
    		break;
    	}
    	
    }

	private long getFirstDayOfPastMonth() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		cal.set(Calendar.DATE, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.setTimeZone(TimeZone.getTimeZone("UTC"));
		return cal.getTimeInMillis();
	}

	private long getLastDayOfPastMonth() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		cal.setTimeZone(TimeZone.getTimeZone("UTC"));
		return cal.getTimeInMillis();
	}
	
	private long getFirstDayOfThisMonth() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.setTimeZone(TimeZone.getTimeZone("UTC"));
		return cal.getTimeInMillis();
	}
	
	private long getYesterday() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, cal.get(Calendar.DATE)-1);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		cal.setTimeZone(TimeZone.getTimeZone("UTC"));
		return cal.getTimeInMillis();
	}

	public String timezoneDate(Timestamp date, String timeZone) {
        if ( date != null ) {
            SimpleDateFormat dateFormatter = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSXXX" );
            dateFormatter.setTimeZone( TimeZone.getTimeZone( timeZone ) );
            return ( dateFormatter.format( date ) );
        } else
            return null;
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
