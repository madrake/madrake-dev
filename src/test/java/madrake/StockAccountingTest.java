package madrake;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.Iterator;

import madrake.needsautovalue.Result;

import org.joda.time.Instant;
import org.junit.Test;

// TODO(madrake): everywhere in this test and elsewhere we need to use Truth and then we need
// to assert on the contents of iterators rather than the ad-hoc way we're doing it right now.
public class StockAccountingTest {

  @Test
  public void testCantAcquireTwice() {
    StockAccounting accounting = new StockAccounting();
    accounting.acquire(Event.create(StaticTestHelperMethods.value(1, 3), StockId.create(1), EventType.ACQUIRE));
    try {
      accounting.acquire(Event.create(StaticTestHelperMethods.value(2, 4), StockId.create(1), EventType.ACQUIRE));
      fail("Can't acquire the same stock twice");
    } catch (IllegalArgumentException expected) { }
  }
  
  @Test
  public void testCantSellTwice() {
    StockAccounting accounting = new StockAccounting();
    accounting.sell(Event.create(StaticTestHelperMethods.value(1, 3), StockId.create(1), EventType.SALE));
    try {
      accounting.sell(Event.create(StaticTestHelperMethods.value(2, 4), StockId.create(1), EventType.SALE));
      fail("Can't sell the same stock twice");
    } catch (IllegalArgumentException expected) { }
  }
  
  @Test
  public void testForBasicAcquireAndSellAccounting() {
    StockAccounting accounting = new StockAccounting();
    accounting.acquire(Event.create(StaticTestHelperMethods.value(1, 3), StockId.create(1), EventType.ACQUIRE));
    accounting.sell(Event.create(StaticTestHelperMethods.value(2, 5), StockId.create(1), EventType.SALE));
    Iterator<Result> iterator = accounting.getResults().iterator();
    StaticTestHelperMethods.assertEquals(
        new Result(
        StockId.create(1),
        RealizableValue.create(StaticTestHelperMethods.dollars(3), new Instant(1)),
        null,
        null,
        RealizableValue.create(StaticTestHelperMethods.dollars(5), new Instant(2)),
        false,
        null,
        StaticTestHelperMethods.dollars(2)), 
        iterator.next());
  }
  
  @Test
  public void testForTwoTransactionAcquireAndSellAccounting() {
    StockAccounting accounting = new StockAccounting();
    accounting.acquire(Event.create(StaticTestHelperMethods.value(1, 3), StockId.create(1), EventType.ACQUIRE));
    accounting.sell(Event.create(StaticTestHelperMethods.value(2, 5), StockId.create(1), EventType.SALE));
    accounting.acquire(Event.create(StaticTestHelperMethods.value(3, 7), StockId.create(2), EventType.ACQUIRE));
    accounting.sell(Event.create(StaticTestHelperMethods.value(4, 11), StockId.create(2), EventType.SALE));
    Iterator<Result> iterator = accounting.getResults().iterator();
    StaticTestHelperMethods.assertEquals(
        new Result(
        StockId.create(1),
        RealizableValue.create(StaticTestHelperMethods.dollars(3), new Instant(1)),
        null,
        null,
        RealizableValue.create(StaticTestHelperMethods.dollars(5), new Instant(2)),
        false,
        null,
        StaticTestHelperMethods.dollars(2)), 
        iterator.next());
    StaticTestHelperMethods.assertEquals(
        new Result(
        StockId.create(2),
        RealizableValue.create(StaticTestHelperMethods.dollars(7), new Instant(3)),
        null,
        null,
        RealizableValue.create(StaticTestHelperMethods.dollars(11), new Instant(4)),
        false,
        null,
        StaticTestHelperMethods.dollars(4)), 
        iterator.next());
  }
  
  @Test
  public void testCantDisallowLossOnGain() {
    StockAccounting accounting = new StockAccounting();
    accounting.acquire(Event.create(StaticTestHelperMethods.value(1, 3), StockId.create(1), EventType.ACQUIRE));
    accounting.sell(Event.create(StaticTestHelperMethods.value(2, 5), StockId.create(1), EventType.SALE));
    try {
      accounting.disallowLossOnSale(StockId.create(1), null);
      fail("Can't disallow a loss if there was a gain!");
    } catch (IllegalArgumentException expected) { }
  }
  
