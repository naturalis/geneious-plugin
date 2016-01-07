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
import com.biomatters.geneious.publicapi.documents.sequence.SequenceDocument;
import com.biomatters.geneious.publicapi.implementations.sequence.DefaultAminoAcidSequence;
import com.biomatters.geneious.publicapi.plugin.Icons;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsAB1DBService extends DatabaseService {

	// LimsAB1Fields limsAB1Fields = new LimsAB1Fields();
	LimsAB1DownLoader limsAB1DownLoader = new LimsAB1DownLoader();

	@Override
	public QueryField[] getSearchFields() {
		return LimsAB1Fields.searchFields;
	}

	@Override
	public void retrieve(Query query, RetrieveCallback retrieveCallback,
			URN[] urns) throws DatabaseServiceException {

		ArrayList<String> fields = new ArrayList<String>();
		fields.add("name");
		fields.add("ExtractID");
		fields.add("PCRPlaatID");
		fields.add("Marker");

		/*
		 * SequenceDocument documents0 = new DefaultNucleotideSequence(
		 * "Reinier Test", "CCYTTAAGAATGGGT");
		 */

		SequenceDocument seq = new DefaultAminoAcidSequence("Reinier Seq",
				fields.toString(), "CCYTTAAGAATGGGT", new Date(), null);

		retrieveCallback.add(seq, null);

		LimsAB1DownLoader documents = new LimsAB1DownLoader(new Date(),
				"Reinier", "CCYTTAAGAATGGGT", null, fields);
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

	protected ImageIcon createImageIcon(String path, String description) {
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	@Override
	public Icons getIcons() {
		ImageIcon icon = createImageIcon("dbicon.jpg", "Downloader Tool");
		return new Icons(icon);
	}

	@Override
	public String getName() {
		return "Naturalis Downloader Tool";
	}

	@Override
	public String getUniqueID() {
		return "AB1Downloader";
	}

}
