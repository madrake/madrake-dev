package madrake.needsautovalue;

import org.joda.money.BigMoney;
import org.joda.time.Instant;

/**
 * A realizable value represents a monetary amount with a specific timestamp associated with it.
 * We are using 'realize' in the sense that a federal tax service has 'realizable' events - 
 * i.e. we can attribute a real increase or decrease or setting of value in something.
 */
public final class RealizableValue extends BigMoneyAndInstant {

  public RealizableValue(BigMoney value, Instant instant) {
    super(value, instant);
  }

  public BigMoney getValue() {
    return value;
  }
  
  public Instant getInstant() {
    return instant;
  } 
}
