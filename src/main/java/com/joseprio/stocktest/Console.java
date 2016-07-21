package com.joseprio.stocktest;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import com.joseprio.stocktest.common.Constants;
import com.joseprio.stocktest.common.OperationType;
import com.joseprio.stocktest.model.Stock;
import com.joseprio.stocktest.service.DWHServiceFactory;
import com.joseprio.stocktest.service.TimeProviderFactory;
import com.joseprio.stocktest.service.TradeServiceFactory;

/**
 * Hello world!
 *
 */
public class Console implements Runnable
{
	static enum State {
		INITIAL,
		DIVIDEND_YIELD,
		DIVIDEND_YIELD_PRICE,
		PE_RATIO,
		PE_RATIO_PRICE,
		RECORD_TRADE,
		RECORD_TRADE_TIMESTAMP,
		RECORD_TRADE_OPERATION,
		RECORD_TRADE_QUANTITY,
		RECORD_TRADE_PRICE,
		EXIT
	};
	
	final private static String OPTION_DIVIDEND_YIELD = "1";
	final private static String OPTION_PE_RATIO = "2";
	final private static String OPTION_RECORD_TRADE = "3";
	final private static String OPTION_VOLUME_WEIGHTED = "4";
	final private static String OPTION_SHARE_INDEX = "5";
	final private static String OPTION_EXIT = "6";
	
	private State mCurrentState = State.INITIAL;
	private Scanner mInput;
	private PrintStream mOutput;
	
	// Temporary data holders
	private String mCurrentTicker;
	private double mCurrentPrice;
	private long mCurrentTimestamp;
	private OperationType mCurrentOperation;
	private long mCurrentQuantity;
	
	public Console(InputStream in, PrintStream out) {
		mInput = new Scanner(in);
		mOutput = out;
	}
	
	private void showStateMessage() {
		String message = "";
		
		switch (mCurrentState) {
		case INITIAL:
			message = "------------------------------------\n"
				+ "1. Calculate dividend yield\n"
				+ "2. Calculate P/E ratio\n"
				+ "3. Record trade\n"
				+ "4. Calculate Volume Weighted Stock Price\n"
				+ "5. Calculate GBCE All Share Index\n"
				+ "6. Exit\n"
				+ "Choose an option: "
				;
			break;
		case DIVIDEND_YIELD:
		case PE_RATIO:
		case RECORD_TRADE:
			message = "Ticker? ";
			break;
		case DIVIDEND_YIELD_PRICE:
		case PE_RATIO_PRICE:
		case RECORD_TRADE_PRICE:
			message = "Price? ";
			break;
		case RECORD_TRADE_OPERATION:
			message = "Operation (Buy/Sell)? ";
			break;
		case RECORD_TRADE_QUANTITY:
			message = "Quantity? ";
			break;
		case RECORD_TRADE_TIMESTAMP:
			message = "Timestamp (leave empty for current)? ";
			break;
		case EXIT:
			message = "Bye!!";
			break;
		}
		
		printMessage(message);
	}
	
	private void printMessage(String message) {
		mOutput.print(message);
	}
	
	private void printMessageLine(String message) {
		printMessage(message + Constants.LINE_SEPARATOR);
	}
	
