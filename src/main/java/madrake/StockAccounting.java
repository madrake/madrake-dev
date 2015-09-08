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
    Preconditions.checkArgument(!resultSoFar.getDisallowedLossOnWashSale());
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
      // TODO(madrake): IMPORTANT this is a bit odd how we construct an 'empty' entry here
      return Result.builder().stockId(stockId)
          .disallowedLossOnWashSale(false)
          .build();
    }
  }

  public AcquisitionAdjustment disallowLossOnSale(StockId saleToDisallowLoss, StockId acquireThatReceivesDisallowedLoss) {
    final Result resultSoFar = Preconditions.checkNotNull(
        stockDetails.get(saleToDisallowLoss),
        "Expected to fetch a partial result based on stockId " + saleToDisallowLoss + " but no match was found");
    Preconditions.checkNotNull(resultSoFar.getOriginalSale());
    Preconditions.checkArgument(!resultSoFar.getDisallowedLossOnWashSale());
    Preconditions.checkArgument(resultSoFar.getRecipientOfDisallowedLoss() == null);
    final AcquisitionAdjustment adjustmentToAcquisition = resultSoFar.getAdjustmentToAcquisition();
    final Instant acquisitionDate = adjustmentToAcquisition == null ?
        resultSoFar.getOriginalAcquisition().getInstant() :
        adjustmentToAcquisition.getInstant();
    BigMoney gain = resultSoFar.getOriginalSale().getValue().minus(resultSoFar.getOriginalAcquisition().getValue());
    if (adjustmentToAcquisition != null) {
      gain = gain.minus(adjustmentToAcquisition.getGain());
    }
    Preconditions.checkArgument(gain.isNegative(), "Can't disallow loss on sale that didn't have a loss!");
    Result newResult = Builder.from(resultSoFar)
        .disallowedLossOnWashSale(true)
        .recipientOfDisallowedLoss(acquireThatReceivesDisallowedLoss)
        .build();
    stockDetails.put(saleToDisallowLoss, newResult);
    return AcquisitionAdjustment.create(gain.negated(), acquisitionDate);

  }

  public void adjustAcquireCostBasis(
      StockId acquireToAdjustCostBasis,
      AcquisitionAdjustment acquisitionAdjustment,
      StockId disallowedWashSell) {
    final Result resultSoFar = Preconditions.checkNotNull(
        stockDetails.get(acquireToAdjustCostBasis),
        "Expected to fetch a partial result based on stockId " + acquireToAdjustCostBasis + " but no match was found");
    Preconditions.checkNotNull(resultSoFar.getOriginalAcquisition());
    Preconditions.checkArgument(resultSoFar.getAdjustmentToAcquisition() == null);
    Result newResult = Builder.from(resultSoFar)
        .adjustmentToAcquisition(acquisitionAdjustment)
        .senderOfDisallowedLoss(disallowedWashSell)
        .build();
    stockDetails.put(acquireToAdjustCostBasis, newResult);
  }

  public Iterable<Result> getResultsSortedByStockId() {
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
