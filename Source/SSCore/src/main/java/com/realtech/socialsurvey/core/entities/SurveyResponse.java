package com.realtech.socialsurvey.core.entities;

public class SurveyResponse {
	
	private String question;
	private String questionType;
	private String answer;
	private boolean isUserRankingQuestion;
	
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getQuestionType() {
		return questionType;
	}
	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
    public boolean getIsUserRankingQuestion()
    {
        return isUserRankingQuestion;
    }
    public void setIsUserRankingQuestion( boolean isUserRankingQuestion )
    {
        this.isUserRankingQuestion = isUserRankingQuestion;
    }
}
