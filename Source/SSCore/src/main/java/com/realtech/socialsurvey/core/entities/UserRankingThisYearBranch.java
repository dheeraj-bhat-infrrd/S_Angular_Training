package com.realtech.socialsurvey.core.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name= "user_ranking_this_year_branch")
public class UserRankingThisYearBranch
{
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY)
    @Column ( name = "user_ranking_this_year_branch_id")
    private String userRankingThisYearBranchId;
    
    @Column ( name = "user_id")
    private long userId;
    
    @Column( name = "company_id")
    private long companyId;
    
    @Column( name = "region_id")
    private long regionId;
    
    @Column(name = "branch_id")
    private long branchId;
    
    @Column( name = "this_year")
    private  int thisYear;
    
    @Column( name = "first_name")
    private String firstName;
    
    @Column(name = "last_name")
    private String lastName;
    
    @Column(name = "email_id")
    private String emailId;
    
    @Column(name = "ranking_score")
    private float rankingScore;
    
    @Column(name = "rank")
    private int rank;
    
    @Column(name = "sps")
    private float sps;
    
    @Column(name = "days_of_registration")
    private int daysOfRegistration;
    
    @Column(name = "completed")
    private int completed;
    
    @Column(name = "sent")
    private int sent;
    
    @Column(name = "completed_percentage")
    private float completedPercentage;
    
    @Column(name = "total_reviews")
    private int totalReviews;
    
    @Column(name = "average_rating")
    private float averageRating;
    
    @Column(name = "is_eligible")
    private	int isEligible;
    
    @Column(name = "internal_branch_rank")
    int internalBranchRank;
    
    @Column(name = "nmls_id")
    private String nmlsId;     
    
    @Transient
    private String profileImageUrlThumbnail;
   
	public float getAverageRating() {
		return averageRating;
	}

	public void setAverageRating(float averageRating) {
		this.averageRating = averageRating;
	}

	public String getUserRankingThisYearBranchId() {
		return userRankingThisYearBranchId;
	}

	public void setUserRankingThisYearBranchId(String userRankingThisYearBranchId) {
		this.userRankingThisYearBranchId = userRankingThisYearBranchId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

	public long getBranchId() {
		return branchId;
	}

	public void setBranchId(long branchId) {
		this.branchId = branchId;
	}

	public int getThisYear() {
		return thisYear;
	}

	public void setThisYear(int thisYear) {
		this.thisYear = thisYear;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
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

    public float getRankingScore() {
		return rankingScore;
	}

	public void setRankingScore(float rankingScore) {
		this.rankingScore = rankingScore;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public float getSps() {
		return sps;
	}

	public void setSps(float sps) {
		this.sps = sps;
	}

	public int getDaysOfRegistration() {
		return daysOfRegistration;
	}

	public void setDaysOfRegistration(int daysOfRegistration) {
		this.daysOfRegistration = daysOfRegistration;
	}

	public int getCompleted() {
		return completed;
	}

	public void setCompleted(int completed) {
		this.completed = completed;
	}

	public int getSent() {
		return sent;
	}

	public void setSent(int sent) {
		this.sent = sent;
	}

	public float getCompletedPercentage() {
		return completedPercentage;
	}

	public void setCompletedPercentage(float completedPercentage) {
		this.completedPercentage = completedPercentage;
	}

	public int getTotalReviews() {
		return totalReviews;
	}

	public void setTotalReviews(int totalReviews) {
		this.totalReviews = totalReviews;
	}

	public int getIsEligible() {
		return isEligible;
	}

	public void setIsEligible(int isEligible) {
		this.isEligible = isEligible;
	}
	
	public int getInternalBranchRank() {
		return internalBranchRank;
	}

	public void setInternalBranchRank(int internalBranchRank) {
		this.internalBranchRank = internalBranchRank;
	}

	public long getRegionId()
    {
        return regionId;
    }

    public void setRegionId( long regionId )
    {
        this.regionId = regionId;
    }

    public String getNmlsId() {
		return nmlsId;
	}

	public void setNmlsId(String nmlsId) {
		this.nmlsId = nmlsId;
	}
	
	

    public String getProfileImageUrlThumbnail()
    {
        return profileImageUrlThumbnail;
    }

    public void setProfileImageUrlThumbnail( String profileImageUrlThumbnail )
    {
        this.profileImageUrlThumbnail = profileImageUrlThumbnail;
    }

    @Override
    public String toString()
    {
        return "UserRankingThisYearBranch [userRankingThisYearBranchId=" + userRankingThisYearBranchId + ", userId=" + userId
            + ", companyId=" + companyId + ", regionId=" + regionId + ", branchId=" + branchId + ", thisYear=" + thisYear
            + ", firstName=" + firstName + ", lastName=" + lastName + ", emailId=" + emailId + ", rankingScore=" + rankingScore
            + ", rank=" + rank + ", sps=" + sps + ", daysOfRegistration=" + daysOfRegistration + ", completed=" + completed
            + ", sent=" + sent + ", completedPercentage=" + completedPercentage + ", totalReviews=" + totalReviews
            + ", averageRating=" + averageRating + ", isEligible=" + isEligible + ", internalBranchRank=" + internalBranchRank
            + ", nmlsId=" + nmlsId + ", profileImageUrlThumbnail=" + profileImageUrlThumbnail + "]";
    }
  
}
