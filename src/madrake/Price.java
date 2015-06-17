package madrake;

import com.google.common.base.Objects;

public final class Price {

  private final long price;
  
  public Price(long price) {
    this.price = price;
  }
  

  @Override
  public int hashCode() {
    return Objects.hashCode(price);
  }
  
  // TODO(madrake):use objects.equals() and check hashcode implementation on all beanlike objects

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Price other = (Price) obj;
    if (price != other.price)
      return false;
    return true;
  }


  @Override
  public String toString() {
    return Long.toString(price);
  }


  public Price minus(Price otherPrice) {
    return new Price(this.price - otherPrice.price);
  }


  public Price minus(AdjustmentToPrice adjustmentToSalePrice) {
    return new Price(this.price - adjustmentToSalePrice.getPrice());
  }

  public Price plus(AdjustmentToPrice adjustmentToSalePrice) {
    return new Price(this.price + adjustmentToSalePrice.getPrice());
  }

  public long getPrice() {
    return price;
  }
  
}
