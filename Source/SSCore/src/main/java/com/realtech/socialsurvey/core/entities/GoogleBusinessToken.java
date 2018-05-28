package com.realtech.socialsurvey.core.entities;

/**
 * Created by ghanashyam on 11/7/16.
 */
public class GoogleBusinessToken
{
	private String googleBusinessLink;


	public String getGoogleBusinessLink()
	{
		return googleBusinessLink;
	}


	public void setGoogleBusinessLink( String googleBusinessLink )
	{
		this.googleBusinessLink = googleBusinessLink;
	}


	@Override public String toString()
	{
		return "GoogleBusinessToken{" + "googleBusinessLink='" + googleBusinessLink + '\'' + '}';
	}
}
