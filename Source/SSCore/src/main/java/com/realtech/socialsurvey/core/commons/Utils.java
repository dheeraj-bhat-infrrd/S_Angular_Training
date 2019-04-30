package com.realtech.socialsurvey.core.commons;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.utils.EncryptionHelper;


@Component
public class Utils
{

    private final String REGION_PROFILE_URL_PATTERN = "/region/%s/%s";
    private final String BRANCH_PROFILE_URL_PATTERN = "/office/%s/%s";
    private final String COMPANY_PROFILE_URL_PATTERN = "/company/%s";
    private final String AGENT_PROFILE_URL_PATTERN = "/%s";
    private static final String PROFILE_REGEX = "[^\\w]{1,}";
    private static final String PROFILE_REGEX_REPLACEMENT = "-";
    private static final Pattern PATTERN = Pattern.compile( PROFILE_REGEX );
    private static final Logger LOG = LoggerFactory.getLogger( Utils.class );
    private static final String URL_REGEX_FOR_FACEBOOK_PAGE_ID= "(?:https?:\\/\\/)?(?:www\\.)?facebook\\.com\\/(?:(?:\\w)*#!\\/)?(?:pages\\/)?(?:groups\\/)?(?:[\\w\\-]*\\/)*?(\\/)?([^/?]*)";


    @Value("${EMAIL_MASKING_PREFIX}")
	private String maskingPrefix;

	@Value("${EMAIL_MASKING_SUFFIX}")
	private String maskingSuffix;

    @Value ( "${SOCIALSURVEYME_SENDER_EMAIL_DOMAIN}")
    private String defaultEmailDomain;

    @Value ( "${APPLICATION_BASE_URL}")
    private  String applicationBaseUrl;

    @Autowired
    private EncryptionHelper encryptionHelper;

    /**
     * Method to generate region profile url based on company profile name and region profile name
     * 
     * @param companyProfileName
     * @param regionProfileName
     * @return
     */
    public String generateRegionProfileUrl( String companyProfileName, String regionProfileName )
    {
        LOG.info( "Method generateRegionProfileUrl called for companyProfileName:" + companyProfileName
            + " and regionProfileName:" + regionProfileName );
        String profileUrl = null;
        profileUrl = String.format( REGION_PROFILE_URL_PATTERN, companyProfileName, regionProfileName );

        LOG.info( "Method generateRegionProfileUrl excecuted. Returning profile url:" + profileUrl );
        return profileUrl;
    }


    /**
     * Method to generate branch profile url
     * 
     * @param companyProfileName
     * @param branchProfileName
     * @return
     */
    public String generateBranchProfileUrl( String companyProfileName, String branchProfileName )
    {
        LOG.info( "Method generateBranchProfileUrl called for companyProfileName:" + companyProfileName
            + " and branchProfileName:" + branchProfileName );
        String profileUrl = null;
        profileUrl = String.format( BRANCH_PROFILE_URL_PATTERN, companyProfileName, branchProfileName );
        LOG.info( "Method generateBranchProfileUrl excecuted. Returning profile url:" + profileUrl );
        return profileUrl;
    }


    /**
     * Method to generate agent profile url
     * 
     * @param agentProfileName
     * @return
     */
    public String generateAgentProfileUrl( String agentProfileName )
    {
        LOG.info( "Method generateAgentProfileUrl called for agentProfileName:" + agentProfileName );
        String profileUrl = null;
        profileUrl = String.format( AGENT_PROFILE_URL_PATTERN, agentProfileName );
        LOG.info( "Method generateAgentProfileUrl excecuted. Returning profile url:" + profileUrl );
        return profileUrl;
    }

    /**
     * Method to generate company profile url
     * 
     * @param companyProfileName
     * @return
     */
    public String generateCompanyProfileUrl( String companyProfileName )
    {
        LOG.info( "Method generateCompanyProfileUrl called for companyProfileName:" + companyProfileName );
        String profileUrl = null;
        profileUrl = String.format( COMPANY_PROFILE_URL_PATTERN, companyProfileName );
        LOG.info( "Method generateCompanyProfileUrl excecuted. Returning profile url:" + profileUrl );
        return profileUrl;
    }
    
