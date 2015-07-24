package madrake.needsautovalue;

import madrake.BigMoneys;

import org.joda.money.BigMoney;
import org.joda.time.Instant;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * For use by subclasses that represent similar data object concepts that represent different 
 * things but are otherwise the same.
 */
abstract class BigMoneyAndInstant {

  protected final BigMoney value;
  protected final Instant instant;

  protected BigMoneyAndInstant(BigMoney value, Instant instant) {
    this.value = Preconditions.checkNotNull(value, "value can't be null");
    this.instant = Preconditions.checkNotNull(instant, "timestamp can't be null");
  }

  @Override
  public final int hashCode() {
    return Objects.hashCode(value, instant, getClass());
  }

  @Override
  public final boolean equals(Object obj) {
    if (obj instanceof BigMoneyAndInstant) {
      final BigMoneyAndInstant other = (BigMoneyAndInstant) obj;
      return Objects.equal(this.instant, other.instant) &&
          BigMoneys.equals(this.value, other.value) &&
          obj.getClass().equals(this.getClass());
    }
    return false;
  }

  @Override
  public final String toString() {
    return getClass().getSimpleName() + " [instant=" 
        + instant 
        + ", value=" 
        + Long.toString(value.getAmountMinorLong()) 
        + "]";
  }
}