package com.realtech.socialsurvey.core.entities;

/**
 * Holds the prfile contact details
 */
public class ContactDetailsSettings {

	private String name;
	private String firstName;
	private String lastName;
	private String address;
	private String address1;
	private String address2;
	private String country;
	private String state;
	private String city;
	private String countryCode;
	private String zipcode;
	private String location;
	private String industry;
	private String about_me;
	private String title;
	private MailIdSettings mail_ids;
	private ContactNumberSettings contact_numbers;
	private SocialLinksSettings social_links;
	private WebAddressSettings web_addresses;
	private boolean updatedBySystem;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public String getAbout_me() {
		return about_me;
	}

	public void setAbout_me(String about_me) {
		this.about_me = about_me;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public MailIdSettings getMail_ids() {
		return mail_ids;
	}

	public void setMail_ids(MailIdSettings mail_ids) {
		this.mail_ids = mail_ids;
	}

	public ContactNumberSettings getContact_numbers() {
		return contact_numbers;
	}

	public void setContact_numbers(ContactNumberSettings contact_numbers) {
		this.contact_numbers = contact_numbers;
	}

	public SocialLinksSettings getSocial_links() {
		return social_links;
	}

	public void setSocial_links(SocialLinksSettings social_links) {
		this.social_links = social_links;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public WebAddressSettings getWeb_addresses() {
		return web_addresses;
	}

	public void setWeb_addresses(WebAddressSettings web_addresses) {
		this.web_addresses = web_addresses;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
	
	public boolean isUpdatedBySystem() {
		return updatedBySystem;
	}

	public void setUpdatedBySystem(boolean updatedBySystem) {
		this.updatedBySystem = updatedBySystem;
	}

	@Override
	public String toString() {
		return "ContactDetailsSettings [name=" + name + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", address=" + address + ", address1=" + address1 + ", address2=" + address2 + ", country=" + country
				+ ", state=" + state + ", city=" + city + ", countryCode=" + countryCode + ", zipcode=" + zipcode
				+ ", location=" + location + ", industry=" + industry + ", about_me=" + about_me + ", title=" + title
				+ ", mail_ids=" + mail_ids + ", contact_numbers=" + contact_numbers + ", social_links=" + social_links
				+ ", web_addresses=" + web_addresses + ", updatedBySystem=" + updatedBySystem + "]";
	}

}
