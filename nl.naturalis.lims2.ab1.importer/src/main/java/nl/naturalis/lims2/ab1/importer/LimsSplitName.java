package nl.naturalis.lims2.ab1.importer;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import nl.naturalis.lims2.utils.LimsAB1Fields;
import nl.naturalis.lims2.utils.LimsDatabaseChecker;
import nl.naturalis.lims2.utils.LimsFrameProgress;
import nl.naturalis.lims2.utils.LimsImporterUtil;
import nl.naturalis.lims2.utils.LimsNotesSplitName;
import nl.naturalis.lims2.utils.LimsReadGeneiousFieldsValues;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.documents.sequence.NucleotideSequenceDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentAction;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;

public class LimsSplitName extends DocumentAction {

	LimsReadGeneiousFieldsValues readGeneiousFieldsValues = new LimsReadGeneiousFieldsValues();
	LimsAB1Fields limsAB1Fields = new LimsAB1Fields();
	LimsNotesSplitName limsSplitNotes = new LimsNotesSplitName();
	LimsFrameProgress limsFrameProgress = new LimsFrameProgress();
	LimsImporterUtil limsImporterUtil = new LimsImporterUtil();

	private final String ab1FileExtension = "ab1";
	private final String fastaFileExtension = "fas";

	private boolean extractIDExists = false;
	private boolean fileExists = false;

	private boolean isContigFile = false;

	private long startBeginTime;
	private String extension;
	private String fastaFileName;
	private String splitFileName;
	private Object filePathExists;

	private static final Logger logger = LoggerFactory
			.getLogger(LimsSplitNameNew.class);

	private ArrayList<AnnotatedPluginDocument> selectedDocuments = new ArrayList<AnnotatedPluginDocument>();
	private List<String> verwerkingList = new ArrayList<String>();
	private List<String> uitValList = new ArrayList<String>();

	private Object documentFileImportPath;

	private int versienummer;
	int counter = 0;

	/**
	 * @return the isContigFile
	 */
	public boolean isContigFile() {
		return isContigFile;
	}

	/**
	 * @param isContigFile
	 *            the isContigFile to set
	 */
	public void setContigFile(boolean isContigFile) {
		this.isContigFile = isContigFile;
	}

	/**
	 * @return the versienummer
	 */
	public int getVersienummer() {
		return versienummer;
	}

	/**
	 * @param versienummer
	 *            the versienummer to set
	 */
	public void setVersienummer(int versienummer) {
		this.versienummer = versienummer;
	}

	/**
	 * @return the documentFileImportPath
	 */
	public Object getDocumentFileImportPath() {
		return documentFileImportPath;
	}

	/**
	 * @param documentFileImportPath
	 *            the documentFileImportPath to set
	 */
	public void setDocumentFileImportPath(Object documentFileImportPath) {
		this.documentFileImportPath = documentFileImportPath;
	}

	/**
	 * @return the filePathExists
	 */
	public Object getFilePathExists() {
		return filePathExists;
	}

	/**
	 * @param filePathExists
	 *            the filePathExists to set
	 */
	public void setFilePathExists(Object filePathExists) {
		this.filePathExists = filePathExists;
	}

	/**
	 * @return the ab1FileExtension
	 */
	public String getAb1FileExtension() {
		return ab1FileExtension;
	}

	/**
	 * @return the fastaFileExtension
	 */
	public String getFastaFileExtension() {
		return fastaFileExtension;
	}

	/**
	 * @return the extractIDExists
	 */
	public boolean isExtractIDExists() {
		return extractIDExists;
	}

	/**
	 * @param extractIDExists
	 *            the extractIDExists to set
	 */
	public void setExtractIDExists(boolean extractIDExists) {
		this.extractIDExists = extractIDExists;
	}

	/**
	 * @return the fileExists
	 */
	public boolean isFileExists() {
		return fileExists;
	}

	/**
	 * @param fileExists
	 *            the fileExists to set
	 */
	public void setFileExists(boolean fileExists) {
		this.fileExists = fileExists;
	}

	/**
	 * @return the fastaFileName
	 */
	public String getFastaFileName() {
		return fastaFileName;
	}

	/**
	 * @param fastaFileName
	 *            the fastaFileName to set
	 */
	public void setFastaFileName(String fastaFileName) {
		this.fastaFileName = fastaFileName;
	}

	/**
	 * @return the ab1FileName
	 */
	public String getSplitFileName() {
		return splitFileName;
	}

	/**
	 * @param ab1FileName
	 *            the ab1FileName to set
	 */
	public void setSplitFileName(String ab1FileName) {
		this.splitFileName = ab1FileName;
	}

	/**
	 * @return the versionNumberExists
	 */
	public boolean isVersionNumberExists() {
		return versionNumberExists;
	}

	/**
	 * @param versionNumberExists
	 *            the versionNumberExists to set
	 */
	public void setVersionNumberExists(boolean versionNumberExists) {
		this.versionNumberExists = versionNumberExists;
	}

