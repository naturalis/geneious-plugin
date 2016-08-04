/**
 * 
 */
package nl.naturalis.lims2.ab1.importer;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.naturalis.lims2.utils.LimsAB1Fields;
import nl.naturalis.lims2.utils.LimsFrameProgress;
import nl.naturalis.lims2.utils.LimsImporterUtil;
import nl.naturalis.lims2.utils.LimsNotes;
import nl.naturalis.lims2.utils.LimsReadGeneiousFieldsValues;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biomatters.geneious.publicapi.components.Dialogs;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.documents.PluginDocument;
import com.biomatters.geneious.publicapi.documents.sequence.NucleotideSequenceDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentAction;
import com.biomatters.geneious.publicapi.plugin.DocumentOperationException;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsImportAB1Update extends DocumentAction {

	PluginDocument documentFileName = null;
	LimsNotes limsNotes = new LimsNotes();
	private LimsAB1Fields limsAB1Fields = new LimsAB1Fields();
	private LimsImporterUtil limsImporterUtil = new LimsImporterUtil();
	private static final Logger logger = LoggerFactory
			.getLogger(LimsImportAB1Update.class);

	private List<String> msgList = new ArrayList<String>();
	private int versienummer = 0;
	private boolean isVersionnumberOne = false;
	LimsFileSelector fcd = new LimsFileSelector();
	LimsFrameProgress limsFrameProgress = new LimsFrameProgress();
	private LimsReadGeneiousFieldsValues ReadGeneiousFieldsValues = new LimsReadGeneiousFieldsValues();
	private String extractAb1FastaFileName = "";
	private boolean fastaFileExists = false;
	private boolean fileExists = false;
	private File file = null;
	private Boolean extractValue = false;

	/**
	 * ActionPerformed start the process of the selected documents and read the
	 * data from the files selected "Samples, CRs and BOLD"
	 * 
	 * */
	@Override
	public void actionPerformed(
			AnnotatedPluginDocument[] annotatedPluginDocuments) {

		/* if no document selected show a message. */
		if (DocumentUtilities.getSelectedDocuments().isEmpty()) {
			EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					Dialogs.showMessageDialog("Select all documents");
					return;
				}
			});
		}

		/**
		 * If documents selected then start the process.
		 **/
		if (!DocumentUtilities.getSelectedDocuments().isEmpty()) {

			Object documentFileImportPath = "";

			/* Get Databasename */
			ReadGeneiousFieldsValues.activeDB = ReadGeneiousFieldsValues
					.getServerDatabaseServiceName();

			/* Create the dialog GUI to see the processing of the documents */
			limsFrameProgress.createProgressGUI();
			logger.info("----------------------------S T A R T -------------------------------");
			/*
			 * Looping thru the selected documents that will be updated with
			 * data
			 */
			for (int cnt = 0; cnt < DocumentUtilities.getSelectedDocuments()
					.size(); cnt++) {

				/*
				 * If selected document is a De Novo Assemble do not process the
				 * document
				 */
				if (DocumentUtilities.getSelectedDocuments().get(cnt).getName()
						.contains("Reads Assembly Contig")) {
					continue;
				}

				/*
				 * Set selected document content to variable PluginDocument
				 * documentFileName
				 */
				try {
					documentFileName = DocumentUtilities.getSelectedDocuments()
							.get(cnt).getDocument();
				} catch (DocumentOperationException e3) {
					e3.printStackTrace();
				}

				/* Check if the file exists in the database */
				fileExists = ReadGeneiousFieldsValues
						.fileNameExistsInGeneiousDatabase(documentFileName
								.getName());

				/*
				 * If true and recordcount > 1 version exists else check in the
				 * selected document content if version exists
				 */
				if (fileExists && ReadGeneiousFieldsValues.recordcount >= 1) {
					isVersionnumberOne = true;
				} else {
					isVersionnumberOne = documentFileName.toString().contains(
							"DocumentVersionCode_Seq");
				}

				/*
				 * Get the value from 'ExtractIDCode_Seq' from the selected
				 * document.
				 */
				extractValue = ReadGeneiousFieldsValues
						.getValueFromAnnotatedPluginDocument(
								annotatedPluginDocuments[cnt],
								"DocumentNoteUtilities-Extract ID (Seq)",
								"ExtractIDCode_Seq");

				/*
				 * Get the import path from the selected document
				 * "C:\Git\Data\Fasta files"
				 */
				documentFileImportPath = DocumentUtilities
						.getSelectedDocuments().get(cnt).getDocumentNotes(true)
						.getNote("importedFrom").getFieldValue("path");

				/*
				 * If there is no Version number in the document(Database) set
				 * version value to "1".
				 */
				if (!isVersionnumberOne && !fileExists) {
					versienummer = 1;
				}

				/* if file exists in the database */
				if (fileExists) {
					if (!(documentFileName.getName().toString().contains("ab1") || !documentFileName
							.getName().toString().contains("dum"))) {

						try {
							file = new File(fcd.loadFastaFile(
									documentFileName.getName(),
									(String) documentFileImportPath));
							if (file.getPath().length() > 0) {
								extractAb1FastaFileName = file.getName();
							} else {
								return;
							}
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						}

						fastaFileExists = ReadGeneiousFieldsValues
								.checkOfFastaOrAB1Exists(
										extractAb1FastaFileName,
										"plugin_document_xml",
										"//XMLSerialisableRootElement/name");

						versienummer = ReadGeneiousFieldsValues
								.getLastVersion_For_AB1_Fasta(extractAb1FastaFileName);

					} else {
						extractAb1FastaFileName = documentFileName.getName();
						versienummer = ReadGeneiousFieldsValues
								.getLastVersion_For_AB1_Fasta(extractAb1FastaFileName);
					}
				}

				if (documentFileName.getName() != null
						&& documentFileName.getName().toString().contains("_")) {
					logger.info("Start extracting value from file: "
							+ documentFileName.getName());
					msgList.add(documentFileName.getName());

					if (documentFileName.getName().contains("ab1")) {
						limsAB1Fields
								.setFieldValuesFromAB1FileName(documentFileName
										.getName());
						if (fileExists && !extractValue) {
							versienummer++;
						}

					} else if (extractAb1FastaFileName.trim() != null) {
						logger.info("Fasta file to extract : "
								+ extractAb1FastaFileName);
						limsAB1Fields
								.setFieldValuesFromAB1FileName(extractAb1FastaFileName);
						if (fastaFileExists && !extractValue) {
							versienummer++;
						}
					}

					if (fastaFileExists && !extractValue) {
						limsAB1Fields.setVersieNummer(versienummer);
					} else if (fileExists && !extractValue) {
						limsAB1Fields.setVersieNummer(versienummer);
					}

					logger.info("Extract ID: " + limsAB1Fields.getExtractID());
					logger.info("PCR plaat ID: "
							+ limsAB1Fields.getPcrPlaatID());
					logger.info("Marker: " + limsAB1Fields.getMarker());
					logger.info("Versienummer: "
							+ limsAB1Fields.getVersieNummer());

					/** set note for Extract-ID */
					limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
							"ExtractIDCode_Seq", "Extract ID (Seq)",
							"Extract ID (Seq)", limsAB1Fields.getExtractID(),
							cnt);

					/** set note for PCR Plate ID */
					limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
							"PCRplateIDCode_Seq", "PCR plate ID (Seq)",
							"PCR plate ID (Seq)",
							limsAB1Fields.getPcrPlaatID(), cnt);

					/** set note for Marker */
					limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
							"MarkerCode_Seq", "Marker (Seq)", "Marker (Seq)",
							limsAB1Fields.getMarker(), cnt);

					/** set note for Document version */
					if (fileExists && !extractValue) {
						limsNotes.setNoteToAB1FileName(
								annotatedPluginDocuments,
								"DocumentVersionCode_Seq", "Document version",
								"Document version",
								Integer.toString(versienummer), cnt);
					}

					/* set note for SequencingStaffCode_FixedValue_Seq */
					try {
						limsNotes
								.setNoteToAB1FileName(
										annotatedPluginDocuments,
										"SequencingStaffCode_FixedValue_Seq",
										"Seq-staff (Seq)",
										"Seq-staff (Seq)",
										limsImporterUtil
												.getPropValues("seqsequencestaff"),
										cnt);
					} catch (IOException e) {
						e.printStackTrace();
					}

					limsNotes.setNoteDropdownFieldToFileName(
							annotatedPluginDocuments,
							limsNotes.ConsensusSeqPass,
							"ConsensusSeqPassCode_Seq", "Pass (Seq)",
							"Pass (Seq)", null, cnt);

					limsFrameProgress.showProgress("Processing: "
							+ DocumentUtilities.getSelectedDocuments().get(cnt)
									.getName());
					logger.info("Done with adding notes to the document");
				}

			}

			logger.info("Total of document(s) updated: "
					+ DocumentUtilities.getSelectedDocuments().size());
			logger.info("------------------------- E N D--------------------------------------");
			if ((documentFileName.getName().toString().contains("ab1") || documentFileName
					.getName().toString().contains("dum"))) {
				logger.info("Done with extracting Ab1/Dummy file name. ");
			} else {
				logger.info("Done with extracting Fas file name. ");
			}
			EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					String filename = "";
					if ((documentFileName.getName().toString().contains("ab1") || documentFileName
							.getName().toString().contains("dum"))) {
						filename = "AB1";
					} else {
						filename = "FAS";
					}
					Dialogs.showMessageDialog(filename + "-Update: "
							+ Integer.toString(msgList.size())
							+ " documents are update." + "\n"
							+ msgList.toString());
					logger.info(filename
							+ "-Update: Total imported document(s): "
							+ msgList.toString());

					msgList.clear();
					limsFrameProgress.hideFrame();
				}
			});
		}
	}

	@Override
	public GeneiousActionOptions getActionOptions() {
		return new GeneiousActionOptions("5 Split name").setInPopupMenu(true)
				.setMainMenuLocation(GeneiousActionOptions.MainMenu.Tools, 4.0)
				.setInMainToolbar(true).setInPopupMenu(true)
				.setAvailableToWorkflows(true);
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

}