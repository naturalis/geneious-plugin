/**
 * <h1>Bold Plugin</h1> 
 */
package nl.naturalis.lims2.ab1.importer;

import java.awt.EventQueue;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
 * @author Reinier.Kartowikromo category Lims Import BOLD plugin
 * @version: 1.0 Date 08 august 2016 Company Naturalis Biodiversity Center City
 *           Leiden Country Netherlands
 */
public class LimsImportBold extends DocumentAction {

	private LimsNotes limsNotes = new LimsNotes();
	private LimsImporterUtil limsImporterUtil = new LimsImporterUtil();
	private LimsBoldFields limsBoldFields = new LimsBoldFields();
	private LimsReadGeneiousFieldsValues readGeneiousFieldsValues = new LimsReadGeneiousFieldsValues();
	private static final Logger logger = LoggerFactory
			.getLogger(LimsReadDataFromBold.class);
	private LimsLogger limsLogger = null;
	private LimsFileSelector fcd = new LimsFileSelector();
	private LimsFrameProgress limsFrameProgress = new LimsFrameProgress();

	private List<String> msgList = new ArrayList<String>();
	private List<String> failureList = new ArrayList<String>();
	private List<String> processedList = new ArrayList<String>();
	private List<String> lackBoldList = new ArrayList<String>();
	private List<AnnotatedPluginDocument> listDocuments = new ArrayList<AnnotatedPluginDocument>();

	private Object resultRegNum = null;
	private Object documentFileName = "";
	private String boldFileSelected = "";
	private String logBoldFileName = "";
	private String[] record = null;
	private String boldFilePath;
	private String boldFile;
	private String extractIDfileName;

	private boolean isRMNHNumber = false;
	private boolean isOverrideCacheName = false;

	public int importCounter;
	private int VerwerktReg = 0;
	private int VerwerktRegMarker = 0;
	private int BoldTotaalRecords = 0;

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
		return new GeneiousActionOptions("7 Bold new").setInPopupMenu(true)
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

