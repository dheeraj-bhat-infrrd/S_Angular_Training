package com.realtech.socialsurvey.core.services.surveybuilder.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.UserDao;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.Survey;
import com.realtech.socialsurvey.core.entities.SurveyAnswerOptions;
import com.realtech.socialsurvey.core.entities.SurveyCompanyMapping;
import com.realtech.socialsurvey.core.entities.SurveyQuestion;
import com.realtech.socialsurvey.core.entities.SurveyQuestionDetails;
import com.realtech.socialsurvey.core.entities.SurveyQuestionsAnswerOption;
import com.realtech.socialsurvey.core.entities.SurveyQuestionsMapping;
import com.realtech.socialsurvey.core.entities.SurveyVerticalMapping;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.VerticalsMaster;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;


public class SurveyBuilderImplTest
{
    @InjectMocks
    private SurveyBuilderImpl surveyBuilderImpl;

    @Mock
    private UserDao userDao;

    @Mock
    private GenericDao<Survey, Long> surveyDao;

    @Mock
    private GenericDao<SurveyQuestion, Long> surveyQuestionDao;

    @Mock
    private GenericDao<SurveyQuestionsAnswerOption, Long> surveyQuestionsAnswerOptionDao;

    @Mock
    private GenericDao<SurveyQuestionsMapping, Long> surveyQuestionsMappingDao;

    @Mock
    private GenericDao<SurveyCompanyMapping, Long> surveyCompanyMappingDao;

    @Mock
    private GenericDao<SurveyVerticalMapping, Long> surveyVerticalMappingDao;

    @Mock
    private GenericDao<VerticalsMaster, Integer> verticalsMasterDao;

    @Mock
    private OrganizationUnitSettingsDao organizationUnitSettingsDao;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {}


    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {}


    @Before
    public void setUp() throws Exception
    {
        surveyBuilderImpl = new SurveyBuilderImpl();
        MockitoAnnotations.initMocks( this );
    }


    @After
    public void tearDown() throws Exception
    {}


    //Tests for isSurveyBuildingAllowed
    @Test ( expected = InvalidInputException.class)
    public void isSurveyBuildingAllowedTestUserNull() throws InvalidInputException
    {
        surveyBuilderImpl.isSurveyBuildingAllowed( null );
    }


    @Test
    public void isSurveyBuildingAllowedTestUserNotCompanyAdmin() throws InvalidInputException
    {
        assertFalse( "User is company admin", surveyBuilderImpl.isSurveyBuildingAllowed( new User() ) );
    }


    @Test
    public void isSurveyBuildingAllowedTestUserIsCompanyAdmin() throws InvalidInputException
    {
        User user = new User();
        user.setCompanyAdmin( true );
        assertTrue( "User is not company admin", surveyBuilderImpl.isSurveyBuildingAllowed( user ) );
    }


    //Tests for checkForExistingSurvey
    @Test ( expected = InvalidInputException.class)
    public void checkForExistingSurveyTestUserNull() throws InvalidInputException
    {
        surveyBuilderImpl.checkForExistingSurvey( null );
    }


    @SuppressWarnings ( "unchecked")
    @Test
    public void checkForExistingSurveyTestSurveyCompanyMappingListEmpty() throws InvalidInputException
    {
        Mockito.when( surveyCompanyMappingDao.findByKeyValue( Mockito.eq( SurveyCompanyMapping.class ), Mockito.anyMap() ) )
            .thenReturn( new ArrayList<SurveyCompanyMapping>() );
        assertEquals( "Not null", null, surveyBuilderImpl.checkForExistingSurvey( new User() ) );
    }


    @SuppressWarnings ( "unchecked")
    @Test
    public void checkForExistingSurveyTestSurveyCompanyMappingListNull() throws InvalidInputException
    {
        Mockito.when( surveyCompanyMappingDao.findByKeyValue( Mockito.eq( SurveyCompanyMapping.class ), Mockito.anyMap() ) )
            .thenReturn( null );
        assertEquals( "Not null", null, surveyBuilderImpl.checkForExistingSurvey( new User() ) );
    }


    //Tests for createNewSurvey
    @Test ( expected = InvalidInputException.class)
    public void createNewSurveyUserNull() throws InvalidInputException
    {
        surveyBuilderImpl.createNewSurvey( null );
    }


    //Tests for deactivateSurveyCompanyMapping
    @Test ( expected = InvalidInputException.class)
    public void deactivateSurveyCompanyMappingTestUserNull() throws InvalidInputException, NoRecordsFetchedException
    {
        surveyBuilderImpl.deactivateSurveyCompanyMapping( null );
    }


    //Tests for countActiveQuestionsInSurvey
    @Test ( expected = InvalidInputException.class)
    public void countActiveQuestionsInSurveyTestSurveyNull() throws InvalidInputException
    {
        surveyBuilderImpl.countActiveQuestionsInSurvey( null );
    }


    //Tests for countActiveRatingQuestionsInSurvey
    @Test ( expected = InvalidInputException.class)
    public void countActiveRatingQuestionsInSurveyTestUserNull() throws InvalidInputException
    {
        surveyBuilderImpl.countActiveRatingQuestionsInSurvey( null );
    }


    //Tests for addQuestionToExistingSurvey
    @Test ( expected = InvalidInputException.class)
    public void addQuestionToExistingSurveyTestUserNull() throws InvalidInputException
    {
        surveyBuilderImpl.addQuestionToExistingSurvey( null, new Survey(), new SurveyQuestionDetails() );
    }


    @Test ( expected = InvalidInputException.class)
    public void addQuestionToExistingSurveyTestSurveyNull() throws InvalidInputException
    {
        surveyBuilderImpl.addQuestionToExistingSurvey( new User(), null, new SurveyQuestionDetails() );
    }


