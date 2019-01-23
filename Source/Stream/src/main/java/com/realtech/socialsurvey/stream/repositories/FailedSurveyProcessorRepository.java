package com.realtech.socialsurvey.stream.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.realtech.socialsurvey.stream.entities.FailedSurveyProcessor;

public interface FailedSurveyProcessorRepository extends MongoRepository<FailedSurveyProcessor, String> {

	List<FailedSurveyProcessor> findByMessageType(String messageType, Pageable pageable);

	long countByMessageType(String messageType);

	void delete(FailedSurveyProcessor failedSurveyProcessor);
	
	FailedSurveyProcessor save(FailedSurveyProcessor failedSurveyProcessor);
}
