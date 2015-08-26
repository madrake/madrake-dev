package madrake;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.Iterator;

import madrake.needsautovalue.Event;
import madrake.needsautovalue.EventType;
import madrake.needsautovalue.AcquisitionAdjustment;
import madrake.needsautovalue.RealizableValue;
import madrake.needsautovalue.Result;
import madrake.needsautovalue.StockId;

import org.joda.time.Instant;
import org.junit.Test;

// TODO(madrake): everywhere in this test and elsewhere we need to use Truth and then we need
// to assert on the contents of iterators rather than the ad-hoc way we're doing it right now.
public class StockAccountingTest {

  @Test
  public void testCantAcquireTwice() {
    StockAccounting accounting = new StockAccounting();
    accounting.acquire(new Event(StaticTestHelperMethods.value(1, 3), new StockId(1), EventType.ACQUIRE));
    try {
      accounting.acquire(new Event(StaticTestHelperMethods.value(2, 4), new StockId(1), EventType.ACQUIRE));
      fail("Can't acquire the same stock twice");
    } catch (IllegalArgumentException expected) { }
  }
  
  @Test
  public void testCantSellTwice() {
    StockAccounting accounting = new StockAccounting();
    accounting.sell(new Event(StaticTestHelperMethods.value(1, 3), new StockId(1), EventType.SALE));
    try {
      accounting.sell(new Event(StaticTestHelperMethods.value(2, 4), new StockId(1), EventType.SALE));
      fail("Can't sell the same stock twice");
    } catch (IllegalArgumentException expected) { }
  }
  
  @Test
  public void testForBasicAcquireAndSellAccounting() {
    StockAccounting accounting = new StockAccounting();
    accounting.acquire(new Event(StaticTestHelperMethods.value(1, 3), new StockId(1), EventType.ACQUIRE));
    accounting.sell(new Event(StaticTestHelperMethods.value(2, 5), new StockId(1), EventType.SALE));
    Iterator<Result> iterator = accounting.getResults().iterator();
    StaticTestHelperMethods.assertEquals(
        new Result(
        new StockId(1),
        new RealizableValue(StaticTestHelperMethods.dollars(3), new Instant(1)),
        null,
        null,
        new RealizableValue(StaticTestHelperMethods.dollars(5), new Instant(2)),
        StaticTestHelperMethods.dollars(0),
        false,
        null,
        StaticTestHelperMethods.dollars(2)), 
        iterator.next());
  }
  
  @Test
  public void testForTwoTransactionAcquireAndSellAccounting() {
    StockAccounting accounting = new StockAccounting();
    accounting.acquire(new Event(StaticTestHelperMethods.value(1, 3), new StockId(1), EventType.ACQUIRE));
    accounting.sell(new Event(StaticTestHelperMethods.value(2, 5), new StockId(1), EventType.SALE));
    accounting.acquire(new Event(StaticTestHelperMethods.value(3, 7), new StockId(2), EventType.ACQUIRE));
    accounting.sell(new Event(StaticTestHelperMethods.value(4, 11), new StockId(2), EventType.SALE));
    Iterator<Result> iterator = accounting.getResults().iterator();
    StaticTestHelperMethods.assertEquals(
        new Result(
        new StockId(1),
        new RealizableValue(StaticTestHelperMethods.dollars(3), new Instant(1)),
        null,
        null,
        new RealizableValue(StaticTestHelperMethods.dollars(5), new Instant(2)),
        StaticTestHelperMethods.dollars(0),
        false,
        null,
        StaticTestHelperMethods.dollars(2)), 
        iterator.next());
    StaticTestHelperMethods.assertEquals(
        new Result(
        new StockId(2),
        new RealizableValue(StaticTestHelperMethods.dollars(7), new Instant(3)),
        null,
        null,
        new RealizableValue(StaticTestHelperMethods.dollars(11), new Instant(4)),
        StaticTestHelperMethods.dollars(0),
        false,
        null,
        StaticTestHelperMethods.dollars(4)), 
        iterator.next());
  }
  
