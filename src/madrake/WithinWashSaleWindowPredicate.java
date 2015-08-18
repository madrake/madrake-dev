package madrake;

import org.joda.time.Duration;
import org.joda.time.Instant;

import com.google.common.base.Predicate;

class WithinWashSaleWindowPredicate implements Predicate<Instant> {

  private final Instant dateOfWashSaleEvent;
  
  WithinWashSaleWindowPredicate(Instant dateOfWashSaleEvent) {
    this.dateOfWashSaleEvent = dateOfWashSaleEvent;
  }

  @Override
  public boolean apply(Instant someAcquire) {
    return someAcquire.compareTo(dateOfWashSaleEvent.minus(Duration.standardDays(30))) >= 0 &&
        someAcquire.compareTo(dateOfWashSaleEvent.plus(Duration.standardDays(30))) <= 0;
  }
}
