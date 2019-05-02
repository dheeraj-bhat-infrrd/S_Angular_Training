package com.realtech.socialsurvey.core.entities;

/**
 * Holds the survey settings for the profile
 */
public class SurveySettings {

	private float auto_post_score;
	private float show_survey_above_score;
	
	private int survey_reminder_interval_in_days;
	private int max_number_of_survey_reminders;
	private boolean isReminderDisabled;
	
	private int social_post_reminder_interval_in_days;
    private int max_number_of_social_pos_reminders;
    private boolean isSocialPostReminderDisabled;
	
	private boolean autoPostEnabled;
	private boolean autoPostLinkToUserSiteEnabled;
	private String happyText;
	private String neutralText;
	private String sadText;
	private String happyTextComplete;
	private String neutralTextComplete;
	private String sadTextComplete;
	
	private String happyTextPartner;
	private String neutralTextPartner;
	private String sadTextPartner;
	private String happyTextCompletePartner;
	private String neutralTextCompletePartner;
	private String sadTextCompletePartner;
	private ComplaintResolutionSettings complaint_res_settings;
	private AbusiveMailSettings abusive_mail_settings;
	private String happyUrl;
	private String okUrl;
	private String sadUrl;
	
    // threshold for sending survey completed mail to agents and admins
    private double surveyCompletedMailThreshold;
	
	private int duplicateSurveyInterval;
	
	private float reviewReplyScore;
	private boolean isReplyEnabled;
	private boolean isReplyEnabledForCompany;

	public int getDuplicateSurveyInterval()
    {
        return duplicateSurveyInterval;
    }

    public void setDuplicateSurveyInterval( int duplicateSurveyInterval )
    {
        this.duplicateSurveyInterval = duplicateSurveyInterval;
    }

    public float getAuto_post_score() {
		return auto_post_score;
	}

	public void setAuto_post_score(float auto_post_score) {
		this.auto_post_score = auto_post_score;
	}

	public float getShow_survey_above_score() {
		return show_survey_above_score;
	}

	public void setShow_survey_above_score(float show_survey_above_score) {
		this.show_survey_above_score = show_survey_above_score;
	}

	public int getSurvey_reminder_interval_in_days() {
		return survey_reminder_interval_in_days;
	}

	public void setSurvey_reminder_interval_in_days(int survey_reminder_interval_in_days) {
		this.survey_reminder_interval_in_days = survey_reminder_interval_in_days;
	}

	public int getMax_number_of_survey_reminders() {
		return max_number_of_survey_reminders;
	}

	public void setMax_number_of_survey_reminders(int max_number_of_survey_reminders) {
		this.max_number_of_survey_reminders = max_number_of_survey_reminders;
	}

	public boolean getIsReminderDisabled() {
		return isReminderDisabled;
	}
	
	public void setReminderDisabled(boolean isReminderDisabled) {
        this.isReminderDisabled = isReminderDisabled;
    }

	public int getSocial_post_reminder_interval_in_days()
    {
        return social_post_reminder_interval_in_days;
    }

    public void setSocial_post_reminder_interval_in_days( int social_post_reminder_interval_in_days )
    {
        this.social_post_reminder_interval_in_days = social_post_reminder_interval_in_days;
    }

    public int getMax_number_of_social_pos_reminders()
    {
        return max_number_of_social_pos_reminders;
    }

    public void setMax_number_of_social_pos_reminders( int max_number_of_social_pos_reminders )
    {
        this.max_number_of_social_pos_reminders = max_number_of_social_pos_reminders;
    }

    public boolean getIsSocialPostReminderDisabled()
    {
        return isSocialPostReminderDisabled;
    }

    public void setSocialPostReminderDisabled( boolean isSocialPostReminderDisabled )
    {
        this.isSocialPostReminderDisabled = isSocialPostReminderDisabled;
    }

	public boolean isAutoPostEnabled() {
		return autoPostEnabled;
	}

	public void setAutoPostEnabled(boolean autoPostEnabled) {
		this.autoPostEnabled = autoPostEnabled;
	}
	
	public boolean isAutoPostLinkToUserSiteEnabled()
    {
        return autoPostLinkToUserSiteEnabled;
    }

    public void setAutoPostLinkToUserSiteEnabled( boolean autoPostLinkToUserSiteEnabled )
    {
        this.autoPostLinkToUserSiteEnabled = autoPostLinkToUserSiteEnabled;
    }

	public String getHappyText() {
		return happyText;
	}

	public void setHappyText(String happyText) {
		this.happyText = happyText;
	}

	public String getNeutralText() {
		return neutralText;
	}

	public void setNeutralText(String neutralText) {
		this.neutralText = neutralText;
	}

	public String getSadText() {
		return sadText;
	}

	public void setSadText(String sadText) {
		this.sadText = sadText;
	}

	public String getHappyTextComplete() {
		return happyTextComplete;
	}

	public void setHappyTextComplete(String happyTextComplete) {
		this.happyTextComplete = happyTextComplete;
	}

	public String getNeutralTextComplete() {
		return neutralTextComplete;
	}

	public void setNeutralTextComplete(String neutralTextComplete) {
		this.neutralTextComplete = neutralTextComplete;
	}

