package com.realtech.socialsurvey.stream.services.impl;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;

import com.realtech.socialsurvey.stream.common.FailedMessageConstants;
import com.realtech.socialsurvey.stream.entities.FailedSocialPost;
import com.realtech.socialsurvey.stream.repositories.FailedSocialPostRepository;
import com.realtech.socialsurvey.stream.services.FailedSocialPostService;


@Service
public class FailedSocialPostServiceImpl implements FailedSocialPostService
{

    private static final Logger LOG = LoggerFactory.getLogger( FailedSocialPostServiceImpl.class );

    private static final int NUMBER_OF_RECORDS = 10;
    private KafkaTemplate<String, String> kafkaSocialMonitorTemplate;
    private FailedSocialPostRepository failedSocialPostRepository;


    @Autowired
    @Qualifier ( "socialMonitorTemplate")
    public void setKafkaSocialMonitorTemplate( KafkaTemplate<String, String> kafkaSocialMonitorTemplate )
    {
        this.kafkaSocialMonitorTemplate = kafkaSocialMonitorTemplate;
    }


    @Autowired
    public void setFailedSocialPostRepository( FailedSocialPostRepository failedSocialPostRepository )
    {
        this.failedSocialPostRepository = failedSocialPostRepository;
    }


    @Override
    public ResponseEntity<?> queueFailedSocialPosts() throws InterruptedException, ExecutionException, TimeoutException
    {
        LOG.info( "Initiated queuing failed social posts onto kafka." );
        getFailedSocialPostsAndQueue();
        return new ResponseEntity<>( HttpStatus.CREATED );
    }


    private void getFailedSocialPostsAndQueue() throws InterruptedException, ExecutionException, TimeoutException
    {
        LOG.info( "Fetching failed social posts from mongo." );
        long totalDocs = failedSocialPostRepository.countByMessageType( FailedMessageConstants.SOCIAL_POST_MESSAGE );
        int pageNum = 0;
        if ( totalDocs != 0 ) {
            List<FailedSocialPost> failedSocialPosts = null;
            Pageable numberOfRecords = new PageRequest( pageNum, NUMBER_OF_RECORDS );
            for ( int i = 0; i < totalDocs; i = i + NUMBER_OF_RECORDS ) {
                failedSocialPosts = failedSocialPostRepository.findByMessageType( FailedMessageConstants.SOCIAL_POST_MESSAGE
                , numberOfRecords);
                for ( FailedSocialPost failedSocialPost : failedSocialPosts ) {
                    if ( failedSocialPost.getData() != null ) {
                        failedSocialPost.getData().setIsRetried( true );
                        failedSocialPost.getData().setId( failedSocialPost.getData().getPostId()+ "_" + failedSocialPost.getData().getCompanyId() );
                        LOG.trace( "failed social post: {}", failedSocialPost.getData() );
                        kafkaSocialMonitorTemplate.send( new GenericMessage<>( failedSocialPost.getData() ) ).get( 60,
                            TimeUnit.SECONDS );
                    }
                }
                failedSocialPostRepository.delete( failedSocialPosts );
            }
        } else {
            LOG.info( "No failed social posts found." );
        }
    }
}
