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
public class LimsSplitnameNotes {

	private static final Logger logger = LoggerFactory
			.getLogger(LimsSplitnameNotes.class);
	private LimsImporterUtil limsImporterUtil = new LimsImporterUtil();

	private ArrayList<DocumentNoteField> listNotes = new ArrayList<DocumentNoteField>();

	/* Extract-ID Seq */
	private String fieldExtractIDSeq;
	private String noteTypeCodeExtractIDSeq;
	private String descriptionExtractIDSeq;
	private String noteTextExtractIDSeq = "Extract ID (Seq)";

	/* PCR Plate ID */
	private String fieldPCRPlate;
	private String noteTypeCodePCRPlate;
	private String descriptionPCRPlate;
	private String noteTextPCRPlate = "PCR plate ID (Seq)";

	/* Marker */
	private String fieldMarker;
	private String noteTypeCodeMarker;
	private String descriptionMarker;
	private String noteTextMarker = "Marker (Seq)";

	/* Document version */
	private String fieldDocversion;
	private String noteTypeCodeDocversion;
	private String descriptionDocversion;
	private String noteTextDocversion = "Document version";

	/* SequencingStaffCode_FixedValue_Seq */
	private String fieldSeqStaff;
	private String noteTypeCodeSeqStaff;
	private String descriptionSeqStaff;
	private String noteTextSeqStaff = "Seq-staff (Seq)";

	/* ConsensusSeqPassCode */
	private String fieldConsensus;
	private String noteTypeCodeConsensus;
	private String descriptionConsensus;
	private String noteTextConsensus = "Pass (Seq)";

	public String[] ConsensusSeqPass = { "OK", "medium", "low",
			"contamination", "endo-contamination", "exo-contamination" };

	public void setSplitDocumentsNotes(
			AnnotatedPluginDocument[] annotatedPluginDocuments, int cnt,
			String ExtractIDSeq, String pcrPlate, String markerCode,
			int versionNumber, String seqStaff) {

		setFieldAndDescriptionSplitName();

		/* =========================================================== */

		addNotesToNotesListSplitName(ConsensusSeqPass);

		/* ========================================================== */

		/* Extract-ID Seq */
		this.noteTypeCodeExtractIDSeq = "DocumentNoteUtilities-"
				+ noteTextExtractIDSeq;
		DocumentNoteType documentNoteTypeExtractIdSeq = DocumentNoteUtilities
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

		/* ========================================================== */
		/* Extract-ID Seq */
		if (documentNoteTypeExtractIdSeq == null) {
			documentNoteTypeExtractIdSeq = DocumentNoteUtilities
					.createNewNoteType(noteTextExtractIDSeq,
							this.noteTypeCodeExtractIDSeq,
							this.descriptionExtractIDSeq, listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypeExtractIdSeq);
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

		/* ========================================================== */

		/* Extract-ID Seq */
		DocumentNote documentNoteExtractIDSeq = documentNoteTypeExtractIdSeq
				.createDocumentNote();
		documentNoteExtractIDSeq.setFieldValue(this.fieldExtractIDSeq,
				ExtractIDSeq);

		/* PCR plate */
		DocumentNote documentNotePCR = documentNoteTypePCR.createDocumentNote();
		documentNotePCR.setFieldValue(this.fieldPCRPlate, pcrPlate);
		logger.info("Note value " + this.fieldPCRPlate + ": " + pcrPlate
				+ " added succesful");

		/* MARKER */
		DocumentNote documentNoteMarker = documentNoteTypeMarker
				.createDocumentNote();
		documentNoteMarker.setFieldValue(this.fieldMarker, markerCode);
		logger.info("Note value " + this.fieldMarker + ": " + markerCode
				+ " added succesful");

		/* Document Version */
		DocumentNote documentNoteDocVersion = documentNoteTypeDocversion
				.createDocumentNote();
		documentNoteDocVersion.setFieldValue(this.fieldDocversion,
				String.valueOf(versionNumber));
		logger.info("Note value " + this.fieldDocversion + ": " + versionNumber
				+ " added succesful");

		/* Seq-staff (Seq) */
		DocumentNote documentNoteSeqStaff = documentNoteTypeSeqStaff
				.createDocumentNote();
		try {
			documentNoteSeqStaff.setFieldValue(this.fieldSeqStaff,
					limsImporterUtil.getPropValues("seqsequencestaff"));
			logger.info("Note value " + this.fieldSeqStaff + ": "
					+ limsImporterUtil.getPropValues("seqsequencestaff")
					+ " added succesful");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		/* ConsensusSeqPassCode */
		DocumentNote documentNoteConsensus = documentNoteTypeConsensus
				.createDocumentNote();
		documentNoteConsensus.setFieldValue(this.fieldConsensus, null);
		logger.info("Note value " + this.fieldConsensus + ": " + null
				+ " added succesful");

		/* ========================================================== */
		AnnotatedPluginDocument.DocumentNotes documentNotes = (DocumentNotes) annotatedPluginDocuments[cnt]
				.getDocumentNotes(true);

		/* Set the notes */
		documentNotes.setNote(documentNoteExtractIDSeq);
		documentNotes.setNote(documentNotePCR);
		documentNotes.setNote(documentNoteMarker);
		documentNotes.setNote(documentNoteDocVersion);
		documentNotes.setNote(documentNoteSeqStaff);
		documentNotes.setNote(documentNoteConsensus);

		/* Save the selected sequence document */
		documentNotes.saveNotes();

		logger.info("Notes added succesful");

		listNotes.clear();

	}

	/**
	 * 
	 */
	private void addNotesToNotesListSplitName(String[] multipleValues) {
		/* Extract-ID Seq */
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
				multipleValues, noteTextConsensus, this.descriptionConsensus,
				this.fieldConsensus, true));
	}

	/**
	 * 
	 */
	private void setFieldAndDescriptionSplitName() {
		/* Extract-ID Seq */
		this.fieldExtractIDSeq = "ExtractIDCode_Seq";
		this.descriptionExtractIDSeq = "Naturalis file " + noteTextExtractIDSeq
				+ " note";

		/* PCR plate */
		this.fieldPCRPlate = "PCRplateIDCode_Seq";
		this.descriptionPCRPlate = "Naturalis file " + noteTextPCRPlate
				+ " note";

		/* MARKER */
		this.fieldMarker = "MarkerCode_Seq";
		this.descriptionMarker = "Naturalis file " + noteTextMarker + " note";

		/* Document Version */
		this.fieldDocversion = "DocumentVersionCode_Seq";
		this.descriptionDocversion = "Naturalis file " + noteTextDocversion
				+ " note";

		/* Seq-staff (Seq) */
		this.fieldSeqStaff = "SequencingStaffCode_FixedValue_Seq";
		this.descriptionSeqStaff = "Naturalis file " + noteTextSeqStaff
				+ " note";

		/* ConsensusSeqPassCode */
		this.fieldConsensus = "ConsensusSeqPassCode_Seq";
		this.descriptionConsensus = "Naturalis file " + noteTextConsensus
				+ " note";
	}

}
