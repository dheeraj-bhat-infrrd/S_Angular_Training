package com.realtech.socialsurvey.core.services.organizationmanagement;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.realtech.socialsurvey.core.entities.Achievement;
import com.realtech.socialsurvey.core.entities.AgentRankingReport;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.Association;
import com.realtech.socialsurvey.core.entities.CompanyPositions;
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.Licenses;
import com.realtech.socialsurvey.core.entities.LinkedInProfileData;
import com.realtech.socialsurvey.core.entities.LockSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.ProfileStage;
import com.realtech.socialsurvey.core.entities.SocialMediaTokens;
import com.realtech.socialsurvey.core.entities.SocialPost;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserFromSearch;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.entities.UserSettings;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;

public interface ProfileManagementService {

	/**
	 * Finalize Lock settings in the hierarchy
	 * 
	 * @param user
	 * @param accountType
	 * @param settings
	 * @throws InvalidInputException
	 */
	public LockSettings aggregateParentLockSettings(User user, AccountType accountType, UserSettings settings, long branchId, long regionId,
			int profilesMaster) throws InvalidInputException;

	/**
	 * Finalize profile settings in the hierarchy
	 * 
	 * @param user
	 * @param accountType
	 * @param settings
	 * @throws InvalidInputException
	 */
	public OrganizationUnitSettings aggregateUserProfile(User user, AccountType accountType, UserSettings settings, long branchId, long regionId,
			int profilesMaster) throws InvalidInputException;

	public String aggregateDisclaimer(OrganizationUnitSettings unitSettings, String entity) throws InvalidInputException;

	// JIRA SS-97 by RM-06 : BOC
	/**
	 * Method to update logo of a company
	 * 
	 * @param collection
	 * @param companySettings
	 * @param logo
	 * @throws InvalidInputException
	 */
	public void updateLogo(String collection, OrganizationUnitSettings companySettings, String logo) throws InvalidInputException;

	public void updateProfileImage(String collection, OrganizationUnitSettings companySettings, String logo) throws InvalidInputException;

	public void updateDisclaimer(String collection, OrganizationUnitSettings unitSettings, String disclaimer) throws InvalidInputException;
	
	public LockSettings updateLockSettings(String collection, OrganizationUnitSettings unitSettings, LockSettings lockSettings)
			throws InvalidInputException;

	/**
	 * Method to update company contact information
	 * 
	 * @param collection
	 * @param unitSettings
	 * @param contactDetailsSettings
	 * @return
	 * @throws InvalidInputException
	 */
	public ContactDetailsSettings updateContactDetails(String collection, OrganizationUnitSettings unitSettings,
			ContactDetailsSettings contactDetailsSettings) throws InvalidInputException;

	public ContactDetailsSettings updateAgentContactDetails(String collection, AgentSettings agentSettings,
			ContactDetailsSettings contactDetailsSettings) throws InvalidInputException;

	/**
	 * Method to add an association
	 * 
	 * @param collection
	 * @param unitSettings
	 * @param associationList
	 * @return
	 * @throws InvalidInputException
	 */
	public List<Association> addAssociations(String collection, OrganizationUnitSettings unitSettings, List<Association> associations)
			throws InvalidInputException;

	public List<Association> addAgentAssociations(String collection, AgentSettings agentSettings, List<Association> associations)
			throws InvalidInputException;

	/**
	 * Method to add an achievement
	 * 
	 * @param collection
	 * @param unitSettings
	 * @param achievements
	 * @return
	 * @throws InvalidInputException
	 */
	public List<Achievement> addAchievements(String collection, OrganizationUnitSettings unitSettings, List<Achievement> achievements)
			throws InvalidInputException;

	public List<Achievement> addAgentAchievements(String collection, AgentSettings agentSettings, List<Achievement> achievements)
			throws InvalidInputException;

	/**
	 * Method to add licence details
	 * 
	 * @param collection
	 * @param unitSettings
	 * @param authorisedIn
	 * @param licenseDisclaimer
	 * @return
	 * @throws InvalidInputException
	 */
	public Licenses addLicences(String collection, OrganizationUnitSettings unitSettings, List<String> authorisedIn) throws InvalidInputException;

	public Licenses addAgentLicences(String collection, AgentSettings agentSettings, List<String> authorisedIn) throws InvalidInputException;

	/**
	 * Method to update social media tokens in profile
	 * 
	 * @param collection
	 * @param unitSettings
	 * @param mediaTokens
	 * @throws InvalidInputException
	 */
	public void updateSocialMediaTokens(String collection, OrganizationUnitSettings unitSettings, SocialMediaTokens mediaTokens)
			throws InvalidInputException;

