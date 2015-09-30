package com.realtech.socialsurvey.core.services.support;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.NonFatalException;

@Component
public interface UserSupportService {

	public void sendHelpMailToAdmin(User user , String mailSubject , String MailText , Map<String , String > attachmentsDetails) throws NonFatalException;

	Map<String, String> saveAttachmentLocally(
			List<MultipartFile> attachmentsList) throws NonFatalException;
	
}
