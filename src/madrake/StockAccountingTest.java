package madrake;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.Iterator;

import org.joda.time.Instant;
import org.junit.Test;

public class StockAccountingTest {

  @Test
  public void testCantAcquireTwice() {
    StockAccounting accounting = new StockAccounting();
    accounting.acquire(new Acquire(new MattsInstant(new Instant(1)), new Price(3), new StockId(1)));
    try {
      accounting.acquire(new Acquire(new MattsInstant(new Instant(2)), new Price(4), new StockId(1)));
      fail("Can't acquire the same stock twice");
    } catch (IllegalArgumentException expected) { }
  }
  
  @Test
  public void testCantSellTwice() {
    StockAccounting accounting = new StockAccounting();
    accounting.sell(new Sale(new MattsInstant(new Instant(1)), new Price(3), new StockId(1)));
    try {
      accounting.sell(new Sale(new MattsInstant(new Instant(2)), new Price(4), new StockId(1)));
      fail("Can't sell the same stock twice");
    } catch (IllegalArgumentException expected) { }
  }
  
  @Test
  public void testForBasicAcquireAndSellAccounting() {
    StockAccounting accounting = new StockAccounting();
    accounting.acquire(new Acquire(new MattsInstant(new Instant(1)), new Price(3), new StockId(1)));
    accounting.sell(new Sale(new MattsInstant(new Instant(2)), new Price(5), new StockId(1)));
    Iterator<Result> iterator = accounting.getResults().iterator();
    // TODO(madrake): use assert on iterator
    assertEquals(
        new Result(
            new StockId(1), 
            new MattsInstant(new Instant(1)), // acquisition date
            new Price(3), // true acquisition price
            new AdjustmentToPrice(0), // adjustment to acquisition price due to a disallowed wash sale*/, 
            null, // no other stockId here since no wash sale disallowed
            new MattsInstant(new Instant(2)), // sale date
            new Price(5), // true sales price
            new AdjustmentToPrice(0), // adjustment to sales price due to a disallowed wash sale
            null, // stockId2 is the recipient of the disallowed loss
            new ReportableGain(2)
            ), 
        iterator.next());
  }
  
  @Test
  public void testForTwoTransactionAcquireAndSellAccounting() {
    StockAccounting accounting = new StockAccounting();
    accounting.acquire(new Acquire(new MattsInstant(new Instant(1)), new Price(3), new StockId(1)));
    accounting.sell(new Sale(new MattsInstant(new Instant(2)), new Price(5), new StockId(1)));
    accounting.acquire(new Acquire(new MattsInstant(new Instant(3)), new Price(7), new StockId(2)));
    accounting.sell(new Sale(new MattsInstant(new Instant(4)), new Price(11), new StockId(2)));
    Iterator<Result> iterator = accounting.getResults().iterator();
    // TODO(madrake): use assert on iterator
    assertEquals(
        new Result(
            new StockId(1), 
            new MattsInstant(new Instant(1)), // acquisition date
            new Price(3), // true acquisition price
            new AdjustmentToPrice(0), // adjustment to acquisition price due to a disallowed wash sale*/, 
            null, // no other stockId here since no wash sale disallowed
            new MattsInstant(new Instant(2)), // sale date
            new Price(5), // true sales price
            new AdjustmentToPrice(0), // adjustment to sales price due to a disallowed wash sale
            null, // stockId2 is the recipient of the disallowed loss
            new ReportableGain(2)
            ), 
        iterator.next());
    assertEquals(
        new Result(
            new StockId(2), 
            new MattsInstant(new Instant(3)), // acquisition date
            new Price(7), // true acquisition price
            new AdjustmentToPrice(0), // adjustment to acquisition price due to a disallowed wash sale*/, 
            null, // no other stockId here since no wash sale disallowed
            new MattsInstant(new Instant(4)), // sale date
            new Price(11), // true sales price
            new AdjustmentToPrice(0), // adjustment to sales price due to a disallowed wash sale
            null, // stockId2 is the recipient of the disallowed loss
            new ReportableGain(4)
            ), 
        iterator.next());
  }
  
