package com.joseprio.stocktest.service.impl;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.joseprio.stocktest.common.OperationType;
import com.joseprio.stocktest.model.Stock;
import com.joseprio.stocktest.model.TradeRecord;
import com.joseprio.stocktest.service.DWHServiceFactory;
import com.joseprio.stocktest.service.TimeProvider;
import com.joseprio.stocktest.service.TimeProviderFactory;

import static org.junit.Assert.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class DWHServiceImplTest {
	private static TimeProvider oldTimeService;
	private static long currentTime = System.currentTimeMillis();
	private static DWHServiceImpl instance;
	
	final static public long getTime() {
		return currentTime;
	}

	@BeforeClass
	public static void prepare() throws Exception {
		oldTimeService = TimeProviderFactory.getInstance();
		TimeProviderFactory.setInstance(new TimeProvider() {
			@Override
			public long currentTimeMillis() {
				return getTime();
			}
		});
		
		instance = (DWHServiceImpl)DWHServiceFactory.getInstance();
		
		// Fill in some data
		TradeRecord.Builder builder = new TradeRecord.Builder();
		builder.timestamp(currentTime - 60 * 10 * 1000)
				.stock(Stock.byTicker("TEA"))
				.type(OperationType.BUY)
				.quantity(3)
				.price(2.0);
		instance.notifyTrade(builder.make());
		
		builder = new TradeRecord.Builder();
		builder.timestamp(currentTime - 8000)
				.stock(Stock.byTicker("POP"))
				.type(OperationType.SELL)
				.quantity(1)
				.price(4.5);
		instance.notifyTrade(builder.make());
		
		builder = new TradeRecord.Builder();
		builder.timestamp(currentTime - 7000)
				.stock(Stock.byTicker("ALE"))
				.type(OperationType.BUY)
				.quantity(4)
				.price(3.0);
		instance.notifyTrade(builder.make());
		
		// Wait for the execution to end
		ExecutorService executorService = instance.getExecutorService();
		
		executorService.shutdown();

		try {
			instance.getExecutorService().awaitTermination(100, TimeUnit.SECONDS);
		} catch (InterruptedException iex) {
			iex.printStackTrace();
		}
	}

	@AfterClass
	public static void restore() throws Exception {
		TimeProviderFactory.setInstance(oldTimeService);
		instance.restartExecutorService();
	}
	
	@Test
	public void testCalculateShareIndex() {
		assertEquals(3.0, instance.calculateShareIndex(), 0.00001);
	}
	
	@Test
	public void testCalculateVolumeWeighted() {
		assertEquals(2.8125, instance.calculateVolumeWeighted(), 0.0);
		currentTime += 60 * 10 * 1000;
		assertEquals(3.3, instance.calculateVolumeWeighted(), 0.0);
		
	}
	

}