	private boolean versionNumberExists = false;

	/**
	 * @return the startBeginTime
	 */
	public long getStartBeginTime() {
		return startBeginTime;
	}

	/**
	 * @param startBeginTime
	 *            the startBeginTime to set
	 */
	public void setStartBeginTime(long startBeginTime) {
		this.startBeginTime = startBeginTime;
	}

	/**
	 * @return the selectedDocuments
	 */
	public ArrayList<AnnotatedPluginDocument> getSelectedDocuments() {
		return selectedDocuments;
	}

	/**
	 * @param selectedDocuments
	 *            the selectedDocuments to set
	 */
	public void setSelectedDocuments(
			ArrayList<AnnotatedPluginDocument> selectedDocuments) {
		this.selectedDocuments = selectedDocuments;
	}

	/**
	 * @return the logger
	 */
	public static Logger getLogger() {
		return logger;
	}

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

	/* ======================================================= */

	@Override
	public void actionPerformed(
			AnnotatedPluginDocument[] annotatedPluginDocument) {

		LimsDatabaseChecker dbchk = new LimsDatabaseChecker();

		LimsFrameProgress limsFrameProgress = new LimsFrameProgress();

		/* No Document select then show a message */
		messageNoDocumentsSelected();

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

			/* Create the dialog GUI to see the processing of the documents */
			limsFrameProgress.createProgressGUI();
			logger.info("----------------------------S T A R T -------------------------------");

			/* Set the selected documents in a list */
			setSelectedDocuments((ArrayList<AnnotatedPluginDocument>) DocumentUtilities
					.getSelectedDocuments());

			for (int cnt = 0; cnt < getSelectedDocuments().size(); cnt++) {
				counter = cnt;

				/* Start time split the document name */
				setStartBeginTime(System.nanoTime());

				/* set Fasta filename */
				setFastaFileName(LimsNotesSplitName
						.getFileNameFromDocument(counter));

				/* Set filename */
				setSplitFileName(getSelectedDocuments().get(cnt).getName());

				/* Skip uncorrected document filename */
				skipUnCorrectedDocumentFileName(limsFrameProgress);

				/* Search for Contig and consensus/(Read) Assembly documents */
				if (isOverrideCacheName(getSelectedDocuments().toString())) {
					setFileExists(readGeneiousFieldsValues
							.checkIfFileExistsInGeneiousDatabase(
									getSelectedDocuments().get(counter)
											.getName(), "override_cache_name"));
					setContigFile(true);
					limsFrameProgress.showProgress("Processing: "
							+ DocumentUtilities.getSelectedDocuments()
									.get(counter).getName());
				} else if (isCacheName(getSelectedDocuments().toString())) {
					setFileExists(readGeneiousFieldsValues
							.checkIfFileExistsInGeneiousDatabase(
									getSelectedDocuments().get(counter)
											.getName(), "cache_name"));
				}

				/* check if extractId exists */
				setExtractIDExists(readGeneiousFieldsValues
						.getValueFromAnnotatedPluginDocument(
								annotatedPluginDocument[cnt],
								"DocumentNoteUtilities-Extract ID (Seq)",
								"ExtractIDCode_Seq"));

				/* check if the filepath exists in the document */
				setFilePathExists(readGeneiousFieldsValues
						.getValueFromAnnotatedPluginDocument(
								annotatedPluginDocument[counter],
								"importedFrom", "path"));

				if (getSelectedDocuments().get(counter).getName()
						.contains("dum")) {
					continue;
				}

				if ((boolean) getFilePathExists()) {
					setDocumentFileImportPath(LimsNotesSplitName
							.getPathFromDocument(counter));
				}

				getVersion(counter);

				/* Show processing dialog */
				limsFrameProgress.showProgress("Processing: "
						+ DocumentUtilities.getSelectedDocuments().get(counter)
								.getName());

				enrichDocumentFileWithNotes(annotatedPluginDocument, counter);

				limsImporterUtil.calculateTimeForAddingNotes(startBeginTime);

				versienummer = 0;
				setContigFile(false);

			}

		} // End Selected

