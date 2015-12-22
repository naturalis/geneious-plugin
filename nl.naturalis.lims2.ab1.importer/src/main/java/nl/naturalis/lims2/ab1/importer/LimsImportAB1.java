/**
 * 
 */
package nl.naturalis.lims2.ab1.importer;

import java.io.File;
import java.io.IOException;
import java.util.List;

import jebl.util.ProgressListener;
import nl.naturalis.lims2.utils.LimsAB1Fields;
import nl.naturalis.lims2.utils.LimsImporterUtil;
import nl.naturalis.lims2.utils.LimsLogger;
import nl.naturalis.lims2.utils.LimsNotes;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentFileImporter;
import com.biomatters.geneious.publicapi.plugin.DocumentImportException;
import com.biomatters.geneious.publicapi.plugin.PluginUtilities;

/*import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;*/

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsImportAB1 extends DocumentFileImporter {

	private LimsAB1Fields limsAB1Fields = new LimsAB1Fields();
	private LimsNotes limsNotes = new LimsNotes();
	private LimsImporterUtil limsImporterUtil = new LimsImporterUtil();
	private AnnotatedPluginDocument document;
	private int count = 0;

	@Override
	public String getFileTypeDescription() {
		return "Naturalis Extract AB1 Filename Importer";
	}

	@Override
	public String[] getPermissibleExtensions() {
		return new String[] { "ab1", "abi" };
	}

	@Override
	public void importDocuments(File file, ImportCallback importCallback,
			ProgressListener progressListener) throws IOException,
			DocumentImportException {

		String logFileName = limsImporterUtil.getLogPath() + File.separator
				+ limsImporterUtil.getLogFilename();

		LimsLogger limsLogger = new LimsLogger(logFileName);

		progressListener.setMessage("Importing sequence data");
		List<AnnotatedPluginDocument> docs = PluginUtilities.importDocuments(
				file, ProgressListener.EMPTY);

		count += docs.size();

		document = importCallback.addDocument(docs.iterator().next());

		if (file.getName() != null) {
			limsAB1Fields.setFieldValuesFromAB1FileName(file.getName());

			limsLogger
					.logMessage("----------------------------S T A R T ---------------------------------");
			limsLogger.logMessage("Start extracting value from file: "
					+ file.getName());

			/* set note for Extract-ID */
			try {
				limsNotes.setImportNotes(document, "ExtractIdCode",
						"Extract ID", "Extract-ID",
						limsAB1Fields.getExtractID());
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			/* set note for PCR Plaat-ID */
			try {
				limsNotes.setImportNotes(document, "PcrPlaatIdCode",
						"PCR plaat ID", "PCR plaat ID",
						limsAB1Fields.getPcrPlaatID());
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			/* set note for Marker */
			try {
				limsNotes.setImportNotes(document, "MarkerCode", "Marker",
						"Marker", limsAB1Fields.getMarker());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		limsLogger.logMessage("Total of document(s) filename extracted: "
				+ count);
		limsLogger
				.logMessage("----------------------------E N D ---------------------------------");
		limsLogger.logMessage("Done with extracting Ab1 file name. ");
		limsLogger.flushCloseFileHandler();
		limsLogger.removeConsoleHandler();
	}

	@Override
	public AutoDetectStatus tentativeAutoDetect(File file,
			String fileContentsStart) {
		return AutoDetectStatus.ACCEPT_FILE;
	}

}
