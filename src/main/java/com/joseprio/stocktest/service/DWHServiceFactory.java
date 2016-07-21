package com.joseprio.stocktest.service;

import com.joseprio.stocktest.service.impl.DWHServiceImpl;

/**
 * The data warehouse service factory
 */
public class DWHServiceFactory {
	private static DWHService singleInstance = null;
	
	private DWHServiceFactory() {
		
	}
	
	/**
	 * Get an instance of the DWH service
	 * @return the DWH service instance
	 */
	public static DWHService getInstance() {
		if (singleInstance == null) {
			singleInstance = new DWHServiceImpl();
		}
		
		return singleInstance;
	}
}
