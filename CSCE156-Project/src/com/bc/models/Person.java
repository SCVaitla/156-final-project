package com.bc.models;


import com.bc.models.Address;

public class Person {
	
	private String code;
	private String firstName;
	private String lastName;
	private Address address;
	private String[] emails;
	
	public String getCode() {
		return code;
	}
	public String getFirstName() {
		return firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public Address getAddress() {
		return address;
	}
	public String[] getEmails() {
		return emails;
	}
	
	public void setEmails(String[] emails) {
		this.emails = emails;
	}
	
	public Person(String code, String firstName, String lastName, Address address, String[] emails) {
		this.code = code;
		this.firstName = firstName;
		this.lastName = lastName;
		this.address = address;
		this.emails = emails;
	}
	
	public String getLastFirstName() {
		String name = this.lastName + ", " + this.firstName;
		return name;
	}
	
	public String printEmails () {
		String emails = "";
		int check = 0;
		for (String s : this.emails) {
			if (check == 0) {
				emails += s;
			}
			else {
				emails += ", " + s;
			}
			check++;
		}
		return emails;
	}
}