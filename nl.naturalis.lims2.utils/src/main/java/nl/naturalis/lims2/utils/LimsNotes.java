/**
 * 
 */
package nl.naturalis.lims2.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
public class LimsNotes {

	private String fieldCode;
	private String description;
	private String noteTypeCode;

	static final Logger logger = LoggerFactory.getLogger(LimsNotes.class);

	/**
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
	 */

	public void setNoteToAB1FileName(
			AnnotatedPluginDocument[] annotatedPluginDocuments,
			String fieldCode, String textNoteField, String noteTypeCode,
			String fieldValue, int count) {
		List<DocumentNoteField> listNotes = new ArrayList<DocumentNoteField>();

		/** "ExtractPlaatNummerCode" */
		this.fieldCode = fieldCode;
		/* Parameter example noteTypeCode = "Extract-Plaatnummer" */
		this.description = "Naturalis AB1 file " + noteTypeCode + " note";

		/**
		 * Parameter: textNoteField= ExtractPlaatNummer, this.fieldcode value
		 * fieldcode
		 */
		listNotes.add(DocumentNoteField.createTextNoteField(textNoteField,
				this.description, this.fieldCode,
				Collections.<Constraint> emptyList(), false));

		/** Check if note type exists */
		/** Parameter noteTypeCode get value "Extract Plaatnummer" */
		this.noteTypeCode = "DocumentNoteUtilities-" + noteTypeCode;
		DocumentNoteType documentNoteType = DocumentNoteUtilities
				.getNoteType(this.noteTypeCode);
		/** Extract-ID note */
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
	}

	public void setImportNotes(AnnotatedPluginDocument document,
			String fieldCode, String textNoteField, String noteTypeCode,
			String fieldValue) {

		List<DocumentNoteField> listNotes = new ArrayList<DocumentNoteField>();

		/* "ExtractPlaatNummerCode" */
		this.fieldCode = fieldCode;
		/* Parameter example noteTypeCode = "Extract-Plaatnummer" */
		this.description = "Naturalis AB1 file " + noteTypeCode + " note";

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
	}
}
