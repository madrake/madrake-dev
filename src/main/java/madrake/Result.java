package madrake;

import javax.annotation.Nullable;

import org.joda.money.BigMoney;

import com.google.auto.value.AutoValue;

// TODO(madrake): need to define what each field means precisely and whether it can be null. Right
// now these are all marked as Nullable but I'm not sure that makes sense.
@AutoValue
public abstract class Result {

  public static Result create(
      StockId stockId,
      RealizableValue originalAcquisition,
      AcquisitionAdjustment adjustmentToAcquisition,
      StockId senderOfDisallowedLoss,
      RealizableValue originalSale,
      boolean washSaleDisallowed,
      StockId recipientOfDisallowedLoss,
      BigMoney reportableGain) {
    return AutoValue_Result.builder()
        .stockId(stockId)
        .originalAcquisition(originalAcquisition)
        .adjustmentToAcquisition(adjustmentToAcquisition)
        .senderOfDisallowedLoss(senderOfDisallowedLoss)
        .originalSale(originalSale)
        .washSaleDisallowed(washSaleDisallowed)
        .recipientOfDisallowedLoss(recipientOfDisallowedLoss)
        .reportableGain(reportableGain)
        .build();
  }

  public abstract @Nullable StockId getStockId();
  public abstract @Nullable RealizableValue getOriginalAcquisition();
  public abstract @Nullable AcquisitionAdjustment getAdjustmentToAcquisition();
  public abstract @Nullable StockId getSenderOfDisallowedLoss();
  public abstract @Nullable RealizableValue getOriginalSale();
  // TODO(madrake): should we flip the semantics around?
  public abstract @Nullable boolean getWashSaleDisallowed();
  public abstract @Nullable StockId getRecipientOfDisallowedLoss();
  public abstract @Nullable BigMoney getReportableGain();

  public static int compareByStockId(Result a0, Result a1) {
    return a0.getStockId().compareTo(a1.getStockId());
  }

  public static Builder builder() {
    return new AutoValue_Result.Builder()
        .washSaleDisallowed(false);
  }

  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder stockId(StockId stockId);
    public abstract Builder originalAcquisition(RealizableValue acquisition);
    public abstract Builder adjustmentToAcquisition(AcquisitionAdjustment adjustment);
    public abstract Builder senderOfDisallowedLoss(StockId stockId);
    public abstract Builder originalSale(RealizableValue sale);
    public abstract Builder washSaleDisallowed(boolean washSaleDisallowed);
    public abstract Builder recipientOfDisallowedLoss(StockId stockId);
    public abstract Builder reportableGain(BigMoney gain);
    public abstract Result build();

    public static Builder from(Result result) {
      return AutoValue_Result.builder()
          .stockId(result.getStockId())
          .originalAcquisition(result.getOriginalAcquisition())
          .adjustmentToAcquisition(result.getAdjustmentToAcquisition())
          .senderOfDisallowedLoss(result.getSenderOfDisallowedLoss())
          .originalSale(result.getOriginalSale())
          .washSaleDisallowed(result.getWashSaleDisallowed())
          .recipientOfDisallowedLoss(result.getRecipientOfDisallowedLoss())
          .reportableGain(result.getReportableGain());
    }
  }
}
