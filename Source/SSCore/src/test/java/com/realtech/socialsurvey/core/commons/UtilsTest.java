package com.realtech.socialsurvey.core.commons;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.utils.EncryptionHelper;

public class UtilsTest {

	private Utils utils;

    private String[] swearWords = new String[] {};

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {}

	@Before
    public void setUp() throws Exception
    {
        utils = new Utils();
        swearWords = new String[] { "anus", "arse", "arsehole", "ass", "ass-hat", "ass-jabber", "ass-pirate", "assbag",
            "assbandit", "assbanger", "assbite", "assclown", "asscock", "asscracker", "asses", "assface", "assfuck",
            "assfucker", "assgoblin", "asshat", "asshead", "asshole", "asshopper", "assjacker", "asslick", "asslicker",
            "assmonkey", "assmunch", "assmuncher", "assnigger", "asspirate", "assshit", "assshole", "asssucker", "asswad",
            "asswipe", "axwound", "bampot", "bastard", "beaner", "bitch", "bitchass", "bitches", "bitchtits", "bitchy",
            "blow job", "blowjob", "bollocks", "bollox", "boner", "brotherfucker", "bullshit", "bumblefuck", "butt plug",
            "butt", "buttfucka", "buttfucker", "camel toe", "carpetmuncher", "chesticle", "chinc", "chink", "choad", "chode",
            "clit", "clitface", "clitfuck", "clusterfuck", "cock", "cockass", "cockbite", "cockburger", "cockface",
            "cockfucker", "cockhead", "cockjockey", "cockknoker", "cockmaster", "cockmongler", "cockmongruel", "cockmonkey",
            "cockmuncher", "cocknose", "cocknugget", "cockshit", "cocksmith", "cocksmoke", "cocksmoker", "cocksniffer",
            "cocksucker", "cockwaffle", "coochie", "coochy", "coon", "cooter", "cracker", "cum", "cumbubble", "cumdumpster",
            "cumguzzler", "cumjockey", "cumslut", "cumtart", "cunnie", "cunnilingus", "cunt", "cuntass", "cuntface",
            "cunthole", "cuntlicker", "cuntrag", "cuntslut", "dago", "damn", "deggo", "dick", "dic", "dickbag", "dickbeaters",
            "dickface", "dickfuck", "dickfucker", "dickhead", "dickhole", "dickjuice", "dickmilk", "dickmonger", "dicks",
            "dickslap", "dicksucker", "dicksucking", "dicktickler", "dickwad", "dickweasel", "dickweed", "dickwod", "dike",
            "dildo", "dipshit", "doochbag", "dookie", "douche", "douch", "douchebag", "douchewaffle", "dumass", "dumb ass",
            "dumbass", "dumbfuck", "dumbshit", "dumshit", "dyke", "fag", "fagbag", "fagfucker", "faggit", "faggot",
            "faggotcock", "fagtard", "fatass", "fellatio", "feltch", "flamer", "fuck", "fuckass", "fuckbag", "fuckboy",
            "fuckbrain", "fuckbutt", "fuckbutter", "fucked", "fucker", "fuckersucker", "fuckface", "fuckhead", "fuckhole",
            "fuckin", "fucking", "fucknut", "fucknutt", "fuckoff", "fucks", "fuckstick", "fucktard", "fucktart", "fuckup",
            "fuckwad", "fuckwit", "fuckwitt", "fudgepacker", "gay", "gayass", "gaybob", "gaydo", "gayfuck", "gayfuckist",
            "gaylord", "gaytard", "gaywad", "goddamn", "goddamnit", "gooch", "gook", "gringo", "guido", "handjob", "hard on",
            "heeb", "hell", "ho", "hoe", "homo", "homodumbshit", "honkey", "humping", "jackass", "jagoff", "jap", "jerk off",
            "jerkass", "jigaboo", "jizz", "jungle bunny", "junglebunny", "kike", "kooch", "kootch", "kraut", "kunt", "kyke",
            "lameass", "lardass", "lesbian", "lesbo", "lezzie", "mcfagget", "mick", "minge", "mothafucka", "mothafuckin\'",
            "motherfucker", "motherfucking", "muff", "muffdiver", "munging", "negro", "nigaboo", "nigga", "nigger", "niggers",
            "niglet", "nut sack", "nutsack", "paki", "panooch", "pecker", "peckerhead", "penis", "penisbanger", "penisfucker",
            "penispuffer", "piss", "pissed", "pissed off", "pissflaps", "polesmoker", "pollock", "poon", "poonani", "poonany",
            "poontang", "porch monkey", "porchmonkey", "prick", "punanny", "punta", "pussies", "pussy", "pussylicking", "puto",
            "queef", "queer", "queerbait", "queerhole", "renob", "rimjob", "ruski", "sand nigger", "sandnigger", "schlong",
            "scrote", "shit", "shitass", "shitbag", "shitbagger", "shitbrains", "shitbreath", "shitcanned", "shitcunt",
            "shitdick", "shitface", "shitfaced", "shithead", "shithole", "shithouse", "shitspitter", "shitstain", "shitter",
            "shittiest", "shitting", "shitty", "shiz", "shiznit", "skank", "skeet", "skullfuck", "slut", "slutbag", "smeg",
            "snatch", "spic", "spick", "splooge", "spook", "suckass", "tard", "testicle", "thundercunt", "tit", "titfuck",
            "tits", "tittyfuck", "twat", "twatlips", "twats", "twatwaffle", "unclefucker", "va-j-j", "vag", "vagina",
            "vajayjay", "vjayjay", "wank", "wankjob", "wetback", "whore", "whorebag", "whoreface", "wop" };
    }

