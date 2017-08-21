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
    
    @Column ( name = "5_star")
    private int fiveStar;
    
    @Column ( name = "4_star")
    private int fourStar;
    
    @Column ( name = "3_star")
    private int threeStar;
    
    @Column ( name = "2_star")
    private int twoStar;
    
    @Column ( name = "1_star")
    private int oneStar;
    
    @Column ( name = "month_val")
    private int monthVal;
    
    @Column ( name = "year_val")
    private int yearVal;

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

	public int getFiveStar() {
		return fiveStar;
	}

	public void setFiveStar(int fiveStar) {
		this.fiveStar = fiveStar;
	}

	public int getFourStar() {
		return fourStar;
	}

	public void setFourStar(int fourStar) {
		this.fourStar = fourStar;
	}

	public int getThreeStar() {
		return threeStar;
	}

	public void setThreeStar(int threeStar) {
		this.threeStar = threeStar;
	}

	public int getTwoStar() {
		return twoStar;
	}

	public void setTwoStar(int twoStar) {
		this.twoStar = twoStar;
	}

	public int getOneStar() {
		return oneStar;
	}

	public void setOneStar(int oneStar) {
		this.oneStar = oneStar;
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

	@Override
	public String toString() {
		return "ScoreStatsQuestionUser [scoreStatsQuestionUserId=" + scoreStatsQuestionUserId + ", userId=" + userId
				+ ", questionId=" + questionId + ", question=" + question + ", fiveStar=" + fiveStar + ", fourStar="
				+ fourStar + ", threeStar=" + threeStar + ", twoStar=" + twoStar + ", oneStar=" + oneStar
				+ ", monthVal=" + monthVal + ", yearVal=" + yearVal + "]";
	}
 }
