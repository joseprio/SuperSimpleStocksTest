package com.joseprio.stocktest.common;

/**
 * Simple class to manage the stock system exceptions
 */
public class StockException extends Exception {
	private static final long serialVersionUID = 1L;

	public StockException(String message) {
		super(message);
	}
}
