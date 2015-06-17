package madrake;

import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;

public class StockAccounting {
  
  private final Map<StockId, Result> stockDetails = Maps.newHashMap();
  
  public void sell(Sale event) {
    final StockId stockId = event.getStockId();
    Result resultSoFar = fetchFromMapIfPresent(stockId);
    Preconditions.checkArgument(resultSoFar.getSaleDate() == null);
    Preconditions.checkArgument(resultSoFar.getTrueSalePrice() == null);
    Preconditions.checkArgument(resultSoFar.getAdjustmentToSalePrice() == null);
    Result newResult = resultSoFar.builder()
        .withSaleDate(event.getDate())
        .withSalePrice(event.getPrice())
        .build();
    stockDetails.put(stockId, newResult);
  }

  public void acquire(Acquire event) {
    final StockId stockId = event.getStockId();
    Result resultSoFar = fetchFromMapIfPresent(stockId);
    // TODO(madrake): this shouldn't be preconditions, what should it be? here and elsewhere
    Preconditions.checkArgument(resultSoFar.getAcquisitionDate() == null);
    Preconditions.checkArgument(resultSoFar.getTrueAcquisitionPrice() == null);
    Preconditions.checkArgument(resultSoFar.getAdjustmentToAcquisitionPrice() == null);
    Result newResult = resultSoFar.builder()
        .withAcquisitionDate(event.getDate())
        .withAcquisitionPrice(event.getPrice())
        .build();
    stockDetails.put(stockId, newResult);
  }

  private Result fetchFromMapIfPresent(StockId stockId) {
    if (stockDetails.containsKey(stockId)) {
      Result result = stockDetails.get(stockId);
      Preconditions.checkArgument(result.getStockId().equals(stockId));
      return result;
    } else {
      return new Result(stockId, null, null, null, null, null, null, null, null, null);
    }
  }

  public AdjustmentToPrice disallowLossOnSale(StockId saleToDisallowLoss, StockId acquireThatReceivesDisallowedLoss) {
    // TODO(madrake): can we make sure this isn't null?
    final Result resultSoFar = stockDetails.get(saleToDisallowLoss);
    Preconditions.checkNotNull(resultSoFar.getSaleDate());
    Preconditions.checkNotNull(resultSoFar.getTrueSalePrice());
    Preconditions.checkArgument(resultSoFar.getAdjustmentToSalePrice() == null);
    Preconditions.checkArgument(resultSoFar.getRecipientOfDisallowedLoss() == null);
    // TODO(madrake): this calculation needs to take into account the acquisition price!
    // It also duplicates logic elsewhere
    Long gain = resultSoFar.getTrueSalePrice().minus(resultSoFar.getTrueAcquisitionPrice()).getPrice();
    if (resultSoFar.getAdjustmentToAcquisitionPrice() != null) {
      gain -= resultSoFar.getAdjustmentToAcquisitionPrice().getPrice();
    }
    Preconditions.checkArgument(gain < 0, "Can't disallow loss on sale that didn't have a loss!");
    Result newResult = resultSoFar.builder()
        .withAdjustmentToSalePrice(-gain) // we should really just mark losses as disallowed rather than adjust the sales price
        .withRecipientOfDisallowedLoss(acquireThatReceivesDisallowedLoss)
        .build();
    stockDetails.put(saleToDisallowLoss, newResult);
    return new AdjustmentToPrice(-gain);
    
  }

  public void adjustAcquireCostBasis(StockId acquireToAdjustCostBasis, AdjustmentToPrice disallowedLoss, StockId disallowedWashSell) {
    // TODO(madrake): can we make sure this isn't null?
    final Result resultSoFar = stockDetails.get(acquireToAdjustCostBasis);
    Preconditions.checkNotNull(resultSoFar.getAcquisitionDate());
    Preconditions.checkNotNull(resultSoFar.getTrueAcquisitionPrice());
    Preconditions.checkArgument(resultSoFar.getAdjustmentToAcquisitionPrice() == null);
    Result newResult = resultSoFar.builder()
        .withAdjustmentToAcquisitionPrice(disallowedLoss)
        .withSenderOfDisallowedLoss(disallowedWashSell)
        .build();
    stockDetails.put(acquireToAdjustCostBasis, newResult);
  }

  public Iterable<Result> getResults() { // TODO(madrake): in sorted order in name
    return FluentIterable.from(stockDetails.values())
        .transform(new NonNullAdjustments())
        .transform(new AddReportableGain())
        .toSortedList(new ResultEventIdComparator());
  }
  
  public Set<StockId> getStocksSoldAtLoss() {
    return FluentIterable.from(stockDetails.values())
        .transform(new NonNullAdjustments())
        .transform(new AddReportableGain())
        .filter(new Predicate<Result>() {
          @Override
          public boolean apply(Result arg0) {
            return arg0.getReportableGain().getGain() < 0;
          }
        })
        .transform(new Function<Result, StockId>() {
          @Override
          public StockId apply(Result arg0) {
            return arg0.getStockId();
          }
        })
        .toSet();
  }

}
