package com.realtech.socialsurvey.core.entities;

import java.util.List;

/*
 * This is a POJO class containing all the details of questions and answers related to a survey.
 */
public class SurveyQuestionDetails {

	private long questionId;
	private String question;
	private List<SurveyAnswerOptions> answers;
	private String questionType;
	private int isRatingQuestion;
	private int isUserRankingQuestion;
	private int questionOrder;
	private String customerResponse;

	public long getQuestionId() {
		return questionId;
	}

	public void setQuestionId(long questionId) {
		this.questionId = questionId;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public List<SurveyAnswerOptions> getAnswers() {
		return answers;
	}

	public void setAnswers(List<SurveyAnswerOptions> answers) {
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

	public int getIsUserRankingQuestion()
    {
        return isUserRankingQuestion;
    }

    public void setIsUserRankingQuestion( int isUserRankingQuestion )
    {
        this.isUserRankingQuestion = isUserRankingQuestion;
    }

    public int getQuestionOrder() {
		return questionOrder;
	}

	public void setQuestionOrder(int questionOrder) {
		this.questionOrder = questionOrder;
	}

	public String getCustomerResponse() {
		return customerResponse;
	}

	public void setCustomerResponse(String customerResponse) {
		this.customerResponse = customerResponse;
	}
}
