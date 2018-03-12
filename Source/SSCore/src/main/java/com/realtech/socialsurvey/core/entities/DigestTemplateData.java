package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;


public class DigestTemplateData implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String month;
    private String year;
    private String averageScoreRating;
    private String userCount;
    private String totalTransactions;
    private String completedTransactions;
    private String surveyCompletionRate;
    private String sps;
    private String promoters;
    private String detractors;
    private String passives;
    private String totalCompletedReviews;
    private String averageScoreRatingIcon;
    private String surveyCompletionRateIcon;
    private String spsIcon;
    private String nps;
    private String npsPromoters;
    private String npsDetractors;
    private String npsPassives;
    private String totalCompletedNpsReviews;
    private String npsIcon;


    public String getMonth()
    {
        return month;
    }


    public void setMonth( String month )
    {
        this.month = month;
    }


    public String getYear()
    {
        return year;
    }


    public void setYear( String year )
    {
        this.year = year;
    }


    public String getAverageScoreRating()
    {
        return averageScoreRating;
    }


    public void setAverageScoreRating( String averageScoreRating )
    {
        this.averageScoreRating = averageScoreRating;
    }


    public String getUserCount()
    {
        return userCount;
    }


    public void setUserCount( String userCount )
    {
        this.userCount = userCount;
    }


    public String getTotalTransactions()
    {
        return totalTransactions;
    }


    public void setTotalTransactions( String totalTransactions )
    {
        this.totalTransactions = totalTransactions;
    }


    public String getCompletedTransactions()
    {
        return completedTransactions;
    }


    public void setCompletedTransactions( String completedTransactions )
    {
        this.completedTransactions = completedTransactions;
    }


    public String getSurveyCompletionRate()
    {
        return surveyCompletionRate;
    }


    public void setSurveyCompletionRate( String surveyCompletionRate )
    {
        this.surveyCompletionRate = surveyCompletionRate;
    }


    public String getSps()
    {
        return sps;
    }


    public void setSps( String sps )
    {
        this.sps = sps;
    }


    public String getPromoters()
    {
        return promoters;
    }


    public void setPromoters( String promoters )
    {
        this.promoters = promoters;
    }


    public String getDetractors()
    {
        return detractors;
    }


    public void setDetractors( String detractors )
    {
        this.detractors = detractors;
    }


    public String getPassives()
    {
        return passives;
    }


    public void setPassives( String passives )
    {
        this.passives = passives;
    }


    public String getTotalCompletedReviews()
    {
        return totalCompletedReviews;
    }


    public void setTotalCompletedReviews( String totalCompletedReviews )
    {
        this.totalCompletedReviews = totalCompletedReviews;
    }


    public String getAverageScoreRatingIcon()
    {
        return averageScoreRatingIcon;
    }


    public void setAverageScoreRatingIcon( String averageScoreRatingIcon )
    {
        this.averageScoreRatingIcon = averageScoreRatingIcon;
    }


    public String getSurveyCompletionRateIcon()
    {
        return surveyCompletionRateIcon;
    }


    public void setSurveyCompletionRateIcon( String surveyCompletionRateIcon )
    {
        this.surveyCompletionRateIcon = surveyCompletionRateIcon;
    }


    public String getSpsIcon()
    {
        return spsIcon;
    }


    public void setSpsIcon( String spsIcon )
    {
        this.spsIcon = spsIcon;
    }


    public String getNps()
    {
        return nps;
    }


    public void setNps( String nps )
    {
        this.nps = nps;
    }


    public String getNpsPromoters()
    {
        return npsPromoters;
    }


    public void setNpsPromoters( String npsPromoters )
    {
        this.npsPromoters = npsPromoters;
    }


    public String getNpsDetractors()
    {
        return npsDetractors;
    }


    public void setNpsDetractors( String npsDetractors )
    {
        this.npsDetractors = npsDetractors;
    }


    public String getNpsPassives()
    {
        return npsPassives;
    }


    public void setNpsPassives( String npsPassives )
    {
        this.npsPassives = npsPassives;
    }


    public String getTotalCompletedNpsReviews()
    {
        return totalCompletedNpsReviews;
    }


    public void setTotalCompletedNpsReviews( String totalCompletedNpsReviews )
    {
        this.totalCompletedNpsReviews = totalCompletedNpsReviews;
    }


    public String getNpsIcon()
    {
        return npsIcon;
    }


    public void setNpsIcon( String npsIcon )
    {
        this.npsIcon = npsIcon;
    }


    @Override
    public String toString()
    {
        return "DigestTemplateData [month=" + month + ", year=" + year + ", averageScoreRating=" + averageScoreRating
            + ", userCount=" + userCount + ", totalTransactions=" + totalTransactions + ", completedTransactions="
            + completedTransactions + ", surveyCompletionRate=" + surveyCompletionRate + ", sps=" + sps + ", promoters="
            + promoters + ", detractors=" + detractors + ", passives=" + passives + ", totalCompletedReviews="
            + totalCompletedReviews + ", averageScoreRatingIcon=" + averageScoreRatingIcon + ", surveyCompletionRateIcon="
            + surveyCompletionRateIcon + ", spsIcon=" + spsIcon + ", nps=" + nps + ", npsPromoters=" + npsPromoters
            + ", npsDetractors=" + npsDetractors + ", npsPassives=" + npsPassives + ", totalCompletedNpsReviews="
            + totalCompletedNpsReviews + ", npsIcon=" + npsIcon + "]";
    }

}
