/**
 * 
 */
package nl.naturalis.lims2.ab1.importer;

import java.io.File;
import java.io.IOException;
import java.util.List;

import jebl.util.ProgressListener;
import nl.naturalis.lims2.excel.importer.LimsNotes;
import nl.naturalis.lims2.utils.LimsAB1Fields;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentFileImporter;
import com.biomatters.geneious.publicapi.plugin.DocumentImportException;
import com.biomatters.geneious.publicapi.plugin.PluginUtilities;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsImportAB1 extends DocumentFileImporter {

	static final Logger logger;
	LimsAB1Fields limsAB1Fields = new LimsAB1Fields();
	LimsNotes limsNotes = new LimsNotes();

	private AnnotatedPluginDocument document;
	/*
	 * private String fieldCode; private String description; private String
	 * noteTypeCode;
	 */
	private int count = 0;

	static {
		logger = LoggerFactory.getLogger(LimsImportAB1.class);
	}

	@Override
	public String getFileTypeDescription() {
		return "Naturalis Extract AB1 Filename Importer";
	}

	@Override
	public String[] getPermissibleExtensions() {
		return new String[] { "ab1", "abi" };
	}

	@Override
	public void importDocuments(File file, ImportCallback importCallback,
			ProgressListener progressListener) throws IOException,
			DocumentImportException {

		progressListener.setMessage("Importing sequence data");
		List<AnnotatedPluginDocument> docs = PluginUtilities.importDocuments(
				file, ProgressListener.EMPTY);

		count += docs.size();

		document = importCallback.addDocument(docs.iterator().next());

		if (file.getName() != null) {
			limsAB1Fields.setFieldValuesFromAB1FileName(file.getName());

			logger.info("-----------------------------------------------------------------");
			logger.info("Start extracting value from file: " + file.getName());

			/* set note for Extract-ID */
			try {
				limsNotes.setImportNotes(document, "ExtractIdCode",
						"Extract ID", "Extract-ID",
						limsAB1Fields.getExtractID());
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			/* set note for PCR Plaat-ID */
			try {
				limsNotes.setImportNotes(document, "PcrPlaatIdCode",
						"PCR plaat ID", "PCR plaat ID",
						limsAB1Fields.getPcrPlaatID());
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			/* set note for Marker */
			try {
				limsNotes.setImportNotes(document, "MarkerCode", "Marker",
						"Marker", limsAB1Fields.getMarker());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		logger.info("Total of document(s) filename extracted: " + count);
		logger.info("-----------------------------------------------------------------");
		logger.info("Done with extracting Ab1 file name. ");
	}

	@Override
	public AutoDetectStatus tentativeAutoDetect(File file,
			String fileContentsStart) {
		return AutoDetectStatus.ACCEPT_FILE;
	}

	/*
	 * public void setNotes(AnnotatedPluginDocument document, String fieldCode,
	 * String textNoteField, String noteTypeCode, String fieldValue) {
	 * 
	 * List<DocumentNoteField> listNotes = new ArrayList<DocumentNoteField>();
	 * 
	 * "ExtractPlaatNummerCode" this.fieldCode = fieldCode; Parameter example
	 * noteTypeCode = "Extract-Plaatnummer" this.description =
	 * "Naturalis AB1 file " + noteTypeCode + " note";
	 * 
	 * 
	 * Parameter: textNoteField= ExtractPlaatNummer, this.fieldcode value
	 * fieldcode
	 * 
	 * listNotes.add(DocumentNoteField.createTextNoteField(textNoteField,
	 * this.description, this.fieldCode, Collections.<Constraint> emptyList(),
	 * true));
	 * 
	 * Check if note type exists Parameter noteTypeCode get value
	 * "Extract Plaatnummer" this.noteTypeCode = "DocumentNoteUtilities-" +
	 * noteTypeCode; DocumentNoteType documentNoteType = DocumentNoteUtilities
	 * .getNoteType(this.noteTypeCode); Extract-ID note if (documentNoteType ==
	 * null) { documentNoteType = DocumentNoteUtilities.createNewNoteType(
	 * noteTypeCode, this.noteTypeCode, this.description, listNotes, false);
	 * DocumentNoteUtilities.setNoteType(documentNoteType);
	 * logger.info("NoteType " + noteTypeCode + " created succesful"); }
	 * 
	 * Create note for Extract-ID DocumentNote documentNote =
	 * documentNoteType.createDocumentNote();
	 * documentNote.setFieldValue(this.fieldCode, fieldValue);
	 * 
	 * AnnotatedPluginDocument.DocumentNotes documentNotes = document
	 * .getDocumentNotes(true);
	 * 
	 * Set note documentNotes.setNote(documentNote); Save the selected sequence
	 * document documentNotes.saveNotes(); logger.info("Note value " +
	 * noteTypeCode + ": " + fieldValue + " added succesful"); }
	 */
}