	private String readInput() {
		String nextLine = mInput.nextLine();
		// Remove any unnecessary spaces
		nextLine = nextLine.trim();
		
		return nextLine;
	}
	
	
	private void process(String input) {
		switch (mCurrentState) {
		case INITIAL:
			if (input.equals(OPTION_DIVIDEND_YIELD)) {
				mCurrentState = State.DIVIDEND_YIELD;
			} else if (input.equals(OPTION_PE_RATIO)) {
				mCurrentState = State.PE_RATIO;
			} else if (input.equals(OPTION_RECORD_TRADE)) {
				mCurrentState = State.RECORD_TRADE;
			} else if (input.equals(OPTION_SHARE_INDEX)) {
				printMessageLine("The GBCE All Share Index is: " + calculateShareIndex());
			} else if (input.equals(OPTION_VOLUME_WEIGHTED)) {
				printMessageLine("The Volume Weighted Stock Price is: " + calculateVolumeWeighted());
			} else if (input.equals(OPTION_EXIT)) {
				mCurrentState = State.EXIT;
			} else {
				printMessageLine("Unknown option");
			}
			break;
		case DIVIDEND_YIELD:
			if (isTickerValid(input)) {
				mCurrentTicker = input;
				mCurrentState = State.DIVIDEND_YIELD_PRICE;
			} else {
				printMessageLine("Ticker incorrect");
			}
			break;
		case PE_RATIO:
			if (isTickerValid(input)) {
				mCurrentTicker = input;
				mCurrentState = State.PE_RATIO_PRICE;
			} else {
				printMessageLine("Ticker incorrect");
			}
			break;
		case DIVIDEND_YIELD_PRICE:
			try {
				mCurrentPrice = Double.parseDouble(input);
				printMessageLine("Dividend yield: " + calculateDividendYield());
				mCurrentState = State.INITIAL;
			} catch (NumberFormatException nfex) {
				printMessageLine("Incorrect price format");
			}
			break;
		case PE_RATIO_PRICE:
			try {
				mCurrentPrice = Double.parseDouble(input);
				printMessageLine("P/E Ratio: " + calculatePERatio());
				mCurrentState = State.INITIAL;
			} catch (NumberFormatException nfex) {
				printMessageLine("Incorrect price format");
			}
			break;
		case RECORD_TRADE:
			if (isTickerValid(input)) {
				mCurrentTicker = input;
				mCurrentState = State.RECORD_TRADE_OPERATION;
			} else {
				printMessageLine("Ticker incorrect");
			}
			break;
		case RECORD_TRADE_OPERATION:
			try {
				mCurrentOperation = OperationType.valueOf(input.toUpperCase());
				mCurrentState = State.RECORD_TRADE_TIMESTAMP;
			} catch (IllegalArgumentException iaex) {
				printMessageLine("Invalid operation");
			}
			break;
		case RECORD_TRADE_TIMESTAMP:
			try {
				if (input.length() > 0) {
					mCurrentTimestamp = Long.parseLong(input);
				} else {
					mCurrentTimestamp = TimeProviderFactory.getInstance().currentTimeMillis();
				}
				mCurrentState = State.RECORD_TRADE_PRICE;
			} catch (NumberFormatException nfex) {
				printMessageLine("Wrong number");
			}
			break;
		case RECORD_TRADE_PRICE:
			try {
				mCurrentPrice = Double.parseDouble(input);
				mCurrentState = State.RECORD_TRADE_QUANTITY;
			} catch (NumberFormatException nfex) {
				printMessageLine("Wrong number");
			}
			break;
		case RECORD_TRADE_QUANTITY:
			try {
				mCurrentQuantity = Long.parseLong(input);
				recordTrade();
				mCurrentState = State.INITIAL;
			} catch (NumberFormatException nfex) {
				printMessageLine("Wrong number");
			}
			break;
		case EXIT:
			break;
		}
	}
	
	private boolean isTickerValid(String ticker) {
		if (ticker == null) {
			return false;
		}
		return (Stock.byTicker(ticker) != null);
	}
	
	private void recordTrade() {
		TradeServiceFactory.getInstance().recordTrade(mCurrentTicker, mCurrentTimestamp, mCurrentQuantity, mCurrentOperation, mCurrentPrice);
		
	}

	private double calculateVolumeWeighted() {
		return DWHServiceFactory.getInstance().calculateVolumeWeighted();
	}

	private double calculateShareIndex() {
		return DWHServiceFactory.getInstance().calculateShareIndex();
	}

	private double calculatePERatio() {
		Stock target = Stock.byTicker(mCurrentTicker);
		return target.calculatePERatio(mCurrentPrice);
	}

	private double calculateDividendYield() {
		Stock target = Stock.byTicker(mCurrentTicker);
		
		return target.calculateDividendYield(mCurrentPrice);
	}

	public void run() {
		do {
			showStateMessage();
			process(readInput());
			
		} while (mCurrentState != State.EXIT);
		
		// Show exit message
		showStateMessage();
	}
	
	
    public static void main( String[] args )
    {
        Console console = new Console(System.in, System.out);
        console.run();
    }
}
