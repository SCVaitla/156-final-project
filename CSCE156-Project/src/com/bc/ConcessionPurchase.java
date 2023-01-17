package com.bc;

import com.bc.models.Concession;
import com.bc.models.Product;

public class ConcessionPurchase extends Purchase {

	private int quantity;
	private String assocRepair;
	
	public int getQuantity() {
		return this.quantity;
	}
	public String getAssocRepair() {
		return this.assocRepair;
	}

	public ConcessionPurchase(Product product, int quantity, String assocRepair) {
		super(product);
		this.quantity = quantity;
		this.assocRepair = assocRepair;
	}

	public boolean getAssocRepairDiscount() {
		boolean discount = false;
		if (this.assocRepair.length() > 0) {
			discount = true;
		}
		return discount;
	}
	
	protected float getPurchaseCost() {
		float subtotal = ((Concession)this.getProduct()).getCost() * this.quantity;
		return subtotal;
	}
}
