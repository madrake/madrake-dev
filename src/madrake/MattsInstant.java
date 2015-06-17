package madrake;

import org.joda.time.Instant;

import com.google.common.base.Objects;

// TODO(madrake): we might want to keep this around and use joda time + an enforced arbitrary
// ordering for consistencies sake
public final class MattsInstant implements Comparable<MattsInstant> {

  private final Instant date;
  
  public MattsInstant(Instant date) {
    this.date = date;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(date);
  }
  
  // TODO(madrake): this should be temporary and it's totally wrong
  public boolean isWithinThirtyDaysAtOrAfter(MattsInstant someOtherInstant) {
    return date == someOtherInstant.date || date.getMillis() == someOtherInstant.date.getMillis() + 1;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    MattsInstant other = (MattsInstant) obj;
    if (!date.equals(other.date))
      return false;
    return true;
  }
  
  @Override
  public String toString() {
    return date.toString();
  }

  @Override
  public int compareTo(MattsInstant arg0) {
    return this.date.compareTo(arg0.date);
  }
}
