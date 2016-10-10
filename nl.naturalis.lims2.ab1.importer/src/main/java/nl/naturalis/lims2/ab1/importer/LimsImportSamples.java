/**
 * <h1>Lims Samples Plugin</h1> 
 * category Lims Import Samples plugin
 * Date 08 august 2016 
 * Company Naturalis Biodiversity Center City
 * Leiden Country Netherlands
 */
package nl.naturalis.lims2.ab1.importer;

import java.awt.EventQueue;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import nl.naturalis.lims2.utils.LimsDatabaseChecker;
import nl.naturalis.lims2.utils.LimsFrameProgress;
import nl.naturalis.lims2.utils.LimsImporterUtil;
import nl.naturalis.lims2.utils.LimsLogger;
import nl.naturalis.lims2.utils.LimsNotes;
import nl.naturalis.lims2.utils.LimsReadGeneiousFieldsValues;
import nl.naturalis.lims2.utils.LimsSQL;
import nl.naturalis.lims2.utils.LimsSamplesFields;
import nl.naturalis.lims2.utils.LimsSamplesNotes;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biomatters.geneious.publicapi.components.Dialogs;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.documents.PluginDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentAction;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;
import com.opencsv.CSVReader;

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
 * Samples plugin: Select one or more documents and click on the
 * "1 of 2 Samples button".<br>
 * A dialog screen is displayed. <br>
 * Browse to the CSV files<br>
 * Select a Sample(s) Csv file. The import process is started. <br>
 * If there is a match between the ID from the csv file with the Extract
 * filename (e4010125015) of the AB1 or Fasta ID in the one of the selected
 * document(s),<br>
 * the notes will be added to the selected document(s) in Geneious.<br>
 * A processing log(matching a registration number) and failure log(not matching
 * a ExtractID) is created.</td>
 * </tr>
 * </table>
 * 
 * @author Reinier.Kartowikromo
 * @version: 1.0
 */
public class LimsImportSamples extends DocumentAction {

	private LimsImporterUtil limsImporterUtil = new LimsImporterUtil();
	private LimsSamplesFields limsExcelFields = new LimsSamplesFields();
	private LimsNotes limsNotes = new LimsNotes();
	private LimsFileSelector fcd = new LimsFileSelector();
	private LimsReadGeneiousFieldsValues ReadGeneiousFieldsValues = new LimsReadGeneiousFieldsValues();
	private LimsDummySeq limsDummySeq = new LimsDummySeq();
	private static final Logger logger = LoggerFactory
			.getLogger(LimsImportSamples.class);
	private LimsFrameProgress limsFrameProgress = new LimsFrameProgress();
	private JFrame frame = new JFrame();
	private LimsLogger limsLogger = null;
	private LimsSQL limsSQL = new LimsSQL();
	private LimsSamplesNotes limsSamplesNotes = new LimsSamplesNotes();

	private List<String> msgList = new ArrayList<String>();
	private List<String> failureList = new ArrayList<String>();
	private List<String> processedList = new ArrayList<String>();
	private List<String> lackList = new ArrayList<String>();
	private List<AnnotatedPluginDocument> listDocuments = new ArrayList<AnnotatedPluginDocument>();

	private CSVReader csvReader = null;

	private String fileSelected = "";

	private String ID = "";
	private String plateNumber = "";
	private String extractIDfileName = "";
	private String logSamplesFileName = "";
	private String documentFileName = "";
	private String readAssembyContigFileName = "";
	private String regScientificname = "";

	private boolean isExtractIDSeqExists = false;
	private boolean match = false;

	private int version = 0;
	private int dummyRecordsVerwerkt = 0;
	private long startTime;
	private long lEndTime = 0;
	private long difference = 0;
	private int sampleTotaalRecords = 0;
	private int importCounter = 0;
	private int sampleRecordFailure = 0;
	private int sampleExactRecordsVerwerkt = 0;
	private int recordCount = 0;
	private int cntRec = 0;

	/*
	 * private final String documentClassAB1 = "ABIPluginDocument"; private
	 * final String documentClassFasta = "DefaultNucleotideSequence"; private
	 * final String documentClassDummy = "DefaultNucleotideSequence";
	 */
	private final String documentTypeNovoAssembly = "NucleotideSequenceDocument";
	private final String documentTypeConsensusSequence = "DefaultAlignmentDocument";

	/**
	 * Read the values from the CSV files of Samples to start adding notes to
	 * the document(s)
	 * 
	 * @param annotatedPluginDocument
	 *            Set param annotatedPluginDocument
	 * */
	@Override
	public void actionPerformed(
			AnnotatedPluginDocument[] annotatedPluginDocument) {
		readSamplesDataFromCSVFile(annotatedPluginDocument);
	}

