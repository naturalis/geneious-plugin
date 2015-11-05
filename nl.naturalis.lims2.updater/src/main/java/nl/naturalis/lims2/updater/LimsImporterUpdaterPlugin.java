/**
 * 
 */
package nl.naturalis.lims2.updater;

import com.biomatters.geneious.publicapi.plugin.DocumentAction;
import com.biomatters.geneious.publicapi.plugin.GeneiousPlugin;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsImporterUpdaterPlugin extends GeneiousPlugin{

	@Override
	public String getAuthors() {
		return "Natauralis Reinier.Kartowikromo";
	}

	@Override
	public String getDescription() {
		return "Handle create fields within a document";
	}

	@Override
	public String getHelp() {
		// TODO Auto-generated method stub
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
		return "Naturalis create fields in a document";
	}

	@Override
	public String getVersion() {
		return "0.1";
	}
	
	
	
	public DocumentAction[] getDocumentActions() {
        return new DocumentAction[]{
        		new LimsImportUpdater()};
    }
}
