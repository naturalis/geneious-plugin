package nl.naturalis.geneious.csv;

import nl.naturalis.common.base.ThrowingFunction;
import nl.naturalis.common.base.ThrowingSupplier;
import nl.naturalis.geneious.note.NaturalisField;
import nl.naturalis.geneious.note.NaturalisNote;

/**
 * A note factory converts a row in a CSV file into a set of annotations held together in a {@link NaturalisNote} object.
 * 
 * @author Ayco Holleman
 *
 * @param <T>
 */
public abstract class NoteFactory<T extends Enum<T>> {

  private final Row<T> row;
  private final int line;

  /**
   * Creates a {@link NoteFactory} for the provided row ({@code cells}). The provided row number is used for reporting only and should be
   * the absolute (including header rows) and user-friendly (one-based) line number of the row.
   * 
   * @param row
   * @param lineNumber
   */
  protected NoteFactory(Row<T> row, int lineNumber) {
    this.row = row;
    this.line = lineNumber;
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
   * Left to subclasses to implement: the actual population of the {@code NaturalisNote}. Subclasses are assisted by (and can probably
   * completely rely on) utility methods present in this class (e.g. {@link #setRequiredValue(NaturalisNote, NaturalisField, Enum)
   * setRequiredValue}).
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
    return row.get(column);
  }

  /**
   * Returns the value of the provided column or throws an {@code InvalidRowException} if the row does not have a value for that column.
   * 
   * @param column
   * @return
   * @throws InvalidRowException
   */
  protected String getRequired(T column) throws InvalidRowException {
    String s = row.get(column);
    if (s == null) {
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
    if (val != null) {
      note.parseAndSet(field, val);
    }
  }

  /**
   * Sets the specified field within the {@code NaturalisNote} to the value of the provided column or throws an {@code InvalidRowException}
   * if the row does not have a value for that column.
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
   * Sets the provided field to the value produced by the provided {@code transformer} <i>if</i> the produced value is not null. The value
   * of the provided column is supposedly the thing that gets transformed and then assign to the field.
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
    if (val != null) {
      note.castAndSet(field, val);
    }
  }

  /**
   * Sets the provided field to the value produced by the provided {@code supplier} <i>if</i> the produced value is not null.
   * 
   * @param note
   * @param field
   * @param supplier
   * @throws InvalidRowException
   */
  protected void setValue(NaturalisNote note, NaturalisField field, ThrowingSupplier<Object, InvalidRowException> supplier)
      throws InvalidRowException {
    Object val = supplier.get();
    if (val != null) {
      note.castAndSet(field, val);
    }
  }

  /**
   * Sets the provided field to the value produced by the provided {@code transformer} or throws an {@code InvalidRowException} if the row
   * does not have a value for the provided column.
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
