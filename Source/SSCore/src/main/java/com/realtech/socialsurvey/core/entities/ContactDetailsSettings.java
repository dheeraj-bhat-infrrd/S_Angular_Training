package com.realtech.socialsurvey.core.entities;

/**
 * Holds the prfile contact details
 */
public class ContactDetailsSettings {

	private String name;
	private String address;
	private String zipcode;
	private String about_me;
	private String title;
	private MailIdSettings mail_ids;
	private ContactNumberSettings contact_numbers;
	private SocialLinksSettings social_links;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	@Override
	public String toString() {
		return "name: " + name + "\t address: " + address + "\t title: " + title + "\t mail_ids: "
				+ (mail_ids != null ? mail_ids.toString() : "null") + "\t contact_numbers: "
				+ (contact_numbers != null ? contact_numbers.toString() : "null") + "\t social_links: "
				+ (social_links != null ? social_links.toString() : "null");
	}

}
