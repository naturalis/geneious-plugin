package nl.naturalis.geneious.note;

import static nl.naturalis.geneious.note.NaturalisField.DOCUMENT_VERSION;
import static nl.naturalis.geneious.note.NaturalisField.SEQ_EXTRACT_ID;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_EXTRACT_ID;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument.DocumentNotes;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

/**
 * A container for all annotations that can be added using the Naturalis plugin. A {@code NaturalisNote} will never
 * contain null or whitespace-only values. This even applies to non-string fields: calling {@code toString()} on them
 * must never return null or a whitespace-only string. Trying to set any field to null or to a whitespace-only
 * string results in an {@link IllegalArgumentException}. Consequently, if a {@code NaturalisNote} extracted from a
 * Geneious document returns null for a particular field, it means the field was not present in the Geneious document.
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
   * Copy constructor
   */
  public NaturalisNote(NaturalisNote other) {
    data = new EnumMap<>(other.data);
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
    Preconditions.checkArgument(StringUtils.isNotBlank(value.toString()), ERR_EMPTY, field);
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
   * Convenience method for retrieving the ubiquitous extract ID.
   * 
   * @return
   */
  public String getExtractId() {
    String s = get(SEQ_EXTRACT_ID);
    return s == null ? get(SMPL_EXTRACT_ID) : s;
  }

  /**
   * Convenience method for retrieving the ubiquitous document version. N.B. we are dealing with a legacy in which the
   * document version was stored as a string, and it is not trivial to repair this.
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
    Preconditions.checkArgument(version >= 0, "Invalid document version: " + version);
    data.put(DOCUMENT_VERSION, String.valueOf(version));
  }

  /**
   * Initializes this note with values from the provided document.
   */
  public void readFrom(AnnotatedPluginDocument document) {
    DocumentNotes notes = document.getDocumentNotes(false);
    for(NaturalisField field : NaturalisField.values()) {
      Object val = field.readFrom(notes);
      if(val == null || StringUtils.isBlank(val.toString())) {
        // Deal with potential legacy where empty values slipped through.
        continue;
      }
      data.put(field, val);
    }
  }

  /**
   * Overwrites the other note with the values in this note. Returns true if the other note's content changed as a result,
   * false otherwise.
   * 
   * @param other
   * @return
   */
  public boolean copyTo(NaturalisNote other) {
    boolean changed = false;
    for(Map.Entry<NaturalisField, Object> e : data.entrySet()) {
      Object val = other.data.get(e.getKey());
      if(val == null || !val.equals(e.getValue())) {
        other.data.put(e.getKey(), e.getValue());
        changed = true;
      }
    }
    return changed;
  }

  /**
   * Copies all values of this note to the other note without overwriting values in the other note. Returns true if the
   * other note's content changed as a result, false otherwise. You can provide an array of fields that you explicitly do
   * not want to get copied over to the other note.
   * 
   * @param other
   * @param dontCopy
   * @return
   */
  public boolean mergeInto(NaturalisNote other, NaturalisField... dontCopy) {
    boolean changed = false;
    EnumSet<NaturalisField> s = EnumSet.copyOf(Arrays.asList(dontCopy));
    for(Map.Entry<NaturalisField, Object> e : data.entrySet()) {
      if(s.contains(e.getKey())) {
        continue;
      }
      Object val = other.data.get(e.getKey());
      if(val == null) {
        other.data.put(e.getKey(), e.getValue());
        changed = true;
      }
    }
    return changed;
  }

  /**
   * Whether or not this note is empty.
   * 
   * @return
   */
  public boolean isEmpty() {
    return data.isEmpty();
  }

  /**
   * Inserts the {@code NaturalisNote} into the provided {@link DocumentNotes} overwriting any previous values, but does
   * not save the notes to the database.
   * 
   * @param document
   */
  public void copyTo(DocumentNotes notes) {
    for(Map.Entry<NaturalisField, Object> e : data.entrySet()) {
      e.getKey().castAndWrite(notes, e.getValue());
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

  @Override
  public boolean equals(Object obj) {
    if(this == obj) {
      return true;
    }
    if(obj == null || getClass() != obj.getClass()) {
      return false;
    }
    return data.equals(((NaturalisNote) obj).data);
  }

  @Override
  public int hashCode() {
    return data.hashCode();
  }

}
