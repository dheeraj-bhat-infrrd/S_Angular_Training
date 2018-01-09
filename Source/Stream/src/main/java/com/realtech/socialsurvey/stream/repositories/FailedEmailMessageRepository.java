package com.realtech.socialsurvey.stream.repositories;

import com.realtech.socialsurvey.stream.entities.FailedEmailMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Created by nishit on 04/01/18.
 */
@Repository
public interface FailedEmailMessageRepository extends MongoRepository<FailedEmailMessage, String>
{
    List<FailedEmailMessage> findByMessageTypeAndPermanentFailure(String messageType, boolean permanentFailure);

    List<FailedEmailMessage> findByMessageType(String messageType);
}
