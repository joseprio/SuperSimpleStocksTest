package com.joseprio.stocktest.service.impl;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.joseprio.stocktest.model.Stock;
import com.joseprio.stocktest.model.TradeRecord;
import com.joseprio.stocktest.service.DWHService;
import com.joseprio.stocktest.service.TimeProviderFactory;

/**
 * Implementation of DWHService
 */
public class DWHServiceImpl implements DWHService {
	final private static long VOLUME_CACHE_EXPIRY_MILLIS = 15 * 60 * 1000; // 15 minutes

	private static ConcurrentLinkedQueue<VolumeRecord> volumeCache = new ConcurrentLinkedQueue<VolumeRecord>();
	private static ConcurrentHashMap<String,StockPriceAverage> priceAverages = new ConcurrentHashMap<String,StockPriceAverage>();

	static {
		// Initialize stock price averages with existing tickers
		for (Stock s : Stock.all()) {
			priceAverages.put(s.getTicker(), new StockPriceAverage());
		}
	}
	
	private ExecutorService executor = Executors.newCachedThreadPool();
	

	/* (non-Javadoc)
	 * @see com.joseprio.stocktest.service.DWHService#notifyTrade(com.joseprio.stocktest.model.TradeRecord)
	 */
	public void notifyTrade(TradeRecord record) {
		// Run DWH functionality asynchronously so we don't hold the trade
		executor.submit(() -> {
			keepVolumeCache(record);
			trackStockPrice(record);
		});
	}
	
	/**
	 * Get the executor service for this service implementation
	 * @return the executor service
	 */
	ExecutorService getExecutorService() {
		return executor;
	}

	/**
	 * Refresh the executor service if it has been shutdown
	 */
	void restartExecutorService() {
		if (executor.isShutdown()) {
			executor = Executors.newCachedThreadPool();
		}
	}

	/* (non-Javadoc)
	 * @see com.joseprio.stocktest.service.DWHService#calculateVolumeWeighted()
	 */
	public double calculateVolumeWeighted() {
		long expiredTimestamp = TimeProviderFactory.getInstance().currentTimeMillis()
									- VOLUME_CACHE_EXPIRY_MILLIS;

		// Use reduce to aggregate the information
		VolumeRecord aggr = volumeCache
			.stream()
			.filter(r -> r.getInformedTimestamp() > expiredTimestamp)
			.reduce(new VolumeRecord(0, 0.0), (v1, v2) -> {
				v1.setQuantity(v1.getQuantity() + v2.getQuantity());
				v1.setVolume(v1.getVolume() + v2.getVolume());
				return v1;
			});
		
		return aggr.getVolume() / aggr.getQuantity();
	}

	public double calculateShareIndex() {
		// Calculate geometric mean using logarithms in order to
		// avoid over/underflows
		StockPriceAverage[] values = priceAverages
								.values()
								.stream()
								.filter(a -> a.getAmount() > 0)
								.toArray(size -> new StockPriceAverage[size]);

		double logSum = Arrays.stream(values)
							.mapToDouble(a -> Math.log(a.getAverage()))
							.sum();
		
		return Math.exp(logSum / values.length);
	}
	
	private void keepVolumeCache(TradeRecord record) {
		volumeCache.add(VolumeRecord.fromTradeRecord(record));

		// Remove older than 15 minutes
		// TODO: Do it randomly or at regular intervals, as it's locking
		purgeVolumeCache();
	}
	
	private synchronized void purgeVolumeCache() {
		boolean foundExpired;
		long expiredTimestamp = TimeProviderFactory.getInstance().currentTimeMillis()
									- VOLUME_CACHE_EXPIRY_MILLIS;
		
		do {
			foundExpired = false;
			
			VolumeRecord oldest = volumeCache.peek();
			if (oldest.getProcessTimestamp() < expiredTimestamp) {
				// Head has expired
				volumeCache.poll();
				foundExpired = true;
			}
		} while (foundExpired);
	}
	
	private void trackStockPrice(TradeRecord record) {
		StockPriceAverage spa = priceAverages.get(record.getStock().getTicker());
		
		if (spa == null) {
			// For some reason it's not initialized... do it now
			spa = new StockPriceAverage();
			priceAverages.put(record.getStock().getTicker(), spa);
		}
		
		// Add current record
		spa.addRecord(record);
	}
}

class StockPriceAverage {
	private double mTotal = 0.0;
	private long mAmount = 0;
	
	/**
	 * Add trade record data to the average calculation
	 * @param record the trade record to add
	 */
	public synchronized void addRecord(TradeRecord record) {
		mTotal += record.getPricePerShare() * record.getQuantity();
		mAmount += record.getQuantity();
	}
	
	/**
	 * Get the calculated average
	 * @return the average
	 */
	public double getAverage() {
		return mTotal / mAmount;
	}
	
	/**
	 * Get the total traded shares
	 * @return the total shares
	 */
	public long getAmount() {
		return mAmount;
	}
}

class VolumeRecord {
	private long mQuantity;
	private double mVolume;
	private long mProcessTimestamp;
	private long mInformedTimestamp;
	
	public VolumeRecord(long quantity, double volume) {
		mQuantity = quantity;
		mVolume = volume;
		mProcessTimestamp = TimeProviderFactory.getInstance().currentTimeMillis();
	}
	
	public long getQuantity() {
		return mQuantity;
	}
	
	public void setQuantity(long quantity) {
		mQuantity = quantity;
	}
	
	public double getVolume() {
		return mVolume;
	}
	
	public void setVolume(double volume) {
		mVolume = volume;
	}
	
	public long getProcessTimestamp() {
		return mProcessTimestamp;
	}
	
	public long getInformedTimestamp() {
		return mInformedTimestamp;
	}
	
	public void setInformedTimestamp(long informedTimestamp) {
		mInformedTimestamp = informedTimestamp;
	}
	
	public static VolumeRecord fromTradeRecord(TradeRecord tr) {
		VolumeRecord record = new VolumeRecord(
								tr.getQuantity(),
								tr.getPricePerShare() * tr.getQuantity());
		record.setInformedTimestamp(tr.getTimestamp());
		
		return record;
	}
}
