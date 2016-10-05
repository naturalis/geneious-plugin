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
public class LimsSamplesNotes {

	private static final Logger logger = LoggerFactory
			.getLogger(LimsSamplesNotes.class);
	private LimsImporterUtil limsImporterUtil = new LimsImporterUtil();

	private String fieldRegistrationNumber;
	private String noteTypeCodeReg;
	private String descriptionReg;
	private String noteTextReg = "Registr-nmbr (Samples)";

	private String fieldTaxonName;
	private String noteTypeCodeTaxon;
	private String descriptionTaxon;
	private String noteTextTaxon = "[Scientific name] (Samples)";

	private String fieldProjectPlate;
	private String noteTypeCodeProjectPlate;
	private String descriptionProjectPlate;
	private String noteTextProjectPlate = "Sample plate ID (Samples)";

	private String fieldExtractPlateNumber;
	private String noteTypeCodeExtractPlateNumber;
	private String descriptionExtractPlateNumber;
	private String noteTextExtractPlateNumber = "Extract plate ID (Samples)";

	private String fieldPlatePosition;
	private String noteTypeCodePlatePosition;
	private String descriptionPlatePosition;
	private String noteTextPlatePosition = "Position (Samples)";

	private String fieldExtractID;
	private String noteTypeCodeExtractID;
	private String descriptionExtractID;
	private String noteTextExtractID = "Extract ID (Samples)";

	private String fieldExtractMethod;
	private String noteTypeCodeExtractMethod;
	private String descriptionExtractMethod;
	private String noteTextExtractMethod = "Extraction method (Samples)";

	private String fieldDocversion;
	private String noteTypeCodeDocversion;
	private String descriptionDocversion;
	private String noteTextDocversion = "Document version";

	/* AmplicificationStaffCode_FixedValue_Samples */
	private String fieldAmplStaff;
	private String noteTypeCodeAmplStaff;
	private String descriptionAmplStaff;
	private String noteTextAmplStaff = "Ampl-staff (Samples)";

	/* Registr-nmbr_[Scientific_name] */
	private String fieldRegScientfic;
	private String noteTypeCodeRegScientfic;
	private String descriptionRegScientfic;
	private String noteTextRegScientfic = "Registr-nmbr_[Scientific_name] (Samples)";

