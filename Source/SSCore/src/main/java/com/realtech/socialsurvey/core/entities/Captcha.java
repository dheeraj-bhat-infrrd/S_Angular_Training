package com.realtech.socialsurvey.core.entities;

/**
 * Holds the captcha details
 */
public class Captcha {

	private String captchaId;
	private String captchaQuestion;
	private String captchaAnswer;

	public String getCaptchaId() {
		return captchaId;
	}

	public void setCaptchaId(String captchaId) {
		this.captchaId = captchaId;
	}

	public String getCaptchaQuestion() {
		return captchaQuestion;
	}

	public void setCaptchaQuestion(String captchaQuestion) {
		this.captchaQuestion = captchaQuestion;
	}

	public String getCaptchaAnswer() {
		return captchaAnswer;
	}

	public void setCaptchaAnswer(String captchaAnswer) {
		this.captchaAnswer = captchaAnswer;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		Captcha captchaObj = (Captcha) obj;
		if (captchaObj.captchaId != null && captchaObj.captchaQuestion != null && captchaObj.captchaAnswer != null
				&& captchaObj.captchaId.equals(captchaId) && captchaObj.captchaQuestion.equals(captchaQuestion)
				&& captchaObj.captchaAnswer.equals(captchaAnswer)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	@Override
	public int hashCode(){
		return (captchaId.hashCode() + captchaQuestion.hashCode() + captchaAnswer.hashCode());
	}
	
	@Override
	public String toString(){
		return ("Captcha Id: "+captchaId+"\t Captch Question: "+captchaQuestion+"\t Captcha Answer: "+captchaAnswer);
	}

}
