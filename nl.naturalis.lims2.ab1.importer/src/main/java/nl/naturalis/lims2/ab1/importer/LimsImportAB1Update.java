/**
 * <h1>Lims Split name Plugin</h1> 
 * <p>
 *  category Lims Import Split Name plugin</br>
 *  Date 08 august 2016</br> 
 *  Company Naturalis Biodiversity Center City</br>
 *  Leiden Country Netherlands
 * </p>
 */
package nl.naturalis.lims2.ab1.importer;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.naturalis.lims2.utils.LimsAB1Fields;
import nl.naturalis.lims2.utils.LimsDatabaseChecker;
import nl.naturalis.lims2.utils.LimsFrameProgress;
import nl.naturalis.lims2.utils.LimsImporterUtil;
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
import com.biomatters.geneious.publicapi.plugin.DocumentOperationException;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;

/**
 * <table>
 * <tr>
 * <td>
 * Date: 24 august 2016</td>
 * </tr>
 * <tr>
 * <td>
 * Company: Naturalis Biodiversity Center</td>
 * </tr>
 * <tr>
 * <td>
 * City: Leiden</td>
 * </tr>
 * <tr>
 * <td>
 * Country: Netherlands</td>
 * </tr>
 * <tr>
 * <td>
 * Description:<br>
 * Extract values from the AB1 and Fasta filename and add the values to the
 * documents notes.<br>
 * </td>
 * </tr>
 * </table>
 * 
 * @author Reinier.Kartowikromo
 * @version: 1.0
 */
public class LimsImportAB1Update extends DocumentAction {

	private SequenceDocument documentFileName = null;
	private LimsNotes limsNotes = new LimsNotes();
	private LimsAB1Fields limsAB1Fields = new LimsAB1Fields();
	private LimsImporterUtil limsImporterUtil = new LimsImporterUtil();
	private static final Logger logger = LoggerFactory
			.getLogger(LimsImportAB1Update.class);

	private List<String> msgList = new ArrayList<String>();
	private int versienummer = 0;
	private boolean isVersionnumberOne = false;
	private LimsFileSelector fcd = new LimsFileSelector();
	private LimsFrameProgress limsFrameProgress = new LimsFrameProgress();
	private LimsReadGeneiousFieldsValues ReadGeneiousFieldsValues = new LimsReadGeneiousFieldsValues();
	private String extractAb1FastaFileName = "";
	private boolean fastaFileExists = false;
	private boolean fileExists = false;
	private File file = null;
	private Boolean extractValue = false;

	/**
	 * ActionPerformed start the process of the selected documents and read the
	 * data from the files selected "Samples, CRS and BOLD"
	 * 
	 * @param annotatedPluginDocuments
	 *            Set param annotatedPluginDocuments
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

		/*
		 * If documents selected then start the process.
		 */
		if (!DocumentUtilities.getSelectedDocuments().isEmpty()) {

			Object documentFileImportPath = "";

			// getDatabaseURL();

			LimsDatabaseChecker dbchk = new LimsDatabaseChecker();
			if (!dbchk.checkDBName()) {
				return;
			}

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
				setDocumentFileName(cnt);

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
				if (documentFileName.getName().contains("dum")) {
					continue;
				} else {
					documentFileImportPath = DocumentUtilities
							.getSelectedDocuments().get(cnt)
							.getDocumentNotes(true).getNote("importedFrom")
							.getFieldValue("path");
				}
				/*
				 * If there is no Version number in the document(Database) set
				 * version value to "1".
				 */
				if (!isVersionnumberOne && !fileExists) {
					versienummer = 1;
				}

				/* if file exists in the database */
				if (fileExists) {
					if (!(documentFileName.getName().toString().contains("ab1") || documentFileName
							.getName().toString().contains("dum"))) {

						/* Get the filename from the Fasta content */
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

						/* Check if fasta file exists in the database */
						fastaFileExists = ReadGeneiousFieldsValues
								.checkOfFastaOrAB1Exists(
										extractAb1FastaFileName,
										"plugin_document_xml",
										"//XMLSerialisableRootElement/name");

						/*
						 * Get the last version number from the last insert
						 * fasta file.
						 */
						versienummer = ReadGeneiousFieldsValues
								.getLastVersion_For_AB1_Fasta(extractAb1FastaFileName);

					} else {
						/* Get AB1 Filename and Version from AB1 file */
						extractAb1FastaFileName = documentFileName.getName();
						versienummer = ReadGeneiousFieldsValues
								.getLastVersion_For_AB1_Fasta(extractAb1FastaFileName);
					}
				}

				/* AB1 File extracten */
				extractAB1File(annotatedPluginDocuments, cnt);

			} // end for loop

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

				/**
				 * Show message dialog screen with processed infoof the
				 * document(s)
				 * */
				@Override
				public void run() {

					Dialogs.showMessageDialog(getSplitFileName(documentFileName)
							+ "_Update: "
							+ Integer.toString(msgList.size())
							+ " documents are updated.");

					logger.info(getSplitFileName(documentFileName)
							+ "-Update: Total imported document(s): "
							+ msgList.toString());

					msgList.clear();
					limsFrameProgress.hideFrame();
				}

