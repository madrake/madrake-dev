package madrake;

import org.joda.money.BigMoney;
import org.joda.time.Instant;

import com.google.auto.value.AutoValue;

/**
 * A realizable value represents a monetary amount with a specific timestamp associated with it.
 * We are using 'realize' in the sense that a federal tax service has 'realizable' events - 
 * i.e. we can attribute a real increase or decrease or setting of value in something.
 */
@AutoValue
public abstract class RealizableValue {
  public static RealizableValue create(BigMoney value, Instant instant) {
    return new AutoValue_RealizableValue(value, instant);
  }
  
  public abstract BigMoney getValue();
  public abstract Instant getInstant();
}
