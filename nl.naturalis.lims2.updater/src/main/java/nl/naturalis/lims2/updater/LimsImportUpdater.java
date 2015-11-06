/**
 * 
 */
package nl.naturalis.lims2.updater;

import java.util.List;

import nl.naturalis.lims2.importer.LimsNotes;
import nl.naturalis.lims2.utils.LimsImporterUtil;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentField;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.documents.sequence.NucleotideSequenceDocument;
import com.biomatters.geneious.publicapi.documents.sequence.SequenceDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentAction;
import com.biomatters.geneious.publicapi.plugin.DocumentOperationException;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsImportUpdater extends DocumentAction {

	static final Logger logger;

	static {
		logger = LoggerFactory.getLogger(LimsImportUpdater.class);
	}
	private static final String KEY_BOS = "BOS";
	final DocumentField documentField = DocumentField.createStringField(
			"Registrationnumber", "Basis of record in the document", KEY_BOS,
			true, true);
	LimsImporterUtil limsImporterUtil = new LimsImporterUtil();
	LimsAB1Fields limsAB1Flieds = new LimsAB1Fields();
	LimsNotes limsNotes = new LimsNotes();

	AnnotatedPluginDocument annotatedPluginDocument;
	/*
	 * private String Name; private String Registrationnumber; private String
	 * CreatedDate; private String Description; private String
	 * ImportedFromFilename; private String ImportedFromPath; private String
	 * Modified; private String Size; private String extractIdFileName;
	 */
	SequenceDocument seq;

	@Override
	public void actionPerformed(
			AnnotatedPluginDocument[] annotatedPluginDocuments) {

		logger.info("-----------------------------------------------------------------");
		logger.info("Start");

		/*
		 * SequenceAnnotation annotationToAdd = new SequenceAnnotation(
		 * Registrationnumber, SequenceAnnotation.TYPE_CDS, new
		 * SequenceAnnotationInterval(4, 50)); EditableSequenceDocument
		 * sequence; try { sequence = (EditableSequenceDocument)
		 * annotatedPluginDocuments[0] .getDocument();
		 * 
		 * List<SequenceAnnotation> annotations = new
		 * ArrayList<SequenceAnnotation>( sequence.getSequenceAnnotations());
		 * annotations.add(annotationToAdd);
		 * sequence.setAnnotations(annotations);
		 * annotatedPluginDocuments[0].saveDocument(); } catch
		 * (DocumentOperationException e) { e.printStackTrace(); }
		 */

		List<AnnotatedPluginDocument> docs;
		try {
			docs = DocumentUtilities.getSelectedDocuments();
			for (int cnt = 0; cnt < docs.size(); cnt++) {
				seq = (SequenceDocument) docs.get(cnt).getDocument();
				logger.info("Selected document: " + seq.getName());

				if (seq.getName() != null) {
					setFieldValuesFromAB1FileName(seq.getName());
					logger.info("Extract-ID: " + limsAB1Flieds.getExtractID());
					logger.info("PCR plaat-ID: "
							+ limsAB1Flieds.getPcrPlaatID());
					logger.info("Mark: " + limsAB1Flieds.getMarker());

					/* set note for Extract-ID */
					limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
							"ExtractIdCode", "Extract ID", "Extract-ID",
							limsAB1Flieds.getExtractID(), cnt);

					/* set note for PCR Plaat-ID */
					limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
							"PcrPlaatIdCode", "PCR plaat ID", "PCR plaat ID",
							limsAB1Flieds.getPcrPlaatID(), cnt);

					/* set note for Marker */
					limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
							"MarkerCode", "Marker", "Marker",
							limsAB1Flieds.getMarker(), cnt);

					/*
					 * setNoteForExtractIDFromAB1FileName(annotatedPluginDocuments
					 * );
					 * setNoteForPcrPlaatIDFromAB1FileName(annotatedPluginDocuments
					 * );
					 * setNoteForMarkerFromAB1FileName(annotatedPluginDocuments
					 * );
					 */
				}
			}
		} catch (DocumentOperationException e) {
			try {
				throw new Exception();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		logger.info("-----------------------------------------------------------------");
		logger.info("Done with reading Ab1 file. ");
	}

	/**
	 * Create note for Extract-ID
	 * 
	 * @param annotatedPluginDocuments
	 *            Add annotatedPluginDocuments
	 */
	/*
	 * private void setNoteForExtractIDFromAB1FileName(
	 * AnnotatedPluginDocument[] annotatedPluginDocuments) {
	 * List<DocumentNoteField> listExtractID = new
	 * ArrayList<DocumentNoteField>(); String fieldExtractId = "ExtractIdCode";
	 * 
	 * listExtractID.add(DocumentNoteField.createTextNoteField("ExtractID",
	 * "Naturalis AB1 file Extract-ID note", fieldExtractId,
	 * Collections.emptyList(), false)); Check if note type exists String
	 * noteTypeCodeExtractId = "DocumentNoteUtilities-Extract ID";
	 * DocumentNoteType noteTypeExtract = DocumentNoteUtilities
	 * .getNoteType(noteTypeCodeExtractId); Extract-ID note if (noteTypeExtract
	 * == null) { noteTypeExtract = DocumentNoteUtilities.createNewNoteType(
	 * "Extract ID", noteTypeCodeExtractId,
	 * "Naturalis AB1 file Extract-ID note", listExtractID, false);
	 * DocumentNoteUtilities.setNoteType(noteTypeExtract); }
	 * 
	 * Create note for Extract-ID DocumentNote documentNoteExtractID =
	 * noteTypeExtract .createDocumentNote();
	 * documentNoteExtractID.setFieldValue(fieldExtractId,
	 * limsAB1Flieds.getExtractID());
	 * 
	 * AnnotatedPluginDocument.DocumentNotes documentNotes = (DocumentNotes)
	 * annotatedPluginDocuments[0] .getDocumentNotes(true);
	 * 
	 * Set note for Extract-ID, PCR plaat-ID and Marker
	 * documentNotes.setNote(documentNoteExtractID); Save the selected sequence
	 * document documentNotes.saveNotes();
	 * System.out.println("Note Extract-ID saved succesful"); }
	 *//**
	 * Create a note PCR plaat-ID
	 * 
	 * @param annotatedPluginDocuments
	 *            Add annotatedPluginDocuments
	 */
	/*
	 * private void setNoteForPcrPlaatIDFromAB1FileName(
	 * AnnotatedPluginDocument[] annotatedPluginDocuments) {
	 * List<DocumentNoteField> listPcrPlaatID = new
	 * ArrayList<DocumentNoteField>(); String fieldPcrPlaatId =
	 * "PcrPlaatIdCode";
	 * 
	 * listPcrPlaatID.add(DocumentNoteField.createTextNoteField("PcrPlaatID",
	 * "Naturalis AB1 file PCR Plaat-ID note", fieldPcrPlaatId,
	 * Collections.emptyList(), false)); Check if note type exists String
	 * noteTypeCodePcrPlaatId = "DocumentNoteUtilities-PCR plaat ID";
	 * DocumentNoteType noteTypePCR = DocumentNoteUtilities
	 * .getNoteType(noteTypeCodePcrPlaatId); PCR plaat-ID note if (noteTypePCR
	 * == null) { noteTypePCR = DocumentNoteUtilities.createNewNoteType(
	 * "PCR plaat ID", noteTypeCodePcrPlaatId,
	 * "Naturalis AB1 file PCR Plaat-ID note", listPcrPlaatID, false);
	 * DocumentNoteUtilities.setNoteType(noteTypePCR); }
	 * 
	 * Create note for PCR plaat-ID DocumentNote documentNotePcrPlaatID =
	 * noteTypePCR.createDocumentNote();
	 * documentNotePcrPlaatID.setFieldValue(fieldPcrPlaatId,
	 * limsAB1Flieds.getPcrPlaatID());
	 * 
	 * AnnotatedPluginDocument.DocumentNotes documentNotes = (DocumentNotes)
	 * annotatedPluginDocuments[0] .getDocumentNotes(true); Set note for
	 * Extract-ID, PCR plaat-ID and Marker
	 * documentNotes.setNote(documentNotePcrPlaatID); Save the selected sequence
	 * document documentNotes.saveNotes();
	 * System.out.println("Note PCR plaat-ID saved succesful"); }
	 *//**
	 * Create note for Marker.
	 * 
	 * @param annotatedPluginDocuments
	 *            Add annotatedPluginDocuments
	 */
	/*
	 * private void setNoteForMarkerFromAB1FileName( AnnotatedPluginDocument[]
	 * annotatedPluginDocuments) { List<DocumentNoteField> listMarker = new
	 * ArrayList<DocumentNoteField>(); String fieldMarker = "MarkerCode";
	 * 
	 * listMarker.add(DocumentNoteField.createTextNoteField("Marker",
	 * "Naturalis AB1 file Marker note", fieldMarker, Collections.emptyList(),
	 * false)); Check if note type exists String noteTypeCodeMarker =
	 * "DocumentNoteUtilities-Marker"; DocumentNoteType noteTypeMarker =
	 * DocumentNoteUtilities .getNoteType(noteTypeCodeMarker);
	 * 
	 * Marker note if (noteTypeMarker == null) { noteTypeMarker =
	 * DocumentNoteUtilities.createNewNoteType("Marker", noteTypeCodeMarker,
	 * "Naturalis AB1 file Marker note", listMarker, false);
	 * DocumentNoteUtilities.setNoteType(noteTypeMarker); }
	 * 
	 * Create note for Marker DocumentNote documentNoteMarker =
	 * noteTypeMarker.createDocumentNote(); documentNoteMarker
	 * .setFieldValue(fieldMarker, limsAB1Flieds.getMarker());
	 * 
	 * AnnotatedPluginDocument.DocumentNotes documentNotes = (DocumentNotes)
	 * annotatedPluginDocuments[0] .getDocumentNotes(true); Set note for
	 * Extract-ID, PCR plaat-ID and Marker
	 * documentNotes.setNote(documentNoteMarker); Save the selected sequence
	 * document documentNotes.saveNotes();
	 * System.out.println("Note Marker saved succesful"); }
	 */
	@Override
	public GeneiousActionOptions getActionOptions() {
		return new GeneiousActionOptions("Update Selected document")
				.setInMainToolbar(true);
	}

	@Override
	public String getHelp() {
		return null;
	}

	@Override
	public DocumentSelectionSignature[] getSelectionSignatures() {
		return new DocumentSelectionSignature[] { new DocumentSelectionSignature(
				NucleotideSequenceDocument.class, 0, Integer.MAX_VALUE) };
	}

	/*
	 * public DocumentType[] getDocumentTypes() { return new DocumentType[]{new
	 * DocumentType<LimsImporterUpdaterDocument>("Naturalis update-File)",
	 * LimsImporterUpdaterDocument.class, null)}; }
	 */

	private void setFieldValuesFromAB1FileName(String ab1FileName) {
		/* for example: e4010125015_Sil_tri_MJ243_COI-A01_M13F_A01_008.ab1 */
		String[] underscore = StringUtils.split(ab1FileName, "_");
		limsAB1Flieds.setExtractID(underscore[0]);
		limsAB1Flieds.setPcrPlaatID(underscore[3]);
		limsAB1Flieds.setMarker(underscore[4]);

	}

	/*
	 * private String getMetaDataRegistrationnumber() { String csvFileName =
	 * null; try { csvFileName = limsImporterUtil.getPropValues() + "BOS.csv"; }
	 * catch (IOException e2) { e2.printStackTrace(); }
	 * 
	 * System.out.println("CSV file: " + csvFileName); CSVReader csvReader =
	 * null; try { csvReader = new CSVReader(new FileReader(csvFileName), ',');
	 * } catch (FileNotFoundException e1) { e1.printStackTrace(); } String[]
	 * record = null; try { csvReader.readNext(); } catch (IOException e) {
	 * e.printStackTrace(); } try { while ((record = csvReader.readNext()) !=
	 * null) { if (record.length == 0) { continue; }
	 * 
	 * Name = record[0]; Registrationnumber = record[1]; CreatedDate =
	 * record[2]; Description = record[3]; ImportedFromFilename = record[4];
	 * ImportedFromPath = record[5]; Modified = record[6]; Size = record[7]; } }
	 * catch (IOException e) { e.printStackTrace(); } try { csvReader.close(); }
	 * catch (IOException e) { e.printStackTrace(); } return Registrationnumber;
	 * }
	 */
}
