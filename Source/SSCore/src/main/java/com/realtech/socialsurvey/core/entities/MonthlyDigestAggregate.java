package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.util.List;
import java.util.Set;


public class MonthlyDigestAggregate implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String profileLevel;
    private long entityId;
    private String entityName;
    private Set<String> recipientMailIds;
    private String monthUnderConcern;
    private String yearUnderConcern;
    private List<DigestTemplateData> digestList;
    private String avgRatingTxt;
    private String surveyPercentageTxt;
    private String statisfactionRatingTxt;
    private boolean isDigestDataAbsent;
    private boolean isUserCountDataAbsent;

    // contains rank, name, average score and total reviews
    private String userRankingHtmlSection;

    // for those companies who have enabled NPS question
    private String npsHtmlsection;

    private boolean havingNpsSection;

    private String npsInferenceTxt;;


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


    public String getUserRankingHtmlSection()
    {
        return userRankingHtmlSection;
    }


    public void setUserRankingHtmlSection( String userRankingHtmlSection )
    {
        this.userRankingHtmlSection = userRankingHtmlSection;
    }


    public String getNpsHtmlsection()
    {
        return npsHtmlsection;
    }


    public void setNpsHtmlsection( String npsHtmlsection )
    {
        this.npsHtmlsection = npsHtmlsection;
    }


    public boolean isHavingNpsSection()
    {
        return havingNpsSection;
    }


    public void setHavingNpsSection( boolean hasNpsSection )
    {
        this.havingNpsSection = hasNpsSection;
    }


    public String getNpsInferenceTxt()
    {
        return npsInferenceTxt;
    }


    public void setNpsInferenceTxt( String npsInferenceTxt )
    {
        this.npsInferenceTxt = npsInferenceTxt;
    }


    public boolean isDigestDataAbsent()
    {
        return isDigestDataAbsent;
    }


    public void setDigestDataAbsent( boolean isDigestDataAbsent )
    {
        this.isDigestDataAbsent = isDigestDataAbsent;
    }


    public boolean isUserCountDataAbsent()
    {
        return isUserCountDataAbsent;
    }


    public void setUserCountDataAbsent( boolean isUserCountDataAbsent )
    {
        this.isUserCountDataAbsent = isUserCountDataAbsent;
    }


    @Override
    public String toString()
    {
        return "MonthlyDigestAggregate [profileLevel=" + profileLevel + ", entityId=" + entityId + ", entityName=" + entityName
            + ", recipientMailIds=" + recipientMailIds + ", monthUnderConcern=" + monthUnderConcern + ", yearUnderConcern="
            + yearUnderConcern + ", digestList=" + digestList + ", avgRatingTxt=" + avgRatingTxt + ", surveyPercentageTxt="
            + surveyPercentageTxt + ", statisfactionRatingTxt=" + statisfactionRatingTxt + ", isDigestDataAbsent="
            + isDigestDataAbsent + ", isUserCountDataAbsent=" + isUserCountDataAbsent + ", userRankingHtmlSection="
            + userRankingHtmlSection + ", npsHtmlsection=" + npsHtmlsection + ", havingNpsSection=" + havingNpsSection
            + ", npsInferenceTxt=" + npsInferenceTxt + "]";
    }
}
