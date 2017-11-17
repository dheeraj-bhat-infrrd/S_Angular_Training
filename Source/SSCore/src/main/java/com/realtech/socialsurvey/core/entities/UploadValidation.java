package com.realtech.socialsurvey.core.entities;

/**
 * User upload validation object. Holds the snapshot of the uploaded file 
 *
 */
public class UploadValidation
{

    // hierarchy data 
    private HierarchyUpload upload;

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
    public HierarchyUpload getUpload()
    {
        return upload;
    }


    public void setUpload( HierarchyUpload upload )
    {
        this.upload = upload;
    }


    public int getNumberOfRegionsAdded()
    {
        return numberOfRegionsAdded;
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


    public boolean hasRegionValidationErrors()
    {
        return hasRegionValidationErrors;
    }


    public void setRegionValidationErrors( boolean hasRegionValidationErrors )
    {
        this.hasRegionValidationErrors = hasRegionValidationErrors;
    }


    public boolean hasBranchValidationErrors()
    {
        return hasBranchValidationErrors;
    }


    public void setBranchValidationErrors( boolean hasBranchValidationErrors )
    {
        this.hasBranchValidationErrors = hasBranchValidationErrors;
    }


    public boolean hasUserValidationErrors()
    {
        return hasUserValidationErrors;
    }


    public void setUserValidationErrors( boolean hasUserValidationErrors )
    {
        this.hasUserValidationErrors = hasUserValidationErrors;
    }


    public boolean hasRegionValidationWarnings()
    {
        return hasRegionValidationWarnings;
    }


    public void setRegionValidationWarnings( boolean hasRegionValidationWarnings )
    {
        this.hasRegionValidationWarnings = hasRegionValidationWarnings;
    }


    public boolean hasBranchValidationWarnings()
    {
        return hasBranchValidationWarnings;
    }


    public void setBranchValidationWarnings( boolean hasBranchValidationWarnings )
    {
        this.hasBranchValidationWarnings = hasBranchValidationWarnings;
    }


    public boolean hasUserValidationWarnings()
    {
        return hasUserValidationWarnings;
    }


    public void setUserValidationWarnings( boolean hasUserValidationWarnings )
    {
        this.hasUserValidationWarnings = hasUserValidationWarnings;
    }

}
