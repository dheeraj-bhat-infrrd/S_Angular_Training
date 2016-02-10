package com.realtech.socialsurvey.core.entities;

/*
 * The view class for Branch
 */

public class BranchUploadVO
{

    private long branchId;
    private boolean isBranchIdModified;
    private LongUploadHistory[] branchIdHistory;
    private String sourceBranchId;
    private boolean isSourceBranchIdModified;
    private boolean isSourceBrancIdGenerated;
    private StringUploadHistory[] sourceBranchIdHistory;
    private long regionId;
    private boolean isRegionIdModified;
    private LongUploadHistory[] regionIdHistory;
    private String sourceRegionId;
    private boolean isSourceRegionIdModified;
    private StringUploadHistory[] sourceRegionIdHistory;
    private String branchName;
    private boolean isBranchNameModified;
    private StringUploadHistory[] branchNameHistory;
    private String branchAddress1;
    private boolean isBranchAddress1Modified;
    private StringUploadHistory[] branchAddress1History;
    private String branchAddress2;
    private boolean isBranchAddress2Modified;
    private StringUploadHistory[] branchAddress2History;
    private String branchCountry;
    private boolean isBranchCountryModified;
    private StringUploadHistory[] branchCountryHistory;
    private String branchCountryCode;
    private boolean isBranchCountryCodeModified;
    private StringUploadHistory[] branchCountryCodeHistory;
    private String branchState;
    private boolean isBranchStateModified;
    private StringUploadHistory[] branchStateHistory;
    private String branchCity;
    private boolean isBranchCityModified;
    private StringUploadHistory[] branchCityHistory;
    private String branchZipcode;
    private boolean isBranchZipcodeModified;
    private StringUploadHistory[] branchZipcodeHistory;
    private boolean assignToCompany;
    private String assignedRegionName;
    private boolean isAddressSet;
    private boolean isBranchAdded;
    private boolean isBranchModified;
    private boolean isErrorRecord;


    public long getBranchId()
    {
        return branchId;
    }


    public void setBranchId( long branchId )
    {
        this.branchId = branchId;
    }


    public boolean isBranchIdModified()
    {
        return isBranchIdModified;
    }


    public void setBranchIdModified( boolean isBranchIdModified )
    {
        this.isBranchIdModified = isBranchIdModified;
    }


    public LongUploadHistory[] getBranchIdHistory()
    {
        return branchIdHistory;
    }


    public void setBranchIdHistory( LongUploadHistory[] branchIdHistory )
    {
        this.branchIdHistory = branchIdHistory;
    }


    public String getSourceBranchId()
    {
        return sourceBranchId;
    }


    public void setSourceBranchId( String sourceBranchId )
    {
        this.sourceBranchId = sourceBranchId;
    }


    public boolean isSourceBranchIdModified()
    {
        return isSourceBranchIdModified;
    }


    public void setSourceBranchIdModified( boolean isSourceBranchIdModified )
    {
        this.isSourceBranchIdModified = isSourceBranchIdModified;
    }


    public boolean isSourceBrancIdGenerated()
    {
        return isSourceBrancIdGenerated;
    }


    public void setSourceBrancIdGenerated( boolean isSourceBrancIdGenerated )
    {
        this.isSourceBrancIdGenerated = isSourceBrancIdGenerated;
    }


    public StringUploadHistory[] getSourceBranchIdHistory()
    {
        return sourceBranchIdHistory;
    }


    public void setSourceBranchIdHistory( StringUploadHistory[] sourceBranchIdHistory )
    {
        this.sourceBranchIdHistory = sourceBranchIdHistory;
    }


    public long getRegionId()
    {
        return regionId;
    }


    public void setRegionId( long regionId )
    {
        this.regionId = regionId;
    }


    public boolean isRegionIdModified()
    {
        return isRegionIdModified;
    }


    public void setRegionIdModified( boolean isRegionIdModified )
    {
        this.isRegionIdModified = isRegionIdModified;
    }


    public LongUploadHistory[] getRegionIdHistory()
    {
        return regionIdHistory;
    }