	public String getSadTextComplete() {
		return sadTextComplete;
	}

	public void setSadTextComplete(String sadTextComplete) {
		this.sadTextComplete = sadTextComplete;
	}

    public String getHappyTextPartner()
    {
        return happyTextPartner;
    }

    public void setHappyTextPartner( String happyTextPartner )
    {
        this.happyTextPartner = happyTextPartner;
    }

    public String getNeutralTextPartner()
    {
        return neutralTextPartner;
    }

    public void setNeutralTextPartner( String neutralTextPartner )
    {
        this.neutralTextPartner = neutralTextPartner;
    }

    public String getSadTextPartner()
    {
        return sadTextPartner;
    }

    public void setSadTextPartner( String sadTextPartner )
    {
        this.sadTextPartner = sadTextPartner;
    }

    public String getHappyTextCompletePartner()
    {
        return happyTextCompletePartner;
    }

    public void setHappyTextCompletePartner( String happyTextCompletePartner )
    {
        this.happyTextCompletePartner = happyTextCompletePartner;
    }

    public String getNeutralTextCompletePartner()
    {
        return neutralTextCompletePartner;
    }

    public void setNeutralTextCompletePartner( String neutralTextCompletePartner )
    {
        this.neutralTextCompletePartner = neutralTextCompletePartner;
    }

    public String getSadTextCompletePartner()
    {
        return sadTextCompletePartner;
    }

    public void setSadTextCompletePartner( String sadTextCompletePartner )
    {
        this.sadTextCompletePartner = sadTextCompletePartner;
    }

    public ComplaintResolutionSettings getComplaint_res_settings()
    {
        return complaint_res_settings;
    }

    public void setComplaint_res_settings( ComplaintResolutionSettings complaint_res_settings )
    {
        this.complaint_res_settings = complaint_res_settings;
    }

    
    public double getSurveyCompletedMailThreshold()
    {
        return surveyCompletedMailThreshold;
    }

    public void setSurveyCompletedMailThreshold( double surveyCompletedMailThreshold )
    {
        this.surveyCompletedMailThreshold = surveyCompletedMailThreshold;
    }

    public AbusiveMailSettings getAbusive_mail_settings() {
		return abusive_mail_settings;
	}

	public void setAbusive_mail_settings(AbusiveMailSettings abusive_mail_settings) {
		this.abusive_mail_settings = abusive_mail_settings;
	}
	
	

	public String getHappyUrl() {
		return happyUrl;
	}

	public void setHappyUrl(String happyUrl) {
		this.happyUrl = happyUrl;
	}

	public String getOkUrl() {
		return okUrl;
	}

	public void setOkUrl(String okUrl) {
		this.okUrl = okUrl;
	}

	public String getSadUrl() {
		return sadUrl;
	}

	public void setSadUrl(String sadUrl) {
		this.sadUrl = sadUrl;
	}

	public float getReviewReplyScore()
    {
        return reviewReplyScore;
    }

    public void setReviewReplyScore( float reviewReplyScore )
    {
        this.reviewReplyScore = reviewReplyScore;
    }


    public boolean isReplyEnabled()
    {
        return isReplyEnabled;
    }

    public void setReplyEnabled( boolean isReplyEnabled )
    {
        this.isReplyEnabled = isReplyEnabled;
    }

    public boolean isReplyEnabledForCompany()
    {
        return isReplyEnabledForCompany;
    }

    public void setReplyEnabledToAll( boolean isReplyEnabledForCompany )
    {
        this.isReplyEnabledForCompany = isReplyEnabledForCompany;
    }

    @Override
    public String toString()
    {
        return "SurveySettings [auto_post_score=" + auto_post_score + ", show_survey_above_score=" + show_survey_above_score
            + ", survey_reminder_interval_in_days=" + survey_reminder_interval_in_days + ", max_number_of_survey_reminders="
            + max_number_of_survey_reminders + ", isReminderDisabled=" + isReminderDisabled
            + ", social_post_reminder_interval_in_days=" + social_post_reminder_interval_in_days
            + ", max_number_of_social_pos_reminders=" + max_number_of_social_pos_reminders + ", isSocialPostReminderDisabled="
            + isSocialPostReminderDisabled + ", autoPostEnabled=" + autoPostEnabled + ", autoPostLinkToUserSiteEnabled="
            + autoPostLinkToUserSiteEnabled + ", happyText=" + happyText + ", neutralText=" + neutralText + ", sadText="
            + sadText + ", happyTextComplete=" + happyTextComplete + ", neutralTextComplete=" + neutralTextComplete
            + ", sadTextComplete=" + sadTextComplete + ", complaint_res_settings=" + complaint_res_settings
            + ", abusive_mail_settings=" + abusive_mail_settings + ", happyUrl=" + happyUrl + ", okUrl=" + okUrl + ", sadUrl="
            + sadUrl + ", surveyCompletedMailThreshold=" + surveyCompletedMailThreshold + ", duplicateSurveyInterval="
            + duplicateSurveyInterval + ", reviewReplyScore=" + reviewReplyScore + ", isReplyEnabled=" + isReplyEnabled
            + ", isReplyEnabledForCompany=" + isReplyEnabledForCompany + "]";
    }

}