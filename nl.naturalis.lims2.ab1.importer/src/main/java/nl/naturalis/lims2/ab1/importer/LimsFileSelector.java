/**
 * 
 */
package nl.naturalis.lims2.ab1.importer;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import nl.naturalis.lims2.utils.LimsImporterUtil;
import nl.naturalis.lims2.utils.LimsLogger;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsFileSelector {

	LimsImporterUtil limsImporterUtil = new LimsImporterUtil();

	String logFileName = limsImporterUtil.getLogPath() + File.separator
			+ limsImporterUtil.getLogFilename();

	LimsLogger limsLogger = new LimsLogger(logFileName);

	String csvPath = "";

	/*
	 * public String loadFile(Frame f, String title, String defDir, String
	 * fileType) { FileDialog fd = new FileDialog(f, title, FileDialog.LOAD);
	 * fd.setFile(fileType); fd.setDirectory(defDir); fd.setLocation(50, 50);
	 * fd.show(true); return fd.getFile(); }
	 */

	public String loadSelectedFile() {
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
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			limsLogger.logMessage("You chose to open this file: "
					+ chooser.getSelectedFile().getName());
		}
		return chooser.getSelectedFile().getName();
	}
}
