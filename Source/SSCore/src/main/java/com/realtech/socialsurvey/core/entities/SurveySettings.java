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
	private boolean autoPostEnabled;
	private String happyText;
	private String neutralText;
	private String sadText;
	private String happyTextComplete;
	private String neutralTextComplete;
	private String sadTextComplete;
	private ComplaintResolutionSettings complaint_res_settings;

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

	public boolean isAutoPostEnabled() {
		return autoPostEnabled;
	}

	public void setAutoPostEnabled(boolean autoPostEnabled) {
		this.autoPostEnabled = autoPostEnabled;
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

    public ComplaintResolutionSettings getComplaint_res_settings()
    {
        return complaint_res_settings;
    }

    public void setComplaint_res_settings( ComplaintResolutionSettings complaint_res_settings )
    {
        this.complaint_res_settings = complaint_res_settings;
    }

    @Override
	public String toString() {
		return "auto_post_score: " + auto_post_score + "\t show_survey_above_score: " + show_survey_above_score
				+ "\t survey_reminder_interval_in_days: " + survey_reminder_interval_in_days + "\t max_number_of_survey_reminders: "
				+ max_number_of_survey_reminders + "\t complaint_res_settings: " + complaint_res_settings;
	}
}