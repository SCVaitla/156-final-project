package com.bc.models;

import com.bc.models.Product;

public class Towing extends Product {
	
	private float costPerMile;

	public float getCostPerMile() {
		return costPerMile;
	}
	
	public Towing(String code, String type, String label, float costPerMile) {
		super(code, type, label);
		this.costPerMile = costPerMile;
	}
}
