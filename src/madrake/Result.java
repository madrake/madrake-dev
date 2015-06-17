package madrake;

// TODO(madrake): need to define what each field means precisely and whether it can be null
public final class Result {

  private final StockId stockId;
  private final MattsInstant acquisitionDate;
  private final Price trueAcquisitionPrice;
  private final AdjustmentToPrice adjustmentToAcquisitionPrice;
  private final StockId senderOfDisallowedLoss;
  private final MattsInstant saleDate;
  private final Price trueSalePrice;
  private final AdjustmentToPrice adjustmentToSalePrice;
  private final StockId recipientOfDisallowedLoss;
  private final ReportableGain reportableGain;

  public Result(
      StockId stockId1,
      MattsInstant mattsInstant, 
      Price price,
      AdjustmentToPrice adjustmentToPrice, 
      StockId senderOfDisallowedLoss,
      MattsInstant mattsInstant2, 
      Price price2,
      AdjustmentToPrice adjustmentToPrice2, 
      StockId recipientOfDisallowedLoss,
      ReportableGain reportableGain) {
    this.stockId = stockId1;
    this.acquisitionDate = mattsInstant;
    this.trueAcquisitionPrice = price;
    this.adjustmentToAcquisitionPrice = adjustmentToPrice;
    this.senderOfDisallowedLoss = senderOfDisallowedLoss;
    this.saleDate = mattsInstant2;
    this.trueSalePrice = price2;
    this.adjustmentToSalePrice = adjustmentToPrice2;
    this.recipientOfDisallowedLoss = recipientOfDisallowedLoss;
    this.reportableGain = reportableGain;
  }

  public StockId getStockId() {
    return stockId;
  }

  public MattsInstant getAcquisitionDate() {
    return acquisitionDate;
  }


  public Price getTrueAcquisitionPrice() {
    return trueAcquisitionPrice;
  }


  public AdjustmentToPrice getAdjustmentToAcquisitionPrice() {
    return adjustmentToAcquisitionPrice;
  }


  public StockId getSenderOfDisallowedLoss() {
    return senderOfDisallowedLoss;
  }


  public MattsInstant getSaleDate() {
    return saleDate;
  }


  public Price getTrueSalePrice() {
    return trueSalePrice;
  }


  public AdjustmentToPrice getAdjustmentToSalePrice() {
    return adjustmentToSalePrice;
  }


  public StockId getRecipientOfDisallowedLoss() {
    return recipientOfDisallowedLoss;
  }


  public ReportableGain getReportableGain() {
    return reportableGain;
  }

  // TODO(madrake): replace both of the following with Objects usages

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + ((acquisitionDate == null) ? 0 : acquisitionDate.hashCode());
    result = prime
        * result
        + ((adjustmentToAcquisitionPrice == null) ? 0
            : adjustmentToAcquisitionPrice.hashCode());
    result = prime
        * result
        + ((adjustmentToSalePrice == null) ? 0 : adjustmentToSalePrice
            .hashCode());
    result = prime
        * result
        + ((recipientOfDisallowedLoss == null) ? 0 : recipientOfDisallowedLoss
            .hashCode());
    result = prime * result
        + ((reportableGain == null) ? 0 : reportableGain.hashCode());
    result = prime * result + ((saleDate == null) ? 0 : saleDate.hashCode());
    result = prime
        * result
        + ((senderOfDisallowedLoss == null) ? 0 : senderOfDisallowedLoss
            .hashCode());
    result = prime * result + ((stockId == null) ? 0 : stockId.hashCode());
    result = prime
        * result
        + ((trueAcquisitionPrice == null) ? 0 : trueAcquisitionPrice.hashCode());
    result = prime * result
        + ((trueSalePrice == null) ? 0 : trueSalePrice.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Result other = (Result) obj;
    if (acquisitionDate == null) {
      if (other.acquisitionDate != null)
        return false;
    } else if (!acquisitionDate.equals(other.acquisitionDate))
      return false;
    if (adjustmentToAcquisitionPrice == null) {
      if (other.adjustmentToAcquisitionPrice != null)
        return false;
    } else if (!adjustmentToAcquisitionPrice
        .equals(other.adjustmentToAcquisitionPrice))
      return false;
    if (adjustmentToSalePrice == null) {
      if (other.adjustmentToSalePrice != null)
        return false;
    } else if (!adjustmentToSalePrice.equals(other.adjustmentToSalePrice))
      return false;
    if (recipientOfDisallowedLoss == null) {
      if (other.recipientOfDisallowedLoss != null)
        return false;
    } else if (!recipientOfDisallowedLoss
        .equals(other.recipientOfDisallowedLoss))
      return false;
    if (reportableGain == null) {
      if (other.reportableGain != null)
        return false;
    } else if (!reportableGain.equals(other.reportableGain))
      return false;
    if (saleDate == null) {
      if (other.saleDate != null)
        return false;
    } else if (!saleDate.equals(other.saleDate))
      return false;
    if (senderOfDisallowedLoss == null) {
      if (other.senderOfDisallowedLoss != null)
        return false;
    } else if (!senderOfDisallowedLoss.equals(other.senderOfDisallowedLoss))
      return false;
    if (stockId == null) {
      if (other.stockId != null)
        return false;
    } else if (!stockId.equals(other.stockId))
      return false;
    if (trueAcquisitionPrice == null) {
      if (other.trueAcquisitionPrice != null)
        return false;
    } else if (!trueAcquisitionPrice.equals(other.trueAcquisitionPrice))
      return false;
    if (trueSalePrice == null) {
      if (other.trueSalePrice != null)
        return false;
    } else if (!trueSalePrice.equals(other.trueSalePrice))
      return false;
    return true;
  }

