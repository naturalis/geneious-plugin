package nl.naturalis.geneious.note;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Set;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument.DocumentNotes;

public class NaturalisNote extends EnumMap<NaturalisField, Object> {

  public NaturalisNote() {
    super(NaturalisField.class);
  }

  public NaturalisNote(AnnotatedPluginDocument document) {
    super(NaturalisField.class);
    read(document);
  }

  public Object put(NaturalisField field, Object value) {
    throw new UnsupportedOperationException();
  }

  public void parseAndSet(NaturalisField field, String value) {
    if (value != null) {
      super.put(field, field.parse(value));
    }
  }

  public void castAndSet(NaturalisField field, Object value) {
    if (value != null) {
      super.put(field, field.cast(value));
    }
  }

  @SuppressWarnings("unchecked")
  public <T> T get(NaturalisField field) {
    Object val = super.get(field);
    return (T) (val == null ? null : val);
  }

  public String getExtractId() {
    return get(NaturalisField.SMPL_EXTRACT_ID);
  }

  public Integer getDocumentVersion() {
    return get(NaturalisField.DOCUMENT_VERSION);
  }

  public void read(AnnotatedPluginDocument document) {
    DocumentNotes notes = document.getDocumentNotes(false);
    for (NaturalisField field : getEmptyFields()) {
      super.put(field, field.readFrom(notes));
    }
  }

  public void attachTo(AnnotatedPluginDocument document) {
    DocumentNotes notes = document.getDocumentNotes(true);
    for (NaturalisField field : keySet()) {
      field.write(notes, super.get(field));
    }
    notes.saveNotes(true, true);
  }

  private Set<NaturalisField> getEmptyFields() {
    EnumSet<NaturalisField> all = EnumSet.allOf(NaturalisField.class);
    all.removeAll(keySet());
    return all;
  }

}
