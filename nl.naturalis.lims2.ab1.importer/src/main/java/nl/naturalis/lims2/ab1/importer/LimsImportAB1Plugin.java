/**
 * 
 */
package nl.naturalis.lims2.ab1.importer;

import com.biomatters.geneious.publicapi.plugin.DocumentAction;
import com.biomatters.geneious.publicapi.plugin.DocumentFileImporter;
import com.biomatters.geneious.publicapi.plugin.DocumentType;
import com.biomatters.geneious.publicapi.plugin.GeneiousPlugin;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsImportAB1Plugin extends GeneiousPlugin {

	static final String HELP = "Naturalis imported ab1 file with Chromatogram and DNA sequence(s)";

	@Override
	public String getAuthors() {
		return "Naturalis Reinier.Kartowikromo";
	}

	@Override
	public String getDescription() {
		return "Import AB1 files";
	}

	@Override
	public String getHelp() {
		return HELP;
	}

	@Override
	public int getMaximumApiVersion() {
		return 4;
	}

	@Override
	public String getMinimumApiVersion() {
		return "4.1";
	}

	@Override
	public String getName() {
		return "Naturalis AB1 file plugin";
	}

	@Override
	public String getVersion() {
		return "0.50";
	}

	public DocumentFileImporter[] getDocumentFileImporters() {
		return new DocumentFileImporter[] { new LimsImportAB1() };
	}

	public DocumentAction[] getDocumentActions() {
		return new DocumentAction[] { new LimsImportAB1Update(),
				new LimsReadDataFromSamples(), new LimsReadDataFromBold(),
				new LimsCRSImporter() };
	}

	@Override
	public DocumentType[] getDocumentTypes() {
		return new DocumentType[] { new DocumentType<LimsSamplesFields>(
				"Samples document", LimsSamplesFields.class, null),
		// new DocumentType<LimsReadDataFromBold>("Bold document",
		// LimsReadDataFromBold.class, null),
		// new DocumentType<LimsCRSImporter>("CRS Document",
		// LimsCRSImporter.class, null)
		};
	}

	// public DocumentOperation[] getDocumentOperations() {
	// return new DocumentOperation[] { new LimsDummySequence() };
	// }

}
