package com.realtech.socialsurvey.core.entities;

public class AgentRankingReport
{
    private String agentName;
    private String agentFirstName;
    private String agentLastName;
    private long agentId;
    private double allTimeAverageScore;
    private double averageScore;
    private long allTimeSentSurveys;
    private long allTimeCompletedSurveys;
    private long allTimeIncompleteSurveys;
    private long sentSurveys;
    private long completedSurveys;
    private long incompleteSurveys;
    
    public String getAgentName()
    {
        return agentName;
    }
    public void setAgentName( String agentName )
    {
        this.agentName = agentName;
    }
    public String getAgentFirstName()
    {
        return agentFirstName;
    }
    public void setAgentFirstName( String agentFirstName )
    {
        this.agentFirstName = agentFirstName;
    }
    public String getAgentLastName()
    {
        return agentLastName;
    }
    public void setAgentLastName( String agentLastName )
    {
        this.agentLastName = agentLastName;
    }
    public long getAgentId()
    {
        return agentId;
    }
    public void setAgentId( long agentId )
    {
        this.agentId = agentId;
    }
    public double getAllTimeAverageScore()
    {
        return allTimeAverageScore;
    }
    public void setAllTimeAverageScore( double allTimeAverageScore )
    {
        this.allTimeAverageScore = allTimeAverageScore;
    }
    public double getAverageScore()
    {
        return averageScore;
    }
    public void setAverageScore( double averageScore )
    {
        this.averageScore = averageScore;
    }
    public long getAllTimeSentSurveys()
    {
        return allTimeSentSurveys;
    }
    public void setAllTimeSentSurveys( long allTimeSentSurveys )
    {
        this.allTimeSentSurveys = allTimeSentSurveys;
    }
    public long getAllTimeCompletedSurveys()
    {
        return allTimeCompletedSurveys;
    }
    public void setAllTimeCompletedSurveys( long allTimeCompletedSurveys )
    {
        this.allTimeCompletedSurveys = allTimeCompletedSurveys;
    }
    public long getAllTimeIncompleteSurveys()
    {
        return allTimeIncompleteSurveys;
    }
    public void setAllTimeIncompleteSurveys( long allTimeIncompleteSurveys )
    {
        this.allTimeIncompleteSurveys = allTimeIncompleteSurveys;
    }
    public long getSentSurveys()
    {
        return sentSurveys;
    }
    public void setSentSurveys( long sentSurveys )
    {
        this.sentSurveys = sentSurveys;
    }
    public long getCompletedSurveys()
    {
        return completedSurveys;
    }
    public void setCompletedSurveys( long completedSurveys )
    {
        this.completedSurveys = completedSurveys;
    }
    public long getIncompleteSurveys()
    {
        return incompleteSurveys;
    }
    public void setIncompleteSurveys( long incompleteSurveys )
    {
        this.incompleteSurveys = incompleteSurveys;
    }
}
