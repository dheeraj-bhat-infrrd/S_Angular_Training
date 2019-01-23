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
import com.realtech.socialsurvey.stream.entities.FailedSurveyProcessor;
import com.realtech.socialsurvey.stream.repositories.FailedSurveyProcessorRepository;
import com.realtech.socialsurvey.stream.services.FailedSurveyProcessorService;

@Service
public class FailedSurveyProcessorServiceImpl implements FailedSurveyProcessorService {

	private static final Logger LOG = LoggerFactory.getLogger(FailedSurveyProcessorServiceImpl.class);

	private static final int NUMBER_OF_RECORDS = 10;
	private KafkaTemplate<String, String> kafkaSurveyProcessorTemplate;
	private FailedSurveyProcessorRepository failedSurveyProcessorRepository;

	@Autowired
	@Qualifier("kafkaSurveyProcessorTemplate")
	public void setkafkaSurveyProcessorTemplate(KafkaTemplate<String, String> kafkaSurveyProcessorTemplate) {
		this.kafkaSurveyProcessorTemplate = kafkaSurveyProcessorTemplate;
	}

	@Autowired
	public void setFailedSurveyProcessorRepository(FailedSurveyProcessorRepository failedSurveyProcessorRepository) {
		this.failedSurveyProcessorRepository = failedSurveyProcessorRepository;
	}

	@Override
	public ResponseEntity<?> queueFailedSurveyProcessor() throws InterruptedException, ExecutionException, TimeoutException{
	 LOG.info( "Initiated queuing failed survey processors onto kafka." );
	 getFailedSurveyProcessorsAndQueue();
	 return new ResponseEntity<>( HttpStatus.CREATED );
	}

	private void getFailedSurveyProcessorsAndQueue() throws InterruptedException, ExecutionException, TimeoutException {
		LOG.info("Fetching failed survey processors from mongo.");
		long totalDocs = failedSurveyProcessorRepository.countByMessageType(FailedMessageConstants.SURVEY_PROCESSOR_MESSAGE);
		int pageNum = 0;
		if (totalDocs != 0) {
			List<FailedSurveyProcessor> failedSurveyProcessors = null;
			Pageable numberOfRecords = new PageRequest(pageNum, NUMBER_OF_RECORDS);
			for (int i = 0; i < totalDocs; i = i + NUMBER_OF_RECORDS) {
				failedSurveyProcessors = failedSurveyProcessorRepository
						.findByMessageType(FailedMessageConstants.SURVEY_PROCESSOR_MESSAGE, numberOfRecords);
				for (FailedSurveyProcessor failedSurveyProcessor : failedSurveyProcessors) {
					if (failedSurveyProcessor.getData() != null && !failedSurveyProcessor.isRetried()) {
						failedSurveyProcessor.setRetried(true);
						failedSurveyProcessor.getData().setId(failedSurveyProcessor.getPostId() + "_"
								+ failedSurveyProcessor.getData().getCompanyId());
						LOG.trace("failed social post: {}", failedSurveyProcessor.getData());
						kafkaSurveyProcessorTemplate.send(new GenericMessage<>(failedSurveyProcessor.getData())).get(60,
								TimeUnit.SECONDS);
					}
				}
				failedSurveyProcessorRepository.save(failedSurveyProcessors);
			}
		} else {
			LOG.info("No failed social posts found.");
		}
	}

}
