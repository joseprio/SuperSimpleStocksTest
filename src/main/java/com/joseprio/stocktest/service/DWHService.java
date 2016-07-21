package com.joseprio.stocktest.service;

import com.joseprio.stocktest.model.TradeRecord;

public interface DWHService {
	public void notifyTrade(TradeRecord record);
	public double calculateVolumeWeighted(); 
	public double calculateShareIndex(); 
}
