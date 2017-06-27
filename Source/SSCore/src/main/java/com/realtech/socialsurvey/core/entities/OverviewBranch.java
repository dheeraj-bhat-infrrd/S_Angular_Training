package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


@Entity
@Table ( name = "overview_branch")
//@NamedQuery ( name = "overview_branch.findAll", query = "SELECT a FROM overview_branch a")
public class OverviewBranch implements Serializable
{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY)
    @Column ( name = "overview_branch_id")
    private String overviewBranchId;

    @Column ( name = "branch_id")
    private int branchId;

    @Column ( name = "sps_score")
    private int spsScore;

    @Column ( name = "total_detractors")
    private int totalDetractors;

    @Column ( name = "detractors_percentage")
    private int detractorPercentage;

    @Column ( name = "total_passives")
    private int totalPassives;

    @Column ( name = "passives_percentage")
    private int passivesPercentage;

    @Column ( name = "total_promoters")
    private int totalPromoters;

    @Column ( name = "promoter_percentage")
    private int promoterPercentage;

    @Column ( name = "total_corrupted")
    private int totalCorrupted;

    @Column ( name = "corrupted_percentage")
    private int corruptedPercentage;

    @Column ( name = "total_mismatched")
    private int totalMismatched;

    @Column ( name = "mismatched_percentage")
    private int mismatchedPercentage;

    @Column ( name = "total_duplicate")
    private int totalDuplicate;

    @Column ( name = "duplicate_percentage")
    private int duplicatePercentage;

    @Column ( name = "total_archieved")
    private int totalArchieved;

    @Column ( name = "archieved_percentage")
    private int archievedPercentage;

    @Column ( name = "total_incomplete_transactions")
    private int totalIncompleteTransactions;

    @Column ( name = "total_survey_sent")
    private int totalSurveySent;

    @Column ( name = "total_survey_completed")
    private int totalSurveyCompleted;

    @Column ( name = "total_social_post")
    private int totalSocialPost;

    @Column ( name = "total_zillow_reviews")
    private int totalZillowReviews;


    public String getOverviewBranchId()
    {
        return overviewBranchId;
    }


    public void setOverviewBranchId( String overviewBranchId )
    {
        this.overviewBranchId = overviewBranchId;
    }


    public int getBranchId()
    {
        return branchId;
    }


    public void setBranchId( int branchId )
    {
        this.branchId = branchId;
    }


    public int getSpsScore()
    {
        return spsScore;
    }


    public void setSpsScore( int spsScore )
    {
        this.spsScore = spsScore;
    }


    public int getTotalDetractors()
    {
        return totalDetractors;
    }


    public void setTotalDetractors( int totalDetractors )
    {
        this.totalDetractors = totalDetractors;
    }


    public int getDetractorPercentage()
    {
        return detractorPercentage;
    }


    public void setDetractorPercentage( int detractorPercentage )
    {
        this.detractorPercentage = detractorPercentage;
    }


    public int getTotalPassives()
    {
        return totalPassives;
    }


    public void setTotalPassives( int totalPassives )
    {
        this.totalPassives = totalPassives;
    }


    public int getPassivesPercentage()
    {
        return passivesPercentage;
    }


    public void setPassivesPercentage( int passivesPercentage )
    {
        this.passivesPercentage = passivesPercentage;
    }


    public int getTotalPromoters()
    {
        return totalPromoters;
    }


    public void setTotalPromoters( int totalPromoters )
    {
        this.totalPromoters = totalPromoters;
    }


    public int getPromoterPercentage()
    {
        return promoterPercentage;
    }


    public void setPromoterPercentage( int promoterPercentage )
    {
        this.promoterPercentage = promoterPercentage;
    }


    public int getTotalCorrupted()
    {
        return totalCorrupted;
    }


    public void setTotalCorrupted( int totalCorrupted )
    {
        this.totalCorrupted = totalCorrupted;
    }


    public int getCorruptedPercentage()
    {
        return corruptedPercentage;
    }


    public void setCorruptedPercentage( int corruptedPercentage )
    {
        this.corruptedPercentage = corruptedPercentage;
    }


    public int getTotalMismatched()
    {
        return totalMismatched;
    }


    public void setTotalMismatched( int totalMismatched )
    {
        this.totalMismatched = totalMismatched;
    }


    public int getMismatchedPercentage()
    {
        return mismatchedPercentage;
    }


    public void setMismatchedPercentage( int mismatchedPercentage )
    {
        this.mismatchedPercentage = mismatchedPercentage;
    }


    public int getTotalDuplicate()
    {
        return totalDuplicate;
    }


    public void setTotalDuplicate( int totalDuplicate )
    {
        this.totalDuplicate = totalDuplicate;
    }


    public int getDuplicatePercentage()
    {
        return duplicatePercentage;
    }


    public void setDuplicatePercentage( int duplicatePercentage )
    {
        this.duplicatePercentage = duplicatePercentage;
    }


    public int getTotalArchieved()
    {
        return totalArchieved;
    }


    public void setTotalArchieved( int totalArchieved )
    {
        this.totalArchieved = totalArchieved;
    }


    public int getArchievedPercentage()
    {
        return archievedPercentage;
    }


    public void setArchievedPercentage( int archievedPercentage )
    {
        this.archievedPercentage = archievedPercentage;
    }


    public int getTotalIncompleteTransactions()
    {
        return totalIncompleteTransactions;
    }


    public void setTotalIncompleteTransactions( int totalIncompleteTransactions )
    {
        this.totalIncompleteTransactions = totalIncompleteTransactions;
    }


    public int getTotalSurveySent()
    {
        return totalSurveySent;
    }


    public void setTotalSurveySent( int totalSurveySent )
    {
        this.totalSurveySent = totalSurveySent;
    }


    public int getTotalSurveyCompleted()
    {
        return totalSurveyCompleted;
    }


    public void setTotalSurveyCompleted( int totalSurveyCompleted )
    {
        this.totalSurveyCompleted = totalSurveyCompleted;
    }


    public int getTotalSocialPost()
    {
        return totalSocialPost;
    }


    public void setTotalSocialPost( int totalSocialPost )
    {
        this.totalSocialPost = totalSocialPost;
    }


    public int getTotalZillowReviews()
    {
        return totalZillowReviews;
    }


    public void setTotalZillowReviews( int totalZillowReviews )
    {
        this.totalZillowReviews = totalZillowReviews;
    }


    public static long getSerialversionuid()
    {
        return serialVersionUID;
    }


    @Override
    public String toString() {
        return "OverviewBranch [overviewBranchId=" + overviewBranchId + ", branchId=" + branchId + ", spsScore=" + spsScore + ", totalDetractors=" + totalDetractors
                + ", detractorPercentage=" + detractorPercentage + ", totalPassives=" + totalPassives + ", passivesPercentage=" + passivesPercentage 
                + ", totalPromoters=" + totalPromoters + ", promoterPercentage=" + ", promoterPercentage=" + totalCorrupted + ", totalCorrupted=" +", "
                + "corruptedPercentage=" + corruptedPercentage + ", totalMismatched=" + totalMismatched + ", mismatchedPercentage=" + mismatchedPercentage + ", totalDuplicate=" +
                totalDuplicate + ", duplicatePercentage=" +duplicatePercentage + ", totalArchieved=" +totalArchieved + ", archievedPercentage=" +archievedPercentage + ", totalIncompleteTransactions=" +
                totalIncompleteTransactions +", totalSurveySent=" +totalSurveySent +", totalSurveyCompleted=" +totalSurveyCompleted +", totalSocialPost=" +totalSocialPost +", totalZillowReviews=" +totalZillowReviews+ "]";
    }
}
