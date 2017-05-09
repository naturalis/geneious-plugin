package nl.naturalis.lims2.ab1.importer;

import com.biomatters.geneious.publicapi.plugin.TestGeneious;

public class TestImportAll {

	public static void initialize() {
		TestGeneious testGeneious = null;
		testGeneious.initialize();
		testGeneious.initializePlugins("All Naturalis Files");
	}

}
