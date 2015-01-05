package com.realtech.socialsurvey.core.entities;

/**
 * Holds the survey settings for the profile
 */
public class SurveySettings {

	private float auto_post_score;
	private float show_survey_above_score;
	private int survey_reminder_interval_in_days;
	private int max_number_of_survey_reminders;

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

	@Override
	public String toString() {
		return "auto_post_score: " + auto_post_score + "\t show_survey_above_score: " + show_survey_above_score
				+ "\t survey_reminder_interval_in_days: " + survey_reminder_interval_in_days + "\t max_number_of_survey_reminders: "
				+ max_number_of_survey_reminders;
	}

}
