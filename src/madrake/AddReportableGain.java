package madrake;

import org.joda.money.BigMoney;

import madrake.needsautovalue.AcquisitionAdjustment;
import madrake.needsautovalue.Result;

import com.google.common.base.Function;

class AddReportableGain implements Function<Result, Result> {

  @Override
  public Result apply(Result in) {
    if (in.getOriginalSale() == null) {
      // Stock hasn't been sold yet!
      return in;
    } else {
      // TODO(madrake): this is one of the areas where we have reusable code
      BigMoney reportableGain = in.getOriginalSale().getValue()
          .plus(in.getAdjustmentToSalePrice())
          .minus(in.getOriginalAcquisition().getValue());
      final AcquisitionAdjustment acquisitionAdjustment = in.getAdjustmentToAcquisition();
      if (acquisitionAdjustment != null) {
        reportableGain = reportableGain.minus(acquisitionAdjustment.getGain());
      }
      return in.builder()
          .withReportableGain(
              reportableGain)
          .build();
    }
  }
}
