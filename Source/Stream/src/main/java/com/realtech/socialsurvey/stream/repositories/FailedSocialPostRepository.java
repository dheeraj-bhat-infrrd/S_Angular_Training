package com.realtech.socialsurvey.stream.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.realtech.socialsurvey.stream.entities.FailedSocialPost;


public interface FailedSocialPostRepository extends MongoRepository<FailedSocialPost, String>
{

    List<FailedSocialPost> findByMessageType( String messageType, Pageable pageable );


    long countByMessageType( String messageType );


    void delete( FailedSocialPost failedSocialPost );
}
