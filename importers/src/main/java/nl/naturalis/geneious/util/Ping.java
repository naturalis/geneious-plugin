package nl.naturalis.geneious.util;

import static com.biomatters.geneious.publicapi.utilities.GuiUtilities.getMainFrame;
import static nl.naturalis.geneious.util.QueryUtils.findByExtractID;
import static nl.naturalis.geneious.util.QueryUtils.getTargetDatabase;

import java.util.List;

import javax.swing.ProgressMonitor;

import org.apache.commons.lang3.StringUtils;

import com.biomatters.geneious.publicapi.databaseservice.DatabaseServiceException;
import com.biomatters.geneious.publicapi.databaseservice.WritableDatabaseService;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.NaturalisPluginException;
import nl.naturalis.geneious.gui.ShowDialog;
import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;

/**
 * Pings the database to ascertain that all documents have been indexed.
 * 
 * @author Ayco Holleman
 *
 */
public class Ping {

  private static final int TRY_COUNT = 100;
  private static final double TRY_INTERVAL = 3.0; // seconds

  /*
   * The number and relative weight (expressed as progress bar progress) of the 1st batch of ping attempts. Since indexing is likely to
   * complete within the 1st batch of pings, we want to make it look as though we're making a lot of progress here (at the expense of the
   * progress bar seeming to grind to a halt if indexing takes more time). Tune to taste.
   */
  private static final int batch0TryCount = 10;
  private static final int batch0TryWeight = 25;
  private static final int batch1TryCount = 10;
  private static final int batch1TryWeight = 5;

  private static final String MSG_WAITING = "Waiting for indexing to complete ...";
  private static final String MSG_ABORTED = "Wait aborted after %d attempts";

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(Ping.class);

  /**
   * Sends a ping value to the database and then starts a ping loop that only ends if [1] the ping value came back from the database; [2] the
   * user cancelled the ping loop; [3] the number of pings exceeds 100 (equivalent to about 5 minutes of pinging). If [1] we assume that all
   * documents have been indexed (because the temporary document containing the ping value is itself apparently indexed) and the ping value is
   * cleared. If [2] or [3] the ping value will be stored and subsequent operation are prevented from proceeding until {@link #resume()}
   * returns {@code true}. Must be called at the very end of a {@code DocumentOperation}.
   * 
   * @return
   * @throws DatabaseServiceException
   */
  public static boolean start() throws DatabaseServiceException {
    return new Ping().doStart();
  }

  /**
   * Checks for a lingering ping value and, if present, starts the ping loop all over again. Must be called at the very beginning of a
   * {@code DocumentOperation}.
   * 
   * @return
   * @throws DatabaseServiceException
   */
  public static boolean resume() throws DatabaseServiceException {
    return new Ping().doResume();
  }

  /**
   * Generates an exception indicating that the ping mechanism got corrupted somehow.
   * 
   * @return
   */
  static NaturalisPluginException pingCorrupted() {
    String msg = "Ping mechanism corrupt. Go to Tools -> Preferences (Naturalis tab) and press the \"Clear ping history\" button";
    return new NaturalisPluginException(msg);
  }

  /**
   * Sets the user free when he/she (accidentally) deleted the ping folder or ping document before the ping loop finished.
   */
  public static void clear() {
    WritableDatabaseService svc = getTargetDatabase();
    if (svc == null) {
      ShowDialog.pleaseSelectDatabase();
    } else {
      new PingHistory().clear();
      ShowDialog.pingHistoryCleared();
    }
  }

  private final PingHistory history;

  private Ping() {
    history = new PingHistory();
  }

  private boolean doStart() throws DatabaseServiceException {
    guiLogger.info(MSG_WAITING);
    PingSequence sequence = new PingSequence(history.generateNewPingValue());
    sequence.save();
    return startPingLoop();
  }

  private boolean doResume() throws DatabaseServiceException {
    if (history.isClear()) {
      return true;
    }
    if (history.isOlderThan(30)) {
      guiLogger.warn("Indexing seems not to have completed within 30 minutes. If you are sure all");
      guiLogger.warn("documents have been indexed properly, cancel the progress bar and go to");
      guiLogger.warn("Tools -> Preferences (Naturalis tab) to clear the ping history");
    }
    guiLogger.info(MSG_WAITING);
    return startPingLoop();
  }

  private boolean startPingLoop() throws DatabaseServiceException {
    ProgressMonitor pm = new ProgressMonitor(getMainFrame(), MSG_WAITING, "", 0, getProgressMax());
    pm.setMillisToDecideToPopup(0);
    pm.setMillisToPopup(0);
    for (int i = 1; i <= TRY_COUNT; ++i) {
      pm.setProgress(getProgress(i));
      sleep();
      if (pm.isCanceled()) {
        pm.close();
        guiLogger.warn(MSG_ABORTED, i);
        return false;
      }
      AnnotatedPluginDocument document = ping();
      if (document != null) {
        pm.close();
        PingSequence.delete(document);
        history.clear();
        guiLogger.info("Indexing complete");
        return true;
      }
    }
    pm.close();
    guiLogger.warn(MSG_ABORTED, TRY_COUNT);
    return false;
  }

  private AnnotatedPluginDocument ping() throws DatabaseServiceException {
    String pingValue = history.getPingValue();
    if (StringUtils.isEmpty(pingValue)) {
      // Seems like you can make this happen with a rather contrived sequence of actions in the GUI
      throw pingCorrupted();
    }
    List<AnnotatedPluginDocument> response = findByExtractID(pingValue);
    return response.isEmpty() ? null : response.get(0);
  }

  private static int getProgressMax() {
    return (batch0TryCount * batch0TryWeight) + (batch1TryCount * batch1TryWeight) + (TRY_COUNT - batch0TryCount - batch1TryCount);
  }

  private static int getProgress(int i) {
    return (i * batch0TryWeight)
        + (max(i - batch0TryCount) * batch1TryWeight)
        + (max(i - batch0TryCount - batch1TryCount));
  }

  private static int max(int x) {
    return Math.max(x, 0);
  }

  private static void sleep() {
    try {
      Thread.sleep((long) TRY_INTERVAL * 1000);
    } catch (InterruptedException e) {
    }
  }

}
