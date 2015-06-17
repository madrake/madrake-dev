package madrake;

import madrake.Result.Builder;

import com.google.common.base.Function;

public class NonNullAdjustments implements Function<Result, Result> {

  @Override
  public Result apply(Result in) {
    Builder builder = in.builder();
    if (in.getAdjustmentToAcquisitionPrice() == null) {
      builder.withAdjustmentToAcquisitionPrice(0);
    }
    if (in.getAdjustmentToSalePrice() == null) {
      builder.withAdjustmentToSalePrice(0);
    }
    return builder.build();
  }

}
