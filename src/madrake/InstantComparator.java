package madrake;

import java.util.Comparator;

import madrake.needsautovalue.Event;

final class InstantComparator implements Comparator<Event> {
  @Override
  public int compare(Event arg0, Event arg1) {
    return arg0.getValue().getInstant().compareTo(arg1.getValue().getInstant());
  }
}