	/**
	 * Add the name for the Samples plugin to the Menu
	 * 
	 * @return Set the name of the plugin to the menubar
	 * @see LimsImportSamples
	 * */
	@Override
	public GeneiousActionOptions getActionOptions() {
		return new GeneiousActionOptions("1 of 2 Samples").setInPopupMenu(true)
				.setMainMenuLocation(GeneiousActionOptions.MainMenu.Tools, 1.0)
				.setInMainToolbar(true).setInPopupMenu(true)
				.setAvailableToWorkflows(true);
	}

	/**
	 * Not yet implemented
	 * 
	 * @return Not yet implemented
	 * @see LimsImportSamples
	 * */
	@Override
	public String getHelp() {
		return null;
	}

	/**
	 * Add the max value of selected document(s)<br>
	 * public static final int MAX_VALUE = 2147483647;
	 * 
	 * @return Return the max value of the documents that has import.
	 * @see LimsImportSamples
	 * */
	@Override
	public DocumentSelectionSignature[] getSelectionSignatures() {
		return new DocumentSelectionSignature[] { new DocumentSelectionSignature(
				PluginDocument.class, 0, Integer.MAX_VALUE) };
	}

	/*
	 * Check of there are document(s) without registration number (Samples)
	 * 
	 * @return
	 */
	private boolean isLackListNotEmpty() {
		if (lackList.size() > 0)
			return true;
		return false;
	}

	/*
	 * Get lack message of document(s) without registrationnumber (Samples)
	 * 
	 * @param missing
	 * 
	 * @return
	 */
	private String getLackMessage(Boolean missing) {
		if (missing)
			return "[4] At least one selected document lacks ExtractID(Seq)";
		return "";
	}

