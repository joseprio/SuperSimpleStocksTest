package com.joseprio.stocktest.service;

import com.joseprio.stocktest.common.OperationType;

/**
 * This service handles trading operations
 */
public interface TradeService {
	/**
	 * This method registers a trade in the trade service
	 * @param ticker the ticker of the traded stock
	 * @param timestamp the timestamp of the transaction
	 * @param quantity the quantity of stocks operated
	 * @param type the operation type (buy/sell)
	 * @param pricePerShare the price per share
	 */
	public void recordTrade(
			String ticker, 
			long timestamp, 
			long quantity, 
			OperationType type, 
			double pricePerShare);
	
}
