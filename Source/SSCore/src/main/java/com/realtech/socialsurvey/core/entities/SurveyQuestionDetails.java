package com.realtech.socialsurvey.core.entities;

import java.util.List;

/*
 * This is a POJO class containing all the details of questions and answers related to a survey. 
 */
public class SurveyQuestionDetails {

	private String question;
	private List<SurveyAnswer> answers;
	private String questionType;
	private int isRatingQuestion;
	private int questionOrder;

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public List<SurveyAnswer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<SurveyAnswer> answers) {
		this.answers = answers;
	}

	public String getQuestionType() {
		return questionType;
	}

	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}

	public int getIsRatingQuestion() {
		return isRatingQuestion;
	}

	public void setIsRatingQuestion(int isRatingQuestion) {
		this.isRatingQuestion = isRatingQuestion;
	}

	public int getQuestionOrder() {
		return questionOrder;
	}

	public void setQuestionOrder(int questionOrder) {
		this.questionOrder = questionOrder;
	}
}
