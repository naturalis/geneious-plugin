/**
 * 
 */
package nl.naturalis.lims2.bold.importer;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.PluginDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentAction;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;

import nl.naturalis.lims2.importer.LimsNotes;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsReadDataFromBold extends DocumentAction  {
	
	static final Logger logger = LoggerFactory.getLogger(LimsReadDataFromBold.class);
	LimsNotes limsNotes = new LimsNotes();

	@Override
	public void actionPerformed(AnnotatedPluginDocument[] arg0) {
		logger.info("-----------------------------------------------------------------");
		logger.info("Start Bold import");
		
		
		logger.info("-----------------------------------------------------------------");
		logger.info("Done with reading bold file. ");
		
	}

	@Override
	public GeneiousActionOptions getActionOptions() {
		return new GeneiousActionOptions("Read data from Bold").setInMainToolbar(true);
	}

	@Override
	public String getHelp() {
		return null;
	}

	@Override
	public DocumentSelectionSignature[] getSelectionSignatures() {
		return new DocumentSelectionSignature[] {new DocumentSelectionSignature(PluginDocument.class,0,Integer.MAX_VALUE)};
	}

}
