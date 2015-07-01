package com.realtech.socialsurvey.core.commons;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class Utils
{

    private final String REGION_PROFILE_URL_PATTERN = "/region/%s/%s";
    private final String BRANCH_PROFILE_URL_PATTERN = "/office/%s/%s";
    private final String AGENT_PROFILE_URL_PATTERN = "/%s";
    private static final String PROFILE_REGEX = "[^\\w]{1,}";
    private static final String PROFILE_REGEX_REPLACEMENT = "-";
    private static final Pattern PATTERN = Pattern.compile( PROFILE_REGEX );
    private static final Logger LOG = LoggerFactory.getLogger( Utils.class );


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
}
