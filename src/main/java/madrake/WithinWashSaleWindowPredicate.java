package madrake;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.MutableDateTime;

import com.google.common.base.Predicate;

/**
 * A sale is considered to be within the wash sale window of an acquisition if the acquisition
 * falls within the 61 calendar days immediately surrounding the sale. That is - if the acquisition
 * happens on the same day as the sale, or within the 30 days prior, or the 30 days after.
 *
 * TODO(madrake): I've seen discussion of 'calendar' days but it's not clear to me what is meant
 * by calendar days - whether a specific calendar or timezone is specified. I'm going to assume
 * the Gregorian Calendar and that the user can specify their preferred timezone but more
 * research is warranted.
 */
final class WithinWashSaleWindowPredicate implements Predicate<Instant> {

  private final DateTime firstMomentOfWashSaleWindow;
  private final DateTime lastMomentOfWashSaleWindow;

  private WithinWashSaleWindowPredicate(Instant dateOfWashSaleEvent, DateTimeZone zone) {
    firstMomentOfWashSaleWindow = firstMomentOfWashSaleWindow(dateOfWashSaleEvent, zone);
    lastMomentOfWashSaleWindow = lastMomentOfWashSaleWindow(dateOfWashSaleEvent, zone);
  }

  @Override
  public boolean apply(Instant someAcquire) {
    return someAcquire.compareTo(firstMomentOfWashSaleWindow) >= 0 &&
        someAcquire.compareTo(lastMomentOfWashSaleWindow) <= 0;
  }

  private static DateTime firstMomentOfWashSaleWindow(
      Instant dateOfWashSaleEvent,
      DateTimeZone zone) {
    MutableDateTime firstMomentOfWashSaleWindow = dateOfWashSaleEvent.toMutableDateTime(zone);
    firstMomentOfWashSaleWindow.setMillisOfDay(0);
    firstMomentOfWashSaleWindow.addDays(-30);
    return firstMomentOfWashSaleWindow.toDateTime();
  }

  private static DateTime lastMomentOfWashSaleWindow(
      Instant dateOfWashSaleEvent,
      DateTimeZone zone) {
    MutableDateTime lastMomentOfWashSaleWindow = dateOfWashSaleEvent.toMutableDateTime(zone);
    lastMomentOfWashSaleWindow.setMillisOfDay(0);
    lastMomentOfWashSaleWindow.addDays(31);
    lastMomentOfWashSaleWindow.addMillis(-1);
    return lastMomentOfWashSaleWindow.toDateTime();
  }

  static WithinWashSaleWindowPredicate build(Instant dateOfWashSaleEvent, DateTimeZone zone) {
    return new WithinWashSaleWindowPredicate(dateOfWashSaleEvent, zone);
  }
}
