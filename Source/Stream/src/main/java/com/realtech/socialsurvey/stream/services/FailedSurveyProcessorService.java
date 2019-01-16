package com.realtech.socialsurvey.stream.services;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.springframework.http.ResponseEntity;

public interface FailedSurveyProcessorService {

	 ResponseEntity<?> queueFailedSurveyProcessor() throws InterruptedException, ExecutionException, TimeoutException;
}
