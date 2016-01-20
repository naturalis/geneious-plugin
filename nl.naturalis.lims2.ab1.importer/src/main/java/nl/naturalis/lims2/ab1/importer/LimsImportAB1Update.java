/**
 * 
 */
package nl.naturalis.lims2.ab1.importer;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jebl.util.ProgressListener;
import nl.naturalis.lims2.utils.LimsAB1Fields;
import nl.naturalis.lims2.utils.LimsNotes;
import nl.naturalis.lims2.utils.LimsReadGeneiousFieldsValues;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biomatters.geneious.publicapi.components.Dialogs;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.documents.sequence.NucleotideSequenceDocument;
import com.biomatters.geneious.publicapi.documents.sequence.SequenceDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentAction;
import com.biomatters.geneious.publicapi.plugin.DocumentFileImporter.ImportCallback;
import com.biomatters.geneious.publicapi.plugin.DocumentImportException;
import com.biomatters.geneious.publicapi.plugin.DocumentOperationException;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;
import com.biomatters.geneious.publicapi.plugin.PluginUtilities;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsImportAB1Update extends DocumentAction {

	// private LimsImporterUtil limsImporterUtil = new LimsImporterUtil();

	SequenceDocument seq;
	LimsNotes limsNotes = new LimsNotes();
	private LimsAB1Fields limsAB1Fields = new LimsAB1Fields();
	private List<AnnotatedPluginDocument> docs;
	private List<AnnotatedPluginDocument> document;
	private ImportCallback importCallback;
	private static final Logger logger = LoggerFactory
			.getLogger(LimsImportAB1Update.class);
	private LimsReadGeneiousFieldsValues readVersionNumberValue = new LimsReadGeneiousFieldsValues();
	private Object versionNumber = "";

	private final String noteCode = "DocumentNoteUtilities-Document version";
	private final String fieldName = "DocumentversionCode";
	private final String noteBOS = "DocumentNoteUtilities-Registrationnumber (Samples)";
	private final String fieldBOS = "RegistrationnumberCode_Samples";

	private List<String> msgList = new ArrayList<String>();
	LimsFileSelector fcd = new LimsFileSelector();

	// private AnnotatedPluginDocument annotatedPluginDocument;

	@Override
	public void actionPerformed(
			AnnotatedPluginDocument[] annotatedPluginDocuments) {

		logger.info("----------------------------S T A R T -------------------------------");
		try {
			docs = DocumentUtilities.getSelectedDocuments();
			AnnotatedPluginDocument annotatedPluginDocument = docs.iterator()
					.next();

			for (int cnt = 0; cnt < docs.size(); cnt++) {
				seq = (SequenceDocument) docs.get(cnt).getDocument();
				System.out.println("Sequence name: " + seq.getName());

				if (seq.getName() != null && seq.getName().contains("_")) {
					logger.info("Start extracting value from file: "
							+ seq.getName());
					msgList.add(seq.getName());

					limsAB1Fields.setFieldValuesFromAB1FileName(seq.getName());

					logger.info("Extract ID: " + limsAB1Fields.getExtractID());
					logger.info("PCR plaat ID: "
							+ limsAB1Fields.getPcrPlaatID());
					logger.info("Marker: " + limsAB1Fields.getMarker());
					logger.info("Versienummer: "
							+ limsAB1Fields.getVersieNummer());

					/** set note for Extract-ID */
					limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
							"ExtractIdCode_Seq", "Extract ID (Seq)",
							"Extract ID (Seq)", limsAB1Fields.getExtractID(),
							cnt);

					/** set note for PCR Plate ID */
					limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
							"PcrPlateIdCode_Seq", "PCR plate ID (Seq)",
							"PCR plate ID (Seq)",
							limsAB1Fields.getPcrPlaatID(), cnt);

					/** set note for Marker */
					limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
							"MarkerCode_Seq", "Marker (Seq)", "Marker (Seq)",
							limsAB1Fields.getMarker(), cnt);

					/** set note for Document version */
					limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
							"DocumentversionCode", "Document version",
							"Document version",
							limsAB1Fields.getVersieNummer(), cnt);
				}

				versionNumber = readVersionNumberValue
						.readValueFromAnnotatedPluginDocument(
								annotatedPluginDocument, noteCode, fieldName);

				if (seq.getName().contains("New Sequence")
						&& versionNumber.equals("0")) {

					String fileSelected = fcd.loadSelectedFile();
					if (fileSelected == null) {
						return;
					}

					File file = new File(fileSelected);

					try {
						document = PluginUtilities.importDocuments(file,
								ProgressListener.EMPTY);
						annotatedPluginDocument = document.iterator().next();
						DocumentUtilities.addGeneratedDocument(
								annotatedPluginDocument, true);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (DocumentImportException e) {
						e.printStackTrace();
					}

					String fileName = fileSelected.substring(fileSelected
							.indexOf("\\e") + 1);

					for (int counter = 0; counter < document.size(); counter++) {

						logger.info("-------------------------- S T A R T --------------------------");
						logger.info("Start Reading data from a excel file.");

						seq = (SequenceDocument) docs.get(cnt).getDocument();
						limsAB1Fields.setFieldValuesFromAB1FileName(fileName);

						msgList.add(fileSelected);

						/* set note for Extract-ID */
						try {
							limsNotes.setImportNotes(docs.iterator().next(),
									"ExtractIdCode", "Extract ID",
									"Extract-ID", limsAB1Fields.getExtractID());
						} catch (Exception ex) {
							ex.printStackTrace();
						}

						/* set note for PCR Plaat-ID */
						try {
							limsNotes.setImportNotes(docs.iterator().next(),
									"PcrPlaatIdCode", "PCR plaat ID",
									"PCR plaat ID",
									limsAB1Fields.getPcrPlaatID());
						} catch (Exception ex) {
							ex.printStackTrace();
						}

						/* set note for Marker */
						try {
							limsNotes.setImportNotes(docs.iterator().next(),
									"MarkerCode", "Marker", "Marker",
									limsAB1Fields.getMarker());
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						/* set note for Marker */
						try {
							limsNotes.setImportNotes(docs.iterator().next(),
									"VersieCode", "Version number",
									"Version number",
									limsAB1Fields.getVersieNummer());
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						logger.info("Done with adding notes to the document");
					}

				}

			}
		} catch (DocumentOperationException e) {
			try {
				throw new Exception();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

		logger.info("Total of document(s) updated: " + docs.size());
		logger.info("------------------------- E N D--------------------------------------");
		logger.info("Done with extracting Ab1 file name. ");
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				Dialogs.showMessageDialog("AB1-Update: "
						+ Integer.toString(msgList.size())
						+ " documents are update." + "\n" + msgList.toString());
				logger.info("AB1-Update: Total imported document(s): "
						+ msgList.toString());
				msgList.clear();
			}
		});
	}

	@Override
	public GeneiousActionOptions getActionOptions() {
		return new GeneiousActionOptions("Update AB1 files")
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
	 * public DocumentFileImporter[] getDocumentFileImporters() { return new
	 * DocumentFileImporter[] { new DocumentFileImporter() {
	 * 
	 * @Override public String getFileTypeDescription() { return
	 * "Naturalis Dummy Extract AB1 Filename Importer"; }
	 * 
	 * @Override public String[] getPermissibleExtensions() { return new
	 * String[] { "ab1", "abi" }; }
	 * 
	 * @Override public void importDocuments(File arg0, ImportCallback arg1,
	 * ProgressListener arg2) throws IOException, DocumentImportException { //
	 * TODO Auto-generated method stub
	 * 
	 * }
	 * 
	 * @Override public AutoDetectStatus tentativeAutoDetect(File arg0, String
	 * arg1) { // TODO Auto-generated method stub return null; } }
	 * 
	 * }; };
	 */
}