  @Test
  public void testCantDisallowLossWithNoGainOrLoss() {
    StockAccounting accounting = new StockAccounting();
    accounting.acquire(Event.create(StaticTestHelperMethods.value(1, 5), StockId.create(1), EventType.ACQUIRE));
    accounting.sell(Event.create(StaticTestHelperMethods.value(2, 5), StockId.create(1), EventType.SALE));
    try {
      accounting.disallowLossOnSale(StockId.create(1), null);
      fail("Can't disallow a loss if there was no change in price!");
    } catch (IllegalArgumentException expected) { }
  }
  
  @Test
  public void testForAcquireAndSellWithDisallowedLoss() {
    StockAccounting accounting = new StockAccounting();
    accounting.acquire(Event.create(StaticTestHelperMethods.value(1, 5), StockId.create(1), EventType.ACQUIRE));
    accounting.sell(Event.create(StaticTestHelperMethods.value(2, 3), StockId.create(1), EventType.SALE));
    accounting.disallowLossOnSale(StockId.create(1), null);
    Iterator<Result> iterator = accounting.getResults().iterator();
    Instant originalAcquisitionDate = new Instant(1);
    Instant originalSaleDate = new Instant(2);
    StaticTestHelperMethods.assertEquals(
        new Result(
        StockId.create(1),
        RealizableValue.create(
                        StaticTestHelperMethods.dollars(5), originalAcquisitionDate),
        null,
        null,
        RealizableValue.create(StaticTestHelperMethods.dollars(3), originalSaleDate),
        true,
        null,
        StaticTestHelperMethods.dollars(0)), 
        iterator.next());
  }
  
  @Test
  public void testCantDisallowLossOnSameStockTwice() {
    StockAccounting accounting = new StockAccounting();
    accounting.acquire(Event.create(StaticTestHelperMethods.value(1, 5), StockId.create(1), EventType.ACQUIRE));
    accounting.sell(Event.create(StaticTestHelperMethods.value(2, 3), StockId.create(1), EventType.SALE));
    accounting.disallowLossOnSale(StockId.create(1), null);
    try {
      accounting.disallowLossOnSale(StockId.create(1), null);
      fail("Shouldn't have been able to disallow the same loss twice!");
    } catch (IllegalArgumentException expected) { }
  }
  
  @Test
  public void testCantAdjustCostBasisOnAcquisitionOfSameStockTwice() {
    StockAccounting accounting = new StockAccounting();
    accounting.acquire(Event.create(StaticTestHelperMethods.value(1, 5), StockId.create(1), EventType.ACQUIRE));
    accounting.adjustAcquireCostBasis(StockId.create(1), AcquisitionAdjustment.create(StaticTestHelperMethods.dollars(2), new Instant(1)), null);
    try {
      accounting.adjustAcquireCostBasis(StockId.create(1), AcquisitionAdjustment.create(StaticTestHelperMethods.dollars(3), new Instant(1)), null);
      fail("Shouldn't have been able to adjust the cost basis on the same acquire twice!");
    } catch (IllegalArgumentException expected) { }
  }
  
  @Test
  public void testAcquireWithAdjustedCostBasis() {
    StockAccounting accounting = new StockAccounting();
    accounting.acquire(Event.create(StaticTestHelperMethods.value(1, 5), StockId.create(1), EventType.ACQUIRE));
    accounting.adjustAcquireCostBasis(StockId.create(1), AcquisitionAdjustment.create(StaticTestHelperMethods.dollars(2), new Instant(1)), null);
    accounting.sell(Event.create(StaticTestHelperMethods.value(2, 3), StockId.create(1), EventType.SALE));
    Iterator<Result> iterator = accounting.getResults().iterator();
    StaticTestHelperMethods.assertEquals(
        new Result(
        StockId.create(1),
        RealizableValue.create(StaticTestHelperMethods.dollars(5), new Instant(1)),
        AcquisitionAdjustment.create(StaticTestHelperMethods.dollars(2), new Instant(1)),
        null,
        RealizableValue.create(StaticTestHelperMethods.dollars(3), new Instant(2)),
        false,
        null,
        StaticTestHelperMethods.dollars(-4)),
        iterator.next());
  }
  
