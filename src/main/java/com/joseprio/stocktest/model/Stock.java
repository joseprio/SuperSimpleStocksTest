package com.joseprio.stocktest.model;

import java.util.HashMap;
import java.util.Map;

import com.joseprio.stocktest.common.Constants;
import com.joseprio.stocktest.common.StockException;

/**
 * @author user
 *
 */
public abstract class Stock {
	private String mTicker;
	private double mLastDividend;
	private double mParValue;
	
	@SuppressWarnings("unused")
	private Stock() {
		
	}
	
	Stock(String ticker) {
		mTicker = ticker;
	}
	
	/**
	 * @return
	 */
	public String getTicker() {
		return mTicker;
	}
	
	/**
	 * @return
	 */
	public double getLastDividend() {
		return mLastDividend;
	}
	
	/**
	 * @param lastDividend
	 */
	public void setLastDividend(double lastDividend) {
		mLastDividend = lastDividend;
	}
	
	/**
	 * @return
	 */
	public double getParValue() {
		return mParValue;
	}
	
	/**
	 * @param parValue
	 */
	public void setParValue(double parValue) {
		mParValue = parValue;
	}
	
	/**
	 * Calculates the P/E ratio for a given market price
	 * @param marketPrice the market price for this ticker
	 * @return the calculated P/E ratio
	 */
	public double calculatePERatio(double marketPrice) {
		// P/E Ratio = Market Price / Dividend
		return marketPrice / mLastDividend;
	}

	/**
	 * Calculates the dividend yield given a market price
	 * @param marketPrice the market price for this ticker
	 * @return the calculated dividend yield
	 */
	public abstract double calculateDividendYield(double marketPrice);
	
	// Persistance layer simulation
	private static Map<String, Stock> stocks = new HashMap<String, Stock>();
	
	static {
		// Hardcoding values
		// Real implementation would recover them from the persistance layer
		try {
			CommonStock.register("TEA", 0.0, 100.0);
			CommonStock.register("POP", 8.0, 100);
			CommonStock.register("ALE", 23.0, 60.0);
			PreferredStock.register("GIN", 8.0, 2.0, 100.0);
			CommonStock.register("JOE", 13.0, 250.0);
		} catch (StockException stex) {
			// We don't expect this to happen here
			stex.printStackTrace();
		}
	}
	
	/**
	 * Obtains the stock instance with the given ticker value
	 * @param ticker
	 * @return
	 */
	public static Stock byTicker(String ticker) {
		return stocks.get(ticker);
	}
	
	/**
	 * Returns an array with all registered stocks
	 * @return an array with the registered stocks
	 */
	public static Stock[] all() {
		return stocks.values().toArray(new Stock[0]);
	}
	
	// Package private, to be used internally
	static void register(Stock newStock) throws StockException {
		// Validate that the ticker is valid
		String newTicker = newStock.getTicker();
		
		if (newTicker == null) {
			throw new StockException("Ticker is null");
		}
		
		// Validate ticker format
		if (!newTicker.matches(Constants.TICKER_REGEX)) {
			throw new StockException("Ticker format is incorrect");
		}
		
		// Simple value validations
		if (newStock.getLastDividend() < 0) {
			throw new StockException("Last dividend cannot be below 0");
		}
		if (newStock.getParValue() < 0) {
			throw new StockException("Par value cannot be below 0");
		}
		
		// TODO: add business logic validations
			
		// If we already have the same ticker, throw exception
		if (stocks.containsKey(newTicker)) {
			throw new StockException("Ticker already exists");
		}
		
		// The stock has been validated, register it
		stocks.put(newTicker, newStock);
	}
}
