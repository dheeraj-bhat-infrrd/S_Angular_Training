package com.realtech.socialsurvey.stream.repositories;

import com.realtech.socialsurvey.stream.entities.FailedEmailMessage;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Created by nishit on 04/01/18.
 */
@Repository
public interface FailedEmailMessageRepository extends MongoRepository<FailedEmailMessage, String>
{
	
    List<FailedEmailMessage> findByMessageType(String messageType, Pageable pageable);
    
    List<FailedEmailMessage> findByMessageTypeAndPermanentFailure(String messageType, boolean permanentFailure, Pageable pageable);
    
    FailedEmailMessage findById(ObjectId objectId);
    
    List<FailedEmailMessage> findByDataCompanyId(long companyId, Pageable pageable);
    
    List<FailedEmailMessage> findByDataRecipients(String recipient, Pageable pageable);
     
}
