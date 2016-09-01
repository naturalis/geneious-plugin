/**
 * 
 */
package nl.naturalis.lims2.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <table>
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
 * Class with methods to read values from the lims-import.properties and
 * limsdatabase.properties file(s)</td>
 * </tr>
 * </table>
 * 
 * @author Reinier.Kartowikromo
 *
 */
public class LimsImporterUtil {

	private String result = "";
	private InputStream inputStream;
	private Properties prop = new Properties();
	private static String propFileName = "lims-import.properties";
	private static String workingDatadirectory = System.getProperty("user.dir");
	private static String absoluteFilePath = null;

	private static final Logger logger = LoggerFactory
			.getLogger(LimsImporterUtil.class);

	/**
	 * Get the logname from the property file logname=Lims2-Import.log
	 * 
	 * @return logFileName;
	 */
	public String getLogFilename() {
		String logFileName = "";

		if (workingDatadirectory != null) {
			absoluteFilePath = workingDatadirectory + File.separator
					+ propFileName;
		}

		try {
			inputStream = new FileInputStream(absoluteFilePath);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		if (inputStream != null) {
			try {
				prop.load(inputStream);
				logFileName = prop.getProperty("logname");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return logFileName;
	}

	/**
	 * Get the logpath from the property file. logpath=C:/Temp/Uitvallijst/
	 * 
	 * @return logPath;
	 * */
	public String getLogPath() {
		String logPath = "";

		if (workingDatadirectory != null) {
			absoluteFilePath = workingDatadirectory + File.separator
					+ propFileName;
		}

		try {
			inputStream = new FileInputStream(absoluteFilePath);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		if (inputStream != null) {
			try {
				prop.load(inputStream);
				logPath = prop.getProperty("logpath");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return logPath;
	}

	private Properties importProps;

	/**
	 * Get the values from lims-import.properties file
	 * 
	 * @param propertyType
	 *            Set param PropertyType value
	 * @return Return value Property type
	 * @throws IOException
	 *             Throw a exception message
	 * 
	 * @see String
	 * */
	public String getPropValues(String propertyType) throws IOException {
		if (importProps == null) {
			try {
				importProps = new Properties();
				String workingDatadirectory = System.getProperty("user.dir");

				String absoluteFilePath = null;

				if (workingDatadirectory != null) {
					absoluteFilePath = workingDatadirectory + File.separator
							+ propFileName;
				}

				inputStream = new FileInputStream(absoluteFilePath);
				if (inputStream != null) {
					importProps.load(inputStream);
				} else {
					/* TODO */
					// Ayco: deze message gaat dubbel gelogd worden, want
					// de FileNotFoundException wordt in het catch block
					// beneden opgevangen en daar wordt de message weer
					// gelogd.
					logger.info("property file '" + propFileName
							+ "' not found in the classpath");
					throw new FileNotFoundException("property file '"
							+ propFileName + "' not found in the classpath");
				}
			} catch (Exception e) {
				logger.info("Exception: " + e);
			} finally {
				inputStream.close();
			}
		}
		// get the property value and print it out
		result = importProps.getProperty(propertyType);
		return result;
	}

	private Properties dbProps;

	/**
	 * Get teh values from limsdatabase.properties
	 * 
	 * @param propertyType
	 *            A property name
	 * @return Return PropertyType
	 * @see String
	 * */
	public String getDatabasePropValues(String propertyType) {
		if (dbProps == null) {
			try {
				dbProps = new Properties();
				String propFileName = "limsdatabase.properties";
				String workingDatadirectory = System.getProperty("user.dir");

				String absoluteFilePath = null;

				if (workingDatadirectory != null) {
					absoluteFilePath = workingDatadirectory + File.separator
							+ propFileName;
				}

				inputStream = new FileInputStream(absoluteFilePath);
				if (inputStream != null) {
					dbProps.load(inputStream);
				} else {
					// Zo log je dezelfde message twee keer, want de
					// FileNotFoundException wordt binnen deze method
					// opgevangen, en het catch block logt de fout
					// opnieuw (zie beneden)
					logger.info("property file '" + propFileName
							+ "' not found in the classpath");
					throw new FileNotFoundException("property file '"
							+ propFileName + "' not found in the classpath");
				}
			} catch (Exception e) {
				logger.info("Exception: " + e);
			} finally {
				try {
					inputStream.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
		// get the property value and print it out
		result = dbProps.getProperty(propertyType);
		return result;
	}

	/**
	 * Used in LimsImportSamples Extract numbers from a string
	 * 
	 * @param str
	 *            Give a string value to check if it contains numbers
	 * @return sb.toString();
	 * 
	 * @see String
	 * */
	public static String extractNumber(final String str) {

		if (str == null || str.isEmpty())
			return "";

		StringBuilder sb = new StringBuilder();
		boolean found = false;
		for (char c : str.toCharArray()) {
			if (Character.isDigit(c)) {
				sb.append(c);
				found = true;
			} else if (found) {
				// If we already found a digit before and this char is not a
				// digit, stop looping
				break;
			}
		}

		return sb.toString();
	}

	/**
	 * Check for Letters character in "ID" (String)
	 * 
	 * @param name
	 *            Set parameter value name
	 * @return true or false
	 * @see boolean
	 * 
	 **/
	public boolean isAlpha(String name) {
		return name.matches("[a-zA-Z]+");
	}

	/**
	 * Get the total records in a csv file
	 * 
	 * @param filename
	 *            Set parameter filename value
	 * @return Return an integer Total of records in a CSV file
	 * @see int
	 * */
	public int countRecordsCSV(String filename) {
		try {
			InputStream is = new BufferedInputStream(new FileInputStream(
					filename));
			try {
				byte[] c = new byte[1024];
				int count = 0;
				int readChars = 0;
				boolean empty = true;
				while ((readChars = is.read(c)) != -1) {
					empty = false;
					for (int i = 0; i < readChars; ++i) {
						if (c[i] == '\n') {
							++count;
						}
					}
				}
				return (count == 0 && !empty) ? 1 : count;
			} finally {
				is.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
