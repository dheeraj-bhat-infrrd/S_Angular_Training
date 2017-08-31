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
	
	@Column(name = "ANSWER")
	private String answer;
	
	@Column(name = " QUESTION")
	private String question;
	
	@Column(name = "QUESTION_TYPE")
	private String questionType;
	
	// bi-directional many-to-one association to Survey Results 
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name ="SURVEY_DETAILS_ID" , referencedColumnName  = "SURVEY_DETAILS_ID" )
    private SurveyResultsCompanyReport surveyResultsCompanyReport;


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
	
	

	public SurveyResultsCompanyReport getSurveyResultsCompanyReport()
    {
        return surveyResultsCompanyReport;
    }

    public void setSurveyResultsCompanyReport( SurveyResultsCompanyReport surveyResultsCompanyReport )
    {
        this.surveyResultsCompanyReport = surveyResultsCompanyReport;
    }

    @Override
    public String toString()
    {
        return "SurveyResponseTable [surveyResponseId=" + surveyResponseId + ", answer=" + answer + ", question=" + question + ", questionType=" + questionType
            + ", surveyResultsCompanyReport=" + surveyResultsCompanyReport + "]";
    }

	
}