package com.joseprio.stocktest.service;

import com.joseprio.stocktest.service.impl.TradeServiceImpl;

/**
 * Factory for the trade service
 */
public class TradeServiceFactory {
	private static TradeService singleInstance = null;
	
	private TradeServiceFactory() {
		
	}
	
	/**
	 * Get an instance of the trade service
	 * @return the trade service instance
	 */
	public static TradeService getInstance() {
		if (singleInstance == null) {
			singleInstance = new TradeServiceImpl();
		}
		
		return singleInstance;
	}
}
