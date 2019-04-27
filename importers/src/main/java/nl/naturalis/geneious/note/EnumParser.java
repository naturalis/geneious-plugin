package nl.naturalis.geneious.note;

/**
 * Parses strings into enum constants.
 *
 * @author Ayco Holleman
 */
public class EnumParser {

  private final Class<?> enumClass;

  public EnumParser(Class<?> enumClass) {
    this.enumClass = enumClass;
  }

  /**
   * Parses the provided string into a enum constant of the class provided when instatiating the {@code EnumParser}.
   * 
   * @param s
   * @return
   */
  @SuppressWarnings("unchecked")
  public <T extends Enum<T>> T parse(String s) {
    for (Object obj : enumClass.getEnumConstants()) {
      if (s.equals(obj.toString())) {
        return (T) obj;
      }
    }
    String fmt = "Illegal value for %s: \"%s\"";
    String msg = String.format(fmt, enumClass.getSigners(), s);
    throw new IllegalArgumentException(msg);
  }

}
