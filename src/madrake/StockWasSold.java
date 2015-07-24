package madrake;

import madrake.needsautovalue.Result;

import com.google.common.base.Predicate;

final class StockWasSold implements Predicate<Result> {
  // Make sure the stock actually did get sold.
  @Override
  public boolean apply(Result input) {
    return input.getOriginalSale() != null;
  }
}