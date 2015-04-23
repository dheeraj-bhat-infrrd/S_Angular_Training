package com.realtech.socialsurvey.core.starter;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.annotation.Transactional;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserAssignmentException;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.services.upload.CsvUploadService;

@Transactional
public class CsvUpload {
	
	public static final Logger LOG = LoggerFactory.getLogger(CsvUpload.class);
	
	public static void main(String[] args){
		
		LOG.info("Starting the csv uploader");
		LOG.debug("Loading the application context");
		ApplicationContext context = new ClassPathXmlApplicationContext("ss-starter-config.xml");
				
		CsvUploadService csvUploadService = context.getBean(CsvUploadService.class);
			
		User adminUser = csvUploadService.getUser(18153l);
		adminUser.setCompanyAdmin(true);
		
		List<String> errorList = null;
			
			
		Map<String, List<Object>> uploadObjects = csvUploadService.parseCsv("/Users/nishit/work/Social_Survey/testhierarchy.txt");
		try {
			errorList = csvUploadService.createAndReturnErrors(uploadObjects, adminUser);
			
			for(String error : errorList){
				LOG.info(error);
			}
			csvUploadService.postProcess(adminUser);
		}
		catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (NoRecordsFetchedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SolrException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (UserAssignmentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
