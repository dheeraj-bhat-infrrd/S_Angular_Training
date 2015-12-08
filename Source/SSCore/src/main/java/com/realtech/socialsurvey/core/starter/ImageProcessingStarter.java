package com.realtech.socialsurvey.core.starter;

import java.util.Map;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.batchtracker.BatchTrackerService;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.utils.images.ImageProcessor;
import com.realtech.socialsurvey.core.utils.images.impl.ImageProcessingException;


public class ImageProcessingStarter extends QuartzJobBean
{

    public static final Logger LOG = LoggerFactory.getLogger( ImageProcessingStarter.class );

    private ImageProcessor imageProcessor;

    @Autowired
    private OrganizationManagementService organizationManagementService;

    private BatchTrackerService batchTrackerService;


    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext ) throws JobExecutionException
    {
        try {
            LOG.info( "Starting processing of images" );
            initializeDependencies( jobExecutionContext.getMergedJobDataMap() );

            // update last start time
            batchTrackerService.getLastRunEndTimeAndUpdateLastStartTimeByBatchType(
                CommonConstants.BATCH_TYPE_IMAGE_PROCESSING_STARTER, CommonConstants.BATCH_NAME_IMAGE_PROCESSING_STARTER );

            Map<Long, String> images = null;
            // get unprocessed company profile images
            images = getUnprocessedProfileImages( CommonConstants.COMPANY_SETTINGS_COLLECTION );
            String fileName = null;
            if ( images != null ) {
                for ( long id : images.keySet() ) {
                    try {
                        fileName = imageProcessor.processImage( images.get( id ), CommonConstants.IMAGE_TYPE_PROFILE );
                        updateImage( id, fileName, CommonConstants.COMPANY_SETTINGS_COLLECTION,
                            CommonConstants.IMAGE_TYPE_PROFILE );
                    } catch ( ImageProcessingException | InvalidInputException e ) {
                        LOG.error( "Skipping... Could not process image: " + id + " : " + images.get( id ), e );
                    }
                }
            }
            // get unprocessed region profile images
            images = getUnprocessedProfileImages( CommonConstants.REGION_SETTINGS_COLLECTION );
            if ( images != null ) {
                for ( long id : images.keySet() ) {
                    try {
                        fileName = imageProcessor.processImage( images.get( id ), CommonConstants.IMAGE_TYPE_PROFILE );
                        updateImage( id, fileName, CommonConstants.REGION_SETTINGS_COLLECTION,
                            CommonConstants.IMAGE_TYPE_PROFILE );
                    } catch ( ImageProcessingException | InvalidInputException e ) {
                        LOG.error( "Skipping... Could not process image: " + id + " : " + images.get( id ), e );
                    }
                }
            }
            // get unprocessed branch profile images
            images = getUnprocessedProfileImages( CommonConstants.BRANCH_SETTINGS_COLLECTION );
            if ( images != null ) {
                for ( long id : images.keySet() ) {
                    try {
                        fileName = imageProcessor.processImage( images.get( id ), CommonConstants.IMAGE_TYPE_PROFILE );
                        updateImage( id, fileName, CommonConstants.BRANCH_SETTINGS_COLLECTION,
                            CommonConstants.IMAGE_TYPE_PROFILE );
                    } catch ( ImageProcessingException | InvalidInputException e ) {
                        LOG.error( "Skipping... Could not process image: " + id + " : " + images.get( id ), e );
                    }
                }
            }
            // get unprocessed agent profile images
            images = getUnprocessedProfileImages( CommonConstants.AGENT_SETTINGS_COLLECTION );
            if ( images != null ) {
                for ( long id : images.keySet() ) {
                    try {
                        fileName = imageProcessor.processImage( images.get( id ), CommonConstants.IMAGE_TYPE_PROFILE );
                        updateImage( id, fileName, CommonConstants.AGENT_SETTINGS_COLLECTION,
                            CommonConstants.IMAGE_TYPE_PROFILE );
                    } catch ( ImageProcessingException | InvalidInputException e ) {
                        LOG.error( "Skipping... Could not process image: " + id + " : " + images.get( id ), e );
                    }
                }
            }

            // get unprocessed company logo images
            images = getUnprocessedLogoImages( CommonConstants.COMPANY_SETTINGS_COLLECTION );
            if ( images != null ) {
                for ( long id : images.keySet() ) {
                    try {
                        fileName = imageProcessor.processImage( images.get( id ), CommonConstants.IMAGE_TYPE_LOGO );
                        updateImage( id, fileName, CommonConstants.COMPANY_SETTINGS_COLLECTION, CommonConstants.IMAGE_TYPE_LOGO );
                    } catch ( ImageProcessingException | InvalidInputException e ) {
                        LOG.error( "Skipping... Could not process image: " + id + " : " + images.get( id ), e );
                    }
                }
            }
            // get unprocessed region logo images
            images = getUnprocessedLogoImages( CommonConstants.REGION_SETTINGS_COLLECTION );
            if ( images != null ) {
                for ( long id : images.keySet() ) {
                    try {
                        fileName = imageProcessor.processImage( images.get( id ), CommonConstants.IMAGE_TYPE_LOGO );
                        updateImage( id, fileName, CommonConstants.REGION_SETTINGS_COLLECTION, CommonConstants.IMAGE_TYPE_LOGO );
                    } catch ( ImageProcessingException | InvalidInputException e ) {
                        LOG.error( "Skipping... Could not process image: " + id + " : " + images.get( id ), e );
                    }
                }
            }
            // get unprocessed branch logo images
            images = getUnprocessedLogoImages( CommonConstants.BRANCH_SETTINGS_COLLECTION );
            if ( images != null ) {
                for ( long id : images.keySet() ) {
                    try {
                        fileName = imageProcessor.processImage( images.get( id ), CommonConstants.IMAGE_TYPE_LOGO );
                        updateImage( id, fileName, CommonConstants.BRANCH_SETTINGS_COLLECTION, CommonConstants.IMAGE_TYPE_LOGO );
                    } catch ( ImageProcessingException | InvalidInputException e ) {
                        LOG.error( "Skipping... Could not process image: " + id + " : " + images.get( id ), e );
                    }
                }
            }

            // get unprocessed branch logo images
            images = getUnprocessedLogoImages( CommonConstants.AGENT_SETTINGS_COLLECTION );
            if ( images != null ) {
                for ( long id : images.keySet() ) {
                    try {
                        fileName = imageProcessor.processImage( images.get( id ), CommonConstants.IMAGE_TYPE_LOGO );
                        updateImage( id, fileName, CommonConstants.AGENT_SETTINGS_COLLECTION, CommonConstants.IMAGE_TYPE_LOGO );
                    } catch ( ImageProcessingException | InvalidInputException e ) {
                        LOG.error( "Skipping... Could not process image: " + id + " : " + images.get( id ), e );
                    }
                }
            }

            /*try {
            	imageProcessor
            			.processImage(
            					"https://don7n2as2v6aa.cloudfront.net/userprofilepics/P-ae12f4d2e10a5437b18dbc58c55170737b409c7dd5aa3a5121f77757f94d5acd71b277130acdb9a08b3ea8169734c834aaee0c036840e20915ca0873e8d0ae19.png",
            					CommonConstants.IMAGE_TYPE_PROFILE);
            }
            catch (ImageProcessingException | InvalidInputException e) {
            	LOG.error("Could not process image", e);
            }*/

            //updating last run time for batch in database
            batchTrackerService.updateLastRunEndTimeByBatchType( CommonConstants.BATCH_TYPE_IMAGE_PROCESSING_STARTER );
            LOG.info( "Finished processing of images" );
        } catch ( Exception e ) {
            LOG.error( "Error in processing of images", e );
            try {
                //update batch tracker with error message
                batchTrackerService.updateErrorForBatchTrackerByBatchType( CommonConstants.BATCH_TYPE_IMAGE_PROCESSING_STARTER,
                    e.getMessage() );
                //send report bug mail to admin
                batchTrackerService.sendMailToAdminRegardingBatchError( CommonConstants.BATCH_NAME_IMAGE_PROCESSING_STARTER,
                    System.currentTimeMillis(), e );
            } catch ( NoRecordsFetchedException | InvalidInputException e1 ) {
                LOG.error( "Error while updating error message in processing of images " );
            } catch ( UndeliveredEmailException e1 ) {
                LOG.error( "Error while sending report excption mail to admin " );
            }
        }
    }


    private void initializeDependencies( JobDataMap jobMap )
    {
        imageProcessor = (ImageProcessor) jobMap.get( "imageProcessor" );
        organizationManagementService = (OrganizationManagementService) jobMap.get( "organizationManagementService" );
        batchTrackerService = (BatchTrackerService) jobMap.get( "batchTrackerService" );
    }


    private Map<Long, String> getUnprocessedProfileImages( String collection )
    {
        LOG.debug( "Getting unprocessed profile images for collection " + collection );
        Map<Long, String> unprocessedProfileImages = null;
        try {
            unprocessedProfileImages = organizationManagementService.getListOfUnprocessedImages( collection,
                CommonConstants.IMAGE_TYPE_PROFILE );
            if ( unprocessedProfileImages == null ) {
                LOG.debug( "No unprocessed profile images exist" );
            }
        } catch ( InvalidInputException e ) {
            LOG.error( "The collection name or the image type is invalid. Reason : ", e );
        }
        LOG.debug( "returning unprocessed profile images" );
        return unprocessedProfileImages;
    }


    private Map<Long, String> getUnprocessedLogoImages( String collection )
    {
        LOG.debug( "Getting unprocessed logo images for collection  " + collection );

        Map<Long, String> unprocessedLogoImages = null;
        if ( collection == null || collection.isEmpty() ) {
            LOG.error( "Collection can't be empty" );
        } else {
            try {
                unprocessedLogoImages = organizationManagementService.getListOfUnprocessedImages( collection,
                    CommonConstants.IMAGE_TYPE_LOGO );
                if ( unprocessedLogoImages == null ) {
                    LOG.debug( "No unprocessed logo images exist" );
                }
            } catch ( InvalidInputException e ) {
                LOG.error( "The collection name or the image type is invalid. Reason : ", e );
            }
            LOG.debug( "returning unprocessed logo images" );
        }
        return unprocessedLogoImages;
    }


    private void updateImage( long iden, String fileName, String collectionName, String imageType )
        throws InvalidInputException
    {
        LOG.info( "Method updateImage started" );
        organizationManagementService.updateImageForOrganizationUnitSetting( iden, fileName, collectionName, imageType, true,
            true );
        LOG.info( "Method updateImage finished" );
    }
}
