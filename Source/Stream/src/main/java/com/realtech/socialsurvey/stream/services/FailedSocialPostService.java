package com.realtech.socialsurvey.stream.services;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.springframework.http.ResponseEntity;

public interface FailedSocialPostService
{

    ResponseEntity<?> queueFailedSocialPosts() throws InterruptedException, ExecutionException, TimeoutException;

}
