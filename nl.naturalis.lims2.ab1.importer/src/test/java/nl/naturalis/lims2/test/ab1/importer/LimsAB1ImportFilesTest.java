package nl.naturalis.lims2.test.ab1.importer;

import java.io.File;
import java.io.IOException;
import java.util.List;

import jebl.util.ProgressListener;
import nl.naturalis.lims2.ab1.importer.LimsImportAB1;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentFileImporter.ImportCallback;
import com.biomatters.geneious.publicapi.plugin.DocumentImportException;
import com.biomatters.geneious.publicapi.plugin.PluginUtilities;

@RunWith(Parameterized.class)
public class LimsAB1ImportFilesTest {

	private static LimsImportAB1 limsImportAB1;
	private static File fileTest = new File(
			"C:\\Git\\Data\\AB1 files\\e4010125015_Sil_tri_MJ243_COI-A01_M13F_A01_008.ab1");
	private static ImportCallback importCallbackTest;
	private static ProgressListener progressListenerTest;
	private static AnnotatedPluginDocument document;

	public LimsAB1ImportFilesTest(File file, ImportCallback importCallback,
			ProgressListener progressListener) {
		super();
		LimsAB1ImportFilesTest.fileTest = file;
		LimsAB1ImportFilesTest.importCallbackTest = importCallback;
		LimsAB1ImportFilesTest.progressListenerTest = progressListener;
	}

	@Parameters()
	public static void testImportDocuments() throws IOException,
			DocumentImportException {

		List<AnnotatedPluginDocument> docs = PluginUtilities.importDocuments(
				fileTest, ProgressListener.EMPTY);

		importCallbackTest.addDocument(docs.listIterator().next());

		progressListenerTest.setMessage("Importing sequence data");

		limsImportAB1.importDocuments(fileTest, importCallbackTest,
				progressListenerTest);
	}

}
