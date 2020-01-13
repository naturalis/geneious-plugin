package nl.naturalis.geneious.util;

import static nl.naturalis.common.StringMethods.ifBlank;
import java.io.File;
import java.io.IOException;
import com.fasterxml.jackson.annotation.JsonInclude;
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
public class RuntimeSettings {

  public static final RuntimeSettings INSTANCE = new RuntimeSettings();

  private final File settingsFile;
  private final ObjectMapper mapper;
  private final ObjectWriter writer;

  private String seqLastSelectedTargetFolderId;
  private String smplLastSelectedTargetFolderId;

  private RuntimeSettings() {
    settingsFile = new File(System.getProperty("user.home") + "/.nbc-geneious-plugin.json");
    mapper = createObjectMapper();
    writer = mapper.writerFor(RuntimeSettings.class).withDefaultPrettyPrinter();
    if (settingsFile.exists()) {
      try {
        mapper.readerForUpdating(this).readValue(settingsFile);
      } catch (IOException e) {
        throw ExceptionMethods.uncheck(e);
      }
    }
  }

  public String getSeqLastSelectedTargetFolderId() {
    return seqLastSelectedTargetFolderId;
  }

  public void setSeqLastSelectedTargetFolderId(String seqLastSelectedTargetFolderId) {
    this.seqLastSelectedTargetFolderId = ifBlank(seqLastSelectedTargetFolderId, null);
    save();
  }

  public String getSmplLastSelectedTargetFolderId() {
    return smplLastSelectedTargetFolderId;
  }

  public void setSmplLastSelectedTargetFolderId(String smplLastSelectedTargetFolderId) {
    this.smplLastSelectedTargetFolderId = ifBlank(smplLastSelectedTargetFolderId, null);
    save();
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
