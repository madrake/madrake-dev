package madrake;

import java.util.Set;

import madrake.needsautovalue.Event;
import madrake.needsautovalue.EventType;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Sets;

/**
 * Keeps track of which sales have been washed already.
 */
public class WashSaleTracker {
  
  private final Set<Event> washedEvents = Sets.newHashSet();

  public Predicate<Event> notWashedPredicate() {
    // Did this return a snapshot at the time the accounting was taken or is it live? Need to define
    return Predicates.not(Predicates.in(washedEvents));
  }

  public void addWashed(Event sale) {
    Preconditions.checkArgument(!washedEvents.contains(sale), "Can't wash the same sale twice!");
    Preconditions.checkArgument(EventType.SALE.equals(sale.getEventType()), 
        "Can only wash a sale");
    washedEvents.add(sale);
  }
}
