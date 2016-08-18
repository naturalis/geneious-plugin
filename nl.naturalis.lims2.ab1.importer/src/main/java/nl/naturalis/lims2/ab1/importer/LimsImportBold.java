/**
 * <h1>Bold Plugin</h1> 
 * <p>
 * category Lims Import BOLD plugin</br>
 * Date 08 august 2016 </br>
 * Company Naturalis Biodiversity Center City</br>
 * Leiden Country Netherlands
 * </p>
 */
package nl.naturalis.lims2.ab1.importer;

import java.awt.EventQueue;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import nl.naturalis.lims2.utils.LimsDatabaseChecker;
import nl.naturalis.lims2.utils.LimsFrameProgress;
import nl.naturalis.lims2.utils.LimsImporterUtil;
import nl.naturalis.lims2.utils.LimsLogger;
import nl.naturalis.lims2.utils.LimsNotes;
import nl.naturalis.lims2.utils.LimsReadGeneiousFieldsValues;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biomatters.geneious.publicapi.components.Dialogs;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.documents.PluginDocument;
import com.biomatters.geneious.publicapi.implementations.DefaultAlignmentDocument;
import com.biomatters.geneious.publicapi.implementations.sequence.DefaultNucleotideGraphSequence;
import com.biomatters.geneious.publicapi.implementations.sequence.DefaultNucleotideSequence;
import com.biomatters.geneious.publicapi.plugin.DocumentAction;
import com.biomatters.geneious.publicapi.plugin.DocumentOperationException;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;
import com.opencsv.CSVReader;

/**
 * @author Reinier.Kartowikromo
 * @version: 1.0
 */
public class LimsImportBold extends DocumentAction {

	private LimsNotes limsNotes = new LimsNotes();
	private LimsImporterUtil limsImporterUtil = new LimsImporterUtil();
	private LimsBoldFields limsBoldFields = new LimsBoldFields();
	private LimsReadGeneiousFieldsValues readGeneiousFieldsValues = new LimsReadGeneiousFieldsValues();
	private static final Logger logger = LoggerFactory
			.getLogger(LimsImportBold.class);
	private LimsLogger limsLogger = null;
	private LimsFileSelector fcd = new LimsFileSelector();
	private LimsFrameProgress limsFrameProgress = new LimsFrameProgress();

	private List<String> failureList = new ArrayList<String>();
	private List<String> processedList = new ArrayList<String>();
	private List<String> lackList = new ArrayList<String>();
	private List<AnnotatedPluginDocument> listDocuments = new ArrayList<AnnotatedPluginDocument>();

	private String resultRegNum = null;
	private Object documentFileName = "";
	private String boldFileSelected = "";
	private String logBoldFileName = "";
	private String[] record = null;
	private String boldFilePath;
	private String boldFile;
	private String extractIDfileName;
	private String regNumber = "";

	private boolean isRMNHNumber = false;
	private boolean isOverrideCacheName = false;

	public int importCounter;
	private int VerwerktReg = 0;
	private int VerwerktRegMarker = 0;
	private int boldTotaalRecords = 0;

	private long startTime;
	private long lEndTime = 0;
	private long difference = 0;
	private long startBeginTime = 0;

	private DefaultAlignmentDocument defaultAlignmentDocument = null;
	private DefaultNucleotideSequence defaultNucleotideSequence = null;

	@Override
	public void actionPerformed(AnnotatedPluginDocument[] DocumentsSelected) {
		readDataFromBold(DocumentsSelected);

	}

