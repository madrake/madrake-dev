package madrake;

import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;

import madrake.needsautovalue.Result;

import com.google.common.base.Function;

class AddReportableGain implements Function<Result, Result> {

  @Override
  public Result apply(Result in) {
    if (in.getOriginalSale() == null) {
      // Stock hasn't been sold yet!
      return in;
    } else {
      if (in.getWashSaleDisallowed()) {
        // If the sale was disallowed as a wash sale there can't be any reportable gain by definition
        return in.builder().withReportableGain(BigMoney.zero(CurrencyUnit.USD)).build();
      }
      // TODO(madrake): this is one of the areas where we have reusable code
      BigMoney reportableGain = in.getOriginalSale().getValue()
          .minus(in.getOriginalAcquisition().getValue());
      final AcquisitionAdjustment acquisitionAdjustment = in.getAdjustmentToAcquisition();
      if (acquisitionAdjustment != null) {
        reportableGain = reportableGain.minus(acquisitionAdjustment.getGain());
      }
      return in.builder()
          .withReportableGain(reportableGain)
          .build();
    }
  }
}
