package nl.naturalis.geneious.util;

import static nl.naturalis.geneious.DocumentType.AB1;
import static nl.naturalis.geneious.DocumentType.CONTIG;
import static nl.naturalis.geneious.DocumentType.DUMMY;
import static nl.naturalis.geneious.DocumentType.FASTA;
import static nl.naturalis.geneious.DocumentType.UNKNOWN;
import static nl.naturalis.geneious.Settings.settings;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.stripEnd;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentField;

import nl.naturalis.geneious.DocumentType;
import nl.naturalis.geneious.NaturalisPluginException;
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.note.NaturalisField;
import nl.naturalis.geneious.note.NaturalisNote;

/**
 * Various methods related to Geneious documents.
 */
public class DocumentUtils {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(DocumentUtils.class);

  private DocumentUtils() {}

  /**
   * Returns the {@link DocumentType document type} of the provided document.
   * 
   * @param name
   * @return
   */
  public static DocumentType getDocumentType(AnnotatedPluginDocument apd) {
    if(apd.getDocumentClass() == DUMMY.getGeneiousType()) {
      // That's 100% certainty, but this class was only introduced in version 2 of the plugin.
      return DUMMY;
    }
    if(apd.getDocumentClass() == AB1.getGeneiousType()) {
      return AB1;
    }
    if(apd.getDocumentClass() == CONTIG.getGeneiousType()) {
      return CONTIG;
    }
    if(apd.getDocumentClass() == FASTA.getGeneiousType()) {
      NaturalisNote note = new NaturalisNote(apd);
      if(note.isEmpty() || !note.get(NaturalisField.SEQ_MARKER).equals("Dum")) {
        return FASTA;
      }
      return DUMMY;
    }
    return UNKNOWN;
  }

  /**
   * Returns the value of the last modified date annotation on the provided document.
   * 
   * @param doc
   * @return
   */
  public static Date getDateModifield(AnnotatedPluginDocument doc) {
    Date d = (Date) doc.getFieldValue(DocumentField.MODIFIED_DATE_FIELD);
    if(d == null) {
      throw new NaturalisPluginException("Document \"%s\": Modified date not set", doc.getName());
    }
    return d;
  }

  /**
   * Whether or not the provided file is an AB1 file (judged solely by the the file name extension, so not water-tight).
   * panel.
   * 
   * @param f
   * @return
   * @throws IOException
   */
  public static boolean isAb1File(File f) throws IOException {
    Set<String> exts = DocumentUtils.getAb1Extensions();
    if(exts.isEmpty()) { // then this is the best we can do:
      return firstChar(f) != '>';
    }
    for (String ext : exts) {
      if(f.getName().endsWith(ext)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Whether or not the specified file is a fasta file (judged solely by the the file name extension, so not water-tight).
   * 
   * @param f
   * @return
   * @throws IOException
   */
  public static boolean isFastaFile(File f) throws IOException {
    Set<String> exts = DocumentUtils.getFastaExtensions();
    if(exts.isEmpty()) { // then this is the best we can do:
      return firstChar(f) == '>';
    }
    for (String ext : exts) {
      if(f.getName().endsWith(ext)) {
        if(firstChar(f) == '>') {
          return true;
        }
        guiLogger.warn("Invalid fasta file: %s. First character in file must be '>'", f.getName());
        return false;
      }
    }
    return false;
  }

  /**
   * Returns the AB1 file extensions in the Geneious Preferences panel.
   * 
   * @return
   */
  public static Set<String> getAb1Extensions() {
    Set<String> exts = new HashSet<>();
    String s = settings().getAb1FileExtensions();
    if(s != null && !(s = stripEnd(s, ", ")).equals("*")) {
      Arrays.stream(s.split(",")).forEach(ext -> {
        ext = ext.trim().toLowerCase();
        if(isNotBlank(ext)) {
          if(!ext.startsWith(".")) {
            ext = "." + ext;
          }
          exts.add(ext);
        }
      });
    }
    return exts;
  }

  /**
   * Returns the fasta file extensions in the Geneious Preferences panel.
   * 
   * @return
   */
  public static Set<String> getFastaExtensions() {
    Set<String> exts = new HashSet<>();
    String s = settings().getFastaFileExtensions();
    if(s != null && !(s = stripEnd(s, ", ")).equals("*")) {
      Arrays.stream(s.split(",")).forEach(ext -> {
        ext = ext.trim().toLowerCase();
        if(isNotBlank(ext)) {
          if(!ext.startsWith(".")) {
            ext = "." + ext;
          }
          exts.add(ext);
        }
      });
    }
    return exts;
  }

  private static char firstChar(File f) throws IOException {
    try (InputStreamReader isr = new InputStreamReader(FileUtils.openInputStream(f))) {
      return (char) isr.read();
    }
  }

}
