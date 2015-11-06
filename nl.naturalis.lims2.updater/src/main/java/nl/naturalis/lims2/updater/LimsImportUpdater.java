/**
 * 
 */
package nl.naturalis.lims2.updater;

import java.util.List;

import nl.naturalis.lims2.importer.LimsNotes;
import nl.naturalis.lims2.utils.LimsImporterUtil;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentField;
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
public class LimsImportUpdater extends DocumentAction {

	static final Logger logger;

	static {
		logger = LoggerFactory.getLogger(LimsImportUpdater.class);
	}
	private static final String KEY_BOS = "BOS";
	final DocumentField documentField = DocumentField.createStringField(
			"Registrationnumber", "Basis of record in the document", KEY_BOS,
			true, true);
	LimsImporterUtil limsImporterUtil = new LimsImporterUtil();
	LimsAB1Fields limsAB1Flieds = new LimsAB1Fields();
	LimsNotes limsNotes = new LimsNotes();

	AnnotatedPluginDocument annotatedPluginDocument;
	SequenceDocument seq;

	@Override
	public void actionPerformed(
			AnnotatedPluginDocument[] annotatedPluginDocuments) {

		logger.info("-----------------------------------------------------------------");
		logger.info("Start");

		List<AnnotatedPluginDocument> docs;
		try {
			docs = DocumentUtilities.getSelectedDocuments();
			for (int cnt = 0; cnt < docs.size(); cnt++) {
				seq = (SequenceDocument) docs.get(cnt).getDocument();
				logger.info("Selected document: " + seq.getName());

				if (seq.getName() != null) {
					setFieldValuesFromAB1FileName(seq.getName());
					logger.info("Extract-ID: " + limsAB1Flieds.getExtractID());
					logger.info("PCR plaat-ID: "
							+ limsAB1Flieds.getPcrPlaatID());
					logger.info("Mark: " + limsAB1Flieds.getMarker());

					/** set note for Extract-ID */
					limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
							"ExtractIdCode", "Extract ID", "Extract-ID",
							limsAB1Flieds.getExtractID(), cnt);

					/** set note for PCR Plaat-ID */
					limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
							"PcrPlaatIdCode", "PCR plaat ID", "PCR plaat ID",
							limsAB1Flieds.getPcrPlaatID(), cnt);

					/** set note for Marker */
					limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
							"MarkerCode", "Marker", "Marker",
							limsAB1Flieds.getMarker(), cnt);
				}
			}
		} catch (DocumentOperationException e) {
			try {
				throw new Exception();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		logger.info("-----------------------------------------------------------------");
		logger.info("Done with reading Ab1 file. ");
	}

	@Override
	public GeneiousActionOptions getActionOptions() {
		return new GeneiousActionOptions("Update Selected document")
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

	private void setFieldValuesFromAB1FileName(String ab1FileName) {
		/* for example: e4010125015_Sil_tri_MJ243_COI-A01_M13F_A01_008.ab1 */
		String[] underscore = StringUtils.split(ab1FileName, "_");
		limsAB1Flieds.setExtractID(underscore[0]);
		limsAB1Flieds.setPcrPlaatID(underscore[3]);
		limsAB1Flieds.setMarker(underscore[4]);

	}
}
