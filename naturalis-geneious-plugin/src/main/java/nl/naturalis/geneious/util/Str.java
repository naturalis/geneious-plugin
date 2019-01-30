package nl.naturalis.geneious.util;

import java.util.ArrayList;

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

  /**
   * Chop {@code word} from the left of {@code string} and repeat until {@code string} does not
   * start with {@code word}.
   * 
   * @param string
   * @param word
   * @return
   */
  public static String lchop(String string, String word) {
    if (string == null) {
      return null;
    }
    if (word == null || word.length() == 0 || word.length() > string.length()) {
      return string;
    }
    int i = 0;
    while (string.regionMatches(i, word, 0, word.length()))
      i += word.length();
    return i == string.length() ? EMPTY : string.substring(i);
  }

  /**
   * Chop {@code word} from the right of {@code string} and repeat until {@code string} does not end
   * with {@code word}.
   * 
   * @param string
   * @param word
   * @return
   */
  public static String rchop(String string, String word) {
    if (string == null)
      return null;
    if (word == null)
      return string;
    while (string.endsWith(word))
      string = string.substring(0, string.length() - word.length());
    return string;
  }
  /**
   * Null-safe split method hat does not interpret the delimiter as a regular
   * expression.
   * 
   * @param s
   * @param delim
   * @return
   */
  public static String[] split(String s, String delim)
  {
    return split(s, delim, 8);
  }

  /**
   * Null-safe split method that does not interpret the delimiter as a regular
   * expression.
   * 
   * @param s
   *            The string to split
   * @param delim
   *            The delimiter around which to split
   * @param numParts
   *            The expected number of parts (might end up larger)
   * @return
   */
  public static String[] split(String s, String delim, int numParts)
  {
    if (s == null)
      return null;
    ArrayList<String> chunks = new ArrayList<>(numParts);
    int from = 0;
    int to = 0;
    while (from < s.length()) {
      to = s.indexOf(delim, from);
      if (to == -1) {
        chunks.add(s.substring(from));
        break;
      }
      chunks.add(s.substring(from, to));
      from = to + delim.length();
    }
    return chunks.toArray(new String[chunks.size()]);
  }

}