	/** Select documents and read data from the BOLD cvs file */
	private void readDataFromBold(AnnotatedPluginDocument[] annotatedDocument) {

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
				msgList.clear();

				/*
				 * Get the path of the log Uitvallijst from the propertie file:
				 * lims-import.properties.
				 */
				logBoldFileName = limsImporterUtil.getLogPath()
						+ "Bold-Uitvallijst-"
						+ limsImporterUtil.getLogFilename();

				/* Create logfile */
				limsLogger = new LimsLogger(logBoldFileName);

				/*
				 * Dialoogscherm voor het selecteren van een Bold file om in
				 * kunnen te lezen.
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
				failureList.add("Bold filename: " + boldFileSelected + "\n");

				String[] headerCOI = null;

				/* Get the total records from the BOLD file */
				if (BoldTotaalRecords == 0) {
					try {
						CSVReader csvReadertot = new CSVReader(new FileReader(
								boldFileSelected), '\t', '\'', 1);
						BoldTotaalRecords = csvReadertot.readAll().size();
						csvReadertot.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				logger.info("Totaal records Bold file: " + BoldTotaalRecords);

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
							String regNumber = record[2];

							/*
							 * Add selected document to a List.
							 */
							listDocuments = DocumentUtilities
									.getSelectedDocuments();

							int cnt = 0;
							/* Looping thru the selected documents to add notes. */
							for (AnnotatedPluginDocument list : listDocuments) {

								resultRegNum = null;
								/* get documentname. */
								documentFileName = list.getName();

								/* Add sequence name for the dialog screen */
								if (DocumentUtilities.getSelectedDocuments()
										.listIterator().hasNext()) {
									msgList.add(documentFileName + "\n");
								}

								/* Reads Assembly Contig 1 file */
								try {

									/*
									 * Check if document contains
									 * override_cache_name
									 */
									isOverrideCacheName = list.toString()
											.contains("override_cache_name");

									if (isOverrideCacheName) {
										/*
										 * Copy of AB1 document is saved as
										 * DefaultNucleotideSequence
										 */
										if (documentFileName.toString()
												.contains("Copy")
												|| documentFileName.toString()
														.contains("kopie")) {
											defaultNucleotideSequence = (DefaultNucleotideSequence) list
													.getDocument();
											documentFileName = defaultNucleotideSequence
													.getName();
										}
										/*
										 * Copy of AB1 document is saved as
										 * DefaultNucleotideGraphSequence
										 */
										else if ((documentFileName.toString()
												.contains("Copy") || documentFileName
												.toString().contains("kopie"))
												&& documentFileName.toString()
														.contains(".ab1")) {
											defaultNucleotideSequence = (DefaultNucleotideGraphSequence) list
													.getDocument();
											documentFileName = defaultNucleotideSequence
													.getName();
										} else {
											defaultAlignmentDocument = (DefaultAlignmentDocument) list
													.getDocument();
											documentFileName = defaultAlignmentDocument
													.getName();
										}
									}
								} catch (DocumentOperationException e) {
									e.printStackTrace();
								}

								/*
								 * Check if document already contain the note
								 * RegistrationNumberCode_Samples
								 */
								isRMNHNumber = DocumentUtilities
										.getSelectedDocuments()
										.get(cnt)
										.toString()
										.contains(
												"RegistrationNumberCode_Samples");

								/* if not contain RegistrationNumberCode_Samples */
								if (!isRMNHNumber) {
									/*
									 * All documents will be added to the
									 * lackBoldlist
									 */
									if (!lackBoldList
											.contains(DocumentUtilities
													.getSelectedDocuments()
													.get(cnt).getName())) {
										lackBoldList.add(DocumentUtilities
												.getSelectedDocuments()
												.get(cnt).getName());
										logger.info("At least one selected document lacks Registr-nmbr (Sample)."
												+ DocumentUtilities
														.getSelectedDocuments()
														.get(cnt).getName());
									}
								} else {
									/*
									 * Get value from
									 * "RegistrationnumberCode_Samples"
									 */
									resultRegNum = (list
											.getDocumentNotes(true)
											.getNote(
													"DocumentNoteUtilities-Registr-nmbr (Samples)")
											.getFieldValue("RegistrationNumberCode_Samples"));
								}

								/** Match only on registration number */
								if (regNumber.equals(resultRegNum)
										&& isRMNHNumber) {

									/*
									 * Start the processing time for the notes
									 * adding to the document.
									 */
									startBeginTime = System.nanoTime();

									/* Show the progressbar with information */
									limsFrameProgress.showProgress("Match: "
											+ documentFileName + "\n");

									/* get the Process ID = NLCOA778-12 */
									String processID = record[1];
									String boldURI = "";
									/*
									 * get the URI from the propertie file
									 * lims-import.properties and concatenating
									 * the Process ID
									 */
									if (processID != null) {
										boldURI = limsImporterUtil
												.getPropValues("bolduri")
												+ record[1];
									}

									/*
									 * Set value from the variables and logfile
									 * BoldID = 1, NumberofImagesBold = 9,
									 * BoldProjectID = 0, FieldID = 3, BoldBIN =
									 * 4, BoldURI = uit LimsProperties File
									 */
									setNotesThatMatchRegistrationNumber(
											record[1], record[9], record[0],
											record[3], record[4], boldURI);

									/* Set the notes. */
									setNotesToBoldDocumentsRegistration(
											annotatedDocument, cnt);

									/*
									 * Add the registration number to the
									 * document which has been processed.
									 */
									if (!processedList.toString().contains(
											regNumber)) {
										processedList.add(regNumber);
										VerwerktReg++;
									}
									/*
									 * Duration time of adding notes.
									 */
									long endTime = System.nanoTime();
									long elapsedTime = endTime - startBeginTime;
									logger.info("Took: "
											+ (TimeUnit.SECONDS.convert(
													elapsedTime,
													TimeUnit.NANOSECONDS))
											+ " second(s)");
									elapsedTime = 0;
								}

								/**
								 * Match only on registration number and Marker
								 */
								if (regNumber.equals(resultRegNum)
										&& headerCOI[6]
												.equals("COI-5P Seq. Length")
										&& isRMNHNumber) {

									/* Get the start time of processing */
									startBeginTime = System.nanoTime();

									/*
									 * Start the progressbar and show some
									 * information.
									 */
									limsFrameProgress.showProgress("Match: "
											+ documentFileName + "\n");
									/*
									 * Set value from the file to the variables
									 * 
									 * record[6] = COI-5P Seq. Length, record[7]
									 * = COI-5P Trace Count, record[8] = COI-5P
									 * Accession
									 */
									setNotesThatMatchRegistrationNumberAndMarker(
											record[6], record[7], record[8]);

									/* Set the notes. */
									setNotesToBoldDocumentsRegistrationMarker(
											annotatedDocument, cnt);
									/*
									 * Add the registration number to the
									 * document which has been processed.
									 */
									if (!processedList.toString().contains(
											regNumber)) {
										processedList.add(regNumber);
										VerwerktRegMarker++;
									}

									/*
									 * Get the end time to see the duration from
									 * processing the notes.
									 */
									long endTime = System.nanoTime();
									long elapsedTime = endTime - startBeginTime;
									logger.info("Took: "
											+ (TimeUnit.SECONDS.convert(
													elapsedTime,
													TimeUnit.NANOSECONDS))
											+ " second(s)");
									elapsedTime = 0;

								}

								/*
								 * Log the total of records that not has been
								 * processed/skip
								 */
								if (!processedList.toString().contains(
										regNumber)
										&& regNumber.matches(".*\\d+.*")) {
									if (!failureList.toString().contains(
											regNumber)) {
										failureList
												.add("No document(s) match found for Registrationnumber: "
														+ regNumber + "\n");
										limsFrameProgress
												.showProgress("No match: "
														+ documentFileName
														+ "\n");
									}
								}
								cnt++;
							} // end for
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
						lEndTime = new Date().getTime();
						difference = lEndTime - startTime;
						String hms = String.format("%02d:%02d:%02d",
								TimeUnit.MILLISECONDS.toHours(difference),
								TimeUnit.MILLISECONDS.toMinutes(difference)
										% TimeUnit.HOURS.toMinutes(1),
								TimeUnit.MILLISECONDS.toSeconds(difference)
										% TimeUnit.MINUTES.toSeconds(1));
						logger.info("Import records in : '" + hms
								+ " hour(s)/minute(s)/second(s).'");
						logger.info("Import records in : '"
								+ TimeUnit.MILLISECONDS.toMinutes(difference)
								+ " minutes.'");

						/* Show result information after the import of data. */
						EventQueue.invokeLater(new Runnable() {

							@Override
							public void run() {
								int totaalVerwerkt = VerwerktReg
										+ VerwerktRegMarker;
								Dialogs.showMessageDialog(Integer
										.toString(BoldTotaalRecords)
										+ " records have been read of which: "
										+ "\n"
										+ "[1] "
										+ processedList.size()
										+ " records are imported and linked to "
										+ Integer.toString(totaalVerwerkt)
										+ " existing documents (of "
										+ listDocuments.size()
										+ " selected)"
										+ "\n"
										+ "\n"
										+ "List of "
										+ Integer.toString(listDocuments.size())
										+ " selected documents: "
										+ "\n"
										+ "[2] "
										+ Integer.toString(failureList.size() - 2)
										+ " records are ignored."
										+ "\n"
										+ "\n"
										+ "[3] "
										+ "At least one or "
										+ lackBoldList.size()
										+ " selected document lacks Registr-nmbr (Sample).");

								logger.info("Bold: Total of document(s) updated: "
										+ processedList.size() + "\n");

								/* Save the logfile */
								limsLogger.logToFile(logBoldFileName,
										failureList.toString());

								msgList.clear();
								failureList.clear();
								processedList.clear();
								/* Hide the progressbar */
								limsFrameProgress.hideFrame();
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

	/** Set value to documents notes if match on Registration and Marker */
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

	/** Set value to documents notes if match only on registration number */
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

	/** Set value to variable */
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

	/* Set value to variable */
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