  @Test
  public void testCantDisallowLossOnGain() {
    StockAccounting accounting = new StockAccounting();
    accounting.acquire(new Event(StaticTestHelperMethods.value(1, 3), new StockId(1), EventType.ACQUIRE));
    accounting.sell(new Event(StaticTestHelperMethods.value(2, 5), new StockId(1), EventType.SALE));
    try {
      accounting.disallowLossOnSale(new StockId(1), null);
      fail("Can't disallow a loss if there was a gain!");
    } catch (IllegalArgumentException expected) { }
  }
  
  @Test
  public void testCantDisallowLossWithNoGainOrLoss() {
    StockAccounting accounting = new StockAccounting();
    accounting.acquire(new Event(StaticTestHelperMethods.value(1, 5), new StockId(1), EventType.ACQUIRE));
    accounting.sell(new Event(StaticTestHelperMethods.value(2, 5), new StockId(1), EventType.SALE));
    try {
      accounting.disallowLossOnSale(new StockId(1), null);
      fail("Can't disallow a loss if there was no change in price!");
    } catch (IllegalArgumentException expected) { }
  }
  
  @Test
  public void testForAcquireAndSellWithDisallowedLoss() {
    StockAccounting accounting = new StockAccounting();
    accounting.acquire(new Event(StaticTestHelperMethods.value(1, 5), new StockId(1), EventType.ACQUIRE));
    accounting.sell(new Event(StaticTestHelperMethods.value(2, 3), new StockId(1), EventType.SALE));
    accounting.disallowLossOnSale(new StockId(1), null);
    Iterator<Result> iterator = accounting.getResults().iterator();
    Instant originalAcquisitionDate = new Instant(1);
    Instant originalSaleDate = new Instant(2);
    StaticTestHelperMethods.assertEquals(
        new Result(
        new StockId(1),
        new RealizableValue(
                        StaticTestHelperMethods.dollars(5), originalAcquisitionDate),
        null,
        null,
        new RealizableValue(StaticTestHelperMethods.dollars(3), originalSaleDate),
        StaticTestHelperMethods.dollars(2),
        false,
        null,
        StaticTestHelperMethods.dollars(0)), 
        iterator.next());
  }
  
  @Test
  public void testCantDisallowLossOnSameStockTwice() {
    StockAccounting accounting = new StockAccounting();
    accounting.acquire(new Event(StaticTestHelperMethods.value(1, 5), new StockId(1), EventType.ACQUIRE));
    accounting.sell(new Event(StaticTestHelperMethods.value(2, 3), new StockId(1), EventType.SALE));
    accounting.disallowLossOnSale(new StockId(1), null);
    try {
      accounting.disallowLossOnSale(new StockId(1), null);
      fail("Shouldn't have been able to disallow the same loss twice!");
    } catch (IllegalArgumentException expected) { }
  }
  
  @Test
  public void testCantAdjustCostBasisOnAcquisitionOfSameStockTwice() {
    StockAccounting accounting = new StockAccounting();
    accounting.acquire(new Event(StaticTestHelperMethods.value(1, 5), new StockId(1), EventType.ACQUIRE));
    accounting.adjustAcquireCostBasis(new StockId(1), new AcquisitionAdjustment(StaticTestHelperMethods.dollars(2), new Instant(1)), null);
    try {
      accounting.adjustAcquireCostBasis(new StockId(1), new AcquisitionAdjustment(StaticTestHelperMethods.dollars(3), new Instant(1)), null);
      fail("Shouldn't have been able to adjust the cost basis on the same acquire twice!");
    } catch (IllegalArgumentException expected) { }
  }
  
  @Test
  public void testAcquireWithAdjustedCostBasis() {
    StockAccounting accounting = new StockAccounting();
    accounting.acquire(new Event(StaticTestHelperMethods.value(1, 5), new StockId(1), EventType.ACQUIRE));
    accounting.adjustAcquireCostBasis(new StockId(1), new AcquisitionAdjustment(StaticTestHelperMethods.dollars(2), new Instant(1)), null);
    accounting.sell(new Event(StaticTestHelperMethods.value(2, 3), new StockId(1), EventType.SALE));
    Iterator<Result> iterator = accounting.getResults().iterator();
    StaticTestHelperMethods.assertEquals(
        new Result(
        new StockId(1),
        new RealizableValue(StaticTestHelperMethods.dollars(5), new Instant(1)),
        new AcquisitionAdjustment(StaticTestHelperMethods.dollars(2), new Instant(1)),
        null,
        new RealizableValue(StaticTestHelperMethods.dollars(3), new Instant(2)),
        StaticTestHelperMethods.dollars(0),
        false,
        null,
        StaticTestHelperMethods.dollars(-4)),
        iterator.next());
  }
  
