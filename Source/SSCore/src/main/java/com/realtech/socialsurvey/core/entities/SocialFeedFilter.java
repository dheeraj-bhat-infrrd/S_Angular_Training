package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.util.List;


/**
 * @author manish
 *
 */
public class SocialFeedFilter implements Serializable
{
    private static final long serialVersionUID = 1L;

    private int startIndex;
    private int limit;
    private String status;
    private boolean flag;
    private List<String> feedtype;
    private Long companyId;
    private List<Long> regionIds;
    private List<Long> branchIds;
    private List<Long> agentIds;
    private String searchText;
    private boolean companySet;


    public int getStartIndex()
    {
        return startIndex;
    }


    public void setStartIndex( int startIndex )
    {
        this.startIndex = startIndex;
    }


    public int getLimit()
    {
        return limit;
    }


    public void setLimit( int limit )
    {
        this.limit = limit;
    }


    public String getStatus()
    {
        return status;
    }


    public void setStatus( String status )
    {
        this.status = status;
    }


    public boolean isFlag()
    {
        return flag;
    }


    public void setFlag( boolean flag )
    {
        this.flag = flag;
    }


    public List<String> getFeedtype()
    {
        return feedtype;
    }


    public void setFeedtype( List<String> feedtype )
    {
        this.feedtype = feedtype;
    }


    public Long getCompanyId()
    {
        return companyId;
    }


    public void setCompanyId( Long companyId )
    {
        this.companyId = companyId;
    }


    public List<Long> getRegionIds()
    {
        return regionIds;
    }


    public void setRegionIds( List<Long> regionIds )
    {
        this.regionIds = regionIds;
    }


    public List<Long> getBranchIds()
    {
        return branchIds;
    }


    public void setBranchIds( List<Long> branchIds )
    {
        this.branchIds = branchIds;
    }


    public List<Long> getAgentIds()
    {
        return agentIds;
    }


    public void setAgentIds( List<Long> agentIds )
    {
        this.agentIds = agentIds;
    }


    public String getSearchText()
    {
        return searchText;
    }


    public void setSearchText( String searchText )
    {
        this.searchText = searchText;
    }


    public boolean isCompanySet()
    {
        return companySet;
    }


    public void setCompanySet( boolean companySet )
    {
        this.companySet = companySet;
    }

}
