package com.realtech.socialsurvey.core.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name= "score_stats_overall_company")
public class ScoreStatsOverallCompany {

	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY)
    @Column ( name = "score_stats_overall_company_id")
    private String scoreStatsOverallCompanyId;
    
    @Column ( name = "company_id")
    private long companyId;
    
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
    
	public String getScoreStatsOverallCompanyId() {
		return scoreStatsOverallCompanyId;
	}

	public void setScoreStatsOverallCompanyId(String scoreStatsOverallCompanyId) {
		this.scoreStatsOverallCompanyId = scoreStatsOverallCompanyId;
	}

	public long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
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

    public float getAvgScore() {
		return avgScore;
	}

	public void setAvgScore(float avgScore) {
		this.avgScore = avgScore;
	}

	@Override
    public String toString()
    {
        return "ScoreStatsOverallCompany [scoreStatsOverallCompanyId=" + scoreStatsOverallCompanyId + ", companyId=" + companyId
            + ", sum=" + sum + ", count=" + count + ", monthVal=" + monthVal + ", yearVal=" + yearVal + ", avgScore=" + avgScore
            + "]";
    }

    public static long getSerialversionuid()
    {
        return serialVersionUID;
    }

}
