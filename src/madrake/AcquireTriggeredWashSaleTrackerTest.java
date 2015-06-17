package madrake;

import static org.junit.Assert.*;

import java.util.Set;

import org.joda.time.Instant;
import org.junit.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;

public class AcquireTriggeredWashSaleTrackerTest {

  @Test
  public void testReturnsAllEventsIfNothingWashed() {
    Sale testEvent1 = new Sale(new MattsInstant(new Instant(1)), null, null);
    Sale testEvent2 = new Sale(new MattsInstant(new Instant(2)), null, null);
    Set<Sale> allEvents = ImmutableSet.<Sale>of(testEvent1, testEvent2);
    WashSaleTracker tracker = new WashSaleTracker();
    Predicate<Sale> filter = tracker.notWashedPredicate();
    int numUnfilteredEvents = FluentIterable.from(allEvents)
        .filter(filter)
        .size();
    assertEquals(numUnfilteredEvents, 2);
  }
  
  @Test
  public void testDoesntReturnWashedEvent() {
    Sale testEvent1 = new Sale(new MattsInstant(new Instant(1)), null, null);
    Sale testEvent2 = new Sale(new MattsInstant(new Instant(2)), null, null);
    Set<Sale> allEvents = ImmutableSet.<Sale>of(testEvent1, testEvent2);
    WashSaleTracker tracker = new WashSaleTracker();
    tracker.addWashed(testEvent2);
    Predicate<Sale> filter = tracker.notWashedPredicate();
    int numUnfilteredEvents = FluentIterable.from(allEvents)
        .filter(filter)
        .size();
    assertEquals(numUnfilteredEvents, 1);
  }
  
  @Test
  public void testCantWashEventTwice() {
    Sale testEvent1 = new Sale(null, null, null);
    WashSaleTracker tracker = new WashSaleTracker();
    tracker.addWashed(testEvent1);
    try {
      tracker.addWashed(testEvent1);
      fail("Shouldn' be able to wash the same event twice");
    } catch (IllegalArgumentException expected) { }
  }
}
