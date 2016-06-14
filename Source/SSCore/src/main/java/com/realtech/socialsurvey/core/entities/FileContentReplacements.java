package com.realtech.socialsurvey.core.entities;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Table;

// JIRA: SS-7: By RM02: BOC
/**
 * Class with attributes that contain a file name and arguments to be replaced in that file
 */
//@Entity
//@Table(name = "FILE_CONTENT_REPLACEMENTS")
public class FileContentReplacements {

	private String fileName;
	private List<String> replacementArgs;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public List<String> getReplacementArgs() {
		return replacementArgs;
	}

	public void setReplacementArgs(List<String> replacementArgs) {
		this.replacementArgs = replacementArgs;
	}
}
// JIRA: SS-7: By RM02: BOC