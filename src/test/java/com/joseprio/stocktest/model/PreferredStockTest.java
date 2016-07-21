package com.joseprio.stocktest.model;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import com.joseprio.stocktest.common.StockException;

public class PreferredStockTest {
	@BeforeClass
	public static void setUpBeforeClass() {
		try {
			PreferredStock.register("PRT", 24.0, 3.0, 90.0);
		} catch (StockException stex) {
			fail("Unexpected exception");
		}
	}

	@Test
	public void testCalculateDividendYield() {
		assertEquals(0.38571428571428573, Stock.byTicker("PRT").calculateDividendYield(700.0), 0.0);
	}


	@Test
	public void testCalculatePERatio() {
		assertEquals(27.08333333, Stock.byTicker("PRT").calculatePERatio(650.0), 0.00001);
	}

}