	@After
	public void tearDown() throws Exception {}

	@Test
	public void testGenerateRegionProfileUrl(){
		assertEquals("Generated Region profile does not match expected", "/region/company/region-name", utils.generateRegionProfileUrl("company", "region-name"));
	}
	
	@Test
	public void testGenerateBranchProfileUrl(){
		assertEquals("Generated Branch profile does not match expected", "/office/company/office-name", utils.generateBranchProfileUrl("company", "office-name"));
	}
	
	@Test
	public void testGenerateCompanyProfileUrl(){
		assertEquals("Generated Company profile does not match expected", "/company/company", utils.generateCompanyProfileUrl("company"));
	}

	@Test
    public void testGenerateAgentProfileUrl(){
	    assertEquals("Generated Agent profile does not match expected", "/rijil-krishnan", utils.generateAgentProfileUrl("rijil-krishnan"));
    }

    @Test
    public void testPrepareProfileNameWithInputHavingSpaces(){
        assertEquals("Generated Profile Name does not match expected", "rijil-krishnan", utils.prepareProfileName("rijil krishnan"));
    }

    @Test
    public void testPrepareProfileNameWithInputHavingHyphen(){
        assertEquals("Generated Profile Name does not match expected", "rijil-krishnan", utils.prepareProfileName("rijil-krishnan"));
    }

    @Test
    public void testAppendIdenToProfileName(){
        assertEquals("Generated Profile Name with iden does not match expected", "rijil-krishnan-2", utils.appendIdenToProfileName("rijil-krishnan", 2));
    }
	
	@Test
	public void testMaskEmailAddress(){
		Whitebox.setInternalState(utils, "maskingPrefix", "test");
		Whitebox.setInternalState(utils, "maskingSuffix", "@abc.com");
		assertEquals("Masked email address does not match expected", "test+my.example.com@abc.com", utils.maskEmailAddress("my@example.com"));
	}


    @Test
    public void testCheckReviewForSwearWordsWithReviewNull()
    {
        assertFalse( "Review is considered abusive", utils.checkReviewForSwearWords( null, new String[] {} ) );
    }


