package com.realtech.socialsurvey.core.entities.ftp;

import java.util.HashMap;
import java.util.Map;


public class FtpSurveyResponse
{
    private int totalTransaction;
    private int totalSurveys;
    private int customer1Count;
    private int customer2Count;
    private int buyerCount;
    private int sellerCount;
    private int errorNum;
    private Map<Integer, String> errorMessage = new HashMap<>();


    public int getTotalTransaction()
    {
        return totalTransaction;
    }


    public void setTotalTransaction( int totalTransaction )
    {
        this.totalTransaction = totalTransaction;
    }


    public int getTotalSurveys()
    {
        return totalSurveys;
    }


    public void setTotalSurveys( int totalSurveys )
    {
        this.totalSurveys = totalSurveys;
    }


    public int getCustomer1Count()
    {
        return customer1Count;
    }


    public void setCustomer1Count( int customer1Count )
    {
        this.customer1Count = customer1Count;
    }


    public int getCustomer2Count()
    {
        return customer2Count;
    }


    public void setCustomer2Count( int customer2Count )
    {
        this.customer2Count = customer2Count;
    }


    public int getErrorNum()
    {
        return errorNum;
    }


    public void setErrorNum( int errorNum )
    {
        this.errorNum = errorNum;
    }


    public Map<Integer, String> getErrorMessage()
    {
        return errorMessage;
    }


    public void setErrorMessage( Map<Integer, String> errorMessage )
    {
        this.errorMessage = errorMessage;
    }


    public int getBuyerCount()
    {
        return buyerCount;
    }


    public void setBuyerCount( int buyerCount )
    {
        this.buyerCount = buyerCount;
    }


    public int getSellerCount()
    {
        return sellerCount;
    }


    public void setSellerCount( int sellerCount )
    {
        this.sellerCount = sellerCount;
    }

}
