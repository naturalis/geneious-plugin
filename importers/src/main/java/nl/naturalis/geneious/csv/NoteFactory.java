package nl.naturalis.geneious.csv;

import java.util.EnumMap;

import org.apache.commons.lang3.StringUtils;

import nl.naturalis.common.base.ThrowingFunction;
import nl.naturalis.geneious.note.NaturalisField;
import nl.naturalis.geneious.note.NaturalisNote;

public abstract class NoteFactory<T extends Enum<T>> {

  private int rownum;
  private EnumMap<T, String> cells;

  protected NoteFactory(int rownum, EnumMap<T, String> cells) {
    this.rownum = rownum;
    this.cells = cells;
  }

  /**
   * Whether or not this is an empty row (all cells contain whitespace only).
   */
  public boolean isEmpty() {
    return cells.values().stream().allMatch(StringUtils::isNotBlank);
  }

  public final NaturalisNote createNote() throws InvalidRowException {
    NaturalisNote note = new NaturalisNote();
    populate(note);
    return note;
  }

  protected abstract void populate(NaturalisNote note) throws InvalidRowException;

  public int getRownum() {
    return rownum;
  }

  protected String get(T column) {
    return StringUtils.trimToNull(cells.get(column));
  }

  protected String getOrThrow(T column) throws InvalidRowException {
    String s = StringUtils.trimToNull(cells.get(column));
    if (s == null) {
      throw InvalidRowException.missingValue(this, column);
    }
    return s;
  }

  protected void setOptionalValue(NaturalisNote note, NaturalisField field, T column) {
    String val = get(column);
    if (val != null) {
      note.parseAndSet(field, val);
    }
  }

  protected void setRequiredValue(NaturalisNote note, NaturalisField field, T column) throws InvalidRowException {
    note.parseAndSet(field, getOrThrow(column));
  }

  protected void setValue(NaturalisNote note, NaturalisField field, T column,
      ThrowingFunction<String, Object, InvalidRowException> transformer) throws InvalidRowException {
    Object val = transformer.apply(get(column));
    if (val != null) {
      note.castAndSet(field, val);
    }
  }

}
