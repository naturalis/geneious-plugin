/**
 * 
 */
package nl.naturalis.lims2.ab1.importer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import nl.naturalis.lims2.utils.LimsImporterUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsFileSelector {

	LimsImporterUtil limsImporterUtil = new LimsImporterUtil();

	private static final Logger logger = LoggerFactory
			.getLogger(LimsImportAB1Update.class);

	private String fileSelected = "";
	private String line;

	/**
	 * File selection for CRS, Bold, Samples
	 * 
	 * @return fileSelected;
	 * */
	public String loadSelectedFile() {
		String csvPath = "";
		File fileMap = null;
		try {
			/* Get the Data path from the property file */
			csvPath = limsImporterUtil
					.getPropValues("lims.import.input.cs_dir");
		} catch (IOException e) {
			e.printStackTrace();
		}
		/* Set the path */
		JFileChooser chooser = new JFileChooser(csvPath);
		/* Set the title for the dialog */
		chooser.setDialogTitle("Select a file");
		/* Get the current directory. */
		chooser.getCurrentDirectory();
		/* Set filter extension for the type of files to select. */
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"TXT & XLS, CSV Files", "txt", "xls", "csv");
		chooser.setFileFilter(filter);
		/* Open the dialog */
		int returnVal = chooser.showOpenDialog(chooser);

		/* Select a file. Choose OK when file selected otherwise Cancel */
		switch (returnVal) {
		case JFileChooser.APPROVE_OPTION: {
			logger.info("You chose to open this CSV file: "
					+ chooser.getSelectedFile().getAbsolutePath());
			fileMap = chooser.getCurrentDirectory();
			fileSelected = fileMap.getAbsolutePath() + File.separator
					+ chooser.getSelectedFile().getName();
			break;
		}
		case JFileChooser.CANCEL_OPTION: {
			logger.info("Cancel or the close-dialog icon was clicked.");
			fileSelected = null;
			break;
		}
		case JFileChooser.ERROR_OPTION: {
			System.out.println("Error");
			break;
		}
		}
		return fileSelected;

	}

	/**
	 * Load a fasta file in memory and get the filename inside the file.
	 * 
	 *
	 * @param fileName
	 *            , fastaImportFilePath
	 * @return
	 * 
	 * @throws FileNotFoundException
	 * 
	 * */
	public String loadFastaFile(String fileName, String fastaImportFilePath)
			throws FileNotFoundException {

		String currentFileName = "";

		/* Set the fasta directory path */
		JFileChooser chooser = new JFileChooser(fastaImportFilePath);
		/* Get the selected filename */
		chooser.setSelectedFile(new File(chooser.getCurrentDirectory(),
				fileName + ".fas"));
		/* Set file extension which file to choose. */
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Fasta Files", "fas");
		/* Set the filter */
		chooser.setFileFilter(filter);

		logger.info("You chose to open this CSV file: "
				+ chooser.getSelectedFile().getAbsolutePath());
		try {
			/* Get the file */
			File fileMap = new File(fastaImportFilePath, fileName + ".fas");

			/* Check if directory is accessible and file is normal */
			if (fileMap.canRead() && fileMap.isFile()) {
				/* get the path of the file */
				fileSelected = fileMap.getAbsolutePath();

				/* Load the file content in the buffer */
				BufferedReader in = new BufferedReader(new FileReader(
						fileSelected));

				/* read the file content */
				try {
					line = in.readLine();

					/* Checked if the content contain a ">" character. */
					if (line.startsWith(">")) {
						/* Get the filename. >e30233392_Lon_rub_RL030_COI */
						currentFileName = line.substring(1);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					/* Close the buffer */
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				/*
				 * When file path is not correct set in the property file Show a
				 * dialog and then throw an exception
				 */
				JOptionPane.showMessageDialog(null,
						"Incorrect Fasta path in the document file: "
								+ fastaImportFilePath, "InfoBox: "
								+ "Read Fasta file",
						JOptionPane.INFORMATION_MESSAGE);
				throw new FileNotFoundException("file path '"
						+ fastaImportFilePath + "' is not correct.");
			}
		} catch (FileNotFoundException e) {
			logger.info("Wrong path for fasta file: " + fastaImportFilePath);
			e.printStackTrace();
		}
		return currentFileName;
	}

	/**
	 * Method use in LimsImportAB1 class. Get the fasta filename from the
	 * content
	 * 
	 * @param
	 * @return resultFastaFileName
	 * @throws FileNotFoundException
	 */
	public String readFastaContent(File file) throws FileNotFoundException {
		String resultFastaFileName = "";
		/* Load content in buffer */
		BufferedReader in = new BufferedReader(new FileReader(file));
		String line;
		try {
			/* Read the content */
			line = in.readLine();
			if (line.startsWith(">")) {
				/* Get the filename. >e30233392_Lon_rub_RL030_COI */
				resultFastaFileName = line.substring(1);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resultFastaFileName;
	}
}
