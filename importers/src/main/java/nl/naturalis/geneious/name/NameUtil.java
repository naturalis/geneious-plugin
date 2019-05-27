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
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.note.NaturalisField;
import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.smpl.DummySequence;

public class NameUtil {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(NameUtil.class);

  public static final String[] ab1Suffixes = {".ab1"};
  public static final String[] fastaSuffixes = {".fas", ".fasta"};
  public static final String[] dummySuffixes = {".dum"};

  private static final String[] all = NArrays.concat(ab1Suffixes, fastaSuffixes, dummySuffixes);

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
    DocumentType guess;
    if (apd.getDocumentClass() == DefaultNucleotideSequence.class) {
      NaturalisNote note = new NaturalisNote(apd);
      if (note.isEmpty() || !note.get(NaturalisField.SEQ_MARKER).equals("Dum")) {
        guess = FASTA;
      } else {
        guess = DUMMY;
      }
    } else if (apd.getDocumentClass() == DefaultNucleotideGraphSequence.class) {
      guess = AB1;
    } else {
      guiLogger.error("Document \"%s\": unexpected document class: %s", apd.getName(), apd.getDocumentClass());
      guess = UNKNOWN;
    }
    return guess;
  }

  /**
   * Whether or not the provided name is a name that the plugin uses for dummy documents. Not water proof because users could change the name
   * of the document.
   * 
   * @param name
   * @return
   */
  public static boolean isDummy(AnnotatedPluginDocument apd) {
    return getDocumentType(apd) == DUMMY;
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

}
