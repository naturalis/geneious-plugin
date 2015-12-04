/**
 * 
 */
package nl.naturalis.lims2.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.server.ExportException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsImporterUtil {

	private final Properties config = null;
	static final Logger logger = LoggerFactory
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
			logger.warn(String
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

	String result = "";
	InputStream inputStream;

	public String getPropValues() throws IOException {

		try {
			Properties prop = new Properties();
			String propFileName = "lims-import.properties";
			String workingDatadirectory = System.getProperty("user.dir");

			String absoluteFilePath = null;

			if (workingDatadirectory != null) {
				absoluteFilePath = workingDatadirectory + File.separator
						+ propFileName;
				// System.out.println("Pad: " + absoluteFilePath);
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
			String csvPath = prop.getProperty("lims.import.input.cs_dir");

			result = csvPath;
		} catch (Exception e) {
			logger.info("Exception: " + e);
			System.out.println("Exception: " + e);
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
				// System.out.println("Pad: " + absoluteFilePath);
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

	byte[] readSmallBinaryFile(String aFileName) throws IOException {
		Path path = Paths.get(aFileName);
		return Files.readAllBytes(path);
	}

	byte[] read(String aInputFileName) {
		LimsImporterUtil.logger.info("Reading in binary file named : "
				+ aInputFileName);
		File file = new File(aInputFileName);
		LimsImporterUtil.logger.info("File size: " + file.length());
		byte[] result = new byte[(int) file.length()];
		try {
			InputStream input = null;
			try {
				int totalBytesRead = 0;
				input = new BufferedInputStream(new FileInputStream(file));
				while (totalBytesRead < result.length) {
					int bytesRemaining = result.length - totalBytesRead;
					// input.read() returns -1, 0, or more :
					int bytesRead = input.read(result, totalBytesRead,
							bytesRemaining);
					if (bytesRead > 0) {
						totalBytesRead = totalBytesRead + bytesRead;
					}
				}
				/*
				 * the above style is a bit tricky: it places bytes into the
				 * 'result' array; 'result' is an output parameter; the while
				 * loop usually has a single iteration only.
				 */
				LimsImporterUtil.logger.info("Num bytes read: "
						+ totalBytesRead);
			} finally {
				LimsImporterUtil.logger.info("Closing input stream.");
				input.close();
			}
		} catch (FileNotFoundException ex) {
			LimsImporterUtil.logger.info("File not found.");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return result;
	}

	/**
	 * Read an input stream, and return it as a byte array. Sometimes the source
	 * of bytes is an input stream instead of a file. This implementation closes
	 * aInput after it's read.
	 */
	byte[] readAndClose(InputStream aInput) {
		// carries the data from input to output :
		byte[] bucket = new byte[32 * 1024];
		ByteArrayOutputStream result = null;
		try {
			try {
				// Use buffering? No. Buffering avoids costly access to disk or
				// network;
				// buffering to an in-memory stream makes no sense.
				result = new ByteArrayOutputStream(bucket.length);
				int bytesRead = 0;
				while (bytesRead != -1) {
					// aInput.read() returns -1, 0, or more :
					bytesRead = aInput.read(bucket);
					if (bytesRead > 0) {
						result.write(bucket, 0, bytesRead);
					}
				}
			} finally {
				aInput.close();
				// result.close(); this is a no-operation for
				// ByteArrayOutputStream
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return result.toByteArray();
	}

	/** Read the given binary file, and return its contents as a byte array. */
	byte[] readAlternateImpl(String aInputFileName) {
		logger.info("Reading in binary file named : " + aInputFileName);
		File file = new File(aInputFileName);
		logger.info("File size: " + file.length());
		byte[] result = null;
		try {
			InputStream input = new BufferedInputStream(new FileInputStream(
					file));
			result = readAndClose(input);
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
		return result;
	}

	public String getFileExtension(File file) {
		String name = file.getName();
		try {
			return name.substring(name.lastIndexOf(".") + 1);
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Tries and find an encoding value on the very first line of the file
	 * contents.
	 * 
	 * @param fileContent
	 *            The content from which to read an encoding value.
	 * @return The charset name if it exists and is supported, <code>null</code>
	 *         otherwise
	 */
	@SuppressWarnings("unused")
	private static String getCharset(String fileContent) {
		String trimmedContent = fileContent.trim();
		String charsetName = null;
		if (trimmedContent.length() > 0) {
			BufferedReader reader = new BufferedReader(new StringReader(
					trimmedContent));
			String firstLine = trimmedContent;
			try {
				firstLine = reader.readLine();
			} catch (IOException e) {
			}
			Pattern encodingPattern = Pattern
					.compile("encoding\\s*=\\s*(\"|\')?([-a-zA-Z0-9]+)\1?");
			Matcher matcher = encodingPattern.matcher(firstLine);
			if (matcher.find()) {
				charsetName = matcher.group(2);
			}
		}
		if (charsetName != null && Charset.isSupported(charsetName)) {
			return charsetName;
		}
		return null;
	}

	public static String readFile(final File file) throws IOException {
		final FileInputStream stream = new FileInputStream(file);
		try {
			final FileChannel fc = stream.getChannel();
			final ByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0,
					fc.size());
			String ENCODING = StandardCharsets.US_ASCII.toString();// "ASCII";
			return Charset.forName(ENCODING).decode(bb).toString();
		} finally {
			stream.close();
		}
	}

	byte[] bytearrayfromfile(File f) throws IOException {
		int filesize = (int) f.length();
		byte[] buffer = new byte[filesize];
		BufferedInputStream instream = new BufferedInputStream(
				new FileInputStream(f));
		for (int i = 0; i < filesize; i++) {
			buffer[i] = (byte) instream.read();
			if (i % 1024 == 0)
				System.out.print(".");
		}
		instream.close();
		return buffer;
	}
}