    /**
     * Generates profile name
     * @param input
     * @return
     */
    public String prepareProfileName( String input )
    {
        LOG.debug( "Preparing profile name for " + input );
        Matcher matcher = PATTERN.matcher( input.trim().toLowerCase() );
        String genereatedProfileName = matcher.replaceAll( PROFILE_REGEX_REPLACEMENT );
        // clear if '-' is at the beginning or end of string
        int hyphenIndex = genereatedProfileName.indexOf( "-" );
        if ( hyphenIndex == 0 ) {
            // remove the first hyphen
            genereatedProfileName = genereatedProfileName.substring( 1 );
        }
        hyphenIndex = genereatedProfileName.lastIndexOf( "-" );
        if ( hyphenIndex == genereatedProfileName.length() - 1 ) {
            // remove the last hyphen
            genereatedProfileName = genereatedProfileName.substring( 0, genereatedProfileName.length() - 1 );
        }
        LOG.debug( "Generated profile name " + genereatedProfileName );
        return genereatedProfileName;
    }


    /**
     * appends profile name with iden
     * @param profileName
     * @param iden
     * @return
     */
    public String appendIdenToProfileName( String profileName, long iden )
    {
        LOG.debug( "Appending " + iden + " to profile name: " + profileName + " for uniqueness" );
        return profileName + "-" + iden;
    }


    public byte[] serializeObject( Object object ) throws IOException
    {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream( byteOut );
        objOut.writeObject( object );
        objOut.close();
        byteOut.close();
        byte[] bytes = byteOut.toByteArray();
        return bytes;
    }


    public Object deserializeObject( byte[] serializedBinary ) throws IOException, ClassNotFoundException
    {
        ByteArrayInputStream byteIn = new ByteArrayInputStream( serializedBinary );
        ObjectInputStream objectInput = new ObjectInputStream( byteIn );
        Object object = objectInput.readObject();
        objectInput.close();
        byteIn.close();
        return object;

    }
    
    public String maskEmailAddress(String emailAddress) {
		LOG.debug("Masking email address: " + emailAddress);
		String maskedEmailAddress = null;
		// replace @ with +
		maskedEmailAddress = emailAddress.replace("@", "+");
		if (maskingPrefix != null && !maskingPrefix.isEmpty()) {
			maskedEmailAddress = maskingPrefix + "+" + maskedEmailAddress;
		}
		if (maskingSuffix == null || maskingSuffix.isEmpty()) {
			return null;
		}
		else {
			maskedEmailAddress = maskedEmailAddress + maskingSuffix;
		}
		return maskedEmailAddress;
	}
    

