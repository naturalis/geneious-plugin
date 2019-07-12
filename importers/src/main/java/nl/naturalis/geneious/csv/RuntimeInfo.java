package nl.naturalis.geneious.csv;

import java.util.HashSet;
import java.util.Set;

import nl.naturalis.geneious.StoredDocument;

/**
 * Maintains tallies and other types of objects accumulating or evolving as a CSV import operation proceeds.
 * 
 * @author Ayco Holleman
 *
 */
public class RuntimeInfo {

  private final boolean[] badRows;
  private final boolean[] usedRows;
  private final Set<StoredDocument> updated;

  public RuntimeInfo(int numRows) {
    this.badRows = new boolean[numRows];
    this.usedRows = new boolean[numRows];
    this.updated = new HashSet<>();
  }

  public int countGoodRows() {
    int x = 0;
    for(boolean bad : badRows) {
      if(!bad) {
        ++x;
      }
    }
    return x;
  }

  public int countBadRows() {
    int x = 0;
    for(boolean bad : badRows) {
      if(bad) {
        ++x;
      }
    }
    return x;
  }

  public int countUsedRows() {
    int x = 0;
    for(boolean used : usedRows) {
      if(used) {
        ++x;
      }
    }
    return x;
  }

  public int countUnusedRows() {
    int x = 0;
    for(boolean used : usedRows) {
      if(!used) {
        ++x;
      }
    }
    return x;
  }

  public Set<StoredDocument> getUpdatedDocuments() {
    return updated;
  }

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

}
