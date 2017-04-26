/**
 * 
 */
package nl.naturalis.lims2.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument.DocumentNotes;
import com.biomatters.geneious.publicapi.documents.Constraint;
import com.biomatters.geneious.publicapi.documents.DocumentNote;
import com.biomatters.geneious.publicapi.documents.DocumentNoteField;
import com.biomatters.geneious.publicapi.documents.DocumentNoteType;
import com.biomatters.geneious.publicapi.documents.DocumentNoteUtilities;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsImportNotes {

	private static final Logger logger = LoggerFactory
			.getLogger(LimsImportNotes.class);
	private String fieldExtractIDSeq;
	private String noteTypeCodeExtractIDSeq;
	private String descriptionExtractIDSeq;
	private static String noteTextExtractIDSeq = "Extract ID (Seq)";

	private String fieldPCRPlate;
	private String noteTypeCodePCRPlate;
	private String descriptionPCRPlate;
	private static String noteTextPCRPlate = "PCR plate ID (Seq)";

	private String fieldMarker;
	private String noteTypeCodeMarker;
	private String descriptionMarker;
	private static String noteTextMarker = "Marker (Seq)";

	private String fieldDocversion;
	private String noteTypeCodeDocversion;
	private String descriptionDocversion;
	private static String noteTextDocversion = "Document version";

	private String fieldSeqStaff;
	private String noteTypeCodeSeqStaff;
	private String descriptionSeqStaff;
	private static String noteTextSeqStaff = "Seq-staff (Seq)";

	/* ConsensusSeqPassCode */
	private String fieldConsensus;
	private String noteTypeCodeConsensus;
	private String descriptionConsensus;
	private static String noteTextConsensus = "Pass (Seq)";

	public String[] ConsensusSeqPass = { "OK", "medium", "low",
			"contamination", "endo-contamination", "exo-contamination",
			"not determined" };

	/**
	 * Set notes for AB1 and Dummy document(s). Used in Plugin:
	 * "All Naturalis files"
	 * 
	 * Used in LimsImportAB1 and LimsDummySeq Class
	 * setFastaFilesNotes(AnnotatedPluginDocument documentAnnotated, String
	 * fileName) createDummySampleSequence(String filename, String extractID,
	 * String projectPlaatnummer, String extractPlaatnummer, String taxonName,
	 * String registrationNumber, String plaatPositie, String extractMethod)
	 * 
	 * @param document
	 *            Document
	 * @param fileName
	 *            Documentname of the csv file
	 * @param extractID
	 *            extractID of the note
	 * @param pcrPlate
	 *            The pcrPlate field value
	 * @param markerCode
	 *            The markerCode field value
	 * @param versionNumber
	 *            The versionNumber field value
	 * @param seqStaff
	 *            The seqStaff field value
	 * */
	public void setImportNotes(AnnotatedPluginDocument document,
			String fileName, String extractID, String pcrPlate,
			String markerCode, int versionNumber, String seqStaff) {

		logger.info("----------------------------S T A R T ---------------------------------");
		logger.info("Start extracting value from file: " + fileName);

		setFieldAndDescriptionValues();
		/* ================================================================== */

		ArrayList<DocumentNoteField> listNotes = new ArrayList<DocumentNoteField>();

		/* ================================================================ */

		listNotes.add(DocumentNoteField.createTextNoteField(
				noteTextExtractIDSeq, this.descriptionExtractIDSeq,
				this.fieldExtractIDSeq, Collections.<Constraint> emptyList(),
				false));

		/* PCR plate */
		listNotes.add(DocumentNoteField.createTextNoteField(noteTextPCRPlate,
				this.descriptionPCRPlate, this.fieldPCRPlate,
				Collections.<Constraint> emptyList(), false));

		/* MARKER */
		listNotes.add(DocumentNoteField.createTextNoteField(noteTextMarker,
				this.descriptionMarker, this.fieldMarker,
				Collections.<Constraint> emptyList(), false));

		/* Document Version */
		listNotes.add(DocumentNoteField.createTextNoteField(noteTextDocversion,
				this.descriptionDocversion, this.fieldDocversion,
				Collections.<Constraint> emptyList(), false));

		/* Seq-staff (Seq) */
		listNotes.add(DocumentNoteField.createTextNoteField(noteTextSeqStaff,
				this.descriptionSeqStaff, this.fieldSeqStaff,
				Collections.<Constraint> emptyList(), false));

		/* ConsensusSeqPassCode */
		listNotes.add(DocumentNoteField.createEnumeratedNoteField(
				ConsensusSeqPass, noteTextConsensus, this.descriptionConsensus,
				this.fieldConsensus, false));

		/* =============================================================== */

		/* Check if note type exists */
		/* Extract ID (Seq) */
		this.noteTypeCodeExtractIDSeq = "DocumentNoteUtilities-"
				+ noteTextExtractIDSeq;
		DocumentNoteType documentNoteTypeExtractSeq = DocumentNoteUtilities
				.getNoteType(this.noteTypeCodeExtractIDSeq);

		/* PCR plate */
		this.noteTypeCodePCRPlate = "DocumentNoteUtilities-" + noteTextPCRPlate;
		DocumentNoteType documentNoteTypePCR = DocumentNoteUtilities
				.getNoteType(this.noteTypeCodePCRPlate);

		/* MARKER */
		this.noteTypeCodeMarker = "DocumentNoteUtilities-" + noteTextMarker;
		DocumentNoteType documentNoteTypeMarker = DocumentNoteUtilities
				.getNoteType(this.noteTypeCodeMarker);

		/* Document Version */
		this.noteTypeCodeDocversion = "DocumentNoteUtilities-"
				+ noteTextDocversion;
		DocumentNoteType documentNoteTypeDocversion = DocumentNoteUtilities
				.getNoteType(this.noteTypeCodeDocversion);

		/* Seq-staff (Seq) */
		this.noteTypeCodeSeqStaff = "DocumentNoteUtilities-" + noteTextSeqStaff;
		DocumentNoteType documentNoteTypeSeqStaff = DocumentNoteUtilities
				.getNoteType(this.noteTypeCodeSeqStaff);

		/* ConsensusSeqPassCode */
		this.noteTypeCodeConsensus = "DocumentNoteUtilities-"
				+ noteTextConsensus;
		DocumentNoteType documentNoteTypeConsensus = DocumentNoteUtilities
				.getNoteType(this.noteTypeCodeConsensus);
		/* =============================================================== */

		/* Extract-ID note */
		if (documentNoteTypeExtractSeq == null) {
			documentNoteTypeExtractSeq = DocumentNoteUtilities
					.createNewNoteType(noteTextExtractIDSeq,
							this.noteTypeCodeExtractIDSeq,
							this.descriptionExtractIDSeq, listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypeExtractSeq);
			logger.info("NoteType " + noteTextExtractIDSeq
					+ " created succesful");
		}

		/* PCR plate */
		if (documentNoteTypePCR == null) {
			documentNoteTypePCR = DocumentNoteUtilities.createNewNoteType(
					noteTextPCRPlate, this.noteTypeCodePCRPlate,
					this.descriptionPCRPlate, listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypePCR);
			logger.info("NoteType " + noteTextPCRPlate + " created succesful");
		}

		/* MARKER */
		if (documentNoteTypeMarker == null) {
			documentNoteTypeMarker = DocumentNoteUtilities.createNewNoteType(
					noteTextMarker, this.noteTypeCodeMarker,
					this.descriptionMarker, listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypeMarker);
			logger.info("NoteType " + noteTextMarker + " created succesful");
		}

		/* Document Version */
		if (documentNoteTypeDocversion == null) {
			documentNoteTypeDocversion = DocumentNoteUtilities
					.createNewNoteType(noteTextDocversion,
							this.noteTypeCodeDocversion,
							this.descriptionDocversion, listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypeDocversion);
			logger.info("NoteType " + noteTextDocversion + " created succesful");
		}

		/* Seq-staff (Seq) */
		if (documentNoteTypeSeqStaff == null) {
			documentNoteTypeSeqStaff = DocumentNoteUtilities.createNewNoteType(
					noteTextSeqStaff, this.noteTypeCodeSeqStaff,
					this.descriptionSeqStaff, listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypeSeqStaff);
			logger.info("NoteType " + noteTextSeqStaff + " created succesful");
		}

		/* ConsensusSeqPassCode */
		if (documentNoteTypeConsensus == null) {
			documentNoteTypeConsensus = DocumentNoteUtilities
					.createNewNoteType(noteTextConsensus,
							this.noteTypeCodeConsensus,
							this.descriptionConsensus, listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypeConsensus);
			logger.info("NoteType " + noteTextConsensus + " created succesful");
		}

		/* ===================================================================== */

		/* Create note for Extract-ID */
		DocumentNote documentNoteExtractSeq = documentNoteTypeExtractSeq
				.createDocumentNote();
		documentNoteExtractSeq.setFieldValue(fieldExtractIDSeq, extractID);

		/* PCR plate */
		DocumentNote documentNotePCR = documentNoteTypePCR.createDocumentNote();
		documentNotePCR.setFieldValue(fieldPCRPlate, pcrPlate);

		/* MARKER */
		DocumentNote documentNoteMarker = documentNoteTypeMarker
				.createDocumentNote();
		documentNoteMarker.setFieldValue(fieldMarker, markerCode);

		/* Document Version */
		DocumentNote documentNoteDocVersion = documentNoteTypeDocversion
				.createDocumentNote();
		documentNoteDocVersion.setFieldValue(fieldDocversion,
				String.valueOf(versionNumber));

		/* Seq-staff (Seq) */
		DocumentNote documentNoteSeqStaff = documentNoteTypeSeqStaff
				.createDocumentNote();
		try {
			LimsImporterUtil limsImporterUtil = new LimsImporterUtil();
			documentNoteSeqStaff.setFieldValue(fieldSeqStaff,
					limsImporterUtil.getPropValues("seqsequencestaff"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		/* ConsensusSeqPassCode */
		DocumentNote documentNoteConsensus = documentNoteTypeConsensus
				.createDocumentNote();
		documentNoteConsensus.setFieldValue(fieldConsensus, null);

		/* ================================================================== */

		if (documentNoteSeqStaff.getName().equals("Seq-staff (Seq)")) {
			documentNoteTypeSeqStaff.setDefaultVisibleInTable(false);
			documentNoteTypeSeqStaff.setVisible(false);

			DocumentNoteUtilities.setNoteType(documentNoteTypeSeqStaff);
		}

		/* ================================================================== */
		AnnotatedPluginDocument.DocumentNotes documentNotes = (DocumentNotes) document
				.getDocumentNotes(true);

		/* ================================================================== */

		/* Set note */
		documentNotes.setNote(documentNoteExtractSeq);
		documentNotes.setNote(documentNotePCR);
		documentNotes.setNote(documentNoteMarker);
		documentNotes.setNote(documentNoteDocVersion);
		documentNotes.setNote(documentNoteSeqStaff);
		documentNotes.setNote(documentNoteConsensus);

		/* Save the selected sequence document */
		documentNotes.saveNotes();

		setNotesLog(extractID, pcrPlate, markerCode, versionNumber, null);
		logger.info("Notes added succesful");

		if (listNotes != null) {
			listNotes.clear();
		}

	}

	/**
	 * @return
	 * @return
	 */

	private ArrayList<DocumentNoteField> addNotesToListNotes(
			String[] multipleValues) {
		ArrayList<DocumentNoteField> listNotes = new ArrayList<DocumentNoteField>();
		/* Extract ID (Seq) */

		return listNotes;
	}

	/**
	 * 
	 */
	private void setFieldAndDescriptionValues() {
		/* Extract ID (Seq) */
		fieldExtractIDSeq = "ExtractIDCode_Seq";
		this.descriptionExtractIDSeq = "Naturalis file " + noteTextExtractIDSeq
				+ " note";

		/* PCR plate */
		fieldPCRPlate = "PCRplateIDCode_Seq";
		this.descriptionPCRPlate = "Naturalis file " + noteTextPCRPlate
				+ " note";

		/* MARKER */
		fieldMarker = "MarkerCode_Seq";
		this.descriptionMarker = "Naturalis file " + noteTextMarker + " note";

		/* Document Version */
		fieldDocversion = "DocumentVersionCode_Seq";
		this.descriptionDocversion = "Naturalis file " + noteTextDocversion
				+ " note";

		/* Seq-staff (Seq) */
		fieldSeqStaff = "SequencingStaffCode_FixedValue_Seq";
		this.descriptionSeqStaff = "Naturalis file " + noteTextSeqStaff
				+ " note";

		/* ConsensusSeqPassCode */
		fieldConsensus = "ConsensusSeqPassCode_Seq";
		this.descriptionConsensus = "Naturalis file " + noteTextConsensus
				+ " note";
	}

	private void setNotesLog(String extractID, String pcrPlate,
			String markerCode, int versionNumber, String seqStaff) {
		logger.info("Note value " + this.fieldExtractIDSeq + ": " + extractID
				+ " added succesful");

		logger.info("Note value " + this.fieldPCRPlate + ": " + pcrPlate
				+ " added succesful");

		logger.info("Note value " + this.fieldMarker + ": " + markerCode
				+ " added succesful");

		logger.info("Note value " + this.fieldDocversion + ": " + versionNumber
				+ " added succesful");

		LimsImporterUtil importerUtil = new LimsImporterUtil();
		try {
			logger.info("Note value " + this.fieldSeqStaff + ": "
					+ importerUtil.getPropValues("seqsequencestaff")
					+ " added succesful");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		logger.info("Note value " + this.fieldConsensus + ": " + null
				+ " added succesful");

	}

}
