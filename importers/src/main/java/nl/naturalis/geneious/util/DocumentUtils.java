package nl.naturalis.geneious.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentField;

import org.apache.commons.io.FileUtils;

import nl.naturalis.geneious.StoredDocument;
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.note.NaturalisField;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.stripEnd;

import static nl.naturalis.geneious.Settings.settings;

/**
 * Various methods related to Geneious documents.
 */
public class DocumentUtils {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(DocumentUtils.class);

  private DocumentUtils() {}

  public static Date getDateModifield(AnnotatedPluginDocument doc) {
    Date d = (Date) doc.getFieldValue(DocumentField.MODIFIED_DATE_FIELD);
    if (d == null) {
      throw new NullPointerException("Did not expect document modification date to be null");
    }
    return d;
  }
  
  /**
   * Whether or not the specified file is an AB1 file as per the user-provided file extensions in the Geneious Preferences
   * panel.
   * 
   * @param f
   * @return
   * @throws IOException
   */
  public static boolean isAb1File(File f) throws IOException {
    Set<String> exts = DocumentUtils.getAb1Extensions();
    if (exts.isEmpty()) { // then this is the best we can do:
      return firstChar(f) != '>';
    }
    for (String ext : exts) {
      if (f.getName().endsWith(ext)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Whether or not the specified file is a fasta file as per the user-provided file extensions in the Geneious
   * Preferences panel.
   * 
   * @param f
   * @return
   * @throws IOException
   */
  public static boolean isFastaFile(File f) throws IOException {
    Set<String> exts = DocumentUtils.getFastaExtensions();
    if (exts.isEmpty()) { // then this is the best we can do:
      return firstChar(f) == '>';
    }
    for (String ext : exts) {
      if (f.getName().endsWith(ext)) {
        if (firstChar(f) == '>') {
          return true;
        }
        guiLogger.warn("Invalid fasta file: %s. First character in file must be '>'", f.getName());
        return false;
      }
    }
    return false;
  }

  public static boolean isDummyDocument(StoredDocument document) {
    String marker = document.getNaturalisNote().get(NaturalisField.SEQ_MARKER);
    return Objects.equals(marker, "Dum");
  }

  /**
   * Returns the AB1 file extensions in the Geneious Preferences panel.
   * 
   * @return
   */
  public static Set<String> getAb1Extensions() {
    Set<String> exts = new HashSet<>();
    String s = settings().getAb1FileExtensions();
    if (s != null && !(s = stripEnd(s, ", ")).equals("*")) {
      Arrays.stream(s.split(",")).forEach(ext -> {
        ext = ext.trim().toLowerCase();
        if (isNotBlank(ext)) {
          if (!ext.startsWith(".")) {
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
    if (s != null && !(s = stripEnd(s, ", ")).equals("*")) {
      Arrays.stream(s.split(",")).forEach(ext -> {
        ext = ext.trim().toLowerCase();
        if (isNotBlank(ext)) {
          if (!ext.startsWith(".")) {
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
