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
import nl.naturalis.common.ExceptionMethods;
import nl.naturalis.geneious.GlobalOptions;
import nl.naturalis.geneious.Settings;

/**
 * Maintains user-selected values and other data across operations and Geneious sessions. This class existed in the very early development
 * stages of the V2 plugin, but was dispensed with as it became clear that Geneious already had an API for this purpose. Meanwhile it has
 * become clear that this API is so awkward and recalcitrant that we have re-introduced this class. Therefore we currently have the two
 * mechanisms existing alongside each other. See {@link GlobalOptions} and {@link Settings}.
 * 
 * @author Ayco Holleman
 *
 */
public class RuntimeSettings extends TreeMap<RuntimeSetting, String> {

  // TreeMap rather than EnumMap so settings remain nicely alphabetically ordered

  private static final RuntimeSettings instance = new RuntimeSettings();

  public static RuntimeSettings runtimeSettings() {
    return instance;
  }

  private final File settingsFile;
  private final ObjectMapper mapper;
  private final ObjectWriter writer;

  private RuntimeSettings() {
    super((s1, s2) -> s1.toString().compareTo(s2.toString()));
    settingsFile = new File(System.getProperty("user.home") + "/.nbc-geneious-plugin.json");
    mapper = createObjectMapper();
    writer = mapper.writerFor(RuntimeSettings.class).withDefaultPrettyPrinter();
    if (settingsFile.exists()) {
      try {
        // First read into HashMap and only then convert to RuntimeSettings to get rid of junk settings (from previous versions)
        TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {};
        HashMap<String, String> temp = mapper.readValue(settingsFile, typeRef);
        for (Map.Entry<String, String> entry : temp.entrySet()) {
          RuntimeSetting s = RuntimeSetting.parse(entry.getKey());
          if (s != null) { // no junk
            put(s, entry.getValue());
          }
        }
      } catch (IOException e) {
        throw ExceptionMethods.uncheck(e);
      }
    }
  }

  public void write(RuntimeSetting setting, String value) {
    if (value == null) {
      if (containsKey(setting)) {
        remove(setting);
        save();
      }
    } else if (!containsKey(setting) || !get(setting).equals(value)) {
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