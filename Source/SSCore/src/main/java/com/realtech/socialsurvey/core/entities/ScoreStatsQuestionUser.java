package com.realtech.socialsurvey.core.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name= "score_stats_question_user")
public class ScoreStatsQuestionUser {

	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY)
    @Column ( name = "score_stats_question_user_id")
    private String scoreStatsQuestionUserId;
    
    @Column ( name = "user_id")
    private long userId;
    
    @Column ( name = "question_id")
    private long questionId;
    
    @Column ( name = "question")
    private String question;
    
    @Column ( name = "sum")
    private float sum;
    
    @Column ( name = "count")
    private int count;
    
    @Column ( name = "month_val")
    private int monthVal;
    
    @Column ( name = "year_val")
    private int yearVal;

    @Column ( name = "avg_score")
    private float avgScore;
    
	public String getScoreStatsQuestionUserId() {
		return scoreStatsQuestionUserId;
	}

	public void setScoreStatsQuestionUserId(String scoreStatsQuestionUserId) {
		this.scoreStatsQuestionUserId = scoreStatsQuestionUserId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

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

	public int getMonthVal() {
		return monthVal;
	}

	public void setMonthVal(int monthVal) {
		this.monthVal = monthVal;
	}

	public int getYearVal() {
		return yearVal;
	}

	public void setYearVal(int yearVal) {
		this.yearVal = yearVal;
	}

	public float getAvgScore() {
		return avgScore;
	}

	public void setAvgScore(float avgScore) {
		this.avgScore = avgScore;
	}

	
	public float getSum()
    {
        return sum;
    }

    public void setSum( float sum )
    {
        this.sum = sum;
    }

    public int getCount()
    {
        return count;
    }

    public void setCount( int count )
    {
        this.count = count;
    }

    public static long getSerialversionuid()
    {
        return serialVersionUID;
    }

    @Override
    public String toString()
    {
        return "ScoreStatsQuestionUser [scoreStatsQuestionUserId=" + scoreStatsQuestionUserId + ", userId=" + userId
            + ", questionId=" + questionId + ", question=" + question + ", sum=" + sum + ", count=" + count + ", monthVal="
            + monthVal + ", yearVal=" + yearVal + ", avgScore=" + avgScore + "]";
    }

}
