package nl.naturalis.geneious.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import nl.naturalis.common.Check;
import nl.naturalis.common.ExceptionMethods;
import nl.naturalis.common.StringMethods;
import nl.naturalis.geneious.GlobalOptions;
import nl.naturalis.geneious.Settings;
import nl.naturalis.geneious.gui.ShowDialog;

/**
 * Maintains user-selected values and other data across operations and Geneious sessions. This class existed in the very early development
 * stages of the V2 plugin, but was dispensed with as it became clear that Geneious already had an API for this purpose. Meanwhile it has
 * become clear that this API is so awkward and recalcitrant that we have re-introduced this class. Therefore we currently have the two
 * mechanisms existing alongside each other. See {@link GlobalOptions} and {@link Settings}.
 * 
 * @author Ayco Holleman
 *
 */
public class History extends TreeMap<HistorySetting, String> {

  // TreeMap rather than EnumMap so settings remain nicely alphabetically ordered

  private static History instance;

  public static History history() {
    if (instance == null) {
      instance = new History();
    }
    return instance;
  }

  private final File settingsFile;
  private final ObjectMapper mapper;
  private final ObjectWriter writer;

  private History() {
    super((s1, s2) -> s1.toString().compareTo(s2.toString()));
    settingsFile = new File(System.getProperty("user.home") + "/.nbc-geneious-plugin.json");
    mapper = createObjectMapper();
    writer = mapper.writerFor(History.class).withDefaultPrettyPrinter();
    if (settingsFile.exists()) {
      try {
        // First read into HashMap and only then convert to RuntimeSettings to get rid of junk settings (from previous versions)
        TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {};
        HashMap<String, String> temp = mapper.readValue(settingsFile, typeRef);
        for (Map.Entry<String, String> entry : temp.entrySet()) {
          HistorySetting s = HistorySetting.parse(entry.getKey());
          if (s != null) { // no junk
            put(s, entry.getValue());
          }
        }
      } catch (Exception e) {
        ShowDialog.errorLoadingPluginSettings(settingsFile, e);
      }
    }
  }

  public String read(HistorySetting setting) {
    String val = super.get(setting);
    if (val == null || val.isBlank()) {
      return null;
    }
    return val;
  }

  public int readInt(HistorySetting setting) {
    String val = super.get(setting);
    if (val == null || val.isBlank()) {
      return 0;
    }
    return Integer.parseInt(val);
  }

  public String read(HistorySetting setting, String dfault) {
    Check.argument(StringMethods.isNotBlank(dfault), "dfault must not be blank");
    String val = super.get(setting);
    if (val == null || val.isBlank()) {
      return dfault;
    }
    return val;
  }

  public void save(HistorySetting setting, String value) {
    if (value == null || value.isBlank()) {
      if (containsKey(setting)) {
        remove(setting);
        save();
      }
    } else if (!containsKey(setting) || !read(setting).equals(value)) {
      put(setting, value);
      save();
    }
  }

  private void save() {
    try {
      writer.writeValue(settingsFile, this);
    } catch (IOException e) {
      throw ExceptionMethods.uncheck(e);
    }
  }

  private static ObjectMapper createObjectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
    mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
    return mapper;
  }

}
