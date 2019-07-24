package nl.naturalis.geneious.name;

import static nl.naturalis.geneious.DocumentType.AB1;
import static nl.naturalis.geneious.DocumentType.DUMMY;
import static nl.naturalis.geneious.DocumentType.FASTA;

import java.util.EnumMap;

import org.apache.commons.lang3.StringUtils;

import nl.naturalis.common.base.ArrayUtil;
import nl.naturalis.geneious.DocumentType;
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;

/**
 * Utility methods shared by the AB1/Fasta Import operation and the Split Name operation.
 * 
 * @author Ayco Holleman
 *
 */
public class NameUtil {

  @SuppressWarnings("unused")
  private static final GuiLogger logger = GuiLogManager.getLogger(NameUtil.class);

  private static final String[] ab1Suffixes = {".ab1"};
  private static final String[] fastaSuffixes = {".fas", ".fasta", ".txt"};
  private static final String[] dummySuffixes = {".dum"};

  private static final String[] all = ArrayUtil.concat(ab1Suffixes, fastaSuffixes, dummySuffixes);

  private static EnumMap<DocumentType, String[]> suffixes = new EnumMap<>(DocumentType.class);

  static {
    suffixes.put(AB1, ab1Suffixes);
    suffixes.put(FASTA, fastaSuffixes);
    suffixes.put(DUMMY, dummySuffixes);
  }

  private NameUtil() {}

  /**
   * Chops off known suffixex like ".ab1" from the provided name.
   * 
   * @param name
   * @return
   */
  public static String removeKnownSuffixes(String name) {
    for (String suffix : all) {
      if(name.endsWith(suffix)) {
        return StringUtils.removeEnd(name, suffix);
      }
    }
    return name;
  }

}
