/**
 * 
 */
package nl.naturalis.lims2.importer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentNote;
import com.biomatters.geneious.publicapi.documents.DocumentNoteField;
import com.biomatters.geneious.publicapi.documents.DocumentNoteType;
import com.biomatters.geneious.publicapi.documents.DocumentNoteUtilities;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument.DocumentNotes;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsNotes {
	
	private String fieldCode;
	private String description;
	private String noteTypeCode;
	
	void setNoteToAB1FileName(AnnotatedPluginDocument[] annotatedPluginDocuments, String fieldCode, 
															 String textNoteField, String noteTypeCode,  String fieldValue)
	{
		List<DocumentNoteField> listNotes =  new ArrayList<DocumentNoteField>();
		
		/* "ExtractPlaatNummerCode" */
		this.fieldCode  = fieldCode; 
		/* Parameter example noteTypeCode = "Extract-Plaatnummer" */
		this.description =  "Naturalis AB1 file "+  noteTypeCode + " note";
		
		/* Parameter: textNoteField= ExtractPlaatNummer, this.fieldcode value fieldcode */
		listNotes.add(DocumentNoteField.createTextNoteField(textNoteField,  this.description, this.fieldCode,  (List) Collections.emptyList(), false));
		
		/* Check if note type exists */
		/* Parameter noteTypeCode get value "Extract Plaatnummer" */
		this.noteTypeCode = "DocumentNoteUtilities-"+ noteTypeCode;
		DocumentNoteType documentNoteType = DocumentNoteUtilities.getNoteType(this.noteTypeCode);
		/* Extract-ID note */
		if (documentNoteType == null)
		{
			documentNoteType = DocumentNoteUtilities.createNewNoteType(noteTypeCode, this.noteTypeCode, this.description,  listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteType);
		}
		
		/* Create note for Extract-ID */
		DocumentNote documentNote = documentNoteType.createDocumentNote();
		documentNote.setFieldValue(this.fieldCode, fieldValue);
		
		AnnotatedPluginDocument.DocumentNotes documentNotes = (DocumentNotes) annotatedPluginDocuments[0].getDocumentNotes(true);
		
		/* Set note */
		documentNotes.setNote(documentNote);
		/* Save the selected sequence document */
		documentNotes.saveNotes();
		System.out.println("Note " + noteTypeCode + " saved succesful");
	}

}
