package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * The persistant class for OverviewUser table in centrelised db
 *
 */
@Entity
@Table(name="overview_user")
//@NamedQuery(name="overview_user.findAll", query="SELECT a FROM overview_user a")
public class OverviewUser implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY)
    @Column ( name = "overview_user_id")
    private String overviewUserId;

    @Column ( name = "user_id")
    private long userId;

    @Column ( name = "sps_score")
    private float spsScore;

    @Column ( name = "total_detractors")
    private int totalDetractors;

    @Column ( name = "detractors_percentage")
    private float detractorPercentage;

    @Column ( name = "total_passives")
    private int totalPassives;

    @Column ( name = "passives_percentage")
    private float passivesPercentage;

    @Column ( name = "total_promoters")
    private int totalPromoters;

    @Column ( name = "promoter_percentage")
    private float promoterPercentage;

    @Column ( name = "total_corrupted")
    private int totalCorrupted;

    @Column ( name = "unassigned")
    private int unassigned;

    @Column ( name = "total_duplicate")
    private int totalDuplicate;

    @Column ( name = "total_archieved")
    private int totalArchieved;

    @Column ( name = "incomplete")
    private int incomplete;

    @Column ( name = "total_survey_sent")
    private int totalSurveySent;

    @Column ( name = "completed")
    private int completed;

    @Column ( name = "social_posts")
    private int socialPosts;

    @Column ( name = "zillow_reviews")
    private int zillowReviews;
    
    @Column ( name = "total_reviews")
    private int totalReviews;
    
    @Column ( name = "third_party")
    private int thirdParty;
    
    @Column ( name = "rating")
    private float rating;
    
    @Column ( name = "completed_percentage")
    private float completedPercentage;
    
    @Column ( name = "incomplete_percentage")
    private float incompletePercentage;
    
    @Column ( name = "processed")
    private int processed;
    
    @Column ( name = "unprocessed")
    private int unprocessed;
    
    @Column ( name = "nps_score")
    private Double npsScore;

    @Column ( name = "nps_detractors")
    private int npsDetractors;

    @Column ( name = "nps_detractors_percentage")
    private float npsDetractorPercentage;

    @Column ( name = "nps_passives")
    private int npsPassives;

    @Column ( name = "nps_passives_percentage")
    private float npsPassivesPercentage;

    @Column ( name = "nps_promoters")
    private int npsPromoters;

    @Column ( name = "nps_promoter_percentage")
    private float npsPromoterPercentage;
    
    @Column ( name = "unsubscribed_count")
    private int unsubscribed;
    
    @Column ( name = "google")
    private int google;
    
    @Column ( name = "faceebook")
    private int facebook;

    public String getOverviewUserId()
    {
        return overviewUserId;
    }

    public void setOverviewUserId( String overviewUserId )
    {
        this.overviewUserId = overviewUserId;
    }

    public long getUserId()
    {
        return userId;
    }

    public void setUserId( long userId )
    {
        this.userId = userId;
    }

    public float getSpsScore()
    {
        return spsScore;
    }

    public void setSpsScore( float spsScore )
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

    public float getDetractorPercentage()
    {
        return detractorPercentage;
    }

    public void setDetractorPercentage( float detractorPercentage )
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

    public float getPassivesPercentage()
    {
        return passivesPercentage;
    }

    public void setPassivesPercentage( float passivesPercentage )
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

    public float getPromoterPercentage()
    {
        return promoterPercentage;
    }

    public void setPromoterPercentage( float promoterPercentage )
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

    public int getUnassigned()
    {
        return unassigned;
    }

    public void setUnassigned( int unassigned )
    {
        this.unassigned = unassigned;
    }

    public int getTotalDuplicate()
    {
        return totalDuplicate;
    }

    public void setTotalDuplicate( int totalDuplicate )
    {
        this.totalDuplicate = totalDuplicate;
    }

    public int getTotalArchieved()
    {
        return totalArchieved;
    }

    public void setTotalArchieved( int totalArchieved )
    {
        this.totalArchieved = totalArchieved;
    }

    public int getIncomplete()
    {
        return incomplete;
    }

    public void setIncomplete( int incomplete )
    {
        this.incomplete = incomplete;
    }

    public int getTotalSurveySent()
    {
        return totalSurveySent;
    }

    public void setTotalSurveySent( int totalSurveySent )
    {
        this.totalSurveySent = totalSurveySent;
    }

    public int getCompleted()
    {
        return completed;
    }

    public void setCompleted( int completed )
    {
        this.completed = completed;
    }

    public int getSocialPosts()
    {
        return socialPosts;
    }

    public void setSocialPosts( int socialPosts )
    {
        this.socialPosts = socialPosts;
    }

    public int getZillowReviews()
    {
        return zillowReviews;
    }

    public void setZillowReviews( int zillowReviews )
    {
        this.zillowReviews = zillowReviews;
    }

    public int getTotalReviews()
    {
        return totalReviews;
    }

    public void setTotalReviews( int totalReviews )
    {
        this.totalReviews = totalReviews;
    }

    public float getRating()
    {
        return rating;
    }

    public void setRating( float rating )
    {
        this.rating = rating;
    }

    public float getCompletedPercentage()
    {
        return completedPercentage;
    }

    public void setCompletedPercentage( float completedPercentage )
    {
        this.completedPercentage = completedPercentage;
    }

    public float getIncompletePercentage()
    {
        return incompletePercentage;
    }

    public void setIncompletePercentage( float incompletePercentage )
    {
        this.incompletePercentage = incompletePercentage;
    }

    public int getProcessed()
    {
        return processed;
    }

    public void setProcessed( int processed )
    {
        this.processed = processed;
    }

    public int getUnprocessed()
    {
        return unprocessed;
    }

    public void setUnprocessed( int unprocessed )
    {
        this.unprocessed = unprocessed;
    }
   
    public int getThirdParty()
    {
        return thirdParty;
    }

    public void setThirdParty( int thirdParty )
    {
        this.thirdParty = thirdParty;
    }

    
    public Double getNpsScore()
    {
        return npsScore;
    }

    public void setNpsScore( Double npsScore )
    {
        this.npsScore = npsScore;
    }

    public int getNpsDetractors()
    {
        return npsDetractors;
    }

    public void setNpsDetractors( int npsDetractors )
    {
        this.npsDetractors = npsDetractors;
    }

    public float getNpsDetractorPercentage()
    {
        return npsDetractorPercentage;
    }

    public void setNpsDetractorPercentage( float npsDetractorPercentage )
    {
        this.npsDetractorPercentage = npsDetractorPercentage;
    }

    public int getNpsPassives()
    {
        return npsPassives;
    }

    public void setNpsPassives( int npsPassives )
    {
        this.npsPassives = npsPassives;
    }

    public float getNpsPassivesPercentage()
    {
        return npsPassivesPercentage;
    }

    public void setNpsPassivesPercentage( float npsPassivesPercentage )
    {
        this.npsPassivesPercentage = npsPassivesPercentage;
    }

    public int getNpsPromoters()
    {
        return npsPromoters;
    }

    public void setNpsPromoters( int npsPromoters )
    {
        this.npsPromoters = npsPromoters;
    }

    public float getNpsPromoterPercentage()
    {
        return npsPromoterPercentage;
    }

    public void setNpsPromoterPercentage( float npsPromoterPercentage )
    {
        this.npsPromoterPercentage = npsPromoterPercentage;
    }

    public static long getSerialversionuid()
    {
        return serialVersionUID;
    }

    public int getUnsubscribed()
    {
        return unsubscribed;
    }

    public void setUnsubscribed( int unsubscribed )
    {
        this.unsubscribed = unsubscribed;
    }

    public int getGoogle()
    {
        return google;
    }

    public void setGoogle( int google )
    {
        this.google = google;
    }

    public int getFacebook()
    {
        return facebook;
    }

    public void setFacebook( int facebook )
    {
        this.facebook = facebook;
    }

    @Override
    public String toString()
    {
        return "OverviewUser [overviewUserId=" + overviewUserId + ", userId=" + userId + ", spsScore=" + spsScore
            + ", totalDetractors=" + totalDetractors + ", detractorPercentage=" + detractorPercentage + ", totalPassives="
            + totalPassives + ", passivesPercentage=" + passivesPercentage + ", totalPromoters=" + totalPromoters
            + ", promoterPercentage=" + promoterPercentage + ", totalCorrupted=" + totalCorrupted + ", unassigned=" + unassigned
            + ", totalDuplicate=" + totalDuplicate + ", totalArchieved=" + totalArchieved + ", incomplete=" + incomplete
            + ", totalSurveySent=" + totalSurveySent + ", completed=" + completed + ", socialPosts=" + socialPosts
            + ", zillowReviews=" + zillowReviews + ", totalReviews=" + totalReviews + ", thirdParty=" + thirdParty + ", rating="
            + rating + ", completedPercentage=" + completedPercentage + ", incompletePercentage=" + incompletePercentage
            + ", processed=" + processed + ", unprocessed=" + unprocessed + ", npsScore=" + npsScore + ", npsDetractors="
            + npsDetractors + ", npsDetractorPercentage=" + npsDetractorPercentage + ", npsPassives=" + npsPassives
            + ", npsPassivesPercentage=" + npsPassivesPercentage + ", npsPromoters=" + npsPromoters + ", npsPromoterPercentage="
            + npsPromoterPercentage + ", unsubscribed=" + unsubscribed + ", google=" + google + ", facebook=" + facebook + "]";
    }

}