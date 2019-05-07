package nl.naturalis.geneious;

import java.time.LocalDateTime;

import javax.swing.ProgressMonitor;

import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;

import static com.biomatters.geneious.publicapi.utilities.GuiUtilities.getMainFrame;

import static nl.naturalis.geneious.Settings.settings;

/**
 * Displays a progress bar to the user indicating the number of seconds to wait before the operation he/she initiated
 * will commence.
 */
public class WaitTimer {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(WaitTimer.class);

  private static final int TICKS_PER_SECOND = 1;

  /**
   * Starts a timer and returns false if the user canceled it, true otherwise.
   * 
   * @return
   */
  public static boolean isOperationAllowed() {
    return new WaitTimer().start();
  }

  /**
   * Convenience method for storing a the current time as the end time of an operation.
   */
  public static void setNewEndTime() {
    settings().setLastFinished(now().truncatedTo(SECONDS));
  }

  private final LocalDateTime lastFinished;
  private final int minWaitTime;

  private WaitTimer() {
    lastFinished = settings().getLastFinished();
    minWaitTime = settings().getMinWaitTime();
  }

  private boolean start() {
    int secondsSinceLastOperation = (int) SECONDS.between(lastFinished, now());
    int secondsToWait = minWaitTime - secondsSinceLastOperation;
    if (secondsToWait <= 0) {
      return true;
    }
    String msg = String.format("Wating for indexing to complete. %s seconds to go ...", secondsToWait);
    guiLogger.info(msg);
    if (secondsToWait < 5) {
      sleep(secondsToWait * 1000);
      return true;
    }
    int ticks = secondsToWait * TICKS_PER_SECOND;
    ProgressMonitor pm = new ProgressMonitor(getMainFrame(), "Waiting for indexing to complete ... ", msg, 0, ticks);
    for (int i = 1; i < ticks; ++i) {
      sleep(1000 / TICKS_PER_SECOND);
      if (pm.isCanceled()) {
        pm.close();
        guiLogger.info("Operation canceled");
        return false;
      }
      msg = String.format("%s seconds to go ...", (secondsToWait - (i * TICKS_PER_SECOND)));
      pm.setNote(msg);
      pm.setProgress(i);
    }
    pm.close();
    return true;
  }

  private static void sleep(int millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
    }
  }

}
