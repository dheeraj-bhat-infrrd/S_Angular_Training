package com.realtech.socialsurvey.core.services.support;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.realtech.socialsurvey.core.entities.EmailAttachment;
import com.realtech.socialsurvey.core.exception.NonFatalException;

@Component
public interface UserSupportService {

	public void sendHelpMailToAdmin( String  senderEmail , String senderName , String mailSubject , String MailText , List<EmailAttachment> attachments) throws NonFatalException;

	List<EmailAttachment> saveAttachmentLocally(
			List<MultipartFile> attachmentsList) throws NonFatalException;
	
}