    public String unmaskEmailAddress( String emailAddress )
    {
        String unmaskedEmailAddress = null;
        LOG.debug( "Unmasking email address: " + emailAddress );
        if ( maskingPrefix == null || maskingPrefix.isEmpty() ) {
            if ( maskingSuffix == null || maskingSuffix.isEmpty() ) {
                return emailAddress;
            }
            int lastPlus = emailAddress.lastIndexOf( '+' );
            if ( lastPlus < 0 ) {
                return emailAddress;
            }
            String unmaskedemailAddress = new StringBuilder(emailAddress).replace( lastPlus, lastPlus + 1, "@" ).toString();
            int lastSuffix = unmaskedemailAddress.lastIndexOf( maskingSuffix );
            if ( lastSuffix <= 0 ) {
                return emailAddress;
            }
            unmaskedemailAddress = new StringBuilder( unmaskedemailAddress ).replace( lastSuffix, unmaskedemailAddress.length(), "" ).toString();
            if ( validateEmail( unmaskedemailAddress ) ) {
                return unmaskedemailAddress;
            }
            return emailAddress;
        }
        String pattern = "^" + maskingPrefix + "\\+(.+)" + maskingSuffix + "$";

        Pattern unmaskRegex = Pattern.compile( pattern );

        Matcher matcher = unmaskRegex.matcher( emailAddress );
        if ( matcher.find() ) {
            unmaskedEmailAddress = matcher.group( 1 );
            //Replace last + with @
            int lastPlus = unmaskedEmailAddress.lastIndexOf( '+' );
            if ( lastPlus > 0 ) {
                unmaskedEmailAddress = new StringBuilder( unmaskedEmailAddress ).replace( lastPlus, lastPlus + 1, "@" )
                    .toString();
                if ( validateEmail( unmaskedEmailAddress ) ) {
                    return unmaskedEmailAddress;
                }
            }

        }
        return emailAddress;
    }
    
    
    public Boolean validateEmail( String emailId )
    {
        boolean validEmail = true;
        LOG.info( "Method validateAndParseEmailIds called" );
        Pattern pattern = Pattern.compile( CommonConstants.EMAIL_REGEX, Pattern.CASE_INSENSITIVE );
        Matcher matcher = pattern.matcher( emailId );
        validEmail = matcher.matches();
        return validEmail;
    }
    
    
    /**
     * @return
     */
    public Timestamp convertEpochDateToTimestamp()
    {
        String string = "January 2, 1970";
        DateFormat format = new SimpleDateFormat( "MMMM d, yyyy", Locale.ENGLISH );
        Date date = null;

        try {
            date = format.parse( string );
        } catch ( ParseException e ) {
            LOG.error( "Exception caught ", e );
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime( date );
        cal.set( Calendar.MILLISECOND, 0 );

        return ( new java.sql.Timestamp( cal.getTimeInMillis() ) );
    }


    public String urlEncodeText( String text )
    {
        LOG.debug( "Encoding of text started." );
        try {
            text = URLEncoder.encode( text, "UTF-8" );
        } catch ( UnsupportedEncodingException e ) {
            LOG.warn( "Error occurred while url encoding params. Reason : ", e );
        }
        LOG.debug( "Encoding of text ended." );
        return text;
    }
    
    public String generateRandomAlphaNumericString(){
        SecureRandom random = new SecureRandom();
        return new BigInteger(40, random).toString(20);
    }


    /**
     * Method to check whether review has abusive words
     * @param review
     * @return
     */
    public boolean checkReviewForSwearWords( String review, String swearWords[] )
    {
        if ( review == null || review.isEmpty() ) {
            LOG.error( "review passed as argument is null or empty" );
            return false;
        }

        if ( swearWords == null || swearWords.length == 0 ) {
            LOG.error( "swearWords passed as argument cannot be null or empty" );
            return false;
        }
        LOG.info( "Method to check review for abusive words, checkReviewForSwearWords called" );
        List<String> swearList = Arrays.asList( swearWords );
        String reviewParts[] = review.split( " " );
        for ( String reviewWord : reviewParts ) {
            if ( swearList.contains( reviewWord.trim().toLowerCase() ) ) {
                LOG.info( "Method to check review for abusive words, checkReviewForSwearWords ended" );
                return true;
            }
        }
        LOG.info( "Method to check review for abusive words, checkReviewForSwearWords ended" );
        return false;
    }


    /**
     * Method to encrypt user id in email id
     * this method is not used by any function but test cases reside on it 
     * */
    public String encryptUserEmailId( String emailId )
    {
        if ( emailId == null || emailId.isEmpty() ) {
            LOG.error( "Email id passed is empty or null in encryptUserEmailId" );
            return emailId;
        }
        LOG.info( "Method to encrypt user id in mail, encryptUserEmailId started" );
        String newEmailId = emailId;
        String agentEmailRegex = "u(\\d+)@" + Matcher.quoteReplacement( defaultEmailDomain );
        Pattern pattern = Pattern.compile( agentEmailRegex );
        Matcher matcher = pattern.matcher( emailId );
        if ( matcher.find() ) {
            LOG.debug( "Mail id belongs to agent mail id format" );
            LOG.debug( "agent id from the mail id is " + matcher.group( 1 ) );
            String userIdString = matcher.group( 1 );
            int paddingBitsNeeded = 16 - userIdString.length();
            if ( paddingBitsNeeded > 0 ) {
                LOG.debug("Adding padding bits to the user id");
                StringBuilder paddedBitString = new StringBuilder();
                for ( int i = 0; i < paddingBitsNeeded; i++ ) {
                    paddedBitString.append( "0" );
                }
                userIdString = paddedBitString.toString() + userIdString;
                LOG.debug("Added padding bits to the user id");
            }
            try {
                LOG.debug("Encrypting the user id");
                userIdString = encryptionHelper.encodeBase64( userIdString );
                newEmailId = "u-" + userIdString + "@" + defaultEmailDomain;
                LOG.debug("Encrypted the user id successfully");
            } catch ( InvalidInputException e ) {
                LOG.error( "Exception occurred while encrypting the user id. Reason : ", e );
            }
        }
        LOG.info( "Method to encrypt user id in mail, encryptUserEmailId ended" );
        return newEmailId;
    }
    
    /**
     * method to convert to char set ISO 8859-1 and then encode it using UTF-8
     * returns null if it fails to convert
     * @param input
     * @return
     */
    public String convert_ISO_8859_1_To_UTF_8_Encoding( String input )
    {
        try {
            return input != null ? new String( input.getBytes( Charset.forName( "ISO-8859-1" ) ), "UTF-8" ) : null;
        } catch ( UnsupportedEncodingException unableToParseString ) {
            return null;
        }
    }
    
    
    public java.sql.Date getNDaysBackDate( int noOfDays){
        LOG.debug( "method getNDaysBackDate started for noOfDays %s " + noOfDays );
        Calendar time = Calendar.getInstance();
        time.add( Calendar.DATE, -1 * noOfDays );
        // strip the time component of start time
        time.set( Calendar.HOUR_OF_DAY, 0 );
        time.set( Calendar.MINUTE, 0 );
        time.set( Calendar.SECOND, 0 );
        time.set( Calendar.MILLISECOND, 0 );

        java.sql.Date date = new java.sql.Date(  time.getTimeInMillis()  );   
        LOG.debug( "method getNDaysBackDate finished for noOfDays %s " + noOfDays );
        return date;
    }
    
    /**
     * Convert to different timezones
     * @param date
     * @param toTimeZone
     * @return
     */
    public String convertDateToTimeZone( long date, String toTimeZone )
    {	
    	DateTime dateTime = new DateTime(new Date(date));
    	DateTimeZone dateTimeZone = DateTimeZone.forID(toTimeZone);
    	DateTime dateTimeWithZone = dateTime.withZone(dateTimeZone);
    	Date dateWithZone = dateTimeWithZone.toLocalDateTime().toDate();
    	SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy, HH:mm");
    	return (dateFormat.format(dateWithZone));
    }


    public String formatNumber( int precision, Object... args ) {
        return String.format( "%." + precision + "f", args);
    }
    
    public String extractHostName( String urlString )
    {
        if ( StringUtils.isEmpty( urlString ) ) {
            return StringUtils.EMPTY;
        } else {
            try {
                return new URL( urlString ).getHost();
            } catch ( MalformedURLException e ) {
                LOG.warn( "extractHostName(): Not a valid URL: {}", urlString );
                return StringUtils.EMPTY;
            }
        }
    }

    /**Common Method to find pageid from social media URL
     * @param url
     * @param urlRegex
     * @return
     */
    public static String getPageIdFromURL(String url, String urlRegex){
        Matcher matcher = Pattern.compile( urlRegex ).matcher( url );
        if(matcher.find() && matcher.groupCount() >=2){
            return matcher.group(2);
        }
        return null;
    }

    /**
     * Get facebook page id from facebook page URL
     * @param url
     * @return
     */
    public static String getFacebookPageIdFromURL(String url){
        return getPageIdFromURL(url, URL_REGEX_FOR_FACEBOOK_PAGE_ID);
    }

    public static boolean validateCustomerEmail( String emailId )
    {
        LOG.info( "Method validateCustomerEmail called" );
        Pattern pattern = Pattern.compile( CommonConstants.CUSTOMER_EMAIL_REGEX, Pattern.CASE_INSENSITIVE );
        Matcher matcher = pattern.matcher( emailId.trim() );
        return matcher.matches();
    }

    /*
     * Method to generate complete profile URL for based on the profile
     */
    public  String getCompleteUrlForSettings(String profileUrl, String collectionName)
    {
            switch ( collectionName ) {
                case CommonConstants.BRANCH_SETTINGS_COLLECTION:
                    return applicationBaseUrl + CommonConstants.BRANCH_PROFILE_FIXED_URL + profileUrl ;
                case CommonConstants.REGION_SETTINGS_COLLECTION:
                    return applicationBaseUrl + CommonConstants.REGION_PROFILE_FIXED_URL + profileUrl;
                case CommonConstants.COMPANY_SETTINGS_COLLECTION:
                    return applicationBaseUrl + CommonConstants.COMPANY_PROFILE_FIXED_URL + profileUrl ;
                case CommonConstants.AGENT_SETTINGS_COLLECTION:
                    return applicationBaseUrl + CommonConstants.AGENT_PROFILE_FIXED_URL + profileUrl ;
            }
            return null;
    }

    /**
     * A utility method for converting a cloudfront image url to s3 image url
     * @param cloudFrontUrl
     * @param amazonEndpoint
     * @param amazonBucket
     */
    public static String convertCloudFrontUrlToS3Url( String cloudFrontUrl, String amazonEndpoint, String amazonBucket )
    {
        String s3Prefix = amazonEndpoint+CommonConstants.FILE_SEPARATOR+amazonBucket;
        return s3Prefix + cloudFrontUrl.substring( 36, cloudFrontUrl.length() ) ;
    }
}
