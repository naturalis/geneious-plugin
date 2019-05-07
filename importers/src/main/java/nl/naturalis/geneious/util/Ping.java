package nl.naturalis.geneious.util;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.swing.ProgressMonitor;

import com.biomatters.geneious.publicapi.databaseservice.DatabaseServiceException;
import com.biomatters.geneious.publicapi.databaseservice.WritableDatabaseService;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import jebl.util.ProgressListener;
import nl.naturalis.common.base.NStrings;
import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;

import static com.biomatters.geneious.publicapi.utilities.GuiUtilities.getMainFrame;

public class Ping {

  private static final int MAX_NUM_ATTEMPTS = 100;
  private static final double ATTEMPT_INTERVAL_SECS = 2.5;

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(Ping.class);

  private static String prevPingVal;

  public static boolean resume() throws DatabaseServiceException {
    return new Ping(prevPingVal).doResume();
  }

  public static boolean start() throws DatabaseServiceException {
    return new Ping().doStart();
  }

  private final String pingValue;

  private Ping() {
    pingValue = (prevPingVal = newPingValue());
  }

  private Ping(String pingValue) {
    this.pingValue = pingValue;
  }

  private boolean doStart() throws DatabaseServiceException {
    guiLogger.info("Waiting for indexing to complete ... ");
    PingSequence sequence = new PingSequence(pingValue);
    guiLogger.debug("Creating ping document");
    sequence.save();
    return startPingLoop();
  }

  private boolean doResume() throws DatabaseServiceException {
    if (pingValue == null) {
      return true;
    }
    guiLogger.info("Waiting for indexing to complete ... ");
    return startPingLoop();
  }

  private boolean startPingLoop() throws DatabaseServiceException {
    ProgressMonitor pm = new ProgressMonitor(getMainFrame(), "Waiting for indexing to complete ... ", "Ping ... ", 0, MAX_NUM_ATTEMPTS);
    guiLogger.debug("Waiting for ping document to come back");
    for (int i = 0; i < MAX_NUM_ATTEMPTS; ++i) {
      sleep();
      if (pm.isCanceled()) {
        guiLogger.warn("Wait aborted. Do not manually delete the ping document.");
        pm.close();
        return false;
      }
      pm.setProgress(i);
      pm.setNote(String.format("Ping ... (attempt %d of %d)", i, MAX_NUM_ATTEMPTS));
      AnnotatedPluginDocument apd = ping();
      if (apd != null) {
        guiLogger.debug("Deleting ping document");
        ((WritableDatabaseService) apd.getDatabase()).removeDocument(apd, ProgressListener.EMPTY);
        pm.close();
        prevPingVal = null;
        guiLogger.info("Indexing complete");
        return true;
      }
    }
    guiLogger.warn("Aborting wait after %d pings", MAX_NUM_ATTEMPTS);
    return false;
  }

  private AnnotatedPluginDocument ping() throws DatabaseServiceException {
    List<AnnotatedPluginDocument> result = QueryUtils.findByExtractID(Arrays.asList(pingValue));
    return result.isEmpty() ? null : result.get(0);
  }

  private static void sleep() {
    try {
      Thread.sleep((long) ATTEMPT_INTERVAL_SECS * 1000);
    } catch (InterruptedException e) {
    }
  }

  private static String newPingValue() {
    return new StringBuilder().append('!')
        .append(NStrings.zpad(new Random().nextInt(10000), 4))
        .append('/')
        .append(System.currentTimeMillis())
        .toString();
  }

}
