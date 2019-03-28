package com.realtech.socialsurvey.core.dao.impl;

import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;


public class MongoOrganizationUnitSettingsDaoImplTest
{
    @InjectMocks
    private MongoOrganizationUnitSettingDaoImpl mongoOrganizationUnitSettingDaoImpl;

    @Mock
    private MongoTemplate mongoTemplate;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {}


    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {}


    @Before
    public void setUp() throws Exception
    {
        MockitoAnnotations.initMocks( this );
    }


    @After
    public void tearDown() throws Exception
    {}


    @Test ( expected = InvalidInputException.class)
    public void getOrganizationUnitListWithCRMSourceTestCollectionNameNull() throws InvalidInputException,
        NoRecordsFetchedException
    {
        mongoOrganizationUnitSettingDaoImpl.getOrganizationUnitListWithCRMSource( null, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void getOrganizationUnitListWithCRMSourceTestCollectionNameEmpty() throws InvalidInputException,
        NoRecordsFetchedException
    {
        mongoOrganizationUnitSettingDaoImpl.getOrganizationUnitListWithCRMSource( null, "" );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void getOrganizationUnitListWithCRMSourceTestOUSListNull() throws InvalidInputException, NoRecordsFetchedException
    {
        Mockito
            .when(
                mongoTemplate.find( Mockito.any( Query.class ), Mockito.eq( OrganizationUnitSettings.class ),
                    Mockito.anyString() ) ).thenReturn( null );
        mongoOrganizationUnitSettingDaoImpl.getOrganizationUnitListWithCRMSource( "test", "test" );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void getOrganizationUnitListWithCRMSourceTestOUSListEmpty() throws InvalidInputException, NoRecordsFetchedException
    {
        Mockito
            .when(
                mongoTemplate.find( Mockito.any( Query.class ), Mockito.eq( OrganizationUnitSettings.class ),
                    Mockito.anyString() ) ).thenReturn( new ArrayList<OrganizationUnitSettings>() );
        mongoOrganizationUnitSettingDaoImpl.getOrganizationUnitListWithCRMSource( "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void getCollectionListOfUnprocessedImagesTestCollectionNameNull() throws InvalidInputException
    {
        mongoOrganizationUnitSettingDaoImpl.getCollectionListOfUnprocessedImages( null, "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void getCollectionListOfUnprocessedImagesTestCollectionNameEmpty() throws InvalidInputException
    {
        mongoOrganizationUnitSettingDaoImpl.getCollectionListOfUnprocessedImages( "", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void getCollectionListOfUnprocessedImagesTestImageTypeNull() throws InvalidInputException
    {
        mongoOrganizationUnitSettingDaoImpl.getCollectionListOfUnprocessedImages( "test", null );
    }


    @Test ( expected = InvalidInputException.class)
    public void getCollectionListOfUnprocessedImagesTestImageTypeEmpty() throws InvalidInputException
    {
        mongoOrganizationUnitSettingDaoImpl.getCollectionListOfUnprocessedImages( "test", "" );
    }


    @Test ( expected = InvalidInputException.class)
    public void getCollectionListOfUnprocessedImagesTestImageTypeInvalid() throws InvalidInputException
    {
        mongoOrganizationUnitSettingDaoImpl.getCollectionListOfUnprocessedImages( "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void updateImageForOrganizationUnitSettingTestIdenInvalid() throws InvalidInputException
    {
        mongoOrganizationUnitSettingDaoImpl.updateImageForOrganizationUnitSetting( -1, "test",  "test", "test",  "test", "test", false, false );
    }



    @Test ( expected = InvalidInputException.class)
    public void updateImageForOrganizationUnitSettingTestCollectionNameNull() throws InvalidInputException
    {
        mongoOrganizationUnitSettingDaoImpl.updateImageForOrganizationUnitSetting( 1, "test",  "test", "test",  null, "test", false, false );
    }


    @Test ( expected = InvalidInputException.class)
    public void updateImageForOrganizationUnitSettingTestCollectionNameEmpty() throws InvalidInputException
    {
        mongoOrganizationUnitSettingDaoImpl.updateImageForOrganizationUnitSetting( 1, "test",  "test", "test",  "", "test", false, false );
    }


    @Test ( expected = InvalidInputException.class)
    public void updateImageForOrganizationUnitSettingTestImageTypeNull() throws InvalidInputException
    {
        mongoOrganizationUnitSettingDaoImpl.updateImageForOrganizationUnitSetting( 1, "test",  "test", "test",  "test", null, false, false );
    }


    @Test ( expected = InvalidInputException.class)
    public void updateImageForOrganizationUnitSettingTestImageTypeEmpty() throws InvalidInputException
    {
        mongoOrganizationUnitSettingDaoImpl.updateImageForOrganizationUnitSetting( 1, "test",  "test", "test",  "test", "", false, false );
    }


    @Test ( expected = InvalidInputException.class)
    public void updateImageForOrganizationUnitSettingTestImageTypeInvalid() throws InvalidInputException
    {
        mongoOrganizationUnitSettingDaoImpl.updateImageForOrganizationUnitSetting( 1, "test",  "test", "test",  "test", "test", false, false );
    }


    @Test ( expected = InvalidInputException.class)
    public void getCompanyListForEncompassTestStateNull() throws InvalidInputException, NoRecordsFetchedException
    {
        mongoOrganizationUnitSettingDaoImpl.getCompanyListForEncompass( null, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void getCompanyListForEncompassTestStateEmpty() throws InvalidInputException, NoRecordsFetchedException
    {
        mongoOrganizationUnitSettingDaoImpl.getCompanyListForEncompass( "" ,"" );
    }


    @Test ( expected = InvalidInputException.class)
    public void getCompanyListForEncompassTestStateInvalid() throws InvalidInputException, NoRecordsFetchedException
    {
        mongoOrganizationUnitSettingDaoImpl.getCompanyListForEncompass( "test" , "test" );
    }
    //Tests for updateAgentSettingsForUserRestoration

    public void updateAgentSettingsForUserRestorationTestAgentSettingsNull() throws InvalidInputException
    {
        mongoOrganizationUnitSettingDaoImpl.updateAgentSettingsForUserRestoration( null, null, false, true );
    }
    
    @Test(expected = InvalidInputException.class)
    public void testUpdateParticularKeyCompanySettingsByIden() throws InvalidInputException {
        
        mongoOrganizationUnitSettingDaoImpl.updateParticularKeyCompanySettingsByIden( "rvp", "testValue", 995l, 30369l );
    }
}
