package nl.naturalis.geneious;

/**
 * Symbolic constants for preconditions that must be met before an operation is allowed to commence. Different
 * operations may require different combinations of preconditions.
 * 
 * @author Ayco Holleman
 *
 */
public enum Precondition {
  /**
   * Make sure the user is allowed to import documents into the selected folder, notably that no attempt is made to write
   * to the ping folder.
   */
  VALID_TARGET_FOLDER,
  /**
   * Make sure the user has selected at least one document.
   */
  AT_LEAST_ONE_DOCUMENT_SELECTED,
  /**
   * Make sure all selected documents are in the same database.
   */
  ALL_DOCUMENTS_IN_SAME_DATABASE

}
