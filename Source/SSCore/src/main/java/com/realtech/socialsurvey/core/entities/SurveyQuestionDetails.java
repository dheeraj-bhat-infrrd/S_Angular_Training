package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.util.List;

/*
 * This is a POJO class containing all the details of questions and answers related to a survey.
 */
public class SurveyQuestionDetails implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long questionId;
	private String question;
	private List<SurveyAnswerOptions> answers;
	private String questionType;
	private int isRatingQuestion;
	private int isUserRankingQuestion;
	private int questionOrder;
	private String customerResponse;
	//Extra fields added for NPS and migrating to API call.
	private int isNPSQuestion;
	private long userId;
	private long companyId;
	private int verticalId;
	private String isUserRankingStr;
	private String isNPSStr;
	private List<String> answerStr;

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

	public int getIsNPSQuestion() {
		return isNPSQuestion;
	}

	public void setIsNPSQuestion(int isNPSQuestion) {
		this.isNPSQuestion = isNPSQuestion;
	}

	public String getIsUserRankingStr() {
		return isUserRankingStr;
	}

	public void setIsUserRankingStr(String isUserRankingStr) {
		this.isUserRankingStr = isUserRankingStr;
	}

	public String getIsNPSStr() {
		return isNPSStr;
	}

	public void setIsNPSStr(String isNPSStr) {
		this.isNPSStr = isNPSStr;
	}

	public List<String> getAnswerStr() {
		return answerStr;
	}

	public void setAnswerStr(List<String> answerStr) {
		this.answerStr = answerStr;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

	public int getVerticalId() {
		return verticalId;
	}

	public void setVerticalId(int verticalId) {
		this.verticalId = verticalId;
	}

	@Override
	public String toString() {
		return "SurveyQuestionDetails [questionId=" + questionId + ", question=" + question + ", answers=" + answers
				+ ", questionType=" + questionType + ", isRatingQuestion=" + isRatingQuestion
				+ ", isUserRankingQuestion=" + isUserRankingQuestion + ", questionOrder=" + questionOrder
				+ ", customerResponse=" + customerResponse + ", isNPSQuestion=" + isNPSQuestion + ", userId=" + userId
				+ ", companyId=" + companyId + ", verticalId=" + verticalId + ", isUserRankingStr=" + isUserRankingStr
				+ ", isNPSStr=" + isNPSStr + ", answerStr=" + answerStr + "]";
	}

	
}
