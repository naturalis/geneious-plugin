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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

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

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.documents.sequence.NucleotideSequenceDocument;
import com.biomatters.geneious.publicapi.implementations.DefaultAlignmentDocument;
import com.biomatters.geneious.publicapi.implementations.sequence.DefaultNucleotideGraphSequence;
import com.biomatters.geneious.publicapi.implementations.sequence.DefaultNucleotideSequence;
import com.biomatters.geneious.publicapi.plugin.DocumentAction;
import com.biomatters.geneious.publicapi.plugin.DocumentOperationException;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;

/**
 * <table summary="Split the filename (extract)">
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

	private DefaultNucleotideGraphSequence defNucleotideGraphSequence = new DefaultNucleotideGraphSequence();
	private DefaultAlignmentDocument defAlignmentDoc = new DefaultAlignmentDocument();

	/* FASTA */
	private DefaultNucleotideSequence defNucleotideSequence = new DefaultNucleotideSequence();

	private Object documentFileImportPath = "";

	private LimsDatabaseChecker dbchk = new LimsDatabaseChecker();
	private ArrayList<AnnotatedPluginDocument> selectedDocuments = new ArrayList<AnnotatedPluginDocument>();

	private final String readsAssemblyConsensusContig = "consensus sequence";
	private final String readsAssembly = "Reads Assembly";
	private final String read_Assembly = "_ Assembly";

	private final String defaultNucleotideGraphSequence = "DefaultNucleotideGraphSequence";
	private final String defaultAlignmentDocument = "DefaultAlignmentDocument";
	private final String defaultNucleotideSequence = "DefaultNucleotideSequence";

	private Object filePathExists = null;

	private final String ab1FileExtension = "ab1";
	private final String fastaFileExtension = "fas";
	private String fastaFilename;
	private String ab1Filename;
	private String docType;
	private String logSplitFileName;
	private String extension;

	/**
	 * @return the extension
	 */
	public String getExtension() {
		return extension;
	}

	/**
	 * @param extension
	 *            the extension to set
	 */
	public void setExtension(String extension) {
		this.extension = extension;
	}

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

			File theDir = new File(limsImporterUtil.getLogPath());
			if (!theDir.exists()) {
				theDir.mkdir();
			}

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
				fastaFilename = "";
				versienummer = 0;
				versionNumberExists = false;
				defAlignmentDoc = null;
				defNucleotideSequence = null;
				defNucleotideGraphSequence = null;

				try {
					docType = (String) DocumentUtilities.getSelectedDocuments()
							.get(cnt).getDocument().getClass().getTypeName();
					/*
					 * Documentname bestaat alleen uit Reads Assembly Consensus
					 * Sequences
					 */
					if (docType.contains("DefaultSequenceListDocument")) {
						logger.info("Documentname only contains: "
								+ selectedDocuments.get(cnt).getName());
						uitValList.add("Documentname only contains: "
								+ selectedDocuments.get(cnt).getName());
						continue;
					}
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
				fastaFilename = getFileNameFromDocument(cnt);
				ab1Filename = selectedDocuments.get(cnt).getName();

				if (fastaFilename != null && !fastaFilename.isEmpty()
						&& !fastaFilename.contains(ab1FileExtension)) {
					getFastaSelectedDocumentsType(cnt,
							selectedDocuments.get(cnt).getName());
				} else if (!ab1Filename.contains("dum")) {
					if (checkFileName(ab1Filename)) {
						/*
						 * Create the dialog GUI to see the processing of the
						 * documents
						 */
						limsFrameProgress.hideFrame();
						continue;
					}
					getAB1SelectedDocumentsType(cnt, selectedDocuments.get(cnt)
							.getName());
				}

				/* Check if the file exists in the database */
				checkIfFileExsistInDatabase(cnt);

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
					+ verwerkingList.size());
			// + DocumentUtilities.getSelectedDocuments().size());
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
									readsAssemblyConsensusContig)) {
						showFastaDialog();
					} else if (defAlignmentDoc != null) {
						showDefaultAlignmentDialog();
					} else if (defNucleotideGraphSequence != null
							&& docType.contains(defaultNucleotideGraphSequence)) {
						showNucleotideGraphSequenceDialog();
					} else if (defNucleotideSequence != null
							&& defNucleotideSequence.getName().contains(
									readsAssemblyConsensusContig)) {
						showNucleotideSequenceDialog();
					} else {
						showAllSelectedDocumentsDialog();
					}
					verwerkingList.clear();
					limsFrameProgress.hideFrame();
				}

				/* Add Image to the dialog */
				private void getDialogMessage() {
					String imageName = limsImporterUtil.getNaturalisPicture()
							.getAbsolutePath();
					ImageIcon icon = new ImageIcon(imageName);
					JOptionPane.showMessageDialog(
							new JFrame(),
							Integer.toString(verwerkingList.size())
									+ " documents (of "
									+ Integer.toString(selectedDocuments.size())
									+ " selected) are updated.", "Split name",
							JOptionPane.INFORMATION_MESSAGE, icon);
					verwerkingList.clear();
				}

				/**
				 * 
				 */
				private void showNucleotideGraphSequenceDialog() {
					getDialogMessage();
					logger.info(defNucleotideGraphSequence.getName()
							+ "-Update: Total imported document(s): "
							+ verwerkingList.toString());
					defNucleotideGraphSequence = null;
				}

				private void showNucleotideSequenceDialog() {
					getDialogMessage();
					logger.info(defNucleotideSequence.getName()
							+ "-Update: Total imported document(s): "
							+ verwerkingList.toString());
					defNucleotideSequence = null;
				}

				/**
				 * 
				 */
				private void showDefaultAlignmentDialog() {
					getDialogMessage();

					logger.info(defaultAlignmentDocument
							+ "-Update: Total imported document(s): "
							+ verwerkingList.toString());
					defAlignmentDoc = null;
				}

				private void showFastaDialog() {
					getDialogMessage();

					logger.info(defNucleotideSequence.getName()
							+ "-Update: Total imported document(s): "
							+ verwerkingList.toString());
					defNucleotideSequence = null;
				}

				private void showAllSelectedDocumentsDialog() {
					if (ab1Filename.contains(".dum")) {
						getDialogMessage();
					} else {
						getDialogMessage();
					}
					logger.info("Update: Total imported document(s): "
							+ verwerkingList.toString());
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
					String imageName = limsImporterUtil.getNaturalisPicture()
							.getAbsolutePath();
					ImageIcon icon = new ImageIcon(imageName);
					JOptionPane.showMessageDialog(new JFrame(),
							"Select all documents", "Split name",
							JOptionPane.INFORMATION_MESSAGE, icon);
					return;
				}
			});
		}
	}

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

		if (fastaFilename != null
				&& fastaFilename.toString().contains(fastaFileExtension)) {
			enrichFastaAndAssemblySequence(cnt);
		} else {
			enrichAB1andAssemblySequence(cnt);
		}

		// setExtension(FilenameUtils.getExtension(fastaFilename));

		int i = fastaFilename.lastIndexOf('.');
		if (i > 0) {
			setExtension(fastaFilename.substring(i + 1));
		}

		if (fileExists && !extractIDValue && getExtension().equals("fas")) {
			versienummer++;
			/* Set version number Fasta file and AB1 */
			limsAB1Fields.setVersieNummer(versienummer);
		}

		/* Processing the notes */
		/* Lims 251- Waar is het veld seq quality? */

		limsSplitNotes.enrichSplitDocumentsWithNotes(annotatedPluginDocuments,
				cnt, fileExists, extractIDValue, versienummer);

		if (limsAB1Fields.getExtractID() == ""
				&& limsAB1Fields.getExtractID().isEmpty()) {
			uitValList.add(selectedDocuments.get(cnt).getName());
			limsLogger.logToFile(logSplitFileName, uitValList.toString());
		}

		/* Show processing dialog */
		limsFrameProgress.showProgress("Processing: "
				+ DocumentUtilities.getSelectedDocuments().get(cnt).getName());
	}

	/**
	 * @param cnt
	 */
	private void enrichAB1andAssemblySequence(int cnt) {
		if (defAlignmentDoc != null && !defAlignmentDoc.toString().isEmpty()) {
			/* DefaultAlignmentDocument */
			processReadAssemblyConsesusContig(cnt, selectedDocuments.get(cnt)
					.getName());
		} else if (defNucleotideGraphSequence != null
				&& !defNucleotideGraphSequence.toString().isEmpty()
				&& !ab1Filename.contains(ab1FileExtension)) {
			/* Default */
			processReadAssemblyConsesusContig(cnt, selectedDocuments.get(cnt)
					.getName());
		} else if (defNucleotideGraphSequence != null
				&& !defNucleotideGraphSequence.toString().isEmpty()
				&& ab1Filename.contains(ab1FileExtension)) {
			extractAB1Document();
			/* Lims-324 16 aug 2017 */
		} else if (docType.contains(defaultNucleotideSequence)) {
			processReadAssemblyConsesusContig(cnt, selectedDocuments.get(cnt)
					.getName());
		}
	}

	/**
	 * @param cnt
	 */
	private void enrichFastaAndAssemblySequence(int cnt) {
		/* Fasta Document */
		if (docType.contains(defaultNucleotideSequence)
				&& selectedDocuments.get(cnt).getName()
						.contains(readsAssemblyConsensusContig)) {
			processReadAssemblyConsesusContig(cnt, fastaFilename);
		} else if (docType.contains(defaultNucleotideSequence)
				&& defNucleotideSequence != null
				&& !selectedDocuments.get(cnt).getName()
						.contains(readsAssembly)) {
			extractFastaSequenceDocument();
		} else if (docType.contains(defaultAlignmentDocument)
				&& selectedDocuments.get(cnt).getName().contains(readsAssembly)) {
			processReadAssemblyConsesusContig(cnt, fastaFilename);
		}
	}

	/**
	 * Add plugin 5 Split name to the menubar
	 * 
	 * @return Add the button to the menubar
	 * @see LimsSplitName
	 * */
	@Override
	public GeneiousActionOptions getActionOptions() {
		return new GeneiousActionOptions("5 Split name", "Split name")
				.setMainMenuLocation(GeneiousActionOptions.MainMenu.Tools)
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

	private void extractFastaSequenceDocument() {
		if (extractAb1FastaFileName != null
				&& fastaFilename.toString().contains(fastaFileExtension)) {
			logger.info("Start extracting value from file: "
					+ extractAb1FastaFileName);
			verwerkingList.add(fastaFilename);

			/* Extract values from the Fasta filename */
			limsSplitNotes
					.extractDocumentFileName((String) extractAb1FastaFileName);
		}
	}

	private void extractAB1Document() {
		if (ab1Filename.toString().contains(ab1FileExtension)) {
			logger.info("Start extracting value from file: "
					+ extractAb1FastaFileName);
			verwerkingList.add(ab1Filename);

			/* Extract values from the Fasta filename */
			limsSplitNotes
					.extractDocumentFileName((String) extractAb1FastaFileName);
		}
	}

	private void processReadAssemblyConsesusContig(int pCnt, String pFilename) {
		if (selectedDocuments.get(pCnt).getName()
				.contains(readsAssemblyConsensusContig)
				|| selectedDocuments.get(pCnt).getName()
						.contains(readsAssembly)
				|| selectedDocuments.get(pCnt).getName()
						.contains(read_Assembly)
				|| selectedDocuments.get(pCnt).toString()
						.contains(defaultNucleotideSequence)
				|| selectedDocuments.get(pCnt).toString()
						.contains(defaultAlignmentDocument)) {

			// if (defAlignmentDoc != null || defNucleotideGraphSequence != null
			// || defNucleotideSequence != null) {

			logger.info("Start extracting value from file: " + pFilename);

			extractAb1FastaFileName = pFilename;
			verwerkingList.add(pFilename);
			limsSplitNotes.extractDocumentFileName((String) pFilename);

			if (selectedDocuments.get(pCnt).toString()
					.contains("DocumentVersionCode_Seq")) {
				versienummer = Integer
						.parseInt((String) getVersionNumberFromDocument(pCnt));
			}
			setVersienummer();
			// }
		}
	}

	private void getAB1SelectedDocumentsType(int pCnt, String documentName) {
		// - DefaultAlignmentSequence Document
		// - DefaultNucleotideGraphSequence Document
		if (docType.contains(defaultAlignmentDocument)) {
			// DefaultAlignmentSequence Document
			setDefaultAlignmentDocumentFileName(pCnt);
		} else if ((documentName.toString().contains(ab1FileExtension) && docType
				.contains(defaultNucleotideGraphSequence))
				|| (docType.contains(defaultNucleotideGraphSequence) && documentName
						.contains(readsAssemblyConsensusContig))) {
			// AB1 DefaultNucleotideGraphSequence
			setDefaultNucleotideGraphSequenceFileName(pCnt);
		} else if (docType.toString().contains(defaultNucleotideSequence)) {
			/* DefaultNucleotideSequence */
			setDefaultNucleotideSequenceFileName(pCnt);
		}
	}

	private void getFastaSelectedDocumentsType(int pCnt, String documentName) {
		// - DefaultAlignmentSequence Document
		// - DefaultNucleotideGraphSequence Document

		if (documentName != null && !documentName.isEmpty()) {
			if (fastaFilename.contains(fastaFileExtension)) {
				/* Document within the name "Reads Assembly and fasta filename" */
				if (documentName.contains(readsAssembly)
						&& docType.toString()
								.contains(defaultAlignmentDocument)) {
					// DefaultAlignmentDocument
					setDefaultAlignmentDocumentFileName(pCnt);
				} else if (documentName.contains(readsAssembly)
						&& docType.toString().contains(
								defaultNucleotideSequence)
						&& documentName.contains(readsAssemblyConsensusContig)) {
					/* DefaultNucleotideSequence */
					setDefaultNucleotideSequenceFileName(pCnt);
				} else if (!documentName.contains(readsAssembly)
						&& docType.toString().contains(
								defaultNucleotideSequence)
						&& !documentName.contains(readsAssemblyConsensusContig)) {
					setDefaultNucleotideSequenceFileName(pCnt);
				}
			}
		}
	}

	/* Check if file exists in the database. */
	private void checkIfFileExsistInDatabase(int pCnt) {

		if (selectedDocuments.get(pCnt).getName() != null) {
			fileExists = readGeneiousFieldsValues
					.fileNameExistsInGeneiousDatabase(selectedDocuments.get(
							pCnt).getName());
		}
	}

	private void checkIfVersionExsist(int pCnt) {
		String fileName = getFileNameFromDocument(pCnt);

		if (fileExists && readGeneiousFieldsValues.recordcount >= 1) {
			versionNumberExists = true;
			/* AB1 */
		} else if (defNucleotideGraphSequence != null) {
			versionNumberExists = selectedDocuments.get(pCnt).toString()
					.contains("DocumentVersionCode_Seq");
			/* Fasta */
		} else if (defAlignmentDoc != null) {
			if (!(fileName != null && fileName.isEmpty())) {
				versionNumberExists = selectedDocuments.get(pCnt).toString()
						.contains("DocumentVersionCode_Seq");
			}
			/* DefaultAlignmentDocument */
		} else if (defAlignmentDoc != null) {
			versionNumberExists = selectedDocuments.get(pCnt).toString()
					.contains("DocumentVersionCode_Seq");
			/* DefaultNucleotideGraphSequence for NOVO */
		} else if (defNucleotideGraphSequence != null) {
			versionNumberExists = selectedDocuments.get(pCnt).toString()
					.contains("DocumentVersionCode_Seq");
		} else if (defNucleotideSequence != null) {
			versionNumberExists = selectedDocuments.get(pCnt).toString()
					.contains("DocumentVersionCode_Seq");
		}
	}

	private Object getVersionNumberFromDocument(int pCnt) {
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
		fastaFilename = getFileNameFromDocument(cnt);
		if (fastaFilename.contains(fastaFileExtension)
				&& !defNucleotideSequence.getName().contains(
						readsAssemblyConsensusContig)) {
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
				versienummer = Integer
						.parseInt((String) getVersionNumberFromDocument(cnt));
			} else {
				versienummer = readGeneiousFieldsValues
						.getLastVersion_For_AB1_Fasta_ConsensusContig((String) extractAb1FastaFileName);
			}
		}
	}

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
				versienummer = Integer
						.parseInt((String) getVersionNumberFromDocument(cnt));
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
				versienummer = Integer
						.parseInt((String) getVersionNumberFromDocument(cnt));
			} else {
				versienummer = readGeneiousFieldsValues
						.getLastVersion_For_AB1_Fasta_ConsensusContig((String) extractAb1FastaFileName);
			}
		}
	}

	private void setDefaultNucleotideSequence(int cnt) {
		if (defNucleotideSequence != null && (boolean) filePathExists) {
			setFastaNotes(cnt);
		}
	}

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

	public Boolean checkFileName(String fileName) {
		/*
		 * String imageName = limsImporterUtil.getNaturalisPicture()
		 * .getAbsolutePath(); ImageIcon icon = new ImageIcon(imageName);
		 */
		String[] ab1FileName = StringUtils.split(fileName, "_");
		Boolean checked = false;
		for (int i = 0; i < ab1FileName.length; i++) {
			if (i == 0) {
				String result = ab1FileName[i].substring(1);
				Pattern p = Pattern.compile("[a-zA-Z]");
				Matcher m = p.matcher(result);

				if (m.find()) {
					logger.info(fileName + " is not correct." + "\n"
							+ "ExtractID " + ab1FileName[0]
							+ " is not correct and will not be added.");
					checked = true;
					continue;
				}
			} else if (i == 4) {
				if (!ab1FileName[4].contains("COI")) {
					logger.info(fileName + " is not correct." + "\n"
							+ "Marker " + ab1FileName[i]
							+ " is not correct and and will not be added.");
					checked = true;
					continue;
				}
			}
		}
		return checked;
	}
}