package com.joseprio.stocktest.model;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.joseprio.stocktest.common.OperationType;
import com.joseprio.stocktest.service.TimeProviderFactory;;

/**
 * This class represents a trade record
 */
public class TradeRecord {
	private long mTimestamp;
	private Stock mStock;
	private OperationType mType;
	private long mQuantity;
	private double mPricePerShare;
	
	private TradeRecord() {
		
	}
	
	/**
	 * Obtain the timestamp
	 * @return the timestamp that was informed
	 */
	public long getTimestamp() {
		return mTimestamp;
	}
	
	/**
	 * Set the informed timestamp
	 * @param informedTimestamp the value to set as informed timestamp
	 */
	public void setTimestamp(long timestamp) {
		mTimestamp = timestamp;
	}
	
	/**
	 * Obtain the stock this record refers to
	 * @return the stock
	 */
	public Stock getStock() {
		return mStock;
	}
	
	/**
	 * Set the stock for this record
	 * @param stock the stock to set
	 */
	public void setStock(Stock stock) {
		mStock = stock;
	}
	
	/**
	 * Obtain the type for this record
	 * @return the type for this record
	 */
	public OperationType getType() {
		return mType;
	}
	
	/**
	 * Sets the operation type for this record
	 * @param type the operation type to set
	 */
	public void setType(OperationType type) {
		mType = type;
	}
	
	/**
	 * Obtai the quantity of shares for this record
	 * @return the quantity of shares
	 */
	public long getQuantity() {
		return mQuantity;
	}
	
	/**
	 * Set the quantity of shares of this record
	 * @param quantity the quantity to set
	 */
	public void setQuantity(long quantity) {
		mQuantity = quantity;
	}
	
	/**
	 * Obtains the price per share for this record
	 * @return the price per share
	 */
	public double getPricePerShare() {
		return mPricePerShare;
	}
	
	/**
	 * Sets the price per share of this record
	 * @param pricePerShare the price to set
	 */
	public void setPricePerShare(double pricePerShare) {
		mPricePerShare = pricePerShare;
	}
	
	/**
	 * Builder utility to create instance of this class
	 *
	 */
	public static class Builder {
		private long mTimestamp = -1;
		private Stock mStock = null;
		private OperationType mType = null;
		private long mQuantity = -1;
		private double mPricePerShare = -1.0;
		
		/**
		 * Initialized the builder
		 */
		public Builder() {
			
		}
		
		/**
		 * Sets the timestamp
		 * @param timestamp
		 * @return the builder instance for chaining
		 */
		public Builder timestamp(long timestamp) {
			if (timestamp <= 0) {
				throw new IllegalArgumentException("The timestamp has to be positive");
			}
			
			if (timestamp > TimeProviderFactory.getInstance().currentTimeMillis()) {
				throw new IllegalArgumentException("The timestamp cannot be set in the future");
			}
			
			mTimestamp = timestamp;
			
			return this;
		}
		
		/**
		 * Sets the stock
		 * @param stock the stock to use for the new instance
		 * @return the builder instance for chaining
		 */
		public Builder stock(Stock stock) {
			if (stock == null) {
				throw new IllegalArgumentException("Stock cannot be null");
			}
			mStock = stock;
			
			return this;
		}
		
		/**
		 * Sets the type for the new instance
		 * @param type the operation type to set (buy/sell)
		 * @return the builder instance for chaining
		 */
		public Builder type(OperationType type) {
			if (type == null) {
				throw new IllegalArgumentException("Type cannot be null");
			}
			mType = type;
			
			return this;
		}
		
		/**
		 * Sets the quantity for the new instance
		 * @param quantity the quantity for the new trade record
		 * @return the builder instance for chaining
		 */
		public Builder quantity(long quantity) {
			if (quantity <= 0) {
				throw new IllegalArgumentException("Quantity has to be positive");
			}
			mQuantity = quantity;
			
			return this;
		}
		
		/**
		 * The price to be set in the new record
		 * @param pricePerShare the price per share
		 * @return the builder instance for chaining
		 */
		public Builder price(double pricePerShare) {
			if (pricePerShare <= 0.0) {
				throw new IllegalArgumentException("Price has to be positive");
			}
			mPricePerShare = pricePerShare;
			
			return this;
		}
		
		public TradeRecord make() {
			// Validate fields
			if (mTimestamp <= 0) {
				throw new IllegalStateException("Missing timestamp");
			}
			
			if (mStock == null) {
				throw new IllegalStateException("Missing stock");
			}
			
			if (mType == null) {
				throw new IllegalStateException("Missing type");
			}
			
			if (mQuantity <= 0) {
				throw new IllegalStateException("Missing quantity");
			}
			
			if (mPricePerShare <= 0) {
				throw new IllegalStateException("Missing price");
			}
			
			// We have everything, proceed
			TradeRecord newRecord = new TradeRecord();
			newRecord.setTimestamp(mTimestamp);
			newRecord.setStock(mStock);
			newRecord.setType(mType);
			newRecord.setQuantity(mQuantity);
			newRecord.setPricePerShare(mPricePerShare);
			
			return newRecord;
		}
	}
	
	// Persistance layer simulation
	// We are asked to record a trade, so it will be stored here
	// Using a concurrent queue to allow multiple additions simultaneously
	private static ConcurrentLinkedQueue<TradeRecord> records = new ConcurrentLinkedQueue<TradeRecord>();
	
	/**
	 * Store a record in the persistance layer
	 * @param record the record to store
	 */
	public static void store(TradeRecord record) {
		records.add(record);
	}
	
}