    public void setRegionIdHistory( LongUploadHistory[] regionIdHistory )
    {
        this.regionIdHistory = regionIdHistory;
    }


    public String getSourceRegionId()
    {
        return sourceRegionId;
    }


    public void setSourceRegionId( String sourceRegionId )
    {
        this.sourceRegionId = sourceRegionId;
    }


    public boolean isSourceRegionIdModified()
    {
        return isSourceRegionIdModified;
    }


    public void setSourceRegionIdModified( boolean isSourceRegionIdModified )
    {
        this.isSourceRegionIdModified = isSourceRegionIdModified;
    }


    public StringUploadHistory[] getSourceRegionIdHistory()
    {
        return sourceRegionIdHistory;
    }


    public void setSourceRegionIdHistory( StringUploadHistory[] sourceRegionIdHistory )
    {
        this.sourceRegionIdHistory = sourceRegionIdHistory;
    }


    public String getBranchName()
    {
        return branchName;
    }


    public void setBranchName( String branchName )
    {
        this.branchName = branchName;
    }


    public boolean isBranchNameModified()
    {
        return isBranchNameModified;
    }


    public void setBranchNameModified( boolean isBranchNameModified )
    {
        this.isBranchNameModified = isBranchNameModified;
    }


    public StringUploadHistory[] getBranchNameHistory()
    {
        return branchNameHistory;
    }


    public void setBranchNameHistory( StringUploadHistory[] branchNameHistory )
    {
        this.branchNameHistory = branchNameHistory;
    }


    public String getBranchAddress1()
    {
        return branchAddress1;
    }


    public void setBranchAddress1( String branchAddress1 )
    {
        this.branchAddress1 = branchAddress1;
    }


    public boolean isBranchAddress1Modified()
    {
        return isBranchAddress1Modified;
    }


    public void setBranchAddress1Modified( boolean isBranchAddress1Modified )
    {
        this.isBranchAddress1Modified = isBranchAddress1Modified;
    }


    public StringUploadHistory[] getBranchAddress1History()
    {
        return branchAddress1History;
    }


    public void setBranchAddress1History( StringUploadHistory[] branchAddress1History )
    {
        this.branchAddress1History = branchAddress1History;
    }


    public String getBranchAddress2()
    {
        return branchAddress2;
    }


    public void setBranchAddress2( String branchAddress2 )
    {
        this.branchAddress2 = branchAddress2;
    }


    public boolean isBranchAddress2Modified()
    {
        return isBranchAddress2Modified;
    }


    public void setBranchAddress2Modified( boolean isBranchAddress2Modified )
    {
        this.isBranchAddress2Modified = isBranchAddress2Modified;
    }


    public StringUploadHistory[] getBranchAddress2History()
    {
        return branchAddress2History;
    }


    public void setBranchAddress2History( StringUploadHistory[] branchAddress2History )
    {
        this.branchAddress2History = branchAddress2History;
    }


    public String getBranchCountry()
    {
        return branchCountry;
    }


    public void setBranchCountry( String branchCountry )
    {
        this.branchCountry = branchCountry;
    }


    public boolean isBranchCountryModified()
    {
        return isBranchCountryModified;
    }


    public void setBranchCountryModified( boolean isBranchCountryModified )
    {
        this.isBranchCountryModified = isBranchCountryModified;
    }


    public StringUploadHistory[] getBranchCountryHistory()
    {
        return branchCountryHistory;
    }


    public void setBranchCountryHistory( StringUploadHistory[] branchCountryHistory )
    {
        this.branchCountryHistory = branchCountryHistory;
    }


    public String getBranchCountryCode()
    {
        return branchCountryCode;
    }


    public void setBranchCountryCode( String branchCountryCode )
    {
        this.branchCountryCode = branchCountryCode;
    }


    public boolean isBranchCountryCodeModified()
    {
        return isBranchCountryCodeModified;
    }


    public void setBranchCountryCodeModified( boolean isBranchCountryCodeModified )
    {
        this.isBranchCountryCodeModified = isBranchCountryCodeModified;
    }


    public StringUploadHistory[] getBranchCountryCodeHistory()
    {
        return branchCountryCodeHistory;
    }


