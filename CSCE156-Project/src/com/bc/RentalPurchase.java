package com.bc;


import com.bc.models.Product;
import com.bc.models.Rental;

public class RentalPurchase extends Purchase {
	private float daysRented;
	public float getDaysRented() {
		return this.daysRented;
	}
	public RentalPurchase(Product product, float daysRented) {
		super(product);
		this.daysRented = daysRented;
	}
	protected float getPurchaseCost() {
		float subtotal = 0;
		
		float daysCost = this.daysRented * ((Rental)this.getProduct()).getDailyCost();
		float deposit = ((Rental)this.getProduct()).getDeposit();
		float cleaningFee = ((Rental)this.getProduct()).getCleaningFee();
		
		subtotal = daysCost - deposit + cleaningFee;
		return subtotal;
	}
	
	
}
