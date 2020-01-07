package nl.naturalis.geneious.note;

import nl.naturalis.geneious.gui.ShowDialog;
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.log.LogSession;
import nl.naturalis.geneious.util.PluginUtils;

/**
 * Utility class that synchronizes the plugin's definitions of the various annotations with Geneious's definitions of
 * them.
 * 
 * @author Ayco Holleman
 *
 */
public class AnnotationMetadataUpdater {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(AnnotationMetadataUpdater.class);

  /**
   * Saves the plugin's definitions of the various annotations to the currently selected database.
   */
  public static void saveFieldDefinitions() {
    if (!ShowDialog.confirmRegenerateAnnotationMetadata()) {
      return;
    }
    try (LogSession session = GuiLogManager.startSession(null, "Update annotation metadata")) {
      for (NaturalisField field : NaturalisField.values()) {
        field.saveOrUpdateNoteType();
      }
      guiLogger.info("Updated annotation metadata for database \"%s\"", PluginUtils.getSelectedDatabaseName());
    }
  }

}
