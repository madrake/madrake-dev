package madrake;

import static org.junit.Assert.*;

import java.util.Set;

import madrake.needsautovalue.Event;
import madrake.needsautovalue.EventType;

import org.junit.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;

public class AcquireTriggeredWashSaleTrackerTest {

  @Test
  public void testReturnsAllEventsIfNothingWashed() {
    Event testEvent1 = new Event(StaticTestHelperMethods.dollarValueAtTime(1), null, EventType.SALE);
    Event testEvent2 = new Event(StaticTestHelperMethods.dollarValueAtTime(2), null, EventType.SALE);
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
    Event testEvent1 = new Event(StaticTestHelperMethods.dollarValueAtTime(1), null, EventType.SALE);
    Event testEvent2 = new Event(StaticTestHelperMethods.dollarValueAtTime(2), null, EventType.SALE);
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
    Event testEvent1 = new Event(null, null, EventType.SALE);
    WashSaleTracker tracker = new WashSaleTracker();
    tracker.addWashed(testEvent1);
    try {
      tracker.addWashed(testEvent1);
      fail("Shouldn' be able to wash the same event twice");
    } catch (IllegalArgumentException expected) { }
  }
}
