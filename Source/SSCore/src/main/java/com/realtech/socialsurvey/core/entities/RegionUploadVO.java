package com.realtech.socialsurvey.core.entities;

import java.util.List;


/*
 * The view class for Region
 */
public class RegionUploadVO
{
    // region information ( 10 fields )
    private long regionId;
    private String regionName;
    private String regionAddress1;
    private String regionAddress2;
    private String regionCountry;
    private String regionCountryCode;
    private String regionState;
    private String regionCity;
    private String regionZipcode;

    private String sourceRegionId;


    //~~~~~~ meta data on region Upload Value Object : BEGIN ~~~~~~~

    // previous values of  region information ( 9 fields )
    private List<StringUploadHistory> regionNameHistory;
    private List<StringUploadHistory> regionAddress1History;
    private List<StringUploadHistory> regionAddress2History;
    private List<StringUploadHistory> regionCountryHistory;
    private List<StringUploadHistory> regionCountryCodeHistory;
    private List<StringUploadHistory> regionStateHistory;
    private List<StringUploadHistory> regionCityHistory;
    private List<StringUploadHistory> regionZipcodeHistory;

    // region entity specific meta data
    private boolean isRegionAdded;
    private boolean isRegionModified;
    private boolean isRegionProcessed;

    // object( Upload Value Object ) specific meta data
    private boolean isErrorRecord;
    private boolean isWarningRecord;

    // upload specific meta data
    private int rowNum;

    //~~~~~ meta data on region Upload Value Object : END ~~~~~~~


    // Setters and getters : BEGIN
    public long getRegionId()
    {
        return regionId;
    }


    public void setRegionId( long regionId )
    {
        this.regionId = regionId;
    }


    public String getRegionName()
    {
        return regionName;
    }


    public void setRegionName( String regionName )
    {
        this.regionName = regionName;
    }


    public String getRegionAddress1()
    {
        return regionAddress1;
    }


    public void setRegionAddress1( String regionAddress1 )
    {
        this.regionAddress1 = regionAddress1;
    }


    public String getRegionAddress2()
    {
        return regionAddress2;
    }


    public void setRegionAddress2( String regionAddress2 )
    {
        this.regionAddress2 = regionAddress2;
    }


    public String getRegionCountry()
    {
        return regionCountry;
    }


    public void setRegionCountry( String regionCountry )
    {
        this.regionCountry = regionCountry;
    }


    public String getRegionCountryCode()
    {
        return regionCountryCode;
    }


    public void setRegionCountryCode( String regionCountryCode )
    {
        this.regionCountryCode = regionCountryCode;
    }


    public String getRegionState()
    {
        return regionState;
    }


    public void setRegionState( String regionState )
    {
        this.regionState = regionState;
    }


    public String getRegionCity()
    {
        return regionCity;
    }


    public void setRegionCity( String regionCity )
    {
        this.regionCity = regionCity;
    }


    public String getRegionZipcode()
    {
        return regionZipcode;
    }


    public void setRegionZipcode( String regionZipcode )
    {
        this.regionZipcode = regionZipcode;
    }


    public String getSourceRegionId()
    {
        return sourceRegionId;
    }


    public void setSourceRegionId( String sourceRegionId )
    {
        this.sourceRegionId = sourceRegionId;
    }


    public List<StringUploadHistory> getRegionNameHistory()
    {
        return regionNameHistory;
    }


    public void setRegionNameHistory( List<StringUploadHistory> regionNameHistory )
    {
        this.regionNameHistory = regionNameHistory;
    }


    public List<StringUploadHistory> getRegionAddress1History()
    {
        return regionAddress1History;
    }


    public void setRegionAddress1History( List<StringUploadHistory> regionAddress1History )
    {
        this.regionAddress1History = regionAddress1History;
    }


    public List<StringUploadHistory> getRegionAddress2History()
    {
        return regionAddress2History;
    }


    public void setRegionAddress2History( List<StringUploadHistory> regionAddress2History )
    {
        this.regionAddress2History = regionAddress2History;
    }


    public List<StringUploadHistory> getRegionCountryHistory()
    {
        return regionCountryHistory;
    }


    public void setRegionCountryHistory( List<StringUploadHistory> regionCountryHistory )
    {
        this.regionCountryHistory = regionCountryHistory;
    }


    public List<StringUploadHistory> getRegionCountryCodeHistory()
    {
        return regionCountryCodeHistory;
    }


    public void setRegionCountryCodeHistory( List<StringUploadHistory> regionCountryCodeHistory )
    {
        this.regionCountryCodeHistory = regionCountryCodeHistory;
    }


    public List<StringUploadHistory> getRegionStateHistory()
    {
        return regionStateHistory;
    }


    public void setRegionStateHistory( List<StringUploadHistory> regionStateHistory )
    {
        this.regionStateHistory = regionStateHistory;
    }


    public List<StringUploadHistory> getRegionCityHistory()
    {
        return regionCityHistory;
    }


    public void setRegionCityHistory( List<StringUploadHistory> regionCityHistory )
    {
        this.regionCityHistory = regionCityHistory;
    }


    public List<StringUploadHistory> getRegionZipcodeHistory()
    {
        return regionZipcodeHistory;
    }


    public void setRegionZipcodeHistory( List<StringUploadHistory> regionZipcodeHistory )
    {
        this.regionZipcodeHistory = regionZipcodeHistory;
    }


    public boolean isRegionAdded()
    {
        return isRegionAdded;
    }


    public void setRegionAdded( boolean isRegionAdded )
    {
        this.isRegionAdded = isRegionAdded;
    }


    public boolean isRegionModified()
    {
        return isRegionModified;
    }


    public void setRegionModified( boolean isRegionModified )
    {
        this.isRegionModified = isRegionModified;
    }


    public boolean isRegionProcessed()
    {
        return isRegionProcessed;
    }


    public void setRegionProcessed( boolean isRegionProcessed )
    {
        this.isRegionProcessed = isRegionProcessed;
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
        RegionUploadVO regionUploadVO = (RegionUploadVO) uploadVo;
        if ( this.sourceRegionId != null && !this.sourceRegionId.isEmpty() && regionUploadVO.sourceRegionId != null
            && !regionUploadVO.sourceRegionId.isEmpty() ) {
            return this.sourceRegionId.equals( regionUploadVO.sourceRegionId );
        } else if ( this.regionId != 0 && regionUploadVO.regionId != 0 ) {
            return ( this.regionId == regionUploadVO.regionId );
        } else {
            return false;
        }
    }


    @Override
    public int hashCode()
    {
        if ( sourceRegionId != null && !sourceRegionId.isEmpty() ) {
            return sourceRegionId.hashCode();
        } else {
            return ( new Long( regionId ) ).hashCode();
        }
    }


}
