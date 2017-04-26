/**
 * 
 */
package nl.naturalis.lims2.utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.XMLFormatter;

/**
 * <table summary="Logger class">
 * <tr>
 * <td>
 * Date: 24 august 2016</td>
 * </tr>
 * <tr>
 * <td>
 * Company: Naturalis Biodiversity Center</td>
 * </tr>
 * <tr>
 * <td>
 * City: Leiden</td>
 * </tr>
 * <tr>
 * <td>
 * Country: Netherlands</td>
 * </tr>
 * <tr>
 * <td>
 * Description:<br>
 * </td>
 * </tr>
 * </table>
 * 
 * @author Reinier.Kartowikromo
 *
 */
public final class LimsLogger {

	LimsImporterUtil limsImporterUtil = new LimsImporterUtil();

	/**
	 * Instance of the real logger object.
	 */
	private Logger logger = Logger.getLogger(limsImporterUtil.getLogPath()
			+ limsImporterUtil.getLogFilename());
	private FileHandler fileHandler = null;
	private Formatter simpleFormatter = null;
	private Handler consoleHandler = new ConsoleHandler();
	private String logFileName = "";

	/**
	 * Method to create a log FileHandler and the format
	 * 
	 * @param name
	 *            Set parameter value name
	 * */
	public LimsLogger(String name) {
		try {
			fileHandler = new FileHandler(name, false);
			// consoleHandler = new ConsoleHandler();

			simpleFormatter = new SimpleFormatter();

			logger.addHandler(fileHandler);
			// logger.addHandler(consoleHandler);

			fileHandler.setFormatter(simpleFormatter);

			fileHandler.setLevel(Level.ALL);
			// consoleHandler.setLevel(Level.ALL);
			logger.setLevel(Level.ALL);
		} catch (IOException exception) {
			logger.log(Level.SEVERE, "Error occur in FileHandler.", exception);
		}
	}

	/**
	 * Method to create the logfile and add message to the log file
	 * 
	 * @param filename
	 *            Give a filename
	 * @param msg
	 *            Message
	 * */
	public void logToFile(String filename, String msg) {
		try {
			LogManager lm = LogManager.getLogManager();
			Logger logger;

			String pathUitVal = limsImporterUtil.getLogPath().substring(0,
					limsImporterUtil.getLogPath().length() - 1);
			File dir = new File(pathUitVal);
			if (!dir.exists()) {
				System.out.println("creating directory: " + pathUitVal);
				boolean result = false;

				try {
					dir.mkdir();
					result = true;
				} catch (SecurityException se) {
					// handle it
				}
				if (result) {
					System.out.println("Directory created");
				}
			}
			FileHandler fh = new FileHandler(filename, true);

			logger = Logger.getLogger("LimsLogger");

			lm.addLogger(logger);
			logger.setLevel(Level.INFO);
			fh.setFormatter(new XMLFormatter());
			// fh.setFormatter(new SimpleFormatter());

			logger.addHandler(fh);
			// root logger defaults to SimpleFormatter. We don't want messages
			// logged twice.
			logger.setUseParentHandlers(false);
			logger.log(Level.INFO, msg);
			fh.flush();
			fh.close();
		} catch (Exception e) {
			System.out.println("Exception thrown: " + e);
			e.printStackTrace();
		}
	}

	/*
	 * public void logMessage(String message) { LogRecord record = new
	 * LogRecord(Level.INFO, message); logger.log(record); }
	 * 
	 * public void flushCloseFileHandler() { fileHandler.flush();
	 * fileHandler.close(); }
	 * 
	 * public void removeConsoleHandler() {
	 * logger.removeHandler(consoleHandler); }
	 */

	/**
	 * Obtain a new or existing Logger instance.
	 * 
	 * @param name
	 *            Name of the logger, package names are recommended
	 */
	/*
	 * public static LimsLogger getLogger(String name) { return new
	 * LimsLogger(name); }
	 */

	/*
	 * public static void configure(String filename) { FileInputStream is =
	 * null; try { is = new FileInputStream(filename);
	 * LogManager.getLogManager().readConfiguration(is); } catch (Exception e) {
	 * System.err .println(
	 * "SIMPLEDBM-ERROR: Failed to initialize logging system due to following error: "
	 * + e.getMessage()); e.printStackTrace(); } finally { try { is.close(); }
	 * catch (IOException e) { } } }
	 * 
	 * public void info(String sourceClass, String sourceMethod, String message)
	 * { logger.logp(Level.INFO, sourceClass, sourceMethod, message); }
	 * 
	 * public void info(String sourceClass, String sourceMethod, String message,
	 * Throwable thrown) { logger.logp(Level.INFO, sourceClass, sourceMethod,
	 * message, thrown); }
	 * 
	 * public void debug(String sourceClass, String sourceMethod, String
	 * message) { logger.logp(Level.FINE, sourceClass, sourceMethod, message); }
	 * 
	 * public void debug(String sourceClass, String sourceMethod, String
	 * message, Throwable thrown) { logger.logp(Level.FINE, sourceClass,
	 * sourceMethod, message, thrown); }
	 * 
	 * public void trace(String sourceClass, String sourceMethod, String
	 * message) { logger.logp(Level.FINER, sourceClass, sourceMethod, message);
	 * }
	 * 
	 * public void trace(String sourceClass, String sourceMethod, String
	 * message, Throwable thrown) { logger.logp(Level.FINER, sourceClass,
	 * sourceMethod, message, thrown); }
	 * 
	 * public void warn(String sourceClass, String sourceMethod, String message)
	 * { logger.logp(Level.WARNING, sourceClass, sourceMethod, message); }
	 * 
	 * public void warn(String sourceClass, String sourceMethod, String message,
	 * Throwable thrown) { logger.logp(Level.WARNING, sourceClass, sourceMethod,
	 * message, thrown); }
	 * 
	 * public void error(String sourceClass, String sourceMethod, String
	 * message) { logger.logp(Level.SEVERE, sourceClass, sourceMethod, message);
	 * }
	 * 
	 * public void error(String sourceClass, String sourceMethod, String
	 * message, Throwable thrown) { logger.logp(Level.SEVERE, sourceClass,
	 * sourceMethod, message, thrown); }
	 * 
	 * public boolean isTraceEnabled() { return logger.isLoggable(Level.FINER);
	 * }
	 * 
	 * public boolean isDebugEnabled() { return logger.isLoggable(Level.FINE); }
	 * 
	 * public void enableDebug() { logger.setLevel(Level.FINE); }
	 * 
	 * public void disableDebug() { logger.setLevel(Level.INFO); }
	 * 
	 * public void createLogFile(String fileName, List<String> list) {
	 * logFileName = limsImporterUtil.getLogPath() + File.separator + fileName +
	 * limsImporterUtil.getLogFilename(); logToFile(logFileName,
	 * list.toString()); }
	 */
}
