package nl.naturalis.geneious;

import java.time.LocalDateTime;
import java.util.EnumMap;

import com.biomatters.geneious.publicapi.plugin.GeneiousPlugin;
import com.biomatters.geneious.publicapi.plugin.Options;

import org.apache.commons.lang3.StringUtils;

import nl.naturalis.geneious.seq.SequenceImportDocumentOperation;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;

import static com.biomatters.geneious.publicapi.plugin.PluginUtilities.getPluginForDocumentOperation;

import static nl.naturalis.geneious.Setting.DEBUG;
import static nl.naturalis.geneious.Setting.LAST_FINISHED;
import static nl.naturalis.geneious.Setting.MIN_WAIT_TIME;

/**
 * A cache and easy means of accessing the settings in the Tools -> Preferences tab. This class mainly exists to work
 * around a Geneious bug that makes directly accessing the options defined there very tricky. BE CAREFUL WHEN TRYING TO
 * MAKE THIS CODE LEANER OR MORE EFFICIENT! Notably tricky are the hidden settings in the Tools -> Preferences panel.
 * For these there are setters in this class, which will update the value of the panel's hidden Swing component. The
 * Swing component on its turn fires a change listener which will update the cache by calling the
 * {@link #update(Setting, Object) update} method. That sounds like you can short-circuit this. Manage your expectations
 * if you try. See also
 * {@linkplain https://support.geneious.com/hc/en-us/community/posts/360043526672--Error-whilst-reading-memory-file-when-clicking-OK-or-Apply-in-Preferences-tabs}.
 *
 * @author Ayco Holleman
 */
/*
 */
public class Settings {

  private static Settings instance;

  /**
   * Returns the singleton instance of the {@code Settings} class.
   * 
   * @return
   */
  public static Settings settings() {
    if (instance == null) {
      instance = new Settings();
    }
    return instance;
  }

  private final EnumMap<Setting, Object> cache = new EnumMap<>(Setting.class);

  private Settings() {}

  /**
   * Callback method for the change listeners in {@link NaturalisPreferencesOptions}.
   * 
   * @param setting
   * @param value
   */
  public void update(Setting setting, Object value) {
    cache.put(setting, value);
  }

  /**
   * Returns the end time of the most recently completed operation.
   * 
   * @return
   */
  public LocalDateTime getLastFinished() {
    String s = (String) cache.get(LAST_FINISHED);
    if (StringUtils.isBlank(s)) {
      return now().minusYears(1).truncatedTo(SECONDS);
    }
    return LocalDateTime.parse(s);
  }

  /**
   * Sets the end time of the most recently completed operation.
   * 
   * @param timestamp
   */
  public void setLastFinished(LocalDateTime timestamp) {
    String s = timestamp.toString();
    cache.put(LAST_FINISHED, s);
    GeneiousPlugin me = getPluginForDocumentOperation(new SequenceImportDocumentOperation());
    Options opts = me.getPluginPreferences().get(0).getActiveOptions();
    opts.getOption(LAST_FINISHED.getName()).setValue(s);
  }

  /**
   * Whether or now to show DEBUG messages in the execution logs.
   * 
   * @return
   */
  public boolean isDebug() {
    return (Boolean) cache.get(DEBUG);
  }

  /**
   * Returns the minium wait time (in seconds) between operations.
   * 
   * @return
   */
  public int getMinWaitTime() {
    return ((Integer) cache.get(MIN_WAIT_TIME));
  }

}
