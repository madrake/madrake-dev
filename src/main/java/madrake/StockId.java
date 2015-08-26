package madrake;

import com.google.auto.value.AutoValue;
import com.google.common.primitives.Longs;

@AutoValue
public abstract class StockId implements Comparable<StockId> {
  public static StockId create(int id) {
    return new AutoValue_StockId(id);
  }
  
  public abstract int getId();

  @Override
  public int compareTo(StockId arg0) {
    return Longs.compare(getId(), arg0.getId());
  }
}
