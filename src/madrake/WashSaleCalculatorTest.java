package madrake;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.joda.time.Instant;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class WashSaleCalculatorTest {

  private static final MattsInstant INSTANT_1000 = new MattsInstant(new Instant(1000));
  private static final MattsInstant INSTANT_1003 = new MattsInstant(new Instant(1003));
  private static final MattsInstant INSTANT_1004 = new MattsInstant(new Instant(1004));
  private static final MattsInstant INSTANT_1005 = new MattsInstant(new Instant(1005));
  private static final MattsInstant INSTANT_1010 = new MattsInstant(new Instant(1010));
  private static final MattsInstant INSTANT_1011 = new MattsInstant(new Instant(1011));
  private static final MattsInstant INSTANT_1013 = new MattsInstant(new Instant(1013));
  private static final MattsInstant INSTANT_1015 = new MattsInstant(new Instant(1015));
  private static final MattsInstant INSTANT_2000 = new MattsInstant(new Instant(2000));
  
  // TODO(madrake): change code so that the holding period changes as well
  
  @Test
  public void testSanityCheckInputWhenMissingStockAcquisition() {
    WashSaleCalculator calculator = new WashSaleCalculator();
    final StockId stockId1 = new StockId(1);
    final StockId stockId2 = new StockId(2);
    final StockId stockId3 = new StockId(3);
    try {
      calculator.calculate( 
          ImmutableList.of(
              new Acquire(INSTANT_1000, new Price(100), stockId1),
              new Acquire(INSTANT_1005, new Price(110), stockId2)),
          ImmutableList.of(
              new Sale(INSTANT_1004, new Price(40), stockId1),
              new Sale(INSTANT_1010, new Price(200), stockId3)));
      fail("Expected an error because of invalid input - stock3 sold but "
          + "not acquired and stock2 acquired but not sold");
    } catch (IllegalArgumentException expected) { /* expected */ }
  }
  
  @Test
  public void testSanityCheckInputWhenDuplicateDataForStockAcquisition() {
    WashSaleCalculator calculator = new WashSaleCalculator();
    final StockId stockId1 = new StockId(1);
    final StockId stockId2 = new StockId(2);
    try {
      calculator.calculate( 
          ImmutableList.of(
              new Acquire(INSTANT_1000, new Price(100), stockId1),
              new Acquire(INSTANT_1003, new Price(110), stockId1),
              new Acquire(INSTANT_1005, new Price(110), stockId2)),
          ImmutableList.of(
              new Sale(INSTANT_1004, new Price(40), stockId1),
              new Sale(INSTANT_1010, new Price(200), stockId2)));
      fail("Expected an error because stock 2 was acquired twice");
    } catch (IllegalArgumentException expected) { /* expected */ }
  }
  
  @Test
  public void testSanityCheckInputWhenDuplicateDataForStockSale() {
    WashSaleCalculator calculator = new WashSaleCalculator();
    final StockId stockId1 = new StockId(1);
    final StockId stockId2 = new StockId(2);
    try {
      calculator.calculate( 
          ImmutableList.of(
              new Acquire(INSTANT_1000, new Price(100), stockId1),
              new Acquire(INSTANT_1005, new Price(110), stockId2)),
          ImmutableList.of(
              new Sale(INSTANT_1004, new Price(40), stockId1),
              new Sale(INSTANT_1010, new Price(200), stockId2),
              new Sale(INSTANT_2000, new Price(400), stockId2)));
      fail("Expected an error because stock 2 was sold twice");
    } catch (IllegalArgumentException expected) { /* expected */ }
  }
  
  @Test
  public void testTwoStockCalculationWithWashSale() {
    WashSaleCalculator calculator = new WashSaleCalculator();
    final StockId stockId1 = new StockId(1);
    final StockId stockId2 = new StockId(2);
     // TODO(madrake): force this to dollars?
    Iterable<Result> results = calculator.calculate(
        ImmutableList.of(
            new Acquire(INSTANT_1000, new Price(100), stockId1),
            new Acquire(INSTANT_1005, new Price(110), stockId2)),
        ImmutableList.of(
            new Sale(INSTANT_1004, new Price(40), stockId1),
            new Sale(INSTANT_1010, new Price(200), stockId2)));
    Iterator<Result> iterator = results.iterator();
    // TODO(madrake): use assert on iterator
    assertEquals(
        new Result(
            stockId1, 
            INSTANT_1000, // acquisition date
            new Price(100), // true acquisition price
            new AdjustmentToPrice(0), // adjustment to acquisition price due to a disallowed wash sale*/, 
            null, // no other stockId here since no wash sale disallowed
            INSTANT_1004, // sale date
            new Price(40), // true sales price
            new AdjustmentToPrice(60), // adjustment to sales price due to a disallowed wash sale
            stockId2, // stockId2 is the recipient of the disallowed loss
            new ReportableGain(0)
            ), 
        iterator.next());
    assertEquals(
        new Result(
            stockId2, 
            INSTANT_1005, // acquisition date
            new Price(110), // true acquisition price
            new AdjustmentToPrice(60), // adjustment to acquisition price due to a disallowed wash sale*/, 
            stockId1, // stockId1 here was the stock where the loss was disallowed
            INSTANT_1010, // sale date
            new Price(200), // true sales price
            new AdjustmentToPrice(0), // adjustment to sales price due to a disallowed wash sale
            null,
            new ReportableGain(30)), 
        iterator.next());
  }
  
  @Test
  public void testTwoStockCalculationNoWashSales() {
    WashSaleCalculator calculator = new WashSaleCalculator();
    final StockId stockId1 = new StockId(1);
    final StockId stockId2 = new StockId(2);
     // TODO(madrake): force this to dollars?
    Iterable<Result> results = calculator.calculate(
        ImmutableList.of(
            new Acquire(INSTANT_1000, new Price(100), stockId1),
            new Acquire(INSTANT_1010, new Price(110), stockId2)),
        ImmutableList.of(
            new Sale(INSTANT_1004, new Price(40), stockId1),
            new Sale(INSTANT_1015, new Price(200), stockId2)));
    Iterator<Result> iterator = results.iterator();
    // TODO(madrake): use assert on iterator
    assertEquals(
        new Result(
            stockId1, 
            INSTANT_1000, // acquisition date
            new Price(100), // true acquisition price
            new AdjustmentToPrice(0), // adjustment to acquisition price due to a disallowed wash sale*/, 
            null, // no other stockId here since no wash sale disallowed
            INSTANT_1004, // sale date
            new Price(40), // true sales price
            new AdjustmentToPrice(0), // adjustment to sales price due to a disallowed wash sale
            null, // stockId2 is the recipient of the disallowed loss
            new ReportableGain(-60)
            ), 
        iterator.next());
    assertEquals(
        new Result(
            stockId2, 
            INSTANT_1010, // acquisition date
            new Price(110), // true acquisition price
            new AdjustmentToPrice(0), // adjustment to acquisition price due to a disallowed wash sale*/, 
            null, // stockId1 here was the stock where the loss was disallowed
            INSTANT_1015, // sale date
            new Price(200), // true sales price
            new AdjustmentToPrice(0), // adjustment to sales price due to a disallowed wash sale
            null,
            new ReportableGain(90)), 
        iterator.next());
  }
  
  @Test
  public void testTwoStockCalculationWithAcquireBeforeSale() {
    WashSaleCalculator calculator = new WashSaleCalculator();
    final StockId stockId1 = new StockId(1);
    final StockId stockId2 = new StockId(2);
     // TODO(madrake): force this to dollars?
    Iterable<Result> results = calculator.calculate(
        ImmutableList.of(
            new Acquire(INSTANT_1000, new Price(100), stockId1),
            new Acquire(INSTANT_1004, new Price(110), stockId2)),
        ImmutableList.of(
            new Sale(INSTANT_1005, new Price(40), stockId1),
            new Sale(INSTANT_1010, new Price(200), stockId2)));
    Iterator<Result> iterator = results.iterator();
    // TODO(madrake): use assert on iterator
    assertEquals(
        new Result(
            stockId1, 
            INSTANT_1000, // acquisition date
            new Price(100), // true acquisition price
            new AdjustmentToPrice(0), // adjustment to acquisition price due to a disallowed wash sale*/, 
            null, // no other stockId here since no wash sale disallowed
            INSTANT_1005, // sale date
            new Price(40), // true sales price
            new AdjustmentToPrice(60), // adjustment to sales price due to a disallowed wash sale
            stockId2, // stockId2 is the recipient of the disallowed loss
            new ReportableGain(0)
            ), 
        iterator.next());
    assertEquals(
        new Result(
            stockId2, 
            INSTANT_1004, // acquisition date
            new Price(110), // true acquisition price
            new AdjustmentToPrice(60), // adjustment to acquisition price due to a disallowed wash sale*/, 
            stockId1, // stockId1 here was the stock where the loss was disallowed
            INSTANT_1010, // sale date
            new Price(200), // true sales price
            new AdjustmentToPrice(0), // adjustment to sales price due to a disallowed wash sale
            null,
            new ReportableGain(30)), 
        iterator.next());
  }
  
  @Test
  public void testTwoStockCalculationWithDifferentValues() {
    WashSaleCalculator calculator = new WashSaleCalculator();
    final StockId stockId1 = new StockId(1);
    final StockId stockId2 = new StockId(2);
     // TODO(madrake): force this to dollars?
    Iterable<Result> results = calculator.calculate(
        ImmutableList.of(
            new Acquire(INSTANT_1000, new Price(130), stockId1),
            new Acquire(INSTANT_1005, new Price(110), stockId2)),
        ImmutableList.of(
            new Sale(INSTANT_1004, new Price(40), stockId1),
            new Sale(INSTANT_1010, new Price(200), stockId2)));
    Iterator<Result> iterator = results.iterator();
    // TODO(madrake): use assert on iterator
    assertEquals(
        new Result(
            stockId1, 
            INSTANT_1000, // acquisition date
            new Price(130), // true acquisition price
            new AdjustmentToPrice(0), // adjustment to acquisition price due to a disallowed wash sale*/, 
            null, // no other stockId here since no wash sale disallowed
            INSTANT_1004, // sale date
            new Price(40), // true sales price
            new AdjustmentToPrice(90), // adjustment to sales price due to a disallowed wash sale
            stockId2, // stockId2 is the recipient of the disallowed loss
            new ReportableGain(0)
            ), 
        iterator.next());
    assertEquals(
        new Result(
            stockId2, 
            INSTANT_1005, // acquisition date
            new Price(110), // true acquisition price
            new AdjustmentToPrice(90), // adjustment to acquisition price due to a disallowed wash sale*/, 
            stockId1, // stockId1 here was the stock where the loss was disallowed
            INSTANT_1010, // sale date
            new Price(200), // true sales price
            new AdjustmentToPrice(0), // adjustment to sales price due to a disallowed wash sale
            null,
            new ReportableGain(0)), 
        iterator.next());
  }
  
  @Test
  public void testTwoWashSalesAccumulateInThirdSale() {
    WashSaleCalculator calculator = new WashSaleCalculator();
    final StockId stockId1 = new StockId(1);
    final StockId stockId2 = new StockId(2);
    final StockId stockId3 = new StockId(3);
    // TODO(madrake): need to support the date changes
    // TODO(madrake): need to support not selling all stock
    // TODO(madrake): force this to dollars?
    Iterable<Result> results = calculator.calculate(
        ImmutableList.of(
            new Acquire(INSTANT_1000, new Price(100), stockId1),
            new Acquire(INSTANT_1005, new Price(60), stockId2),
            new Acquire(INSTANT_1011, new Price(45), stockId3)),
        ImmutableList.of(
            new Sale(INSTANT_1004, new Price(50), stockId1),
            new Sale(INSTANT_1010, new Price(40), stockId2),
            new Sale(INSTANT_1013, new Price(46), stockId3)));
    Iterator<Result> iterator = results.iterator();
    // TODO(madrake): use assert on iterator
    assertEquals(
        new Result(
            stockId1, 
            INSTANT_1000, // acquisition date
            new Price(100), // true acquisition price
            new AdjustmentToPrice(0), // adjustment to acquisition price due to a disallowed wash sale*/, 
            null, // no other stockId here since no wash sale disallowed
            INSTANT_1004, // sale date
            new Price(50), // true sales price
            new AdjustmentToPrice(50), // adjustment to sales price due to a disallowed wash sale
            stockId2, // stockId2 is the recipient of the disallowed loss
            new ReportableGain(0)
            ),
        iterator.next());
    assertEquals(
        new Result(
            stockId2, 
            INSTANT_1005, // acquisition date
            new Price(60), // true acquisition price
            new AdjustmentToPrice(50), // adjustment to acquisition price due to a disallowed wash sale*/, 
            stockId1, // stockId1 here was the stock where the loss was disallowed
            INSTANT_1010, // sale date
            new Price(40), // true sales price
            new AdjustmentToPrice(70), // adjustment to sales price due to a disallowed wash sale
            stockId3,
            new ReportableGain(0)), 
        iterator.next());
    assertEquals(
        new Result(
            stockId3, 
            INSTANT_1011, // acquisition date
            new Price(45), // true acquisition price
            new AdjustmentToPrice(70), // adjustment to acquisition price due to a disallowed wash sale*/, 
            stockId2, // stockId2 here was the stock where the loss was disallowed
            INSTANT_1013, // sale date
            new Price(46), // true sales price
            new AdjustmentToPrice(0), // adjustment to sales price due to a disallowed wash sale
            null,
            new ReportableGain(-69)), 
        iterator.next());
  }
  
  @Test
  public void testTwoWashSalesAccumulateInThirdSaleWithAcquiresBeforeSales() {
    WashSaleCalculator calculator = new WashSaleCalculator();
    final StockId stockId1 = new StockId(1);
    final StockId stockId2 = new StockId(2);
    final StockId stockId3 = new StockId(3);
    // TODO(madrake): need to support the date changes
    // TODO(madrake): need to support not selling all stock
    // TODO(madrake): force this to dollars?
    Iterable<Result> results = calculator.calculate(
        ImmutableList.of(
            new Acquire(INSTANT_1000, new Price(100), stockId1),
            new Acquire(INSTANT_1004, new Price(60), stockId2),
            new Acquire(INSTANT_1010, new Price(45), stockId3)),
        ImmutableList.of(
            new Sale(INSTANT_1005, new Price(50), stockId1),
            new Sale(INSTANT_1011, new Price(40), stockId2),
            new Sale(INSTANT_1013, new Price(46), stockId3)));
    Iterator<Result> iterator = results.iterator();
    // TODO(madrake): use assert on iterator
    assertEquals(
        new Result(
            stockId1, 
            INSTANT_1000, // acquisition date
            new Price(100), // true acquisition price
            new AdjustmentToPrice(0), // adjustment to acquisition price due to a disallowed wash sale*/, 
            null, // no other stockId here since no wash sale disallowed
            INSTANT_1005, // sale date
            new Price(50), // true sales price
            new AdjustmentToPrice(50), // adjustment to sales price due to a disallowed wash sale
            stockId2, // stockId2 is the recipient of the disallowed loss
            new ReportableGain(0)
            ),
        iterator.next());
    assertEquals(
        new Result(
            stockId2, 
            INSTANT_1004, // acquisition date
            new Price(60), // true acquisition price
            new AdjustmentToPrice(50), // adjustment to acquisition price due to a disallowed wash sale*/, 
            stockId1, // stockId1 here was the stock where the loss was disallowed
            INSTANT_1011, // sale date
            new Price(40), // true sales price
            new AdjustmentToPrice(70), // adjustment to sales price due to a disallowed wash sale
            stockId3,
            new ReportableGain(0)), 
        iterator.next());
    assertEquals(
        new Result(
            stockId3, 
            INSTANT_1010, // acquisition date
            new Price(45), // true acquisition price
            new AdjustmentToPrice(70), // adjustment to acquisition price due to a disallowed wash sale*/, 
            stockId2, // stockId2 here was the stock where the loss was disallowed
            INSTANT_1013, // sale date
            new Price(46), // true sales price
            new AdjustmentToPrice(0), // adjustment to sales price due to a disallowed wash sale
            null,
            new ReportableGain(-69)), 
        iterator.next());
  }
}
