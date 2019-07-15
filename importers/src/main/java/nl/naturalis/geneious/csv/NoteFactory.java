package nl.naturalis.geneious.csv;

import java.util.EnumMap;

import nl.naturalis.common.base.ThrowingFunction;
import nl.naturalis.geneious.note.NaturalisField;
import nl.naturalis.geneious.note.NaturalisNote;

/**
 * Abstract base class for {@link NaturalisNote} factories used by the importers of CSV-like files (BOLD Import, CRS
 * Import, Sample Sheet Import).
 * 
 * @author Ayco Holleman
 *
 * @param <T>
 */
public abstract class NoteFactory<T extends Enum<T>> {

  private int line;
  private EnumMap<T, String> cells;

  /**
   * Creates a {@link NoteFactory} for the provided row ({@code cells}). The provided row number is used for reporting
   * only and must be the absolute (including header rows) and user-friendly (one-based) line number of the row.
   * 
   * @param lineNumber
   * @param cells
   */
  protected NoteFactory(int lineNumber, EnumMap<T, String> cells) {
    this.line = lineNumber;
    this.cells = cells;
  }

  /**
   * Creates a {@link NaturalisNote} based on the data of a single row within the CSV-like file.
   * 
   * @return
   * @throws InvalidRowException
   */
  public final NaturalisNote createNote() throws InvalidRowException {
    NaturalisNote note = new NaturalisNote();
    populate(note);
    return note;
  }

  /**
   * Left to subclasses to implement: the actual population of the {@code NaturalisNote}. Subclasses are assisted by (and
   * can probably completely rely on) utility methods present in this class (e.g.
   * {@link #setRequiredValue(NaturalisNote, NaturalisField, Enum) setRequiredValue}).
   * 
   * @param note
   * @throws InvalidRowException
   */
  protected abstract void populate(NaturalisNote note) throws InvalidRowException;

  /**
   * Returns the user-friendly (one-based) number of the row.
   * 
   * @return
   */
  protected int getLineNumber() {
    return line;
  }

  /**
   * Returns the value of the provided column.
   * 
   * @param column
   * @return
   */
  protected String get(T column) {
    return cells.get(column);
  }

  /**
   * Returns the value of the provided column or throws an {@code InvalidRowException} if the row does not have a value
   * for that column.
   * 
   * @param column
   * @return
   * @throws InvalidRowException
   */
  protected String getRequired(T column) throws InvalidRowException {
    String s = cells.get(column);
    if(s == null) {
      throw InvalidRowException.missingValue(this, column);
    }
    return s;
  }

  /**
   * Sets the specified field within the {@code NaturalisNote} to the value of the provided column.
   * 
   * @param note
   * @param field
   * @param column
   */
  protected void setValue(NaturalisNote note, NaturalisField field, T column) {
    String val = get(column);
    if(val != null) {
      note.parseAndSet(field, val);
    }
  }

  /**
   * Sets the specified field within the {@code NaturalisNote} to the value of the provided column or throws an
   * {@code InvalidRowException} if the row does not have a value for that column.
   * 
   * @param note
   * @param field
   * @param column
   * @throws InvalidRowException
   */
  protected void setRequiredValue(NaturalisNote note, NaturalisField field, T column) throws InvalidRowException {
    note.parseAndSet(field, getRequired(column));
  }

  /**
   * Transforms the value of the provided column using the provided {@code transformer} and then sets the specified field
   * to the transformed value. If the transformed value is null the {@code NaturalisNote} is left alone (it is forbidden
   * to set any of its fields to null).
   * 
   * @param note
   * @param field
   * @param column
   * @param transformer
   * @throws InvalidRowException
   */
  protected void setValue(NaturalisNote note, NaturalisField field, T column,
      ThrowingFunction<String, Object, InvalidRowException> transformer) throws InvalidRowException {
    Object val = transformer.apply(get(column));
    if(val != null) {
      note.castAndSet(field, val);
    }
  }

  /**
   * Transforms the value of the provided column using the provided {@code transformer} and then sets the specified field
   * to the transformed value, <i>or</i> throws an {@code InvalidRowException} if the row does not have a value for the
   * provided column. Subclasses must not provide transformers that transfor non-null values into null values. Doing so
   * will cause an {@code IllegalArgumentException} to be thrown.
   * 
   * @param note
   * @param field
   * @param column
   * @param transformer
   * @throws InvalidRowException
   */
  protected void setRequiredValue(NaturalisNote note, NaturalisField field, T column,
      ThrowingFunction<String, Object, InvalidRowException> transformer) throws InvalidRowException {
    note.castAndSet(field, transformer.apply(getRequired(column)));
  }

}
