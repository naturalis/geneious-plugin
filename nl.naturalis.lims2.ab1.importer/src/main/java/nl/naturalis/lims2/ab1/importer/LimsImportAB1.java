/**
 * 
 */
package nl.naturalis.lims2.ab1.importer;

import java.io.File;
import java.io.FileNotFoundException;
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
		try {
			String ab1File = file.getCanonicalPath();

			progressListener.setMessage("Importing sequence data");

			List<AnnotatedPluginDocument> docs = PluginUtilities
					.importDocuments(new File(ab1File), ProgressListener.EMPTY);

			DocumentUtilities.addGeneratedDocuments(docs, true);

			if (file.getName() != null) {
				setExtractIDFromAB1FileName(file.getName());

				setNotes(annotatedPluginDocuments[1], "ExtractIdCode",
						"Extract ID", "Extract-ID",
						limsAB1Flieds.getExtractID(), 1);
			}

			/*
			 * new LimsImportAB1FieldDocument(file.getName(), new Date(), "",
			 * docs .iterator().next());
			 */

			/*
			 * 
			 * for (int cnt = 0; cnt < docs.size(); cnt++) {
			 * logger.info("Selected document: " + file.getName());
			 * this.annotatedPluginDocuments = docs; }
			 */

			/*
			 * ab1Docs = docs;
			 * 
			 * DocumentUtilities.addGeneratedDocuments(docs, true, ab1Docs);
			 * System.out.println("Test Docs: " + ab1Docs);
			 */
			// importCallback.addDocument(docs.iterator().next());

			/*
			 * try { pluginDocuments = documents.getDocument(); } catch
			 * (DocumentOperationException e1) { // TODO Auto-generated catch
			 * block e1.printStackTrace(); }
			 */

			/*
			 * documents = docs.stream().iterator().next();
			 * System.out.println("Documents: " + documents);
			 */

			/*
			 * try { sequence = (SequenceDocument) documents.getDocument(); }
			 * catch (DocumentOperationException e) { // TODO Auto-generated
			 * catch block e.printStackTrace(); }
			 */

			/*
			 * String name = file.getName(); Date createDate = new
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
			 * System.out.println("Test: " + ab1Docs); for (int cnt = 0; cnt <
			 * docs.size(); cnt++) { logger.info("Selected document: " +
			 * file.getName());
			 * 
			 * try { pluginDocuments = docs.get(cnt).getDocument();
			 * 
			 * // annotatedPluginDocuments =
			 * 
			 * } catch (DocumentOperationException e) { // TODO Auto-generated
			 * catch block e.printStackTrace(); } //
			 * DocumentUtilities.getAnnotatedPluginDocumentThatContains
			 * (pluginDocuments);
			 * 
			 * if (file.getName() != null) {
			 * setExtractIDFromAB1FileName(file.getName());
			 * logger.info("Extract-ID: " + limsAB1Flieds.getExtractID());
			 * logger.info("PCR plaat-ID: " + limsAB1Flieds.getPcrPlaatID());
			 * logger.info("Mark: " + limsAB1Flieds.getMarker());
			 * 
			 * 
			 * setNotes(annotatedPluginDocuments, "ExtractIdCode", "Extract ID",
			 * "Extract-ID", limsAB1Flieds.getExtractID(), cnt);
			 * 
			 * 
			 * 
			 * List<AnnotationGeneratorResult> resultsList = new ArrayList
			 * <SequenceAnnotationGenerator.AnnotationGeneratorResult >();
			 * 
			 * AnnotationGeneratorResult result = new
			 * AnnotationGeneratorResult(); DocumentField barcodeField =
			 * makeExtractcodeField(); result.addDocumentFieldToSet(new
			 * DocumentFieldAndValue( barcodeField,
			 * limsAB1Flieds.getExtractID())); System.out.println(barcodeField);
			 * 
			 * resultsList.add(result);
			 * System.out.println(resultsList.toString());
			 *//** set note for Extract-ID */
			/*
			 * 
			 * if (pluginDocuments != null) {
			 * 
			 * 
			 * limsNotes.setNoteToAB1FileName( annotatedPluginDocuments,
			 * "ExtractIdCode", "Extract ID", "Extract-ID",
			 * limsAB1Flieds.getExtractID(), cnt);
			 * 
			 * 
			 * set note for PCR Plaat-ID
			 * 
			 * limsNotes.setNoteToAB1FileName(annotatedPluginDocuments ,
			 * "PcrPlaatIdCode", "PCR plaat ID", "PCR plaat ID",
			 * limsAB1Flieds.getPcrPlaatID(), cnt);
			 *//** set note for Marker */
			/*
			 * 
			 * limsNotes.setNoteToAB1FileName(annotatedPluginDocuments ,
			 * "MarkerCode", "Marker", "Marker", limsAB1Flieds.getMarker(),
			 * cnt);
			 * 
			 * } } }
			 */
		} catch (FileNotFoundException ex) {
			throw new DocumentImportException("File not found: "
					+ file.getName(), ex);
		}
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

		AnnotatedPluginDocument.DocumentNotes documentNotes = (DocumentNotes) annotatedPluginDocuments[count]
				.getDocumentNotes(true);

		/* Set note */
		documentNotes.setNote(documentNote);
		/* Save the selected sequence document */
		documentNotes.saveNotes();
		logger.info("Note value " + noteTypeCode + " saved succesful");
	}
}
