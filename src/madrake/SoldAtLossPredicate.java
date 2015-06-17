package madrake;

import java.util.Set;

import com.google.common.base.Predicate;

public class SoldAtLossPredicate implements Predicate<Sale> {
  
  private final Set<StockId> lossStocks;
  
  public SoldAtLossPredicate(Set<StockId> lossStocks) {
    this.lossStocks = lossStocks;    
  }

  @Override
  public boolean apply(Sale sale) {
    return lossStocks.contains(sale.getStockId());
  }
}
