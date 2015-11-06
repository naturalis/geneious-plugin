/**
 * 
 */
package nl.naturalis.lims2.ab1.importer;

import com.biomatters.geneious.publicapi.plugin.DocumentFileImporter;
import com.biomatters.geneious.publicapi.plugin.GeneiousPlugin;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsImportAB1Plugin extends GeneiousPlugin {

	@Override
	public String getAuthors() {
		return "Natauralis Reinier.Kartowikromo";
	}

	@Override
	public String getDescription() {
		return "Import AB1 files";
	}

	@Override
	public String getHelp() {
		return null;
	}

	@Override
	public int getMaximumApiVersion() {
		return 8;
	}

	@Override
	public String getMinimumApiVersion() {
		return "7.0";
	}

	@Override
	public String getName() {
		return "Naturalis AB1 file plugin";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	public DocumentFileImporter[] getDocumentFileImporters() {
		return new DocumentFileImporter[] { new LimsImportAB1() };
	}

}
