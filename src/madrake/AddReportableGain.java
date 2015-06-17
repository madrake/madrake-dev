package madrake;

import com.google.common.base.Function;

class AddReportableGain implements Function<Result, Result> {

  @Override
  public Result apply(Result in) {
    // TODO(madrake): this is one of the areas where we have reusable code
    return in.builder()
        .withReportableGain(
            in.getTrueSalePrice()
                .plus(in.getAdjustmentToSalePrice())
                .minus(in.getTrueAcquisitionPrice())
                .minus(in.getAdjustmentToAcquisitionPrice())
                .getPrice())
        .build();
  }

}
