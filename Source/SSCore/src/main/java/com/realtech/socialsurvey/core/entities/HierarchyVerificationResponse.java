package com.realtech.socialsurvey.core.entities;

import java.util.Map;


public class HierarchyVerificationResponse
{
    // reference to the upload data in mongoDB generated for the purpose of verification
    private String hierarchyIntermediateId;

    // hierarchy data
    private Map<String, RegionUploadVO> regions;
    private Map<String, BranchUploadVO> branches;
    private Map<String, UserUploadVO> users;


    // flags used in upload verification: BEGIN
    private int numberOfRegionsAdded;
    private int numberOfBranchesAdded;
    private int numberOfUsersAdded;

    private int numberOfRegionsModified;
    private int numberOfBranchesModified;
    private int numberOfUsersModified;

    private int numberOfBranchesDeleted;
    private int numberOfUsersDeleted;
    private int numberOfRegionsDeleted;

    private boolean hasRegionValidationErrors;
    private boolean hasBranchValidationErrors;
    private boolean hasUserValidationErrors;

    private boolean hasRegionValidationWarnings;
    private boolean hasBranchValidationWarnings;
    private boolean hasUserValidationWarnings;
    // flags used in upload verification: END


    public int getNumberOfRegionsAdded()
    {
        return numberOfRegionsAdded;
    }


    public String getHierarchyIntermediateId()
    {
        return hierarchyIntermediateId;
    }


    public void setHierarchyIntermediateId( String hierarchyIntermediateId )
    {
        this.hierarchyIntermediateId = hierarchyIntermediateId;
    }


    public Map<String, RegionUploadVO> getRegions()
    {
        return regions;
    }


    public void setRegions( Map<String, RegionUploadVO> regions )
    {
        this.regions = regions;
    }


    public Map<String, BranchUploadVO> getBranches()
    {
        return branches;
    }


    public void setBranches( Map<String, BranchUploadVO> branches )
    {
        this.branches = branches;
    }


    public Map<String, UserUploadVO> getUsers()
    {
        return users;
    }


    public void setUsers( Map<String, UserUploadVO> users )
    {
        this.users = users;
    }


    public void setNumberOfRegionsAdded( int numberOfRegionsAdded )
    {
        this.numberOfRegionsAdded = numberOfRegionsAdded;
    }


    public int getNumberOfBranchesAdded()
    {
        return numberOfBranchesAdded;
    }


    public void setNumberOfBranchesAdded( int numberOfBranchesAdded )
    {
        this.numberOfBranchesAdded = numberOfBranchesAdded;
    }


    public int getNumberOfUsersAdded()
    {
        return numberOfUsersAdded;
    }


    public void setNumberOfUsersAdded( int numberOfUsersAdded )
    {
        this.numberOfUsersAdded = numberOfUsersAdded;
    }


    public int getNumberOfRegionsModified()
    {
        return numberOfRegionsModified;
    }


    public void setNumberOfRegionsModified( int numberOfRegionsModified )
    {
        this.numberOfRegionsModified = numberOfRegionsModified;
    }


    public int getNumberOfBranchesModified()
    {
        return numberOfBranchesModified;
    }


    public void setNumberOfBranchesModified( int numberOfBranchesModified )
    {
        this.numberOfBranchesModified = numberOfBranchesModified;
    }


    public int getNumberOfUsersModified()
    {
        return numberOfUsersModified;
    }


    public void setNumberOfUsersModified( int numberOfUsersModified )
    {
        this.numberOfUsersModified = numberOfUsersModified;
    }


    public int getNumberOfBranchesDeleted()
    {
        return numberOfBranchesDeleted;
    }


    public void setNumberOfBranchesDeleted( int numberOfBranchesDeleted )
    {
        this.numberOfBranchesDeleted = numberOfBranchesDeleted;
    }


    public int getNumberOfUsersDeleted()
    {
        return numberOfUsersDeleted;
    }


    public void setNumberOfUsersDeleted( int numberOfUsersDeleted )
    {
        this.numberOfUsersDeleted = numberOfUsersDeleted;
    }


    public int getNumberOfRegionsDeleted()
    {
        return numberOfRegionsDeleted;
    }


    public void setNumberOfRegionsDeleted( int numberOfRegionsDeleted )
    {
        this.numberOfRegionsDeleted = numberOfRegionsDeleted;
    }


    public boolean isHasRegionValidationErrors()
    {
        return hasRegionValidationErrors;
    }


    public void setHasRegionValidationErrors( boolean hasRegionValidationErrors )
    {
        this.hasRegionValidationErrors = hasRegionValidationErrors;
    }


    public boolean isHasBranchValidationErrors()
    {
        return hasBranchValidationErrors;
    }


    public void setHasBranchValidationErrors( boolean hasBranchValidationErrors )
    {
        this.hasBranchValidationErrors = hasBranchValidationErrors;
    }


    public boolean isHasUserValidationErrors()
    {
        return hasUserValidationErrors;
    }


    public void setHasUserValidationErrors( boolean hasUserValidationErrors )
    {
        this.hasUserValidationErrors = hasUserValidationErrors;
    }


    public boolean isHasRegionValidationWarnings()
    {
        return hasRegionValidationWarnings;
    }


    public void setHasRegionValidationWarnings( boolean hasRegionValidationWarnings )
    {
        this.hasRegionValidationWarnings = hasRegionValidationWarnings;
    }


    public boolean isHasBranchValidationWarnings()
    {
        return hasBranchValidationWarnings;
    }


    public void setHasBranchValidationWarnings( boolean hasBranchValidationWarnings )
    {
        this.hasBranchValidationWarnings = hasBranchValidationWarnings;
    }


    public boolean isHasUserValidationWarnings()
    {
        return hasUserValidationWarnings;
    }


    public void setHasUserValidationWarnings( boolean hasUserValidationWarnings )
    {
        this.hasUserValidationWarnings = hasUserValidationWarnings;
    }
}
