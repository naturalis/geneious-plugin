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
			Boolean pFileExists, Boolean pExtract, int pVersionnumber) {

		logger.info("Extract ID: " + limsAB1Fields.getExtractID());
		logger.info("PCR plaat ID: " + limsAB1Fields.getPcrPlaatID());
		logger.info("Marker: " + limsAB1Fields.getMarker());
		logger.info("Versienummer: " + pVersionnumber);

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
			setVersionNumber(annotatedPluginDocuments, pCount, pVersionnumber);
		} else if (pFileExists && !pExtract) {
			setVersionNumber(annotatedPluginDocuments, pCount, pVersionnumber);
		} else if (!pFileExists && !pExtract) {
			setVersionNumber(annotatedPluginDocuments, pCount, pVersionnumber);
		}

		/* set note for SequencingStaffCode_FixedValue_Seq */
		try {
			limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
					"SequencingStaffCode_FixedValue_Seq", "Seq-staff (Seq)",
					"Seq-staff (Seq)",
					limsImporterUtil.getPropValues("seqsequencestaff"), pCount);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		/* Set note ConsensusSeqPassCode_Seq */
		try {
			limsNotes.setNoteDropdownFieldToFileName(annotatedPluginDocuments,
					limsNotes.ConsensusSeqPass, "ConsensusSeqPassCode_Seq",
					"Pass (Seq)", "Pass (Seq)",
					limsNotes.ConsensusSeqPass[6] = "not determined", pCount);
		} catch (Exception e) {
			limsFrameProgress.hideFrame();
			throw new RuntimeException(e);
		}

		/* Show processing dialog */
		limsFrameProgress.showProgress("Processing: "
				+ DocumentUtilities.getSelectedDocuments().get(pCount)
						.getName());
		logger.info("Done with adding notes to the document");
	}

	public void extractDocumentFileName(String extractAb1FastaFileName) {
		limsAB1Fields.extractAB1_FastaFileName(extractAb1FastaFileName);
	}

	private void setVersionNumber(
			AnnotatedPluginDocument[] annotatedPluginDocuments, int pCount,
			int pVersionnumber) {
		limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
				"DocumentVersionCode_Seq", "Document version",
				"Document version", Integer.toString(pVersionnumber), pCount);
	}

	/* Get path value from the document */
	public static String getPathFromDocument(int cnt) {
		Object result = null;
		if (DocumentUtilities.getSelectedDocuments().get(cnt)
				.getDocumentNotes(true).getNote("importedFrom") != null) {
			result = DocumentUtilities.getSelectedDocuments().get(cnt)
					.getDocumentNotes(true).getNote("importedFrom")
					.getFieldValue("path");
		}
		return (String) result;
	}

	/* Get the filename from the selected document. */
	public static String getFileNameFromDocument(int cnt) {
		Object result = null;
		if (DocumentUtilities.getSelectedDocuments().get(cnt)
				.getDocumentNotes(true).getNote("importedFrom") != null) {
			result = DocumentUtilities.getSelectedDocuments().get(cnt)
					.getDocumentNotes(true).getNote("importedFrom")
					.getFieldValue("filename");
		}
		return (String) result;
	}

	public static String getVersionNumberFromDocument(int pCnt) {
		Object result = null;
		if (DocumentUtilities.getSelectedDocuments().get(pCnt)
				.getDocumentNotes(true)
				.getNote("DocumentNoteUtilities-Document version") != null) {
			result = DocumentUtilities.getSelectedDocuments().get(pCnt)
					.getDocumentNotes(true)
					.getNote("DocumentNoteUtilities-Document version")
					.getFieldValue("DocumentVersionCode_Seq");
		}
		return (String) result;
	}

}
