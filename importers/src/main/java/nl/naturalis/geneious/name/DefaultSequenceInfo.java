package nl.naturalis.geneious.name;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.DocumentType;
import nl.naturalis.geneious.note.ImportedFromNote;
import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.split.SplitNameDocumentOperation;
import nl.naturalis.geneious.util.DocumentUtils;

/**
 * A subclass of {code SequenceInfo} exclusively used by the {@link SplitNameDocumentOperation Split Name} operation. It
 * works on pre-existing documents, using the document name to feed into the {@link SequenceNameParser} rather than a
 * file name or fasta header (as with the AB1/Fasta Import).
 */
public class DefaultSequenceInfo extends SequenceInfo {

  private final String name;
  private final DocumentType type;
  private final NaturalisNote note;

  public DefaultSequenceInfo(AnnotatedPluginDocument document) {
    super(new ImportedFromNote(document).getFile());
    name = NameUtil.removeKnownSuffixes(document.getName());
    type = DocumentUtils.getDocumentType(document);
    note = new NaturalisNote(document); // Should be empty if the user opted to ignoreDocsWithNaturalisNote
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
