package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;


public class ParsedHierarchyUpload implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    // identifiers
    @Id
    private String id;
    private long companyId;

    // user who started the import
    private long importInitiatedUserId;

    // data about the uploaded xlsx
    private String givenFileName;
    private Date uploadedDate;
    private String fileURI;

    // status of the hierarchy upload process
    private int status;

    // mode of the upload process
    private boolean isInAppendMode;

    // flag to ignore warning
    private boolean isWarningToBeIgnored;

    // flag to abort after verification
    private boolean verifyOnly;

    // hierarchy verification statistics
    private int numberOfRegionsAdded;
    private int numberOfBranchesAdded;
    private int numberOfUsersAdded;

    private int numberOfRegionsModified;
    private int numberOfBranchesModified;
    private int numberOfUsersModified;

    private int numberOfBranchesDeleted;
    private int numberOfUsersDeleted;
    private int numberOfRegionsDeleted;

    // error and warning lists
    private List<String> regionErrors;
    private List<String> branchErrors;
    private List<String> userErrors;

    private List<String> regionValidationWarnings;
    private List<String> branchValidationWarnings;
    private List<String> userValidationWarnings;

    private List<String> generalErrors;

    // global error and warning flag
    private boolean hasErrors;
    private boolean hasWarnings;
    private boolean hasGeneralErrors;


    public boolean hasGeneralErrors()
    {
        return hasGeneralErrors;
    }


    public void setHasGeneralErrors( boolean hasGeneralErrors )
    {
        this.hasGeneralErrors = hasGeneralErrors;
    }


    public boolean hasErrors()
    {
        return hasErrors;
    }


    public void setHasErrors( boolean hasErrors )
    {
        this.hasErrors = hasErrors;
    }


    public boolean hasWarnings()
    {
        return hasWarnings;
    }


    public void setHasWarnings( boolean hasWarnings )
    {
        this.hasWarnings = hasWarnings;
    }


    public List<String> getGeneralErrors()
    {
        return generalErrors;
    }


    public void setGeneralErrors( List<String> generalErrors )
    {
        this.generalErrors = generalErrors;
    }


    public String getId()
    {
        return id;
    }


    public void setId( String id )
    {
        this.id = id;
    }


    public long getCompanyId()
    {
        return companyId;
    }


    public void setCompanyId( long companyId )
    {
        this.companyId = companyId;
    }


    public String getFileURI()
    {
        return fileURI;
    }


    public void setFileURI( String fileURI )
    {
        this.fileURI = fileURI;
    }


    public int getStatus()
    {
        return status;
    }


    public void setStatus( int status )
    {
        this.status = status;
    }


    public boolean isWarningToBeIgnored()
    {
        return isWarningToBeIgnored;
    }


    public void setWarningToBeIgnored( boolean isWarningToBeIgnored )
    {
        this.isWarningToBeIgnored = isWarningToBeIgnored;
    }


    public boolean getVerifyOnly()
    {
        return verifyOnly;
    }


    public void setVerifyOnly( boolean verifyOnly )
    {
        this.verifyOnly = verifyOnly;
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


    public List<String> getRegionErrors()
    {
        return regionErrors;
    }


    public void setRegionErrors( List<String> regionErrors )
    {
        this.regionErrors = regionErrors;
    }


    public List<String> getBranchErrors()
    {
        return branchErrors;
    }


    public void setBranchErrors( List<String> branchErrors )
    {
        this.branchErrors = branchErrors;
    }


    public List<String> getUserErrors()
    {
        return userErrors;
    }


    public void setUserErrors( List<String> userErrors )
    {
        this.userErrors = userErrors;
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


    public boolean isInAppendMode()
    {
        return isInAppendMode;
    }


    public void setInAppendMode( boolean isInAppendMode )
    {
        this.isInAppendMode = isInAppendMode;
    }


    public long getImportInitiatedUserId()
    {
        return importInitiatedUserId;
    }


    public void setImportInitiatedUserId( long importInitiatedUserId )
    {
        this.importInitiatedUserId = importInitiatedUserId;
    }


    public String getGivenFileName()
    {
        return givenFileName;
    }


    public void setGivenFileName( String givenFileName )
    {
        this.givenFileName = givenFileName;
    }


    public Date getUploadedDate()
    {
        return uploadedDate;
    }


    public void setUploadedDate( Date uploadedDate )
    {
        this.uploadedDate = uploadedDate;
    }


    @Override
    public String toString()
    {
        return "ParsedHierarchyUpload [_id=" + id + ", companyId=" + companyId + ", importInitiatedUserId="
            + importInitiatedUserId + ", givenFileName=" + givenFileName + ", uploadedDate=" + uploadedDate + ", fileURI="
            + fileURI + ", status=" + status + ", isInAppendMode=" + isInAppendMode + ", isWarningToBeIgnored="
            + isWarningToBeIgnored + ", verifyOnly=" + verifyOnly + ", numberOfRegionsAdded=" + numberOfRegionsAdded
            + ", numberOfBranchesAdded=" + numberOfBranchesAdded + ", numberOfUsersAdded=" + numberOfUsersAdded
            + ", numberOfRegionsModified=" + numberOfRegionsModified + ", numberOfBranchesModified=" + numberOfBranchesModified
            + ", numberOfUsersModified=" + numberOfUsersModified + ", numberOfBranchesDeleted=" + numberOfBranchesDeleted
            + ", numberOfUsersDeleted=" + numberOfUsersDeleted + ", numberOfRegionsDeleted=" + numberOfRegionsDeleted
            + ", regionErrors=" + regionErrors + ", branchErrors=" + branchErrors + ", userErrors=" + userErrors
            + ", regionValidationWarnings=" + regionValidationWarnings + ", branchValidationWarnings="
            + branchValidationWarnings + ", userValidationWarnings=" + userValidationWarnings + ", generalErrors="
            + generalErrors + ", hasErrors=" + hasErrors + ", hasWarnings=" + hasWarnings + ", hasGeneralErrors="
            + hasGeneralErrors + "]";
    }
}
