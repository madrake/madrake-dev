package madrake;

import java.util.Arrays;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class AdjustmentToPrice {

  private final long adjustmentToPrice;
  
  public AdjustmentToPrice(long adjustmentToPrice) {
    this.adjustmentToPrice = Preconditions.checkNotNull(adjustmentToPrice);
  }

  // TODO(madrake): use AutoValue
  
  @Override
  public int hashCode() {
    return Objects.hashCode(adjustmentToPrice);
  }
 
  @Override
  public boolean equals(Object obj) {
    return obj instanceof AdjustmentToPrice &&
        Objects.equal(this.adjustmentToPrice, ((AdjustmentToPrice) obj).adjustmentToPrice);
  }
  

  @Override
  public String toString() {
    return Long.toString(adjustmentToPrice);
  }

  public long getPrice() {
    return adjustmentToPrice;
  }
}