  @Test
  public void testCantDisallowLossOnGain() {
    StockAccounting accounting = new StockAccounting();
    accounting.acquire(new Acquire(new MattsInstant(new Instant(1)), new Price(3), new StockId(1)));
    accounting.sell(new Sale(new MattsInstant(new Instant(2)), new Price(5), new StockId(1)));
    try {
      accounting.disallowLossOnSale(new StockId(1), null);
      fail("Can't disallow a loss if there was a gain!");
    } catch (IllegalArgumentException expected) { }
  }
  
  @Test
  public void testCantDisallowLossWithNoGainOrLoss() {
    StockAccounting accounting = new StockAccounting();
    accounting.acquire(new Acquire(new MattsInstant(new Instant(1)), new Price(5), new StockId(1)));
    accounting.sell(new Sale(new MattsInstant(new Instant(2)), new Price(5), new StockId(1)));
    try {
      accounting.disallowLossOnSale(new StockId(1), null);
      fail("Can't disallow a loss if there was no change in price!");
    } catch (IllegalArgumentException expected) { }
  }
  
  @Test
  public void testForAcquireAndSellWithDisallowedLoss() {
    StockAccounting accounting = new StockAccounting();
    accounting.acquire(new Acquire(new MattsInstant(new Instant(1)), new Price(5), new StockId(1)));
    accounting.sell(new Sale(new MattsInstant(new Instant(2)), new Price(3), new StockId(1)));
    accounting.disallowLossOnSale(new StockId(1), null);
    Iterator<Result> iterator = accounting.getResults().iterator();
    // TODO(madrake): use assert on iterator
    assertEquals(
        new Result(
            new StockId(1), 
            new MattsInstant(new Instant(1)), // acquisition date
            new Price(5), // true acquisition price
            new AdjustmentToPrice(0), // adjustment to acquisition price due to a disallowed wash sale*/, 
            null, // no other stockId here since no wash sale disallowed
            new MattsInstant(new Instant(2)), // sale date
            new Price(3), // true sales price
            new AdjustmentToPrice(2), // adjustment to sales price due to a disallowed wash sale
            null, // stockId2 is the recipient of the disallowed loss
            new ReportableGain(0)
            ), 
        iterator.next());
  }
  
  @Test
  public void testCantDisallowLossOnSameStockTwice() {
    StockAccounting accounting = new StockAccounting();
    accounting.acquire(new Acquire(new MattsInstant(new Instant(1)), new Price(5), new StockId(1)));
    accounting.sell(new Sale(new MattsInstant(new Instant(2)), new Price(3), new StockId(1)));
    accounting.disallowLossOnSale(new StockId(1), null);
    try {
      accounting.disallowLossOnSale(new StockId(1), null);
      fail("Shouldn't have been able to disallow the same loss twice!");
    } catch (IllegalArgumentException expected) { }
  }
  
  @Test
  public void testCantAdjustCostBasisOnAcquisitionOfSameStockTwice() {
    StockAccounting accounting = new StockAccounting();
    accounting.acquire(new Acquire(new MattsInstant(new Instant(1)), new Price(5), new StockId(1)));
    accounting.adjustAcquireCostBasis(new StockId(1), new AdjustmentToPrice(2), null);
    try {
      accounting.adjustAcquireCostBasis(new StockId(1), new AdjustmentToPrice(3), null);
      fail("Shouldn't have been able to adjust the cost basis on the same acquire twice!");
    } catch (IllegalArgumentException expected) { }
  }
  
