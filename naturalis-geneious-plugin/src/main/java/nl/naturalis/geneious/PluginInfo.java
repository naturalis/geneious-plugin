package nl.naturalis.geneious;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PluginInfo {

  private static PluginInfo instance;

  public static PluginInfo getInstance() {
    if (instance == null) {
      instance = new PluginInfo();
    }
    return instance;
  }

  private final String version;
  private final String buildDate;
  private final String gitBranch;
  private final String gitCommit;

  private PluginInfo() {
    InputStream is = getClass().getResourceAsStream("/git.properties");
    if (is == null) {
      // Can only happen during development
      throw new RuntimeException("Yo, run Maven -> Update Project...");
    }
    Properties props = new Properties();
    try {
      props.load(is);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    version = props.getProperty("git.closest.tag.name");
    buildDate = props.getProperty("git.build.time");
    gitBranch = props.getProperty("git.branch");
    gitCommit = props.getProperty("git.commit.id.abbrev");
  }

  public String getVersion() {
    return version;
  }

  public String getBuildDate() {
    return buildDate;
  }

  public String getGitBranch() {
    return gitBranch;
  }

  public String getGitCommit() {
    return gitCommit;
  }

}
