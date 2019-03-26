package nl.naturalis.geneious.note;

import java.util.EnumMap;
import java.util.Map;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument.DocumentNotes;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import org.apache.commons.lang3.StringUtils;

import nl.naturalis.geneious.util.StoredDocument;

import static nl.naturalis.geneious.note.NaturalisField.DOCUMENT_VERSION;
import static nl.naturalis.geneious.note.NaturalisField.SEQ_EXTRACT_ID;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_EXTRACT_ID;

public final class NaturalisNote {

  private final EnumMap<NaturalisField, Object> data;

  private static final String ERR_EMPTY = "Value must not be null or whitespace only (field=%s)";

  public NaturalisNote() {
    data = new EnumMap<>(NaturalisField.class);
  }

  public NaturalisNote(AnnotatedPluginDocument document) {
    data = new EnumMap<>(NaturalisField.class);
    readFrom(document);
  }

  public void parseAndSet(NaturalisField field, String value) {
    Preconditions.checkArgument(StringUtils.isNotBlank(value), ERR_EMPTY, field);
    data.put(field, field.parse(value));
  }

  public void castAndSet(NaturalisField field, Object value) {
    Preconditions.checkNotNull(value, ERR_EMPTY, field);
    if (value instanceof CharSequence && StringUtils.isBlank((CharSequence) value)) {
      throw new IllegalArgumentException(String.format(ERR_EMPTY, field));
    }
    data.put(field, field.cast(value)); // Force ClassCastException as soon as possible
  }

  @SuppressWarnings("unchecked")
  public <T> T get(NaturalisField field) {
    return (T) data.get(field);
  }

  public String getExtractId() {
    String s = get(SEQ_EXTRACT_ID);
    return s == null ? get(SMPL_EXTRACT_ID) : s;
  }

  public Integer getDocumentVersion() {
    String val = get(DOCUMENT_VERSION);
    return val == null ? null : Integer.valueOf(val);
  }

  public void setDocumentVersion(int version) {
    data.put(DOCUMENT_VERSION, String.valueOf(version));
  }

  public void incrementDocumentVersion(int ifNull) {
    Integer v = getDocumentVersion();
    String s = String.valueOf(v == null ? ifNull : v.intValue() + 1);
    data.put(DOCUMENT_VERSION, s);
  }

  /**
   * Copies this note's values to the other note (overwriting any previous values).
   * 
   * @param other
   */
  public void copyFrom(NaturalisNote other) {
    data.putAll(other.data);
  }

  public boolean copyTo(NaturalisNote other) {
    boolean changed = false;
    for (Map.Entry<NaturalisField, Object> e : data.entrySet()) {
      Object val = other.data.get(e.getKey());
      if (val == null || !val.equals(e.getValue())) {
        other.data.put(e.getKey(), e.getValue());
        changed = true;
      }
    }
    return changed;
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
    document.save();
  }

  public boolean saveTo(StoredDocument document) {
    if (copyTo(document.getNaturalisNote())) {
      saveTo(document.getGeneiousDocument());
      return true;
    }
    return false;
  }

  @JsonValue
  public Map<NaturalisField, Object> data() {
    return ImmutableMap.copyOf(data);
  }

  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    return data.equals(((NaturalisNote) obj).data);
  }

  public int hashCode() {
    return data.hashCode();
  }

}
