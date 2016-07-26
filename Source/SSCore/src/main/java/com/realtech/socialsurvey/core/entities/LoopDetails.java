package com.realtech.socialsurvey.core.entities;

import java.util.Map;


public class LoopDetails
{
	private String loopId;
	private Map<String, Map<String, String>> sections;


	public String getLoopId()
	{
		return loopId;
	}


	public void setLoopId( String loopId )
	{
		this.loopId = loopId;
	}


	public Map<String, Map<String, String>> getSections()
	{
		return sections;
	}


	public void setSections( Map<String, Map<String, String>> sections )
	{
		this.sections = sections;
	}
}
