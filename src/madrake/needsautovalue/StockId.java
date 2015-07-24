package madrake.needsautovalue;

import com.google.common.primitives.Longs;

// TODO(madrake): use AutoValue
public final class StockId implements Comparable<StockId> {

  private final int id;
  
  public StockId(int id) {
    this.id = id;
  }

  @Override
  public int hashCode() {
    return id;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    StockId other = (StockId) obj;
    if (id != other.id)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return Long.toString(id);
  }

  @Override
  public int compareTo(StockId arg0) {
    return Longs.compare(this.id, arg0.id);
  }
}
