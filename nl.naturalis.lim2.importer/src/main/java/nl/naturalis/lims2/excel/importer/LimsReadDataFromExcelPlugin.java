/**
 * 
 */
package nl.naturalis.lims2.excel.importer;

import com.biomatters.geneious.publicapi.plugin.DocumentAction;
import com.biomatters.geneious.publicapi.plugin.GeneiousPlugin;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsReadDataFromExcelPlugin extends GeneiousPlugin {

	@Override
	public String getAuthors() {
		return "Natauralis Reinier.Kartowikromo";
	}

	@Override
	public String getDescription() {
		return "Read data from excel file";
	}

	@Override
	public String getHelp() {
		return null;
	}

	@Override
	public int getMaximumApiVersion() {
		return 7;
	}

	@Override
	public String getMinimumApiVersion() {
		return "4.0";
	}

	@Override
	public String getName() {
		return "Naturalis import from Excel document";
	}

	@Override
	public String getVersion() {
		return "0.1";
	}

	public DocumentAction[] getDocumentActions() {
		return new DocumentAction[] { new LimsReadDataFromExcel() };
	}
}
