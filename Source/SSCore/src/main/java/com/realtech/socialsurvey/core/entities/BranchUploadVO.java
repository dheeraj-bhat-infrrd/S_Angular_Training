package com.realtech.socialsurvey.core.entities;

import java.util.List;


/*
 * The view class for Branch
 */

public class BranchUploadVO
{
    // branch information ( 12 fields )
    private long branchId;
    private long regionId;
    private String branchName;
    private String branchAddress1;
    private String branchAddress2;
    private String branchCountry;
    private String branchCountryCode;
    private String branchState;
    private String branchCity;
    private String branchZipcode;

    private String sourceBranchId;
    private String sourceRegionId;


    //~~~~~~ meta data on branch Upload Value Object : BEGIN ~~~~~

    // previous values of  branch information ( 11 fields )
    private List<LongUploadHistory> regionIdHistory;
    private List<StringUploadHistory> branchNameHistory;
    private List<StringUploadHistory> branchAddress1History;
    private List<StringUploadHistory> branchAddress2History;
    private List<StringUploadHistory> branchCountryHistory;
    private List<StringUploadHistory> branchCountryCodeHistory;
    private List<StringUploadHistory> branchStateHistory;
    private List<StringUploadHistory> branchCityHistory;
    private List<StringUploadHistory> branchZipcodeHistory;

    private List<StringUploadHistory> sourceRegionIdHistory;

    // branch entity specific meta data
    private boolean isBranchAdded;
    private boolean isBranchModified;
    private boolean isBranchProcessed;


    // object( Upload Value Object ) specific meta data
    private boolean isErrorRecord;
    private boolean isWarningRecord;

    // upload specific meta data
    private int rowNum;
    //~~~~~~ meta data on branch Upload Value Object : END ~~~~~~~~~


    // Setters and getters : BEGIN
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


    public String getBranchName()
    {
        return branchName;
    }


    public void setBranchName( String branchName )
    {
        this.branchName = branchName;
    }


    public String getBranchAddress1()
    {
        return branchAddress1;
    }


    public void setBranchAddress1( String branchAddress1 )
    {
        this.branchAddress1 = branchAddress1;
    }


    public String getBranchAddress2()
    {
        return branchAddress2;
    }


    public void setBranchAddress2( String branchAddress2 )
    {
        this.branchAddress2 = branchAddress2;
    }


    public String getBranchCountry()
    {
        return branchCountry;
    }


    public void setBranchCountry( String branchCountry )
    {
        this.branchCountry = branchCountry;
    }


    public String getBranchCountryCode()
    {
        return branchCountryCode;
    }


    public void setBranchCountryCode( String branchCountryCode )
    {
        this.branchCountryCode = branchCountryCode;
    }


    public String getBranchState()
    {
        return branchState;
    }


    public void setBranchState( String branchState )
    {
        this.branchState = branchState;
    }


    public String getBranchCity()
    {
        return branchCity;
    }


    public void setBranchCity( String branchCity )
    {
        this.branchCity = branchCity;
    }


    public String getBranchZipcode()
    {
        return branchZipcode;
    }


    public void setBranchZipcode( String branchZipcode )
    {
        this.branchZipcode = branchZipcode;
    }


    public String getSourceBranchId()
    {
        return sourceBranchId;
    }


    public void setSourceBranchId( String sourceBranchId )
    {
        this.sourceBranchId = sourceBranchId;
    }


    public String getSourceRegionId()
    {
        return sourceRegionId;
    }


    public void setSourceRegionId( String sourceRegionId )
    {
        this.sourceRegionId = sourceRegionId;
    }


    public List<LongUploadHistory> getRegionIdHistory()
    {
        return regionIdHistory;
    }


    public void setRegionIdHistory( List<LongUploadHistory> regionIdHistory )
    {
        this.regionIdHistory = regionIdHistory;
    }


    public List<StringUploadHistory> getBranchNameHistory()
    {
        return branchNameHistory;
    }


    public void setBranchNameHistory( List<StringUploadHistory> branchNameHistory )
    {
        this.branchNameHistory = branchNameHistory;
    }


    public List<StringUploadHistory> getBranchAddress1History()
    {
        return branchAddress1History;
    }


    public void setBranchAddress1History( List<StringUploadHistory> branchAddress1History )
    {
        this.branchAddress1History = branchAddress1History;
    }


    public List<StringUploadHistory> getBranchAddress2History()
    {
        return branchAddress2History;
    }


    public void setBranchAddress2History( List<StringUploadHistory> branchAddress2History )
    {
        this.branchAddress2History = branchAddress2History;
    }


    public List<StringUploadHistory> getBranchCountryHistory()
    {
        return branchCountryHistory;
    }


    public void setBranchCountryHistory( List<StringUploadHistory> branchCountryHistory )
    {
        this.branchCountryHistory = branchCountryHistory;
    }


    public List<StringUploadHistory> getBranchCountryCodeHistory()
    {
        return branchCountryCodeHistory;
    }


    public void setBranchCountryCodeHistory( List<StringUploadHistory> branchCountryCodeHistory )
    {
        this.branchCountryCodeHistory = branchCountryCodeHistory;
    }


    public List<StringUploadHistory> getBranchStateHistory()
    {
        return branchStateHistory;
    }


    public void setBranchStateHistory( List<StringUploadHistory> branchStateHistory )
    {
        this.branchStateHistory = branchStateHistory;
    }


    public List<StringUploadHistory> getBranchCityHistory()
    {
        return branchCityHistory;
    }


    public void setBranchCityHistory( List<StringUploadHistory> branchCityHistory )
    {
        this.branchCityHistory = branchCityHistory;
    }


    public List<StringUploadHistory> getBranchZipcodeHistory()
    {
        return branchZipcodeHistory;
    }


    public void setBranchZipcodeHistory( List<StringUploadHistory> branchZipcodeHistory )
    {
        this.branchZipcodeHistory = branchZipcodeHistory;
    }


    public List<StringUploadHistory> getSourceRegionIdHistory()
    {
        return sourceRegionIdHistory;
    }


    public void setSourceRegionIdHistory( List<StringUploadHistory> sourceRegionIdHistory )
    {
        this.sourceRegionIdHistory = sourceRegionIdHistory;
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


    public boolean isBranchProcessed()
    {
        return isBranchProcessed;
    }


    public void setBranchProcessed( boolean isBranchProcessed )
    {
        this.isBranchProcessed = isBranchProcessed;
    }


    public boolean isErrorRecord()
    {
        return isErrorRecord;
    }


    public void setErrorRecord( boolean isErrorRecord )
    {
        this.isErrorRecord = isErrorRecord;
    }


    public boolean isWarningRecord()
    {
        return isWarningRecord;
    }


    public void setWarningRecord( boolean isWarningRecord )
    {
        this.isWarningRecord = isWarningRecord;
    }


    public int getRowNum()
    {
        return rowNum;
    }


    public void setRowNum( int rowNum )
    {
        this.rowNum = rowNum;
    }
    // Setters and getters : END


    // priority on source Id and then internal Id
    @Override
    public boolean equals( Object uploadVo )
    {
        BranchUploadVO branchUploadVO = (BranchUploadVO) uploadVo;
        if ( this.sourceBranchId != null && !this.sourceBranchId.isEmpty() && branchUploadVO.sourceBranchId != null
            && !branchUploadVO.sourceBranchId.isEmpty() ) {
            return this.sourceBranchId.equals( branchUploadVO.sourceBranchId );
        } else if ( this.branchId != 0 && branchUploadVO.branchId != 0 ) {
            return ( this.branchId == branchUploadVO.branchId );
        } else {
            return false;
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
