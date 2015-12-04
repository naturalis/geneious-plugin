/**
 * 
 */
package nl.naturalis.lims2.bold.importer;


import com.biomatters.geneious.publicapi.plugin.DocumentAction;
import com.biomatters.geneious.publicapi.plugin.GeneiousPlugin;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsReadDataFromBoldPlugin extends GeneiousPlugin {

	@Override
	public String getAuthors() {
		return "Natauralis Reinier.Kartowikromo";
	}

	@Override
	public String getDescription() {
		return "Read data from bold file";
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
		return "Naturalis import from Bold document";
	}

	@Override
	public String getVersion() {
		return "0.1";
	}
	
	public DocumentAction[] getDocumentActions() {
        return new DocumentAction[]{
        		new LimsReadDataFromBold()};
    }

}