	/**
	 * Method to fetch all users under the specified branch of specified company
	 * 
	 * @param companyProfileName
	 * @param branchProfileName
	 * @return
	 * @throws InvalidInputException
	 */
	public List<AgentSettings> getIndividualsForBranch(String companyProfileName, String branchProfileName) throws InvalidInputException, ProfileNotFoundException;

	/**
	 * Method to fetch all users under the specified branch of specified company
	 * 
	 * @param branchId
	 * @return
	 * @throws InvalidInputException
	 */
	public List<AgentSettings> getIndividualsByBranchId(long branchId) throws InvalidInputException;

	/**
	 * Method to fetch all users for the list of branches specified
	 * 
	 * @param branchIds
	 * @return
	 * @throws InvalidInputException
	 */
	public List<AgentSettings> getIndividualsByBranchIds(Set<Long> branchIds) throws InvalidInputException;

	/**
	 * Method to fetch all users under the specified region of specified company
	 * 
	 * @param companyProfileName
	 * @param regionProfileName
	 * @return
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 */
	public List<AgentSettings> getIndividualsForRegion(String companyProfileName, String regionProfileName) throws InvalidInputException,
			NoRecordsFetchedException, ProfileNotFoundException;

	/**
	 * Method to fetch all users under the specified region
	 * 
	 * @param branchId
	 * @return
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 */
	public List<AgentSettings> getIndividualsByRegionId(long regionId) throws InvalidInputException, NoRecordsFetchedException;

	/**
	 * Method to fetch all users under the specified list of regions
	 * 
	 * @param regionIds
	 * @return
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 */
	public List<AgentSettings> getIndividualsByRegionIds(Set<Long> regionIds) throws InvalidInputException, NoRecordsFetchedException;

	/**
	 * Method to fetch all individuals directly linked to a company
	 * 
	 * @param companyProfileName
	 * @return
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 */
	public List<AgentSettings> getIndividualsForCompany(String companyProfileName) throws InvalidInputException, NoRecordsFetchedException, ProfileNotFoundException;

	/**
	 * Method to get the region profile based on region and company profile name
	 * 
	 * @param companyProfileName
	 * @param regionProfileName
	 * @return
	 * @throws InvalidInputException
	 */
	public OrganizationUnitSettings getRegionByProfileName(String companyProfileName, String regionProfileName) throws ProfileNotFoundException, InvalidInputException;

	/**
	 * Method to get the branch profile based on branch and company profile name
	 * 
	 * @param companyProfileName
	 * @param branchProfileName
	 * @return
	 * @throws InvalidInputException
	 */
	public OrganizationUnitSettings getBranchByProfileName(String companyProfileName, String branchProfileName) throws ProfileNotFoundException, InvalidInputException;

	/**
	 * JIRA SS-117 by RM-02 Method to fetch company profile when profile name is provided
	 * 
	 * @param collection
	 * @param companySettings
	 * @param logo
	 * @throws InvalidInputException
	 */
	public OrganizationUnitSettings getCompanyProfileByProfileName(String profileName) throws ProfileNotFoundException;

	/**
	 * Method to get profile of an individual
	 * 
	 * @param profileName
	 * @return
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 */
	public OrganizationUnitSettings getIndividualByProfileName(String profileName) throws ProfileNotFoundException, InvalidInputException;

	public SocialMediaTokens aggregateSocialProfiles(OrganizationUnitSettings unitSettings, String entity) throws InvalidInputException,
		NoRecordsFetchedException;
	
	public User getUserByProfileName(String profileName) throws InvalidInputException, NoRecordsFetchedException, ProfileNotFoundException;

	/**
	 * Method to fetch reviews based on the profile level specified, iden is one of
	 * agentId/branchId/regionId or companyId based on the profile level
	 * 
	 * @param iden
	 * @param startScore
	 * @param limitScore
	 * @param startIndex
	 * @param numOfRows
	 * @param profileLevel
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws InvalidInputException
	 */
	public List<SurveyDetails> getReviews(long iden, double startScore, double limitScore, int startIndex, int numOfRows, String profileLevel,
			boolean fetchAbusive, Date startDate, Date endDate, String sortCriteria) throws InvalidInputException;

	/**
	 * Method to get average ratings based on the profile level specified, iden is one of
	 * agentId/branchId/regionId or companyId based on the profile level
	 * 
	 * @param companyId
	 * @param profileLevel
	 * @return
	 * @throws InvalidInputException
	 */
	public double getAverageRatings(long companyId, String profileLevel, boolean aggregateAbusive) throws InvalidInputException;

