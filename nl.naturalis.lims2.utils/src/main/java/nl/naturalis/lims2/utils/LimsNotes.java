/**
 * 
 */
package nl.naturalis.lims2.utils;

import java.util.ArrayList;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument.DocumentNotes;
import com.biomatters.geneious.publicapi.documents.Constraint;
import com.biomatters.geneious.publicapi.documents.DocumentNote;
import com.biomatters.geneious.publicapi.documents.DocumentNoteField;
import com.biomatters.geneious.publicapi.documents.DocumentNoteType;
import com.biomatters.geneious.publicapi.documents.DocumentNoteUtilities;

/**
 * <table>
 * <tr>
 * <td>
 * Date: 24 august 2016</td>
 * </tr>
 * <tr>
 * <td>
 * Company: Naturalis Biodiversity Center</td>
 * </tr>
 * <tr>
 * <td>
 * City: Leiden</td>
 * </tr>
 * <tr>
 * <td>
 * Country: Netherlands</td>
 * </tr>
 * <tr>
 * <td>
 * Description:<br>
 * A Class of Methods to set notes to one or more documents.<br>
 * Used in following Class: - LimsImportAB1Update "Plugin Split name" -
 * LimsImportBold "Plugin Bold" - LimsImportCRS "Plugin CRS" - LimsImporSamples
 * "Plugin Samples"</td>
 * </tr>
 * </table>
 * 
 * @author Reinier.Kartowikromo
 *
 */
public class LimsNotes {

	private String fieldCode;
	private String description;
	private String noteTypeCode;
	LimsImporterUtil limsImporterUtil = new LimsImporterUtil();

	private static final Logger logger = LoggerFactory
			.getLogger(LimsNotes.class);

	/**
	 * Notes values for Consensus Sequence(ComboBox control)
	 * */
	public String[] ConsensusSeqPass = { "OK", "medium", "low",
			"contamination", "endo-contamination", "exo-contamination" };

	/**
	 * Set notes to a document(s) Used in following Class: - LimsImportAB1Update
	 * "Plugin Split name" - LimsImportBold "Plugin Bold" - LimsImportCRS
	 * "Plugin CRS" - LimsImporSamples "Plugin Samples"
	 * 
	 * Package for Setting notes in a selected sequence document.
	 * 
	 * @param annotatedPluginDocuments
	 *            set annotatedPluginDocuments
	 * @param fieldCode
	 *            set fieldCode for the Notes
	 * @param textNoteField
	 *            set textNoteField for the Notes
	 * @param noteTypeCode
	 *            set NotetTypeCode Label
	 * @param fieldValue
	 *            Set the Value for the Note
	 * @param count
	 *            Set count of the documents
	 */

