package nl.naturalis.geneious.note;

import java.util.EnumMap;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument.DocumentNotes;

import nl.naturalis.geneious.util.StoredDocument;

public final class NaturalisNote {

  private final EnumMap<NaturalisField, Object> data;

  public NaturalisNote() {
    data = new EnumMap<>(NaturalisField.class);
  }

  public NaturalisNote(AnnotatedPluginDocument document) {
    data = new EnumMap<>(NaturalisField.class);
    readFrom(document);
  }

  public void parseAndSet(NaturalisField field, String value) {
    if (value != null) {
      data.put(field, field.parse(value));
    }
  }

  public void castAndSet(NaturalisField field, Object value) {
    if (value != null) {
      data.put(field, field.cast(value));
    }
  }

  @SuppressWarnings("unchecked")
  public <T> T get(NaturalisField field) {
    Object val = data.get(field);
    return (T) (val == null ? null : val);
  }

  public String getExtractId() {
    return get(NaturalisField.SMPL_EXTRACT_ID);
  }

  public Integer getDocumentVersion() {
    String val = get(NaturalisField.DOCUMENT_VERSION);
    return val == null ? null : Integer.valueOf(val);
  }

  public void setDocumentVersion(int version) {
    data.put(NaturalisField.DOCUMENT_VERSION, version);
  }

  public void setDocumentVersion(String version) {
    data.put(NaturalisField.DOCUMENT_VERSION, version);
  }

  public void incrementDocumentVersion(int whenNull) {
    Integer version = getDocumentVersion();
    if (version == null) {
      data.put(NaturalisField.DOCUMENT_VERSION, String.valueOf(whenNull));
    } else {
      int i = version.intValue() + 1;
      data.put(NaturalisField.DOCUMENT_VERSION, String.valueOf(i));
    }
  }

  public void copyFrom(NaturalisNote other) {
    data.putAll(other.data);
  }

  public void copyTo(NaturalisNote other) {
    other.data.putAll(data);
  }

  public void readFrom(AnnotatedPluginDocument document) {
    DocumentNotes notes = document.getDocumentNotes(false);
    for (NaturalisField field : NaturalisField.values()) {
      data.put(field, field.readFrom(notes));
    }
  }

  public void saveTo(AnnotatedPluginDocument document) {
    DocumentNotes notes = document.getDocumentNotes(true);
    for (NaturalisField field : data.keySet()) {
      field.castAndWrite(notes, data.get(field));
    }
    notes.saveNotes();
  }

  public void saveTo(StoredDocument document) {
    copyTo(document.getNaturalisNote());
    saveTo(document.getGeneiousDocument());
  }

}
