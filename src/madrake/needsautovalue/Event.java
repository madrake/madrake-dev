package madrake.needsautovalue;

import com.google.common.base.Objects;

public class Event {

  // TODO(madrake): use AutoValue
  
  private final RealizableValue value;
  private final StockId stock;
  private final EventType type;
  
  public Event(RealizableValue value, StockId stock, EventType type) {
    this.value = value;
    this.stock = stock;
    this.type = type;
  }

  public RealizableValue getValue() {
    return value;
  }

  public StockId getStockId() {
    return stock;
  }
  
  public EventType getEventType() {
    return type;
  }

  public String toString() {
    return "Event [value=" + value + ", stock=" + stock + ", type=" + type + "]";
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value, stock, type);
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Event &&
        Objects.equal(this.value, ((Event) obj).value) &&
        Objects.equal(this.stock, ((Event) obj).stock) &&
        Objects.equal(this.stock, ((Event) obj).type);
  }
}
