package madrake;

import org.joda.money.BigMoney;
import org.joda.time.Instant;

import com.google.auto.value.AutoValue;

/**
 * This class looks a lot like a RealizableValue. However this represents a different
 * concept. The instant associated with this object is a *replacement* acquisition instant. The
 * gain - the value associated with this object - represents an adjustment to a previous value,
 * not a replacement value. Since the gain in this class doesn't represent an absolute value 
 * in any sense this class is kept separate from RealizableValue.
 */
@AutoValue
public abstract class AcquisitionAdjustment {
  public static AcquisitionAdjustment create(BigMoney gain, Instant instant) {
    return new AutoValue_AcquisitionAdjustment(gain, instant);
  }
  
  public abstract BigMoney getGain();
  public abstract Instant getInstant();
}
