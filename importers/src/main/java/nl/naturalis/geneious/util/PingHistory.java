package nl.naturalis.geneious.util;

import static nl.naturalis.geneious.Settings.settings;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import com.biomatters.geneious.publicapi.databaseservice.DatabaseServiceException;
import com.biomatters.geneious.publicapi.databaseservice.WritableDatabaseService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import nl.naturalis.common.ExceptionMethods;
import nl.naturalis.geneious.NaturalisPluginException;

/**
 * Generates and saves unique ping values that are inserted as extract IDs into special, temporary documents. The {@code PingHistory}
 * survives Geneious sessions, so that pinging will resume until and unless a document is found with the extract ID provided by the
 * {@code PingHistory}.
 * 
 * @author Ayco Holleman
 *
 */
class PingHistory {

  private static final ObjectMapper mapper = new ObjectMapper();
  private static final ObjectReader reader = mapper.readerFor(new TypeReference<Map<String, String>>() {});
  private static final ObjectWriter writer = mapper.writerFor(new TypeReference<Map<String, String>>() {});

  private static final String user = System.getProperty("user.name");

  private final WritableDatabaseService database;
  private final Map<String, String> cache;
  private final String key;

  /**
   * Loads the ping history for the provided database.
   * 
   * @param database
   */
  PingHistory(WritableDatabaseService database) {
    this.database = database;
    this.cache = loadHistory();
    this.key = user + '@' + database.getUniqueID();
  }

  /**
   * Whether or not operations can safely start doing what they are meant to do, knowing that any documents created or updated by a previous
   * operation have now been indexed.
   * 
   * @return
   */
  boolean isClear() {
    return !cache.containsKey(key);
  }

  /**
   * Clears the ping history. A panic method in case a user accidentally deleted his own or someone else's ping folder, in which case
   * operations will never get past the pinging phase.
   */
  void clear() {
    cache.remove(key);
    try {
      String json = writer.writeValueAsString(cache);
      settings().setPingHistory(json);
    } catch (JsonProcessingException e) {
      throw new NaturalisPluginException(e);
    }
    try {
      database.createChildFolder(PingSequence.PING_FOLDER).removeChildFolder(user);
    } catch (DatabaseServiceException e) {
      // no ping folder for current user in currently selected database
    }
  }

  /**
   * Returns the current ping value.
   * 
   * @return
   */
  String getPingValue() {
    return cache.get(key);
  }

  /**
   * Determines whether the current ping value was generated more than the specified number of minutes ago.
   * 
   * @param minutes
   * @return
   */
  boolean isOlderThan(int minutes) {
    String pingValue = cache.get(key);
    long timestamp = Long.parseLong(pingValue.substring(pingValue.lastIndexOf('/') + 1));
    return (System.currentTimeMillis() - timestamp) > (minutes * 60 * 1000);
  }

  /**
   * Generates and returns a new ping value. The new ping value is stored in the ping history before being returned. The value has the
   * following format: &lt;user&gt;//&lt;timestamp&gt; and is stored in the ping history under key &lt;user&gt;@&lt;database&gt;.
   * 
   * @return
   */
  String generateNewPingValue() {
    if (isClear()) {
      String value = user + "//" + System.currentTimeMillis();
      cache.put(key, value);
      try {
        String json = writer.writeValueAsString(cache);
        settings().setPingHistory(json);
      } catch (JsonProcessingException e) {
        throw new NaturalisPluginException(e);
      }
      return value;
    }
    throw Ping.pingCorrupted();
  }

  private static Map<String, String> loadHistory() {
    String s = settings().getPingHistory();
    if (StringUtils.isEmpty(s)) {
      return new HashMap<>();
    }
    try {
      return reader.readValue(s);
    } catch (IOException e) {
      throw ExceptionMethods.uncheck(e);
    }
  }

}
