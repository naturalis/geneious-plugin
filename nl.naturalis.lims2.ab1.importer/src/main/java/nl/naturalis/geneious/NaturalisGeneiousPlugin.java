package nl.naturalis.geneious;

import com.biomatters.geneious.publicapi.plugin.DocumentAction;
import com.biomatters.geneious.publicapi.plugin.DocumentOperation;
import com.biomatters.geneious.publicapi.plugin.GeneiousPlugin;
import nl.naturalis.geneious.bold.BOLDImportDocumentAction;
import nl.naturalis.geneious.samplesheet.SampleSheetDocumentAction;
import nl.naturalis.geneious.split.CreateNoteFromFileNameDocumentAction;
import nl.naturalis.geneious.tracefile.TraceFileDocumentOperation;

public class NaturalisGeneiousPlugin extends GeneiousPlugin {

  @Override
  public DocumentAction[] getDocumentActions() {
    return new DocumentAction[] {new SampleSheetDocumentAction(), new BOLDImportDocumentAction(),
        new CreateNoteFromFileNameDocumentAction()};
  }

  @Override
  public DocumentOperation[] getDocumentOperations() {
    return new DocumentOperation[] {new TraceFileDocumentOperation()};
  }

  @Override
  public String getAuthors() {
    return "Wilfred Gerritsen, Denise de Haan, Ayco Holleman, Judith Slaa, Oscar Vorst";
  }

  @Override
  public String getDescription() {
    return "Naturalis utilities for Geneious";
  }

  @Override
  public String getHelp() {
    return "Under construction";
  }

  @Override
  public int getMaximumApiVersion() {
    return 0;
  }

  @Override
  public String getMinimumApiVersion() {
    return "4.1";
  }

  @Override
  public String getName() {
    return "Naturalis Geneious Plugin";
  }

  @Override
  public String getVersion() {
    return "2.0.0";
  }

}
