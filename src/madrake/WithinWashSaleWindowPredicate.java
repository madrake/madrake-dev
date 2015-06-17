package madrake;

import com.google.common.base.Predicate;

class WithinWashSaleWindowPredicate implements Predicate<Sale> {

  private final MattsInstant dateOfWashSaleEvent;
  
  WithinWashSaleWindowPredicate(MattsInstant dateOfWashSaleEvent) {
    this.dateOfWashSaleEvent = dateOfWashSaleEvent;
  }

  @Override
  public boolean apply(Sale someAcquire) {
    // TODO(madrake): this is not how we should calculate this!
    // TODO(madrake): also write unit tests
    return someAcquire.getDate().isWithinThirtyDaysAtOrAfter(dateOfWashSaleEvent) ||
        dateOfWashSaleEvent.isWithinThirtyDaysAtOrAfter(someAcquire.getDate());
  }

}
