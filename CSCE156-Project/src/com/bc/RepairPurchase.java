package com.bc;


import com.bc.models.Product;
import com.bc.models.Repair;

public class RepairPurchase extends Purchase {

	private float hoursWorked;
	
	public float getHoursWorked() {
		return this.hoursWorked;
	}

	public RepairPurchase(Product product, float hoursWorked) {
		super(product);
		this.hoursWorked = hoursWorked;
	}
	
	protected float getPurchaseCost() {
		float subtotal = 0;
		
		float partsCost = ((Repair)this.getProduct()).getPartsCost();
		float laborCost = this.hoursWorked * ((Repair)this.getProduct()).getLaborRate();
		
		subtotal = partsCost + laborCost;
		return subtotal;
	}
}
