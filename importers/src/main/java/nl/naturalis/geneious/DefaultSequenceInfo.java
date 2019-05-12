package nl.naturalis.geneious;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.name.NameUtil;
import nl.naturalis.geneious.name.NotParsableException;
import nl.naturalis.geneious.name.SequenceNameParser;
import nl.naturalis.geneious.note.ImportedFromNote;
import nl.naturalis.geneious.note.NaturalisNote;

/**
 * A {@code SequenceInfo} initialized solely from the information already present within a {@code AnnotatedPluginDocument}. In fact, the
 * current implementation only uses the document name and ImportedFrom note to construct everything it needs, which is somewhat risky
 * because users can change the document name. In practice the {@code AnnotatedPluginDocument} always is one of the user-selected documents
 * passed on to various operation (although it could conceivably have been retrieved via a database query).
 */
public class DefaultSequenceInfo extends SequenceInfo {

  private final String name;
  private final DocumentType type;
  private final NaturalisNote note;

  public DefaultSequenceInfo(AnnotatedPluginDocument document) {
    super(new ImportedFromNote(document).getFile());
    name = NameUtil.removeKnownSuffixes(document.getName());
    type = NameUtil.getDocumentType(document);
    note = new NaturalisNote(document); // Should be empty, certainly if the user opted to ignoreDocsWithNaturalisNote.
  }

  @Override
  public DocumentType getDocumentType() {
    return type;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void createNote() throws NotParsableException {
    NaturalisNote note = new SequenceNameParser(name).parseName();
    note.copyTo(this.note);
  }

  @Override
  public NaturalisNote getNaturalisNote() {
    return note;
  }

}
