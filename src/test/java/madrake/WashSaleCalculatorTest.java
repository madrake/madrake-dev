package madrake;

import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.Iterator;

import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

// TODO(madrake): would helper methods make this class more readable?

public class WashSaleCalculatorTest {

  private static final DateTimeZone ZONE = DateTimeZone.forID("America/Los_Angeles");
  private static final Instant INSTANT_1000 = Instant.parse("2012-10-01");
  private static final Instant INSTANT_1003 = Instant.parse("2012-11-03");
  private static final Instant INSTANT_1004 = Instant.parse("2012-11-15");
  private static final Instant INSTANT_1005 = Instant.parse("2012-12-03");
  private static final Instant INSTANT_1010 = Instant.parse("2013-04-01");
  private static final Instant INSTANT_1011 = Instant.parse("2013-04-30");
  private static final Instant INSTANT_1013 = Instant.parse("2013-06-02");
  private static final Instant INSTANT_1015 = Instant.parse("2013-08-03");
  private static final Instant INSTANT_2000 = Instant.parse("2015-10-10");

  // TODO(madrake): !!IMPORTANT change code so that the holding period changes as well.
  // It might now....but tests don't reflect this

  @Test
  public void testSanityCheckInputWhenMissingStockAcquisition() {
    WashSaleCalculator calculator = new WashSaleCalculator(ZONE);
    final StockId stockId1 = StockId.create(1);
    final StockId stockId2 = StockId.create(2);
    final StockId stockId3 = StockId.create(3);
    try {
      calculator.calculate(
          ImmutableList.of(
              Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(100), INSTANT_1000), stockId1, EventType.ACQUIRE),
              Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(110), INSTANT_1005), stockId2, EventType.ACQUIRE)),
          ImmutableList.of(
              Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(40), INSTANT_1004), stockId1, EventType.SALE),
              Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(200), INSTANT_1010), stockId3, EventType.SALE)));
      fail("Expected an error because of invalid input - stock3 sold but "
          + "not acquired and stock2 acquired but not sold");
    } catch (IllegalArgumentException expected) { /* expected */ }
  }

  @Test
  public void testSanityCheckInputWhenDuplicateDataForStockAcquisition() {
    WashSaleCalculator calculator = new WashSaleCalculator(ZONE);
    final StockId stockId1 = StockId.create(1);
    final StockId stockId2 = StockId.create(2);
    try {
      calculator.calculate(
          ImmutableList.of(
              Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(100), INSTANT_1000), stockId1, EventType.ACQUIRE),
              Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(110), INSTANT_1003), stockId1, EventType.ACQUIRE),
              Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(110), INSTANT_1005), stockId2, EventType.ACQUIRE)),
          ImmutableList.of(
              Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(40), INSTANT_1004), stockId1, EventType.SALE),
              Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(200), INSTANT_1010), stockId2, EventType.SALE)));
      fail("Expected an error because stock 2 was acquired twice");
    } catch (IllegalArgumentException expected) { /* expected */ }
  }

  @Test
  public void testSanityCheckInputWhenDuplicateDataForStockSale() {
    WashSaleCalculator calculator = new WashSaleCalculator(ZONE);
    final StockId stockId1 = StockId.create(1);
    final StockId stockId2 = StockId.create(2);
    try {
      calculator.calculate(
          ImmutableList.of(
              Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(100), INSTANT_1000), stockId1, EventType.ACQUIRE),
              Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(110), INSTANT_1005), stockId2, EventType.ACQUIRE)),
          ImmutableList.of(
              Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(40), INSTANT_1004), stockId1, EventType.SALE),
              Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(200), INSTANT_1010), stockId2, EventType.SALE),
              Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(400), INSTANT_2000), stockId2, EventType.SALE)));
      fail("Expected an error because stock 2 was sold twice");
    } catch (IllegalArgumentException expected) { /* expected */ }
  }

  @Test
  public void testTwoStockCalculationWithWashSale() {
    WashSaleCalculator calculator = new WashSaleCalculator(ZONE);
    final StockId stockId1 = StockId.create(1);
    final StockId stockId2 = StockId.create(2);
    Iterable<Result> results = calculator.calculate(
        ImmutableList.of(
            Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(100), INSTANT_1000), stockId1, EventType.ACQUIRE),
            Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(110), INSTANT_1005), stockId2, EventType.ACQUIRE)),
        ImmutableList.of(
            Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(40), INSTANT_1004), stockId1, EventType.SALE),
            Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(200), INSTANT_1010), stockId2, EventType.SALE)));
    Iterator<Result> iterator = results.iterator();
    StaticTestHelperMethods.assertEquals(
        Result.create(
            stockId1,
            RealizableValue.create(StaticTestHelperMethods.dollars(100), INSTANT_1000),
            null,
            null,
            RealizableValue.create(StaticTestHelperMethods.dollars(40), INSTANT_1004),
            true,
            stockId2,
            StaticTestHelperMethods.dollars(0)),
        iterator.next());
    StaticTestHelperMethods.assertEquals(
        Result.create(
            stockId2,
            RealizableValue.create(StaticTestHelperMethods.dollars(110), INSTANT_1005),
            AcquisitionAdjustment.create(StaticTestHelperMethods.dollars(60), INSTANT_1000),
            stockId1,
            RealizableValue.create(StaticTestHelperMethods.dollars(200), INSTANT_1010),
            false,
            null,
            StaticTestHelperMethods.dollars(30)),
        iterator.next());
  }

  @Test
  public void testTwoStockCalculationNoWashSales() {
    WashSaleCalculator calculator = new WashSaleCalculator(ZONE);
    final StockId stockId1 = StockId.create(1);
    final StockId stockId2 = StockId.create(2);
    Iterable<Result> results = calculator.calculate(
        ImmutableList.of(
            Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(100), INSTANT_1000), stockId1, EventType.ACQUIRE),
            Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(110), INSTANT_1010), stockId2, EventType.ACQUIRE)),
        ImmutableList.of(
            Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(40), INSTANT_1004), stockId1, EventType.SALE),
            Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(200), INSTANT_1015), stockId2, EventType.SALE)));
    Iterator<Result> iterator = results.iterator();
    StaticTestHelperMethods.assertEquals(
        Result.create(
            stockId1,
            RealizableValue.create(StaticTestHelperMethods.dollars(100), INSTANT_1000),
            null,
            null,
            RealizableValue.create(StaticTestHelperMethods.dollars(40), INSTANT_1004),
            false,
            null,
            StaticTestHelperMethods.dollars(-60)),
        iterator.next());
    StaticTestHelperMethods.assertEquals(
        Result.create(
            stockId2,
            RealizableValue.create(StaticTestHelperMethods.dollars(110), INSTANT_1010),
            null,
            null,
            RealizableValue.create(StaticTestHelperMethods.dollars(200), INSTANT_1015),
            false,
            null,
            StaticTestHelperMethods.dollars(90)),
        iterator.next());
  }

  @Test
  public void testTwoStockCalculationWithAcquireBeforeSale() {
    WashSaleCalculator calculator = new WashSaleCalculator(ZONE);
    final StockId stockId1 = StockId.create(1);
    final StockId stockId2 = StockId.create(2);
    Iterable<Result> results = calculator.calculate(
        ImmutableList.of(
            Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(100), INSTANT_1000), stockId1, EventType.ACQUIRE),
            Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(110), INSTANT_1004), stockId2, EventType.ACQUIRE)),
        ImmutableList.of(
            Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(40), INSTANT_1005), stockId1, EventType.SALE),
            Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(200), INSTANT_1010), stockId2, EventType.SALE)));
    Iterator<Result> iterator = results.iterator();
    StaticTestHelperMethods.assertEquals(
        Result.create(
            stockId1,
            RealizableValue.create(StaticTestHelperMethods.dollars(100), INSTANT_1000),
            null,
            null,
            RealizableValue.create(StaticTestHelperMethods.dollars(40), INSTANT_1005),
            true,
            stockId2,
            StaticTestHelperMethods.dollars(0)),
        iterator.next());
    StaticTestHelperMethods.assertEquals(
        Result.create(
            stockId2,
            RealizableValue.create(StaticTestHelperMethods.dollars(110), INSTANT_1004),
            AcquisitionAdjustment.create(StaticTestHelperMethods.dollars(60), INSTANT_1000),
            stockId1,
            RealizableValue.create(StaticTestHelperMethods.dollars(200), INSTANT_1010),
            false,
            null,
            StaticTestHelperMethods.dollars(30)),
        iterator.next());
  }

  @Test
  public void testTwoStockCalculationWithDifferentValues() {
    WashSaleCalculator calculator = new WashSaleCalculator(ZONE);
    final StockId stockId1 = StockId.create(1);
    final StockId stockId2 = StockId.create(2);
    Iterable<Result> results = calculator.calculate(
        ImmutableList.of(
            Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(130), INSTANT_1000), stockId1, EventType.ACQUIRE),
            Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(110), INSTANT_1005), stockId2, EventType.ACQUIRE)),
        ImmutableList.of(
            Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(40), INSTANT_1004), stockId1, EventType.SALE),
            Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(200), INSTANT_1010), stockId2, EventType.SALE)));
    Iterator<Result> iterator = results.iterator();
    StaticTestHelperMethods.assertEquals(
        Result.create(
            stockId1,
            RealizableValue.create(StaticTestHelperMethods.dollars(130), INSTANT_1000),
            null,
            null,
            RealizableValue.create(StaticTestHelperMethods.dollars(40), INSTANT_1004),
            true,
            stockId2,
            StaticTestHelperMethods.dollars(0)),
        iterator.next());
    StaticTestHelperMethods.assertEquals(
        Result.create(
            stockId2,
            RealizableValue.create(StaticTestHelperMethods.dollars(110), INSTANT_1005),
            AcquisitionAdjustment.create(StaticTestHelperMethods.dollars(90), INSTANT_1000),
            stockId1,
            RealizableValue.create(StaticTestHelperMethods.dollars(200), INSTANT_1010),
            false,
            null,
            StaticTestHelperMethods.dollars(0)),
        iterator.next());
  }

  @Test
  public void testTwoWashSalesAccumulateInThirdSale() {
    WashSaleCalculator calculator = new WashSaleCalculator(ZONE);
    final StockId stockId1 = StockId.create(1);
    final StockId stockId2 = StockId.create(2);
    final StockId stockId3 = StockId.create(3);
    Iterable<Result> results = calculator.calculate(
        ImmutableList.of(
            Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(100), INSTANT_1000), stockId1, EventType.ACQUIRE),
            Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(60), INSTANT_1005), stockId2, EventType.ACQUIRE),
            Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(45), INSTANT_1011), stockId3, EventType.ACQUIRE)),
        ImmutableList.of(
            Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(50), INSTANT_1004), stockId1, EventType.SALE),
            Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(40), INSTANT_1010), stockId2, EventType.SALE),
            Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(46), INSTANT_1013), stockId3, EventType.SALE)));
    Iterator<Result> iterator = results.iterator();
    StaticTestHelperMethods.assertEquals(
        Result.create(
            stockId1,
            RealizableValue.create(StaticTestHelperMethods.dollars(100), INSTANT_1000),
            null,
            null,
            RealizableValue.create(StaticTestHelperMethods.dollars(50), INSTANT_1004),
            true,
            stockId2,
            StaticTestHelperMethods.dollars(0)),
        iterator.next());
    StaticTestHelperMethods.assertEquals(
        Result.create(
            stockId2,
            RealizableValue.create(StaticTestHelperMethods.dollars(60), INSTANT_1005),
            AcquisitionAdjustment.create(StaticTestHelperMethods.dollars(50), INSTANT_1000),
            stockId1,
            RealizableValue.create(StaticTestHelperMethods.dollars(40), INSTANT_1010),
            true,
            stockId3,
            StaticTestHelperMethods.dollars(0)),
        iterator.next());
    StaticTestHelperMethods.assertEquals(
        Result.create(
            stockId3,
            RealizableValue.create(StaticTestHelperMethods.dollars(45), INSTANT_1011),
            AcquisitionAdjustment.create(StaticTestHelperMethods.dollars(70), INSTANT_1000),
            stockId2,
            RealizableValue.create(StaticTestHelperMethods.dollars(46), INSTANT_1013),
            false,
            null,
            StaticTestHelperMethods.dollars(-69)),
        iterator.next());
  }

  @Test
  public void testTwoWashSalesAccumulateInThirdSaleWithAcquiresBeforeSales() {
    WashSaleCalculator calculator = new WashSaleCalculator(ZONE);
    final StockId stockId1 = StockId.create(1);
    final StockId stockId2 = StockId.create(2);
    final StockId stockId3 = StockId.create(3);
    Iterable<Result> results = calculator.calculate(
        ImmutableList.of(
            Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(100), INSTANT_1000), stockId1, EventType.ACQUIRE),
            Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(60), INSTANT_1004), stockId2, EventType.ACQUIRE),
            Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(45), INSTANT_1010), stockId3, EventType.ACQUIRE)),
        ImmutableList.of(
            Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(50), INSTANT_1005), stockId1, EventType.SALE),
            Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(40), INSTANT_1011), stockId2, EventType.SALE),
            Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(46), INSTANT_1013), stockId3, EventType.SALE)));
    Iterator<Result> iterator = results.iterator();
    StaticTestHelperMethods.assertEquals(
        Result.create(
            stockId1,
            RealizableValue.create(StaticTestHelperMethods.dollars(100), INSTANT_1000),
            null,
            null,
            RealizableValue.create(StaticTestHelperMethods.dollars(50), INSTANT_1005),
            true,
            stockId2,
            StaticTestHelperMethods.dollars(0)),
        iterator.next());
    StaticTestHelperMethods.assertEquals(
        Result.create(
            stockId2,
            RealizableValue.create(StaticTestHelperMethods.dollars(60), INSTANT_1004),
            AcquisitionAdjustment.create(StaticTestHelperMethods.dollars(50), INSTANT_1000),
            stockId1,
            RealizableValue.create(StaticTestHelperMethods.dollars(40), INSTANT_1011),
            true,
            stockId3,
            StaticTestHelperMethods.dollars(0)),
        iterator.next());
    StaticTestHelperMethods.assertEquals(
        Result.create(
            stockId3,
            RealizableValue.create(StaticTestHelperMethods.dollars(45), INSTANT_1010),
            AcquisitionAdjustment.create(StaticTestHelperMethods.dollars(70), INSTANT_1000),
            stockId2,
            RealizableValue.create(StaticTestHelperMethods.dollars(46), INSTANT_1013),
            false,
            null,
            StaticTestHelperMethods.dollars(-69)),
        iterator.next());
  }

  @Test
  public void testBoughtASingleStock() {
    WashSaleCalculator calculator = new WashSaleCalculator(ZONE);
    final StockId stockId1 = StockId.create(1);
    Iterable<Result> results = calculator.calculate(
        ImmutableList.of(
            Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(100), INSTANT_1000), stockId1, EventType.ACQUIRE)),
        Collections.<Event>emptySet());
    Iterator<Result> iterator = results.iterator();
    StaticTestHelperMethods.assertEquals(
        Result.create(
            stockId1,
            RealizableValue.create(StaticTestHelperMethods.dollars(100), INSTANT_1000),
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
    WashSaleCalculator calculator = new WashSaleCalculator(ZONE);
    final StockId stockId1 = StockId.create(1);
    final StockId stockId2 = StockId.create(2);
    Iterable<Result> results = calculator.calculate(
        ImmutableList.of(
            Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(100), INSTANT_1000), stockId1, EventType.ACQUIRE),
            Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(110), INSTANT_1005), stockId2, EventType.ACQUIRE)),
        ImmutableList.of(
            Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(40), INSTANT_1004), stockId1, EventType.SALE)));
    Iterator<Result> iterator = results.iterator();
    StaticTestHelperMethods.assertEquals(
        Result.create(
           stockId1,
           RealizableValue.create(StaticTestHelperMethods.dollars(100), INSTANT_1000),
           null,
           null,
           RealizableValue.create(StaticTestHelperMethods.dollars(40), INSTANT_1004),
           true,
           stockId2,
           StaticTestHelperMethods.dollars(0)),
        iterator.next());
    StaticTestHelperMethods.assertEquals(
        Result.create(
            stockId2,
            RealizableValue.create(StaticTestHelperMethods.dollars(110), INSTANT_1005),
            AcquisitionAdjustment.create(StaticTestHelperMethods.dollars(60), INSTANT_1000),
            stockId1,
            null,
            false,
            null,
            null),
        iterator.next());
  }


  @Test
  public void testTwoWashSalesAccumulateInThirdStockThatIsntSold() {
    WashSaleCalculator calculator = new WashSaleCalculator(ZONE);
    final StockId stockId1 = StockId.create(1);
    final StockId stockId2 = StockId.create(2);
    final StockId stockId3 = StockId.create(3);
    Iterable<Result> results = calculator.calculate(
        ImmutableList.of(
            Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(100), INSTANT_1000), stockId1, EventType.ACQUIRE),
            Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(60), INSTANT_1004), stockId2, EventType.ACQUIRE),
            Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(45), INSTANT_1010), stockId3, EventType.ACQUIRE)),
        ImmutableList.of(
            Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(50), INSTANT_1005), stockId1, EventType.SALE),
            Event.create(RealizableValue.create(StaticTestHelperMethods.dollars(40), INSTANT_1011), stockId2, EventType.SALE)));
    Iterator<Result> iterator = results.iterator();
    StaticTestHelperMethods.assertEquals(
        Result.create(
            stockId1,
            RealizableValue.create(StaticTestHelperMethods.dollars(100), INSTANT_1000),
            null,
            null,
            RealizableValue.create(StaticTestHelperMethods.dollars(50), INSTANT_1005),
            true,
            stockId2,
            StaticTestHelperMethods.dollars(0)),
        iterator.next());
    StaticTestHelperMethods.assertEquals(
        Result.create(
            stockId2,
            RealizableValue.create(StaticTestHelperMethods.dollars(60), INSTANT_1004),
            AcquisitionAdjustment.create(StaticTestHelperMethods.dollars(50), INSTANT_1000),
            stockId1,
            RealizableValue.create(StaticTestHelperMethods.dollars(40), INSTANT_1011),
            true,
            stockId3,
            StaticTestHelperMethods.dollars(0)),
        iterator.next());
    StaticTestHelperMethods.assertEquals(
        Result.create(
            stockId3,
            RealizableValue.create(StaticTestHelperMethods.dollars(45), INSTANT_1010),
            AcquisitionAdjustment.create(StaticTestHelperMethods.dollars(70), INSTANT_1000),
            stockId2,
            null,
            false,
            null,
            null),
        iterator.next());
  }
}
