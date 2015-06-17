package madrake;

import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Sets;

/**
 * Keeps track of which sales have been washed already.
 */
public class WashSaleTracker {
  
  private final Set<Sale> washedEvents = Sets.newHashSet();

  public Predicate<Sale> notWashedPredicate() {
    // Did this return a snapshot at the time the accounting was taken or is it live? Need to define
    return Predicates.not(Predicates.in(washedEvents));
  }

  public void addWashed(Sale sale) {
    Preconditions.checkArgument(!washedEvents.contains(sale), "Can't wash the same sale twice!");
    washedEvents.add(sale);
  }
}
