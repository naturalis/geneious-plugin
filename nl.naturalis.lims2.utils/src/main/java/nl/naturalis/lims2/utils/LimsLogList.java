/**
 * 
 */
package nl.naturalis.lims2.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
 * Depricated<br>
 * </td>
 * </tr>
 * </table>
 * 
 * @author Reinier.Kartowikromo
 *
 */
public class LimsLogList {

	private LimsImporterUtil limsImporterUtil = new LimsImporterUtil();
	public List<String> msgUitvalList = new ArrayList<String>();
	public List<String> UitvalList = new ArrayList<String>();
	private String logFileName = "";

	public LimsLogList() {
		msgUitvalList.clear();
		UitvalList.clear();
	}

	private void createLogFile(String fileName, List<String> list) {
		logFileName = limsImporterUtil.getLogPath() + File.separator + fileName
				+ limsImporterUtil.getLogFilename();
		LimsLogger limsLogger = new LimsLogger(logFileName);
		limsLogger.logToFile(logFileName, list.toString());
	}

}
