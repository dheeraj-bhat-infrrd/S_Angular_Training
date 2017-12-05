package com.realtech.socialsurvey.core.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name= "score_stats_overall_region")
public class ScoreStatsOverallRegion {

	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY)
    @Column ( name = "score_stats_overall_region_id")
    private String scoreStatsOverallRegionId;
    
    @Column ( name = "region_id")
    private long regionId;
    
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

	public String getScoreStatsOverallRegionId() {
		return scoreStatsOverallRegionId;
	}

	public void setScoreStatsOverallRegionId(String scoreStatsOverallRegionId) {
		this.scoreStatsOverallRegionId = scoreStatsOverallRegionId;
	}
	
	public long getRegionId() {
		return regionId;
	}	

	public void setRegionId(long regionId) {
		this.regionId = regionId;
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
    public String toString()
    {
        return "ScoreStatsOverallRegion [scoreStatsOverallRegionId=" + scoreStatsOverallRegionId + ", regionId=" + regionId
            + ", sum=" + sum + ", count=" + count + ", monthVal=" + monthVal + ", yearVal=" + yearVal + ", avgScore=" + avgScore
            + "]";
    }

}
