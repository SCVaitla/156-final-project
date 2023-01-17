package com.bc.ext;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class InvoiceData {

	public static void removeAllPersons() {
		emptyTable("Email");

		emptyTable("Person");

		emptyTable("Purchase");

		emptyTable("Invoice");

		emptyTable("Person");
	}

	public static void addPerson(String personCode, String firstName, String lastName, String street, String city, String state, String zip, String country) {

		try {
			if (personCode==null || personCode.trim().isEmpty() || firstName==null || firstName.trim().isEmpty()
					|| lastName==null || lastName.trim().isEmpty()) {
				throw new Exception();
			}
		} catch (Exception e) {
			System.out.println("RuntimeException: Missing values in one or more needed fields");
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		int personID = getID("Person", personCode);
		if (personID != 0) {
			return;
		}

		int addressID = getAddressID(street, city, zip, state, country);
		if (addressID == 0) {
			addressID = addAddress(street, city, zip, state, country);
		}

		Connection conn = DatabaseInfo.connectToDatabase();
		
		PreparedStatement ps = null;

		String query = "INSERT INTO Person (personCode, firstName, lastName, address_id) "
				+ "VALUES (?, ?, ?, ?)";

		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, personCode);
			ps.setString(2, firstName);
			ps.setString(3, lastName);
			ps.setInt(4, addressID);
			ps.executeUpdate();
		} catch (SQLException e) {
			System.out.println("SQLException: Couldn't create new person record");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
		    try {ps.close();} catch (Exception e) {/* ignored */}
		    try {conn.close();} catch (Exception e) {/* ignored */}
		}
	}

	public static void addEmail(String personCode, String email) {
		try {
			if (personCode==null || personCode.trim().isEmpty() || email==null || email.trim().isEmpty()) {
				throw new Exception();
			}
		} catch (Exception e) {
			System.out.println("RuntimeException: Missing values in one or more needed fields");
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		int personID = getID("Person", personCode);
		if (personID == 0) {
			return;
		}

		Connection conn = DatabaseInfo.connectToDatabase();
		
		PreparedStatement ps = null;
		String query = "INSERT INTO Email (emailName, person_id) values (?, ?)";
		
		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, email);
			ps.setInt(2, personID);
			ps.executeUpdate();
		} catch (SQLException e) {
			System.out.println("SQLException: Couldn't add new email record");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
		    try {ps.close();} catch (Exception e) {/* ignored */}
		    try {conn.close();} catch (Exception e) {/* ignored */}
		}
	}

	public static void removeAllCusomters() {
		emptyTable("Person");

		emptyTable("Purchase");

		emptyTable("Invoice");

		emptyTable("Customer");
	}

	public static void addCustomer(String customerCode, String customerType, String primaryContactPersonCode, String name, String street, String city, String state, String zip, String country) {

		try {
			if (customerCode==null || customerCode.trim().isEmpty() || customerType==null || customerType.trim().isEmpty()
					|| primaryContactPersonCode==null || primaryContactPersonCode.trim().isEmpty() || name==null || name.trim().isEmpty()) {
				throw new Exception();
			}
		} catch (Exception e) {
			System.out.println("RuntimeException: Missing values in one or more needed fields");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		if (!customerType.equals("B") && !customerType.equals("P")) {
			return;
		}

		int customerID = getID("Customer", customerCode);
		if (customerID != 0) {
			return;
		}

		int personID = getID("Person", primaryContactPersonCode);
		if (personID == 0) {
			return;
		}
		

		int addressID = getAddressID(street, city, zip, state, country);
		if (addressID == 0) {
			addressID = addAddress(street, city, zip, state, country);
		}

		Connection conn = DatabaseInfo.connectToDatabase();

		String query = "INSERT INTO Customer (customerCode, customerName, customerType, address_id) "
				+ "VALUES (?, ?, ?, ?)";
		
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, customerCode);
			ps.setString(2, name);
			ps.setString(3, customerType);
			ps.setInt(4, addressID);
			ps.executeUpdate();
			rs = ps.getGeneratedKeys();
			rs.next();
			customerID = rs.getInt(1);
			rs.close();
		} catch (SQLException e) {
			System.out.println("SQLException: Couldn't create new customer record");
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		query = "INSERT INTO PersonCustomer (person_id, customer_id) VALUES (?, ?)";

		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, personID);
			ps.setInt(2, customerID);
			ps.executeUpdate();
		} catch (SQLException e) {
			System.out.println("SQLException: Couldn't associate person and customer");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			try {rs.close();} catch (Exception e) {/* ignored */}
		    try {ps.close();} catch (Exception e) {/* ignored */}
		    try {conn.close();} catch (Exception e) {/* ignored */}
		}
	}
	public static void removeAllProducts() {
		emptyTable("Purchase");

		emptyTable("Product");
	}

	public static void addConcession(String productCode, String productLabel, double unitCost) {

		try {
			if (productCode==null || productCode.trim().isEmpty() || productLabel==null || productLabel.trim().isEmpty()) {
				throw new Exception();
			}
		} catch (Exception e) {
			System.out.println("RuntimeException: Missing values in one or more field(s)");
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		int productID = getID("Invoice", productCode);
		if (productID != 0) {
			return;
		}

		Connection conn = DatabaseInfo.connectToDatabase();
		
		PreparedStatement ps = null;

		String query = "INSERT INTO Product (productCode, productType, label, unitCost) " +
					   "VALUES (?, \"C\", ?, ?)";
		
		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, productCode);
			ps.setString(2, productLabel);
			ps.setFloat(3, (float)unitCost);
			ps.executeUpdate();
		} catch (SQLException e) {
			System.out.println("SQLException: Couldn't create new concession");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
		    try {ps.close();} catch (Exception e) {/* ignored */}
		    try {conn.close();} catch (Exception e) {/* ignored */}
		}
	}

