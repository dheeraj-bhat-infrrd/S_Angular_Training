package com.realtech.socialsurvey.core.services.surveybuilder;

import java.util.List;
import java.util.Map;
import org.apache.solr.client.solrj.SolrServerException;
import com.realtech.socialsurvey.core.entities.AbusiveSurveyReportWrapper;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SocialMediaPostDetails;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileNotFoundException;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.services.surveybuilder.impl.DuplicateSurveyRequestException;
import com.realtech.socialsurvey.core.services.surveybuilder.impl.SelfSurveyInitiationException;


public interface SurveyHandler
{

    /**
     * Method to store question and answer format into mongo.
     * 
     * @param agentId
     * @throws InvalidInputException
     * @throws SolrServerException
     * @throws NoRecordsFetchedException
     * @throws SolrException
     */
    /*
     * public void storeInitialSurveyDetails(long agentId, long companyId, long regionId, long
     * branchId, String customerEmail, int reminderCount) throws InvalidInputException,
     * SolrException, NoRecordsFetchedException, SolrServerException;
     */
    /*
     * Method to update answers to all the questions and current stage in MongoDB.
     * @param agentId
     * @param customerEmail
     * @param question
     * @param answer
     * @param stage
     * @throws Exception
     */
    public void updateCustomerAnswersInSurvey( long agentId, String customerEmail, String question, String questionType,
        String answer, int stage );


    /*
     * Method to update customer review and final score on the basis of rating questions in
     * SURVEY_DETAILS.
     */
    public void updateGatewayQuestionResponseAndScore( long agentId, String customerEmail, String mood, String review,
        boolean isAbusive, String agreedToShare );


    public SurveyDetails storeInitialSurveyDetails( long agentId, String customerEmail, String firstName, String lastName,
        int reminderCount, String custRelationWithAgent, String url, String source ) throws SolrException,
        NoRecordsFetchedException, InvalidInputException;


    public SurveyDetails getSurveyDetails( long agentId, String customerEmail, String firstName, String lastName );


    public String getApplicationBaseUrl();


    public void updateSurveyAsClicked( long agentId, String customerEmail );


    public String getSwearWords();


    public void updateReminderCount( long surveyPreInitiationId, boolean reminder );


    public void markSurveyAsSent( SurveyPreInitiation surveyPreInitiation );


    public Map<String, String> getEmailIdsOfAdminsInHierarchy( long agentId ) throws InvalidInputException;


    public List<SurveyPreInitiation> getIncompleteSurveyCustomersEmail( Company company );


    public void updateReminderCount( List<Long> agents, List<String> customers );


    public List<SurveyDetails> getIncompleteSocialPostSurveys( long companyId );


    public String getMoodsToSendMail();


    public void increaseSurveyCountForAgent( long agentId ) throws SolrException;


    public void decreaseSurveyCountForAgent( long agentId ) throws SolrException;


    public void updateSharedOn( String socialSite, long agentId, String customerEmail );


    public String getGoogleShareUri();


    public String getSurveyUrl( long agentId, String customerEmail, String baseUrl ) throws InvalidInputException;


    public void changeStatusOfSurvey( long agentId, String customerEmail, String firstName, String lastName, boolean editable );


    public void sendSurveyInvitationMail( String custFirstName, String custLastName, String custEmail,
        String custRelationWithAgent, User user, boolean isAgent, String source ) throws InvalidInputException, SolrException,
        NoRecordsFetchedException, UndeliveredEmailException, ProfileNotFoundException;


    public void sendSurveyRestartMail( String custFirstName, String custLastName, String custEmail,
        String custRelationWithAgent, User user, String link ) throws InvalidInputException, UndeliveredEmailException,
        ProfileNotFoundException;


    public SurveyPreInitiation getPreInitiatedSurvey( long agentId, String customerEmail, String custFirstName,
        String custLastName ) throws NoRecordsFetchedException;


    public SurveyPreInitiation getPreInitiatedSurvey( long surveyPreInitiationId ) throws NoRecordsFetchedException;


    public void deleteSurveyPreInitiationDetailsPermanently( SurveyPreInitiation surveyPreInitiation );


    public String composeLink( long userId, String custEmail, String custFirstName, String custaLastName )
        throws InvalidInputException;


    public void markSurveyAsStarted( SurveyPreInitiation surveyPreInitiation );


    public void updateReminderCountForSocialPosts( Long agentId, String customerEmail );


    public Map<String, Object> mapAgentsInSurveyPreInitiation();


    public Map<String, Integer> getReminderInformationForCompany( long companyId );


    public Boolean checkIfTimeIntervalHasExpired( long lastRemindedTime, long systemTime, int reminderInterval );


    void insertSurveyDetails( SurveyDetails surveyDetails );


    SurveyDetails getSurveyDetailsBySourceIdAndMongoCollection( String surveySourceId, long iden, String collectionName );


    SurveyPreInitiation getPreInitiatedSurveyById( long surveyPreInitiationId ) throws NoRecordsFetchedException;


    /**
     * @param surveyPreInitiation
     */
    public void saveSurveyPreInitiationObject( SurveyPreInitiation surveyPreInitiation ) throws InvalidInputException;


    void updateSurveyAsAbusive( String surveymongoId, String reporterEmail, String reporterName );


    /**
     * Sends survey request to the customer on behalf of agent.
     * @param agentId
     * @param recipientEmailId
     * @param recipientFirstname
     * @param recipientLastname
     * @param source
     * @throws DuplicateSurveyRequestException
     * @throws InvalidInputException
     * @throws SelfSurveyInitiationException
     * @throws SolrException
     * @throws NoRecordsFetchedException
     * @throws UndeliveredEmailException
     * @throws ProfileNotFoundException 
     */
    public void initiateSurveyRequest( long agentId, String recipientEmailId, String recipientFirstname,
        String recipientLastname, String source ) throws DuplicateSurveyRequestException, InvalidInputException,
        SelfSurveyInitiationException, SolrException, NoRecordsFetchedException, UndeliveredEmailException,
        ProfileNotFoundException;


    void deleteZillowSurveysByEntity( String entityType, long entityId ) throws InvalidInputException;


    void deleteExcessZillowSurveysByEntity( String entityType, long entityId ) throws InvalidInputException;


    public List<AbusiveSurveyReportWrapper> getSurveysReporetedAsAbusive( int startIndex, int numOfRows );


    void sendSurveyCompletionMail( String custEmail, String custFirstName, String custLastName, User user )
        throws InvalidInputException, UndeliveredEmailException, ProfileNotFoundException;


    void sendSocialPostReminderMail( String custEmail, String custFirstName, String custLastName, User user, String links )
        throws InvalidInputException, UndeliveredEmailException, ProfileNotFoundException;


    public SocialMediaPostDetails getSocialMediaPostDetailsBySurvey( SurveyDetails surveyDetails,
        OrganizationUnitSettings companyUnitSettings, List<OrganizationUnitSettings> regionUnitSettings,
        List<OrganizationUnitSettings> branchUnitSettings );


    void updateSurveyDetails( SurveyDetails surveyDetails );


    public List<SurveyDetails> getSurveyDetailsByAgentAndCompany( long companyId );


    public Boolean canPostOnSocialMedia( OrganizationUnitSettings unitSetting, Double rating );

}