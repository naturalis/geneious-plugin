/**
 * <h1>Lims AB1, Fasta Plugin</h1> 
 * <table>
 * <tr>
 * <td>
 * Date 08 august 2016
 * Company Naturalis Biodiversity Center City
 * Leiden Country Netherlands
 * </td>
 * </tr>
 * </table>
 * <p>category Lims Import AB1 Fasta plugin</p>
 */
package nl.naturalis.lims2.ab1.importer;

import com.biomatters.geneious.publicapi.plugin.DocumentAction;
import com.biomatters.geneious.publicapi.plugin.DocumentFileImporter;
import com.biomatters.geneious.publicapi.plugin.DocumentType;
import com.biomatters.geneious.publicapi.plugin.GeneiousPlugin;

/**
 * @author Reinier.Kartowikromo
 * @version: 1.0
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
		return "Import AB1/Fasta files";
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
		return "Naturalis AB1/Fasta file plugin";
	}

	@Override
	public String getVersion() {
		return "0.75";
	}

	public DocumentFileImporter[] getDocumentFileImporters() {
		return new DocumentFileImporter[] { new LimsImportAB1() };
	}

	/**
	 * Get Document Actions
	 * 
	 * @return
	 * */
	public DocumentAction[] getDocumentActions() {
		return new DocumentAction[] { new LimsImportAB1Update(),
				new LimsImportCRS(), new LimsImportBold(),
				new LimsImportSamples() };
		/* Depricated classes */
		/*
		 * new LimsReadDataFromSamples(), new LimsReadDataFromBold(), new
		 * LimsCRSImporter(),
		 */
	}

	@Override
	public DocumentType[] getDocumentTypes() {
		return null;

	}
}
