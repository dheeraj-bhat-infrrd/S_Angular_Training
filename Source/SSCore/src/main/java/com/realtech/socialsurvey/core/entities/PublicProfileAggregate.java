package com.realtech.socialsurvey.core.entities;

import java.util.List;
import java.util.Map;


/**
 *  Model class which hold the public profile information for a particular hierarchy
 *
 */
public class PublicProfileAggregate
{
    private String profileUrl;
    private Map<String, Long> hierarchyMap;
    private OrganizationUnitSettings profile;
    private String profileLevel;
    private String profileName;
    private double averageRating;
    private long reviewCount;
    private String profileJson;
    private List<SurveyDetails> reviews;
    private String companyProfileName;
    private String findAProCompanyProfileName;
    private String reviewSortCriteria;
    private boolean isAgent;
    private boolean hiddenSection;
    private String completeCompanyProfileUrl;
    private String completeRegionProfileUrl;
    private String completeBranchProfileUrl;
    private IndividualReviewAggregate reviewAggregate;
    private String surveyId;
    private String companyName;
    private boolean addPhototsToReview;


	public String getProfileUrl()
    {
        return profileUrl;
    }


    public void setProfileUrl( String profileUrl )
    {
        this.profileUrl = profileUrl;
    }


    public Map<String, Long> getHierarchyMap()
    {
        return hierarchyMap;
    }


    public void setHierarchyMap( Map<String, Long> hierarchyMap )
    {
        this.hierarchyMap = hierarchyMap;
    }


    public OrganizationUnitSettings getProfile()
    {
        return profile;
    }


    public void setProfile( OrganizationUnitSettings profile )
    {
        this.profile = profile;
    }


    public String getProfileLevel()
    {
        return profileLevel;
    }


    public void setProfileLevel( String profileLevel )
    {
        this.profileLevel = profileLevel;
    }


    public String getProfileName()
    {
        return profileName;
    }


    public void setProfileName( String profileName )
    {
        this.profileName = profileName;
    }


    public double getAverageRating()
    {
        return averageRating;
    }


    public void setAverageRating( double averageRating )
    {
        this.averageRating = averageRating;
    }


    public long getReviewCount()
    {
        return reviewCount;
    }


    public void setReviewCount( long reviewCount )
    {
        this.reviewCount = reviewCount;
    }


    public String getProfileJson()
    {
        return profileJson;
    }


    public void setProfileJson( String profileJson )
    {
        this.profileJson = profileJson;
    }


    public List<SurveyDetails> getReviews()
    {
        return reviews;
    }


    public void setReviews( List<SurveyDetails> reviews )
    {
        this.reviews = reviews;
    }


    public String getCompanyProfileName()
    {
        return companyProfileName;
    }


    public void setCompanyProfileName( String companyProfileName )
    {
        this.companyProfileName = companyProfileName;
    }


    public String getFindAProCompanyProfileName()
    {
        return findAProCompanyProfileName;
    }


    public void setFindAProCompanyProfileName( String findAProCompanyProfileName )
    {
        this.findAProCompanyProfileName = findAProCompanyProfileName;
    }


    public String getReviewSortCriteria()
    {
        return reviewSortCriteria;
    }


    public void setReviewSortCriteria( String reviewSortCriteria )
    {
        this.reviewSortCriteria = reviewSortCriteria;
    }


    public boolean isAgent()
    {
        return isAgent;
    }


    public void setAgent( boolean isAgent )
    {
        this.isAgent = isAgent;
    }


    public boolean isHiddenSection()
    {
        return hiddenSection;
    }


    public void setHiddenSection( boolean hiddenSection )
    {
        this.hiddenSection = hiddenSection;
    }


    public String getCompleteCompanyProfileUrl()
    {
        return completeCompanyProfileUrl;
    }


    public void setCompleteCompanyProfileUrl( String completeCompanyProfileUrl )
    {
        this.completeCompanyProfileUrl = completeCompanyProfileUrl;
    }


    public String getCompleteRegionProfileUrl()
    {
        return completeRegionProfileUrl;
    }


    public void setCompleteRegionProfileUrl( String completeRegionProfileUrl )
    {
        this.completeRegionProfileUrl = completeRegionProfileUrl;
    }


    public String getCompleteBranchProfileUrl()
    {
        return completeBranchProfileUrl;
    }


    public void setCompleteBranchProfileUrl( String completeBranchProfileUrl )
    {
        this.completeBranchProfileUrl = completeBranchProfileUrl;
    }


    public IndividualReviewAggregate getReviewAggregate()
    {
        return reviewAggregate;
    }


    public void setReviewAggregate( IndividualReviewAggregate reviewAggregate )
    {
        this.reviewAggregate = reviewAggregate;
    }


    public String getSurveyId()
    {
        return surveyId;
    }


    public void setSurveyId( String surveyId )
    {
        this.surveyId = surveyId;
    }


    public String getCompanyName() {
		return companyName;
	}


	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
	public boolean isAddPhototsToReview() {
		return addPhototsToReview;
	}


	public void setAddPhototsToReview(boolean addPhototsToReview) {
		this.addPhototsToReview = addPhototsToReview;
	}

	@Override
    public String toString()
    {
        return "PublicProfileAggregate [profileUrl=" + profileUrl + ", hierarchyMap=" + hierarchyMap + ", profile=" + profile
            + ", profileLevel=" + profileLevel + ", profileName=" + profileName + ", averageRating=" + averageRating
            + ", reviewCount=" + reviewCount + ", profileJson=" + profileJson + ", reviews=" + reviews + ", companyProfileName="
            + companyProfileName + ", findAProCompanyProfileName=" + findAProCompanyProfileName + ", reviewSortCriteria="
            + reviewSortCriteria + ", isAgent=" + isAgent + ", hiddenSection=" + hiddenSection + ", completeCompanyProfileUrl=" 
            + completeCompanyProfileUrl + ", completeRegionProfileUrl=" + completeRegionProfileUrl
            + ", completeBranchProfileUrl=" + completeBranchProfileUrl + ", reviewAggregate=" + reviewAggregate + ", surveyId="
            + surveyId + "]";
    }


}
