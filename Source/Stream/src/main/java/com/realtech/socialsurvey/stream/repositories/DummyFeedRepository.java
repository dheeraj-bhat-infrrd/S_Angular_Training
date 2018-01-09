package com.realtech.socialsurvey.stream.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.realtech.socialsurvey.stream.entities.DummyFeed;

/**
 * Feed repository to store into mongo
 * @author nishit
 *
 */
@Repository
public interface DummyFeedRepository extends MongoRepository<DummyFeed, String>
{
}
