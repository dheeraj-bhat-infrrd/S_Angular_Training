package com.realtech.socialsurvey.core.entities;

/*
 * Stores answer to a specific question and order in which answer should appear for Multiple Choice
 * Questions.
 */
public class SurveyAnswer {

	private long answerId;
	private String answerText;
	private int answerOrder;

	public long getAnswerId() {
		return answerId;
	}

	public void setAnswerId(long answerId) {
		this.answerId = answerId;
	}

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
