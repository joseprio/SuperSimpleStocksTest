package com.joseprio.stocktest.service.impl;

import com.joseprio.stocktest.common.OperationType;
import com.joseprio.stocktest.model.Stock;
import com.joseprio.stocktest.model.TradeRecord;
import com.joseprio.stocktest.service.DWHServiceFactory;
import com.joseprio.stocktest.service.TradeService;

/**
 * Implementation of the TradeService interface
 */
public class TradeServiceImpl implements TradeService {

	/* (non-Javadoc)
	 * @see com.joseprio.stocktest.service.TradeService#recordTrade(java.lang.String, long, long, com.joseprio.stocktest.common.OperationType, double)
	 */
	public void recordTrade(String ticker,
							long timestamp,
							long quantity,
							OperationType type,
							double pricePerShare) {
		TradeRecord.Builder builder = new TradeRecord.Builder();
		builder.timestamp(timestamp)
			.stock(Stock.byTicker(ticker))
			.quantity(quantity)
			.price(pricePerShare)
			.type(type);
		TradeRecord record = builder.make();

		// TODO: Do all necessary validations and notifications
		
		// Store in persistance layer
		TradeRecord.store(record);
		
		// Notify the DWH layer
		DWHServiceFactory.getInstance().notifyTrade(record);
		
	}

}
