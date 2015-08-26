package madrake;

import static org.junit.Assert.*;

import org.joda.time.Instant;
import org.junit.Test;

public class WithinWashSaleWindowPredicateTest {

  // TODO(madrake): Check to see the precise wash sale timing. Is it 30 days and calendar days or
  // window time? The current implementation is 30*24 hours and the tests at granularity of days. 
  // We should improve the tests and implementation based on ruling.
  
  @Test
  public void testSaleIsExactly30DaysBeforePurchase_IsWashSale() {
    assertTrue(acquireAndSaleWithinWindow("2015-06-01", "2015-07-01"));
  }

  @Test
  public void testSaleIsExactly30DaysAfterPurchase_IsWashSale() {
    assertTrue(acquireAndSaleWithinWindow("2015-07-01", "2015-06-01"));
  }
  
  @Test
  public void testSaleIsExactly29DaysBeforePurchase_IsWashSale() {
    assertTrue(acquireAndSaleWithinWindow("2015-06-02", "2015-07-01"));
  }

  @Test
  public void testSaleIsExactly29DaysAfterPurchase_IsWashSale() {
    assertTrue(acquireAndSaleWithinWindow("2015-07-01", "2015-06-02"));
  }
  
  @Test
  public void testSaleIsExactly1DaysAfterPurchase_IsWashSale() {
    assertTrue(acquireAndSaleWithinWindow("2015-07-01", "2015-07-02"));
  }
  
  @Test
  public void testSaleIsExactly1DaysBeforePurchase_IsWashSale() {
    assertTrue(acquireAndSaleWithinWindow("2015-07-01", "2015-06-30"));
  }
  
  @Test
  public void testSaleIsSameDay_IsWashSale() {
    assertTrue(acquireAndSaleWithinWindow("2015-07-01", "2015-07-01"));
  }
  
  @Test
  public void testSaleIsExactly31DaysBeforePurchase_IsntWashSale() {
    assertFalse(acquireAndSaleWithinWindow("2015-05-31", "2015-07-01"));
  }

  @Test
  public void testSaleIsExactly31DaysAfterPurchase_IsntWashSale() {
    assertFalse(acquireAndSaleWithinWindow("2015-06-01", "2015-07-02"));
  }
  
  private static boolean acquireAndSaleWithinWindow(final String sale, final String acquire) {
    return new WithinWashSaleWindowPredicate(Instant.parse(sale))
        .apply(Instant.parse(acquire));
  }
}
