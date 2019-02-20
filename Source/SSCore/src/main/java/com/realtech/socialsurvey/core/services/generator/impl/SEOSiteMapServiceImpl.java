package com.realtech.socialsurvey.core.services.generator.impl;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.batchtracker.BatchTrackerService;
import com.realtech.socialsurvey.core.services.generator.SEOSiteMapService;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.upload.FileUploadService;
import com.realtech.socialsurvey.core.utils.sitemap.SEOSiteMapGenerator;
import com.realtech.socialsurvey.core.utils.sitemap.SiteMapGenerator;


public class SEOSiteMapServiceImpl implements SEOSiteMapService
{
    public static final Logger LOG = LoggerFactory.getLogger( SEOSiteMapServiceImpl.class );

    @Autowired private SEOSiteMapGenerator seoCompanySiteMapGenerator;
    @Autowired private SEOSiteMapGenerator seoBranchSiteMapGenerator;
    @Autowired private SEOSiteMapGenerator seoAgentSiteMapGenerator;
    @Autowired private FileUploadService uploadService;
    @Value ("${AMAZON_ENV_PREFIX}") private String envPrefix;

    @Value ("${COMPANY_SEO_SITEMAP_PATH}") private String companySiteMapPath;
    @Value ("${BRANCH_SEO_SITEMAP_PATH}") private String branchSiteMapPath;
    @Value ("${INDIVIDUAL_SEO_SITEMAP_PATH}") private String individualSiteMapPath;
    @Value ("${SITEMAP_BUCKET}") private String siteMapBucket;
    @Autowired private BatchTrackerService batchTrackerService;


    @Override@Transactional public void seoSiteMapGenerator()
    {
        try {

            //update last run start time
            batchTrackerService
                .getLastRunEndTimeAndUpdateLastStartTimeByBatchType( CommonConstants.BATCH_TYPE_SEO_SITE_MAP_GENERATOR,
                    CommonConstants.BATCH_NAME_SEO_SITE_MAP_GENERATOR );

            seoCompanySiteMapGenerator.setOrganizationUnit( SiteMapGenerator.ORG_COMPANY );
            Thread companySiteMapGeneratorThread = new Thread( seoCompanySiteMapGenerator );
            companySiteMapGeneratorThread.start();
            seoBranchSiteMapGenerator.setOrganizationUnit( SiteMapGenerator.ORG_BRANCH );
            Thread branchSiteMapGeneratorThread = new Thread( seoBranchSiteMapGenerator );
            branchSiteMapGeneratorThread.start();
            seoAgentSiteMapGenerator.setOrganizationUnit( SiteMapGenerator.ORG_INDIVIDUAL );
            Thread agentSiteMapGeneratorThread = new Thread( seoAgentSiteMapGenerator );
            agentSiteMapGeneratorThread.start();
            try {
                companySiteMapGeneratorThread.join();
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
            batchTrackerService.updateLastRunEndTimeByBatchType( CommonConstants.BATCH_TYPE_SEO_SITE_MAP_GENERATOR );

        } catch ( Exception e ) {
            LOG.error( "Error in SEOSiteMapGenerator", e );
            try {
                //update batch tracker with error message
                batchTrackerService
                    .updateErrorForBatchTrackerByBatchType( CommonConstants.BATCH_TYPE_SEO_SITE_MAP_GENERATOR,
                        e.getMessage() );
                //send report bug mail to admin
                batchTrackerService
                    .sendMailToAdminRegardingBatchError( CommonConstants.BATCH_NAME_SEO_SITE_MAP_GENERATOR,
                        System.currentTimeMillis(), e );
            } catch ( NoRecordsFetchedException | InvalidInputException e1 ) {
                LOG.error( "NoRecordFetched Exception in SEOSiteMapServiceImpl" + e.getMessage());
            } catch ( UndeliveredEmailException e1 ) {
            	LOG.error( "Undelivered Exception in SEOSiteMapServiceImpl" + e.getMessage());
            }
        }
    }


    public void uploadFile( String filePath, FileUploadService uploadService, String envPrefix ) throws NonFatalException
    {
        LOG.info( "Uploading seo sitemap" + filePath + " to Amazon" );
        uploadService.uploadFileAtSpecifiedBucket( new File( filePath ),
            filePath.substring( filePath.lastIndexOf( CommonConstants.FILE_SEPARATOR ) + 1 ), siteMapBucket, true );

    }
}