	/**
	 * Method to get the reviews count for a company within limit of rating score specified
	 * 
	 * @param companyId
	 * @param minScore
	 * @param maxScore
	 * @return
	 */
	public long getReviewsCountForCompany(long companyId, double minScore, double maxScore, boolean fetchAbusive);

	/**
	 * Method to get reviews count based on the profile level specified, iden is one of
	 * agentId/branchId/regionId or companyId based on the profile level within limit of rating
	 * score specified
	 * 
	 * @param iden
	 * @param minScore
	 * @param maxScore
	 * @param profileLevel
	 * @return
	 * @throws InvalidInputException
	 */
	public long getReviewsCount(long iden, double minScore, double maxScore, String profileLevel, boolean fetchAbusive) throws InvalidInputException;

	/**
	 * Method to get the list of individuals for branch/region or company as specified ide in one of
	 * branchId/regionId/companyId
	 * 
	 * @param iden
	 * @param profileLevel
	 * @return
	 * @throws MalformedURLException
	 * @throws SolrException
	 */
	public Collection<UserFromSearch> getProListByProfileLevel(long iden, String profileLevel, int start, int numOfRows) throws InvalidInputException,
			SolrException;

	public void generateVerificationUrl(Map<String, String> urlParams, String applicationUrl, String recipientMailId, String recipientName)
			throws InvalidInputException, UndeliveredEmailException;

	public void updateEmailVerificationStatus(String urlParamsStr) throws InvalidInputException;

	/**
	 * Method to fetch reviews based on the profile level specified, iden is one of
	 * agentId/branchId/regionId or companyId based on the profile level
	 * 
	 * @param iden
	 * @param startScore
	 * @param limitScore
	 * @param startIndex
	 * @param numOfRows
	 * @param profileLevel
	 * @return
	 * @throws InvalidInputException
	 */
	public List<SurveyPreInitiation> getIncompleteSurvey(long iden, double startScore, double limitScore, int startIndex, int numOfRows, String profileLevel,
			Date startDate, Date endDate, boolean realtechAdmin) throws InvalidInputException;
	
	/**
	 * Method that mails the contact us message to the respective individual,branch,region,company
	 * @param agentProfileName
	 * @param message
	 * @param senderMailId
	 * @param profileType
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 * @throws UndeliveredEmailException
	 */
	public void findProfileMailIdAndSendMail(String agentProfileName,String message, String senderName, String senderMailId, String profileType) throws InvalidInputException, NoRecordsFetchedException, UndeliveredEmailException;

	public void addSocialPosts(UserProfile selectedProfile, String postText) throws InvalidInputException;

	public List<SocialPost> getSocialPosts(UserProfile selectedProfile, int startIndex, int batchSize) throws InvalidInputException ;

	public long getPostsCountForUser(long userId);
	
	/**
	 * Updates linkedin profile data to collection
	 * @param collectionName
	 * @param organizationUnitSettings
	 * @param linkedInProfileData
	 * @throws InvalidInputException
	 */
	public void updateLinkedInProfileData(String collectionName, OrganizationUnitSettings organizationUnitSettings, LinkedInProfileData linkedInProfileData) throws InvalidInputException;
	
	/**
	 * Updates the expertise for agent
	 * @param agentSettings
	 * @param expertise
	 * @throws InvalidInputException
	 */
	public void updateAgentExpertise(AgentSettings agentSettings, List<String> expertise) throws InvalidInputException;
	
	/**
	 * Updates the agents hobbies
	 * @param agentSettings
	 * @param hobbies
	 * @throws InvalidInputException
	 */
	public void updateAgentHobbies(AgentSettings agentSettings, List<String> hobbies) throws InvalidInputException;
	
	/**
	 * Updates the company positions for an agent
	 * @param agentSettings
	 * @param companyPositions
	 * @throws InvalidInputException
	 */
	public void updateAgentCompanyPositions(AgentSettings agentSettings, List<CompanyPositions> companyPositions) throws InvalidInputException;

	public void updateProfileStages(List<ProfileStage> profileStages, OrganizationUnitSettings settings, String collectionName);

	public void setAgentProfileUrlForReview(List<SurveyDetails> reviews);
	
	public void updateVertical(String collection, OrganizationUnitSettings companySettings, String vertical) throws InvalidInputException;
	
	public void updateCompanyName(long userId, long companyId, String companyName) throws InvalidInputException;

    public List<AgentRankingReport> getAgentReport( long iden, String profileLevel, Date startDate, Date endDate, Object object ) throws InvalidInputException;
}