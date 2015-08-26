package madrake;

import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;

public class SoldAtLossPredicate implements Predicate<Event> {
  
  private final Set<StockId> lossStocks;
  
  public SoldAtLossPredicate(Set<StockId> lossStocks) {
    this.lossStocks = lossStocks;    
  }

  @Override
  public boolean apply(Event sale) {
    Preconditions.checkArgument(EventType.SALE.equals(sale.getEventType()), 
        "Should only work on Sale events");
    return lossStocks.contains(sale.getStockId());
  }
}
