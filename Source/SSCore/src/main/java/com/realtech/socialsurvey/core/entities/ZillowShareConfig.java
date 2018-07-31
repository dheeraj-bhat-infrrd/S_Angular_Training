package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;

public class ZillowShareConfig implements Serializable{

	private boolean isAutoFillReviewContent;
	
	private String subjectContent;
	
	private String reviewFooterContent;

	public boolean isAutoFillReviewContent() {
		return isAutoFillReviewContent;
	}

	public void setAutoFillReviewContent(boolean isAutoFillReviewContent) {
		this.isAutoFillReviewContent = isAutoFillReviewContent;
	}

	public String getSubjectContent() {
		return subjectContent;
	}

	public void setSubjectContent(String subjectContent) {
		this.subjectContent = subjectContent;
	}

	public String getReviewFooterContent() {
		return reviewFooterContent;
	}

	public void setReviewFooterContent(String reviewFooterContent) {
		this.reviewFooterContent = reviewFooterContent;
	}
}
