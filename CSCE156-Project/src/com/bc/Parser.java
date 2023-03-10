package com.bc;

import com.bc.models.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Parser {

	public static ArrayList<Person> parsePersonsList(){
		String fileName = "data/Persons.dat";
		Scanner s = null;
		try {
			s = new Scanner (new File(fileName));
		} catch (FileNotFoundException fnfe) {
			throw new RuntimeException(fnfe);
		}
		
		ArrayList<Person> personsList = new ArrayList<Person>();
		s.nextLine();
		while (s.hasNext()) {
			String line = s.nextLine();
			if (!line.trim().isEmpty()) {
				Person p = null;
				String tokens[] = line.split(";");
				String code = tokens[0];
				String name = tokens[1];
				String address = tokens[2];
				String emails[] = null;
				if (tokens.length == 4) {
					emails = tokens[3].split(",");
				}
				else {
					emails = new String[1]; 
					emails[0] = "";
				}
				
				String nameTokens[] = name.split(",");
				String firstName = nameTokens[1];
				String lastName = nameTokens[0];
				
				String addressTokens[] = address.split(",");
				String street = addressTokens[0];
				String city = addressTokens[1];
				String state = addressTokens[2];
				String zip = addressTokens[3];
				String country = addressTokens[4];
				
				Address a = new Address(street, city, state, zip, country);
				
				p = new Person(code, firstName, lastName, a, emails);
				
				personsList.add(p);
			}
		}
		
		s.close();
		
		return personsList;
	}
	
	public static ArrayList<Customer> parseCustomerList(ArrayList<Person> personList) {
		String fileName = "data/Customers.dat";
		Scanner s = null;
		try {
			s = new Scanner(new File(fileName));
		} 
		catch (FileNotFoundException fnfe) {
			throw new RuntimeException(fnfe);
		}
		
		ArrayList<Customer> customerList = new ArrayList<Customer>();
		s.nextLine();

		while (s.hasNext()) {
			String line = s.nextLine();
			if(!line.trim().isEmpty()) {
				Customer c = null;
				String tokens[] = line.split(";");
				String code = tokens[0];
				String type = tokens[1];
				String name = tokens[2];
				String contactCode = tokens[3];
				String address = tokens[4];
				
				Person primaryContact = null;
				for (Person p : personList) {
					if (p.getCode().equals(contactCode)) {
						primaryContact = p;
					}
				}
				
				String addressTokens[] = address.split(",");
				String street = addressTokens[0];
				String city = addressTokens[1];
				String state = addressTokens[2];
				String zip = addressTokens[3];
				String country = addressTokens[4];
				Address a = new Address(street, city, state, zip, country);
				
				c = new Customer(code, type, name, primaryContact, a);
				
				customerList.add(c);
			}
			
		}
		s.close();
		
		return customerList;
	 }
	
	public static ArrayList<Product> parseProductsList() {
		String fileName = "data/Products.dat";
		Scanner s = null;
		try {
			s = new Scanner(new File(fileName));
		} 
		catch (FileNotFoundException fnfe) {
			throw new RuntimeException(fnfe);
		}
		
		ArrayList<Product> productsList = new ArrayList<Product>();
		s.nextLine();
		while (s.hasNext()) {
			String line = s.nextLine();
			if(!line.trim().isEmpty()) {
				Product p = null;
				String tokens[] = line.split(";");
				String code = tokens[0];
				String type = tokens[1];
				String label = tokens[2];
				
				if (type.equals("R")) {
					float dailyCost = Float.parseFloat(tokens[3]);
					float deposit = Float.parseFloat(tokens[4]);
					float cleaningFee = Float.parseFloat(tokens[5]);
					p = new Rental(code, type, label, dailyCost, deposit, cleaningFee);
				}
				else if (type.equals("F")) {
					float partsCost = Float.parseFloat(tokens[3]);
					float laborRate = Float.parseFloat(tokens[4]);
					p = new Repair(code, type, label, partsCost, laborRate);
				}
				else if (type.equals("C")) {
					float cost = Float.parseFloat(tokens[3]);
					p = new Concession(code, type, label, cost);
				}
				else if (type.equals("T")) {
					float costPerMile = Float.parseFloat(tokens[3]);
					p = new Towing(code, type, label, costPerMile);
				}
				
				productsList.add(p);
			}
			
		}
		s.close();
		
		return productsList;
	}
	
	public static ArrayList<Invoice> parseInvoiceList(ArrayList<Person> personList,
			ArrayList<Customer> customerList, ArrayList<Product> productsList) {
		String fileName = "data/Invoices.dat";
		Scanner s = null;
		try {
			s = new Scanner (new File(fileName));
		} catch (FileNotFoundException fnfe) {
			throw new RuntimeException(fnfe);
		}
		
		ArrayList<Invoice> invoiceList = new ArrayList<Invoice>();
		s.nextLine();
		while (s.hasNext()) {
			String line = s.nextLine();
			if (!line.trim().isEmpty()) {
				Invoice invoice = null;
				Person person = null;
				Customer customer = null;
				String tokens[] = line.split(";");
				String invoiceCode = tokens[0];

				for (Person p : personList) {
					if (tokens[1].equals(p.getCode())) {
						person = p;
					}
				}

				for (Customer c : customerList) {
					if (tokens[2].equals(c.getCode())) {
						customer = c;
					}
				}
				
				String purchTokens[] = tokens[3].split(",");

				ArrayList<Purchase> purchaseList = new ArrayList<Purchase>();
				
				for (int i=0; i<purchTokens.length; i++) {
					
					String purchSubTokens[] = purchTokens[i].split(":");
					
					for (Product p : productsList) {
						if (purchSubTokens[0].equals(p.getCode())) {
							Purchase e = null;
							if (p.getType().equals("R")) {
								int daysRented = Integer.parseInt(purchSubTokens[1]);
								e = new RentalPurchase(p, daysRented);
							}
							else if (p.getType().equals("F")) {
								float hoursWorked = Float.parseFloat(purchSubTokens[1]);
								e = new RepairPurchase(p, hoursWorked);
							}
							else if (p.getType().equals("C")) {
								int quantity = Integer.parseInt(purchSubTokens[1]);
								String assocRepair = "";
								if (purchSubTokens.length == 3) {
									assocRepair = purchSubTokens[2];
								}
								e = new ConcessionPurchase(p, quantity, assocRepair);
							}
							else if (p.getType().equals("T")) {
								float milesTowed = Float.parseFloat(purchSubTokens[1]);
								e = new TowingPurchase(p, milesTowed);
							}
							
							purchaseList.add(e);
						}
					}
				}
				
				invoice = new Invoice(invoiceCode, person, customer, purchaseList);
				invoiceList.add(invoice);
			}
		}
		
		s.close();
		return invoiceList;
	}
}
