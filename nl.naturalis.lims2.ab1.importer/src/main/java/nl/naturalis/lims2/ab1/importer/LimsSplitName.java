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
import java.util.ArrayList;
import java.util.List;

import nl.naturalis.lims2.utils.LimsAB1Fields;
import nl.naturalis.lims2.utils.LimsDatabaseChecker;
import nl.naturalis.lims2.utils.LimsFrameProgress;
import nl.naturalis.lims2.utils.LimsImporterUtil;
import nl.naturalis.lims2.utils.LimsLogger;
import nl.naturalis.lims2.utils.LimsNotesSplitName;
import nl.naturalis.lims2.utils.LimsReadGeneiousFieldsValues;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biomatters.geneious.publicapi.components.Dialogs;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.documents.sequence.NucleotideSequenceDocument;
import com.biomatters.geneious.publicapi.documents.sequence.SequenceDocument;
import com.biomatters.geneious.publicapi.implementations.DefaultAlignmentDocument;
import com.biomatters.geneious.publicapi.implementations.sequence.DefaultNucleotideGraphSequence;
import com.biomatters.geneious.publicapi.implementations.sequence.DefaultNucleotideSequence;
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
public class LimsSplitName extends DocumentAction {

	// private SequenceDocument documentFileName = null;
	private LimsAB1Fields limsAB1Fields = new LimsAB1Fields();
	private LimsImporterUtil limsImporterUtil = new LimsImporterUtil();
	private static final Logger logger = LoggerFactory
			.getLogger(LimsSplitName.class);
	private LimsNotesSplitName limsSplitNotes = new LimsNotesSplitName();
	private LimsLogger limsLogger = null;

	private List<String> verwerkingList = new ArrayList<String>();
	private List<String> uitValList = new ArrayList<String>();
	private int versienummer = 0;
	private boolean versionNumberExists = false;
	private LimsFileSelector fcd = new LimsFileSelector();
	private LimsFrameProgress limsFrameProgress = new LimsFrameProgress();
	private LimsReadGeneiousFieldsValues readGeneiousFieldsValues = new LimsReadGeneiousFieldsValues();
	private Object extractAb1FastaFileName;
	private boolean fileExists = false;
	private File file = null;
	private Boolean extractIDValue = false;
	private long startBeginTime = 0;
	private String documentDescription;
	private DefaultNucleotideGraphSequence defNucleotideGraphSequence = new DefaultNucleotideGraphSequence();
	private DefaultAlignmentDocument defAlignmentDoc = new DefaultAlignmentDocument();

	/* FASTA */
	private DefaultNucleotideSequence defNucleotideSequence = new DefaultNucleotideSequence();

	private Object documentFileImportPath = "";

	private LimsDatabaseChecker dbchk = new LimsDatabaseChecker();
	private ArrayList<AnnotatedPluginDocument> selectedDocuments = new ArrayList<AnnotatedPluginDocument>();

	private final String readsAssemblyContig = "Reads Assembly Contig";
	private final String readsAssembyConsensusContig = "consensus sequence";
	private final String readsAssemblyFasta = "Reads Assembly";

	private final String defaultNucleotideGraphSequence = "DefaultNucleotideGraphSequence";
	private final String defaultAlignmentDocument = "DefaultAlignmentDocument";
	private final String defaultNucleotideSequence = "DefaultNucleotideSequence";

	private Object filePathExists = null;

	private final String ab1FileExtension = "ab1";
	private final String fastaFileExtension = "fas";
	private String fastFilename;
	private String docType;
	private String logSplitFileName;

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
		isSelectedDocumentEmpty();

