package com.realtech.socialsurvey.core.starter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.realtech.socialsurvey.core.utils.mongo.PostToWall;

public class PostToSocialSurvey {

	public static final Logger LOG = LoggerFactory.getLogger(PostToSocialSurvey.class);
	
	private static String fileName = "/Users/nishit/work/Social_Survey/emailids.txt";

	public static void main(String[] args) {
		LOG.info("Starting up the PostToSocialSurvey");

		// TODO parse emailIds
		List<String> emailIds = readEmailIdsFromFile(fileName);

		ApplicationContext context = new ClassPathXmlApplicationContext("ss-starter-config.xml");
		PostToWall postToWall = (PostToWall) context.getBean("postToWall");
		postToWall.postStatusForUser(emailIds);

		// Closing the context
		LOG.info("Finished the PostToSocialSurvey");
		((ConfigurableApplicationContext) context).close();
	}
	
	public static List<String> readEmailIdsFromFile(String fileName){
		BufferedReader bfrReader = null;
		FileReader reader = null;
		List<String> emailIds = new ArrayList<>();
		try {
			reader = new FileReader(fileName);
			bfrReader = new BufferedReader(reader);
			String line = null;
			while ((line = bfrReader.readLine()) != null) {
				emailIds.add(line.trim());
			}
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if (bfrReader != null) {
				try {
					bfrReader.close();
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (reader != null) {
				try {
					reader.close();
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return emailIds;
	}
}