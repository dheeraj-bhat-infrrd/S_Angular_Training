package com.realtech.socialsurvey.core.entities;




/**
 * @author manish
 * 
 * Class for getting profileImageUrl from mongo DB.
 *  
 */
public class ProfileImageUrlEntity {

	private long iden;
	private String profileImageUrl;

	public long getIden() {
		return iden;
	}

	public void setIden(long iden) {
		this.iden = iden;
	}

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
