package com.realtech.socialsurvey.core.vo;

import java.io.Serializable;
import java.util.List;


public class ManageTeamBulkRequest implements Serializable
{
    private static final long serialVersionUID = 1L;

    private List<Long> userIds;
    private long adminId;

    private List<String> userEmailIds;
    private String adminEmailId;

    private long branchId;

    private long regionId;

    //input required for uploading profile picture
    private int selectedX;
    private int selectedY;
    private int selectedW;
    private int selectedH;
    private int resizeWidth;
    private int resizeHeight;
    private String imageBase64;
    private String imageFileName;
    private double minimumSocialPostScore;

    String logoFileName;

    public List<Long> getUserIds()
    {
        return userIds;
    }


    public void setUserIds( List<Long> userIds )
    {
        this.userIds = userIds;
    }


    public long getAdminId()
    {
        return adminId;
    }


    public void setAdminId( long adminId )
    {
        this.adminId = adminId;
    }


    public List<String> getUserEmailIds()
    {
        return userEmailIds;
    }


    public void setUserEmailIds( List<String> userEmailIds )
    {
        this.userEmailIds = userEmailIds;
    }


    public String getAdminEmailId()
    {
        return adminEmailId;
    }


    public void setAdminEmailId( String adminEmailId )
    {
        this.adminEmailId = adminEmailId;
    }


    public long getBranchId()
    {
        return branchId;
    }


    public void setBranchId( long branchId )
    {
        this.branchId = branchId;
    }


    public long getRegionId()
    {
        return regionId;
    }


    public void setRegionId( long regionId )
    {
        this.regionId = regionId;
    }


    public int getSelectedX()
    {
        return selectedX;
    }


    public int getSelectedY()
    {
        return selectedY;
    }


    public int getSelectedW()
    {
        return selectedW;
    }


    public int getSelectedH()
    {
        return selectedH;
    }


    public int getResizeWidth()
    {
        return resizeWidth;
    }


    public int getResizeHeight()
    {
        return resizeHeight;
    }


    public String getImageBase64()
    {
        return imageBase64;
    }


    public String getImageFileName()
    {
        return imageFileName;
    }


    public double getMinimumSocialPostScore()
    {
        return minimumSocialPostScore;
    }


    public void setMinimumSocialPostScore( double minimumSocialPostScore )
    {
        this.minimumSocialPostScore = minimumSocialPostScore;
    }


    public String getLogoFileName()
    {
        return logoFileName;
    }


    public void setLogoFileName( String logoFileName )
    {
        this.logoFileName = logoFileName;
    }
}