	/* Read data from the csv file and add the notes */
	private void readSamplesDataFromCSVFile(AnnotatedPluginDocument[] documents) {

		LimsDatabaseChecker dbchk = new LimsDatabaseChecker();
		if (!dbchk.checkDBName()) {
			return;
		}
		/* Get Database name */
		ReadGeneiousFieldsValues.activeDB = ReadGeneiousFieldsValues
				.getServerDatabaseServiceName();

		if (ReadGeneiousFieldsValues.activeDB != null) {

			Object[] options = { "Ok", "No", "Cancel" };
			int n = JOptionPane.showOptionDialog(frame,
					"Create dummy sequences for unknown extract ID's?",
					"Samples", JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, options, options[2]);

			/* If OK Selected */
			if (n == 0) {
				/* Check if document(s) has been selected * */
				if (!DocumentUtilities.getSelectedDocuments().isEmpty()) {

					/* Get uitvallijst logfile name */
					logSamplesFileName = limsImporterUtil.getLogPath()
							+ "Sample-method-Uitvallijst-"
							+ limsImporterUtil.getLogFilename();

					limsLogger = new LimsLogger(logSamplesFileName);

					logger.info("Start updating selected document(s).");
					fileSelected = fcd.loadSelectedFile();

					/* Add selected documents to a list. */
					if (fileSelected == null) {
						return;
					}

					/* Get the total of records of the Sample CSV file */
					sampleTotaalRecords = limsImporterUtil
							.countRecordsCSV(fileSelected);

					/*
					 * limsImporterUtil .getlineNumber(fileSelected);
					 */

					/* Create the progressbar */
					limsFrameProgress.createProgressGUI();

					/* Start reading data from the file selected */
					logger.info("-------------------------- S T A R T --------------------------");
					logger.info("Start Reading data from a samples file.");
					logger.info("CSV file: " + fileSelected);

					failureList.clear();

					/* Start reading data from csv file */
					try {
						csvReader = new CSVReader(new FileReader(fileSelected),
								'\t', '\'', 0);
						csvReader.readNext();

						/* Get the total size of selected documents */
						importCounter = DocumentUtilities
								.getSelectedDocuments().size();

						/* Add selected documents to a list */
						listDocuments = DocumentUtilities
								.getSelectedDocuments();

						String[] record = null;
						while ((record = csvReader.readNext()) != null) {
							if (record.length == 1 && record[0].isEmpty()) {
								continue;
							}

							/* Start time of processing the documents notes */
							long startBeginTime = System.nanoTime();

							/* Get the ID from CSV file */
							ID = "e" + record[3];

							processSampleDocuments(documents, record,
									startBeginTime);

							/*
							 * Add documents that did not match to the
							 * failureList
							 */
							if (!processedList.toString().contains(ID)
									&& !match) {

								clearVariables();
								recordCount++;

								if (!failureList.toString().contains(ID)) {
									failureList
											.add("No document(s) match found for Registrationnumber: "
													+ ID + "\n");

									limsFrameProgress
											.showProgress("No match : " + ID
													+ "\n" + "  Recordcount: "
													+ recordCount);
								}
							}

						} // end While
						logger.info("--------------------------------------------------------");
						logger.info("Total of document(s) updated: "
								+ listDocuments.size());

						/* Set for creating dummy files */
						if (!ID.equals(extractIDfileName)) {
							/* Create progressbar GUI */
							limsFrameProgress.createProgressGUI();
							/* Create dummy files for samples */
							setExtractIDFromSamplesSheet(fileSelected,
									extractIDfileName);
							/* Hide the progressbar GUI */
							limsFrameProgress.hideFrame();
						}

						logger.info("-------------------------- E N D --------------------------");
						logger.info("Done with updating the selected document(s). ");

						/* Add failure records to the list */
						if (extractIDfileName != null) {
							failureList.add("Total records not matched: "
									+ Integer.toString(failureList.size())
									+ "\n");
						}

						/* Show duration time of the process */
						showProcessingDuration();

						/*
						 * Show a dialog with the results after processing the
						 * documents
						 */
						EventQueue.invokeLater(new Runnable() {

							/**
							 * Show Message dialog with information after
							 * finished adding notes to the document
							 */
							@Override
							public void run() {
								showFinishedDialogMessageOK();

								logger.info("Sample-method: Total imported document(s): "
										+ msgList.toString());

								failureList.add("Filename: " + fileSelected
										+ "\n");
								limsLogger.logToFile(logSamplesFileName,
										failureList.toString());

								clearSamplesVariablesAndList();
								limsFrameProgress.hideFrame();
							}

							private void clearSamplesVariablesAndList() {
								msgList.clear();
								failureList.clear();
								processedList.clear();
								lackList.clear();
								sampleExactRecordsVerwerkt = 0;
								sampleRecordFailure = 0;
								dummyRecordsVerwerkt = 0;
								recordCount = 0;
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
				} else { // If no document selected then add dummy documents.
					startTime = new Date().getTime();
					limsFrameProgress.createProgressGUI();
					fileSelected = fcd.loadSelectedFile();
					setExtractIDFromSamplesSheet(fileSelected,
							extractIDfileName);
					limsFrameProgress.hideFrame();
					/* Add failure records to the list */
					if (extractIDfileName == null) {
						failureList.add("Total records not matched: "
								+ Integer.toString(failureList.size()) + "\n");
					}

					/* Show duration time of the process */
					showProcessingDuration();

					/*
					 * Show a dialog with the results after processing the
					 * documents
					 */
					EventQueue.invokeLater(new Runnable() {

						/**
						 * Show Message dialog with information after finished
						 * adding notes to the document
						 */
						@Override
						public void run() {
							showFinishedDialogMessageDummyOK();

							logger.info("Sample-method: Total imported document(s): "
									+ msgList.toString());

							failureList.add("Filename: " + fileSelected + "\n");
							/*
							 * limsLogger.logToFile(logSamplesFileName,
							 * failureList.toString());
							 */

							clearSamplesVariablesAndList();
							limsFrameProgress.hideFrame();
						}

						private void clearSamplesVariablesAndList() {
							msgList.clear();
							failureList.clear();
							processedList.clear();
							lackList.clear();
							sampleExactRecordsVerwerkt = 0;
							sampleRecordFailure = 0;
							dummyRecordsVerwerkt = 0;
							recordCount = 0;
						}
					});

					// showSelectedDocumentsMessage();
				}
				/*
				 * Choose "No" only samples documents will be processed and no
				 * dummy documents will be created.
				 */
			} else if (n == 1) {
				/* Check if document(s) has been selected * */
				if (!DocumentUtilities.getSelectedDocuments().isEmpty()) {
					/* Load the Sample CSV file that will be processed */
					fileSelected = fcd.loadSelectedFile();

					/* Create progressbar GUI */
					limsFrameProgress.createProgressGUI();

					/* Add notes to the documents */
					extractSamplesRecord_Choose_No(fileSelected, documents);
					/* Hide the progressbar GUI */
					limsFrameProgress.hideFrame();

				} else {
					showSelectedDocumentsMessage();
				}
			} else if (n == 2) {
				return;
			}
		}
	}

	/* Calculate processing time of the notes */
	private void calculateTimeForAddingNotes(long startBeginTime) {
		long endTime = System.nanoTime();
		long elapsedTime = endTime - startBeginTime;
		logger.info("Took: "
				+ (TimeUnit.SECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS))
				+ " second(s)");
		elapsedTime = 0;
		endTime = 0;
	}

	/* Duration process of all selected documents */
	private void showProcessingDuration() {
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
		logger.info("Totaal records verwerkt: " + recordCount);
	}

	/* Show message to select at least one document in Geneious */
	private void showSelectedDocumentsMessage() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {

				Dialogs.showMessageDialog("Select at least one document");
				return;
			}
		});
	}

