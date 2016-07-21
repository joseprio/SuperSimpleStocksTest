package com.joseprio.stocktest.common;

public class Constants {
	/**
	 * The allowed format of a ticker
	 */
	final public static String TICKER_REGEX = "^[A-Z]{3}$";
	/**
	 * The system-dependent line separator
	 */
	final public static String LINE_SEPARATOR = System.getProperty("line.separator");
}
