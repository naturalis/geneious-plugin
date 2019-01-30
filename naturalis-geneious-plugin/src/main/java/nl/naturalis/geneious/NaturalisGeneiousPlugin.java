package nl.naturalis.geneious;

import java.io.File;
import java.util.List;

import com.biomatters.geneious.publicapi.plugin.DocumentAction;
import com.biomatters.geneious.publicapi.plugin.DocumentOperation;
import com.biomatters.geneious.publicapi.plugin.GeneiousPlugin;
import com.biomatters.geneious.publicapi.plugin.Icons;
import com.biomatters.geneious.publicapi.plugin.PluginPreferences;
import com.biomatters.geneious.publicapi.utilities.IconUtilities;

import nl.naturalis.geneious.samplesheet.SampleSheetDocumentAction;
import nl.naturalis.geneious.tracefile.TraceFileDocumentOperation;

public class NaturalisGeneiousPlugin extends GeneiousPlugin {

  @Override
  public Icons getIcons() {
    return IconUtilities.getIconsFromJar(getClass(), "/naturalis.ico");
  }

  @Override
  @SuppressWarnings("rawtypes")
  public List<PluginPreferences> getPluginPreferences() {
    return super.getPluginPreferences();
  }

  @Override
  public void initialize(File pluginUserDirectory, File pluginDirectory) {
    super.initialize(pluginUserDirectory, pluginDirectory);
  }

  @Override
  public DocumentAction[] getDocumentActions() {
    return new DocumentAction[] {
        new SampleSheetDocumentAction()
    };
  }

  @Override
  public DocumentOperation[] getDocumentOperations() {
    return new DocumentOperation[] {
        new TraceFileDocumentOperation()
    };
  }

  @Override
  public String getAuthors() {
    return "Rudy Broekhuizen, Wilfred Gerritsen, Ayco Holleman, Judith Slaa, Chantal SlegtenHorst, Nick Stolk, Oscar Vorst";
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
