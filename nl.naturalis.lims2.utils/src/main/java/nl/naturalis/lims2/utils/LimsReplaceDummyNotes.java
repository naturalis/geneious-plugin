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
import com.biomatters.geneious.publicapi.documents.Constraint;
import com.biomatters.geneious.publicapi.documents.DocumentNote;
import com.biomatters.geneious.publicapi.documents.DocumentNoteField;
import com.biomatters.geneious.publicapi.documents.DocumentNoteType;
import com.biomatters.geneious.publicapi.documents.DocumentNoteUtilities;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsReplaceDummyNotes {

	private static final Logger logger = LoggerFactory
			.getLogger(LimsReplaceDummyNotes.class);
	private LimsImporterUtil limsImporterUtil = new LimsImporterUtil();
	// private LimsAB1Fields limsAB1Fields = new LimsAB1Fields();

	private String fieldPCRPlate;
	private String noteTypeCodePCRPlate;
	private String descriptionPCRPlate;
	private String noteTextPCRPlate = "PCR plate ID (Seq)";

	private String fieldMarker;
	private String noteTypeCodeMarker;
	private String descriptionMarker;
	private String noteTextMarker = "Marker (Seq)";

	private String fieldExtractIDSeq;
	private String noteTypeCodeExtractIDSeq;
	private String descriptionExtractIDSeq;
	private String noteTextExtractIDSeq = "Extract ID (Seq)";

	private String fieldExtractID;
	private String noteTypeCodeExtractID;
	private String descriptionExtractID;
	private String noteTextExtractID = "Extract ID (Samples)";

	/* Project plate number */
	private String fieldProjectPlate;
	private String noteTypeCodeProjectPlate;
	private String descriptionProjectPlate;
	private String noteTextProjectPlate = "Sample plate ID (Samples)";

	/* Taxon name */
	private String fieldTaxonName;
	private String noteTypeCodeTaxon;
	private String descriptionTaxon;
	private String noteTextTaxon = "[Scientific name] (Samples)";

	/* Registration number */
	private String fieldRegistrationNumber;
	private String noteTypeCodeReg;
	private String descriptionReg;
	private String noteTextReg = "Registr-nmbr (Samples)";

	/* Plate position */
	private String fieldPlatePosition;
	private String noteTypeCodePlatePosition;
	private String descriptionPlatePosition;
	private String noteTextPlatePosition = "Position (Samples)";

	/* Document version */
	private String fieldDocversion;
	private String noteTypeCodeDocversion;
	private String descriptionDocversion;
	private String noteTextDocversion = "Document version";

	/* Seq Staff */
	private String fieldSeqStaff;
	private String noteTypeCodeSeqStaff;
	private String descriptionSeqStaff;
	private String noteTextSeqStaff = "Seq-staff (Seq)";

	/* AmplicificationStaffCode_FixedValue_Samples */
	private String fieldAmplStaff;
	private String noteTypeCodeAmplStaff;
	private String descriptionAmplStaff;
	private String noteTextAmplStaff = "Ampl-staff (Samples)";

	/* Extract plate ID Samples */
	private String fieldExtractPlateNumber;
	private String noteTypeCodeExtractPlateNumber;
	private String descriptionExtractPlateNumber;
	private String noteTextExtractPlateNumber = "Extract plate ID (Samples)";

	/* Extract method */
	private String fieldExtractMethod;
	private String noteTypeCodeExtractMethod;
	private String descriptionExtractMethod;
	private String noteTextExtractMethod = "Extraction method (Samples)";

	/* Registr-nmbr_[Scientific_name] */
	private String fieldRegScientfic;
	private String noteTypeCodeRegScientfic;
	private String descriptionRegScientfic;
	private String noteTextRegScientfic = "Registr-nmbr_[Scientific_name] (Samples)";

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
	 * @param fieldCode
	 *            set fieldcode param
	 * @param textNoteField
	 *            field of the Note
	 * @param noteTypeCode
	 *            Fieldcode of the note
	 * @param fieldValue
	 *            The field value
	 * */
	/*
	 * public void replaceDummyNotesWithAB1Notes(AnnotatedPluginDocument
	 * document, String pcrPlateID, String markerCode, String extractIDSeq,
	 * String extractID, String projectPlateNumber, String taxonName, String
	 * regNumber, String platePosition, String extractPlateNumber, String
	 * subSample, String regScientificname, int versionNumber) {
	 * 
	 * // if (limsAB1Fields.getExtractID().equals(extractID)) { logger.info(
	 * "----------------------------S T A R T ---------------------------------"
	 * );
	 * 
	 * setFieldAndDescription();
	 * 
	 * 
	 * ==================================================================
	 * 
	 * ArrayList<DocumentNoteField> listNotes = createListNotes();
	 * 
	 * ===============================================================
	 * 
	 * Check if note type exists PCR plate this.noteTypeCodePCRPlate =
	 * "DocumentNoteUtilities-" + noteTextPCRPlate; DocumentNoteType
	 * documentNoteTypePCR = DocumentNoteUtilities
	 * .getNoteType(this.noteTypeCodePCRPlate);
	 * 
	 * MARKER this.noteTypeCodeMarker = "DocumentNoteUtilities-" +
	 * noteTextMarker; DocumentNoteType documentNoteTypeMarker =
	 * DocumentNoteUtilities .getNoteType(this.noteTypeCodeMarker);
	 * 
	 * Extract ID (Seq) this.noteTypeCodeExtractIDSeq = "DocumentNoteUtilities-"
	 * + noteTextExtractIDSeq; DocumentNoteType documentNoteTypeExtractSeq =
	 * DocumentNoteUtilities .getNoteType(this.noteTypeCodeExtractIDSeq);
	 * 
	 * Extract ID this.noteTypeCodeExtractID = "DocumentNoteUtilities-" +
	 * noteTextExtractID; DocumentNoteType documentNoteTypeExtractID =
	 * DocumentNoteUtilities .getNoteType(this.noteTypeCodeExtractID);
	 * 
	 * Project plate number this.noteTypeCodeProjectPlate = "";
	 * this.noteTypeCodeProjectPlate = "DocumentNoteUtilities-" +
	 * noteTextProjectPlate; DocumentNoteType documentNoteTypeProjectPlate =
	 * DocumentNoteUtilities .getNoteType(this.noteTypeCodeProjectPlate);
	 * 
	 * Taxon name this.noteTypeCodeTaxon = ""; this.noteTypeCodeTaxon =
	 * "DocumentNoteUtilities-" + noteTextTaxon; DocumentNoteType
	 * documentNoteTypeTaxon = DocumentNoteUtilities
	 * .getNoteType(this.noteTypeCodeTaxon);
	 * 
	 * Registrationnumber this.noteTypeCodeReg = ""; this.noteTypeCodeReg =
	 * "DocumentNoteUtilities-" + noteTextReg; DocumentNoteType
	 * documentNoteTypeReg = DocumentNoteUtilities
	 * .getNoteType(this.noteTypeCodeReg);
	 * 
	 * Plate position this.noteTypeCodePlatePosition = "DocumentNoteUtilities-"
	 * + noteTextPlatePosition; DocumentNoteType documentNoteTypePlatePosition =
	 * DocumentNoteUtilities .getNoteType(this.noteTypeCodePlatePosition);
	 * 
	 * Seq-staff (Seq) this.noteTypeCodeSeqStaff = "DocumentNoteUtilities-" +
	 * noteTextSeqStaff; DocumentNoteType documentNoteTypeSeqStaff =
	 * DocumentNoteUtilities .getNoteType(this.noteTypeCodeSeqStaff);
	 * 
	 * AmplicificationStaffCode_FixedValue_Samples this.noteTypeCodeAmplStaff =
	 * "DocumentNoteUtilities-" + noteTextAmplStaff; DocumentNoteType
	 * documentNoteTypeAmplStaff = DocumentNoteUtilities
	 * .getNoteType(this.noteTypeCodeAmplStaff);
	 * 
	 * Extract plate number this.noteTypeCodeExtractPlateNumber =
	 * "DocumentNoteUtilities-" + noteTextExtractPlateNumber; DocumentNoteType
	 * documentNoteTypeExtractPlate = DocumentNoteUtilities
	 * .getNoteType(this.noteTypeCodeExtractPlateNumber);
	 * 
	 * Extract Method this.noteTypeCodeExtractMethod = "DocumentNoteUtilities-"
	 * + noteTextExtractMethod; DocumentNoteType documentNoteTypeExtractMethod =
	 * DocumentNoteUtilities .getNoteType(this.noteTypeCodeExtractMethod);
	 * 
	 * Registr-nmbr_[Scientific_name] this.noteTypeCodeRegScientfic =
	 * "DocumentNoteUtilities-" + noteTextRegScientfic; DocumentNoteType
	 * documentNoteTypeRegScientific = DocumentNoteUtilities
	 * .getNoteType(this.noteTypeCodeRegScientfic);
	 * 
	 * Document Version this.noteTypeCodeDocversion = "DocumentNoteUtilities-" +
	 * noteTextDocversion; DocumentNoteType documentNoteTypeDocversion =
	 * DocumentNoteUtilities .getNoteType(this.noteTypeCodeDocversion);
	 * 
	 * ===============================================================
	 * 
	 * PCR plate if (documentNoteTypePCR == null) { documentNoteTypePCR =
	 * DocumentNoteUtilities.createNewNoteType( noteTextPCRPlate,
	 * this.noteTypeCodePCRPlate, this.descriptionPCRPlate, listNotes, false);
	 * DocumentNoteUtilities.setNoteType(documentNoteTypePCR);
	 * logger.info("NoteType " + noteTextPCRPlate + " created succesful"); }
	 * 
	 * MARKER if (documentNoteTypeMarker == null) { documentNoteTypeMarker =
	 * DocumentNoteUtilities.createNewNoteType( noteTextMarker,
	 * this.noteTypeCodeMarker, this.descriptionMarker, listNotes, false);
	 * DocumentNoteUtilities.setNoteType(documentNoteTypeMarker);
	 * logger.info("NoteType " + noteTextMarker + " created succesful"); }
	 * 
	 * Extract-ID note if (documentNoteTypeExtractSeq == null) {
	 * documentNoteTypeExtractSeq = DocumentNoteUtilities
	 * .createNewNoteType(noteTextExtractIDSeq, this.noteTypeCodeExtractIDSeq,
	 * this.descriptionExtractIDSeq, listNotes, false);
	 * DocumentNoteUtilities.setNoteType(documentNoteTypeExtractSeq);
	 * logger.info("NoteType " + noteTextExtractIDSeq + " created succesful"); }
	 * 
	 * Extract ID if (documentNoteTypeExtractID == null) {
	 * documentNoteTypeExtractID = DocumentNoteUtilities
	 * .createNewNoteType(noteTextExtractID, this.noteTypeCodeExtractID,
	 * this.descriptionExtractID, listNotes, false);
	 * DocumentNoteUtilities.setNoteType(documentNoteTypeExtractID);
	 * logger.info("NoteType " + noteTextExtractID + " created succesful"); }
	 * 
	 * Project Plate number note if (documentNoteTypeProjectPlate == null) {
	 * documentNoteTypeProjectPlate = DocumentNoteUtilities
	 * .createNewNoteType(noteTextProjectPlate, this.noteTypeCodeProjectPlate,
	 * this.descriptionProjectPlate, listNotes, false);
	 * DocumentNoteUtilities.setNoteType(documentNoteTypeProjectPlate);
	 * logger.info("NoteType " + noteTextProjectPlate + " created succesful"); }
	 * 
	 * TaxonName note if (documentNoteTypeTaxon == null) { documentNoteTypeTaxon
	 * = DocumentNoteUtilities.createNewNoteType( noteTextTaxon,
	 * this.noteTypeCodeTaxon, this.descriptionTaxon, listNotes, false);
	 * DocumentNoteUtilities.setNoteType(documentNoteTypeTaxon);
	 * logger.info("NoteType " + noteTextTaxon + " created succesful"); }
	 * 
	 * Registration note if (documentNoteTypeReg == null) { documentNoteTypeReg
	 * = DocumentNoteUtilities.createNewNoteType( noteTextReg,
	 * this.noteTypeCodeReg, this.descriptionReg, listNotes, false);
	 * DocumentNoteUtilities.setNoteType(documentNoteTypeReg);
	 * logger.info("NoteType " + noteTextReg + " created succesful"); }
	 * 
	 * Plate position note if (documentNoteTypePlatePosition == null) {
	 * documentNoteTypePlatePosition = DocumentNoteUtilities
	 * .createNewNoteType(noteTextPlatePosition, this.noteTypeCodePlatePosition,
	 * this.descriptionPlatePosition, listNotes, false);
	 * DocumentNoteUtilities.setNoteType(documentNoteTypePlatePosition);
	 * logger.info("NoteType " + noteTextPlatePosition + " created succesful");
	 * }
	 * 
	 * Seq-staff (Seq) if (documentNoteTypeSeqStaff == null) {
	 * documentNoteTypeSeqStaff = DocumentNoteUtilities.createNewNoteType(
	 * noteTextSeqStaff, this.noteTypeCodeSeqStaff, this.descriptionSeqStaff,
	 * listNotes, false);
	 * DocumentNoteUtilities.setNoteType(documentNoteTypeSeqStaff);
	 * logger.info("NoteType " + noteTextSeqStaff + " created succesful"); }
	 * 
	 * AmplicificationStaffCode_FixedValue_Samples if (documentNoteTypeAmplStaff
	 * == null) { documentNoteTypeAmplStaff = DocumentNoteUtilities
	 * .createNewNoteType(noteTextAmplStaff, this.noteTypeCodeAmplStaff,
	 * this.descriptionAmplStaff, listNotes, false);
	 * DocumentNoteUtilities.setNoteType(documentNoteTypeAmplStaff);
	 * logger.info("NoteType " + noteTextAmplStaff + " created succesful"); }
	 * 
	 * Extract Plate number note if (documentNoteTypeExtractPlate == null) {
	 * documentNoteTypeExtractPlate = DocumentNoteUtilities
	 * .createNewNoteType(noteTextExtractPlateNumber,
	 * this.noteTypeCodeExtractPlateNumber, this.descriptionExtractPlateNumber,
	 * listNotes, false);
	 * DocumentNoteUtilities.setNoteType(documentNoteTypeExtractPlate);
	 * logger.info("NoteType " + noteTextExtractPlateNumber +
	 * " created succesful"); }
	 * 
	 * Extract Method if (documentNoteTypeExtractMethod == null) {
	 * documentNoteTypeExtractMethod = DocumentNoteUtilities
	 * .createNewNoteType(noteTextExtractMethod, this.noteTypeCodeExtractMethod,
	 * this.descriptionExtractMethod, listNotes, false);
	 * DocumentNoteUtilities.setNoteType(documentNoteTypeExtractMethod);
	 * logger.info("NoteType " + noteTextExtractMethod + " created succesful");
	 * }
	 * 
	 * 
	 * 
	 * Lims-190:Sample import maak of update extra veld veldnaam -
	 * Registr-nmbr_[Scientific name] (Samples) en veldcode =
	 * RegistrationNumberCode_TaxonName2Code_Samples
	 * Registr-nmbr_[Scientific_name]
	 * 
	 * if (documentNoteTypeRegScientific == null) {
	 * documentNoteTypeRegScientific = DocumentNoteUtilities
	 * .createNewNoteType(noteTextRegScientfic, this.noteTypeCodeRegScientfic,
	 * this.descriptionRegScientfic, listNotes, false);
	 * DocumentNoteUtilities.setNoteType(documentNoteTypeRegScientific);
	 * logger.info("NoteType " + noteTextRegScientfic + " created succesful"); }
	 * 
	 * Document Version if (documentNoteTypeDocversion == null) {
	 * documentNoteTypeDocversion = DocumentNoteUtilities
	 * .createNewNoteType(noteTextDocversion, this.noteTypeCodeDocversion,
	 * this.descriptionDocversion, listNotes, false);
	 * DocumentNoteUtilities.setNoteType(documentNoteTypeDocversion);
	 * logger.info("NoteType " + noteTextDocversion + " created succesful"); }
	 * 
	 * 
	 * ======================================================================
	 * 
	 * 
	 * PCR plate DocumentNote documentNotePCR =
	 * documentNoteTypePCR.createDocumentNote();
	 * documentNotePCR.setFieldValue(this.fieldPCRPlate, pcrPlateID);
	 * logger.info("Note value " + this.fieldPCRPlate + ": " + pcrPlateID +
	 * " added succesful");
	 * 
	 * MARKER DocumentNote documentNoteMarker = documentNoteTypeMarker
	 * .createDocumentNote(); documentNoteMarker.setFieldValue(this.fieldMarker,
	 * markerCode); logger.info("Note value " + this.fieldMarker + ": " +
	 * markerCode + " added succesful");
	 * 
	 * Create note for Extract-ID DocumentNote documentNoteExtractSeq =
	 * documentNoteTypeExtractSeq .createDocumentNote();
	 * documentNoteExtractSeq.setFieldValue(this.fieldExtractIDSeq,
	 * extractIDSeq); logger.info("Note value " + this.fieldExtractIDSeq + ": "
	 * + extractIDSeq + " added succesful");
	 * 
	 * Extract ID DocumentNote documentNoteExtractID = documentNoteTypeExtractID
	 * .createDocumentNote();
	 * documentNoteExtractID.setFieldValue(this.fieldExtractID, extractID);
	 * logger.info("Note value " + this.fieldExtractID + ": " + extractID +
	 * " added succesful");
	 * 
	 * Project Plate number DocumentNote documentNoteProjectPlate =
	 * documentNoteTypeProjectPlate .createDocumentNote();
	 * documentNoteProjectPlate.setFieldValue(this.fieldProjectPlate,
	 * projectPlateNumber); logger.info("Note value " + this.fieldProjectPlate +
	 * ": " + projectPlateNumber + " added succesful");
	 * 
	 * Taxonname DocumentNote documentNoteTaxon = documentNoteTypeTaxon
	 * .createDocumentNote();
	 * documentNoteTaxon.setFieldValue(this.fieldTaxonName, taxonName);
	 * logger.info("Note value " + this.fieldTaxonName + ": " + taxonName +
	 * " added succesful");
	 * 
	 * Registrationnumber DocumentNote documentNoteReg =
	 * documentNoteTypeReg.createDocumentNote();
	 * documentNoteReg.setFieldValue(this.fieldRegistrationNumber, regNumber);
	 * logger.info("Note value " + this.fieldRegistrationNumber + ": " +
	 * regNumber + " added succesful");
	 * 
	 * Plate position DocumentNote documentNotePlatePosition =
	 * documentNoteTypePlatePosition .createDocumentNote();
	 * documentNotePlatePosition.setFieldValue(this.fieldPlatePosition,
	 * platePosition); logger.info("Note value " + this.fieldPlatePosition +
	 * ": " + platePosition + " added succesful");
	 * 
	 * Seq-staff (Seq) DocumentNote documentNoteSeqStaff =
	 * documentNoteTypeSeqStaff .createDocumentNote(); try {
	 * documentNoteSeqStaff.setFieldValue(this.fieldSeqStaff,
	 * limsImporterUtil.getPropValues("seqsequencestaff"));
	 * logger.info("Note value " + this.fieldSeqStaff + ": " +
	 * limsImporterUtil.getPropValues("seqsequencestaff") + " added succesful");
	 * } catch (IOException e) { throw new RuntimeException(e); }
	 * 
	 * AmplicificationStaffCode_FixedValue_Samples DocumentNote
	 * documentNoteAmplStaff = documentNoteTypeAmplStaff .createDocumentNote();
	 * try { documentNoteAmplStaff.setFieldValue(this.fieldAmplStaff,
	 * limsImporterUtil.getPropValues("samplesamplicification"));
	 * logger.info("Note value " + this.fieldAmplStaff + ": " +
	 * limsImporterUtil.getPropValues("samplesamplicification") +
	 * " added succesful"); } catch (IOException e) { throw new
	 * RuntimeException(e); }
	 * 
	 * Extract Plate number DocumentNote documentNoteExtractPlate =
	 * documentNoteTypeExtractPlate .createDocumentNote();
	 * documentNoteExtractPlate.setFieldValue(this.fieldExtractPlateNumber,
	 * extractPlateNumber); logger.info("Note value " +
	 * this.fieldExtractPlateNumber + ": " + extractPlateNumber +
	 * " added succesful");
	 * 
	 * Extract Method DocumentNote documentNoteExtractMethod =
	 * documentNoteTypeExtractMethod .createDocumentNote();
	 * documentNoteExtractMethod.setFieldValue(this.fieldExtractMethod,
	 * subSample); logger.info("Note value " + this.fieldExtractMethod + ": " +
	 * subSample + " added succesful");
	 * 
	 * Registr-nmbr_[Scientific_name] DocumentNote documentNoteRegScientific =
	 * documentNoteTypeRegScientific .createDocumentNote();
	 * documentNoteRegScientific.setFieldValue(this.fieldRegScientfic,
	 * regScientificname); logger.info("Note value " + this.fieldRegScientfic +
	 * ": " + regScientificname + " added succesful");
	 * 
	 * Document Version DocumentNote documentNoteDocVersion =
	 * documentNoteTypeDocversion .createDocumentNote();
	 * documentNoteDocVersion.setFieldValue(this.fieldDocversion,
	 * String.valueOf(versionNumber)); logger.info("Note value " +
	 * this.fieldDocversion + ": " + versionNumber + " added succesful");
	 * 
	 * 
	 * ==================================================================
	 * 
	 * 
	 * if (documentNoteSeqStaff.getName().equals("Seq-staff (Seq)")) {
	 * documentNoteTypeSeqStaff.setDefaultVisibleInTable(false);
	 * documentNoteTypeSeqStaff.setVisible(false);
	 * 
	 * DocumentNoteUtilities.setNoteType(documentNoteTypeSeqStaff); }
	 * 
	 * AnnotatedPluginDocument.DocumentNotes documentNotes = document
	 * .getDocumentNotes(true);
	 * 
	 * Set note documentNotes.setNote(documentNotePCR);
	 * documentNotes.setNote(documentNoteMarker);
	 * documentNotes.setNote(documentNoteExtractSeq);
	 * documentNotes.setNote(documentNoteExtractID);
	 * documentNotes.setNote(documentNoteProjectPlate);
	 * documentNotes.setNote(documentNoteTaxon);
	 * documentNotes.setNote(documentNoteReg);
	 * documentNotes.setNote(documentNotePlatePosition);
	 * documentNotes.setNote(documentNoteSeqStaff);
	 * documentNotes.setNote(documentNoteAmplStaff);
	 * documentNotes.setNote(documentNoteExtractPlate);
	 * documentNotes.setNote(documentNoteExtractMethod);
	 * documentNotes.setNote(documentNoteRegScientific);
	 * documentNotes.setNote(documentNoteDocVersion);
	 * 
	 * Save the selected sequence document documentNotes.saveNotes();
	 * 
	 * logger.info("Notes added succesful");
	 * 
	 * if (listNotes != null) { listNotes.clear(); }
	 * 
	 * // } }
	 */

	/**
	 * @return
	 */
	private ArrayList<DocumentNoteField> createListNotes() {
		ArrayList<DocumentNoteField> listNotes = new ArrayList<DocumentNoteField>();

		/* PCR plate */
		listNotes.add(DocumentNoteField.createTextNoteField(noteTextPCRPlate,
				this.descriptionPCRPlate, this.fieldPCRPlate,
				Collections.<Constraint> emptyList(), false));

		/* MARKER */
		listNotes.add(DocumentNoteField.createTextNoteField(noteTextMarker,
				this.descriptionMarker, this.fieldMarker,
				Collections.<Constraint> emptyList(), false));

		/* Extract ID (Seq) */
		listNotes.add(DocumentNoteField.createTextNoteField(
				noteTextExtractIDSeq, this.descriptionExtractIDSeq,
				this.fieldExtractIDSeq, Collections.<Constraint> emptyList(),
				false));

		/* Extract ID */
		listNotes.add(DocumentNoteField.createTextNoteField(noteTextExtractID,
				this.descriptionExtractID, this.fieldExtractID,
				Collections.<Constraint> emptyList(), false));

		/* Project plate number */
		listNotes.add(DocumentNoteField.createTextNoteField(
				noteTextProjectPlate, this.descriptionProjectPlate,
				this.fieldProjectPlate, Collections.<Constraint> emptyList(),
				false));

		/* Taxon name */
		listNotes.add(DocumentNoteField.createTextNoteField(noteTextTaxon,
				this.descriptionTaxon, this.fieldTaxonName,
				Collections.<Constraint> emptyList(), false));

		/* Registration number */
		listNotes.add(DocumentNoteField.createTextNoteField(noteTextReg,
				this.descriptionReg, this.fieldRegistrationNumber,
				Collections.<Constraint> emptyList(), false));

		/* Plate Postion */
		listNotes.add(DocumentNoteField.createTextNoteField(
				noteTextPlatePosition, this.descriptionPlatePosition,
				this.fieldPlatePosition, Collections.<Constraint> emptyList(),
				false));

		/* Seq-staff (Seq) */
		listNotes.add(DocumentNoteField.createTextNoteField(noteTextSeqStaff,
				this.descriptionSeqStaff, this.fieldSeqStaff,
				Collections.<Constraint> emptyList(), false));

		/* AmplicificationStaffCode_FixedValue_Samples */
		listNotes.add(DocumentNoteField.createTextNoteField(noteTextAmplStaff,
				this.descriptionAmplStaff, this.fieldAmplStaff,
				Collections.<Constraint> emptyList(), false));

		/* Extract plate number */
		listNotes.add(DocumentNoteField.createTextNoteField(
				noteTextExtractPlateNumber, this.descriptionExtractPlateNumber,
				this.fieldExtractPlateNumber,
				Collections.<Constraint> emptyList(), false));

		/* Extract Method */
		listNotes.add(DocumentNoteField.createTextNoteField(
				noteTextExtractMethod, this.descriptionExtractMethod,
				this.fieldExtractMethod, Collections.<Constraint> emptyList(),
				false));

		/* Registr-nmbr_[Scientific_name] */
		listNotes.add(DocumentNoteField.createTextNoteField(
				noteTextRegScientfic, this.descriptionRegScientfic,
				this.fieldRegScientfic, Collections.<Constraint> emptyList(),
				false));

		/* Document Version */
		listNotes.add(DocumentNoteField.createTextNoteField(noteTextDocversion,
				this.descriptionDocversion, this.fieldDocversion,
				Collections.<Constraint> emptyList(), false));

		/*
		 * listNotes.add(DocumentNoteField.createTextNoteField(
		 * noteTextAmplStaffSeq, this.descriptionAmplStaffSeq,
		 * this.fieldAmplStaffSeq, Collections.<Constraint> emptyList(),
		 * false));
		 */

		return listNotes;
	}

	/**
	 * 
	 */
	private void setFieldAndDescription() {
		/* PCR plate */
		this.fieldPCRPlate = "PCRplateIDCode_Seq";
		this.descriptionPCRPlate = "Naturalis file " + noteTextPCRPlate
				+ " note";

		/* MARKER */
		this.fieldMarker = "MarkerCode_Seq";
		this.descriptionMarker = "Naturalis file " + noteTextMarker + " note";

		/* Extract ID (Seq) */
		this.fieldExtractIDSeq = "ExtractIDCode_Seq";
		this.descriptionExtractIDSeq = "Naturalis file " + noteTextExtractIDSeq
				+ " note";

		/* Extract ID */
		this.fieldExtractID = "ExtractIDCode_Samples";
		this.descriptionExtractID = "Naturalis file " + noteTextExtractID
				+ " note";

		/* Projecte plate number */
		this.fieldProjectPlate = "ProjectPlateNumberCode_Samples";
		this.descriptionProjectPlate = "Naturalis file " + noteTextProjectPlate
				+ " note";

		/* Taxon name */
		this.fieldTaxonName = "TaxonName2Code_Samples";
		this.descriptionTaxon = "Naturalis file " + noteTextTaxon + " note";

		/* Registration number */
		this.fieldRegistrationNumber = "RegistrationNumberCode_Samples";
		this.descriptionReg = "Naturalis file " + noteTextReg + " note";

		/* Plate position */
		this.fieldPlatePosition = "PlatePositionCode_Samples";
		this.descriptionPlatePosition = "Naturalis file "
				+ noteTextPlatePosition + " note";

		/* Seq-staff (Seq) */
		this.fieldSeqStaff = "SequencingStaffCode_FixedValue_Seq";
		this.descriptionSeqStaff = "Naturalis file " + noteTextSeqStaff
				+ " note";

		/* AmplicificationStaffCode_FixedValue_Samples */
		this.fieldAmplStaff = "AmplicificationStaffCode_FixedValue_Samples";
		this.descriptionAmplStaff = "Naturalis file " + noteTextAmplStaff
				+ " note";

		/* Extract plate number */
		this.fieldExtractPlateNumber = "ExtractPlateNumberCode_Samples";
		this.descriptionExtractPlateNumber = "Naturalis file "
				+ noteTextExtractPlateNumber + " note";

		/* Extraction method (Samples) */
		this.fieldExtractMethod = "SampleMethodCode_Samples";
		this.descriptionExtractMethod = "Naturalis file "
				+ noteTextExtractMethod + " note";

		/* Registr-nmbr_[Scientific_name] */
		this.fieldRegScientfic = "RegistrationNumberCode_TaxonName2Code_Samples";
		this.descriptionRegScientfic = "Naturalis file " + noteTextRegScientfic
				+ " note";

		/* Document Version */
		this.fieldDocversion = "DocumentVersionCode_Seq";
		this.descriptionDocversion = "Naturalis file " + noteTextDocversion
				+ " note";

		/*
		 * Ampl staff Seq this.fieldAmplStaffSeq = "PCRplateIDCode_Seq";
		 * this.descriptionAmplStaffSeq = "Naturalis file " +
		 * noteTextAmplStaffSeq + " note";
		 */

	}

	public void enrichAb1DocumentWithDummyNotes(
			AnnotatedPluginDocument documentAnnotatedPlugin, Dummy found,
			int versionNumber, String pcrPlateID, String markerCode,
			String extractIDSeq) {
		logger.info("----------------------------S T A R T ---------------------------------");

		setFieldAndDescription();

		/*
		 * ==================================================================
		 */
		ArrayList<DocumentNoteField> listNotes = createListNotes();

		/* =============================================================== */

		/* Check if note type exists */
		/* PCR plate */
		this.noteTypeCodePCRPlate = "DocumentNoteUtilities-" + noteTextPCRPlate;
		DocumentNoteType documentNoteTypePCR = DocumentNoteUtilities
				.getNoteType(this.noteTypeCodePCRPlate);

		/* MARKER */
		this.noteTypeCodeMarker = "DocumentNoteUtilities-" + noteTextMarker;
		DocumentNoteType documentNoteTypeMarker = DocumentNoteUtilities
				.getNoteType(this.noteTypeCodeMarker);

		/* Extract ID (Seq) */
		this.noteTypeCodeExtractIDSeq = "DocumentNoteUtilities-"
				+ noteTextExtractIDSeq;
		DocumentNoteType documentNoteTypeExtractSeq = DocumentNoteUtilities
				.getNoteType(this.noteTypeCodeExtractIDSeq);

		/* Extract ID */
		this.noteTypeCodeExtractID = "DocumentNoteUtilities-"
				+ noteTextExtractID;
		DocumentNoteType documentNoteTypeExtractID = DocumentNoteUtilities
				.getNoteType(this.noteTypeCodeExtractID);

		/* Project plate number */
		this.noteTypeCodeProjectPlate = "";
		this.noteTypeCodeProjectPlate = "DocumentNoteUtilities-"
				+ noteTextProjectPlate;
		DocumentNoteType documentNoteTypeProjectPlate = DocumentNoteUtilities
				.getNoteType(this.noteTypeCodeProjectPlate);

		/* Taxon name */
		this.noteTypeCodeTaxon = "";
		this.noteTypeCodeTaxon = "DocumentNoteUtilities-" + noteTextTaxon;
		DocumentNoteType documentNoteTypeTaxon = DocumentNoteUtilities
				.getNoteType(this.noteTypeCodeTaxon);

		/* Registrationnumber */
		this.noteTypeCodeReg = "";
		this.noteTypeCodeReg = "DocumentNoteUtilities-" + noteTextReg;
		DocumentNoteType documentNoteTypeReg = DocumentNoteUtilities
				.getNoteType(this.noteTypeCodeReg);

		/* Plate position */
		this.noteTypeCodePlatePosition = "DocumentNoteUtilities-"
				+ noteTextPlatePosition;
		DocumentNoteType documentNoteTypePlatePosition = DocumentNoteUtilities
				.getNoteType(this.noteTypeCodePlatePosition);

		/* Seq-staff (Seq) */
		this.noteTypeCodeSeqStaff = "DocumentNoteUtilities-" + noteTextSeqStaff;
		DocumentNoteType documentNoteTypeSeqStaff = DocumentNoteUtilities
				.getNoteType(this.noteTypeCodeSeqStaff);

		/* AmplicificationStaffCode_FixedValue_Samples */
		this.noteTypeCodeAmplStaff = "DocumentNoteUtilities-"
				+ noteTextAmplStaff;
		DocumentNoteType documentNoteTypeAmplStaff = DocumentNoteUtilities
				.getNoteType(this.noteTypeCodeAmplStaff);

		/* Extract plate number */
		this.noteTypeCodeExtractPlateNumber = "DocumentNoteUtilities-"
				+ noteTextExtractPlateNumber;
		DocumentNoteType documentNoteTypeExtractPlate = DocumentNoteUtilities
				.getNoteType(this.noteTypeCodeExtractPlateNumber);

		/* Extract Method */
		this.noteTypeCodeExtractMethod = "DocumentNoteUtilities-"
				+ noteTextExtractMethod;
		DocumentNoteType documentNoteTypeExtractMethod = DocumentNoteUtilities
				.getNoteType(this.noteTypeCodeExtractMethod);

		/* Registr-nmbr_[Scientific_name] */
		this.noteTypeCodeRegScientfic = "DocumentNoteUtilities-"
				+ noteTextRegScientfic;
		DocumentNoteType documentNoteTypeRegScientific = DocumentNoteUtilities
				.getNoteType(this.noteTypeCodeRegScientfic);

		/* Document Version */
		this.noteTypeCodeDocversion = "DocumentNoteUtilities-"
				+ noteTextDocversion;
		DocumentNoteType documentNoteTypeDocversion = DocumentNoteUtilities
				.getNoteType(this.noteTypeCodeDocversion);

		/* =============================================================== */

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

		/* Extract ID */
		if (documentNoteTypeExtractID == null) {
			documentNoteTypeExtractID = DocumentNoteUtilities
					.createNewNoteType(noteTextExtractID,
							this.noteTypeCodeExtractID,
							this.descriptionExtractID, listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypeExtractID);
			logger.info("NoteType " + noteTextExtractID + " created succesful");
		}

		/* Project Plate number note */
		if (documentNoteTypeProjectPlate == null) {
			documentNoteTypeProjectPlate = DocumentNoteUtilities
					.createNewNoteType(noteTextProjectPlate,
							this.noteTypeCodeProjectPlate,
							this.descriptionProjectPlate, listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypeProjectPlate);
			logger.info("NoteType " + noteTextProjectPlate
					+ " created succesful");
		}

		/* TaxonName note */
		if (documentNoteTypeTaxon == null) {
			documentNoteTypeTaxon = DocumentNoteUtilities.createNewNoteType(
					noteTextTaxon, this.noteTypeCodeTaxon,
					this.descriptionTaxon, listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypeTaxon);
			logger.info("NoteType " + noteTextTaxon + " created succesful");
		}

		/* Registration note */
		if (documentNoteTypeReg == null) {
			documentNoteTypeReg = DocumentNoteUtilities.createNewNoteType(
					noteTextReg, this.noteTypeCodeReg, this.descriptionReg,
					listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypeReg);
			logger.info("NoteType " + noteTextReg + " created succesful");
		}

		/* Plate position note */
		if (documentNoteTypePlatePosition == null) {
			documentNoteTypePlatePosition = DocumentNoteUtilities
					.createNewNoteType(noteTextPlatePosition,
							this.noteTypeCodePlatePosition,
							this.descriptionPlatePosition, listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypePlatePosition);
			logger.info("NoteType " + noteTextPlatePosition
					+ " created succesful");
		}

		/* Seq-staff (Seq) */
		if (documentNoteTypeSeqStaff == null) {
			documentNoteTypeSeqStaff = DocumentNoteUtilities.createNewNoteType(
					noteTextSeqStaff, this.noteTypeCodeSeqStaff,
					this.descriptionSeqStaff, listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypeSeqStaff);
			logger.info("NoteType " + noteTextSeqStaff + " created succesful");
		}

		/* AmplicificationStaffCode_FixedValue_Samples */
		if (documentNoteTypeAmplStaff == null) {
			documentNoteTypeAmplStaff = DocumentNoteUtilities
					.createNewNoteType(noteTextAmplStaff,
							this.noteTypeCodeAmplStaff,
							this.descriptionAmplStaff, listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypeAmplStaff);
			logger.info("NoteType " + noteTextAmplStaff + " created succesful");
		}

		/* Extract Plate number note */
		if (documentNoteTypeExtractPlate == null) {
			documentNoteTypeExtractPlate = DocumentNoteUtilities
					.createNewNoteType(noteTextExtractPlateNumber,
							this.noteTypeCodeExtractPlateNumber,
							this.descriptionExtractPlateNumber, listNotes,
							false);
			DocumentNoteUtilities.setNoteType(documentNoteTypeExtractPlate);
			logger.info("NoteType " + noteTextExtractPlateNumber
					+ " created succesful");
		}

		/* Extract Method */
		if (documentNoteTypeExtractMethod == null) {
			documentNoteTypeExtractMethod = DocumentNoteUtilities
					.createNewNoteType(noteTextExtractMethod,
							this.noteTypeCodeExtractMethod,
							this.descriptionExtractMethod, listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypeExtractMethod);
			logger.info("NoteType " + noteTextExtractMethod
					+ " created succesful");
		}

		/*
		 * 
		 * Lims-190:Sample import maak of update extra veld veldnaam -
		 * Registr-nmbr_[Scientific name] (Samples) en veldcode =
		 * RegistrationNumberCode_TaxonName2Code_Samples
		 * Registr-nmbr_[Scientific_name]
		 */
		if (documentNoteTypeRegScientific == null) {
			documentNoteTypeRegScientific = DocumentNoteUtilities
					.createNewNoteType(noteTextRegScientfic,
							this.noteTypeCodeRegScientfic,
							this.descriptionRegScientfic, listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypeRegScientific);
			logger.info("NoteType " + noteTextRegScientfic
					+ " created succesful");
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

		/*
		 * ======================================================================
		 */

		/* PCR plate */
		DocumentNote documentNotePCR = documentNoteTypePCR.createDocumentNote();
		documentNotePCR.setFieldValue(this.fieldPCRPlate, pcrPlateID);
		logger.info("Note value " + this.fieldPCRPlate + ": " + pcrPlateID
				+ " added succesful");

		/* MARKER */
		DocumentNote documentNoteMarker = documentNoteTypeMarker
				.createDocumentNote();
		documentNoteMarker.setFieldValue(this.fieldMarker, markerCode);
		logger.info("Note value " + this.fieldMarker + ": " + markerCode
				+ " added succesful");

		/* Create note for Extract-ID */
		DocumentNote documentNoteExtractSeq = documentNoteTypeExtractSeq
				.createDocumentNote();
		documentNoteExtractSeq.setFieldValue(this.fieldExtractIDSeq,
				extractIDSeq);
		logger.info("Note value " + this.fieldExtractIDSeq + ": "
				+ extractIDSeq + " added succesful");

		/* Extract ID */
		DocumentNote documentNoteExtractID = documentNoteTypeExtractID
				.createDocumentNote();
		documentNoteExtractID.setFieldValue(this.fieldExtractID,
				found.getExtractID());
		logger.info("Note value " + this.fieldExtractID + ": "
				+ found.getExtractID() + " added succesful");

		/* Project Plate number */
		DocumentNote documentNoteProjectPlate = documentNoteTypeProjectPlate
				.createDocumentNote();
		documentNoteProjectPlate.setFieldValue(this.fieldProjectPlate,
				found.getExtractPlateNumberIDSamples());
		logger.info("Note value " + this.fieldProjectPlate + ": "
				+ found.getExtractPlateNumberIDSamples() + " added succesful");

		/* Taxonname */
		DocumentNote documentNoteTaxon = documentNoteTypeTaxon
				.createDocumentNote();
		documentNoteTaxon.setFieldValue(this.fieldTaxonName,
				found.getScientificName());
		logger.info("Note value " + this.fieldTaxonName + ": "
				+ found.getScientificName() + " added succesful");

		/* Registrationnumber */
		DocumentNote documentNoteReg = documentNoteTypeReg.createDocumentNote();
		documentNoteReg.setFieldValue(this.fieldRegistrationNumber,
				found.getRegistrationnumber());
		logger.info("Note value " + this.fieldRegistrationNumber + ": "
				+ found.getRegistrationnumber() + " added succesful");

		/* Plate position */
		DocumentNote documentNotePlatePosition = documentNoteTypePlatePosition
				.createDocumentNote();
		documentNotePlatePosition.setFieldValue(this.fieldPlatePosition,
				found.getPosition());
		logger.info("Note value " + this.fieldPlatePosition + ": "
				+ found.getPosition() + " added succesful");

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

		/* AmplicificationStaffCode_FixedValue_Samples */
		DocumentNote documentNoteAmplStaff = documentNoteTypeAmplStaff
				.createDocumentNote();
		try {
			documentNoteAmplStaff.setFieldValue(this.fieldAmplStaff,
					limsImporterUtil.getPropValues("samplesamplicification"));
			logger.info("Note value " + this.fieldAmplStaff + ": "
					+ limsImporterUtil.getPropValues("samplesamplicification")
					+ " added succesful");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		/* Extract Plate number */
		DocumentNote documentNoteExtractPlate = documentNoteTypeExtractPlate
				.createDocumentNote();
		documentNoteExtractPlate.setFieldValue(this.fieldExtractPlateNumber,
				found.getExtractPlateNumberIDSamples());
		logger.info("Note value " + this.fieldExtractPlateNumber + ": "
				+ found.getExtractPlateNumberIDSamples() + " added succesful");

		/* Extract Method */
		DocumentNote documentNoteExtractMethod = documentNoteTypeExtractMethod
				.createDocumentNote();
		documentNoteExtractMethod.setFieldValue(this.fieldExtractMethod,
				found.getExtractMethod());
		logger.info("Note value " + this.fieldExtractMethod + ": "
				+ found.getExtractMethod() + " added succesful");

		/* Registr-nmbr_[Scientific_name] */
		DocumentNote documentNoteRegScientific = documentNoteTypeRegScientific
				.createDocumentNote();
		documentNoteRegScientific.setFieldValue(this.fieldRegScientfic,
				found.getRegistrationScientificName());
		logger.info("Note value " + this.fieldRegScientfic + ": "
				+ found.getRegistrationScientificName() + " added succesful");

		/* Document Version */
		DocumentNote documentNoteDocVersion = documentNoteTypeDocversion
				.createDocumentNote();
		documentNoteDocVersion.setFieldValue(this.fieldDocversion,
				String.valueOf(versionNumber));
		logger.info("Note value " + this.fieldDocversion + ": " + versionNumber
				+ " added succesful");

		/*
		 * ==================================================================
		 */

		if (documentNoteSeqStaff.getName().equals("Seq-staff (Seq)")) {
			documentNoteTypeSeqStaff.setDefaultVisibleInTable(false);
			documentNoteTypeSeqStaff.setVisible(false);

			DocumentNoteUtilities.setNoteType(documentNoteTypeSeqStaff);
		}

		AnnotatedPluginDocument.DocumentNotes documentNotes = documentAnnotatedPlugin
				.getDocumentNotes(true);

		/* Set note */
		documentNotes.setNote(documentNotePCR);
		documentNotes.setNote(documentNoteMarker);
		documentNotes.setNote(documentNoteExtractSeq);
		documentNotes.setNote(documentNoteExtractID);
		documentNotes.setNote(documentNoteProjectPlate);
		documentNotes.setNote(documentNoteTaxon);
		documentNotes.setNote(documentNoteReg);
		documentNotes.setNote(documentNotePlatePosition);
		documentNotes.setNote(documentNoteSeqStaff);
		documentNotes.setNote(documentNoteAmplStaff);
		documentNotes.setNote(documentNoteExtractPlate);
		documentNotes.setNote(documentNoteExtractMethod);
		documentNotes.setNote(documentNoteRegScientific);
		documentNotes.setNote(documentNoteDocVersion);

		/* Save the selected sequence document */
		documentNotes.saveNotes();

		logger.info("Notes added succesful");

		if (listNotes != null) {
			listNotes.clear();
		}

	}

}
