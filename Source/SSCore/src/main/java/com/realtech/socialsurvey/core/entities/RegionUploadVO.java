package com.realtech.socialsurvey.core.entities;

import java.util.ArrayList;
import java.util.List;


/*
 * The view class for Region
 */
public class RegionUploadVO
{

    private long regionId;
    private boolean isRegionIdModified;
    private List<LongUploadHistory> regionIdHistory;
    private String sourceRegionId;
    private boolean isSourceRegionIdModified;
    private boolean isSourceRegionIdGenerated;
    private List<StringUploadHistory> sourceRegionIdHistory;
    private String regionName;
    private boolean isRegionNameModified;
    private List<StringUploadHistory> regionNameHistory;
    private String regionAddress1;
    private boolean isRegionAddress1Modified;
    private List<StringUploadHistory> regionAddress1History;
    private String regionAddress2;
    private boolean isRegionAddress2Modified;
    private List<StringUploadHistory> regionAddress2History;
    private String regionCountry;
    private boolean isRegionCountryModified;
    private List<StringUploadHistory> regionCountryHistory;
    private String regionCountryCode;
    private boolean isRegionCountryCodeModified;
    private List<StringUploadHistory> regionCountryCodeHistory;
    private String regionState;
    private boolean isRegionStateModified;
    private List<StringUploadHistory> regionStateHistory;
    private String regionCity;
    private boolean isRegionCityModified;
    private List<StringUploadHistory> regionCityHistory;
    private String regionZipcode;
    private boolean isRegionZipcodeModified;
    private List<StringUploadHistory> regionZipcodeHistory;
    private boolean isAddressSet;
    private boolean isRegionAdded;
    private boolean isRegionModified;
    private boolean isErrorRecord;
    private boolean isDeletedRecord;
    private int rowNum;
    private boolean isWarningRecord;
    private List<String> validationErrors = new ArrayList<String>();
    private List<String> validationWarnings = new ArrayList<String>();
    private boolean isInAppendMode;


    public boolean isInAppendMode()
    {
        return isInAppendMode;
    }


