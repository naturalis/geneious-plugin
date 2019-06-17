package nl.naturalis.geneious.util;

import static com.biomatters.geneious.publicapi.documents.DocumentUtilities.addAndReturnGeneratedDocuments;
import static nl.naturalis.geneious.note.NaturalisField.SEQ_EXTRACT_ID;
import static nl.naturalis.geneious.note.NaturalisField.SEQ_MARKER;
import static nl.naturalis.geneious.util.QueryUtils.getTargetDatabase;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.biomatters.geneious.publicapi.databaseservice.DatabaseService;
import com.biomatters.geneious.publicapi.databaseservice.DatabaseServiceException;
import com.biomatters.geneious.publicapi.databaseservice.WritableDatabaseService;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument.DocumentNotes;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.implementations.sequence.DefaultNucleotideSequence;

import nl.naturalis.geneious.NaturalisPluginException;
import nl.naturalis.geneious.note.NaturalisNote;

/**
 * An extension of {@code DefaultNucleotideSequence} solely meant to create "ping documents". See {@link Ping}.
 *
 * @author Ayco Holleman
 */
public class PingSequence extends DefaultNucleotideSequence {

  static final String PING_FOLER = "@ping";

  private static final String NUCLEOTIDES = "AAAAAAAAAA";
  private static final String DUMMY_MARKER = "Ping";

  private static final String userFolderName = System.getProperty("user.name");
  private static final String geneiousFolderPrefix = "Folder: ";

  static void delete(AnnotatedPluginDocument pingDocument) throws DatabaseServiceException {
    DatabaseService userFolder = pingDocument.getDatabase();
    if (userFolder.getName().equals(geneiousFolderPrefix + userFolderName)) {
      WritableDatabaseService pingFolder = (WritableDatabaseService) userFolder.getParentService();
      if (pingFolder.getName().equals(geneiousFolderPrefix + PING_FOLER)) {
        pingFolder.removeChildFolder(userFolderName);
        return;
      }
    }
    throw new NaturalisPluginException("Unexpected location for ping document: " + pingDocument.getDatabase().getFullPath());
  }

  private final String pingValue;

  /**
   * No-arg constructor required by Geneious framework.
   */
  public PingSequence() {
    this("shouldn't happen");
  }

  PingSequence(String pingValue) {
    super(pingValue, "", NUCLEOTIDES, new Date());
    this.pingValue = pingValue;
  }

  /**
   * Wraps the sequence into a {@code StoredDoucment}.
   * 
   * @return
   * @throws DatabaseServiceException
   */
  void save() throws DatabaseServiceException {
    WritableDatabaseService pingFolder = getTargetDatabase().createChildFolder(PING_FOLER);
    WritableDatabaseService userFolder = pingFolder.createChildFolder(userFolderName);
    pingFolder.setColor(Color.lightGray);
    userFolder.setColor(Color.lightGray);
    AnnotatedPluginDocument apd = DocumentUtilities.createAnnotatedPluginDocument(this);
    NaturalisNote note = new NaturalisNote();
    note.setDocumentVersion(0);
    note.castAndSet(SEQ_MARKER, DUMMY_MARKER);
    note.castAndSet(SEQ_EXTRACT_ID, pingValue);
    DocumentNotes notes = apd.getDocumentNotes(true);
    note.copyTo(notes);
    notes.saveNotes(true);
    List<AnnotatedPluginDocument> apds = addAndReturnGeneratedDocuments(
        Arrays.asList(apd),
        false,
        Collections.emptyList(),
        userFolder);
    if (apds.size() != 1) {
      throw new NaturalisPluginException("Error saving ping document");
    }
  }

}
