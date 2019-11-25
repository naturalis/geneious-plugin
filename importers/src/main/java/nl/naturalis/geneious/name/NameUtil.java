package nl.naturalis.geneious.name;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import nl.naturalis.common.StringMethods;
import nl.naturalis.geneious.StoredDocument;
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;

import static java.util.stream.Collectors.toList;

import static nl.naturalis.common.StringMethods.ltrim;
import static nl.naturalis.geneious.Settings.settings;

/**
 * Utility methods shared by the AB1/Fasta Import operation and the Split Name operation.
 * 
 * @author Ayco Holleman
 *
 */
public class NameUtil {

  @SuppressWarnings("unused")
  private static final GuiLogger logger = GuiLogManager.getLogger(NameUtil.class);

  private static final String[] ab1Exts = {"ab1", "ab1 (reversed)"};
  private static final String[] fastaExts = {"fas", "fasta", "txt"};
  private static final String[] dummyExts = {"dum"};

  private NameUtil() {}

  /**
   * Returns the default AB1 file extensions (the "factory" settings).
   * 
   * @return
   */
  public static List<String> getDefaultAb1Suffixes() {
    return Arrays.asList(ab1Exts);
  }

  /**
   * Returns the default fasta file extensions (the "factory" settings).
   * 
   * @return
   */
  public static List<String> getDefaultFastaExtensions() {
    return Arrays.asList(fastaExts);
  }

  /**
   * Returns a comma-separated string containing the default AB1 file extensions (the "factory" settings).
   * 
   * @return
   */
  public static String getDefaultAb1ExtensionsAsString() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < ab1Exts.length; ++i) {
      if (i != 0) {
        sb.append(", ");
      }
      sb.append("*.").append(ab1Exts[i]);
    }
    return sb.toString();
  }

  /**
   * Returns a comma-separated string containing the default AB1 file extensions (the "factory" settings).
   * 
   * @return
   */
  public static String getDefaultFastaExtensionsAsString() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < fastaExts.length; ++i) {
      if (i != 0) {
        sb.append(", ");
      }
      sb.append("*.").append(fastaExts[i]);
    }
    return sb.toString();
  }

  /**
   * Returns the AB1 file extsensions as configured in the Preferences panel.
   * 
   * @return
   */
  public static List<String> getCurrentAb1Extensions() {
    String setting = settings().getAb1FileExtensions();
    if (setting == null) {
      return getDefaultAb1Suffixes();
    }
    String[] exts = StringUtils.split(setting, ",");
    List<String> suffixes = new ArrayList<>(exts.length);
    for (String ext : exts) {
      suffixes.add(ltrim(ltrim(ext.strip(), '*'), '.'));
    }
    return suffixes;
  }

  /**
   * Returns the fasta file extsensions as configured in the Preferences panel.
   * 
   * @return
   */
  public static List<String> getCurrentFastaExtensions() {
    String setting = settings().getFastaFileExtensions();
    if (setting == null) {
      return getDefaultFastaExtensions();
    }
    String[] exts = StringUtils.split(setting, ",");
    List<String> suffixes = new ArrayList<>(exts.length);
    for (String ext : exts) {
      suffixes.add(ltrim(ltrim(ext.strip(), '*'), '.'));
    }
    return suffixes;
  }

  /**
   * Returns the AB1 file extensions prefixed with a dot (like .ab1).
   * 
   * @return
   */
  public static List<String> getCurrentAb1ExtensionsWithDot() {
    return getCurrentAb1Extensions().stream().map(s -> "." + s).collect(toList());
  }

  /**
   * Returns the fasta file extensions prefixed with a dot (like .fas).
   * 
   * @return
   */
  public static List<String> getCurrentFastaExtensionsWithDot() {
    return getCurrentFastaExtensions().stream().map(s -> "." + s).collect(toList());
  }

  /**
   * Returns a list of all known file extensions for documents handled by the plugin, including those for dummy documents.
   * 
   * @return
   */
  public static List<String> getAllKnownFileExtensions() {
    List<String> exts = new ArrayList<>();
    exts.addAll(getCurrentAb1Extensions());
    exts.addAll(getCurrentFastaExtensions());
    exts.addAll(Arrays.asList(dummyExts));
    return exts;
  }

  public static boolean hasKnownFileExtension(String name) {
    return StringMethods.endsWith(name, true, getAllKnownFileExtensions());
  }

  /**
   * Chops off all known suffixes like ".ab1" from the provided name.
   * 
   * @param name
   * @return
   */
  public static String removeKnownSuffixes(String name) {
    return StringMethods.rchop(name, true, getAllKnownFileExtensions());
  }

  public static String getExtractId(StoredDocument doc) {
    return doc.getNaturalisNote().getExtractId();
  }

  public static String getExtractId(StorableDocument doc) {
    return doc.getSequenceInfo().getNaturalisNote().getExtractId();
  }

}
