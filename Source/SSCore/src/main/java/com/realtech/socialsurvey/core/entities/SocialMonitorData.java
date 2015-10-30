package com.realtech.socialsurvey.core.entities;

import java.util.List;


/**
 * An entity used to hold social posts as well as the total count of the social posts for the social monitor page 
 *
 */
public class SocialMonitorData
{
    private List<SocialMonitorPost> socialMonitorPosts;
    private long count;
    private List<ProfileImageUrlData> profileImageUrlDataList;


    public List<SocialMonitorPost> getSocialMonitorPosts()
    {
        return socialMonitorPosts;
    }


    public void setSocialMonitorPosts( List<SocialMonitorPost> socialMonitorPosts )
    {
        this.socialMonitorPosts = socialMonitorPosts;
    }


    public long getCount()
    {
        return count;
    }


    public void setCount( long count )
    {
        this.count = count;
    }


    public List<ProfileImageUrlData> getProfileImageUrlDataList()
    {
        return profileImageUrlDataList;
    }


    public void setProfileImageUrlDataList( List<ProfileImageUrlData> profileImageUrlDataList )
    {
        this.profileImageUrlDataList = profileImageUrlDataList;
    }
}
