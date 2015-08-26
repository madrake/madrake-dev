package madrake.needsautovalue;

import org.joda.money.BigMoney;
import org.joda.time.Instant;

/**
 * This class looks a lot like a RealizableValue and in fact, for convenience to avoid duplicate
 * code this is implemented underneath as a realizable value. However this represents a different
 * concept. The instant associated with this object is a *replacement* acquisition instant. The
 * gain - the value associated with this object - represents an adjustment to a previous value,
 * not a replacement value. Since the gain in this class doesn't represent an absolute value 
 * in any sense this class is kept separate from RealizableValue.
 */
public final class AcquisitionAdjustment extends BigMoneyAndInstant {
  
  public AcquisitionAdjustment(BigMoney gain, Instant instant) {
    super(gain, instant);
  }

  public BigMoney getGain() {
    return value;
  }

  public Instant getInstant() {
    return instant;
  }
}
