package com.realtech.socialsurvey.core.entities;

/**
 * User upload validation object. Holds the snapshot of the uploaded file 
 *
 */
public class UploadValidation
{
    private int numberOfRegionsAdded;
    private int numberOfRegionsModified;
    private int numberOfBranchesAdded;
    private int numberOfBranchesModified;
    private int numberOfUsersAdded;
    private int numberOfUsersModified;

    private String[] regionValidationErrors;
    private String[] branchValidationErrors;
    private String[] userValidationErrors;

    private String[] regionValidationWarnings;
    private String[] branchValidationWarnings;
    private String[] userValidationWarnings;

    private UserUpload upload;


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


    public void setNumberOfUsersModified( int numberOfUsersModified )
    {
        this.numberOfUsersModified = numberOfUsersModified;
    }


    public String[] getRegionValidationErrors()
    {
        return regionValidationErrors;
    }


    public void setRegionValidationErrors( String[] regionValidationErrors )
    {
        this.regionValidationErrors = regionValidationErrors;
    }


    public String[] getBranchValidationErrors()
    {
        return branchValidationErrors;
    }


    public void setBranchValidationErrors( String[] branchValidationErrors )
    {
        this.branchValidationErrors = branchValidationErrors;
    }


    public String[] getUserValidationErrors()
    {
        return userValidationErrors;
    }


    public void setUserValidationErrors( String[] userValidationErrors )
    {
        this.userValidationErrors = userValidationErrors;
    }


    public String[] getRegionValidationWarnings()
    {
        return regionValidationWarnings;
    }


    public void setRegionValidationWarnings( String[] regionValidationWarnings )
    {
        this.regionValidationWarnings = regionValidationWarnings;
    }


    public String[] getBranchValidationWarnings()
    {
        return branchValidationWarnings;
    }


    public void setBranchValidationWarnings( String[] branchValidationWarnings )
    {
        this.branchValidationWarnings = branchValidationWarnings;
    }


    public String[] getUserValidationWarnings()
    {
        return userValidationWarnings;
    }


    public void setUserValidationWarnings( String[] userValidationWarnings )
    {
        this.userValidationWarnings = userValidationWarnings;
    }


    public UserUpload getUpload()
    {
        return upload;
    }


    public void setUpload( UserUpload upload )
    {
        this.upload = upload;
    }


}
