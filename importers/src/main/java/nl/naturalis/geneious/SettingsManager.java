package nl.naturalis.geneious;

import java.util.EnumMap;

import com.biomatters.geneious.publicapi.plugin.GeneiousPlugin;
import com.biomatters.geneious.publicapi.plugin.Options;
import com.biomatters.geneious.publicapi.plugin.Options.Option;

import nl.naturalis.geneious.seq.SequenceImportDocumentOperation;

import static com.biomatters.geneious.publicapi.plugin.PluginUtilities.getPluginForDocumentOperation;

@SuppressWarnings({"rawtypes"})
public class SettingsManager {

  private static SettingsManager instance;

  public static SettingsManager settingsManager() {
    if (instance == null) {
      instance = new SettingsManager();
    }
    return instance;
  }

  private final Options options;
  private final EnumMap<Setting, Option> opts = new EnumMap<>(Setting.class);

  private SettingsManager() {
    GeneiousPlugin me = getPluginForDocumentOperation(new SequenceImportDocumentOperation());
    options = me.getPluginPreferences().get(0).getActiveOptions();
    for (Setting setting : Setting.values()) {
      opts.put(setting, options.getOption(setting.getName()));
    }
  }

  public void setAndSave(Setting setting, Object value) {
    opts.get(setting).setValue(value);
    options.savePreferences();
  }

  public Object get(Setting setting) {
    return opts.get(setting).getValue();
  }

}
