package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;


public class Digest implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String profileLevel;
    private long entityId;
    private String entityName;
    private int month;
    private int year;
    private double averageScoreRating;
    private long userCount;
    private long totalTransactions;
    private long completedTransactions;
    private double surveyCompletionRate;
    private Double sps;
    private long promoters;
    private long detractors;
    private long passives;
    private long totalCompletedReviews;
    private Double nps;
    private long npsPromoters;
    private long npsDetractors;
    private long npsPassives;
    private boolean isDigestRecordNull;
    private boolean hasZeroUserCount;


    public String getProfileLevel()
    {
        return profileLevel;
    }


    public void setProfileLevel( String profileLevel )
    {
        this.profileLevel = profileLevel;
    }


    public long getEntityId()
    {
        return entityId;
    }


    public void setEntityId( long entityId )
    {
        this.entityId = entityId;
    }


    public String getEntityName()
    {
        return entityName;
    }


    public void setEntityName( String entityName )
    {
        this.entityName = entityName;
    }


    public int getMonth()
    {
        return month;
    }


    public void setMonth( int month )
    {
        this.month = month;
    }


    public int getYear()
    {
        return year;
    }


    public void setYear( int year )
    {
        this.year = year;
    }


    public double getAverageScoreRating()
    {
        return averageScoreRating;
    }


    public void setAverageScoreRating( double averageScoreRating )
    {
        this.averageScoreRating = averageScoreRating;
    }


    public long getUserCount()
    {
        return userCount;
    }


    public void setUserCount( long userCount )
    {
        this.userCount = userCount;
    }


    public long getTotalTransactions()
    {
        return totalTransactions;
    }


    public void setTotalTransactions( long totalTransactions )
    {
        this.totalTransactions = totalTransactions;
    }


    public long getCompletedTransactions()
    {
        return completedTransactions;
    }


    public void setCompletedTransactions( long completedTransactions )
    {
        this.completedTransactions = completedTransactions;
    }


    public double getSurveyCompletionRate()
    {
        return surveyCompletionRate;
    }


    public void setSurveyCompletionRate( double surveyCompletionRate )
    {
        this.surveyCompletionRate = surveyCompletionRate;
    }


    public Double getSps()
    {
        return sps;
    }


    public void setSps( Double sps )
    {
        this.sps = sps;
    }


    public long getPromoters()
    {
        return promoters;
    }


    public void setPromoters( long promoters )
    {
        this.promoters = promoters;
    }


    public long getDetractors()
    {
        return detractors;
    }


    public void setDetractors( long detractors )
    {
        this.detractors = detractors;
    }


    public long getPassives()
    {
        return passives;
    }


    public void setPassives( long passives )
    {
        this.passives = passives;
    }


    public long getTotalCompletedReviews()
    {
        return totalCompletedReviews;
    }


    public void setTotalCompletedReviews( long totalCompletedReviews )
    {
        this.totalCompletedReviews = totalCompletedReviews;
    }


    public Double getNps()
    {
        return nps;
    }


    public void setNps( Double nps )
    {
        this.nps = nps;
    }


    public long getNpsPromoters()
    {
        return npsPromoters;
    }


    public void setNpsPromoters( long npsPromoters )
    {
        this.npsPromoters = npsPromoters;
    }


    public long getNpsDetractors()
    {
        return npsDetractors;
    }


    public void setNpsDetractors( long npsDetractors )
    {
        this.npsDetractors = npsDetractors;
    }


    public long getNpsPassives()
    {
        return npsPassives;
    }


    public void setNpsPassives( long npsPassives )
    {
        this.npsPassives = npsPassives;
    }


    public boolean isDigestRecordNull()
    {
        return isDigestRecordNull;
    }


    public void setDigestRecordNull( boolean isDigestRecordNull )
    {
        this.isDigestRecordNull = isDigestRecordNull;
    }


    public boolean getHasZeroUserCount()
    {
        return hasZeroUserCount;
    }


    public void setHasZeroUserCount( boolean hasZeroUserCount )
    {
        this.hasZeroUserCount = hasZeroUserCount;
    }


    @Override
    public String toString()
    {
        return "Digest [profileLevel=" + profileLevel + ", entityId=" + entityId + ", entityName=" + entityName + ", month="
            + month + ", year=" + year + ", averageScoreRating=" + averageScoreRating + ", userCount=" + userCount
            + ", totalTransactions=" + totalTransactions + ", completedTransactions=" + completedTransactions
            + ", surveyCompletionRate=" + surveyCompletionRate + ", sps=" + sps + ", promoters=" + promoters + ", detractors="
            + detractors + ", passives=" + passives + ", totalCompletedReviews=" + totalCompletedReviews + ", nps=" + nps
            + ", npsPromoters=" + npsPromoters + ", npsDetractors=" + npsDetractors + ", npsPassives=" + npsPassives
            + ", isDigestRecordNull=" + isDigestRecordNull + ", hasZeroUserCount=" + hasZeroUserCount + "]";
    }
}
