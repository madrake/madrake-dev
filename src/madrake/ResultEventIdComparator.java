package madrake;

import java.util.Comparator;

import madrake.needsautovalue.Result;

public class ResultEventIdComparator implements Comparator<Result> {
  
  @Override
  public int compare(Result arg0, Result arg1) {
    return arg0.getStockId().compareTo(arg1.getStockId());
  }
}