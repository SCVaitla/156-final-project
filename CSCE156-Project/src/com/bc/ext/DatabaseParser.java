package com.bc.ext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.bc.models.Address;
import com.bc.models.Concession;
import com.bc.ConcessionPurchase;
import com.bc.models.Customer;
import com.bc.Invoice;
import com.bc.models.Person;
import com.bc.models.Product;
import com.bc.Purchase;
import com.bc.models.Rental;
import com.bc.RentalPurchase;
import com.bc.models.Repair;
import com.bc.RepairPurchase;
import com.bc.models.Towing;
import com.bc.TowingPurchase;

public class DatabaseParser {
	
	public static ArrayList<Person> parsePersonList(){
		Connection conn = DatabaseInfo.connectToDatabase();
		
		PreparedStatement ps = null;
		ResultSet rs = null;

		ArrayList<Person> personList = new ArrayList<Person>();

		String query = "SELECT p.personCode, p.firstName, p.lastName, a.street, a.city, a.zip, s.name AS state, c.name AS country "
				+ "FROM Person p "
				+ "JOIN Address a ON p.address_id = a.address_id "
				+ "LEFT JOIN State s ON a.state_id = s.state_id "
				+ "JOIN Country c ON a.country_id = c.country_id";

		try {
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();
			
			while (rs.next()) {
				String personCode = rs.getString("personCode");
				String firstName = rs.getString("firstName");
				String lastName = rs.getString("lastName");
				String street = rs.getString("street");
				String city = rs.getString("city");
				String zip = rs.getString("zip");
				String state = rs.getString("state");
				String country = rs.getString("country");
				Address a = new Address(street, city, state, zip, country);
				Person p = new Person(personCode, firstName, lastName, a, null);
				personList.add(p);
			}
			rs.close();
			
		} catch (SQLException e) {
			System.out.println("SQLException: Couldn't select persons");
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		query = "SELECT emailName FROM Email WHERE person_id = "
				+ "(SELECT person_id FROM Person WHERE personCode = ?)";

		for (Person p : personList) {
			String personCode = p.getCode();
			List<String> emailList = new ArrayList<String>();
			try {
				ps = conn.prepareStatement(query);
				ps.setString(1, personCode);
				rs = ps.executeQuery();				
				while (rs.next()) {
					String s = rs.getString("emailName");
					if (s==null) {
						s = "";
					}
					emailList.add(s);
				}
				String[] emails = emailList.toArray(new String[0]);
				p.setEmails(emails);
				rs.close();
				
			} catch (SQLException e) {
				System.out.println("SQLException: Couldn't return emails");
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}

		try {rs.close();} catch (Exception e) {/* ignored */}
	    try {ps.close();} catch (Exception e) {/* ignored */}
	    try {conn.close();} catch (Exception e) {/* ignored */}
		
		return personList;
	}
	
	public static ArrayList<Customer> parseCustomerList(ArrayList<Person> personList){

		Connection conn = DatabaseInfo.connectToDatabase();
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<Customer> customerList = new ArrayList<Customer>();

		String query = "SELECT c.customerCode, c.customerType, c.customerName, p.personCode, a.street, a.city, a.zip, s.name AS state, co.name AS country "
				+ "FROM Customer c "
				+ "JOIN PersonCustomer pc ON c.customer_id = pc.customer_id "
				+ "JOIN Person p ON pc.customer_id = p.person_id "
				+ "JOIN Address a ON c.address_id = a.address_id "
				+ "LEFT JOIN State s ON a.state_id = s.state_id "
				+ "JOIN Country co ON a.country_id = co.country_id";

		try {
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();
			
			while (rs.next()) {
				String customerCode = rs.getString("customerCode");
				String customerType = rs.getString("customerType");
				String customerName = rs.getString("customerName");
				String personCode = rs.getString("personCode");
				String street = rs.getString("street");
				String city = rs.getString("city");
				String zip = rs.getString("zip");
				String state = rs.getString("state");
				String country = rs.getString("country");
				Address a = new Address(street, city, state, zip, country);
				Person primaryContact = null;

				for (Person p : personList) {
					if (p.getCode().equals(personCode)) {
						primaryContact = p;
					}
				}
				Customer c = new Customer(customerCode, customerType, customerName, primaryContact, a);
				customerList.add(c);
			}
			rs.close();
			
		} catch (SQLException e) {
			System.out.println("SQLException: Couldn't select customers");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			try {rs.close();} catch (Exception e) {/* ignored */}
		    try {ps.close();} catch (Exception e) {/* ignored */}
		    try {conn.close();} catch (Exception e) {/* ignored */}
		}
		
		return customerList;
	}
	
	public static ArrayList<Product> parseProductList(){
		Connection conn = DatabaseInfo.connectToDatabase();
		
		PreparedStatement ps = null;
		ResultSet rs = null;

		ArrayList<Product> productList = new ArrayList<Product>();
		String query = "SELECT * FROM Product";
		try {
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();
			
			while (rs.next()) {
				Product p = null;
				String code = rs.getString("productCode");
				String type = rs.getString("productType");
				String label = rs.getString("label");
				if (type.equals("R")) {
					float dailyCost = rs.getFloat("dailyCost");
					float deposit = rs.getFloat("deposit");
					float cleaningFee = rs.getFloat("cleaningFee");
					p = new Rental(code, type, label, dailyCost, deposit, cleaningFee);
				}
				else if (type.equals("F")) {
					float partsCost = rs.getFloat("partsCost");
					float hourlyLaborCost = rs.getFloat("hourlyLaborCost");
					p = new Repair(code, type, label, partsCost, hourlyLaborCost);
				}
				else if (type.equals("C")) {
					float unitCost = rs.getFloat("unitCost");
					p = new Concession(code, type, label, unitCost);
				}
				else if (type.equals("T")) {
					float costPerMile = rs.getFloat("costPerMile");
					p = new Towing(code, type, label, costPerMile);
				}
				productList.add(p);
			}
			rs.close();
			
		} catch (SQLException e) {
			System.out.println("SQLException: Couldn't select products");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			try {rs.close();} catch (Exception e) {/* ignored */}
		    try {ps.close();} catch (Exception e) {/* ignored */}
		    try {conn.close();} catch (Exception e) {/* ignored */}
		}
		
		return productList;
	}
	
	public static ArrayList<Invoice> parseInvoiceList(ArrayList<Person> personList,
			ArrayList<Customer> customerList, ArrayList<Product> productList){
		Connection conn = DatabaseInfo.connectToDatabase();
		
		PreparedStatement ps = null;
		ResultSet rs = null;

		ArrayList<Invoice> invoiceList = new ArrayList<Invoice>();

		String query = "SELECT i.invoiceCode, p.personCode, c.customerCode "
						+ "FROM Invoice i "
						+ "JOIN Person p ON i.person_id = p.person_id "
						+ "JOIN Customer c ON i.customer_id = c.customer_id";

		try {
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();
			
			while (rs.next()) {
				Invoice invoice = null;
				Person person = null;
				Customer customer = null;

				String invoiceCode = rs.getString("invoiceCode");
				String personCode = rs.getString("personCode");
				String customerCode = rs.getString("customerCode");

				for (Person p : personList) {
					if (p.getCode().equals(personCode)) {
						person = p;
						break;
					}
				}

				for (Customer c : customerList) {
					if (c.getCode().equals(customerCode)) {
						customer = c;
						break;
					}
				}
				
				invoice = new Invoice(invoiceCode, person, customer, null);
				invoiceList.add(invoice);
			}
			rs.close();
			
		} catch (SQLException e) {
			System.out.println("SQLException: Couldn't select persons");
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		query = "SELECT pr.productCode, pu.daysRented, pu.hoursWorked, pu.quantity, pu.associatedRepair, pu.milesTowed "
				+ "FROM Purchase pu "
				+ "JOIN Product pr ON pu.product_id = pr.product_id "
				+ "WHERE invoice_id = (SELECT invoice_id FROM Invoice WHERE invoiceCode = ?)";

		for (Invoice inv : invoiceList) {
			String invoiceCode = inv.getInvoiceCode();
			ArrayList<Purchase> purchaseList = new ArrayList<Purchase>();
			try {
				ps = conn.prepareStatement(query);
				ps.setString(1, invoiceCode);
				rs = ps.executeQuery();				
				while (rs.next()) {
					String productCode = rs.getString("productCode");
					for (Product p : productList) {
						if (productCode.equals(p.getCode())) {
							Purchase e = null;
							if (p.getType().equals("R")) {
								float daysRented = rs.getFloat("daysRented");
								e = new RentalPurchase(p, daysRented);
							}
							else if (p.getType().equals("F")) {
								float hoursWorked = rs.getFloat("hoursWorked");
								e = new RepairPurchase(p, hoursWorked);
							}
							else if (p.getType().equals("C")) {
								int quantity = rs.getInt("quantity");
								String associatedRepair = rs.getString("associatedRepair");
								if (associatedRepair==null) {
									associatedRepair = "";
								}
								e = new ConcessionPurchase(p, quantity, associatedRepair);
							}
							else if (p.getType().equals("T")) {
								float milesTowed = rs.getFloat("milesTowed");
								e = new TowingPurchase(p, milesTowed);
							}
							purchaseList.add(e);
						}
					}
				}
				rs.close();
			} catch (SQLException e) {
				System.out.println("SQLException: Couldn't return purchases");
				e.printStackTrace();
				throw new RuntimeException(e);
			}

			inv.setPurchaseList(purchaseList);
		}
		try {rs.close();} catch (Exception e) {/* ignored */}
	    try {ps.close();} catch (Exception e) {/* ignored */}
	    try {conn.close();} catch (Exception e) {/* ignored */}
	    
		return invoiceList;
	}
}
