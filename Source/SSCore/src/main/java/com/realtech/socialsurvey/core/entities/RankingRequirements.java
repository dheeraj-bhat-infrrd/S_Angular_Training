package com.realtech.socialsurvey.core.entities;

public class RankingRequirements{
    
    private int minDaysOfRegistration;
    private float minCompletedPercentage;
    private int minNoOfReviews;
    private int monthOffset;
    private int yearOffset;
    public int getMinDaysOfRegistration()
    {
        return minDaysOfRegistration;
    }
    public void setMinDaysOfRegistration( int minDaysOfRegistration )
    {
        this.minDaysOfRegistration = minDaysOfRegistration;
    }
    public float getMinCompletedPercentage()
    {
        return minCompletedPercentage;
    }
    public void setMinCompletedPercentage( float minCompletedPercentage )
    {
        this.minCompletedPercentage = minCompletedPercentage;
    }
    public int getMinNoOfReviews()
    {
        return minNoOfReviews;
    }
    public void setMinNoOfReviews( int minNoOfReviews )
    {
        this.minNoOfReviews = minNoOfReviews;
    }
    public int getMonthOffset()
    {
        return monthOffset;
    }
    public void setMonthOffset( int monthOffset )
    {
        this.monthOffset = monthOffset;
    }
    public int getYearOffset()
    {
        return yearOffset;
    }
    public void setYearOffset( int yearOffset )
    {
        this.yearOffset = yearOffset;
    }
    
    @Override
    public String toString()
    {
        return "RankingRequirements [minDaysOfRegistration=" + minDaysOfRegistration + ", minCompletedPercentage="
            + minCompletedPercentage + ", minNoOfReviews=" + minNoOfReviews + ", monthOffset=" + monthOffset + ", yearOffset="
            + yearOffset + "]";
    }
   
    

}