  // TODO(madrake): make this cleaner
  
  @Override
  public String toString() {
    return "Result [stockId=" + stockId + ", acquisitionDate=" + acquisitionDate
        + ", trueAcquisitionPrice=" + trueAcquisitionPrice + ", adjustmentToAcquisitionPrice="
        + adjustmentToAcquisitionPrice + ", senderOfDisallowedLoss=" + senderOfDisallowedLoss
        + ", saleDate=" + saleDate + ", trueSalePrice=" + trueSalePrice
        + ", adjustmentToSalePrice=" + adjustmentToSalePrice + ", recipientOfDisallowedLoss="
        + recipientOfDisallowedLoss + ", reportableGain=" + reportableGain + "]";
  }

  public Builder builder() {
    return new Builder(
        stockId, 
        acquisitionDate, 
        trueAcquisitionPrice, 
        adjustmentToAcquisitionPrice, 
        senderOfDisallowedLoss, 
        saleDate,
        trueSalePrice,
        adjustmentToSalePrice,
        recipientOfDisallowedLoss,
        reportableGain);
  }
  
  static final class Builder {
    private StockId stockId = null;
    private MattsInstant acquisitionDate = null;
    private Price trueAcquisitionPrice = null;
    private AdjustmentToPrice adjustmentToAcquisitionPrice = null;
    private StockId senderOfDisallowedLoss = null;
    private MattsInstant saleDate = null;
    private Price trueSalePrice = null;
    private AdjustmentToPrice adjustmentToSalePrice = null;
    private StockId recipientOfDisallowedLoss = null;
    private ReportableGain reportableGain = null;
    
    public Builder(
        StockId stockId1,
        MattsInstant mattsInstant, 
        Price price,
        AdjustmentToPrice adjustmentToPrice, 
        StockId senderOfDisallowedLoss,
        MattsInstant mattsInstant2, 
        Price price2,
        AdjustmentToPrice adjustmentToPrice2, 
        StockId recipientOfDisallowedLoss,
        ReportableGain reportableGain) {
      this.stockId = stockId1;
      this.acquisitionDate = mattsInstant;
      this.trueAcquisitionPrice = price;
      this.adjustmentToAcquisitionPrice = adjustmentToPrice;
      this.senderOfDisallowedLoss = senderOfDisallowedLoss;
      this.saleDate = mattsInstant2;
      this.trueSalePrice = price2;
      this.adjustmentToSalePrice = adjustmentToPrice2;
      this.recipientOfDisallowedLoss = recipientOfDisallowedLoss;
      this.reportableGain = reportableGain;
    }

    public Builder withAcquisitionDate(MattsInstant date) {
      this.acquisitionDate = date;
      return this;
    }

    public Builder withAcquisitionPrice(Price price) {
      this.trueAcquisitionPrice = price;
      return this;
    }

    public Result build() {
      return new Result(stockId, 
        acquisitionDate, 
        trueAcquisitionPrice, 
        adjustmentToAcquisitionPrice, 
        senderOfDisallowedLoss, 
        saleDate,
        trueSalePrice,
        adjustmentToSalePrice,
        recipientOfDisallowedLoss,
        reportableGain);
    }

    public Builder withSaleDate(MattsInstant date) {
      this.saleDate = date;
      return this;
    }

    public Builder withSalePrice(Price price) {
      this.trueSalePrice = price;
      return this;
    }

    // TODO(madrake): these should be typed not primitive?
    
    public Builder withReportableGain(long gain) {
      this.reportableGain = new ReportableGain(gain);
      return this;
    }

    public Builder withAdjustmentToAcquisitionPrice(long price) {
      this.adjustmentToAcquisitionPrice = new AdjustmentToPrice(price);
      return this;
    }

    public Builder withAdjustmentToSalePrice(long price) {
      this.adjustmentToSalePrice = new AdjustmentToPrice(price);
      return this;
    }
    
    public Builder withRecipientOfDisallowedLoss(StockId stockId) {
      this.recipientOfDisallowedLoss = stockId;
      return this;
    }

    public Builder withAdjustmentToAcquisitionPrice(AdjustmentToPrice adjustment) {
      this.adjustmentToAcquisitionPrice = adjustment;
      return this;
    }
    
    public Builder withSenderOfDisallowedLoss(StockId stockId) {
      this.senderOfDisallowedLoss = stockId;
      return this;
    }
  }
}
