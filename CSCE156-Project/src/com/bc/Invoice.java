package com.bc;

import com.bc.models.Customer;
import com.bc.models.Person;

import java.util.ArrayList;
import java.util.Comparator;


public class Invoice implements Comparator<Invoice> {
	
	private String invoiceCode;
	private Person person;
	private Customer customer;
	private ArrayList<Purchase> purchaseList;
	
	public String getInvoiceCode() {
		return this.invoiceCode;
	}
	public Person getPerson() {
		return this.person;
	}
	public Customer getCustomer() {
		return this.customer;
	}
	public ArrayList<Purchase> getPurchaseList() {
		return this.purchaseList;
	}
	
	public void setPurchaseList(ArrayList<Purchase> purchaseList) {
		this.purchaseList = purchaseList;
	}
	
	public Invoice(String invoiceCode, Person person, Customer customer, ArrayList<Purchase> purchaseList) {
		this.invoiceCode = invoiceCode;
		this.person = person;
		this.customer = customer;
		this.purchaseList = purchaseList;
	}

	public float calculateSubTotal() {
		
		float subtotal = 0;
		for (Purchase purch : this.purchaseList) {
			if (purch.getProduct().getType().equals("R")) {
				subtotal += purch.getPurchaseCost();
			}
			else if (purch.getProduct().getType().equals("F")) {
				subtotal += purch.getPurchaseCost();
			}
			else if (purch.getProduct().getType().equals("C")) {
				subtotal += purch.getPurchaseCost();
			}
			else if (purch.getProduct().getType().equals("T")) {
				subtotal += purch.getPurchaseCost();
			}
		}
		
		return subtotal;
	}
	
	public float calculatePreTaxDiscounts() {
		float indItemDiscount = 0;
		int towingFlag=0, repairFlag=0, rentalFlag=0;
		for (Purchase purch : this.purchaseList) {
			
			if (purch.getProduct().getType().equals("T")) {
				towingFlag = 1;
			}
			if (purch.getProduct().getType().equals("R")) {
				repairFlag = 1;
			}
			if (purch.getProduct().getType().equals("F")) {
				rentalFlag = 1;
			}
		}
		int freeTowing = towingFlag + repairFlag + rentalFlag;
		if (freeTowing == 3) {
			for (Purchase purch : this.purchaseList) {
				if (purch.getProduct().getType().equals("T")) {
					indItemDiscount += ((TowingPurchase)purch).getPurchaseCost();
				}
			}
		}

		for (Purchase purch : this.purchaseList) {
			if (purch.getProduct().getType().equals("C")) {
				if (((ConcessionPurchase)purch).getAssocRepairDiscount()) {
					indItemDiscount += ((ConcessionPurchase)purch).getPurchaseCost()*0.1;
				}
			}
		}
		
		return indItemDiscount;
	}
	
	public float calculateTax() {
		float tax = 0;
		
		if (this.customer.getType().equals("B")) {
			tax += (this.calculateSubTotal() - this.calculatePreTaxDiscounts()) * .0425;
		}
		else {
			tax += (this.calculateSubTotal() - this.calculatePreTaxDiscounts()) * .08;
		}
		
		return tax;
	}
	
	public float calculateFees() {
		float fees = 0;
		
		if (this.customer.getType().equals("B")) {
			fees = (float)75.5;
		}
		
		return fees;
	}
	
	public float calculatePostTaxDiscounts() {
		float invoiceDiscount = 0;
		if (this.customer.getType().equals("P")
				&& this.customer.getPrimaryContact().getEmails().length >= 2) {
			invoiceDiscount += (this.calculateSubTotal() - this.calculatePreTaxDiscounts()
					+ this.calculateTax() + this.calculateFees()) * 0.05;
		}
		return invoiceDiscount;
	}
	
	public float calculateTotalCost() {
		float totalCost = this.calculateSubTotal() - this.calculatePreTaxDiscounts()
				- this.calculatePostTaxDiscounts() + this.calculateFees() + this.calculateTax();
		
		return totalCost;
	}
	
	@Override
	public int compare(Invoice inv1, Invoice inv2) {
		if (inv1.calculateTotalCost() > inv2.calculateTotalCost()) {
			return 1;
		}
		else if (inv1.calculateTotalCost() < inv2.calculateTotalCost()) {
			return -1;
		}
		else {
			return 0;
		}
	}
	
}
