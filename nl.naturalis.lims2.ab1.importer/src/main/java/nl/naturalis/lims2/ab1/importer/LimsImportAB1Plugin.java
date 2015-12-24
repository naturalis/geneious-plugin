/**
 * 
 */
package nl.naturalis.lims2.ab1.importer;

import java.io.File;

import com.biomatters.geneious.publicapi.plugin.DocumentAction;
import com.biomatters.geneious.publicapi.plugin.DocumentFileImporter;
import com.biomatters.geneious.publicapi.plugin.DocumentOperation;
import com.biomatters.geneious.publicapi.plugin.GeneiousPlugin;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsImportAB1Plugin extends GeneiousPlugin {

	static final String HELP = "Naturalis imported ab1 file with Chromatogram and DNA sequence(s)";

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
		return "0.1";
	}

	/*
	 * public GeneiousService[] getServices() { URL resource =
	 * ExampleGeneiousService.class.getResource("/sampledb.fasta"); if(resource
	 * == null) { throw new
	 * IllegalStateException("Could not find sampledb.fasta"); }
	 * 
	 * File fastaFile; try { fastaFile = new File(resource.toURI()); } catch
	 * (URISyntaxException e) { throw new
	 * IllegalStateException("Could not load sampledb.fasta: " + e.getMessage(),
	 * e); }
	 * 
	 * if(!fastaFile.exists()) { throw new
	 * IllegalStateException("sampledb.fasta is missing"); } return new
	 * GeneiousService[]{ new ExampleGeneiousService(fastaFile) }; }
	 */

	public DocumentFileImporter[] getDocumentFileImporters() {
		File ab1File = null;
		return new DocumentFileImporter[] { new LimsImportAB1(ab1File) };
	}

	public DocumentAction[] getDocumentActions() {
		return new DocumentAction[] { new LimsImportAB1Update(),
				new LimsReadDataFromExcel(), new LimsReadDataFromBold() };
	}

	public DocumentOperation[] getDocumentOperations() {
		return new DocumentOperation[] { new LimsDummySequence() };
	}

}
