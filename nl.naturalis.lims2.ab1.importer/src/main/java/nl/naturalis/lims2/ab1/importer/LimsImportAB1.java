/**
 * 
 */
package nl.naturalis.lims2.ab1.importer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jebl.util.ProgressListener;
import nl.naturalis.lims2.importer.LimsNotes;
import nl.naturalis.lims2.updater.LimsAB1Fields;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument.DocumentNotes;
import com.biomatters.geneious.publicapi.documents.DocumentField;
import com.biomatters.geneious.publicapi.documents.DocumentNote;
import com.biomatters.geneious.publicapi.documents.DocumentNoteField;
import com.biomatters.geneious.publicapi.documents.DocumentNoteType;
import com.biomatters.geneious.publicapi.documents.DocumentNoteUtilities;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.documents.PluginDocument;
import com.biomatters.geneious.publicapi.documents.sequence.SequenceDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentFileImporter;
import com.biomatters.geneious.publicapi.plugin.DocumentImportException;
import com.biomatters.geneious.publicapi.plugin.PluginUtilities;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsImportAB1 extends DocumentFileImporter {

	static final Logger logger;
	SequenceDocument sequence;
	LimsAB1Fields limsAB1Flieds = new LimsAB1Fields();
	LimsNotes limsNotes = new LimsNotes();
	PluginDocument pluginDocuments;
	AnnotatedPluginDocument documents;
	private AnnotatedPluginDocument[] annotatedPluginDocuments;
	List<AnnotatedPluginDocument> ab1Docs;

	private String fieldCode;
	private String description;
	private String noteTypeCode;

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

	private DocumentField makeExtractcodeField() {
		return DocumentField.createStringField("Extract-ID",
				"Extract-id in the document", "Extract.code");
	}

	@Override
	public void importDocuments(File file, ImportCallback importCallback,
			ProgressListener progressListener) throws IOException,
			DocumentImportException {
		// try {

		String ab1File = file.getCanonicalPath();

		progressListener.setMessage("Importing sequence data");

		List<AnnotatedPluginDocument> docs = PluginUtilities.importDocuments(
				new File(ab1File), ProgressListener.EMPTY);

		/*
		 * ImportUtilities.importDocuments(file, importCallback,
		 * ImportUtilities.ActionWhenInvalid.ImportWithInvalidBases,
		 * ImportUtilities.ImportDocumentType.AskUser, progressListener);
		 */

		// ab1Docs = docs;

		// DocumentUtilities.addAndReturnGeneratedDocuments(docs, true,
		// ab1Docs);

		try {
			DocumentUtilities.addGeneratedDocuments(docs, false);
		} catch (Exception ex) {
			ex.printStackTrace();

		}

		// importCallback.addDocument(docs.iterator().next());

		documents = docs.iterator().next();

		// docs = DocumentUtilities.getSelectedDocuments();

		// documents = docs.iterator().next();

		for (int count = 0; count < docs.size(); count++) {
			if (file.getName() != null) {
				setExtractIDFromAB1FileName(file.getName());

				/* set note for Extract-ID */
				setNotes(documents, "ExtractIdCode", "Extract ID",
						"Extract-ID", limsAB1Flieds.getExtractID(), count);
				logger.info("Extract-ID: " + limsAB1Flieds.getExtractID());

				/* set note for PCR Plaat-ID */
				setNotes(documents, "PcrPlaatIdCode", "PCR plaat ID",
						"PCR plaat ID", limsAB1Flieds.getPcrPlaatID(), count);
				logger.info("PCR plaat-ID: " + limsAB1Flieds.getPcrPlaatID());

				/* set note for Marker */
				setNotes(documents, "MarkerCode", "Marker", "Marker",
						limsAB1Flieds.getMarker(), count);
				logger.info("Mark: " + limsAB1Flieds.getMarker());

			}
		}

		/*
		 * For Dummy Files String name = file.getName(); Date createDate = new
		 * Date(file.lastModified()); Chromatogram trace; try { trace =
		 * ChromatogramFactory.create(file); System.out.println("Trace:" +
		 * trace);
		 * 
		 * SymbolList symbols = ChromatogramTools.getDNASequence(trace);
		 * System.out.println("Symbols:" + symbols);
		 * 
		 * SimpleSequence seq = new SimpleSequence(symbols, file.getName(),
		 * file.getName(), Annotation.EMPTY_ANNOTATION); String output =
		 * docs.iterator().next().toString(); // seq.seqString();
		 * System.out.println("Sequence:" + output);
		 * 
		 * importCallback.addDocument(new LimsImportAB1FieldDocument(name,
		 * createDate, output, docs.iterator().next()));
		 * 
		 * } catch (UnsupportedChromatogramFormatException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */

		/*
		 * } catch (FileNotFoundException ex) { throw new
		 * DocumentImportException("File not found: " + file.getName(), ex);
		 * 
		 * }
		 */
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

	public void setNotes(final AnnotatedPluginDocument documents,
			String fieldCode, String textNoteField, String noteTypeCode,
			String fieldValue, int count) {

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
				this.description, this.fieldCode, Collections.emptyList(),
				false));

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

		AnnotatedPluginDocument.DocumentNotes documentNotes = (DocumentNotes) documents
				.getDocumentNotes(true);

		/* Set note */
		documentNotes.setNote(documentNote);
		/* Save the selected sequence document */
		documentNotes.saveNotes();
		logger.info("Note value " + noteTypeCode + " saved succesful");
	}
}
