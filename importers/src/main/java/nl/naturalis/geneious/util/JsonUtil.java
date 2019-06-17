package nl.naturalis.geneious.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import nl.naturalis.common.base.WrappedException;
import nl.naturalis.geneious.note.NaturalisNote;

import static nl.naturalis.geneious.Settings.settings;

/**
 * Provides method for serializing objects to JSON.
 * 
 * @author Ayco Holleman
 *
 */
public class JsonUtil {

  private static final ObjectMapper mapper = new ObjectMapper();
  private static final ObjectWriter noteWriter = mapper.writerFor(NaturalisNote.class);

  /**
   * Serializes a {@link NaturalisNote} object to JSON. If "Show pretty notes" was checked in <i>Tools -&gt;
   * Preferences</i>, a pretty printer will be used.
   * 
   * @param note
   * @return
   */
  public static String toJson(NaturalisNote note) {
    try {
      if (settings().isPrettyNotes()) {
        return noteWriter.withDefaultPrettyPrinter().writeValueAsString(note);
      }
      return noteWriter.writeValueAsString(note);
    } catch (JsonProcessingException e) {
      throw new WrappedException(e);
    }
  }

  /**
   * Serializes any object to JSON.
   * 
   * @param obj
   * @return
   */
  public static String toJson(Object obj) {
    try {
      return mapper.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      throw new WrappedException(e);
    }
  }

  /**
   * Serializes any object to JSON using a pretty printer.
   * 
   * @param obj
   * @return
   */
  public static String toPrettyJson(Object obj) {
    try {
      return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      throw new WrappedException(e);
    }
  }

  private JsonUtil() {}

}