	public void setNoteToAB1FileName(
			AnnotatedPluginDocument[] annotatedPluginDocuments,
			String fieldCode, String textNoteField, String noteTypeCode,
			String fieldValue, int count) {

		ArrayList<DocumentNoteField> listNotes = new ArrayList<DocumentNoteField>();

		/* "ExtractPlaatNummerCode" */
		this.fieldCode = fieldCode;
		/* Parameter example noteTypeCode = "Extract-Plaatnummer" */
		this.description = "Naturalis file " + noteTypeCode + " note";

		/*
		 * Parameter: textNoteField= ExtractPlaatNummer, this.fieldcode value
		 * fieldcode
		 */
		listNotes.add(DocumentNoteField.createTextNoteField(textNoteField,
				this.description, this.fieldCode,
				Collections.<Constraint> emptyList(), false));

		/* Check if note type exists */
		/* Parameter noteTypeCode get value "" */
		this.noteTypeCode = "DocumentNoteUtilities-" + noteTypeCode;
		DocumentNoteType documentNoteType = DocumentNoteUtilities
				.getNoteType(this.noteTypeCode);

		/* Extract-ID note */
		if (documentNoteType == null) {
			documentNoteType = DocumentNoteUtilities.createNewNoteType(
					noteTypeCode, this.noteTypeCode, this.description,
					listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteType);
			logger.info("NoteType " + noteTypeCode + " created succesful");
		}

		if (documentNoteType.getName().equals("Extraction method (Samples)")
				|| documentNoteType.getName().equals(
						"Extract plate ID (Samples)")
				|| documentNoteType.getName().equals("Region (CRS)")
				|| documentNoteType.getName().equals("Lat (CRS)")
				|| documentNoteType.getName().equals("Long (CRS)")
				|| documentNoteType.getName().equals("Altitude (CRS)")
				|| documentNoteType.getName().equals("Phylum (CRS)")
				|| documentNoteType.getName().equals("Class (CRS)")
				|| documentNoteType.getName().equals("Family (CRS)")
				|| documentNoteType.getName().equals("Subfamily (CRS)")
				|| documentNoteType.getName().equals("Genus (CRS)")
				|| documentNoteType.getName().equals("BOLD proj-ID (Bold)")
				|| documentNoteType.getName().equals("Field ID (Bold)")
				|| documentNoteType.getName().equals("BOLD BIN (Bold)")
				|| documentNoteType.getName()
						.equals("Nucleotide length (Bold)")
				|| documentNoteType.getName().equals("GenBank ID (Bold)")) {
			documentNoteType.setDefaultVisibleInTable(false);
		}

		/* Create note for Extract-ID */

		DocumentNote documentNote = documentNoteType.createDocumentNote();
		documentNote.setFieldValue(this.fieldCode, fieldValue);

		AnnotatedPluginDocument.DocumentNotes documentNotes = (DocumentNotes) annotatedPluginDocuments[count]
				.getDocumentNotes(true);

		/* Set note */
		documentNotes.setNote(documentNote);
		/* Save the selected sequence document */
		documentNotes.saveNotes();

		logger.info("Note value " + noteTypeCode + ": " + fieldValue
				+ " added succesful");
		// geneiousPlugin.getDocumentTypes();
		listNotes.clear();

	}

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
	public void setImportNotes(AnnotatedPluginDocument document,
			String fieldCode, String textNoteField, String noteTypeCode,
			String fieldValue) {

		ArrayList<DocumentNoteField> listNotes = new ArrayList<DocumentNoteField>();

		/* "ExtractPlaatNummerCode" */
		this.fieldCode = fieldCode;
		/* Parameter example noteTypeCode = "Extract-Plaatnummer" */
		this.description = "Naturalis file " + noteTypeCode + " note";

		/*
		 * Parameter: textNoteField= ExtractPlaatNummer, this.fieldcode value
		 * fieldcode
		 */
		listNotes.add(DocumentNoteField.createTextNoteField(textNoteField,
				this.description, this.fieldCode,
				Collections.<Constraint> emptyList(), true));

		/* Check if note type exists */
		/* Parameter noteTypeCode get value "Extract Plaatnummer" */
		this.noteTypeCode = "DocumentNoteUtilities-" + noteTypeCode;
		DocumentNoteType documentNoteType = DocumentNoteUtilities
				.getNoteType(this.noteTypeCode);
		/* Extract-ID note */
		if (documentNoteType == null) {
			documentNoteType = DocumentNoteUtilities.createNewNoteType(
					noteTypeCode, this.noteTypeCode, this.description,
					listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteType);
			logger.info("NoteType " + noteTypeCode + " created succesful");
		}

		if (documentNoteType.getName().equals("Ampl-staff (Seq)")) {
			documentNoteType.setDefaultVisibleInTable(false);
			documentNoteType.setVisible(false);
			DocumentNoteUtilities.setNoteType(documentNoteType);
		}

		/* Create note for Extract-ID */
		DocumentNote documentNote = documentNoteType.createDocumentNote();
		documentNote.setFieldValue(this.fieldCode, fieldValue);

		AnnotatedPluginDocument.DocumentNotes documentNotes = document
				.getDocumentNotes(true);

		/* Set note */
		documentNotes.setNote(documentNote);
		/* Save the selected sequence document */
		documentNotes.saveNotes();
		logger.info("Note value " + noteTypeCode + ": " + fieldValue
				+ " added succesful");
		if (listNotes != null) {
			listNotes.clear();
		}
	}

