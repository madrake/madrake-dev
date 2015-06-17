package madrake;

public final class Acquire implements Event {

  private final MattsInstant date;
  private final StockId stockId;
  private final Price price;

  public Acquire(MattsInstant mattsInstant, Price price2, StockId stockId1) {
    this.date = mattsInstant;
    this.price = price2;
    this.stockId = stockId1;
  }

  @Override
  public MattsInstant getDate() {
    return date;
  }

  @Override
  public StockId getStockId() {
    return stockId;
  }

  public Price getPrice() {
    return price;
  }
  
  // TODO(madrake): implement equalshashcodestring
}
