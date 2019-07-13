package nl.naturalis.geneious.csv;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import nl.naturalis.geneious.StoredDocument;
import nl.naturalis.geneious.note.NaturalisNote;

/**
 * Maintains tallies and other types of objects accumulating or evolving as a CSV import operation proceeds.
 * 
 * @author Ayco Holleman
 *
 */
public class RuntimeInfo {

  private final boolean[] badRows;
  private final boolean[] usedRows;
  private final HashSet<StoredDocument> updated;
  // Maps document table lookup keys to row numbers
  private final HashMap<Object, Integer> duplicates;

  public RuntimeInfo(int numRows) {
    this.badRows = new boolean[numRows];
    this.usedRows = new boolean[numRows];
    this.updated = new HashSet<>();
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
  public Set<StoredDocument> getUpdatedDocuments() {
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
   * Where or not the provided array index corresponds to a row in a BOLD spreadsheet that has already been marked as a
   * bad (unprocessable) row.
   * 
   * @param arrayIndex
   * @return
   */
  public boolean isBadRow(int arrayIndex) {
    return badRows[arrayIndex];
  }

  /**
   * Mark the row corresponding to the provided array index as a bad (unprocessable) row.
   * 
   * @param arrayIndex
   */
  public void markBad(int arrayIndex) {
    badRows[arrayIndex] = true;
  }

  /**
   * Where or not the provided array index corresponds to a row matching one or more selected documents.
   * 
   * @param arrayIndex
   * @return
   */
  public boolean isUsedRow(int arrayIndex) {
    return usedRows[arrayIndex];
  }

  /**
   * Mark the row corresponding to the provided array index as matching one or more selected documents.
   * 
   * @param arrayIndex
   */
  public void markUsed(int arrayIndex) {
    usedRows[arrayIndex] = true;
  }

  /**
   * Add the provided document to the set of documents that were updated during the operation (at least one annotation was
   * added/changed/removed).
   * 
   * @param doc
   */
  public void updated(StoredDocument doc) {
    updated.add(doc);
  }

  /**
   * Checks if the provided key is a duplicate and, if so, returns the line number of the row containing that key. If the
   * provided key is not a duplicate, null is returned and the key is cached and associated with the provided line number.
   * 
   * @param key
   * @return
   */
  public Integer checkKey(Object key, int line) {
    return duplicates.putIfAbsent(key, line);
  }

  /**
   * Add the provided key found in the row at the specified line.
   * 
   * @param key
   * @param lineNumber
   */
  public void addKey(Object key, int lineNumber) {
    duplicates.put(key, lineNumber);
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
