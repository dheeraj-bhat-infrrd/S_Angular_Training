package com.realtech.socialsurvey.core.entities;

import java.util.List;

public class SurveyTemplate {

	private long surveyId;
	private String surveyName;
	private List<SurveyQuestionDetails> questions;

	public long getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(long surveyId) {
		this.surveyId = surveyId;
	}

	public String getSurveyName() {
		return surveyName;
	}

	public void setSurveyName(String surveyName) {
		this.surveyName = surveyName;
	}

	public List<SurveyQuestionDetails> getQuestions() {
		return questions;
	}

	public void setQuestions(List<SurveyQuestionDetails> questions) {
		this.questions = questions;
	}
}