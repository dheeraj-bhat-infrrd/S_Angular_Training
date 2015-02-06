package com.realtech.socialsurvey.core.entities;

import java.util.List;

public class SurveyDetail {

	private String status;
	private List<SurveyQuestionDetails> questions;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<SurveyQuestionDetails> getQuestions() {
		return questions;
	}

	public void setQuestions(List<SurveyQuestionDetails> questions) {
		this.questions = questions;
	}
}