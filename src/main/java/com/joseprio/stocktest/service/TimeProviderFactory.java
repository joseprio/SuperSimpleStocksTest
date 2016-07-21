package com.joseprio.stocktest.service;

import com.joseprio.stocktest.service.impl.TimeProviderImpl;

/**
 * This factory provides time provider instances
 */
public class TimeProviderFactory {
	private static TimeProvider instance = null;
	
	private TimeProviderFactory() {
		
	}
	
	/**
	 * Gets the time provider instance
	 * @return the time provider instance
	 */
	public static TimeProvider getInstance() {
		if (instance == null) {
			instance = new TimeProviderImpl();
		}
		
		return instance;
	}
	
	/**
	 * Sets a time provider (useful for unit testing)
	 * @param provider the new time provider
	 */
	public static void setInstance(TimeProvider provider) {
		instance = provider;
	}
}
