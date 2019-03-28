package com.realtech.socialsurvey.core.services.social.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.CompanyPositions;
import com.realtech.socialsurvey.core.entities.LinkedInProfileData;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.PositionValues;
import com.realtech.socialsurvey.core.entities.SkillValues;
import com.realtech.socialsurvey.core.entities.SocialMediaTokens;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.services.social.SocialAsyncService;
import com.realtech.socialsurvey.core.services.social.SocialManagementService;
import com.realtech.socialsurvey.core.vo.IdInfoVO;


@Component
public class SocialAsyncServiceImpl implements SocialAsyncService
{

    private static final Logger LOG = LoggerFactory.getLogger( SocialAsyncServiceImpl.class );

    @Autowired
    private ProfileManagementService profileManagementService;

    @Autowired
    private SolrSearchService solrSearchService;

    @Autowired
    private SocialManagementService socialManagementService;

    @Value ( "${LINKED_IN_REST_API_URI_V2}")
    private String linkedInRestApiUriV2;


    @Async
    @Override
    public Future<OrganizationUnitSettings> linkedInDataUpdateAsync( String collection, OrganizationUnitSettings unitSettings,
        SocialMediaTokens mediaTokens )
    {
        LOG.info( "Method linkedInDataUpdateAsync() called from SocialAsyncServiceImpl" );
        unitSettings = linkedInDataUpdate( collection, unitSettings, mediaTokens );
        unitSettings = updateLinkedInProfileImage( collection, unitSettings );
        LOG.info( "Method linkedInDataUpdateAsync() finished from SocialAsyncServiceImpl" );
        return new AsyncResult<OrganizationUnitSettings>( unitSettings );
    }


