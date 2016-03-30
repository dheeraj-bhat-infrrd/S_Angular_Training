package com.realtech.socialsurvey.core.entities;

import java.util.List;


/**
 * User upload validation object. Holds the snapshot of the uploaded file 
 *
 */
public class UploadValidation
{
    private int numberOfRegionsAdded;
    private int numberOfRegionsModified;
    private int numberOfRegionsDeleted;
    private int numberOfBranchesAdded;
    private int numberOfBranchesModified;
    private int numberOfBranchesDeleted;
    private int numberOfUsersAdded;
    private int numberOfUsersModified;
    private int numberOfUsersDeleted;

    private List<String> regionValidationErrors;
    private List<String> branchValidationErrors;
    private List<String> userValidationErrors;

    private List<String> regionValidationWarnings;
    private List<String> branchValidationWarnings;
    private List<String> userValidationWarnings;

    private boolean isRegionHeadersInvalid;
    private boolean isBranchHeadersInvalid;
    private boolean isUserHeadersInvalid;

    private HierarchyUpload upload;


    public boolean isRegionHeadersInvalid()
    {
        return isRegionHeadersInvalid;
    }


    public void setRegionHeadersInvalid( boolean isRegionHeadersInvalid )
    {
        this.isRegionHeadersInvalid = isRegionHeadersInvalid;
    }


    public boolean isBranchHeadersInvalid()
    {
        return isBranchHeadersInvalid;
    }


    public void setBranchHeadersInvalid( boolean isBranchHeadersInvalid )
    {
        this.isBranchHeadersInvalid = isBranchHeadersInvalid;
    }


    public boolean isUserHeadersInvalid()
    {
        return isUserHeadersInvalid;
    }


    public void setUserHeadersInvalid( boolean isUserHeadersInvalid )
    {
        this.isUserHeadersInvalid = isUserHeadersInvalid;
    }


    public int getNumberOfRegionsAdded()
    {
        return numberOfRegionsAdded;
    }


    public void setNumberOfRegionsAdded( int numberOfRegionsAdded )
    {
        this.numberOfRegionsAdded = numberOfRegionsAdded;
    }


    public int getNumberOfRegionsModified()
    {
        return numberOfRegionsModified;
    }


    public void setNumberOfRegionsModified( int numberOfRegionsModified )
    {
        this.numberOfRegionsModified = numberOfRegionsModified;
    }


    public int getNumberOfRegionsDeleted()
    {
        return numberOfRegionsDeleted;
    }


    public void setNumberOfRegionsDeleted( int numberOfRegionsDeleted )
    {
        this.numberOfRegionsDeleted = numberOfRegionsDeleted;
    }


    public int getNumberOfBranchesAdded()
    {
        return numberOfBranchesAdded;
    }


    public void setNumberOfBranchesAdded( int numberOfBranchesAdded )
    {
        this.numberOfBranchesAdded = numberOfBranchesAdded;
    }


    public int getNumberOfBranchesModified()
    {
        return numberOfBranchesModified;
    }


    public void setNumberOfBranchesModified( int numberOfBranchesModified )
    {
        this.numberOfBranchesModified = numberOfBranchesModified;
    }


    public int getNumberOfBranchesDeleted()
    {
        return numberOfBranchesDeleted;
    }


    public void setNumberOfBranchesDeleted( int numberOfBranchesDeleted )
    {
        this.numberOfBranchesDeleted = numberOfBranchesDeleted;
    }


    public int getNumberOfUsersAdded()
    {
        return numberOfUsersAdded;
    }


    public void setNumberOfUsersAdded( int numberOfUsersAdded )
    {
        this.numberOfUsersAdded = numberOfUsersAdded;
    }


    public int getNumberOfUsersModified()
    {
        return numberOfUsersModified;
    }


    public int getNumberOfUsersDeleted()
    {
        return numberOfUsersDeleted;
    }


    public void setNumberOfUsersDeleted( int numberOfUsersDeleted )
    {
        this.numberOfUsersDeleted = numberOfUsersDeleted;
    }


    public void setNumberOfUsersModified( int numberOfUsersModified )
    {
        this.numberOfUsersModified = numberOfUsersModified;
    }


    public List<String> getRegionValidationErrors()
    {
        return regionValidationErrors;
    }


    public void setRegionValidationErrors( List<String> regionValidationErrors )
    {
        this.regionValidationErrors = regionValidationErrors;
    }


    public List<String> getBranchValidationErrors()
    {
        return branchValidationErrors;
    }


    public void setBranchValidationErrors( List<String> branchValidationErrors )
    {
        this.branchValidationErrors = branchValidationErrors;
    }


    public List<String> getUserValidationErrors()
    {
        return userValidationErrors;
    }


    public void setUserValidationErrors( List<String> userValidationErrors )
    {
        this.userValidationErrors = userValidationErrors;
    }


    public List<String> getRegionValidationWarnings()
    {
        return regionValidationWarnings;
    }


    public void setRegionValidationWarnings( List<String> regionValidationWarnings )
    {
        this.regionValidationWarnings = regionValidationWarnings;
    }


    public List<String> getBranchValidationWarnings()
    {
        return branchValidationWarnings;
    }


    public void setBranchValidationWarnings( List<String> branchValidationWarnings )
    {
        this.branchValidationWarnings = branchValidationWarnings;
    }


    public List<String> getUserValidationWarnings()
    {
        return userValidationWarnings;
    }


    public void setUserValidationWarnings( List<String> userValidationWarnings )
    {
        this.userValidationWarnings = userValidationWarnings;
    }


    public HierarchyUpload getUpload()
    {
        return upload;
    }


    public void setUpload( HierarchyUpload upload )
    {
        this.upload = upload;
    }


}