    public void setBranchCountryCodeHistory( StringUploadHistory[] branchCountryCodeHistory )
    {
        this.branchCountryCodeHistory = branchCountryCodeHistory;
    }


    public String getBranchState()
    {
        return branchState;
    }


    public void setBranchState( String branchState )
    {
        this.branchState = branchState;
    }


    public boolean isBranchStateModified()
    {
        return isBranchStateModified;
    }


    public void setBranchStateModified( boolean isBranchStateModified )
    {
        this.isBranchStateModified = isBranchStateModified;
    }


    public StringUploadHistory[] getBranchStateHistory()
    {
        return branchStateHistory;
    }


    public void setBranchStateHistory( StringUploadHistory[] branchStateHistory )
    {
        this.branchStateHistory = branchStateHistory;
    }


    public String getBranchCity()
    {
        return branchCity;
    }


    public void setBranchCity( String branchCity )
    {
        this.branchCity = branchCity;
    }


    public boolean isBranchCityModified()
    {
        return isBranchCityModified;
    }


    public void setBranchCityModified( boolean isBranchCityModified )
    {
        this.isBranchCityModified = isBranchCityModified;
    }


    public StringUploadHistory[] getBranchCityHistory()
    {
        return branchCityHistory;
    }


    public void setBranchCityHistory( StringUploadHistory[] branchCityHistory )
    {
        this.branchCityHistory = branchCityHistory;
    }


    public String getBranchZipcode()
    {
        return branchZipcode;
    }


    public void setBranchZipcode( String branchZipcode )
    {
        this.branchZipcode = branchZipcode;
    }


    public boolean isBranchZipcodeModified()
    {
        return isBranchZipcodeModified;
    }


    public void setBranchZipcodeModified( boolean isBranchZipcodeModified )
    {
        this.isBranchZipcodeModified = isBranchZipcodeModified;
    }


    public StringUploadHistory[] getBranchZipcodeHistory()
    {
        return branchZipcodeHistory;
    }


    public void setBranchZipcodeHistory( StringUploadHistory[] branchZipcodeHistory )
    {
        this.branchZipcodeHistory = branchZipcodeHistory;
    }


    public boolean isAssignToCompany()
    {
        return assignToCompany;
    }


    public void setAssignToCompany( boolean assignToCompany )
    {
        this.assignToCompany = assignToCompany;
    }


    public String getAssignedRegionName()
    {
        return assignedRegionName;
    }


    public void setAssignedRegionName( String assignedRegionName )
    {
        this.assignedRegionName = assignedRegionName;
    }


    public boolean isAddressSet()
    {
        return isAddressSet;
    }


    public void setAddressSet( boolean isAddressSet )
    {
        this.isAddressSet = isAddressSet;
    }


    public boolean isBranchAdded()
    {
        return isBranchAdded;
    }


    public void setBranchAdded( boolean isBranchAdded )
    {
        this.isBranchAdded = isBranchAdded;
    }


    public boolean isBranchModified()
    {
        return isBranchModified;
    }


    public void setBranchModified( boolean isBranchModified )
    {
        this.isBranchModified = isBranchModified;
    }


    public boolean isErrorRecord()
    {
        return isErrorRecord;
    }


    public void setErrorRecord( boolean isErrorRecord )
    {
        this.isErrorRecord = isErrorRecord;
    }


    @Override
    public boolean equals( Object uploadVo )
    {
        BranchUploadVO branchUploadVO = (BranchUploadVO) uploadVo;
        if ( this.sourceBranchId != null && !this.sourceBranchId.isEmpty() && branchUploadVO.sourceBranchId != null
            && !branchUploadVO.sourceBranchId.isEmpty() ) {
            return this.sourceBranchId.equals( branchUploadVO.sourceBranchId );
        } else {
            return ( this.branchId == branchUploadVO.branchId );
        }
    }


    @Override
    public int hashCode()
    {
        if ( sourceBranchId != null && !sourceBranchId.isEmpty() ) {
            return sourceBranchId.hashCode();
        } else {
            return ( new Long( branchId ) ).hashCode();
        }
    }

}