    @Test
    public void testCheckReviewForSwearWordsWithReviewIsEmpty()
    {
        assertFalse( "Review is considered abusive", utils.checkReviewForSwearWords( "", new String[] {} ) );
    }


    @Test
    public void testCheckReviewForSwearWordsWithSwearWordsArrayIsNull()
    {
        assertFalse( "Review is considered abusive", utils.checkReviewForSwearWords( "A", null ) );
    }


    @Test
    public void testCheckReviewForSwearWordsWithSwearWordsArrayIsEmpty()
    {
        assertFalse( "Review is considered abusive", utils.checkReviewForSwearWords( "desperate", new String[] {} ) );
    }


    @Test
    public void testCheckReviewForSwearWordsWithNonAbusiveReview()
    {
        assertFalse( "Review is considered abusive", utils.checkReviewForSwearWords( "desperate", new String[] {} ) );
    }


    @Test
    public void testCheckReviewForSwearWordsWithAbusiveReview()
    {
        assertTrue( "Review is not considered abusive", utils.checkReviewForSwearWords( "you sir are an ass", swearWords ) );
    }

    @Test
    public void testUnmaskEmailAddressActuallyMasked()
    {
        Whitebox.setInternalState( utils, "maskingPrefix", "" );
        Whitebox.setInternalState( utils, "maskingSuffix", "@vfgllc.com" );
        assertEquals( "Unmask successful", "bhayes+bhhspro.com@vfgllc.com", utils.unmaskEmailAddress( "bhayes+bhhspro.com+vfgllc.com@vfgllc.com" ) );
    }
    
    
    @Test
    public void testUnmaskEmailAddressNotMasked()
    {
        Whitebox.setInternalState( utils, "maskingPrefix", "" );
        Whitebox.setInternalState( utils, "maskingSuffix", "@abc.com" );
        assertEquals( "Unmask successful", "test+xy@abc.com", utils.unmaskEmailAddress( "test+xy@abc.com" ) );
    }
    
    
    @Test
    public void testUnmaskEmailAddressWithPlusNotMasked()
    {
        Whitebox.setInternalState( utils, "maskingPrefix", "test" );
        Whitebox.setInternalState( utils, "maskingSuffix", "@abc.com" );
        assertEquals( "Unmask successful", "test+1@abc.com", utils.unmaskEmailAddress( "test+1@abc.com" ) );
    }


    @Test
    public void testEncryptUserEmailIdWithUserEmailId()
    {
        Whitebox.setInternalState( utils, "defaultEmailDomain", "raremile.com" );
        Whitebox.setInternalState( utils, "encryptionHelper", new EncryptionHelper() );
        assertEquals( "Encrypted mail id is not as expected", utils.encryptUserEmailId( "u1@raremile.com" ),
            "u-MDAwMDAwMDAwMDAwMDAwMQ@raremile.com" );
    }



    @Test
    public void testEncryptUserEmailIdWhileEncryptingThrowsException() throws InvalidInputException
    {
        EncryptionHelper encryptionHelper = Mockito.mock( EncryptionHelper.class );
        Whitebox.setInternalState( utils, "defaultEmailDomain", "raremile.com" );
        Whitebox.setInternalState( utils, "encryptionHelper", encryptionHelper );
        Mockito.when( encryptionHelper.encodeBase64( Mockito.anyString() ) ).thenThrow( new InvalidInputException( "test exception" ) );
        assertEquals( "Encrypted mail id is not as expected", utils.encryptUserEmailId( "u1@raremile.com" ),
            "u1@raremile.com" );
    }

    @Test
    public void testEncryptUserEmailIdWithNonUserId()
    {
        Whitebox.setInternalState( utils, "defaultEmailDomain", "raremile.com" );
        Whitebox.setInternalState( utils, "encryptionHelper", new EncryptionHelper() );
        assertEquals( "Encrypted mail id is not as expected", utils.encryptUserEmailId( "admin@raremile.com" ),
            "admin@raremile.com" );
    }
}
