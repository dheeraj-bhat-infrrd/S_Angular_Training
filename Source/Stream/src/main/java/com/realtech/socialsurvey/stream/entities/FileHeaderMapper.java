package com.realtech.socialsurvey.stream.entities;

public class FileHeaderMapper
{
    private int fundedDate;
    private int fileName;
    private int bor1FirstName;
    private int bor1LastName;
    private int bor1Email;
    private int bor2FirstName;
    private int bor2LastName;
    private int bor2Email;
    private int servicer;
    private int serviceremail;
    private int subPropCity;
    private int subPropState;
    private int loanPurpose;


    public int getFundedDate()
    {
        return fundedDate;
    }


    public void setFundedDate( int fundedDate )
    {
        this.fundedDate = fundedDate;
    }


    public int getFileName()
    {
        return fileName;
    }


    public void setFileName( int fileName )
    {
        this.fileName = fileName;
    }


    public int getBor1FirstName()
    {
        return bor1FirstName;
    }


    public void setBor1FirstName( int bor1FirstName )
    {
        this.bor1FirstName = bor1FirstName;
    }


    public int getBor1LastName()
    {
        return bor1LastName;
    }


    public void setBor1LastName( int bor1LastName )
    {
        this.bor1LastName = bor1LastName;
    }


    public int getBor1Email()
    {
        return bor1Email;
    }


    public void setBor1Email( int bor1Email )
    {
        this.bor1Email = bor1Email;
    }


    public int getBor2FirstName()
    {
        return bor2FirstName;
    }


    public void setBor2FirstName( int bor2FirstName )
    {
        this.bor2FirstName = bor2FirstName;
    }


    public int getBor2LastName()
    {
        return bor2LastName;
    }


    public void setBor2LastName( int bor2LastName )
    {
        this.bor2LastName = bor2LastName;
    }


    public int getBor2Email()
    {
        return bor2Email;
    }


    public void setBor2Email( int bor2Email )
    {
        this.bor2Email = bor2Email;
    }


    public int getServicer()
    {
        return servicer;
    }


    public void setServicer( int servicer )
    {
        this.servicer = servicer;
    }


    public int getServiceremail()
    {
        return serviceremail;
    }


    public void setServiceremail( int serviceremail )
    {
        this.serviceremail = serviceremail;
    }


    public int getSubPropCity()
    {
        return subPropCity;
    }


    public void setSubPropCity( int subPropCity )
    {
        this.subPropCity = subPropCity;
    }


    public int getSubPropState()
    {
        return subPropState;
    }


    public void setSubPropState( int subPropState )
    {
        this.subPropState = subPropState;
    }


    public int getLoanPurpose()
    {
        return loanPurpose;
    }


    public void setLoanPurpose( int loanPurpose )
    {
        this.loanPurpose = loanPurpose;
    }


    @Override
    public String toString()
    {
        return "FileHeaderMapper [fundedDate=" + fundedDate + ", fileName=" + fileName + ", bor1FirstName=" + bor1FirstName
            + ", bor1LastName=" + bor1LastName + ", bor1Email=" + bor1Email + ", bor2FirstName=" + bor2FirstName
            + ", bor2LastName=" + bor2LastName + ", bor2Email=" + bor2Email + ", servicer=" + servicer + ", serviceremail="
            + serviceremail + ", subPropCity=" + subPropCity + ", subPropState=" + subPropState + ", loanPurpose=" + loanPurpose
            + "]";
    }


}