	public void setAllNotesToAB1FileName(
			AnnotatedPluginDocument[] annotatedPluginDocuments, int count,
			String regNumber, String taxonNaam, String projectPlateNumber,
			String extractPlateNumber, String platePosition, String extractID,
			String subSample, Object versionNumber, String regScientificname) {
		/* Registration number */
		this.fieldRegistrationNumber = "RegistrationNumberCode_Samples";
		this.descriptionReg = "Naturalis file " + noteTextReg + " note";
		/* Taxon name */
		this.fieldTaxonName = "TaxonName2Code_Samples";
		this.descriptionTaxon = "Naturalis file " + noteTextTaxon + " note";

		/* Projecte plate number */
		this.fieldProjectPlate = "ProjectPlateNumberCode_Samples";
		this.descriptionProjectPlate = "Naturalis file " + noteTextProjectPlate
				+ " note";

		/* Extract plate number */
		this.fieldExtractPlateNumber = "ExtractPlateNumberCode_Samples";
		this.descriptionExtractPlateNumber = "Naturalis file "
				+ noteTextExtractPlateNumber + " note";

		/* Plate position */
		this.fieldPlatePosition = "PlatePositionCode_Samples";
		this.descriptionPlatePosition = "Naturalis file "
				+ noteTextPlatePosition + " note";

		/* Extract ID */
		this.fieldExtractID = "ExtractIDCode_Samples";
		this.descriptionExtractID = "Naturalis file " + noteTextExtractID
				+ " note";

		/* Extraction method (Samples) */
		this.fieldExtractMethod = "SampleMethodCode_Samples";
		this.descriptionExtractMethod = "Naturalis file "
				+ noteTextExtractMethod + " note";

		/* Document Version */
		this.fieldDocversion = "DocumentVersionCode_Seq";
		this.descriptionDocversion = "Naturalis file " + noteTextDocversion
				+ " note";

		/* AmplicificationStaffCode_FixedValue_Samples */
		this.fieldAmplStaff = "AmplicificationStaffCode_FixedValue_Samples";
		this.descriptionAmplStaff = "Naturalis file " + noteTextAmplStaff
				+ " note";

		/* Registr-nmbr_[Scientific_name] */
		this.fieldRegScientfic = "RegistrationNumberCode_TaxonName2Code_Samples";
		this.descriptionRegScientfic = "Naturalis file " + noteTextRegScientfic
				+ " note";

		/*
		 * =====================================================================
		 */

		// if (docName.equals(resultExists.toString())) {
		ArrayList<DocumentNoteField> listNotes = new ArrayList<DocumentNoteField>();

		/* Registration number */
		listNotes.add(DocumentNoteField.createTextNoteField(noteTextReg,
				this.descriptionReg, this.fieldRegistrationNumber,
				Collections.<Constraint> emptyList(), false));
		/* Taxon name */
		listNotes.add(DocumentNoteField.createTextNoteField(noteTextTaxon,
				this.descriptionTaxon, this.fieldTaxonName,
				Collections.<Constraint> emptyList(), false));

		/* Project plate number */
		listNotes.add(DocumentNoteField.createTextNoteField(
				noteTextProjectPlate, this.descriptionProjectPlate,
				this.fieldProjectPlate, Collections.<Constraint> emptyList(),
				false));

		/* Plate Postion */
		listNotes.add(DocumentNoteField.createTextNoteField(
				noteTextPlatePosition, this.descriptionPlatePosition,
				this.fieldPlatePosition, Collections.<Constraint> emptyList(),
				false));

		/* Extract ID */
		listNotes.add(DocumentNoteField.createTextNoteField(noteTextExtractID,
				this.descriptionExtractID, this.fieldExtractID,
				Collections.<Constraint> emptyList(), false));

		/* Extract Method */
		listNotes.add(DocumentNoteField.createTextNoteField(
				noteTextExtractMethod, this.descriptionExtractMethod,
				this.fieldExtractMethod, Collections.<Constraint> emptyList(),
				false));

		/* Document Version */
		listNotes.add(DocumentNoteField.createTextNoteField(noteTextDocversion,
				this.descriptionDocversion, this.fieldDocversion,
				Collections.<Constraint> emptyList(), false));

		/* AmplicificationStaffCode_FixedValue_Samples */
		listNotes.add(DocumentNoteField.createTextNoteField(noteTextAmplStaff,
				this.descriptionAmplStaff, this.fieldAmplStaff,
				Collections.<Constraint> emptyList(), false));

		/* Registr-nmbr_[Scientific_name] */
		listNotes.add(DocumentNoteField.createTextNoteField(
				noteTextRegScientfic, this.descriptionRegScientfic,
				this.fieldRegScientfic, Collections.<Constraint> emptyList(),
				false));

		/* ================================================================ */

		/* Check if note type exists */
		/* Parameter noteTypeCode get value "" */
		/* Registrationnumber */
		this.noteTypeCodeReg = "";
		this.noteTypeCodeReg = "DocumentNoteUtilities-" + noteTextReg;
		DocumentNoteType documentNoteTypeReg = DocumentNoteUtilities
				.getNoteType(this.noteTypeCodeReg);

		/* Taxon name */
		this.noteTypeCodeTaxon = "";
		this.noteTypeCodeTaxon = "DocumentNoteUtilities-" + noteTextTaxon;
		DocumentNoteType documentNoteTypeTaxon = DocumentNoteUtilities
				.getNoteType(this.noteTypeCodeTaxon);

		/* Project plate number */
		this.noteTypeCodeProjectPlate = "";
		this.noteTypeCodeProjectPlate = "DocumentNoteUtilities-"
				+ noteTextProjectPlate;
		DocumentNoteType documentNoteTypeProjectPlate = DocumentNoteUtilities
				.getNoteType(this.noteTypeCodeProjectPlate);

		/* Extract plate number */
		this.noteTypeCodeExtractPlateNumber = "DocumentNoteUtilities-"
				+ noteTextExtractPlateNumber;
		DocumentNoteType documentNoteTypeExtractPlate = DocumentNoteUtilities
				.getNoteType(this.noteTypeCodeExtractPlateNumber);

		/* Plate position */
		this.noteTypeCodePlatePosition = "DocumentNoteUtilities-"
				+ noteTextPlatePosition;
		DocumentNoteType documentNoteTypePlatePosition = DocumentNoteUtilities
				.getNoteType(this.noteTypeCodePlatePosition);

		/* Extract ID */
		this.noteTypeCodeExtractID = "DocumentNoteUtilities-"
				+ noteTextExtractID;
		DocumentNoteType documentNoteTypeExtractID = DocumentNoteUtilities
				.getNoteType(this.noteTypeCodeExtractID);

		/* Extract Method */
		this.noteTypeCodeExtractMethod = "DocumentNoteUtilities-"
				+ noteTextExtractMethod;
		DocumentNoteType documentNoteTypeExtractMethod = DocumentNoteUtilities
				.getNoteType(this.noteTypeCodeExtractMethod);

		/* Document Version */
		this.noteTypeCodeDocversion = "DocumentNoteUtilities-"
				+ noteTextDocversion;
		DocumentNoteType documentNoteTypeDocversion = DocumentNoteUtilities
				.getNoteType(this.noteTypeCodeDocversion);

		/* AmplicificationStaffCode_FixedValue_Samples */
		this.noteTypeCodeAmplStaff = "DocumentNoteUtilities-"
				+ noteTextAmplStaff;
		DocumentNoteType documentNoteTypeAmplStaff = DocumentNoteUtilities
				.getNoteType(this.noteTypeCodeAmplStaff);

		/* Registr-nmbr_[Scientific_name] */
		this.noteTypeCodeRegScientfic = "DocumentNoteUtilities-"
				+ noteTextRegScientfic;
		DocumentNoteType documentNoteTypeRegScientific = DocumentNoteUtilities
				.getNoteType(this.noteTypeCodeRegScientfic);

		/* ================================================================== */

		/* Registration note */
		if (documentNoteTypeReg == null) {
			documentNoteTypeReg = DocumentNoteUtilities.createNewNoteType(
					noteTextReg, this.noteTypeCodeReg, this.descriptionReg,
					listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypeReg);
			logger.info("NoteType " + noteTextReg + " created succesful");
		}

		/* TaxonName note */
		if (documentNoteTypeTaxon == null) {
			documentNoteTypeTaxon = DocumentNoteUtilities.createNewNoteType(
					noteTextTaxon, this.noteTypeCodeTaxon,
					this.descriptionTaxon, listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypeTaxon);
			logger.info("NoteType " + noteTextTaxon + " created succesful");
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

		/* Extract ID */
		if (documentNoteTypeExtractID == null) {
			documentNoteTypeExtractID = DocumentNoteUtilities
					.createNewNoteType(noteTextExtractID,
							this.noteTypeCodeExtractID,
							this.descriptionExtractID, listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypeExtractID);
			logger.info("NoteType " + noteTextExtractID + " created succesful");
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

		/* Document Version */
		if (documentNoteTypeDocversion == null) {
			documentNoteTypeDocversion = DocumentNoteUtilities
					.createNewNoteType(noteTextDocversion,
							this.noteTypeCodeDocversion,
							this.descriptionDocversion, listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypeDocversion);
			logger.info("NoteType " + noteTextDocversion + " created succesful");
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

		if (documentNoteTypeExtractMethod.getName().equals(
				"Extraction method (Samples)")) {
			documentNoteTypeExtractMethod.setDefaultVisibleInTable(false);
		}
		if (documentNoteTypeExtractPlate.getName().equals(
				"Extract plate ID (Samples)")) {
			documentNoteTypeExtractPlate.setDefaultVisibleInTable(false);
		}

		/*
		 * if (documentNoteType.getName().equals("Extraction method (Samples)")
		 * || documentNoteType.getName().equals( "Extract plate ID (Samples)")
		 * || documentNoteType.getName().equals("Region (CRS)") ||
		 * documentNoteType.getName().equals("Lat (CRS)") ||
		 * documentNoteType.getName().equals("Long (CRS)") ||
		 * documentNoteType.getName().equals("Altitude (CRS)") ||
		 * documentNoteType.getName().equals("Phylum (CRS)") ||
		 * documentNoteType.getName().equals("Class (CRS)") ||
		 * documentNoteType.getName().equals("Family (CRS)") ||
		 * documentNoteType.getName().equals("Subfamily (CRS)") ||
		 * documentNoteType.getName().equals("Genus (CRS)") ||
		 * documentNoteType.getName().equals("BOLD proj-ID (Bold)") ||
		 * documentNoteType.getName().equals("Field ID (Bold)") ||
		 * documentNoteType.getName().equals("BOLD BIN (Bold)") ||
		 * documentNoteType.getName() .equals("Nucleotide length (Bold)") ||
		 * documentNoteType.getName().equals("GenBank ID (Bold)")) {
		 * documentNoteType.setDefaultVisibleInTable(false); }
		 */

		/* Create note for Extract-ID */

		/* Registrationnumber */
		DocumentNote documentNoteReg = documentNoteTypeReg.createDocumentNote();
		documentNoteReg.setFieldValue(this.fieldRegistrationNumber, regNumber);
		// limsExcelFields.getRegistrationNumber());

		/* Taxonname */
		DocumentNote documentNoteTaxon = documentNoteTypeTaxon
				.createDocumentNote();
		documentNoteTaxon.setFieldValue(this.fieldTaxonName, taxonNaam);

		/* Project Plate number */
		DocumentNote documentNoteProjectPlate = documentNoteTypeProjectPlate
				.createDocumentNote();
		documentNoteProjectPlate.setFieldValue(this.fieldProjectPlate,
				projectPlateNumber);

		/* Extract Plate number */
		DocumentNote documentNoteExtractPlate = documentNoteTypeExtractPlate
				.createDocumentNote();
		documentNoteExtractPlate.setFieldValue(this.fieldExtractPlateNumber,
				extractPlateNumber);

		/* Plate position */
		DocumentNote documentNotePlatePosition = documentNoteTypePlatePosition
				.createDocumentNote();
		documentNotePlatePosition.setFieldValue(this.fieldPlatePosition,
				platePosition);

		/* Extract ID */
		DocumentNote documentNoteExtractID = documentNoteTypeExtractID
				.createDocumentNote();
		documentNoteExtractID.setFieldValue(this.fieldExtractID, extractID);

		/* Extract Method */
		DocumentNote documentNoteExtractMethod = documentNoteTypeExtractMethod
				.createDocumentNote();
		documentNoteExtractMethod.setFieldValue(this.fieldExtractMethod,
				subSample);

		/* Document Version */
		DocumentNote documentNoteDocVersion = documentNoteTypeDocversion
				.createDocumentNote();
		documentNoteDocVersion.setFieldValue(this.fieldDocversion,
				String.valueOf(versionNumber));

		/* AmplicificationStaffCode_FixedValue_Samples */
		DocumentNote documentNoteAmplStaff = documentNoteTypeAmplStaff
				.createDocumentNote();
		try {
			documentNoteAmplStaff.setFieldValue(this.fieldAmplStaff,
					limsImporterUtil.getPropValues("samplesamplicification"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		/* Registr-nmbr_[Scientific_name] */
		DocumentNote documentNoteRegScientific = documentNoteTypeRegScientific
				.createDocumentNote();
		documentNoteRegScientific.setFieldValue(this.fieldRegScientfic,
				regScientificname);

		/*
		 * ======================================================================
		 */

		AnnotatedPluginDocument.DocumentNotes documentNotes = (DocumentNotes) annotatedPluginDocuments[count]
				.getDocumentNotes(true);

		/* Set the notes */
		documentNotes.setNote(documentNoteReg);
		documentNotes.setNote(documentNoteTaxon);
		documentNotes.setNote(documentNoteProjectPlate);
		documentNotes.setNote(documentNoteExtractPlate);
		documentNotes.setNote(documentNotePlatePosition);
		documentNotes.setNote(documentNoteExtractID);
		documentNotes.setNote(documentNoteExtractMethod);
		documentNotes.setNote(documentNoteDocVersion);
		documentNotes.setNote(documentNoteAmplStaff);
		documentNotes.setNote(documentNoteRegScientific);

		/* Save the selected sequence document */
		documentNotes.saveNotes();

		logger.info("Notes added succesful");

		listNotes.clear();

		// }

	}
}
