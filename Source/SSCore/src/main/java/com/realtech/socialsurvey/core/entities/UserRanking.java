package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;


public class UserRanking implements Serializable
{
    private static final long serialVersionUID = 1L;
    private long userId;
    private long companyId;
    private long regionId;
    private long branchId;
    private int month;
    private int year;
    private String firstName;
    private String lastName;
    private String emailId;
    private float rankingScore;
    private int rank;
    private float sps;
    private int daysOfRegistration;
    private int completed;
    private int sent;
    private float completedPercentage;
    private int totalReviews;
    private float averageRating;
    private int isEligible;
    private String nmlsId;


    public long getUserId()
    {
        return userId;
    }


    public void setUserId( long userId )
    {
        this.userId = userId;
    }


    public long getCompanyId()
    {
        return companyId;
    }


    public void setCompanyId( long companyId )
    {
        this.companyId = companyId;
    }


    public long getRegionId()
    {
        return regionId;
    }


    public void setRegionId( long regionId )
    {
        this.regionId = regionId;
    }


    public long getBranchId()
    {
        return branchId;
    }


    public void setBranchId( long branchId )
    {
        this.branchId = branchId;
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


    public String getFirstName()
    {
        return firstName;
    }


    public void setFirstName( String firstName )
    {
        this.firstName = firstName;
    }


    public String getLastName()
    {
        return lastName;
    }


    public void setLastName( String lastName )
    {
        this.lastName = lastName;
    }


    public String getEmailId()
    {
        return emailId;
    }


    public void setEmailId( String emailId )
    {
        this.emailId = emailId;
    }


    public float getRankingScore()
    {
        return rankingScore;
    }


    public void setRankingScore( float rankingScore )
    {
        this.rankingScore = rankingScore;
    }


    public int getRank()
    {
        return rank;
    }


    public void setRank( int rank )
    {
        this.rank = rank;
    }


    public float getSps()
    {
        return sps;
    }


    public void setSps( float sps )
    {
        this.sps = sps;
    }


    public int getDaysOfRegistration()
    {
        return daysOfRegistration;
    }


    public void setDaysOfRegistration( int daysOfRegistration )
    {
        this.daysOfRegistration = daysOfRegistration;
    }


    public int getCompleted()
    {
        return completed;
    }


    public void setCompleted( int completed )
    {
        this.completed = completed;
    }


    public int getSent()
    {
        return sent;
    }


    public void setSent( int sent )
    {
        this.sent = sent;
    }


    public float getCompletedPercentage()
    {
        return completedPercentage;
    }


    public void setCompletedPercentage( float completedPercentage )
    {
        this.completedPercentage = completedPercentage;
    }


    public int getTotalReviews()
    {
        return totalReviews;
    }


    public void setTotalReviews( int totalReviews )
    {
        this.totalReviews = totalReviews;
    }


    public float getAverageRating()
    {
        return averageRating;
    }


    public void setAverageRating( float averageRating )
    {
        this.averageRating = averageRating;
    }


    public int getIsEligible()
    {
        return isEligible;
    }


    public void setIsEligible( int isEligible )
    {
        this.isEligible = isEligible;
    }


    public String getNmlsId()
    {
        return nmlsId;
    }


    public void setNmlsId( String nmlsId )
    {
        this.nmlsId = nmlsId;
    }


    @Override
    public String toString()
    {
        return "UserRanking [userId=" + userId + ", companyId=" + companyId + ", regionId=" + regionId + ", branchId="
            + branchId + ", month=" + month + ", year=" + year + ", firstName=" + firstName + ", lastName=" + lastName
            + ", emailId=" + emailId + ", rankingScore=" + rankingScore + ", rank=" + rank + ", sps=" + sps
            + ", daysOfRegistration=" + daysOfRegistration + ", completed=" + completed + ", sent=" + sent
            + ", completedPercentage=" + completedPercentage + ", totalReviews=" + totalReviews + ", averageRating="
            + averageRating + ", isEligible=" + isEligible + ", nmlsId=" + nmlsId + "]";
    }


}
