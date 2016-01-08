/**
 * 
 */
package nl.naturalis.lims2.utils;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentNote;
import com.biomatters.geneious.publicapi.documents.DocumentNoteType;
import com.biomatters.geneious.publicapi.documents.DocumentNoteUtilities;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsReadGeneiousFieldsValues {

	public Object readValueFromAnnotatedPluginDocument(
			AnnotatedPluginDocument annotatedPluginDocument, String noteCode,
			String fieldName) {

		/** noteCode = "DocumentNoteUtilities-Registrationnumber"; */
		DocumentNoteType noteType = DocumentNoteUtilities.getNoteType(noteCode);
		Object fieldValue = null;

		if (noteType != null) {
			AnnotatedPluginDocument.DocumentNotes documentNotes = annotatedPluginDocument
					.getDocumentNotes(true);

			DocumentNote bos = documentNotes.getNote(noteCode);
			/** example: FieldName = "BasisOfRecordCode" */
			fieldValue = bos.getFieldValue(fieldName);
		}

		return fieldValue;
	}

	public Object object(AnnotatedPluginDocument annotatedPluginDocument) {

		/** noteCode = "DocumentNoteUtilities-Version number"; */
		DocumentNoteType noteType = DocumentNoteUtilities
				.getNoteType("DocumentNoteUtilities-VersieCode");
		Object fieldValue = null;

		if (noteType != null) {
			AnnotatedPluginDocument.DocumentNotes documentNotes = annotatedPluginDocument
					.getDocumentNotes(true);

			DocumentNote bos = documentNotes
					.getNote("DocumentNoteUtilities-VersieCode");
			/** example: FieldName = "Version number" */
			fieldValue = bos.getFieldValue("Version number");
		}

		return fieldValue;
	}

}