				private String getSplitFileName(SequenceDocument document) {
					if ((document.getName().contains("ab1") || document
							.getName().contains("dum")))
						return "AB1";
					return "Fasta";

				}
			});
		}
	}

	/*
	 * Get the Document content
	 * 
	 * @param cnt
	 */
	private void setDocumentFileName(int cnt) {
		try {
			documentFileName = (SequenceDocument) DocumentUtilities
					.getSelectedDocuments().get(cnt).getDocument();
		} catch (DocumentOperationException e3) {
			e3.printStackTrace();
		}
	}

	/*
	 * Extract AB1 document(s)
	 * 
	 * @param annotatedPluginDocuments , int
	 */
	private void extractAB1File(
			AnnotatedPluginDocument[] annotatedPluginDocuments, int cnt) {
		if (documentFileName.getName() != null
				&& documentFileName.getName().toString().contains("_")) {
			logger.info("Start extracting value from file: "
					+ documentFileName.getName());
			msgList.add(documentFileName.getName());

			if (documentFileName.getName().contains("ab1")) {
				/* Extract values from the AB1 filename */
				limsAB1Fields.extractAB1_FastaFileName(documentFileName
						.getName());
				/*
				 * if file exists and is not extravalue "ExtractIDCode_Seq"
				 * increase Version number.
				 */
				if (fileExists && !extractValue) {
					versienummer++;
				}

			} else if (extractAb1FastaFileName.trim() != null) {
				logger.info("Fasta file to extract : "
						+ extractAb1FastaFileName);
				/* Extract values from the Fasta filename */
				limsAB1Fields
						.extractAB1_FastaFileName(extractAb1FastaFileName);
				/*
				 * if file exists and is not extravalue "ExtractIDCode_Seq"
				 * increase Version number.
				 */
				if (fastaFileExists && !extractValue) {
					versienummer++;
				}
			}

			/* Set version number Fasta file and AB1 */
			if (fastaFileExists && !extractValue) {
				limsAB1Fields.setVersieNummer(versienummer);
			} else if (fileExists && !extractValue) {
				limsAB1Fields.setVersieNummer(versienummer);
			}

			/* Processing the notes */
			setSplitDocumentsNotes(annotatedPluginDocuments, cnt);
		}
	}

	/*
	 * Add notes for the Split plugin
	 * 
	 * @param annotatedPluginDocuments , int
	 */
	private void setSplitDocumentsNotes(
			AnnotatedPluginDocument[] annotatedPluginDocuments, int cnt) {
		logger.info("Extract ID: " + limsAB1Fields.getExtractID());
		logger.info("PCR plaat ID: " + limsAB1Fields.getPcrPlaatID());
		logger.info("Marker: " + limsAB1Fields.getMarker());
		logger.info("Versienummer: " + limsAB1Fields.getVersieNummer());

		/* set note for Extract-ID */
		limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
				"ExtractIDCode_Seq", "Extract ID (Seq)", "Extract ID (Seq)",
				limsAB1Fields.getExtractID(), cnt);

		/* set note for PCR Plate ID */
		limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
				"PCRplateIDCode_Seq", "PCR plate ID (Seq)",
				"PCR plate ID (Seq)", limsAB1Fields.getPcrPlaatID(), cnt);

		/* set note for Marker */
		limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
				"MarkerCode_Seq", "Marker (Seq)", "Marker (Seq)",
				limsAB1Fields.getMarker(), cnt);

		/* set note for Document version */
		if (fileExists && !extractValue) {
			limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
					"DocumentVersionCode_Seq", "Document version",
					"Document version", Integer.toString(versienummer), cnt);
		}

		/* set note for SequencingStaffCode_FixedValue_Seq */
		try {
			limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
					"SequencingStaffCode_FixedValue_Seq", "Seq-staff (Seq)",
					"Seq-staff (Seq)",
					limsImporterUtil.getPropValues("seqsequencestaff"), cnt);
		} catch (IOException e) {
			e.printStackTrace();
		}

		/* Set note ConsensusSeqPassCode_Seq */
		limsNotes.setNoteDropdownFieldToFileName(annotatedPluginDocuments,
				limsNotes.ConsensusSeqPass, "ConsensusSeqPassCode_Seq",
				"Pass (Seq)", "Pass (Seq)", null, cnt);

		/* Show processing dialog */
		limsFrameProgress.showProgress("Processing: "
				+ DocumentUtilities.getSelectedDocuments().get(cnt).getName());
		logger.info("Done with adding notes to the document");
	}

	/**
	 * Add plugin 5 Split name to the menubar
	 * 
	 * @return Add the button to the menubar
	 * @see LimsImportAB1Update
	 * */
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

	/**
	 * Add the max value of selected document(s)<br>
	 * public static final int MAX_VALUE = 2147483647;
	 * 
	 * @return Return the count value of imported documents
	 * @see LimsImportAB1Update
	 * */
	@Override
	public DocumentSelectionSignature[] getSelectionSignatures() {
		return new DocumentSelectionSignature[] { new DocumentSelectionSignature(
				NucleotideSequenceDocument.class, 0, Integer.MAX_VALUE) };
	}

}