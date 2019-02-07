package nl.naturalis.geneious;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.biomatters.geneious.publicapi.plugin.DocumentAction;
import com.biomatters.geneious.publicapi.plugin.DocumentOperation;
import com.biomatters.geneious.publicapi.plugin.GeneiousPlugin;
import com.biomatters.geneious.publicapi.plugin.Icons;
import com.biomatters.geneious.publicapi.plugin.PluginPreferences;
import com.biomatters.geneious.publicapi.utilities.IconUtilities;

import nl.naturalis.geneious.gui.ShowDialog;
import nl.naturalis.geneious.samplesheet.SampleSheetDocumentAction;
import nl.naturalis.geneious.tracefile.TraceFileDocumentOperation;

public class NaturalisGeneiousPlugin extends GeneiousPlugin {

  @Override
  public Icons getIcons() {
    return IconUtilities.getIconsFromJar(getClass(), "/naturalis.jpg");
  }

  @Override
  @SuppressWarnings("rawtypes")
  public List<PluginPreferences> getPluginPreferences() {
    return Arrays.asList(new NaturalisPluginPreferences());
  }

  @Override
  public void initialize(File pluginUserDirectory, File pluginDirectory) {
    super.initialize(pluginUserDirectory, pluginDirectory);
    if (!System.getProperty("file.encoding").equals("UTF-8")) {
      ShowDialog.invalidEncoding(System.getProperty("file.encoding"));
    }
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
    /*
     * Geneious does not allow version name like V2.0.0-ALPHA. Only 2.0.0 is allowed for this particular example.
     */
    String version = PluginInfo.getInstance().getVersion();
    int i = version.indexOf('-');
    if (i == -1) {
      return version.substring(1);
    }
    return version.substring(1, i);
  }

}
