package com.realtech.socialsurvey.core.commons;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.realtech.socialsurvey.core.entities.ProfileStage;
import com.realtech.socialsurvey.core.enums.ProfileStages;

public class ProfileCompletionListTest
{
    
    private ProfileCompletionList profileCompletionList;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {}

    @AfterClass
    public static void tearDownAfterClass() throws Exception {}

    @Before
    public void setUp() throws Exception {
        profileCompletionList = new ProfileCompletionList();
    }

    @After
    public void tearDown() throws Exception {}
    
    @Test
    public void testGetDefaultProfileCompletionListForISAgentTrue() {
        List<ProfileStage> profileStages = profileCompletionList.getDefaultProfileCompletionList( true );
        assertEquals("Length of the stage returned is incorrect", 10, profileStages.size());
        boolean isLisenceProfilePresent = false;
        boolean isHobbiesProfilePresent = false;
        boolean isAchievemetsProfilePresent = false;
        for(ProfileStage profileStage : profileStages){
            if(profileStage.getProfileStageKey().equalsIgnoreCase( ProfileStages.LICENSE_PRF.name())){
                isLisenceProfilePresent = true;
            } else if(profileStage.getProfileStageKey().equalsIgnoreCase( ProfileStages.HOBBIES_PRF.name())){
                isHobbiesProfilePresent = true;
            } else if(profileStage.getProfileStageKey().equalsIgnoreCase( ProfileStages.ACHIEVEMENTS_PRF.name())){
                isAchievemetsProfilePresent = true;
            }
        }
        Assert.assertTrue( "Profile Stage LICENSE_PRF missing", isLisenceProfilePresent );
        Assert.assertTrue( "Profile Stage HOBBIES_PRF missing", isHobbiesProfilePresent );
        Assert.assertTrue( "Profile Stage ACHIEVEMENTS_PRF missing", isAchievemetsProfilePresent );
    }
    
    
    @Test
    public void testGetDefaultProfileCompletionListForISAgentFalse() {
        List<ProfileStage> profileStages = profileCompletionList.getDefaultProfileCompletionList( false );
        assertEquals("Length of the stage returned is incorrect", 7, profileStages.size());
        boolean isLisenceProfilePresent = false;
        boolean isHobbiesProfilePresent = false;
        boolean isAchievemetsProfilePresent = false;
        for(ProfileStage profileStage : profileStages){
            if(profileStage.getProfileStageKey().equalsIgnoreCase( ProfileStages.LICENSE_PRF.name())){
                isLisenceProfilePresent = true;
            } else if(profileStage.getProfileStageKey().equalsIgnoreCase( ProfileStages.HOBBIES_PRF.name())){
                isHobbiesProfilePresent = true;
            } else if(profileStage.getProfileStageKey().equalsIgnoreCase( ProfileStages.ACHIEVEMENTS_PRF.name())){
                isAchievemetsProfilePresent = true;
            }
        }
        Assert.assertFalse( "Profile Stage LICENSE_PRF present", isLisenceProfilePresent );
        Assert.assertFalse( "Profile Stage HOBBIES_PRF present", isHobbiesProfilePresent );
        Assert.assertFalse( "Profile Stage ACHIEVEMENTS_PRF present", isAchievemetsProfilePresent );
    }
    
    
    @Test
    public void testGetProfileCompletionList() {
        List<ProfileStage> profileStages = new ArrayList<ProfileStage>();
        
        ProfileStage profileStageA = new ProfileStage();
        profileStageA.setStatus( CommonConstants.STATUS_ACTIVE );
        profileStages.add( profileStageA );
        
        ProfileStage profileStageB = new ProfileStage();
        profileStageB.setStatus( CommonConstants.STATUS_INACTIVE );
        profileStages.add( profileStageB );
        
        ProfileStage profileStageC = new ProfileStage();
        profileStageC.setStatus( CommonConstants.STATUS_ACTIVE );
        profileStages.add( profileStageC );
        
        assertEquals("Test", 2 , profileCompletionList.getProfileCompletionList( profileStages ).size());
    }

}