	/**
	 * Set Consensus notes value to the document(s). See import plugin:
	 * "All Naturalis files"
	 * limsNotes.setImportConsensusSeqPassNotes(documentAnnotated,
	 * limsNotes.ConsensusSeqPass, "ConsensusSeqPassCode_Seq", "Pass (Seq)",
	 * "Pass (Seq)", null);
	 * 
	 * @param document
	 *            Document
	 * @param multipleValues
	 *            Set more than one values
	 * @param fieldCode
	 *            The field code of the note
	 * @param textNoteField
	 *            Text of the field
	 * @param noteTypeCode
	 *            Typecode of note
	 * @param fieldValue
	 *            The field value
	 * */
	public void setImportConsensusSeqPassNotes(
			AnnotatedPluginDocument document, String[] multipleValues,
			String fieldCode, String textNoteField, String noteTypeCode,
			String fieldValue) {

		ArrayList<DocumentNoteField> listNotes = new ArrayList<DocumentNoteField>();

		/* "ExtractPlaatNummerCode" */
		this.fieldCode = fieldCode;
		/* Parameter example noteTypeCode = "Extract-Plaatnummer" */
		this.description = "Naturalis file " + noteTypeCode + " note";

		/*
		 * Parameter: textNoteField= ExtractPlaatNummer, this.fieldcode value
		 * fieldcode
		 */
		listNotes.add(DocumentNoteField.createEnumeratedNoteField(
				multipleValues, textNoteField, this.description,
				this.fieldCode, true));

		/* Check if note type exists */
		/* Parameter noteTypeCode get value "Extract Plaatnummer" */
		this.noteTypeCode = "DocumentNoteUtilities-" + noteTypeCode;
		DocumentNoteType documentNoteType = DocumentNoteUtilities
				.getNoteType(this.noteTypeCode);
		/* Extract-ID note */
		if (documentNoteType == null) {
			documentNoteType = DocumentNoteUtilities.createNewNoteType(
					noteTypeCode, this.noteTypeCode, this.description,
					listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteType);
			logger.info("NoteType " + noteTypeCode + " created succesful");
		}

		/* Create note for Extract-ID */
		DocumentNote documentNote = documentNoteType.createDocumentNote();
		documentNote.setFieldValue(this.fieldCode, fieldValue);

		AnnotatedPluginDocument.DocumentNotes documentNotes = document
				.getDocumentNotes(true);

		/* Set note */
		documentNotes.setNote(documentNote);
		/* Save the selected sequence document */
		documentNotes.saveNotes();
		logger.info("Note value " + noteTypeCode + ": " + fieldValue
				+ " added succesful");
		if (listNotes != null) {
			listNotes.clear();
		}
	}

