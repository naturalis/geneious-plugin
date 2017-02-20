/**
 * 
 */
package nl.naturalis.lims2.utils;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsNotesSplitName {

	private static final Logger logger = LoggerFactory
			.getLogger(LimsNotesSplitName.class);
	private LimsAB1Fields limsAB1Fields = new LimsAB1Fields();
	private LimsNotes limsNotes = new LimsNotes();
	private LimsImporterUtil limsImporterUtil = new LimsImporterUtil();
	private LimsFrameProgress limsFrameProgress = new LimsFrameProgress();

	public void enrichSplitDocumentsWithNotes(
			AnnotatedPluginDocument[] annotatedPluginDocuments, int pCount,
			Boolean pFileExists, Boolean pExtract, int pVersienummer) {

		logger.info("Extract ID: " + limsAB1Fields.getExtractID());
		logger.info("PCR plaat ID: " + limsAB1Fields.getPcrPlaatID());
		logger.info("Marker: " + limsAB1Fields.getMarker());
		logger.info("Versienummer: " + pVersienummer);

		/* set note for Extract-ID */
		limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
				"ExtractIDCode_Seq", "Extract ID (Seq)", "Extract ID (Seq)",
				limsAB1Fields.getExtractID(), pCount);

		/* set note for PCR Plate ID */
		limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
				"PCRplateIDCode_Seq", "PCR plate ID (Seq)",
				"PCR plate ID (Seq)", limsAB1Fields.getPcrPlaatID(), pCount);

		/* set note for Marker */
		limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
				"MarkerCode_Seq", "Marker (Seq)", "Marker (Seq)",
				limsAB1Fields.getMarker(), pCount);

		/* set note for Document version */
		if (pFileExists && pExtract) {
			limsNotes
					.setNoteToAB1FileName(annotatedPluginDocuments,
							"DocumentVersionCode_Seq", "Document version",
							"Document version",
							Integer.toString(pVersienummer), pCount);
		} else if (pFileExists && !pExtract) {
			limsNotes
					.setNoteToAB1FileName(annotatedPluginDocuments,
							"DocumentVersionCode_Seq", "Document version",
							"Document version",
							Integer.toString(pVersienummer), pCount);
		}
		/* set note for SequencingStaffCode_FixedValue_Seq */
		try {
			limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
					"SequencingStaffCode_FixedValue_Seq", "Seq-staff (Seq)",
					"Seq-staff (Seq)",
					limsImporterUtil.getPropValues("seqsequencestaff"), pCount);
		} catch (IOException e) {
			e.printStackTrace();
		}

		/* Set note ConsensusSeqPassCode_Seq */
		limsNotes.setNoteDropdownFieldToFileName(annotatedPluginDocuments,
				limsNotes.ConsensusSeqPass, "ConsensusSeqPassCode_Seq",
				"Pass (Seq)", "Pass (Seq)", null, pCount);

		/* Show processing dialog */
		limsFrameProgress.showProgress("Processing: "
				+ DocumentUtilities.getSelectedDocuments().get(pCount)
						.getName());
		logger.info("Done with adding notes to the document");
	}

	public void extractDocumentFileName(String extractAb1FastaFileName) {
		limsAB1Fields.extractAB1_FastaFileName(extractAb1FastaFileName);
	}

	/*
	 * public void setFastaDocumentFileName(String pExtractFastaFileName) {
	 * limsAB1Fields.extractAB1_FastaFileName(pExtractFastaFileName); }
	 * 
	 * public void extractContigDocumentFileName(String pExtractFastaFileName) {
	 * limsAB1Fields.extractAB1_FastaFileName(pExtractFastaFileName); }
	 */

}
