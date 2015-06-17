package madrake;

import java.util.Collection;
import java.util.Set;
import java.util.SortedSet;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;

public final class WashSaleCalculator {

  public Iterable<Result> calculate(
      Collection<Acquire> acquireEvents,
      Collection<Sale> saleEvents) {
    Preconditions.checkNotNull(acquireEvents);
    Preconditions.checkNotNull(saleEvents);
    
    final Set<StockId> acquisitionStockIdsSeen = Sets.newHashSet();
    final Set<StockId> saleStockIdsSeen = Sets.newHashSet();
    for (Acquire event : acquireEvents) {
      acquisitionStockIdsSeen.add(event.getStockId());
    }
    for (Sale event : saleEvents) {
      saleStockIdsSeen.add(event.getStockId());
    }
    Preconditions.checkArgument(
        acquisitionStockIdsSeen.equals(saleStockIdsSeen), 
        "Not a full set of stock acquisition and sale events entered!");
    Preconditions.checkArgument(
        acquisitionStockIdsSeen.size() == acquireEvents.size(),
        "Duplicate acquire events!");
    Preconditions.checkArgument(
        saleStockIdsSeen.size() == saleEvents.size(),
        "Duplicate sale events!");
    
    SortedSet<Sale> sortedSales = ImmutableSortedSet.copyOf(
        new InstantComparator(),
        saleEvents);
    
    SortedSet<Acquire> sortedAcquires = ImmutableSortedSet.copyOf(
        new InstantComparator(),
        acquireEvents);
    
    // TODO(madrake): Can we do this with an immutablesortedsetbuilder?
    SortedSet<Event> allEventsInTimeOrder = Sets.<Event>newTreeSet(new InstantComparator());
    allEventsInTimeOrder.addAll(acquireEvents);
    allEventsInTimeOrder.addAll(saleEvents);
    allEventsInTimeOrder = ImmutableSortedSet.copyOf(
        new InstantComparator(),
        allEventsInTimeOrder);
    
    final WashSaleTracker tracker = new WashSaleTracker();
    
    final StockAccounting accounting = new StockAccounting();
    
    // TODO(madrake): we don't account for 'replacement stock' right now or the
    // 'first share sold is the first share purchased'
    
    for (Sale sale : sortedSales) {
      accounting.sell(sale);
    }
    
    for (Acquire acquire : sortedAcquires) {
      accounting.acquire(acquire);
    }
    
    for (Acquire acquire : sortedAcquires) {
      // TODO(madrake): this is going to be horribly inefficient but for now who cares.
      // Find the first sale before or after the acquisition that, if it exists, triggers
      // a wash sale
      Optional<Sale> possibleWashSale = FluentIterable.from(sortedSales)
          .filter(new WithinWashSaleWindowPredicate(acquire.getDate()))
          .filter(new SoldAtLossPredicate(accounting.getStocksSoldAtLoss()))
          .filter(tracker.notWashedPredicate())
          .first();
      if (possibleWashSale.isPresent()) {
        Sale sale = possibleWashSale.get();
        tracker.addWashed(sale); // don't use it again
        AdjustmentToPrice disallowedLoss = accounting.disallowLossOnSale(sale.getStockId(), acquire.getStockId());
        accounting.adjustAcquireCostBasis(acquire.getStockId(), disallowedLoss, sale.getStockId());
      } else {
        // No wash sale!!! TODO(madrake): maybe we want to log this fact?
      }
    }
    
    return accounting.getResults();
  }
}
