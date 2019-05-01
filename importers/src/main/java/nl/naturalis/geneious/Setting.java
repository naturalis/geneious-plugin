package nl.naturalis.geneious;

import java.util.Arrays;
import java.util.HashMap;

public enum Setting {

  /**
   * Whether or not to show DEBUG messages in the log file.
   */
  DEBUG("nl.naturalis.geneious.log.debug"),
  /**
   * The end time of the previous operation executed by the user
   */
  LAST_FINISHED("nl.naturalis.geneious.operation.lastEndTime"),
  /**
   * The minimum wait time (in seconds) between any two operations. This works around a bug in Geneious, which currently
   * does not provide a reliable way of establishing whether all documents have ben indexed.
   */
  MIN_WAIT_TIME("nl.naturalis.geneious.operation.minWaitTime");

  private static final HashMap<String, Setting> reverse = new HashMap<>(values().length, 1F);

  static {
    Arrays.stream(values()).forEach(s -> reverse.put(s.getName(), s));
  }

  public static Setting forName(String name) {
    return reverse.get(name);
  }

  private final String name;

  private Setting(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

}
