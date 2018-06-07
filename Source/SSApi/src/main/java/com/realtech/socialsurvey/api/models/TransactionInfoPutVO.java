package com.realtech.socialsurvey.api.models;

public class TransactionInfoPutVO {

	
 	private String transactionRef;
 	private String transactionDate;
 	private String transactionCity;
 	private String transactionState;
 	private String transactionType;
 	
 	
 	private String customer1FirstName;
 	private String customer1LastName;
 	private String customer1Email;
 	private String customer2FirstName;
 	private String customer2LastName;
 	private String customer2Email;
 	
 	 //adding property address feild 
    private String propertyAddress;

 	
 	
	public String getTransactionRef() {
		return transactionRef;
	}
	public void setTransactionRef(String transactionRef) {
		this.transactionRef = transactionRef;
	}
	public String getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}
	public String getTransactionCity() {
		return transactionCity;
	}
	public void setTransactionCity(String transactionCity) {
		this.transactionCity = transactionCity;
	}
	public String getTransactionState() {
		return transactionState;
	}
	public void setTransactionState(String transactionState) {
		this.transactionState = transactionState;
	}
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	public String getCustomer1FirstName() {
		return customer1FirstName;
	}
	public void setCustomer1FirstName(String customer1FirstName) {
		this.customer1FirstName = customer1FirstName;
	}
	public String getCustomer1LastName() {
		return customer1LastName;
	}
	public void setCustomer1LastName(String customer1LastName) {
		this.customer1LastName = customer1LastName;
	}
	public String getCustomer1Email() {
		return customer1Email;
	}
	public void setCustomer1Email(String customer1Email) {
		this.customer1Email = customer1Email;
	}
	public String getCustomer2FirstName() {
		return customer2FirstName;
	}
	public void setCustomer2FirstName(String customer2FirstName) {
		this.customer2FirstName = customer2FirstName;
	}
	public String getCustomer2LastName() {
		return customer2LastName;
	}
	public void setCustomer2LastName(String customer2LastName) {
		this.customer2LastName = customer2LastName;
	}
	public String getCustomer2Email() {
		return customer2Email;
	}
	public void setCustomer2Email(String customer2Email) {
		this.customer2Email = customer2Email;
	}
    public String getPropertyAddress()
    {
        return propertyAddress;
    }
    public void setPropertyAddress( String propertyAddress )
    {
        this.propertyAddress = propertyAddress;
    }
	 
	
}
