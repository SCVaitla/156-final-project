package com.bc;


import com.bc.models.Product;
import com.bc.models.Towing;

public class TowingPurchase extends Purchase {

	private float milesTowed;
	
	public float getMilesTowed() {
		return this.milesTowed;
	}

	public TowingPurchase(Product product, float milesTowed) {
		super(product);
		this.milesTowed = milesTowed;
	}

	protected float getPurchaseCost() {
		float subtotal = this.milesTowed * ((Towing)this.getProduct()).getCostPerMile();
		return subtotal;
	}
}
