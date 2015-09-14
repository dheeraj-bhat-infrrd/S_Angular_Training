package com.realtech.socialsurvey.core.services.support.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.support.UserSupportService;

@Component
public class UserSupportServiceImpl implements UserSupportService {
	
	@Value("${FILE_DIRECTORY_LOCATION}")
	private String fileDirectoryLocation;
	
	@Value ( "${APPLICATION_ADMIN_EMAIL}")
    private String applicationAdminEmail;

    @Value ( "${APPLICATION_ADMIN_NAME}")
    private String applicationAdminName;

	@Autowired
	private EmailServices emailServices;
	
	private static Logger LOG = LoggerFactory.getLogger(UserSupportServiceImpl.class);
	
	/***
	 * 
	 */
	@Override
	public void sendReportBugMailToAdmin(User user , String mailSubject , String MailText , Map<String , String > attachmentsDetails) throws NonFatalException{
		
		LOG.info("Method sendReportBugMailToAdmin started.");
		try {
			emailServices.sendReportBugMailFromUser(user ,applicationAdminName, mailSubject, MailText, applicationAdminEmail, attachmentsDetails );
		} catch (InvalidInputException | UndeliveredEmailException e) {
			// TODO Auto-generated catch block
			LOG.info("Exception caught : " + e.getMessage());
			throw new NonFatalException();
		}
		
		LOG.info("Method sendReportBugMailToAdmin ended.");
	}
	
	/****
	 * 
	 * @param attachmentsList
	 * @return
	 * @throws NonFatalException
	 */
	@Override
	public Map<String , String > saveAttachmentLocally(List<MultipartFile> attachmentsList) throws NonFatalException{
		LOG.info("Method saveAttachmentLocally started.");
		Map<String , String > attachmentsDetails = new HashMap<String, String>();
		String filePath = null;
		String fileName = null;
		if(attachmentsList !=null){
			for(MultipartFile attachment : attachmentsList){
				if( attachment != null){
					try {
						filePath = saveFileAtDirectoryLocation(attachment);
						fileName = attachment.getOriginalFilename();
						attachmentsDetails.put(fileName, filePath);
					} catch (IOException e) {
						throw new NonFatalException();
					}
				}
			}
		}
		LOG.info("Method saveAttachmentLocally ended.");
		return attachmentsDetails;
	}
	
	private String saveFileAtDirectoryLocation(MultipartFile oldFile) throws IOException{
		String originalFileName = oldFile.getOriginalFilename();
		File file = new File( fileDirectoryLocation + File.separator + originalFileName);
		OutputStream outputStream = null;
		InputStream inputStream = null;
		try{
			
			inputStream = oldFile.getInputStream();
			outputStream = new FileOutputStream(file);

			int chunk;
			while ((chunk = inputStream.read()) != -1) {
	        	outputStream.write(chunk);
	        }
		}catch(IOException e){
			LOG.error("Exception caught while saving file: " + e.getMessage());
			throw e;
		}finally{
			try {
				inputStream.close();
				outputStream.close();
			} catch (IOException e) {
				LOG.error("Exception caught while closing stream: " + e.getMessage());
				throw e;
			}
		}
		return file.getPath();
	}
}
