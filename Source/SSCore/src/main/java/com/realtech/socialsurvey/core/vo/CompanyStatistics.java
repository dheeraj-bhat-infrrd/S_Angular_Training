package com.realtech.socialsurvey.core.vo;

import java.io.Serializable;

public class CompanyStatistics implements Serializable
{
    private static final long serialVersionUID = 1L;

    private long companyId;
    private long userCount;
    private long verifiedUserCount;
    private long regionCount;
    private long branchCount;
    private long branchVerifiedGmb;
    private long branchMissingGmb;
    private long regionVerifiedGmb;
    private long regionMissingGmb;
    private long twitterConnectionCount;
    private long facebookConnectionCount;
    private long mismatchCount;
    private long missingPhotoCountForUsers;
    private Object totalReviews;
    private long linkedinConnectionCount;
    private Object mismatchCount90Days;
    private Object completedSurveyCount90Days;
    private Object completedSurveyCountAllTime;
    private Object completedSurveyCountThisYear;
    private Object completedSurveyCountThisMonth;
    private long verifiedPercent;
    private long twitterPercent;
    private long facebookPercent;
    private long linkedInPercent;
    private long missingPhotoPercentForUsers;
    private long branchGmbPercent;
    private long regionGmbPercent;

    public CompanyStatistics()
    {
    }

    public long getCompanyId()
    {
        return companyId;
    }


    public void setCompanyId( long companyId )
    {
        this.companyId = companyId;
    }


    public long getUserCount()
    {
        return userCount;
    }


    public void setUserCount( long userCount )
    {
        this.userCount = userCount;
    }


    public long getVerifiedUserCount()
    {
        return verifiedUserCount;
    }


    public void setVerifiedUserCount( long verifiedUserCount )
    {
        this.verifiedUserCount = verifiedUserCount;
    }


    public long getRegionCount()
    {
        return regionCount;
    }


    public void setRegionCount( long regionCount )
    {
        this.regionCount = regionCount;
    }


    public long getBranchCount()
    {
        return branchCount;
    }


    public void setBranchCount( long branchCount )
    {
        this.branchCount = branchCount;
    }


    public long getBranchVerifiedGmb()
    {
        return branchVerifiedGmb;
    }


    public void setBranchVerifiedGmb( long branchVerifiedGmb )
    {
        this.branchVerifiedGmb = branchVerifiedGmb;
    }


    public long getBranchMissingGmb()
    {
        return branchMissingGmb;
    }


    public void setBranchMissingGmb( long branchMissingGmb )
    {
        this.branchMissingGmb = branchMissingGmb;
    }


    public long getTwitterConnectionCount()
    {
        return twitterConnectionCount;
    }


    public void setTwitterConnectionCount( long twitterConnectionCount )
    {
        this.twitterConnectionCount = twitterConnectionCount;
    }


    public long getFacebookConnectionCount()
    {
        return facebookConnectionCount;
    }


    public void setFacebookConnectionCount( long facebookConnectionCount )
    {
        this.facebookConnectionCount = facebookConnectionCount;
    }


    public long getMismatchCount()
    {
        return mismatchCount;
    }


    public void setMismatchCount( long mismatchCount )
    {
        this.mismatchCount = mismatchCount;
    }


    public long getMissingPhotoCountForUsers()
    {
        return missingPhotoCountForUsers;
    }


    public void setMissingPhotoCountForUsers( long missingPhotoCountForUsers )
    {
        this.missingPhotoCountForUsers = missingPhotoCountForUsers;
    }


    public Object getTotalReviews()
    {
        return totalReviews;
    }


    public void setTotalReviews( Object totalReviews )
    {
        this.totalReviews = totalReviews;
    }


    public long getLinkedinConnectionCount()
    {
        return linkedinConnectionCount;
    }


    public void setLinkedinConnectionCount( long linkedinConnectionCount )
    {
        this.linkedinConnectionCount = linkedinConnectionCount;
    }


    public Object getMismatchCount90Days()
    {
        return mismatchCount90Days;
    }


    public void setMismatchCount90Days( Object mismatchCount90Days )
    {
        this.mismatchCount90Days = mismatchCount90Days;
    }


    public Object getCompletedSurveyCount90Days()
    {
        return completedSurveyCount90Days;
    }


    public void setCompletedSurveyCount90Days( Object completedSurveyCount90Days )
    {
        this.completedSurveyCount90Days = completedSurveyCount90Days;
    }


