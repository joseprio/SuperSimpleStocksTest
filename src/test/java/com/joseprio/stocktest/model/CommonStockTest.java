package com.joseprio.stocktest.model;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import com.joseprio.stocktest.common.StockException;

public class CommonStockTest {
	@BeforeClass
	public static void setUpBeforeClass() {
		try {
			CommonStock.register("TST", 54.0, 100.0);
		} catch (StockException stex) {
			fail("Unexpected exception");
		}
	}

	@Test
	public void testCalculateDividendYield() {
		assertEquals(0.07714285714285714, Stock.byTicker("TST").calculateDividendYield(700.0), 0.0);
	}


	@Test
	public void testCalculatePERatio() {
		assertEquals(12.037037037037036, Stock.byTicker("TST").calculatePERatio(650.0), 0.0);
	}

}
