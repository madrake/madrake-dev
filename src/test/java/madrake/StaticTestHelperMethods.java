package madrake;

import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.joda.time.Instant;
import org.junit.ComparisonFailure;

final class StaticTestHelperMethods {

  private StaticTestHelperMethods() { /* do not instantiate */ }
  
  // TODO(madrake): move this somewhere more appropriate Truth or some interface we can implement?
  static final void assertEquals(Result expected, Result actual) {
    if (!expected.equals(actual)) {
      throw new ComparisonFailure(
          "expected:" + expected.toString() + " but was:" + actual.toString(),
          expected.toString(),
          actual.toString());
    }
  }

  static final BigMoney dollars(final int amount) {
    return BigMoney.of(CurrencyUnit.USD, amount);
  }

  static RealizableValue dollarValueAtTime(final int timestamp) {
    return value(timestamp, 1);
  }

  public static RealizableValue value(final int timestamp, final int dollars) {
    return RealizableValue.create(dollars(dollars), new Instant(timestamp));
  }
}
