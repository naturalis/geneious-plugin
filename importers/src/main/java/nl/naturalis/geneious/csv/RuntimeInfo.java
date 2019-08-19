package nl.naturalis.geneious.csv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nl.naturalis.geneious.StoredDocument;
import nl.naturalis.geneious.note.NaturalisNote;

/**
 * Maintains counters and other types of objects accumulating or evolving as a CSV import operation proceeds.
 * 
 * @author Ayco Holleman
 *
 */
public class RuntimeInfo {

  private final boolean[] badRows;
  private final boolean[] usedRows;
  private final ArrayList<StoredDocument> updated;
  private final HashMap<Object, Integer> duplicates; // Maps keys to line numbers

  public RuntimeInfo(int numRows) {
    this.badRows = new boolean[numRows];
    this.usedRows = new boolean[numRows];
    this.updated = new ArrayList<>();
    this.duplicates = new HashMap<>();
  }

  /**
   * Returns the number of rows that were successfully concerted to a {@link NaturalisNote}.
   * 
   * @return
   */
  public int countGoodRows() {
    return count(badRows, false);
  }

  /**
   * Returns the number of invalid or empty rows.
   * 
   * @return
   */
  public int countBadRows() {
    return count(badRows, true);
  }

  /**
   * Returns the number of rows that matched one or more selected selected documents.
   * 
   * @return
   */
  public int countUsedRows() {
    return count(usedRows, true);
  }

  /**
   * Returns the number of rows matching none of the selected documents.
   * 
   * @return
   */
  public int countUnusedRows() {
    return count(usedRows, false);
  }

  /**
   * Returns the documents that were updated (at least one annotation added or changed).
   * 
   * @return
   */
  public List<StoredDocument> getUpdatedDocuments() {
    return updated;
  }

  /**
   * Returns the number of updated documents.
   * 
   * @return
   */
  public int countUpdatedDocuments() {
    return updated.size();
  }

  /**
   * Where or not the provided (absolute and zero-based) row number corresponds to a row in a BOLD spreadsheet that has
   * already been marked as a bad (unprocessable) row.
   * 
   * @param rownum
   * @return
   */
  public boolean isBadRow(int rownum) {
    return badRows[rownum];
  }

  /**
   * Mark the row corresponding to the provided row number as a bad (unprocessable) row.
   * 
   * @param rownum
   */
  public void markBad(int rownum) {
    badRows[rownum] = true;
  }

  /**
   * Where or not the provided (absolute and zero-based) c corresponds to a row matching one or more selected documents.
   * 
   * @param rownum
   * @return
   */
  public boolean isUsedRow(int rownum) {
    return usedRows[rownum];
  }

  /**
   * Mark the row corresponding to the provided array row number as matching one or more selected documents.
   * 
   * @param rownum
   */
  public void markUsed(int rownum) {
    usedRows[rownum] = true;
  }

  /**
   * Adds the provided document to the set of documents that were updated during the operation (i&#46;e&#46; at least one
   * annotation was added/changed/removed).
   * 
   * @param doc
   */
  public void updated(StoredDocument doc) {
    updated.add(doc);
  }

  /**
   * Checks if the provided key is a duplicate and, if so, returns the line number of the first row containing the same
   * key. If the provided key is not a duplicate, null is returned and the key is cached and associated with the provided
   * line number.
   * 
   * @param key
   * @return
   */
  public Integer checkAndAddKey(Object key, int line) {
    return duplicates.putIfAbsent(key, line);
  }

  private static int count(boolean[] array, boolean value) {
    int x = 0;
    for(boolean b : array) {
      if(b == value) {
        ++x;
      }
    }
    return x;
  }

}
