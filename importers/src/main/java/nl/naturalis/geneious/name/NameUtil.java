package nl.naturalis.geneious.name;

import java.util.EnumMap;

import org.apache.commons.lang3.StringUtils;

import nl.naturalis.common.base.NArrays;
import nl.naturalis.geneious.DocumentType;

import static nl.naturalis.geneious.DocumentType.AB1;
import static nl.naturalis.geneious.DocumentType.DUMMY;
import static nl.naturalis.geneious.DocumentType.FASTA;
import static nl.naturalis.geneious.DocumentType.UNKNOWN;

public class NameUtil {

  // V1 plugin uses file name extension type suffixes; V2 plugin appends the type between parentheses
  public static final String[] ab1Suffixes = {".ab1", " (ab1)"};
  public static final String[] fastaSuffixes = {".fas", ".fasta", " (fasta)"};
  public static final String[] dummySuffixes = {".dum", " (dummy)"};

  private static final String[] all = NArrays.combine(ab1Suffixes, fastaSuffixes, dummySuffixes);

  private static EnumMap<DocumentType, String[]> suffixes = new EnumMap<>(DocumentType.class);

  static {
    suffixes.put(AB1, ab1Suffixes);
    suffixes.put(FASTA, fastaSuffixes);
    suffixes.put(DUMMY, dummySuffixes);
  }

  private NameUtil() {}

  /**
   * Infer the document type from the specified name (presumably a document name). Not water proof because users could change the name of
   * the document.
   * 
   * @param name
   * @return
   */
  public static DocumentType inferDocumentTypeFromName(String name) {
    for (DocumentType t : suffixes.keySet()) {
      for (String suffix : suffixes.get(t)) {
        if (name.endsWith(suffix)) {
          return t;
        }
      }
    }
    return UNKNOWN;
  }

  /**
   * Whether or not the provided name is a name that the plugin uses for AB1 documents. Not water proof because users could change the name
   * of the document.
   * 
   * @param name
   * @return
   */
  public static boolean isAb1(String name) {
    for (String suffix : ab1Suffixes) {
      if (name.endsWith(suffix)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Whether or not the provided name is a name that the plugin uses for fasta documents. Not water proof because users could change the
   * name of the document.
   * 
   * @param name
   * @return
   */
  public static boolean isFasta(String name) {
    for (String suffix : fastaSuffixes) {
      if (name.endsWith(suffix)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Whether or not the provided name is a name that the plugin uses for dummy documents. Not water proof because users could change the
   * name of the document.
   * 
   * @param name
   * @return
   */
  public static boolean isDummy(String name) {
    for (String suffix : dummySuffixes) {
      if (name.endsWith(suffix)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Chops off any of the known suffixes from the provided name.
   * 
   * @param name
   * @return
   */
  public static String removeKnownSuffixes(String name) {
    int x;
    for (String suffix : all) {
      x = name.length();
      if (x != (name = StringUtils.removeEnd(name, suffix)).length()) {
        break;
      }
    }
    return name;
  }

  /**
   * Whether or not the provided name ends with one of the known suffixes for document names.
   * 
   * @param name
   * @return
   */
  public static boolean hasKnownSuffix(String name) {
    for (String suffix : all) {
      if (name.endsWith(suffix)) {
        return true;
      }
    }
    return false;
  }

  /**
   * If the provided name has one of the suffixes known to the plugin, it will return that suffix, otherwise null.
   * 
   * @param name
   * @return
   */
  public static String getKnownSuffix(String name) {
    for (String suffix : all) {
      if (name.endsWith(suffix)) {
        return suffix;
      }
    }
    return null;
  }

}