	@Override
	public GeneiousActionOptions getActionOptions() {
		return new GeneiousActionOptions("4 Bold").setInPopupMenu(true)
				.setMainMenuLocation(GeneiousActionOptions.MainMenu.Tools, 3.0)
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
				PluginDocument.class, 0, Integer.MAX_VALUE) };
	}

	/**
	 * Check if document has an ExtractID(Seq)
	 * 
	 * @param missing
	 * **/
	private String getLackMessage(Boolean missing) {
		if (missing)
			return "[3] At least one selected document lacks ExtractID(Seq)";
		return "";
	}

	/**
	 * Select one- or more documents and read data from the BOLD cvs file to add
	 * notes to the document(s).
	 * 
	 * @param annotatedDocument
	 * */
	private void readDataFromBold(AnnotatedPluginDocument[] annotatedDocument) {

		LimsDatabaseChecker dbchk = new LimsDatabaseChecker();
		if (!dbchk.checkDBName()) {
			return;
		}

		/* Get Databasename */
		readGeneiousFieldsValues.activeDB = readGeneiousFieldsValues
				.getServerDatabaseServiceName();

		/* if database exists then continue the process else abort. */
		if (readGeneiousFieldsValues.activeDB != null) {

			/* if no documents in Geneious has been selected show a message. */
			if (DocumentUtilities.getSelectedDocuments().isEmpty()) {
				EventQueue.invokeLater(new Runnable() {

					@Override
					public void run() {
						Dialogs.showMessageDialog("Select at least one document.");
						return;
					}
				});
			} else /*
					 * if documents has been selected continue the process to
					 * import data
					 */
			if (!DocumentUtilities.getSelectedDocuments().isEmpty()) {
				/*
				 * Get the path of the log Uitvallijst from the propertie file:
				 * lims-import.properties.
				 */
				setBoldLogFileName();

				/* Create logfile */
				limsLogger = new LimsLogger(logBoldFileName);

				/*
				 * Open a dialog screen to choose a BOLD file
				 */
				boldFileSelected = fcd.loadSelectedFile();
				if (boldFileSelected == null) {
					return;
				}

				/* Create Dialog windows for processing the file */
				limsFrameProgress.createProgressGUI();
				logger.info("------------------------------S T A R T -----------------------------------");
				logger.info("Start reading from Bold File(s)");

				failureList.clear();

				String[] headerCOI = null;

				/* Get the total records from the BOLD file */
				boldTotaalRecords = limsImporterUtil
						.countRecordsCSV(boldFileSelected);

				logger.info("Totaal records Bold file: " + boldTotaalRecords);

				/* Start time of processing the notes . */
				startTime = new Date().getTime();

				/* Create CSv object to read the Csv file. */
				try {
					CSVReader csvReader = new CSVReader(new FileReader(
							boldFileSelected), '\t', '\'', 0);

					/* Get the rowheader of the csv file. */
					headerCOI = csvReader.readNext();

					try {

						while ((record = csvReader.readNext()) != null) {
							/*
							 * Continue if record is empty
							 */
							if (record.length == 1 && record[0].isEmpty()) {
								continue;
							}

							/*
							 * Get the registration number from the CSV file.
							 * Sample ID
							 */
							regNumber = record[2];

							/* Get the total size of selected documents */
							importCounter = DocumentUtilities
									.getSelectedDocuments().size();

							/*
							 * Add selected document to a List.
							 */
							listDocuments = DocumentUtilities
									.getSelectedDocuments();

							int cnt = 0;
							/* Looping thru the selected documents to add notes. */
							for (AnnotatedPluginDocument list : listDocuments) {

								resultRegNum = null;
								isRMNHNumber = false;

								/* get documentname. */
								documentFileName = list.getName();

								/* Reads Assembly Contig 1 file */
								checkIfDcoumentContainsOverrideCacheName(list);

								/*
								 * Check if document already contain the note
								 * RegistrationNumberCode_Samples
								 */
								isRMNHNumber = list.toString().contains(
										"RegistrationNumberCode_Samples");

								if (isRMNHNumber) {
									/*
									 * Get value from
									 * "RegistrationnumberCode_Samples"
									 */
									resultRegNum = (String) (list
											.getDocumentNotes(true)
											.getNote(
													"DocumentNoteUtilities-Registr-nmbr (Samples)")
											.getFieldValue("RegistrationNumberCode_Samples"));
								} else {
									if (!lackList.toString().contains(
											list.getName())) {
										lackList.add(list.getName());
										logger.info("At least one selected document lacks Registr-nmbr (Sample)."
												+ list.getName());
									}
								}

								/*
								 * if not Match on registration number go to the
								 * next record
								 */
								if ((resultRegNum == null)
										|| (!regNumber.trim().equals(
												resultRegNum) && !DocumentUtilities
												.getSelectedDocuments()
												.isEmpty())) {
									cnt++;
									continue;
								}

								/* Match only on registration number */
								addBoldNotesToDocuments(annotatedDocument,
										regNumber, cnt);

								/*
								 * Match only on registration number and Marker
								 */
								addBoldNotesMatchRegistrationAndMarker(
										annotatedDocument, headerCOI,
										regNumber, cnt);

								cnt++;
							} // end for
							/*
							 * Add records to the failure list which does not
							 * matched.
							 */
							addRegistrationNumberToFailureList(regNumber);
						} // end While

						logger.info("Total of document(s) updated: "
								+ processedList.size());
						logger.info("------------------------------E N D -----------------------------------");
						logger.info("Done with reading bold file. ");

						/*
						 * add the total of records that has been skipped(no
						 * match).
						 */
						if (documentFileName != null) {
							failureList.add("Total records: "
									+ Integer.toString(failureList.size())
									+ "\n");
						}

						/** Calculating the Duration of the import **/
						setProcessingDurationTime();

						/* Show result information after the import of data. */
						EventQueue.invokeLater(new Runnable() {

							@Override
							public void run() {

								/* Hide the progressbar */
								limsFrameProgress.hideFrame();
								int totaalVerwerkt = VerwerktReg
										+ VerwerktRegMarker;
								showDialogMessageBoldEndProcess(totaalVerwerkt);

								logger.info("Bold: Total of document(s) updated: "
										+ processedList.size() + "\n");

								failureList.add("Bold filename: "
										+ boldFileSelected + "\n");
								/* Save the logfile */
								limsLogger.logToFile(logBoldFileName,
										failureList.toString());

								// failureList.clear();
								processedList.clear();
								lackList.clear();

							}

							/**
							 * @param totaalVerwerkt
							 */
							private void showDialogMessageBoldEndProcess(
									int totaalVerwerkt) {
								Dialogs.showMessageDialog(Integer
										.toString(boldTotaalRecords)
										+ " records have been read of which: "
										+ "\n"
										+ "[1] "
										+ processedList.size()
										+ " records are imported and linked to "
										+ Integer.toString(totaalVerwerkt)
										+ " existing documents (of "
										+ importCounter
										+ " selected)"
										+ "\n"
										+ "\n"
										+ "[2] "
										+ Integer.toString(failureList.size() - 1)
										+ " records are ignored."
										+ "\n"
										+ "\n"
										+ getLackMessage(isLackListNotEmpty()));
							}
						});

					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						csvReader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Check of there are document(s) without registration number (Samples)
	 * 
	 * @return
	 */
	private boolean isLackListNotEmpty() {
		if (lackList.size() > 0)
			return true;
		return false;
	}

	/**
	 * Get logfilename from the properties file
	 */
	private void setBoldLogFileName() {
		logBoldFileName = limsImporterUtil.getLogPath() + "Bold-Uitvallijst-"
				+ limsImporterUtil.getLogFilename();
	}

	/**
	 * Add registration number to failure list
	 * 
	 * @param regNumber
	 * @return
	 */
	private void addRegistrationNumberToFailureList(String regNumber) {

		if (!processedList.toString().contains(regNumber)
				&& regNumber.matches(".*\\d+.*")) {

			if (!failureList.toString().contains(regNumber)
					&& regNumber.matches(".*\\d+.*")) {
				failureList.add(regNumber + "\n");
				limsFrameProgress.showProgress("No match : " + documentFileName
						+ "\n");
			}

		}
	}

	/**
	 * Set the duration time of processing the documents.
	 */
	private void setProcessingDurationTime() {
		lEndTime = new Date().getTime();
		difference = lEndTime - startTime;
		String hms = String.format(
				"%02d:%02d:%02d",
				TimeUnit.MILLISECONDS.toHours(difference),
				TimeUnit.MILLISECONDS.toMinutes(difference)
						% TimeUnit.HOURS.toMinutes(1),
				TimeUnit.MILLISECONDS.toSeconds(difference)
						% TimeUnit.MINUTES.toSeconds(1));
		logger.info("Import records in : '" + hms
				+ " hour(s)/minute(s)/second(s).'");
		logger.info("Import records in : '"
				+ TimeUnit.MILLISECONDS.toMinutes(difference) + " minutes.'");
	}

	/**
	 * Check if document(s) contais "override_cache_name"
	 * 
	 * @param list
	 */
	private void checkIfDcoumentContainsOverrideCacheName(
			AnnotatedPluginDocument list) {
		try {

			/*
			 * Check if document contains override_cache_name
			 */
			isOverrideCacheName = list.toString().contains(
					"override_cache_name");

			if (isOverrideCacheName) {
				/*
				 * Copy of AB1 document is saved as DefaultNucleotideSequence
				 */
				if (documentFileName.toString().contains("Copy")
						|| documentFileName.toString().contains("kopie")) {
					defaultNucleotideSequence = (DefaultNucleotideSequence) list
							.getDocument();
					documentFileName = defaultNucleotideSequence.getName();
				}
				/*
				 * Copy of AB1 document is saved as
				 * DefaultNucleotideGraphSequence
				 */
				else if ((documentFileName.toString().contains("Copy") || documentFileName
						.toString().contains("kopie"))
						&& documentFileName.toString().contains(".ab1")) {
					defaultNucleotideSequence = (DefaultNucleotideGraphSequence) list
							.getDocument();
					documentFileName = defaultNucleotideSequence.getName();
				} else {
					defaultAlignmentDocument = (DefaultAlignmentDocument) list
							.getDocument();
					documentFileName = defaultAlignmentDocument.getName();
				}
			}
		} catch (DocumentOperationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Add Notes to the selected documents match only on Registration number and
	 * Marker
	 * 
	 * @param annotatedDocument
	 * @param headerCOI
	 * @param regNumber
	 * @param cnt
	 */
	private void addBoldNotesMatchRegistrationAndMarker(
			AnnotatedPluginDocument[] annotatedDocument, String[] headerCOI,
			String regNumber, int cnt) {

		if (regNumber.equals(resultRegNum)
				&& headerCOI[6].equals("COI-5P Seq. Length") && isRMNHNumber) {

			/* Get the start time of processing */
			startBeginTime = System.nanoTime();

			/*
			 * Start the progressbar and show some information.
			 */
			limsFrameProgress.showProgress("Match: " + documentFileName + "\n");
			/*
			 * Set value from the file to the variables
			 * 
			 * record[6] = COI-5P Seq. Length, record[7] = COI-5P Trace Count,
			 * record[8] = COI-5P Accession
			 */
			setNotesThatMatchRegistrationNumberAndMarker(record[6], record[7],
					record[8]);

			/* Set the notes. */
			setNotesToBoldDocumentsRegistrationMarker(annotatedDocument, cnt);
			/*
			 * Add the registration number to the document which has been
			 * processed.
			 */
			if (!processedList.toString().contains(regNumber)) {
				processedList.add(regNumber);
				VerwerktRegMarker++;
			}

			/*
			 * Get the end time to see the duration from processing the notes.
			 */
			long endTime = System.nanoTime();
			long elapsedTime = endTime - startBeginTime;
			logger.info("Took: "
					+ (TimeUnit.SECONDS.convert(elapsedTime,
							TimeUnit.NANOSECONDS)) + " second(s)");
			elapsedTime = 0;
		}
	}

	/**
	 * Add Notes to the selected documents match only on Registration number
	 * 
	 * @param annotatedDocument
	 * @param regNumber
	 * @param cnt
	 * @throws IOException
	 */
	private void addBoldNotesToDocuments(
			AnnotatedPluginDocument[] annotatedDocument, String regNumber,
			int cnt) throws IOException {

		if (regNumber.equals(resultRegNum) && isRMNHNumber) {
			// isMatchedRegNr = true;
			/*
			 * Start the processing time for the notes adding to the document.
			 */
			startBeginTime = System.nanoTime();

			/* Show the progressbar with information */
			limsFrameProgress.showProgress("Match: " + documentFileName + "\n");

			/* get the Process ID = NLCOA778-12 */
			String processID = record[1];
			String boldURI = "";
			/*
			 * get the URI from the propertie file lims-import.properties and
			 * concatenating the Process ID
			 */
			if (processID != null) {
				boldURI = limsImporterUtil.getPropValues("bolduri") + record[1];
			}

			/*
			 * Set value from the variables and logfile BoldID = 1,
			 * NumberofImagesBold = 9, BoldProjectID = 0, FieldID = 3, BoldBIN =
			 * 4, BoldURI = uit LimsProperties File
			 */
			setNotesThatMatchRegistrationNumber(record[1], record[9],
					record[0], record[3], record[4], boldURI);

			/* Set the notes. */
			setNotesToBoldDocumentsRegistration(annotatedDocument, cnt);

			/*
			 * Add the registration number to the document which has been
			 * processed.
			 */
			if (!processedList.toString().contains(regNumber)) {
				processedList.add(regNumber);
				VerwerktReg++;

			}
			/*
			 * Duration time of adding notes.
			 */
			long endTime = System.nanoTime();
			long elapsedTime = endTime - startBeginTime;
			logger.info("Took: "
					+ (TimeUnit.SECONDS.convert(elapsedTime,
							TimeUnit.NANOSECONDS)) + " second(s)");
			elapsedTime = 0;
		}
	}

	/**
	 * Set value to documents notes if match on Registration and Marker
	 * 
	 * @param annotatedPluginDocuments
	 *            , cnt
	 * */
	private void setNotesToBoldDocumentsRegistrationMarker(
			AnnotatedPluginDocument[] annotatedPluginDocuments, int cnt) {
		/** set note for TraceFile Presence */
		limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
				"TraceFilePresenceCode_Bold", "N traces (Bold)",
				"N traces (Bold)", limsBoldFields.getTraceFilePresence(), cnt);

		/** set note for Nucleotide Length */
		limsNotes
				.setNoteToAB1FileName(annotatedPluginDocuments,
						"NucleotideLengthCode_Bold", "Nucl-length (Bold)",
						"Nucl-length (Bold)",
						limsBoldFields.getNucleotideLength(), cnt);

		/** set note for GenBankID */
		limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
				"GenBankIDCode_Bold", "GenBank ID (Bold)", "GenBank ID (Bold)",
				limsBoldFields.getGenBankID(), cnt);

		/** set note for GenBank URI */
		try {
			limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
					"GenBankURICode_FixedValue_Bold", "GenBank URI (Bold)",
					"GenBank URI (Bold)",
					limsImporterUtil.getPropValues("boldurigenbank")
							+ limsBoldFields.getCoi5PAccession(), cnt);
		} catch (IOException e) {
			e.printStackTrace();
		}

		logger.info("Done with adding notes to the document");
		logger.info(" ");
	}

	/**
	 * Set value to documents notes if match only on registration number
	 * 
	 * @param annotatedPluginDocuments
	 *            , cnt
	 * */
	private void setNotesToBoldDocumentsRegistration(
			AnnotatedPluginDocument[] annotatedPluginDocuments, int cnt) {
		/** set note for BOLD-ID */
		limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
				"BOLDIDCode_Bold", "BOLD ID (Bold)", "BOLD ID (Bold)",
				limsBoldFields.getBoldID(), cnt);

		/** set note for Number of Images */
		limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
				"NumberOfImagesCode_Bold", "N images (Bold)",
				"N images (Bold)", limsBoldFields.getNumberOfImagesBold(), cnt);

		/** set note for BoldProjectID */
		limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
				"BOLDprojIDCode_Bold", "BOLD proj-ID (Bold)",
				"BOLD proj-ID (Bold)", limsBoldFields.getBoldProjectID(), cnt);

		/** set note for FieldID */
		limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
				"FieldIDCode_Bold", "Field ID (Bold)", "Field ID (Bold)",
				limsBoldFields.getFieldID(), cnt);

		/** set note for BOLD BIN Code */
		limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
				"BOLDBINCode_Bold", "BOLD BIN (Bold)", "BOLD BIN (Bold)",
				limsBoldFields.getBoldBIN(), cnt);

		/** set note for BOLD URI */
		limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
				"BOLDURICode_FixedValue_Bold", "BOLD URI (Bold)",
				"BOLD URI (Bold)", limsBoldFields.getBoldURI(), cnt);

		logger.info("Done with adding notes to the document");
	}

	/**
	 * Set value to variable
	 * 
	 * @param boldID
	 *            , numberOfImagesBold, boldProjectID, fieldID, boldBIN, boldURI
	 * */
	private void setNotesThatMatchRegistrationNumber(String boldID,
			String numberOfImagesBold, String boldProjectID, String fieldID,
			String boldBIN, String boldURI) {

		logger.info("Match Bold record only on registrationnumber.");

		limsBoldFields.setBoldID(boldID);
		limsBoldFields.setNumberOfImagesBold(numberOfImagesBold);
		limsBoldFields.setBoldProjectID(boldProjectID);
		limsBoldFields.setFieldID(fieldID);
		limsBoldFields.setBoldBIN(boldBIN);
		limsBoldFields.setBoldURI(boldURI);

		logger.info("Bold-ID: " + limsBoldFields.getBoldID());
		logger.info("Number of Images Bold: "
				+ limsBoldFields.getNumberOfImagesBold());
		logger.info("BoldProjectID: " + limsBoldFields.getBoldProjectID());
		logger.info("FieldID: " + limsBoldFields.getFieldID());
		logger.info("BoldBIN: " + limsBoldFields.getBoldBIN());
		logger.info("BoldURI: " + limsBoldFields.getBoldURI());

	}

	/**
	 * Set value to variable
	 * 
	 * @param nucleotideLength
	 *            , tracebestandPresence, coi5pAccession
	 * */
	private void setNotesThatMatchRegistrationNumberAndMarker(
			String nucleotideLength, String tracebestandPresence,
			String coi5pAccession) {

		logger.info("Match Bold record on registrationnumber and marker.");

		limsBoldFields.setNucleotideLength(nucleotideLength);
		limsBoldFields.setTraceFilePresence(tracebestandPresence);
		limsBoldFields.setGenBankID(coi5pAccession);
		limsBoldFields.setCoi5PAccession(coi5pAccession);

		logger.info("Nucleotide length: "
				+ limsBoldFields.getNucleotideLength());
		logger.info("TraceFile Presence: "
				+ limsBoldFields.getTraceFilePresence());
		logger.info("GenBankID: " + limsBoldFields.getCoi5PAccession());
		try {
			logger.info("GenBankUri: "
					+ limsImporterUtil.getPropValues("boldurigenbank")
					+ limsBoldFields.getCoi5PAccession());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getBoldFilePath() {
		return boldFilePath;
	}

	public void setBoldFilePath(String boldFilePath) {
		this.boldFilePath = boldFilePath;
	}

	public String getBoldFile() {
		return boldFile;
	}

	public void setBoldFile(String boldFile) {
		this.boldFile = boldFile;
	}

	public String getExtractIDfileName() {
		return extractIDfileName;
	}

	public void setExtractIDfileName(String extractIDfileName) {
		this.extractIDfileName = extractIDfileName;
	}

}
