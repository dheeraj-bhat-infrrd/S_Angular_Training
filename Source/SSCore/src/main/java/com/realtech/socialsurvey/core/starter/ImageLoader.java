package com.realtech.socialsurvey.core.starter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import javax.imageio.ImageIO;
import org.apache.commons.io.FileUtils;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.upload.FileUploadService;


/*
 * This class is responsible for getting all the profile images from Mongodb which point to Linkedin.
 * It stores into the Amazon server and updates the same in MongoDB.
 */
public class ImageLoader extends QuartzJobBean
{

    public static final Logger LOG = LoggerFactory.getLogger( ImageLoader.class );

    private OrganizationManagementService organizationManagementService;
    private ProfileManagementService profileManagementService;
    private FileUploadService fileUploadService;

    private String amazonImageBucket;
    private String cdnUrl;


    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext )
    {
        LOG.info( "Executing ImageUploader" );
        new File( CommonConstants.TEMP_FOLDER ).mkdir();
        // initialize the dependencies
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        // Fetch all the profile images pointing to linkedin for company, regions, branches and individuals.
        Map<Long, OrganizationUnitSettings> companySettings = organizationManagementService
            .getSettingsMapWithLinkedinImage( CommonConstants.COMPANY );
        Map<Long, OrganizationUnitSettings> regionSettings = organizationManagementService
            .getSettingsMapWithLinkedinImage( CommonConstants.REGION_COLUMN );
        Map<Long, OrganizationUnitSettings> branchSettings = organizationManagementService
            .getSettingsMapWithLinkedinImage( CommonConstants.BRANCH_NAME_COLUMN );
        Map<Long, OrganizationUnitSettings> agentSettings = organizationManagementService
            .getSettingsMapWithLinkedinImage( "agent" );

        // Process all the company profile images.
        for ( Entry<Long, OrganizationUnitSettings> companySetting : companySettings.entrySet() ) {
            try {
                String image = loadImages( companySetting.getValue() );
                if ( image != null ) {
                    profileManagementService.updateProfileImage(
                        MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySetting.getValue(), image );
                }
            } catch ( Exception e ) {
                LOG.error( "Exception caught in ImageLoader while copying image from linkedin to SocialSurvey server. "
                    + "Nested exception is ", e );
                continue;
            }
        }

        // Process all the region profile images.
        for ( Entry<Long, OrganizationUnitSettings> regionSetting : regionSettings.entrySet() ) {
            try {
                String image = loadImages( regionSetting.getValue() );
                if ( image != null ) {
                    profileManagementService.updateProfileImage(
                        MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionSetting.getValue(), image );
                }
            } catch ( Exception e ) {
                LOG.error( "Exception caught in ImageLoader while copying image from linkedin to SocialSurvey server. "
                    + "Nested exception is ", e );
                continue;
            }
        }

        // Process all the branch profile images.
        for ( Entry<Long, OrganizationUnitSettings> branchSetting : branchSettings.entrySet() ) {
            try {
                String image = loadImages( branchSetting.getValue() );
                if ( image != null ) {
                    profileManagementService.updateProfileImage(
                        MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchSetting.getValue(), image );
                }
            } catch ( Exception e ) {
                LOG.error( "Exception caught in ImageLoader while copying image from linkedin to SocialSurvey server. "
                    + "Nested exception is ", e );
                continue;
            }
        }

        // Process all the individual profile images.
        for ( Entry<Long, OrganizationUnitSettings> agentSetting : agentSettings.entrySet() ) {
            try {
                String image = loadImages( agentSetting.getValue() );
                if ( image != null ) {
                    profileManagementService.updateProfileImage( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION,
                        agentSetting.getValue(), image );
                }
            } catch ( Exception e ) {
                LOG.error( "Exception caught in ImageLoader while copying image from linkedin to SocialSurvey server. "
                    + "Nested exception is ", e );
                continue;
            }
        }
        LOG.info( "Completed ImageUploader" );
    }


    private String loadImages( OrganizationUnitSettings setting ) throws Exception
    {
        String linkedinImageUrl = setting.getProfileImageUrl();
        String imageName = java.util.UUID.randomUUID().toString();
        if ( linkedinImageUrl.contains( ".png" ) || linkedinImageUrl.contains( ".PNG" ) ) {
            imageName = imageName + ".png";
        } else if ( linkedinImageUrl.contains( ".jpg" ) || linkedinImageUrl.contains( ".JPG" ) ) {
            imageName = imageName + ".jpg";
        } else if ( linkedinImageUrl.contains( ".jpeg" ) || linkedinImageUrl.contains( ".JPEG" ) ) {
            imageName = imageName + ".jpeg";
        }

        String destination = copyImage( linkedinImageUrl, imageName );
        return destination;
    }


    private BufferedImage getImageFromUrl( String imageUrl )
    {
        BufferedImage image = null;
        try {
            URL url = new URL( imageUrl );
            image = ImageIO.read( url );
        } catch ( IOException e ) {
            LOG.error( "Exception caught " + e.getMessage() );
        }
        return image;
    }


    private String copyImage( String source, String imageName ) throws Exception
    {

        String fileName = null;
        try {
            BufferedImage image = getImageFromUrl( source );
            if ( image != null ) {
                File tempImage = new File( CommonConstants.TEMP_FOLDER + CommonConstants.FILE_SEPARATOR + imageName );
                tempImage.createNewFile();
                if ( tempImage.exists() ) {
                    if ( imageName.endsWith( ".jpg" ) || imageName.endsWith( ".JPG" ) ) {
                        ImageIO.write( image, "jpg", tempImage );
                    } else if ( imageName.endsWith( ".jpeg" ) || imageName.endsWith( ".JPEG" ) ) {
                        ImageIO.write( image, "png", tempImage );
                    } else if ( imageName.endsWith( ".png" ) || imageName.endsWith( ".PNG" ) ) {
                        ImageIO.write( image, "png", tempImage );
                    }
                    fileName = fileUploadService.uploadProfileImageFile( tempImage, imageName, false );
                    FileUtils
                        .deleteQuietly( new File( CommonConstants.TEMP_FOLDER + CommonConstants.FILE_SEPARATOR + imageName ) );
                    LOG.info( "Successfully retrieved photo of contact" );
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch ( Exception e ) {
            LOG.error( e.getMessage() + ": " + source );
            throw e;
        }

        return cdnUrl + CommonConstants.FILE_SEPARATOR + amazonImageBucket + CommonConstants.FILE_SEPARATOR + fileName;

    }


    private void initializeDependencies( JobDataMap jobMap )
    {
        organizationManagementService = (OrganizationManagementService) jobMap.get( "organizationManagementService" );
        fileUploadService = (FileUploadService) jobMap.get( "fileUploadService" );
        profileManagementService = (ProfileManagementService) jobMap.get( "profileManagementService" );

        amazonImageBucket = (String) jobMap.get( "amazonImageBucket" );
        cdnUrl = (String) jobMap.get( "cdnUrl" );
    }
}