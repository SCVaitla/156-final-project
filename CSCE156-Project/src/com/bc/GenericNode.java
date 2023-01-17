package com.bc;


public class GenericNode <T> {
	
	private GenericNode<T> next;
	private T item;
	
	public GenericNode(T item) {
		this.item = item;
		this.next = null;
	}
	
	public T getItem() {
        return item;
    }
	
	public GenericNode<?> getNext() {
		return next;
	}

    public void setNext(GenericNode<T> next) {
        this.next = next;
    }
    
    public boolean hasNext() {
    	return this.next!=null;
    }
}