    public Object getCompletedSurveyCountAllTime()
    {
        return completedSurveyCountAllTime;
    }


    public void setCompletedSurveyCountAllTime( Object completedSurveyCountAllTime )
    {
        this.completedSurveyCountAllTime = completedSurveyCountAllTime;
    }


    public Object getCompletedSurveyCountThisYear()
    {
        return completedSurveyCountThisYear;
    }


    public void setCompletedSurveyCountThisYear( Object completedSurveyCountThisYear )
    {
        this.completedSurveyCountThisYear = completedSurveyCountThisYear;
    }


    public Object getCompletedSurveyCountThisMonth()
    {
        return completedSurveyCountThisMonth;
    }


    public void setCompletedSurveyCountThisMonth( Object completedSurveyCountThisMonth )
    {
        this.completedSurveyCountThisMonth = completedSurveyCountThisMonth;
    }


    public long getRegionVerifiedGmb()
    {
        return regionVerifiedGmb;
    }


    public void setRegionVerifiedGmb( long regionVerifiedGmb )
    {
        this.regionVerifiedGmb = regionVerifiedGmb;
    }


    public long getRegionMissingGmb()
    {
        return regionMissingGmb;
    }


    public void setRegionMissingGmb( long regionMissingGmb )
    {
        this.regionMissingGmb = regionMissingGmb;
    }

    public long getVerifiedPercent()
    {
        return verifiedPercent;
    }


    public void setVerifiedPercent( long verifiedPercent )
    {
        this.verifiedPercent = verifiedPercent;
    }


    public long getTwitterPercent()
    {
        return twitterPercent;
    }


    public void setTwitterPercent( long twitterPercent )
    {
        this.twitterPercent = twitterPercent;
    }


    public long getFacebookPercent()
    {
        return facebookPercent;
    }


    public void setFacebookPercent( long facebookPercent )
    {
        this.facebookPercent = facebookPercent;
    }


    public long getLinkedInPercent()
    {
        return linkedInPercent;
    }


    public void setLinkedInPercent( long linkedInPercent )
    {
        this.linkedInPercent = linkedInPercent;
    }


    public long getMissingPhotoPercentForUsers()
    {
        return missingPhotoPercentForUsers;
    }


    public void setMissingPhotoPercentForUsers( long missingPhotoPercentForUsers )
    {
        this.missingPhotoPercentForUsers = missingPhotoPercentForUsers;
    }


    public long getBranchGmbPercent()
    {
        return branchGmbPercent;
    }


    public void setBranchGmbPercent( long branchGmbPercent )
    {
        this.branchGmbPercent = branchGmbPercent;
    }


    public long getRegionGmbPercent()
    {
        return regionGmbPercent;
    }


    public void setRegionGmbPercent( long regionGmbPercent )
    {
        this.regionGmbPercent = regionGmbPercent;
    }


    @Override public String toString()
    {
        return "CompanyStatistics{" + "companyId=" + companyId + ", userCount=" + userCount + ", verifiedUserCount="
            + verifiedUserCount + ", regionCount=" + regionCount + ", branchCount=" + branchCount + ", branchVerifiedGmb="
            + branchVerifiedGmb + ", branchMissingGmb=" + branchMissingGmb + ", regionVerifiedGmb=" + regionVerifiedGmb
            + ", regionMissingGmb=" + regionMissingGmb + ", twitterConnectionCount=" + twitterConnectionCount
            + ", facebookConnectionCount=" + facebookConnectionCount + ", mismatchCount=" + mismatchCount
            + ", missingPhotoCountForUsers=" + missingPhotoCountForUsers + ", totalReviews=" + totalReviews + ", linkedinConnectionCount="
            + linkedinConnectionCount + ", mismatchCount90Days=" + mismatchCount90Days + ", completedSurveyCount90Days="
            + completedSurveyCount90Days + ", completedSurveyCountAllTime=" + completedSurveyCountAllTime
            + ", completedSurveyCountThisYear=" + completedSurveyCountThisYear + ", completedSurveyCountThisMonth="
            + completedSurveyCountThisMonth + ", verifiedPercent=" + verifiedPercent + ", twitterPercent=" + twitterPercent
            + ", facebookPercent=" + facebookPercent + ", linkedInPercent=" + linkedInPercent + ", missingPhotoPercentForUsers="
            + missingPhotoPercentForUsers + ", branchGmbPercent=" + branchGmbPercent + ", regionGmbPercent=" + regionGmbPercent + '}';
    }

}
