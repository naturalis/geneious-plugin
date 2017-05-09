/**
 * 
 */
package nl.naturalis.lims2.utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
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

	/**
	 * Method to create a log FileHandler and the format
	 * 
	 * @param name
	 *            Set parameter value name
	 * */
	public LimsLogger(String name) {
		try {
			fileHandler = new FileHandler(name, false);
			simpleFormatter = new SimpleFormatter();
			logger.addHandler(fileHandler);
			fileHandler.setFormatter(simpleFormatter);
			fileHandler.setLevel(Level.ALL);
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
			logger.addHandler(fh);
			logger.setUseParentHandlers(false);
			logger.log(Level.INFO, msg);
			fh.flush();
			fh.close();
		} catch (Exception e) {
			System.out.println("Exception thrown: " + e);
			e.printStackTrace();
		}
	}
}