  @Test
  public void testGetStocksSoldAtLossSingleSaleAtLoss() {
    StockAccounting accounting = new StockAccounting();
    accounting.acquire(new Event(StaticTestHelperMethods.value(1, 5), new StockId(1), EventType.ACQUIRE));
    accounting.sell(new Event(StaticTestHelperMethods.value(2, 3), new StockId(1), EventType.SALE));
    assertEquals(Collections.singleton(new StockId(1)), 
        accounting.getStocksSoldAtLoss());
  }
  
  @Test
  public void testGetStocksSingleSaleAtGain() {
    StockAccounting accounting = new StockAccounting();
    accounting.acquire(new Event(StaticTestHelperMethods.value(1, 5), new StockId(1), EventType.ACQUIRE));
    accounting.sell(new Event(StaticTestHelperMethods.value(2, 9), new StockId(1), EventType.SALE));
    assertEquals(Collections.emptySet(), 
        accounting.getStocksSoldAtLoss());
  }
  
  @Test
  public void testGetStocksOneGainOneLoss() {
    StockAccounting accounting = new StockAccounting();
    accounting.acquire(new Event(StaticTestHelperMethods.value(1, 5), new StockId(1), EventType.ACQUIRE));
    accounting.sell(new Event(StaticTestHelperMethods.value(2, 9), new StockId(1), EventType.SALE));
    accounting.acquire(new Event(StaticTestHelperMethods.value(1, 5), new StockId(2), EventType.ACQUIRE));
    accounting.sell(new Event(StaticTestHelperMethods.value(2, 3), new StockId(2), EventType.SALE));
    assertEquals(Collections.singleton(new StockId(2)), 
        accounting.getStocksSoldAtLoss());
  }
  
  @Test
  public void testGetStocksWithNoLossAfterWashOnSale() {
    StockAccounting accounting = new StockAccounting();
    accounting.acquire(new Event(StaticTestHelperMethods.value(1, 5), new StockId(1), EventType.ACQUIRE));
    accounting.sell(new Event(StaticTestHelperMethods.value(2, 3), new StockId(1), EventType.SALE));
    accounting.disallowLossOnSale(new StockId(1), null);
    assertEquals(Collections.emptySet(), 
        accounting.getStocksSoldAtLoss());
  }
  
  @Test
  public void testGetStocksWithLossAfterAdjustedAcquire() {
    StockAccounting accounting = new StockAccounting();
    accounting.acquire(new Event(StaticTestHelperMethods.value(1, 3), new StockId(1), EventType.ACQUIRE));
    accounting.sell(new Event(StaticTestHelperMethods.value(2, 5), new StockId(1), EventType.SALE));
    accounting.adjustAcquireCostBasis(new StockId(1), new AcquisitionAdjustment(StaticTestHelperMethods.dollars(3), new Instant(1)), null);
    assertEquals(Collections.singleton(new StockId(1)), 
        accounting.getStocksSoldAtLoss());
  }
  
  @Test
  public void testGetStocksWithNoLossAfterAdjustedAcquire() {
    StockAccounting accounting = new StockAccounting();
    accounting.acquire(new Event(StaticTestHelperMethods.value(1, 3), new StockId(1), EventType.ACQUIRE));
    accounting.sell(new Event(StaticTestHelperMethods.value(2, 5), new StockId(1), EventType.SALE));
    accounting.adjustAcquireCostBasis(new StockId(1), new AcquisitionAdjustment(StaticTestHelperMethods.dollars(2), new Instant(1)), null);
    assertEquals(Collections.emptySet(), 
        accounting.getStocksSoldAtLoss());
  }
}