    @Override
    public OrganizationUnitSettings linkedInDataUpdate( String collection, OrganizationUnitSettings unitSettings,
        SocialMediaTokens mediaTokens )
    {
        LOG.info( "Method linkedInDataUpdate() called from SocialAsyncServiceImpl" );

        LinkedInProfileData linkedInProfileData = null;
        try {
            HttpClient httpclient = HttpClientBuilder.create().build();
            HttpGet httpGet = new HttpGet( linkedInRestApiUriV2 );
            httpGet.setHeader( HttpHeaders.AUTHORIZATION, "Bearer " + mediaTokens.getLinkedInV2Token().getLinkedInAccessToken() );
            httpGet.setHeader( CommonConstants.X_RESTLI_PROTOCOL_VERSION, CommonConstants.X_RESTLI_PROTOCOL_VERSION_VALUE );
            String basicProfileStr = httpclient.execute( httpGet, new BasicResponseHandler() );
            IdInfoVO idInfoVO = new Gson().fromJson( basicProfileStr, IdInfoVO.class );

            linkedInProfileData = new LinkedInProfileData();
            linkedInProfileData.setId( idInfoVO.getId() );
        } catch ( Exception e ) {
            LOG.error( e.getMessage(), e );
        }

        if ( linkedInProfileData != null ) {
            LOG.debug( "Adding linkedin data into collection" );
            try {
                // Waiting for becoming LinkedIn partner.
                //mediaTokens.setLinkedInProfileUrl( linkedInProfileData.getPublicProfileUrl() );
                if ( collection.equalsIgnoreCase( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION ) ) {
                    socialManagementService.updateAgentSocialMediaTokens( (AgentSettings) unitSettings, mediaTokens );
                }
                unitSettings.setLinkedInProfileData( linkedInProfileData );
                profileManagementService.updateLinkedInProfileData( collection, unitSettings, linkedInProfileData );
            } catch ( InvalidInputException e ) {
                LOG.error( "Error while updating linkedin profile data", e );
            }
        }

        // update the details if its not present in the user settings already
        // check to see if contact details were modified
        boolean isContactDetailsUpdated = false;
        if ( unitSettings.getContact_details().getAbout_me() == null
            || unitSettings.getContact_details().getAbout_me().isEmpty() ) {
            LOG.debug( "About me is empty. Filling with linkedin data" );
            if ( linkedInProfileData.getSummary() != null && !linkedInProfileData.getSummary().isEmpty() ) {
                unitSettings.getContact_details().setAbout_me( linkedInProfileData.getSummary() );
                isContactDetailsUpdated = true;
            }
        }

        if ( unitSettings.getContact_details().getName() == null || unitSettings.getContact_details().getName().isEmpty() ) {
            LOG.debug( "Name is empty. Filling with linkedin data" );
            unitSettings.getContact_details().setFirstName( linkedInProfileData.getFirstName() );
            if ( linkedInProfileData.getLastName() != null && !linkedInProfileData.getLastName().isEmpty() ) {
                unitSettings.getContact_details().setLastName( linkedInProfileData.getLastName() );
            }
            unitSettings.getContact_details().setName(
                linkedInProfileData.getFirstName()
                    + ( linkedInProfileData.getLastName() != null ? " " + linkedInProfileData.getLastName() : "" ) );
            isContactDetailsUpdated = true;
        }

        if ( unitSettings.getContact_details().getTitle() == null || unitSettings.getContact_details().getTitle().isEmpty() ) {
            LOG.debug( "Title is empty. Filling with linkedin data" );
            if ( linkedInProfileData.getHeadline() != null && !linkedInProfileData.getHeadline().isEmpty() ) {
                unitSettings.getContact_details().setTitle( linkedInProfileData.getHeadline() );
                isContactDetailsUpdated = true;
            }
        }

        if ( unitSettings.getContact_details().getLocation() == null
            || unitSettings.getContact_details().getLocation().isEmpty() ) {
            LOG.debug( "Location is empty. Filling with linkedin data" );
            if ( linkedInProfileData.getLocation() != null ) {
                unitSettings.getContact_details().setLocation( linkedInProfileData.getLocation().getName() );
                isContactDetailsUpdated = true;
            }
        }

        if ( unitSettings.getContact_details().getIndustry() == null
            || unitSettings.getContact_details().getIndustry().isEmpty() ) {
            LOG.debug( "Industry is empty. Filling with linkedin data" );
            if ( linkedInProfileData.getIndustry() != null && !linkedInProfileData.getIndustry().isEmpty() ) {
                unitSettings.getContact_details().setIndustry( linkedInProfileData.getIndustry() );
                isContactDetailsUpdated = true;
            }
        }

        // updating contact details to mongo
        if ( isContactDetailsUpdated ) {
            LOG.debug( "Contact details were updated. Updating the same in database" );
            try {
                profileManagementService.updateContactDetails( collection, unitSettings, unitSettings.getContact_details() );
            } catch ( InvalidInputException e ) {
                LOG.error( e.getMessage(), e );
            }
        }

        if ( unitSettings instanceof AgentSettings ) {
            AgentSettings agentSettings = (AgentSettings) unitSettings;

            if ( agentSettings.getExpertise() == null ) {
                LOG.debug( "Expertise is not present. Filling the data from linkedin" );
                if ( linkedInProfileData.getSkills() != null && linkedInProfileData.getSkills().getValues() != null
                    && !linkedInProfileData.getSkills().getValues().isEmpty() ) {

                    List<String> expertiseList = new ArrayList<String>();
                    for ( SkillValues skillValue : linkedInProfileData.getSkills().getValues() ) {
                        expertiseList.add( skillValue.getSkill().getName() );
                    }
                    agentSettings.setExpertise( expertiseList );

                    if ( agentSettings.getExpertise() != null && agentSettings.getExpertise().size() > 0 ) {
                        try {
                            profileManagementService.updateAgentExpertise( agentSettings, agentSettings.getExpertise() );
                        } catch ( InvalidInputException e ) {
                            LOG.error( e.getMessage(), e );
                        }
                    }
                }
            }

            if ( agentSettings.getPositions() == null || agentSettings.getPositions().isEmpty() ) {
                LOG.debug( "Positions is not present. Filling the data from linkedin" );

                if ( linkedInProfileData.getPositions() != null && linkedInProfileData.getPositions().getValues() != null
                    && !linkedInProfileData.getPositions().getValues().isEmpty() ) {

                    List<CompanyPositions> companyPositionsList = new ArrayList<CompanyPositions>();
                    CompanyPositions companyPositions = null;

                    for ( PositionValues positionValue : linkedInProfileData.getPositions().getValues() ) {
                        companyPositions = new CompanyPositions();
                        companyPositions.setName( positionValue.getCompany().getName() );
                        companyPositions.setStartMonth( positionValue.getStartDate().getMonth() );
                        companyPositions.setStartYear( positionValue.getStartDate().getYear() );
                        companyPositions.setStartTime( positionValue.getStartDate().getMonth() + "-"
                            + positionValue.getStartDate().getYear() );
                        companyPositions.setIsCurrent( positionValue.isCurrent() );
                        companyPositions.setTitle( positionValue.getTitle() );

                        if ( !positionValue.isCurrent() ) {
                            companyPositions.setEndTime( positionValue.getEndDate().getMonth() + "-"
                                + positionValue.getEndDate().getYear() );
                            companyPositions.setEndMonth( positionValue.getEndDate().getMonth() );
                            companyPositions.setEndYear( positionValue.getEndDate().getYear() );
                        }
                        companyPositionsList.add( companyPositions );
                    }

                    if ( companyPositionsList.size() > 0 ) {
                        agentSettings.setPositions( companyPositionsList );
                        try {
                            profileManagementService.updateAgentCompanyPositions( agentSettings, companyPositionsList );
                        } catch ( InvalidInputException e ) {
                            LOG.error( e.getMessage(), e );
                        }
                    }
                }
            }

            // finally update details in solr
            try {
                LOG.debug( "Updating details in solr" );
                solrSearchService.editUserInSolr( agentSettings.getIden(), CommonConstants.ABOUT_ME_SOLR, agentSettings
                    .getContact_details().getAbout_me() );
            } catch ( SolrException e ) {
                LOG.error( "Could not update details in solr", e );
            } catch ( InvalidInputException e ) {
                LOG.error( "Could not update details in solr", e );
            }
        }

        LOG.info( "Method linkedInDataUpdate() finished from SocialAsyncServiceImpl" );
        return unitSettings;
    }


