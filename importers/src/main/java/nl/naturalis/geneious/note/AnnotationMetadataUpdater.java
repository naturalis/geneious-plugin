package nl.naturalis.geneious.note;

import nl.naturalis.geneious.gui.ShowDialog;
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.log.LogSession;
import nl.naturalis.geneious.util.QueryUtils;

public class AnnotationMetadataUpdater {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(AnnotationMetadataUpdater.class);

  public static void saveFieldDefinitions() {
    if (!ShowDialog.confirmRegenerateAnnotationMetadata()) {
      return;
    }
    try (LogSession session = GuiLogManager.startSession("Update annotation metadata")) {
      for (NaturalisField field : NaturalisField.values()) {
        field.saveOrUpdateNoteType();
      }
      guiLogger.info("Updated annotation metadata for database \"%s\"", QueryUtils.getTargetDatabaseName());
    }
  }

}
