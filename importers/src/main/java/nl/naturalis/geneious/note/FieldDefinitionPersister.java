package nl.naturalis.geneious.note;

import nl.naturalis.geneious.gui.ShowDialog;
import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;

public class FieldDefinitionPersister {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(FieldDefinitionPersister.class);

  public static void saveFieldDefinitions() {
    if (!ShowDialog.confirmRegenerateAnnotationMetadata()) {
      return;
    }
    try {
      for (NaturalisField nf : NaturalisField.values()) {
        nf.saveOrUpdateDefinition();
      }
      guiLogger.info("Finished regenerating annotation metadata");
    } finally {
      GuiLogManager.showLogAndClose("Annotation metadata log");
    }
  }

}
