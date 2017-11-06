package com.realtech.socialsurvey.core.entities;

import java.util.Map;

import com.google.common.collect.BiMap;


public class HierarchyUploadAggregate
{
    private Company company;

    // old and new entity value object lists
    private Map<Long, RegionUploadVO> oldRegionUploadVOMap;
    private Map<Long, RegionUploadVO> newRegionUploadVOMap;

    private Map<Long, BranchUploadVO> oldBranchUploadVOMap;
    private Map<Long, BranchUploadVO> newBranchUploadVOMap;

    private Map<Long, UserUploadVO> oldUserUploadVOMap;
    private Map<Long, UserUploadVO> newUserUploadVOMap;

    // old and new source ~ internal ID mappings
    private BiMap<String, Long> oldRegionSourceMapping;
    private BiMap<String, Long> newRegionSourceMapping;

    private BiMap<String, Long> oldBranchSourceMapping;
    private BiMap<String, Long> newBranchSourceMapping;

    private BiMap<String, Long> oldUserSourceMapping;
    private BiMap<String, Long> newUserSourceMapping;

    // updated hierarchy upload
    private HierarchyUpload hierarchyUpload;

    // sourceId based uploadVO map to be used by hierarchy upload process
    private Map<String, RegionUploadVO> regionUploadVOMap;
    private Map<String, BranchUploadVO> branchUploadVOMap;
    private Map<String, UserUploadVO> userUploadVOMap;

    // sourceId based name map to be used by hierarchy upload process
    private Map<String, String> regionNameMap;
    private Map<String, String> branchNameMap;


    public HierarchyUpload getHierarchyUpload()
    {
        return hierarchyUpload;
    }


    public void setHierarchyUpload( HierarchyUpload hierarchyUpload )
    {
        this.hierarchyUpload = hierarchyUpload;
    }


    public Company getCompany()
    {
        return company;
    }


    public void setCompany( Company company )
    {
        this.company = company;
    }


    public Map<Long, RegionUploadVO> getOldRegionUploadVOMap()
    {
        return oldRegionUploadVOMap;
    }


    public void setOldRegionUploadVOMap( Map<Long, RegionUploadVO> oldRegionUploadVOMap )
    {
        this.oldRegionUploadVOMap = oldRegionUploadVOMap;
    }


    public Map<Long, RegionUploadVO> getNewRegionUploadVOMap()
    {
        return newRegionUploadVOMap;
    }


    public void setNewRegionUploadVOMap( Map<Long, RegionUploadVO> newRegionUploadVOMap )
    {
        this.newRegionUploadVOMap = newRegionUploadVOMap;
    }


    public Map<Long, BranchUploadVO> getOldBranchUploadVOMap()
    {
        return oldBranchUploadVOMap;
    }


    public void setOldBranchUploadVOMap( Map<Long, BranchUploadVO> oldBranchUploadVOMap )
    {
        this.oldBranchUploadVOMap = oldBranchUploadVOMap;
    }


    public Map<Long, BranchUploadVO> getNewBranchUploadVOMap()
    {
        return newBranchUploadVOMap;
    }


    public void setNewBranchUploadVOMap( Map<Long, BranchUploadVO> newBranchUploadVOMap )
    {
        this.newBranchUploadVOMap = newBranchUploadVOMap;
    }


    public Map<Long, UserUploadVO> getOldUserUploadVOMap()
    {
        return oldUserUploadVOMap;
    }


    public void setOldUserUploadVOMap( Map<Long, UserUploadVO> oldUserUploadVOMap )
    {
        this.oldUserUploadVOMap = oldUserUploadVOMap;
    }


    public Map<Long, UserUploadVO> getNewUserUploadVOMap()
    {
        return newUserUploadVOMap;
    }


    public void setNewUserUploadVOMap( Map<Long, UserUploadVO> newUserUploadVOMap )
    {
        this.newUserUploadVOMap = newUserUploadVOMap;
    }


    public Map<String, String> getRegionNameMap()
    {
        return regionNameMap;
    }


    public void setRegionNameMap( Map<String, String> regionNameMap )
    {
        this.regionNameMap = regionNameMap;
    }


    public Map<String, String> getBranchNameMap()
    {
        return branchNameMap;
    }


    public void setBranchNameMap( Map<String, String> branchNameMap )
    {
        this.branchNameMap = branchNameMap;
    }


    public Map<String, RegionUploadVO> getRegionUploadVOMap()
    {
        return regionUploadVOMap;
    }


    public void setRegionUploadVOMap( Map<String, RegionUploadVO> regionUploadVOMap )
    {
        this.regionUploadVOMap = regionUploadVOMap;
    }


    public Map<String, BranchUploadVO> getBranchUploadVOMap()
    {
        return branchUploadVOMap;
    }


    public void setBranchUploadVOMap( Map<String, BranchUploadVO> branchUploadVOMap )
    {
        this.branchUploadVOMap = branchUploadVOMap;
    }


    public Map<String, UserUploadVO> getUserUploadVOMap()
    {
        return userUploadVOMap;
    }


    public void setUserUploadVOMap( Map<String, UserUploadVO> userUploadVOMap )
    {
        this.userUploadVOMap = userUploadVOMap;
    }


    public BiMap<String, Long> getOldRegionSourceMapping()
    {
        return oldRegionSourceMapping;
    }


    public void setOldRegionSourceMapping( BiMap<String, Long> oldRegionSourceMapping )
    {
        this.oldRegionSourceMapping = oldRegionSourceMapping;
    }


    public BiMap<String, Long> getNewRegionSourceMapping()
    {
        return newRegionSourceMapping;
    }


    public void setNewRegionSourceMapping( BiMap<String, Long> newRegionSourceMapping )
    {
        this.newRegionSourceMapping = newRegionSourceMapping;
    }


    public BiMap<String, Long> getOldBranchSourceMapping()
    {
        return oldBranchSourceMapping;
    }


    public void setOldBranchSourceMapping( BiMap<String, Long> oldBranchSourceMapping )
    {
        this.oldBranchSourceMapping = oldBranchSourceMapping;
    }


    public BiMap<String, Long> getNewBranchSourceMapping()
    {
        return newBranchSourceMapping;
    }


    public void setNewBranchSourceMapping( BiMap<String, Long> newBranchSourceMapping )
    {
        this.newBranchSourceMapping = newBranchSourceMapping;
    }


    public BiMap<String, Long> getOldUserSourceMapping()
    {
        return oldUserSourceMapping;
    }


    public void setOldUserSourceMapping( BiMap<String, Long> oldUserSourceMapping )
    {
        this.oldUserSourceMapping = oldUserSourceMapping;
    }


    public BiMap<String, Long> getNewUserSourceMapping()
    {
        return newUserSourceMapping;
    }


    public void setNewUserSourceMapping( BiMap<String, Long> newUserSourceMapping )
    {
        this.newUserSourceMapping = newUserSourceMapping;
    }


}
