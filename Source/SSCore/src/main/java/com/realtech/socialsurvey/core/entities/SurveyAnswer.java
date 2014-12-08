package com.realtech.socialsurvey.core.entities;

/*
 * Stores answer to a specific question and order in which answer should appear for Multiple Choice
 * Questions.
 */
public class SurveyAnswer {

	private String answerText;
	private int answerOrder;

	public String getAnswerText() {
		return answerText;
	}

	public void setAnswerText(String answerText) {
		this.answerText = answerText;
	}

	public int getAnswerOrder() {
		return answerOrder;
	}

	public void setAnswerOrder(int answerOrder) {
		this.answerOrder = answerOrder;
	}
}
