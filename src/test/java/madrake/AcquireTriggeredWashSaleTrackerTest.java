package madrake;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;

public class AcquireTriggeredWashSaleTrackerTest {

  @Test
  public void testReturnsAllEventsIfNothingWashed() {
    Event testEvent1 = Event.create(StaticTestHelperMethods.dollarValueAtTime(1), StockId.create(1), EventType.SALE);
    Event testEvent2 = Event.create(StaticTestHelperMethods.dollarValueAtTime(2), StockId.create(1), EventType.SALE);
    Set<Event> allEvents = ImmutableSet.<Event>of(testEvent1, testEvent2);
    WashSaleTracker tracker = new WashSaleTracker();
    Predicate<Event> filter = tracker.notWashedPredicate();
    int numUnfilteredEvents = FluentIterable.from(allEvents)
        .filter(filter)
        .size();
    assertEquals(numUnfilteredEvents, 2);
  }
  
  @Test
  public void testDoesntReturnWashedEvent() {
    Event testEvent1 = Event.create(StaticTestHelperMethods.dollarValueAtTime(1), StockId.create(1), EventType.SALE);
    Event testEvent2 = Event.create(StaticTestHelperMethods.dollarValueAtTime(2), StockId.create(2), EventType.SALE);
    Set<Event> allEvents = ImmutableSet.<Event>of(testEvent1, testEvent2);
    WashSaleTracker tracker = new WashSaleTracker();
    tracker.addWashed(testEvent2);
    Predicate<Event> filter = tracker.notWashedPredicate();
    int numUnfilteredEvents = FluentIterable.from(allEvents)
        .filter(filter)
        .size();
    assertEquals(numUnfilteredEvents, 1);
  }

  @Test
  public void testCantWashEventTwice() {
    Event testEvent1 = Event.create(StaticTestHelperMethods.dollarValueAtTime(1), StockId.create(1), EventType.SALE);
    WashSaleTracker tracker = new WashSaleTracker();
    tracker.addWashed(testEvent1);
    try {
      tracker.addWashed(testEvent1);
      fail("Shouldn' be able to wash the same event twice");
    } catch (IllegalArgumentException expected) { }
  }
}
