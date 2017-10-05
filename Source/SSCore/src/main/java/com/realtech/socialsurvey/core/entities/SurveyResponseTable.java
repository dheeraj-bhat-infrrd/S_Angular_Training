package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "survey_response")
public class SurveyResponseTable implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SURVEY_RESPONSE_ID")
	private String surveyResponseId;
	
   @Column(name = "SURVEY_DETAILS_ID")
    private String surveyDetailsId;
	   
	@Column(name = "ANSWER")
	private String answer;
	
	@Column(name = " QUESTION")
	private String question;
	
	@Column(name = "QUESTION_TYPE")
	private String questionType;


	public String getSurveyResponseId() {
		return surveyResponseId;
	}

	public void setSurveyResponseId(String surveyResponseId) {
		this.surveyResponseId = surveyResponseId;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

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
	
	
	
    public String getSurveyDetailsId()
    {
        return surveyDetailsId;
    }

    public void setSurveyDetailsId( String surveyDetailsId )
    {
        this.surveyDetailsId = surveyDetailsId;
    }

    @Override
    public String toString()
    {
        return "SurveyResponseTable [surveyResponseId=" + surveyResponseId + ", surveyDetailsId=" + surveyDetailsId
            + ", answer=" + answer + ", question=" + question + ", questionType=" + questionType + "]";
    }

	
}