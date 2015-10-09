package com.realtech.socialsurvey.core.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.entities.FileContentReplacements;
import com.realtech.socialsurvey.core.exception.FatalException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

// JIRA: SS-7: By RM02: BOC
/**
 * Utility class to perform file operations
 */
@Component
public final class FileOperations {

	private static final Logger LOG = LoggerFactory.getLogger(FileOperations.class);

	/**
	 * Method to read content from a file
	 * 
	 * @param fileName
	 * @return
	 * @throws InvalidInputException
	 */
	public String getContentFromFile(String fileName) throws InvalidInputException {
		if (fileName == null || fileName.isEmpty()) {
			LOG.error("filename is null or empty while getting content from file");
			throw new InvalidInputException("File name is null or empty while getting contents from file");
		}
		LOG.debug("Getting content from file : " + fileName);
		StringBuilder fileContentSb = new StringBuilder();
		String strLine = "";
		String content = null;
		try {
			InputStream inputStream = FileOperations.class.getClassLoader().getResourceAsStream(fileName);
			if (inputStream != null) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
				while (strLine != null) {
					strLine = reader.readLine();
					if ((strLine != null) && (!strLine.isEmpty())) {
						fileContentSb.append(strLine + "\n");
					}
				}
				reader.close();
			}
			content = fileContentSb.toString();
		}
		catch (IOException e) {
			LOG.error("IOException occured while reading file.Reason : " + e.getMessage(), e);
			throw new FatalException("IOException occured while reading file.Reason : " + e.getMessage(), e);
		}
		LOG.debug("File reading complete.Returning : " + content);
		return content;

	}

	/**
	 * Method replaces the content in file with the replacement arguments provided
	 * 
	 * @param fileContentReplacements
	 * @return
	 * @throws InvalidInputException
	 */
	public String replaceFileContents(FileContentReplacements fileContentReplacements) throws InvalidInputException {
		if (fileContentReplacements == null) {
			throw new InvalidInputException("file and contents to replace are null while replacing file contents");
		}
		LOG.debug("Method replaceFileContents called for : " + fileContentReplacements.getFileName());
		
		String fileContent = getContentFromFile(fileContentReplacements.getFileName());
		if (fileContent == null) {
			throw new InvalidInputException("Content not found for file : " + fileContentReplacements.getFileName());
		}
		
		String replacedContent = fileContent;
		List<String> replacementArgs = fileContentReplacements.getReplacementArgs();
		if (replacementArgs != null && !replacementArgs.isEmpty()) {
			LOG.trace("Replacing the file contents with replacement arguments");
			for (String replacementArg : replacementArgs) {
				replacedContent = replacedContent.replaceFirst("%s", Matcher.quoteReplacement(replacementArg));
			}
		}
		else {
			LOG.debug("No arguments present to be replaced in the file.Returning file content as it is");
		}

		return replacedContent;
	}
}
// JIRA: SS-7: By RM02: EOC