    @Override
    public OrganizationUnitSettings updateLinkedInProfileImage( String collection, OrganizationUnitSettings unitSettings )
    {
        LOG.debug( "Method updateLinkedInProfileImage() called from SocialAsyncServiceImpl" );
        LinkedInProfileData linkedInProfileData = unitSettings.getLinkedInProfileData();

        // updating profile image url to mongo
        if ( unitSettings.getProfileImageUrl() == null || unitSettings.getProfileImageUrl().isEmpty() ) {
            try {
                if ( linkedInProfileData != null && linkedInProfileData.getPictureUrls() != null
                    && linkedInProfileData.getPictureUrls().getValues() != null
                    && !linkedInProfileData.getPictureUrls().getValues().isEmpty() ) {
                    String photoUrl = uploadImageToCloud( linkedInProfileData.getPictureUrls().getValues().get( 0 ) );
                    unitSettings.setProfileImageUrl( photoUrl );
                    unitSettings.setProfileImageUrlThumbnail( photoUrl );
                    profileManagementService.updateProfileImage( collection, unitSettings, unitSettings.getProfileImageUrl() );
                }
            } catch ( InvalidInputException e ) {
                LOG.error( e.getMessage(), e );
            } catch ( Exception e ) {
                LOG.error( e.getMessage(), e );
            }
        }

        // updating profile image url to solr
        try {
            LOG.debug( "Updating profile image in solr" );
            Map<String, Object> updateMap = new HashMap<String, Object>();
            updateMap.put( CommonConstants.PROFILE_IMAGE_URL_SOLR, unitSettings.getProfileImageUrl() );
            updateMap.put( CommonConstants.PROFILE_IMAGE_THUMBNAIL_COLUMN, unitSettings.getProfileImageUrlThumbnail() );
            updateMap.put( CommonConstants.IS_PROFILE_IMAGE_SET_SOLR, true );
            solrSearchService.editUserInSolrWithMultipleValues( unitSettings.getIden(), updateMap );
            /*solrSearchService.editUserInSolr( unitSettings.getIden(), CommonConstants.PROFILE_IMAGE_URL_SOLR,
                unitSettings.getProfileImageUrl() );*/
        } catch ( SolrException e ) {
            LOG.error( "Could not update details in solr", e );
        } catch ( InvalidInputException e ) {
            LOG.error( "Could not update details in solr", e );
        }

        LOG.debug( "Method updateLinkedInProfileImage() called from SocialAsyncServiceImpl" );
        return unitSettings;
    }
    

    public String uploadImageToCloud(String imageUrl) throws Exception 
    {
        LOG.info( "Method uploadImageToCloud() called for image url {}", imageUrl );
        String imageName = java.util.UUID.randomUUID().toString();
        if ( imageUrl.contains( ".png" ) || imageUrl.contains( ".PNG" ) ) {
            imageName = imageName + ".png";
        } else if ( imageUrl.contains( ".jpg" ) || imageUrl.contains( ".JPG" ) ) {
            imageName = imageName + ".jpg";
        } else if ( imageUrl.contains( ".jpeg" ) || imageUrl.contains( ".JPEG" ) ) {
            imageName = imageName + ".jpeg";
        } else if(imageUrl.contains( "media.licdn.com" )){
            imageName = imageName + ".jpg";
        }else {

            LOG.error( "The url given is not a valid image url {} " , imageUrl );
            throw new InvalidInputException( "Image format not valid" );
        }
        return profileManagementService.copyImage( imageUrl, imageName );
        
    }

}
