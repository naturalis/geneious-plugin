/**
 * 
 */
package nl.naturalis.lims2.downloader;

import java.util.ArrayList;
import java.util.Date;

import javax.swing.ImageIcon;

import com.biomatters.geneious.publicapi.databaseservice.DatabaseService;
import com.biomatters.geneious.publicapi.databaseservice.DatabaseServiceException;
import com.biomatters.geneious.publicapi.databaseservice.Query;
import com.biomatters.geneious.publicapi.databaseservice.QueryField;
import com.biomatters.geneious.publicapi.databaseservice.RetrieveCallback;
import com.biomatters.geneious.publicapi.documents.URN;
import com.biomatters.geneious.publicapi.plugin.Icons;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsAB1DBService extends DatabaseService {

	LimsAB1Fields limsAB1Fields;
	LimsAB1DownLoader limsAB1DownLoader;

	@Override
	public QueryField[] getSearchFields() {
		return limsAB1Fields.searchFields;
	}

	@Override
	public void retrieve(Query query, RetrieveCallback retrieveCallback,
			URN[] urns) throws DatabaseServiceException {

		ArrayList<String> fields = new ArrayList<String>();
		fields.add("Test");
		fields.add("Test1");
		fields.add("Test2");
		fields.add("Test3");
		fields.add("Test4");
		LimsAB1DownLoader documents = new LimsAB1DownLoader(new Date(), "",
				"Document 1", new URN("AB1Downloader", "Test.ab1", "11112015"),
				fields);
		retrieveCallback.add(documents, null);
	}

	@Override
	public String getDescription() {
		return "Download AB1 files from Naturalis Geneious Lims2.";
	}

	@Override
	public String getHelp() {
		return "Help is in construction";
	}

	@Override
	public Icons getIcons() {
		ImageIcon icon = new ImageIcon("dbicon.jpg");
		return new Icons(icon);
	}

	@Override
	public String getName() {
		return "AB1 Downloader Tool";
	}

	@Override
	public String getUniqueID() {
		return "AB1Downloader";
	}

}
