package nl.naturalis.lims2.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.biomatters.geneious.publicapi.components.Dialogs;
import com.biomatters.geneious.publicapi.plugin.PluginUtilities;

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
 * Verify how many schema Database are active.<br>
 * There is a property in the lims-import.properties file
 * "Example: databasename=geneioustest" Only the schema that is mention in the
 * property must be active.<br>
 * If one or more schema is/are active,<br>
 * a message will be shown to disconnect the other schema. <br>
 * </td>
 * </tr>
 * </table>
 * 
 * @author Reinier.Kartowikromo
 * @version: 1.0
 */

public class LimsDatabaseChecker {

	private LimsImporterUtil limsImporterUtil = new LimsImporterUtil();

	/**
	 * Get the message that will be shown
	 * */
	public String msg = "Geneious will (unfortunately) shutdown and immediately restart after you have clicked the OK button. "
			+ "\n \n"
			+ "Make sure that Geneious is only connected to the database server"
			+ "\n"
			+ " with database name: ["
			+ limsImporterUtil.getDatabasePropValues("databasename")
			+ "]"
			+ "\n"
			+ "when trying to import ab1 or fasta files with the All Naturalis Files plugin.";

	/**
	 * Check how many schema database is/are active.
	 * 
	 * @return Return the true or false with a message
	 * */
	public boolean checkDBName() {
		LimsImporterUtil util = new LimsImporterUtil();
		String result = util.getDatabasePropValues("databasename");
		Set<String> names = getActiveDatabases();
		if (names.size() == 1 && names.iterator().next().equals(result))
			return true;
		names.remove(result);
		String msg = "Currently Geneious is connected to an unspecified database, or to multiple databases.\n"
				+ "This plugin may only be used on the database server with "
				+ "database name: "
				+ result
				+ "\n \n"
				+ "In order to avoid possible data changes on other databases "
				+ "you have to close the connection(s) with the following database(s): "
				+ names.toString();
		Dialogs.showMessageDialog(msg);
		return false;
	}

	/**
	 * Get database name
	 * 
	 * @return
	 * */
	private static Set<String> getActiveDatabases() {
		List<String> lstdb = new ArrayList<String>();

		String output = "";

		ListIterator<?> itr = PluginUtilities.getWritableDatabaseServiceRoots()
				.listIterator();

		while (itr.hasNext()) {
			String dbsvc = itr.next().toString();
			if (dbsvc.contains("geneious")) {
				String st[] = dbsvc.split("name=");
				Map<String, Integer> mp = new TreeMap<String, Integer>();
				for (int i = 0; i < st.length; i++) {
					Integer count = mp.get(st[i]);
					if (count == null) {
						count = 0;
					}
					mp.put(st[i], ++count);
					String strGeneious = st[i];

					if (strGeneious.contains("geneious")) {
						int indexEnd = strGeneious.indexOf(",");
						output = strGeneious.substring(0, indexEnd);
						lstdb.add(output);
					}
				}
			}
		}

		Iterator<String> lstitr = lstdb.listIterator();
		Set<String> names = new HashSet<>(lstdb.size());
		while (lstitr.hasNext()) {
			names.add(lstitr.next().toString());
		}
		return names; // databaseName[0];
	}

	/**
	 * After the message is shown that one or more schema database is/are
	 * active, Geneious will be restarted.
	 * */
	public void restartGeneious() {
		File f2 = new File("geneious.bat");
		String batchPath = f2.getAbsolutePath();
		String path = "cmd /c start " + batchPath;
		try {
			Process rn = Runtime.getRuntime().exec(path);
			try {
				Runtime.getRuntime().exec("taskkill /f /im cmd.exe");
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.exit(0);
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
