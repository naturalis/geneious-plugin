/**
 * 
 */
package nl.naturalis.lims2.ab1.importer;

import java.io.File;
import java.util.List;

import nl.naturalis.lims2.utils.LimsAB1Fields;
import nl.naturalis.lims2.utils.LimsImporterUtil;
import nl.naturalis.lims2.utils.LimsLogger;
import nl.naturalis.lims2.utils.LimsNotes;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.documents.sequence.NucleotideSequenceDocument;
import com.biomatters.geneious.publicapi.documents.sequence.SequenceDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentAction;
import com.biomatters.geneious.publicapi.plugin.DocumentOperationException;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsImportAB1Update extends DocumentAction {

	private LimsImporterUtil limsImporterUtil = new LimsImporterUtil();

	SequenceDocument seq;
	LimsNotes limsNotes = new LimsNotes();
	private LimsAB1Fields limsAB1Fields = new LimsAB1Fields();
	private List<AnnotatedPluginDocument> docs;

	@Override
	public void actionPerformed(
			AnnotatedPluginDocument[] annotatedPluginDocuments) {

		String logFileName = limsImporterUtil.getLogPath() + File.separator
				+ limsImporterUtil.getLogFilename();

		LimsLogger limsLogger = new LimsLogger(logFileName);
		limsLogger
				.logMessage("----------------------------S T A R T -------------------------------");
		try {
			docs = DocumentUtilities.getSelectedDocuments();
			for (int cnt = 0; cnt < docs.size(); cnt++) {
				seq = (SequenceDocument) docs.get(cnt).getDocument();

				limsLogger.logMessage("Start extracting value from file: "
						+ seq.getName());

				if (seq.getName() != null) {
					limsAB1Fields.setFieldValuesFromAB1FileName(seq.getName());

					limsLogger.logMessage("Extract-ID: "
							+ limsAB1Fields.getExtractID());
					limsLogger.logMessage("PCR plaat-ID: "
							+ limsAB1Fields.getPcrPlaatID());
					limsLogger.logMessage("Mark: " + limsAB1Fields.getMarker());

					/** set note for Extract-ID */
					limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
							"ExtractIdCode", "Extract ID", "Extract-ID",
							limsAB1Fields.getExtractID(), cnt);

					/** set note for PCR Plaat-ID */
					limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
							"PcrPlaatIdCode", "PCR plaat ID", "PCR plaat ID",
							limsAB1Fields.getPcrPlaatID(), cnt);

					/** set note for Marker */
					limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
							"MarkerCode", "Marker", "Marker",
							limsAB1Fields.getMarker(), cnt);
				}
			}
		} catch (DocumentOperationException e) {
			try {
				throw new Exception();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

		limsLogger.logMessage("Total of document(s) updated: " + docs.size());
		limsLogger
				.logMessage("------------------------- E N D--------------------------------------");
		limsLogger.logMessage("Done with extracting Ab1 file name. ");
		limsLogger.flushCloseFileHandler();
		limsLogger.removeConsoleHandler();

	}

	@Override
	public GeneiousActionOptions getActionOptions() {
		return new GeneiousActionOptions("Read AB1 file")
				.setInMainToolbar(true);
	}

	@Override
	public String getHelp() {
		return null;
	}

	@Override
	public DocumentSelectionSignature[] getSelectionSignatures() {
		return new DocumentSelectionSignature[] { new DocumentSelectionSignature(
				NucleotideSequenceDocument.class, 0, Integer.MAX_VALUE) };
	}

}
