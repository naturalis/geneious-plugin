package nl.naturalis.geneious.csv;

import java.util.EnumMap;

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

  public final NaturalisNote createNote() throws InvalidRowException {
    NaturalisNote note = new NaturalisNote();
    populate(note);
    return note;
  }

  protected abstract void populate(NaturalisNote note) throws InvalidRowException;

  protected int getRownum() {
    return rownum;
  }

  protected String get(T column) {
    return cells.get(column);
  }

  protected String getRequired(T column) throws InvalidRowException {
    String s = cells.get(column);
    if (s == null) {
      throw InvalidRowException.missingValue(this, column);
    }
    return s;
  }

  protected void setValue(NaturalisNote note, NaturalisField field, T column) {
    String val = get(column);
    if (val != null) {
      note.parseAndSet(field, val);
    }
  }

  protected void setRequiredValue(NaturalisNote note, NaturalisField field, T column) throws InvalidRowException {
    note.parseAndSet(field, getRequired(column));
  }

  protected void setValue(NaturalisNote note, NaturalisField field, T column,
      ThrowingFunction<String, Object, InvalidRowException> transformer) throws InvalidRowException {
    Object val = transformer.apply(get(column));
    if (val != null) {
      note.castAndSet(field, val);
    }
  }

  protected void setRequiredValue(NaturalisNote note, NaturalisField field, T column,
      ThrowingFunction<String, Object, InvalidRowException> transformer) throws InvalidRowException {
    note.castAndSet(field, transformer.apply(getRequired(column)));
  }

}
