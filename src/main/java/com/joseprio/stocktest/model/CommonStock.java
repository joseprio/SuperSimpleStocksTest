package com.joseprio.stocktest.model;

import com.joseprio.stocktest.common.StockException;

/**
 * This class represents a common stock ticker
 */
public class CommonStock extends Stock {
	CommonStock(String ticker) {
		super(ticker);
	}

	/* (non-Javadoc)
	 * @see com.joseprio.stocktest.model.Stock#calculateDividendYield(double)
	 */
	@Override
	public double calculateDividendYield(double marketPrice) {
		// Dividend Yield for common stock = Last Dividend / Market Price
		return getLastDividend() / marketPrice;
	}
	
	// Static factory
	public static CommonStock register(String ticker,
											double lastDividend,
											double parValue) throws StockException {
		// TODO:add validations for the stock values that follow business
		// rules for common stocks
		
		// Instance it
		CommonStock newStock = new CommonStock(ticker);
		newStock.setLastDividend(lastDividend);
		newStock.setParValue(parValue);
		
		// Validate and register in the main stock registry
		Stock.register(newStock);
		
		return newStock;
	}
}
