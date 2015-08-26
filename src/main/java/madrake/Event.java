package madrake;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Event {
  public static Event create(RealizableValue value, StockId stock, EventType type) {
    return new AutoValue_Event(value, stock, type);
  }

  public abstract RealizableValue getValue();
  public abstract StockId getStockId();
  public abstract EventType getEventType();
}
