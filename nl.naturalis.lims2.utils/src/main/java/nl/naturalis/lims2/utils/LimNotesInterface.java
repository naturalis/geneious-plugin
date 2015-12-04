/**
 * 
 */
package nl.naturalis.lims2.utils;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

/**
 * @author Reinier.Kartowikromo
 *
 */
public interface LimNotesInterface {

	void setNoteToAB1FileName(
			AnnotatedPluginDocument[] annotatedPluginDocuments,
			String fieldCode, String textNoteField, String noteTypeCode,
			String fieldValue, int count);

	void setImportNotes(AnnotatedPluginDocument document, String fieldCode,
			String textNoteField, String noteTypeCode, String fieldValue);
}
