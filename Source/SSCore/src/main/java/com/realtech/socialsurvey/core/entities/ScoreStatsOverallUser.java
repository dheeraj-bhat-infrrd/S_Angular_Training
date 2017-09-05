package com.realtech.socialsurvey.core.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name= "score_stats_overall_user")
public class ScoreStatsOverallUser {

	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY)
    @Column ( name = "score_stats_overall_user_id")
    private String scoreStatsOverallUserId;
    
    @Column ( name = "user_id")
    private long userId;
    
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

    @Column ( name = "avg_score")
    private float avgScore;
    
	public String getScoreStatsOverallUserId() {
		return scoreStatsOverallUserId;
	}

	public void setScoreStatsOverallUserId(String scoreStatsOverallUserId) {
		this.scoreStatsOverallUserId = scoreStatsOverallUserId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
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

	public float getAvgScore() {
		return avgScore;
	}

	public void setAvgScore(float avgScore) {
		this.avgScore = avgScore;
	}

	@Override
	public String toString() {
		return "ScoreStatsOverallUser [scoreStatsOverallUserId=" + scoreStatsOverallUserId + ", userId=" + userId
				+ ", fiveStar=" + fiveStar + ", fourStar=" + fourStar + ", threeStar=" + threeStar + ", twoStar="
				+ twoStar + ", oneStar=" + oneStar + ", monthVal=" + monthVal + ", yearVal=" + yearVal + ", avgScore="
				+ avgScore + "]";
	}
	
}
