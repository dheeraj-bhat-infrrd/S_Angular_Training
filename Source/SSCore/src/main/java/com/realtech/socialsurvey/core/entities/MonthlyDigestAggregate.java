package com.realtech.socialsurvey.core.entities;

import java.util.List;


public class MonthlyDigestAggregate
{

    private long companyId;
    private String companyName;
    private String recipientMailId;
    private String MonthUnderConcern;
    private String yearUnderConcern;
    private List<DigestTemplateData> digestList;
    private String avgRatingTxt;
    private String surveyPercentageTxt;
    private String statisfactionRatingTxt;

    // contains rank, name, average score and total reviews  
    private String userRankingHtmlRows;


    public long getCompanyId()
    {
        return companyId;
    }


    public void setCompanyId( long companyId )
    {
        this.companyId = companyId;
    }


    public String getCompanyName()
    {
        return companyName;
    }


    public void setCompanyName( String companyName )
    {
        this.companyName = companyName;
    }


    public String getRecipientMailId()
    {
        return recipientMailId;
    }


    public void setRecipientMailId( String recipientMailId )
    {
        this.recipientMailId = recipientMailId;
    }


    public String getMonthUnderConcern()
    {
        return MonthUnderConcern;
    }


    public void setMonthUnderConcern( String monthUnderConcern )
    {
        MonthUnderConcern = monthUnderConcern;
    }


    public String getYearUnderConcern()
    {
        return yearUnderConcern;
    }


    public void setYearUnderConcern( String yearUnderConcern )
    {
        this.yearUnderConcern = yearUnderConcern;
    }


    public List<DigestTemplateData> getDigestList()
    {
        return digestList;
    }


    public void setDigestList( List<DigestTemplateData> digestList )
    {
        this.digestList = digestList;
    }


    public String getAvgRatingTxt()
    {
        return avgRatingTxt;
    }


    public void setAvgRatingTxt( String avgRatingTxt )
    {
        this.avgRatingTxt = avgRatingTxt;
    }


    public String getSurveyPercentageTxt()
    {
        return surveyPercentageTxt;
    }


    public void setSurveyPercentageTxt( String surveyPercentageTxt )
    {
        this.surveyPercentageTxt = surveyPercentageTxt;
    }


    public String getStatisfactionRatingTxt()
    {
        return statisfactionRatingTxt;
    }


    public void setStatisfactionRatingTxt( String statisfactionRatingTxt )
    {
        this.statisfactionRatingTxt = statisfactionRatingTxt;
    }


    public String getUserRankingHtmlRows()
    {
        return userRankingHtmlRows;
    }


    public void setUserRankingHtmlRows( String userRankingHtmlRows )
    {
        this.userRankingHtmlRows = userRankingHtmlRows;
    }

}

