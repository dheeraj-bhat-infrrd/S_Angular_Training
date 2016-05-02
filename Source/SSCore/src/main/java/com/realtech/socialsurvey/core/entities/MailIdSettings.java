package com.realtech.socialsurvey.core.entities;

import java.util.List;

/**
 * Holds the mail ids for a profile
 */
public class MailIdSettings {

	private String work;
	private String workEmailToVerify;
	private boolean isWorkEmailVerified = true;
	private boolean isWorkMailVerifiedByAdmin;
	private String personal;
	private String personalEmailToVerify;
	private boolean isPersonalEmailVerified = true;
	private List<MiscValues> others;

	// Need to implement verification for others
	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public String getWorkEmailToVerify() {
		return workEmailToVerify;
	}

	public void setWorkEmailToVerify(String workEmailToVerify) {
		this.workEmailToVerify = workEmailToVerify;
	}

	public boolean getIsWorkEmailVerified() {
		return isWorkEmailVerified;
	}

	public void setWorkEmailVerified(boolean isWorkEmailVerified) {
		this.isWorkEmailVerified = isWorkEmailVerified;
	}

	public String getPersonal() {
		return personal;
	}

	public void setPersonal(String personal) {
		this.personal = personal;
	}

	public String getPersonalEmailToVerify() {
		return personalEmailToVerify;
	}

	public void setPersonalEmailToVerify(String personalEmailToVerify) {
		this.personalEmailToVerify = personalEmailToVerify;
	}

	public boolean getIsPersonalEmailVerified() {
		return isPersonalEmailVerified;
	}

	public void setPersonalEmailVerified(boolean isPersonalEmailVerified) {
		this.isPersonalEmailVerified = isPersonalEmailVerified;
	}

	public boolean getIsWorkMailVerifiedByAdmin()
    {
        return isWorkMailVerifiedByAdmin;
    }

    public void setWorkMailVerifiedByAdmin( boolean isWorkMailVerifiedByAdmin )
    {
        this.isWorkMailVerifiedByAdmin = isWorkMailVerifiedByAdmin;
    }

    public List<MiscValues> getOthers() {
		return others;
	}

	public void setOthers(List<MiscValues> others) {
		this.others = others;
	}

    

    @Override
	public String toString() {
		return "work: " + work + "\t isWorkEmailVerified: " + isWorkEmailVerified + "\t personal: " + personal + "\t isPersonalEmailVerified: "
				+ isPersonalEmailVerified + "\t others: " + (others != null ? others.toString() : "null");
	}
}