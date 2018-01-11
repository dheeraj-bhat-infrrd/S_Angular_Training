package com.realtech.socialsurvey.core.services.support.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.realtech.socialsurvey.core.entities.EmailAttachment;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.support.UserSupportService;
import com.realtech.socialsurvey.core.services.upload.FileUploadService;

@Component
public class UserSupportServiceImpl implements UserSupportService {
	
	@Value("${FILE_DIRECTORY_LOCATION}")
	private String fileDirectoryLocation;
	
	@Value ( "${APPLICATION_SUPPORT_EMAIL}")
    private String applicationSupportEmail;

    @Value ( "${APPLICATION_ADMIN_NAME}")
    private String applicationAdminName;

	@Autowired
	private EmailServices emailServices;
    
    @Autowired
    private FileUploadService fileUploadService;
	
	private static Logger LOG = LoggerFactory.getLogger(UserSupportServiceImpl.class);
	
	/***
	 * 
	 */
	@Override
	public void sendHelpMailToAdmin( String  senderEmail , String senderName , String mailSubject , String MailText , List<EmailAttachment> attachments) throws NonFatalException{
		
		LOG.info("Method sendHelpMailToAdmin started.");
		if(senderEmail == null || senderEmail.isEmpty()){
		    throw new InvalidInputException("Sender Email Address is not valid");
		}
		
		if(senderName == null || senderName.isEmpty()){
            throw new InvalidInputException("Sender Name is not valid");
        }
		
		try {
			emailServices.sendHelpMailToAdmin( senderEmail , senderName ,applicationAdminName, mailSubject, MailText, applicationSupportEmail, attachments );
		} catch (InvalidInputException | UndeliveredEmailException e) {
			// TODO Auto-generated catch block
			LOG.info("Exception caught : " + e.getMessage());
			throw new NonFatalException();
		}
		
		LOG.info("Method sendHelpMailToAdmin ended.");
	}
	
	/****
	 * 
	 * @param attachmentsList
	 * @return
	 * @throws NonFatalException
	 */
	@Override
	public List<EmailAttachment> saveAttachmentLocally(List<MultipartFile> attachmentsList) throws NonFatalException{
		LOG.info("Method saveAttachmentLocally started.");
		List<EmailAttachment> attachments = new ArrayList<EmailAttachment>();
        
		String filePath = null;
		String fileName = null;
		if(attachmentsList !=null){
			for(MultipartFile attachment : attachmentsList){
				if( attachment != null){
					try {
						filePath = saveFileAtDirectoryLocation(attachment);
						fileName = attachment.getOriginalFilename();
						attachments.add( new EmailAttachment(fileName, filePath) );
					} catch (IOException e) {
						throw new NonFatalException("Exception while saving attachment", e);
					}
				}
			}
		}
		LOG.info("Method saveAttachmentLocally ended.");
		return attachments;
	}
	
	private String saveFileAtDirectoryLocation(MultipartFile oldFile) throws IOException, NonFatalException{
		String originalFileName = oldFile.getOriginalFilename();
		File file = new File( fileDirectoryLocation + File.separator + originalFileName);
		OutputStream outputStream = null;
		InputStream inputStream = null;
		String filePath;
		try{
			
			inputStream = oldFile.getInputStream();
			outputStream = new FileOutputStream(file);

			int chunk;
			while ((chunk = inputStream.read()) != -1) {
	        	outputStream.write(chunk);
	        }
			
			filePath = fileUploadService.uploadOldReport( file, originalFileName );
	        
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
		
		return filePath;
	}
}
