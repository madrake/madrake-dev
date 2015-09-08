package madrake;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.junit.Test;

public class WithinWashSaleWindowPredicateTest {

  // TODO(madrake): add tests that span year boundaries and leap years and check that we do
  // the correct 30 day boundary condition with these events in the middle.

  // TODO(madrake): add tests that if we change the time zone we get different results...but
  // first see if the actual 'calendar day' rule is day specific

  @Test
  public void testSaleIsExactly30CalendarDaysBeforePurchase_IsWashSale() {
    assertTrue(acquireAndSaleWithinWindow("2015-06-01T12:00", "2015-07-01T12:00"));
    // These tests have 30 calendar days but > than 30 * 24 hours
    assertTrue(acquireAndSaleWithinWindow("2015-06-01T12:00", "2015-07-01T13:00"));
    assertTrue(acquireAndSaleWithinWindow("2015-06-01T00:00", "2015-07-01T23:59"));
  }

  @Test
  public void testSaleIsExactly30CalendarDaysAfterPurchase_IsWashSale() {
    assertTrue(acquireAndSaleWithinWindow("2015-07-01T12:00", "2015-06-01T12:00"));
    // These tests have 30 calendar days but > than 30 * 24 hours
    assertTrue(acquireAndSaleWithinWindow("2015-07-01T13:00", "2015-06-01T12:00"));
    assertTrue(acquireAndSaleWithinWindow("2015-07-01T00:00", "2015-06-01T23:59"));
  }


  @Test
  public void testSaleIsExactly29CalendarDaysBeforePurchase_IsWashSale() {
    assertTrue(acquireAndSaleWithinWindow("2015-06-02T12:00", "2015-07-01T12:00"));
  }

  @Test
  public void testSaleIsExactly29CalendarDaysAfterPurchase_IsWashSale() {
    assertTrue(acquireAndSaleWithinWindow("2015-07-01T12:00", "2015-06-02T12:00"));
  }

  @Test
  public void testSaleIsExactly1DaysAfterPurchase_IsWashSale() {
    assertTrue(acquireAndSaleWithinWindow("2015-07-01T12:00", "2015-07-02T12:00"));
  }

  @Test
  public void testSaleIsExactly1DaysBeforePurchase_IsWashSale() {
    assertTrue(acquireAndSaleWithinWindow("2015-07-01T12:00", "2015-06-30T12:00"));
  }

  @Test
  public void testSaleIsSameDay_IsWashSale() {
    assertTrue(acquireAndSaleWithinWindow("2015-07-01T12:00", "2015-07-01T12:00"));
    assertTrue(acquireAndSaleWithinWindow("2015-07-01T13:00", "2015-07-01T12:00"));
    assertTrue(acquireAndSaleWithinWindow("2015-07-01T13:00", "2015-07-01T11:00"));
  }

  @Test
  public void testSaleIsExactly31CalendarDaysBeforePurchase_IsntWashSale() {
    assertFalse(acquireAndSaleWithinWindow("2015-05-31T12:00", "2015-07-01T12:00"));
    // These tests have 31 calendar days but less than 31 * 24 hours
    assertFalse(acquireAndSaleWithinWindow("2015-05-31T23:59", "2015-07-01T12:00"));
    assertFalse(acquireAndSaleWithinWindow("2015-05-31T23:59", "2015-07-01T00:00"));
  }

  @Test
  public void testSaleIsExactly31CalendarDaysAfterPurchase_IsntWashSale() {
    assertFalse(acquireAndSaleWithinWindow("2015-06-01T12:00", "2015-07-02T12:00"));
    // These tests have 31 calendar days but less than 31 * 24 hours
    assertFalse(acquireAndSaleWithinWindow("2015-06-01T23:59", "2015-07-02T12:00"));
    assertFalse(acquireAndSaleWithinWindow("2015-06-01T23:59", "2015-07-02T00:00"));
  }

  private static boolean acquireAndSaleWithinWindow(final String sale, final String acquire) {
    return WithinWashSaleWindowPredicate.build(
        Instant.parse(sale),
        DateTimeZone.forID("America/Los_Angeles"))
        .apply(Instant.parse(acquire));
  }
}
