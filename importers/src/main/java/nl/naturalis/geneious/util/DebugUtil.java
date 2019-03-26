package nl.naturalis.geneious.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import nl.naturalis.geneious.WrappedException;
import nl.naturalis.geneious.note.NaturalisNote;

public class DebugUtil {

  private static final ObjectMapper mapper = new ObjectMapper();
  private static final ObjectWriter noteWriter = mapper.writerFor(NaturalisNote.class);

  public static String toJson(NaturalisNote note) {
    return toJson(note, true);
  }

  public static String toJson(NaturalisNote note, boolean pretty) {
    try {
      if (pretty) {
        return noteWriter.withDefaultPrettyPrinter().writeValueAsString(note);
      }
      return noteWriter.writeValueAsString(note);
    } catch (JsonProcessingException e) {
      throw new WrappedException(e);
    }
  }

  public static String toJson(Object obj) {
    try {
      return mapper.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      throw new WrappedException(e);
    }
  }

  public static String toJson(Object obj, boolean pretty) {
    try {
      if (pretty) {
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
      }
      return mapper.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      throw new WrappedException(e);
    }
  }

  private DebugUtil() {}

}
