/**
 * 
 */
package nl.naturalis.lims2.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.rmi.server.ExportException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsImporterUtil {

	private final Properties config = null;
	private String result = "";
	private InputStream inputStream;
	private Properties prop = new Properties();
	private static String propFileName = "lims-import.properties";
	private static String workingDatadirectory = System.getProperty("user.dir");
	private static String absoluteFilePath = null;

	private static final Logger logger = LoggerFactory
			.getLogger(LimsImporterUtil.class);

	public String required(String property) throws Exception {
		if (config.containsKey(property)) {
			String s = $(property);
			if (s.trim().isEmpty()) {
				throw new Exception(property);
			}
			return s;
		}
		throw new Exception(property);
	}

	private String $(String property) {
		return config.getProperty(property);
	}

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
				e.printStackTrace();
			}
		}
		return logFileName;
	}

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
				e.printStackTrace();
			}
		}
		return logPath;
	}

	public File getLimsImportDir() throws ExportException {
		String outputRoot = null;
		try {
			outputRoot = required("lims.import.input.cs_dir");

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		Path path = FileSystems.getDefault().getPath(outputRoot, "data");
		File exportDir = path.toFile();
		if (exportDir.isDirectory()) {
			if (!exportDir.canWrite()) {
				throw new ExportException(String.format(
						"Directory not writable: \"%s\"", path));
			}
		} else {
			logger.info(String
					.format("No such directory (lims.import.input.cs_dir): \"%s\". Will attempt to create it",
							path));
			try {
				java.nio.file.Files.createDirectories(path);
			} catch (IOException e) {
				throw new ExportException(String.format(
						"Failed to create directory \"%s\"", path), e);
			}
		}
		return exportDir;
	}

	public String getPropValues(String propertyType) throws IOException {
		try {
			Properties prop = new Properties();
			String propFileName = "lims-import.properties";
			String workingDatadirectory = System.getProperty("user.dir");

			String absoluteFilePath = null;

			if (workingDatadirectory != null) {
				absoluteFilePath = workingDatadirectory + File.separator
						+ propFileName;
			}

			inputStream = new FileInputStream(absoluteFilePath);
			if (inputStream != null) {
				prop.load(inputStream);
			} else {
				logger.info("property file '" + propFileName
						+ "' not found in the classpath");
				throw new FileNotFoundException("property file '"
						+ propFileName + "' not found in the classpath");
			}

			// get the property value and print it out
			result = prop.getProperty(propertyType);
		} catch (Exception e) {
			logger.info("Exception: " + e);
		} finally {
			inputStream.close();
		}
		return result;
	}

	public String getFileFromPropertieFile(String fileType) throws IOException {
		try {
			Properties prop = new Properties();

			String propFileName = "lims-import.properties";
			String workingDatadirectory = System.getProperty("user.dir");

			String absoluteFilePath = null;

			if (workingDatadirectory != null) {
				absoluteFilePath = workingDatadirectory + File.separator
						+ propFileName;
			}

			inputStream = new FileInputStream(absoluteFilePath);
			if (inputStream != null) {
				prop.load(inputStream);
			} else {
				logger.info("property file '" + propFileName
						+ "' not found in the classpath");
				throw new FileNotFoundException("property file '"
						+ propFileName + "' not found in the classpath");
			}

			String csvFileName = "";
			// get the property value and print it out
			if (fileType.equals("excel")) {
				csvFileName = prop.getProperty("excelfile");
			}
			if (fileType.equals("bold")) {
				csvFileName = prop.getProperty("boldfile");
			}

			result = csvFileName;
		} catch (Exception e) {
			logger.info("Exception: " + e);
			System.out.println("Exception: " + e);
		} finally {
			if (inputStream != null)
				inputStream.close();
		}
		return result;
	}

	public String getDatabasePropValues(String propertyType) throws IOException {
		try {
			Properties prop = new Properties();
			String propFileName = "limsdatabase.properties";
			String workingDatadirectory = System.getProperty("user.dir");

			String absoluteFilePath = null;

			if (workingDatadirectory != null) {
				absoluteFilePath = workingDatadirectory + File.separator
						+ propFileName;
			}

			inputStream = new FileInputStream(absoluteFilePath);
			if (inputStream != null) {
				prop.load(inputStream);
			} else {
				logger.info("property file '" + propFileName
						+ "' not found in the classpath");
				throw new FileNotFoundException("property file '"
						+ propFileName + "' not found in the classpath");
			}

			// get the property value and print it out
			result = prop.getProperty(propertyType);
		} catch (Exception e) {
			logger.info("Exception: " + e);
		} finally {
			inputStream.close();
		}
		return result;
	}

}