    public void setInAppendMode( boolean isInAppendMode )
    {
        this.isInAppendMode = isInAppendMode;
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


    public List<LongUploadHistory> getRegionIdHistory()
    {
        return regionIdHistory;
    }


    public void setRegionIdHistory( List<LongUploadHistory> regionIdHistory )
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


    public boolean isSourceRegionIdGenerated()
    {
        return isSourceRegionIdGenerated;
    }


    public void setSourceRegionIdGenerated( boolean isSourceRegionIdGenerated )
    {
        this.isSourceRegionIdGenerated = isSourceRegionIdGenerated;
    }


    public List<StringUploadHistory> getSourceRegionIdHistory()
    {
        return sourceRegionIdHistory;
    }


    public void setSourceRegionIdHistory( List<StringUploadHistory> sourceRegionIdHistory )
    {
        this.sourceRegionIdHistory = sourceRegionIdHistory;
    }


    public String getRegionName()
    {
        return regionName;
    }


    public void setRegionName( String regionName )
    {
        this.regionName = regionName;
    }


    public boolean isRegionNameModified()
    {
        return isRegionNameModified;
    }


    public void setRegionNameModified( boolean isRegionNameModified )
    {
        this.isRegionNameModified = isRegionNameModified;
    }


    public List<StringUploadHistory> getRegionNameHistory()
    {
        return regionNameHistory;
    }


    public void setRegionNameHistory( List<StringUploadHistory> regionNameHistory )
    {
        this.regionNameHistory = regionNameHistory;
    }


    public String getRegionAddress1()
    {
        return regionAddress1;
    }


    public void setRegionAddress1( String regionAddress1 )
    {
        this.regionAddress1 = regionAddress1;
    }


    public boolean isRegionAddress1Modified()
    {
        return isRegionAddress1Modified;
    }


    public void setRegionAddress1Modified( boolean isRegionAddress1Modified )
    {
        this.isRegionAddress1Modified = isRegionAddress1Modified;
    }


    public List<StringUploadHistory> getRegionAddress1History()
    {
        return regionAddress1History;
    }


    public void setRegionAddress1History( List<StringUploadHistory> regionAddress1History )
    {
        this.regionAddress1History = regionAddress1History;
    }


    public String getRegionAddress2()
    {
        return regionAddress2;
    }


    public void setRegionAddress2( String regionAddress2 )
    {
        this.regionAddress2 = regionAddress2;
    }


    public boolean isRegionAddress2Modified()
    {
        return isRegionAddress2Modified;
    }


    public void setRegionAddress2Modified( boolean isRegionAddress2Modified )
    {
        this.isRegionAddress2Modified = isRegionAddress2Modified;
    }


    public List<StringUploadHistory> getRegionAddress2History()
    {
        return regionAddress2History;
    }


    public void setRegionAddress2History( List<StringUploadHistory> regionAddress2History )
    {
        this.regionAddress2History = regionAddress2History;
    }


    public String getRegionCountry()
    {
        return regionCountry;
    }


    public void setRegionCountry( String regionCountry )
    {
        this.regionCountry = regionCountry;
    }


    public boolean isRegionCountryModified()
    {
        return isRegionCountryModified;
    }


    public void setRegionCountryModified( boolean isRegionCountryModified )
    {
        this.isRegionCountryModified = isRegionCountryModified;
    }


    public List<StringUploadHistory> getRegionCountryHistory()
    {
        return regionCountryHistory;
    }


    public void setRegionCountryHistory( List<StringUploadHistory> regionCountryHistory )
    {
        this.regionCountryHistory = regionCountryHistory;
    }


    public String getRegionCountryCode()
    {
        return regionCountryCode;
    }


    public void setRegionCountryCode( String regionCountryCode )
    {
        this.regionCountryCode = regionCountryCode;
    }


    public boolean isRegionCountryCodeModified()
    {
        return isRegionCountryCodeModified;
    }


    public void setRegionCountryCodeModified( boolean isRegionCountryCodeModified )
    {
        this.isRegionCountryCodeModified = isRegionCountryCodeModified;
    }


    public List<StringUploadHistory> getRegionCountryCodeHistory()
    {
        return regionCountryCodeHistory;
    }


    public void setRegionCountryCodeHistory( List<StringUploadHistory> regionCountryCodeHistory )
    {
        this.regionCountryCodeHistory = regionCountryCodeHistory;
    }


    public String getRegionState()
    {
        return regionState;
    }


    public void setRegionState( String regionState )
    {
        this.regionState = regionState;
    }


    public boolean isRegionStateModified()
    {
        return isRegionStateModified;
    }


    public void setRegionStateModified( boolean isRegionStateModified )
    {
        this.isRegionStateModified = isRegionStateModified;
    }


    public List<StringUploadHistory> getRegionStateHistory()
    {
        return regionStateHistory;
    }


    public void setRegionStateHistory( List<StringUploadHistory> regionStateHistory )
    {
        this.regionStateHistory = regionStateHistory;
    }


    public String getRegionCity()
    {
        return regionCity;
    }


    public void setRegionCity( String regionCity )
    {
        this.regionCity = regionCity;
    }


    public boolean isRegionCityModified()
    {
        return isRegionCityModified;
    }


    public void setRegionCityModified( boolean isRegionCityModified )
    {
        this.isRegionCityModified = isRegionCityModified;
    }


    public List<StringUploadHistory> getRegionCityHistory()
    {
        return regionCityHistory;
    }


    public void setRegionCityHistory( List<StringUploadHistory> regionCityHistory )
    {
        this.regionCityHistory = regionCityHistory;
    }


    public String getRegionZipcode()
    {
        return regionZipcode;
    }


    public void setRegionZipcode( String regionZipcode )
    {
        this.regionZipcode = regionZipcode;
    }


    public boolean isRegionZipcodeModified()
    {
        return isRegionZipcodeModified;
    }


    public void setRegionZipcodeModified( boolean isRegionZipcodeModified )
    {
        this.isRegionZipcodeModified = isRegionZipcodeModified;
    }


    public List<StringUploadHistory> getRegionZipcodeHistory()
    {
        return regionZipcodeHistory;
    }


    public void setRegionZipcodeHistory( List<StringUploadHistory> regionZipcodeHistory )
    {
        this.regionZipcodeHistory = regionZipcodeHistory;
    }


    public boolean isAddressSet()
    {
        return isAddressSet;
    }


    public void setAddressSet( boolean isAddressSet )
    {
        this.isAddressSet = isAddressSet;
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


    public boolean isErrorRecord()
    {
        return isErrorRecord;
    }


    public void setErrorRecord( boolean isErrorRecord )
    {
        this.isErrorRecord = isErrorRecord;
    }


    public boolean isDeletedRecord()
    {
        return isDeletedRecord;
    }


    public void setDeletedRecord( boolean isDeletedRecord )
    {
        this.isDeletedRecord = isDeletedRecord;
    }


    public List<String> getValidationErrors()
    {
        return validationErrors;
    }


    public void setValidationErrors( List<String> validationErrors )
    {
        this.validationErrors = validationErrors;
    }


    public List<String> getValidationWarnings()
    {
        return validationWarnings;
    }


    public void setValidationWarnings( List<String> validationWarnings )
    {
        this.validationWarnings = validationWarnings;
    }


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
