package com.joseprio.stocktest.service.impl;

import com.joseprio.stocktest.service.TimeProvider;

/**
 * Time provider implementation
 */
public class TimeProviderImpl implements TimeProvider {

	/* (non-Javadoc)
	 * @see com.joseprio.stocktest.service.TimeProvider#currentTimeMillis()
	 */
	public long currentTimeMillis() {
		return System.currentTimeMillis();
	}

}
