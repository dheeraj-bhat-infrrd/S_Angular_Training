package com.realtech.socialsurvey.core.entities;

public class LockSettings
{

    private boolean isLogoLocked;
    private boolean isWebAddressLocked;
    private boolean isBlogAddressLocked;
    private boolean isWorkPhoneLocked;
    private boolean isPersonalPhoneLocked;
    private boolean isFaxPhoneLocked;
    private boolean isAboutMeLocked;
    private boolean isAddressLocked;
    private boolean isMinScoreLocked;


    public boolean getIsLogoLocked()
    {
        return isLogoLocked;
    }


    public void setLogoLocked( boolean isLogoLocked )
    {
        this.isLogoLocked = isLogoLocked;
    }


    public boolean getIsWebAddressLocked()
    {
        return isWebAddressLocked;
    }


    public void setWebAddressLocked( boolean isWebAddressLocked )
    {
        this.isWebAddressLocked = isWebAddressLocked;
    }


    public boolean getIsBlogAddressLocked()
    {
        return isBlogAddressLocked;
    }


    public void setBlogAddressLocked( boolean isBlogAddressLocked )
    {
        this.isBlogAddressLocked = isBlogAddressLocked;
    }


    public boolean getIsWorkPhoneLocked()
    {
        return isWorkPhoneLocked;
    }


    public void setWorkPhoneLocked( boolean isWorkPhoneLocked )
    {
        this.isWorkPhoneLocked = isWorkPhoneLocked;
    }


    public boolean getIsPersonalPhoneLocked()
    {
        return isPersonalPhoneLocked;
    }


    public void setPersonalPhoneLocked( boolean isPersonalPhoneLocked )
    {
        this.isPersonalPhoneLocked = isPersonalPhoneLocked;
    }


    public boolean getIsFaxPhoneLocked()
    {
        return isFaxPhoneLocked;
    }


    public void setFaxPhoneLocked( boolean isFaxPhoneLocked )
    {
        this.isFaxPhoneLocked = isFaxPhoneLocked;
    }


    public boolean getIsAboutMeLocked()
    {
        return isAboutMeLocked;
    }


    public void setAboutMeLocked( boolean isAboutMeLocked )
    {
        this.isAboutMeLocked = isAboutMeLocked;
    }


    public boolean getIsAddressLocked()
    {
        return isAddressLocked;
    }


    public void setAddressLocked( boolean isAddressLocked )
    {
        this.isAddressLocked = isAddressLocked;
    }


    public boolean isMinScoreLocked()
    {
        return isMinScoreLocked;
    }


    public void setMinScoreLocked( boolean isMinScoreLocked )
    {
        this.isMinScoreLocked = isMinScoreLocked;
    }


    @Override
    public String toString()
    {
        return "LockSettings [isLogoLocked=" + isLogoLocked + ", isWebAddressLocked=" + isWebAddressLocked
            + ", isBlogAddressLocked=" + isBlogAddressLocked + ", isWorkPhoneLocked=" + isWorkPhoneLocked
            + ", isPersonalPhoneLocked=" + isPersonalPhoneLocked + ", isFaxPhoneLocked=" + isFaxPhoneLocked
            + ", isAboutMeLocked=" + isAboutMeLocked + ", isAddressLocked=" + isAddressLocked + "]";
    }
}