		logger.info("Total of document(s) updated: " + verwerkingList.size());
		logger.info("------------------------- E N D--------------------------------------");

		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				getDialogMessage();

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
								+ Integer.toString(getSelectedDocuments()
										.size()) + " selected) are updated.",
						"Split name", JOptionPane.INFORMATION_MESSAGE, icon);
				verwerkingList.clear();
				limsFrameProgress.hideFrame();
			}

		});
	}

	/**
	 * @param limsFrameProgress
	 */
	private void skipUnCorrectedDocumentFileName(
			LimsFrameProgress limsFrameProgress) {
		if (!getSplitFileName().contains("dum")) {
			/*
			 * Create the dialog GUI to see the processing of the documents
			 */
			LimsImporterUtil limsImporterUtil = new LimsImporterUtil();
			if (limsImporterUtil.checkFileName(getSplitFileName())) {
				// limsFrameProgress.hideFrame();
				return;
			}
		}
	}

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

	@Override
	public DocumentSelectionSignature[] getSelectionSignatures() {
		return new DocumentSelectionSignature[] { new DocumentSelectionSignature(
				NucleotideSequenceDocument.class, 0, Integer.MAX_VALUE) };
	}

	private void messageNoDocumentsSelected() {
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
					limsImporterUtil = null;
					return;
				}

			});
		}
	}

	private Boolean isOverrideCacheName(String fileContent) {
		return fileContent.toString().contains("override_cache_name");
	}

	private Boolean isCacheName(String fileContent) {
		return fileContent.toString().contains("cache_name");
	}

	private void getVersion(int pCnt) {
		String fileName = LimsNotesSplitName.getFileNameFromDocument(pCnt);

		if (fileName == null) {
			fileName = DocumentUtilities.getSelectedDocuments().get(pCnt)
					.getName();
		}

		if (isFileExists() && !isContigFile()) {
			String fileContent = getSelectedDocuments().get(pCnt).toString();
			setVersionNumberExists(fileContent.toString().contains(
					"DocumentVersionCode_Seq"));

			if (isVersionNumberExists()) {
				versienummer = Integer.parseInt(LimsNotesSplitName
						.getVersionNumberFromDocument(pCnt));
			} else {
				setVersionNumber(pCnt);

			}

		} else if (isFileExists() && isContigFile()) {
			String fileContent = getSelectedDocuments().get(pCnt).toString();
			setVersionNumberExists(fileContent.toString().contains(
					"DocumentVersionCode_Seq"));
			if (isVersionNumberExists()) {
				if (isOverrideCacheName(getSelectedDocuments().toString())) {
					versienummer = Integer.parseInt(LimsNotesSplitName
							.getVersionNumberFromDocument(pCnt));
				}
			} else {
				setVersionNumber(pCnt);
			}
		}

	}

	/*
	 * private void getVersionNumber(int cnt) { if (!isVersionNumberExists()) {
	 * setVersienummer(Integer.parseInt(LimsNotesSplitName
	 * .getVersionNumberFromDocument(cnt))); } else if
	 * (isOverrideCacheName(getSelectedDocuments().toString())) { versienummer =
	 * readGeneiousFieldsValues
	 * .getLastVersion_From_ContigAssemblyDocuments((String)
	 * getSelectedDocuments() .get(cnt).getName()); } else if
	 * (!isVersionNumberExists()) { versienummer = readGeneiousFieldsValues
	 * .getLastVersionFromDocument((String) getSelectedDocuments()
	 * .get(cnt).getName()); }
	 * 
	 * setVersionNumber(cnt);
	 * 
	 * }
	 */

	private void setVersionNumber(int pCnt) {
		if (versienummer == 0 && !isFileExists()) {
			versienummer = 1;
		} else if (isFileExists() && isContigFile()) {
			versienummer = readGeneiousFieldsValues
					.getLastVersion_From_ContigAssemblyDocuments((String) getSelectedDocuments()
							.get(pCnt).getName());
			versienummer++;
		} else if (isFileExists() && !isContigFile()) {
			versienummer = readGeneiousFieldsValues
					.getLastVersionFromDocument((String) getSelectedDocuments()
							.get(pCnt).getName());
			versienummer++;
		}
		/* Set version number Fasta file and AB1 */

		limsAB1Fields.setVersieNummer(versienummer);
	}

	private void enrichDocumentFileWithNotes(
			AnnotatedPluginDocument[] annotatedPluginDocument, int cnt) {

		processSplittingTheFileName(cnt, getSelectedDocuments().get(cnt)
				.getName());

		if (getFastaFileName() != null
				&& getFastaFileName().toString().contains(fastaFileExtension)) {
			int i = getFastaFileName().lastIndexOf('.');
			if (i > 0) {
				setExtension(getFastaFileName().substring(i + 1));
			}

			if (isFileExists() && !isExtractIDExists()
					&& getExtension().equals("fas")) {
				if (!isVersionNumberExists()) {
					versienummer = readGeneiousFieldsValues
							.getLastVersionFromDocument((String) getSelectedDocuments()
									.get(cnt).getName());
				}
				setVersionNumber(cnt);
			}
		}

		/* Processing the notes */
		/* Lims 251- Waar is het veld seq quality? */

		limsSplitNotes.enrichSplitDocumentsWithNotes(annotatedPluginDocument,
				cnt, isFileExists(), isExtractIDExists(), versienummer);

		if (limsAB1Fields.getExtractID() == ""
				&& limsAB1Fields.getExtractID().isEmpty()) {
			uitValList.add(getSelectedDocuments().get(cnt).getName());
		}
	}

	private void processSplittingTheFileName(int pCnt, String pFilename) {
		logger.info("Start extracting value from file: " + pFilename);

		verwerkingList.add(pFilename);
		limsSplitNotes.extractDocumentFileName((String) pFilename);
	}

}
