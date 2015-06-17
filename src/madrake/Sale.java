package madrake;

import com.google.common.base.Objects;

public final class Sale implements Event {

  private final MattsInstant date;
  private final Price price;
  private final StockId stock;

  public Sale(MattsInstant mattsInstant, Price price2, StockId stockId1) {
    this.date = mattsInstant;
    this.price = price2;
    this.stock = stockId1;
  }

  @Override
  public MattsInstant getDate() {
    return date;
  }
  
  public Price getPrice() {
    return price;
  }

  @Override
  public StockId getStockId() {
    return stock;
  }

  @Override
  public String toString() {
    return "Sale [date=" + date + ", price=" + price + ", stock=" + stock + "]";
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(date, price, stock);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Sale other = (Sale) obj;
    if (date == null) {
      if (other.date != null)
        return false;
    } else if (!date.equals(other.date))
      return false;
    if (price == null) {
      if (other.price != null)
        return false;
    } else if (!price.equals(other.price))
      return false;
    if (stock == null) {
      if (other.stock != null)
        return false;
    } else if (!stock.equals(other.stock))
      return false;
    return true;
  }
}
