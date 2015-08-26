package madrake.needsautovalue;

import org.joda.money.BigMoney;

import com.google.common.base.Objects;

import madrake.BigMoneys;

// TODO(madrake): Use AutoValue with Builder pattern for this class
// TODO(madrake): need to define what each field means precisely and whether it can be null
public final class Result {

  private final StockId stockId;
  private final RealizableValue originalAcquisition;
  private final AcquisitionAdjustment adjustmentToAcquisition;
  private final StockId senderOfDisallowedLoss;
  private final RealizableValue originalSale;
  private final BigMoney adjustmentToSalePrice;
  private final boolean washSaleDisallowed;
  private final StockId recipientOfDisallowedLoss;
  private final BigMoney reportableGain;

  public Result(
      StockId stockId,
      RealizableValue originalAcquisition,
      AcquisitionAdjustment adjustmentToAcquisition,
      StockId senderOfDisallowedLoss,
      RealizableValue originalSale,
      BigMoney adjustmentToSalePrice,
      boolean washSaleDisallowed,
      StockId recipientOfDisallowedLoss,
      BigMoney reportableGain) {
    this.stockId = stockId;
    this.originalAcquisition = originalAcquisition;
    this.adjustmentToAcquisition = adjustmentToAcquisition;
    this.senderOfDisallowedLoss = senderOfDisallowedLoss;
    this.originalSale = originalSale;
    this.adjustmentToSalePrice = adjustmentToSalePrice;
    this.recipientOfDisallowedLoss = recipientOfDisallowedLoss;
    this.reportableGain = reportableGain;
    this.washSaleDisallowed = washSaleDisallowed;
  }

  public StockId getStockId() {
    return stockId;
  }

  public RealizableValue getOriginalAcquisition() {
    return originalAcquisition;
  }

  public AcquisitionAdjustment getAdjustmentToAcquisition() {
    return adjustmentToAcquisition;
  }

  public StockId getSenderOfDisallowedLoss() {
    return senderOfDisallowedLoss;
  }

  public RealizableValue getOriginalSale() {
    return originalSale;
  }

  public BigMoney getAdjustmentToSalePrice() {
    return adjustmentToSalePrice;
  }

  public StockId getRecipientOfDisallowedLoss() {
    return recipientOfDisallowedLoss;
  }

  public BigMoney getReportableGain() {
    return reportableGain;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(
        stockId,
        originalAcquisition,
        adjustmentToAcquisition,
        senderOfDisallowedLoss,
        originalSale,
        adjustmentToSalePrice,
        recipientOfDisallowedLoss,
        reportableGain);
  }

  //TODO(madrake): for Result we need a more fluently readable equality check

  @Override
  public boolean equals(Object obj) {
   // TODO(madrake); I don't think autovalue will work here because of bigdecimal comparison
    if (obj instanceof Result) {
      final Result other = (Result) obj;
      return Objects.equal(this.stockId, other.stockId) &&
          Objects.equal(this.originalAcquisition, other.originalAcquisition) &&
          Objects.equal(this.adjustmentToAcquisition, other.adjustmentToAcquisition) &&
          Objects.equal(this.senderOfDisallowedLoss, other.senderOfDisallowedLoss) &&
          Objects.equal(this.originalSale, other.originalSale) &&
          BigMoneys.equals(this.adjustmentToSalePrice, other.adjustmentToSalePrice) &&
          Objects.equal(this.recipientOfDisallowedLoss, other.recipientOfDisallowedLoss) &&
          BigMoneys.equals(this.reportableGain, other.reportableGain);
    }
    return false;
  }

  @Override
  public String toString() {
    // TODO(madrake): make this cleaner
    return "Result[id="
        + stockId
        + ",acquisition="
        + originalAcquisition
        + ",adjustmentToAcquisition="
        + adjustmentToAcquisition
        + ",senderOfDisallowedLoss="
        + senderOfDisallowedLoss
        + ",sale="
        + originalSale
        + ",adjustmentToSalePrice="
        + BigMoneys.priceToString(adjustmentToSalePrice)
        + ",recipientOfDisallowedLoss="
        + recipientOfDisallowedLoss
        + ",reportableGain="
        + BigMoneys.priceToString(reportableGain)
        + "]";
  }

  public static int compareByStockId(Result a0, Result a1) {
    return a0.getStockId().compareTo(a1.getStockId());
  }

  public Builder builder() {
    return new Builder(
        stockId,
        originalAcquisition,
        adjustmentToAcquisition,
        senderOfDisallowedLoss,
        originalSale,
        adjustmentToSalePrice,
        washSaleDisallowed,
        recipientOfDisallowedLoss,
        reportableGain);
  }

  public static final class Builder {
    private StockId stockId = null;
    private RealizableValue originalAcquisition = null;
    private AcquisitionAdjustment adjustmentToAcquisition = null;
    private StockId senderOfDisallowedLoss = null;
    private RealizableValue originalSale = null;
    private BigMoney adjustmentToSalePrice = null;
    private boolean washSaleDisallowed = false;
    private StockId recipientOfDisallowedLoss = null;
    private BigMoney reportableGain = null;

    public Builder(
        StockId stockId,
        RealizableValue originalAcquisition,
        AcquisitionAdjustment adjustmentToAcquisition,
        StockId senderOfDisallowedLoss,
        RealizableValue originalSale,
        BigMoney adjustmentToPrice2,
        boolean washSaleDisallowed,
        StockId recipientOfDisallowedLoss,
        BigMoney reportableGain) {
      this.stockId = stockId;
      this.originalAcquisition = originalAcquisition;
      this.adjustmentToAcquisition = adjustmentToAcquisition;
      this.senderOfDisallowedLoss = senderOfDisallowedLoss;
      this.originalSale = originalSale;
      this.adjustmentToSalePrice = adjustmentToPrice2;
      this.washSaleDisallowed = washSaleDisallowed;
      this.recipientOfDisallowedLoss = recipientOfDisallowedLoss;
      this.reportableGain = reportableGain;
    }

    public Builder withAcquisition(RealizableValue acquisition) {
      this.originalAcquisition = acquisition;
      return this;
    }

    public Builder withAdjustmentToAcquisition(AcquisitionAdjustment adjustment) {
      this.adjustmentToAcquisition = adjustment;
      return this;
    }

    public Builder withSale(RealizableValue sale) {
      this.originalSale = sale;
      return this;
    }

    public Builder withReportableGain(BigMoney gain) {
      this.reportableGain = gain;
      return this;
    }

    public Builder withAdjustmentToSalePrice(BigMoney adjustmentToSalePrice) {
      // TODO(madrake): we can just record a boolean that the wash sale is disallowed
      // TODO(madrake): CONTINUE HERE
      this.adjustmentToSalePrice = adjustmentToSalePrice;
      this.washSaleDisallowed = true;
      return this;
    }

    public Builder withRecipientOfDisallowedLoss(StockId stockId) {
      this.recipientOfDisallowedLoss = stockId;
      return this;
    }

    public Builder withSenderOfDisallowedLoss(StockId stockId) {
      this.senderOfDisallowedLoss = stockId;
      return this;
    }

    public Result build() {
      return new Result(
        stockId,
        originalAcquisition,
        adjustmentToAcquisition,
        senderOfDisallowedLoss,
        originalSale,
        adjustmentToSalePrice,
        washSaleDisallowed,
        recipientOfDisallowedLoss,
        reportableGain);
    }
  }
}