  @Test
  public void testAcquireWithAdjustedCostBasis() {
    StockAccounting accounting = new StockAccounting();
    accounting.acquire(new Acquire(new MattsInstant(new Instant(1)), new Price(5), new StockId(1)));
    accounting.adjustAcquireCostBasis(new StockId(1), new AdjustmentToPrice(2), null);
    accounting.sell(new Sale(new MattsInstant(new Instant(2)), new Price(3), new StockId(1)));
    Iterator<Result> iterator = accounting.getResults().iterator();
    // TODO(madrake): use assert on iterator
    assertEquals(
        new Result(
            new StockId(1), 
            new MattsInstant(new Instant(1)), // acquisition date
            new Price(5), // true acquisition price
            new AdjustmentToPrice(2), // adjustment to acquisition price due to a disallowed wash sale*/, 
            null, // no other stockId here since no wash sale disallowed
            new MattsInstant(new Instant(2)), // sale date
            new Price(3), // true sales price
            new AdjustmentToPrice(0), // adjustment to sales price due to a disallowed wash sale
            null, // stockId2 is the recipient of the disallowed loss
            new ReportableGain(-4)
            ), 
        iterator.next());
  }
  
  @Test
  public void testGetStocksSoldAtLossSingleSaleAtLoss() {
    StockAccounting accounting = new StockAccounting();
    accounting.acquire(new Acquire(new MattsInstant(new Instant(1)), new Price(5), new StockId(1)));
    accounting.sell(new Sale(new MattsInstant(new Instant(2)), new Price(3), new StockId(1)));
    assertEquals(Collections.singleton(new StockId(1)), 
        accounting.getStocksSoldAtLoss());
  }
  
  @Test
  public void testGetStocksSingleSaleAtGain() {
    StockAccounting accounting = new StockAccounting();
    accounting.acquire(new Acquire(new MattsInstant(new Instant(1)), new Price(5), new StockId(1)));
    accounting.sell(new Sale(new MattsInstant(new Instant(2)), new Price(9), new StockId(1)));
    assertEquals(Collections.emptySet(), 
        accounting.getStocksSoldAtLoss());
  }
  
  @Test
  public void testGetStocksOneGainOneLoss() {
    StockAccounting accounting = new StockAccounting();
    accounting.acquire(new Acquire(new MattsInstant(new Instant(1)), new Price(5), new StockId(1)));
    accounting.sell(new Sale(new MattsInstant(new Instant(2)), new Price(9), new StockId(1)));
    accounting.acquire(new Acquire(new MattsInstant(new Instant(1)), new Price(5), new StockId(2)));
    accounting.sell(new Sale(new MattsInstant(new Instant(2)), new Price(3), new StockId(2)));
    assertEquals(Collections.singleton(new StockId(2)), 
        accounting.getStocksSoldAtLoss());
  }
  
  @Test
  public void testGetStocksWithNoLossAfterWashOnSale() {
    StockAccounting accounting = new StockAccounting();
    accounting.acquire(new Acquire(new MattsInstant(new Instant(1)), new Price(5), new StockId(1)));
    accounting.sell(new Sale(new MattsInstant(new Instant(2)), new Price(3), new StockId(1)));
    accounting.disallowLossOnSale(new StockId(1), null);
    assertEquals(Collections.emptySet(), 
        accounting.getStocksSoldAtLoss());
  }
  
  @Test
  public void testGetStocksWithLossAfterAdjustedAcquire() {
    StockAccounting accounting = new StockAccounting();
    accounting.acquire(new Acquire(new MattsInstant(new Instant(1)), new Price(3), new StockId(1)));
    accounting.sell(new Sale(new MattsInstant(new Instant(2)), new Price(5), new StockId(1)));
    accounting.adjustAcquireCostBasis(new StockId(1), new AdjustmentToPrice(3), null);
    assertEquals(Collections.singleton(new StockId(1)), 
        accounting.getStocksSoldAtLoss());
  }
  
  @Test
  public void testGetStocksWithNoLossAfterAdjustedAcquire() {
    StockAccounting accounting = new StockAccounting();
    accounting.acquire(new Acquire(new MattsInstant(new Instant(1)), new Price(3), new StockId(1)));
    accounting.sell(new Sale(new MattsInstant(new Instant(2)), new Price(5), new StockId(1)));
    accounting.adjustAcquireCostBasis(new StockId(1), new AdjustmentToPrice(2), null);
    assertEquals(Collections.emptySet(), 
        accounting.getStocksSoldAtLoss());
  }
}
