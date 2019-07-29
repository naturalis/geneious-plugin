package nl.naturalis.geneious.note;

/**
 * Parses strings into enum constants.
 *
 * @author Ayco Holleman
 */
class EnumParser {

  private final Class<?> enumClass;

  /**
   * Creates an {@code EnumParser} for the provided {@code enum} class.
   * 
   * @param enumClass
   */
  EnumParser(Class<?> enumClass) {
    this.enumClass = enumClass;
  }

  /**
   * Parses the provided string into a enum constant of the class passed in through the constructor.
   * 
   * @param s
   * @return
   */
  @SuppressWarnings("unchecked")
  <T extends Enum<T>> T parse(String s) {
    for(Object obj : enumClass.getEnumConstants()) {
      if(s.equals(obj.toString())) {
        return (T) obj;
      }
    }
    String fmt = "Illegal value for %s: \"%s\"";
    String msg = String.format(fmt, enumClass.getSigners(), s);
    throw new IllegalArgumentException(msg);
  }

}