    @Test ( expected = InvalidInputException.class)
    public void addQuestionToExistingSurveyTestSurveyQuestionDetailsNull() throws InvalidInputException
    {
        surveyBuilderImpl.addQuestionToExistingSurvey( new User(), new Survey(), null );
    }


    @Test ( expected = InvalidInputException.class)
    public void addQuestionToExistingSurveyTestMCQAndAnswersSizeOne() throws InvalidInputException
    {
        SurveyQuestionDetails surveyQuestionDetails = new SurveyQuestionDetails();
        List<SurveyAnswerOptions> answers = new ArrayList<SurveyAnswerOptions>();
        answers.add( new SurveyAnswerOptions() );
        surveyQuestionDetails.setAnswers( answers );
        surveyQuestionDetails.setQuestionType( CommonConstants.QUESTION_MULTIPLE_CHOICE );
        surveyBuilderImpl.addQuestionToExistingSurvey( new User(), new Survey(), surveyQuestionDetails );
    }


    //Tests for getAllActiveQuestionsOfMappedSurvey
    @Test ( expected = InvalidInputException.class)
    public void getAllActiveQuestionsOfMappedSurveyTestUserNull() throws InvalidInputException
    {
        surveyBuilderImpl.getAllActiveQuestionsOfMappedSurvey( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void getAllActiveQuestionsOfMappedSurveyTestCompanyNull() throws InvalidInputException
    {
        surveyBuilderImpl.getAllActiveQuestionsOfMappedSurvey( new User() );
    }


    //Tests for changeSurveyStatus
    @Test ( expected = InvalidInputException.class)
    public void changeSurveyStatusTestUserNull() throws InvalidInputException
    {
        surveyBuilderImpl.changeSurveyStatus( null, 0 );
    }


    //Tests for getSurveyTemplates
    @Test ( expected = InvalidInputException.class)
    public void getSurveyTemplatesTestUserNull() throws InvalidInputException
    {
        surveyBuilderImpl.getSurveyTemplates( null );
    }


    //Tests for updateQuestionAndAnswers
    @Test ( expected = InvalidInputException.class)
    public void updateQuestionAndAnswersTestUserNull() throws InvalidInputException
    {
        surveyBuilderImpl.updateQuestionAndAnswers( null, 0, new SurveyQuestionDetails() );
    }


    @Test ( expected = InvalidInputException.class)
    public void updateQuestionAndAnswersTestSurveyQuestionDetailsNull() throws InvalidInputException
    {
        surveyBuilderImpl.updateQuestionAndAnswers( new User(), 0, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void updateQuestionAndAnswersTestMCQAndAnswersSizeOne() throws InvalidInputException
    {
        SurveyQuestionDetails surveyQuestionDetails = new SurveyQuestionDetails();
        List<SurveyAnswerOptions> answers = new ArrayList<SurveyAnswerOptions>();
        answers.add( new SurveyAnswerOptions() );
        surveyQuestionDetails.setAnswers( answers );
        surveyQuestionDetails.setQuestionType( CommonConstants.QUESTION_MULTIPLE_CHOICE );
        surveyBuilderImpl.updateQuestionAndAnswers( new User(), 0, surveyQuestionDetails );
    }


    //Tests for deactivateQuestionSurveyMapping
    @Test ( expected = InvalidInputException.class)
    public void deactivateQuestionSurveyMappingTestUserNull() throws InvalidInputException
    {
        surveyBuilderImpl.deactivateQuestionSurveyMapping( null, 0 );
    }


    //Tests for reorderQuestion
    @Test ( expected = InvalidInputException.class)
    public void reorderQuestionTestUserNull() throws InvalidInputException
    {
        surveyBuilderImpl.reorderQuestion( null, 0, "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void reorderQuestionTestReorderTypeNull() throws InvalidInputException
    {
        surveyBuilderImpl.reorderQuestion( new User(), 0, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void reorderQuestionTestReorderTypeEmpty() throws InvalidInputException
    {
        surveyBuilderImpl.reorderQuestion( new User(), 0, "" );
    }


    //Tests for cloneSurveyFromTemplate
    @Test ( expected = InvalidInputException.class)
    public void cloneSurveyFromTemplateTestUserNull() throws InvalidInputException, NoRecordsFetchedException
    {
        surveyBuilderImpl.cloneSurveyFromTemplate( null, 0 );
    }


    //Tests for addDefaultSurveyToCompany
    @Test ( expected = InvalidInputException.class)
    public void addDefaultSurveyToCompanyTestUserNull() throws InvalidInputException
    {
        surveyBuilderImpl.addDefaultSurveyToCompany( null );
    }


    //Tests for checkIfSurveyIsDefaultAndClone
    @Test ( expected = InvalidInputException.class)
    public void checkIfSurveyIsDefaultAndCloneTestUserNull() throws InvalidInputException, NoRecordsFetchedException
    {
        surveyBuilderImpl.checkIfSurveyIsDefaultAndClone( null );
    }


    //Tests for mapSurveyToCompany
    @Test ( expected = InvalidInputException.class)
    public void mapSurveyToCompanyTestSurveyNull() throws InvalidInputException
    {
        surveyBuilderImpl.mapSurveyToCompany( new User(), null, new Company() );
    }


    @Test ( expected = InvalidInputException.class)
    public void mapSurveyToCompanyTestCompanyNull() throws InvalidInputException
    {
        surveyBuilderImpl.mapSurveyToCompany( new User(), new Survey(), null );
    }


    @Test ( expected = InvalidInputException.class)
    public void mapSurveyToCompanyTestUserNull() throws InvalidInputException
    {
        surveyBuilderImpl.mapSurveyToCompany( null, new Survey(), new Company() );
    }
}
