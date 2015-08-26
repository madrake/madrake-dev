package madrake;

import org.joda.money.BigMoney;

public final class BigMoneys {

  private BigMoneys() { /* do not instantiate */ }
  
  // TODO(madrake): Find a better way to do this
  public static final boolean equals(BigMoney first, BigMoney second) {
    if (first == null && second == null) {
      return true;
    } else if (first == null || second == null) {
      return false;
    } else {
      return first.compareTo(second) == 0;
    }
  }

  public static String priceToString(BigMoney price) {
    return price != null ? Long.toString(price.getAmountMinorLong()) : "null";
  }
}