	/* Process the documents */
	private void processSampleDocuments(AnnotatedPluginDocument[] documents,
			String[] record, long startBeginTime) {
		int cnt = 0;
		Object resultExists;
		for (AnnotatedPluginDocument list : listDocuments) {

			resultExists = null;
			/* Check if "ExtractIDCode_Seq" note exists */
			if (list.toString().contains("ExtractIDCode_Seq")) {
				resultExists = list.getDocumentNotes(true)
						.getNote("DocumentNoteUtilities-Extract ID (Seq)")
						.getFieldValue("ExtractIDCode_Seq");

			} else {
				if (!lackList.toString().contains(list.getName())) {
					lackList.add(list.getName());
					logger.info("At least one selected document lacks Extract ID (Seq)."
							+ list.getName());
				}
			}

			if (resultExists != null) {
				isExtractIDSeqExists = true;
			} else {
				isExtractIDSeqExists = false;
			}

			/* Read the cache_name from the document */
			if (list.toString().contains("cache_name")) {
				documentFileName = (String) list.getFieldValue("cache_name");
			} else {
				/*
				 * Read the override_cache_name from the document
				 */
				readAssembyContigFileName = (String) list
						.getFieldValue("override_cache_name");
			}

			/*
			 * Compare the cache_name with the name of the document
			 */
			if (documentFileName.equals(list.getName())) {
				/*
				 * if selected document is a
				 * "De Novo Assemble continue the process "
				 */
				if (list.toString().contains(documentTypeNovoAssembly)
						|| list.toString().contains(
								documentTypeConsensusSequence)) {
					continue;
				}

				/*
				 * if "ExtractIDCode_Seq" note exists get the version number
				 */

				if (isExtractIDSeqExists)
					version = Integer.parseInt(ReadGeneiousFieldsValues
							.getVersionValueFromAnnotatedPluginDocument(
									documents,
									"DocumentNoteUtilities-Document version",
									"DocumentVersionCode_Seq", cnt));
			}

			/* Check if name is from a Contig file */
			if ((readAssembyContigFileName != null)
					&& readAssembyContigFileName.toString().contains(
							"Reads Assembly Contig")) {
				documentFileName = list.getName();
			} /*
			 * Check if name is from a Consensus document
			 */
			else if (list.getName().toString().contains("consensus sequence")) {
				documentFileName = list.getName();
			} /*
			 * Check if name is from a dummy document
			 */
			else if (list.getName().toString().contains("dum")) {
				documentFileName = list.getName();
			} /* from a imported file */
			else if (!(list.toString().contains(documentTypeNovoAssembly) || list
					.toString().contains(documentTypeConsensusSequence))) {
				/* Contig don't have imported filename. */
				documentFileName = (String) list.getDocumentNotes(true)
						.getNote("importedFrom").getFieldValue("filename");
			}

			/*
			 * if filename contain "Fas" or "AB1" or "dum" get the filename from
			 * the document else from the "De Novo Assemble""
			 */
			if ((documentFileName.toString().contains("fas"))
					|| (documentFileName.toString().contains("ab1"))
					|| (documentFileName.toString().contains("dum"))) {
				extractIDfileName = getExtractIDFromAB1FileName(list.getName());
			} else if (list.getName().toString().contains("consensus sequence")
					|| list.getName().toString().contains("Contig")) {
				extractIDfileName = list.getName();
			}

			/*
			 * Check if record(PlateNumber) contain "-"
			 */
			if (record[2].length() > 0 && record[2].contains("-")) {
				plateNumber = record[2].substring(0, record[2].indexOf("-"));
			}

			/*
			 * ID (ID = record[0]) match extractid from the filename
			 */
			if (ID.equals(extractIDfileName) && isExtractIDSeqExists) {

				/*
				 * Start time for processing the notes to the documents
				 */
				startTime = new Date().getTime();

				match = true;

				msgList.add(extractIDfileName + "\n");

				recordCount++;
				cntRec++;
				/* Show the progress bar */
				limsFrameProgress.showProgress("Filename match : "
						+ extractIDfileName + "\n" + "  Recordcount: "
						+ recordCount);

				/* Set values to the variables */
				// [0] : Projectplaatnr
				// [1] : Plaatpositie
				// [2] : ExtractPlaatnr
				// [3] : ExtractID
				// [4] : RegistrationNumber
				// [5] : TaxonNaam
				// [] : Version
				// [6] : Sample Method
				setFieldsValues(record[0], record[1], plateNumber, ID,
						record[4], record[5], version, record[6]);

				logger.info("Document Filename: " + documentFileName);

				logger.info("Start with adding notes to the document");

				/* Set the notes to the documents */
				// setSamplesNotes(documents, cnt);
				limsSamplesNotes.setAllNotesToAB1FileName(documents, cnt,
						record[4], record[5], record[1], record[3], record[2],
						record[3], record[6], version, regScientificname);

				logger.info("Done with adding notes to the document");

				/*
				 * Add ID of records that has been process to the list.
				 */
				if (!processedList.contains(ID)) {
					processedList.add(ID);
				}

				/*
				 * Duration of processing the notes to a document
				 */
				calculateTimeForAddingNotes(startBeginTime);

				logger.info("=====================================");
				match = false;

			} // end IF
			cnt++;
		} // end For
	}

