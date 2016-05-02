package com.realtech.socialsurvey.core.services.generator.impl;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.batchtracker.BatchTrackerService;
import com.realtech.socialsurvey.core.services.generator.SiteMapService;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.upload.FileUploadService;
import com.realtech.socialsurvey.core.utils.sitemap.SiteMapGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;


public class SiteMapServiceImpl implements SiteMapService
{
    public static final Logger LOG = LoggerFactory.getLogger( SiteMapServiceImpl.class );

    @Autowired private SiteMapGenerator companySiteMapGenerator;
    @Autowired private SiteMapGenerator regionSiteMapGenerator;
    @Autowired private SiteMapGenerator branchSiteMapGenerator;
    @Autowired private SiteMapGenerator agentSiteMapGenerator;
    @Autowired private FileUploadService uploadService;
    @Value ("${AMAZON_ENV_PREFIX}") private String envPrefix;

    @Value ("${COMPANY_SITEMAP_PATH}") private String companySiteMapPath;

    @Value ("${REGION_SITEMAP_PATH}") private String regionSiteMapPath;
    @Value ("${BRANCH_SITEMAP_PATH}") private String branchSiteMapPath;
    @Value ("${INDIVIDUAL_SITEMAP_PATH}") private String individualSiteMapPath;
    @Value ("${SITEMAP_BUCKET}") private String siteMapBucket;
    @Autowired private BatchTrackerService batchTrackerService;


    @Override @Transactional public void siteMapGenerator()
    {
        try {

            //update last run start time
            batchTrackerService
                .getLastRunEndTimeAndUpdateLastStartTimeByBatchType( CommonConstants.BATCH_TYPE_APPLICATION_SITE_MAP_GENERATOR,
                    CommonConstants.BATCH_NAME_APPLICATION_SITE_MAP_GENERATOR );

            companySiteMapGenerator.setInterval( SiteMapGenerator.DAILY_CONTENT );
            companySiteMapGenerator.setOrganizationUnit( SiteMapGenerator.ORG_COMPANY );
            Thread companySiteMapGeneratorThread = new Thread( companySiteMapGenerator );
            companySiteMapGeneratorThread.start();
            regionSiteMapGenerator.setInterval( SiteMapGenerator.DAILY_CONTENT );
            regionSiteMapGenerator.setOrganizationUnit( SiteMapGenerator.ORG_REGION );
            Thread regionSiteMapGeneratorThread = new Thread( regionSiteMapGenerator );
            regionSiteMapGeneratorThread.start();
            branchSiteMapGenerator.setInterval( SiteMapGenerator.DAILY_CONTENT );
            branchSiteMapGenerator.setOrganizationUnit( SiteMapGenerator.ORG_BRANCH );
            Thread branchSiteMapGeneratorThread = new Thread( branchSiteMapGenerator );
            branchSiteMapGeneratorThread.start();
            agentSiteMapGenerator.setInterval( SiteMapGenerator.DAILY_CONTENT );
            agentSiteMapGenerator.setOrganizationUnit( SiteMapGenerator.ORG_INDIVIDUAL );
            Thread agentSiteMapGeneratorThread = new Thread( agentSiteMapGenerator );
            agentSiteMapGeneratorThread.start();
            try {
                companySiteMapGeneratorThread.join();
                regionSiteMapGeneratorThread.join();
                branchSiteMapGeneratorThread.join();
                agentSiteMapGeneratorThread.join();
            } catch ( InterruptedException e ) {
                LOG.error( "Exception while joining to sitemap threads. ", e );
                throw e;
            }
            LOG.info( "Done creating sitemaps. Now dumping the sitemaps" );
            // upload company sitemap
            try {
                uploadFile( companySiteMapPath, uploadService, envPrefix );
            } catch ( NonFatalException e ) {
                LOG.error( "Could not upload company sitemap to amazon", e );
                throw e;
            }
            try {
                uploadFile( regionSiteMapPath, uploadService, envPrefix );
            } catch ( NonFatalException e ) {
                LOG.error( "Could not upload region sitemap to amazon", e );
                throw e;
            }
            try {
                uploadFile( branchSiteMapPath, uploadService, envPrefix );
            } catch ( NonFatalException e ) {
                LOG.error( "Could not upload office sitemap to amazon", e );
                throw e;
            }
            try {
                uploadFile( individualSiteMapPath, uploadService, envPrefix );
            } catch ( NonFatalException e ) {
                LOG.error( "Could not upload individual sitemap to amazon", e );
                throw e;
            }

            //Update last build time in batch tracker table
            batchTrackerService.updateLastRunEndTimeByBatchType( CommonConstants.BATCH_TYPE_APPLICATION_SITE_MAP_GENERATOR );

        } catch ( Exception e ) {
            LOG.error( "Error in ApplicationSiteMapGenerator", e );
            try {
                //update batch tracker with error message
                batchTrackerService
                    .updateErrorForBatchTrackerByBatchType( CommonConstants.BATCH_TYPE_APPLICATION_SITE_MAP_GENERATOR,
                        e.getMessage() );
                //send report bug mail to admin
                batchTrackerService
                    .sendMailToAdminRegardingBatchError( CommonConstants.BATCH_NAME_APPLICATION_SITE_MAP_GENERATOR,
                        System.currentTimeMillis(), e );
            } catch ( NoRecordsFetchedException | InvalidInputException e1 ) {
                LOG.error( "Error while updating error message in SocialPostsDeltaImport " );
            } catch ( UndeliveredEmailException e1 ) {
                LOG.error( "Error while sending report excption mail to admin " );
            }
        }
    }


    public void uploadFile( String filePath, FileUploadService uploadService, String envPrefix ) throws NonFatalException
    {
        LOG.info( "Uploading " + filePath + " to Amazon" );
        uploadService.uploadFileAtSpeicifiedBucket( new File( filePath ),
            filePath.substring( filePath.lastIndexOf( CommonConstants.FILE_SEPARATOR ) + 1 ), siteMapBucket, true );

    }
}
