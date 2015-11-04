/**
 * 
 */
package nl.naturalis.lims2.importer;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.sequence.NucleotideSequenceDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentAction;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsReadDataFromExcel extends DocumentAction {

	@Override
	public void actionPerformed(AnnotatedPluginDocument[] arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GeneiousActionOptions getActionOptions() {
		return new GeneiousActionOptions("Read data from Excel").setInMainToolbar(true);
	}

	@Override
	public String getHelp() {
		return null;
	}

	@Override
	public DocumentSelectionSignature[] getSelectionSignatures() {
		return new DocumentSelectionSignature[] {new DocumentSelectionSignature(NucleotideSequenceDocument.class,0,Integer.MAX_VALUE)};
	}

}