  @Test
  public void testGetStocksSoldAtLossSingleSaleAtLoss() {
    StockAccounting accounting = new StockAccounting();
    accounting.acquire(Event.create(StaticTestHelperMethods.value(1, 5), StockId.create(1), EventType.ACQUIRE));
    accounting.sell(Event.create(StaticTestHelperMethods.value(2, 3), StockId.create(1), EventType.SALE));
    assertEquals(Collections.singleton(StockId.create(1)), 
        accounting.getStocksSoldAtLoss());
  }
  
  @Test
  public void testGetStocksSingleSaleAtGain() {
    StockAccounting accounting = new StockAccounting();
    accounting.acquire(Event.create(StaticTestHelperMethods.value(1, 5), StockId.create(1), EventType.ACQUIRE));
    accounting.sell(Event.create(StaticTestHelperMethods.value(2, 9), StockId.create(1), EventType.SALE));
    assertEquals(Collections.emptySet(), 
        accounting.getStocksSoldAtLoss());
  }
  
  @Test
  public void testGetStocksOneGainOneLoss() {
    StockAccounting accounting = new StockAccounting();
    accounting.acquire(Event.create(StaticTestHelperMethods.value(1, 5), StockId.create(1), EventType.ACQUIRE));
    accounting.sell(Event.create(StaticTestHelperMethods.value(2, 9), StockId.create(1), EventType.SALE));
    accounting.acquire(Event.create(StaticTestHelperMethods.value(1, 5), StockId.create(2), EventType.ACQUIRE));
    accounting.sell(Event.create(StaticTestHelperMethods.value(2, 3), StockId.create(2), EventType.SALE));
    assertEquals(Collections.singleton(StockId.create(2)), 
        accounting.getStocksSoldAtLoss());
  }
  
  @Test
  public void testGetStocksWithNoLossAfterWashOnSale() {
    StockAccounting accounting = new StockAccounting();
    accounting.acquire(Event.create(StaticTestHelperMethods.value(1, 5), StockId.create(1), EventType.ACQUIRE));
    accounting.sell(Event.create(StaticTestHelperMethods.value(2, 3), StockId.create(1), EventType.SALE));
    accounting.disallowLossOnSale(StockId.create(1), null);
    assertEquals(Collections.emptySet(), 
        accounting.getStocksSoldAtLoss());
  }
  
  @Test
  public void testGetStocksWithLossAfterAdjustedAcquire() {
    StockAccounting accounting = new StockAccounting();
    accounting.acquire(Event.create(StaticTestHelperMethods.value(1, 3), StockId.create(1), EventType.ACQUIRE));
    accounting.sell(Event.create(StaticTestHelperMethods.value(2, 5), StockId.create(1), EventType.SALE));
    accounting.adjustAcquireCostBasis(StockId.create(1), AcquisitionAdjustment.create(StaticTestHelperMethods.dollars(3), new Instant(1)), null);
    assertEquals(Collections.singleton(StockId.create(1)), 
        accounting.getStocksSoldAtLoss());
  }
  
  @Test
  public void testGetStocksWithNoLossAfterAdjustedAcquire() {
    StockAccounting accounting = new StockAccounting();
    accounting.acquire(Event.create(StaticTestHelperMethods.value(1, 3), StockId.create(1), EventType.ACQUIRE));
    accounting.sell(Event.create(StaticTestHelperMethods.value(2, 5), StockId.create(1), EventType.SALE));
    accounting.adjustAcquireCostBasis(StockId.create(1), AcquisitionAdjustment.create(StaticTestHelperMethods.dollars(2), new Instant(1)), null);
    assertEquals(Collections.emptySet(), 
        accounting.getStocksSoldAtLoss());
  }
}
