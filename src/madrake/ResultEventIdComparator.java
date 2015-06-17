package madrake;

import java.util.Comparator;

public class ResultEventIdComparator implements Comparator<Result> {
  
  @Override
  public int compare(Result arg0, Result arg1) {
    return arg0.getStockId().compareTo(arg1.getStockId());
  }
}