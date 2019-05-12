package nl.naturalis.geneious.name;

import static nl.naturalis.geneious.DocumentType.AB1;
import static nl.naturalis.geneious.DocumentType.DUMMY;
import static nl.naturalis.geneious.DocumentType.FASTA;
import static nl.naturalis.geneious.DocumentType.UNKNOWN;

import java.util.EnumMap;

import org.apache.commons.lang3.StringUtils;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.implementations.sequence.DefaultNucleotideGraphSequence;
import com.biomatters.geneious.publicapi.implementations.sequence.DefaultNucleotideSequence;

import nl.naturalis.common.base.NArrays;
import nl.naturalis.geneious.DocumentType;
import nl.naturalis.geneious.NaturalisPluginException;
import nl.naturalis.geneious.StorableDocument;
import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.smpl.DummySequence;

public class NameUtil {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(NameUtil.class);

  // V1 plugin uses file name extension type suffixes; V2 plugin appends the type between parentheses. The V2 suffixes are (and must be)
  // listed first. They are now the default suffixes and other code depends on them being listed first.
  public static final String[] ab1Suffixes = {" (ab1)", ".ab1"};
  public static final String[] fastaSuffixes = {" (fasta)", ".fas", ".fasta"};
  public static final String[] dummySuffixes = {" (dummy)", ".dum"};

  private static final String[] all = NArrays.combine(ab1Suffixes, fastaSuffixes, dummySuffixes);

  private static EnumMap<DocumentType, String[]> suffixes = new EnumMap<>(DocumentType.class);

  static {
    suffixes.put(AB1, ab1Suffixes);
    suffixes.put(FASTA, fastaSuffixes);
    suffixes.put(DUMMY, dummySuffixes);
  }

  private NameUtil() {}

  /**
   * Infer the document type from the specified name (presumably a document name). Not water proof because users could change the name of the
   * document.
   * 
   * @param name
   * @return
   */
  public static DocumentType getDocumentType(AnnotatedPluginDocument apd) {
    if (apd.getDocumentClass() == DummySequence.class) {
      // That's 100% certainty, but this class was only introduced in version 2 of the plugin.
      return DUMMY;
    }
    DocumentType guess1;
    if (apd.getDocumentClass() == DefaultNucleotideSequence.class) {
      guess1 = FASTA;
    } else if (apd.getDocumentClass() == DefaultNucleotideGraphSequence.class) {
      guess1 = AB1;
    } else {
      String fmt = "Document \"%s\": unexpected document class: %s";
      String msg = String.format(fmt, apd.getName(), apd.getDocumentClass());
      throw new NaturalisPluginException(msg);
    }
    DocumentType guess2 = UNKNOWN;
    for (DocumentType t : suffixes.keySet()) {
      for (String suffix : suffixes.get(t)) {
        if (apd.getName().endsWith(suffix)) {
          guess2 = t;
          break;
        }
      }
    }
    if (guess2 == UNKNOWN) {
      return guess1;
    }
    if (guess1 != guess2) {
      guiLogger.error("Document \"%s\": mismatch between suffix (\"%s\") and class (%s)",
          apd.getName(),
          guess2,
          apd.getDocumentClass());
      return UNKNOWN;
    }
    return guess1;
  }

  /**
   * Whether or not the provided name is a name that the plugin uses for AB1 documents. Not water proof because users could change the name of
   * the document.
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
   * Whether or not the provided name is a name that the plugin uses for fasta documents. Not water proof because users could change the name
   * of the document.
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
   * Whether or not the provided name is a name that the plugin uses for dummy documents. Not water proof because users could change the name
   * of the document.
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
    for (String suffix : all) {
      if (name.endsWith(suffix)) {
        return StringUtils.removeEnd(name, suffix);
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

  /**
   * Returns the default suffix for the provided document, based on its {@link DocumentType}.
   * 
   * @param doc
   * @return
   */
  public static String getDefaultSuffix(StorableDocument doc) {
    switch (doc.getSequenceInfo().getDocumentType()) {
      case AB1:
        return ab1Suffixes[0];
      case FASTA:
        return fastaSuffixes[0];
      case DUMMY:
        return dummySuffixes[0];
    }
    throw new IllegalArgumentException("Cannot return name suffix for unknown document type");
  }

}
