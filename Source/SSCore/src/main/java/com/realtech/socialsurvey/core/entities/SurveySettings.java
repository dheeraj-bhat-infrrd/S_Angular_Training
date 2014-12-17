package com.realtech.socialsurvey.core.entities;

/**
 * Holds the survey settings for the profile
 */
public class SurveySettings {

	private float auto_post_score;
	private float show_survey_above_score;

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
	
	@Override
	public String toString(){
		return "auto_post_score: "+auto_post_score+"\t show_survey_above_score: "+show_survey_above_score;
	}

}
