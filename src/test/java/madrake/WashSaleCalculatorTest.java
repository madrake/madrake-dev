package madrake;

import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.Iterator;

import org.joda.time.Instant;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import madrake.needsautovalue.AcquisitionAdjustment;
import madrake.needsautovalue.Event;
import madrake.needsautovalue.EventType;
import madrake.needsautovalue.RealizableValue;
import madrake.needsautovalue.Result;
import madrake.needsautovalue.StockId;

public class WashSaleCalculatorTest {

  private static final Instant INSTANT_1000 = Instant.parse("2012-10-01");
  private static final Instant INSTANT_1003 = Instant.parse("2012-11-03");
  private static final Instant INSTANT_1004 = Instant.parse("2012-11-15");
  private static final Instant INSTANT_1005 = Instant.parse("2012-12-03");
  private static final Instant INSTANT_1010 = Instant.parse("2013-04-01");
  private static final Instant INSTANT_1011 = Instant.parse("2013-04-30");
  private static final Instant INSTANT_1013 = Instant.parse("2013-06-02");
  private static final Instant INSTANT_1015 = Instant.parse("2013-08-03");
  private static final Instant INSTANT_2000 = Instant.parse("2015-10-10");

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
              new Event(new RealizableValue(StaticTestHelperMethods.dollars(100), INSTANT_1000), stockId1, EventType.ACQUIRE),
              new Event(new RealizableValue(StaticTestHelperMethods.dollars(110), INSTANT_1005), stockId2, EventType.ACQUIRE)),
          ImmutableList.of(
              new Event(new RealizableValue(StaticTestHelperMethods.dollars(40), INSTANT_1004), stockId1, EventType.SALE),
              new Event(new RealizableValue(StaticTestHelperMethods.dollars(200), INSTANT_1010), stockId3, EventType.SALE)));
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
              new Event(new RealizableValue(StaticTestHelperMethods.dollars(100), INSTANT_1000), stockId1, EventType.ACQUIRE),
              new Event(new RealizableValue(StaticTestHelperMethods.dollars(110), INSTANT_1003), stockId1, EventType.ACQUIRE),
              new Event(new RealizableValue(StaticTestHelperMethods.dollars(110), INSTANT_1005), stockId2, EventType.ACQUIRE)),
          ImmutableList.of(
              new Event(new RealizableValue(StaticTestHelperMethods.dollars(40), INSTANT_1004), stockId1, EventType.SALE),
              new Event(new RealizableValue(StaticTestHelperMethods.dollars(200), INSTANT_1010), stockId2, EventType.SALE)));
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
              new Event(new RealizableValue(StaticTestHelperMethods.dollars(100), INSTANT_1000), stockId1, EventType.ACQUIRE),
              new Event(new RealizableValue(StaticTestHelperMethods.dollars(110), INSTANT_1005), stockId2, EventType.ACQUIRE)),
          ImmutableList.of(
              new Event(new RealizableValue(StaticTestHelperMethods.dollars(40), INSTANT_1004), stockId1, EventType.SALE),
              new Event(new RealizableValue(StaticTestHelperMethods.dollars(200), INSTANT_1010), stockId2, EventType.SALE),
              new Event(new RealizableValue(StaticTestHelperMethods.dollars(400), INSTANT_2000), stockId2, EventType.SALE)));
      fail("Expected an error because stock 2 was sold twice");
    } catch (IllegalArgumentException expected) { /* expected */ }
  }

  @Test
  public void testTwoStockCalculationWithWashSale() {
    WashSaleCalculator calculator = new WashSaleCalculator();
    final StockId stockId1 = new StockId(1);
    final StockId stockId2 = new StockId(2);
    Iterable<Result> results = calculator.calculate(
        ImmutableList.of(
            new Event(new RealizableValue(StaticTestHelperMethods.dollars(100), INSTANT_1000), stockId1, EventType.ACQUIRE),
            new Event(new RealizableValue(StaticTestHelperMethods.dollars(110), INSTANT_1005), stockId2, EventType.ACQUIRE)),
        ImmutableList.of(
            new Event(new RealizableValue(StaticTestHelperMethods.dollars(40), INSTANT_1004), stockId1, EventType.SALE),
            new Event(new RealizableValue(StaticTestHelperMethods.dollars(200), INSTANT_1010), stockId2, EventType.SALE)));
    Iterator<Result> iterator = results.iterator();
    StaticTestHelperMethods.assertEquals(
        new Result(
        stockId1,
        new RealizableValue(StaticTestHelperMethods.dollars(100), INSTANT_1000),
        null,
        null,
        new RealizableValue(StaticTestHelperMethods.dollars(40), INSTANT_1004),
        StaticTestHelperMethods.dollars(60),
        true,
        stockId2,
        StaticTestHelperMethods.dollars(0)),
        iterator.next());
    StaticTestHelperMethods.assertEquals(
        new Result(
        stockId2,
        new RealizableValue(StaticTestHelperMethods.dollars(110), INSTANT_1005),
        new AcquisitionAdjustment(StaticTestHelperMethods.dollars(60), INSTANT_1000),
        stockId1,
        new RealizableValue(StaticTestHelperMethods.dollars(200), INSTANT_1010),
        StaticTestHelperMethods.dollars(0),
        false,
        null,
        StaticTestHelperMethods.dollars(30)),
        iterator.next());
  }

  @Test
  public void testTwoStockCalculationNoWashSales() {
    WashSaleCalculator calculator = new WashSaleCalculator();
    final StockId stockId1 = new StockId(1);
    final StockId stockId2 = new StockId(2);
    Iterable<Result> results = calculator.calculate(
        ImmutableList.of(
            new Event(new RealizableValue(StaticTestHelperMethods.dollars(100), INSTANT_1000), stockId1, EventType.ACQUIRE),
            new Event(new RealizableValue(StaticTestHelperMethods.dollars(110), INSTANT_1010), stockId2, EventType.ACQUIRE)),
        ImmutableList.of(
            new Event(new RealizableValue(StaticTestHelperMethods.dollars(40), INSTANT_1004), stockId1, EventType.SALE),
            new Event(new RealizableValue(StaticTestHelperMethods.dollars(200), INSTANT_1015), stockId2, EventType.SALE)));
    Iterator<Result> iterator = results.iterator();
    StaticTestHelperMethods.assertEquals(
        new Result(
        stockId1,
        new RealizableValue(StaticTestHelperMethods.dollars(100), INSTANT_1000),
        null,
        null,
        new RealizableValue(StaticTestHelperMethods.dollars(40), INSTANT_1004),
        StaticTestHelperMethods.dollars(0),
        false,
        null,
        StaticTestHelperMethods.dollars(-60)),
        iterator.next());
    StaticTestHelperMethods.assertEquals(
        new Result(
        stockId2,
        new RealizableValue(StaticTestHelperMethods.dollars(110), INSTANT_1010),
        null,
        null,
        new RealizableValue(StaticTestHelperMethods.dollars(200), INSTANT_1015),
        StaticTestHelperMethods.dollars(0),
        false,
        null,
        StaticTestHelperMethods.dollars(90)),
        iterator.next());
  }

  @Test
  public void testTwoStockCalculationWithAcquireBeforeSale() {
    WashSaleCalculator calculator = new WashSaleCalculator();
    final StockId stockId1 = new StockId(1);
    final StockId stockId2 = new StockId(2);
    Iterable<Result> results = calculator.calculate(
        ImmutableList.of(
            new Event(new RealizableValue(StaticTestHelperMethods.dollars(100), INSTANT_1000), stockId1, EventType.ACQUIRE),
            new Event(new RealizableValue(StaticTestHelperMethods.dollars(110), INSTANT_1004), stockId2, EventType.ACQUIRE)),
        ImmutableList.of(
            new Event(new RealizableValue(StaticTestHelperMethods.dollars(40), INSTANT_1005), stockId1, EventType.SALE),
            new Event(new RealizableValue(StaticTestHelperMethods.dollars(200), INSTANT_1010), stockId2, EventType.SALE)));
    Iterator<Result> iterator = results.iterator();
    StaticTestHelperMethods.assertEquals(
        new Result(
        stockId1,
        new RealizableValue(StaticTestHelperMethods.dollars(100), INSTANT_1000),
        null,
        null,
        new RealizableValue(StaticTestHelperMethods.dollars(40), INSTANT_1005),
        StaticTestHelperMethods.dollars(60),
        true,
        stockId2,
        StaticTestHelperMethods.dollars(0)),
        iterator.next());
    StaticTestHelperMethods.assertEquals(
        new Result(
        stockId2,
        new RealizableValue(StaticTestHelperMethods.dollars(110), INSTANT_1004),
        new AcquisitionAdjustment(StaticTestHelperMethods.dollars(60), INSTANT_1000),
        stockId1,
        new RealizableValue(StaticTestHelperMethods.dollars(200), INSTANT_1010),
        StaticTestHelperMethods.dollars(0),
        false,
        null,
        StaticTestHelperMethods.dollars(30)),
        iterator.next());
  }

  @Test
  public void testTwoStockCalculationWithDifferentValues() {
    WashSaleCalculator calculator = new WashSaleCalculator();
    final StockId stockId1 = new StockId(1);
    final StockId stockId2 = new StockId(2);
    Iterable<Result> results = calculator.calculate(
        ImmutableList.of(
            new Event(new RealizableValue(StaticTestHelperMethods.dollars(130), INSTANT_1000), stockId1, EventType.ACQUIRE),
            new Event(new RealizableValue(StaticTestHelperMethods.dollars(110), INSTANT_1005), stockId2, EventType.ACQUIRE)),
        ImmutableList.of(
            new Event(new RealizableValue(StaticTestHelperMethods.dollars(40), INSTANT_1004), stockId1, EventType.SALE),
            new Event(new RealizableValue(StaticTestHelperMethods.dollars(200), INSTANT_1010), stockId2, EventType.SALE)));
    Iterator<Result> iterator = results.iterator();
    StaticTestHelperMethods.assertEquals(
        new Result(
        stockId1,
        new RealizableValue(StaticTestHelperMethods.dollars(130), INSTANT_1000),
        null,
        null,
        new RealizableValue(StaticTestHelperMethods.dollars(40), INSTANT_1004),
        StaticTestHelperMethods.dollars(90),
        true,
        stockId2,
        StaticTestHelperMethods.dollars(0)),
        iterator.next());
    StaticTestHelperMethods.assertEquals(
        new Result(
        stockId2,
        new RealizableValue(StaticTestHelperMethods.dollars(110), INSTANT_1005),
        new AcquisitionAdjustment(StaticTestHelperMethods.dollars(90), INSTANT_1000),
        stockId1,
        new RealizableValue(StaticTestHelperMethods.dollars(200), INSTANT_1010),
        StaticTestHelperMethods.dollars(0),
        false,
        null,
        StaticTestHelperMethods.dollars(0)),
        iterator.next());
  }

  @Test
  public void testTwoWashSalesAccumulateInThirdSale() {
    WashSaleCalculator calculator = new WashSaleCalculator();
    final StockId stockId1 = new StockId(1);
    final StockId stockId2 = new StockId(2);
    final StockId stockId3 = new StockId(3);
    Iterable<Result> results = calculator.calculate(
        ImmutableList.of(
            new Event(new RealizableValue(StaticTestHelperMethods.dollars(100), INSTANT_1000), stockId1, EventType.ACQUIRE),
            new Event(new RealizableValue(StaticTestHelperMethods.dollars(60), INSTANT_1005), stockId2, EventType.ACQUIRE),
            new Event(new RealizableValue(StaticTestHelperMethods.dollars(45), INSTANT_1011), stockId3, EventType.ACQUIRE)),
        ImmutableList.of(
            new Event(new RealizableValue(StaticTestHelperMethods.dollars(50), INSTANT_1004), stockId1, EventType.SALE),
            new Event(new RealizableValue(StaticTestHelperMethods.dollars(40), INSTANT_1010), stockId2, EventType.SALE),
            new Event(new RealizableValue(StaticTestHelperMethods.dollars(46), INSTANT_1013), stockId3, EventType.SALE)));
    Iterator<Result> iterator = results.iterator();
    StaticTestHelperMethods.assertEquals(
        new Result(
        stockId1,
        new RealizableValue(StaticTestHelperMethods.dollars(100), INSTANT_1000),
        null,
        null,
        new RealizableValue(StaticTestHelperMethods.dollars(50), INSTANT_1004),
        StaticTestHelperMethods.dollars(50),
        true,
        stockId2,
        StaticTestHelperMethods.dollars(0)),
        iterator.next());
    StaticTestHelperMethods.assertEquals(
        new Result(
        stockId2,
        new RealizableValue(StaticTestHelperMethods.dollars(60), INSTANT_1005),
        new AcquisitionAdjustment(StaticTestHelperMethods.dollars(50), INSTANT_1000),
        stockId1,
        new RealizableValue(StaticTestHelperMethods.dollars(40), INSTANT_1010),
        StaticTestHelperMethods.dollars(70),
        true,
        stockId3,
        StaticTestHelperMethods.dollars(0)),
        iterator.next());
    StaticTestHelperMethods.assertEquals(
        new Result(
        stockId3,
        new RealizableValue(StaticTestHelperMethods.dollars(45), INSTANT_1011),
        new AcquisitionAdjustment(StaticTestHelperMethods.dollars(70), INSTANT_1000),
        stockId2,
        new RealizableValue(StaticTestHelperMethods.dollars(46), INSTANT_1013),
        StaticTestHelperMethods.dollars(0),
        false,
        null,
        StaticTestHelperMethods.dollars(-69)),
        iterator.next());
  }

  @Test
  public void testTwoWashSalesAccumulateInThirdSaleWithAcquiresBeforeSales() {
    WashSaleCalculator calculator = new WashSaleCalculator();
    final StockId stockId1 = new StockId(1);
    final StockId stockId2 = new StockId(2);
    final StockId stockId3 = new StockId(3);
    Iterable<Result> results = calculator.calculate(
        ImmutableList.of(
            new Event(new RealizableValue(StaticTestHelperMethods.dollars(100), INSTANT_1000), stockId1, EventType.ACQUIRE),
            new Event(new RealizableValue(StaticTestHelperMethods.dollars(60), INSTANT_1004), stockId2, EventType.ACQUIRE),
            new Event(new RealizableValue(StaticTestHelperMethods.dollars(45), INSTANT_1010), stockId3, EventType.ACQUIRE)),
        ImmutableList.of(
            new Event(new RealizableValue(StaticTestHelperMethods.dollars(50), INSTANT_1005), stockId1, EventType.SALE),
            new Event(new RealizableValue(StaticTestHelperMethods.dollars(40), INSTANT_1011), stockId2, EventType.SALE),
            new Event(new RealizableValue(StaticTestHelperMethods.dollars(46), INSTANT_1013), stockId3, EventType.SALE)));
    Iterator<Result> iterator = results.iterator();
    StaticTestHelperMethods.assertEquals(
        new Result(
        stockId1,
        new RealizableValue(StaticTestHelperMethods.dollars(100), INSTANT_1000),
        null,
        null,
        new RealizableValue(StaticTestHelperMethods.dollars(50), INSTANT_1005),
        StaticTestHelperMethods.dollars(50),
        true,
        stockId2,
        StaticTestHelperMethods.dollars(0)),
        iterator.next());
    StaticTestHelperMethods.assertEquals(
        new Result(
        stockId2,
        new RealizableValue(StaticTestHelperMethods.dollars(60), INSTANT_1004),
        new AcquisitionAdjustment(StaticTestHelperMethods.dollars(50), INSTANT_1000),
        stockId1,
        new RealizableValue(StaticTestHelperMethods.dollars(40), INSTANT_1011),
        StaticTestHelperMethods.dollars(70),
        true,
        stockId3,
        StaticTestHelperMethods.dollars(0)),
        iterator.next());
    StaticTestHelperMethods.assertEquals(
        new Result(
        stockId3,
        new RealizableValue(StaticTestHelperMethods.dollars(45), INSTANT_1010),
        new AcquisitionAdjustment(StaticTestHelperMethods.dollars(70), INSTANT_1000),
        stockId2,
        new RealizableValue(StaticTestHelperMethods.dollars(46), INSTANT_1013),
        StaticTestHelperMethods.dollars(0),
        false,
        null,
        StaticTestHelperMethods.dollars(-69)),
        iterator.next());
  }

  @Test
  public void testBoughtASingleStock() {
    WashSaleCalculator calculator = new WashSaleCalculator();
    final StockId stockId1 = new StockId(1);
    Iterable<Result> results = calculator.calculate(
        ImmutableList.of(
            new Event(new RealizableValue(StaticTestHelperMethods.dollars(100), INSTANT_1000), stockId1, EventType.ACQUIRE)),
        Collections.<Event>emptySet());
    Iterator<Result> iterator = results.iterator();
    StaticTestHelperMethods.assertEquals(
        new Result(
        stockId1,
        new RealizableValue(StaticTestHelperMethods.dollars(100), INSTANT_1000),
        null,
        null,
        null,
        null,
        false,
        null,
        null),
        iterator.next());
  }

  @Test
  public void testTwoStockCalculationWithNotAllStockSoldAtEnd() {
    WashSaleCalculator calculator = new WashSaleCalculator();
    final StockId stockId1 = new StockId(1);
    final StockId stockId2 = new StockId(2);
    Iterable<Result> results = calculator.calculate(
        ImmutableList.of(
            new Event(new RealizableValue(StaticTestHelperMethods.dollars(100), INSTANT_1000), stockId1, EventType.ACQUIRE),
            new Event(new RealizableValue(StaticTestHelperMethods.dollars(110), INSTANT_1005), stockId2, EventType.ACQUIRE)),
        ImmutableList.of(
            new Event(new RealizableValue(StaticTestHelperMethods.dollars(40), INSTANT_1004), stockId1, EventType.SALE)));
    Iterator<Result> iterator = results.iterator();
    StaticTestHelperMethods.assertEquals(
        new Result(
        stockId1,
        new RealizableValue(StaticTestHelperMethods.dollars(100), INSTANT_1000),
        null,
        null,
        new RealizableValue(StaticTestHelperMethods.dollars(40), INSTANT_1004),
        StaticTestHelperMethods.dollars(60),
        true,
        stockId2,
        StaticTestHelperMethods.dollars(0)),
        iterator.next());
    StaticTestHelperMethods.assertEquals(
        new Result(
        stockId2,
        new RealizableValue(StaticTestHelperMethods.dollars(110), INSTANT_1005),
        new AcquisitionAdjustment(StaticTestHelperMethods.dollars(60), INSTANT_1000),
        stockId1,
        null,
        null,
        false,
        null,
        null),
        iterator.next());
  }


  @Test
  public void testTwoWashSalesAccumulateInThirdStockThatIsntSold() {
    WashSaleCalculator calculator = new WashSaleCalculator();
    final StockId stockId1 = new StockId(1);
    final StockId stockId2 = new StockId(2);
    final StockId stockId3 = new StockId(3);
    Iterable<Result> results = calculator.calculate(
        ImmutableList.of(
            new Event(new RealizableValue(StaticTestHelperMethods.dollars(100), INSTANT_1000), stockId1, EventType.ACQUIRE),
            new Event(new RealizableValue(StaticTestHelperMethods.dollars(60), INSTANT_1004), stockId2, EventType.ACQUIRE),
            new Event(new RealizableValue(StaticTestHelperMethods.dollars(45), INSTANT_1010), stockId3, EventType.ACQUIRE)),
        ImmutableList.of(
            new Event(new RealizableValue(StaticTestHelperMethods.dollars(50), INSTANT_1005), stockId1, EventType.SALE),
            new Event(new RealizableValue(StaticTestHelperMethods.dollars(40), INSTANT_1011), stockId2, EventType.SALE)));
    Iterator<Result> iterator = results.iterator();
    StaticTestHelperMethods.assertEquals(
        new Result(
        stockId1,
        new RealizableValue(StaticTestHelperMethods.dollars(100), INSTANT_1000),
        null,
        null,
        new RealizableValue(StaticTestHelperMethods.dollars(50), INSTANT_1005),
        StaticTestHelperMethods.dollars(50),
        true,
        stockId2,
        StaticTestHelperMethods.dollars(0)),
        iterator.next());
    StaticTestHelperMethods.assertEquals(
        new Result(
        stockId2,
        new RealizableValue(StaticTestHelperMethods.dollars(60), INSTANT_1004),
        new AcquisitionAdjustment(StaticTestHelperMethods.dollars(50), INSTANT_1000),
        stockId1,
        new RealizableValue(StaticTestHelperMethods.dollars(40), INSTANT_1011),
        StaticTestHelperMethods.dollars(70),
        true,
        stockId3,
        StaticTestHelperMethods.dollars(0)),
        iterator.next());
    StaticTestHelperMethods.assertEquals(
        new Result(
        stockId3,
        new RealizableValue(StaticTestHelperMethods.dollars(45), INSTANT_1010),
        new AcquisitionAdjustment(StaticTestHelperMethods.dollars(70), INSTANT_1000),
        stockId2,
        null,
        null,
        false,
        null,
        null),
        iterator.next());
  }
}
