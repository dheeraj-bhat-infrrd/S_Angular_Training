package com.realtech.socialsurvey.stream.services;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface OptInOptOutContactService {

	/**
	 * Method will process incoming message to unsubscribe or resubscribe contact number
	 * @param contactNumber
	 * @param messageBody
	 * @throws TimeoutException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public void processIncomingMessage( String contactNumber, String messageBody ) throws InterruptedException, ExecutionException, TimeoutException;
}
