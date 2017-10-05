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

import nl.naturalis.lims2.utils.LimsSamplesFields;

import com.biomatters.geneious.publicapi.plugin.DocumentAction;
import com.biomatters.geneious.publicapi.plugin.DocumentFileImporter;
import com.biomatters.geneious.publicapi.plugin.DocumentType;
import com.biomatters.geneious.publicapi.plugin.GeneiousPlugin;

/**
 * <table summary="Show Plugins and version">
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
 * A Class to create the plugins for Naturalis Genious.<br>
 * Classes: - LimsImportAB1Update "Plugin Split name" - LimsImportBold
 * "Plugin Bold" - LimsImportCRS "Plugin CRS" - LimsImporSamples
 * "Plugin Samples"</td>
 * </tr>
 * </table>
 * 
 * @author Reinier.Kartowikromo
 * @version: 1.0
 * 
 */
public class LimsImportAB1Plugin extends GeneiousPlugin {

	static final String HELP = "Naturalis imported ab1 file with Chromatogram and DNA sequence(s)";

	/**
	 * Get the author name
	 * 
	 * @return Return author name
	 * @see LimsImportAB1Plugin
	 * */
	@Override
	public String getAuthors() {
		return "Naturalis Reinier.Kartowikromo";
	}

	/**
	 * Get the description of the plugin
	 * 
	 * @return Return Description value
	 * @see LimsImportAB1Plugin
	 * */
	@Override
	public String getDescription() {
		return "Import AB1/Fasta files";
	}

	/**
	 * Get Help documentation
	 * 
	 * @return Help documentation
	 * @see LimsImportAB1Plugin
	 * */
	@Override
	public String getHelp() {
		return HELP;
	}

	/**
	 * Get maximum Api version
	 * 
	 * @return Return the maximun version of the API
	 * @see LimsImportAB1Plugin
	 * */
	@Override
	public int getMaximumApiVersion() {
		return 4;
	}

	/**
	 * Get the minimum api version
	 * 
	 * @return Return the minimum version of the API
	 * @see LimsImportAB1Plugin
	 * */
	@Override
	public String getMinimumApiVersion() {
		return "4.1";
	}

	/**
	 * Get the name of the plugin
	 * 
	 * @return Return the name of plugins
	 * @see LimsImportAB1Plugin
	 * */
	@Override
	public String getName() {
		return "Naturalis AB1/Fasta file plugin";
	}

	/**
	 * Get the plugin version
	 * 
	 * @return Return the version of the plugins
	 * @see LimsImportAB1Plugin
	 * */
	@Override
	public String getVersion() {
		return "1.0.17";
	}

	/**
	 * Create Plugin "All Naturalis files" to import AB1 and Fasta files
	 * 
	 * @return Return "All naturalis files" in the dialog screen tos elect a Csv
	 *         file
	 * @see LimsImportAB1Plugin
	 * */
	public DocumentFileImporter[] getDocumentFileImporters() {
		return new DocumentFileImporter[] { new LimsImportAB1() };
	}

	/**
	 * Create plugins for "Samples, CRS, BOld and Split name"
	 * 
	 * @return Add the plugins to the menubar
	 * @see LimsImportAB1Plugin
	 * */
	public DocumentAction[] getDocumentActions() {
		return new DocumentAction[] { new LimsSplitName(), new LimsImportCRS(),
				new LimsImportBold(), new LimsImportSamples() };
	}

	@SuppressWarnings("rawtypes")
	@Override
	public DocumentType[] getDocumentTypes() {
		return new DocumentType[] { new DocumentType<LimsSamplesFields>(
				"Samples document", LimsSamplesFields.class, null) };
	}
}