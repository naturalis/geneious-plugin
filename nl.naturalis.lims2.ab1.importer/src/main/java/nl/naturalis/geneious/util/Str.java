package nl.naturalis.geneious.util;

/**
 * String utilities.
 * 
 * @author Ayco Holleman
 *
 */
public class Str {

  /**
   * The empty, zero-length {@code String}.
   */
  public static final String EMPTY = "";

  /**
   * Zero-pads a string.
   * 
   * @param obj The object to call toString() on. The resulting string is padded. If null, the
   *        returned string will consist of {@code width} zeros plus the separator.
   * @param width The total length of the padded string. If the string itself is wider than the
   *        specified width, the string is printed without padding.
   * @param separator The delimiter to insert between the padding and the string. Useful if you want
   *        to print string like 13:02:08 (the colon being the separator)
   * @return
   */
  public static String zpad(Object obj, int width, String separator) {
    return lpad(obj, width, '0', separator);
  }

  /**
   * Zero-pads a string to the specified width.
   * 
   * @param obj The object to call toString() on. The resulting string is padded. If null, the
   *        returned string will consist of {@code width} zeros plus the separator.
   * @param width The total length of the padded string. If the string itself is wider than the
   *        specified width, the string is printed without padding.
   * @return
   */
  public static String zpad(Object obj, int width) {
    return lpad(obj, width, '0');
  }

  public static String lpad(Object obj, int width, String separator) {
    return lpad(obj, width, ' ', separator);
  }

  public static String pad(Object obj, int width) {
    return pad(obj, width, ' ', EMPTY);
  }

  public static String pad(Object obj, int width, char padChar) {
    return pad(obj, width, padChar, EMPTY);
  }

  public static String pad(Object obj, int width, char padChar, String separator) {
    String s;
    if (obj == null)
      s = EMPTY;
    else {
      s = obj instanceof String ? (String) obj : obj.toString();
      if (s.length() >= width) {
        return s + separator;
      }
    }
    int left = (width - s.length()) / 2;
    int right = width - left - s.length();
    StringBuilder sb = new StringBuilder(width + separator.length());
    for (int i = 0; i < left; ++i)
      sb.append(padChar);
    sb.append(s);
    for (int i = 0; i < right; ++i)
      sb.append(padChar);
    sb.append(separator);
    return sb.toString();
  }

  public static String lpad(Object obj, int width) {
    return lpad(obj, width, ' ', EMPTY);
  }

  public static String lpad(Object obj, int width, char padChar) {
    return lpad(obj, width, padChar, EMPTY);
  }

  public static String lpad(Object obj, int width, char padChar, String separator) {
    String s;
    if (obj == null)
      s = EMPTY;
    else {
      s = obj.toString();
      if (s.length() >= width) {
        return s + separator;
      }
    }
    StringBuilder sb = new StringBuilder(width + separator.length());
    for (int i = s.length(); i < width; ++i) {
      sb.append(padChar);
    }
    sb.append(s);
    sb.append(separator);
    return sb.toString();
  }

  public static String rpad(Object obj, int width, String separator) {
    return rpad(obj, width, ' ', separator);
  }

  public static String rpad(Object obj, int width) {
    return rpad(obj, width, ' ', EMPTY);
  }

  public static String rpad(Object obj, int width, char padChar) {
    return rpad(obj, width, padChar, EMPTY);
  }

  public static String rpad(Object obj, int width, char padChar, String separator) {
    String s;
    if (obj == null)
      s = EMPTY;
    else {
      s = obj instanceof String ? (String) obj : obj.toString();
      if (s.length() >= width) {
        return s + separator;
      }
    }
    StringBuilder sb = new StringBuilder(width + separator.length());
    sb.append(s);
    for (int i = s.length(); i < width; ++i) {
      sb.append(padChar);
    }
    sb.append(separator);
    return sb.toString();
  }
}