//
	public static void addRepair(String productCode, String productLabel, double partsCost, double laborRate) {

		try {
			if (productCode==null || productCode.trim().isEmpty() || productLabel==null || productLabel.trim().isEmpty()) {
				throw new Exception();
			}
		} catch (Exception e) {
			System.out.println("RuntimeException: Missing values in one or more field(s)");
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		int productID = getID("Invoice", productCode);
		if (productID != 0) {
			return;
		}

		Connection conn = DatabaseInfo.connectToDatabase();
		
		PreparedStatement ps = null;

		String query = "INSERT INTO Product (productCode, productType, label, partsCost, hourlyLaborCost) " +
					   "VALUES (?, \"F\", ?, ?, ?)";
				
		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, productCode);
			ps.setString(2, productLabel);
			ps.setFloat(3, (float)partsCost);
			ps.setFloat(4, (float)laborRate);
			ps.executeUpdate();
		} catch (SQLException e) {
			System.out.println("SQLException: Couldn't create new repair");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
		    try {ps.close();} catch (Exception e) {/* ignored */}
		    try {conn.close();} catch (Exception e) {/* ignored */}
		}
	}

	public static void addTowing(String productCode, String productLabel, double costPerMile) {
		try {
			if (productCode==null || productCode.trim().isEmpty() || productLabel==null || productLabel.trim().isEmpty()) {
				throw new Exception();
			}
		} catch (Exception e) {
			System.out.println("RuntimeException: Missing values in one or more field(s)");
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		int productID = getID("Invoice", productCode);
		if (productID != 0) {
			return;
		}

		Connection conn = DatabaseInfo.connectToDatabase();
		
		PreparedStatement ps = null;

		String query = "INSERT INTO Product (productCode, productType, label, costPerMile) " +
					   "VALUES (?, \"T\", ?, ?)";
		
		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, productCode);
			ps.setString(2, productLabel);
			ps.setFloat(3, (float)costPerMile);
			ps.executeUpdate();
		} catch (SQLException e) {
			System.out.println("SQLException: Couldn't create new repair");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
		    try {ps.close();} catch (Exception e) {/* ignored */}
		    try {conn.close();} catch (Exception e) {/* ignored */}
		}
	}

	public static void addRental(String productCode, String productLabel, double dailyCost, double deposit, double cleaningFee) {
		try {
			if (productCode==null || productCode.trim().isEmpty() || productLabel==null || productLabel.trim().isEmpty()) {
				throw new Exception();
			}
		} catch (Exception e) {
			System.out.println("RuntimeException: Missing values in one or more field(s)");
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		int productID = getID("Invoice", productCode);
		if (productID != 0) {
			return;
		}

		Connection conn = DatabaseInfo.connectToDatabase();
		
		PreparedStatement ps = null;

		String query = "INSERT INTO Product (productCode, productType, label, dailyCost, deposit, cleaningFee) " +
					   "VALUES (?, \"R\", ?, ?, ?, ?)";
		
		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, productCode);
			ps.setString(2, productLabel);
			ps.setFloat(3, (float)dailyCost);
			ps.setFloat(4, (float)deposit);
			ps.setFloat(5, (float)cleaningFee);
			ps.executeUpdate();
		} catch (SQLException e) {
			System.out.println("SQLException: Couldn't create new repair");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
		    try {ps.close();} catch (Exception e) {/* ignored */}
		    try {conn.close();} catch (Exception e) {/* ignored */}
		}
	}

	public static void removeAllInvoices() {
		emptyTable("Purchase");

		emptyTable("Invoice");
	}

	public static void addInvoice(String invoiceCode, String ownerCode, String customerCode) {

		try {
			if (invoiceCode==null || invoiceCode.trim().isEmpty() || ownerCode==null || ownerCode.trim().isEmpty()
					|| customerCode==null || customerCode.trim().isEmpty()) {
				throw new Exception();
			}
		} catch (Exception e) {
			System.out.println("RuntimeException: Missing values in one or more field(s)");
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		int invoiceID = getID("Invoice", invoiceCode);
		if (invoiceID != 0) {
			return;
		}

		int personID = getID("Person", ownerCode);
		if (personID == 0) {
			return;
		}

		int customerID = getID("Customer", customerCode);
		if (customerID == 0) {
			return;
		}

		Connection conn = DatabaseInfo.connectToDatabase();
    	
		PreparedStatement ps = null;

		String query = "INSERT INTO Invoice (invoiceCode, person_id, customer_id) VALUES " +
					   "(?, ?, ?)";
				
		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, invoiceCode);
			ps.setInt(2, personID);
			ps.setInt(3, customerID);
			ps.executeUpdate();
		} catch (SQLException e) {
			System.out.println("SQLException: Couldn't create new invoice");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
		    try {ps.close();} catch (Exception e) {/* ignored */}
		    try {conn.close();} catch (Exception e) {/* ignored */}
		}
	}

	public static void addTowingToInvoice(String invoiceCode, String productCode, double milesTowed) {
		// A method that adds a towing purchase to an invoice
		
		//Check whether String arguments are empty, throw exception if so
		try {
			if (invoiceCode==null || invoiceCode.trim().isEmpty() || productCode==null || productCode.trim().isEmpty()) {
				throw new Exception();
			}
		} catch (Exception e) {
			System.out.println("RuntimeException: Missing values in one or more field(s)");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		// Make sure that both an invoice with this invoiceCode and a product with this productCode exist
		int invoiceID = getID("Invoice", invoiceCode);
		int productID = getID("Product", productCode);
		if (invoiceID == 0 || productID == 0) {
			// If the invoice or the product do not exist, exit the function
			return;
		}
		
		// Connect to database using connectToDatabase method in DatabaseInfo.java
		Connection conn = DatabaseInfo.connectToDatabase();
		
		PreparedStatement ps = null;
				
		// Create a towing object in the Purchase table and link it to the provided invoiceCode
		String query = "INSERT INTO Purchase (invoice_id, product_id, milesTowed) VALUES " +
					   "((select invoice_id from Invoice where invoiceCode = ?), (select product_id from Product where productCode = ?), ?)";
		
		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, invoiceCode);
			ps.setString(2, productCode);
			ps.setFloat(3, (float)milesTowed);
			ps.executeUpdate();
		} catch (SQLException e) {
			System.out.println("SQLException: Couldn't add towing purchase to invoice " + invoiceCode);
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
		    try {ps.close();} catch (Exception e) {/* ignored */}
		    try {conn.close();} catch (Exception e) {/* ignored */}
		}
	}

	public static void addRepairToInvoice(String invoiceCode, String productCode, double hoursWorked) {
		// A method that adds a repair purchase to an invoice
		
		//Check whether String arguments are empty, throw exception if so
		try {
			if (invoiceCode==null || invoiceCode.trim().isEmpty() || productCode==null || productCode.trim().isEmpty()) {
				throw new Exception();
			}
		} catch (Exception e) {
			System.out.println("RuntimeException: Missing values in one or more field(s)");
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		// Make sure that both an invoice with this invoiceCode and a product with this productCode exist
		int invoiceID = getID("Invoice", invoiceCode);
		int productID = getID("Product", productCode);
		if (invoiceID == 0 || productID == 0) {
			// If the invoice or the product do not exist, exit the function
			return;
		}
		
		// Connect to database using connectToDatabase method in DatabaseInfo.java
		Connection conn = DatabaseInfo.connectToDatabase();
		
    	PreparedStatement ps = null;
		    	
		// Create a repair object in the Purchase table and link it to the provided invoiceCode
		String query = "INSERT INTO Purchase (invoice_id, product_id, hoursWorked) VALUES " +
					   "((select invoice_id from Invoice where invoiceCode = ?), (select product_id from Product where productCode = ?), ?)";
				
		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, invoiceCode);
			ps.setString(2, productCode);
			ps.setFloat(3, (float)hoursWorked);
			ps.executeUpdate();
		} catch (SQLException e) {
			System.out.println("SQLException: Couldn't add repair purchase to invoice " + invoiceCode);
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
		    try {ps.close();} catch (Exception e) {/* ignored */}
		    try {conn.close();} catch (Exception e) {/* ignored */}
		}
	}

    public static void addConcessionToInvoice(String invoiceCode, String productCode, int quantity, String repairCode) {

		try {
			if (invoiceCode==null || invoiceCode.trim().isEmpty() || productCode==null || productCode.trim().isEmpty()) {
				throw new Exception();
			}
		} catch (Exception e) {
			System.out.println("RuntimeException: Missing values in one or more field(s)");
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		int invoiceID = getID("Invoice", invoiceCode);
		int productID = getID("Product", productCode);
		if (invoiceID == 0 || productID == 0) {
			return;
		}

		int repairID = 0;
		if (repairCode != null && !repairCode.trim().isEmpty()) {
			repairID = getID("Product", repairCode);
			if (repairID == 0) {
				return;
			}
		}
		Connection conn = DatabaseInfo.connectToDatabase();
    	
		PreparedStatement ps = null;
		String query = null;
		if (repairID != 0) {
			query = "INSERT INTO Purchase (invoice_id, product_id, quantity, associatedRepair) VALUES "
					+ "((select invoice_id from Invoice where invoiceCode = ?), ?, ?, ?)";
		}
		else {
			query = "INSERT INTO Purchase (invoice_id, product_id, quantity) VALUES "
					+ "((select invoice_id from Invoice where invoiceCode = ?), ?, ?)";
		}
				
		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, invoiceCode);
			ps.setInt(2, productID);
			ps.setInt(3, quantity);
			if (repairID != 0) {
				ps.setInt(4, repairID);
			}
			ps.executeUpdate();
		} catch (SQLException e) {
			System.out.println("SQLException: Couldn't add concession purchase to invoice " + invoiceCode);
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
		    try {ps.close();} catch (Exception e) {/* ignored */}
		    try {conn.close();} catch (Exception e) {/* ignored */}
		}
    }

    public static void addRentalToInvoice(String invoiceCode, String productCode, double daysRented) {
    	// A method that adds a rental purchase to an invoice
    	
    	//Check whether String arguments are empty, throw exception if so
		try {
			if (invoiceCode==null || invoiceCode.trim().isEmpty() || productCode==null || productCode.trim().isEmpty()) {
				throw new Exception();
			}
		} catch (Exception e) {
			System.out.println("RuntimeException: Missing values in one or more field(s)");
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		// Make sure that both an invoice with this invoiceCode and a product with this productCode exist
		int invoiceID = getID("Invoice", invoiceCode);
		int productID = getID("Product", productCode);
		if (invoiceID == 0 || productID == 0) {
			// If the invoice or the product do not exist, exit the function
			return;
		}
		
		// Connect to database using connectToDatabase method in DatabaseInfo.java
		Connection conn = DatabaseInfo.connectToDatabase();
		
    	PreparedStatement ps = null;
		
		// Create a rental object in the Purchase table and link it to the provided invoiceCode
		String query = "INSERT INTO Purchase (invoice_id, product_id, daysRented) VALUES " +
					   "((select invoice_id from Invoice where invoiceCode = ?), (select product_id from Product where productCode = ?), ?)";
		
		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, invoiceCode);
			ps.setString(2, productCode);
			ps.setFloat(3, (float)daysRented);
			ps.executeUpdate();
		} catch (SQLException e) {
			System.out.println("SQLException: Couldn't add rental purchase to invoice " + invoiceCode);
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
		    try {ps.close();} catch (Exception e) {/* ignored */}
		    try {conn.close();} catch (Exception e) {/* ignored */}
		}
    }
    
    public static void emptyTable(String table) {
		// A generic method that removes all records from the specified table 
    	// NOTE: does not DROP the table, only empties it of all records
		
		// Connect to database using connectToDatabase method in DatabaseInfo.java
		Connection conn = DatabaseInfo.connectToDatabase();
		
		PreparedStatement ps = null;
		String query = null;
		
		// Set the query according to which table is being emptied
    	if (table.equals("Person")) {
    		query = "DELETE FROM Person";
    	}
    	else if (table.equals("Customer")) {
    		query = "DELETE FROM Customer";
    	}
    	else if (table.equals("Invoice")) {
    		query = "DELETE FROM Invoice";
    	}
    	else if (table.equals("Product")) {
    		query = "DELETE FROM Product";
    	}
    	else if (table.equals("Purchase")) {
    		query = "DELETE FROM Purchase";
    	}
    	else if (table.equals("Email")) {
    		query = "DELETE FROM Email";
    	}
    	else if (table.equals("Person")) {
    		query = "DELETE FROM Person";
    	}
		
		// Execute query for specified table
		try {
			ps = conn.prepareStatement(query);
			ps.executeUpdate();
		} catch (SQLException e) {
			System.out.println("SQLException: Couldn't remove table " + table);
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
		    try {ps.close();} catch (Exception e) {/* ignored */}
		    try {conn.close();} catch (Exception e) {/* ignored */}
		}
	}
    
    public static int getID(String table, String code) {
    	Connection conn = DatabaseInfo.connectToDatabase();
    	PreparedStatement ps = null;
		ResultSet rs = null;
    	String query = null;
    	String idToReturn = null;

    	if (table.equals("Person")) {
    		query = "SELECT person_id FROM Person WHERE personCode = ?";
    		idToReturn = "person_id";
    	}
    	else if (table.equals("Customer")) {
    		query = "SELECT customer_id FROM Customer WHERE customerCode = ?";
    		idToReturn = "customer_id";
    	}
    	else if (table.equals("Invoice")) {
    		query = "SELECT invoice_id FROM Invoice WHERE invoiceCode = ?";
    		idToReturn = "invoice_id";
    	}
    	else if (table.equals("Product")) {
    		query = "SELECT product_id FROM Product WHERE productCode = ?";
    		idToReturn = "product_id";
    	}
    	else if (table.equals("State")) {
    		query = "SELECT state_id FROM State WHERE name = ?";
    		idToReturn = "state_id";
    	}
    	else if (table.equals("Country")) {
    		query = "SELECT country_id FROM Country WHERE name = ?";
    		idToReturn = "country_id";
    	}
    	
    	int returnedID = 0;

		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, code);
			rs = ps.executeQuery();
			if(rs.next()) {
				returnedID = rs.getInt(idToReturn);
			}
			rs.close();
		} catch (SQLException e) {
			System.out.println("SQLException: could not search for code " + code + " in table " + table);
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			try {rs.close();} catch (Exception e) {/* ignored */}
		    try {ps.close();} catch (Exception e) {/* ignored */}
		    try {conn.close();} catch (Exception e) {/* ignored */}
		}
		
		return returnedID;
    }
    
    public static int getAddressID (String street, String city, String zip, String state, String country) {
    	Connection conn = DatabaseInfo.connectToDatabase();
    	
    	PreparedStatement ps = null;
		ResultSet rs = null;
		int addressID = 0;
		String query = "SELECT address_id FROM Address WHERE street = ? AND city = ? AND zip = ? "
						+ "AND state_id = (SELECT state_id FROM State WHERE name = ?) "
						+ "AND country_id = (SELECT country_id FROM Country WHERE name = ?)";

    	try {
			ps = conn.prepareStatement(query);
			ps.setString(1, street);
			ps.setString(2, city);
			ps.setString(3, zip);
			ps.setString(4, state);
			ps.setString(5, country);
			rs = ps.executeQuery();
			if(rs.next()) {
				addressID = rs.getInt("address_id");
			}
			rs.close();
		} catch (SQLException e) {
			System.out.println("SQLException: could not search for address");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			try {rs.close();} catch (Exception e) {/* ignored */}
		    try {ps.close();} catch (Exception e) {/* ignored */}
		    try {conn.close();} catch (Exception e) {/* ignored */}
		}
    	
    	return addressID;
    }
    
    public static int addAddress (String street, String city, String zip, String state, String country) {
    	
    	int addressID = 0;

    	Connection conn = DatabaseInfo.connectToDatabase();
    	
    	PreparedStatement ps = null;
		ResultSet rs = null;
		String query = null;

		if (!state.trim().isEmpty()) {
	    	if (getID("State", state) == 0) {
	    		query = "INSERT INTO State (name) VALUES (?)";
	    		try {
	    			ps = conn.prepareStatement(query);
	    			ps.setString(1, state);
	    			ps.executeUpdate();
	    		} catch (SQLException e) {
	    			System.out.println("SQLException: could not create new state " + state);
	    			e.printStackTrace();
	    			throw new RuntimeException(e);
	    		}
	    	}
		}

	    if (!country.trim().isEmpty()) {
			if (getID("Country", country) == 0) {
	    		query = "INSERT INTO Country (name) VALUES (?)";
	    		try {
	    			ps = conn.prepareStatement(query);
	    			ps.setString(1, country);
	    			ps.executeUpdate();
	    		} catch (SQLException e) {
	    			System.out.println("SQLException: could not create new country " + country);
	    			e.printStackTrace();
	    			throw new RuntimeException(e);
	    		}
	    	}
	    }

    	if (state.trim().isEmpty() && country.trim().isEmpty()) {
    		query = "INSERT INTO Address (street, city, zip, country_id, address_id) VALUES "
    				+ "(?, ?, ?, null, null)";
    	}
    	else if (state.trim().isEmpty()){
    		query = "INSERT INTO Address (street, city, zip, country_id, address_id) VALUES "
    				+ "(?, ?, ?, (SELECT country_id FROM Country WHERE name = ?), null)";
    	}
    	else if (country.trim().isEmpty()) {
    		query = "INSERT INTO Address (street, city, zip, country_id, address_id) VALUES "
    				+ "(?, ?, ?, null, (SELECT address_id FROM State WHERE name = ?))";
    	}
    	else {
    		query = "INSERT INTO Address (street, city, zip, country_id, address_id) VALUES "
    				+ "(?, ?, ?, (SELECT country_id FROM Country WHERE name = ?), (SELECT address_id FROM State WHERE name = ?))";
    	}
		
    	try {
			ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, street);
			ps.setString(2, city);
			ps.setString(3, zip);
			if (!state.trim().isEmpty() && !country.trim().isEmpty()) {
				ps.setString(4, country);
				ps.setString(5, state);
			}
			else if (!state.trim().isEmpty()) {
				ps.setString(4, state);
			}
			else if (!country.trim().isEmpty()) {
				ps.setString(4, country);
			}
			ps.executeUpdate();
			rs = ps.getGeneratedKeys();
			rs.next();
			addressID = rs.getInt(1);
			rs.close();
		} catch (SQLException e) {
			System.out.println("SQLException: Couldn't create new address");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			try {rs.close();} catch (Exception e) {/* ignored */}
		    try {ps.close();} catch (Exception e) {/* ignored */}
		    try {conn.close();} catch (Exception e) {/* ignored */}
		}
    	
    	return addressID;
    }
    
}
