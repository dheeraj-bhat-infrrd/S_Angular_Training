package com.realtech.socialsurvey.core.entities;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;


@Document
public class ApplicationSettings {

	
	
	private LOSearchEngine loSearchEngine;
	private String defaultSmsReminderText;
	private int defaultSmsSurveyReminderCount;
	private SMSTimeWindow smsTimeWindow;
	private long createdOn;
	private long modifiedOn;

	private String defaultHappyTextPartner;
	private String defaultNeutralTextPartner;
	private String defaultSadTextPartner;
	private String defaultHappyTextCompletePartner;
	private String defaultNeutralTextCompletePartner;
	private String defaultSadTextCompletePartner;
	
	private List<SSTimeZone> timeZones;
	
	// private constructor so no one can create instance 
	private ApplicationSettings() {}
	
	public LOSearchEngine getLoSearchEngine() {
		return loSearchEngine;
	}

	public void setLoSearchEngine(LOSearchEngine loSearchEngine) {
		this.loSearchEngine = loSearchEngine;
	}
	
	
	public String getDefaultSmsReminderText() {
		return defaultSmsReminderText;
	}

	public void setDefaultSmsReminderText(String defaultSmsReminderText) {
		this.defaultSmsReminderText = defaultSmsReminderText;
	}

	public int getDefaultSmsSurveyReminderCount() {
		return defaultSmsSurveyReminderCount;
	}

	public void setDefaultSmsSurveyReminderCount(int defaultSmsSurveyReminderCount) {
		this.defaultSmsSurveyReminderCount = defaultSmsSurveyReminderCount;
	}

	public long getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
	}

	public long getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(long modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

    public String getDefaultHappyTextPartner()
    {
        return defaultHappyTextPartner;
    }

    public void setDefaultHappyTextPartner( String defaultHappyTextPartner )
    {
        this.defaultHappyTextPartner = defaultHappyTextPartner;
    }

    public String getDefaultNeutralTextPartner()
    {
        return defaultNeutralTextPartner;
    }

    public void setDefaultNeutralTextPartner( String defaultNeutralTextPartner )
    {
        this.defaultNeutralTextPartner = defaultNeutralTextPartner;
    }

    public String getDefaultSadTextPartner()
    {
        return defaultSadTextPartner;
    }

    public void setDefaultSadTextPartner( String defaultSadTextPartner )
    {
        this.defaultSadTextPartner = defaultSadTextPartner;
    }

    public String getDefaultHappyTextCompletePartner()
    {
        return defaultHappyTextCompletePartner;
    }

    public void setDefaultHappyTextCompletePartner( String defaultHappyTextCompletePartner )
    {
        this.defaultHappyTextCompletePartner = defaultHappyTextCompletePartner;
    }

    public String getDefaultNeutralTextCompletePartner()
    {
        return defaultNeutralTextCompletePartner;
    }

    public void setDefaultNeutralTextCompletePartner( String defaultNeutralTextCompletePartner )
    {
        this.defaultNeutralTextCompletePartner = defaultNeutralTextCompletePartner;
    }

    public String getDefaultSadTextCompletePartner()
    {
        return defaultSadTextCompletePartner;
    }

    public void setDefaultSadTextCompletePartner( String defaultSadTextCompletePartner )
    {
        this.defaultSadTextCompletePartner = defaultSadTextCompletePartner;
    }

    public SMSTimeWindow getSmsTimeWindow()
    {
        return smsTimeWindow;
    }

    public void setSmsTimeWindow( SMSTimeWindow smsTimeWindow )
    {
        this.smsTimeWindow = smsTimeWindow;
    }
    
    public List<SSTimeZone> getTimeZones()
    {
        return timeZones;
    }

    public void setTimeZones( List<SSTimeZone> timeZones )
    {
        this.timeZones = timeZones;
    }

    @Override
    public String toString()
    {
        return "ApplicationSettings [loSearchEngine=" + loSearchEngine + ", defaultSmsReminderText=" + defaultSmsReminderText
            + ", defaultSmsSurveyReminderCount=" + defaultSmsSurveyReminderCount + ", smsTimeWindow=" + smsTimeWindow
            + ", createdOn=" + createdOn + ", modifiedOn=" + modifiedOn + ", defaultHappyTextPartner=" + defaultHappyTextPartner
            + ", defaultNeutralTextPartner=" + defaultNeutralTextPartner + ", defaultSadTextPartner=" + defaultSadTextPartner
            + ", defaultHappyTextCompletePartner=" + defaultHappyTextCompletePartner + ", defaultNeutralTextCompletePartner="
            + defaultNeutralTextCompletePartner + ", defaultSadTextCompletePartner=" + defaultSadTextCompletePartner
            + ", timeZones=" + timeZones + "]";
    }
    
}
