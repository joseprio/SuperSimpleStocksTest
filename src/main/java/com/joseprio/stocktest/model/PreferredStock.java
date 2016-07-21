package com.joseprio.stocktest.model;

import com.joseprio.stocktest.common.StockException;

public class PreferredStock extends Stock {
	private double mFixedDividend;
	
	/**
	 * This class represents a preferred stock ticker
	 */
	private PreferredStock(String ticker) {
		super(ticker);
	}

	/**
	 * Gets the defined fixed dividend value
	 * @return the current fixed dividend value
	 */
	public double getFixedDividend() {
		return mFixedDividend;
	}

	/**
	 * Sets the fixed dividend value
	 * @param fixedDividend the fixed dividend value to set
	 */
	public void setFixedDividend(double fixedDividend) {
		mFixedDividend = fixedDividend;
	}

	/* (non-Javadoc)
	 * @see com.joseprio.stocktest.model.Stock#calculateDividendYield(double)
	 */
	@Override
	public double calculateDividendYield(double marketPrice) {
		// Dividend Yield for preferred stock =
		//			Fixed Dividend * Par Value / Market Price
		return getFixedDividend() * getParValue() / marketPrice;
	}
	
	// Static factory
	public static PreferredStock register(String ticker,
											double lastDividend,
											double fixedDividend,
											double parValue) throws StockException {
		// Simple value validation
		if (fixedDividend < 0) {
			throw new StockException("Fixed dividend cannot be below 0");
		}
		
		// TODO:add validations for the stock values that follow business
		// rules for preferred stocks
		
		// Instance it
		PreferredStock newStock = new PreferredStock(ticker);
		newStock.setLastDividend(lastDividend);
		newStock.setFixedDividend(fixedDividend);
		newStock.setParValue(parValue);
		
		// Validate and register in the main stock registry
		Stock.register(newStock);
		
		return newStock;
	}
}
