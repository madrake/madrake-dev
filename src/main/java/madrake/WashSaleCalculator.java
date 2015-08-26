package madrake;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;

import org.joda.time.Instant;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;

import madrake.needsautovalue.Result;

public final class WashSaleCalculator {

  public Iterable<Result> calculate(
      Collection<Event> acquireEvents,
      Collection<Event> saleEvents) {
    Preconditions.checkNotNull(acquireEvents);
    Preconditions.checkNotNull(saleEvents);
    for (Event event : acquireEvents) {
      Preconditions.checkArgument(EventType.ACQUIRE.equals(event.getEventType()));
    }
    for (Event event : saleEvents) {
      Preconditions.checkArgument(EventType.SALE.equals(event.getEventType()));
    }

    final Set<StockId> acquisitionStockIdsSeen = Sets.newHashSet();
    final Set<StockId> saleStockIdsSeen = Sets.newHashSet();
    for (Event event : acquireEvents) {
      acquisitionStockIdsSeen.add(event.getStockId());
    }
    for (Event event : saleEvents) {
      saleStockIdsSeen.add(event.getStockId());
    }
    Preconditions.checkArgument(
        acquisitionStockIdsSeen.containsAll(saleStockIdsSeen),
        "Stock sold that wasn't acquired!");
    Preconditions.checkArgument(
        acquisitionStockIdsSeen.size() == acquireEvents.size(),
        "Duplicate acquire events!");
    Preconditions.checkArgument(
        saleStockIdsSeen.size() == saleEvents.size(),
        "Duplicate sale events!");

    final Comparator<Event> instantComparator =
        (Event arg0, Event arg1) ->
        arg0.getValue().getInstant().compareTo(arg1.getValue().getInstant());

    SortedSet<Event> sortedSales = ImmutableSortedSet.copyOf(
        instantComparator,
        saleEvents);
    SortedSet<Event> sortedAcquires = ImmutableSortedSet.copyOf(
        instantComparator,
        acquireEvents);

    // TODO(madrake): Can we do this with an immutablesortedsetbuilder?
    SortedSet<Event> allEventsInTimeOrder = Sets.<Event>newTreeSet(instantComparator);
    allEventsInTimeOrder.addAll(acquireEvents);
    allEventsInTimeOrder.addAll(saleEvents);
    allEventsInTimeOrder = ImmutableSortedSet.copyOf(
        instantComparator,
        allEventsInTimeOrder);

    final WashSaleTracker tracker = new WashSaleTracker();

    final StockAccounting accounting = new StockAccounting();

    // TODO(madrake): we don't account for 'replacement stock' right now or the
    // 'first share sold is the first share purchased'

    for (Event sale : sortedSales) {
      accounting.sell(sale);
    }

    for (Event acquire : sortedAcquires) {
      accounting.acquire(acquire);
    }

    for (Event acquire : sortedAcquires) {
      // This algorithm is horribly inefficient from a big-O standpoint but we can improve on this
      // later once someone has enough stock sale info that this is actually a performance
      // problem.

      // Find the first sale before or after the acquisition that, if it exists, triggers
      // a wash sale
      Optional<Event> possibleWashSale = FluentIterable.from(sortedSales)
          .filter(Predicates.compose(
              new WithinWashSaleWindowPredicate(acquire.getValue().getInstant()),
              new Function<Event, Instant>() {
                @Override
                public Instant apply(Event input) {
                  return input.getValue().getInstant();
                }
              }))
          .filter(new SoldAtLossPredicate(accounting.getStocksSoldAtLoss()))
          .filter(tracker.notWashedPredicate())
          .first();
      if (possibleWashSale.isPresent()) {
        Event sale = possibleWashSale.get();
        tracker.addWashed(sale); // don't use it again
        AcquisitionAdjustment acquisitionAdjustment = accounting.disallowLossOnSale(
            sale.getStockId(),
            acquire.getStockId());
        // TODO(madrake): Need to add more unit tests for start date change
        accounting.adjustAcquireCostBasis(
            acquire.getStockId(),
            acquisitionAdjustment,
            sale.getStockId());
      } else {
        // No wash sale!!! TODO(madrake): maybe we want to log this fact?
      }
    }

    return accounting.getResults();
  }
}
