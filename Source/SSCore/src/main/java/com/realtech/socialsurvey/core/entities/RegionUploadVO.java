package com.realtech.socialsurvey.core.entities;

/*
 * The view class for Region
 */
public class RegionUploadVO
{

    private long regionId;
    private boolean isRegionIdModified;
    private LongUploadHistory[] regionIdHistory;
    private String sourceRegionId;
    private boolean isSourceRegionIdModified;
    private boolean isSourceRegionIdGenerated;
    private StringUploadHistory[] sourceRegionIdHistory;
    private String regionName;
    private boolean isRegionNameModified;
    private StringUploadHistory[] regionNameHistory;
    private String regionAddress1;
    private boolean isRegionAddress1Modified;
    private StringUploadHistory[] regionAddress1History;
    private String regionAddress2;
    private boolean isRegionAddress2Modified;
    private StringUploadHistory[] regionAddress2History;
    private String regionCountry;
    private boolean isRegionCountryModified;
    private StringUploadHistory[] regionCountryHistory;
    private String regionCountryCode;
    private boolean isRegionCountryCodeModified;
    private StringUploadHistory[] regionCountryCodeHistory;
    private String regionState;
    private boolean isRegionStateModified;
    private StringUploadHistory[] regionStateHistory;
    private String regionCity;
    private boolean isRegionCityModified;
    private StringUploadHistory[] regionCityHistory;
    private String regionZipcode;
    private boolean isRegionZipcodeModified;
    private StringUploadHistory[] regionZipcodeHistory;
    private boolean isAddressSet;
    private boolean isRegionAdded;
    private boolean isRegionModified;
    private boolean isErrorRecord;


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


    public boolean isSourceRegionIdGenerated()
    {
        return isSourceRegionIdGenerated;
    }


    public void setSourceRegionIdGenerated( boolean isSourceRegionIdGenerated )
    {
        this.isSourceRegionIdGenerated = isSourceRegionIdGenerated;
    }


    public StringUploadHistory[] getSourceRegionIdHistory()
    {
        return sourceRegionIdHistory;
    }


    public void setSourceRegionIdHistory( StringUploadHistory[] sourceRegionIdHistory )
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


    public StringUploadHistory[] getRegionNameHistory()
    {
        return regionNameHistory;
    }


    public void setRegionNameHistory( StringUploadHistory[] regionNameHistory )
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


    public StringUploadHistory[] getRegionAddress1History()
    {
        return regionAddress1History;
    }


    public void setRegionAddress1History( StringUploadHistory[] regionAddress1History )
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


    public StringUploadHistory[] getRegionAddress2History()
    {
        return regionAddress2History;
    }


    public void setRegionAddress2History( StringUploadHistory[] regionAddress2History )
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


    public StringUploadHistory[] getRegionCountryHistory()
    {
        return regionCountryHistory;
    }


    public void setRegionCountryHistory( StringUploadHistory[] regionCountryHistory )
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


    public StringUploadHistory[] getRegionCountryCodeHistory()
    {
        return regionCountryCodeHistory;
    }


    public void setRegionCountryCodeHistory( StringUploadHistory[] regionCountryCodeHistory )
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


    public StringUploadHistory[] getRegionStateHistory()
    {
        return regionStateHistory;
    }


    public void setRegionStateHistory( StringUploadHistory[] regionStateHistory )
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


    public StringUploadHistory[] getRegionCityHistory()
    {
        return regionCityHistory;
    }


    public void setRegionCityHistory( StringUploadHistory[] regionCityHistory )
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


    public StringUploadHistory[] getRegionZipcodeHistory()
    {
        return regionZipcodeHistory;
    }


    public void setRegionZipcodeHistory( StringUploadHistory[] regionZipcodeHistory )
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


    @Override
    public boolean equals( Object uploadVo )
    {
        RegionUploadVO regionUploadVO = (RegionUploadVO) uploadVo;
        if ( this.sourceRegionId != null && !this.sourceRegionId.isEmpty() && regionUploadVO.sourceRegionId != null
            && !regionUploadVO.sourceRegionId.isEmpty() ) {
            return this.sourceRegionId.equals( regionUploadVO.sourceRegionId );
        } else {
            return ( this.regionId == regionUploadVO.regionId );
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
