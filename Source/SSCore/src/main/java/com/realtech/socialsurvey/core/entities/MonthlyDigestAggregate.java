package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.util.List;
import java.util.Set;


public class MonthlyDigestAggregate implements Serializable
{
    private static final long serialVersionUID = 1L;
    private long companyId;
    private String companyName;
    private Set<String> recipientMailIds;
    private String monthUnderConcern;
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


    public Set<String> getRecipientMailIds()
    {
        return recipientMailIds;
    }


    public void setRecipientMailIds( Set<String> recipientMailIds )
    {
        this.recipientMailIds = recipientMailIds;
    }


    public String getMonthUnderConcern()
    {
        return this.monthUnderConcern;
    }


    public void setMonthUnderConcern( String monthUnderConcern )
    {
        this.monthUnderConcern = monthUnderConcern;
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


    @Override
    public String toString()
    {
        return "MonthlyDigestAggregate [companyId=" + companyId + ", companyName=" + companyName + ", recipientMailIds="
            + recipientMailIds + ", monthUnderConcern=" + monthUnderConcern + ", yearUnderConcern=" + yearUnderConcern
            + ", digestList=" + digestList + ", avgRatingTxt=" + avgRatingTxt + ", surveyPercentageTxt=" + surveyPercentageTxt
            + ", statisfactionRatingTxt=" + statisfactionRatingTxt + ", userRankingHtmlRows=" + userRankingHtmlRows + "]";
    }
}