	/* Clear the fields variables */
	private void clearVariables() {
		limsExcelFields.setProjectPlaatNummer("");
		limsExcelFields.setPlaatPositie("");
		limsExcelFields.setExtractPlaatNummer("");
		limsExcelFields.setExtractID("");
		limsExcelFields.setRegistrationNumber("");
		limsExcelFields.setTaxonNaam("");
	}

	/* Adding notes to the documents */
	private void setSamplesNotes(AnnotatedPluginDocument[] documents, int cnt) {

		/* set note for Registration number */
		limsNotes.setNoteToAB1FileName(documents,
				"RegistrationNumberCode_Samples", "Registr-nmbr (Samples)",
				"Registr-nmbr (Samples)",
				limsExcelFields.getRegistrationNumber(), cnt);

		/* set note for Taxonname */
		limsNotes.setNoteToAB1FileName(documents, "TaxonName2Code_Samples",
				"[Scientific name] (Samples)", "[Scientific name] (Samples)",
				limsExcelFields.getTaxonNaam(), cnt);

		/* set note for Project Plate number */
		limsNotes.setNoteToAB1FileName(documents,
				"ProjectPlateNumberCode_Samples", "Sample plate ID (Samples)",
				"Sample plate ID (Samples)",
				limsExcelFields.getProjectPlaatNummer(), cnt);

		/* Set note for Extract plate number */
		limsNotes.setNoteToAB1FileName(documents,
				"ExtractPlateNumberCode_Samples", "Extract plate ID (Samples)",
				"Extract plate ID (Samples)",
				limsExcelFields.getExtractPlaatNummer(), cnt);

		/* set note for Plate position */
		limsNotes.setNoteToAB1FileName(documents, "PlatePositionCode_Samples",
				"Position (Samples)", "Position (Samples)",
				limsExcelFields.getPlaatPositie(), cnt);

		/* set note for Extract-ID */
		limsNotes.setNoteToAB1FileName(documents, "ExtractIDCode_Samples",
				"Extract ID (Samples)", "Extract ID (Samples)",
				limsExcelFields.getExtractID(), cnt);

		/* set note for Sample method */
		limsNotes.setNoteToAB1FileName(documents, "SampleMethodCode_Samples",
				"Extraction method (Samples)", "Extraction method (Samples)",
				limsExcelFields.getSubSample(), cnt);

		/* Set note for the document version */
		limsNotes.setNoteToAB1FileName(documents, "DocumentVersionCode_Seq",
				"Document version", "Document version",
				String.valueOf(limsExcelFields.getVersieNummer()), cnt);

		/* AmplicificationStaffCode_FixedValue_Samples */

		try {
			limsNotes.setNoteToAB1FileName(documents,
					"AmplicificationStaffCode_FixedValue_Samples",
					"Ampl-staff (Samples)", "Ampl-staff (Samples)",
					limsImporterUtil.getPropValues("samplesamplicification"),
					cnt);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		/*
		 * Lims-190:Sample import maak of update extra veld veldnaam -
		 * Registr-nmbr_[Scientific name] (Samples) en veldcode =
		 * RegistrationNumberCode_TaxonName2Code_Samples
		 */
		limsNotes.setNoteToAB1FileName(documents,
				"RegistrationNumberCode_TaxonName2Code_Samples",
				"Registr-nmbr_[Scientific_name] (Samples)",
				"Registr-nmbr_[Scientific_name] (Samples)",
				limsExcelFields.getRegNumberScientificName(), cnt);
	}

	/*
	 * Create dummy files for samples when there is no match with records in the
	 * database
	 * 
	 * @param fileName , extractFileID
	 */
	private void setExtractIDFromSamplesSheet(String fileName,
			String extractFileID) {
		try {
			if (fileName != null) {
				logger.info("Read samples file: " + fileName);
				CSVReader csvReader = new CSVReader(new FileReader(fileName),
						'\t', '\'', 0);
				csvReader.readNext();
				String[] record = null;
				try {
					while ((record = csvReader.readNext()) != null) {
						if (record.length == 1 && record[0].isEmpty()) {
							continue;
						}

						if (record[3].trim() != null) {
							ID = "e" + record[3];
						}

						if (record[2].length() > 0 && record[2].contains("-")) {
							plateNumber = record[2].substring(0,
									record[2].indexOf("-"));
						}

						int dummyExists = limsSQL
								.checkIfSampleDocExistsInTableAnnotatedDocument(ID);

						if (dummyExists == 0) {
							limsFrameProgress
									.showProgress("Creating dummy file: " + ID
											+ ".dum");

							/* extract only the numbers from ID */
							if (ID.equals("e")
									&& LimsImporterUtil.extractNumber(ID)
											.isEmpty()) {
								logger.info("Record is empty: " + ID + ".dum");
							} else {
								/* Create dummy sequence */
								limsDummySeq.createDummySampleSequence(ID, ID,
										record[0], plateNumber, record[5],
										record[4], record[1], record[6]);
								dummyRecordsVerwerkt++;
							}
						} else {
							limsFrameProgress
									.showProgress("Dummy file already exists in the DB: "
											+ ID);
						}

						boolean dummyFileExists = limsSQL.documentNameExist(ID
								+ ".dum");
						if (!dummyFileExists) {
							try {
								limsSQL.insertIntoTableDocumentImport(ID
										+ ".dum", limsSQL.importcounter);
							} catch (SQLException e) {
								throw new RuntimeException(e);
							}
						} else {
							int counter = limsSQL.importcounter + 1;
							limsSQL.updateImportCount(counter, ID + ".dum");
						}

					} // end While

				} catch (IOException e) {

					throw new RuntimeException(e);
				}
				csvReader.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * Extract the ID from the filename
	 * 
	 * @param annotatedPluginDocuments set the param
	 * 
	 * @return
	 */
	private String getExtractIDFromAB1FileName(String fileName) {
		/* for example: e4010125015_Sil_tri_MJ243_COI-A01_M13F_A01_008.ab1 */
		String[] underscore = null;
		if (fileName.contains("_") && fileName.contains("ab1")) {
			underscore = StringUtils.split(fileName, "_");
		} else if (fileName.contains("_") && !fileName.contains(".")) {
			underscore = StringUtils.split(fileName, "_");
		} else if (fileName.contains(".") && fileName.contains("dum")) {
			underscore = StringUtils.split(fileName, ".");
		} else if (fileName.contains("_")) {
			underscore = StringUtils.split(fileName, "_");
		} else {
			underscore = StringUtils.split(fileName, "");
		}
		return underscore[0];
	}

	/* Add the values to the fields variables */
	private void setFieldsValues(String projectPlaatNr, String plaatPositie,
			String extractPlaatNr, String extractID, String registrationNumber,
			String taxonNaam, Object versieNummer, String sampleMethod) {

		// record[0]
		limsExcelFields.setProjectPlaatNummer(projectPlaatNr);

		// record[1]
		limsExcelFields.setPlaatPositie(plaatPositie);

		// record[2]
		limsExcelFields.setExtractPlaatNummer(extractPlaatNr);

		// record[3]
		if (extractID != null) {
			limsExcelFields.setExtractID(extractID);
		} else {
			limsExcelFields.setExtractID("");
		}

		// record[4]
		limsExcelFields.setRegistrationNumber(registrationNumber);

		// record[5]
		limsExcelFields.setTaxonNaam(taxonNaam);

		// record[6]
		limsExcelFields.setSubSample(sampleMethod);

		if (registrationNumber.length() > 0 && taxonNaam.length() > 0) {
			regScientificname = registrationNumber + " " + taxonNaam;
		} else if (registrationNumber.length() > 0) {
			regScientificname = registrationNumber;
		} else if (registrationNumber.length() == 0 && taxonNaam.length() > 0) {
			regScientificname = taxonNaam;
		}

		/*
		 * Set a combination of registrationnumber with scientificname or only
		 * scientificname
		 */
		limsExcelFields.setRegNumberScientificName(regScientificname);

		/*
		 * Set the version number
		 */
		limsExcelFields.setVersieNummer(versieNummer);

		logger.info("Extract-ID: " + limsExcelFields.getExtractID());
		logger.info("Project plaatnummer: "
				+ limsExcelFields.getProjectPlaatNummer());
		logger.info("Extract plaatnummer: "
				+ limsExcelFields.getExtractPlaatNummer());
		logger.info("Taxon naam: " + limsExcelFields.getTaxonNaam());
		logger.info("Registrationnumber: "
				+ limsExcelFields.getRegistrationNumber());
		logger.info("Plaat positie: " + limsExcelFields.getPlaatPositie());
		logger.info("Sample method: " + limsExcelFields.getSubSample());
		logger.info("Registr-nmbr_[Scientific_name] (Samples): "
				+ limsExcelFields.getRegNumberScientificName());
	}

	/*
	 * When user choose "No". Processing the samples CSV record to the selected
	 * documents. No Dummy documents are created.
	 * 
	 * @param fileName , docsSamples
	 */
	private void extractSamplesRecord_Choose_No(String fileName,
			AnnotatedPluginDocument[] docsSamples) {

		List<String> failureList = new ArrayList<String>();
		List<String> exactProcessedList = new ArrayList<String>();
		String[] record = null;
		try {
			if (fileName != null) {
				logger.info("Read samples file: " + fileName);
				/* Read the records and skip the header */
				CSVReader csvReader = new CSVReader(new FileReader(fileName),
						'\t', '\'', 1);
				csvReader.readNext();

				/* Get "logname=Lims2-Import.log" from the property file */
				logSamplesFileName = limsImporterUtil.getLogPath()
						+ "Sample-method-Uitvallijst-"
						+ limsImporterUtil.getLogFilename();

				limsLogger = new LimsLogger(logSamplesFileName);

				try {
					while ((record = csvReader.readNext()) != null) {
						/* skip if record is empty and continue the process */
						if (record.length == 1 && record[0].isEmpty()) {
							continue;
						}

						/* ID = "4010125015" */
						if (record[3].trim() != null) {
							ID = "e" + record[3];
						}

						/* ExtractPlaatnr */
						if (record[2].length() > 0 && record[2].contains("-")) {
							plateNumber = record[2].substring(0,
									record[2].indexOf("-"));
						}

						Boolean isMatched = false;
						/* Add selected documents to the List */
						if (!DocumentUtilities.getSelectedDocuments().isEmpty()) {
							listDocuments = DocumentUtilities
									.getSelectedDocuments();
						}

						int cnt = 0;
						/* Looping thru the selected documents in the List */
						for (AnnotatedPluginDocument list : listDocuments) {
							/*
							 * Check if name is from a Contig file De Novo
							 * Assemble
							 */
							long startBeginTime = System.nanoTime();
							if (list.toString().contains(
									documentTypeNovoAssembly)
									|| list.toString().contains(
											documentTypeConsensusSequence)) {
								continue;
							}

							/* get the filename */
							if ((list.toString().contains("fas"))
									|| (list.toString().contains("ab1"))
									|| (list.toString().contains("dum"))) {
								extractIDfileName = getExtractIDFromAB1FileName(list
										.getName());
							}

							/* Check if note exists */
							isExtractIDSeqExists = list.toString().contains(
									"ExtractIDCode_Seq");

							/*
							 * if not exists processed it to the
							 * lacklist/Failure list
							 */
							if (!isExtractIDSeqExists) {
								if (!lackList.contains(DocumentUtilities
										.getSelectedDocuments().get(cnt)
										.getName())) {
									logger.info("At least one selected document lacks Extract ID (Seq)."
											+ list.getName());
									lackList.add(list.getName());
								}
								/*
								 * Get the version number of last inserted
								 * document from the database
								 */
							} else if (isExtractIDSeqExists) {
								version = Integer
										.parseInt(ReadGeneiousFieldsValues
												.getVersionValueFromAnnotatedPluginDocument(
														docsSamples,
														"DocumentNoteUtilities-Document version",
														"DocumentVersionCode_Seq",
														cnt));
							}

							if (version == 0)
								version = 1;
							/*
							 * if extract filename match the ID from the samples
							 * Csv record start processing the notes.
							 */
							if (ID.equals(extractIDfileName)
									&& isExtractIDSeqExists) {

								startTime = new Date().getTime();
								isMatched = true;
								/* if match add to processed List */
								if (!exactProcessedList.contains(ID)) {
									exactProcessedList.add(ID);
								}
								recordCount++;
								/* Show progressbar GUI */
								limsFrameProgress
										.showProgress("Document match: " + ID);
								/*
								 * Set values to the variables [0] :
								 * Projectplaatnr [1] : Plaatpositie [2] :
								 * ExtractPlaatnr [3] : ExtractID [4] :
								 * RegistrationNumber [5] : TaxonNaam [] :
								 * Version [6] : Sample Method
								 */
								setFieldsValues(record[0], record[1],
										plateNumber, ID, record[4], record[5],
										version, record[6]);
								logger.info("Start with adding notes to the document");
								/* Add notes to the selected documents */
								setSamplesNotes(docsSamples, cnt);

								logger.info("Done with adding notes to the document");

								/* Calculate processing time of the notes */
								calculateTimeForAddingNotes(startBeginTime);

								logger.info("=====================================");
							}
							cnt++;
						} // For
						if (!exactProcessedList.contains(ID) && !isMatched) {
							/* isAlpha: Check for Letters character in "ID " */
							if (!failureList.contains(ID)
									&& !limsImporterUtil.isAlpha(ID)) {
								failureList
										.add("No document(s) match found for Registrationnumber: "
												+ ID + "\n");
								limsFrameProgress
										.showProgress("No document match: "
												+ ID);
							}
						}
						isMatched = false;
					} // end While

					/* Show duration time of the process */
					showProcessingDuration();

					/* Show result dialog after processing the documents */
					showFinishedDialogMessageNo(fileName, failureList,
							exactProcessedList);

					failureList.add("Total records not matched: "
							+ Integer.toString(failureList.size()) + "\n");

					/* Create failure log file */
					limsLogger.logToFile(logSamplesFileName,
							failureList.toString());
					failureList.clear();
					exactProcessedList.clear();
					lackList.clear();
					recordCount = 0;

				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				try {
					csvReader.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * Show dialog message after processed the notes when choose "No"
	 * 
	 * @param fileName , failureList, exactProcessedList
	 */
	private void showFinishedDialogMessageNo(String fileName,
			List<String> failureList, List<String> exactProcessedList) {
		Dialogs.showMessageDialog(readTotalRecordsOfFileSelected(fileName)
				+ " sample records have been read of which: " + "\n" + "\n"
				+ "[1] " + Integer.toString(exactProcessedList.size())
				+ " samples are imported and linked to "
				+ Integer.toString(recordCount) + " existing documents (of "
				+ listDocuments.size() + " selected)" + "\n" + "\n" + "[2] "
				+ "0 samples are imported as dummy." + "\n" + "\n" + "[3] "
				+ Integer.toString(failureList.size())
				+ " samples records are ignored." + "\n" + "\n"
				+ getLackMessage(isLackListNotEmpty()));
	}

	/*
	 * Get the total of records from the samples csv file
	 * 
	 * @param fileName
	 * 
	 * @return
	 */
	private int readTotalRecordsOfFileSelected(String fileName) {
		int result = 0;
		if (result == 0) {
			try {
				csvReader = new CSVReader(new FileReader(fileName), '\t', '\'',
						1);
				result = csvReader.readAll().size();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			csvReader = null;
		}
		return result;
	}

	/*
	 * Show dialog message after processed the notes when Choose "OK"
	 * 
	 * @param fileName , failureList, exactProcessedList
	 */
	private void showFinishedDialogMessageOK() {
		sampleRecordFailure = failureList.size() - 1;
		sampleExactRecordsVerwerkt = processedList.size();

		Dialogs.showMessageDialog(Integer.toString(sampleTotaalRecords)
				+ " sample records have been read of which: " + "\n" + "\n"
				+ "[1] " + Integer.toString(sampleExactRecordsVerwerkt)
				+ " samples are imported and linked to "
				+ Integer.toString(cntRec) + " existing documents (of "
				+ importCounter + " selected)" + "\n" + "\n" + "[2] "
				+ Integer.toString(dummyRecordsVerwerkt)
				+ " samples are imported as dummy" + "\n" + "\n" + "[3] "
				+ Integer.toString(sampleRecordFailure - dummyRecordsVerwerkt)
				+ " sample records are ignored." + "\n" + "\n"
				+ getLackMessage(isLackListNotEmpty()));
	}

	private void showFinishedDialogMessageDummyOK() {
		sampleRecordFailure = failureList.size();
		sampleExactRecordsVerwerkt = processedList.size();

		Dialogs.showMessageDialog(Integer.toString(sampleTotaalRecords)
				+ " sample records have been read of which: " + "\n" + "\n"
				+ "[1] " + Integer.toString(sampleExactRecordsVerwerkt)
				+ " samples are imported and linked to "
				+ Integer.toString(cntRec) + " existing documents (of "
				+ importCounter + " selected)" + "\n" + "\n" + "[2] "
				+ Integer.toString(dummyRecordsVerwerkt)
				+ " samples are imported as dummy" + "\n" + "\n" + "[3] "
				+ Integer.toString(failureList.size())
				+ " sample records are ignored." + "\n" + "\n"
				+ getLackMessage(isLackListNotEmpty()));
	}

}
