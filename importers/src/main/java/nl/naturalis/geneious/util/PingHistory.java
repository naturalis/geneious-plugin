package nl.naturalis.geneious.util;

import static nl.naturalis.geneious.Settings.settings;
import static nl.naturalis.geneious.util.QueryUtils.getTargetDatabaseName;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

import nl.naturalis.geneious.NaturalisPluginException;

public class PingHistory {

  private static final ObjectMapper mapper = new ObjectMapper();
  private static final ObjectReader reader = mapper.readerFor(new TypeReference<Map<String, String>>() {});
  private static final ObjectWriter writer = mapper.writerFor(new TypeReference<Map<String, String>>() {});

  private static final String user = System.getProperty("user.name");

  final Map<String, String> cache;
  private final String key;

  public PingHistory() {
    cache = loadHistory();
    key = user + '@' + getTargetDatabaseName();
  }

  public boolean isClear() {
    return !cache.containsKey(key);
  }

  public void clear() {
    cache.remove(key);
    try {
      String json = writer.writeValueAsString(cache);
      settings().setPingHistory(json);
    } catch (JsonProcessingException e) {
      throw new NaturalisPluginException(e);
    }
  }

  public String getPingValue() {
    return cache.get(key);
  }

  public boolean isOlderThan(int minutes) {
    String pingValue = cache.get(key);
    long timestamp = Long.parseLong(pingValue.substring(pingValue.lastIndexOf('/') + 1));
    return (System.currentTimeMillis() - timestamp) > (minutes * 60 * 1000);
  }

  public String generateNewPingValue() {
    if (isClear()) {
      String value = "ping:" + key + "//" + System.currentTimeMillis();
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
      throw new NaturalisPluginException(e);
    }
  }

}
