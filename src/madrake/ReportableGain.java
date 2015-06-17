package madrake;

import com.google.common.base.Objects;

public final class ReportableGain {

  private final long gain;
  
  public ReportableGain(long gain) {
    this.gain = gain;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(gain);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ReportableGain other = (ReportableGain) obj;
    if (gain != other.gain)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "ReportableGain [gain=" + gain + "]";
  }

  public long getGain() {
    return gain;
  }
  
}