	/**
	 * Set notes value for a combobox control in the document(s). See plugin:
	 * "Split name"
	 * limsNotes.setNoteDropdownFieldToFileName(annotatedPluginDocuments,
	 * limsNotes.ConsensusSeqPass, "ConsensusSeqPassCode_Seq", "Pass (Seq)",
	 * "Pass (Seq)", null, cnt);
	 * 
	 * @param annotatedPluginDocuments
	 *            Documents
	 * @param multipleValues
	 *            Add more than one value
	 * @param fieldCode
	 *            set field code
	 * @param textNoteField
	 *            Add text for the fieldcode
	 * @param noteTypeCode
	 *            The type of the field,
	 * @param fieldValue
	 *            the field value
	 * @param count
	 *            document count
	 * */
	public void setNoteDropdownFieldToFileName(
			AnnotatedPluginDocument[] annotatedPluginDocuments,
			String[] multipleValues, String fieldCode, String textNoteField,
			String noteTypeCode, String fieldValue, int count) {

		ArrayList<DocumentNoteField> listNotes = new ArrayList<DocumentNoteField>();

		/* "ExtractPlaatNummerCode" */
		this.fieldCode = fieldCode;
		/* Parameter example noteTypeCode = "Extract-Plaatnummer" */
		this.description = "Naturalis Consensus Seq Pass file " + noteTypeCode
				+ " note";

		/*
		 * Parameter: textNoteField= ExtractPlaatNummer, this.fieldcode value
		 * fieldcode
		 */

		listNotes.add(DocumentNoteField.createEnumeratedNoteField(
				multipleValues, textNoteField, this.description,
				this.fieldCode, false));

		/* Check if note type exists */
		/* Parameter noteTypeCode get value "" */
		this.noteTypeCode = "DocumentNoteUtilities-" + noteTypeCode;
		DocumentNoteType documentNoteType = DocumentNoteUtilities
				.getNoteType(this.noteTypeCode);

		/* Extract-ID note */
		if (documentNoteType == null) {
			documentNoteType = DocumentNoteUtilities.createNewNoteType(
					noteTypeCode, this.noteTypeCode, this.description,
					listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteType);
			logger.info("NoteType " + noteTypeCode + " created succesful");
		}

		/* Create note for Extract-ID */
		DocumentNote documentNote = documentNoteType.createDocumentNote();
		documentNote.setFieldValue(this.fieldCode, fieldValue);

		AnnotatedPluginDocument.DocumentNotes documentNotes = (DocumentNotes) annotatedPluginDocuments[count]
				.getDocumentNotes(true);

		/* Set note */
		documentNotes.setNote(documentNote);
		/* Save the selected sequence document */
		documentNotes.saveNotes();

		logger.info("Note value " + noteTypeCode + ": " + fieldValue
				+ " added succesful");
		if (listNotes != null)
			listNotes.clear();
	}

	/**
	 * Set boolean value for notes limsNotes.setImportTrueFalseNotes(documents,
	 * "CRSCode_CRS", "CRS (CRS)", "CRS (CRS)", true, cnt);
	 * 
	 * @param document
	 *            Documents
	 * @param fieldCode
	 *            Set the fieldcode
	 * @param textNoteField
	 *            Add text to the field
	 * @param noteTypeCode
	 *            Type of Code
	 * @param fieldValue
	 *            Set the field value
	 * @param count
	 *            Documents count
	 * */
	public void setImportTrueFalseNotes(AnnotatedPluginDocument[] document,
			String fieldCode, String textNoteField, String noteTypeCode,
			boolean fieldValue, int count) {

		ArrayList<DocumentNoteField> listNotes = new ArrayList<DocumentNoteField>();

		/* "ExtractPlaatNummerCode" */
		this.fieldCode = fieldCode;
		/* Parameter example noteTypeCode = "Extract-Plaatnummer" */
		this.description = "Naturalis file " + noteTypeCode + " note";

		/*
		 * Parameter: textNoteField= ExtractPlaatNummer, this.fieldcode value
		 * fieldcode
		 */
		listNotes.add(DocumentNoteField.createBooleanNoteField(textNoteField,
				this.description, this.fieldCode, true));

		/* Check if note type exists */
		/* Parameter noteTypeCode get value "Extract Plaatnummer" */
		this.noteTypeCode = "DocumentNoteUtilities-" + noteTypeCode;
		DocumentNoteType documentNoteType = DocumentNoteUtilities
				.getNoteType(this.noteTypeCode);
		/* Extract-ID note */
		if (documentNoteType == null) {
			documentNoteType = DocumentNoteUtilities.createNewNoteType(
					noteTypeCode, this.noteTypeCode, this.description,
					listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteType);
			logger.info("NoteType " + noteTypeCode + " created succesful");
		}

		/* Create note for Extract-ID */
		DocumentNote documentNote = documentNoteType.createDocumentNote();
		documentNote.setFieldValue(this.fieldCode, fieldValue);

		AnnotatedPluginDocument.DocumentNotes documentNotes = (DocumentNotes) document[count]
				.getDocumentNotes(true);

		/* Set note */
		documentNotes.setNote(documentNote);
		/* Save the selected sequence document */
		documentNotes.saveNotes();
		logger.info("Note value " + noteTypeCode + ": " + fieldValue
				+ " added succesful");
		if (listNotes != null)
			listNotes.clear();
	}

}
