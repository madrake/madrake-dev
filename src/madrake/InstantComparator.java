package madrake;

import java.util.Comparator;

final class InstantComparator implements Comparator<Event> {
  @Override
  public int compare(Event arg0, Event arg1) {
    return arg0.getDate().compareTo(arg1.getDate());
  }
}