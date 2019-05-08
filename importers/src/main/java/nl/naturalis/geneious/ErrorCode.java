package nl.naturalis.geneious;

public enum ErrorCode {

  /**
   * No errors detected
   */
  OK,
  /**
   * file.encoding is not UTF-8
   */
  BAD_CHARSET(1),
  /**
   * one or more documents come from another database than the target database for the operation (e.g. because of a
   * query). This is currently not supported.
   */
  BAD_DOCUMENT_DATABASE(1),

  /**
   * The user did not select a sample sheet in the options dialog.
   */
  SMPL_MISSING_SAMPLE_SHEET,

  /**
   * The user did not selected any documents and also unchecked the "Create dummies" checkbox. He/she must either select
   * at least one document or check the "Create dummies" checkbox.
   */
  SMPL_NO_DOCUMENTS_SELECTED,

  /**
   * The user did not select a CRS file in the options dialog.
   */
  CSV_NO_FILE_PROVIDED,

  /**
   * The user-selected file did not have a csv, tsv, txt or xls extension
   */
  CSV_UNSUPPORTED_FILE_TYPE(1),
  
  /**
   * Generic success message for plugin actions.
   */
  OPERATION_SUCCESS,

  /**
   * The user specified 0 for lines to skip, but BOLD files <i>must</i> have a header
   */
  BOLD_NO_HEADER;

  private final int msgArgCount;

  private ErrorCode() {
    this.msgArgCount = 0;
  }

  private ErrorCode(int msgArgCount) {
    this.msgArgCount = msgArgCount;
  }

  public int getMessageArgCount() {
    return msgArgCount;
  }

}
