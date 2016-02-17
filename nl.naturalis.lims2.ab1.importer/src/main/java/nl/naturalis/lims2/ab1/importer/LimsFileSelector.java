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

	private String csvPath = "";
	private String fastaPath = "";
	private String fileSelected = "";
	public File fileSelect = null;

	/*
	 * public String loadFile(Frame f, String title, String defDir, String
	 * fileType) { FileDialog fd = new FileDialog(f, title, FileDialog.LOAD);
	 * fd.setFile(fileType); fd.setDirectory(defDir); fd.setLocation(50, 50);
	 * fd.show(true); return fd.getFile(); }
	 */

	public String loadSelectedFile() {
		File fileMap = null;
		try {
			csvPath = limsImporterUtil
					.getPropValues("lims.import.input.cs_dir");
		} catch (IOException e) {
			e.printStackTrace();
		}
		JFileChooser chooser = new JFileChooser(csvPath);
		chooser.setDialogTitle("Select a file");
		chooser.getCurrentDirectory();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"TXT & XLS, CSV Files", "txt", "xls", "csv");
		chooser.setFileFilter(filter);
		int returnVal = chooser.showOpenDialog(chooser);

		switch (returnVal) {
		case JFileChooser.APPROVE_OPTION: {
			logger.info("You chose to open this CSV file: "
					+ chooser.getSelectedFile().getAbsolutePath());
			fileMap = chooser.getCurrentDirectory();
			fileSelect = fileMap.getAbsoluteFile();
			fileSelected = fileMap.getAbsolutePath() + fileMap.separator
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

	private File getSelectedFile() {
		// TODO Auto-generated method stub
		return null;
	}

	public String loadFastaFile(String fileName) throws FileNotFoundException {

		File fileMap = null;
		try {
			fastaPath = limsImporterUtil.getPropValues("fastadirectory");
		} catch (IOException e) {
			e.printStackTrace();
		}
		JFileChooser chooser = new JFileChooser(fastaPath);
		chooser.setSelectedFile(new File(chooser.getCurrentDirectory(),
				fileName + ".fas"));
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Fasta Files", "fas");
		chooser.setFileFilter(filter);

		logger.info("You chose to open this CSV file: "
				+ chooser.getSelectedFile().getAbsolutePath());
		fileMap = chooser.getCurrentDirectory();
		fileSelected = fileMap.getAbsolutePath() + fileMap.separator
				+ chooser.getSelectedFile().getName();

		BufferedReader in = new BufferedReader(new FileReader(fileSelected));
		String line;
		String currentName = "";
		try {
			line = in.readLine();

			if (line.startsWith(">")) {
				currentName = line.substring(1);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return currentName;
	}

	public String readFastaContent(File file, String fastafileName)
			throws FileNotFoundException {
		BufferedReader in = new BufferedReader(new FileReader(file));
		String line;
		try {
			line = in.readLine();
			if (line.startsWith(">")) {
				fastafileName = line.substring(1);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fastafileName;
	}
}
