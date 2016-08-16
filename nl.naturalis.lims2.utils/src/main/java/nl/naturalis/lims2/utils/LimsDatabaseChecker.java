package nl.naturalis.lims2.utils;

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

public class LimsDatabaseChecker {

	public boolean checkDBName() {

		LimsImporterUtil util = new LimsImporterUtil();
		String result = util.getDatabasePropValues("databasename");
		Set<String> names = getActiveDatabases();
		if (names.size() == 1 && names.iterator().next().equals(result))
			return true;
		names.remove(result);
		String msg = "Currently Geneious is connected to multiple databases.\n"
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

}
