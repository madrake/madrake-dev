package madrake;

import org.joda.money.BigMoney;

import madrake.needsautovalue.Result;
import madrake.needsautovalue.Result.Builder;

import com.google.common.base.Function;

//TODO(madrake): should we need this class at all or should we treat nulls as acceptable?
public class NonNullAdjustments implements Function<Result, Result> {

  @Override
  public Result apply(Result in) {
    Builder builder = in.builder();
    if (in.getAdjustmentToSalePrice() == null && 
        in.getOriginalSale() != null /* stock was sold */) {
      builder.withAdjustmentToSalePrice(BigMoney.zero(in.getOriginalSale().getValue().getCurrencyUnit()));
    }
    return builder.build();
  }

}
