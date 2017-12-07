package com.realtech.socialsurvey.core.entities;

public class SurveyResponse {
	
	private String question;
	private String questionType;
	private String answer;
	private boolean isUserRankingQuestion;
	private boolean isNpsQuestion;
	private int questionId;
	private boolean considerForScore;
	
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
    public boolean getIsNpsQuestion()
    {
        return isNpsQuestion;
    }
    public void setIsNpsQuestion( boolean isNpsQuestion )
    {
        this.isNpsQuestion = isNpsQuestion;
    }
    public int getQuestionId()
    {
        return questionId;
    }
    public void setQuestionId( int questionId )
    {
        this.questionId = questionId;
    }
    public boolean isConsiderForScore()
    {
        return considerForScore;
    }
    public void setConsiderForScore( boolean considerForScore )
    {
        this.considerForScore = considerForScore;
    }
    
    
}
