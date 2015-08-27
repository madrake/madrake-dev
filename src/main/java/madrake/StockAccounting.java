package madrake;

import java.util.Map;
import java.util.Set;

import org.joda.money.BigMoney;
import org.joda.time.Instant;

import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;

import madrake.Result.Builder;

public class StockAccounting {

  private final Map<StockId, Result> stockDetails = Maps.newHashMap();

  public void sell(Event event) {
    final StockId stockId = event.getStockId();
    Result resultSoFar = fetchFromMapIfPresent(stockId);
    Preconditions.checkArgument(resultSoFar.getOriginalSale() == null);
    Preconditions.checkArgument(!resultSoFar.getWashSaleDisallowed());
    Result newResult = Builder.from(resultSoFar)
        .originalSale(event.getValue())
        .build();
    stockDetails.put(stockId, newResult);
  }

  public void acquire(Event event) {
    final StockId stockId = event.getStockId();
    Result resultSoFar = fetchFromMapIfPresent(stockId);
    // TODO(madrake): this shouldn't be preconditions, what should it be? here and elsewhere
    Preconditions.checkArgument(resultSoFar.getOriginalAcquisition() == null);
    Preconditions.checkArgument(resultSoFar.getAdjustmentToAcquisition() == null);
    Result newResult = Builder.from(resultSoFar)
        .originalAcquisition(event.getValue())
        .build();
    stockDetails.put(stockId, newResult);
  }

  private Result fetchFromMapIfPresent(StockId stockId) {
    if (stockDetails.containsKey(stockId)) {
      Result result = stockDetails.get(stockId);
      Preconditions.checkArgument(result.getStockId().equals(stockId));
      return result;
    } else {
      // TODO(madrake): this is a bit odd how we construct an 'empty' entry here
      return Result.builder().stockId(stockId)
          .washSaleDisallowed(false)
          .build();
    }
  }

  public AcquisitionAdjustment disallowLossOnSale(StockId saleToDisallowLoss, StockId acquireThatReceivesDisallowedLoss) {
    // TODO(madrake): can we make sure this isn't null?
    final Result resultSoFar = stockDetails.get(saleToDisallowLoss);
    Preconditions.checkNotNull(resultSoFar.getOriginalSale());
    Preconditions.checkArgument(!resultSoFar.getWashSaleDisallowed());
    Preconditions.checkArgument(resultSoFar.getRecipientOfDisallowedLoss() == null);
    // TODO(madrake): this calculation needs to take into account the acquisition price!
    // It also duplicates logic elsewhere
    BigMoney gain = resultSoFar.getOriginalSale().getValue().minus(resultSoFar.getOriginalAcquisition().getValue());
    // TODO(madrake): I think we can ignore the null because BigMoney handles this
    Instant acquisitionDate;
    if (resultSoFar.getAdjustmentToAcquisition() != null) {
      gain = gain.minus(resultSoFar.getAdjustmentToAcquisition().getGain());
      acquisitionDate = resultSoFar.getAdjustmentToAcquisition().getInstant();
    } else {
      acquisitionDate = resultSoFar.getOriginalAcquisition().getInstant();
    }
    Preconditions.checkArgument(gain.isNegative(), "Can't disallow loss on sale that didn't have a loss!");
    Result newResult = Builder.from(resultSoFar)
        .washSaleDisallowed(true)
        .recipientOfDisallowedLoss(acquireThatReceivesDisallowedLoss)
        .build();
    stockDetails.put(saleToDisallowLoss, newResult);
    return AcquisitionAdjustment.create(gain.negated(), acquisitionDate);

  }

  public void adjustAcquireCostBasis(
      StockId acquireToAdjustCostBasis,
      AcquisitionAdjustment acquisitionAdjustment,
      StockId disallowedWashSell) {
    // TODO(madrake): can we make sure this isn't null?
    final Result resultSoFar = stockDetails.get(acquireToAdjustCostBasis);
    Preconditions.checkNotNull(resultSoFar.getOriginalAcquisition());
    Preconditions.checkArgument(resultSoFar.getAdjustmentToAcquisition() == null);
    Result newResult = Builder.from(resultSoFar)
        .adjustmentToAcquisition(acquisitionAdjustment)
        .senderOfDisallowedLoss(disallowedWashSell)
        .build();
    stockDetails.put(acquireToAdjustCostBasis, newResult);
  }

  public Iterable<Result> getResults() { // TODO(madrake): in sorted order in name
    return FluentIterable.from(stockDetails.values())
        .transform(new AddReportableGain())
        .toSortedList(Result::compareByStockId);
  }

  public Set<StockId> getStocksSoldAtLoss() {
    return FluentIterable.from(stockDetails.values())
        .filter((Result input) -> input.getOriginalSale() != null /* stock was sold */)
        .transform(new AddReportableGain())
        .filter((Result arg0) -> arg0.getReportableGain().isNegative())
        .transform(Result::getStockId)
        .toSet();
  }
}
