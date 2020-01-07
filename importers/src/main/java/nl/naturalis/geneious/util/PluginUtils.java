package nl.naturalis.geneious.util;

import static nl.naturalis.geneious.DocumentType.AB1;
import static nl.naturalis.geneious.DocumentType.CONTIG;
import static nl.naturalis.geneious.DocumentType.DUMMY;
import static nl.naturalis.geneious.DocumentType.FASTA;
import static nl.naturalis.geneious.DocumentType.UNKNOWN;
import static nl.naturalis.geneious.name.NameUtil.getCurrentAb1ExtensionsWithDot;
import static nl.naturalis.geneious.name.NameUtil.getCurrentFastaExtensionsWithDot;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Optional;
import org.apache.commons.io.FileUtils;
import com.biomatters.geneious.publicapi.databaseservice.WritableDatabaseService;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentField;
import com.biomatters.geneious.publicapi.plugin.GeneiousService;
import com.biomatters.geneious.publicapi.plugin.ServiceUtilities;
import nl.naturalis.common.StringMethods;
import nl.naturalis.geneious.DocumentType;
import nl.naturalis.geneious.NaturalisPluginException;
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.note.NaturalisField;
import nl.naturalis.geneious.note.NaturalisNote;

/**
 * Commonly used methods.
 */
public class PluginUtils {

  private static final GuiLogger logger = GuiLogManager.getLogger(PluginUtils.class);

  private PluginUtils() {}

  public static Optional<WritableDatabaseService> getSelectedFolder() {
    GeneiousService svc = ServiceUtilities.getSelectedService();
    if (svc instanceof WritableDatabaseService) {
      return Optional.of((WritableDatabaseService) svc);
    }
    return Optional.empty();
  }

  public static Optional<WritableDatabaseService> getSelectedDatabase() {
    Optional<WritableDatabaseService> sf = getSelectedFolder();
    return sf.isEmpty() ? sf : Optional.of(sf.get().getPrimaryDatabaseRoot());
  }

  public static String getSelectedDatabaseName() {
    Optional<WritableDatabaseService> db = getSelectedDatabase();
    return db.isEmpty() ? "<no database selected>" : db.get().getFolderName();
  }

  /**
   * Returns the {@link DocumentType document type} of the provided document, based on the class of the document and the annotations on the
   * document.
   * 
   * @param name
   * @return
   */
  public static DocumentType getDocumentType(AnnotatedPluginDocument apd) {
    return getDocumentType(apd, new NaturalisNote(apd));
  }

  /**
   * Returns the {@link DocumentType document type} of the provided document, based on the class of the document and the provided
   * annotations (presumedly read from the document).
   * 
   * @param apd
   * @param note
   * @return
   */
  public static DocumentType getDocumentType(AnnotatedPluginDocument apd, NaturalisNote note) {
    if (apd.getDocumentClass() == DUMMY.getGeneiousType()) {
      // That's 100% certainty, but this class was only introduced in version 2 of the plugin.
      return DUMMY;
    }
    if (apd.getDocumentClass() == AB1.getGeneiousType()) {
      return AB1;
    }
    if (apd.getDocumentClass() == CONTIG.getGeneiousType()) {
      return CONTIG;
    }
    if (apd.getDocumentClass() == FASTA.getGeneiousType()) {
      if (note.isEmpty() || !note.get(NaturalisField.SEQ_MARKER).equals("Dum")) {
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
    if (d == null) {
      throw new NaturalisPluginException("Document \"%s\": Modified date not set", doc.getName());
    }
    return d;
  }

  /**
   * Whether or not the provided file is an AB1 file, judged by the file name extension and <i>negatively</i> judged by the contents not
   * looking like a fasta file.
   * 
   * @param f
   * @return
   * @throws IOException
   */
  public static boolean isAb1File(File f) throws IOException {
    return StringMethods.endsWith(f.getName(), true, getCurrentAb1ExtensionsWithDot());
  }

  /**
   * Whether or not the specified file is a fasta file, judged by the file name extension and contents of the file.
   * 
   * @param f
   * @return
   * @throws IOException
   */
  public static boolean isFastaFile(File f) throws IOException {
    if (StringMethods.endsWith(f.getName(), true, getCurrentFastaExtensionsWithDot())) {
      if (firstChar(f) != '>') {
        logger.warn("Invalid fasta file: %s. First character in fasta file must be '>'", f.getName());
        return false;
      }
      return true;
    }
    return false;
  }

  private static char firstChar(File f) throws IOException {
    try (InputStreamReader r = new InputStreamReader(FileUtils.openInputStream(f))) {
      return (char) r.read();
    }
  }

}
