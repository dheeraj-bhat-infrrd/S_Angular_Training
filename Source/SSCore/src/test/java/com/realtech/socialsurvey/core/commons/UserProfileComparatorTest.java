package com.realtech.socialsurvey.core.commons;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import com.realtech.socialsurvey.core.entities.ProfilesMaster;
import com.realtech.socialsurvey.core.entities.UserProfile;

public class UserProfileComparatorTest {

	private UserProfileComparator comparator;
	private UserProfile profileA;
	private UserProfile profileB;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {}

	@Before
	public void setUp() throws Exception {
		comparator = new UserProfileComparator();
		profileA = new UserProfile();
		profileA.setProfilesMaster(new ProfilesMaster());
		profileB = new UserProfile();
		profileB.setProfilesMaster(new ProfilesMaster());
	}

	@After
	public void tearDown() throws Exception {}

	@Test
	public void testComparingWithHigherFirstUserProfile() {
		profileA.getProfilesMaster().setProfileId(4);
		profileB.getProfilesMaster().setProfileId(2);
		assertEquals("Test", 1, comparator.compare(profileA, profileB));
	}
	
	@Test
	public void testComparingWithHigherSecondUserProfile() {
		profileA.getProfilesMaster().setProfileId(2);
		profileB.getProfilesMaster().setProfileId(4);
		assertEquals("Test", -1, comparator.compare(profileA, profileB));
	}
	
	@Test
	public void testComparingWithEqualUserProfile() {
		profileA.getProfilesMaster().setProfileId(2);
		profileB.getProfilesMaster().setProfileId(2);
		assertEquals("Test", 0, comparator.compare(profileA, profileB));
	}

}
