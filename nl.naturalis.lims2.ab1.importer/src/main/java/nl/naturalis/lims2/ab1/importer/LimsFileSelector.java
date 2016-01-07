/**
 * 
 */
package nl.naturalis.lims2.ab1.importer;

import java.io.File;
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

	/*
	 * String logFileName = limsImporterUtil.getLogPath() + File.separator +
	 * limsImporterUtil.getLogFilename();
	 * 
	 * LimsLogger limsLogger = new LimsLogger(logFileName);
	 */

	String csvPath = "";

	/*
	 * public String loadFile(Frame f, String title, String defDir, String
	 * fileType) { FileDialog fd = new FileDialog(f, title, FileDialog.LOAD);
	 * fd.setFile(fileType); fd.setDirectory(defDir); fd.setLocation(50, 50);
	 * fd.show(true); return fd.getFile(); }
	 */

	public String loadSelectedFile() {
		String fileSelected = "";
		File fileMap = null;
		try {
			csvPath = limsImporterUtil.getPropValues();
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
			logger.info("You chose to open this file: "
					+ chooser.getSelectedFile().getName());
			fileMap = chooser.getCurrentDirectory();
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
}
