package nl.naturalis.geneious.note;

import java.util.EnumMap;
import java.util.Map;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument.DocumentNotes;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import org.apache.commons.lang3.StringUtils;

import static nl.naturalis.geneious.note.NaturalisField.DOCUMENT_VERSION;
import static nl.naturalis.geneious.note.NaturalisField.SEQ_EXTRACT_ID;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_EXTRACT_ID;

/**
 * A containing for all annotations that can be added using the Naturalis plugin.
 *
 * @author Ayco Holleman
 */
public final class NaturalisNote implements Note {

  private static final String ERR_EMPTY = "Value must not be null or whitespace only (field=%s)";

  private final EnumMap<NaturalisField, Object> data;

  /**
   * Creates a new empty note.
   */
  public NaturalisNote() {
    data = new EnumMap<>(NaturalisField.class);
  }

  /**
   * Creates a new note and initializes it with the values found in the specified document.
   * 
   * @param document
   */
  public NaturalisNote(AnnotatedPluginDocument document) {
    data = new EnumMap<>(NaturalisField.class);
    readFrom(document);
  }

  /**
   * Sets the specified field to the specified value, parsing it into an object of the field's datatype. This method will
   * throw an {@code IllegalArgumentException} if the string cannot be parsed into such an object, or if the string is
   * null or empty.
   * 
   * @param field
   * @param value
   */
  public void parseAndSet(NaturalisField field, String value) {
    Preconditions.checkArgument(StringUtils.isNotBlank(value), ERR_EMPTY, field);
    data.put(field, field.parse(value));
  }

  /**
   * Sets the specified field to the specified value, casting it to an object of the field's datatype. This method will
   * throw a {@code ClassCastException} if the value cannot be cast this way, a {@code NullPointerException} if the value
   * is null, and an {@code IllegalArgumentException} if the value is an empty string.
   * 
   * @param field
   * @param value
   */
  public void castAndSet(NaturalisField field, Object value) {
    Preconditions.checkNotNull(value, ERR_EMPTY, field);
    data.put(field, field.cast(value)); // Force ClassCastException as soon as possible
  }

  /**
   * Sets the specified field to the specified value. This method will throw a {@code ClassCastException} if the field's
   * datatype in not {@link String} and an {@code IllegalArgumentException} if the value is an empty string.
   * 
   * @param field
   * @param value
   */
  public void castAndSet(NaturalisField field, String value) {
    Preconditions.checkArgument(StringUtils.isNotBlank(value), ERR_EMPTY, field);
    data.put(field, field.cast(value)); // Force ClassCastException as soon as possible
  }

  /**
   * Returns the value of the specified field within this note.
   * 
   * @param field
   * @return
   */
  @SuppressWarnings("unchecked")
  public <T> T get(NaturalisField field) {
    return (T) data.get(field);
  }

  /**
   * Removes the specified field (and its value) from the note. Returns true if this note did have a value for the
   * specified field, false otherwise.
   * 
   * @param field
   * @return
   */
  public boolean remove(NaturalisField field) {
    return data.remove(field) != null;
  }

  /**
   * Convenience method for retrieving the ubiquitous extract ID.
   * 
   * @return
   */
  public String getExtractId() {
    String s = get(SEQ_EXTRACT_ID);
    return s == null ? get(SMPL_EXTRACT_ID) : s;
  }

  /**
   * Convenience method for retrieving the ubiquitous document version. N.B. In version 1 the document version was stored
   * as a string, and it is not trivial to repair this.
   * 
   * @return
   */
  public String getDocumentVersion() {
    return get(DOCUMENT_VERSION);
  }

  /**
   * Convenience method for setting the document version,
   * 
   * @param version
   */
  public void setDocumentVersion(int version) {
    if (version < 0) {
      throw new IllegalArgumentException("Invalid document version: " + version);
    }
    data.put(DOCUMENT_VERSION, String.valueOf(version));
  }

  /**
   * Initializes this note with values from the specified document.
   */
  public void readFrom(AnnotatedPluginDocument document) {
    DocumentNotes notes = document.getDocumentNotes(false);
    for (NaturalisField field : NaturalisField.values()) {
      Object val = field.readFrom(notes);
      if (val != null) {
        data.put(field, val);
      }
    }
  }

  /**
   * Copies all values of this note <i>except the document version</i> to the other note, overwriting any previous values
   * the other note may have had. Returns true if there was a change in the target note, false otherwise.
   * 
   * @param other
   * @return
   */
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

  /**
   * Copies all values of this note <i>except the document version</i> to the other note. Returns true if there was a
   * change in the target note, false otherwise.
   * 
   * @param other
   * @param overwrite Whether or not to overwrite the values in the other note.
   * @return
   */
  public boolean copyTo(NaturalisNote other, boolean overwrite) {
    if (overwrite) {
      return copyTo(other);
    }
    boolean changed = false;
    for (Map.Entry<NaturalisField, Object> e : data.entrySet()) {
      Object val = other.data.get(e.getKey());
      if (val == null) {
        other.data.put(e.getKey(), e.getValue());
        changed = true;
      }
    }
    return changed;
  }

  /**
   * Inserts the {@code NaturalisNote} into the provided {@link DocumentNotes}, but does not save the notes to the
   * database.
   * 
   * @param document
   */
  public void copyTo(DocumentNotes notes) {
    for (NaturalisField field : data.keySet()) {
      field.castAndWrite(notes, data.get(field));
    }
  }

  /**
   * Returns an immutable copy of the internal state of this note.
   * 
   * @return
   */
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