		/*
		 * If documents selected then start the process.
		 */
		if (!DocumentUtilities.getSelectedDocuments().isEmpty()) {

			// getDatabaseURL();
			if (!dbchk.checkDBName()) {
				return;
			}

			/* Get Databasename */
			if (readGeneiousFieldsValues.activeDB.length() == 0) {
				readGeneiousFieldsValues.activeDB = readGeneiousFieldsValues
						.getServerDatabaseServiceName();
			}

			logSplitFileName = limsImporterUtil.getLogPath()
					+ "Splitname-Uitvallijst-"
					+ limsImporterUtil.getLogFilename();

			/* Create logfile */
			limsLogger = new LimsLogger(logSplitFileName);

			/* Create the dialog GUI to see the processing of the documents */
			limsFrameProgress.createProgressGUI();
			logger.info("----------------------------S T A R T -------------------------------");
			/*
			 * Looping thru the selected documents that will be updated with
			 * data
			 */

			selectedDocuments = (ArrayList<AnnotatedPluginDocument>) DocumentUtilities
					.getSelectedDocuments();

			for (int cnt = 0; cnt < selectedDocuments.size(); cnt++) {
				startBeginTime = System.nanoTime();
				fastFilename = "";
				defAlignmentDoc = null;
				defNucleotideSequence = null;
				defNucleotideGraphSequence = null;
				versienummer = 0;
				versionNumberExists = false;

				extractDataFromDescription(cnt, selectedDocuments.get(cnt)
						.getName());

				try {
					docType = (String) DocumentUtilities.getSelectedDocuments()
							.get(cnt).getDocument().getClass().getTypeName();
				} catch (DocumentOperationException e) {
					throw new RuntimeException(e);
				}

				/*
				 * Set selected document content to variable PluginDocument
				 * documentFileName Bepaal om welk type document het gaat.
				 */
				// - Sequence Document
				// - DefaultAlignmentSequence Document
				// - DefaultNucleotideGraphSequence Document
				// - DefaultNucleotideSequence Document
				fastFilename = getFileNameFromDocument(cnt);

				if (fastFilename != null && !fastFilename.isEmpty()
						&& !fastFilename.contains(ab1FileExtension)) {
					getFastaSelectedDocumentsType(cnt,
							selectedDocuments.get(cnt).getName());
				} else {
					getAB1SelectedDocumentsType(cnt, selectedDocuments.get(cnt)
							.getName());
				}

				/* Check if the file exists in the database */
				checkIfFileExsistInDatabase(cnt, selectedDocuments.get(cnt)
						.getName().toString());

				/*
				 * Get the value from 'ExtractIDCode_Seq' from the selected
				 * document.
				 */
				extractIDValue = readGeneiousFieldsValues
						.getValueFromAnnotatedPluginDocument(
								annotatedPluginDocuments[cnt],
								"DocumentNoteUtilities-Extract ID (Seq)",
								"ExtractIDCode_Seq");

				filePathExists = readGeneiousFieldsValues
						.getValueFromAnnotatedPluginDocument(
								annotatedPluginDocuments[cnt], "importedFrom",
								"path");

				/*
				 * Get the import path from the selected document
				 * "C:\Git\Data\Fasta files"
				 */
				if (selectedDocuments.get(cnt).getName().contains("dum")) {
					continue;
				} else if ((boolean) filePathExists) {
					documentFileImportPath = getPathFromDocument(cnt);
				}

				/*
				 * If true and recordcount > 1 version exists else check in the
				 * selected document content if version exists
				 */
				checkIfVersionExsist(cnt);

				/*
				 * If there is no Version number in the document(Database) set
				 * version value to "1".
				 */
				if (!versionNumberExists && !fileExists) {
					versienummer = 1;
				}

				/* if file exists in the database */
				if (fileExists && (boolean) filePathExists) {
					ExtractFilenameAndVersienumber(annotatedPluginDocuments,
							cnt);
				} else {
					/*
					 * Bij AB1 Novo Assembly document bevat geen notes Filepath
					 * en Filename. Wel bij de Fasta Novo Assembly
					 */
					ExtractFilenameAndVersienumber(annotatedPluginDocuments,
							cnt);
				}

				/* AB1 File extracten */
				enrichAB1AndFastaFileWith_Notes(annotatedPluginDocuments, cnt);

				limsImporterUtil.calculateTimeForAddingNotes(startBeginTime);
			} // end for loop

			logger.info("Total of document(s) updated: "
					+ DocumentUtilities.getSelectedDocuments().size());
			logger.info("------------------------- E N D--------------------------------------");
			setDefaultAlignmentDocumentLog();
			setDefaultNucleotideGraphSequenceLog();
			setDefaultNucleotideSequenceLog();

			EventQueue.invokeLater(new Runnable() {

				/**
				 * Show message dialog screen with processed infoof the
				 * document(s)
				 * */
				@Override
				public void run() {
					if (defNucleotideSequence != null
							&& !defNucleotideSequence.getName().contains(
									readsAssembyConsensusContig)) {
						showFastaDialog();
					} else if (defAlignmentDoc != null) {
						showDefaultAlignmentDialog();
					} else if (defNucleotideGraphSequence != null
							&& docType.contains(defaultNucleotideGraphSequence)) {
						showNucleotideGraphSequenceDialog();
					} else if (defNucleotideSequence != null
							&& defNucleotideSequence.getName().contains(
									readsAssembyConsensusContig)) {
						showNucleotideSequenceDialog();
					} else {
						showAllSelectedDocumentsDialog();
					}
					verwerkingList.clear();
					limsFrameProgress.hideFrame();
				}

				/**
				 * 
				 */
				private void showNucleotideGraphSequenceDialog() {
					Dialogs.showMessageDialog(defNucleotideGraphSequence
							.getName()
							+ " : "
							+ Integer.toString(selectedDocuments.size())
							+ " documents are updated.");

					logger.info(defNucleotideGraphSequence.getName()
							+ "-Update: Total imported document(s): "
							+ verwerkingList.toString());
					defNucleotideGraphSequence = null;
				}

				private void showNucleotideSequenceDialog() {
					Dialogs.showMessageDialog(defNucleotideSequence.getName()
							+ " : "
							+ Integer.toString(selectedDocuments.size())
							+ " documents are updated.");

					logger.info(defNucleotideSequence.getName()
							+ "-Update: Total imported document(s): "
							+ verwerkingList.toString());
					defNucleotideSequence = null;
				}

				/**
				 * 
				 */
				private void showDefaultAlignmentDialog() {
					Dialogs.showMessageDialog(defAlignmentDoc.getName()
							+ "_Update: "
							+ Integer.toString(selectedDocuments.size())
							+ " documents are updated.");

					logger.info("Reads Assembly Contig-Update: Total imported document(s): "
							+ verwerkingList.toString());
					defAlignmentDoc = null;
				}

				/**
				 * 
				 */
				/*
				 * private void showAB1FastaDialog() {
				 * Dialogs.showMessageDialog(getSplitFileName(documentFileName)
				 * + "_Update: " + Integer.toString(msgList.size()) +
				 * " documents are updated.");
				 * 
				 * logger.info(getSplitFileName(documentFileName) +
				 * "-Update: Total imported document(s): " +
				 * msgList.toString()); documentFileName = null;
				 * msgList.clear(); }
				 */

				private void showFastaDialog() {
					Dialogs.showMessageDialog(defNucleotideSequence.getName()
							+ " : "
							+ Integer.toString(selectedDocuments.size())
							+ " documents are updated.");

					logger.info(defNucleotideSequence.getName()
							+ "-Update: Total imported document(s): "
							+ verwerkingList.toString());
					defNucleotideSequence = null;
				}

				private void showAllSelectedDocumentsDialog() {
					Dialogs.showMessageDialog(Integer
							.toString(selectedDocuments.size())
							+ " selected documents are updated.");

					logger.info("Update: Total imported document(s): "
							+ verwerkingList.toString());
				}

				private String getSplitFileName(SequenceDocument document) {
					if ((document.getName().contains(ab1FileExtension) || document
							.getName().contains("dum")))
						return "AB1";
					return "Fasta";
				}
			});
		}
	}

	/**
	 * @param annotatedPluginDocuments
	 * @param cnt
	 */
	private void ExtractFilenameAndVersienumber(
			AnnotatedPluginDocument[] annotatedPluginDocuments, int cnt) {
		if (selectedDocuments.get(cnt).getName() != null) {
			setDefaultAligmentDocument(cnt);
			setDefaultNucleotideGraphSequence(cnt);
			setDefaultNucleotideSequence(cnt);
		}
	}

	/**
	 * 
	 */
	private void isSelectedDocumentEmpty() {
		if (DocumentUtilities.getSelectedDocuments().isEmpty()) {
			EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					Dialogs.showMessageDialog("Select all documents");
					return;
				}
			});
		}
	}

	/*
	 * Get the Document content
	 * 
	 * @param cnt
	 */
	/*
	 * private void setSequenceDocumentFileName(int cnt) { try {
	 * documentFileName = (SequenceDocument) DocumentUtilities
	 * .getSelectedDocuments().get(cnt).getDocument(); } catch
	 * (DocumentOperationException e) { throw new RuntimeException(e); } }
	 */
	private void setDefaultAlignmentDocumentFileName(int cnt) {
		try {
			defAlignmentDoc = (DefaultAlignmentDocument) DocumentUtilities
					.getSelectedDocuments().get(cnt).getDocument();
		} catch (DocumentOperationException e) {
			throw new RuntimeException(e);
		}
	}

	private void setDefaultNucleotideGraphSequenceFileName(int cnt) {
		try {
			defNucleotideGraphSequence = (DefaultNucleotideGraphSequence) DocumentUtilities
					.getSelectedDocuments().get(cnt).getDocument();
		} catch (DocumentOperationException e) {
			throw new RuntimeException(e);
		}
	}

	private void setDefaultNucleotideSequenceFileName(int cnt) {
		try {
			defNucleotideSequence = (DefaultNucleotideSequence) DocumentUtilities
					.getSelectedDocuments().get(cnt).getDocument();
		} catch (DocumentOperationException e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * Extract AB1 document(s)
	 * 
	 * @param annotatedPluginDocuments , int
	 */
	private void enrichAB1AndFastaFileWith_Notes(
			AnnotatedPluginDocument[] annotatedPluginDocuments, int cnt) {

		documentDescription = "";

		/*
		 * if (documentFileName != null &&
		 * !fastFilename.toString().contains(fastaFileExtension)) { AB1 document
		 * extractAB1SequenceDocument(cnt); } else
		 */
		if (fastFilename != null
				&& fastFilename.toString().contains(fastaFileExtension)) {

			/* Fasta Document */
			if (docType.contains(defaultNucleotideSequence)
					&& selectedDocuments.get(cnt).getName()
							.contains(readsAssembyConsensusContig)) {
				extractDataFromDescription(cnt, fastFilename);
			} else if (docType.contains(defaultNucleotideSequence)
					&& defNucleotideSequence != null
					&& !selectedDocuments.get(cnt).getName()
							.contains(readsAssemblyFasta)) {
				extractFastaSequenceDocument();
			} else if (docType.contains(defaultAlignmentDocument)
					&& selectedDocuments.get(cnt).getName()
							.contains(readsAssemblyFasta)) {
				extractDataFromDescription(cnt, fastFilename);
			}
		} else if (defAlignmentDoc != null
				&& !defAlignmentDoc.toString().isEmpty()) {
			/* DefaultAlignmentDocument */
			extractReadAssemblyContigDocument(cnt);
		} else if (defNucleotideGraphSequence != null
				&& !defNucleotideGraphSequence.toString().isEmpty()) {
			/* Default */
			extractReadAssemblyConsesusSequence(cnt);
		}

		/* Set version number Fasta file and AB1 */
		if (fileExists && !extractIDValue) {
			limsAB1Fields.setVersieNummer(versienummer);
		}

		/* Processing the notes */
		/* Lims 251- Waar is het veld seq quality? */
		limsSplitNotes.enrichSplitDocumentsWithNotes(annotatedPluginDocuments,
				cnt, fileExists, extractIDValue, versienummer);

		if (limsAB1Fields.getExtractID() == null) {
			uitValList.add(selectedDocuments.get(cnt).getName());
			limsLogger.logToFile(logSplitFileName, uitValList.toString());
		}

		/* Show processing dialog */
		limsFrameProgress.showProgress("Processing: "
				+ DocumentUtilities.getSelectedDocuments().get(cnt).getName());
		// logger.info("Done with adding notes to the document");
	}

	/**
	 * Add plugin 5 Split name to the menubar
	 * 
	 * @return Add the button to the menubar
	 * @see LimsSplitName
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
	 * @see LimsSplitName
	 * */
	@Override
	public DocumentSelectionSignature[] getSelectionSignatures() {
		return new DocumentSelectionSignature[] { new DocumentSelectionSignature(
				NucleotideSequenceDocument.class, 0, Integer.MAX_VALUE) };
	}

	/*
	 * private void extractAB1SequenceDocument(int cnt) {
	 * 
	 * if (selectedDocuments.get(cnt).getName().contains(ab1FileExtension) &&
	 * extractAb1FastaFileName != null) {
	 * logger.info("Start extracting value from file: " +
	 * documentFileName.getName()); msgList.add(documentFileName.getName());
	 * Extract values from the AB1 filename Lims 251- Waar is het veld seq
	 * quality? limsSplitNotes .extractDocumentFileName((String)
	 * extractAb1FastaFileName);
	 * 
	 * if file exists and is not extravalue "ExtractIDCode_Seq" increase Version
	 * number.
	 * 
	 * if (fileExists && !extractIDValue) { versienummer++; } } }
	 */

	private void extractFastaSequenceDocument() {
		if (extractAb1FastaFileName != null
				&& fastFilename.toString().contains(fastaFileExtension)) {

			logger.info("Start extracting value from file: "
					+ extractAb1FastaFileName);
			verwerkingList.add(fastFilename);

			/* Extract values from the Fasta filename */
			limsSplitNotes
					.extractDocumentFileName((String) extractAb1FastaFileName);
			/*
			 * if file exists and is not extravalue "ExtractIDCode_Seq" increase
			 * Version number.
			 */
			if (fileExists && !extractIDValue) {
				versienummer++;
			}
		}
	}

	private void extractDataFromDescription(int pCnt, String pFilename) {

		if (pFilename != null) {
			logger.info("Start extracting value from file: " + pFilename);
			verwerkingList.add(pFilename);
			try {
				documentDescription = (String) DocumentUtilities
						.getSelectedDocuments().get(pCnt).getDocument()
						.getDescription();
				if (documentDescription.toString().length() != 0
						&& !documentDescription.isEmpty()) {
					String[] description = StringUtils.split(
							documentDescription, ":");
					String[] filename = StringUtils.splitPreserveAllTokens(
							description[1], ",");
					extractAb1FastaFileName = filename[0];
					limsSplitNotes
							.extractDocumentFileName((String) extractAb1FastaFileName);

					if (versionNumberExists) {
						versienummer = Integer
								.parseInt(getVersionNumberFromDocument(
										fastFilename, pCnt));
					} else {
						versienummer = readGeneiousFieldsValues
								.getLastVersion_For_AB1_Fasta_ConsensusContig((String) extractAb1FastaFileName);
					}

					setVersienummer();
				}
			} catch (DocumentOperationException e) {
				throw new RuntimeException(e);
			}

		}
	}

	private void extractReadAssemblyContigDocument(int pCnt) {
		if (defAlignmentDoc != null) {
			logger.info("Start extracting value from file: "
					+ defAlignmentDoc.getName());
			verwerkingList.add(defAlignmentDoc.getName());
			try {
				documentDescription = (String) DocumentUtilities
						.getSelectedDocuments().get(pCnt).getDocument()
						.getDescription();
				if (documentDescription != null
						&& !documentDescription.isEmpty()) {
					String[] description = StringUtils.split(
							documentDescription, ":");
					String[] filename = StringUtils.splitPreserveAllTokens(
							description[1], ",");
					extractAb1FastaFileName = filename[0];
					limsSplitNotes
							.extractDocumentFileName((String) extractAb1FastaFileName);

				} else {
					limsSplitNotes
							.extractDocumentFileName((String) extractAb1FastaFileName);

				}
			} catch (DocumentOperationException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void extractReadAssemblyConsesusSequence(int pCnt) {
		if (defNucleotideGraphSequence != null) {
			logger.info("Start extracting value from file: "
					+ defNucleotideGraphSequence.getName());
			verwerkingList.add(defNucleotideGraphSequence.getName());

			try {
				documentDescription = (String) DocumentUtilities
						.getSelectedDocuments().get(pCnt).getDocument()
						.getDescription();
				if (documentDescription != null
						&& !documentDescription.isEmpty()) {
					String[] description = StringUtils.split(
							documentDescription, ":");
					String[] filename = StringUtils.splitPreserveAllTokens(
							description[1], ",");
					extractAb1FastaFileName = filename[0];
					limsSplitNotes
							.extractDocumentFileName((String) extractAb1FastaFileName);

				} else {
					limsSplitNotes
							.extractDocumentFileName((String) extractAb1FastaFileName);
				}
			} catch (DocumentOperationException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/*
	 * private void getSelectedDocuments(int pCnt, String documentName) { // -
	 * Sequence Document // - DefaultAlignmentSequence Document // -
	 * DefaultNucleotideGraphSequence Document if
	 * (documentName.toString().contains(readsAssemblyContig) &&
	 * !documentName.toString().contains( readsAssembyConsensusContig)) { //
	 * DefaultAlignmentSequence Document
	 * setDefaultAlignmentDocumentFileName(pCnt); } else if
	 * (documentName.toString().contains(ab1FileExtension) &&
	 * docType.contains(defaultNucleotideGraphSequence)) { // Sequence Document
	 * // setSequenceDocumentFileName(pCnt);
	 * setDefaultNucleotideGraphSequenceFileName(pCnt); } else if
	 * (documentName.toString() .contains(readsAssembyConsensusContig) &&
	 * !docType.toString().contains( readsAssemblyFastaConsensusContig)) { //
	 * DefaultNucleotideGraphSequence Document
	 * setDefaultNucleotideGraphSequenceFileName(pCnt); } else if
	 * (!documentName.toString().contains(ab1FileExtension) &&
	 * !documentName.toString().contains(readsAssemblyContig) &&
	 * !documentName.toString().contains( readsAssembyConsensusContig) &&
	 * !docType.toString().contains("DefaultAlignmentDocument")) { // FASTA
	 * Document setSequenceDocumentFileName(pCnt); } else if
	 * (documentName.toString().contains(readsAssemblyFasta) &&
	 * docType.toString().contains("DefaultAlignmentDocument")) { // Fasta Reads
	 * Assembly setDefaultAlignmentDocumentFileName(pCnt); } else if
	 * (documentName.toString() .contains(readsAssembyConsensusContig) &&
	 * docType.toString().contains( readsAssemblyFastaConsensusContig)) {
	 * setDefaultNucleotideSequenceFileName(pCnt); } }
	 */

	private void getAB1SelectedDocumentsType(int pCnt, String documentName) {
		// - DefaultAlignmentSequence Document
		// - DefaultNucleotideGraphSequence Document
		if (defAlignmentDoc != null
				&& docType.contains(defaultAlignmentDocument)
				&& defAlignmentDoc.getName().contains(readsAssemblyFasta)) {
			// DefaultAlignmentSequence Document
			setDefaultAlignmentDocumentFileName(pCnt);
		} else if (documentName.toString().contains(ab1FileExtension)
				&& docType.contains(defaultNucleotideGraphSequence)
				&& !documentName.contains(readsAssembyConsensusContig)) {
			// AB1 DefaultNucleotideGraphSequence
			setDefaultNucleotideGraphSequenceFileName(pCnt);
		} else if (defNucleotideGraphSequence != null
				&& docType.contains(defaultNucleotideGraphSequence)
				&& documentName.contains(readsAssembyConsensusContig)) {
			// Consensus Contig DefaultNucleotideGraphSequence Document
			setDefaultNucleotideGraphSequenceFileName(pCnt);
		}
	}

	private void getFastaSelectedDocumentsType(int pCnt, String documentName) {
		// - DefaultAlignmentSequence Document
		// - DefaultNucleotideGraphSequence Document

		if (documentName != null && !documentName.isEmpty()) {
			if (fastFilename.contains(fastaFileExtension)) {
				/* Document within the name "Reads Assembly and fasta filename" */
				if (documentName.contains(readsAssemblyFasta)
						&& docType.toString()
								.contains(defaultAlignmentDocument)) {
					// DefaultAlignmentDocument
					setDefaultAlignmentDocumentFileName(pCnt);
				} else if (documentName.contains(readsAssemblyFasta)
						&& docType.toString().contains(
								defaultNucleotideSequence)
						&& documentName.contains(readsAssembyConsensusContig)) {
					/* DefaultNucleotideSequence */
					setDefaultNucleotideSequenceFileName(pCnt);
				} else if (!documentName.contains(readsAssemblyFasta)
						&& docType.toString().contains(
								defaultNucleotideSequence)
						&& !documentName.contains(readsAssembyConsensusContig)) {
					setDefaultNucleotideSequenceFileName(pCnt);
				}

			}
		}
	}

	private void checkIfFileExsistInDatabase(int pCnt, String fileName) {

		if (selectedDocuments.get(pCnt).getName() != null) {
			fileExists = readGeneiousFieldsValues
					.fileNameExistsInGeneiousDatabase(fileName);
		}
		/*
		 * if (selectedDocuments.get(pCnt).getName().toString()
		 * .contains(ab1FileExtension)) { fileExists = readGeneiousFieldsValues
		 * .fileNameExistsInGeneiousDatabase(documentFileName .getName()); }
		 * else if (defAlignmentDoc != null &&
		 * selectedDocuments.get(pCnt).getName().toString()
		 * .contains(readsAssemblyContig)) { fileExists =
		 * readGeneiousFieldsValues
		 * .fileNameExistsInGeneiousDatabase(defAlignmentDoc.getName()); } else
		 * if (contigSequence != null &&
		 * selectedDocuments.get(pCnt).getName().toString()
		 * .contains(readsAssembyConsensusContig)) { //
		 * DefaultNucleotideGraphSequence fileExists = readGeneiousFieldsValues
		 * .fileNameExistsInGeneiousDatabase(contigSequence.getName()); // FASTA
		 * } else if (!(selectedDocuments.get(pCnt).getName().toString()
		 * .contains(ab1FileExtension) &&
		 * selectedDocuments.get(pCnt).getName().toString()
		 * .contains(readsAssemblyContig) && selectedDocuments
		 * .get(pCnt).getName().toString()
		 * .contains(readsAssembyConsensusContig))) { fileExists =
		 * readGeneiousFieldsValues
		 * .fileNameExistsInGeneiousDatabase(documentFileName .getName()); }
		 */
	}

	private void checkIfVersionExsist(int pCnt) {
		String fileName = getFileNameFromDocument(pCnt);

		if (fileExists && readGeneiousFieldsValues.recordcount >= 1) {
			versionNumberExists = true;
			/* AB1 */
		} else if (selectedDocuments.get(pCnt).toString()
				.contains(ab1FileExtension)
				&& defNucleotideGraphSequence != null) {
			versionNumberExists = selectedDocuments.get(pCnt).toString()
					.contains("DocumentVersionCode_Seq");
			/* Fasta */
		} else if (!selectedDocuments.get(pCnt).toString()
				.contains(ab1FileExtension)
				&& defAlignmentDoc != null) {
			if (!(fileName != null && fileName.isEmpty())) {
				versionNumberExists = selectedDocuments.get(pCnt).toString()
						.contains("DocumentVersionCode_Seq");
			}
			/* DefaultAlignmentDocument */
		} else if (defAlignmentDoc != null
				&& selectedDocuments.get(pCnt).getName() != null) {
			versionNumberExists = selectedDocuments.get(pCnt).toString()
					.contains("DocumentVersionCode_Seq");
			/* DefaultNucleotideGraphSequence for NOVO */
		} else if (defNucleotideGraphSequence != null
				&& selectedDocuments.get(pCnt).getName() != null) {
			versionNumberExists = selectedDocuments.get(pCnt).toString()
					.contains("DocumentVersionCode_Seq");
		} else if (defNucleotideSequence != null
				&& fastFilename.contains(fastaFileExtension)) {
			versionNumberExists = selectedDocuments.get(pCnt).toString()
					.contains("DocumentVersionCode_Seq");
		}
	}

	private String getVersionNumberFromDocument(String pDocName, int pCnt) {
		Object result = null;
		if (DocumentUtilities.getSelectedDocuments().get(pCnt)
				.getDocumentNotes(true)
				.getNote("DocumentNoteUtilities-Document version") != null) {
			result = DocumentUtilities.getSelectedDocuments().get(pCnt)
					.getDocumentNotes(true)
					.getNote("DocumentNoteUtilities-Document version")
					.getFieldValue("DocumentVersionCode_Seq");
		}
		return (String) result;
	}

	/* Get the filename from the selected document. */
	private String getFileNameFromDocument(int cnt) {
		Object result = null;
		if (DocumentUtilities.getSelectedDocuments().get(cnt)
				.getDocumentNotes(true).getNote("importedFrom") != null) {
			result = DocumentUtilities.getSelectedDocuments().get(cnt)
					.getDocumentNotes(true).getNote("importedFrom")
					.getFieldValue("filename");
		}
		return (String) result;
	}

	/* Get path value from the document */
	private String getPathFromDocument(int cnt) {
		Object result = null;
		if (DocumentUtilities.getSelectedDocuments().get(cnt)
				.getDocumentNotes(true).getNote("importedFrom") != null) {
			result = DocumentUtilities.getSelectedDocuments().get(cnt)
					.getDocumentNotes(true).getNote("importedFrom")
					.getFieldValue("path");
		}
		return (String) result;
	}

	private void setFastaNotes(int cnt) {
		fastFilename = getFileNameFromDocument(cnt);
		if (fastFilename.contains(fastaFileExtension)
				&& !defNucleotideSequence.getName().contains(
						readsAssembyConsensusContig)) {
			/* Get the filename from the Fasta content */
			try {
				file = new File(fcd.loadFastaFile(selectedDocuments.get(cnt)
						.getName(), (String) documentFileImportPath));
				if (file.getPath().length() > 0) {
					extractAb1FastaFileName = file.getName();
				} else {
					return;
				}
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}

			/*
			 * Get the last version number from the last insert fasta file.
			 */
			if (versionNumberExists) {
				versienummer = Integer.parseInt(getVersionNumberFromDocument(
						fastFilename, cnt));
			} else {
				versienummer = readGeneiousFieldsValues
						.getLastVersion_For_AB1_Fasta_ConsensusContig((String) extractAb1FastaFileName);
			}
			// setVersienummer();
		}
	}

	/*
	 * private void setAB1Notes(int cnt) { if
	 * (selectedDocuments.get(cnt).toString().contains(ab1FileExtension)) { Get
	 * AB1 Filename and Version from AB1 file extractAb1FastaFileName =
	 * documentFileName.getName(); if (versionNumberExists) { versienummer =
	 * Integer.parseInt(getVersionNumberFromDocument( (String)
	 * extractAb1FastaFileName, cnt)); } else { versienummer =
	 * readGeneiousFieldsValues
	 * .getLastVersion_For_AB1_Fasta_ConsensusContig((String)
	 * extractAb1FastaFileName); } // setVersienummer(); } }
	 */

	private void setVersienummer() {
		if (versienummer == 0) {
			versienummer = 1;
		}
	}

	private void setDefaultAligmentDocument(int cnt) {
		if (defAlignmentDoc != null
				&& selectedDocuments.get(cnt).getName() != null) {
			extractAb1FastaFileName = defAlignmentDoc.getName();
			if (versionNumberExists) {
				versienummer = Integer.parseInt(getVersionNumberFromDocument(
						(String) extractAb1FastaFileName, cnt));
			} else {
				versienummer = readGeneiousFieldsValues
						.getLastVersion_From_ContigAssemblyDocuments((String) extractAb1FastaFileName);
			}
			setVersienummer();
		}
	}

	private void setDefaultNucleotideGraphSequence(int cnt) {
		if (defNucleotideGraphSequence != null
				&& selectedDocuments.get(cnt).getName() != null) {
			extractAb1FastaFileName = defNucleotideGraphSequence.getName();
			if (versionNumberExists) {
				versienummer = Integer.parseInt(getVersionNumberFromDocument(
						(String) extractAb1FastaFileName, cnt));
			} else {
				versienummer = readGeneiousFieldsValues
						.getLastVersion_For_AB1_Fasta_ConsensusContig((String) extractAb1FastaFileName);
			}
			// setVersienummer();
			/*
			 * if file exists and is not extravalue "ExtractIDCode_Seq" increase
			 * Version number
			 */
			if (fileExists && !extractIDValue) {
				versienummer++;
			}
		}
	}

	private void setDefaultNucleotideSequence(int cnt) {
		if (defNucleotideSequence != null && (boolean) filePathExists) {
			setFastaNotes(cnt);
			// setVersienummer();
		}
	}

	/*
	 * private void setAB1FastaLog() { if
	 * ((documentFileName.getName().toString().contains(ab1FileExtension) ||
	 * documentFileName .getName().toString().contains("dum"))) {
	 * logger.info("Done with extracting Ab1/Dummy file name. "); } else {
	 * logger.info("Done with extracting Fas file name. "); } }
	 */

	private void setDefaultAlignmentDocumentLog() {
		if (defAlignmentDoc != null) {
			logger.info("Done with extracting " + defAlignmentDoc.getName());
		}
	}

	private void setDefaultNucleotideGraphSequenceLog() {
		if (defNucleotideGraphSequence != null
				&& !defNucleotideGraphSequence.toString().isEmpty()) {
			logger.info("Done with extracting "
					+ defNucleotideGraphSequence.getName());
		}
	}

	private void setDefaultNucleotideSequenceLog() {
		if (defNucleotideSequence != null
				&& !defNucleotideSequence.toString().isEmpty()) {
			logger.info("Done with extracting "
					+ defNucleotideSequence.getName());
		}
	}
}