package com.bc;


import com.bc.models.Product;

public abstract class Purchase {
	private Product product;
	public Product getProduct() {
		return this.product;
	}
	public Purchase(Product product) {
		this.product = product;
	}
	protected abstract float getPurchaseCost();
}
