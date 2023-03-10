package com.bc.models;


import com.bc.models.Product;

public class Rental extends Product {
	
	private float dailyCost;
	private float deposit;
	private float cleaningFee;
	
	public float getDailyCost() {
		return dailyCost;
	}
	public float getDeposit() {
		return deposit;
	}
	public float getCleaningFee() {
		return cleaningFee;
	}

	public Rental(String code, String type, String label, float dailyCost, float deposit, 
			float cleaningFee) {
		super(code, type, label);
		this.dailyCost = dailyCost;
		this.deposit = deposit;
		this.cleaningFee = cleaningFee;
	}
}
