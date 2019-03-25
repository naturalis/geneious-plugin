package nl.naturalis.geneious;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.EnumMap;

import com.google.common.base.Charsets;

import org.apache.commons.lang3.StringUtils;

import static nl.naturalis.geneious.ErrorCode.OK;

public class MessageProvider {

  public static class Message {
    private final ErrorCode code;
    private final String message;

    public Message(ErrorCode code, String message) {
      this.code = code;
      this.message = message;
    }

    public ErrorCode getCode() {
      return code;
    }

    public String getMessage() {
      return message;
    }

  }

  public static final Message OK_MESSAGE = new Message(OK, "OK");

  public static Message messageFor(ErrorCode key, Object... msgArgs) {
    return instance().getMessage(key, msgArgs);
  }

  public static String get(ErrorCode key, Object... msgArgs) {
    return instance().getMessage(key, msgArgs).getMessage();
  }

  private static MessageProvider instance;

  public static MessageProvider instance() {
    if (instance == null) {
      instance = new MessageProvider();
    }
    return instance;
  }

  private final EnumMap<ErrorCode, String> messages;

  private MessageProvider() {
    messages = new EnumMap<>(ErrorCode.class);
    loadMessages();
  }

  public Message getMessage(ErrorCode key, Object... msgArgs) {
    String fmt = messages.get(key);
    if (fmt == null || msgArgs.length != key.getMessageArgCount()) { // Be lenient
      return new Message(key, key.toString());
    }
    return new Message(key, String.format(fmt, msgArgs));
  }

  private void loadMessages() {
    EnumMap<ErrorCode, String> messages = this.messages;
    InputStream is = getClass().getResourceAsStream("/messages.txt");
    LineNumberReader lnr = new LineNumberReader(new InputStreamReader(is, Charsets.UTF_8));
    StringBuilder value = new StringBuilder();
    try {
      String line = lnr.readLine();
      do {
        for (; isComment(line) || isBlank(line); line = lnr.readLine());
        if (endOfFile(line)) {
          break;
        }
        if (!isKey(line)) {
          throw new NaturalisPluginException("Message key expected but not encountered. Error at line " + lnr.getLineNumber());
        }
        ErrorCode key = toKey(line);
        if (!isValue(line = lnr.readLine())) {
          throw new NaturalisPluginException("Message value expected but not encountered. Error at line " + lnr.getLineNumber());
        }
        value.setLength(0);
        value.append(line.trim());
        for (line = lnr.readLine(); isValue(line); line = lnr.readLine()) {
          value.append(' ').append(line.trim());
        }
        messages.put(key, value.toString());
      } while (true);
    } catch (IOException e) {
      throw new WrappedException(e);
    }
  }

  private static ErrorCode toKey(String line) {
    try {
      return ErrorCode.valueOf(line.trim());
    } catch (IllegalArgumentException e) {
      throw new NaturalisPluginException("Invalid message key: " + line);
    }
  }

  private static boolean endOfFile(String line) {
    return line == null;
  }

  private static boolean isBlank(String line) {
    return !endOfFile(line) && StringUtils.isBlank(line);
  }

  private static boolean isComment(String line) {
    return !endOfFile(line) && !isBlank(line) && line.charAt(0) == '#';
  }

  private static boolean isValue(String line) {
    return !endOfFile(line) && !isBlank(line) && line.charAt(0) == '\t';
  }

  private static boolean isKey(String line) {
    return !endOfFile(line) && !isComment(line) && !isBlank(line) && !isValue(line);
  }

}
