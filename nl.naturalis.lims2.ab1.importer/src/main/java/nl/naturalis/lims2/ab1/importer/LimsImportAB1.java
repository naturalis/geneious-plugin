/**
 * 
 */
package nl.naturalis.lims2.ab1.importer;

import java.io.File;
import java.io.IOException;
import java.util.List;

import jebl.util.ProgressListener;
import nl.naturalis.lims2.excel.importer.LimsNotes;
import nl.naturalis.lims2.updater.LimsAB1Fields;

import org.apache.commons.lang3.StringUtils;
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
	LimsAB1Fields limsAB1Flieds = new LimsAB1Fields();
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
		return "Naturalis Chromatogram AB1 Importer";
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
			setExtractIDFromAB1FileName(file.getName());

			logger.info("-----------------------------------------------------------------");
			logger.info("Start extracting value from file: " + file.getName());

			/* set note for Extract-ID */
			try {
				limsNotes.setImportNotes(document, "ExtractIdCode",
						"Extract ID", "Extract-ID",
						limsAB1Flieds.getExtractID());
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			/* set note for PCR Plaat-ID */
			try {
				limsNotes.setImportNotes(document, "PcrPlaatIdCode",
						"PCR plaat ID", "PCR plaat ID",
						limsAB1Flieds.getPcrPlaatID());
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			/* set note for Marker */
			try {
				limsNotes.setImportNotes(document, "MarkerCode", "Marker",
						"Marker", limsAB1Flieds.getMarker());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		logger.info("Total of document(s) imported: " + count);
		logger.info("DONE");
	}

	@Override
	public AutoDetectStatus tentativeAutoDetect(File file,
			String fileContentsStart) {
		return AutoDetectStatus.ACCEPT_FILE;
	}

	private void setExtractIDFromAB1FileName(String fileName) {
		/* for example: e4010125015_Sil_tri_MJ243_COI-A01_M13F_A01_008.ab1 */
		String[] underscore = StringUtils.split(fileName, "_");
		limsAB1Flieds.setExtractID(underscore[0]);
		limsAB1Flieds.setPcrPlaatID(underscore[3]);
		limsAB1Flieds.setMarker(underscore[4]);
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
