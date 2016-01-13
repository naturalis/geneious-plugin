/**
 * 
 */
package nl.naturalis.lims2.utils;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsLogToFile {

	public LimsLogToFile() {

	}

	public void LogToFile(String path, String message) {
		Logger logger = Logger.getLogger("LimsLogToFile");
		FileHandler fh;

		try {

			// This block configure the logger with handler and formatter
			fh = new FileHandler(path);
			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);

			// the following statement is used to log any messages
			logger.info(message);

		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.setUseParentHandlers(false);
	}

}
